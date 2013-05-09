package database;

import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class CustomProperty extends DatabaseProperty {

	
	private DatabaseProperty[] _subProperties;
	
	public CustomProperty(String name, DatabaseProperty[] subProperties) 
	{
		_name = name;
		_type = "Custom";
		_subProperties = subProperties;
		
		JPanel editUI = new JPanel();
		editUI.setLayout(new BoxLayout(editUI, BoxLayout.Y_AXIS));
		
		// Add all of our sub-property uis to our ui
		for (DatabaseProperty subProperty : _subProperties) {
			editUI.add(subProperty.getEditingUI());
		}
		
		_editUI = editUI;
	}
	
	@Override 
	public void update(DatabaseModel model) {
		for (DatabaseProperty subProperty : _subProperties) {
			subProperty.update(model);
		}
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
		DatabaseProperty[] copyArray = new DatabaseProperty[_subProperties.length];
		for(int i = 0; i < copyArray.length; i++) {
			copyArray[i] = _subProperties[i].initializeInstance();
		}
		return new CustomProperty(_name, copyArray);
	}
	
	@Override 
	public void resolveNamesToItems(Map<String, DatabaseItem> itemMap) 
	{
		for (DatabaseProperty subProperty : _subProperties) {
			subProperty.resolveNamesToItems(itemMap);
		}
	}
	
	@Override 
	public String toXML() {
		String xml = "<CustomProperty name=\"" + _name + "\">\n";
		for (DatabaseProperty subProperty : _subProperties) {
			xml += subProperty.toXML() + "\n";
		}
		xml += "</CustomProperty>";
		return xml;
	}

}
