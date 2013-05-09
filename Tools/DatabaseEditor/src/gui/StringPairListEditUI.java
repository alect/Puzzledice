package gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import utils.StringPair;

public class StringPairListEditUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6211243873148848369L;
	
	private JTextField _string1Select; 
	private JTextField _string2Select; 
	private JList _pairList; 
	
	public StringPairListEditUI() { 
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel addDeletePanel = new JPanel(); 
		add(addDeletePanel); 
		addDeletePanel.setLayout(new BoxLayout(addDeletePanel, BoxLayout.X_AXIS)); 
		
		JButton btnAdd = new JButton("Add:"); 
		btnAdd.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 

				DefaultListModel listModel = (DefaultListModel)_pairList.getModel(); 
				listModel.addElement(new StringPair(_string1Select.getText(), _string2Select.getText()));
				
			}
		}); 
		addDeletePanel.add(btnAdd); 
		
		JLabel string1Label = new JLabel("Element 1:");
		addDeletePanel.add(string1Label);
		
		_string1Select = new JTextField();
		_string1Select.setMaximumSize(new Dimension(Integer.MAX_VALUE, string1Label.getPreferredSize().height*2));
		_string1Select.setColumns(10);
		addDeletePanel.add(_string1Select); 
		
		JLabel string2Label = new JLabel("Element 2:"); 
		addDeletePanel.add(string2Label); 
		
		_string2Select = new JTextField(); 
		_string2Select.setMaximumSize(new Dimension(Integer.MAX_VALUE, string2Label.getPreferredSize().height*2)); 
		_string2Select.setColumns(10); 
		addDeletePanel.add(_string2Select); 
		
		final JButton btnRemovePair = new JButton("Remove Pair"); 
		btnRemovePair.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				if (_pairList.getSelectedValue() != null) { 
					DefaultListModel listModel = (DefaultListModel)_pairList.getModel(); 
					listModel.removeElement(_pairList.getSelectedValue());
				}
			}
		});
		btnRemovePair.setVisible(false); 
		addDeletePanel.add(btnRemovePair); 
		
		JScrollPane pairListScroll = new JScrollPane(); 
		add(pairListScroll); 
		
		_pairList = new JList(); 
		_pairList.addListSelectionListener(new ListSelectionListener() { 
			public void valueChanged(ListSelectionEvent evt) { 
				if (_pairList.getSelectedValue() != null)
					btnRemovePair.setVisible(true); 
				else 
					btnRemovePair.setVisible(false);
			}
		});
		_pairList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pairListScroll.setViewportView(_pairList);
		
		DefaultListModel listModel = new DefaultListModel(); 
		_pairList.setModel(listModel);
		
	}
	
	public void setPairList(StringPair[] pairList) { 
		DefaultListModel listModel = new DefaultListModel(); 
		for (StringPair pair : pairList) { 
			listModel.addElement(pair); 
		}
		_pairList.setModel(listModel); 
		_pairList.setSelectedValue(null, true);
	}
	
	public StringPair[] getPairList() { 
		StringPair[] retVal = new StringPair[_pairList.getModel().getSize()];
		for (int i = 0; i < _pairList.getModel().getSize(); i++) { 
			retVal[i] = (StringPair)_pairList.getModel().getElementAt(i); 
		}
		return retVal;
	}
	

}
