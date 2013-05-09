using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

namespace puzzlegen.buildingblocks
{ 

	public class CombineContainerPuzzle : BuildingBlock 
	{
		private BuildingBlock _outputPuzzleMap; 
		
		public CombineContainerPuzzle(BuildingBlock containerInput, BuildingBlock carryableInput, BuildingBlock nonCarryableInput) : base(new List<BuildingBlock>() { })
		{ 
			// Filter to generate a carryable container 
			Dictionary<string, object> containerFilterProps = new Dictionary<string, object>(); 
			containerFilterProps["carryable"] = true; 
			FilterBlock containerFilter = new FilterBlock(containerInput, containerFilterProps); 
			// Filter to make sure the item in the container is not carryable
			Dictionary<string, object> carryableFilterProps = new Dictionary<string, object>(); 
			carryableFilterProps["carryable"] = false; 
			FilterBlock carryableFilter = new FilterBlock(carryableInput, carryableFilterProps); 
			// Filter to make sure the item not in the container is not carryable
			Dictionary<string, object> nonCarryableFilterProps = new Dictionary<string, object>(); 
			nonCarryableFilterProps["carryable"] = false; 
			FilterBlock nonCarryableFilter = new FilterBlock(nonCarryableInput, nonCarryableFilterProps); 
			InsertionPuzzle insertStep = new InsertionPuzzle(containerFilter, carryableFilter); 
			_outputPuzzleMap = new CombinePuzzle(nonCarryableFilter, new UnboxingPuzzle(insertStep)); 
		} 
		
		public override PuzzleOutput generatePuzzle (string outputName, Dictionary<string, object> desiredOutputProperties)
		{
			if (_verbose) Debug.Log(string.Format("Generating Combine Container Puzzle for {0}", outputName)); 
			return _outputPuzzleMap.generatePuzzle(outputName, desiredOutputProperties); 
		}
		
		public override void despawnItems ()
		{
			_outputPuzzleMap.despawnItems();
		}
		
	}
	
}
