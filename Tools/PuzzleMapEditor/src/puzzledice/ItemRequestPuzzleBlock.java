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

public class ItemRequestPuzzleBlock extends PuzzleBlock {

	private PuzzleBlock _requesterBlock, _requestedBlock;
	private static int nextIndex = 0;
	public static void reset() {
		nextIndex = 0;
	}
	
	private String _requesterName, _requestedName;
	public void setRequesterName(String value) {
		_requesterName = value;
	}
	public void setRequestedName(String value) {
		_requestedName = value;
	}
	
	private JComboBox _requesterSelect, _requestedSelect;
	
	public ItemRequestPuzzleBlock()
	{
		_name = "Item-Request-Puzzle-" + ++nextIndex;
		_type = "Item Request Puzzle";
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		// Requester Panel
		JPanel requesterPanel = new JPanel();
		requesterPanel.setLayout(new BoxLayout(requesterPanel, BoxLayout.X_AXIS));
		
		JLabel requesterLabel = new JLabel("Requester:");
		requesterPanel.add(requesterLabel);
		
		_requesterSelect = new JComboBox();
		_requesterSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _requesterSelect.getPreferredSize().height));
		requesterPanel.add(_requesterSelect);
		
		editPanel.add(requesterPanel);
		
		// Requested Panel
		JPanel requestedPanel = new JPanel();
		requestedPanel.setLayout(new BoxLayout(requestedPanel, BoxLayout.X_AXIS));
		
		JLabel requestedLabel = new JLabel("Requested:");
		requestedPanel.add(requestedLabel);
		
		_requestedSelect = new JComboBox();
		_requestedSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _requestedSelect.getPreferredSize().height));
		requestedPanel.add(_requestedSelect);
		
		editPanel.add(requestedPanel);
		
		_editUI = editPanel;
		
		_requesterSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_requesterBlock == _requesterSelect.getSelectedItem() || _requesterBlock == null && _requesterSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_requesterSelect.getSelectedItem() != null && !_requesterSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_requesterSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(ItemRequestPuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_requesterBlock != null)
							_requesterSelect.setSelectedItem(_requesterBlock); 
						else 
							_requesterSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_requesterBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_requesterBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_requesterSelect.getSelectedItem() == null)
					_requesterSelect.setSelectedIndex(0);
				
				if(_requesterSelect.getSelectedItem().equals("None"))
					_requesterBlock = null;
				else
				{
					_requesterBlock = (PuzzleBlock)_requesterSelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _requesterBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				// Need to update our requested list
				_requestedSelect.setModel(new DefaultComboBoxModel(makeRequestedList()));
				// And the selected values
				if(_requestedBlock == null)
					_requestedSelect.setSelectedIndex(0);
				else
					_requestedSelect.setSelectedItem(_requestedBlock);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_requestedSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_requestedBlock == _requestedSelect.getSelectedItem() || _requestedBlock == null && _requestedSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_requestedSelect.getSelectedItem() != null && !_requestedSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_requestedSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(ItemRequestPuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_requestedBlock != null)
							_requestedSelect.setSelectedItem(_requestedBlock); 
						else 
							_requestedSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_requestedBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_requestedBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_requestedSelect.getSelectedItem() == null)
					_requestedSelect.setSelectedIndex(0);
				
				
				if(_requestedSelect.getSelectedItem().equals("None"))
					_requestedBlock = null;
				else
				{
					_requestedBlock = (PuzzleBlock)_requestedSelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _requestedBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				_requesterSelect.setModel(new DefaultComboBoxModel(makeRequesterList()));
				if(_requesterBlock == null)
					_requesterSelect.setSelectedIndex(0);
				else
					_requesterSelect.setSelectedItem(_requesterBlock);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
	}
	
	@Override
	public void update() {
		// Update the UI ComboBoxes
		_requesterSelect.setModel(new DefaultComboBoxModel(makeRequesterList()));
		
		// Update our selected values
		if (_requesterBlock == null)
			_requesterSelect.setSelectedIndex(0);
		else
			_requesterSelect.setSelectedItem(_requesterBlock);
		
		_requestedSelect.setModel(new DefaultComboBoxModel(makeRequestedList()));
		
		if (_requestedBlock == null)
			_requestedSelect.setSelectedIndex(0);
		else
			_requestedSelect.setSelectedItem(_requestedBlock);
	}

	
	private Object[] makeRequesterList()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_requestedSelect.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	private Object[] makeRequestedList()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_requesterSelect.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	@Override
	public void maybeRemoveRef(PuzzleBlock block)
	{
		if(block.equals(_requesterBlock)) {
			_requesterBlock = null;
			_requesterSelect.setSelectedIndex(0);
		}
		if(block.equals(_requestedBlock)) {
			_requestedBlock = null;
			_requestedSelect.setSelectedIndex(0);
		}
	}
	
	@Override
	public String getTextualDescription()
	{
		String retVal = "";
		if(_requesterBlock != null)
			retVal += _requesterBlock.getTextualDescription();
		if(_requestedBlock != null)
			retVal += _requestedBlock.getTextualDescription();
		
		_outputTempName = PuzzleEditPanel.nextItemName();
		String requester = (_requesterBlock == null) ? "SOMEONE" : _requesterBlock.getOutputTempName();
		String requested = (_requestedBlock == null) ? "SOMETHING" : _requestedBlock.getOutputTempName();
		
		retVal += requester + " requests " + requested + " from the player. When the player fulfills this request, he/she receives " + _outputTempName + " as a reward. ";
		
		return retVal;
		
	}
	
	@Override 
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles) 
	{
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		if (_requesterName != null) {
			_requesterBlock = puzzles.get(_requesterName);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _requesterBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		if (_requestedName != null) {
			_requestedBlock = puzzles.get(_requestedName);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _requestedBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		
		this.update();
	}
	
	@Override 
	public PuzzleBlock[] getPuzzleInputs() 
	{ 
		if (_requesterBlock != null && _requestedBlock != null)
			return new PuzzleBlock[] { _requesterBlock, _requestedBlock };
		else if (_requesterBlock != null)
			return new PuzzleBlock[] { _requesterBlock };
		else if (_requestedBlock != null)
			return new PuzzleBlock[] { _requestedBlock };
		return new PuzzleBlock[0];
	}
	
	@Override
	public String toXML()
	{
		String xml = "<ItemRequestPuzzle name=\"" + _name + "\" ";
		
		if (_requesterBlock != null) 
			xml += "requester=\"" + _requesterBlock.getName() + "\" ";
		if (_requestedBlock != null)
			xml += "requested=\"" + _requestedBlock.getName() + "\" ";
		
		xml += "/>";
		
		return xml;
	}
	
}
