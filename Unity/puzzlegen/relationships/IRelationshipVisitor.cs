using UnityEngine;
using System.Collections;

namespace puzzlegen.relationship
{ 
	// Add more fields to this interface for additional relationships
	public interface IRelationshipVisitor {
		void accept(CombineRelationship rel); 
		void accept(PropertyChangeRelationship rel); 
		void accept(ItemRequestRelationship rel); 
		void accept(InsertionRelationship rel); 
		void accept(AreaConnectionRelationship rel); 
		void accept(StartAreaRelationship rel); 
	}
}