using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

using puzzlegen.database;
using puzzlegen.relationship; 

namespace puzzlegen.buildingblocks 
{
	/// <summary>
	///	BuildingBlock is the base class from which all puzzle building blocks inherit. In the original python
	/// implementation, this class was called Puzzle. I chose to change its name for this implementation mainly
	/// because classes like Area, Filter, etc. aren't really "Puzzles" in the traditional sense but still need
	/// to derive from the base class.
	/// </summary>
	public abstract class BuildingBlock
	{
		private List<BuildingBlock> _inputs; 
		
		protected List<PuzzleItem> _itemsToSpawn; 
		protected List<relationship.IRelationship> _relationshipsToSpawn; 
		
		// So we know which items to "Despawn" if necessary 
		protected List<string> _spawnedItems; 
		
		protected PuzzleItem _spawnedOutput; 
		
		protected bool _verbose; 
		
		// To keep track of the kind of properties our output item should have 
		protected Dictionary<string, object> _desiredOutputProperties; 
		
		public BuildingBlock(List<BuildingBlock> inputs, bool verbose)
		{
			_inputs = inputs; 
			_itemsToSpawn = new List<PuzzleItem>(); 
			_relationshipsToSpawn = new List<relationship.IRelationship>(); 
			_spawnedItems = new List<string>(); 
			_verbose = verbose; 
			_desiredOutputProperties = new Dictionary<string, object>(); 
		}
		
		public BuildingBlock(List<BuildingBlock> inputs) : this(inputs, true)
		{
		}
		
		// For determining if two items are carryable to each other, 
		// need to determine if there is a possibility that one item can be carried to the other. 
		public virtual bool outputHasContainer() 
		{ 
			return false; 
		} 
		
		// For determining the unique id of an item. 
		public virtual int outputSpawnIndex() 
		{ 
			if (_spawnedOutput == null)
				return -1; 
			return _spawnedOutput.SpawnIndex; 
		} 
			
		//Function that's called when a process in the puzzle generation fails and the items produced 
		//by this building block need to be despawned
		public virtual void despawnItems() 
		{
			foreach (string itemName in _spawnedItems) { 
				if (_verbose) 
					Debug.Log("Despawning item " + itemName); 
				Database.Instance.getItem(itemName).despawnItem(); 
			}
			
			_spawnedItems.Clear(); 
			foreach (BuildingBlock inputItem in _inputs) { 
				inputItem.despawnItems(); 	
			}
		}
		
		// General outline of puzzle generation 
		protected virtual bool spawnFilteredOutput(string outputName) 
		{
			if (!Database.Instance.itemExists(outputName)) { 
				if (_verbose) Debug.Log("Failed to generate puzzle, output does not exist in database " + outputName); 
				return false; 
			}
			
			DBItem dbitem = Database.Instance.getItem(outputName); 
			if (dbitem.Spawned) { 
				if (_verbose) Debug.Log("Failed to generate puzzle: " + outputName + " already spawned");
				return false; 
			}
			
			PuzzleItem output = dbitem.spawnItem();  
			_spawnedItems.Add(outputName); 
			output = applyPropertiesToSpawnedItem(_desiredOutputProperties, output); 
			// Give the output a unique id if it doesn't already have one
			if (!output.propertyExists("spawnIndex")) { 
				output.setProperty("spawnIndex", dbitem.NextSpawnIndex);
			}
			_spawnedOutput = output; 
			return true;  
		}
		
		
		// A useful function for applying our desired properties to a spawned item 
		// useful primarily because it allows leeway for doing things like inserting items into containers
		// via the contains property 
		protected virtual PuzzleItem applyPropertiesToSpawnedItem(Dictionary<string, object> desiredProperties, PuzzleItem item) 
		{
			foreach (string propertyName in desiredProperties.Keys) { 
				if (propertyName == "contains") { 
					List<PuzzleItem> itemsInside = new List<PuzzleItem>(); 
					List<string> itemsToInsert = desiredProperties["contains"] as List<string>; 
					foreach (string itemToInsertName in itemsToInsert)  { 
						if (!Database.Instance.itemExists(itemToInsertName)) { 
							if (_verbose) Debug.Log("Puzzle failed to generate output, internal item " + itemToInsertName + " does not exist"); 
							return null; 
						}	
						if (Database.Instance.getItem(itemToInsertName).Spawned) { 
							if (_verbose) Debug.Log("Puzzle failed to generate output. Internal item " + itemToInsertName + " already spawned"); 
							return null; 
						}
						PuzzleItem itemToInsert = Database.Instance.getItem(itemToInsertName).spawnItem(); 
						applyPropertiesToSpawnedItem(desiredProperties["innerItemProps"] as Dictionary<string, object>, itemToInsert); 
						itemsInside.Add(itemToInsert); 
						_spawnedItems.Add(itemToInsertName); 
					}
					item.setProperty("contains", itemsInside); 
				}
				else 
					item.setProperty(propertyName, desiredProperties[propertyName]); 
			}
			
			return item; 
		}
		
		
		// This function implemented by the sub classes 
		protected virtual bool spawnFilteredInputs(string outputName) 
		{
			return false; 
		}
		
		// The function that is the official entry point to generating a puzzle with this building block 
		public virtual PuzzleOutput generatePuzzle(string outputName, Dictionary<string, object> desiredOutputProperties) 
		{
			_itemsToSpawn = new List<PuzzleItem>(); 
			_relationshipsToSpawn = new List<IRelationship>(); 
			_desiredOutputProperties = desiredOutputProperties; 
			_spawnedOutput = null; 
			if (_verbose) Debug.Log("Generating puzzle for  " + outputName); 
			if (!spawnFilteredOutput(outputName))
				return null; 
			if (!spawnFilteredInputs(outputName))
				return null; 
			// In case of success, time to create a puzzle output
			PuzzleOutput result = new PuzzleOutput(); 
			result.Items = _itemsToSpawn; 
			result.Relationships = _relationshipsToSpawn; 
			return result; 	
		}
		
		public virtual PuzzleOutput generatePuzzle(string outputName) 
		{
			return generatePuzzle(outputName, new Dictionary<string, object>());
		} 
		
		// Simple utility function used by most building blocks to shuffle the contents of an array 
		public static void shuffle<T>(IList<T> list) 
		{ 
			int n = list.Count; 
			while (n > 1) { 
				n--; 
				int k = Random.Range(0, n+1); 
				T value = list[k]; 
				list[k] = list[n]; 
				list[n] = value; 
			}
		}
		
		//utility function useful for some building blocks to tell if any items in a list of items are carryable
		//to one another. This way, the various building blocks won't generate non-carryable items in different 
		//rooms without ensuring that at least one of them can fit inside a container
		//NOTE: Feel free to make this function more robust if the current implementation is not sufficient to 
		//ensure solvability. 
		// FLAG: Have to actually implement this 
		public static bool areCarryable(List<string> itemNames, List<BuildingBlock> inputs) 
		{
			bool oneStationaryItem = false; 
			for (int i = 0; i < itemNames.Count; i++) { 
				if (!(bool)Database.Instance.getItem(itemNames[i]).getProperty("carryable")) { 
					if (!(inputs[i].outputHasContainer())) { 
						if (oneStationaryItem)
							return false; 
						else 
							oneStationaryItem = true;
					} 
				} 
			} 
			return true; 
		}
		
		public static bool isCarryable(string itemName, BuildingBlock input) 
		{ 
			return (bool)Database.Instance.getItem(itemName).getProperty("carryable") || input.outputHasContainer(); 	
		} 
		
		
	}
	
}
