using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using puzzlegen.relationship;

namespace puzzlegen.buildingblocks 
{

	public class StartAreaBlock : BuildingBlock, IAreaConnector 
	{
		public StartAreaBlock() : base(new List<BuildingBlock>()) 
		{ 
		} 
		
		public PuzzleOutput areaGeneratePuzzle (BuildingBlock buildingBlockToBind)
		{
			return new PuzzleOutput(); 
		}
		
		public void areaDespawnItems (BuildingBlock possibleBind)
		{
			
		}
		
		public puzzlegen.relationship.AreaConnectionRelationship makeConnection (IAreaConnector otherArea)
		{
			return new StartAreaRelationship(otherArea.Name());
		}
		
		public string Name ()
		{
			return "start";
		}
	}
}
