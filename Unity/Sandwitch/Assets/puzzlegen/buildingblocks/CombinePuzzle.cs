using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen.database; 
using puzzlegen.relationship; 

namespace puzzlegen.buildingblocks 
{

	public class CombinePuzzle : BuildingBlock
	{
		private BuildingBlock _input1, _input2; 
		
		public CombinePuzzle(BuildingBlock input1, BuildingBlock input2) : base(new List<BuildingBlock> { input1, input2 })
		{	
			_input1 = input1; 
			_input2 = input2; 
		}
		
		protected override bool spawnFilteredOutput (string outputName)
		{
			if (_verbose) Debug.Log("Generating Combine Puzzle for " + outputName); 
			return base.spawnFilteredOutput (outputName);
		}
		
		protected override bool spawnFilteredInputs (string outputName)
		{
			DBItem rewardDBItem = Database.Instance.getItem(outputName); 
			if (!rewardDBItem.propertyExists("madeby")) {
				if (_verbose) Debug.Log("Failed to generate combine puzzle: Reward could not be the result of combination"); 
				return false; 
			}
			List<KeyValuePair<string, string>> workingCombinations = rewardDBItem.getProperty("madeby") as List<KeyValuePair<string, string>>; 
			List<KeyValuePair<string, string>> filteredCombinations = filterWorkingCombinations(workingCombinations); 
			if (filteredCombinations.Count == 0)  { 
				if (_verbose) Debug.Log("Failed to generate combine puzzle: No working combinations fit constraints"); 
				return false; 
			} 
			
			// Now randomly shuffle our working combinations 
			BuildingBlock.shuffle(filteredCombinations); 
			// and try each combination 
			foreach (KeyValuePair<string, string> itemPair in filteredCombinations) { 
				// Unpack the "Tuples" 
				string itemName1 = itemPair.Key; 
				string itemName2 = itemPair.Value; 
				
				PuzzleOutput possibleInput1 = _input1.generatePuzzle(itemName1); 
				PuzzleOutput possibleInput2 = _input2.generatePuzzle(itemName2); 
				
				if (possibleInput1 == null || possibleInput2 == null) { 
					if (_verbose) Debug.Log("Failed to generate combine puzzle using " + itemName1 + " and " + itemName2); 	
					Debug.Log(string.Format("input1: {0}, input2: {1}", possibleInput1, possibleInput2));
					_input1.despawnItems(); 
					_input2.despawnItems(); 
					continue; 
				} 
				else if (!BuildingBlock.areCarryable(new List<string>() { itemName1, itemName2}, new List<BuildingBlock>() { _input1, _input2 })) { 
					if (_verbose) Debug.Log("Failed to generate combine puzzle using " + itemName1 + " and " + itemName2 + " because neither item is carryable to the other item."); 
					_input1.despawnItems(); 
					_input2.despawnItems(); 
					continue; 
				} 
				// Success!
				else { 
					if (_verbose) Debug.Log("Successfully generated combine puzzle for " + outputName + " with  " + itemName1 + " and " + itemName2); 
					// Add all the items spawned by our inputs 
					_itemsToSpawn.AddRange(possibleInput1.Items); 
					_itemsToSpawn.AddRange(possibleInput2.Items); 
					_relationshipsToSpawn.AddRange(possibleInput1.Relationships); 
					_relationshipsToSpawn.AddRange(possibleInput2.Relationships); 

					// Generate the essential combine relationship
					CombineRelationship combine = new CombineRelationship(itemName1, _input1.outputSpawnIndex(), itemName2, _input2.outputSpawnIndex(), _spawnedOutput as PuzzleItem); 
					_relationshipsToSpawn.Add(combine); 
					
					return true; 	
				} 
				
			}
			if (_verbose) Debug.Log("Failed to generate combine puzzle: no working combinations worked!"); 
			
			return false; 
		}
		
		private List<KeyValuePair<string, string>> filterWorkingCombinations(List<KeyValuePair<string, string>> workingCombinations) 
		{ 
			List<KeyValuePair<string, string>> returnVal = new List<KeyValuePair<string, string>>(); 
			// iterate through the pairs 
			foreach (KeyValuePair<string, string> itemPair in workingCombinations) { 
				// If the itemPairs aren't set up correctly, we should very justifiably crash here 
				string itemName1 = itemPair.Key; 
				string itemName2 = itemPair.Value as string; 
				
				DBItem dbItem1 = Database.Instance.getItem(itemName1); 
				DBItem dbItem2 = Database.Instance.getItem(itemName2); 
				
				if (!dbItem1.Spawned && !dbItem2.Spawned) {
					returnVal.Add(new KeyValuePair<string, string> (itemName1, itemName2)); 
					returnVal.Add(new KeyValuePair<string, string> (itemName2, itemName1)); 
				} 
			} 
			return returnVal; 
		} 
		
	}
}
