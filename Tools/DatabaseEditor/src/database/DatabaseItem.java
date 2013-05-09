package database;

import java.util.HashMap;
import java.util.Map;

public class DatabaseItem {

	private String _name;
	
	private Map<String, DatabaseProperty> _properties;
	
	public DatabaseItem(String name) 
	{
		_properties = new HashMap<String, DatabaseProperty>();
		_name = name;
	}
	
	/**
	 * Adds a new property to this item's list of properties. If everything is working correctly, all database items should 
	 * end up with the same list of property "types" (uniquely named properties). Note that because the database item will often receive
	 * an initial default value of 'null' for any new property, the propertyName is split from the actual value of the property we're placing
	 * in the item. 
	 * 
	 * @param propertyName The name of the property to add to the property map
	 * @param property an initial value for that property (usually null)
	 */
	public void addProperty(String propertyName, DatabaseProperty property) 
	{
		_properties.put(propertyName, property);
	}
	
	public boolean hasProperty(String propertyName) {
		return _properties.containsKey(propertyName);
	}
	
	public void removeProperty(String propertyName) 
	{ 
		_properties.remove(propertyName);
	}
	
	public Object[] getPropertyNames() 
	{
		return _properties.keySet().toArray();
	}
	
	public DatabaseProperty getPropertyValue(String propertyName) 
	{
		return _properties.get(propertyName);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void resolveNamesToItems(Map<String, DatabaseItem> itemMap) 
	{
		for (DatabaseProperty property : _properties.values()) {
			if (property != null)
				property.resolveNamesToItems(itemMap);
		}
	}
	
	public String toXML() 
	{
		String xml = "<Item name=\"" + _name + "\">\n";
		for (DatabaseProperty property : _properties.values()) {
			if (property != null)
				xml += property.toXML() + "\n";
		}
		xml += "</Item>";
		return xml;
	}
	
	@Override
	public String toString()
	{
		return _name;
	}
}
