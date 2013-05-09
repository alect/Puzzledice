package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import puzzledice.AreaBlock;
import puzzledice.CombinePuzzleBlock;
import puzzledice.DoorUnlockBlock;
import puzzledice.FilterBlock;
import puzzledice.InsertionPuzzleBlock;
import puzzledice.ItemRequestPuzzleBlock;
import puzzledice.ORBlock;
import puzzledice.OutputBlock;
import puzzledice.PropertyChangePuzzleBlock;
import puzzledice.PuzzleBlock;
import puzzledice.SpawnPuzzleBlock;

public class Loader {

	public static boolean LoadFromXML(File xmlFile) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document dom = builder.parse(xmlFile);
			
			Map<String, AreaBlock> areaMap = new HashMap<String, AreaBlock>();
			Map<AreaBlock, List<String>> areaToDoors = new HashMap<AreaBlock, List<String>>();
			
			// Go through the list of areas and add them to the area edit panel 
			Element docElement = dom.getDocumentElement();
			NodeList areas = docElement.getElementsByTagName("area");
			for (int i = 0; i < areas.getLength(); i++) {
				Element area = (Element)areas.item(i);
				String name = area.getAttribute("name");
				boolean startArea = Boolean.parseBoolean(area.getAttribute("startArea")); 
				// Make an area block
				AreaBlock newArea = new AreaBlock(name);
				newArea.setStartArea(startArea);
				areaMap.put(name, newArea);
				// Attach all its doors 
				List<String> doorNames = new ArrayList<String>();
				NodeList doors = area.getElementsByTagName("door");
				for (int j = 0; j < doors.getLength(); j++) {
					Element door = (Element)doors.item(j);
					String doorName = door.getAttribute("name");
					doorNames.add(doorName);
				}
				areaToDoors.put(newArea, doorNames);
				
			}
			// Now that we have all the areas, connect them up 
			for (AreaBlock area : areaToDoors.keySet()) {
				List<String> doorNames = areaToDoors.get(area);
				for (String doorName : doorNames) {
					AreaBlock door = areaMap.get(doorName);
					area.addDoor(door);
				}
				
				AreaEditPanel.addArea(area);
			}
			// Go through again and add doors this time
			for (AreaBlock area : areaToDoors.keySet()) {
				AreaEditPanel.addDoors(area);
			}
			// Build the puzzle graph for the areas 
			AreaEditPanel.buildAreaPuzzleGraph();
			
			// Now it's time to get the puzzle blocks
			Map<String, PuzzleBlock> puzzleMap = new HashMap<String, PuzzleBlock>();
			
			// Spawn Puzzles 
			NodeList spawnPuzzles = docElement.getElementsByTagName("SpawnPuzzle");
			for (int i = 0; i < spawnPuzzles.getLength(); i++) {
				Element spawnPuzzle = (Element)spawnPuzzles.item(i);
				String spawnArea = spawnPuzzle.getAttribute("spawnArea");
				String puzzleName = spawnPuzzle.getAttribute("name");
				SpawnPuzzleBlock newSpawnPuzzle = new SpawnPuzzleBlock();
				newSpawnPuzzle.setName(puzzleName);
				if (!spawnArea.equals(""))
					newSpawnPuzzle.setSpawnAreaName(spawnArea);
				PuzzleEditPanel.addPuzzle(newSpawnPuzzle);
				puzzleMap.put(puzzleName, newSpawnPuzzle);
			}
			
			// Combine Puzzles 
			NodeList combinePuzzles = docElement.getElementsByTagName("CombinePuzzle");
			for (int i = 0; i < combinePuzzles.getLength(); i++) {
				Element combinePuzzle = (Element)combinePuzzles.item(i);
				String puzzleName = combinePuzzle.getAttribute("name");
				String ingredient1 = combinePuzzle.getAttribute("ingredient1");
				String ingredient2 = combinePuzzle.getAttribute("ingredient2");
				CombinePuzzleBlock newCombinePuzzle = new CombinePuzzleBlock();
				newCombinePuzzle.setName(puzzleName);
				if (!ingredient1.equals(""))
					newCombinePuzzle.setIngredientName1(ingredient1);
				if (!ingredient2.equals(""))
					newCombinePuzzle.setIngredientName2(ingredient2);
				PuzzleEditPanel.addPuzzle(newCombinePuzzle);
				puzzleMap.put(puzzleName, newCombinePuzzle);
			}
			
			// Property Change Puzzles 
			NodeList propertyPuzzles = docElement.getElementsByTagName("PropertyChangePuzzle");
			for (int i = 0; i < propertyPuzzles.getLength(); i++) {
				Element propertyPuzzle = (Element)propertyPuzzles.item(i);
				String puzzleName = propertyPuzzle.getAttribute("name");
				String changer = propertyPuzzle.getAttribute("changer");
				String changee = propertyPuzzle.getAttribute("changee");
				String propertyName = propertyPuzzle.getAttribute("propertyName");
				String propertyValue = propertyPuzzle.getAttribute("propertyValue");
				PropertyChangePuzzleBlock newPropertyPuzzle = new PropertyChangePuzzleBlock();
				newPropertyPuzzle.setName(puzzleName);
				if (!changer.equals(""))
					newPropertyPuzzle.setChangerName(changer);
				if (!changee.equals(""))
					newPropertyPuzzle.setChangeeName(changee);
				if (!propertyName.equals(""))
					newPropertyPuzzle.setPropertyName(propertyName);
				if (!propertyValue.equals(""))
					newPropertyPuzzle.setPropertyValue(propertyValue);
				PuzzleEditPanel.addPuzzle(newPropertyPuzzle);
				puzzleMap.put(puzzleName, newPropertyPuzzle);
			}
			
			// Insertion Puzzles 
			NodeList insertionPuzzles = docElement.getElementsByTagName("InsertionPuzzle");
			for (int i = 0; i < insertionPuzzles.getLength(); i++) {
				Element insertionPuzzle = (Element)insertionPuzzles.item(i);
				String puzzleName = insertionPuzzle.getAttribute("name");
				String box = insertionPuzzle.getAttribute("box");
				String boxee = insertionPuzzle.getAttribute("boxee");
				InsertionPuzzleBlock newInsertionPuzzle = new InsertionPuzzleBlock();
				newInsertionPuzzle.setName(puzzleName);
				if (!box.equals(""))
					newInsertionPuzzle.setBoxName(box);
				if (!boxee.equals(""))
					newInsertionPuzzle.setBoxeeName(boxee);
				PuzzleEditPanel.addPuzzle(newInsertionPuzzle);
				puzzleMap.put(puzzleName, newInsertionPuzzle);
			}
		
			// Item Request Puzzles 
			NodeList requestPuzzles = docElement.getElementsByTagName("ItemRequestPuzzle");
			for (int i = 0; i < requestPuzzles.getLength(); i++) {
				Element requestPuzzle = (Element)requestPuzzles.item(i);
				String puzzleName = requestPuzzle.getAttribute("name");
				String requester = requestPuzzle.getAttribute("requester");
				String requested = requestPuzzle.getAttribute("requested");
				ItemRequestPuzzleBlock newRequestPuzzle = new ItemRequestPuzzleBlock();
				newRequestPuzzle.setName(puzzleName);
				if (!requester.equals(""))
					newRequestPuzzle.setRequesterName(requester);
				if (!requested.equals("")) 
					newRequestPuzzle.setRequestedName(requested);
				PuzzleEditPanel.addPuzzle(newRequestPuzzle);
				puzzleMap.put(puzzleName, newRequestPuzzle);
			}
			
			// Door Unlock Puzzles 
			NodeList unlockPuzzles = docElement.getElementsByTagName("DoorUnlockPuzzle");
			for (int i = 0; i < unlockPuzzles.getLength(); i++) {
				Element unlockPuzzle = (Element)unlockPuzzles.item(i);
				String puzzleName = unlockPuzzle.getAttribute("name");
				String sourceArea = unlockPuzzle.getAttribute("source");
				String destArea = unlockPuzzle.getAttribute("dest");
				String key = unlockPuzzle.getAttribute("key");
				DoorUnlockBlock newDoorUnlock = new DoorUnlockBlock();
				newDoorUnlock.setName(puzzleName);
				if (!sourceArea.equals(""))
					newDoorUnlock.setSourceAreaName(sourceArea);
				if (!destArea.equals(""))
					newDoorUnlock.setDestAreaName(destArea);
				if (!key.equals(""))
					newDoorUnlock.setKeyName(key);
				PuzzleEditPanel.addPuzzle(newDoorUnlock);
				puzzleMap.put(puzzleName, newDoorUnlock);
			}
			
			// Filters
			NodeList filters = docElement.getElementsByTagName("Filter");
			for (int i = 0; i < filters.getLength(); i++) {
				Element filter = (Element)filters.item(i);
				String puzzleName = filter.getAttribute("name");
				String input = filter.getAttribute("input");
				String propertyName = filter.getAttribute("propertyName");
				String propertyValue = filter.getAttribute("propertyValue");
				FilterBlock newFilter = new FilterBlock();
				newFilter.setName(puzzleName);
				if (!input.equals(""))
					newFilter.setInputName(input);
				if (!propertyName.equals(""))
					newFilter.setPropertyName(propertyName);
				if (!propertyValue.equals(""))
					newFilter.setPropertyValue(propertyValue);
				PuzzleEditPanel.addPuzzle(newFilter);
				puzzleMap.put(puzzleName, newFilter);
			}
			
			// Outputs 
			NodeList outputs = docElement.getElementsByTagName("Output"); 
			for (int i = 0; i < outputs.getLength(); i++) { 
				Element output = (Element)outputs.item(i);
				String puzzleName = output.getAttribute("name"); 
				String input = output.getAttribute("input"); 
				String request = output.getAttribute("requestName"); 
				OutputBlock newOutput = new OutputBlock(); 
				newOutput.setName(puzzleName); 
				if (!input.equals(""))
					newOutput.setInputName(input);
				if (!request.equals(""))
					newOutput.setRequestName(request);
				PuzzleEditPanel.addPuzzle(newOutput);
				puzzleMap.put(puzzleName, newOutput);
			}
			
			// OR Blocks 
			NodeList orBlocks = docElement.getElementsByTagName("ORBlock"); 
			for (int i = 0; i < orBlocks.getLength(); i++) { 
				Element orBlock = (Element)orBlocks.item(i); 
				String puzzleName = orBlock.getAttribute("name"); 
				String option1 = orBlock.getAttribute("option1");
				String option2 = orBlock.getAttribute("option2"); 
				ORBlock newOrBlock = new ORBlock(); 
				newOrBlock.setName(puzzleName);
				if (!option1.equals(""))
					newOrBlock.setOptionName1(option1);
				if (!option2.equals(""))
					newOrBlock.setOptionName2(option2);
				PuzzleEditPanel.addPuzzle(newOrBlock);
				puzzleMap.put(puzzleName, newOrBlock);
			}
			
			for (PuzzleBlock puzzle : puzzleMap.values()) {
				puzzle.attachBlocksToName(areaMap, puzzleMap);
			}
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
}
