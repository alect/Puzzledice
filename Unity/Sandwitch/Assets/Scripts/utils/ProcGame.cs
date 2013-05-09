using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen;
using puzzlegen.database; 
using puzzlegen.buildingblocks;
using puzzlegen.relationship; 

public class ProcGame : IRelationshipVisitor
{
	
	// The list of maps we use for generation 
	protected List<string> _puzzleMaps = new List<string>() { "full-combine-1", "full-combine-2", "full-request-1", "full-request-2", "full-unlock-1" }; 
	
	protected const int MAX_TRIES = 10; 	
	protected Room _startRoom; 
	public Room startRoom { 
		get { return _startRoom; } 
	}
	
	protected Player _player; 
	public Player player { 
		get { return _player; } 
	} 	
	
	protected Dictionary<string, Dictionary<string, IRelationship>> _relationshipMap; 
	public IRelationship getRelationship(string name1, string name2) 
	{ 
		if (_relationshipMap.ContainsKey(name1) && _relationshipMap[name1].ContainsKey(name2))
			return _relationshipMap[name1][name2]; 
		return null; 
	} 
	
	protected Dictionary<string, ItemRequestRelationship> _requests; 
	public ItemRequestRelationship getRequest(string requesterName) 
	{ 
		if (_requests.ContainsKey(requesterName))
			return _requests[requesterName]; 
		return null; 
	} 
	
	protected HashSet<string> _itemNames; 
	
	protected Dictionary<string, Room> _areaRooms;
	protected Dictionary<string, List<string>> _areaConnections; 
	protected Dictionary<string, List<AreaConnectionRelationship>> _lockedAreaConnections; 
	protected string _startArea; 
	
	// Control the filler rooms between two areas 
	protected const int MIN_FILLER_ROOMS = 1, MAX_FILLER_ROOMS = 2; 
	// The maximum size of the box containing the areas
	// Should be odd so we can stick the start area at the center. 
	protected const int MAX_MAP_SIZE = 9; 
	
	public ProcGame() 
	{ 
		generateGame(); 	
	} 
		
	
	protected void generateGame() 
	{ 
		
		_relationshipMap = new Dictionary<string, Dictionary<string, IRelationship>>(); 
		_requests = new Dictionary<string, ItemRequestRelationship>(); 
		_areaRooms = new Dictionary<string, Room>(); 
		_areaConnections = new Dictionary<string, List<string>>(); 
		_lockedAreaConnections = new Dictionary<string, List<AreaConnectionRelationship>>(); 
		_itemNames = new HashSet<string>(); 
		_startRoom = null; 
		_startArea = null; 
		
		// Let's make a puzzle!
		 
		Database.Instance.removeExtensions();
		Database.Instance.addExtension(new ChangesExtension()); 
		Database.Instance.addExtension(new ChangedbyExtension()); 
		Database.Instance.addExtension(new ParentExtension()); 
		Database.Instance.addExtension(new FilledbyExtension());
		Database.Instance.addExtension(new MakesExtension()); 
		Database.Instance.addExtension(new GivesExtension()); 
		
		Globals.shuffle(_puzzleMaps); 
		PuzzleOutput output = null; 
		int puzzleI = 0; 
		int numTries = 0; 
		while(output == null && numTries < MAX_TRIES) { 
			Database.Instance.clearDatabase();
			DBParser.createDatabaseFromXml(new string[] { "database" }); 
			Database.Instance.runExtensions(); 
			
			BuildingBlock puzzle = PuzzleMapParser.createBuildingBlockFromXml(_puzzleMaps[puzzleI]);
			output = puzzle.generatePuzzle("sandwich"); 
			puzzleI = (puzzleI+1) % _puzzleMaps.Count; 
			numTries++;
		}
		
		
		
		foreach (IRelationship rel in output.Relationships) {
			rel.addRelationshipToGame(this);
			Debug.Log(rel.ToString());
		}
		
		// Now add all the relationships and items 
		foreach (PuzzleItem item in output.Items) 
			spawnItem(item); 
		 
		createAreaConnections(); 
		createAuxiliaryRelationships(); 
		
		// Add the player to the start room. 
		GameObject playerObj = GameObject.Instantiate(PlayState.instance.playerPrefab) as GameObject; 
		_player = playerObj.GetComponent<Player>();
		_startRoom.addPiece(_player);
		
	} 
	
	protected void addArea(string areaName) 
	{ 
		if (_areaRooms.ContainsKey(areaName))
			return; 
		
		// Make a base room for our new area 
		Room areaRoom = new Room(); 
		// Check the area name for specific tile names 
		if (areaName.ToLower().Contains("grass"))
			areaRoom.floorTilesName = "grasstiles"; 
		else if (areaName.ToLower().Contains("dirt"))
			areaRoom.floorTilesName = "dirttiles";
		else if (areaName.ToLower().Contains("kitchen"))
			areaRoom.floorTilesName = "kitchentiles";
		
		_areaRooms[areaName] = areaRoom; 
		_areaConnections[areaName] = new List<string>(); 
		_lockedAreaConnections[areaName] = new List<AreaConnectionRelationship>(); 
	} 
	
	
	protected SpawnedPuzzleItem spawnItem(PuzzleItem item) 
	{ 	
		_itemNames.Add(item.Name); 
		GameObject puzzleItemObj = GameObject.Instantiate(PlayState.instance.puzzleItemPrefab) as GameObject; 
		SpawnedPuzzleItem spawnedItem = puzzleItemObj.GetComponent<SpawnedPuzzleItem>(); 
		
		// Give the spawned item all the appropriate properties. 
		foreach (string propertyName in item.getPropertyNames()) { 
			spawnedItem.setProperty(propertyName, item.getProperty(propertyName)); 	
		} 
		if (getRequest(spawnedItem.itemName) != null) 
			spawnedItem.initRequest(getRequest(spawnedItem.itemName)); 
		
		// Figure out where to spawn the item 
		if (item.propertyExists("spawnArea")) { 
			string spawnAreaName = item.getProperty("spawnArea") as string; 
			addArea(spawnAreaName); 
			_areaRooms[spawnAreaName].addPiece(spawnedItem); 
		}  
		return spawnedItem;
	} 
	
	protected void createAreaConnections() 
	{ 
		if (_startArea == null || !_areaRooms.ContainsKey(_startArea))
			throw new UnityException("No start area!"); 
		_startRoom = _areaRooms[_startArea]; 
		// Create a grid of rooms to organize everything 
		Room[,] roomGrid = new Room[MAX_MAP_SIZE, MAX_MAP_SIZE]; 
		// Also keep a record of area positions in the grid 
		Dictionary<string, Vector2> areaPositions = new Dictionary<string, Vector2>(); 
		
		// Place the start room in the center of the grid
		roomGrid[MAX_MAP_SIZE/2, MAX_MAP_SIZE/2] = _startRoom;
		areaPositions[_startArea] = new Vector2((int)(MAX_MAP_SIZE/2), (int)(MAX_MAP_SIZE)/2); 
		
		// Branch off from the rooms one by one
		Queue<string> agenda = new Queue<string>(); 
		agenda.Enqueue(_startArea); 
		
		while (agenda.Count > 0) { 
			string areaName = agenda.Dequeue(); 
			Vector2 areaPosition = areaPositions[areaName]; 
			Room areaRoom = _areaRooms[areaName]; 
			if (!_areaConnections.ContainsKey(areaName))
				continue; 
			foreach (string neighbor in _areaConnections[areaName]) { 
				// Create a connection to this area
				
				// Decide how many filler rooms we'll make 
				int numFillerRooms = Random.Range(MIN_FILLER_ROOMS, MAX_FILLER_ROOMS+1);
				
				Room currentRoom = areaRoom; 
				Vector2 currentPos = areaPosition; 
				uint dir; 
				for (int i = 0; i < numFillerRooms; i++) { 
					// Choose a direction to expand
					dir = expandRoom(currentRoom, currentPos, roomGrid);
					Vector2 newRoomPos = pointFromDir(dir, currentPos); 
					Room newRoom = new Room(); 
					newRoom.floorTilesName = currentRoom.floorTilesName; 
					// Connect our rooms
					currentRoom.makeSpatialConnection(dir, newRoom); 
					newRoom.makeSpatialConnection(oppositeDir(dir), currentRoom);
					// Add some goblins to the room 
					newRoom.addMonsters(); 
					// Update the grid
					roomGrid[(int)newRoomPos.x, (int)newRoomPos.y] = newRoom; 
					
					currentRoom = newRoom; 
					currentPos = newRoomPos; 
				} 
				// Finally, create the neighboring room at the next location. 
				Room neighborRoom = _areaRooms[neighbor]; 
				dir = expandRoom(currentRoom, currentPos, roomGrid); 
				Vector2 neighborPos = pointFromDir(dir, currentPos); 
				currentRoom.makeSpatialConnection(dir, neighborRoom); 
				neighborRoom.makeSpatialConnection(oppositeDir(dir), currentRoom); 
				roomGrid[(int)neighborPos.x, (int)neighborPos.y] = neighborRoom; 
				areaPositions[neighbor] = neighborPos; 
				agenda.Enqueue(neighbor); 
			}
			// Now, the locked doors 
			foreach (AreaConnectionRelationship lockedRel in _lockedAreaConnections[areaName]) { 
				string neighbor = lockedRel.secondAreaName; 
				// Spawn a lock 
				SpawnedPuzzleItem lockedDoor = spawnLockedDoor(); 
				// Add the locked door relationship to our map 
				// Figure out what our actual key name is
				string keyName = lockedRel.keyName; 
				DBItem dbKey = Database.Instance.getItem(keyName); 
				if (dbKey.propertyExists("keyname"))
					keyName = dbKey.getProperty("keyname") as string; 
				_itemNames.Add(keyName); 
				
				if (!_relationshipMap.ContainsKey(keyName)) 
					_relationshipMap[keyName] = new Dictionary<string, IRelationship>(); 
				_relationshipMap[keyName][lockedDoor.itemName] = lockedRel; 
				if (!_relationshipMap.ContainsKey(lockedDoor.itemName))
					_relationshipMap[lockedDoor.itemName] = new Dictionary<string, IRelationship>(); 
				_relationshipMap[lockedDoor.itemName][keyName] = lockedRel; 
				// Decide how many filler rooms we'll make 
				int numFillerRooms = Random.Range(MIN_FILLER_ROOMS, MAX_FILLER_ROOMS+1);
				
				Room currentRoom = areaRoom; 
				Vector2 currentPos = areaPosition; 
				uint dir; 
				for (int i = 0; i < numFillerRooms; i++) { 
					// Choose a direction to expand
					dir = expandRoom(currentRoom, currentPos, roomGrid);
					Vector2 newRoomPos = pointFromDir(dir, currentPos); 
					Room newRoom = new Room(); 
					newRoom.floorTilesName = currentRoom.floorTilesName; 
					// Connect our rooms
					if (i == 0) {
						int doorIndex = currentRoom.makeLockedConnection(dir, newRoom, lockedDoor);
						newRoom.makeLockedConnection(oppositeDir(dir), currentRoom, doorIndex); 
					}
					else {
						currentRoom.makeSpatialConnection(dir, newRoom); 
						newRoom.makeSpatialConnection(oppositeDir(dir), currentRoom);
					}
					// Add some goblins to the room 
					newRoom.addMonsters(); 
					// Update the grid
					roomGrid[(int)newRoomPos.x, (int)newRoomPos.y] = newRoom; 
					
					currentRoom = newRoom; 
					currentPos = newRoomPos; 
				} 
				// Finally, create the neighboring room at the next location. 
				Room neighborRoom = _areaRooms[neighbor]; 
				dir = expandRoom(currentRoom, currentPos, roomGrid); 
				Vector2 neighborPos = pointFromDir(dir, currentPos); 
				currentRoom.makeSpatialConnection(dir, neighborRoom); 
				neighborRoom.makeSpatialConnection(oppositeDir(dir), currentRoom); 
				roomGrid[(int)neighborPos.x, (int)neighborPos.y] = neighborRoom; 
				areaPositions[neighbor] = neighborPos; 
				agenda.Enqueue(neighbor);
				
			} 
		}
		
		// Finally, go through all the existing rooms and make sure they're not active 
		foreach (Room room in roomGrid) { 
			if (room != null)
				room.deactivateRoom(); 
		} 
		
	} 
	
	protected uint oppositeDir(uint dir)
	{ 
		switch(dir) { 
		case GridPiece.UP:
			return GridPiece.DOWN; 
		case GridPiece.RIGHT:
			return GridPiece.LEFT; 
		case GridPiece.DOWN:
			return GridPiece.UP; 
		case GridPiece.LEFT:
			return GridPiece.RIGHT; 
		default:
			return GridPiece.UP;
		} 
	} 
	
	protected Vector2 pointFromDir(uint dir, Vector2 point) 
	{ 	
		switch(dir) { 
		case GridPiece.UP: 
			return new Vector2(point.x, point.y+1); 
		case GridPiece.RIGHT:
			return new Vector2(point.x+1, point.y); 
		case GridPiece.DOWN:
			return new Vector2(point.x, point.y-1); 
		case GridPiece.LEFT: 
			return new Vector2(point.x-1, point.y); 
		default:
			return Vector2.zero; 
		} 
	} 
	
	protected uint expandRoom(Room room, Vector2 roomPos, Room[,] roomGrid) 
	{  
		List<uint> validExits = new List<uint>(); 
		foreach (uint maybeExit in room.possibleExits()) { 
			if (maybeExit == GridPiece.UP && roomPos.y < MAX_MAP_SIZE-1 && roomGrid[(int)roomPos.x, (int)roomPos.y+1] == null)
				validExits.Add(maybeExit); 
			if (maybeExit == GridPiece.RIGHT && roomPos.x < MAX_MAP_SIZE-1 && roomGrid[(int)roomPos.x+1, (int)roomPos.y] == null) 
				validExits.Add(maybeExit); 
			if (maybeExit == GridPiece.DOWN && roomPos.y > 0 && roomGrid[(int)roomPos.x, (int)roomPos.y-1] == null)
				validExits.Add(maybeExit); 
			if (maybeExit == GridPiece.LEFT && roomPos.x > 0 && roomGrid[(int)roomPos.x-1, (int)roomPos.y] == null)
				validExits.Add(maybeExit); 
		} 
		if (validExits.Count == 0)
			throw new UnityException("Room Expansion Failure!"); 
		return Globals.getRandom(validExits); 
	} 
	
	protected SpawnedPuzzleItem spawnLockedDoor() 
	{ 
		DBItem lockDB = Database.Instance.getItem("lock"); 
		PuzzleItem lockItem = lockDB.spawnItem();
		return spawnItem(lockItem); 
	} 
	
	
	public void accept (AreaConnectionRelationship rel)
	{
		// Mark a connection between two areas here 
		// The actual connections are built after all the relationships are added
		addArea(rel.firstAreaName); 
		addArea(rel.secondAreaName); 
		
		if (!rel.locked) { 
			if (!_areaConnections[rel.firstAreaName].Contains(rel.secondAreaName))
				_areaConnections[rel.firstAreaName].Add(rel.secondAreaName); 
		}
		else { 
			// Handle locked connections. 
			if (!_lockedAreaConnections[rel.firstAreaName].Contains(rel))
				_lockedAreaConnections[rel.firstAreaName].Add(rel); 
			_itemNames.Add(rel.keyName); 
		} 
	}
	
	public void accept (CombineRelationship rel)
	{
		// Add the combine relationship to the map 
		if (!_relationshipMap.ContainsKey(rel.ingredient1Name)) 
			_relationshipMap[rel.ingredient1Name] = new Dictionary<string, IRelationship>(); 
		_relationshipMap[rel.ingredient1Name][rel.ingredient2Name] = rel; 
		if (!_relationshipMap.ContainsKey(rel.ingredient2Name))
			_relationshipMap[rel.ingredient2Name] = new Dictionary<string, IRelationship>(); 
		_relationshipMap[rel.ingredient2Name][rel.ingredient1Name] = rel; 
		
	 	// Make sure our reward item is included in the list of item names 
		_itemNames.Add(rel.resultItem.Name); 
	}
	
	public void accept (InsertionRelationship rel)
	{
		// Add the insertion relationship to the map 
		if (!_relationshipMap.ContainsKey(rel.containerName))
			_relationshipMap[rel.containerName] = new Dictionary<string, IRelationship>(); 
		_relationshipMap[rel.containerName][rel.fillerName] = rel; 
		if (!_relationshipMap.ContainsKey(rel.fillerName))
			_relationshipMap[rel.fillerName] = new Dictionary<string, IRelationship>(); 
		_relationshipMap[rel.fillerName][rel.containerName] = rel; 
	}
	
	public void accept (ItemRequestRelationship rel)
	{
		// First, add this to our list of requests
		_requests[rel.requesterName] = rel; 

		// Add the item request relationship to the map
		if (!_relationshipMap.ContainsKey(rel.requesterName))
			_relationshipMap[rel.requesterName] = new Dictionary<string, IRelationship>(); 
		_relationshipMap[rel.requesterName][rel.requestedName] = rel; 
		if (!_relationshipMap.ContainsKey(rel.requestedName))
			_relationshipMap[rel.requestedName] = new Dictionary<string, IRelationship>(); 
		_relationshipMap[rel.requestedName][rel.requesterName] = rel; 
		
		// Make sure the reward item is included in the list of item names 
		_itemNames.Add(rel.rewardItem.Name); 
	}
	
	public void accept (PropertyChangeRelationship rel)
	{
		// Add the property change relationship to the map 
		if (!_relationshipMap.ContainsKey(rel.changerName))
			_relationshipMap[rel.changerName] = new Dictionary<string, IRelationship>(); 	
		_relationshipMap[rel.changerName][rel.changeeName] = rel; 
		if (!_relationshipMap.ContainsKey(rel.changeeName))
			_relationshipMap[rel.changeeName] = new Dictionary<string, IRelationship>(); 
		_relationshipMap[rel.changeeName][rel.changerName] = rel; 	
	}
	
	public void accept (StartAreaRelationship rel)
	{
		// Make the indicated area into the start area. 
		addArea(rel.areaName);
		_startArea = rel.areaName;
	}
	
	protected void createAuxiliaryRelationships() 
	{ 
		foreach (string name1 in _itemNames) { 
			foreach (string name2 in _itemNames) { 
				if (name1 == name2 || getRelationship(name1, name2) != null) 
					continue; 
				addAuxiliaryRelationship(name1, name2); 
			} 	
		} 
	} 
	
	protected void addAuxiliaryRelationship(string name1, string name2) 
	{ 
		IRelationship relToAdd = null; 
		
		DBItem dbitem1 = Database.Instance.getItem(name1); 
		DBItem dbitem2 = Database.Instance.getItem(name2); 
		
		// Figure out if one of the items can be inserted in the other 
		if (dbitem1.propertyExists("container") && (bool)dbitem1.getProperty("container")
			&& dbitem1.propertyExists("filledby") && (dbitem1.getProperty("filledby") as List<string>).Contains(name2)) { 
			 	relToAdd = new InsertionRelationship(name2, 0, name1, 0); 
		}
		if (relToAdd == null) { 
			if (dbitem2.propertyExists("container") && (bool)dbitem2.getProperty("container")
				&& dbitem2.propertyExists("filledby") && (dbitem2.getProperty("filledby") as List<string>).Contains(name1)) { 
				 	relToAdd = new InsertionRelationship(name1, 0, name2, 0); 
			}
		}
		// Next, try combine relationships 
		if (relToAdd == null) { 
			if (dbitem1.propertyExists("makes")) { 
				List<KeyValuePair<string, string>> makes = new List<KeyValuePair<string, string>>((List<KeyValuePair<string, string>>)dbitem1.getProperty("makes"));
				Globals.shuffle(makes); 
				foreach (KeyValuePair<string, string> recipe in makes) { 
					if (recipe.Key == name2) { 
						if (!Database.Instance.itemExists(recipe.Value))
							continue; 
						DBItem dbResult = Database.Instance.getItem(recipe.Value); 
						if (dbResult.Spawned)
							continue; 
						PuzzleItem resultItem = dbResult.spawnItem(); 
						relToAdd = new CombineRelationship(name1, 0, name2, 0, resultItem); 	
						break;
					} 
				} 	
			} 
		}
		if (relToAdd == null) { 
			if (dbitem2.propertyExists("makes")) { 
				List<KeyValuePair<string, string>> makes = new List<KeyValuePair<string, string>>((List<KeyValuePair<string, string>>)dbitem2.getProperty("makes"));
				Globals.shuffle(makes); 
				foreach (KeyValuePair<string, string> recipe in makes) { 
					if (recipe.Key == name1) { 
						if (!Database.Instance.itemExists(recipe.Value))
							continue; 
						DBItem dbResult = Database.Instance.getItem(recipe.Value); 
						if (dbResult.Spawned)
							continue; 
						PuzzleItem resultItem = dbResult.spawnItem(); 
						relToAdd = new CombineRelationship(name1, 0, name2, 0, resultItem); 	
						break;
					} 
				} 	
			} 
		} 
		// Finally try property change relationships 
		if (relToAdd == null) { 
			if (dbitem1.propertyExists("changes")) { 
				List<KeyValuePair<string, string>> changes = new List<KeyValuePair<string, string>>(); 
				Dictionary<string, List<string>> changesDict = (Dictionary<string, List<string>>)dbitem1.getProperty("changes"); 
				foreach (string changeProp in changesDict.Keys) { 
					foreach (string changeVal in changesDict[changeProp]) { 
						changes.Add(new KeyValuePair<string, string>(changeProp, changeVal)); 	
					} 
				} 
	
				Globals.shuffle(changes); 
				foreach (KeyValuePair<string, string> change in changes) { 
					// Check if the property is mutable and allows our value 
					if (!dbitem2.propertyExists("mutables") || !((List<string>)dbitem2.getProperty("mutables")).Contains(change.Key))
						continue; 
					if (!dbitem2.propertyExists(change.Key) || !((List<string>)dbitem2.getProperty(change.Key)).Contains(change.Value))
						continue; 
					relToAdd = new PropertyChangeRelationship(name2, 0, name1, 0, change.Key, change.Value); 
					break; 
				} 
			} 	
		}
		if (relToAdd == null) { 
			if (dbitem2.propertyExists("changes")) { 
				List<KeyValuePair<string, string>> changes = new List<KeyValuePair<string, string>>(); 
				Dictionary<string, List<string>> changesDict = (Dictionary<string, List<string>>)dbitem2.getProperty("changes"); 
				foreach (string changeProp in changesDict.Keys) { 
					foreach (string changeVal in changesDict[changeProp]) { 
						changes.Add(new KeyValuePair<string, string>(changeProp, changeVal)); 	
					} 
				} 
				Globals.shuffle(changes); 
				foreach (KeyValuePair<string, string> change in changes) { 
					// Check if the property is mutable and allows our value 
					if (!dbitem1.propertyExists("mutables") || !((List<string>)dbitem1.getProperty("mutables")).Contains(change.Key))
						continue; 
					if (!dbitem1.propertyExists(change.Key) || !((List<string>)dbitem1.getProperty(change.Key)).Contains(change.Value))
						continue; 
					relToAdd = new PropertyChangeRelationship(name1, 0, name2, 0, change.Key, change.Value); 
					break; 
				} 
			}	
			
		} 
		
		if (relToAdd != null) { 
			if (!_relationshipMap.ContainsKey(name1)) 
				_relationshipMap[name1] = new Dictionary<string, IRelationship>(); 
			_relationshipMap[name1][name2] = relToAdd; 
			if (!_relationshipMap.ContainsKey(name2))
				_relationshipMap[name2] = new Dictionary<string, IRelationship>(); 
			_relationshipMap[name2][name1] = relToAdd; 
		}
		
	} 	
	
	
}
