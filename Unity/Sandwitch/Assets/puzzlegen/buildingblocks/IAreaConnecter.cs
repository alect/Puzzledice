using UnityEngine;
using System.Collections;
using puzzlegen.relationship;

namespace puzzlegen.buildingblocks {

	public interface IAreaConnector 
	{
		string Name(); 
		PuzzleOutput areaGeneratePuzzle(BuildingBlock buildingBlockToBind); 	
		void areaDespawnItems(BuildingBlock possibleBind);
		AreaConnectionRelationship makeConnection(IAreaConnector otherArea);
	}
	
}
