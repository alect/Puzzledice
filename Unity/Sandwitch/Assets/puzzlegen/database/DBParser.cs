using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Xml;

namespace puzzlegen.database
{

	public class DBParser
	{
		public static bool verbose = true;
		
		public static void createDatabaseFromXml(TextAsset[] xmlAssets)
		{ 
			Database.Instance.clearDatabase();
			
			foreach (TextAsset xmlAsset in xmlAssets) { 
				XmlDocument xmlDoc = new XmlDocument(); 
				xmlDoc.LoadXml(xmlAsset.text); 
				// Now begin actually parsing it
				foreach (XmlElement item in xmlDoc.GetElementsByTagName("Item")) { 
					parseItem(item);
				} 
			} 
		} 
		
		public static void createDatabaseFromXml(string[] xmlFiles)
		{
			TextAsset[] xmlAssets = new TextAsset[xmlFiles.Length];
			for (int i = 0; i < xmlFiles.Length; i++) { 
				xmlAssets[i] = Resources.Load(xmlFiles[i]) as TextAsset; 
			} 	
			createDatabaseFromXml(xmlAssets); 
		}
		
		protected static void parseItem(XmlElement element) 
		{
			string name = element.GetAttribute("name");
			if (Database.Instance.itemExists(name)) {
				Debug.Log(string.Format("WARNING: Duplicated item in database: {0}", name));
				return;
			}
			DBItem dbitem = new DBItem(name.Trim()); 
			// Parse each of the item's properties
			foreach (XmlElement property in element.ChildNodes) { 
				string propertyName = property.GetAttribute("name");
				object propertyVal = parseProperty(property);
				dbitem.setProperty(propertyName, propertyVal);
			}
			Database.Instance.addItem(dbitem);
		}
		
		protected static object parseProperty(XmlElement element)
		{ 
			switch(element.Name) { 
			case "TextProperty":
				return parseTextProperty(element); 
			case "IntegerProperty":
				return parseIntProperty(element); 
			case "BooleanProperty":
				return parseBoolProperty(element); 
			case "ItemListProperty":
				return parseItemListProperty(element); 
			case "StringListProperty":
				return parseStringListProperty(element); 
			case "StringPairListProperty":
				return parseStringPairListProperty(element); 
			case "CustomProperty":
				return parseCustomProperty(element);
			default:
				return null;
			} 
		}
		
		protected static string parseTextProperty(XmlElement element)
		{ 
			return element.GetAttribute("text"); 
		}
		
		protected static int parseIntProperty(XmlElement element)
		{ 
			return int.Parse(element.GetAttribute("value")); 
		} 
		
		protected static bool parseBoolProperty(XmlElement element)
		{ 
			return bool.Parse(element.GetAttribute("value"));
		} 
		
		protected static List<string> parseItemListProperty(XmlElement element) 
		{ 
			List<string> itemNames = new List<string>(); 
			foreach (XmlElement itemRef in element.GetElementsByTagName("ItemReference")) { 
				itemNames.Add(itemRef.GetAttribute("name"));	
			} 
			return itemNames;
		}
		
		protected static List<string> parseStringListProperty(XmlElement element)
		{ 
			List<string> strings = new List<string>(); 
			foreach (XmlElement str in element.GetElementsByTagName("String")) { 
				strings.Add(str.GetAttribute("string")); 	
			} 
			return strings;
		}
		
		protected static List<KeyValuePair<string, string>> parseStringPairListProperty(XmlElement element)
		{ 
			List<KeyValuePair<string, string>> pairs = new List<KeyValuePair<string, string>>(); 
			foreach (XmlElement pair in element.GetElementsByTagName("StringPair")) { 
				string string1 = pair.GetAttribute("string1"); 
				string string2 = pair.GetAttribute("string2"); 
				pairs.Add(new KeyValuePair<string, string>(string1, string2));
			} 
			return pairs;
		} 
		
		protected static Dictionary<string, object> parseCustomProperty(XmlElement element)
		{ 
			Dictionary<string, object> properties = new Dictionary<string, object>(); 
			foreach (XmlElement property in element.ChildNodes) { 
				string propertyName = property.GetAttribute("name"); 
				object propertyVal = parseProperty(property); 
				properties[propertyName] = propertyVal;
			} 
			return properties;
		} 
		
	}
}
