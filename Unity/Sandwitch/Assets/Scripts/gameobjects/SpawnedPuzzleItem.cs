using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

public class SpawnedPuzzleItem : GridPiece {
	
	public AudioClip pickupClip; 
	
	protected string _name; 
	public string itemName { 
		get { return _name; } 
		set { _name = value; } 
	} 
	
	protected string _baseDescription; 
	public string baseDescription { 
		get { return _baseDescription; } 
		set { _baseDescription = value; } 
	} 
	
	protected bool _carryable; 
	public bool carryable { 
		get { return _carryable; } 
		set { _carryable = value; } 
	} 
	
	protected bool _npc;
	public bool npc { 
		get { return _npc; } 
		set { _npc = value; } 
	} 
	
	protected bool _hasRequest = false; 
	protected bool _requestFulfilled = false; 
	public bool requestFulfilled { 
		set { _requestFulfilled = value; } 	
	} 
	
	protected InventorySlot _currentSlot = null; 
	public InventorySlot currentSlot { 
		get { return _currentSlot; } 	
	} 
	public bool inInventory { 
		get { return _currentSlot != null; } 
	}
	
	
	// For containers 
	protected SpawnedPuzzleItem _childItem; 
	public SpawnedPuzzleItem childItem { 
		get { return _childItem; } 
		set { _childItem = value; } 
	} 
	public bool containsItem { 
		get { return _childItem != null; } 	
	} 
	
	protected SpawnedPuzzleItem _parentItem; 
	public SpawnedPuzzleItem parentItem { 
		get { return _parentItem; } 	
	} 
	public bool insideItem { 
		get { return _parentItem != null; } 	
	} 
	
	protected Dictionary<string, object> _properties; 
	public void setProperty(string propertyName, object propertyVal) 
	{ 
		if (_properties == null)
			_properties = new Dictionary<string, object>(); 
		_properties[propertyName] = propertyVal; 
		
		if (propertyName == "classname")
			_name = propertyVal as string; 
		if (propertyName == "description")
			_baseDescription = propertyVal as string; 
		if (propertyName == "carryable") 
			_carryable = (bool)propertyVal;
		if (propertyName == "npc")
			_npc = (bool)propertyVal; 
	} 
	
	public bool propertyExists(string propertyName)
	{ 
		return _properties.ContainsKey(propertyName); 
	} 
	
	public object getProperty(string propertyName) 
	{ 
		if (propertyExists(propertyName))
			return _properties[propertyName];
		return null;
	} 

	public override void init ()
	{
		// Do some fancy stuff with properties here to determine type, sprite image, 
		// description, etc. 
		if (_properties == null)
			_properties = new Dictionary<string, object>(); 
		
		if (_properties.ContainsKey("key") && (bool)_properties["key"])
			_name = _properties["keyname"] as string;
		
		_sprite = GetComponent<tk2dSprite>(); 
		if (_properties.ContainsKey("sprite"))
			_sprite.spriteId = _sprite.GetSpriteIdByName(_properties["sprite"] as string);
		_text = GetComponentInChildren<tk2dTextMesh>(); 
		
		if (_carryable)
			_type |= CARRYABLE_TYPE;
		else 
			_type |= WALL_TYPE; 
		if (_npc) 
			_type |= NPC_TYPE; 
		
		_gridPos = PlayState.instance.toGridCoordinates(x, y); 
		_nextPoint = _gridPos; 
	}
	
	
	
	// Update is called once per frame
	public override void Update () {
		base.Update(); 
				
		// Check for a mouse over 
		Vector3 mouseActualPos = tk2dCamera.inst.mainCamera.ScreenToWorldPoint(Input.mousePosition);
		Vector2 mouseGridPos = PlayState.instance.toGridCoordinates(mouseActualPos.x-PlayState.instance.gridX, mouseActualPos.y-PlayState.instance.gridY); 
		if (mouseGridPos == _gridPos) {
			PlayState.instance.setHighlightedItem(this);
		}
	}
	
	
	public override void performTurn ()
	{
		// If we're carryable, check to see if the player has picked us up. 
		if (_carryable) { 
			bool onPlayer = false; 
			foreach (GridPiece inhabitant in PlayState.instance.currentGridInhabitants(_gridPos)) { 
				if (inhabitant.hasType(PLAYER_TYPE))
					onPlayer = true; 
			} 
			if (onPlayer) { 
				addToInventory();
			} 
		} 
	}
	
	public void addToInventory() 
	{
		if (_text != null && _text.gameObject.activeInHierarchy) 
			_text.gameObject.SetActive(false);
		InventorySlot maybeSlot = PlayState.instance.inventory.nextEmptySlot(); 
		if (maybeSlot != null) { 
			PlayState.instance.removeFromGrid(this); 
			maybeSlot.addItem(this); 
			_currentSlot = maybeSlot;
			PlayState.instance.addPlayerText(string.Format("+1 {0}", _name));
			PlayState.instance.playAudio(pickupClip); 
			if (_name == "sandwich") 
				PlayState.instance.sandwichGet(); 
		}
		
	} 
	
	
	public void addToOtherItem(SpawnedPuzzleItem otherItem) 
	{ 
		if (otherItem.containsItem || insideItem) 
			throw new UnityException("Item insertion conflict!"); 
		
		if (_text != null && _text.gameObject.activeInHierarchy) 
			_text.gameObject.SetActive(false);
		
		_parentItem = otherItem; 
		_parentItem.childItem = this;
		_parentItem.alpha = 0.5f; 
		
		transform.parent = otherItem.transform; 
		transform.localPosition = new Vector3(0, 0, 0.5f); 
		transform.localScale = new Vector3(0.5f, 0.5f, 1); 
		
		if (inInventory) 
			_currentSlot.removeItem(); 
		PlayState.instance.removeFromGrid(this); 
		
		
		// Disable this item so it doesn't receive updates while in a container
		this.enabled = false; 
	}
	
	public void removeFromOtherItem() 
	{ 
		_pathSpeed = 0; 
		_parentItem.childItem = null; 
		_parentItem.alpha = 1.0f;
		_parentItem = null;
	} 
	
	public virtual string description() 
	{ 
		string desc = _baseDescription; 
		List<string> mutables; 
		if (_properties.ContainsKey("mutables"))
			mutables = _properties["mutables"] as List<string>; 
		else 
			mutables = new List<string>(); 
		
		List<string> extraProperties = new List<string>(); 
		foreach (string mutable in mutables) { 
			if (_properties.ContainsKey(mutable))
				extraProperties.Add(_properties[mutable] as string); 
		} 
		if (extraProperties.Count > 0) 
			desc+=" It is "; 
		for (int i = 0; i < extraProperties.Count; i++) { 
			if (extraProperties.Count > 1 && i == extraProperties.Count-1)
				desc += " and "; // Oxford comma
			desc+=extraProperties[i];
			if (i < extraProperties.Count-1)
				desc+=", "; 
			else 
				desc+=".";
		} 
		if (insideItem) { 
			desc += string.Format(" (inside {0})", _parentItem.itemName); 	
		} 
			
		return desc; 
	} 
	
	public void initRequest(puzzlegen.relationship.ItemRequestRelationship request) 
	{ 
		_hasRequest = true; 
		_requestFulfilled = false; 
		if (_properties.ContainsKey("requesttext") && (string)(_properties["requesttext"]) != "") { 
			string existingText = _properties["requesttext"] as string; 
			existingText = existingText.Replace("REQUEST", request.requestedName); 
			existingText = existingText.Replace("PROPERTY", request.requestedPropertyValue as string); 
			existingText = existingText.Replace("REWARD", request.rewardItem.Name); 
			_properties["requesttext"] = existingText; 
		}
		if (_properties.ContainsKey("postrequesttext") && (string)(_properties["postrequesttext"]) != "")	{
			string existingText = _properties["postrequesttext"] as string; 
			existingText = existingText.Replace("REQUEST", request.requestedName); 
			existingText = existingText.Replace("PROPERTY", request.requestedPropertyValue as string); 
			existingText = existingText.Replace("REWARD", request.rewardItem.Name); 
			_properties["postrequesttext"] = existingText;
		}
		if (!_properties.ContainsKey("wrongitemtext") || (string)(_properties["wrongitemtext"]) == "") { 
			_properties["wrongitemtext"] = "That's not right. I wanted the REQUEST"; 	
		} 
		string wrongItemText = _properties["wrongitemtext"] as string;
		wrongItemText = wrongItemText.Replace("REQUEST", request.requestedName); 
		wrongItemText = wrongItemText.Replace("PROPERTY", request.requestedPropertyValue as string); 
		wrongItemText = wrongItemText.Replace("REWARD", request.rewardItem.Name); 
		_properties["wrongitemtext"] = wrongItemText;	
		
		if (!_properties.ContainsKey("wrongpropertytext") || (string)(_properties["wrongpropertytext"]) == "") { 
			_properties["wrongpropertytext"] = "That's not right. I wanted the PROPERTY REQUEST"; 
		}
		
		string wrongPropertyText = _properties["wrongpropertytext"] as string; 
		wrongPropertyText = wrongPropertyText.Replace("REQUEST", request.requestedName); 
		wrongPropertyText = wrongPropertyText.Replace("PROPERTY", request.requestedPropertyValue as string); 
		wrongPropertyText = wrongPropertyText.Replace("REWARD", request.rewardItem.Name); 
		_properties["wrongpropertytext"] = wrongPropertyText;
		
		if (!_properties.ContainsKey("fulfilledtext") || (string)(_properties["fulfilledtext"]) == "") { 
			_properties["fulfilledtext"] = 	"Great! Here you go!";
		} 
		
		string fulfilledText = _properties["fulfilledtext"] as string; 
		fulfilledText = fulfilledText.Replace("REQUEST", request.requestedName); 
		fulfilledText = fulfilledText.Replace("PROPERTY", request.requestedPropertyValue as string); 
		fulfilledText = fulfilledText.Replace("REWARD", request.rewardItem.Name); 
		_properties["fulfilledtext"] = fulfilledText; 
		
	} 
	
	
	public void activateNpcText() 
	{ 
		activateText(npcRequestText()); 
	} 
	
	public virtual string npcRequestText() 
	{ 
		if (!_hasRequest) { 
			if (_properties.ContainsKey("generictext") && (string)(_properties["generictext"]) != "")
				return _properties["generictext"] as string;
			else 
				return "Hello"; 
		}
		else if (_requestFulfilled) { 
			if (_properties.ContainsKey("postrequesttext") && (string)(_properties["postrequesttext"]) != "")
				return _properties["postrequesttext"] as string;
			else 
				return "Thanks again!";
		}
		else { 
			if (_properties.ContainsKey("requesttext") && (string)(_properties["requesttext"]) != "") 
				return _properties["requesttext"] as string; 
			else
				return "I want something. I'll give you something in return"; 
		} 
	}
	
	public virtual string npcFulfilledText() 
	{
		if (_properties.ContainsKey("fulfilledtext") && (string)(_properties["fulfilledtext"]) != "")
			return _properties["fulfilledtext"] as string; 
		else 
			return "Great! Here you go!";
	} 
	
	public virtual string npcWrongItemText() 
	{ 
		if (_properties.ContainsKey("wrongitemtext") && (string)(_properties["wrongitemtext"]) != "") 
			return _properties["wrongitemtext"] as string; 
		else 
			return "That's not what I wanted!"; 
	} 
	
	public virtual string npcWrongPropertyText() 
	{ 
		if (_properties.ContainsKey("wrongpropertytext") && (string)(_properties["wrongpropertytext"]) != "")
			return _properties["wrongpropertytext"] as string; 
		else 
			return "The item doesn't have the right property I requested.";
	} 
	
	
}
