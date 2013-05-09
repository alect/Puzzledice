using UnityEngine;
using System.Collections;
using System.Collections.Generic; 


using puzzlegen.database; 
using puzzlegen.relationship;

namespace puzzlegen.buildingblocks 
{ 

	public class SpawnPuzzle : BuildingBlock 
	{
		protected Area _myArea; 
		
		public SpawnPuzzle(Area myArea) : base(new List<BuildingBlock>())
		{ 
			_myArea = myArea; 
		} 
		
		protected override bool spawnFilteredOutput (string outputName)
		{
			if (_verbose) Debug.Log(string.Format("Generating SpawnPuzzle for {0}", outputName)); 
			bool success = base.spawnFilteredOutput(outputName); 
			if (success)
				_itemsToSpawn.Add(_spawnedOutput);
			return success; 
		}
		
		protected override bool spawnFilteredInputs (string outputName)
		{
			PuzzleOutput possibleInput = _myArea.areaGeneratePuzzle(this); 
			if (possibleInput == null) { 
				if (_verbose) Debug.Log("Failed to generate Spawn Puzzle. Input Area was unable to generate."); 
				return false; 
			} 
			_itemsToSpawn.AddRange(possibleInput.Items); 
			_relationshipsToSpawn.AddRange(possibleInput.Relationships); 
			(_spawnedOutput as PuzzleItem).setProperty("spawnArea", _myArea.name); 
			return true; 
		}
		
		public override void despawnItems ()
		{
			base.despawnItems ();
			_myArea.areaDespawnItems(this); 
		}
		
	}
}
