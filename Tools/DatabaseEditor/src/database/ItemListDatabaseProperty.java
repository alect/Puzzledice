package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gui.ItemListEditUI;

public class ItemListDatabaseProperty extends DatabaseProperty {

	private List<String> _itemNames;
	
	public ItemListDatabaseProperty(String name) 
	{
		_name = name;
		_type = "Item List";
		_itemNames = new ArrayList<String>();
		_editUI = new ItemListEditUI();
	}
	
	public void addItemName(String name) {
		_itemNames.add(name);
	}
	
	@Override
	public void update(DatabaseModel model) 
	{
		((ItemListEditUI)_editUI).setItemSelect(model.getItemList());
	}
	
	@Override
	public Object getTableElement() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setTableElement(Object value) {
		// TODO Auto-generated method stub

	}
	
	@Override 
	public boolean isTableElementEditable() {
		return false;
	}

	@Override
	public DatabaseProperty initializeInstance() {
		// TODO Auto-generated method stub
		return new ItemListDatabaseProperty(_name); 
	}
	
	@Override 
	public void resolveNamesToItems(Map<String, DatabaseItem> itemMap) 
	{
		DatabaseItem[] items = new DatabaseItem[_itemNames.size()];
		for (int i = 0 ; i < items.length; i++) {
			items[i] = itemMap.get(_itemNames.get(i));
		}
		((ItemListEditUI)_editUI).setItemList(items);
	}
	
	
	@Override 
	public String toXML() {
		String xml = "<ItemListProperty name=\"" + _name + "\">\n";
		DatabaseItem[] items = ((ItemListEditUI)_editUI).getItemList();
		for (DatabaseItem item : items) {
			xml += "<ItemReference name=\"" + item.getName() + "\"/>\n";
		}
		xml += "</ItemListProperty>";
		return xml;
	}

}
