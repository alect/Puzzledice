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

public class ORBlock extends PuzzleBlock {

	private PuzzleBlock _optionBlock1, _optionBlock2;
	private static int nextIndex = 0; 
	public static void reset() { 
		nextIndex = 0;
	}
	
	private String _optionName1, _optionName2;
	public void setOptionName1(String value) { 
		_optionName1 = value; 
	}
	public void setOptionName2(String value) { 
		_optionName2 = value;
	}
	
	private JComboBox _optionSelect1, _optionSelect2; 
	
	public ORBlock() 
	{ 
		_name = "OR-Block-" + ++nextIndex; 
		_type = "OR Block";
		
		JPanel editPanel = new JPanel(); 
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		JPanel optionPanel1 = new JPanel();
		optionPanel1.setLayout(new BoxLayout(optionPanel1, BoxLayout.X_AXIS));
		
		JLabel optionLabel1 = new JLabel("Option 1:");
		optionPanel1.add(optionLabel1);
		
		_optionSelect1 = new JComboBox(); 
		_optionSelect1.setMaximumSize(new Dimension(Integer.MAX_VALUE, _optionSelect1.getPreferredSize().height));
		optionPanel1.add(_optionSelect1);
		
		editPanel.add(optionPanel1);
		
		JPanel optionPanel2 = new JPanel(); 
		optionPanel2.setLayout(new BoxLayout(optionPanel2, BoxLayout.X_AXIS));
		
		JLabel optionLabel2 = new JLabel("Option 2:");
		optionPanel2.add(optionLabel2);
		
		_optionSelect2 = new JComboBox(); 
		_optionSelect2.setMaximumSize(new Dimension(Integer.MAX_VALUE, _optionSelect2.getPreferredSize().height));
		optionPanel2.add(_optionSelect2);
		
		editPanel.add(optionPanel2);
		
		_editUI = editPanel;
		
		_optionSelect1.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				if(_optionBlock1 == _optionSelect1.getSelectedItem() || _optionBlock1 == null && _optionSelect1.getSelectedItem().equals("None"))
					return;
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
				
				// Before anything, check for a cycle
				if (_optionSelect1.getSelectedItem() != null && !_optionSelect1.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_optionSelect1.getSelectedItem(); 
					if (block.canReachBlockBackwards(ORBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_optionBlock1 != null)
							_optionSelect1.setSelectedItem(_optionBlock1); 
						else 
							_optionSelect1.setSelectedIndex(0);
						return;
					}
				}
				
				// First, see if we need to remove a previous edge
				if (_optionBlock1 != null) { 
					puzzleGraph.getModel().beginUpdate(); 
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_optionBlock1.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_optionSelect1.getSelectedItem() == null)
					_optionSelect1.setSelectedIndex(0);
				
				if (_optionSelect1.getSelectedItem().equals("None")) 
					_optionBlock1 = null;
				else { 
					_optionBlock1 = (PuzzleBlock)_optionSelect1.getSelectedItem();
					// Update the graph with a new edge
					puzzleGraph.getModel().beginUpdate(); 
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _optionBlock1.getGraphCell(), _graphCell); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				// Need to update our other option list 
				_optionSelect2.setModel(new DefaultComboBoxModel(makeComboBoxList2()));
				// Update our selected values 
				if (_optionBlock2 == null)
					_optionSelect2.setSelectedIndex(0);
				else 
					_optionSelect2.setSelectedItem(_optionBlock2);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
				
			}
		});
		
		_optionSelect2.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				if(_optionBlock2 == _optionSelect2.getSelectedItem() || _optionBlock2 == null && _optionSelect2.getSelectedItem().equals("None"))
					return;
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
				
				// Before anything, check for a cycle
				if (_optionSelect2.getSelectedItem() != null && !_optionSelect2.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_optionSelect2.getSelectedItem(); 
					if (block.canReachBlockBackwards(ORBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_optionBlock2 != null)
							_optionSelect2.setSelectedItem(_optionBlock2); 
						else 
							_optionSelect2.setSelectedIndex(0);
						return;
					}
				}
				
				// First, see if we need to remove a previous edge
				if (_optionBlock2 != null) { 
					puzzleGraph.getModel().beginUpdate(); 
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_optionBlock2.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_optionSelect2.getSelectedItem() == null)
					_optionSelect2.setSelectedIndex(0);
				
				if (_optionSelect2.getSelectedItem().equals("None")) 
					_optionBlock2 = null;
				else { 
					_optionBlock2 = (PuzzleBlock)_optionSelect2.getSelectedItem();
					// Update the graph with a new edge
					puzzleGraph.getModel().beginUpdate(); 
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _optionBlock2.getGraphCell(), _graphCell); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				// Need to update our other option list 
				_optionSelect1.setModel(new DefaultComboBoxModel(makeComboBoxList1()));
				// Update our selected values 
				if (_optionBlock1 == null)
					_optionSelect1.setSelectedIndex(0);
				else 
					_optionSelect1.setSelectedItem(_optionBlock1);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
				
			}
		});
		
		
	}
	
	@Override
	public void update() {
		// Update the UI comboBoxes
		_optionSelect1.setModel(new DefaultComboBoxModel(makeComboBoxList1()));
		
		// Update the selected values 
		if (_optionBlock1 == null)
			_optionSelect1.setSelectedIndex(0); 
		else
			_optionSelect1.setSelectedItem(_optionBlock1);
		
		_optionSelect2.setModel(new DefaultComboBoxModel(makeComboBoxList2()));
		
		if (_optionBlock2 == null)
			_optionSelect2.setSelectedIndex(0);
		else 
			_optionSelect2.setSelectedItem(_optionBlock2);

	}
	
	private Object[] makeComboBoxList1() 
	{ 
		List<Object> retVal = new ArrayList<Object>(); 
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None"); 
		for (PuzzleBlock p : blockList) { 
			if (!p.equals(_optionSelect2.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray(); 
	}
	
	private Object[] makeComboBoxList2() 
	{ 
		List<Object> retVal = new ArrayList<Object>(); 
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None"); 
		for (PuzzleBlock p : blockList) { 
			if (!p.equals(_optionSelect1.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray(); 
	}
	
	@Override 
	public void maybeRemoveRef(PuzzleBlock block)
	{ 
		if (block.equals(_optionBlock1)) { 
			_optionBlock1 = null; 
			_optionSelect1.setSelectedIndex(0);
		}
		if (block.equals(_optionBlock2)) { 
			_optionBlock2 = null; 
			_optionSelect2.setSelectedIndex(0);
		}
	}
	
	@Override 
	public String getTextualDescription() 
	{ 
		String retVal = ""; 
		if (_optionBlock1 != null)
			retVal += _optionBlock1.getTextualDescription();
		if (_optionBlock2 != null) 
			retVal += _optionBlock2.getTextualDescription();
		
		String input1 = (_optionBlock1 == null) ? "SOMETHING" : _optionBlock1.getOutputTempName();
		String input2 = (_optionBlock2 == null) ? "SOMETHING" : _optionBlock2.getOutputTempName();
		
		_outputTempName = "(" + input1 + " or " + input2 + ")";
		
		return retVal;
	}
	
	@Override 
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles)
	{
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
		if (_optionName1 != null) { 
			_optionBlock1 = puzzles.get(_optionName1);
			puzzleGraph.getModel().beginUpdate(); 
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _optionBlock1.getGraphCell(), _graphCell); }
			finally { puzzleGraph.getModel().endUpdate(); } 
		}
		if (_optionName2 != null) { 
			_optionBlock2 = puzzles.get(_optionName2);
			puzzleGraph.getModel().beginUpdate(); 
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _optionBlock2.getGraphCell(), _graphCell); }
			finally { puzzleGraph.getModel().endUpdate(); } 
		}
		
		this.update();
		
	}
	
	@Override 
	public PuzzleBlock[] getPuzzleInputs() 
	{ 
		if (_optionBlock1 != null && _optionBlock2 != null)
			return new PuzzleBlock[] { _optionBlock1, _optionBlock2 } ; 
		else if (_optionBlock1 != null)
			return new PuzzleBlock[] { _optionBlock1 };
		else if (_optionBlock2 != null)
			return new PuzzleBlock[] { _optionBlock2 };
		return new PuzzleBlock[0];
	}
	
	@Override
	public String toXML() 
	{ 
		String xml = "<ORBlock name=\"" + _name + "\" ";
		
		if (_optionBlock1 != null)
			xml += "option1=\"" + _optionBlock1.getName() + "\" ";
		if (_optionBlock2 != null)
			xml += "option2=\"" + _optionBlock2.getName() + "\" ";
		
		xml += "/>";
		
		return xml;
	}
	
	
	

}
