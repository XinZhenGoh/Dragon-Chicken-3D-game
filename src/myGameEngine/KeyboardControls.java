package myGameEngine;

import myProtocolClient.MyGame;
import myProtocolClient.ProtocolClient;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.physics.PhysicsObject;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;

public class KeyboardControls {
	private SceneNode myDolphin;
	private PhysicsObject dolphinObj;
	public float speed = 0.1f;
	public float LRspeed = 0.05f;
	private float rotAmt = 0.5f;
	public float scale = 0.2f;
	private MyGame game;
	private ProtocolClient protClient;
	
	public KeyboardControls(String controllerName, MyGame g) {
		myDolphin = g.getDolphin();
		game = g;
		protClient = g.getPClient();
		setupInput(g.getIm(),controllerName);
	}
	
	public void setSpeed(float spd) {
		
	speed = spd;
	}
	
		private void setupInput(InputManager im,String cn){
			
		    Action moveForwardAction = new MoveForwardAction();
		    Action moveBackwardAction = new MoveBackwardAction();
		    Action moveLeftAction = new MoveLeftAction();
		    Action moveRightAction = new MoveRightAction();
		    Action rotateLeftAction = new RotateLeft();
		    Action rotateRightAction = new RotateRight();
		    ScriptAction scriptAction = new ScriptAction(game);
		    
			 			im.associateAction(cn,
					    net.java.games.input.Component.Identifier.Key.W,
					    moveForwardAction,
					    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					    
					    im.associateAction(cn,
					    net.java.games.input.Component.Identifier.Key.S,
					    moveBackwardAction,
					    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					    
					    im.associateAction(cn,
					    net.java.games.input.Component.Identifier.Key.D,
					    moveLeftAction,
					    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					    
					    im.associateAction(cn,
					    net.java.games.input.Component.Identifier.Key.A,
					    moveRightAction,
					    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					    
					    im.associateAction(cn,
					    net.java.games.input.Component.Identifier.Key.LEFT,
					    rotateLeftAction,
					    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					    
					    im.associateAction(cn,
					    net.java.games.input.Component.Identifier.Key.RIGHT,
					    rotateRightAction,
					    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					    
					    im.associateAction(cn,net.java.games.input.Component.Identifier.Key.SPACE,
					    scriptAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);


		}
		private class ShootAction extends AbstractInputAction{

			@Override
			public void performAction(float arg0, Event arg1) {
				//bullet.moveForward(0.1f);
			}
		}

		private class MoveForwardAction extends AbstractInputAction{

			@Override
			public void performAction(float arg0, Event arg1) {
				// TODO Auto-generated method stub
				 myDolphin.moveForward(speed);
				 game.updateVerticalPosition();
				protClient.sendMoveMessage(myDolphin.getLocalPosition()); //was get world changed!!!!, change back if doesnt work  
					
			}
		}

		private class MoveBackwardAction extends AbstractInputAction{

			@Override
			public void performAction(float arg0, Event arg1) {
				// TODO Auto-generated method stub
				 myDolphin.moveBackward(speed);
				 game.updateVerticalPosition();
				 protClient.sendMoveMessage(myDolphin.getLocalPosition()); //was world, change if doesnt fix error!!
					
			}
		}

		private class MoveLeftAction extends AbstractInputAction{
			@Override
			public void performAction(float arg0, Event arg1) {
				// TODO Auto-generated method stub
				 myDolphin.moveLeft(LRspeed);
				 game.updateVerticalPosition();
					protClient.sendMoveMessage(myDolphin.getLocalPosition()); //was world, change if doesnt fix error!! 
					
			}
		}

		private class MoveRightAction extends AbstractInputAction{
			@Override
			public void performAction(float arg0, Event arg1) {
				// TODO Auto-generated method stub
				 myDolphin.moveRight(LRspeed);
				 game.updateVerticalPosition();
					protClient.sendMoveMessage(myDolphin.getLocalPosition()); //was world, change if doesnt fix error!! 
			}
		}


		private class RotateLeft extends AbstractInputAction{

			@Override
			public void performAction(float arg0, Event arg1) {
				 
				 myDolphin.yaw(Degreef.createFrom(0.5f));
				 protClient.sendRotateMessage(String.valueOf(rotAmt)); 
			}
		}

		private class RotateRight extends AbstractInputAction{

			@Override
			public void performAction(float arg0, Event arg1) {
				// TODO Auto-generated method stub

				 myDolphin.yaw(Degreef.createFrom(0.5f).mult(-1));
				 protClient.sendRotateMessage(String.valueOf(-rotAmt)); 
			}

		}
}
