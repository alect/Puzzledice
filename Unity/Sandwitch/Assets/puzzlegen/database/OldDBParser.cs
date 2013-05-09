using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using System.Xml;

namespace puzzlegen.database 
{

	public class OldDBParser 
	{
		public static void createDatabaseFromXml(List<string> xmlFiles, bool verbose) 
		{
			Database.Instance.clearDatabase();
			Database.Instance.removeExtensions(); 
			Database.Instance.addExtension(new ParentExtension());
			Database.Instance.addExtension(new ChangesExtension());
			Database.Instance.addExtension(new ChangedbyExtension());
			
			
			foreach (string xmlFilename in xmlFiles) { 
				TextAsset xmlText = Resources.Load(xmlFilename) as TextAsset; 
				XmlDocument xmlDoc = new XmlDocument(); 
				xmlDoc.LoadXml(xmlText.text);
				foreach (XmlElement item in xmlDoc.GetElementsByTagName("Items")) { 
					string className = item.GetElementsByTagName("classname")[0].InnerText;
					if (verbose) Debug.Log(string.Format("Making Item: {0}", className));
					DBItem dbitem = new DBItem(className.Trim());
					
					XmlNodeList hints = item.GetElementsByTagName("hints"); 
					Dictionary<string, string> hintdict = new Dictionary<string, string>(); 
					foreach (XmlElement hint in hints) { 
						foreach (XmlElement child in hint.ChildNodes) { 
							if (child.InnerText != null && child.InnerText != "") { 
								if (verbose) Debug.Log(string.Format("Adding {0} hint to {1}: {2}", child.Name, className, child.InnerText)); 
								hintdict[child.Name] = child.InnerText; 
							} 
						} 
					}
					if (hintdict.Count > 0) { 
						if (verbose) Debug.Log("setting hints property to a hints dictionary"); 
						dbitem.setProperty("hints", hintdict); 
					}
					
					XmlNodeList auxtext = item.GetElementsByTagName("text"); 
					Dictionary<string, string> textdict = new Dictionary<string, string>(); 
					foreach (XmlElement txt in auxtext) { 
						foreach (XmlElement child in txt.ChildNodes) { 	
							if (child.InnerText != null && child.InnerText != "") { 
								if (verbose) Debug.Log(string.Format("Adding {0} text to {1}: {2}", child.Name, className, child.InnerText)); 
								textdict[child.Name] = child.InnerText;
							} 
						} 
					}
					if (textdict.Count > 0) { 
						if (verbose) Debug.Log("setting text property to a text dictionary"); 
						dbitem.setProperty("text", textdict); 
					}
					
					foreach (XmlElement child in item.ChildNodes) { 
						if (child.Name == "hints" || child.Name == "text" || child.Name == "classname" || child.InnerText == null || child.InnerText == "")
							continue; 
						
						// Check whether this property is a dictionary 
						bool dic = false; 
						foreach (XmlNode grandChild in child.ChildNodes) { 
							if (grandChild.NodeType != XmlNodeType.Element)
								continue;
							if (grandChild.Name == "dictionary") { 
								dic = true; 
								Dictionary<string, object> dict1 = parseDictionary((XmlElement)grandChild);
								if (dict1.Count > 0) { 
									if (verbose) Debug.Log(string.Format("Setting property {0} to a parsed dictionary", child.Name)); 
									dbitem.setProperty(child.Name, dict1);
								} 
							}
						}
						if (dic) continue; 
						// Handle nearly every other type of property 
						string potentialVal = child.InnerText.Trim(); 
						object val; 
						int maybeInt; 
						if (potentialVal == "true" || potentialVal == "True")
							val = true; 
						else if (potentialVal == "false" || potentialVal == "False")
							val = false; 
						else if (potentialVal == "None")
							val = null; 
						else if (int.TryParse(potentialVal, out maybeInt))
							val = maybeInt; 
						else if (potentialVal[0] == '[') { 
							// Make sure it isn't a list of tuples first 
							if (potentialVal[1] == '(') { 
							 	potentialVal = potentialVal.Replace("[", "").Replace("]", "").Trim();
								List<string> valueList = new List<string>(potentialVal.Split(new char[] {','})); 
								val = parseAsTuples(valueList); 
							} 
							
							// Parse any string starting with an open bracket as a list
							else
								val = parseAsList(potentialVal); 
						}
						else if (potentialVal[0] == '(') { 
							List<string> valueList = new List<string>(potentialVal.Split(new char[] {','})); 
							val = parseAsTuples(valueList); 
						}
						else if (potentialVal == "")
							continue; 
						else 
							val = potentialVal; 
						if (verbose) Debug.Log(string.Format("Adding property: ({0}, {1})", child.Name, val)); 
						dbitem.setProperty(child.Name.Trim(), val); 						
					}
					if (Database.Instance.itemExists(dbitem.ClassName)) Debug.Log(string.Format("WARNING: Duplicated item in database: {0}", dbitem.ClassName)); 
					Database.Instance.addItem(dbitem);
				} 
					
			}
			
			Database.Instance.runExtensions();
			
		}
		
		private static List<KeyValuePair<string, string>> parseAsTuples(List<string> valueList) 
		{ 
			string currentKey=null, currentVal=null; 
			List<KeyValuePair<string, string>> allTuples = new List<KeyValuePair<string, string>>(); 
			foreach (string iV in valueList) { 
				string v = iV.Trim(); 
				if (v[0] == '(') { 
					currentKey = v.Replace("(", "");
				}
				else if (v[v.Length-1] == ')') { 
					currentVal = v.Replace(")", ""); 
					allTuples.Add(new KeyValuePair<string, string>(currentKey, currentVal)); 
				} 
			} 	
			return allTuples; 
		} 
		
		private static List<string> parseAsList(string val) 
		{ 
			val = val.Replace("[", "").Replace("]", ""); 
			if (val[0] == '(') { 
				// Parse as tuples	
			} 
			string[] valueList = val.Split(new char[] {','}); 
			List<string> list = new List<string>(); 
			foreach (string s in valueList) { 
				list.Add(s.Trim());	
			} 
			return list; 
		} 
		
		private static Dictionary<string, object> parseDictionary(XmlElement dictNode) 
		{ 
			Dictionary<string, object> d = new Dictionary<string, object>(); 
			foreach (XmlElement entry in dictNode.GetElementsByTagName("DictionaryEntry")) { 
				string key = entry.GetElementsByTagName("key")[0].InnerText; 
				string val = entry.GetElementsByTagName("value")[0].InnerText;
				d[key] = val; 
			} 
			return d; 
		} 
	}
	
}
