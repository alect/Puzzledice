using UnityEngine;
using System.Collections;
using System.Collections.Generic;

namespace puzzlegen.database 
{
	public class ChangesExtension : DatabaseExtension 
	{
		public ChangesExtension() 
		{ 
		}
		
		public override void runExtension (Database db)
		{
			// Basically , take the "changes" property and change it from a List<KeyValuePair<string, string>>
			// To a Dictionary<string, List<string>>
			foreach (DBItem dbitem in db.getAllItems()) { 
				if (dbitem.propertyExists("changes")) { 
					List<KeyValuePair<string, string>> changesPairs = (List<KeyValuePair<string, string>>)dbitem.getProperty("changes");
					Dictionary<string, List<string>> changesDict = new Dictionary<string, List<string>>(); 
					foreach (KeyValuePair<string, string> changesPair in changesPairs) { 
						string changePropName = changesPair.Key; 
						string changePropVal = changesPair.Value;
						if (changesDict.ContainsKey(changePropName))
							changesDict[changePropName].Add(changePropVal); 
						else 
							changesDict[changePropName] = new List<string>() { changePropVal };
					} 
					dbitem.setProperty("changes", changesDict);		
				}
			} 
		}

	}
}
