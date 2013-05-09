using UnityEngine;
using System.Collections;

namespace puzzlegen.relationship
{ 

	public class StartAreaRelationship : AreaConnectionRelationship 
	{
		protected string _areaName; 
		public string areaName { 
			get { return _areaName; } 	
		} 
		
		public StartAreaRelationship(string areaName) 
		{ 
			_areaName = areaName; 	
		} 
		
		public override void addRelationshipToGame (IRelationshipVisitor visitor)
		{
			visitor.accept(this);
		}
		
		public override string ToString ()
		{
			return string.Format ("[StartAreaRelationship: {0} is the start area]", _areaName);
		}
	}
}
