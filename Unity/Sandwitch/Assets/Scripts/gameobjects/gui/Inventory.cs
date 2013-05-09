using UnityEngine;
using System.Collections;

public class Inventory : MonoBehaviour {

	protected InventorySlot[] _slots; 
	
	
	protected InventorySlot _selectedSlot = null; 
	public InventorySlot selectedSlot { 
		get { return _selectedSlot; } 
		set { 
			if (_selectedSlot != null && _selectedSlot != value) { 
				_selectedSlot.selected = false;
			} 
			_selectedSlot = value; 
			if (_selectedSlot != null)
				_selectedSlot.selected = true; 
		} 
	} 
	
	protected InventorySlot _hoveredSlot = null; 
	public InventorySlot hoveredSlot { 
		get { return _hoveredSlot; } 
		set { 
			_hoveredSlot = value; 	
		} 
	} 
	
		
	// Use this for initialization
	void Start () {
		_slots = new InventorySlot[transform.GetChildCount()]; 
		for (int i = 0; i < transform.GetChildCount(); i++) {
			Transform child = transform.GetChild(i); 
			InventorySlot slot = child.GetComponent<InventorySlot>(); 
			_slots[i] = slot;
		} 
	}
	
	public InventorySlot nextEmptySlot() 
	{ 
		foreach (InventorySlot slot in _slots) { 
			if (slot.isEmpty)
				return slot; 
		} 
		return null; 
	} 
}
