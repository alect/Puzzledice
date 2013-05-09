package database;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextDatabaseProperty extends DatabaseProperty {

	
	private String _text = "";
	private JTextField _editField;
	
	public TextDatabaseProperty(String name) 
	{
		_name = name;
		_type = "Text";
		// Create the UI for editing this property
		JPanel editUI = new JPanel();
		editUI.setLayout(new BoxLayout(editUI, BoxLayout.X_AXIS));
		
		JLabel editLabel = new JLabel(" " + _name + ":");
		editUI.add(editLabel);
		
		_editField = new JTextField();
		_editField.setMaximumSize(new Dimension(Integer.MAX_VALUE, editLabel.getPreferredSize().height*2));
		_editField.setColumns(10);
		_editField.getDocument().addDocumentListener(new DocumentListener() {
			 public void changedUpdate(DocumentEvent e) {
				_text = _editField.getText(); 
			 }
			 
			 public void insertUpdate(DocumentEvent e) {
				 _text = _editField.getText();
			 }
			 
			 public void removeUpdate(DocumentEvent e) {
				 _text = _editField.getText();
			 }
		});
		
		
		editUI.add(_editField);
		
		_editUI = editUI;
		
	}
	
	public void setText(String text) {
		_text = text;
		_editField.setText(_text);
	}
	
	@Override 
	public void update(DatabaseModel model) {
		_editField.setText(_text);
	}
	
	@Override
	public Object getTableElement() {
		return _text;
	}

	@Override
	public void setTableElement(Object value) {
		_text = value.toString();
	}
	
	@Override 
	public boolean isTableElementEditable() {
		return true;
	}

	@Override
	public DatabaseProperty initializeInstance() {
		return new TextDatabaseProperty(_name);
	}
	
	@Override 
	public String toXML() {
		String xml = "<TextProperty name=\"" + _name + "\" text=\"" + _text + "\"/>";
		return xml;
	}

}
