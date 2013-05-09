using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using puzzlegen.database;

namespace puzzlegen.buildingblocks 
{

	public class FilterBlock : BuildingBlock 
	{
		protected BuildingBlock _input; 
		
		protected Dictionary<string, object> _propertiesToFilter; 
		
		public override bool outputHasContainer ()
		{
			return _input.outputHasContainer(); 
		}
		
		public override int outputSpawnIndex ()
		{
			return _input.outputSpawnIndex(); 
		}
		
		public FilterBlock(BuildingBlock input, Dictionary<string, object> propertiesToFilter) : base ( new List<BuildingBlock>() { input })
		{ 
			_input = input; 
			_propertiesToFilter = propertiesToFilter; 
		} 
		
		// For simplicity, override the generate puzzle function 
		public override PuzzleOutput generatePuzzle (string outputName, Dictionary<string, object> desiredOutputProperties)
		{
			if (_verbose) Debug.Log(string.Format("Filtering {0}", outputName)); 
			DBItem dbitem = Database.Instance.getItem(outputName); 
			if (dbitem == null || !satisfiesConstraints(dbitem)) { 
				if (_verbose) Debug.Log(string.Format("Item {0} did not pass the filter.", outputName)); 
				return null; 
			}
			return _input.generatePuzzle(outputName, desiredOutputProperties); 
		}
		
		protected bool satisfiesConstraints(DBItem dbitem) 
		{ 
			foreach (string propertyName in _propertiesToFilter.Keys) { 
				if (!dbitem.propertyExists(propertyName))
					return false; 
				else if (_propertiesToFilter[propertyName] != null) { 
					if (dbitem.getProperty(propertyName) is List<string>) { 
						if (!(dbitem.getProperty(propertyName) as List<string>).Contains(_propertiesToFilter[propertyName] as string))
							return false; 
						
					}
					else if (!_propertiesToFilter[propertyName].Equals(dbitem.getProperty(propertyName))) {
						Debug.Log(string.Format("Item {0} failed filter for property {1} expected: {2} actual: {3}", dbitem.ClassName, propertyName, _propertiesToFilter[propertyName], dbitem.getProperty(propertyName)));
						return false; 
					}
				} 
			}
			return true; 
		} 
		
	}
	
}
