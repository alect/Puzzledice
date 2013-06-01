using UnityEngine;
using System.Collections;

public class InstructionState : MonoBehaviour {

	void Start () {
	
	}
	
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
