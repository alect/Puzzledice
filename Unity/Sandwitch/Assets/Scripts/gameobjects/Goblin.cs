using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

public class Goblin : GridPiece {
	
	protected const float MIN_TIME_BETWEEN_MOVES = 0.5f; 
	protected const float MAX_TIME_BETWEEN_MOVES = 3; 
	
	protected float _currentTimeBetweenMoves; 
	protected float _timeSinceLastMove; 
	
	public override void init ()
	{
		base.init ();
		
		_type = GridPiece.WALL_TYPE | GridPiece.ENEMY_TYPE; 
		
		_currentTimeBetweenMoves = Random.Range(MIN_TIME_BETWEEN_MOVES, MAX_TIME_BETWEEN_MOVES); 
		_timeSinceLastMove = 0; 
	}
	
	// Update is called once per frame
	public override void Update () {
		base.Update(); 
		_timeSinceLastMove += Time.deltaTime; 
	}
	
	
	public override void performTurn ()
	{
		// Only move once in a while. 
		if (_timeSinceLastMove < _currentTimeBetweenMoves) 
			return; 
		
		// Figure out which points we can move to 
		List<Vector2> possiblePoints = new List<Vector2>(); 
		foreach (uint dir in new uint[] { UP, RIGHT, DOWN, LEFT }) { 
			Vector2 point = pointFromDir(_gridPos, dir); 
			bool validPoint = PlayState.instance.inGrid(point) && point.x > 0 && point.x < Globals.ROOM_WIDTH-1 
				&& point.y > 0 && point.y < Globals.ROOM_HEIGHT-1; 
			foreach (GridPiece inhabitant in PlayState.instance.currentGridInhabitants(point)) { 
				if (inhabitant.hasType(WALL_TYPE) && !inhabitant.hasType(PLAYER_TYPE))
					validPoint = false; 
			} 
			foreach (GridPiece inhabitant in PlayState.instance.claimedGridInhabitants(point)) { 
				if (inhabitant.hasType(WALL_TYPE) && !inhabitant.hasType(PLAYER_TYPE))
					validPoint = false; 
			} 
			if (validPoint) 
				possiblePoints.Add(point); 
		}
		if (possiblePoints.Count == 0)
			return; 
		
		_nextPoint = Globals.getRandom(possiblePoints); 
		
		_timeSinceLastMove = 0; 
		_currentTimeBetweenMoves = Random.Range(MAX_TIME_BETWEEN_MOVES, MAX_TIME_BETWEEN_MOVES); 
	}
	
}
