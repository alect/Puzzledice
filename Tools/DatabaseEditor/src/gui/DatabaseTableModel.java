package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import database.DatabaseItem;
import database.DatabaseModel;
import database.DatabaseProperty;

public class DatabaseTableModel extends DefaultTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9048042861984139507L;

	private List<DatabaseItem> _databaseItems;
	
	private DatabaseModel _model;
	
	public DatabaseTableModel (DatabaseModel model) 
	{
		super();
		_databaseItems = new ArrayList<DatabaseItem>();
		_model = model;
		
		this.addColumn("Item Name");
		
		// Add any existing properties and items 
		for (DatabaseProperty property : _model.getPropertyList()) {
			this.addProperty(property);
		}
		
		for (DatabaseItem item : _model.getItemList()) {
			this.addItem(item);
		}
		
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) 
	{
		DatabaseItem item = _databaseItems.get(row);
		String propertyName = this.getColumnName(column);
		if(propertyName.equals("Item Name"))
			return false;
		
		DatabaseProperty property = item.getPropertyValue(propertyName);
		if(property == null) 
			return false;
		else
			return property.isTableElementEditable();
	}
	
	@Override 
	public Object getValueAt(int row, int column)
	{
		
		DatabaseItem item = _databaseItems.get(row);
		String propertyName = this.getColumnName(column);
		if(propertyName.equals("Item Name"))
			return item.getName();
		
		
		DatabaseProperty property = item.getPropertyValue(propertyName);
		if(property == null) {
			property = _model.getPropertyValue(propertyName);
			if(property.isTableElementEditable())
				return property.getTableElement();
			else
				return "...";
		}
		else if(!property.isTableElementEditable())
			return "...";
		else
			return property.getTableElement();
			
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int column) 
	{	
		String propertyName = this.getColumnName(column);
		if(propertyName.equals("Item Name"))
			return String.class;
		
		DatabaseProperty property = _model.getPropertyValue(propertyName);
		if(property.isTableElementEditable())
			return property.getTableElement().getClass();
		else 
			return String.class;
	}

	@Override 
	public void setValueAt(Object aValue, int row, int column) 
	{
		DatabaseItem item = _databaseItems.get(row);
		String propertyName = this.getColumnName(column);
		DatabaseProperty property = item.getPropertyValue(propertyName);
		if(property != null)
			property.setTableElement(aValue);
	}
	
	public void addProperty(DatabaseProperty newProperty) {
		//Object[] properties = new Object[getRowCount()];
		
		
		this.addColumn(newProperty.getName());
	}
	
	@SuppressWarnings("rawtypes")
	public void removeProperty(DatabaseProperty propertyToRemove) { 
		int column = this.findColumn(propertyToRemove.getName()); 
		if (column != -1) { 
			columnIdentifiers.remove(column); 
			for (Object row : this.dataVector) { 
				((Vector)row).remove(column); 
			}
			this.fireTableStructureChanged();
		}
	}
	
	public void addItem(DatabaseItem newItem) {
		// First construct the array of the properties
		_databaseItems.add(newItem);
		
		Object[] properties = new Object[getColumnCount()];
		for(int i = 0; i < getColumnCount(); i++) {
			String propertyName = getColumnName(i);
			properties[i] = newItem.getPropertyValue(propertyName);
		}
		this.addRow(properties);
		
	}
	
	public void removeItem(DatabaseItem itemToRemove) { 
		int rowToRemove = _databaseItems.indexOf(itemToRemove); 
		if (rowToRemove != -1)
			this.removeRow(rowToRemove);
		_databaseItems.remove(itemToRemove);
	}
	
	public DatabaseItem getSelectedItem(int row) { 
		return _databaseItems.get(row); 
	}
	
	public DatabaseProperty selectProperty(int row, int column) {
		DatabaseItem item = _databaseItems.get(row);
		String propertyName = this.getColumnName(column);
		if(propertyName.equals("Item Name"))
			return null;
		DatabaseProperty property = item.getPropertyValue(propertyName);
		if(property == null) {
			property = _model.createInstance(propertyName);
			item.addProperty(propertyName, property);
		}
		property.update(_model);
		return property;
	}
	
	
}
