package myGameEngine;


import ray.input.InputManager;
import ray.input.action.AbstractInputAction;

import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3PKeyboard {
	

		private Camera camera; //the camera being controlled   
		private SceneNode cameraN; //the node the camera is attached to   
		private SceneNode target; //the target the camera looks at   
		private float cameraAzimuth; //rotation of camera around Y axi
		private float cameraElevation; //elevation
		private float radias;
		private Vector3 targetPos; //target’s position in the world
		private Vector3 worldUpVec;   
		public Camera3PKeyboard (Camera cam, SceneNode camN, SceneNode targ, String keyboardName, InputManager im)  
		{ 
			camera = cam;   
			cameraN = camN;   
			target = targ;   
			cameraAzimuth = 180.0f; // start from BEHIND and ABOVE the target   was 225.0f;
			cameraElevation = 20.0f; // elevation is in degrees   
			radias = 2.0f;   
			worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);   
			//cameraN.setLocalPosition((Vector3)camera.getPo());
			camera.setMode('n');
			
			setupInput(im, keyboardName);   
			updateCameraPosition();  
			
			
			}  // Updates camera position: computes azimuth, elevation, and distance   
		// relative to the target in spherical coordinates, then converts those  
		// to world Cartesian coordinates and setting the camera position   
		public void updateCameraPosition()  
		{ 
			//System.out.println("in updateCameraPosition "); 
			//System.out.println("position was: "+ cameraN.getLocalPosition()); 
			double theta = Math.toRadians(cameraAzimuth); // rot around target   
			double phi = Math.toRadians(cameraElevation); // altitude angle   
			double x = radias * Math.cos(phi) * Math.sin(theta);   
			double y = radias * Math.sin(phi);   
			double z = radias * Math.cos(phi) * Math.cos(theta);     
			cameraN.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(target.getWorldPosition()));   
			cameraN.lookAt(target, worldUpVec); 
			//System.out.println("position is now: "+ cameraN.getLocalPosition()); 
			//System.out.println("position is now: "+ camera.getPo()); 
			}  
		private void setupInput(InputManager im, String kbName)  
		{ 
			
			//rotations without changing avatar location
			 KBPosOrbitAction kbPosRot= new  KBPosOrbitAction();
			 KBNegOrbitAction kbNegRot= new KBNegOrbitAction();
			im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.UP,  kbPosRot,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);   
			im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.DOWN,  kbNegRot,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);   
			
			//rotations by changing avatar location
			 
			
			YawCameraAction yawAct= new YawCameraAction(target);
	    	NegativeYawCameraAction negYawAct= new NegativeYawCameraAction(target);
			//im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.RIGHT,   yawAct,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    		//im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.LEFT,   negYawAct,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
			
    		//change elevation
    		IncreaseElevation posElevation= new IncreaseElevation();
    		DecreaseElevation decElevation= new DecreaseElevation();
    		im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.U,   posElevation,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    		im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.J,   decElevation,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
			
    		//Zoom in and out
    		ZoomIn zoomClose=new ZoomIn();
    		ZoomOut zoomFar=new ZoomOut();
    		im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.NUMPAD8,   zoomClose,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    		im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.NUMPAD2,   zoomFar,    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
			
			} 
		 //  similar for OrbitRadiasAction, OrbitElevationAction 
		private class KBPosOrbitAction extends AbstractInputAction{
		

			@Override
			public void performAction(float arg0, net.java.games.input.Event arg1) {
				float rotAmount=0.2f; 
				// System.out.println("In positve perform Action"); 
				
				cameraAzimuth += rotAmount;       
				cameraAzimuth = cameraAzimuth % 360; 
				 //System.out.println("CameraAzimuth is: "+cameraAzimuth);
				 
				updateCameraPosition();   
				} 
				
			}
		private class KBNegOrbitAction extends AbstractInputAction{
			

			@Override
			public void performAction(float arg0, net.java.games.input.Event arg1) {
				float rotAmount=-0.2f; 
				 //System.out.println("In positve negative Action"); 
				cameraAzimuth += rotAmount;       
				cameraAzimuth = cameraAzimuth % 360;       
				updateCameraPosition();   
				} 
				
			}
private class IncreaseElevation extends AbstractInputAction{
			

			@Override
			public void performAction(float arg0, net.java.games.input.Event arg1) {
				
				 //System.out.println("In positve negative Action"); 
				cameraElevation+=0.2f;      
				updateCameraPosition();   
				} 
				
			}
private class DecreaseElevation extends AbstractInputAction{
	

	@Override
	public void performAction(float arg0, net.java.games.input.Event arg1) {
		cameraElevation-=0.2f;        
		updateCameraPosition();   
		} 
		
	}
private class ZoomIn extends AbstractInputAction{
	

	@Override
	public void performAction(float arg0, net.java.games.input.Event arg1) {
		radias-=0.02f;        
		updateCameraPosition();   
		} 
		
	}
private class ZoomOut extends AbstractInputAction{
	

	@Override
	public void performAction(float arg0, net.java.games.input.Event arg1) {
		radias+=0.02f;        
		updateCameraPosition();   
		} 
		
	}
			
			
		
		
	}
