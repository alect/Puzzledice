using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen.database; 

public class FilledbyExtension : DatabaseExtension 
{
	public FilledbyExtension() 
	{ 
	} 
	
	public override void runExtension (Database db)
	{
		foreach (DBItem container in db.getSpawnableItems()) { 
			// If this is a container, make it fillable by all smaller items
			if (!container.propertyExists("container") || !(bool)container.getProperty("container"))
				continue; 
			
			int containerSize = 0; 
			if (container.propertyExists("size"))
				containerSize = (int)container.getProperty("size"); 
			
			foreach (DBItem filler in db.getSpawnableItems()) { 
				int fillerSize = 0; 
				if (filler.propertyExists("size"))
					fillerSize = (int)filler.getProperty("size"); 
				if (fillerSize < containerSize) { 
					if (!container.propertyExists("filledby"))
						container.setProperty("filledby", new List<string>() { filler.ClassName }); 
					else 
						(container.getProperty("filledby") as List<string>).Add(filler.ClassName); 
					if (!filler.propertyExists("fills"))
						filler.setProperty("fills", new List<string>() { container.ClassName }); 
					else 
						(filler.getProperty("fills") as List<string>).Add(container.ClassName); 
				} 
			} 
			
		} 
	}
	
}
