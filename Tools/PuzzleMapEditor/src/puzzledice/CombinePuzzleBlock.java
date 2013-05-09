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

public class CombinePuzzleBlock extends PuzzleBlock {

	private PuzzleBlock _ingredientBlock1, _ingredientBlock2;
	private static int nextIndex = 0;
	public static void reset() {
		nextIndex = 0;
	}
	
	private String _ingredientName1, _ingredientName2;
	public void setIngredientName1(String value) {
		_ingredientName1 = value;
	}
	public void setIngredientName2(String value) {
		_ingredientName2 = value;
	}
	
	
	private JComboBox _ingredientSelect1, _ingredientSelect2;
	
	public CombinePuzzleBlock()
	{
		_name = "Combine-Puzzle-" + ++nextIndex;
		_type = "Combine Puzzle";
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		// First combobox panel
		JPanel ingredientPanel1 = new JPanel();
		ingredientPanel1.setLayout(new BoxLayout(ingredientPanel1, BoxLayout.X_AXIS));
		
		JLabel ingredientLabel1 = new JLabel("Ingredient 1:");
		ingredientPanel1.add(ingredientLabel1);
		
		_ingredientSelect1 = new JComboBox();
		_ingredientSelect1.setMaximumSize(new Dimension(Integer.MAX_VALUE, _ingredientSelect1.getPreferredSize().height));
		ingredientPanel1.add(_ingredientSelect1);
		
		editPanel.add(ingredientPanel1);
		
		
		JPanel ingredientPanel2 = new JPanel();
		ingredientPanel2.setLayout(new BoxLayout(ingredientPanel2, BoxLayout.X_AXIS));
		
		JLabel ingredientLabel2 = new JLabel("Ingredient 2:");
		ingredientPanel2.add(ingredientLabel2);
		
		_ingredientSelect2 = new JComboBox();
		_ingredientSelect2.setMaximumSize(new Dimension(Integer.MAX_VALUE, _ingredientSelect2.getPreferredSize().height));
		ingredientPanel2.add(_ingredientSelect2);
		
		editPanel.add(ingredientPanel2);
		
		_editUI = editPanel;
		
		_ingredientSelect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_ingredientBlock1 == _ingredientSelect1.getSelectedItem() || _ingredientBlock1 == null && _ingredientSelect1.getSelectedItem().equals("None"))
					return;
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle
				if (_ingredientSelect1.getSelectedItem() != null && !_ingredientSelect1.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_ingredientSelect1.getSelectedItem(); 
					if (block.canReachBlockBackwards(CombinePuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_ingredientBlock1 != null)
							_ingredientSelect1.setSelectedItem(_ingredientBlock1); 
						else 
							_ingredientSelect1.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_ingredientBlock1 != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try	{ puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_ingredientBlock1.getGraphCell(), _graphCell, false));}
					finally {puzzleGraph.getModel().endUpdate();}
				}
				if (_ingredientSelect1.getSelectedItem() == null) 
					_ingredientSelect1.setSelectedIndex(0);
				
				if(_ingredientSelect1.getSelectedItem().equals("None"))
					_ingredientBlock1 = null;
				else
				{
					_ingredientBlock1 = (PuzzleBlock)_ingredientSelect1.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _ingredientBlock1.getGraphCell(), _graphCell); }
					finally {puzzleGraph.getModel().endUpdate();}
				}
				// Need to update our other ingredient list
				_ingredientSelect2.setModel(new DefaultComboBoxModel(makeComboBoxList2()));
				// Update our selected values
				if (_ingredientBlock2 == null)
					_ingredientSelect2.setSelectedIndex(0);
				else
					_ingredientSelect2.setSelectedItem(_ingredientBlock2);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
		_ingredientSelect2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(_ingredientBlock2 == _ingredientSelect2.getSelectedItem() ||   _ingredientBlock2 == null && _ingredientSelect2.getSelectedItem().equals("None"))
					return;
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				
				// Before anything, check for a cycle
				if (_ingredientSelect2.getSelectedItem() != null && !_ingredientSelect2.getSelectedItem().equals("None")) { 
					PuzzleBlock block = (PuzzleBlock)_ingredientSelect2.getSelectedItem(); 
					if (block.canReachBlockBackwards(CombinePuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() {
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_ingredientBlock2 != null)
							_ingredientSelect2.setSelectedItem(_ingredientBlock2); 
						else 
							_ingredientSelect2.setSelectedIndex(0);
						return;
					}
				}
				
				// first, see if we need to remove a previous edge
				if(_ingredientBlock2 != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try	{ puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_ingredientBlock2.getGraphCell(), _graphCell, false));}
					finally {puzzleGraph.getModel().endUpdate();}
				}
				if (_ingredientSelect2.getSelectedItem() == null)
					_ingredientSelect2.setSelectedIndex(0);
				
				if(_ingredientSelect2.getSelectedItem().equals("None"))
					_ingredientBlock2 = null;
				else
				{
					_ingredientBlock2 = (PuzzleBlock)_ingredientSelect2.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _ingredientBlock2.getGraphCell(), _graphCell); }
					finally {puzzleGraph.getModel().endUpdate();}
				}
				
				_ingredientSelect1.setModel(new DefaultComboBoxModel(makeComboBoxList1()));
				// Update our selected values
				if (_ingredientBlock1 == null)
					_ingredientSelect1.setSelectedIndex(0);
				else
					_ingredientSelect1.setSelectedItem(_ingredientBlock1);
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
	}
	
	
	@Override
	public void update() {
		// Update the UI comboBoxes
		_ingredientSelect1.setModel(new DefaultComboBoxModel(makeComboBoxList1()));

		// Update our selected values
		if (_ingredientBlock1 == null)
			_ingredientSelect1.setSelectedIndex(0);
		else
			_ingredientSelect1.setSelectedItem(_ingredientBlock1);
		
		_ingredientSelect2.setModel(new DefaultComboBoxModel(makeComboBoxList2()));
		
		if(_ingredientBlock2 == null)
			_ingredientSelect2.setSelectedIndex(0);
		else
			_ingredientSelect2.setSelectedItem(_ingredientBlock2);
	}

	
	private Object[] makeComboBoxList1()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_ingredientSelect2.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	private Object[] makeComboBoxList2()
	{
		List<Object> retVal = new ArrayList<Object>();
		PuzzleBlock[] blockList = PuzzleEditPanel.getBlockList();
		retVal.add("None");
		for(PuzzleBlock p : blockList) {
			if(!p.equals(_ingredientSelect1.getSelectedItem()) && !p.equals(this))
				retVal.add(p);
		}
		return retVal.toArray();
	}
	
	@Override
	public void maybeRemoveRef(PuzzleBlock block)
	{
		if(block.equals(_ingredientBlock1)) {
			_ingredientBlock1 = null;
			_ingredientSelect1.setSelectedIndex(0);
		}
		if(block.equals(_ingredientBlock2)) {
			_ingredientBlock2 = null;
			_ingredientSelect2.setSelectedIndex(0);
		}
	}
	
	@Override
	public String getTextualDescription()
	{
		String retVal = "";
		if(_ingredientBlock1 != null)
			retVal += _ingredientBlock1.getTextualDescription();
		if(_ingredientBlock2 != null)
			retVal += _ingredientBlock2.getTextualDescription();
		
		_outputTempName = PuzzleEditPanel.nextItemName();
		String input1 = (_ingredientBlock1 == null) ? "SOMETHING" : _ingredientBlock1.getOutputTempName();
		String input2 = (_ingredientBlock2 == null) ? "SOMETHING" : _ingredientBlock2.getOutputTempName();
		
		retVal += "The Player combines " + input1 +  " with " + input2 + " to create " + _outputTempName + ". ";
		
		
		return retVal;
	}
	
	@Override
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles) 
	{
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		if (_ingredientName1 != null) {
			_ingredientBlock1 = puzzles.get(_ingredientName1);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _ingredientBlock1.getGraphCell(), _graphCell); }
			finally {puzzleGraph.getModel().endUpdate();}
		}
		if (_ingredientName2 != null) {
			_ingredientBlock2 = puzzles.get(_ingredientName2);
			puzzleGraph.getModel().beginUpdate();
			try { puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _ingredientBlock2.getGraphCell(), _graphCell); }
			finally {puzzleGraph.getModel().endUpdate();}
		}
		
		this.update();
	}
	
	@Override 
	public PuzzleBlock[] getPuzzleInputs() 
	{ 
		if (_ingredientBlock1 != null && _ingredientBlock2 != null)
			return new PuzzleBlock[] { _ingredientBlock1, _ingredientBlock2 };
		else if (_ingredientBlock1 != null) 
			return new PuzzleBlock[] { _ingredientBlock1 };
		else if (_ingredientBlock2 != null) 
			return new PuzzleBlock[] { _ingredientBlock2 };
		else 
			return new PuzzleBlock[0];
	}
	
	@Override
	public String toXML() 
	{ 
		String xml = "<CombinePuzzle name=\"" + _name + "\" ";
		
		if (_ingredientBlock1 != null)
			xml += "ingredient1=\"" + _ingredientBlock1.getName() + "\" ";
		if (_ingredientBlock2 != null)
			xml += "ingredient2=\"" + _ingredientBlock2.getName() + "\" ";
		
		xml += "/>";
		
		return xml;
	}
	
}
