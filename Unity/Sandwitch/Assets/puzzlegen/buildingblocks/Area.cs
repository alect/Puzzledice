using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen.database; 
using puzzlegen.relationship; 

namespace puzzlegen.buildingblocks 
{ 
	
	public class Area : BuildingBlock, IAreaConnector
	{
		// Inputs to an area can be a little goofy 
		protected List<IAreaConnector> _inputs; 
		protected IAreaConnector _input; 
		
		protected string _name; 
		public string name { get { return _name; } }
		
		protected BuildingBlock _boundBuildingBlock; 
		
		public Area(string name, IAreaConnector input) : base(new List<BuildingBlock>())
		{ 
			_name = name; 
			_boundBuildingBlock = null;  
			_inputs = new List<IAreaConnector>() {input}; 
		} 
		
		public Area(string name, List<IAreaConnector> inputs) : base(new List<BuildingBlock>())
		{
			_name = name; 
			_boundBuildingBlock = null; 
			_inputs = inputs; 
		}
		
		
		public PuzzleOutput areaGeneratePuzzle(BuildingBlock buildingBlockToBind) 
		{ 
			if (_verbose) Debug.Log("spawning Area: " + _name); 
			if (_boundBuildingBlock != null) { 
				if (_verbose) Debug.Log("Area already bound to another building block!"); 
				return new PuzzleOutput(); 
			} 
			else { 
				BuildingBlock.shuffle(_inputs); 
				foreach (IAreaConnector input in _inputs) { 
				
					PuzzleOutput possibleInput = input.areaGeneratePuzzle(this); 
					if (possibleInput == null) 
						continue; 
					_input = input; 
					_boundBuildingBlock = buildingBlockToBind; 
					PuzzleOutput 	result = new PuzzleOutput(); 
					result.Items.AddRange(possibleInput.Items); 
					result.Relationships.AddRange(possibleInput.Relationships); 
				
					// Add an area connection relationship here 
					AreaConnectionRelationship connectionRelationship = input.makeConnection(this); 
					result.Relationships.Add(connectionRelationship); 
				
					return result; 
				}
				return null; 
				
			} 
		}
		
		public AreaConnectionRelationship makeConnection (IAreaConnector otherArea)
		{
			return new AreaConnectionRelationship(_name, otherArea.Name()); 
		}
		
		public string Name ()
		{
			return _name; 
		}
		
		public void areaDespawnItems(BuildingBlock possibleBind) 
		{
			if (_verbose) Debug.Log(string.Format("Attempting to despawn items for area {0}", _name)); 
			if (possibleBind == _boundBuildingBlock) { 
				if (_verbose) Debug.Log(string.Format("Successfully despawning items for area {0}", _name));
				_input.areaDespawnItems(this); 
				_boundBuildingBlock = null; 
			} 
		}
		
	}
}
