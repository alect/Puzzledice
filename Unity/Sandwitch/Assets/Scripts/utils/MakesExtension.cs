using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using puzzlegen.database;

public class MakesExtension : DatabaseExtension 
{
	public MakesExtension() 
	{ 
	} 
	
	public override void runExtension (Database db)
	{
		foreach (DBItem result in db.getSpawnableItems()) { 
			// If this item can be made by other items, create a symmetric "Makes" property 
			if (!result.propertyExists("madeby"))
				continue; 
			
			foreach (KeyValuePair<string, string> ingredients in (List<KeyValuePair<string, string>>)result.getProperty("madeby")) { 
				string ingredient1 = ingredients.Key; 
				string ingredient2 = ingredients.Value; 
				if (!db.itemExists(ingredient1) || !db.itemExists(ingredient2))
					continue; 
				DBItem dbIng1 = db.getItem(ingredient1);
				DBItem dbIng2 = db.getItem(ingredient2); 
				if (!dbIng1.propertyExists("makes")) 
					dbIng1.setProperty("makes", new List<KeyValuePair<string, string>>() { new KeyValuePair<string, string>(ingredient2, result.ClassName) });
				else 
					(dbIng1.getProperty("makes") as List<KeyValuePair<string, string>>).Add(new KeyValuePair<string, string>(ingredient2, result.ClassName));
				if (!dbIng2.propertyExists("makes")) 
					dbIng2.setProperty("makes", new List<KeyValuePair<string, string>>() { new KeyValuePair<string, string>(ingredient1, result.ClassName) }); 
				else 
					(dbIng2.getProperty("makes") as List<KeyValuePair<string, string>>).Add(new KeyValuePair<string, string>(ingredient1, result.ClassName)); 
				
			} 
		} 
	}
	
	
}
