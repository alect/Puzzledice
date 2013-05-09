package database;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IntegerDatabaseProperty extends DatabaseProperty {

	private int _value;
	private JSpinner _editSpinner;
	
	public IntegerDatabaseProperty(String name) 
	{
		_name = name;
		_type = "Integer";
		_value = 0;
		
		JPanel editUI = new JPanel();
		editUI.setLayout(new BoxLayout(editUI, BoxLayout.X_AXIS));
		
		JLabel editLabel = new JLabel(" " + _name + ":");
		editUI.add(editLabel);
		
		_editSpinner = new JSpinner();
		_editSpinner.setMaximumSize(new Dimension(editLabel.getPreferredSize().width, editLabel.getPreferredSize().height*4));
		_editSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				_value = (Integer)_editSpinner.getValue();
			}
		});
		
		editUI.add(_editSpinner);
		
		_editUI = editUI;
		
	}
	
	public void setValue(int value) {
		_value = value;
		_editSpinner.setValue(_value);
	}
	
	@Override 
	public void update(DatabaseModel model) {
		_editSpinner.setValue(_value);
	}
	
	
	@Override
	public Object getTableElement() {
		return _value;
	}

	@Override
	public void setTableElement(Object value) {
		_value = (Integer)value;
	}

	@Override 
	public boolean isTableElementEditable() {
		return true;
	}
	
	@Override
	public DatabaseProperty initializeInstance() {
		return new IntegerDatabaseProperty(_name);
	}
	
	@Override 
	public String toXML() {
		String xml = "<IntegerProperty name=\"" + _name + "\" value=\"" + _value + "\"/>";
		return xml;
	}

}
