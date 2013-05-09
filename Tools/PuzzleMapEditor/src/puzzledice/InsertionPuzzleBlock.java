package puzzledice;

import gui.PuzzleEditPanel;
import gui.WindowMain;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mxgraph.view.mxGraph;

public class InsertionPuzzleBlock extends PuzzleBlock {
	
	private PuzzleBlock _boxBlock, _boxeeBlock;
	private static int nextIndex = 0;
	public static void reset() {
		nextIndex = 0;
	}
	
	private String _boxName, _boxeeName;
	public void setBoxName(String value) {
		_boxName = value;
	}
	public void setBoxeeName(String value) {
		_boxeeName = value;
	}
	
	private JComboBox _boxSelect, _boxeeSelect;
	
	public InsertionPuzzleBlock()
	{
		_name = "Insertion-Puzzle-" + ++nextIndex;
		_type = "Insertion Puzzle";
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		// Box Panel
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		
		JLabel boxLabel = new JLabel("Box:");
		boxPanel.add(boxLabel);
		
		_boxSelect = new JComboBox();
		_boxSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _boxSelect.getPreferredSize().height));
		boxPanel.add(_boxSelect);
		
		editPanel.add(boxPanel);
		
		// Boxee panel
		JPanel boxeePanel = new JPanel();
		boxeePanel.setLayout(new BoxLayout(boxeePanel, BoxLayout.X_AXIS));
		
		JLabel boxeeLabel = new JLabel("Item to Insert:");
		boxeePanel.add(boxeeLabel);
		
		_boxeeSelect = new JComboBox();
		_boxeeSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _boxeeSelect.getPreferredSize().height));
		boxeePanel.add(_boxeeSelect);
		
		editPanel.add(boxeePanel);
		
		_editUI = editPanel;
		
		_boxSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_boxBlock == _boxSelect.getSelectedItem() || _boxBlock == null && _boxSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_boxSelect.getSelectedItem() != null && !_boxSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_boxSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(InsertionPuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_boxBlock != null)
							_boxSelect.setSelectedItem(_boxBlock); 
						else 
							_boxSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_boxBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_boxBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_boxSelect.getSelectedItem() == null)
					_boxSelect.setSelectedIndex(0);
				
				if(_boxSelect.getSelectedItem().equals("None"))
					_boxBlock = null;
				else
				{
					_boxBlock = (PuzzleBlock)_boxSelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _boxBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				// Update the other list
				_boxeeSelect.setModel(new DefaultComboBoxModel(makeBoxeeList()));
				if (_boxeeBlock == null)
					_boxeeSelect.setSelectedIndex(0);
				else
					_boxeeSelect.setSelectedItem(_boxeeBlock);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_boxeeSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_boxeeBlock == _boxeeSelect.getSelectedItem() || _boxeeBlock == null && _boxeeSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_boxeeSelect.getSelectedItem() != null && !_boxeeSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_boxeeSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(InsertionPuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_boxeeBlock != null)
							_boxeeSelect.setSelectedItem(_boxeeBlock); 
						else 
							_boxeeSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_boxeeBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_boxeeBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_boxeeSelect.getSelectedItem() == null)
					_boxeeSelect.setSelectedIndex(0);
				
				if(_boxeeSelect.getSelectedItem().equals("None"))
					_boxeeBlock = null;
				else
				{
					_boxeeBlock = (PuzzleBlock)_boxeeSelect.getSelectedItem();
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _boxeeBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				// Update the other list
				_boxSelect.setModel(new DefaultComboBoxModel(makeBoxList()));
				if(_boxBlock == null)
					_boxSelect.setSelectedIndex(0);
				else
					_boxSelect.setSelectedItem(_boxBlock);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
	}
	
	@Override
	public void update()
	{
		// Update the UI ComboBoxes and the selected values
		_boxSelect.setModel(new DefaultComboBoxModel(makeBoxList()));
		if(_boxBlock == null)
			_boxSelect.setSelectedIndex(0);
		else
			_boxSelect.setSelectedItem(_boxBlock);
		
		_boxeeSelect.setModel(new DefaultComboBoxModel(makeBoxeeList()));
		if(_boxeeBlock == null)
			_boxeeSelect.setSelectedIndex(0);
		else
			_boxeeSelect.setSelectedItem(_boxeeBlock);
	}

	
	private Object[] makeBoxList()
	{
		List<Object> retVal  = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_boxeeSelect.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	private Object[] makeBoxeeList()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_boxSelect.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	@Override
	public void maybeRemoveRef(PuzzleBlock block)
	{
		if(block.equals(_boxBlock)) {
			_boxBlock = null;
			_boxSelect.setSelectedIndex(0);
		}
		if(block.equals(_boxeeBlock)) {
			_boxeeBlock = null;
			_boxeeSelect.setSelectedIndex(0);
		}
	}
	
	@Override
	public String getTextualDescription()
	{
		String retVal = "";
		if(_boxeeBlock != null)
			retVal += _boxeeBlock.getTextualDescription();
		if(_boxBlock != null)
			retVal += _boxBlock.getTextualDescription();

		String box = (_boxBlock == null) ? "SOMETHING" : _boxBlock.getOutputTempName();
		String boxee = (_boxeeBlock == null) ? "SOMETHING" : _boxeeBlock.getOutputTempName();
		
		retVal += "The player puts " + boxee + " inside of " + box + ". ";
		_outputTempName = box + "(containing " + boxee + ")";
		
		return retVal;
	}
	
	@Override 
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles) 
	{
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		if (_boxName != null) {
			_boxBlock = puzzles.get(_boxName);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _boxBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		if (_boxeeName != null) {
			_boxeeBlock = puzzles.get(_boxeeName);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _boxeeBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		
		this.update();
	}
	
	@Override 
	public PuzzleBlock[] getPuzzleInputs()
	{ 
		if (_boxBlock != null && _boxeeBlock != null)
			return new PuzzleBlock[] { _boxBlock, _boxeeBlock };
		else if (_boxBlock != null)
			return new PuzzleBlock[] { _boxBlock }; 
		else if (_boxeeBlock != null)
			return new PuzzleBlock[] { _boxeeBlock };
		return new PuzzleBlock[0];
	}
	
	@Override 
	public String toXML() 
	{
		String xml = "<InsertionPuzzle name=\"" + _name + "\" ";
		if (_boxeeBlock != null) 
			xml += "boxee=\"" + _boxeeBlock.getName() + "\" ";
		if (_boxBlock != null) 
			xml += "box=\"" + _boxBlock.getName() + "\" ";
		
		xml += "/>";
		
		return xml;
	}
}  
