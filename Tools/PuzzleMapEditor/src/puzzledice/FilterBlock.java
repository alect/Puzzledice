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

public class FilterBlock extends PuzzleBlock {

	private PuzzleBlock _inputBlock;
	private static int nextIndex = 0;
	public static void reset() {
		nextIndex = 0;
	}
	
	private String _inputName, _propertyName, _propertyValue;
	public void setInputName(String value) {
		_inputName = value;
	}
	public void setPropertyName(String value) {
		_propertyName = value;
	}
	public void setPropertyValue(String value) {
		_propertyValue = value;
	}
	
	private JComboBox _inputSelect;
	private JTextField _propertyNameSelect, _propertyValueSelect;
	
	public FilterBlock()
	{
		_name = "Filter-" + ++nextIndex;
		_type = "Filter";
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		// Input ComboBox Panel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		
		JLabel inputLabel = new JLabel("Input to Filter:");
		inputPanel.add(inputLabel);
		
		_inputSelect = new JComboBox();
		_inputSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _inputSelect.getPreferredSize().height));
		inputPanel.add(_inputSelect);
		
		editPanel.add(inputPanel);
		
		JPanel propertyNamePanel = new JPanel();
		propertyNamePanel.setLayout(new BoxLayout(propertyNamePanel, BoxLayout.X_AXIS));
		
		JLabel propertyNameLabel = new JLabel("Required Database Property Name:");
		propertyNamePanel.add(propertyNameLabel);
		
		_propertyNameSelect = new JTextField();
		_propertyNameSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _propertyNameSelect.getPreferredSize().height));
		_propertyNameSelect.setText("None");
		_propertyNameSelect.setColumns(10);
		propertyNamePanel.add(_propertyNameSelect);
		
		editPanel.add(propertyNamePanel);
		
		JPanel propertyValuePanel = new JPanel();
		propertyValuePanel.setLayout(new BoxLayout(propertyValuePanel, BoxLayout.X_AXIS));
		
		JLabel propertyValueLabel = new JLabel("Required Property Value:");
		propertyValuePanel.add(propertyValueLabel);
		
		_propertyValueSelect = new JTextField();
		_propertyValueSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _propertyValueSelect.getPreferredSize().height));
		_propertyValueSelect.setText("ALL");
		_propertyValueSelect.setColumns(10);
		propertyValuePanel.add(_propertyValueSelect);
		
		editPanel.add(propertyValuePanel);
		
		_editUI = editPanel;
		
		_inputSelect.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				if(_inputBlock == _inputSelect.getSelectedItem() || _inputBlock == null && _inputSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_inputSelect.getSelectedItem() != null && !_inputSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_inputSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(FilterBlock.this)) { 
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
				
				// first, see if we need to remove a previous edge
				if(_inputBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_inputBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_inputSelect.getSelectedItem() == null)
					_inputSelect.setSelectedIndex(0);
				
				if(_inputSelect.getSelectedItem().equals("None"))
					_inputBlock = null;
				else 
				{
					_inputBlock = (PuzzleBlock)_inputSelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _inputBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_propertyNameSelect.getDocument().addDocumentListener(new DocumentListener() {
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
		
		_propertyValueSelect.getDocument().addDocumentListener(new DocumentListener() {
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
	public void update() {
		_inputSelect.setModel(new DefaultComboBoxModel(makeInputList()));
		if(_inputBlock == null)
			_inputSelect.setSelectedIndex(0);
		else
			_inputSelect.setSelectedItem(_inputBlock);

	}
	
	private Object[] makeInputList()
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
	
	@Override 
	public void maybeRemoveRef(PuzzleBlock block)
	{
		if(block.equals(_inputBlock)) {
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
		
		retVal += input + " must have database property " + _propertyNameSelect.getText() + " with value: " + _propertyValueSelect.getText() + ". ";
		_outputTempName = input;
		
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
		if (_propertyName != null) {
			_propertyNameSelect.setText(_propertyName);
		}
		if (_propertyValue != null) {
			_propertyValueSelect.setText(_propertyValue);
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
		String xml = "<Filter name=\"" + _name + "\" ";
		if (_inputBlock != null)
			xml += "input=\"" + _inputBlock.getName() + "\" ";
		if (_propertyNameSelect.getText() != null && !_propertyNameSelect.getText().equals(""))
			xml += "propertyName=\"" + _propertyNameSelect.getText() + "\" ";
		if (_propertyValueSelect.getText() != null && !_propertyValueSelect.getText().equals(""))
			xml += "propertyValue=\"" + _propertyValueSelect.getText() + "\" ";
		
		xml += "/>";
		
		return xml;
	}

}
