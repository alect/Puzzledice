package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import puzzledice.CombinePuzzleBlock;
import puzzledice.DoorUnlockBlock;
import puzzledice.FilterBlock;
import puzzledice.InsertionPuzzleBlock;
import puzzledice.ItemRequestPuzzleBlock;
import puzzledice.ORBlock;
import puzzledice.PropertyChangePuzzleBlock;
import puzzledice.PuzzleBlock;
import puzzledice.SpawnPuzzleBlock;
import puzzledice.OutputBlock;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PuzzleEditPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String[] puzzleBlockTypes = {"Output", "Spawn Puzzle", "Combine Puzzle", "Property Change Puzzle", "Item Request Puzzle", "Insertion Puzzle", "Filter", "OR Block", "Door Unlock Puzzle"};
	private final JTextField txtBlockName;
	private final JComboBox puzzleTypeSelect;
	private static JList blockList;
	private final static DefaultListModel blockListModel = new DefaultListModel();
	private final JButton btnChangeBlock;
	
	private int selectedBlockIndex;
	private PuzzleBlock selectedBlock;
	
	// Used for generating the textual descriptions
	private static int nextItemNumber = 0;
	private static int nextNPCNumber = 0;
	
	/**
	 * Create the panel.
	 */
	public PuzzleEditPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		JButton btnNewPuzzle = new JButton("Add");
		panel_2.add(btnNewPuzzle);
		btnNewPuzzle.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		puzzleTypeSelect = new JComboBox();
		puzzleTypeSelect.setModel(new DefaultComboBoxModel(puzzleBlockTypes));
		puzzleTypeSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnNewPuzzle.getPreferredSize().height));
		panel_2.add(puzzleTypeSelect);
		
		
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane);
		blockList = new JList(blockListModel);
		
		scrollPane.setViewportView(blockList);
		
		
		
		final JPanel editingPanel = new JPanel();
		//editing panel should be invisible until a puzzle is selected
		editingPanel.setVisible(false);
		add(editingPanel);
		editingPanel.setLayout(new BoxLayout(editingPanel, BoxLayout.Y_AXIS));
		
		JButton btnDeletePuzzle = new JButton("Delete Puzzle Block");
		
		btnDeletePuzzle.setAlignmentX(Component.CENTER_ALIGNMENT);
		editingPanel.add(btnDeletePuzzle);
		
		final JLabel lblPuzzleType = new JLabel("Type:");
		lblPuzzleType.setAlignmentX(Component.CENTER_ALIGNMENT);
		editingPanel.add(lblPuzzleType);
		
		JPanel namePanel = new JPanel();
		editingPanel.add(namePanel);
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		
		JLabel lblNameLabel = new JLabel("Block Name:");
		namePanel.add(lblNameLabel);
		
		txtBlockName = new JTextField();
		
		
		
		txtBlockName.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtBlockName.getPreferredSize().height));
		txtBlockName.setText("Block Name");
		namePanel.add(txtBlockName);
		txtBlockName.setColumns(10);
		
		btnChangeBlock = new JButton("Change");
		
		namePanel.add(btnChangeBlock);
		btnChangeBlock.setVisible(false);
		
		txtBlockName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				maybeChangeBlockName(txtBlockName.getText(), blockList.getSelectedIndex());
			}
		});
		txtBlockName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				maybeChangeBlockName(txtBlockName.getText(), blockList.getSelectedIndex());
			}
		});
		
		txtBlockName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				btnChangeBlock.setVisible(true);
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				btnChangeBlock.setVisible(true);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				btnChangeBlock.setVisible(true);
			}
		});
		
		btnChangeBlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				maybeChangeBlockName(txtBlockName.getText(), blockList.getSelectedIndex());
			}
		});
		
		// Add a new puzzle block to the list when 
		btnNewPuzzle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PuzzleBlock blockToAdd;
				String puzzleType = (String)puzzleTypeSelect.getSelectedItem();

				if (puzzleType.equals("Output"))
					blockToAdd = new OutputBlock();
				else if (puzzleType.equals("Spawn Puzzle"))
					blockToAdd = new SpawnPuzzleBlock();
				else if(puzzleType.equals("Combine Puzzle"))
					blockToAdd = new CombinePuzzleBlock();
				else if(puzzleType.equals("Property Change Puzzle"))
					blockToAdd = new PropertyChangePuzzleBlock();
				else if(puzzleType.equals("Item Request Puzzle"))
					blockToAdd = new ItemRequestPuzzleBlock();
				else if(puzzleType.equals("Insertion Puzzle"))
					blockToAdd = new InsertionPuzzleBlock();
				else if(puzzleType.equals("Filter"))
					blockToAdd = new FilterBlock();
				else if(puzzleType.equals("OR Block"))
					blockToAdd = new ORBlock(); 
				else if(puzzleType.equals("Door Unlock Puzzle"))
					blockToAdd = new DoorUnlockBlock();
				// If we don't have a constructor for the type yet, silently do nothing.
				else
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				Object parent = puzzleGraph.getDefaultParent();
				puzzleGraph.getModel().beginUpdate();
				try
				{
					Object cell = puzzleGraph.insertVertex(parent, null, blockToAdd, 0, 0, 0, 0, blockToAdd.getCellStyle());
					mxCell edge = (mxCell)puzzleGraph.insertEdge(parent, null, null, WindowMain.getHierarchyRoot(), cell);
					edge.setVisible(false);
					blockToAdd.setGraphCell(cell);
					puzzleGraph.updateCellSize(cell);
				}
				finally
				{
					puzzleGraph.getModel().endUpdate();
					WindowMain.updatePuzzleGraph();
				}
				
				blockListModel.addElement(blockToAdd);
				blockList.setSelectedIndex(blockListModel.getSize()-1);
				txtBlockName.setText(blockToAdd.getName());
				lblPuzzleType.setText("Type: " + puzzleType);
				// Make sure the editing panel is visible before we try to grant one of its members focus
				editingPanel.setVisible(true);
				txtBlockName.selectAll();
				txtBlockName.requestFocusInWindow();
				// Set the change block name button invisible until we actually change the text.
				btnChangeBlock.setVisible(false);
				
			}
		});
		
		blockList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(btnChangeBlock.isVisible()) 
					maybeChangeBlockName(txtBlockName.getText(), selectedBlockIndex);
				// remove the custom editing UI if we need to
				if(selectedBlock != null && selectedBlock.getUI() != null) {
					editingPanel.remove(selectedBlock.getUI());
				}
				
				selectedBlockIndex = blockList.getSelectedIndex();
				
				
				
				selectedBlock = (PuzzleBlock)blockList.getSelectedValue();
				if(selectedBlock != null) {
					// update the selected block with any new information
					selectedBlock.update();
					
					// add the custom editing UI if we can
					if(selectedBlock.getUI() != null) {
						editingPanel.add(selectedBlock.getUI());
						editingPanel.validate();
						editingPanel.repaint();
					}
					
					txtBlockName.setText(selectedBlock.getName());
					lblPuzzleType.setText("Type: " + selectedBlock.getPuzzleType());
					editingPanel.setVisible(true);
						
					// Set the change block button invisible until we actually change the text
					btnChangeBlock.setVisible(false);
					
					// for now, here's where we update the textual description
					resetTextualDescription();
				}
				else
					editingPanel.setVisible(false);
			}
		});
		
		btnDeletePuzzle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PuzzleBlock blockToDelete = (PuzzleBlock)blockList.getSelectedValue();
				// Only delete the block if the user confirms
				if (JOptionPane.showConfirmDialog((Component)arg0.getSource(), "Really delete Puzzle Block: " + blockToDelete.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					int newIndex = Math.max(0, blockList.getSelectedIndex()-1);
					blockListModel.remove(blockList.getSelectedIndex());
					// Now go through the blocks and remove the reference to this block
					for(Object o : blockListModel.toArray()) {
						PuzzleBlock p = (PuzzleBlock)o;
						p.maybeRemoveRef(blockToDelete);
					}
					// Let the block to its own cleaning if necessary
					blockToDelete.onDelete();
					
					// Delete this block from the graph
					mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
					
					puzzleGraph.getModel().beginUpdate();
					try
					{
						Object[] cells = new Object[]{blockToDelete.getGraphCell()};
						puzzleGraph.removeCells(cells, true);
					}
					finally
					{
						puzzleGraph.getModel().endUpdate();
						WindowMain.updatePuzzleGraph();
					}
					
					
					// If we can, select a new block from the list
					if(blockListModel.size() >= 1) 
						blockList.setSelectedIndex(newIndex);
				}
			}
		});
	}
	
	private void maybeChangeBlockName(String newBlockName, int selectedIndex)
	{
		PuzzleBlock currentBlock = (PuzzleBlock)blockListModel.get(selectedIndex);
		if(newBlockName.equals(currentBlock.getName()))
			return;
		currentBlock.setName(newBlockName);
		blockListModel.set(selectedIndex, currentBlock);
		btnChangeBlock.setVisible(false);
		
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		puzzleGraph.getModel().beginUpdate();
		try
		{
			puzzleGraph.updateCellSize(currentBlock.getGraphCell());
		}
		finally
		{
			puzzleGraph.getModel().endUpdate();
			WindowMain.updatePuzzleGraph();
		}
		
	}
	
	// This function provides a way for other components to access the current list of puzzle blocks
	public static PuzzleBlock[] getBlockList() 
	{
		PuzzleBlock[] retArray = new PuzzleBlock[blockListModel.size()];
		blockListModel.copyInto(retArray);
		return retArray;
	}
	
	// Function to update the currently selected block (used by the AreaEditPanel)
	public static void updateCurrentBlock()
	{
		PuzzleBlock currentBlock = (PuzzleBlock)blockList.getSelectedValue();
		if(currentBlock != null)
			currentBlock.update();
	}
	
	// Used when generating the textual descriptions to create random item names
	public static String nextItemName()
	{
		return "ITEM-" + ++nextItemNumber;
	}
	
	public static String nextNPCName()
	{
		return "NPC-" + ++nextNPCNumber;
	}
	
	public static void resetTextualDescription()
	{
		nextItemNumber = 0;
		nextNPCNumber = 0;
		PuzzleBlock currentBlock = (PuzzleBlock)blockList.getSelectedValue();
		String newDescription = "Full Puzzle Description: ";
		if(currentBlock != null)
			newDescription += currentBlock.getTextualDescription();
		WindowMain.updateTextDescription(newDescription);
	}
	
	public void justLoaded() 
	{
		if (blockList.getModel().getSize() > 0) 
			blockList.setSelectedIndex(0);
		WindowMain.updatePuzzleGraph();
	}
	
	public void clear() 
	{
		blockListModel.clear();
		blockList.setSelectedValue(null, false);
	}
	
	// A function to add puzzles to the list 
	public static void addPuzzle(PuzzleBlock blockToAdd) 
	{
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		Object parent = puzzleGraph.getDefaultParent();
		puzzleGraph.getModel().beginUpdate();
		try
		{
			Object cell = puzzleGraph.insertVertex(parent, null, blockToAdd, 0, 0, 0, 0, blockToAdd.getCellStyle());
			mxCell edge = (mxCell)puzzleGraph.insertEdge(parent, null, null, WindowMain.getHierarchyRoot(), cell);
			edge.setVisible(false);
			blockToAdd.setGraphCell(cell);
			puzzleGraph.updateCellSize(cell);
		}
		finally
		{
			puzzleGraph.getModel().endUpdate();
		}
		
		blockListModel.addElement(blockToAdd);
	}
	

}
