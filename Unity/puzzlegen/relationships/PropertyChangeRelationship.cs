using UnityEngine;
using System.Collections;

namespace puzzlegen.relationship 
{

	public class PropertyChangeRelationship : IRelationship
	{
		protected string _changerName, _changeeName, _propertyName;
		protected int _changerIndex, _changeeIndex; 
		public string changerName { 
			get { return _changerName; } 	
		} 
		public int changerIndex { 
			get { return _changerIndex; } 	
		} 
		public string changeeName { 
			get { return _changeeName; } 	
		} 
		public int changeeIndex { 
			get { return _changeeIndex; } 	
		} 
		public string propertyName { 
			get { return _propertyName; } 	
		} 
		
		protected object _propertyVal;
		public object propertyVal { 
			get { return _propertyVal; } 	
		} 
		
		public PropertyChangeRelationship(string changeeName, int changeeIndex, string changerName, int changerIndex, string propertyName, object propertyVal)
		{ 
			_changeeName = changeeName; 
			_changerName = changerName; 
			_propertyName = propertyName; 
			_propertyVal = propertyVal; 
		} 
		
		public void addRelationshipToGame (IRelationshipVisitor visitor)
		{
			visitor.accept(this);
		}
		
		public override string ToString ()
		{
			return string.Format ("[PropertyChangeRelationship: {0}#{1} can be used to change the {2} property of {3}#{4} to {5}]", _changerName, _changerIndex, 
				_propertyName, _changeeName, _changeeIndex, _propertyVal.ToString());
		}

	}
	
}
