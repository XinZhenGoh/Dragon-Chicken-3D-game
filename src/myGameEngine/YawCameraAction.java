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

public class YawCameraAction  extends AbstractInputAction{


		private SceneNode myDolphin;
		private final Angle rotationAngle= Degreef.createFrom(5.5f).negate();
		private MyGame game;
		public YawCameraAction(SceneNode dNode)  
		{
		 	myDolphin=dNode;
		 
		} 
	 
	 public void performAction(float time, Event e)  
	 { 
		myDolphin.yaw(rotationAngle);
		
		 
	 }
}
