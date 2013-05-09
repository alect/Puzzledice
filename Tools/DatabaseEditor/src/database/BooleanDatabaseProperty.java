package database;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BooleanDatabaseProperty extends DatabaseProperty {

	private boolean _value;
	private JCheckBox _editCheckBox;
	
	public BooleanDatabaseProperty(String name) 
	{
		_name = name;
		_type = "Boolean";
		_value = false;
		
		
		JPanel editUI = new JPanel();
		editUI.setLayout(new BoxLayout(editUI, BoxLayout.X_AXIS));
		
		JLabel editLabel = new JLabel(" " + _name + ":");
		editUI.add(editLabel);
		
		_editCheckBox = new JCheckBox();
		_editCheckBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				_value = _editCheckBox.isSelected();
			}
		});
		
		editUI.add(_editCheckBox);
		
		
		_editUI = editUI;
		
	}
	
	public void setValue(boolean value) {
		_value = value;
		_editCheckBox.setSelected(value);
	}
	
	@Override 
	public void update(DatabaseModel model) {
		_editCheckBox.setSelected(_value);
	}
	
	@Override
	public Object getTableElement() {
		return (Boolean)_value;
	}

	@Override
	public void setTableElement(Object value) {
		_value = (Boolean)value;

	}
	
	@Override 
	public boolean isTableElementEditable() {
		return true;
	}

	@Override
	public DatabaseProperty initializeInstance() {
		return new BooleanDatabaseProperty(_name);
	}
	
	@Override 
	public String toXML() {
		String xml = "<BooleanProperty name=\"" + _name + "\" value=\"" + _value + "\"/>";
		return xml;
	}

}
