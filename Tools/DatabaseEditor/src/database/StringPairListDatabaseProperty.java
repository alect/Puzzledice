package database;

import gui.StringPairListEditUI;
import utils.StringPair;

public class StringPairListDatabaseProperty extends DatabaseProperty {

	public StringPairListDatabaseProperty(String name) 
	{ 
		_name = name; 
		_type = "String Pair List";
		_editUI = new StringPairListEditUI();
	}
	
	public void setStringPairs(StringPair[] pairList) { 
		((StringPairListEditUI)_editUI).setPairList(pairList);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DatabaseProperty initializeInstance() {
		// TODO Auto-generated method stub
		return new StringPairListDatabaseProperty(_name);
	}
	
	@Override 
	public String toXML() { 
		String xml = "<StringPairListProperty name=\"" + _name +"\">\n"; 
		StringPair[] pairs = ((StringPairListEditUI)_editUI).getPairList(); 
		for (StringPair pair : pairs) { 
			xml += "<StringPair string1=\"" + pair.getString1() + "\" string2=\"" + pair.getString2() + "\"/>\n";
		}
		xml += "</StringPairListProperty>";
		return xml;
	}

}
