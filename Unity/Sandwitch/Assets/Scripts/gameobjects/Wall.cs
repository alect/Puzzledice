using UnityEngine;
using System.Collections;

public class Wall : GridPiece {
	
	public override void init ()
	{
		base.init ();
		// Just a wall, that's it.
		_type = WALL_TYPE; 
	}
	
}
