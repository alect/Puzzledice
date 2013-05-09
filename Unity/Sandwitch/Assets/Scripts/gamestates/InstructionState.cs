using UnityEngine;
using System.Collections;

public class InstructionState : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () 
	{
		if (Input.GetKeyDown(KeyCode.Escape)) 
			Application.LoadLevel("TitleScene"); 
	}
	
	public void backButtonPressed() 
	{ 
		MainMenuState.winState = false;
		Application.LoadLevel("TitleScene"); 
	} 
}
