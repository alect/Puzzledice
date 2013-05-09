using UnityEngine;
using System.Collections;

/// <summary>
/// An individual object in the game. Can move between tiles. 
/// </summary>
[RequireComponent(typeof(tk2dSprite))]
public class GridPiece : MonoBehaviour 
{
	public const uint WALL_TYPE = 0x1; 
	public const uint NPC_TYPE = 0x2; 
	public const uint CARRYABLE_TYPE = 0x4; 
	public const uint PLAYER_TYPE = 0x8; 
	public const uint ENEMY_TYPE = 0x10; 
	
	// For checking directions 
	public const uint LEFT = 0x0001; 
	public const uint RIGHT = 0x0010; 
	public const uint UP = 0x0100; 
	public const uint DOWN = 0x1000; 
	
	// If the game is paused. Gives us more control than setting timeScale to 0
	public static bool paused = false; 
	
	// Assuming you're using 2dToolkit. 
	// replace this object with whatever rendering component you want 
	protected tk2dSprite _sprite; 
	// Additionally for the text, replace with whatever text component you need 
	protected tk2dTextMesh _text; 
	protected float _timeSinceTextStart; 
	protected float _textDuration; 
	
	protected Vector2 _velocity; 
	
	protected Vector2 _currentMovementTarget; 
	protected float _pathSpeed; 
	
	// The position in the current grid (might correspond to an individual room)
	protected Vector2 _gridPos; 
	public Vector2 gridPos { 
		get { return _gridPos; }
		set { _gridPos = value; } 
	} 
	
	protected Vector2 _nextPoint; 
	public Vector2 nextPoint { 
		get { return _nextPoint; } 
		set { _nextPoint = value; } 
	} 
	
	public bool moving { 
		get { return _pathSpeed != 0; }	
	} 
	
	protected bool _alive = true; 
	public bool alive { 
		get { return _alive; } 
		set { _alive = value; } 
	}
	
	// The bitwise type of this object. 
	// Types can be ored together
	protected uint _type; 
	public uint type { 
		get { return _type; } 	
	}
	public bool hasType(uint testType) 
	{ 
		return (_type & testType) != 0; 
	} 
	
	// Whether this piece has performed its turn 
	// in this iteration of the turns 
	protected bool _turnPerformed; 
	public bool turnPerformed { 
		get { return _turnPerformed; } 
		set { _turnPerformed = value; } 
	} 
	
	// Easy access to 2D coordinates 
	public float x { 
		get { return transform.localPosition.x; } 
		set { transform.localPosition = new Vector3(value, transform.localPosition.y, transform.localPosition.z); } 
	} 
	public float y { 
		get { return transform.localPosition.y; } 
		set { transform.localPosition = new Vector3(transform.localPosition.x, value, transform.localPosition.z); } 
	}
	public float alpha { 
		get { return _sprite.color.a; }	
		set { _sprite.color = new Color(_sprite.color.r, _sprite.color.g, _sprite.color.b, value); }
	} 
	
	public bool textActive { 
		get { return _text != null && _text.gameObject.activeInHierarchy; } 	
	} 
	
	// Since we don't have full control over when Unity calls "Start", 
	// useful to have a manually called "init" function 
	public virtual void init() 
	{ 	
		_sprite = GetComponent<tk2dSprite>(); 
		_text = GetComponentInChildren<tk2dTextMesh>(); 
		
		_gridPos = PlayState.instance.toGridCoordinates(x, y); 
		_nextPoint = _gridPos; 
	} 
	
	public void resetGridPos() 
	{ 
		_gridPos = PlayState.instance.toGridCoordinates(x, y); 
		_nextPoint = _gridPos; 
	} 

	public virtual void activateText(string text, float duration) 
	{ 	
		// Have the piece "talk" if necessary
		if (!_text.gameObject.activeInHierarchy && !hasType(PLAYER_TYPE)) { 
			PlayState.instance.playAudio(PlayState.instance.talkClip); 
		} 
		
		// Figure out the positioning of our text
		float textX = gridPos.x; 
		if (textX < 2) 
			textX = 2; 
		if (textX > 13) 
			textX = 13; 
		float textY = gridPos.y+1; 
		float textYOffset = PlayState.instance.gridY-Globals.CELL_SIZE/2; 
		_text.anchor = TextAnchor.LowerCenter; 
		if (textY > Globals.ROOM_HEIGHT-2) { 
			textY = gridPos.y-1;
			textYOffset = PlayState.instance.gridY+Globals.CELL_SIZE/2; 
			_text.anchor = TextAnchor.UpperCenter; 
		}
		Vector2 actualPos = PlayState.instance.toActualCoordinates(new Vector2(textX, textY)); 
		_text.transform.position = new Vector3(actualPos.x, actualPos.y+textYOffset, _text.transform.position.z); 
		
		_text.text = text; 
		_text.Commit();
		_textDuration = duration; 
		_timeSinceTextStart = 0; 
		_text.gameObject.SetActive(true); 
	} 
	
	public void activateText(string text) 
	{ 
		activateText(text, Globals.TEXT_DURATION); 	
	} 
	
	
	// Functions relating to our turn
	
	// Called at start of grid before turns begin
	public virtual void preStart()
	{ 
	} 
	
	// Called just before turns begin 
	public virtual void preTurn()
	{ 
		_gridPos = PlayState.instance.toGridCoordinates(x, y); 
	} 
	
	// Called just after turns happen 
	public virtual void postTurn()
	{ 
	} 
	
	// Whether this piece is ready for the next turn. Useful for synchronization between pieces
	public virtual bool isTurnReady()
	{ 
		// By default, ready for our next turn once we're in position
		return _pathSpeed == 0;
	} 
	
	// The actual turn function, the true meat of any turn based tile game 
	public virtual void performTurn() 
	{ 
		
	} 
	
	public virtual void die() 
	{ 
		_alive = false; 
		// Let the grid manager handle destroying our object to avoid null references
		PlayState.instance.removeFromGame(this); 
	} 
	
	protected virtual void updateText() 
	{ 
		if (_text != null && _text.gameObject.activeInHierarchy) { 
			_timeSinceTextStart += Time.deltaTime; 
			if (_timeSinceTextStart >= _textDuration) 
				_text.gameObject.SetActive(false); 
		} 
	} 
	
	// Movement functions 
	protected void updateMotion()
	{ 
		float delta = _velocity.x*Time.deltaTime; 
		x += delta; 
		delta = _velocity.y*Time.deltaTime; 
		y += delta; 
	} 
	
	protected void updatePointMovement() 
	{ 	
		float pointX = x; 
		float pointY = y; 
		float deltaX = _currentMovementTarget.x - pointX; 
		float deltaY = _currentMovementTarget.y - pointY; 
		
		float velocityX, velocityY; 
		
		if (Mathf.Sqrt(deltaX*deltaX + deltaY*deltaY) < _pathSpeed*Time.deltaTime) 
			_pathSpeed = 0; 
		else if (_pathSpeed != 0) { 
			if (pointX != _currentMovementTarget.x)
				velocityX = (pointX < _currentMovementTarget.x) ? _pathSpeed : -_pathSpeed; 
			else 
				velocityX = 0; 
			if (pointY != _currentMovementTarget.y) 
				velocityY = (pointY < _currentMovementTarget.y) ? _pathSpeed : -_pathSpeed; 
			else 
				velocityY = 0; 
			_velocity = new Vector2(velocityX, velocityY); 
		}
	} 
	
	public void moveToPoint(Vector2 point, float speed)
	{ 
		_currentMovementTarget = point; 
		_pathSpeed = speed; 
	} 
	
	public virtual void Update() 
	{ 
		if (paused)
			return; 
		
		updateText();
		updatePointMovement(); 
		
		if (_pathSpeed == 0) { 
			Vector2 gridPos = PlayState.instance.toGridCoordinates(x, y); 
			Vector2 actualPos = PlayState.instance.toActualCoordinates(gridPos); 
			x = actualPos.x;
			y = actualPos.y; 
			_velocity = Vector2.zero; 
		} 
		
		updateMotion(); 
	} 
	
	
	// Useful utility functions 
	public static Vector2 pointFromDir(Vector2 point, uint dir) 
	{
		switch(dir) { 
		case UP:
			return new Vector2(point.x, point.y+1); 
		case RIGHT:
			return new Vector2(point.x+1, point.y); 
		case DOWN:
			return new Vector2(point.x, point.y-1); 
		case LEFT: 
			return new Vector2(point.x-1, point.y); 
		default:
			return point; 
		}
	}
	
}
