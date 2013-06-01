using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using puzzlegen.database;
using puzzlegen.relationship; 

namespace puzzlegen.buildingblocks 
{ 

	public class PropertyChangePuzzle : BuildingBlock
	{
		protected BuildingBlock _changerInput; 
		protected BuildingBlock _changeeInput; 
		
		
		// For propertychange puzzles we can specifically specify which property we want to change and to what
		protected string _desiredPropertyName; 
		protected object _desiredPropertyVal; 
		// Can also specify whether we want the property change relationship produced by the puzzle to force 
		// a mirror or not 
		// TODO: FLAG figure out what the fuck that means?
		protected bool _forceMirror;  
		
		public PropertyChangePuzzle(BuildingBlock changerInput, BuildingBlock changeeInput, 
									string desiredPropertyName, object desiredPropertyVal, bool forceMirror) : base(new List<BuildingBlock>() { changerInput, changeeInput })
		{ 
			_changerInput = changerInput; 
			_changeeInput = changeeInput; 
			_desiredPropertyName = desiredPropertyName; 
			_desiredPropertyVal = desiredPropertyVal; 
			_forceMirror = forceMirror; 
		} 
		public PropertyChangePuzzle(BuildingBlock changerInput, BuildingBlock changeeInput, string desiredPropertyName, object desiredPropertyVal) 
			: this(changerInput, changeeInput, desiredPropertyName, desiredPropertyVal, false) 
		{ 	
		}
		public PropertyChangePuzzle(BuildingBlock changerInput, BuildingBlock changeeInput) 
			: this(changerInput, changeeInput, null, null, false)
		{
		}
		
		public override bool outputHasContainer ()
		{
			return _changeeInput.outputHasContainer(); 
		}
		
		public override int outputSpawnIndex ()
		{
			return _changeeInput.outputSpawnIndex(); 
		}
		
		protected override bool spawnFilteredOutput (string outputName)
		{
			
			if (_verbose) Debug.Log(string.Format("Generating Property Change Puzzle for {0}", outputName)); 
			
			// For property change puzzles we don't actually want to spawn the output here since the output nees to be spawned
			// (with relevant starting properties) at one of our inputs. 
			if (!Database.Instance.itemExists(outputName)) { 
				if (_verbose) Debug.Log(string.Format("WARNING: Tried to access item that does not exist in database: {0}", outputName)); 
				return false; 
			} 
			DBItem dbOutput = Database.Instance.getItem(outputName); 
			if (dbOutput.Spawned) { 
				if (_verbose) Debug.Log(string.Format("Failed to generate Property Change Puzzle: {0} already spawned", outputName)); 
				return false; 
			} 
			
			return true; 
		}
		
		protected override bool spawnFilteredInputs (string outputName)
		{
			// If we don't have a specific property we're trying to change 
			if (_desiredPropertyName == null) { 
				// Need to first find out whether our desired properties dictionary actually has any elements 
				List<string> desiredPropertyKeys = new List<string>(_desiredOutputProperties.Keys); 
				List<string> possiblePropertyNames = new List<string>(); 
				if (desiredPropertyKeys.Count == 0) 
					possiblePropertyNames = new List<string>(Database.Instance.getItem(outputName).getProperty("mutables") as List<string>);
				else 
					possiblePropertyNames = new List<string>(desiredPropertyKeys); 
				// Shuffle up our property names 
				BuildingBlock.shuffle(possiblePropertyNames); 
				foreach (string propertyName in possiblePropertyNames) { 
					object tryPropertyVal = _desiredPropertyVal; 
					if (_desiredOutputProperties.ContainsKey(propertyName))
						tryPropertyVal = _desiredOutputProperties[propertyName];
					bool maybeSuccess = tryToGeneratePuzzles(outputName, propertyName, tryPropertyVal, null); 
					if (maybeSuccess)
						return true; 
				} 	
			}
			else { 
				bool maybeSuccess = tryToGeneratePuzzles(outputName, _desiredPropertyName, _desiredPropertyVal, null); 
				if (maybeSuccess)
					return true; 
			}
			if (_verbose) Debug.Log("Failed to generate property change puzzle: No combinations worked"); 
			return false;
		}
		
		protected bool tryToGeneratePuzzles(string changeeName, string propertyName, object propertyVal, object startingVal)
		{ 
			if (!Database.Instance.itemExists(changeeName)) { 
				if (_verbose) { 
					Debug.Log("Failed to generate property change puzzle."); 
					Debug.Log(string.Format("WARNING: tried to generate puzzle with item that does not exist in database: {0}", changeeName)); 
				} 
				return false; 
			} 
			
			DBItem dbChangee = Database.Instance.getItem(changeeName); 
			if (!dbChangee.propertyExists(propertyName)) { 
				if (_verbose) Debug.Log(string.Format("Failed to generate property change puzzle. Attempted property: {0} did not exist for {1}", propertyName, changeeName)); 
				return false; 
			} 
			
			List<string> possibleVals = new List<string>(dbChangee.getProperty(propertyName) as List<string>); 
			
			// Now we need to iterate through all of the values instead of just a random choice. 
			// this ensures we try all options 
			if (propertyVal == null) { 
				// shuffle our values 
				BuildingBlock.shuffle(possibleVals); 
				foreach (string maybeVal in possibleVals) { 
					bool maybeGood = tryToGeneratePuzzles(changeeName, propertyName, maybeVal, startingVal); 
					if (maybeGood)
						return true; 
				} 
				if (_verbose) Debug.Log(string.Format("Failed to generate property change puzzle: No desired value for property {0} worked.", propertyName)); 
				return false; 
			} 
			
			// Now we need to remove the desired property from the list of posible values so we can effectively 
			// choose a different starting value 
			possibleVals.Remove(propertyVal as string); 
			
			// Iterate through possible starting values if we don't already have on e
			if (startingVal == null) { 
				if (possibleVals.Count == 0) 
					return false; 
				BuildingBlock.shuffle(possibleVals); 
				foreach (string maybeStartingVal in possibleVals) { 
					bool maybeGood = tryToGeneratePuzzles(changeeName, propertyName, propertyVal, maybeStartingVal); 
					if (maybeGood)
						return true; 
				} 
				if (_verbose) Debug.Log(string.Format("Failed to generate property change puzzle: No possible starting value for property {0} worked.", propertyName)); 
				return false; 
			} 
			
			// Since we've reached this point in the function, we can assume that we have a valid changee name, property name, property value, and starting property value 
			Dictionary<string, object> changeeStartingProp= new Dictionary<string, object>(); 
			// Make sure all the other properties we desire are taken care of 
			foreach (string otherName in _desiredOutputProperties.Keys) { 
				changeeStartingProp[otherName] = _desiredOutputProperties[otherName];	
			} 
			changeeStartingProp[propertyName] = startingVal; 
			PuzzleOutput possibleChangeeInput = _changeeInput.generatePuzzle(changeeName, changeeStartingProp); 
			if (possibleChangeeInput == null) {
				if (_verbose) Debug.Log(string.Format("Failed to generate property change puzzle with {0} as the changee.", changeeName));
				_changeeInput.despawnItems(); 
				return false; 
			} 
			
			if (!dbChangee.propertyExists("changedby")) { 
				if (_verbose) Debug.Log(string.Format("Failed to generate property change puzzle with {0} as the changee. Does not possess the changedby property.")); 
				return false; 
			} 
			
			// Now we choose a random changer 
			List<string> filteredChangers = dbChangee.getProperty("changedby") as List<string>; 
			filteredChangers = getRelevantChangers(filteredChangers, propertyName, propertyVal); 
			// Randomly shuffle our changers 
			BuildingBlock.shuffle(filteredChangers); 
			foreach (string changerName in filteredChangers) { 
				if (!areCarryable(new List<string>() { changeeName, changerName }, new List<BuildingBlock>() { _changeeInput, _changerInput }))
					continue; 
				
				PuzzleOutput possibleChangerInput = _changerInput.generatePuzzle(changerName); 
				if (possibleChangerInput == null) { 
					if (_verbose) Debug.Log(string.Format("Failed to generate property change puzzle with {0} as the changer", changerName)); 
					_changerInput.despawnItems(); 
				} 
				else { 
					onSuccess(changeeName, changerName, possibleChangeeInput, possibleChangerInput, propertyName, propertyVal);
					return true;
				} 
			} 
			if (_verbose) Debug.Log(string.Format("Failed to generate property change puzzle with {0} as the changee. No changers worked.", changeeName)); 
			_changeeInput.despawnItems();
			return false; 
		} 
		
		// A function used to get changers from our list of changers who are capable of changing a desired property name to a desired property value 
		protected List<string> getRelevantChangers(List<string> listOfChangers, string propertyName, object propertyVal)
		{ 	
			List<string> possibleChangers = Database.Instance.filterListByProperty(listOfChangers, "changes"); 
			List<string> finalList = new List<string>(); 
			foreach (string changerName in possibleChangers) { 
				DBItem dbChanger = Database.Instance.getItem(changerName); 
				
				Dictionary<string, List<string>> changesDict = dbChanger.getProperty("changes") as Dictionary<string, List<string>>;
				if (changesDict != null && changesDict.ContainsKey(propertyName) && changesDict[propertyName].Contains((string)propertyVal)) { 
					finalList.Add(changerName); 	
				} 
			} 	
			return finalList; 
		} 
		
		// The function that's called when we actually generate the puzzle. 
		protected void onSuccess(string changeeName, string changerName, PuzzleOutput changeeInput, PuzzleOutput changerInput, string propertyName, object propertyVal)
		{ 
			if (_verbose) Debug.Log(string.Format("Successfully generated property change puzzle with {0} as changer and {1} as changee", changerName, changeeName)); 
			_itemsToSpawn.AddRange(changeeInput.Items); 
			_itemsToSpawn.AddRange(changerInput.Items); 
			_relationshipsToSpawn.AddRange(changeeInput.Relationships); 
			_relationshipsToSpawn.AddRange(changerInput.Relationships); 
			PropertyChangeRelationship changeRelationship = new PropertyChangeRelationship(changeeName, _changeeInput.outputSpawnIndex(), changerName, _changerInput.outputSpawnIndex(), propertyName, propertyVal); 
			_relationshipsToSpawn.Add(changeRelationship); 
		} 
		
		
		
		
	}

}