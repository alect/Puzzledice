package gui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;

import java.awt.Dimension;
import javax.swing.JComboBox;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.view.mxGraph;

import puzzledice.AreaBlock;
import puzzledice.DoorUnlockBlock;
import puzzledice.PuzzleBlock;

public class AreaEditPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTextField txtAreaName;
	private final JList areaList;
	private final static DefaultListModel areaListModel = new DefaultListModel();
	private final JButton btnChangeArea;
	private final JScrollPane scrollPane;
	private final JComboBox addDoorSelect;
	private final JComboBox removeDoorSelect;
	private final JButton btnDeleteArea;
	
	private static AreaBlock _startArea = null; 
	
	private int selectedAreaIndex;
	
	private static int nextAreaIndex = 0;
	public static void reset() {
		nextAreaIndex = 0;
	}
	/**
	 * Create the panel.
	 */
	public AreaEditPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
				
		final JButton btnNewArea = new JButton("Add Area");
		panel_1.add(btnNewArea);
		btnNewArea.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		scrollPane = new JScrollPane();
		panel_1.add(scrollPane);
		areaList = new JList(areaListModel);
		areaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(areaList);
		
		
		
		
		final JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setVisible(false);
		
		btnDeleteArea = new JButton("Delete Area");
		
		btnDeleteArea.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnDeleteArea);
		
		final JPanel namePanel = new JPanel();
		panel.add(namePanel);
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		
		JLabel lblAreaName = new JLabel("Area Name:");
		namePanel.add(lblAreaName);
		
		txtAreaName = new JTextField();
		
		txtAreaName.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtAreaName.getPreferredSize().height));
		namePanel.add(txtAreaName);
		txtAreaName.setText("Area name");
		txtAreaName.setColumns(10);
		
		btnChangeArea = new JButton("Change");
		namePanel.add(btnChangeArea);
		btnChangeArea.setVisible(false);
		
		JPanel addDoorPanel = new JPanel();
		panel.add(addDoorPanel);
		addDoorPanel.setLayout(new BoxLayout(addDoorPanel, BoxLayout.X_AXIS));
		
		JButton btnRemoveDoor = new JButton("Remove Door From:");
		JButton btnAddDoor = new JButton("Add Door to: ");
		
		btnAddDoor.setPreferredSize(btnRemoveDoor.getPreferredSize());
		addDoorPanel.add(btnAddDoor);
		
		addDoorSelect = new JComboBox();
		addDoorSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, addDoorSelect.getPreferredSize().height));
		addDoorPanel.add(addDoorSelect);
		
		JPanel removeDoorPanel = new JPanel();
		panel.add(removeDoorPanel);
		removeDoorPanel.setLayout(new BoxLayout(removeDoorPanel, BoxLayout.X_AXIS));
		
		
		removeDoorPanel.add(btnRemoveDoor);
		
		removeDoorSelect = new JComboBox();
		removeDoorSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, removeDoorSelect.getPreferredSize().height));
		removeDoorPanel.add(removeDoorSelect);
		
		
		final JButton btnStartArea = new JButton("Make Start Area"); 
		btnStartArea.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(btnStartArea);
		
		// When we press enter in the text area, attempt to change the name of the selected area
		txtAreaName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				maybeChangeAreaName(txtAreaName.getText(), areaList.getSelectedIndex());
			}
		});
		// When we leave the text area, attempt to change the name of the selected area
		txtAreaName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				maybeChangeAreaName(txtAreaName.getText(), areaList.getSelectedIndex());
			}
		});
		
		// Whenever the text of the area name is changed, make the areaNameChange button visible
		txtAreaName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				btnChangeArea.setVisible(true);
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				btnChangeArea.setVisible(true);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				btnChangeArea.setVisible(true);
			}
		});
		
		// When the area name change button is pressed, attempt to change the name of the selected area
		btnChangeArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				maybeChangeAreaName(txtAreaName.getText(), areaList.getSelectedIndex());
			}
		});
		
		areaList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(btnChangeArea.isVisible())
					maybeChangeAreaName(txtAreaName.getText(), selectedAreaIndex);
				
				selectedAreaIndex = areaList.getSelectedIndex();
				AreaBlock currentArea = (AreaBlock)areaList.getSelectedValue();
				if(currentArea != null) {
					txtAreaName.setText(currentArea.getName());
					panel.setVisible(true);
				
					//Change the add door combobox to correspond to our current set of areas (with a few exceptions)
					addDoorSelect.setModel(new DefaultComboBoxModel(possibleDoors(currentArea)));
				
					//Change the remove door combobox to correspond to our current set of doors 
					removeDoorSelect.setModel(new DefaultComboBoxModel(currentArea.getDoorList()));
		
					// set the change area button invisible until we actually change the text
					btnChangeArea.setVisible(false);
					
					// See if this is the start area (and whether we need the start area button 
					if (currentArea.isStartArea()) { 
						btnStartArea.setVisible(false); 
					}
					else { 
						btnStartArea.setVisible(true);
					}
					
				}
				else
					panel.setVisible(false);
			}
		});
		
		btnNewArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(btnChangeArea.isVisible())
					maybeChangeAreaName(txtAreaName.getText(), selectedAreaIndex);
				String newAreaName = "Area-" + ++nextAreaIndex;
				AreaBlock areaToAdd = new AreaBlock(newAreaName);
				if (_startArea == null) { 
					_startArea = areaToAdd;
					areaToAdd.setStartArea(true);
				}
				//add this area to the graph layout
				mxGraph areaGraph = WindowMain.getAreaGraph();
				Object parent = areaGraph.getDefaultParent();
				
				areaGraph.getModel().beginUpdate();
				try
				{
					
					String style = (nextAreaIndex % 2 == 0) ? "fillColor=#8FFEDD" : "fillColor=#BAD0EF";
					Object cell = areaGraph.insertVertex(parent, null, areaToAdd, 0, 0, 0, 0, style);
					
					
					areaToAdd.setGraphCell(cell);
					areaGraph.updateCellSize(cell);
				}
				finally
				{
					
					areaGraph.getModel().endUpdate();
					// update with an animation
					WindowMain.updateAreaGraph();
				}
				
				
				areaListModel.addElement(areaToAdd);
				areaList.setSelectedIndex(areaListModel.getSize()-1);
				txtAreaName.setText(newAreaName);
				panel.setVisible(true);
				txtAreaName.selectAll();
				txtAreaName.requestFocusInWindow();
				// set the change area button invisible until we actually change the text
				btnChangeArea.setVisible(false);
				
				// Update the block panel
				PuzzleEditPanel.updateCurrentBlock();
				
				buildAreaPuzzleGraph(); 
			}
		});
		
		// Add the currently selected area (from the combobox) to the current area (from the list) as a door
		btnAddDoor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AreaBlock currentArea = (AreaBlock)areaList.getSelectedValue();
				AreaBlock doorToAdd = (AreaBlock)addDoorSelect.getSelectedItem();
				if(doorToAdd == null)
					return;
				currentArea.addDoor(doorToAdd);
				doorToAdd.addDoor(currentArea);
				
				//add an edge to the graph layout
				mxGraph areaGraph = WindowMain.getAreaGraph();
				Object parent = areaGraph.getDefaultParent();
				
				areaGraph.getModel().beginUpdate();
				try
				{
					areaGraph.insertEdge(parent, null, null, currentArea.getGraphCell(), doorToAdd.getGraphCell());
					areaGraph.insertEdge(parent, null, null, doorToAdd.getGraphCell(), currentArea.getGraphCell());
				}
				finally
				{
					
					areaGraph.getModel().endUpdate();
					// update with an animation
					WindowMain.updateAreaGraph();
				}
				//Change the remove door combobox to correspond to our current set of doors 
				removeDoorSelect.setModel(new DefaultComboBoxModel(currentArea.getDoorList()));
				//Change the add door combobox to correspond to our current set of areas (with a few exceptions)
				addDoorSelect.setModel(new DefaultComboBoxModel(possibleDoors(currentArea)));
				
				buildAreaPuzzleGraph(); 
			}
		});
		
		// Remove the currently selected area (from the combobox) as a door from the current area (from the list)
		btnRemoveDoor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AreaBlock currentArea = (AreaBlock)areaList.getSelectedValue();
				AreaBlock doorToRemove = (AreaBlock)removeDoorSelect.getSelectedItem();
				if(doorToRemove == null)
					return;
				currentArea.removeDoor(doorToRemove);
				doorToRemove.removeDoor(currentArea);
				
				mxGraph areaGraph = WindowMain.getAreaGraph();
				Object parent = areaGraph.getDefaultParent();
				
				areaGraph.getModel().beginUpdate();
				try
				{
					Object[] cells = areaGraph.getEdgesBetween(currentArea.getGraphCell(), doorToRemove.getGraphCell(), false);	
					areaGraph.removeCells(cells);
				}
				finally
				{
					
					areaGraph.getModel().endUpdate();
					// update with an animation
					WindowMain.updateAreaGraph();
					
				}
				
				
				//Change the remove door combobox to correspond to our current set of doors 
				removeDoorSelect.setModel(new DefaultComboBoxModel(currentArea.getDoorList()));
				//Change the add door combobox to correspond to our current set of areas (with a few exceptions)
				addDoorSelect.setModel(new DefaultComboBoxModel(possibleDoors(currentArea)));
				
				buildAreaPuzzleGraph(); 
			}
		});
		
		// The delete function. Slightly more complicated since we have to remove any doors the area might have
		btnDeleteArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AreaBlock areaToDelete = (AreaBlock)areaList.getSelectedValue();
				// Only delete the area if the user confirms
				if (JOptionPane.showConfirmDialog((Component)arg0.getSource(), "Really delete area: " + areaToDelete.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				
					int newIndex = Math.max(0, areaList.getSelectedIndex()-1);
					areaListModel.remove(areaList.getSelectedIndex());
					// Now go through the areas and remove this any doors involving this area
					for(Object o : areaListModel.toArray()) {
						AreaBlock a = (AreaBlock)o;
						a.removeDoor(areaToDelete);
					}
					// Remove the puzzle edges related to this area
					if (_startArea == areaToDelete) { 
						_startArea = null; 
						areaToDelete.setStartArea(false); 
					}
					areaToDelete.removePuzzleEdges(); 
					areaToDelete.maybeDeletePuzzleCell(); 
					
					
					// Delete this area from the graph
					mxGraph areaGraph = WindowMain.getAreaGraph();
					Object parent = areaGraph.getDefaultParent();
					
					areaGraph.getModel().beginUpdate();
					try
					{
						Object[] cells = new Object[]{areaToDelete.getGraphCell()};
						areaGraph.removeCells(cells, true);
					}
					finally
					{
						
						areaGraph.getModel().endUpdate();
						// update with an animation
						WindowMain.updateAreaGraph();
						
					}
					
					// And go through the list of puzzle blocks and remove all references of this area
					for(PuzzleBlock p : PuzzleEditPanel.getBlockList())
					{
						p.maybeRemoveRef(areaToDelete);
					}
				
					
					
					
					// If we can, want to select a new area in the list
					if(areaListModel.size() >= 1)
						areaList.setSelectedIndex(newIndex);
					
					buildAreaPuzzleGraph(); 
				}
			}
		});
		
		btnStartArea.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				if (_startArea != null) { 
					_startArea.setStartArea(false); 
				}
				AreaBlock currentArea = (AreaBlock)areaList.getSelectedValue(); 
				currentArea.setStartArea(true);
				mxGraph areaGraph = WindowMain.getAreaGraph(); 
				areaGraph.getModel().beginUpdate(); 
				try { 
					areaGraph.updateCellSize(currentArea.getGraphCell()); 
					if (_startArea != null)
						areaGraph.updateCellSize(_startArea.getGraphCell());
				} 
				finally { 
					areaGraph.getModel().endUpdate(); 
					WindowMain.updateAreaGraph();
				}
				
				
				_startArea = currentArea;
				areaList.repaint();
				btnStartArea.setVisible(false);
				
				buildAreaPuzzleGraph(); 
			}
		});
	}
	
	private void maybeChangeAreaName(String newAreaName, int selectedIndex) 
	{
		// TODO: Implement this function
		AreaBlock currentArea = (AreaBlock)areaListModel.get(selectedIndex);
		if(newAreaName.equals(currentArea.getName()))
			return;
		currentArea.setName(newAreaName);
		areaListModel.set(selectedIndex, currentArea);
		btnChangeArea.setVisible(false);
		
		mxGraph areaGraph = WindowMain.getAreaGraph();
		areaGraph.getModel().beginUpdate();
		try
		{
			areaGraph.updateCellSize(currentArea.getGraphCell());
		}
		finally
		{
			areaGraph.getModel().endUpdate();
		}
	}
	
	//Returns a list of the doors that can be placed in the addDoorSelect combobox when a new area is selected.
	//This list should include all the current areas without the current area and without any of the current area's doors
	private Object[] possibleDoors(AreaBlock currentBlock) 
	{
		if(areaListModel.isEmpty())
			return new Object[0];
		//Start with a list and convert it to an array
		List<AreaBlock> doors = new ArrayList<AreaBlock>();
		for(Object o : areaListModel.toArray()) {
			AreaBlock a = (AreaBlock)o;
			if (!a.equals(currentBlock) && !currentBlock.hasDoor(a))
				doors.add(a);
		}
		return doors.toArray();		
	}
	
	
	// A Function to reset the visuals of the area edit panel on a load 
	public void justLoaded() 
	{
		if (areaList.getModel().getSize() > 0)
			areaList.setSelectedIndex(0);
		WindowMain.updateAreaGraph();
	}
	
	// This function provides a way for other components to access the current list of areas
	public static AreaBlock[] getAreaList() 
	{
		AreaBlock[] retArray = new AreaBlock[areaListModel.size()];
		areaListModel.copyInto(retArray);
		return retArray;
	}
	
	public void clear() 
	{
		areaListModel.clear();
		areaList.setSelectedValue(null, true);
	}
	
	// A function to add areas to the list 
	public static void addArea(AreaBlock areaToAdd) 
	{
		mxGraph areaGraph = WindowMain.getAreaGraph();
		Object parent = areaGraph.getDefaultParent();
		if (areaToAdd.isStartArea())
			_startArea = areaToAdd;
		
		areaGraph.getModel().beginUpdate();
		try
		{
			
			String style = (nextAreaIndex % 2 == 0) ? "fillColor=#8FFEDD" : "fillColor=#BAD0EF";
			Object cell = areaGraph.insertVertex(parent, null, areaToAdd, 0, 0, 0, 0, style);
			areaToAdd.setGraphCell(cell);
			areaGraph.updateCellSize(cell);

		}
		finally
		{
			areaGraph.getModel().endUpdate();
		}
		areaListModel.addElement(areaToAdd);
		nextAreaIndex++;
		
	}
	
	public static void addDoors(AreaBlock area) { 
		mxGraph areaGraph = WindowMain.getAreaGraph();
		Object parent = areaGraph.getDefaultParent();
		
		areaGraph.getModel().beginUpdate();
		try
		{	
			for (AreaBlock door : area.getDoorList()) {
				areaGraph.insertEdge(parent, null, null, area.getGraphCell(), door.getGraphCell());
			}
			
		}
		finally
		{
			areaGraph.getModel().endUpdate();
		}
	}
	
	
	// For creating the acyclic puzzle graph representation of areas 
	public static void buildAreaPuzzleGraph() { 
		
		
		AreaBlock[] areaList = getAreaList(); 
		
		// First, remove any edge connections so we can start anew
		for (AreaBlock area : areaList) { 
			area.removePuzzleEdges();
		}
		
		
		Set<AreaBlock> closedBlocks = new HashSet<AreaBlock>(); 
		Queue<AreaBlock> agenda = new LinkedList<AreaBlock>();
		
		if (_startArea != null) { 
		
			agenda.add(_startArea); 
			while (agenda.size() > 0) { 
				AreaBlock block = agenda.remove(); 
				if (closedBlocks.contains(block))
					continue; 
				closedBlocks.add(block);
				// Create an edge to all of this blocks neighbors if they're not in the closed set
				for (AreaBlock neighbor : block.getDoorList()) { 
					if (!closedBlocks.contains(neighbor) && !agenda.contains(neighbor)) { 
						block.addPuzzleEdge(neighbor);
						agenda.add(neighbor);
					}
				}
				// add any destination locked neighbors to the agenda
				for (DoorUnlockBlock lockSource : block.getSourceLockList()) { 
					AreaBlock destBlock = lockSource.getDestBlock();  
					if (destBlock != null && !closedBlocks.contains(destBlock) && !agenda.contains(destBlock)) { 
						agenda.add(destBlock);
					}
				}
			}
		}
		// Now treat each lock source that didn't make it as a potential new start area
		for (AreaBlock area : areaList) { 
			if (!closedBlocks.contains(area) && area.getSourceLockList().length > 0) { 
				agenda.add(area); 
				while (agenda.size() > 0) { 
					AreaBlock block = agenda.remove(); 
					if (closedBlocks.contains(block))
						continue; 
					closedBlocks.add(block);
					// Create an edge to all of this blocks neighbors if they're not in the closed set
					for (AreaBlock neighbor : block.getDoorList()) { 
						if (!closedBlocks.contains(neighbor) && !agenda.contains(neighbor)) { 
							block.addPuzzleEdge(neighbor);
							agenda.add(neighbor);
						}
					}
					// add any destination locked neighbors to the agenda
					for (DoorUnlockBlock lockSource : block.getSourceLockList()) { 
						AreaBlock destBlock = lockSource.getDestBlock();  
						if (destBlock != null && !closedBlocks.contains(destBlock) && !agenda.contains(destBlock)) { 
							agenda.add(destBlock);
						}
					}
				}
			}
		}
		
		// Now find all locked doors and check if they created a cycle in the graph. 
		for (PuzzleBlock block : PuzzleEditPanel.getBlockList()) { 
			if (block instanceof DoorUnlockBlock) { 
				DoorUnlockBlock lock = (DoorUnlockBlock)block; 
				AreaBlock source = lock.getSourceBlock(); 
				AreaBlock dest = lock.getDestBlock(); 
				if (source != null && dest != null && dest.nodeReachable(source)) { 
					lock.disconnectSource();
				}
			}
		}
		
		// Garbage collection on puzzle graph cells that are no longer needed
		for (AreaBlock area : areaList) { 
			area.maybeDeletePuzzleCell();
		}
		
		// At the very end, update the puzzle graph finally
		WindowMain.updatePuzzleGraph();
		PuzzleEditPanel.resetTextualDescription();
	}
	

}
