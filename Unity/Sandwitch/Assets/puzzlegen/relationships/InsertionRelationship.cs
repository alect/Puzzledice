using UnityEngine;
using System.Collections;

namespace puzzlegen.relationship 
{ 

	public class InsertionRelationship : IRelationship 
	{
		protected string _itemToInsertName; 
		public string fillerName { 
			get { return _itemToInsertName; } 	
		} 
		protected int _itemToInsertIndex; 
		public int itemToInsertIndex { 
			get { return _itemToInsertIndex; } 	
		} 
		protected string _containerName; 
		public string containerName { 
			get { return _containerName; } 	
		} 
		protected int _containerIndex; 
		public int containerIndex { 
			get { return _containerIndex; } 	
		} 
		
		public InsertionRelationship(string itemToInsertName, int itemToInsertIndex, string containerName, int containerIndex) 
		{ 
			_itemToInsertName = itemToInsertName; 
			_itemToInsertIndex = itemToInsertIndex; 
			_containerName = containerName; 
			_containerIndex = containerIndex; 
		} 
		
		public void addRelationshipToGame (IRelationshipVisitor visitor)
		{
			visitor.accept(this);
		}
		
		public override string ToString ()
		{
			return string.Format ("[InsertionRelationship: {0}#{1} can be inserted into {2}#{3}]", _itemToInsertName, _itemToInsertIndex, _containerName, _containerIndex);
		}
	}

}