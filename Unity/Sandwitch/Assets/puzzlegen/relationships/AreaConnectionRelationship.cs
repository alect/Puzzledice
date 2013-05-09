using UnityEngine;
using System.Collections;

namespace puzzlegen.relationship 
{ 

	public class AreaConnectionRelationship : IRelationship
	{
		protected string _firstAreaName, _secondAreaName;
		public string firstAreaName { 
			get { return _firstAreaName; } 	
		} 
		public string secondAreaName { 
			get { return _secondAreaName; } 	
		}
		
		protected bool _locked; 
		public bool locked { 
			get { return _locked; } 	
		} 
		
		protected string _keyName;
		public string keyName { 
			get { return _keyName; } 	
		} 
		protected int _keyIndex;
		public int keyIndex {
			get { return _keyIndex; } 	
		}
		
		
		public AreaConnectionRelationship(string firstAreaName, string secondAreaName, bool locked, string keyName, int keyIndex)
		{ 
			_firstAreaName = firstAreaName; 
			_secondAreaName = secondAreaName; 
			_locked = locked; 
			_keyName = keyName;
			_keyIndex = keyIndex;
		} 
		
		public AreaConnectionRelationship(string firstAreaName, string secondAreaName) : this(firstAreaName, secondAreaName, false, null, 0)
		{ 
		} 
		public AreaConnectionRelationship() : this(null, null, false, null, 0)
		{
		}
		
		public virtual void addRelationshipToGame (IRelationshipVisitor visitor)
		{
			visitor.accept(this);
		}
		
		public override string ToString ()
		{
			if (!_locked)
				return string.Format("[AreaConnectionRelationship: {0} is connected to {1}]", _firstAreaName, _secondAreaName);
			else 
				return string.Format("[AreaConnectionRelationship: {0} is connected to {1} with a lock that can be opened by {2}#{3}]", _firstAreaName, _secondAreaName, _keyName, _keyIndex); 
		}
		
	}
}
