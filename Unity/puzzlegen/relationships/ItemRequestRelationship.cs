using UnityEngine;
using System.Collections;
using puzzlegen.relationship; 

namespace puzzlegen.relationship
{ 
	public class ItemRequestRelationship : IRelationship
	{
		protected string _requesterName, _requestedName, _requestedPropertyName; 
		protected int _requesterIndex, _requestedIndex; 
		
		protected object _requestedPropertyVal; 
		protected PuzzleItem _rewardItem; 
		public string requesterName { 
			get { return _requesterName; } 	
		} 
		public int requesterIndex { 
			get { return _requesterIndex; } 	
		} 
		public string requestedName { 
			get { return _requestedName; } 	
		} 
		public int requestedIndex { 
			get { return _requestedIndex; } 	
		} 
		public string requestedPropertyName { 
			get { return _requestedPropertyName; } 	
		} 
		public object requestedPropertyValue { 
			get { return _requestedPropertyVal; } 	
		} 
		public PuzzleItem rewardItem { 
			get { return _rewardItem; } 	
		} 
		
		public ItemRequestRelationship(string requesterName, int requesterIndex, string requestedName, int requestedIndex, PuzzleItem rewardItem, string propertyName, object propertyVal)
		{
			_requesterName = requesterName; 
			_requesterIndex = requesterIndex; 
			_requestedName = requestedName; 
			_requestedIndex = requestedIndex; 
			_rewardItem = rewardItem; 
			_requestedPropertyName = propertyName; 
			_requestedPropertyVal = propertyVal; 
		}
		
		public void addRelationshipToGame(IRelationshipVisitor visitor)
		{
			visitor.accept(this); 
		}
		
		public override string ToString ()
		{
			if (_requestedName == null)
				return string.Format("[ItemRequestRelationship: {0}#{1} requests {2}#{3} and gives {4} as reward]", _requesterName, _requesterIndex, 
					_requestedName, _requestedIndex, _rewardItem.ToString());
			else 
				return string.Format("[ItemRequestRelationship: {0}#{1} requests {2}#{3} with property ({4}, {5}) and gives {6} as reward]", _requesterName, _requesterIndex,
					_requestedName, _requestedIndex, _requestedPropertyName, _requestedPropertyVal, _rewardItem); 
		}
		
	}
}
