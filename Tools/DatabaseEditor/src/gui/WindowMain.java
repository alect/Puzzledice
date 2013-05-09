package gui;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import database.CustomProperty;
import database.DatabaseItem;
import database.DatabaseModel;
import database.DatabaseProperty;
import database.IntegerDatabaseProperty;
import database.ItemListDatabaseProperty;
import database.StringListDatabaseProperty;
import database.TextDatabaseProperty;
import database.BooleanDatabaseProperty;
import database.StringPairListDatabaseProperty;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;



public class WindowMain {

	private static JFrame frame;
	private static DatabaseTableModel tableModel;

	private static JList itemList, propertyList;
	private static DefaultListModel itemListModel;
	
	private static JTabbedPane viewSelectPanel;
	
	private static DatabaseModel _model;
	private static JTextField txtItemName;
	private static JTextField txtPropertyName;
	private static JComboBox propertyTypeSelect;
	private static JButton btnDeleteItem; 
	private static JButton btnDeleteProperty; 
	
	private static Component propertyEditComponent;
	
	private static DatabaseItem selectedItem; 
	private static DatabaseProperty selectedProperty; 
	
	private static String[] _propertyTypes = new String[]{"Text", "Integer", "Boolean", "Item List", "String List", "String Pairs", "Custom Type"};
	private static JTable databaseTable;
	
	// Our currently open file for purposes of save and save as
	private File _openFile;
	private String _emptyXml;
	
	private WindowMain _selfReference;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Database Editor");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) { }
		
		EventQueue.invokeLater(new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					WindowMain window = new WindowMain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WindowMain() {
		_selfReference = this;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		_model = new DatabaseModel();
		_emptyXml = _model.xmlDigest();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				if(onExit())
					System.exit(0);
			}
		});
		// Set up the program to catch OSX quit events 
		try {
			OSXAdapter.setQuitHandler(this, this.getClass().getMethod("onExit", new Class[] {}));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		// The Menu Bar 
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				clear();
				_openFile = null;
			}
		});
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Run the file dialogue later since it's blocking
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						FileDialog chooser = new FileDialog(frame, "Open", FileDialog.LOAD);
						chooser.setVisible(true);
						if (chooser.getFile() != null) {
							clear();
							File file = new File(chooser.getDirectory(), chooser.getFile());
							DatabaseModel maybeModel = DatabaseModel.createModelFromXML(file);
							if (maybeModel != null) {
								setNewModel(maybeModel);
								_openFile = file;
							}
							else { 
								JOptionPane.showMessageDialog(frame, "File failed to open!", "Open Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
			}
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Invoke the save later since it's a blocking method 
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						save();
					}
				});
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Invoke the save later since it's a blocking method
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						String xml = _model.xmlDigest();
						saveAs(xml);
					}
				});
			}
		});
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (onExit())
					System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit (TODO)");
		menuBar.add(mnEdit);
		
		JMenuItem mntmUndo = new JMenuItem("Undo (TODO)");
		mnEdit.add(mntmUndo);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(0.5);
		splitPane.setAlignmentY(Component.CENTER_ALIGNMENT);
		splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		frame.getContentPane().add(splitPane);
		
		JPanel newItemPanel = new JPanel();
		splitPane.setLeftComponent(newItemPanel);
		newItemPanel.setLayout(new BoxLayout(newItemPanel, BoxLayout.X_AXIS));
		
		JButton btnNewItem = new JButton("Add item with name:");
		newItemPanel.add(btnNewItem);
		
		btnNewItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		splitPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnNewItem.getPreferredSize().height));
		
		txtItemName = new JTextField();
		txtItemName.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnNewItem.getPreferredSize().height));
		newItemPanel.add(txtItemName);
		txtItemName.setText("itemname");
		txtItemName.setColumns(10);
		
		btnNewItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String itemName = txtItemName.getText();
				DatabaseItem newItem = new DatabaseItem(itemName);
				if(!_model.addItemToDatabase(newItem))
					JOptionPane.showMessageDialog((Component)arg0.getSource(), "Item name: " + itemName + " already exists in database!");
				// Set our selected item index to the new item
				else
					itemList.setSelectedValue(newItem, true);
			}
		});
		
		btnDeleteItem = new JButton("Delete Selected Item"); 
		btnDeleteItem.setVisible(false);
		newItemPanel.add(btnDeleteItem);
		
		btnDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) { 
				SwingUtilities.invokeLater(new Runnable() { 
					public void run() { 
						if (selectedItem == null)
							return;
						int response = JOptionPane.showConfirmDialog(frame, "Really remove " + selectedItem.getName() + "?", "Delete?", JOptionPane.YES_NO_OPTION);
						if (response == JOptionPane.YES_OPTION) { 
							_model.removeItemFromDatabase(selectedItem);
						}
					}
				});
				
			}
		}); 
		
		JPanel newPropertyPanel = new JPanel();
		splitPane.setRightComponent(newPropertyPanel);
		newPropertyPanel.setLayout(new BoxLayout(newPropertyPanel, BoxLayout.X_AXIS));
		
		JButton btnNewProperty = new JButton("Add property of type:");
		btnNewProperty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Create a new Database property based on the selected type and name
				String selectedType = propertyTypeSelect.getSelectedItem().toString();
				String attemptedName = txtPropertyName.getText();
				DatabaseProperty attemptedProperty;
				if(selectedType.equals("Text"))
					attemptedProperty = new TextDatabaseProperty(attemptedName);
				else if(selectedType.equals("Boolean"))
					attemptedProperty = new BooleanDatabaseProperty(attemptedName);
				else if(selectedType.equals("Integer"))
					attemptedProperty = new IntegerDatabaseProperty(attemptedName);
				else if(selectedType.equals("Item List"))
					attemptedProperty = new ItemListDatabaseProperty(attemptedName);
				else if(selectedType.equals("String List"))
					attemptedProperty = new StringListDatabaseProperty(attemptedName);
				else if(selectedType.equals("String Pairs"))
					attemptedProperty = new StringPairListDatabaseProperty(attemptedName);
				else if(selectedType.equals("Custom Type"))
				{
					if(_model.isPropertyNameInDatabase(attemptedName)) {
						JOptionPane.showMessageDialog((Component)arg0.getSource(), "Property name: " + attemptedName + " already exists in database!");
						return;
					}
					frame.setEnabled(false);
					JFrame propertyDefine = new CustomPropertyDefinitionFrame(_selfReference, attemptedName);
					propertyDefine.setVisible(true);
					return;
				}
				else
					return;
				
				if(!_model.addPropertyToDatabase(attemptedProperty))
					JOptionPane.showMessageDialog((Component)arg0.getSource(), "Property name: " + attemptedName + " already exists in database!");
				
				
			}
		});
		newPropertyPanel.add(btnNewProperty);
		
		
		propertyTypeSelect = new JComboBox();
		propertyTypeSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnNewProperty.getPreferredSize().height));
		newPropertyPanel.add(propertyTypeSelect);
		propertyTypeSelect.setModel(new DefaultComboBoxModel(_propertyTypes));
		
		JLabel lblWithName = new JLabel("with name:");
		newPropertyPanel.add(lblWithName);
		
		
		
		txtPropertyName = new JTextField();
		txtPropertyName.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnNewProperty.getPreferredSize().height));
		newPropertyPanel.add(txtPropertyName);
		txtPropertyName.setText("propertyname");
		txtPropertyName.setColumns(10);
		
		btnDeleteProperty = new JButton("Remove Selected Property"); 
		btnDeleteProperty.setVisible(false); 
		newPropertyPanel.add(btnDeleteProperty); 
		
		btnDeleteProperty.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				SwingUtilities.invokeLater(new Runnable() { 
					public void run() { 
						if (selectedProperty == null)
							return;
						int response = JOptionPane.showConfirmDialog(frame, "Really remove property " + selectedProperty.getName() + "?", "Delete?", JOptionPane.YES_NO_OPTION);
						if (response == JOptionPane.YES_OPTION) { 
							_model.removePropertyFromDatabase(selectedProperty);
						}
					}
				});
			}
		}); 
		
		viewSelectPanel = new JTabbedPane(JTabbedPane.TOP);
		
		
		frame.getContentPane().add(viewSelectPanel);
		
		JPanel listViewPanel = new JPanel();
		viewSelectPanel.addTab("List View", null, listViewPanel, null);
		listViewPanel.setLayout(new BoxLayout(listViewPanel, BoxLayout.X_AXIS));
		
		JPanel itemSelectPanel = new JPanel();
		listViewPanel.add(itemSelectPanel);
		itemSelectPanel.setLayout(new BoxLayout(itemSelectPanel, BoxLayout.Y_AXIS));
		
		JLabel itemSelectLabel = new JLabel("Database Items");
		itemSelectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		itemSelectPanel.add(itemSelectLabel);
		
		
		
		JScrollPane itemSelectScroll = new JScrollPane();
		itemSelectPanel.add(itemSelectScroll);
		itemList = new JList();
		itemList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				selectedItem = (DatabaseItem)itemList.getSelectedValue();
				
				if(selectedItem == null) { 
					btnDeleteItem.setVisible(false); 
					return;
				}
				btnDeleteItem.setVisible(true);
				// Change the list of displayed properties to the property names the item has
				propertyList.setListData(selectedItem.getPropertyNames());
			}
		});
		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListModel = new DefaultListModel();
		itemList.setModel(itemListModel);
		itemSelectScroll.setViewportView(itemList);
		
		JPanel propertySelectPanel = new JPanel();
		listViewPanel.add(propertySelectPanel);
		propertySelectPanel.setLayout(new BoxLayout(propertySelectPanel, BoxLayout.Y_AXIS));
		
		JLabel propertySelectLabel = new JLabel("Properties");
		propertySelectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		propertySelectPanel.add(propertySelectLabel);
		
		JScrollPane propertySelectScroll = new JScrollPane();
		propertySelectPanel.add(propertySelectScroll);
		
		propertyList = new JList();
		propertyList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(propertyEditComponent != null) {
					frame.getContentPane().remove(propertyEditComponent);
				}
				if(propertyList.getSelectedValue() != null && itemList.getSelectedValue() != null) {
					DatabaseItem selectedItem = (DatabaseItem)itemList.getSelectedValue();
					String propertyName = propertyList.getSelectedValue().toString();
					btnDeleteProperty.setVisible(true); 
					selectedProperty = (DatabaseProperty)selectedItem.getPropertyValue(propertyName);
					if(selectedProperty == null) {
						selectedProperty = _model.createInstance(propertyName);
						selectedItem.addProperty(propertyName, selectedProperty);
					}
					selectedProperty.update(_model);
					propertyEditComponent = selectedProperty.getEditingUI();
					frame.getContentPane().add(propertyEditComponent);
				}
				else {
					propertyEditComponent = null;
					btnDeleteProperty.setVisible(false); 
				}
				frame.getContentPane().validate();
				frame.getContentPane().repaint();
			}
		});
		propertySelectScroll.setViewportView(propertyList);
		
		
		
		JPanel tableViewPanel = new JPanel();
		viewSelectPanel.addTab("Table View", null, tableViewPanel, null);
		tableViewPanel.setLayout(new BoxLayout(tableViewPanel, BoxLayout.Y_AXIS));
		
		
		JScrollPane scrollPane = new JScrollPane();
		tableViewPanel.add(scrollPane);
		
		databaseTable = new JTable();
		databaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		databaseTable.setRowSelectionAllowed(false);
		scrollPane.setViewportView(databaseTable);
		tableModel = new DatabaseTableModel(_model);
		databaseTable.setModel(tableModel);
		//databaseTable.add
		
		ListSelectionListener tableListener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(propertyEditComponent != null) {
					frame.getContentPane().remove(propertyEditComponent);
				}
				if(databaseTable.getSelectedColumn() != -1 && databaseTable.getSelectedRow() != -1) {
					selectedItem = tableModel.getSelectedItem(databaseTable.getSelectedRow());
					selectedProperty = tableModel.selectProperty(databaseTable.getSelectedRow(), databaseTable.getSelectedColumn());
					btnDeleteProperty.setVisible(selectedProperty != null);
					btnDeleteItem.setVisible(true); 
					if(selectedProperty == null || selectedProperty.isTableElementEditable())
						propertyEditComponent = null;
					else {
						propertyEditComponent = selectedProperty.getEditingUI();
						frame.getContentPane().add(propertyEditComponent);
					}
				}
				else {
					btnDeleteItem.setVisible(false);
					btnDeleteProperty.setVisible(false); 
					selectedItem = null; 
					selectedProperty = null; 
					propertyEditComponent = null;
				}
				frame.getContentPane().validate();
				frame.getContentPane().repaint();
				
			}
			
		};
		databaseTable.getSelectionModel().addListSelectionListener(tableListener);
		databaseTable.getColumnModel().getSelectionModel().addListSelectionListener(tableListener);
		
		

		
		
		viewSelectPanel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				//System.out.println(viewSelectPanel.getSelectedIndex());
				if(viewSelectPanel.getSelectedIndex() == 1) {
					propertyList.clearSelection();
				}
				else if(viewSelectPanel.getSelectedIndex() == 0) {
					databaseTable.clearSelection();
				}
				
			}
			
		});
	}
	
	public static void addItemToListView(DatabaseItem item)
	{
		itemListModel.addElement(item);
	}
	
	public static void removeItemFromListView(DatabaseItem item) 
	{ 
		itemListModel.removeElement(item); 
	}
	
	
	public static void addItemToTableView(DatabaseItem item) 
	{
		tableModel.addItem(item);
	}
	
	public static void removeItemFromTableView(DatabaseItem item) 
	{ 
		tableModel.removeItem(item); 
	}
	
	public static void removePropertyFromTableView(DatabaseProperty property) 
	{ 
		tableModel.removeProperty(property);
	}
	
	public static void addPropertyToTableView(DatabaseProperty property) {
		tableModel.addProperty(property);
	}
	
	public static void updateGUI()
	{
		itemList.invalidate();
		propertyList.invalidate();
		
		DatabaseItem selectedItem = (DatabaseItem)itemList.getSelectedValue();
		// Update the list of properties for the selected database item
		if(selectedItem != null) {
			propertyList.setListData(selectedItem.getPropertyNames());
		}
		
	}
	
	
	public void returnFromCustomPropertyDef(CustomProperty property) 
	{
		frame.setEnabled(true);
		
		if(property != null)
			_model.addPropertyToDatabase(property);
	}
	
	public void clear() {
		_model = new DatabaseModel();
		itemListModel.clear();
		itemList.setSelectedValue(null, true);
		propertyList.setModel(new DefaultListModel());
		propertyList.setSelectedValue(null, true);
		tableModel = new DatabaseTableModel(_model);
		databaseTable.setModel(tableModel);
		
	}
	
	public void setNewModel(DatabaseModel model) {
		_model = model;
		tableModel = new DatabaseTableModel(_model);
		databaseTable.setModel(tableModel);
	}
	
	private boolean save() {
		String xml = _model.xmlDigest();
		if (_openFile != null) {
			try { 
				BufferedWriter writer = new BufferedWriter(new FileWriter(_openFile));
				writer.write(xml);
				writer.close();
				return true;
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				return false;
			}
		}
		else 
			return saveAs(xml);
	}
	
	private boolean saveAs(String xml) {
		FileDialog chooser = new FileDialog(frame, "Save", FileDialog.SAVE);
		chooser.setVisible(true);
		if (chooser.getFile() != null) {
			try { 
				File saveFile = new File(chooser.getDirectory(), chooser.getFile());
				BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
				writer.write(xml);
				writer.close();
				_openFile = saveFile;
				return true;
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				return false;
			}
		}
		else { 
			return false;
		}
	}
	
	public boolean onExit() {
		if (!unsavedChanges())
			return true;
		else { 
			int response = JOptionPane.showConfirmDialog(frame, "Save Unsaved Changes?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
			if ((response == JOptionPane.YES_OPTION && save()) || response == JOptionPane.NO_OPTION)
				return true;
		}
		return false;
	}
	
	private Boolean unsavedChanges() {
		String currentXml = _model.xmlDigest();
		String oldXml;
		if (_openFile == null)
			oldXml = _emptyXml;
		else { 
			
			try {
				FileInputStream stream = new FileInputStream(_openFile);
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				/* Instead of using default, pass in a decoder. */
				oldXml = Charset.defaultCharset().decode(bb).toString();
			 }
			 catch (Exception e) {
				 e.printStackTrace();
				 return true;
			 }
		}
		return !currentXml.equals(oldXml);
	}
	

}
