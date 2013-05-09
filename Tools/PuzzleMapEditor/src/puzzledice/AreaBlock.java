package puzzledice;

import gui.AreaEditPanel;
import gui.WindowMain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set; 
import java.util.HashSet; 
import java.util.Queue; 
import java.util.LinkedList;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class AreaBlock {

	private List<AreaBlock> _doors;
	private String _name;
	private Object _graphCell;
	private Object _puzzleGraphCell = null;
	
	// For keeping track of our edges in the puzzle graph. 
	private List<AreaBlock> _puzzleEdges; 
	
	private boolean _isStartArea = false; 
	public void setStartArea(boolean value) { 
		if (!_isStartArea && value) {
			_isStartArea = value; 
			createPuzzleCell(); 
		}
		_isStartArea = value; 
	}
	public boolean isStartArea() { 
		return _isStartArea; 
	}
	
	
	
	// Used to properly display the puzzle textual descriptions
	// If this door has a non-empty list, then this area must be a destination area behind a locked door.
	private List<DoorUnlockBlock> _lockedDoors;
	// Used for correctly building the puzzle graphs. 
	// If we're the source of a locked door 
	private List<DoorUnlockBlock> _sourceLockedDoors; 
		
	public AreaBlock(String name) 
	{
		_name = name;
		_doors = new ArrayList<AreaBlock>();
		_lockedDoors = new ArrayList<DoorUnlockBlock>();
		_sourceLockedDoors = new ArrayList<DoorUnlockBlock>(); 
		_puzzleEdges = new ArrayList<AreaBlock>(); 
	}
	
	public void addDoor(AreaBlock doorToAdd) 
	{
		_doors.add(doorToAdd);
	}
	
	public boolean hasDoor(AreaBlock door) 
	{
		return _doors.contains(door);
	}
	
	public void removeDoor(AreaBlock doorToRemove)
	{
		_doors.remove(doorToRemove);
	}
	
	public void addPuzzleEdge(AreaBlock edgeToAdd) 
	{ 
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
		createPuzzleCell(); 
		puzzleGraph.getModel().beginUpdate(); 
		try
		{
			// Have our given destination add a puzzle graph cell if it doesn't have one already
			edgeToAdd.createPuzzleCell(); 
			puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, _puzzleGraphCell, edgeToAdd.getPuzzleGraphCell());
			_puzzleEdges.add(edgeToAdd);
		}
		finally { puzzleGraph.getModel().endUpdate(); }
	}
	
	public void removePuzzleEdges()
	{ 
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
		if (_puzzleGraphCell != null) { 
			puzzleGraph.getModel().beginUpdate(); 
			try
			{ 
				for (AreaBlock edge : _puzzleEdges) { 
					puzzleGraph.removeCells(puzzleGraph.getEdgesBetween(_puzzleGraphCell, edge.getPuzzleGraphCell(), false)); 
				}
			}
			finally { puzzleGraph.getModel().endUpdate(); }
		}
		_puzzleEdges.clear(); 
	}
	
	public AreaBlock[] getPuzzleEdges() 
	{ 
		return _puzzleEdges.toArray(new AreaBlock[_puzzleEdges.size()]); 
	}
	
	// For finding cycles in the area puzzle graph
	public boolean nodeReachable(AreaBlock target) { 
		// Do a simple bfs
		Set<AreaBlock> closedBlocks = new HashSet<AreaBlock>();  
		Queue<AreaBlock> agenda = new LinkedList<AreaBlock>(); 
		agenda.add(this); 
		while (agenda.size() > 0) { 
			AreaBlock block = agenda.remove(); 
			if (block == target) 
				return true; 
			closedBlocks.add(block); 
			for (AreaBlock neighbor : block.getPuzzleEdges()) { 
				if (!closedBlocks.contains(neighbor))
					agenda.add(neighbor); 
			}
		}
		return false; 
	}
	
	public boolean canReachBlockBackwards(Object target) 
	{ 
		Set<Object> closedBlocks = new HashSet<Object>(); 
		Queue<Object> agenda = new LinkedList<Object>(); 
		agenda.add(this); 
		while (agenda.size() > 0) {
			Object block = agenda.remove(); 
			System.out.println(block);
			if (block.equals(target))
				return true; 
			closedBlocks.add(block); 
			if (block instanceof PuzzleBlock) { 
				PuzzleBlock pBlock = (PuzzleBlock)block;
				for (PuzzleBlock neighbor : pBlock.getPuzzleInputs()) { 
					if (!closedBlocks.contains(neighbor))
						agenda.add(neighbor); 
				}
				for (AreaBlock neighbor : pBlock.getAreaInputs()) { 
					if (!closedBlocks.contains(neighbor))
						agenda.add(neighbor); 
				}
			}
			else if (block instanceof AreaBlock) { 
				AreaBlock aBlock = (AreaBlock)block; 
				for (DoorUnlockBlock neighbor : aBlock.getLockedDoorList()) { 
					if (!closedBlocks.contains(neighbor))
						agenda.add(neighbor);
				}
				for (AreaBlock neighbor : aBlock.getInputAreas()) { 
					if (!closedBlocks.contains(neighbor))
						agenda.add(neighbor); 
				}
			}
			
		}
		return false;
	}
	
	public AreaBlock[] getDoorList() 
	{
		return _doors.toArray(new AreaBlock[_doors.size()]);
	}
	
	public void setName(String newName) 
	{
		_name = newName;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void createPuzzleCell()
	{ 
		mxGraph puzzleGraph = WindowMain.getPuzzleGraph(); 
		puzzleGraph.getModel().beginUpdate(); 
		try
		{
			// First, create a graph cell if we don't have one already 
			if (_puzzleGraphCell == null) { 
				setPuzzleGraphCell(puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, this, 0, 0, 0, 0, null));
				mxCell edge = (mxCell)puzzleGraph.insertEdge(puzzleGraph.getDefaultParent(), null, null, WindowMain.getHierarchyRoot(), _puzzleGraphCell);
				edge.setVisible(_isStartArea);
				puzzleGraph.updateCellSize(_puzzleGraphCell);
			}
			else { 
				puzzleGraph.updateCellSize(_puzzleGraphCell);
				for (Object cell : puzzleGraph.getEdgesBetween(WindowMain.getHierarchyRoot(), _puzzleGraphCell, false)) {
					((mxCell)cell).setVisible(_isStartArea);
				}
			}
		}
		finally { puzzleGraph.getModel().endUpdate(); }
	}
	
	public void setGraphCell(Object graphCell) 
	{
		_graphCell = graphCell;
	}
	
	public void setPuzzleGraphCell(Object graphCell)
	{
		_puzzleGraphCell = graphCell;
	}
	
	public Object getGraphCell()
	{
		return _graphCell;
	}
	
	public Object getPuzzleGraphCell()
	{
		return _puzzleGraphCell;
	}
	
	
	
	// function that deletes our puzzle cell if it no longer has any edges
	public void maybeDeletePuzzleCell()
	{
		if (_puzzleGraphCell == null)
			return;
		mxCell cell = (mxCell)_puzzleGraphCell;
		if(cell.getEdgeCount() <= 1 && !_isStartArea)
		{
			mxGraph puzzleGraph = WindowMain.getPuzzleGraph();
			puzzleGraph.removeCells(new Object[]{_puzzleGraphCell});
			_puzzleGraphCell = null;
		}
	}
	
	@Override 
	public String toString() 
	{
		if (!_isStartArea)
			return _name;
		else 
			return _name + "\n(Start Area)";
	}
	
	public void addDoorLock(DoorUnlockBlock lock)
	{
		_lockedDoors.add(lock);
	}
	
	public void removeDoorLock(DoorUnlockBlock lockToRemove)
	{
		_lockedDoors.remove(lockToRemove);
	}
	
	public DoorUnlockBlock[] getLockedDoorList()
	{ 
		return _lockedDoors.toArray(new DoorUnlockBlock[_lockedDoors.size()]);
	}
	
	public void addSourceLock(DoorUnlockBlock lock)
	{ 
		_sourceLockedDoors.add(lock); 
	}
	
	public void removeSourceLock(DoorUnlockBlock lock)
	{ 
		_sourceLockedDoors.remove(lock); 
	}
	
	public DoorUnlockBlock[] getSourceLockList() 
	{ 
		return _sourceLockedDoors.toArray(new DoorUnlockBlock[_sourceLockedDoors.size()]); 
	}
	
	public AreaBlock[] getInputAreas() { 
		// Find all areas that have an edge pointing to this area 
		List<AreaBlock> inputAreas = new ArrayList<AreaBlock>(); 
		for (AreaBlock area : AreaEditPanel.getAreaList()) { 
			for (AreaBlock maybeMe : area.getPuzzleEdges()) { 
				if (maybeMe == this) { 
					inputAreas.add(area); 
					break;
				}
			}
		}
		return inputAreas.toArray(new AreaBlock[inputAreas.size()]); 
	}
	
	public String getTextualDescription()
	{
		String retVal = "";
		for (DoorUnlockBlock d : _lockedDoors)
			retVal += d.getTextualDescription();
		for (AreaBlock a : getInputAreas()) 
			retVal += a.getTextualDescription();
		return retVal;
	}
	
	public String toXML() 
	{
		String xml = "<area name=\"" + _name + "\" startArea=\"" + _isStartArea + "\"";
		if (_lockedDoors.size() == 0 && _doors.size() == 0)
			xml += "/>\n";
		else { 
			xml += ">\n";
			for (AreaBlock area : _doors) {
				xml += "<door name=\"" + area.getName() + "\"/>\n";
			}
			for (DoorUnlockBlock lock : _lockedDoors) {
				xml += "<lockedDoor name=\"" + lock.getName() + "\"/>\n"; 
			}
			for (AreaBlock inputArea : getInputAreas()) { 
				xml += "<inputArea name=\"" + inputArea.getName() + "\"/>\n";
			}
			xml += "</area>";
		}
		return xml;
	}
	
}
