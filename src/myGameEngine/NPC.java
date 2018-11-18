package myGameEngine;

//import ray.input.GenericInputManager;
import ray.rage.scene.generic.*;
import ray.rage.game.Game;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import java.util.Random;
import java.util.UUID;
public class NPC {
	private double locx,locy,locz;
	private int currentState;
	private int maxStates;
	private UUID targetId;
	//private SceneNode node;
	//private SceneNode target;//target if in attack/chase mode
	//private SceneNode treasureToGuard;// represents location of treasure to pretect
	private Vector3 targetPos;
	private long timeSinceLast;
	private double centerX;
	private double centerZ;
	private double treasurelocz;
	private double treasurelocx;
	private boolean s1AtCenter;
	private boolean state0MRight;
	private Vector3f npcVec;
	private int dragonHealth;
	
	//GenericSceneManager sm;
	private String type;
	public NPC(String t)
	{
	
		type=t;
		currentState=0;// guard state
		maxStates=5;
		state0MRight=true;
		s1AtCenter=false;
		dragonHealth= 15;
		//sm=new GenericSceneManager();
		//((SceneManager) sm).createSceneNode("NPC");
		//node=((SceneManager) sm).getSceneNode("NPC");
	//	node=GenericSceneNode();
		//target=((SceneManager) sm).createSceneNode("target");
		initLocation();
	}
	public int getDragonHealth()
	{
		return this.dragonHealth;
	}
public void decDragonHealth()
{
	if(this.dragonHealth!=0)
	{this.dragonHealth-=1;}
}
	public String getType()
	{
		return type;
	}
	//***CHANGE*********************
	//should use random range function below
	//Merely easier to test if the npcs are close to origin, and I dont have to look for them
	//****CHANGE****************/
	public void initLocation()
	{
		switch(this.type) {
		case ("dragon"):
			//locx=getRandomNumberInRange(50, 60);
			locy=2.8f;
			//locz=getRandomNumberInRange(-3, -1);
			locx=15.0;
			locz=-2.0;
			//node.setLocalPosition(Vector3f.createFrom((float) locx, (float)locy, (float)locz));
			centerX=locx;
			centerZ=locz;
			npcVec=(Vector3f) Vector3f.createFrom((float)locx, (float)locy, (float)locz);
			//treasureToGuard=((SceneManager) sm).createSceneNode("treasureNode");
			//treasureToGuard.setLocalPosition(node.getLo);
			//treasureToGuard.moveBackward(1f);
			treasurelocz=locz;
			treasurelocx=locx-1f;
		case("spider"):
			locy=1f;
			//locz=getRandomNumberInRange(-3, -1);
			locx=13;
			locz=2;
			//node.setLocalPosition(Vector3f.createFrom((float) locx, (float)locy, (float)locz));
			centerX=locx;
			centerZ=locz;
			npcVec=(Vector3f) Vector3f.createFrom((float)locx, (float)locy, (float)locz);
			
			break;
		case("genericNPC"):
			locy=1f;
			//locz=getRandomNumberInRange(-3, -1);
			locx=5;
			locz=1;
			//node.setLocalPosition(Vector3f.createFrom((float) locx, (float)locy, (float)locz));
			centerX=locx;
			centerZ=locz;
			npcVec=(Vector3f) Vector3f.createFrom((float)locx, (float)locy, (float)locz);
			break;
		}
	}
	public void changeTarget(UUID id, Vector3 loc)
	{
		targetId=id;
		targetPos=loc;
	}
	public void setTargetID(UUID id)
	{
		targetId=id;
	}
	public UUID getTargetID()
	{
		return targetId;
	}
	public void timeChange(long time)
	{
		if( (time-timeSinceLast)/10000000>50)// was 50, then 100
		{
			//System.out.println("current time is: "+time+"  time was "+ timeSinceLast);
			timeSinceLast=time;
			if(currentState==0)
			
			{
				state0MRight=!state0MRight;
			}
			
		}
	}
	public void createTarget(Vector3 loc)
	{
		targetPos=(Vector3) loc;
	}
	public void updateTargetLoc(Vector3 newloc)
	{
		targetPos=(Vector3) newloc;
	}
	public void follow()
	{
		
	}
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public double getX()
	{
		return locx;
	}
	public double getY()
	{
		return locy;
	}
	public double getZ()
	{
		return locz;
	}
	public void changeLocation(Vector3f newLoc)
	{
		locx=newLoc.x();
		locy=newLoc.y();
		locz=newLoc.z();
	
	}
		
	
	public void moveTowardCenter()
	{
		if(!s1AtCenter)
		{
			//npcVec=(Vector3f)Vector3f.createFrom((float)locx, (float)locy, (float)locz);
			//SceneNode temp=node;
			if(state0MRight )
		
			{
				
				//Vector3 temp=(Vector3f)Vector3f.createFrom((float)locx-0.5f, (float)locy, (float)locz);
				//if(npcVec.sub(temp).length()<.6)
				if(Math.abs((locx+.05f)-centerX)<=.6f|| locx==centerX)
				{
					//close enough to original spot place at center spot
					locx=centerX;
					locz=centerZ;
					s1AtCenter=true;
					System.out.println("At Center state 1");
				}
				else //not close enough so just go left towards center
				{
					//node.moveLeft(.5f);
					locx=locx-0.05f;
				//	locz=node.getLocalPosition().z();
					
				}
			}
			else
			{
			//	temp.moveRight(0.5f);
				//if(node.getLocalPosition().sub(temp.getLocalPosition()).length()<.6)
				//Vector3 temp=(Vector3f)Vector3f.createFrom((float)locx+0.5f, (float)locy, (float)locz);
				//if(npcVec.sub(temp).length()<.6)
				if(Math.abs((locx+.05f)-centerX)<=.6f|| locx==centerX)
				{
					//close enough to original spot place at center spot
					locx=centerX;
					locz=centerZ;
					s1AtCenter=true;
				}
				else //not close enough so just go left towards center
				{
					//node.moveRight(.5f);
					locx=locx+0.05f;
					//locx=node.getLocalPosition().x();
				//	locz=node.getLocalPosition().z();
					
				}
			}
		}
	}
	public void updateLocation()
	
	{
		//...
		//move based on state
		
		
		switch(currentState) {
		
		//state 0 patroll
		case 0:
			
			if(state0MRight)
			{
				//node.moveRight((float) 0.5);
				//locx=node.getLocalPosition().x();
				//locz=node.getLocalPosition().z();
				locx=locx-0.05f;
			}
			else
			{
				//node.moveLeft((float) 0.5);
				//locx=node.getLocalPosition().x();
				//locz=node.getLocalPosition().z();
				locx=locx+0.05f;
			
			}
			break;
		//state 1 move to center and stare at possible opponent
		case 1:
			moveTowardCenter(); //will check if at center
			//System.out.println("state changed to 1");

		case 2:
			//state 2 move to attack, (until close enough to attack), chase if needed
			
			
		case 3:// state 3, close enough to attack, so fight
			
		case 4: //state 4, opponent is dead or out of range so return home and then go to state 0
			
		//state 5, dead, remove
		}
		
		
	}
	
	public Vector3 getLocation()
	{
		return ((Vector3f) (Vector3f.createFrom((float)locx , (float)locy, (float)locz)));
	}
	public void changeMaxState(int newMax)
	{
		if( newMax>0)// cant have zero or less states
		{
			maxStates=newMax;
		}
	}
	public void setState(int newState)
	{
		if(newState<=maxStates)
		{
			currentState=newState;
		}
	}
	public int getState()
	{
		return currentState;
	}
	
	

}
