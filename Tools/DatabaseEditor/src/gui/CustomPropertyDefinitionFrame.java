package gui;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JList;

import database.BooleanDatabaseProperty;
import database.CustomProperty;
import database.DatabaseProperty;
import database.IntegerDatabaseProperty;
import database.ItemListDatabaseProperty;
import database.TextDatabaseProperty;
import database.StringListDatabaseProperty;
import database.StringPairListDatabaseProperty;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class CustomPropertyDefinitionFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7417065173869407704L;
	private JPanel contentPane;
	private JTextField nameSelect;
	private JList subpropertyList;
	private DefaultListModel listModel;
	private JComboBox typeSelect;
	private JButton btnRemove;
	
	private JFrame _selfRef;
	
	private static String[] _propertyTypes = new String[]{"Text", "Integer", "Boolean", "Item List", "String List", "String Pairs"};
	
	
	private WindowMain _parent;
	private String _name;
	
	/**
	 * Create the frame.
	 */
	public CustomPropertyDefinitionFrame(WindowMain parent, String propertyName) {
		_selfRef = this;
		
		_parent = parent;
		_name = propertyName;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				_parent.returnFromCustomPropertyDef(null);
			}
		});
		setTitle("Define Your Own Custom Property");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 718, 466);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		JPanel addRemovePanel = new JPanel();
		contentPane.add(addRemovePanel);
		addRemovePanel.setLayout(new BoxLayout(addRemovePanel, BoxLayout.X_AXIS));
		
		JButton btnAdd = new JButton("Add Subproperty of Type:");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String propertyType = typeSelect.getSelectedItem().toString();
				String propertyName = nameSelect.getText();
				DatabaseProperty subProperty;
				if(propertyType.equals("Text"))
					subProperty = new TextDatabaseProperty(propertyName) {
					@Override 
					public String toString() {
						return getName() + " (" + getType() + ")";
					}
				};
				else if(propertyType.equals("Integer"))
					subProperty = new IntegerDatabaseProperty(propertyName) {
					@Override
					public String toString() {
						return getName() + " (" + getType() + ")";
					}
				};
				else if(propertyType.equals("Boolean"))
					subProperty = new BooleanDatabaseProperty(propertyName) {
					@Override
					public String toString() {
						return getName() + " (" + getType() + ")";
					}
				};
				else if (propertyType.equals("String List"))
					subProperty = new StringListDatabaseProperty(propertyName) { 
					@Override 
					public String toString() { 
						return getName() + " (" + getType() + ")";
					}
				};
				else if(propertyType.equals("Item List"))
					subProperty = new ItemListDatabaseProperty(propertyName) {
					@Override 
					public String toString() {
						return getName() + " (" + getType() + ")";
					}
				};
				else if(propertyType.equals("String Pairs"))
					subProperty = new StringPairListDatabaseProperty(propertyName) { 
					@Override
					public String toString() { 
						return getName() + " (" + getType() + ")";
					}
				};
				else
					return;
				
				listModel.addElement(subProperty);
				subpropertyList.setSelectedValue(subProperty, true);
			}
		});
		addRemovePanel.add(btnAdd);
		
		typeSelect = new JComboBox();
		typeSelect.setModel(new DefaultComboBoxModel(_propertyTypes));
		typeSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnAdd.getPreferredSize().height));
		addRemovePanel.add(typeSelect);
		
		JLabel lblWithName = new JLabel("with name:");
		addRemovePanel.add(lblWithName);
		
		nameSelect = new JTextField();
		addRemovePanel.add(nameSelect);
		nameSelect.setColumns(10);
		nameSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnAdd.getPreferredSize().height));
		
		btnRemove = new JButton("Remove Selected Subproperty");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(subpropertyList.getSelectedValue() != null) {
					listModel.removeElement(subpropertyList.getSelectedValue());
				}
			}
		});
		btnRemove.setVisible(false);
		addRemovePanel.add(btnRemove);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);
		
		subpropertyList = new JList();
		subpropertyList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				btnRemove.setVisible(subpropertyList.getSelectedValue() != null);
			}
		});
		subpropertyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(subpropertyList);
		
		listModel = new DefaultListModel();
		subpropertyList.setModel(listModel);
		
		JButton btnDone = new JButton("Done!");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(listModel.size() == 0) {
					_parent.returnFromCustomPropertyDef(null);
				}
				
				DatabaseProperty[] propertyList = new DatabaseProperty[listModel.size()];
				for(int i = 0; i < listModel.size(); i++) {
					propertyList[i] = (DatabaseProperty)listModel.get(i);
				}
				CustomProperty propertyToReturn = new CustomProperty(_name, propertyList);
				_parent.returnFromCustomPropertyDef(propertyToReturn);
				
				_selfRef.dispose();
		
			}
		});
		btnDone.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(btnDone);
	}

}
