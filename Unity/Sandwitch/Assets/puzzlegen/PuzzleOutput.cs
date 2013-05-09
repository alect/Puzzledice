using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using puzzlegen.relationship;

namespace puzzlegen 
{ 

	/// <summary>
	/// The puzzle output class encapsulates all of the output we would expect to receive from a call to 
	///	generatePuzzle in a building block. It was simply represented as a tuple in the original python
	///	implementation.	
	/// </summary>
	public class PuzzleOutput 
	{
		private List<PuzzleItem> _items; 
		public List<PuzzleItem> Items {
			get { return _items; }
			set { _items = value; }
		}
		
		private List<IRelationship> _relationships; 
		public List<IRelationship> Relationships {
			get { return _relationships; } 
			set { _relationships = value; }
		}
		
		public PuzzleOutput() 
		{ 
		 	_items = new List<PuzzleItem>(); 
			_relationships = new List<IRelationship>(); 
		} 
	
	}
	
}
