package puzzledice;

import gui.AreaEditPanel;
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

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class DoorUnlockBlock extends PuzzleBlock {

	private AreaBlock _sourceArea, _destArea;
	private PuzzleBlock _keyBlock;
	private static int nextIndex = 0;
	public static void reset() {
		nextIndex = 0;
	}
	
	private String _sourceAreaName, _destAreaName, _keyName;
	public void setSourceAreaName(String value) {
		_sourceAreaName = value;
	}
	public void setDestAreaName(String value) {
		_destAreaName = value;
	}
	public void setKeyName(String value) {
		_keyName = value;
	}
	
	private JComboBox _sourceAreaSelect, _destAreaSelect, _keySelect;
	
	// So we can actually reference ourself inside actionlisteners
	private DoorUnlockBlock _selfReference;
	
	public DoorUnlockBlock()
	{
		_selfReference = this;
		_name = "Door-Unlock-Puzzle-" + ++nextIndex;
		_type = "Door Unlock Puzzle";
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		// Key Select Panel
		JPanel keyPanel = new JPanel();
		keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.X_AXIS));
		
		JLabel keyLabel = new JLabel("Key Spawn:");
		keyPanel.add(keyLabel);
		
		_keySelect = new JComboBox();
		_keySelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _keySelect.getPreferredSize().height));
		keyPanel.add(_keySelect);
		
		editPanel.add(keyPanel);
		
		// Source Area Select Panel
		JPanel sourceAreaPanel = new JPanel();
		sourceAreaPanel.setLayout(new BoxLayout(sourceAreaPanel, BoxLayout.X_AXIS));
		
		JLabel sourceAreaLabel = new JLabel("Door Spawn Area:");
		sourceAreaPanel.add(sourceAreaLabel);
		
		_sourceAreaSelect = new JComboBox();
		_sourceAreaSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _sourceAreaSelect.getPreferredSize().height));
		sourceAreaPanel.add(_sourceAreaSelect);
		
		editPanel.add(sourceAreaPanel);
		
		// Destination Area Select Panel
		JPanel destAreaPanel = new JPanel();
		destAreaPanel.setLayout(new BoxLayout(destAreaPanel, BoxLayout.X_AXIS));
		
		JLabel destAreaLabel = new JLabel("Destination Area:");
		destAreaPanel.add(destAreaLabel);
		
		_destAreaSelect = new JComboBox();
		_destAreaSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _destAreaSelect.getPreferredSize().height));
		destAreaPanel.add(_destAreaSelect);
		
		editPanel.add(destAreaPanel);
		
		_editUI = editPanel;
		
		
		_keySelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_keyBlock == _keySelect.getSelectedItem() || _keyBlock == null && _keySelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_keySelect.getSelectedItem() != null && !_keySelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_keySelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(DoorUnlockBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_keyBlock != null)
							_keySelect.setSelectedItem(_keyBlock); 
						else 
							_keySelect.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_keyBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_keyBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_keySelect.getSelectedItem() == null)
					_keySelect.setSelectedIndex(0);
				
				if(_keySelect.getSelectedItem().equals("None"))
					_keyBlock = null;
				else
				{
					_keyBlock = (PuzzleBlock)_keySelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _keyBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_sourceAreaSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_sourceArea == _sourceAreaSelect.getSelectedItem() || _sourceArea == null && _sourceAreaSelect.getSelectedItem().equals("None"))
					return;
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle 
				if (_sourceAreaSelect.getSelectedItem() != null && !_sourceAreaSelect.getSelectedItem().equals("None")) { 
					AreaBlock block = (AreaBlock)_sourceAreaSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(DoorUnlockBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() { 
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_sourceArea != null)
							_sourceAreaSelect.setSelectedItem(_sourceArea); 
						else 
							_sourceAreaSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// First, see if we need to remove a previous edge 
				if(_sourceArea != null)
				{
					_sourceArea.removeSourceLock(_selfReference);
					puzzleGraph.getModel().beginUpdate();
					try 
					{ 
						puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_sourceArea.getPuzzleGraphCell(), _graphCell, false));
						_sourceArea.maybeDeletePuzzleCell();
					}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				if (_sourceAreaSelect.getSelectedItem() == null)
					_sourceAreaSelect.setSelectedIndex(0);
				
				if(_sourceAreaSelect.getSelectedItem().equals("None"))
					_sourceArea = null;
				else
				{
					_sourceArea = (AreaBlock)_sourceAreaSelect.getSelectedItem();
					_sourceArea.addSourceLock(_selfReference);
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try
					{
						if(_sourceArea.getPuzzleGraphCell() == null) {
							_sourceArea.setPuzzleGraphCell(puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, _sourceArea, 0, 0, 0, 0, null));
							mxCell edge = (mxCell)puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, WindowMain.getHierarchyRoot(), _sourceArea.getPuzzleGraphCell());
							edge.setVisible(false);
							puzzleGraph.updateCellSize(_sourceArea.getPuzzleGraphCell());
						}
						puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _sourceArea.getPuzzleGraphCell(), _graphCell);
					}
					finally {puzzleGraph.getModel().endUpdate();}
				}
				// Update the other list
				_destAreaSelect.setModel(new DefaultComboBoxModel(makeDestAreaList()));
				if(_destArea == null)
					_destAreaSelect.setSelectedIndex(0);
				else
					_destAreaSelect.setSelectedItem(_destArea);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_destAreaSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_destArea == _destAreaSelect.getSelectedItem() || _destArea == null && _destAreaSelect.getSelectedItem().equals("None"))
					return;
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle 
				if (_destAreaSelect.getSelectedItem() != null && !_destAreaSelect.getSelectedItem().equals("None")) { 
					AreaBlock block = (AreaBlock)_destAreaSelect.getSelectedItem(); 
					if (DoorUnlockBlock.this.canReachBlockBackwards(block)) { 
						SwingUtilities.invokeLater(new Runnable() { 
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_destArea != null)
							_destAreaSelect.setSelectedItem(_destArea); 
						else 
							_destAreaSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// First, see if we need to remove a previous edge 
				if(_destArea != null)
				{
					_destArea.removeDoorLock(_selfReference);
					puzzleGraph.getModel().beginUpdate();
					try 
					{ 
						puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_destArea.getPuzzleGraphCell(), _graphCell, false));
						_destArea.maybeDeletePuzzleCell();
					}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				if (_destAreaSelect.getSelectedItem() == null)
					_destAreaSelect.setSelectedIndex(0);
				
				
				if(_destAreaSelect.getSelectedItem().equals("None"))
					_destArea = null;
				else
				{
					_destArea = (AreaBlock)_destAreaSelect.getSelectedItem();
					_destArea.addDoorLock(_selfReference);
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try
					{
						if(_destArea.getPuzzleGraphCell() == null) {
							_destArea.setPuzzleGraphCell(puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, _destArea, 0, 0, 0, 0, null));
							mxCell edge = (mxCell)puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, WindowMain.getHierarchyRoot(), _destArea.getPuzzleGraphCell());
							edge.setVisible(false);
							puzzleGraph.updateCellSize(_destArea.getPuzzleGraphCell());
						}
						puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _graphCell,  _destArea.getPuzzleGraphCell());
					}
					finally {puzzleGraph.getModel().endUpdate();}
				}
				// Update the other list
				_sourceAreaSelect.setModel(new DefaultComboBoxModel(makeSourceAreaList()));
				if(_sourceArea == null)
					_sourceAreaSelect.setSelectedIndex(0);
				else
					_sourceAreaSelect.setSelectedItem(_sourceArea);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		_keySelect.setModel(new DefaultComboBoxModel(makeKeyList()));
		if (_keyBlock == null)
			_keySelect.setSelectedIndex(0);
		else
			_keySelect.setSelectedItem(_keyBlock);
		
		_sourceAreaSelect.setModel(new DefaultComboBoxModel(makeSourceAreaList()));
		if(_sourceArea == null)
			_sourceAreaSelect.setSelectedIndex(0);
		else
			_sourceAreaSelect.setSelectedItem(_sourceArea);
		
		_destAreaSelect.setModel(new DefaultComboBoxModel(makeDestAreaList()));
		if(_destArea == null)
			_destAreaSelect.setSelectedIndex(0);
		else
			_destAreaSelect.setSelectedItem(_destArea);

	}
	
	private Object[] makeKeyList()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	
	private Object[] makeSourceAreaList()
	{
		List<Object> retVal = new ArrayList<Object>();
		AreaBlock[] areaList = AreaEditPanel.getAreaList();
		retVal.add("None");
		for(AreaBlock a : areaList) {
			if(!a.equals(_destAreaSelect.getSelectedItem()))
				retVal.add(a);
		}
		return retVal.toArray();
	}
	
	private Object[] makeDestAreaList()
	{
		List<Object> retVal = new ArrayList<Object>();
		AreaBlock[] areaList = AreaEditPanel.getAreaList();
		retVal.add("None");
		for(AreaBlock a : areaList) {
			if(!a.equals(_sourceAreaSelect.getSelectedItem()))
				retVal.add(a);
		}
		return retVal.toArray();
	}
	
	
	
	@Override 
	public void maybeRemoveRef(PuzzleBlock block) 
	{
		if (block.equals(_keyBlock)) {
			_keyBlock = null;
			_keySelect.setSelectedIndex(0);
		}
	}
	
	@Override 
	public void onDelete()
	{
		// Remove our reference to our source and dest areas
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		if(_sourceArea != null)
		{
			_sourceArea.removeSourceLock(this);
			puzzleGraph.getModel().beginUpdate();
			try 
			{ 
				puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_sourceArea.getPuzzleGraphCell(), _graphCell, false));
				_sourceArea.maybeDeletePuzzleCell();
			}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		
		if(_destArea != null)
		{
			_destArea.removeDoorLock(this); 
			puzzleGraph.getModel().beginUpdate();
			try 
			{ 
				puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_destArea.getPuzzleGraphCell(), _graphCell, false));
				_destArea.maybeDeletePuzzleCell();
			}
			finally { puzzleGraph.getModel().endUpdate();}
		}
	}
	
	// For when the changing area topology creates a cycle with this door unlock. 
	// Need to break the source edges to fix the cycle
	public void disconnectSource() 
	{ 
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
		if (_sourceArea != null) { 
			_sourceArea.removeSourceLock(this);
			puzzleGraph.getModel().beginUpdate(); 
			try 
			{ 
				puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_sourceArea.getPuzzleGraphCell(), _graphCell, false));
			}
			finally { puzzleGraph.getModel().endUpdate(); } 
			_sourceArea = null; 
			_sourceAreaSelect.setSelectedIndex(0);
		}
		if (_keyBlock != null) { 
			puzzleGraph.getModel().beginUpdate(); 
			try 
			{ 
				puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_keyBlock.getGraphCell(), _graphCell, false));
			}
			finally { puzzleGraph.getModel().endUpdate(); } 
			_keyBlock = null; 
			_keySelect.setSelectedIndex(0);
		}
	}
	
	public AreaBlock getSourceBlock() 
	{
		return _sourceArea; 
	}
	
	public AreaBlock getDestBlock() 
	{ 
		return _destArea; 
	}
	
	@Override 
	public void maybeRemoveRef(AreaBlock area) 
	{
		if(area.equals(_sourceArea)) {
			_sourceArea = null;
			_sourceAreaSelect.setSelectedIndex(0);
		}
		if(area.equals(_destArea)) {
			_destArea = null;
			_destAreaSelect.setSelectedIndex(0);
		}
	}
	
	@Override 
	public String getTextualDescription()
	{
		String retVal = "";
		if (_sourceArea != null)
			retVal += _sourceArea.getTextualDescription(); 
		if(_keyBlock != null)
			retVal += _keyBlock.getTextualDescription();
		
		String sourceArea = (_sourceArea == null) ? "SOMEWHERE" : _sourceArea.getName();
		String destArea = (_destArea == null) ? "SOMEWHERE" : _destArea.getName();
		String key = (_keyBlock == null) ? "SOMETHING" : _keyBlock.getOutputTempName();
		
		retVal += "The player uses " + key + " to unlock the door from " + sourceArea + " to " + destArea + ". ";
		
		return retVal;
	}
	
	@Override
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles) 
	{
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		if (_sourceAreaName != null) {
			_sourceArea = areas.get(_sourceAreaName);
			_sourceArea.addSourceLock(_selfReference);
			puzzleGraph.getModel().beginUpdate();
			try
			{
				if(_sourceArea.getPuzzleGraphCell() == null) {
					_sourceArea.setPuzzleGraphCell(puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, _sourceArea, 0, 0, 0, 0, null));
					mxCell edge = (mxCell)puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, WindowMain.getHierarchyRoot(), _sourceArea.getPuzzleGraphCell());
					edge.setVisible(false);
					puzzleGraph.updateCellSize(_sourceArea.getPuzzleGraphCell());
				}
				puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _sourceArea.getPuzzleGraphCell(), _graphCell);
			}
			finally {puzzleGraph.getModel().endUpdate();}
		}
		if (_destAreaName != null) {
			_destArea = areas.get(_destAreaName);
			_destArea.addDoorLock(_selfReference);
			// update the graph with a new edge
			puzzleGraph.getModel().beginUpdate();
			try
			{
				if(_destArea.getPuzzleGraphCell() == null) {
					_destArea.setPuzzleGraphCell(puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, _destArea, 0, 0, 0, 0, null));
					mxCell edge = (mxCell)puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, WindowMain.getHierarchyRoot(), _destArea.getPuzzleGraphCell());
					edge.setVisible(false);
					puzzleGraph.updateCellSize(_destArea.getPuzzleGraphCell());
				}
				puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _graphCell,  _destArea.getPuzzleGraphCell());
			}
			finally {puzzleGraph.getModel().endUpdate();}
		}
		if (_keyName != null) {
			_keyBlock = puzzles.get(_keyName);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _keyBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		
		this.update();
	}
	
	@Override 
	public PuzzleBlock[] getPuzzleInputs()
	{ 
		if (_keyBlock != null) 
			return new PuzzleBlock[] { _keyBlock };
		return new PuzzleBlock[0];
	}
	
	@Override
	public AreaBlock[] getAreaInputs()
	{ 
		if (_sourceArea != null) 
			return new AreaBlock[] { _sourceArea };
		return new AreaBlock[0];
	}
	
	@Override
	public String toXML() 
	{
		String xml = "<DoorUnlockPuzzle name=\"" + _name + "\" ";
		
		if (_sourceArea != null)
			xml += "source=\"" + _sourceArea.getName() + "\" ";
		if (_destArea != null) 
			xml += "dest=\"" + _destArea.getName() + "\" ";
		if (_keyBlock != null) 
			xml += "key=\"" + _keyBlock.getName() + "\" ";
		
		xml += "/>";
		
		return xml;
	}

}
