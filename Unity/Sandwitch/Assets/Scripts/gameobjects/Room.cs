using UnityEngine;
using System.Collections;
using System.Collections.Generic;

/// <summary>
/// Basic class for supporting swapping between rooms
/// </summary>
public class Room {
	
	protected const int MIN_COSMETIC_WALLS = 0, MAX_COSMETIC_WALLS = 5; 
	protected const int MIN_GOBLINS = 0, MAX_GOBLINS = 5; 
	protected const int MIN_SPIKES = 0, MAX_SPIKES = 4; 
	
	
	protected string _floorTilesName = "dirttiles"; 
	public string floorTilesName {
		get { return _floorTilesName; }
		set { _floorTilesName = value; } 
	}
	
	protected List<GridPiece> _gridPieces; 
	public List<GridPiece> gridPieces { 
		get { return _gridPieces; }
	} 
	
	
	protected Room _upExit, _rightExit, _downExit, _leftExit; 
	public List<uint> possibleExits() 
	{ 
		List<uint> exits = new List<uint>(); 
		if (_upExit == null)
			exits.Add(GridPiece.UP); 
		if (_rightExit == null)
			exits.Add(GridPiece.RIGHT); 
		if (_downExit == null) 
			exits.Add(GridPiece.DOWN); 
		if (_leftExit == null)
			exits.Add(GridPiece.LEFT); 
		return exits;
	} 
	
	

	public Room() 
	{ 
		_gridPieces = new List<GridPiece>(); 	
		makeEmptyRoom(); 
		addCosmeticWalls();
	} 
	
	public void deactivateRoom() 
	{ 
		foreach (GridPiece piece in _gridPieces) { 
			piece.gameObject.SetActive(false); 	
		} 
	} 
	
	protected void makeEmptyRoom()
	{ 
		// First thing to do is create wall tiles around the border 
		for (int i = 0; i < Globals.ROOM_WIDTH; i++) { 
			// Make a wall along the horizontal edges
			GameObject wallObj = GameObject.Instantiate(PlayState.instance.wallPrefab) as GameObject; 
			wallObj.transform.parent = PlayState.instance.transform;
			GridPiece wallPiece = wallObj.GetComponent<GridPiece>(); 
			wallPiece.x = Globals.CELL_SIZE*i + Globals.CELL_SIZE/2; 
			wallPiece.y = Globals.CELL_SIZE/2; 
			 
			wallPiece.init(); 
			_gridPieces.Add(wallPiece); 
			
			wallObj = GameObject.Instantiate(PlayState.instance.wallPrefab) as GameObject; 
			wallObj.transform.parent = PlayState.instance.transform; 
			wallPiece = wallObj.GetComponent<GridPiece>(); 
			wallPiece.x = Globals.CELL_SIZE*i + Globals.CELL_SIZE/2; 
			wallPiece.y = Globals.CELL_SIZE*(Globals.ROOM_HEIGHT-1) + Globals.CELL_SIZE/2;
			
			wallPiece.init(); 
			_gridPieces.Add(wallPiece); 
		} 
		for (int i = 1; i < Globals.ROOM_HEIGHT-1; i++) { 
			// Make a wall along the horizontal edges
			GameObject wallObj = GameObject.Instantiate(PlayState.instance.wallPrefab) as GameObject; 
			wallObj.transform.parent = PlayState.instance.transform;
			GridPiece wallPiece = wallObj.GetComponent<GridPiece>(); 
			wallPiece.y = Globals.CELL_SIZE*i + Globals.CELL_SIZE/2; 
			wallPiece.x = Globals.CELL_SIZE/2; 
			
			wallPiece.init(); 
			_gridPieces.Add(wallPiece); 
			
			wallObj = GameObject.Instantiate(PlayState.instance.wallPrefab) as GameObject; 
			wallObj.transform.parent = PlayState.instance.transform;
			wallPiece = wallObj.GetComponent<GridPiece>(); 
			wallPiece.y = Globals.CELL_SIZE*i + Globals.CELL_SIZE/2; 
			wallPiece.x = Globals.CELL_SIZE*(Globals.ROOM_WIDTH-1) + Globals.CELL_SIZE/2;
			
			wallPiece.init(); 
			_gridPieces.Add(wallPiece);
		}	
	}
	
	protected void addCosmeticWalls() 
	{ 
		int numWalls = Random.Range(MIN_COSMETIC_WALLS, MAX_COSMETIC_WALLS+1); 
		for (int i = 0; i < numWalls; i++) { 
			GameObject newWall = GameObject.Instantiate(PlayState.instance.wallPrefab) as GameObject; 
			newWall.transform.parent = PlayState.instance.transform; 
			GridPiece wallPiece = newWall.GetComponent<GridPiece>(); 
			addPiece(wallPiece); 
		} 
	} 
	
	public void addMonsters() 
	{ 
		addGoblins(); 
		addSpikes(); 
	} 
	
	public void addGoblins() 
	{
		int numGoblins = Random.Range(MIN_GOBLINS, MAX_GOBLINS+1); 
		for (int i = 0; i < numGoblins; i++) { 
			GameObject newGoblin = GameObject.Instantiate(PlayState.instance.goblinPrefab) as GameObject; 
			newGoblin.transform.parent = PlayState.instance.transform; 
			GridPiece goblin = newGoblin.GetComponent<GridPiece>(); 
			addPiece(goblin); 
		} 
	} 
	
	public void addSpikes() 
	{ 
		int numSpikes = Random.Range(MIN_SPIKES, MAX_SPIKES+1); 
		for (int i = 0; i < numSpikes; i++) { 
			GameObject newSpike = GameObject.Instantiate(PlayState.instance.spikePrefab) as GameObject; 
			newSpike.transform.parent = PlayState.instance.transform; 
			GridPiece spike = newSpike.GetComponent<GridPiece>(); 
			addPiece(spike); 
		} 
	} 
	
	public bool checkForPlayerExit(Vector2 playerGridPos) 
	{
		if (_upExit != null && playerGridPos.y >= Globals.ROOM_HEIGHT) { 
			Vector2 newPlayerPos = new Vector2(playerGridPos.x, playerGridPos.y % Globals.ROOM_HEIGHT); 
			PlayState.instance.switchRoom(_upExit, newPlayerPos); 
			return true; 
		} 
		else if (_rightExit != null && playerGridPos.x >= Globals.ROOM_WIDTH) { 
			Vector2 newPlayerPos = new Vector2(playerGridPos.x % Globals.ROOM_WIDTH, playerGridPos.y); 
			PlayState.instance.switchRoom(_rightExit, newPlayerPos);
			return true; 
		}
		else if (_downExit != null && playerGridPos.y < 0) { 
			Vector2 newPlayerPos = new Vector2(playerGridPos.x, (playerGridPos.y % Globals.ROOM_HEIGHT) + Globals.ROOM_HEIGHT); 
			PlayState.instance.switchRoom(_downExit, newPlayerPos); 
			return true; 
		} 
		else if (_leftExit != null && playerGridPos.x < 0) { 
			Vector2 newPlayerPos = new Vector2((playerGridPos.x % Globals.ROOM_WIDTH) + Globals.ROOM_WIDTH, playerGridPos.y); 
			PlayState.instance.switchRoom(_leftExit, newPlayerPos); 
			return true;
		}
		
		return false;
	} 
	
	public int makeLockedConnection(uint dir, Room otherRoom, SpawnedPuzzleItem lockedDoor)
	{ 
		return makeLockedConnection(dir, otherRoom, lockedDoor, -1); 
	} 
	
	public int makeLockedConnection(uint dir, Room otherRoom, int doorIndex)
	{ 
		return makeLockedConnection(dir, otherRoom, null, doorIndex); 
	} 
	
	public int makeLockedConnection(uint dir, Room otherRoom, SpawnedPuzzleItem lockedDoor, int doorIndex)
	{ 
		if (dir == GridPiece.UP) { 
			if (_upExit != null) 
				throw new UnityException("Tried to add two up exits to room!"); 
			_upExit = otherRoom; 
			
			// Carve only a tiny piece out of our room 
			if (doorIndex == -1)
				doorIndex = Random.Range(6, 10); 
			GridPiece wallToRemove = null; 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.y == Globals.ROOM_HEIGHT-1 && piece.gridPos.x == doorIndex)
					wallToRemove = piece;
			} 
			_gridPieces.Remove(wallToRemove); 
			GameObject.Destroy(wallToRemove.gameObject); 
			if (lockedDoor != null)
				addPiece(lockedDoor, new Vector2(doorIndex, Globals.ROOM_HEIGHT-1));
			return doorIndex; 
		}
		else if (dir == GridPiece.RIGHT) { 
			if (_rightExit != null) 
				throw new UnityException("Tried to add two right exits to room!"); 
			_rightExit = otherRoom; 
			
			if (doorIndex == -1)
				doorIndex = Random.Range(3, 7); 
			GridPiece wallToRemove = null; 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.x == Globals.ROOM_WIDTH-1 && piece.gridPos.y == doorIndex)
					wallToRemove = piece; 
			} 
			_gridPieces.Remove(wallToRemove); 
			GameObject.Destroy(wallToRemove.gameObject); 
			if (lockedDoor != null)
				addPiece(lockedDoor, new Vector2(Globals.ROOM_WIDTH-1, doorIndex)); 
			return doorIndex; 
		} 
		else if (dir == GridPiece.DOWN) { 
			if (_downExit != null) 
				throw new UnityException("Tried to add two down exits to room!"); 
			_downExit = otherRoom; 
			
			// Carve only a tiny piece out of our room 
			if (doorIndex == -1)
				doorIndex = Random.Range(6, 10); 
			GridPiece wallToRemove = null; 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.y == 0 && piece.gridPos.x == doorIndex)
					wallToRemove = piece;
			} 
			_gridPieces.Remove(wallToRemove); 
			GameObject.Destroy(wallToRemove.gameObject); 
			if (lockedDoor != null)
				addPiece(lockedDoor, new Vector2(doorIndex, 0)); 
			return doorIndex; 
		}
		else if (dir == GridPiece.LEFT) { 
			if (_leftExit != null) 
				throw new UnityException("Tried to add two left exits to room!"); 
			_leftExit = otherRoom; 
			if (doorIndex == -1)
				doorIndex = Random.Range(3, 7); 
			GridPiece wallToRemove = null; 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.x == 0 && piece.gridPos.y == doorIndex)
					wallToRemove = piece; 
			} 
			_gridPieces.Remove(wallToRemove); 
			GameObject.Destroy(wallToRemove.gameObject);
			if (lockedDoor != null)
				addPiece(lockedDoor, new Vector2(0, doorIndex)); 
			return doorIndex; 
		}
		return -1;
	} 
	
	public void makeSpatialConnection(uint dir, Room otherRoom)
	{ 
		if (dir == GridPiece.UP) { 
			if (_upExit != null) 
				throw new UnityException("Tried to add two up exits to room!"); 
			
			_upExit = otherRoom; 
			
			// Carve a little piece out of our wall 
			List<GridPiece> wallsToRemove = new List<GridPiece>(); 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.y == Globals.ROOM_HEIGHT-1 && piece.gridPos.x > 5 && piece.gridPos.x < 10)
					wallsToRemove.Add(piece); 
			}
			foreach (GridPiece piece in wallsToRemove) { 
				_gridPieces.Remove(piece); 
				GameObject.Destroy(piece.gameObject);
			} 
		}
		else if (dir == GridPiece.RIGHT) { 
			if (_rightExit != null)
				throw new UnityException("Tried to add two right exits to room!"); 
			
			_rightExit = otherRoom; 
			// Carve a little piece out of our wall 
			List<GridPiece> wallsToRemove = new List<GridPiece>(); 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.x == Globals.ROOM_WIDTH-1 && piece.gridPos.y > 2 && piece.gridPos.y < 7)
					wallsToRemove.Add(piece); 
			}
			foreach (GridPiece piece in wallsToRemove) { 
				_gridPieces.Remove(piece); 
				GameObject.Destroy(piece.gameObject);
			} 
		}
		else if (dir == GridPiece.DOWN) { 
			if (_downExit != null) 
				throw new UnityException("Tried to add two down exits to room!"); 
			
			_downExit = otherRoom; 
			
			// Carve a little piece out of our wall 
			List<GridPiece> wallsToRemove = new List<GridPiece>(); 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.y == 0 && piece.gridPos.x > 5 && piece.gridPos.x < 10)
					wallsToRemove.Add(piece); 
			}
			foreach (GridPiece piece in wallsToRemove) { 
				_gridPieces.Remove(piece); 
				GameObject.Destroy(piece.gameObject);
			} 	
		}
		else if (dir == GridPiece.LEFT) { 
			if (_leftExit != null)
				throw new UnityException("Tried to add two left exits to room!"); 
			
			_leftExit = otherRoom; 
			// Carve a little piece out of our wall 
			List<GridPiece> wallsToRemove = new List<GridPiece>(); 
			foreach (GridPiece piece in _gridPieces) { 
				if (piece.gridPos.x == 0 && piece.gridPos.y > 2 && piece.gridPos.y < 7)
					wallsToRemove.Add(piece); 
			}
			foreach (GridPiece piece in wallsToRemove) { 
				_gridPieces.Remove(piece); 
				GameObject.Destroy(piece.gameObject);
			} 	
		} 
	}
	
	
	public void addPiece(GridPiece piece, Vector2 gridPos) 
	{ 
		Vector2 actualPos = PlayState.instance.toActualCoordinates(gridPos); 
		piece.transform.parent = PlayState.instance.transform; 
		piece.x = actualPos.x; 
		piece.y = actualPos.y; 
		piece.init(); 
		_gridPieces.Add(piece); 
	} 
	
	public void addPiece(GridPiece piece) 
	{ 
		piece.init(); 
		
		uint[,] typeGrid = new uint[Globals.ROOM_WIDTH, Globals.ROOM_HEIGHT]; 
		for (int i = 0; i < Globals.ROOM_WIDTH; i++) { 
			for (int j = 0; j < Globals.ROOM_HEIGHT; j++) { 
				typeGrid[i, j] = 0; 	
			} 
		}
		foreach (GridPiece existingPiece in _gridPieces) { 
			typeGrid[(int)existingPiece.gridPos.x, (int)existingPiece.gridPos.y] = existingPiece.type;	
		} 
			
		// If the piece is not a wall, can insert it in any spot. 
		if (!piece.hasType(GridPiece.WALL_TYPE)) { 
			List<Vector2> emptyTiles = getEmptyTiles(typeGrid); 
			if (emptyTiles.Count == 0)
				throw new UnityException("Overflow in room!"); 
			Vector2 tile = Globals.getRandom(emptyTiles); 
			Vector2 actualPos = PlayState.instance.toActualCoordinates(tile); 
			piece.transform.parent = PlayState.instance.transform;
			piece.x = actualPos.x; 
			piece.y = actualPos.y;
			_gridPieces.Add(piece); 
		} 
		// Otherwise, have to make sure it's not next to a wall or edge. 
		else { 
			List<Vector2> spaciousTiles = getSpaciousTiles(typeGrid); 
			if (spaciousTiles.Count == 0)
				throw new UnityException("Overflow in room!"); 
			Vector2 tile = Globals.getRandom(spaciousTiles); 
			Vector2 actualPos = PlayState.instance.toActualCoordinates(tile); 
			piece.transform.parent = PlayState.instance.transform;
			piece.x = actualPos.x; 
			piece.y = actualPos.y;
			_gridPieces.Add(piece); 
		}
		piece.resetGridPos();
		
	} 
	
	protected List<Vector2> getEmptyTiles(uint[,] typeGrid) 
	{ 
		List<Vector2> emptyTiles = new List<Vector2>(); 
		for (int i = 0; i < Globals.ROOM_WIDTH; i++) { 
			for (int j = 0; j < Globals.ROOM_HEIGHT; j++) { 
				if (typeGrid[i, j] == 0)
					emptyTiles.Add(new Vector2(i, j)); 
			} 
		} 
		return emptyTiles; 
	}
	
	protected List<Vector2> getSpaciousTiles(uint[,] typeGrid) 
	{ 
		List<Vector2> spaciousTiles = new List<Vector2>(); 
		for (int i = 0; i < Globals.ROOM_WIDTH; i++) { 
			for (int j = 0; j < Globals.ROOM_HEIGHT; j++) { 
				if (typeGrid[i, j] != 0 || i == 0 || i == Globals.ROOM_WIDTH-1 || j == 0 || j == Globals.ROOM_HEIGHT-1)
					continue; 
				// Make sure that all surrounding tiles are empty
				bool allEmpty = true; 
				for (int xCor = Mathf.Max(0, i-1); xCor <= Mathf.Min(i+1, Globals.ROOM_WIDTH-1); xCor++) { 
					for (int yCor = Mathf.Max(0, j-1); yCor <= Mathf.Min(j+1, Globals.ROOM_HEIGHT-1); yCor++) { 
						if ((typeGrid[xCor, yCor] & GridPiece.WALL_TYPE) != 0)
							allEmpty = false; 
					} 
				}
				if (allEmpty)
					spaciousTiles.Add(new Vector2(i, j)); 
			} 	
		} 
		return spaciousTiles; 
	} 
	
	
	
	
}
