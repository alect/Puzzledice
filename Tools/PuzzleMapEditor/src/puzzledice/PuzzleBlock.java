package puzzledice;

import java.awt.Component;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


// Abstract class representing a single PuzzleBlock
// Subclasses implement specifics for different types of puzzle blocks
public abstract class PuzzleBlock {
	
	
	
	protected String _name;
	// Normally I would make any kind of type variable an enum, but the primary purpose of this field is to give 
	// a textual representation to the user of what kind of puzzle this is, so I'm making it a string. 
	protected String _type;
	protected Component _editUI;
	
	protected Object _graphCell;
	
	protected String _outputTempName = "";
	
	
	// Accessors and Mutators
	public String getName()
	{
		return _name;
	}
	
	public void setName(String newName) 
	{
		_name = newName;
	}
	
	//Get this type of Puzzle Blocks specific UI component that's used to edit this puzzle block.
	public Component getUI()
	{
		return _editUI;
	}
	
	// The type of this puzzle, represented as a string
	public String getPuzzleType()
	{
		return _type;
	}
	
	// Called when the puzzle block is selected. Used to update the puzzle blocks ui
	public abstract void update();
	
	// Overridden by subclasses
	// When a puzzle block is deleted, need to allow all puzzle blocks to remove references to it
	// By default, these functions should silently do nothing
	public void maybeRemoveRef(PuzzleBlock blockToRemove) {}
	// Similarly when an area is removed
	public void maybeRemoveRef(AreaBlock areaToRemove) {}
	
	// Called when a block is deleted to give it a chance to clean up its references (particularly useful for puzzle blocks that connect to areas)
	public void onDelete() {}
	
	
	// Used to construct the text view
	public String getTextualDescription() {return "";}
	public String getOutputTempName() {return _outputTempName;}
	
	public void setGraphCell(Object graphCell)
	{
		_graphCell = graphCell;
	}
	
	public Object getGraphCell()
	{
		return _graphCell;
	}
	
	public String getCellStyle()
	{
		return "fillcolor=#BAD0EF";
	}
	
	@Override
	public String toString() 
	{
		return _name;
	}
	
	// Function for finishing up the loading process. 
	public void attachBlocksToName(Map<String, AreaBlock> areas, Map<String, PuzzleBlock> puzzles) 
	{
		
	}
	
	// Functions used to detect cycles 
	public PuzzleBlock[] getPuzzleInputs() 
	{ 
		return new PuzzleBlock[0]; 
	}
	
	public AreaBlock[] getAreaInputs()
	{ 
		return new AreaBlock[0];
	} 
	
	public boolean canReachBlockBackwards(Object target)
	{ 
		Set<Object> closedBlocks = new HashSet<Object>(); 
		Queue<Object> agenda = new LinkedList<Object>(); 
		agenda.add(this); 
		while (agenda.size() > 0) {
			Object block = agenda.remove(); 
			if (block == target)
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
	
	
	public String toXML()
	{
		return "<PuzzleBlock name=\"" + _name + "\" />";
	}

}
