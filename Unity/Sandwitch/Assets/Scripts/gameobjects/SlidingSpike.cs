using UnityEngine;
using System.Collections;

public class SlidingSpike : GridPiece 
{
	
	protected bool _sliding; 
	protected uint _slidingDirection; 
	
	public override void init ()
	{
		base.init ();
		
		_type = GridPiece.WALL_TYPE | GridPiece.ENEMY_TYPE; 
	}
	
	public override void performTurn ()
	{
		if (_sliding) { 
			// If we're sliding, keep sliding. 
			Vector2 maybeNextPoint = pointFromDir(_gridPos, _slidingDirection); 
			// Can't move onto the edges of the room.
			bool blocked = !PlayState.instance.inGrid(maybeNextPoint) || maybeNextPoint.x == 0 || maybeNextPoint.x == Globals.ROOM_WIDTH-1 || maybeNextPoint.y == 0 || maybeNextPoint.y == Globals.ROOM_HEIGHT-1; 
			foreach (GridPiece inhabitant in PlayState.instance.currentGridInhabitants(maybeNextPoint)) { 
				if (inhabitant.hasType(GridPiece.WALL_TYPE) && !inhabitant.hasType(GridPiece.PLAYER_TYPE))
					blocked = true; 
			} 
			foreach (GridPiece inhabitant in PlayState.instance.claimedGridInhabitants(maybeNextPoint)) { 
				if (inhabitant.hasType(GridPiece.WALL_TYPE) && !inhabitant.hasType(GridPiece.PLAYER_TYPE))
					blocked = true; 
			} 
			if (blocked)
				_sliding = false; 
			else 
				_nextPoint = maybeNextPoint; 
		} 
		else { 
			// Check to see if the player is in our view 	
			Player player = PlayState.instance.player; 
			if (player.gridPos.x == _gridPos.x) { 
				_sliding = true; 
				if (player.gridPos.y > _gridPos.y)
					_slidingDirection = UP; 
				else 
					_slidingDirection = DOWN; 	
			}
			else if (player.gridPos.y == _gridPos.y) { 
				_sliding = true; 
				if (player.gridPos.x > _gridPos.x)
					_slidingDirection = RIGHT; 
				else 
					_slidingDirection = LEFT; 
			} 
			
			
		}
	}
}
