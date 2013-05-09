using UnityEngine;
using System.Collections;
using System.Collections.Generic; 


namespace puzzlegen.database { 

	public class Database
	{
		private Dictionary<string, DBItem> _masterList; 
		private Dictionary<string, DBItem> _spawnableItems; 
		private Dictionary<string, DBItem> _nonSpawnableItems; 
		
		private List<DatabaseExtension> _extensions; 
		
		private static Database _instance = null; 
		public static Database Instance { 
			get { 
				if (_instance == null)
					_instance = new Database(); 
				return _instance; 
			}
		}
		
		public void clearDatabase() 
		{
			_masterList = new Dictionary<string, DBItem>(); 
			_spawnableItems = new Dictionary<string, DBItem>(); 
			_nonSpawnableItems = new Dictionary<string, DBItem>(); 
		}
		
		public void addExtension(DatabaseExtension extension) 
		{ 
			_extensions.Add(extension);
		} 
		
		public void removeExtensions() 
		{ 
			_extensions.Clear(); 
		} 
		
		public void runExtensions() 
		{ 
			foreach (DatabaseExtension extension in _extensions) { 
				extension.runExtension(this);	
			} 
		} 
		
		public List<string> spawnableItemNames() 
		{ 
			return new List<string>(_spawnableItems.Keys); 
		} 
		
		public List<DBItem> getSpawnableItems() 
		{
			return new List<DBItem>(_spawnableItems.Values); 
		}
		
		public List<string> allItemNames() 
		{ 
			return new List<string>(_masterList.Keys);
		} 
		
		public List<DBItem> getAllItems() 
		{
			return new List<DBItem>(_masterList.Values);
		} 
		
		public Database() 
		{
			if (_instance != null) 
				throw new UnityException("Tried to spawn multiple item databases");
			
			_masterList = new Dictionary<string, DBItem>(); 
			_spawnableItems = new Dictionary<string, DBItem>(); 
			_nonSpawnableItems = new Dictionary<string, DBItem>(); 
			_extensions = new List<DatabaseExtension>(); 
		}
		
		public void addItem(DBItem item) 
		{
			_masterList[item.ClassName] = item; 
			if (item.Abstract)
				_nonSpawnableItems[item.ClassName] = item; 
			else 
				_spawnableItems[item.ClassName] = item; 
		}
		
		public List<DBItem> getItemsWithProperty(string propertyName) 
		{
			List<DBItem> filteredList = new List<DBItem>(); 
			foreach (DBItem item in _spawnableItems.Values)  {
				if (item.propertyExists(propertyName) && !item.Spawned)
					filteredList.Add(item); 
			}
			return filteredList; 
		}
		
		public List<string> filterListByProperty(List<string> itemNames, string propertyName)
		{
			List<string> filteredList = new List<string>(); 
			foreach(string itemName in itemNames) { 
				DBItem item = getItem(itemName); 
				if (item != null && item.propertyExists(propertyName) && !item.Spawned)
					filteredList.Add(itemName); 
			}
			return filteredList; 
		}
		
		public DBItem getItem(string itemName) 
		{
			if (_spawnableItems.ContainsKey(itemName))
				return _spawnableItems[itemName];
			return null; 
		}
		
		public bool itemExists(string itemName)
		{
			return _spawnableItems.ContainsKey(itemName); 
		}
		
		public DBItem getItemFromMasterList(string itemName)
		{
			if (_masterList.ContainsKey(itemName))
				return _masterList[itemName]; 
			return null; 
		}
		
		public bool itemExistsInMasterList(string itemName) 
		{
			return _masterList.ContainsKey(itemName); 
		}
		
		public DBItem copyItem(string itemToCopyName, string newItemName)
		{
			if (itemToCopyName == newItemName)
				throw new UnityException("Cannot create a copy of a database item with the same name"); 
			if (!_masterList.ContainsKey(itemToCopyName))
				throw new UnityException("Cannot create copy of database item " + itemToCopyName + ". Item does not exist in database."); 
			if (_masterList.ContainsKey(newItemName))
				throw new UnityException("cannot create new copy " + newItemName + ". An item already exists with that name."); 
			DBItem newDBItem = getItem(itemToCopyName).copyToNewName(newItemName); 
			addItem(newDBItem);
			return newDBItem;
		}
		
	}
	
}