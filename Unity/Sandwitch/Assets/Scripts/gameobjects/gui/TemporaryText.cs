using UnityEngine;
using System.Collections;

public class TemporaryText : MonoBehaviour 
{
	
	private float _timeSinceSpawn; 
	public float timeVisible; 

	// Use this for initialization
	void Start () {
		_timeSinceSpawn = 0; 
	}
	
	// Update is called once per frame
	void Update () {
		_timeSinceSpawn += Time.deltaTime; 
		if (_timeSinceSpawn >= timeVisible)
			Destroy(gameObject);
	}
}
