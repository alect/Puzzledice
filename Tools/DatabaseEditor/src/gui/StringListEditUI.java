package gui;

import javax.swing.JPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StringListEditUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8999521059583478855L;

	private JTextField _stringSelect; 
	private JList _stringList; 
	
	public StringListEditUI() { 
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
		
		JPanel addDeletePanel = new JPanel(); 
		add(addDeletePanel); 
		addDeletePanel.setLayout(new BoxLayout(addDeletePanel, BoxLayout.X_AXIS)); 
		
		JButton btnAdd = new JButton("Add:"); 
		btnAdd.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				DefaultListModel listModel = (DefaultListModel)_stringList.getModel();
				listModel.addElement(_stringSelect.getText());
			}
		});
		addDeletePanel.add(btnAdd); 
		
		JLabel stringLabel = new JLabel("Text:"); 
		addDeletePanel.add(stringLabel); 
		
		_stringSelect = new JTextField(); 
		_stringSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, stringLabel.getPreferredSize().height*2)); 
		_stringSelect.setColumns(10); 
		addDeletePanel.add(_stringSelect); 
		
		final JButton btnRemoveString = new JButton("Remove Text"); 
		btnRemoveString.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				if (_stringList.getSelectedValue() != null) { 
					DefaultListModel listModel = (DefaultListModel)_stringList.getModel();
					listModel.removeElement(_stringList.getSelectedValue()); 
				}
			}
		});
		btnRemoveString.setVisible(false); 
		addDeletePanel.add(btnRemoveString);
		
		JScrollPane stringListScroll = new JScrollPane(); 
		add(stringListScroll); 
		
		_stringList = new JList(); 
		_stringList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) { 
				if (_stringList.getSelectedValue() != null)
					btnRemoveString.setVisible(true); 
				else 
					btnRemoveString.setVisible(false);
			}
		}); 
		_stringList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		stringListScroll.setViewportView(_stringList); 
		
		DefaultListModel listModel = new DefaultListModel(); 
		_stringList.setModel(listModel); 
	}
	
	public void setStringList(String[] stringList) { 
		DefaultListModel listModel = new DefaultListModel(); 
		for (String str : stringList) { 
			listModel.addElement(str);
		}
		_stringList.setModel(listModel);
		_stringList.setSelectedValue(null, true);
	}
	
	public String[] getStringList() { 
		String[] retVal = new String[_stringList.getModel().getSize()];
		for (int i = 0; i < _stringList.getModel().getSize(); i++) { 
			retVal[i] = (String)_stringList.getModel().getElementAt(i); 
		}
		return retVal;
	}
	
}
