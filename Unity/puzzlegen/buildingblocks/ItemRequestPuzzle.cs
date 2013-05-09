using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using puzzlegen.database; 
using puzzlegen.relationship;

namespace puzzlegen.buildingblocks {

	public class ItemRequestPuzzle : BuildingBlock 
	{
		// Inputs for both the requester and the requested item 
		protected BuildingBlock _requesterInput; 
		protected BuildingBlock _requestedInput; 
		
		public ItemRequestPuzzle(BuildingBlock requester, BuildingBlock requested) : base(new List<BuildingBlock>() { requester, requested })
		{ 
			_requesterInput = requester; 
			_requestedInput = requested; 
		} 
		
		protected override bool spawnFilteredOutput (string outputName)
		{
			if (_verbose) Debug.Log(string.Format("Generating Item Request Puzzle for {0} as reward", outputName)); 
			return base.spawnFilteredOutput(outputName); 
		}
		
		protected override bool spawnFilteredInputs (string outputName)
		{
			DBItem rewardDBItem = Database.Instance.getItem(outputName); 
			if (!rewardDBItem.propertyExists("givenby")) { 
				if (_verbose) Debug.Log("Failed to generate item request puzzle: Reward could not be result of item request"); 
				return false; 
			} 
			List<string> workingQuestgivers = rewardDBItem.getProperty("givenby") as List<string>;
			List<string> filteredQuestgivers = filterQuestgivers(workingQuestgivers); 
			if (filteredQuestgivers.Count == 0) { 
				if (_verbose) Debug.Log(string.Format("Failed to generate item request puzzle: no questgivers met constraints")); 
				return false; 
			} 
			
			BuildingBlock.shuffle(filteredQuestgivers); 
			foreach (string giverName in filteredQuestgivers) { 
				PuzzleOutput possibleGiverInput = _requesterInput.generatePuzzle(giverName); 
				if (possibleGiverInput == null) { 
					if (_verbose) Debug.Log(string.Format("Failed to generate item request puzzle using {0} as requester", giverName)); 
					_requesterInput.despawnItems(); 
					continue; 
				}
				DBItem giverDBItem = Database.Instance.getItem(giverName); 
				if (!giverDBItem.propertyExists("requests")) { 
					if (_verbose) Debug.Log(string.Format("Failed to generate item request puzzle: requester {0} had no requests", giverName)); 
					_requesterInput.despawnItems(); 
					continue; 
				}
				List<string> requests = new List<string>(giverDBItem.getProperty("requests") as List<string>); 
				BuildingBlock.shuffle(requests); 
				foreach (string requestName in requests) { 
					if (!areCarryable(new List<string>() { giverName, requestName }, new List<BuildingBlock>() { _requesterInput, _requestedInput }))
						continue; 
					
					DBItem dbRequestItem = Database.Instance.getItem(requestName); 
					if (dbRequestItem == null) { 
						if (_verbose) Debug.Log(string.Format("WARNING: tried to access item in database that doesn't exist: {0}", requestName)); 
						continue; 
					} 
					else if (dbRequestItem.Spawned) { 
						if (_verbose) Debug.Log(string.Format("failed to use {0} as a requested item. Item already spawned.", requestName)); 
						continue; 
					} 
					// Now we need to iterate through the mutable properties of the requested item 
					List<string> propertyNames = dbRequestItem.getProperty("mutables") as List<string>; 
					// If we don't have any mutable properties, then just go ahead and generate a normal request 
					if (propertyNames == null || propertyNames.Count == 0) { 
						PuzzleOutput possibleRequestInput = _requestedInput.generatePuzzle(requestName); 
						if (possibleRequestInput == null) { 
							if (_verbose) Debug.Log(string.Format("Failed to generate item request puzzle with {0} as the requested item.", requestName));	
							_requestedInput.despawnItems(); 
						} 
						else { 
							onSuccess(outputName, giverName, requestName, possibleGiverInput, possibleRequestInput); 
							return true; 
						} 
					}
					else { 
						BuildingBlock.shuffle(propertyNames); 
						foreach (string propertyName in propertyNames) { 
							List<string> values = dbRequestItem.getProperty(propertyName) as List<string>; 
							if (values == null) 
								values = new List<string>() { null }; 
							values = new List<string>(values); 
							BuildingBlock.shuffle(values); 
							foreach (object val in values) { 
								Dictionary<string, object> inputDesiredProps = new Dictionary<string, object>(); 
								inputDesiredProps[propertyName] = val;
								PuzzleOutput possibleRequestInput = _requestedInput.generatePuzzle(requestName, inputDesiredProps); 
								if (possibleRequestInput == null) { 
									_requestedInput.despawnItems(); 
									if (_verbose) Debug.Log(string.Format("Failed to generate item request puzzle with {0} as the requested item", requestName)); 
								} 
								else { 
									onSuccess(outputName, giverName, requestName, possibleGiverInput, possibleRequestInput, propertyName, val); 
									return true; 
								} 
							} 
						} 
							
					} 	
				}
				if (_verbose) Debug.Log(string.Format("failed to generate item request puzzle with {0} as the giver. No requests worked", giverName)); 
				_requesterInput.despawnItems(); 
			} 
			if (_verbose) Debug.Log("Failed to generate item request puzzle: No working combinations of quest giver and request worked"); 
			return false; 
			
		}
		
		protected void onSuccess(string outputName, string giverName, string requestName, PuzzleOutput giverInput, PuzzleOutput requestInput, string propertyName, object propertyVal) 
		{ 
			if (_verbose) Debug.Log(string.Format("Successfully generated item request puzzle with {0} as giver and {1} as requested item and {2} as reward.", giverName, requestName, outputName)); 
			_itemsToSpawn.AddRange(giverInput.Items); 
			_itemsToSpawn.AddRange(requestInput.Items); 
			_relationshipsToSpawn.AddRange(giverInput.Relationships); 
			_relationshipsToSpawn.AddRange(requestInput.Relationships); 
			
			ItemRequestRelationship requestRelationship = new ItemRequestRelationship(giverName, _requesterInput.outputSpawnIndex(), requestName, _requestedInput.outputSpawnIndex(), _spawnedOutput, propertyName, propertyVal);
			_relationshipsToSpawn.Add(requestRelationship);
			
		} 
		
		protected void onSuccess(string outputName, string giverName, string requestName, PuzzleOutput giverInput, PuzzleOutput requestInput) 
		{ 
			onSuccess(outputName, giverName, requestName, giverInput, requestInput, null, null); 
		} 
		
		
		// Function to filter out quest-givers who have already been spawned. 
		protected List<string> filterQuestgivers(List<string> workingQuestgivers) 
		{ 
			List<string> returnVal = new List<string>(); 
			foreach (string giverName in workingQuestgivers) { 
				DBItem dbGiver = Database.Instance.getItem(giverName); 
				if (dbGiver == null) { 
					if (_verbose) Debug.Log(string.Format("WARNING: tried to access giver that does not exist in database: {0}", giverName)); 
				} 
				else if (!dbGiver.Spawned)
					returnVal.Add(giverName); 
			} 
			return returnVal; 
		} 
		
	}
	
}
