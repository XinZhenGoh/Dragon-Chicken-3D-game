package myProtocolClient;

import javax.swing.text.html.parser.Entity;

import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class GhostNPC {
	
	
		
		private int currentState;
		private int id;   
		private SceneNode node;   
		private SkeletalEntity entity;   
		private Vector3 npcLoc;
		private SceneNode target;//target if in attack/chase mode
	//	private SceneNode treasureToGuard;// represents location of treasure to pretect
		private Vector3f	treasureLoc;
		private long timeSinceLast;
		private float centerX;
		private float centerZ;
		private boolean s1AtCenter;
		private boolean state0MRight;
		private boolean hasTarget;
		private String type;
		private SkeletalEntity sk;
		private Vector3f spiderInitLoc;
		private int regenCount;
		private int dragonHealth;
		public GhostNPC(int id, Vector3 position, String t)     
		// constructor   
		{   
			dragonHealth= 15;
			regenCount=0;
			type=t;
			this.id = id; 
			currentState=0;
			npcLoc=position;
			hasTarget=false;
		}
		public boolean targetExists() {
			return this.hasTarget;
		}
		
		public String getType()
		{
			return type;
		}
		public int getID()
		{
			return id;
		}
		
		public Vector3f getTreasureVector()
		{
			return treasureLoc;
		}
		public void createTreasureVectore()
		{
			
			treasureLoc=(Vector3f) this.getNode().getLocalPosition();
			
			
		}
	//	public SceneNode getTreasureNode()
		//{
	//		return treasureToGuard;
	//	}
		public void setTarget(SceneNode n)
		{
			target=n;
			hasTarget=true;
		}
		public void removeTarget()
		{
			target=null;
			hasTarget=false;
		}
		public SceneNode getTarget() {
			return this.target;
		}
		public int getState()
		{
			return currentState;
		}
		public void setIndex(int i)
		{
			this.id=i;
		}
		public void setGhostEntitiy(SkeletalEntity e)
		{
			this.entity=e;
		}
		public void setNode(SceneNode n)
		{
			this.node=n;
		}
		public SceneNode getNode()
		{
			return node;
		}
		public void changeState(int newState)
		{
			currentState=newState;
		}
		public void setPosition()   
		{   
			node.setLocalPosition(npcLoc); 
			
			
		}   
		public void locationHasChanged(Vector3 newLoc)
		{
			node.setLocalPosition(newLoc);
			npcLoc=newLoc;
		}
		public Vector3 getPosition( )   
		{   
			return npcLoc;
		} 
		public void setSkeletalEntity(SkeletalEntity s)
		{
			this.sk=s;
		}
		public SkeletalEntity getSkeletalEntity()
		{
			return sk;
		}
		public void setSpiderInitLoc(Vector3f loc)
		{
			this.spiderInitLoc=loc;
		}
		public Vector3f getSpiderInitLoc()
		{
			return this.spiderInitLoc;
		}
		public void incRegenCount()
		{
			this.regenCount++;
		}
		public int getRegenCount()
		{
			return this.regenCount;
		}
		public void decDragonHealth()
		{
			this.dragonHealth-=1;
		}
		public int getDragonHealth()
		{
			return this.dragonHealth;
		}
		
	
}
