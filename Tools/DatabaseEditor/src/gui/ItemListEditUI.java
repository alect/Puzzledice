package gui;

import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import database.DatabaseItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ItemListEditUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5920606904420208505L;
	private JComboBox _itemSelectComboBox;
	private JList _itemList;
	/**
	 * Create the panel.
	 */
	public ItemListEditUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel addDeletePanel = new JPanel();
		add(addDeletePanel);
		addDeletePanel.setLayout(new BoxLayout(addDeletePanel, BoxLayout.X_AXIS));
		
		JButton btnAdd = new JButton("Add:");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(_itemSelectComboBox.getSelectedItem() != null) {
					DefaultListModel listModel = (DefaultListModel)_itemList.getModel();
					listModel.addElement(_itemSelectComboBox.getSelectedItem());
				}
			}
		});
		addDeletePanel.add(btnAdd);
		
		_itemSelectComboBox = new JComboBox();
		_itemSelectComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnAdd.getPreferredSize().height));
		addDeletePanel.add(_itemSelectComboBox);
		
		final JButton btnRemoveSelectedItem = new JButton("Remove Selected Item");
		btnRemoveSelectedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(_itemList.getSelectedValue() != null) {
					DefaultListModel listModel = (DefaultListModel)_itemList.getModel();
					listModel.removeElement(_itemList.getSelectedValue());
				}
			}
		});
		btnRemoveSelectedItem.setVisible(false);
		addDeletePanel.add(btnRemoveSelectedItem);
		
		JScrollPane itemListScroll = new JScrollPane();
		add(itemListScroll);
		
		_itemList = new JList();
		_itemList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(_itemList.getSelectedValue() != null)
					btnRemoveSelectedItem.setVisible(true);
			}
		});
		_itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListScroll.setViewportView(_itemList);
		
		DefaultListModel listModel = new DefaultListModel();
		_itemList.setModel(listModel);

	}
	
	public void setItemSelect(Object[] itemList) {
		_itemSelectComboBox.setModel(new DefaultComboBoxModel(itemList));
	}
	
	public void setItemList(DatabaseItem[] itemList) {
		DefaultListModel listModel = new DefaultListModel();
		for (DatabaseItem item : itemList) {
			listModel.addElement(item);
		}
		_itemList.setModel(listModel);
		_itemList.setSelectedValue(null, true);
	}
	
	public DatabaseItem[] getItemList() 
	{
		DatabaseItem[] retVal = new DatabaseItem[_itemList.getModel().getSize()];
		for (int i = 0; i < _itemList.getModel().getSize(); i++) {
			retVal[i] = (DatabaseItem)_itemList.getModel().getElementAt(i);
		}

		return retVal;
	}

}
