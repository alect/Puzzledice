using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using puzzlegen.database;
using puzzlegen.relationship;

namespace puzzlegen.buildingblocks 
{ 

	public class DoorUnlockPuzzle : BuildingBlock, IAreaConnector 
	{
		// The door unlock puzzle only takes in one block as input to produce its key 
		protected BuildingBlock _keyInput; 
		
		// The area in which the door starts 
		protected Area _connectingArea; 
		
		// Keeping a record of our key name 
		protected string _keyName; 
		
		public DoorUnlockPuzzle(BuildingBlock keyInput, Area connectingArea) : base(new List<BuildingBlock>() { keyInput })
		{
			_keyInput = keyInput; 
			_connectingArea = connectingArea; 
		}
		
		public PuzzleOutput areaGeneratePuzzle (BuildingBlock buildingBlockToBind)
		{
			return generatePuzzle((buildingBlockToBind as Area).name, new Dictionary<string, object>()); 
		}
		
		public void areaDespawnItems (BuildingBlock possibleBind)
		{
			if (_verbose) Debug.Log("Despawning Door Unlock Puzzle"); 
			_keyInput.despawnItems(); 
			_connectingArea.areaDespawnItems(this); 
		}
		
		protected override bool spawnFilteredOutput (string outputName)
		{
			// Makeshift way of making unique keys 
			_keyName = outputName + " key"; 
			if (Database.Instance.itemExists(_keyName)) { 
				DBItem dbKey = Database.Instance.getItem(_keyName); 
				// If our key has been spawned already, we're pretty much borked 
				if (dbKey.Spawned) { 
					if (_verbose) Debug.Log("Failed to generate door unlock puzzle: key already spawned."); 
					return false; 
				}
				return (dbKey != null); 
			} 
			else { 
				DBItem dbKey = Database.Instance.copyItem("key", _keyName); 
				return (dbKey != null); 
			} 
		}
		
		protected override bool spawnFilteredInputs (string outputName)
		{
			if (!isCarryable(_keyName, _keyInput)) { 
				if (_verbose) Debug.Log("Failed to generate door unlock puzzle: key was not carryable."); 
				return false; 
			} 
			
			// Try to generate our key 
			PuzzleOutput possibleKeyInput = _keyInput.generatePuzzle(_keyName); 
			if (possibleKeyInput == null) { 
				if (_verbose) Debug.Log("Failed to generate door unlock puzzle: key input failed"); 
				return false; 
			} 
			
			// Make sure our connecting area is valid 
			PuzzleOutput areaValidation = _connectingArea.areaGeneratePuzzle(this); 
			if (areaValidation == null) { 
				_keyInput.despawnItems(); 
				if (_verbose) Debug.Log("Failed to generate door unlock puzzle: room of origin failed"); 
				return false; 
			} 
			
			if (_verbose) Debug.Log(string.Format("Successfully generated door unlock puzzle with {0} as a key", _keyName)); 
			_itemsToSpawn.AddRange(possibleKeyInput.Items); 
			_relationshipsToSpawn.AddRange(possibleKeyInput.Relationships); 
			_itemsToSpawn.AddRange(areaValidation.Items); 
			_relationshipsToSpawn.AddRange(areaValidation.Relationships); 
			
			return true; 
			
		}
		
		public AreaConnectionRelationship makeConnection (IAreaConnector otherArea)
		{
			return new AreaConnectionRelationship(_connectingArea.name, otherArea.Name(), true, _keyName, _keyInput.outputSpawnIndex()); 
		}
		
		public string Name ()
		{
			return null;
		}
		
	
	}
	
} 