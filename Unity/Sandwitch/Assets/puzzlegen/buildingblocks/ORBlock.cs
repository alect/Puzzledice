using UnityEngine;
using System.Collections;
using System.Collections.Generic;

namespace puzzlegen.buildingblocks
{ 
	public class ORBlock : BuildingBlock 
	{
		protected BuildingBlock _option1, _option2;
		
		protected BuildingBlock _spawnedOption = null; 
		
		public ORBlock(BuildingBlock option1, BuildingBlock option2) : base(new List<BuildingBlock> () { option1, option2 })
		{ 
			_option1 = option1; 
			_option2 = option2;
		}
		
		public override bool outputHasContainer ()
		{
			// FLAG: For OR blocks, since we don't have control over which output they choose, 
			// both outputs have to have a container input for the or block to say it has a container input
			return _option1.outputHasContainer() && _option2.outputHasContainer(); 
		}
		
		public override int outputSpawnIndex ()
		{
			return _spawnedOption.outputSpawnIndex(); 
		}
		
		public override PuzzleOutput generatePuzzle (string outputName, System.Collections.Generic.Dictionary<string, object> desiredOutputProperties)
		{
			if (_verbose) Debug.Log(string.Format("Generating OR Block")); 
			if (Random.value >= 0.5f) { 
			 	// Try option 1 first 
				if (_verbose) Debug.Log("TRYING OPTION 1");
				PuzzleOutput maybeOutput = _option1.generatePuzzle(outputName, desiredOutputProperties); 
				if (maybeOutput != null) { 
					_spawnedOption = _option1; 
					return maybeOutput;
				}
				// Next try option 2
				if (_verbose) Debug.Log("TRYING OPTION 2");
				maybeOutput = _option2.generatePuzzle(outputName, desiredOutputProperties); 
				if (maybeOutput != null) { 
					_spawnedOption = _option2; 
					return maybeOutput;
				}
				return null; 
			}
			else { 
				// Try option 2 first 
				if (_verbose) Debug.Log("TRYING OPTION 2");
				PuzzleOutput maybeOutput = _option2.generatePuzzle(outputName, desiredOutputProperties); 
				if (maybeOutput != null) { 
					_spawnedOption = _option2; 
					return maybeOutput;
				}
				// Next try option 1
				if (_verbose) Debug.Log("TRYING OPTION 1");
				maybeOutput = _option1.generatePuzzle(outputName, desiredOutputProperties); 
				if (maybeOutput != null) { 
					_spawnedOption = _option1; 
					return maybeOutput;
				}
				return null; 	
			}
		}
		
		public override void despawnItems ()
		{
			if (_spawnedOption != null)
				_spawnedOption.despawnItems(); 
			_spawnedOption = null;
		}
		
		
	}
}
