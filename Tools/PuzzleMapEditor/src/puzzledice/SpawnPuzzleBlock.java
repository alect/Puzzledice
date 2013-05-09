package puzzledice;

import gui.AreaEditPanel;
import gui.PuzzleEditPanel;
import gui.WindowMain;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class SpawnPuzzleBlock extends PuzzleBlock {
	
	
	private AreaBlock _spawnArea;
	private static int nextIndex = 0;
	public static void reset() {
		nextIndex = 0;
	}
	
	private final JComboBox spawnAreaSelect;
	
	private String _spawnAreaName;
	public void setSpawnAreaName(String value) {
		_spawnAreaName = value;
	}
	
	public SpawnPuzzleBlock()
	{
		_name = "Spawn-Puzzle-" + ++nextIndex;
		_type = "Spawn Puzzle";
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
		
		JLabel spawnLabel = new JLabel("Spawn Area:");
		
		
		editPanel.add(spawnLabel);
		
		spawnAreaSelect = new JComboBox();
		spawnAreaSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, spawnAreaSelect.getPreferredSize().height));
		editPanel.add(spawnAreaSelect);
		
		_editUI = editPanel;
		
		spawnAreaSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(_spawnArea == spawnAreaSelect.getSelectedItem() || _spawnArea == null && spawnAreaSelect.getSelectedItem().equals("None"))
					return;
				mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
				
				// Before anything, check for a cycle 
				if (spawnAreaSelect.getSelectedItem() != null && !spawnAreaSelect.getSelectedItem().equals("None")) { 
					AreaBlock block = (AreaBlock)spawnAreaSelect.getSelectedItem(); 
					if (block.canReachBlockBackwards(SpawnPuzzleBlock.this)) { 
						SwingUtilities.invokeLater(new Runnable() { 
							public void run() { 
								JOptionPane.showMessageDialog(null, "Error: Cannot add cycle to puzzle graph.");
							}
						});
						if (_spawnArea != null)
							spawnAreaSelect.setSelectedItem(_spawnArea); 
						else 
							spawnAreaSelect.setSelectedIndex(0);
						return;
					}
				}
				
				// First, see if we need to remove a previous edge 
				if(_spawnArea != null)
				{
					puzzleGraph.getModel().beginUpdate();
					try 
					{ 
						puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_spawnArea.getPuzzleGraphCell(), _graphCell, false));
						_spawnArea.maybeDeletePuzzleCell();
					}
					finally { puzzleGraph.getModel().endUpdate();}
				}
				if (spawnAreaSelect.getSelectedItem() == null)
					spawnAreaSelect.setSelectedIndex(0);
				
				
				if(spawnAreaSelect.getSelectedItem().equals("None"))
					_spawnArea = null;
				else
				{	
					_spawnArea = (AreaBlock)spawnAreaSelect.getSelectedItem();
					// update the graph with a new edge
					puzzleGraph.getModel().beginUpdate();
					try
					{
						if(_spawnArea.getPuzzleGraphCell() == null) {
							_spawnArea.setPuzzleGraphCell(puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, _spawnArea, 0, 0, 0, 0, null));
							mxCell edge = (mxCell)puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, WindowMain.getHierarchyRoot(), _spawnArea.getPuzzleGraphCell());
							edge.setVisible(false);
							puzzleGraph.updateCellSize(_spawnArea.getPuzzleGraphCell());
						}
						puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _spawnArea.getPuzzleGraphCell(), _graphCell);
					}
					finally {puzzleGraph.getModel().endUpdate();}
						
				}
				PuzzleEditPanel.resetTextualDescription();
				WindowMain.updatePuzzleGraph();
			}
		});
		
	}
	
	@Override 
	public void update() 
	{
		// Need to update the UI ComboBox
		spawnAreaSelect.setModel(new DefaultComboBoxModel(makeComboBoxList()));
		// Update our selected value
		if(_spawnArea == null)
			spawnAreaSelect.setSelectedIndex(0);
		else
			spawnAreaSelect.setSelectedItem(_spawnArea);
		
	}
	
	private Object[] makeComboBoxList() 
	{
		AreaBlock[] areaList = AreaEditPanel.getAreaList();
		Object[] retVal = new Object[areaList.length+1];
		retVal[0] = "None";
		for(int i = 0; i < areaList.length; i++) {
			retVal[i+1] = areaList[i];
		}
		return retVal;
	}
	
	
	public void setSpawnArea(AreaBlock area) 
	{
		_spawnArea = area;
	}
	
	@Override
	public void maybeRemoveRef(AreaBlock area)
	{
		if(area.equals(_spawnArea)) {
			_spawnArea = null;
			spawnAreaSelect.setSelectedIndex(0);
		}
			
	}
	
	
	@Override
	public void onDelete() 
	{
		// Remove our reference to our spawn area
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		if(_spawnArea != null)
		{
			puzzleGraph.getModel().beginUpdate();
			try 
			{ 
				puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_spawnArea.getPuzzleGraphCell(), _graphCell, false));
				_spawnArea.maybeDeletePuzzleCell();
			}
			finally { puzzleGraph.getModel().endUpdate();}
		}
	}
	
	@Override 
	public String getTextualDescription() 
	{
		String retVal = (_spawnArea == null) ? "" : _spawnArea.getTextualDescription();
		String roomName = (_spawnArea == null) ? "SOMEWHERE" : _spawnArea.getName();
		_outputTempName = PuzzleEditPanel.nextItemName();
		return retVal + _outputTempName + " shows up in " + roomName + ". ";
	}
	
	@Override 
	public String getCellStyle()
	{
		return "fillColor=#8FFEDD";
	}
	
	@Override 
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles) 
	{
		if (_spawnAreaName == null)
			return;
		
		_spawnArea = areas.get(_spawnAreaName);
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
		puzzleGraph.getModel().beginUpdate();
		try
		{
			if(_spawnArea.getPuzzleGraphCell() == null) {
				_spawnArea.setPuzzleGraphCell(puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, _spawnArea, 0, 0, 0, 0, null));
				mxCell edge = (mxCell)puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, WindowMain.getHierarchyRoot(), _spawnArea.getPuzzleGraphCell());
				edge.setVisible(false);
				puzzleGraph.updateCellSize(_spawnArea.getPuzzleGraphCell());
			}
			puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _spawnArea.getPuzzleGraphCell(), _graphCell);
		}
		finally {puzzleGraph.getModel().endUpdate();}
		
		
		this.update();
	}
	
	@Override
	public AreaBlock[] getAreaInputs()
	{ 
		if (_spawnArea != null)
			return new AreaBlock[] { _spawnArea };
		return new AreaBlock[0];
	}
	
	public String toXML() 
	{
		String xml = "<SpawnPuzzle name=\"" + _name + "\" ";
		
		if (_spawnArea != null) 
			xml += "spawnArea=\"" + _spawnArea.getName() + "\"";
		
		xml += "/>";
		
		return xml;
	}

}
