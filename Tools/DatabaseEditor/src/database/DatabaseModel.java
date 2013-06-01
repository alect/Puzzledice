package database;

import gui.WindowMain;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.StringPair;
/**
 * 
 * @author Alec Thomson
 * Class representing a model of the database. 
 * Characterized by a list of DatabaseItems and DatabaseProperties
 * Interacts with the database views (list and table) to correctly represent the data. 1
 */
public class DatabaseModel {

	//private List<DatabaseItem> _items;
	private Map<String, DatabaseItem> _items;
	
	// A list of specific properties each item might want to define. 
	private Map<String, DatabaseProperty> _properties;
	
	public DatabaseModel()
	{
		//_items = new ArrayList<DatabaseItem>();
		_items = new HashMap<String, DatabaseItem>();
		_properties = new HashMap<String, DatabaseProperty>();
	}
	
	/**
	 * Function to add a new item to the database. Updates all of the views and the internal model of the database
	 * Returns true if the item was successfully added. Returns false otherwise (such as if the item name already exists in the database)
	 * 
	 * @param item the item to add to the database
	 * @return Whether the item was successfully inserted
	 */
	public boolean addItemToDatabase(DatabaseItem item) 
	{
		if(_items.containsKey(item.getName())) {
			return false;
		}
		// Give the item a copy of every property, if it doesn't already have it
		for(String propertyName : _properties.keySet()) {
			if (!item.hasProperty(propertyName))
				item.addProperty(propertyName, null);
		}
		
		_items.put(item.getName(), item);
		WindowMain.addItemToListView(item);
		WindowMain.addItemToTableView(item);
		return true;
	}
	
	public void removeItemFromDatabase(DatabaseItem item) 
	{ 
		if (!_items.containsKey(item.getName()))
			return; 
		_items.remove(item.getName());
		WindowMain.removeItemFromListView(item); 
		WindowMain.removeItemFromTableView(item);
	}
	
	
	/**
	 * Function to add a new property type to the database. Acts as a master list of representative property types. 
	 * @param property a Database property that must be initialized
	 * @return true if the property was successfully added. Returns false otherwise (such as if the property name already exists in the database)
	 */
	public boolean addPropertyToDatabase(DatabaseProperty property) 
	{
		if(_properties.containsKey(property.getName()))
			return false;
		
		_properties.put(property.getName(), property);
		
		// Add an initial null value for this property to all of our database items. 
		for(DatabaseItem item : _items.values()) {
			item.addProperty(property.getName(), null);
		}
		
		WindowMain.addPropertyToTableView(property);
		WindowMain.updateGUI();
		return true;
	}
	
	public void removePropertyFromDatabase(DatabaseProperty property)
	{ 
		if (!_properties.containsKey(property.getName()))
			return; 
		_properties.remove(property.getName()); 
		for(DatabaseItem item : _items.values()) {
			item.removeProperty(property.getName());
		}
		WindowMain.removePropertyFromTableView(property);
		WindowMain.updateGUI();
	}
	
	
	/**
	 * Creates an instance of a specific property based on its name. If the name of the property is not contained within
	 * our property map, returns null 
	 * @param propertyName
	 * @return an instance of a specific property type, null if the property name is not in the property map.
	 */
	public DatabaseProperty createInstance(String propertyName) 
	{
		if(!_properties.containsKey(propertyName))
			return null;
		
		return _properties.get(propertyName).initializeInstance();
	}
	
	public boolean isPropertyNameInDatabase(String propertyName) {
		return _properties.containsKey(propertyName);
	}
	
	public DatabaseItem[] getItemList()
	{
		DatabaseItem[] items = new DatabaseItem[_items.values().size()];
		return _items.values().toArray(items);
	}
	
	public DatabaseProperty[] getPropertyList() 
	{
		DatabaseProperty[] properties = new DatabaseProperty[_properties.values().size()];
		return _properties.values().toArray(properties);
	}
	
	public DatabaseProperty getPropertyValue(String propertyName) {
		return _properties.get(propertyName);
	}
	
	/**
	 * Returns an XML representation of this database. 
	 * @return an XML representation of this databse.
	 */
	public String xmlDigest() 
	{
		String xml = "<Database>\n<Properties>\n";
		// First define our properties 
		for (DatabaseProperty property : _properties.values()) {
			xml += property.toXML() + "\n";
		}
		xml += "</Properties>\n<Items>\n";
		for (DatabaseItem item : _items.values()) {
			xml += item.toXML() + "\n";
		}
		xml += "</Items>\n</Database>";
		return xml;
	}
	
	
	/**
	 * Function that loads a database model from an xml file 
	 * @param xmlFile the file to load a database from 
	 * @return the database model loaded from the xml file
	 */
	public static DatabaseModel createModelFromXML(File xmlFile) 
	{
		DatabaseModel model = new DatabaseModel();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try { 
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document dom = builder.parse(xmlFile);
			Element docElement = dom.getDocumentElement();
			// Go through the list of properties and add them to our database. 
			NodeList propertyGroups = docElement.getElementsByTagName("Properties");
			if (propertyGroups.getLength() == 0)
				return null;
			for (int i = 0; i < propertyGroups.getLength(); i++) {
				Element propertyList = (Element)propertyGroups.item(i);
				NodeList properties = propertyList.getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					if (properties.item(j).getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element propertyElem = (Element)properties.item(j);
					DatabaseProperty propertyTemplate = parsePropertyTemplate(propertyElem);
					model.addPropertyToDatabase(propertyTemplate);
				}
			}
			// Go through the list of items and add them to our database
			NodeList itemGroups = docElement.getElementsByTagName("Items");
			if (itemGroups.getLength() == 0)
				return null;
			Map<String, DatabaseItem> itemMap = new HashMap<String, DatabaseItem>();
			for (int i = 0; i < itemGroups.getLength(); i++) {
				Element itemList = (Element)itemGroups.item(i);
				NodeList items = itemList.getChildNodes();
				for (int j = 0; j < items.getLength(); j++) {
					if (items.item(j).getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element itemElem = (Element)items.item(j);
					DatabaseItem item = parseItem(itemElem);
					model.addItemToDatabase(item);
					itemMap.put(item.getName(), item);
				}
			}
			// After we've added all of our items to the database 
			// resolve all the names to actual references
			for (DatabaseItem item : model.getItemList()) {
				item.resolveNamesToItems(itemMap);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return model;
	}
	
	private static DatabaseItem parseItem(Element itemElem) 
	{
		String itemName = itemElem.getAttribute("name");
		DatabaseItem item = new DatabaseItem(itemName);
		NodeList itemProperties = itemElem.getChildNodes();
		for (int i = 0; i < itemProperties.getLength(); i++) {
			if (itemProperties.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element propertyElem = (Element)itemProperties.item(i);
			DatabaseProperty property = parseFullProperty(propertyElem);
			item.addProperty(property.getName(), property);
		}
		return item;
	}
	
	private static DatabaseProperty parseFullProperty(Element propertyElem) 
	{
		String propertyName = propertyElem.getAttribute("name");
		DatabaseProperty property;
		String propertyType = propertyElem.getTagName();
		// Switch statement for our property type
		if (propertyType.equals("BooleanProperty")) {
			property = new BooleanDatabaseProperty(propertyName);
			boolean value = propertyElem.getAttribute("value").equals("true");
			((BooleanDatabaseProperty)property).setValue(value);
		}
		else if (propertyType.equals("IntegerProperty")) {
			property = new IntegerDatabaseProperty(propertyName);
			int value = Integer.parseInt(propertyElem.getAttribute("value"));
			((IntegerDatabaseProperty)property).setValue(value);
		}
		else if (propertyType.equals("TextProperty")) { 
			property = new TextDatabaseProperty(propertyName);
			String text = propertyElem.getAttribute("text");
			((TextDatabaseProperty)property).setText(text);
		}
		else if (propertyType.equals("ItemListProperty")) {
			property = new ItemListDatabaseProperty(propertyName); 
			NodeList itemRefs = propertyElem.getElementsByTagName("ItemReference");
			for (int i = 0; i < itemRefs.getLength(); i++) {
				Element itemRef = (Element)itemRefs.item(i);
				String itemName = itemRef.getAttribute("name");
				((ItemListDatabaseProperty)property).addItemName(itemName);
			}
		}
		else if (propertyType.equals("StringListProperty")) { 
			property = new StringListDatabaseProperty(propertyName); 
			NodeList stringRefs = propertyElem.getElementsByTagName("String"); 
			String[] stringList = new String[stringRefs.getLength()];
			for (int i = 0; i < stringRefs.getLength(); i++) { 
				Element stringRef = (Element)stringRefs.item(i); 
				stringList[i] = stringRef.getAttribute("string");
			}
			((StringListDatabaseProperty)property).setStringList(stringList);
		}
		else if (propertyType.equals("StringPairListProperty")) { 
			property = new StringPairListDatabaseProperty(propertyName); 
			NodeList pairRefs = propertyElem.getElementsByTagName("StringPair"); 
			StringPair[] pairList = new StringPair[pairRefs.getLength()];
			for (int i = 0; i < pairRefs.getLength(); i++) { 
				Element pairRef = (Element)pairRefs.item(i); 
				String string1 = pairRef.getAttribute("string1");
				String string2 = pairRef.getAttribute("string2"); 
				pairList[i] = new StringPair(string1, string2); 
			}
			((StringPairListDatabaseProperty)property).setStringPairs(pairList);
		}
		else if (propertyType.equals("CustomProperty")) {
			NodeList subPropertyElems = propertyElem.getChildNodes();
			List<DatabaseProperty> subProperties = new ArrayList<DatabaseProperty>();
			for (int i = 0; i < subPropertyElems.getLength(); i++) {
				if (subPropertyElems.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element subPropertyElem = (Element)subPropertyElems.item(i);
				subProperties.add(parseFullProperty(subPropertyElem));
			}
			DatabaseProperty[] subPropertyArray = new DatabaseProperty[subProperties.size()];
			property = new CustomProperty(propertyName, subProperties.toArray(subPropertyArray));
		}
		else 
			property = null;
		return property;
	}
	
	private static DatabaseProperty parsePropertyTemplate(Element propertyElem) 
	{
		String propertyName = propertyElem.getAttribute("name");
		DatabaseProperty property;
		String propertyType = propertyElem.getTagName();
		// Switch statement for our property type
		if (propertyType.equals("BooleanProperty"))
			property = new BooleanDatabaseProperty(propertyName);
		else if (propertyType.equals("IntegerProperty"))
			property = new IntegerDatabaseProperty(propertyName);
		else if (propertyType.equals("TextProperty"))
			property = new TextDatabaseProperty(propertyName);
		else if (propertyType.equals("ItemListProperty"))
			property = new ItemListDatabaseProperty(propertyName);
		else if (propertyType.equals("StringListProperty"))
			property = new StringListDatabaseProperty(propertyName);
		else if (propertyType.equals("StringPairListProperty"))
			property = new StringPairListDatabaseProperty(propertyName);
		else if (propertyType.equals("CustomProperty")) {
			NodeList subPropertyElems = propertyElem.getChildNodes();
			List<DatabaseProperty> subProperties = new ArrayList<DatabaseProperty>();
			for (int i = 0; i < subPropertyElems.getLength(); i++) {
				if (subPropertyElems.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element subPropertyElem = (Element)subPropertyElems.item(i);
				subProperties.add(parsePropertyTemplate(subPropertyElem));
			}
			DatabaseProperty[] subPropertyArray = new DatabaseProperty[subProperties.size()];
			property = new CustomProperty(propertyName, subProperties.toArray(subPropertyArray));
		}
		else 
			property = null;
			
		return property;
	}
	
	
}
