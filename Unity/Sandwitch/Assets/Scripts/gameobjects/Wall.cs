using UnityEngine;
using System.Collections;

public class Wall : GridPiece {
	
	public override void init ()
	{
		base.init ();
		_type = WALL_TYPE; 
	}
	
}
