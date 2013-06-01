using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen.relationship; 

public class PlayState : MonoBehaviour {
	
	// Place spawnable prefabs here 
	public GameObject playerPrefab, wallPrefab, ladderPrefab, puzzleItemPrefab, goblinPrefab, spikePrefab; 
	
	// For playing global audio 
	public AudioClip pickupClip, talkClip, lockClip; 
	
	// The popup text for item descriptions
	public GameObject popupTextPrefab; 
	
	private enum GameState { 
		PAUSE_STATE, 
		RUN_STATE,
	} 
	private GameState _currentState = GameState.RUN_STATE; 
	
	// Singleton pattern 
	private static PlayState _instance; 
	public static PlayState instance { 
		get { return _instance; }	
	} 
	
	// For generating the same level again on a reset 
	public static bool seedGenerate = false; 
	private static int _currentSeed; 
	
	
	// The Inventory 
	private Inventory _inventory; 
	public Inventory inventory { 
		get { return _inventory; } 	
	} 
	
	// The generate game 
	private ProcGame _currentGame; 
	
	// Player and room information 
	private Player _player; 
	public Player player { 
		get { return _player; } 	
	} 
	
	private Room _currentRoom; 
	
	private tk2dSprite _floorSprite; 
	
	private tk2dTextMesh _popupText; 
	
	private AudioSource _audioSource; 
	
	// Grid variables (might need to make these correspond to different rooms if possible
	private List<GridPiece> _gridPieces = new List<GridPiece>(); 
	// Current grid accessible to every piece while they're making decisions 
	private List<GridPiece>[,] _currentGrid; 
	// The claimed grid for recording claimed positions during a turn 
	private List<GridPiece>[,] _claimedGrid; 
	// The pieces that will be removed at the end of the update 
	private List<GridPiece> _piecesToRemove; 
	private List<GridPiece> _piecesToDestroy; 
	
	public int gridX { 
		get { return (int)transform.position.x; }	
	}
	public int gridY { 
		get { return (int)transform.position.y; }	
	} 
	
	private int _gridWidth, _gridHeight; 
	public int gridWidth { 
		get { return _gridWidth; } 
		set { _gridWidth = value; } 
	} 
	public int gridHeight { 
		get { return _gridHeight; } 
		set { _gridHeight = value; } 
	} 
	
	protected bool _hasSandwich = false; 
	public void sandwichGet() 
	{ 
		_hasSandwich = true; 
	} 
	
	protected bool _npcTextShowing = false; 
	public bool npcTextShowing { 
		get { return _npcTextShowing; } 
		set { _npcTextShowing = value; } 
	} 
		
	protected bool _shouldHidePopupText = true; 
	public void setPopupText(string text, Vector2 textGridPos)
	{ 
		_shouldHidePopupText = false;
		float textX = textGridPos.x; 
		if (textX < 2)
			textX = 2; 
		if (textX > 13)
			textX = 13; 
		float textY = textGridPos.y+1; 
		_popupText.anchor = TextAnchor.LowerCenter; 
		if (textY > Globals.ROOM_HEIGHT-2) { 
			textY = textGridPos.y;
			_popupText.anchor = TextAnchor.UpperCenter; 
		}
		Vector2 actualPos = toActualCoordinates(new Vector2(textX, textY)); 
		_popupText.transform.localPosition = new Vector3(actualPos.x, actualPos.y-Globals.CELL_SIZE/2, _popupText.transform.localPosition.z); 
		_popupText.text = text; 
		_popupText.Commit(); 
		_popupText.gameObject.SetActive(true); 
	} 
	
	// For drawing dialogue next to the mouse 
	public void setMousePopupText(string text, Vector2 mousePos) 
	{ 
		_shouldHidePopupText = false; 
		
		float textX = mousePos.x; 
		if (textX < 2*Globals.CELL_SIZE)
			textX = 2*Globals.CELL_SIZE; 
		if (textX > 14*Globals.CELL_SIZE)
			textX = 14*Globals.CELL_SIZE; 
		float textY = mousePos.y+Globals.CELL_SIZE/2; 
		_popupText.anchor = TextAnchor.LowerCenter; 
		if (textY > Globals.CELL_SIZE*(Globals.ROOM_HEIGHT-1)+gridY) { 
			_popupText.anchor = TextAnchor.LowerCenter; 	
			textY = Globals.CELL_SIZE*(Globals.ROOM_HEIGHT-1)+gridY; 
		} 
		_popupText.transform.position = new Vector3(textX, textY, _popupText.transform.position.z); 
		_popupText.text = text; 
		_popupText.Commit(); 
		_popupText.gameObject.SetActive(true); 
	} 
	
	// For drawing temporary text on the screen 
	public void addTempText(string text, Vector2 textGridPos, float duration)
	{ 
		GameObject textObj = GameObject.Instantiate(popupTextPrefab) as GameObject; 
		textObj.transform.parent = transform;
		tk2dTextMesh textToAdd = textObj.GetComponent<tk2dTextMesh>(); 
		TemporaryText tempText = textObj.AddComponent<TemporaryText>(); 
		tempText.timeVisible = duration; 
		float textX = textGridPos.x; 
		if (textX < 2)
			textX = 2; 
		if (textX > 13)
			textX = 13; 
		float textY = textGridPos.y+1; 
		_popupText.anchor = TextAnchor.LowerCenter; 
		if (textY > Globals.ROOM_HEIGHT-2) { 
			textY = textGridPos.y;
			_popupText.anchor = TextAnchor.UpperCenter; 
		}
		Vector2 actualPos = toActualCoordinates(new Vector2(textX, textY)); 
		textToAdd.transform.localPosition = new Vector3(actualPos.x, actualPos.y-Globals.CELL_SIZE/2, textToAdd.transform.localPosition.z); 
		textToAdd.text = text; 
		textToAdd.Commit(); 
		textToAdd.gameObject.SetActive(true);
	}
	
	public void addPlayerText(string text) 
	{ 
		_player.activateText(text, 2); 
	} 
	
	protected SpawnedPuzzleItem _highlightedItem; 
	public void setHighlightedItem(SpawnedPuzzleItem item) 
	{ 
		_highlightedItem = item; 
	} 
	
	public void playAudio(AudioClip clip) 
	{ 
		_audioSource.PlayOneShot(clip); 
	} 
	
	
	// Use this for initialization
	void Start () 
	{
		// If we're generating from a seed 
		if (seedGenerate) 
			Random.seed = _currentSeed; 
		_currentSeed = Random.seed; 
		Random.seed = _currentSeed; 
		
		_instance = this; 
		
		_inventory = (GameObject.Find("inventory") as GameObject).GetComponent<Inventory>(); 
		
		_audioSource = GetComponent<AudioSource>(); 
		
		_floorSprite = GetComponentInChildren<tk2dSprite>(); 
		
		_popupText = (Instantiate(popupTextPrefab) as GameObject).GetComponent<tk2dTextMesh>();
		_popupText.transform.parent = transform;
		_popupText.gameObject.SetActive(false); 
		
		_piecesToRemove = new List<GridPiece>(); 
		_piecesToDestroy = new List<GridPiece>(); 
		
		_gridWidth = Globals.ROOM_WIDTH; 
		_gridHeight = Globals.ROOM_HEIGHT; 
		
		
		_currentGame = new ProcGame(); 
		
		_currentRoom = _currentGame.startRoom; 
		_player = _currentGame.player;
		_floorSprite.spriteId = _floorSprite.GetSpriteIdByName(_currentRoom.floorTilesName); 
		_gridPieces = _currentRoom.gridPieces; 
		foreach (GridPiece piece in _gridPieces) { 
			piece.gameObject.SetActive(true);	
		}
		
		// Begin the game
		addPlayerText("Hungry...."); 
	}
	
	// Update is called once per frame
	void Update () 
	{
		
		// Check if we've won yet 
		if (_hasSandwich && !_player.textActive) { 
			MainMenuState.winState = true; 
			Application.LoadLevel("TitleScene"); 
		} 
		else if (Input.GetKeyDown(KeyCode.Escape)) { 
			MainMenuState.winState = false; 
			Application.LoadLevel("TitleScene"); 
		} 
		
		// If we want to reset the level 
		if (Input.GetKeyDown(KeyCode.R)) { 
			seedGenerate = true; 
			Application.LoadLevel("PlayScene"); 
		} 
		
		// remove or destroy any pieces that need to be removed before we begin
		foreach (GridPiece piece in _piecesToRemove) { 
			if (piece != null) { 
				_gridPieces.Remove(piece); 
			} 
		}
		_piecesToRemove.Clear(); 
		foreach (GridPiece piece in _piecesToDestroy) { 
			if (piece != null) 
				Destroy(piece.gameObject); 
		} 
		_piecesToDestroy.Clear(); 
		
		if (_shouldHidePopupText) 
			_popupText.gameObject.SetActive(false); 
		_shouldHidePopupText = true; 
		
		Vector3 mouseActualPos = tk2dCamera.inst.mainCamera.ScreenToWorldPoint(Input.mousePosition); 
		Vector2 mousePos = new Vector2(mouseActualPos.x, mouseActualPos.y); 
		Vector2 mouseGridPos = PlayState.instance.toGridCoordinates(mouseActualPos.x-PlayState.instance.gridX, mouseActualPos.y-PlayState.instance.gridY);
		// Check to see if we have a selected item
		if (_inventory.selectedSlot != null) { 
			 
			string useText = string.Format("Use {0} with", _inventory.selectedSlot.item.itemName); 
			if (_inventory.selectedSlot.item.insideItem && _highlightedItem == null && _inventory.hoveredSlot == null) { 
				if (inGrid(mouseGridPos) && currentGridInhabitants(mouseGridPos).Count == 0) { 
					useText = string.Format("Remove {0} from {1}", _inventory.selectedSlot.item.itemName, _inventory.selectedSlot.item.parentItem.itemName); 
				} 
			}
			else if (_inventory.selectedSlot != null && _highlightedItem == null && _inventory.selectedSlot.item.insideItem && 
			_inventory.selectedSlot.item.carryable && _inventory.hoveredSlot.isEmpty) {
				useText = string.Format("Remove {0} from {1}", _inventory.selectedSlot.item.itemName, _inventory.selectedSlot.item.parentItem.itemName); 	
			}
			
			if (_highlightedItem != null)
				useText = string.Format("Use {0} with {1}", _inventory.selectedSlot.item.itemName, _highlightedItem.itemName); 
			
			setMousePopupText(useText, mousePos); 
		} 
		else if (_highlightedItem != null && !_highlightedItem.textActive) { 
			setPopupText(_highlightedItem.description(), _highlightedItem.gridPos); 
		}
		
		
		if (_inventory.selectedSlot != null && _highlightedItem != null && Input.GetMouseButtonDown(0)) { 
			useItemsTogether(_inventory.selectedSlot.item, _highlightedItem);	
		} 
		else if (_inventory.selectedSlot != null && _highlightedItem == null && _inventory.selectedSlot.item.insideItem && 
			_inventory.hoveredSlot == null && Input.GetMouseButtonDown(0)) { 
			// Check to see if we're over a grid position
			if (inGrid(mouseGridPos) && currentGridInhabitants(mouseGridPos).Count == 0) { 
				SpawnedPuzzleItem itemInContainer = _inventory.selectedSlot.item; 
				itemInContainer.removeFromOtherItem(); 
				itemInContainer.transform.parent = transform; 
				Vector2 actualPos = toActualCoordinates(mouseGridPos); 
				itemInContainer.transform.localScale = new Vector3(4, 4, 1); 
				itemInContainer.x = actualPos.x; 
				itemInContainer.y = actualPos.y;
				itemInContainer.gridPos = mouseGridPos; 
				itemInContainer.nextPoint = itemInContainer.gridPos; 
				itemInContainer.enabled = true; 
				_gridPieces.Add(itemInContainer); 
				_inventory.selectedSlot = null; 
			} 
		} 
		// Finally, removing into the inventory 
		else if (_inventory.selectedSlot != null && _highlightedItem == null && _inventory.selectedSlot.item.insideItem && 
			_inventory.selectedSlot.item.carryable && _inventory.hoveredSlot != null && _inventory.hoveredSlot.isEmpty && Input.GetMouseButtonDown(0)) { 
			SpawnedPuzzleItem itemInContainer = _inventory.selectedSlot.item; 
			itemInContainer.removeFromOtherItem(); 
			itemInContainer.transform.parent = transform; 
			itemInContainer.transform.localScale = new Vector3(4, 4, 1); 
			itemInContainer.addToInventory(); 
			_inventory.selectedSlot = null; 
		} 
		
		_highlightedItem = null; 
		
		if (_currentState == GameState.RUN_STATE) { 	
			
			// Before anything, check to see if we're at an exit 
			if (!_player.moving) {
				Vector2 playerGridPos = toGridCoordinates(_player.x, _player.y); 
				if (_currentRoom.checkForPlayerExit(playerGridPos))
					return; 
			}
			
			constructCurrentGrid(); 
			constructClaimedGrid(); 
			
			bool turnReady = true;  
			_npcTextShowing = false; 
			foreach (GridPiece piece in _gridPieces) { 
				if (!piece.isTurnReady())
					turnReady = false;
				if (!piece.hasType(GridPiece.PLAYER_TYPE) && piece.textActive) 
					_npcTextShowing = true; 
			} 
			// Perform the turn if we're ready for it 
			if (turnReady) {
				// First, a pre-turn 
				foreach (GridPiece piece in _gridPieces) { 
					piece.turnPerformed = false; 
					piece.preTurn(); 
				} 
				
				// Now the actual turn. Since pieces might be added to 
				// The piece list, using a normal for loop (rather than foreach)
				for (int i = 0; i < _gridPieces.Count; i++) { 
					GridPiece piece = _gridPieces[i]; 
					performPieceTurn(piece); 
				} 
				
				// Update movement 
				foreach (GridPiece piece in _gridPieces) { 
					performTurnMovement(piece); 	
				}
				
				// Finally, the post turn 
				foreach (GridPiece piece in _gridPieces) { 
					piece.postTurn(); 	
				}	
			} 
		}
		
	}
	
	public void performPieceTurn(GridPiece piece) 
	{ 
		if (!piece.turnPerformed) { 
			piece.turnPerformed = true; 
			piece.performTurn(); 
			if (inGrid(piece.nextPoint))
				_claimedGrid[(int)piece.nextPoint.x, (int)piece.nextPoint.y].Add(piece); 
		} 
	}
	
	private void performTurnMovement(GridPiece piece) 
	{ 
		if (piece.nextPoint != piece.gridPos) { 
			float moveSpeed = Globals.MOVE_SPEED; 
			Vector2 nextActualPoint = toActualCoordinates(piece.nextPoint); 
			piece.moveToPoint(nextActualPoint, moveSpeed); 
		} 
	}
	
	
	public void constructCurrentGrid() 
	{ 
		_currentGrid = new List<GridPiece>[_gridWidth, _gridHeight]; 
		for (int i = 0; i < _gridWidth; i++) { 
			for (int j = 0; j < _gridHeight; j++) { 
				_currentGrid[i, j] = new List<GridPiece>(); 	
			} 
		} 
		foreach (GridPiece piece in _gridPieces) { 
			 Vector2 gridPos = toGridCoordinates(piece.x, piece.y); 
			if (inGrid(gridPos))
				_currentGrid[(int)gridPos.x, (int)gridPos.y].Add(piece);
		} 
	}
	public void constructClaimedGrid() 
	{ 
		_claimedGrid = new List<GridPiece>[_gridWidth, _gridHeight]; 
		for (int i = 0; i < _gridWidth; i++) { 
			for (int j = 0; j < _gridHeight; j++) { 
				_claimedGrid[i, j] = new List<GridPiece>(); 	
			} 
		} 
	} 
	
	public List<GridPiece> currentGridInhabitants(Vector2 point) 
	{ 
		if (inGrid(point))
			return _currentGrid[(int)point.x, (int)point.y]; 
		else 
			return new List<GridPiece>(); 
	}
	
	public List<GridPiece> claimedGridInhabitants(Vector2 point)
	{ 
		if (inGrid(point))
			return _claimedGrid[(int)point.x, (int)point.y]; 
		else 
			return new List<GridPiece>(); 
	} 
	
	// Useful grid functions 
	public bool inGrid(Vector2 point) 
	{
		return point.x >= 0 && point.x < _gridWidth && point.y >= 0 && point.y < _gridHeight; 
	}
	
	public Vector2 toGridCoordinates(float x, float y)
	{ 
		return new Vector2(Mathf.Floor(x / Globals.CELL_SIZE), Mathf.Floor( y / Globals.CELL_SIZE)); 
	} 
	
	public Vector2 toActualCoordinates(Vector2 point)
	{ 
		return new Vector2(point.x*Globals.CELL_SIZE + Globals.CELL_SIZE/2, point.y*Globals.CELL_SIZE + Globals.CELL_SIZE/2); 
	} 
		
	public void removeFromGame(GridPiece item) 
	{ 
		_piecesToRemove.Add(item); 
		_piecesToDestroy.Add(item); 
	} 
	
	public void removeFromGrid(GridPiece item) 
	{ 
		_piecesToRemove.Add(item); 
	} 
	
	// Room switching code 
	public void switchRoom(Room newRoom, Vector2 newPlayerGridPos) 
	{ 
		// First up, remove the player from our current grid piece list 
		_gridPieces.Remove(_player); 
		Vector2 actualPos = toActualCoordinates(newPlayerGridPos); 
		_player.x = actualPos.x; 
		_player.y = actualPos.y; 
		_player.resetGridPos();
		_player.roomStartPos = newPlayerGridPos; 
		
		// Deactivate all the current room grid pieces
		foreach (GridPiece piece in _gridPieces) { 
			piece.gameObject.SetActive(false); 
		} 	
		
		// Switch!
		_currentRoom = newRoom; 
		_floorSprite.spriteId = _floorSprite.GetSpriteIdByName(newRoom.floorTilesName); 
		_gridPieces = newRoom.gridPieces; 
		_gridPieces.Add(_player); 
		
		// Reactivate all the pieces. 
		foreach (GridPiece piece in _gridPieces) { 
			piece.gameObject.SetActive(true); 	
		} 
	}
	
	
	public void useItemsTogether(SpawnedPuzzleItem item1, SpawnedPuzzleItem item2) 
	{ 
		Debug.Log(string.Format("Using {0} with {1}", item1.itemName, item2.itemName)); 
		IRelationship maybeRel = _currentGame.getRelationship(item1.itemName, item2.itemName); 
		if (maybeRel != null) { 
			// Create an executor for using these two items 
			RelationshipExecutor executor = new RelationshipExecutor(item1, item2, _currentRoom, _currentGame); 
			maybeRel.addRelationshipToGame(executor); 
		}
		else { 
			_player.activateText("\"I don't think I can use those together.\"", 2); 
		} 
		
		_inventory.selectedSlot = null; 
		
	} 
	
}
