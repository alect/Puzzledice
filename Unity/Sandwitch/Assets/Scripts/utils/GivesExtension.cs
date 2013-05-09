using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using puzzlegen.database; 

public class GivesExtension : DatabaseExtension {

	public GivesExtension() 
	{ 
	} 
	
	public override void runExtension (Database db)
	{
		foreach (DBItem giver in db.getSpawnableItems()) { 
			if (!giver.propertyExists("gives"))
				continue; 
			List<string> gives = (List<string>)giver.getProperty("gives"); 
			
			foreach (string giftName in gives) { 
				if (!db.itemExists(giftName))
					continue; 
				
				DBItem gift = db.getItem(giftName); 
				if (!gift.propertyExists("givenby"))
					gift.setProperty("givenby", new List<string>() { giver.ClassName }); 
				else if (!(gift.getProperty("givenby") as List<string>).Contains(giver.ClassName))
					(gift.getProperty("givenby") as List<string>).Add(giver.ClassName); 
			} 
			
		} 
	}
}
