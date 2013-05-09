using UnityEngine;
using System.Collections;

public class InventorySlot : MonoBehaviour 
{	
	protected tk2dSprite _sprite; 
	protected Inventory _inventory; 
	protected bool _mouseOver = false; 
	
	protected SpawnedPuzzleItem _item; 
	public SpawnedPuzzleItem item { 
		get { 
			if (_item.containsItem) 
				return _item.childItem; 
			else 
				return _item; 
		} 	
	} 
	public bool isEmpty { 
		get { return _item == null; } 	
	}
	
	protected bool _selected; 
	public bool selected { 
		get { return _selected; } 
		set { 
			_selected = value; 
			if (_selected) 
				_sprite.spriteId = _sprite.GetSpriteIdByName("inventoryslotselected");
			else 
				_sprite.spriteId = _sprite.GetSpriteIdByName("inventoryslotnormal");
		}
	} 
	
	void Start() 
	{ 
		_inventory = transform.parent.GetComponent<Inventory>();
		_sprite = GetComponent<tk2dSprite>();
	} 
	
	public void addItem(SpawnedPuzzleItem item) 
	{ 
		if (!isEmpty) 
			throw new UnityException("Inventory Conflict"); 
		_item = item; 
		_item.transform.parent = transform; 
		_item.transform.localPosition = new Vector3(0, 0, -1);
		_item.enabled = false; 
	} 
	
	public void removeItem()
	{ 
		_item = null; 
	} 
	
	// Update is called once per frame
	void Update () 
	{
		// Check to see if we're mousing over this slot. 
		if (!isEmpty && _mouseOver) { 
			if (_inventory.selectedSlot == null) {
				float textX = Mathf.Floor(transform.position.x / Globals.CELL_SIZE); 
				PlayState.instance.setPopupText(item.description(), new Vector2(textX, -1)); 
			}
			else if (_inventory.selectedSlot != this) { 
				PlayState.instance.setHighlightedItem(this.item); 	
			} 
			if (Input.GetMouseButtonDown(0)) { 
				if (_inventory.selectedSlot == null)
					_inventory.selectedSlot = this; 
				else if (_inventory.selectedSlot == this)
					_inventory.selectedSlot = null; 					
			} 
		} 
	}
	
	void OnMouseEnter() 
	{ 
		_mouseOver = true; 
		_inventory.hoveredSlot = this;
	} 
	
	void OnMouseExit() 
	{
		_mouseOver = false; 
		if (_inventory.hoveredSlot == this)
			_inventory.hoveredSlot = null; 
	} 
}
