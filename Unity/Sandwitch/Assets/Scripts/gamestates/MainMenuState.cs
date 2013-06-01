using UnityEngine;
using System.Collections;

public class MainMenuState : MonoBehaviour {
	
	public static bool winState = false; 
	
	void Start () {
		tk2dTextMesh titleText = GameObject.Find("titletext").GetComponent<tk2dTextMesh>(); 
		if (winState) {
			titleText.text = "You Win!"; 
		}
		else { 
			titleText.text = "Sandwitch"; 
		} 
		titleText.Commit(); 
	}
	
	void Update () {
		if (Input.GetKeyDown(KeyCode.Escape))
			Application.Quit(); 
	}
	
	public void playButtonPressed()
	{ 
		PlayState.seedGenerate = false;
		Application.LoadLevel("PlayScene"); 
	} 
	
	public void instructionsButtonPressed() 
	{ 
		Application.LoadLevel("InstructionsScene"); 
	} 
}
