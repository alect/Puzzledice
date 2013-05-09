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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mxgraph.view.mxGraph;

public class PropertyChangePuzzleBlock extends PuzzleBlock {

	private PuzzleBlock _changerBlock, _changeeBlock;
	private static int nextIndex = 0;
	public static void reset() {
		nextIndex = 0;
	}
	
	private JComboBox _changerSelect, _changeeSelect;
	private JCheckBox _useProperty;
	private JPanel _propertyNamePanel, _propertyValuePanel;
	private JTextField _propertyNameSelect, _propertyValueSelect;
	
	private String _changerName, _changeeName, _propertyName, _propertyValue;
	public void setChangerName(String value) {
		_changerName = value;
	}
	public void setChangeeName(String value) { 
		_changeeName = value;
	}
	public void setPropertyName(String value) {
		_propertyName = value;
	}
	public void setPropertyValue(String value) {
		_propertyValue = value;
	}
	
	public PropertyChangePuzzleBlock()
	{
		_name = "Property-Change-" + ++nextIndex;
		_type = "Property Change Puzzle";
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		// Changer ComboBox Panel
		JPanel changerPanel = new JPanel();
		changerPanel.setLayout(new BoxLayout(changerPanel, BoxLayout.X_AXIS));
		
		JLabel changerLabel = new JLabel("Changer:");
		changerPanel.add(changerLabel);
		
		_changerSelect = new JComboBox();
		_changerSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _changerSelect.getPreferredSize().height));
		changerPanel.add(_changerSelect);
		
		editPanel.add(changerPanel);
		
		// Changee ComboBox Panel
		JPanel changeePanel = new JPanel();
		changeePanel.setLayout(new BoxLayout(changeePanel, BoxLayout.X_AXIS));
		
		JLabel changeeLabel = new JLabel("Changee:");
		changeePanel.add(changeeLabel);
		
		_changeeSelect = new JComboBox();
		_changeeSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _changeeSelect.getPreferredSize().height));
		changeePanel.add(_changeeSelect);
		editPanel.add(changeePanel);
		
		// Panel for asking to change a specific property
		JPanel propertyPanel = new JPanel();
		propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.X_AXIS));
		
		JLabel propertyLabel = new JLabel("Use Specific Property");
		propertyPanel.add(propertyLabel);
		
		_useProperty = new JCheckBox();
		propertyPanel.add(_useProperty);
		
		editPanel.add(propertyPanel);
		
		// Panel for changing the specific property name
		_propertyNamePanel = new JPanel();
		_propertyNamePanel.setLayout(new BoxLayout(_propertyNamePanel, BoxLayout.X_AXIS));
		
		JLabel propertyNameLabel = new JLabel("Desired Property Name:");
		_propertyNamePanel.add(propertyNameLabel);
		
		_propertyNameSelect = new JTextField();
		_propertyNameSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _propertyNameSelect.getPreferredSize().height));
		_propertyNameSelect.setText("None");
		_propertyNamePanel.add(_propertyNameSelect);
		_propertyNameSelect.setColumns(10);
		
		_propertyNamePanel.setVisible(false);
		
	
		editPanel.add(_propertyNamePanel);
		
		// Panel for changing the specific property value
		_propertyValuePanel = new JPanel();
		_propertyValuePanel.setLayout(new BoxLayout(_propertyValuePanel, BoxLayout.X_AXIS));
		
		JLabel propertyValueLabel = new JLabel("Desired Property Value:");
		_propertyValuePanel.add(propertyValueLabel);
		
		_propertyValueSelect = new JTextField();
		_propertyValueSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, _propertyValueSelect.getPreferredSize().height));
		_propertyValueSelect.setText("None");
		_propertyValuePanel.add(_propertyValueSelect);
		_propertyValueSelect.setColumns(10);
		
		_propertyValuePanel.setVisible(false);
		
		editPanel.add(_propertyValuePanel);
		
		_editUI = editPanel;
		
		_changerSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_changerBlock == _changerSelect.getSelectedItem() || _changerBlock == null && _changerSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_changerSelect.getSelectedItem() != null && !_changerSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_changerSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(PropertyChangePuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_changerBlock != null)
							_changerSelect.setSelectedItem(_changerBlock); 
						else 
							_changerSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_changerBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_changerBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_changerSelect.getSelectedItem() == null)
					_changerSelect.setSelectedIndex(0);
				
				if(_changerSelect.getSelectedItem().equals("None"))
					_changerBlock = null;
				else
				{
					_changerBlock = (PuzzleBlock)_changerSelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _changerBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				_changeeSelect.setModel(new DefaultComboBoxModel(makeChangeeComboBox()));
				if (_changeeBlock == null)
					_changeeSelect.setSelectedIndex(0);
				else
					_changeeSelect.setSelectedItem(_changeeBlock);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_changeeSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_changeeBlock == _changeeSelect.getSelectedItem() || _changeeBlock == null && _changeeSelect.getSelectedItem().equals("None"))
					return;
				
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_changeeSelect.getSelectedItem() != null && !_changeeSelect.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_changeeSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(PropertyChangePuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_changeeBlock != null)
							_changeeSelect.setSelectedItem(_changeeBlock); 
						else 
							_changeeSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_changeeBlock != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_changeeBlock.getGraphCell(), _graphCell, false)); }
					finally { puzzleGraph.getModel().endUpdate(); }
				}
				if (_changeeSelect.getSelectedItem() == null)
					_changeeSelect.setSelectedIndex(0);
				
				if(_changeeSelect.getSelectedItem().equals("None"))
					_changeeBlock = null;
				else
				{
					_changeeBlock = (PuzzleBlock)_changeeSelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _changeeBlock.getGraphCell(), _graphCell);}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				_changerSelect.setModel(new DefaultComboBoxModel(makeChangerComboBox()));
				if (_changerBlock == null)
					_changerSelect.setSelectedIndex(0);
				else
					_changerSelect.setSelectedItem(_changerBlock);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_useProperty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				// Set the visibility of certain elements
				_propertyNamePanel.setVisible(_useProperty.isSelected());
				_propertyValuePanel.setVisible(_useProperty.isSelected());
				PuzzleEditPanel.resetTextualDescription();
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
		_changerSelect.setModel(new DefaultComboBoxModel(makeChangerComboBox()));
		
		if (_changerBlock == null)
			_changerSelect.setSelectedIndex(0);
		else
			_changerSelect.setSelectedItem(_changerBlock);
		
		_changeeSelect.setModel(new DefaultComboBoxModel(makeChangeeComboBox()));
		
		if (_changeeBlock == null)
			_changeeSelect.setSelectedIndex(0);
		else
			_changeeSelect.setSelectedItem(_changeeBlock);
		
	}

	
	private Object[] makeChangerComboBox()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_changeeSelect.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	private Object[] makeChangeeComboBox()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_changerSelect.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	@Override
	public void maybeRemoveRef(PuzzleBlock block)
	{
		if(block.equals(_changerBlock)) {
			_changerBlock = null;
			_changerSelect.setSelectedIndex(0);
		}
		if(block.equals(_changeeBlock)) {
			_changeeBlock = null;
			_changeeSelect.setSelectedIndex(0);
		}
	}
	
	@Override 
	public String getTextualDescription()
	{
		String retVal = "";
		if (_changerBlock != null)
			retVal += _changerBlock.getTextualDescription();
		if (_changeeBlock != null)
			retVal += _changeeBlock.getTextualDescription();
		
		_outputTempName = (_changeeBlock == null) ? "SOMETHING" : _changeeBlock.getOutputTempName();
		String changer = (_changerBlock == null) ? "SOMETHING" : _changerBlock.getOutputTempName();
		
		String propertyName, propertyValue;
		if(_useProperty.isSelected())
		{
			propertyName = "the " + _propertyNameSelect.getText() + " property";
			propertyValue = "the value: " + _propertyValueSelect.getText();
		}
		else
		{
			propertyName = "some property";
			propertyValue = "some value";
		}
		
		
		retVal += "The player uses " + changer + " to change " + propertyName + " of " + _outputTempName + " to " + propertyValue + ". ";
		
		_outputTempName += " (with " + propertyName + " set to " + propertyValue + ")";
		
		return retVal;
	}
	
	@Override 
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles) 
	{
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		if (_changerName != null) {
			_changerBlock = puzzles.get(_changerName);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _changerBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		if (_changeeName != null) {
			_changeeBlock = puzzles.get(_changeeName);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _changeeBlock.getGraphCell(), _graphCell);}
			finally { puzzleGraph.getModel().endUpdate();}
		}
		if (_propertyName != null) {
			_propertyNameSelect.setText(_propertyName);
			_useProperty.setSelected(true);
		}
		if (_propertyValue != null) {
			_propertyValueSelect.setText(_propertyValue);
			_useProperty.setSelected(true);
		}
		
		_propertyNamePanel.setVisible(_useProperty.isSelected());
		_propertyValuePanel.setVisible(_useProperty.isSelected());
		
		this.update();
	}
	
	@Override
	public PuzzleBlock[] getPuzzleInputs() 
	{ 
		if (_changerBlock != null && _changeeBlock != null) 
			return new PuzzleBlock[] { _changerBlock, _changeeBlock };
		else if (_changerBlock != null)
			return new PuzzleBlock[] { _changerBlock };
		else if (_changeeBlock != null) 
			return new PuzzleBlock[] { _changeeBlock }; 
		return new PuzzleBlock[0];
	}
	
	@Override 
	public String toXML() 
	{
		String xml = "<PropertyChangePuzzle name=\"" + _name + "\" ";
		
		if (_changerBlock != null) 
			xml += "changer=\"" + _changerBlock.getName() + "\" ";
		if (_changeeBlock != null) 
			xml += "changee=\"" + _changeeBlock.getName() + "\" ";
		if (_useProperty.isSelected()) {
			if (_propertyNameSelect.getText() != null && !_propertyNameSelect.getText().equals("") && !_propertyNameSelect.getText().equals("None"))
				xml += "propertyName=\"" + _propertyNameSelect.getText() + "\" ";
			if (_propertyValueSelect.getText() != null && !_propertyValueSelect.getText().equals("") && !_propertyValueSelect.getText().equals("None"))
				xml += "propertyValue=\"" + _propertyValueSelect.getText() + "\" ";
		}
		
		xml += "/>";
		
		return xml;
	}
	
}
