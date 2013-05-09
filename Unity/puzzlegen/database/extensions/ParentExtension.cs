using UnityEngine;
using System.Collections;
using System.Collections.Generic;

namespace puzzlegen.database
{ 

	public class ParentExtension : DatabaseExtension
	{
		public ParentExtension() 
		{
		}
		
		// The Parent Extension is used to allow certain database items to inherit properties from other (abstract) items. 
		public override void runExtension (Database db)
		{
			HashSet<string> closedNames = new HashSet<string>(); 
			foreach (string itemName in db.allItemNames()) { 
				handleItem(itemName, db, closedNames);
			}
		}
		
		private void handleItem(string itemName, Database db, HashSet<string> closedNames) 
		{ 
			if (closedNames.Contains(itemName))
				return; 
			closedNames.Add(itemName); 
			if (!db.itemExistsInMasterList(itemName))
				return;
			
			// Want to receive properties from all of our parent types 
			DBItem dbItem = db.getItemFromMasterList(itemName); 
			List<string> parentNames = dbItem.getProperty("types") as List<string>;
			if (parentNames == null)
				return; 
			foreach (string parentName in parentNames) { 
				if (!db.itemExistsInMasterList(parentName))
					continue; 
				// Have to handle our parents first so they're up to date 
				handleItem(parentName, db, closedNames); 
				// Now grab the item and merge any properties we don't have 
				DBItem parentItem = db.getItemFromMasterList(parentName); 
				foreach (string propertyName in parentItem.getPropertyNames()) { 
					// Special case for the mutables property 
					if (propertyName == "mutables") { 
						if (dbItem.propertyExists("mutables")) {
							HashSet<string> currentMutables = new HashSet<string>((List<string>)dbItem.getProperty("mutables"));
							HashSet<string> parentMutables = new HashSet<string>((List<string>)parentItem.getProperty("mutables")); 
							currentMutables.UnionWith(parentMutables); 
							dbItem.setProperty("mutables", new List<string>(currentMutables)); 
						}
						else { 
							dbItem.setProperty("mutables", new List<string>((List<string>)parentItem.getProperty("mutables"))); 	
						} 
					} 
					else if (!dbItem.propertyExists(propertyName)) { 	
						dbItem.setProperty(propertyName, parentItem.getProperty(propertyName));
					} 
				} 
			} 
		} 
		
		
		
	}
}
