package database;

import gui.StringListEditUI;

public class StringListDatabaseProperty extends DatabaseProperty {

	public StringListDatabaseProperty(String name) 
	{ 
		_name = name; 
		_type = "String List"; 
		_editUI = new StringListEditUI(); 
	}
	
	public void setStringList(String[] stringList) { 
		((StringListEditUI)_editUI).setStringList(stringList);
	}
	
	@Override
	public Object getTableElement() {
		return this;
	}

	@Override
	public void setTableElement(Object value) {
	}

	@Override
	public boolean isTableElementEditable() {
		return false;
	}

	@Override
	public DatabaseProperty initializeInstance() {
		return new StringListDatabaseProperty(_name);
	}
	
	@Override 
	public String toXML() { 
		String xml = "<StringListProperty name=\"" + _name +"\">\n"; 
		String[] strings = ((StringListEditUI)_editUI).getStringList(); 
		for (String str : strings) { 
			xml += "<String string=\"" + str + "\"/>\n";
		}
		xml += "</StringListProperty>";
		return xml;
	}

}
