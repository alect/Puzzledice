using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen; 
using puzzlegen.relationship; 

public class RelationshipExecutor : IRelationshipVisitor
{
	// The actual item references used during a relationship 
	protected SpawnedPuzzleItem _item1, _item2; 
	
	protected Room _currentRoom; 
	protected ProcGame _game; 
	
	public RelationshipExecutor(SpawnedPuzzleItem item1, SpawnedPuzzleItem item2, Room room, ProcGame game) 
	{ 	
		_item1 = item1; 
		_item2 = item2; 
		_currentRoom = room; 
		_game = game; 
	} 
	
	
	public void accept (AreaConnectionRelationship rel)
	{
		// Not dealing with these during runtime unless it's a locked door
		if (!rel.locked)
			throw new UnityException("AreaConnectionRelationship applied during game!"); 
		
		// Otherwise, destroy both our key and lock for now 
		PlayState.instance.playAudio(PlayState.instance.lockClip); 
		
		if (_item1.inInventory)
			_item1.currentSlot.removeItem(); 
		if (_item1.insideItem) 
			_item1.removeFromOtherItem();
		_item1.die(); 
		if (_item2.inInventory) 
			_item2.currentSlot.removeItem(); 
		if (_item2.insideItem)
			_item2.removeFromOtherItem();
		_item2.die(); 
	}
	
	protected SpawnedPuzzleItem spawnItem(PuzzleItem item) 
	{ 
		GameObject newObj = GameObject.Instantiate(PlayState.instance.puzzleItemPrefab) as GameObject; 
		SpawnedPuzzleItem spawnedItem = newObj.GetComponent<SpawnedPuzzleItem>(); 
		// Give the spawned item all the appropriate properties. 
		foreach (string propertyName in item.getPropertyNames()) { 
			spawnedItem.setProperty(propertyName, item.getProperty(propertyName)); 	
		} 
		
		if (_game.getRequest(spawnedItem.itemName) != null) 
			spawnedItem.initRequest(_game.getRequest(spawnedItem.itemName)); 
		
		return spawnedItem;
	} 
	
	public void accept (CombineRelationship rel)
	{
		// Create a new spawn item for the new item 
		SpawnedPuzzleItem itemToSpawn = spawnItem(rel.resultItem); 
		// Now, need to figure out where to spawn the new item.  
		bool spawnInInventory = false, spawnAtLocation = false, spawnInItem1Container = false, spawnInItem2Container = false; 
		Vector2 locationToSpawn = Vector2.zero; 
		SpawnedPuzzleItem item1Container = _item1.parentItem; 
		SpawnedPuzzleItem item2Container = _item2.parentItem; 
		
		bool destroyItem1 = !_item1.propertyExists("destroyoncombine") || (bool)_item1.getProperty("destroyoncombine"); 
		bool destroyItem2 = !_item2.propertyExists("destroyoncombine") || (bool)_item2.getProperty("destroyoncombine"); 
		
		// If neither ingredient is destoyed, just spawn in the room 
		if (!destroyItem1 && !destroyItem2) { 
			spawnAtLocation = false; 
			spawnInInventory = false; 
			spawnInItem1Container = false; 
			spawnInItem2Container = false; 
		} 
		else if (itemToSpawn.carryable && ((destroyItem1 && _item1.inInventory) || (destroyItem2 && _item2.inInventory))) 
			spawnInInventory = true; 
		// If the item is not carryable, have to spawn in room 
		else { 
			spawnInInventory = false; 
			// Check if we can spawn in item1's container 
			if (destroyItem1 && _item1.insideItem && itemToSpawn.propertyExists("fills") && (itemToSpawn.getProperty("fills") as List<string>).Contains(item1Container.itemName)) { 
				spawnInItem1Container = true; 		
			} 
			else if (destroyItem2 && _item2.insideItem && itemToSpawn.propertyExists("fills") && (itemToSpawn.getProperty("fills") as List<string>).Contains(item2Container.itemName)) { 
				spawnInItem2Container = true; 	
			} 
			else if (destroyItem1 && !_item1.inInventory) { 
				spawnAtLocation = true; 
				locationToSpawn = _item1.gridPos;
			} 
			else if (destroyItem2 && !_item2.inInventory) { 
				spawnAtLocation = true; 
				locationToSpawn = _item2.gridPos; 
			} 
		}
		
		
		
		// Destroy items that need to be destroyed. 
		if (destroyItem1) { 
			if (_item1.inInventory)
				_item1.currentSlot.removeItem(); 
			if (_item1.insideItem) 
				_item1.removeFromOtherItem();
			_item1.die(); 
		} 
		if (destroyItem2) { 
			if (_item2.inInventory)
				_item2.currentSlot.removeItem();
			if (_item2.insideItem)
				_item2.removeFromOtherItem(); 
			_item2.die(); 
		} 
		
		
		// Now finally spawn the item. 
		if (spawnInInventory) { 
			itemToSpawn.init(); 
			itemToSpawn.addToInventory();
		} 
		else if (spawnInItem1Container) { 
			itemToSpawn.init(); 
			itemToSpawn.addToOtherItem(item1Container);
		} 
		else if (spawnInItem2Container) { 
			itemToSpawn.addToOtherItem(item2Container);	
		} 
		else if (spawnAtLocation) { 
			_currentRoom.addPiece(itemToSpawn, locationToSpawn); 	
		} 
		else { 
			_currentRoom.addPiece(itemToSpawn);
		}
		
		// Play a sound 
		PlayState.instance.playAudio(PlayState.instance.pickupClip); 
		
	}
	
	public void accept (InsertionRelationship rel)
	{
		// Insert one item in the other. 
		SpawnedPuzzleItem box, filler; 
		if (rel.containerName == _item1.itemName) { 
			box = _item1; 
			filler = _item2; 
		} 
		else { 
			box = _item2; 
			filler = _item1; 
		} 
		// If the box is empty, insert the filler item 
		if (!box.containsItem) { 
			filler.addToOtherItem(box); 
			PlayState.instance.playAudio(PlayState.instance.pickupClip); 
		} 
		else { 
			PlayState.instance.addPlayerText(string.Format("The {0} is full.", box.itemName)); 	
		} 
		
	}
	
	public void accept (ItemRequestRelationship rel)
	{
		// Give one item to another. 
		
		SpawnedPuzzleItem requested, requester; 
		if (rel.requestedName == _item1.itemName) { 
			requested = _item1; 
			requester = _item2; 
		}
		else { 
			requested = _item2; 
			requester = _item1; 
		}
		// If the requester is inside something, have to remove it first 
		if (requester.insideItem) { 
			PlayState.instance.addPlayerText(string.Format("Have to remove the {0} first.", requester.itemName));
			return;
		} 
		// Check to see if the item has the requested property 
		if (rel.requestedPropertyName != null && (!requested.propertyExists(rel.requestedPropertyName) || requested.getProperty(rel.requestedPropertyName) != rel.requestedPropertyValue)) { 
			requester.activateText(requester.npcWrongPropertyText()); 
			return; 
		}
		requester.requestFulfilled = true; 
		PlayState.instance.playAudio(PlayState.instance.pickupClip); 
		// On success, destroy the old item and spawn the new one. 
		if (requested.inInventory)
			requested.currentSlot.removeItem(); 
		if (requested.insideItem) 
			requested.removeFromOtherItem();
		requested.die(); 
		
		SpawnedPuzzleItem itemToSpawn = spawnItem(rel.rewardItem); 
		if (itemToSpawn.carryable) { 
			itemToSpawn.init(); 
			itemToSpawn.addToInventory(); 
		} 
		else { 
			_currentRoom.addPiece(itemToSpawn); 	
		} 
		
		// Display the fulfilled text. 
		requester.activateText(requester.npcFulfilledText()); 
	}
	
	public void accept (PropertyChangeRelationship rel)
	{
		// Change a property of an item 
		SpawnedPuzzleItem changer, changee; 
		if (rel.changerName == _item1.itemName) { 
			changer = _item1; 
			changee = _item2; 
		} 
		else { 
			changer = _item2; 
			changee = _item1; 
		}
		if (!changee.propertyExists(rel.propertyName) || changee.getProperty(rel.propertyName) != rel.propertyVal) { 
			PlayState.instance.playAudio(PlayState.instance.pickupClip); 
			PlayState.instance.addPlayerText(string.Format("The {0} is now {1}", changee.itemName, rel.propertyVal)); 
			changee.setProperty(rel.propertyName, rel.propertyVal); 
		}
		else { 
			PlayState.instance.addPlayerText("Nothing happened."); 	
		} 
		if (changer.propertyExists("destroyonpropchange") && (bool)changer.getProperty("destroyonprochange")) { 
			if (changer.inInventory)
				changer.currentSlot.removeItem(); 
			changer.die(); 
		}
		
	}
	
	public void accept (StartAreaRelationship rel)
	{
		// Not dealing with these during runtime
		throw new UnityException("StartAreaRelationship applied during game!"); 
	}
}
