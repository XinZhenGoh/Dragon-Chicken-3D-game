package myProtocolClient;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import ray.networking.IGameConnection.ProtocolType;
import static ray.rage.scene.SkeletalEntity.EndType.*;
import javax.script.ScriptEngine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.vecmath.Vector3d;

import myGameEngine.AliveController;
import myGameEngine.Camera3PController;
import myGameEngine.Camera3PKeyboard;
import myGameEngine.ColorAction;
import myGameEngine.DisplaySettingsDialog;
import myGameEngine.KeyboardControls;

import myGameEngine.SendCloseConnectionPacketAction;
import myGameEngine.ShootingController;
import myGameEngine.ShowXYZ;
import myGameEngine.moveSpiderGenerator;
import ray.audio.AudioManagerFactory;
import ray.audio.AudioResource;
import ray.audio.AudioResourceType;
import ray.audio.IAudioManager;
import ray.audio.Sound;
import ray.audio.SoundType;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.networking.IGameConnection.ProtocolType;
import ray.rage.Engine;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.game.Game;
import ray.rage.game.VariableFrameRateGame;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.RenderWindow;
import ray.rage.rendersystem.Renderable.DataSource;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.Viewport;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.Light;
import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SceneObject;
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.SkyBox;
import ray.rage.scene.Tessellation;
import ray.rage.scene.Camera;
import ray.rage.scene.Camera.Frustum.Projection;
import ray.rage.scene.controllers.OrbitController;
import ray.rage.scene.controllers.RotationController;
import ray.rage.util.BufferUtil;
import ray.rage.util.Configuration;
import ray.rml.Degreef;
import ray.rml.Matrix4;
import ray.rml.Matrix4f;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsObject;
import ray.physics.PhysicsEngineFactory;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsObject;
import ray.physics.PhysicsEngineFactory;

public class MyGame extends VariableFrameRateGame  implements MouseListener, MouseMotionListener{
	
	//Fixed variables - dont change
	private String serverAddress;  
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient = this.getPClient();
	private boolean isClientConnected;
	private Vector<UUID> gameObjectsToRemove;
    RenderSystem rs;
    //GL4RenderSystem rs;
    //scripting
	protected ScriptEngine jsEngine;
	protected File scriptFile3;
	//HUD elements
	float elapsTime = 0.0f;
	String elapsTimeStr, dispStr;
	int elapsTimeSec = 0;
	
    private Camera myCamera;		
    private SceneManager s;
    private InputManager im;
    private Camera3PKeyboard orbitKbController;
    KeyboardControls kbControls1;
    //mouse
    private Robot robot; // these are additional variable declarations
    private Canvas canvas;
    private RenderWindow rw;
    private float prevMouseX, prevMouseY, curMouseX, curMouseY;
    private boolean isRecentering; //indicates the Robot is in action
	 int left;
	 int top;
	 int widt;
	 int hei;
	 int centerX;
	 int centerY;

    
    //Skies and materials
    private SkyBox             skyBox;
    private Color              skyColor = Color.PINK.brighter().brighter().brighter().brighter();
    private Light              skyLight, orbitLight;
    private Material mat;
    private Texture tex;
    private TextureState texState;
	//physics
    private SceneNode myRootNode;
    private SceneNode originNode, sceneryNode, bulletN, spiderN,ringNode, dolphinN, ghostN, riderNode, ballNode, groundNode;
	private final static String GROUND_E = "Ground";
	private final static String GROUND_N = "GroundNode";
	private PhysicsEngine physicsEng;
	private PhysicsObject spiderObj,ballObj, gndPlaneP;
	private boolean running = true;
	private int bulletCount = 0,ghostbulletCount = -1;
	private float charge = 100;
	private int playerHealth = 100;
	private boolean collected = false;
	ShootingController sc;
	Iterable<Node> collisionArray;
	Vector<GhostNPC> nodeArray;
	Entity bulletE;
	SkeletalEntity spiderE;
	private int fired;
	private boolean avatarPresent = false;
	double[] temptf;
	moveSpiderGenerator ms;
	private String bulletName, spiderName;
	Vector<Node> spiders = new Vector<Node>();
	Vector<Node> bullets = new Vector<Node>();
	Vector<Node> hearts = new Vector<Node>();
	Vector<Node> ring = new Vector<Node>();
	Vector<Node> scenery = new Vector<Node>();
	Vector<SkeletalEntity> spiderz = new Vector<SkeletalEntity>();
	float generateTime = 1000f;
	private int spiderCount = 0;
	Material mat2;
	TextureState tstate;
	public static String avt;
	String ringInfo = "Ring not obtained";
	//just added
	public int dragonCount, genericNPCCount;
	private boolean ringFree = true;
	private boolean ghostExist = false;

	
	private final int SPIDERMAX=10;
	private boolean isFirstClient;
	
	private IAudioManager audioMgr;
	private Sound monsterSound, BellSound, shootSound, ReloadSound;
	AudioResource resource1, resource2, resource3, resource4,
    resource5,resource6;
	
	public MyGame( String serverAddr, int sPort)
	{
        super();
    	dragonCount=0;
        spiderCount=0;
        isFirstClient=false;
        genericNPCCount=0;
        //health=10;
		 this.serverAddress = serverAddr;   
	     this.serverPort = sPort;   
	      this.serverProtocol = ProtocolType.UDP;
	      setupNetworking();
	}
	
	
	 public static void main(String[] args) {
		 String hostIP = JOptionPane.showInputDialog("Please input hostIP: ");
		int port = Integer.parseInt(JOptionPane.showInputDialog("Please input port: "));
		avt = String.valueOf((JOptionPane.showInputDialog("Please enter avatar: ")));
			    
		 MyGame game = new MyGame(hostIP, port);
	        try {
	            game.startup();
	         //   ProtocolClient pc= new ProtocolClient(InetAddress remAddr, int remPort,     ProtocolType.UDP, game);     

	            game.run();
	        } catch (Exception e) {
	            e.printStackTrace(System.err);
	        } finally {
	            game.shutdown();
	            game.exit();
	        }
	    }
	 @Override
	 protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		 rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
//         GraphicsDevice gd = ge.getDefaultScreenDevice();
//         DisplayMode curMode = gd.getDisplayMode();
//         DisplaySettingsDialog dsd = new DisplaySettingsDialog(ge.getDefaultScreenDevice());
//         dsd.showIt();
//
//         JFrame myFrame;
//         Canvas rendererCanvas;
//         rendererCanvas = rs.getCanvas();
//
//         rs.createRenderWindow(dsd.getSelectedDisplayMode(), true);
 }
	 
	 
	 
	  @Override
	  protected void setupCameras(SceneManager sm, RenderWindow rw) {
		  
	       SceneNode rootNode = sm.getRootSceneNode();
	        myCamera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
	        rw.getViewport(0).setCamera(myCamera);
			
	        myCamera.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
	        myCamera.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
	        myCamera.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
	        myCamera.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));

	        SceneNode cameraNode = rootNode.createChildSceneNode(myCamera.getName() + "Node");
	        cameraNode.attachObject(myCamera);
	        myCamera.setMode('n');   
	        myCamera.getFrustum().setFarClipDistance(1000.0f); 
	        
	        //mouse
	        initMouseMode(rs, rw);
	    }
		

	  @Override
	    protected void setupScene(Engine eng, SceneManager sm) throws IOException {

				  
		   //end of test animation
		    s=sm;
		    
	        //skybox
	        skyBox = createSkyBox(eng, sm, "SkyBox", 1, ".jpg");
	        sm.setActiveSkyBox(skyBox);
	  
	        skyLight = sm.createLight("SkyBoxLight", Light.Type.DIRECTIONAL);
	        skyLight.setDiffuse(skyColor);
	        skyLight.setSpecular(skyColor);
	        skyLight.setRange(10f);
	        SceneNode lightNode = sm.getRootSceneNode().createChildSceneNode(skyLight.getName() + "Node");
	        lightNode.attachObject(skyLight);
	        lightNode.setLocalPosition(1, 1, 1);
	        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
	        
	        orbitLight = sm.createLight("RotationLight", Light.Type.POINT);
	        orbitLight.setDiffuse(skyColor);
	        orbitLight.setSpecular(skyColor);
	        SceneNode orbitLightNode = sm.getRootSceneNode().createChildSceneNode(orbitLight.getName() + "Node");
	        orbitLightNode.attachObject(orbitLight);
	        //orbitLightNode.setLocalPosition(1, 1, 1);
	        orbitLightNode.moveForward(3f);
	        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
	        
	        Light ringLight = sm.createLight("RingLight", Light.Type.SPOT);
	        ringLight.setDiffuse(skyColor);
	        ringLight.setSpecular(skyColor);
	        SceneNode ringLightNode = sm.getRootSceneNode().createChildSceneNode(ringLight.getName() + "Node");
	        ringLightNode.attachObject(ringLight);
	        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
	        
			Entity ballE = sm.createEntity("ball1", "Skull.obj");
			ballNode = sm.getRootSceneNode().createChildSceneNode("Ball1Node");
			//ballNode.setLocalPosition(dolphinN.getLocalPosition());
			ballNode.setLocalScale(5f,5f,5f);
			ballNode.moveUp(10f);
			ballNode.attachChild(orbitLightNode);
			ballNode.attachObject(ballE);
			
			Entity SceneryE = sm.createEntity("volcano", "volcano.obj");
			sceneryNode = sm.getRootSceneNode().createChildSceneNode("VolcanoNode");
			sceneryNode.moveLeft(100f);
			sceneryNode.setLocalScale(30f,30f,30f);
			sceneryNode.attachObject(SceneryE);

	        
			// Ground plane
			Entity groundEntity = sm.createEntity(GROUND_E, "cube.obj");
			groundNode = sm.getRootSceneNode().createChildSceneNode(GROUND_N);
			groundNode.attachObject(groundEntity);
			groundNode.setLocalPosition(0, 0, 0);
        	mat = eng.getMaterialManager().createManualAsset("FloorMaterial");
	        mat.setEmissive(Color.CYAN.darker().darker());
	        groundEntity.setMaterial(mat);
			Texture tex3 = sm.getTextureManager().getAssetByPath("snow.jpg");
			TextureState tstate3 = (TextureState) sm.getRenderSystem()
			.createRenderState(RenderState.Type.TEXTURE);
			tstate3.setTexture(tex3);
			groundEntity.setRenderState(tstate3);
	
	        //setting material and texture
	        tex = eng.getTextureManager().getAssetByPath("red.jpeg");
      	    texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
     	    texState.setTexture(tex);
        	mat = eng.getMaterialManager().createManualAsset("DogMaterial");
       	    mat = sm.getMaterialManager().getAssetByPath("default.mtl");
	        mat.setEmissive(Color.ORANGE.darker().darker().darker().darker().darker().darker().darker().darker().darker());
	        
	        Entity dolphinE = sm.createEntity("myDolphin", avt);
	        dolphinE.setPrimitive(Primitive.TRIANGLES);
    	    //dolphinE.setRenderState(texState);
	        dolphinE.setMaterial(mat);
	        
	        //character
	        dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
	        dolphinN.moveBackward(20f);
	        dolphinN.moveUp(1f);
	        dolphinN.attachObject(dolphinE);
	        dolphinN.scale(0.3f,0.3f,0.3f);     
	        riderNode = sm.getRootSceneNode().createChildSceneNode(dolphinN.getName() + "Node");
	        dolphinN.attachChild(riderNode);
	        //riderNode.yaw(Degreef.createFrom(-90f));
	        riderNode.moveBackward(15f);
	        riderNode.moveUp(10f);
	        riderNode.attachObject(myCamera);


	        //tesselation
	        Tessellation tessE = s.createTessellation("tessE", 6); 
	        tessE.setSubdivisions(8f);  
	        SceneNode tessN =sm.getRootSceneNode().createChildSceneNode("TessN");  
	        tessN.attachObject(tessE);  
	        tessN.scale(100, 60, 100);   
	        tessE.setHeightMap(this.getEngine(), "terrain2.jpg");  
	        tessE.setNormalMap(this.getEngine(), "terrain2_Normal.jpg");  
	        tessE.setTexture(this.getEngine(), "bottom.jpg");  
	        this.updateVerticalPosition();
	        
	        sc = new ShootingController();
	        sm.addController(sc);

	  
	   RandomGen rg2 = new RandomGen(eng,sm,this);    
       setupInputs(sm);
	   setupOrbitCamera(eng, sm);
	   
		initPhysicsSystem();
		createRagePhysicsWorld();
		Entity ringE = sm.createEntity("ring1", "ring.obj");
		ringNode = sm.getRootSceneNode().createChildSceneNode("ringNode");
		ringNode.attachChild(ringLightNode);
		//ringNode.setLocalPosition(ballNode.getLocalPosition());
		
		ringNode.moveUp(1f);
		ringNode.moveForward(10f);
		ringNode.setLocalScale(0.5f,0.5f,0.5f);
		ringNode.attachObject(ringE);
		ring.add(ringNode);
		
		originNode = sm.getRootSceneNode().createChildSceneNode("originNode");
		originNode.moveUp(1f);
		originNode.moveForward(10f);
		originNode.setLocalPosition(ringNode.getLocalPosition());
		originNode.setLocalScale(0.5f,0.5f,0.5f);
		originNode.attachChild(ringNode);
		
//		  double[] temptf = toDoubleArray(ringNode.getLocalTransform().toFloatArray());
//		  ringObj = physicsEng.addSphereObject(physicsEng.nextUID(),0.1f, temptf, 0f);
//		  ringNode.setPhysicsObject(ringObj);
		
		RotationController rc = new RotationController();
		ms = new moveSpiderGenerator(this.protClient);
		rc.addNode(ballNode);
		rc.addNode(ringNode);
		sm.addController(rc);
		sm.addController(ms);
		
		tex = sm.getTextureManager().getAssetByPath("sp.jpg");
		  tstate = (TextureState) sm.getRenderSystem()
		  .createRenderState(RenderState.Type.TEXTURE);
		  tstate.setTexture(tex);
		
		 mat2 = eng.getMaterialManager().createManualAsset("SpiderMaterial");
		
		initAudio(sm);
	    }
	  
	  //phyInit
	  private void initPhysicsSystem()
	  { String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
	  float[] gravity = {0, -6f, 0};
	  physicsEng = PhysicsEngineFactory.createPhysicsEngine(engine);
	  physicsEng.initSystem();
	  physicsEng.setGravity(gravity);
	  }
	  
	  //phyWorld
	  private void createRagePhysicsWorld()
	  { float mass = 1.0f;
	  float up[] = {0,1,0};
	  
//	  double[] temptf = toDoubleArray(ballNode.getLocalTransform().toFloatArray());
//	  ballObj = physicsEng.addSphereObject(physicsEng.nextUID(),mass, temptf, 0f);
//	  //ball1Obj.setBounciness(0.9f);
//	  ballNode.setPhysicsObject(ballObj);
	  
	  temptf = toDoubleArray(groundNode.getLocalTransform().toFloatArray()); 
	  gndPlaneP = physicsEng.addStaticPlaneObject(physicsEng.nextUID(),temptf, up, 1f);
	  gndPlaneP.setBounciness(1.2f);
	  //gndPlaneP.setFriction(100f);
	  groundNode.scale(100f, .05f, 100f);
	  groundNode.setLocalPosition(0, 0.5F, 0);
	  groundNode.setPhysicsObject(gndPlaneP);
	  }
	  
	  //phy converting func
	  private float[] toFloatArray(double[] arr)
	  { if (arr == null) return null;
	  int n = arr.length;
	  float[] ret = new float[n];
	  for (int i = 0; i < n; i++)
	  { ret[i] = (float)arr[i];
	  }
	  return ret;
	  }
	  
	  //phy converting func  
	  private double[] toDoubleArray(float[] arr)
	  { if (arr == null) return null;
	  int n = arr.length;
	  double[] ret = new double[n];
	  for (int i = 0; i < n; i++)
	  { ret[i] = (double)arr[i];
	  }
	  return ret;
	  }
	  
	  
	    protected void loadConfiguration(Configuration config) throws IOException {
	        super.loadConfiguration(config);
	        final String baseDirectory = "assets/skyboxes/oga/";
	        config.setKeyValuePair("test.skybox1.path", baseDirectory + "ice/");
	    }
	    
	    public void updateVerticalPosition()  
	    { 
	    	SceneNode dolphinN =this.getEngine().getSceneManager().getSceneNode("myDolphinNode");  
	    	SceneNode tessN = this.getEngine().getSceneManager().getSceneNode("TessN");
	    	Tessellation tessE = ((Tessellation) tessN.getAttachedObject("tessE"));   
	    	// Figure out Avatar's position relative to plane   
	    	Vector3 worldAvatarPosition = dolphinN.getWorldPosition();   
	    	Vector3 localAvatarPosition = dolphinN.getLocalPosition();   
	    	// use avatar World coordinates to get coordinates for height   
	    	Vector3 newAvatarPosition = Vector3f.createFrom(localAvatarPosition.x(),tessE.getWorldHeight(       worldAvatarPosition.x(),       worldAvatarPosition.z())+.03f,localAvatarPosition.z()   );         

	    	dolphinN.setLocalPosition(newAvatarPosition);  
	    }     
	    public void updateSceneryVerticalPosition(SceneNode node)  
	    { 
	    	//SceneNode dolphinN =this.getEngine().getSceneManager().getSceneNode("myDolphinNode");   
	    	SceneNode tessN = this.getEngine().getSceneManager().getSceneNode("TessN");
	    	Tessellation tessE = ((Tessellation) tessN.getAttachedObject("tessE"));   
	    	// Figure out Avatar's position relative to plane   
	    	Vector3 worldSceneryPosition = node.getWorldPosition();   
	    	Vector3 localSceneryPosition = node.getLocalPosition();   
	    	// use avatar World coordinates to get coordinates for height   
	    	Vector3 newSceneryPosition = Vector3f.createFrom(localSceneryPosition.x(),tessE.getWorldHeight(       worldSceneryPosition.x(),       worldSceneryPosition.z())+.05f,localSceneryPosition.z()   );          
	    	node.setLocalPosition(newSceneryPosition);  
	    } 

	  
	  private SkyBox createSkyBox(Engine eng, SceneManager sm, String skyBoxName, int skyBoxId, String fileExtension)
	           throws IOException {
	        Configuration conf = eng.getConfiguration();
	        TextureManager tm = getEngine().getTextureManager();

	        tm.setBaseDirectoryPath(conf.valueOf("test.skybox" + skyBoxId + ".path"));
	        Texture front = tm.getAssetByPath("front" + fileExtension);
	        Texture back = tm.getAssetByPath("back" + fileExtension);
	        Texture left = tm.getAssetByPath("left" + fileExtension);
	        Texture right = tm.getAssetByPath("right" + fileExtension);
	        Texture top = tm.getAssetByPath("top" + fileExtension);
	        Texture bottom = tm.getAssetByPath("bottom" + fileExtension);

	        tm.setBaseDirectoryPath(conf.valueOf("assets.textures.path"));

	        AffineTransform xform = new AffineTransform();
	        xform.translate(0, front.getImage().getHeight());
	        xform.scale(1d, -1d);

	        front.transform(xform);
	        back.transform(xform);
	        left.transform(xform);
	        right.transform(xform);
	        top.transform(xform);
	        bottom.transform(xform);

	        SkyBox sb = sm.createSkyBox(skyBoxName + skyBoxId);
	        sb.setTexture(front, SkyBox.Face.FRONT);
	        sb.setTexture(back, SkyBox.Face.BACK);
	        sb.setTexture(left, SkyBox.Face.LEFT);
	        sb.setTexture(right, SkyBox.Face.RIGHT);
	        sb.setTexture(top, SkyBox.Face.TOP);
	        sb.setTexture(bottom, SkyBox.Face.BOTTOM);
	        return sb;
	    }

	 
	  protected void update(Engine engine) {
			
		  // build and set HUD
			rs = (GL4RenderSystem) engine.getRenderSystem();
			elapsTime += engine.getElapsedTimeMillis();
			elapsTimeSec = Math.round(elapsTime/1000.0f);
			elapsTimeStr = Integer.toString(elapsTimeSec);
			processNetworking(elapsTime); 
			//HUD settings
			im.update(elapsTime); 
			if(collected == true) {
				ringInfo = "  Ring has been collected";
			}
			else {
				ringInfo = "Ring is not obtained";
			}
			dispStr = "Time = " + elapsTimeStr + "  health = " + playerHealth +" spider = " +spiderCount + ringInfo;
			rs.setHUD(dispStr, 15, 15);

			//physics update
			if (running)
			{ Matrix4 mat;
			physicsEng.update(elapsTime);
			for (SceneNode s : engine.getSceneManager().getSceneNodes())
			{ if (s.getPhysicsObject() != null)
			{ mat = Matrix4f.createFrom(toFloatArray(
			s.getPhysicsObject().getTransform()));
			s.setLocalPosition(mat.value(0,3),mat.value(1,3),
			mat.value(2,3));
			} } } 


			if(charge<100) {
			 charge+=0.5;
			}
			
			//spidergenerate
			/*if(elapsTime>=generateTime && spiderCount <= 10) {
				try {
					
					generateSpider();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				generateTime = generateTime + 1200;
			}*/
		

		NPCcollisionDetection();
		targetCheckNPCGhost();
		//collisionArray= (Iterable<Node>) myRootNode.getChildNodes();
		
		//spider collision detection
		//for(int i=0; i< spiders.size(); i++){
		for(int i=0; i< protClient.getSpiderVect().size(); i++){
			//Vector3 dist =  spiders.elementAt(i).getLocalPosition().sub(dolphinN.getLocalPosition());
			Vector3 dist=protClient.getSpiderVect().get(i).getNode().getLocalPosition().sub(dolphinN.getLocalPosition());
			
			//spiderz.elementAt(i).update();
			protClient.getSpiderVect().get(i).getSkeletalEntity().update();
			if(dist.length()<= 2) {
				playerHealth -= 1;
				
				
			}
		}
		
//		for(int i=0; i<spiderz.size();i++) {
//			spiderz.elementAt(i).update();
//		}
		
		if(ringFree == true) {
			Vector3 dist5 = ringNode.getLocalPosition().sub(dolphinN.getLocalPosition());
			if(dist5.length()<=1 && collected == false) {
				originNode.detachChild(ringNode);
				dolphinN.attachChild(ringNode);
				ringNode.setLocalPosition(dolphinN.getLocalPosition());
				protClient.sendCollectedRingMessage();
				collected = true;
				//send collected ring
			}
			}
		
		if(collected == true) {
			Vector3 dist6 = dolphinN.getLocalPosition().sub(sceneryNode.getLocalPosition());
			if(dist6.length() <= 10) {
				System.out.println("winner chicken dinner");
				Component frame = null;
				JOptionPane.showMessageDialog(frame,
					    "Winner winner chicken dinner",
					    "Very well played",
					    JOptionPane.PLAIN_MESSAGE);
			}
			}
		

		
		for(int i=0; i< bullets.size(); i++){
			if(ghostExist == true && ringFree == false) {
				Vector3 dist4 = bullets.elementAt(i).getLocalPosition().sub(ghostN.getLocalPosition());
				if(dist4.length() <= 1) {
					System.out.println("drop the ring");
					ghostN.detachChild(ringNode);
					originNode.attachChild(ringNode);
					ringNode.setLocalPosition(originNode.getLocalPosition());
					ringFree = true;
					System.out.println("ringFree = " +ringFree + "collected = "+collected);
					protClient.sendDropMessage();
				}
			}
			for(int j=0; j< protClient.getSpiderVect().size(); j++){
				Vector3 dist =  bullets.elementAt(i).getLocalPosition().sub(dolphinN.getLocalPosition());
				//Vector3 dist2 =  spiders.elementAt(j).getLocalPosition().sub(bullets.elementAt(i).getLocalPosition());
			
				Vector3 dist2 =  protClient.getSpiderVect().get(j).getNode().getLocalPosition().sub(bullets.elementAt(i).getLocalPosition());
			//	Vector3 dist3 = spiders.elementAt(j).getLocalPosition().sub(dolphinN.getLocalPosition());
				Vector3 dist3 =  protClient.getSpiderVect().get(j).getNode().getLocalPosition().sub(dolphinN.getLocalPosition());
				
				
				
				
				if(dist2.length()<= 2) {
				bulletName = bullets.elementAt(i).getName();
					s.destroySceneNode(bulletName);
					bullets.removeElementAt(i);
				//	spiderName = spiders.elementAt(j).getName();
					//s.destroySceneNode(spiderName);
					if(this.isFirstClient)
					{
						try {
							System.out.println("resetting spider loc after shot");
							resetSpiderLocation(j);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						try {
							protClient.sendHitSpiderMessage(j);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//spiders.removeElementAt(j);
					//spiderz.removeElementAt(j);
					//System.out.println("remove");
					monsterSound.play();
					//spiderCount--;
					break;
				}

				else if (dist.length() >= 100) {
					System.out.println("removing bullet");
					bulletName = bullets.elementAt(i).getName();
					s.destroySceneNode(bulletName);
					bullets.removeElementAt(i);
					System.out.println("remove");
					break;
				}
				
				else if(dist3.length() >= 100) {
					//spiderName = spiders.elementAt(j).getName();
					spiderName=protClient.getSpiderVect().get(j).getNode().getName();
					//s.destroySceneNode(spiderName);
					if(this.isFirstClient)
					{
						try {
							System.out.println("resetting spider after too far away");
							resetSpiderLocation(j);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//spiders.removeElementAt(j);
					//spiderz.removeElementAt(j);
					break;
				}
			}
		}
		
		//Check if bullet hit the dragon
		for(int i=0; i< bullets.size(); i++){
			for(int j=0; j< protClient.getNPCs().size(); j++){
				//Vector3 dist =  bullets.elementAt(i).getLocalPosition().sub(dolphinN.getLocalPosition());
				//Vector3 dist2 =  spiders.elementAt(j).getLocalPosition().sub(bullets.elementAt(i).getLocalPosition());
			
				Vector3 dist2 =  protClient.getNPCs().get(j).getNode().getLocalPosition().sub(bullets.elementAt(i).getLocalPosition());
			//	Vector3 dist3 = spiders.elementAt(j).getLocalPosition().sub(dolphinN.getLocalPosition());
				//Vector3 dist3 =  protClient.getSpiderVect().get(j).getNode().getLocalPosition().sub(dolphinN.getLocalPosition());
				
				if(dist2.length()<= 2) {
				bulletName = bullets.elementAt(i).getName();
					s.destroySceneNode(bulletName);
					bullets.removeElementAt(i);
					if(protClient.getNPCs().get(j).getDragonHealth()>1)
					{
						try {
							protClient.sendHitDragon(j);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						try {
						protClient.deadNPC(j);
						protClient.sendDeadNPC(j);
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//protClient.sendDeadNPC(j);
					}
					break;
				}

			}
		}
		

		
		if(fired == 1) {
			fire();
			shootSound.play();
			fired = 0;
		}
		
		
		
		
//		
//		BellSound.setLocation(ballNode.getLocalPosition());
//		monsterSound.setLocation(ballNode.getLocalPosition());
		setEarParameters(s);
	      }
		
	  


		public float randomize() {
			Random r = new Random();  
			float rand = 1.0f + r.nextFloat() * (0.02f - 0.002f);
			return rand;
		}

	  protected void setupOrbitCamera(Engine eng, SceneManager sm)  
	  { 
		  SceneNode dolphin = sm.getSceneNode("myDolphinNode");   
		  SceneNode cameraN = sm.getSceneNode("MainCameraNode");
		  Camera camera = sm.getCamera("MainCamera"); 
		  String gpName = im.getFirstGamepadName();   
		  String kbName = im.getKeyboardName();  
		  String mName = im.getMouseName();
		 
		  if(kbName!=null)
		  {
			  orbitKbController = new Camera3PKeyboard(camera, cameraN, dolphin, kbName, im);
				  
		 }
		  else {
			  System.out.println("no keyboard registered");
		  }

		}  
	 private void setupNetworking()
	 {
		 gameObjectsToRemove = new Vector<UUID>();   
		 isClientConnected = false;   
		 try    
		 { 
			 protClient = new ProtocolClient(InetAddress.     getByName(serverAddress), serverPort, serverProtocol, this);
			 }  
		 catch (UnknownHostException e) 
		 { 
			 e.printStackTrace();   
		} 
		 catch (IOException e) 
		 { 
			 e.printStackTrace();   
		}   
		 if (protClient == null)   
		 { 
			 System.out.println("missing protocol host");
		}   
		else   
		{ // ask client protocol to send initial join message    
			//to server, with a unique identifier for this client    
			protClient.sendJoinMessage(); 
			
		 }
		 
		 
		 
	 }
	    
	              
public InputManager getIm() {
	    	 return im;
	     }
	     protected void setupInputs(SceneManager sm)  { 
		     	im = new GenericInputManager();   
		     	String kbName = im.getKeyboardName();   
		     	String gpName = im.getFirstGamepadName();   
		     	String mName = im.getMouseName();
		     	
		     	kbControls1 = new KeyboardControls(kbName,this);
		     	//test
	     		ColorAction colorAction = new ColorAction(sm,this);

		     	// Add exit action
		     	SendCloseConnectionPacketAction close = new SendCloseConnectionPacketAction(protClient, isClientConnected);
		     }
	 
	     protected void processNetworking(float elapsTime)  
	     { 
	    	 if (protClient != null)    
	    		 protClient.processPackets();    
	    	 Iterator<UUID> it = gameObjectsToRemove.iterator();   
	    	 while(it.hasNext())   
	    	 { 
	    		 s.destroySceneNode(it.next().toString());
	    	}   
	    	 gameObjectsToRemove.clear();  
	    }
	     

	     public void keyPressed(KeyEvent e) {
	         switch (e.getKeyCode()) {
	             case KeyEvent.VK_G:

	                 
	             case KeyEvent.VK_R:
	            	 ReloadSound.play();
	            	 //reloadhere
	            	 break;
	         }
	         super.keyPressed(e);
	     }

	     public SceneNode getDolphin()
	     {
	  	   return dolphinN;
	     }
	     
		public void setIsConnected(boolean b) {
			// TODO Auto-generated method stub
			isClientConnected=b;
		}
		public Vector3 getPlayerPosition()  { 
			return dolphinN.getLocalPosition();  
		}   
		
		public void removeFromGameWorld(Object avatar) {
			// TODO Auto-generated method stub
			if(avatar != null) gameObjectsToRemove.add(((GhostAvatar) avatar).getID()); 
		}
		public void addGhostAvatarToGameWorld(GhostAvatar avatar)            throws IOException  
		{ 
			if (avatar != null)   
			{ 
				ghostExist = true;
				Entity ghostE = s.createEntity("ghost", "spider.obj");
				
				ghostE.setPrimitive(Primitive.TRIANGLES);
				ghostN = s.getRootSceneNode().createChildSceneNode(avatar.getID().toString());
				ghostN.attachObject(ghostE);
				ghostN.setLocalPosition(avatar.getStartPosition());
				avatar.setNode(ghostN); 
				avatar.setEntity(ghostE);
				avatar.setPosition(ghostN.getLocalPosition());  
	
				ghostN.setLocalScale(0.3f,0.3f,0.3f);
				avatarPresent = true;
				} 
			}
		public ProtocolClient getPClient()
		{
			return protClient;
			
		}
		 public File getScript() {
	    	 return scriptFile3;
	     }
		 
		 
		 public void NPCcollisionDetection()
	     {
	    	 
		 	 Vector<GhostNPC> nodeArray= protClient.getNPCs();
		 	
		 	 for(int i=0; i< nodeArray.size(); i++)
		 	  {
		 		Vector3 dist = dolphinN.getLocalPosition().sub(nodeArray.get(i).getNode().getLocalPosition());
		 		if(dist.length()<=30)
		 		{
		 			NPCHandleCollision(dist.length(), nodeArray.get(i), i);
		 		}
		 	  }
	 	}

// public void addGhostNPCtoGameWorld(GhostNPC newNPC) throws IOException 
//		 {
//			 System.out.println("adding NPC!!");
//			 SkeletalEntity dragonE =
//						s.createSkeletalEntity("dragonNPC", "dragonV1Mesh.rkm", "dragonV1Skeleton.rks");
//						Texture tex2 = s.getTextureManager().getAssetByPath("dragonV1.png");
//						TextureState tstate2 = (TextureState) s.getRenderSystem()
//						.createRenderState(RenderState.Type.TEXTURE);
//						tstate2.setTexture(tex2);
//						dragonE.setRenderState(tstate2);
//						
//			        	mat = this.getEngine().getMaterialManager().getAssetByPath("default.mtl");
//				        mat.setEmissive(Color.ORANGE.darker().darker().darker().darker().darker().darker());
//				        dragonE.setMaterial(mat);
//								  
//			SceneNode myGhostNPCnode= s.getRootSceneNode().createChildSceneNode("dragonNPC");
//			myGhostNPCnode.attachObject(dragonE);
//			myGhostNPCnode.scale(0.1f, 0.1f, 0.1f);
//			myGhostNPCnode.setLocalPosition(newNPC.getPosition());
//			myGhostNPCnode.setLocalScale(0.3f, 0.3f, 0.3f);
//			myGhostNPCnode.moveUp(3f);
//
//			newNPC.setGhostEntitiy( dragonE);
//			newNPC.changeState(0);
//			newNPC.setNode(myGhostNPCnode);
//			
//			newNPC.locationHasChanged(myGhostNPCnode.getLocalPosition());
//			 System.out.println("location final is "+ myGhostNPCnode.getLocalPosition());
//			 
//		 }
		 //animation
		 private void doTheWalk()
		 { SkeletalEntity spiderE =
		 (SkeletalEntity) getEngine().getSceneManager().getEntity("spiderAv");
		 spiderE.stopAnimation();
		 spiderE.playAnimation("walkAnimation", 0.60f, LOOP, 0);
		 }
		 
		 
		 private void 
		 initMouseMode(RenderSystem r, RenderWindow w)
		 { rw = w;
		 rs = r;
		 Viewport v = rw.getViewport(0);
		 left = rw.getLocationLeft();
		 top = rw.getLocationTop();
		 widt = v.getActualScissorWidth();
		 hei = v.getActualScissorHeight();
		 centerX = left + widt/2;
		 centerY = top + hei/2;
		 isRecentering = false;
		 try // note that some platforms may not support the Robot class
		 { robot = new Robot(); } catch (AWTException ex)
		 { throw new RuntimeException("Couldn't create Robot!"); 
		 }
			if(elapsTimeSec > 2) {
				recenterMouse();	
			}
		 prevMouseX = centerX; // 'prevMouse' defines the initial
		 prevMouseY = centerY; // mouse position
		 // also change the cursor
//		 Image faceImage = new
//		 ImageIcon("./assets/images/face.gif").getImage();
//		 Cursor faceCursor = Toolkit.getDefaultToolkit().
//		 createCustomCursor(faceImage, new Point(0,0), "FaceCursor");
//		 canvas = rs.getCanvas();
//		 canvas.setCursor(faceCursor);
		 }
		 
		 
		 private void recenterMouse()
		 {// use the robot to move the mouse to the center point.
		 // Note that this generates one MouseEvent.
		 Viewport v = rw.getViewport(0);
		 int left = rw.getLocationLeft();
		 int top = rw.getLocationTop();
		 int widt = v.getActualScissorWidth();
		 int hei = v.getActualScissorHeight();
		 centerX = left + widt/2;
		 centerY = top + hei/2;
		 isRecentering = true;
		 //Canvas canvas = rs.getCanvas();
		 robot.mouseMove((int)centerX, (int)centerY);
		 }

		 public void fire() {
				if(charge >= 1) {

			        bulletE = null;
					try {
						bulletE = s.createEntity("bullet"+ Integer.toString(bulletCount), "sphere.obj");
					} catch (IOException e) {
						// TODO Auto-generated catch blockaaaa
						e.printStackTrace();
						System.out.println("here");
					}
			        bulletE.setPrimitive(Primitive.TRIANGLES);
		    	    //bulletE.setRenderState(texState);
			        //Material mat2 = new Material();
			        //mat.setEmissive(Color.BLUE.brighter().brighter().brighter());
			        //bulletE.setMaterial(mat);
			        
			        //bulletN = s.getRootSceneNode().createChildSceneNode(bulletE.getName() + "Node");
			        bulletN = s.getRootSceneNode().createChildSceneNode(bulletE.getName() + "Node");
			        bulletN.attachObject(bulletE);
			        bulletN.setLocalScale(0.6f,0.6f,0.6f);
			        bulletN.setLocalPosition(dolphinN.getLocalPosition());
			        bulletN.setLocalRotation(dolphinN.getWorldRotation());
			        //bulletN.setLocalRotation(dolphinN.getLocalForwardAxis());
			        bulletN.moveUp(0.5f);
			        bulletCount++;
			        charge = 0;
			        sc.addNode(bulletN);
			        bullets.add(bulletN);
			       protClient.sendShootMessage();
					}
		 }

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			//shooting
			fired = 1;
	        
		}



		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			// if robot is recentering and the MouseEvent location is in the center,
			// then this event was generated by the robot
			if (isRecentering &&
			centerX == e.getXOnScreen() && centerY == e.getYOnScreen())
			{ isRecentering = false; } // mouse recentered, recentering complete
			else
			{ // event was due to a user mouse-move, and must be processed
			curMouseX = e.getXOnScreen();
			curMouseY = e.getYOnScreen();
			float mouseDeltaX = prevMouseX - curMouseX;
			float mouseDeltaY = prevMouseY - curMouseY;
			if(elapsTimeSec > 2) {
			yaw(mouseDeltaX);
			}
			//pitch(mouseDeltaY);
			prevMouseX = curMouseX;
			prevMouseY = curMouseY;
			// tell robot to put the cursor to the center (since user just moved it)
			recenterMouse();
			prevMouseX = centerX; //reset prev to center
			prevMouseY = centerY;
			}
			
		}
		public void pitch(float mouseDeltaY)
		{
			
		}
		
		public void yaw(float mouseDeltaX) {
			//System.out.println(mouseDeltaX);
			if(mouseDeltaX > 0) {
			dolphinN.yaw(Degreef.createFrom(1f));
			protClient.sendRotateMessage(String.valueOf(1f)); 
			}
			
			else if(mouseDeltaX < 0) {
			dolphinN.yaw(Degreef.createFrom(-1f));
			protClient.sendRotateMessage(String.valueOf(-1f)); 
			}
			
			if(mouseDeltaX == 0) {
				//stay
			}
		} // not shown here


		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		public void initAudio(SceneManager sm)
		{ 
		audioMgr = AudioManagerFactory.createAudioManager(
		"ray.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize())
		{ System.out.println("Audio Manager failed to initialize!");
		return;
		}

		resource1 = audioMgr.createAudioResource("bell.wav",
		AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource("pain.wav",
		AudioResourceType.AUDIO_SAMPLE);
		resource3 = audioMgr.createAudioResource("Shoot2.wav",
		AudioResourceType.AUDIO_SAMPLE);
		resource4 = audioMgr.createAudioResource("reload.wav",
		AudioResourceType.AUDIO_SAMPLE);
		
		
		BellSound = new Sound(resource1,
		SoundType.SOUND_EFFECT, 100, false);
		monsterSound = new Sound(resource2,
		SoundType.SOUND_EFFECT, 100, false);
		shootSound = new Sound(resource3,
		SoundType.SOUND_EFFECT, 100, false);
		ReloadSound = new Sound(resource4,
		SoundType.SOUND_EFFECT, 100, false);
		
		
		BellSound.initialize(audioMgr);
		monsterSound.initialize(audioMgr);
		shootSound.initialize(audioMgr);
		ReloadSound.initialize(audioMgr);
		
		
		BellSound.setMaxDistance(10.0f);
		BellSound.setMinDistance(0.5f);
		BellSound.setRollOff(5.0f);
		monsterSound.setMaxDistance(10.0f);
		monsterSound.setMinDistance(0.5f);
		monsterSound.setRollOff(5.0f);
		shootSound.setMaxDistance(10.0f);
		shootSound.setMinDistance(0.5f);
		shootSound.setRollOff(5.0f);
		ReloadSound.setMaxDistance(10.0f);
		ReloadSound.setMinDistance(0.5f);
		ReloadSound.setRollOff(5.0f);
		

		setEarParameters(sm);

		}
		

public void setEarParameters(SceneManager sm)
{ //SceneNode dolphinNode = sm.getSceneNode("myDolphinNode");
Vector3 avDir = riderNode.getWorldForwardAxis();
// note - should get the camera's forward direction
// - avatar direction plus azimuth
audioMgr.getEar().setLocation(riderNode.getWorldPosition());
audioMgr.getEar().setOrientation(avDir, Vector3f.createFrom(0,1,0));
}



public void ghostFire(GhostAvatar avatar) {
	//System.out.println("ghost fired");
    bulletE = null;
	try {
		bulletE = s.createEntity("bullet"+ Integer.toString(ghostbulletCount), "sphere.obj");
	} catch (IOException e) {
		// TODO Auto-generated catch blockaaaa
		e.printStackTrace();
		System.out.println("here");
	}
    bulletE.setPrimitive(Primitive.TRIANGLES);
    //bulletE.setRenderState(texState);
    //Material mat2 = new Material();
    //mat.setEmissive(Color.BLUE.brighter().brighter().brighter());
    bulletE.setMaterial(mat2);
    
    //bulletN = s.getRootSceneNode().createChildSceneNode(bulletE.getName() + "Node");
    bulletN = s.getRootSceneNode().createChildSceneNode(bulletE.getName() + "Node");
    bulletN.attachObject(bulletE);
    bulletN.setLocalScale(0.6f,0.6f,0.6f);
    bulletN.setLocalPosition(avatar.getPosition());
    bulletN.setLocalRotation(avatar.getRotation());
    //bulletN.setLocalRotation(dolphinN.getLocalForwardAxis());
    bulletN.moveUp(0.5f);
    ghostbulletCount--;
    charge = 0;
    sc.addNode(bulletN);
}

public void ghostCollide(GhostAvatar avatar, SceneNode node) {
	
	
	
	
	
}

public void playerDamaged() {
	//health -= 15;
}

public void playerAddHealth() {
	//playerHealth += 20;
}

public void ghostAddHealth() {
	//playerHealth += 20;
}

public void ghostDamaged() {
	//ghostHealth -= 15
}

//ghost collect
public void ghostRing(GhostAvatar avatar) {
	ghostN.attachChild(ringNode);
	ringNode.setLocalPosition(avatar.getPosition());
	originNode.detachChild(ringNode);
	ringFree = false;
	
}

//ringcollide
public void dropRing() {
	System.out.println("ring dropped");
	dolphinN.detachChild(ringNode);
	originNode.attachChild(ringNode);
	ringNode.setLocalPosition(originNode.getLocalPosition());
	ringFree = true;
	collected = false;
}

public void firstClientSetUp() throws IOException
{
	isFirstClient=true;
	ms = new moveSpiderGenerator(this.protClient);
	s.addController(ms);
	 System.out.println("In MyGame, I am the first client, create 10 spiders");
	for(int i=0;i<10;i++)
	{
		generateSpider(i);
	}
}

public void generateSpider(int index) throws IOException {
	
	spiderE = null;
	try {
		//spiderE = s.createEntity("spider"+ Integer.toString(bulletCount), "spider.obj");
		spiderE = s.createSkeletalEntity("spiderAv"+ Integer.toString(bulletCount), "Spidey.rkm", "Spidey.rks");
	
	} catch (IOException e) {
		e.printStackTrace();
	}

	spiderE.setRenderState(tstate);
    spiderE.setPrimitive(Primitive.TRIANGLES);
    spiderN = s.getRootSceneNode().createChildSceneNode(spiderE.getName() + "Node");
    spiderN.attachObject(spiderE);
    spiderN.setLocalScale(0.8f,0.8f,0.8f);
    spiderN.setLocalPosition(ballNode.getLocalPosition());
    spiderN.moveUp(0.5f);
    spiderN.setLocalRotation(ballNode.getLocalRotation());
    spiderN.moveUp(0.5f);
    bulletCount++;
    spiderE.loadAnimation("walkAnimation", "Spidey.rka");
    spiderE.playAnimation("walkAnimation", 0.6f, LOOP, 0);
    //spiderz.add(spiderE);
	  double[] temptf = toDoubleArray(spiderN.getLocalTransform().toFloatArray());
	  spiderObj = physicsEng.addSphereObject(physicsEng.nextUID(),0.1f, temptf, 0f);
	  spiderN.setPhysicsObject(spiderObj);
	  ms.addNode(spiderN);
	  //spiders.add(spiderN);
	  spiderObj.applyForce(50,0,0,0,0,0);
	  
	  GhostNPC spider=new GhostNPC(index, spiderN.getLocalPosition(), "spider");
	  spider.setNode(spiderN);
	  spider.setSkeletalEntity(spiderE);
	  spider.setSpiderInitLoc((Vector3f)spiderN.getLocalPosition());
	  
	  protClient.addSpider(spider);
	  protClient.sendSpiderMovedMessage(spider);
	  spiderCount++;
	
	
}

public void targetCheckNPCGhost()
{
	 Vector<GhostNPC> nodeArray= protClient.getNPCs();
	 //Vector<GhostAvatar> ghostAvatars= protClient.getGhostAvatars();
	 for(int j=0; j<nodeArray.size(); j++)
	 {
		 if(nodeArray.get(j).targetExists())
		 {
			 nodeArray.get(j).getNode().lookAt(nodeArray.get(j).getTarget());
		 }
	 }
}


public void NPCHandleCollision(float dist, GhostNPC collidingNPC, int id)
{
	 SceneNode temp;
	 Vector3f distFromTreasure=(Vector3f) dolphinN.getLocalPosition().sub(collidingNPC.getNode().getLocalPosition());
	 
		
	 switch(collidingNPC.getType()) {
	 
	 case("dragon"):
		 if(isClosestTarget(collidingNPC))
		 {
			 distFromTreasure=(Vector3f) dolphinN.getLocalPosition().sub(collidingNPC.getNode().getLocalPosition());
				
			// System.out.println("Is closes target Dist is "+dist+" dist from treasure is "+distFromTreasure.length());
			 if ((dist<=30 && dist>25 ) || this.playerHealth<=0 ||(distFromTreasure.length()>=30f))
		 
			 {
				 //System.out.println("first if dist<=15");
				 
				 //if the target is too far, NPC should choose to return to its home 
				 //check if state is at anything other than 0 then check if it still has this node as its target
				 if(  collidingNPC.getState()!=0 && collidingNPC.getTarget()==dolphinN &&(collidingNPC.getState()!=4))
				 {
					 //check distance from home base( treasure)
					 //head home
					 if(distFromTreasure.length()>2f)
					 {
						 System.out.print("State is 4");
						 protClient.changeState(id, 4);// state in which opponent is dead or out of range
					 
						
						 collidingNPC.getNode().lookAt(collidingNPC.getTreasureVector());
						 //collidingNPC.changeState(4)
						 //collidingNPC.getNode().lookat(collidingNPC.getTreasureVector())
						 collidingNPC.getNode().moveForward(0.01f);
						 //this.updateSceneryVerticalPosition(collidingNPC.getNode());
						 protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());
				 			
						 protClient.sendNPCMoveMessage(collidingNPC);
						 protClient.sendChangeState(id, 4);
					 }
					 else
					 {
						 System.out.println("returning to state 0");
						 collidingNPC.changeState(0);// state in which opponent is dead or out of range
						 collidingNPC.getNode().setLocalPosition(collidingNPC.getTreasureVector());
						 //collidingNPC.getNode().lookat(collidingNPC.getTreasureVector());
						
						 protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());
						 protClient.sendNPCMoveMessage(collidingNPC);
						 protClient.sendChangeState(id, 0);
					 }
				 }
				 else if( collidingNPC.getTarget()==dolphinN &&(collidingNPC.getState()==4))
				 {
					 //check distance, if not at treasure head home
					 if(distFromTreasure.length()>2f)
					 {
						 //System.out.println("in first else if, state is 4 but close to home");
						 collidingNPC.getNode().lookAt(collidingNPC.getTreasureVector());
						 collidingNPC.getNode().moveForward(0.01f);
						 protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());
						 protClient.sendNPCMoveMessage(collidingNPC);
					 }
					 else {
						 System.out.println("state 4 close enough to zoom home");
						 collidingNPC.changeState(0);// state in which opponent is dead or out of range
						 collidingNPC.getNode().setLocalPosition(collidingNPC.getTreasureVector());
						 //collidingNPC.getNode().lookat(collidingNPC.getTreasureVector());
						 protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());
						 protClient.sendChangeState(id, 0);
						 protClient.sendNPCMoveMessage(collidingNPC);
					 }
					 
					 //if at treasure change to state 1
				 }
				 
			 }
			 else if(dist<=25&& dist>20)
		 		{
		 			if(collidingNPC.getState()==0)
		 			{
		 				//System.out.println("change to state1");
		 				collidingNPC.changeState(1);
		 				protClient.sendChangeState(id, 1);
		 				protClient.sendNPCinRange(protClient.getID(), dolphinN.getLocalPosition(), id);
		 				//System.out.println("Scenenode location of colliding NPC"+collidingNPC.getNode().getLocalPosition());
		 				//System.out.println("Scenenode location of protclient(id)"+protClient.getNPC(collidingNPC.getID()).getNode().getLocalPosition());
		 				
		 				collidingNPC.getNode().lookAt(dolphinN);
		 				
		 			}
		 			else if (collidingNPC.getState()==1)// if already in state 1
		 			{
		 				//1st check if Ghost is the target
		 				//if(collidingNPC.getTarget()==dolphinN)
		 				//{
		 					collidingNPC.getNode().lookAt(dolphinN); //make sure the npc is looking at the avatar
		 				
		 				
		 					
		 			}
		 		}
		 		else if( dist<20 && dist>5)
		 		{
		 			if(collidingNPC.getState()==1|| collidingNPC.getState()==0)
		 			{
		 				//System.out.println("change to state2, MOVe to attack!!!");
		 				//System.out.println(" Old location was with protoclient"+protClient.getNPC(id).getNode().getLocalPosition());
		 				//System.out.println(" Old location was with collidningNPC node"+collidingNPC.getNode().getLocalPosition());
		 				
		 				collidingNPC.changeState(2);
		 				protClient.sendChangeState(id, 2);
		 				//protClient.sendNPCclose(protClient.getID(), dolphinN.getLocalPosition(), id);
		 				collidingNPC.getNode().lookAt(dolphinN);
		 				collidingNPC.getNode().moveForward(.1f);
		 				temp=collidingNPC.getNode();
		 				//System.out.println(" Old location with Temp"+collidingNPC.getNode().getLocalPosition());
		 				
		 				temp.moveForward(.1f);
		 				//collidingNPC.getNode().moveForward(.05f);
		 				//collidingNPC.getNode().setLocalPosition(temp.getLocalPosition());
		 				//protClient.getNPC(id).getNode().setLocalPosition(temp.getLocalPosition());
		 				//protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());
		 				//System.out.println(" New location before set "+protClient.getNPC(id).getNode().getLocalPosition());
		 				//System.out.println("New location was with collidningNPC node"+collidingNPC.getNode().getLocalPosition());
		 				
		 				protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());// was temp.getlocal position
		 				//System.out.println(" New location after set with prot client"+protClient.getNPC(id).getNode().getLocalPosition());
		 				//System.out.println(" Temp Location"+temp.getLocalPosition());
		 				
		 				protClient.sendNPCMoveMessage(collidingNPC);// changed was just collidingNPC as parameter
		 			
		 				
		 			}
		 			else if(collidingNPC.getState()==2) {
		 				//System.out.println(" state2");
		 				//System.out.println(" Old location with protclient "+protClient.getNPC(id).getNode().getLocalPosition());
		 				//System.out.println(" Old location was with collidningNPC node"+collidingNPC.getNode().getLocalPosition());
		 				
		 			/*	collidingNPC.getNode().lookAt(dolphinN);
		 	//***old version****
		 				//collidingNPC.getNode().moveForward(.01f);
		 				temp=collidingNPC.getNode();
		 				System.out.println(" Old location with Temp"+collidingNPC.getNode().getLocalPosition());
		 				
		 				//temp.moveForward(.01f);
		 				//collidingNPC.getNode().setLocalPosition(temp.getLocalPosition());
		 				//protClient.getNPC(id).getNode().setLocalPosition(temp.getLocalPosition());
						
		 				temp.moveForward(.01f);
		 				collidingNPC.getNode().moveForward(.01f);
		 				System.out.println(" Old location was "+protClient.getNPC(id).getNode().getLocalPosition());
		 				System.out.println(" colliding node location is"+collidingNPC.getNode().getLocalPosition());
		 				
		 				//collidingNPC.getNode().setLocalPosition(temp.getLocalPosition());
		 				//protClient.getNPC(id).getNode().setLocalPosition(temp.getLocalPosition());
		 				protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());
		 				System.out.println(" New location after setwith prot client"+protClient.getNPC(id).getNode().getLocalPosition());
		 				System.out.println(" Temp Location"+temp.getLocalPosition());
		 				
		 				protClient.sendNPCMoveMessage(collidingNPC);
		 				 			*/
		 				//protClient.sendNPCclose(protClient.getID(), dolphinN.getLocalPosition(), id);
		 				collidingNPC.getNode().lookAt(dolphinN);
		 				collidingNPC.getNode().moveForward(.1f);
		 				//temp=collidingNPC.getNode();
		 				//System.out.println(" Old location with Temp"+collidingNPC.getNode().getLocalPosition());
		 				
		 				//temp.moveForward(.01f);
		 				//collidingNPC.getNode().moveForward(.05f);
		 				//collidingNPC.getNode().setLocalPosition(temp.getLocalPosition());
		 				//protClient.getNPC(id).getNode().setLocalPosition(temp.getLocalPosition());
		 				//protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());
		 				//System.out.println(" New location before set "+protClient.getNPC(id).getNode().getLocalPosition());
		 				//System.out.println("New location was with collidningNPC node"+collidingNPC.getNode().getLocalPosition());
		 				
		 				protClient.getNPC(id).getNode().setLocalPosition(collidingNPC.getNode().getLocalPosition());// was temp.getlocalposition
		 				//System.out.println(" New location after set with prot client"+protClient.getNPC(id).getNode().getLocalPosition());
		 			//	System.out.println(" Temp Location"+temp.getLocalPosition());
		 				
		 				protClient.sendNPCMoveMessage(collidingNPC);
			 				
		 			}
		 		}
		 		else if( dist <=.7)
		 		{
		 			//if(collidingNPC.getState()==2)
		 			//{
		 				//dont really need to change state, just decrease player health and move npc back
		 				//collidingNPC.changeState(3); 
		 				//send hit message? not needed just send when health is 0
		 			System.out.println(" attack state3");	
		 			collidingNPC.getNode().lookAt(dolphinN);
		 				//collidingNPC.getNode().moveBackward((float)((Math.abs(dist-.7)*2)));
		 				temp=collidingNPC.getNode();
		// 				temp.moveBackward((float)((Math.abs(dist-.7)*2)));
		 				temp.moveBackward(4f);
		 				//collidingNPC.getNode().setLocalPosition(temp.getLocalPosition());
		 				protClient.getNPC(id).getNode().setLocalPosition(temp.getLocalPosition());
			 			
		 				protClient.sendNPCMoveMessage(collidingNPC);
		 				this.playerHealth-=5;
		 				if(playerHealth<=0) {
		 					System.out.println("YOU'RE DEAD");
		 				}
		 				
		 			//	System.out.println("change to state3, attack commense!");
		 			//}
		 		
		 		}
		 }
		 break;
	 case("spider"):
		 break;
	 case("genericNPC"):
		 break;
	 }
	 
}


public boolean isClosestTarget(GhostNPC npc)
{
	if(!npc.targetExists())
	{
		protClient.getNPC(npc.getID()).setTarget(dolphinN);
		protClient.sendTargetChangedMessage(npc.getID());
		return true;
	}
	else if (npc.getTarget()==dolphinN)
	{
		return true;
	}
	else {
		Vector3f distFromTarget=(Vector3f) npc.getNode().getLocalPosition().sub(npc.getTarget().getLocalPosition());
		Vector3f distFromThisClient=(Vector3f) npc.getNode().getLocalPosition().sub(dolphinN.getLocalPosition());
		if(distFromThisClient.length()<distFromTarget.length())
		{
			protClient.getNPC(npc.getID()).setTarget(dolphinN);
			protClient.sendTargetChangedMessage(npc.getID());
			return true;
		}
		else
		{
			return false;
		}
	}
}

public void addGhostNPCtoGameWorld(GhostNPC newNPC) throws IOException 
{
System.out.println("adding NPC!!");
	switch(newNPC.getType())
	{
	case("dragon"):
	
			//System.out.println("creating new dragon");
			 SkeletalEntity dragonE =
						s.createSkeletalEntity("dragonNPC", "dragonV1Mesh.rkm", "dragonV1Skeleton.rks");
						Texture tex2 = s.getTextureManager().getAssetByPath("dragonV1.png");
						TextureState tstate2 = (TextureState) s.getRenderSystem()
						.createRenderState(RenderState.Type.TEXTURE);
						tstate2.setTexture(tex2);
						dragonE.setRenderState(tstate2);
						
			        	mat = this.getEngine().getMaterialManager().getAssetByPath("default.mtl");
				        mat.setEmissive(Color.ORANGE.darker().darker().darker().darker().darker().darker());
				        dragonE.setMaterial(mat);
								  
			SceneNode myGhostNPCnode= s.getRootSceneNode().createChildSceneNode("dragonNPC"+String.valueOf(this.dragonCount)+"Node");
			myGhostNPCnode.attachObject(dragonE);
			myGhostNPCnode.scale(0.1f, 0.1f, 0.1f);
			//System.out.println("Dragon NODE location before set location"+ myGhostNPCnode.getLocalPosition());
			myGhostNPCnode.setLocalPosition(15f, 2.5f,3f );
			//System.out.println("Dragon NODE location after set location"+ myGhostNPCnode.getLocalPosition());
			myGhostNPCnode.setLocalScale(0.3f, 0.3f, 0.3f);
			
			//myGhostNPCnode.moveUp(15f);

			newNPC.setGhostEntitiy( dragonE);
			newNPC.changeState(0);
			newNPC.setNode(myGhostNPCnode);
			
			newNPC.locationHasChanged(myGhostNPCnode.getLocalPosition());
			 System.out.println("location final is "+ myGhostNPCnode.getLocalPosition());
			 System.out.println("location final in vector "+ protClient.getNPC(newNPC.getID()).getNode().getLocalPosition());
			 this.dragonCount++;
			 newNPC.createTreasureVectore();
			 break;
	case("spider"):
		System.out.println("creating new spider in game, addNPCs");
	SkeletalEntity spiderE = null;
	try {
		//spiderE = s.createEntity("spider"+ Integer.toString(bulletCount), "spider.obj");
		spiderE = s.createSkeletalEntity("spiderAv"+ Integer.toString(bulletCount), "Spidey.rkm", "Spidey.rks");
		bulletCount++;
	} catch (IOException e) {
		e.printStackTrace();
	}

	spiderE.setRenderState(tstate);
    spiderE.setPrimitive(Primitive.TRIANGLES);
    spiderN = s.getRootSceneNode().createChildSceneNode("spider" +String.valueOf(this.spiderCount)+ "Node");
    spiderN.attachObject(spiderE);
    spiderN.setLocalScale(0.8f,0.8f,0.8f);
    //spiderN.setLocalPosition(ballNode.getLocalPosition());
   // spiderN.moveUp(0.5f);
    spiderN.setLocalPosition(newNPC.getPosition());
    spiderN.setLocalRotation(ballNode.getLocalRotation());
    //spiderN.moveUp(0.5f);
    //bulletCount++;
    spiderE.loadAnimation("walkAnimation", "Spidey.rka");
    spiderE.playAnimation("walkAnimation", 0.6f, LOOP, 0);
    
	  
	  //spiderObj.applyForce(50,0,0,0,0,0);
	  
	//  GhostNPC spider=new GhostNPC(index, spiderN.getLocalPosition(), "spider");
	 newNPC.setNode(spiderN);
	 newNPC.setSkeletalEntity(spiderE);
	  
	  
	  //Already Done in when protocal client recieves the messageprotClient.addSpider(spider);
	  //protClient.sendSpiderMovedMessage(spider);

		 this.spiderCount++;
	break;
	case("genericNPC"):
		String nodeName= "genericNPC"+String.valueOf(this.genericNPCCount)+"Node";
		break;
	
	}		 
}


public void resetHealth()
{
	 System.out.println("Health reset");
	 this.playerHealth=10;
}
public void plusOrMinusHealth(float signedFloat)
{
	 System.out.println("Health changed");
	 this.playerHealth+=signedFloat;
}
public float getHealth()
{
	 return this.playerHealth;
}

public boolean getIfFirst()
{
	return this.isFirstClient;
}

public void resetSpiderLocation(int spiderIndex) throws IOException
{
	//protClient.getSpiderVect().get(spiderIndex).getNode().setLocalPosition(protClient.getSpiderVect().get(spiderIndex).getSpiderInitLoc());
	// destroy scene node, and destroy entity
	ms.removeNode(protClient.getSpiderVect().get(spiderIndex).getNode());
	s.destroySceneNode(protClient.getSpiderVect().get(spiderIndex).getNode().getName());
	
	//s.destroyEntity(protClient.getSpiderVect().get(spiderIndex).getSkeletalEntity());
	//set node= null, and entity = null
	bulletCount++;
	protClient.getSpiderVect().get(spiderIndex).setNode(null);
	protClient.getSpiderVect().get(spiderIndex).setSkeletalEntity(null);
	//
	regenerateSpider(spiderIndex);
}

public void regenerateSpider(int index)throws IOException{
	try {
		//spiderE = s.createEntity("spider"+ Integer.toString(bulletCount), "spider.obj");
		spiderE = s.createSkeletalEntity("spiderAv"+ Integer.toString(bulletCount), "Spidey.rkm", "Spidey.rks");
	
	} catch (IOException e) {
		e.printStackTrace();
	}

	spiderE.setRenderState(tstate);
    spiderE.setPrimitive(Primitive.TRIANGLES);
    spiderN = s.getRootSceneNode().createChildSceneNode(spiderE.getName() + "Node");
    spiderN.attachObject(spiderE);
    spiderN.setLocalScale(0.8f,0.8f,0.8f);
    spiderN.setLocalPosition(ballNode.getLocalPosition());
    spiderN.moveUp(0.5f);
    spiderN.setLocalRotation(ballNode.getLocalRotation());
    spiderN.moveUp(0.5f);
    bulletCount++;
    spiderE.loadAnimation("walkAnimation", "Spidey.rka");
    spiderE.playAnimation("walkAnimation", 0.6f, LOOP, 0);
    //spiderz.add(spiderE);
	  double[] temptf = toDoubleArray(spiderN.getLocalTransform().toFloatArray());
	  spiderObj = physicsEng.addSphereObject(physicsEng.nextUID(),0.1f, temptf, 0f);
	  spiderN.setPhysicsObject(spiderObj);
	  ms.addNode(spiderN);
	  //spiders.add(spiderN);
	  spiderObj.applyForce(50,0,0,0,0,0);
	  
	
		protClient.getSpiderVect().get(index).setNode(spiderN);
		protClient.getSpiderVect().get(index).setSkeletalEntity(spiderE);
		protClient.getSpiderVect().get(index).setSpiderInitLoc((Vector3f)spiderN.getLocalPosition());
	  
	  
	  protClient.sendSpiderMovedMessage(protClient.getSpiderVect().get(index));	
}


public KeyboardControls getKbControls(){
	return this.kbControls1;
}
		

	     
}
