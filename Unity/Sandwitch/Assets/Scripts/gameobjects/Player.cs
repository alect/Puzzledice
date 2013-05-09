using UnityEngine;
using System.Collections;

public class Player : GridPiece 
{
	
	// Audio to play when we get hit by an enemy 
	public AudioClip hurtClip, walkClip; 
	
	protected bool _upPressed, _rightPressed, _downPressed, _leftPressed; 
	
	protected tk2dAnimatedSprite _animSprite; 
	
	// For flashing the sprite when we're damaged. 
	protected const float FLASH_TIME = 1; 
	protected float _timeSinceFlashStart = 0; 
	protected const int FLASH_FRAMES = 3; 
	protected int _flashCounter = 0; 
	protected bool _flashing = false; 
	
	protected Renderer _renderer; 
	
	// For when we need to respawn at the start of the room (hit by an enemy)
	protected Vector2 _roomStartPos; 
	public Vector2 roomStartPos { 
		get { return _roomStartPos; } 
		set { _roomStartPos = value; } 
	} 
	
	
	public override void init ()
	{
		base.init ();
		_renderer = GetComponent<Renderer>(); 
		_animSprite = _sprite as tk2dAnimatedSprite; 
		_type = GridPiece.WALL_TYPE | GridPiece.NPC_TYPE | GridPiece.PLAYER_TYPE;
	}
	
	// Update is called once per frame
	public override void Update () {
		base.Update(); 
		
		if (Input.GetKeyDown(KeyCode.UpArrow) || Input.GetKeyDown(KeyCode.W))
			_upPressed = true; 
		if (Input.GetKeyDown(KeyCode.RightArrow) || Input.GetKeyDown(KeyCode.D))
			_rightPressed = true; 
		if (Input.GetKeyDown(KeyCode.DownArrow) || Input.GetKeyDown(KeyCode.S))
			_downPressed = true; 
		if (Input.GetKeyDown(KeyCode.LeftArrow) || Input.GetKeyDown(KeyCode.A))
			_leftPressed = true; 
		
		// Handle flashing
		if (_flashing) { 
			_flashCounter = (_flashCounter + 1) % FLASH_FRAMES; 
			if (_flashCounter == 0) 
				_renderer.enabled = !_renderer.enabled; 
			
			_timeSinceFlashStart += Time.deltaTime; 
			if (_timeSinceFlashStart >= FLASH_TIME) { 
				_flashing = false; 
				_renderer.enabled = true; 
			} 
		} 
		
	}
	
	protected override void updateText ()
	{
		// Player should only show text when NPCs aren't talking
		if (_text != null && _text.gameObject.activeInHierarchy) { 
			Renderer textRender = _text.GetComponent<Renderer>(); 
			if (PlayState.instance.npcTextShowing) { 
				textRender.enabled = false;
			} 
			else { 
				textRender.enabled = true;
				_timeSinceTextStart += Time.deltaTime; 
				if (_timeSinceTextStart >= _textDuration) 
					_text.gameObject.SetActive(false); 
			}
		} 
	}
	
	protected void startFlashing() 
	{ 
		_flashing = true; 
		_flashCounter = 0; 
		_timeSinceFlashStart = 0; 
	} 
	
	protected bool holdingKey(KeyCode key)
	{ 
		return Input.GetKey(key) && !Input.GetKeyDown(key); 	
	} 
	
	public override bool isTurnReady ()
	{
		if (!base.isTurnReady())	
			return false; 
		
		if (holdingKey(KeyCode.UpArrow) || holdingKey(KeyCode.W))
			_upPressed = true; 
		if (holdingKey(KeyCode.RightArrow) || holdingKey(KeyCode.D))
			_rightPressed = true; 
		if (holdingKey(KeyCode.DownArrow) || holdingKey(KeyCode.S))
			_downPressed = true; 
		if (holdingKey(KeyCode.LeftArrow) || holdingKey(KeyCode.A))
			_leftPressed = true; 
		
		return base.isTurnReady();
	}
	
	public override void performTurn ()
	{
		
		// See if we're on top of an enemy. 
		if (!_flashing) { 
		
			foreach (GridPiece inhabitant in PlayState.instance.currentGridInhabitants(_gridPos)) { 
				if (inhabitant.hasType(GridPiece.ENEMY_TYPE)) { 
					Vector2 actualPos = PlayState.instance.toActualCoordinates(_roomStartPos); 
					x = actualPos.x; 
					y = actualPos.y; 
					_gridPos = PlayState.instance.toGridCoordinates(x, y); 
					_nextPoint = _gridPos; 
					
					_upPressed = false; 
					_rightPressed = false; 
					_downPressed = false; 
						_leftPressed = false; 
					
					startFlashing(); 
					PlayState.instance.playAudio(hurtClip); 
					
					return; 
				} 
			} 
		}
		
		
		
		// Try each of our pressed directions in order 
		bool moved = false; 
		if (_upPressed && tryMove(UP))
			moved = true;
		else if (_rightPressed && tryMove(RIGHT)) {
			moved = true; 
			_sprite.scale = new Vector3(Mathf.Abs(_sprite.scale.x), _sprite.scale.y, _sprite.scale.z); 	
		}
		else if (_downPressed && tryMove(DOWN))
			moved = true; 
		else if (_leftPressed && tryMove(LEFT)) {
			moved = true; 
			_sprite.scale = new Vector3(-Mathf.Abs(_sprite.scale.x), _sprite.scale.y, _sprite.scale.z); 
		}
		
		if (moved) { 
			if (_animSprite.CurrentClip.name != "walk")
				_animSprite.Play("walk"); 
			PlayState.instance.playAudio(walkClip);
		}
		else 
			_animSprite.Play("idle"); 
		
		_upPressed = false; 
		_rightPressed = false; 
		_downPressed = false; 
		_leftPressed = false;
	}
	
	protected bool tryMove(uint direction) 
	{ 
		Vector2 maybeNextPoint = pointFromDir(_gridPos, direction); 
		// Don't move into walls
		foreach (GridPiece inhabitant in PlayState.instance.currentGridInhabitants(maybeNextPoint)) { 
			// If it's an NPC, activate their text
			if (inhabitant.hasType(GridPiece.NPC_TYPE))
				(inhabitant as SpawnedPuzzleItem).activateNpcText(); 
			if (inhabitant.hasType(GridPiece.WALL_TYPE) && !inhabitant.hasType(GridPiece.ENEMY_TYPE))
				return false; 
		} 
		foreach (GridPiece inhabitant in PlayState.instance.claimedGridInhabitants(maybeNextPoint)) { 
			if (inhabitant.hasType(GridPiece.WALL_TYPE) && !inhabitant.hasType(GridPiece.ENEMY_TYPE))
				return false; 
		}
		// Otherwise, success. 
		_nextPoint = maybeNextPoint; 
		return true; 
	} 
	
	
}
