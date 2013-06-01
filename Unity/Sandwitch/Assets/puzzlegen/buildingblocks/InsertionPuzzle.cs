using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using puzzlegen.database;
using puzzlegen.relationship;


namespace puzzlegen.buildingblocks 
{ 

	public class InsertionPuzzle : BuildingBlock 
	{
		protected BuildingBlock _containerInput; 
		protected BuildingBlock _itemToInsertInput; 
		
		public override int outputSpawnIndex ()
		{
			return _containerInput.outputSpawnIndex(); 
		}
		
		public InsertionPuzzle(BuildingBlock containerInput, BuildingBlock itemToInsertInput) : base(new List<BuildingBlock>() { containerInput, itemToInsertInput })
		{ 
			_containerInput = containerInput; 
			_itemToInsertInput = itemToInsertInput; 
		}
		
		protected override bool spawnFilteredOutput (string outputName)
		{
			
			if (_verbose) Debug.Log(string.Format("Generating Insertion puzzle for {0}", outputName)); 
			if (!Database.Instance.itemExists(outputName)) { 
				if (_verbose) Debug.Log(string.Format("WARNING: Tried to access item that does not exist in database: {0}", outputName)); 
				return false; 
			} 
			DBItem dbReward = Database.Instance.getItem(outputName); 
			if (dbReward.Spawned) { 
				if (_verbose) Debug.Log(string.Format("Failed to generate Insertion Puzzle: {0} already spawned", outputName)); 
				return false; 
			} 
			return true; 
		}
		
		protected override bool spawnFilteredInputs (string outputName)
		{
			// First determine if our desiredProperties specifically has any items it wants to contain 
			DBItem dbOutput = Database.Instance.getItem(outputName); 
			List<string> possibleFillers; 
			if (_desiredOutputProperties["contains"] != null)
				possibleFillers = new List<string>(_desiredOutputProperties["contains"] as List<string>); 
			else if (dbOutput.propertyExists("filledby"))
				possibleFillers = new List<string>(dbOutput.getProperty("filledby") as List<string>); 
			else { 
				if (_verbose) Debug.Log("Failed to generate insertion puzzle. No possible fillers worked."); 
				return false; 
			} 
			
			BuildingBlock.shuffle(possibleFillers); 
			foreach (string fillerName in possibleFillers) { 
				if (!areCarryable(new List<string>() { outputName, fillerName }, new List<BuildingBlock>() { _containerInput, _itemToInsertInput }))
					continue; 
				
				// Update the properties we're passing to our inputs 
				Dictionary<string, object> newDesiredOutputProperties = new Dictionary<string, object>(); 
				foreach (string key in _desiredOutputProperties.Keys) { 
					newDesiredOutputProperties[key] = _desiredOutputProperties[key];	
				} 
				// Make sure we can open our item 
				newDesiredOutputProperties["open"] = true; 
				if (newDesiredOutputProperties["contains"] != null) { 
					List<string> fillerArray = new List<string>(newDesiredOutputProperties["contains"] as List<string>);
					fillerArray.Remove(fillerName); 
					if (fillerArray.Count == 0) 
						newDesiredOutputProperties.Remove("contains"); 
					else 
						newDesiredOutputProperties["contains"] = fillerArray; 
				} 
				
				// Now try to generate the inputs 
				Dictionary<string, object> innerItemProps = new Dictionary<string, object>(); 
				if (_desiredOutputProperties.ContainsKey("innerItemProps"))
					innerItemProps = _desiredOutputProperties["innerItemProps"] as Dictionary<string, object>; 
				PuzzleOutput possibleContainerInput = _containerInput.generatePuzzle(outputName, newDesiredOutputProperties); 
				PuzzleOutput possibleFillerInput = _itemToInsertInput.generatePuzzle(fillerName, innerItemProps); 
				if (possibleContainerInput == null || possibleFillerInput == null) { 
					if (_verbose) Debug.Log(string.Format("Failed to generate insertion puzzle with {0} as the container and {1} as the filler.", outputName, fillerName)); 
					_containerInput.despawnItems(); 
					_itemToInsertInput.despawnItems(); 
					continue; 
				} 
				
				if (_verbose) Debug.Log(string.Format("Successfully generated insertion puzzle with {0} as container and {1} as the filler.", outputName, fillerName)); 
				_itemsToSpawn.AddRange(possibleContainerInput.Items); 
				_itemsToSpawn.AddRange(possibleFillerInput.Items); 
				_relationshipsToSpawn.AddRange(possibleContainerInput.Relationships); 
				_relationshipsToSpawn.AddRange(possibleFillerInput.Relationships); 
				
				// Add an insertion relationship here
				InsertionRelationship insertionRelationship = new InsertionRelationship(fillerName, _itemToInsertInput.outputSpawnIndex(), outputName, _containerInput.outputSpawnIndex()); 
				_relationshipsToSpawn.Add(insertionRelationship); 
				
				return true; 
				
			}
			
			if (_verbose) Debug.Log("Failed to generate insertion puzzle. No possible fillers worked."); 
			return false; 
			
		}
	}
}
