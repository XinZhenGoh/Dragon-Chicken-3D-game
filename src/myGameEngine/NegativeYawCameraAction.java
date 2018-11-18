package myGameEngine;

import myProtocolClient.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class NegativeYawCameraAction  extends AbstractInputAction{

	private SceneNode myDolphin;
	private final Angle rotationAngle= Degreef.createFrom(5.5f);
	private MyGame game;
	public NegativeYawCameraAction(SceneNode dNode)  
	{
	 	myDolphin=dNode;
	 
	} 
 
 public void performAction(float time, Event e)  
 { 
	myDolphin.yaw(rotationAngle);
	
	 
 }
}
