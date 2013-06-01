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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mxgraph.view.mxGraph;

public class OutputBlock extends PuzzleBlock {

	private PuzzleBlock _inputBlock; 
	private static int nextIndex = 0; 
	public static void reset() { 
		nextIndex = 0; 
	}
	
	private String _inputName, _requestName; 
	public void setInputName(String value) { 
		_inputName = value; 
	}
	public void setRequestName(String value) { 
		_requestName = value; 
	}
	
	private JComboBox _inputSelect; 
	private JTextField _requestSelect; 
	
	public OutputBlock() 
	{ 
		_name = "Output-" + ++nextIndex; 
		_type = "Output";
		
		JPanel editPanel = new JPanel(); 
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS)); 
		
		// Input ComboBoxPanel
		JPanel inputPanel = new JPanel(); 
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS)); 
		
		JLabel inputLabel = new JLabel("Root Block:"); 
		inputPanel.add(inputLabel);
		
		_inputSelect = new JComboBox(); 
		_inputSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _inputSelect.getPreferredSize().height));
		inputPanel.add(_inputSelect); 
		
		editPanel.add(inputPanel);
		
		JPanel requestPanel = new JPanel(); 
		requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.X_AXIS)); 
		
		JLabel requestLabel = new JLabel("Requested Output Item: "); 
		requestPanel.add(requestLabel);
		
		_requestSelect = new JTextField();
		_requestSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _requestSelect.getPreferredSize().height));
		_requestSelect.setText("None");
		_requestSelect.setColumns(10);
		requestPanel.add(_requestSelect);
		
		editPanel.add(requestPanel); 
		
		_editUI = editPanel; 
		
		_inputSelect.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent evt) { 
				if (_inputBlock == _inputSelect.getSelectedItem() || _inputBlock == null && _inputSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
				
				// Before anything, check for a cycle
				if (_inputSelect.getSelectedItem() != null && !_inputSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_inputSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(OutputBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_inputBlock != null)
							_inputSelect.setSelectedItem(_inputBlock); 
						else 
							_inputSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// First, see if we need to remove a previous edge
				if (_inputBlock != null) { 
					puzzleGraph.getModel().beginUpdate(); 
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_inputBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); } 
				}
				if (_inputSelect.getSelectedItem() == null)
					_inputSelect.setSelectedIndex(0);
				
				if (_inputSelect.getSelectedItem().equals("None"))
					_inputBlock = null;
				else { 
					_inputBlock = (PuzzleBlock)_inputSelect.getSelectedItem();
					// Update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _inputBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
				
			}
		});
		
		_requestSelect.getDocument().addDocumentListener(new DocumentListener() { 
			@Override
			public void changedUpdate(DocumentEvent evt) {
				PuzzleEditPanel.resetTextualDescription();
			}
			
			@Override
			public void insertUpdate(DocumentEvent evt) {
				PuzzleEditPanel.resetTextualDescription();
			}
			
			@Override
			public void removeUpdate(DocumentEvent evt) {
				PuzzleEditPanel.resetTextualDescription();
			}
		});
		
	}
	
	
	@Override
	public void update() 
	{
		_inputSelect.setModel(new DefaultComboBoxModel(makeInputList())); 
		if (_inputBlock == null)
			_inputSelect.setSelectedIndex(0);
		else 
			_inputSelect.setSelectedItem(_inputBlock);
	}
	
	private Object[] makeInputList()
	{ 
		List<Object> retVal = new ArrayList<Object>(); 
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList(); 
		retVal.add("None"); 
		for (PuzzleBlock p : blockList) { 
			if (!p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	@Override 
	public void maybeRemoveRef(PuzzleBlock block) 
	{ 
		if (block.equals(_inputBlock)) { 
			_inputBlock = null; 
			_inputSelect.setSelectedIndex(0);
		}
	}
	
	@Override 
	public String getTextualDescription()
	{ 
		String retVal = "";
		if(_inputBlock != null)
			retVal += _inputBlock.getTextualDescription();
		
		String input = (_inputBlock == null) ? "SOMETHING" : _inputBlock.getOutputTempName(); 
		
		retVal += input + " is the output item " + _requestSelect.getText() + " and ends the puzzle.";
		_outputTempName = _requestSelect.getText(); 
		
		return retVal;
		
	}
	
	@Override 
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles)
	{ 
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
		if (_inputName != null) { 
			_inputBlock = puzzles.get(_inputName); 
			puzzleGraph.getModel().beginUpdate(); 
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _inputBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		if (_requestName != null) { 
			_requestSelect.setText(_requestName);
		}
		this.update();
	}
	
	@Override 
	public PuzzleBlock[] getPuzzleInputs() 
	{ 
		if (_inputBlock != null)
			return new PuzzleBlock[] { _inputBlock };
		return new PuzzleBlock[0];
	}
	
	@Override 
	public String toXML()
	{ 
		String xml = "<Output name=\"" + _name + "\" ";
		if (_inputBlock != null) 
			xml += "input=\"" + _inputBlock.getName() + "\" ";
		if (_requestSelect.getText() != null && !_requestSelect.getText().equals(""))
			xml += "requestName=\"" + _requestSelect.getText() + "\" ";
		
		xml += "/>";
		
		return xml;
	}

}
