using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen.database;
using puzzlegen.relationship; 

namespace puzzlegen.buildingblocks 
{ 
	
	
	
	public class UnboxingPuzzle : BuildingBlock
	{
		// The unboxing puzzle only has one input. The input for the container that contains the item 
		protected BuildingBlock _containerInput; 
		
		// The unboxing puzzle needs to create the spawn index for its output itself
		protected int _outputSpawnIndex; 
		
		public UnboxingPuzzle(BuildingBlock containerInput) : base(new List<BuildingBlock>() { containerInput })
		{ 
			_containerInput = containerInput; 
		} 
		
		public override bool outputHasContainer ()
		{
			return true; 
		}
		
		public override int outputSpawnIndex ()
		{
			return _outputSpawnIndex; 
		}
		
		// Like property change and insertion puzzles, we don't actually generate the desired output here. 
		protected override bool spawnFilteredOutput (string outputName)
		{
			if (_verbose) Debug.Log(string.Format("Generating unboxing puzzle for {0}", outputName));
			if (!Database.Instance.itemExists(outputName)) { 
				if (_verbose) Debug.Log(string.Format("WARNING: Tried to generate item not in database: {0}", outputName)); 
				return false; 
			} 
			DBItem dbOutput = Database.Instance.getItem(outputName); 
			if (dbOutput.Spawned) { 
				if (_verbose) Debug.Log("Failed to generate Unboxing Puzzle: output already spawned"); 
				return false; 
			} 
			_outputSpawnIndex = dbOutput.NextSpawnIndex; 
			return true; 
		}
		
		protected override bool spawnFilteredInputs (string outputName)
		{
			DBItem dbOutput = Database.Instance.getItem(outputName); 
			
			// Get the possible boxes to contain our item 
			List<string> possibleContainers = new List<string>(); 
			if (_desiredOutputProperties.ContainsKey("fills"))
				possibleContainers = new List<string>(_desiredOutputProperties["fills"] as List<string>);
			else if (dbOutput.propertyExists("fills"))
				possibleContainers = new List<string>(dbOutput.getProperty("fills") as List<string>); 
			
			BuildingBlock.shuffle(possibleContainers); 
			foreach (string boxName in possibleContainers) { 
				Dictionary<string, object> boxProperties = new Dictionary<string, object>(); 
				boxProperties["open"] = false; 
				boxProperties["contains"] = new List<string>() { outputName };
				boxProperties["innerItemProps"] = new Dictionary<string, object>(_desiredOutputProperties); 
				// Include the spawn index 
				(boxProperties["innerItemProps"] as Dictionary<string, object>)["spawnIndex"] = _outputSpawnIndex; 
				PuzzleOutput possibleInput = _containerInput.generatePuzzle(boxName, boxProperties); 
				if (possibleInput == null) { 
					if (_verbose) Debug.Log(string.Format("Failed to generate unboxing puzzle with {0} as the box and {1} as the item to remove.", boxName, outputName));
					continue; 
				} 
				
				// If we officially succeed 
				if (_verbose) Debug.Log(string.Format("Successfully generated unboxing puzzle with {0} as the box and {1} as the item to remove.", boxName, outputName)); 
				_itemsToSpawn.AddRange(possibleInput.Items); 
				_relationshipsToSpawn.AddRange(possibleInput.Relationships); 
				// Add the insertion relationship
				InsertionRelationship insertionRelationship = new InsertionRelationship(outputName, _outputSpawnIndex, boxName, _containerInput.outputSpawnIndex()); 
				_relationshipsToSpawn.Add(insertionRelationship); 
				
				return true; 
			}
			if (_verbose) Debug.Log("Failed to generate unboxing puzzle. No possible boxes worked."); 
			return false; 
		}
	
	}

}