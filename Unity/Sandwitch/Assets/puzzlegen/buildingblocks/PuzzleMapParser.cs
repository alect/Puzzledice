using UnityEngine;
using System.Collections;
using System.Collections.Generic; 
using System.Xml; 

namespace puzzlegen.buildingblocks 
{ 

	public class PuzzleMapParser 
	{
		public static bool verbose = true; 
		
		private static Dictionary<string, Area> _spawnedAreas; 
		
		public static BuildingBlock createBuildingBlockFromXml(TextAsset xmlAsset)
		{
			_spawnedAreas = new Dictionary<string, Area>(); 
			XmlDocument xmlDoc = new XmlDocument(); 
			xmlDoc.LoadXml(xmlAsset.text); 
			// First, find our output and parse it 
			XmlNodeList outputs = xmlDoc.GetElementsByTagName("Output"); 
			if (outputs.Count == 0) { 
				if (verbose) Debug.Log("Puzzle map loading failed: no output blocks!"); 
				return null; 
			}
			else 
				return parseOutputBlock(outputs[0] as XmlElement, xmlDoc); 
		}
		
		public static BuildingBlock createBuildingBlockFromXml(string xmlFile)
		{ 
			TextAsset xmlAsset = Resources.Load(xmlFile) as TextAsset; 
			return createBuildingBlockFromXml(xmlAsset);
		} 
		
		protected static BuildingBlock parseOutputBlock(XmlElement outputElem, XmlDocument xmlDoc) 
		{ 
			return findAndParsePuzzleBlock(outputElem.GetAttribute("input"), xmlDoc); 
		} 
		
		protected static BuildingBlock findAndParsePuzzleBlock(string blockName, XmlDocument xmlDoc) 
		{ 
			if (blockName == "" || blockName == null)
				return null; 
			foreach (XmlElement elem in xmlDoc.GetElementsByTagName("puzzles")) { 
				foreach (XmlElement child in elem.ChildNodes) { 
					if (child.GetAttribute("name") == blockName) { 
						// Now we figure out what kind of block this is	
						string blockType = child.Name; 
						switch (blockType) { 
						case "SpawnPuzzle":
							return parseSpawnPuzzle(child, xmlDoc); 
						case "CombinePuzzle":
							return parseCombinePuzzle(child, xmlDoc); 
						case "ItemRequestPuzzle":
							return parseItemRequestPuzzle(child, xmlDoc); 
						case "PropertyChangePuzzle":
							return parsePropertyChangePuzzle(child, xmlDoc); 
						case "DoorUnlockPuzzle":
							return parseDoorUnlock(child, xmlDoc); 
						case "Filter":
							return parseFilter(child, xmlDoc); 
						case "InsertionPuzzle":
							return parseInsertionPuzzle(child, xmlDoc); 
						case "ORBlock":
							return parseORBlock(child, xmlDoc); 
						default: 
							return null; 
						}
					} 
				} 
			}
			return null; 
		} 
		
		protected static Area findAndParseAreaBlock(string blockName, XmlDocument xmlDoc) 
		{ 
			if (blockName == "" || blockName == null)
				return null; 
			
			if (_spawnedAreas.ContainsKey(blockName))
				return _spawnedAreas[blockName];
			
			foreach (XmlElement elem in xmlDoc.GetElementsByTagName("area")) { 
				if (elem.GetAttribute("name") == blockName) { 
					// We have our area now. 
					List<IAreaConnector> areaInputs = new List<IAreaConnector>(); 
					// If it's the start area
					if (bool.Parse(elem.GetAttribute("startArea")))
						areaInputs.Add(new StartAreaBlock()); 
					// Go through all of the input areas 
					foreach (XmlElement inputAreaElem in elem.GetElementsByTagName("inputArea")) { 
						Area inputArea = findAndParseAreaBlock(inputAreaElem.GetAttribute("name"), xmlDoc); 
						if (inputArea != null)
							areaInputs.Add(inputArea); 
					} 
					// Go through all of the locked doors 
					foreach (XmlElement lockedDoorElem in elem.GetElementsByTagName("lockedDoor")) { 
						DoorUnlockPuzzle lockedDoor = findAndParsePuzzleBlock(lockedDoorElem.GetAttribute("name"), xmlDoc) as DoorUnlockPuzzle;
						if (lockedDoor != null) 
							areaInputs.Add(lockedDoor); 
					} 
					// If we don't have any inputs, it's not valid 
					if (areaInputs.Count == 0) {
						if (verbose) Debug.Log(string.Format("Failed to load area {0} because none of its inputs loaded", blockName)); 
						return null; 
					}
					else {
						Area newArea = new Area(blockName, areaInputs); 
						_spawnedAreas[blockName] = newArea;
						return newArea; 
					}
				}
			} 
			return null;  
		}
		
		protected static SpawnPuzzle parseSpawnPuzzle(XmlElement elem, XmlDocument xmlDoc) 
		{ 
			Area input = findAndParseAreaBlock(elem.GetAttribute("spawnArea"), xmlDoc); 
			if (input != null)
				return new SpawnPuzzle(input); 
			return null; 
		} 
		
		protected static CombinePuzzle parseCombinePuzzle(XmlElement elem, XmlDocument xmlDoc)
		{
			BuildingBlock ingredient1 = findAndParsePuzzleBlock(elem.GetAttribute("ingredient1"), xmlDoc); 
			BuildingBlock ingredient2 = findAndParsePuzzleBlock(elem.GetAttribute("ingredient2"), xmlDoc); 
			if (ingredient1 != null && ingredient2 != null)
				return new CombinePuzzle(ingredient1, ingredient2); 
			return null; 
		}
		
		protected static PropertyChangePuzzle parsePropertyChangePuzzle(XmlElement elem, XmlDocument xmlDoc)
		{
			BuildingBlock changer = findAndParsePuzzleBlock(elem.GetAttribute("changer"), xmlDoc); 
			BuildingBlock changee = findAndParsePuzzleBlock(elem.GetAttribute("changee"), xmlDoc); 
			
			if (changer != null && changee != null) { 
				string propertyName = elem.GetAttribute("propertyName"); 
				if (propertyName == "")
					propertyName = null; 
				string propertyValue = elem.GetAttribute("propertyValue"); 
				if (propertyValue == "")
					propertyValue = null; 
				return new PropertyChangePuzzle(changer, changee, propertyName, parseSimplePropertyVal(propertyValue));
			} 
			return null; 
		}
		
		protected static ItemRequestPuzzle parseItemRequestPuzzle(XmlElement elem, XmlDocument xmlDoc)
		{ 
			BuildingBlock requester = findAndParsePuzzleBlock(elem.GetAttribute("requester"), xmlDoc); 
			BuildingBlock requested = findAndParsePuzzleBlock(elem.GetAttribute("requested"), xmlDoc); 
			if (requester != null && requested != null) { 
				return new ItemRequestPuzzle(requester, requested); 	
			} 
			return null; 
		} 
		
		protected static UnboxingPuzzle parseInsertionPuzzle(XmlElement elem, XmlDocument xmlDoc) 
		{ 
			BuildingBlock boxee = findAndParsePuzzleBlock(elem.GetAttribute("boxee"), xmlDoc); 
			BuildingBlock box = findAndParsePuzzleBlock(elem.GetAttribute("box"), xmlDoc); 
			if (box != null && boxee != null) { 
				return new UnboxingPuzzle(new InsertionPuzzle(box, boxee)); 	
			} 
			return null; 
		} 
		
		protected static FilterBlock parseFilter(XmlElement elem, XmlDocument xmlDoc)
		{ 
			BuildingBlock input = findAndParsePuzzleBlock(elem.GetAttribute("input"), xmlDoc); 
			if (input != null) { 
				string propertyName = elem.GetAttribute("propertyName"); 
				if (propertyName == "")
					propertyName = null; 
				string propertyValue = elem.GetAttribute("propertyValue"); 
				if (propertyValue == "")
					propertyValue = null; 
				Dictionary<string, object> filterProps = new Dictionary<string, object>(); 
				if (propertyName != null)
					filterProps[propertyName] = parseSimplePropertyVal(propertyValue); 
				return new FilterBlock(input, filterProps); 
			} 
			return null; 
		} 
		
		protected static DoorUnlockPuzzle parseDoorUnlock(XmlElement elem, XmlDocument xmlDoc)
		{ 	
			Area source = findAndParseAreaBlock(elem.GetAttribute("source"), xmlDoc); 
			BuildingBlock key = findAndParsePuzzleBlock(elem.GetAttribute("key"), xmlDoc); 
			if (source != null && key != null) { 
				return new DoorUnlockPuzzle(key, source); 	
			}
			return null; 
		} 
		
		protected static ORBlock parseORBlock(XmlElement elem, XmlDocument xmlDoc)
		{
			BuildingBlock option1 = findAndParsePuzzleBlock(elem.GetAttribute("option1"), xmlDoc); 
			BuildingBlock option2 = findAndParsePuzzleBlock(elem.GetAttribute("option2"), xmlDoc); 
			if (option1 != null && option2 != null) {
				return new ORBlock(option1, option2);	
			}
			return null; 
		} 
		
		protected static object parseSimplePropertyVal(string valString) 
		{ 
			bool maybeBool; 
			if (bool.TryParse(valString, out maybeBool))
				return maybeBool; 
			int maybeInt; 
			if (int.TryParse(valString, out maybeInt))
				return maybeInt; 
			return valString; 
		} 
	
	}
	
}
