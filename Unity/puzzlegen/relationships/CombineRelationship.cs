using UnityEngine;
using System.Collections;

namespace puzzlegen.relationship { 

	public class CombineRelationship : IRelationship
	{
		protected string _ingredient1Name; 
		public string ingredient1Name { 
			get { return _ingredient1Name; } 	
		} 
		
		protected int _ingredient1Index; 
		public int ingredient1Index { 
			get { return _ingredient1Index; } 	
		} 
		
		protected string _ingredient2Name; 
		public string ingredient2Name { 
			get { return _ingredient2Name; } 	
		} 
		
		protected int _ingredient2Index; 
		public int ingredient2Index { 
			get { return _ingredient2Index; } 	
		} 
		
		protected PuzzleItem _resultItem; 
		public PuzzleItem resultItem { 
			get { return _resultItem; } 
		} 
		
		public CombineRelationship(string ingredient1Name, int ing1Index, string ingredient2Name, int ing2Index, PuzzleItem resultItem) 
		{ 
			_ingredient1Name = ingredient1Name; 
			_ingredient1Index = ing1Index; 
			_ingredient2Name = ingredient2Name; 
			_ingredient2Index = ing2Index;
			_resultItem = resultItem; 
		} 
		
		public void addRelationshipToGame (IRelationshipVisitor visitor)
		{
			visitor.accept(this); 
		}
		
		public override string ToString ()
		{
			string returnVal = string.Format("[Combine Relationship: {0}#{1} and {2}#{3} form {4}]", _ingredient1Name, _ingredient1Index, _ingredient2Name, _ingredient2Index, _resultItem.ToString()); 
			return returnVal; 
		}
		
	}
}
