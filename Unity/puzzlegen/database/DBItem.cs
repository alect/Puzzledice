using UnityEngine;
using System; 
using System.Collections;
using System.Collections.Generic; 

namespace puzzlegen.database { 

	public class DBItem
	{
		private string _className; 
		public string ClassName { 
			get { return _className; }	
		}
				
		private Dictionary<string, object> _properties; 
		
		public bool Spawned { 
			get { return _numSpawned >= _capacity; }
		}
		
		private int _capacity; 
		private int _numSpawned; 
		
		// For giving items unique spawn indices 
		private int _nextSpawnIndex; 
		public int NextSpawnIndex { 
			get { return _nextSpawnIndex++; }
		}
		
		private List<string> _types; 
		public List<string> types { 
			get { return _types; }
		}	
		
		private bool _abstract; 
		public bool Abstract { 
			get { return _abstract; }
		}
		
		
		public DBItem(string className) 
		{
			_className = className; 
			_numSpawned = 0; 
			_capacity = 1; 
			_types = new List<string>(); 
			_abstract = false; 
			_properties = new Dictionary<string, object>(); 
			_properties["classname"] = className; 
			_properties["carryable"] = false; 
			_properties["NPC"] = false; 
			_properties["mutables"] = new List<string>(); 
		}
		
		public void setProperty(string propertyName, object propertyVal)
		{
			if (propertyName == "abstract") 
				_abstract = (bool)propertyVal; 
			if (propertyName == "capacity" && (int)propertyVal > 0) 
				_capacity = (int)propertyVal; 
			else 
				_properties[propertyName] = propertyVal; 
		}
		
		public object getProperty(string propertyName)
		{
			if (_properties.ContainsKey(propertyName))
				return _properties[propertyName]; 	
			return null; 
		}
		
		public List<string> getPropertyNames() 
		{ 
			return new List<string>(_properties.Keys);
		} 
		
		public bool propertyExists(string propertyName) 
		{
			return _properties.ContainsKey(propertyName); 
		}
		
		public object getPropertyNoRecurs(string propertyName) 
		{
			if (_properties.ContainsKey(propertyName))
				return _properties[propertyName];
			else 
				return null; 
		}
		
		public DBItem copyToNewName(string newName) 
		{
			DBItem newDBItem = new DBItem(newName); 
			foreach(KeyValuePair<string, object> keyVal in _properties) { 
				if (keyVal.Key != "classname")
					newDBItem.setProperty(keyVal.Key, keyVal.Value); 
			}
			return newDBItem; 
		}
		
		public PuzzleItem spawnItem() 
		{
			Debug.Log("SPAWNING " + _className); 
						
			_numSpawned++; 
			
			PuzzleItem itemToReturn = new PuzzleItem(_className); 
			List<string> mutables; 
			if (_properties.ContainsKey("mutables"))
				mutables = _properties["mutables"] as List<string>;
			else 
				mutables = new List<string>(); 
			
			
			// Copy properties from the dbitem to the output item 
			foreach (string propertyName in _properties.Keys) {
				if (!mutables.Contains(propertyName))
					itemToReturn.setProperty(propertyName, _properties[propertyName]);	
			} 
			
			return itemToReturn; 
		}
		
		public void despawnItem() 
		{ 
			_numSpawned--;
		} 
		
		
	}
	
}
