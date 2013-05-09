using UnityEngine;
using System.Collections;
using System.Collections.Generic; 

public class Globals {
	
	public const int CELL_SIZE = 64; 
	
	public const float MOVE_SPEED = 300; 
	
	public const int ROOM_WIDTH = 16; 
	public const int ROOM_HEIGHT = 10; 
	
	public const float TEXT_DURATION = 3; 
	
	// Utility functions for choosing randomly from arrays and lists and the like 
	public static T getRandom<T>(T[] array) { 
		return array[Random.Range(0, array.Length)];
	}
	
	public static T getRandom<T>(List<T> list)  { 
		return list[Random.Range(0, list.Count)];
	}
	
	public static void partialShuffle<T>(List<T> list, uint howManyTimes) { 
		int i = 0; 
		int index1, index2; 
		T element; 
		while (i < howManyTimes) {
			index1 = Random.Range(0, list.Count);
			index2 = Random.Range(0, list.Count);
			element = list[index2]; 
			list[index2] = list[index1]; 
			list[index1] = element; 
			i++; 
		} 
	}
	
	public static void partialShuffle<T>(T[] array, uint howManyTimes) { 
		int i = 0; 
		int index1, index2; 
		T element; 
		while (i < howManyTimes) { 
			index1 = Mathf.FloorToInt(Random.Range(0, array.Length));
			index2 = Mathf.FloorToInt(Random.Range(0, array.Length)); 
			element = array[index2]; 
			array[index2] = array[index1]; 
			array[index1] = element;
			i++;
		} 
	} 
	
	public static void shuffle<T>(List<T> list) { 
			int n = list.Count; 
			while (n > 1) { 
				n--; 
				int k = Random.Range(0, n+1); 
				T elem = list[k]; 
				list[k] = list[n]; 
				list[n] = elem; 
			}	
	} 
	
	public static void shuffle<T>(T[] array) { 
		int n = array.Length; 
		while (n > 1) { 
			n--; 
			int k = Random.Range(0, n+1);
			T elem = array[k]; 
			array[k] = array[n]; 
			array[n] = elem; 
		} 	
	}
	
}
