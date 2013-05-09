using UnityEngine;
using System.Collections;
using System.Collections.Generic;

namespace puzzlegen.database
{

	public class ChangedbyExtension : DatabaseExtension
	{
		public ChangedbyExtension() 
		{ 
		} 
		
		public override void runExtension (Database db)
		{
			foreach (DBItem dbItem in db.getSpawnableItems()) { 
				if (!dbItem.propertyExists("changes"))
					continue; 
				
				Dictionary<string, List<string>> changesDict = (Dictionary<string, List<string>>)dbItem.getProperty("changes");
				// Now find each item we can change via the mutables property 
				foreach (string propertyName in changesDict.Keys) { 
					foreach (DBItem maybeChangee in db.getSpawnableItems()) { 
						if (!maybeChangee.propertyExists("mutables"))
							continue; 
						List<string> mutables = (List<string>)maybeChangee.getProperty("mutables");
						if (!mutables.Contains(propertyName))
							continue; 
						if (maybeChangee.propertyExists("changedby")) { 
							List<string> changedby = (List<string>)maybeChangee.getProperty("changedby"); 
							if (!changedby.Contains(dbItem.ClassName))
								changedby.Add(dbItem.ClassName); 
						} 
						else { 
							maybeChangee.setProperty("changedby", new List<string>() { dbItem.ClassName }); 	
						} 
					} 
				} 
			} 
		}		
		
	}
}
