using UnityEngine;
using System.Collections;

namespace puzzlegen.relationship { 
	public interface IRelationship
	{
		void addRelationshipToGame(IRelationshipVisitor visitor); 
	}
}
