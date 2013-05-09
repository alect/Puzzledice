package database;

import java.awt.Component;
import java.util.Map;

public abstract class DatabaseProperty {
	
	// Each Specific property needs to be identified by a name
	protected String _name;
	protected String _type;
	
	protected Component _editUI;
	
	
	public Component getEditingUI() 
	{
		return _editUI;
	}
	
	// Since we want to be able to edit things like text, integers, and booleans directly in the 
	// table view, need to provide access to specific table elements. 
	public abstract Object getTableElement();
	public abstract void setTableElement(Object value);
	public abstract boolean isTableElementEditable();
	
	// Used to create a generalized constructor for the different types of database properties
	// i.e. The database model keeps a list of a "representative" property that then initialize 
	// specific instances. 
	public abstract DatabaseProperty initializeInstance();
	
	public String getName() 
	{
		return _name;
	}
	
	public String getType() 
	{
		return _type;
	}
	
	public void update(DatabaseModel model)
	{
		
	}
	
	/** 
	 * Function called at the end of loading to resolve given item names to actual item references
	 * @param itemMap
	 */
	public void resolveNamesToItems(Map<String, DatabaseItem> itemMap) 
	{
		
	}
	
	public String toXML() 
	{
		return "<Property name=\"" + _name + "\" />";
	}
	
	
	@Override
	public String toString() 
	{
		return _name;
	}
}
