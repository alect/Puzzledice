using UnityEngine;
using System.Collections;
using System.Collections.Generic; 


namespace puzzlegen 
{

	/// <summary>
/// The Puzzle Item is one kind of output from the puzzle generator. 
///	It represents an item created from a DBItem and given certain specific properties
///	It needs a specific implementation to actually be added to a game. 
///	This is just a sample implementation. 
/// </summary>
	public class PuzzleItem  
	{
		private Dictionary<string, object> _properties; 
	
		private string _name; 
		public string Name { 
			get { 
				if (_properties.ContainsKey("key") && (bool)_properties["key"])
					return _properties["keyname"] as string; 
				else
					return _name;
			}
		}
		
		// To distinguish between different spawns of the same item
		private int _spawnIndex; 
		public int SpawnIndex { 
			get { 
				return _spawnIndex;
			}
		}
		
	
		public PuzzleItem(string name) 
		{
			_name = name;  
			_properties = new Dictionary<string, object>(); 
		}
	
		public void setProperty(string propertyName, object propertyVal)
		{
			if (propertyName == "spawnIndex")
				_spawnIndex = (int)(propertyVal);
			
			_properties[propertyName] = propertyVal; 
		}
	
		public bool propertyExists(string propertyName) 
		{
			return _properties.ContainsKey(propertyName); 
		}
		
		public object getProperty(string propertyName) 
		{
			return _properties[propertyName]; 
		}
			
		public List<string> getPropertyNames()
		{ 	
			return new List<string>(_properties.Keys); 
		} 
	
		public override string ToString ()
		{
			string retVal = string.Format ("[Puzzle Item {0} with properties: ", Name);
			foreach(KeyValuePair<string, object> keyVal in _properties) { 
				retVal += "(" + keyVal.Key + ", " + keyVal.Value +") ";
			}
			retVal += " ]";
			return retVal;
		}
	
	
	}
	
}
