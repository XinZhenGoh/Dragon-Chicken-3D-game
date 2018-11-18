package myGameEngine;

import java.awt.Event;

import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3PController {
	private Camera camera; //the camera being controlled   
	private SceneNode cameraN; //the node the camera is attached to   
	private SceneNode target; //the target the camera looks at   
	private float cameraAzimuth; //rotation of camera around Y axi
	private float cameraElevation; //elevation
	private float radias;
	private Vector3 targetPos; //target’s position in the world
	private Vector3 worldUpVec;   
	public Camera3PController(Camera cam, SceneNode camN, SceneNode targ, String controllerName, InputManager im)  
	{ 
		camera = cam;   
		cameraN = camN;   
		target = targ;   
		cameraAzimuth = 225.0f; // start from BEHIND and ABOVE the target   
		cameraElevation = 20.0f; // elevation is in degrees   
		radias = 2.0f;   
		worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);   
		setupInput(im, controllerName);   
		updateCameraPosition();  
		}  // Updates camera position: computes azimuth, elevation, and distance   
	// relative to the target in spherical coordinates, then converts those  
	// to world Cartesian coordinates and setting the camera position   
	public void updateCameraPosition()  
	{ 
		double theta = Math.toRadians(cameraAzimuth); // rot around target   
		double phi = Math.toRadians(cameraElevation); // altitude angle   
		double x = radias * Math.cos(phi) * Math.sin(theta);   
		double y = radias * Math.sin(phi);   
		double z = radias * Math.cos(phi) * Math.cos(theta);     
		cameraN.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(target.getWorldPosition()));   
		cameraN.lookAt(target, worldUpVec);   
		}  
	private void setupInput(InputManager im, String cn)  
	{ 
		Action orbitAAction = new OrbitAroundAction();  
		RadiasNegAction zIn= new RadiasNegAction();
		//RadiasPosAction zOut= new RadiasPosAction();
		IncreaseElevation higher= new IncreaseElevation();
		//DecreaseElevation lower= new DecreaseElevation();
		//rotate by rotating avatar
		YawCameraAction myPadYaw= new YawCameraAction(target);
		NegativeYawCameraAction myPadNegYaw= new NegativeYawCameraAction(target);
		im.associateAction(cn,net.java.games.input.Component.Identifier.Axis.RX,  orbitAAction,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);   
		im.associateAction(cn,    net.java.games.input.Component.Identifier.Button._1,   myPadYaw,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		im.associateAction(cn,    net.java.games.input.Component.Identifier.Button._2,   myPadNegYaw,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		im.associateAction(cn,net.java.games.input.Component.Identifier.Axis.RY,  zIn,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);   
		//im.associateAction(cn,net.java.games.input.Component.Identifier.Button._6,  zOut,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);   
		im.associateAction(cn,net.java.games.input.Component.Identifier.Axis.Z,  higher,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);   
		//im.associateAction(cn,net.java.games.input.Component.Identifier.Button._7,  lower,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);   
		
		
		
		//  similar input set up for OrbitRadiasAction, OrbitElevationAction  
		} 
	private class OrbitAroundAction extends AbstractInputAction  { 
		// Moves the camera around the target (changes camera azimuth).  
		public void performAction(float time, net.java.games.input.Event evt)   
		{ 
			float rotAmount;   
			if (evt.getValue() < -0.2)      
			{ 
				rotAmount=-0.2f; 
			}      
			else       
			{ 
				if (evt.getValue() > 0.2)        
				{ 
					rotAmount=0.2f; 
				}       
				else       
				{ 
					rotAmount=0.0f; 
				}    
			}       
			cameraAzimuth += rotAmount;       
			cameraAzimuth = cameraAzimuth % 360;       
			updateCameraPosition();   
			} 
		}   //  similar for OrbitRadiasAction, OrbitElevationAction 
	
	//Zoom in and zoom out function
	private class RadiasNegAction extends AbstractInputAction  { 
		// Moves the camera around the target (changes camera azimuth).  
		public void performAction(float time, net.java.games.input.Event evt)   
		{
			if (evt.getValue() < -0.2)      
		
			{ 
		
				radias-=0.02f;        
			       
				updateCameraPosition();   
			
			}      
			else       
			{ 
			
				radias+=0.02f;        
		       
				updateCameraPosition();  
			}
		}   
//	private class RadiasPosAction extends AbstractInputAction  { 
		// Moves the camera around the target (changes camera azimuth).  
	//	public void performAction(float time, net.java.games.input.Event evt)   
	//	{ 
			 
			//} 
		}
	private class IncreaseElevation extends AbstractInputAction{
		

		@Override
		public void performAction(float arg0, net.java.games.input.Event arg1) {
			
			 //System.out.println("In positve negative Action"); 
			if (arg1.getValue() < -0.2)      
				
			{ 
		
				cameraElevation+=0.2f;      
				updateCameraPosition();   
			} 
			else       
			{
				cameraElevation-=0.2f;        
				updateCameraPosition();   
			}
			
		}
	}
private class DecreaseElevation extends AbstractInputAction{


	@Override
	public void performAction(float arg0, net.java.games.input.Event arg1) {
		cameraElevation-=0.2f;        
		updateCameraPosition();   
		} 
	
	}	
		
	
	
}
