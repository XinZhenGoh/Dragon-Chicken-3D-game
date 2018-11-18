package myProtocolClient;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import javax.vecmath.Vector3d;
import myProtocolClient.GhostAvatar;

import ray.networking.client.GameConnectionClient;
import ray.rml.Matrix3;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class ProtocolClient extends GameConnectionClient { 
	private MyGame game;  
	private UUID id;  
	private int currentNPCCount;
	private Vector<GhostAvatar> ghostAvatars; 
    private Vector<GhostNPC> ghostNPCs; 
    private Vector<GhostNPC> spiderNPCs; 
    private int spiderCount;
public ProtocolClient(InetAddress remAddr, int remPort,     ProtocolType pType, MyGame game) throws IOException     
{ 
	super(remAddr, remPort, pType);    
	this.game = game;   
	this.id = UUID.randomUUID();   
	this.ghostAvatars = new Vector<GhostAvatar>(); 
	this.ghostNPCs= new Vector<GhostNPC>();
	this.spiderNPCs= new Vector<GhostNPC>();
	currentNPCCount=0;
	spiderCount=0;
	
	}  
public Vector<GhostNPC> getNPCs()
{
	return ghostNPCs;
}
public UUID getID()
{
	return id;
}
@Override  
protected void processPacket(Object msg)   
{ 
	String strMessage = (String)msg;   
	String[] messageTokens = strMessage.split(",");   
	if(messageTokens.length > 0)   
	{   
		
		if(messageTokens[0].compareTo("join") == 0)         
			// receive “join”    
		{ //  format:  join, success   or   join, failure     
			System.out.println("Found a join");
			if(messageTokens[1].compareTo("success") == 0)     
			{ 
				System.out.println("Join Success");
				game.setIsConnected(true);      
				sendCreateMessage((Vector3)game.getPlayerPosition());
			}     
			if(messageTokens[1].compareTo("failure") == 0)
			{ 
				game.setIsConnected(false);  
				System.out.println("Join Failure");
			} 
		}    
		if(messageTokens[0].compareTo("bye") == 0)    
			// receive “bye”    
		{ //  format:  bye, remoteId    
			System.out.println("Bye message");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			removeGhostAvatar(ghostID);    
		}    
		if  ((messageTokens[0].compareTo("dsfr") == 0))
				// receive “dsfr”      || (messageTokens[0].compareTo("create")==0)) 
		{ 
			System.out.println("dsfr message");

			UUID ghostID = UUID.fromString(messageTokens[1]);
			sendDetailsForMessage(ghostID, game.getPlayerPosition());
		
		}    
		if(messageTokens[0].compareTo("create") == 0)  
			// rec. “create…”    
		{  
				//  etc…..  
			System.out.println("create message");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			float x = Float.valueOf(messageTokens[2]);
			float y = Float.valueOf(messageTokens[3]);
			float z = Float.valueOf(messageTokens[4]);
			Vector3f ghostPosition = (Vector3f) Vector3f.createFrom( x, y, z);
			try {
				createGhostAvatar(ghostID, ghostPosition);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}    
		if(messageTokens[0].compareTo("dmsg") == 0)  
			// rec. “wants…”    
		{  
			//  etc….. 
			System.out.println("dmsg message");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			float x = Float.valueOf(messageTokens[2]);
			float y = Float.valueOf(messageTokens[3]);
			float z = Float.valueOf(messageTokens[4]);
			Vector3f ghostPosition = (Vector3f) Vector3f.createFrom( x, y, z);
			
			try {
				createGhostAvatar(ghostID, ghostPosition);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}    
		if(messageTokens[0].compareTo("move") == 0)  
			// rec. “move...”    
		{  
			//  etc…..  
			//System.out.println("Move message lol");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			float x = Float.valueOf(messageTokens[2]);
			float y = Float.valueOf(messageTokens[3]);
			float z = Float.valueOf(messageTokens[4]);
			Vector3f ghostPosition =(Vector3f) Vector3f.createFrom( x, y, z);
			updateGhostAvatar(ghostID,ghostPosition);
		}  
		
		if(messageTokens[0].compareTo("rotate") == 0)  
			// rec. “move...”    
		{  
			//  etc…..  
			//System.out.println("Rotate message");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			//fix here
			float x = Float.valueOf(messageTokens[2]);	

			updateGhostAvatarRot(ghostID, x);
		}
		
		if(messageTokens[0].compareTo("shoot") == 0)     
		{  
			System.out.println("shoot received");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			GhostAvatar avatar = getGhost(ghostID);
			game.ghostFire(avatar);
		}
if(messageTokens[0].compareTo("ring") == 0)     
		{  
			System.out.println("ring received");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			GhostAvatar avatar = getGhost(ghostID);
			game.ghostRing(avatar);
		}
		
		if(messageTokens[0].compareTo("drop") == 0)     
		{  
			System.out.println("drop received");
			UUID ghostID = UUID.fromString(messageTokens[1]);
			GhostAvatar avatar = getGhost(ghostID);
			game.dropRing();
			//game.ghostRing(avatar);
		}
			
		
		// handle updates to NPC positions   
		  // format: (mnpc,npcID,x,y,z)    
	if(messageTokens[0].compareTo("mnpc") == 0)   
	{ 
		//System.out.println("recieved mnpc message from server");
		int ghostID = Integer.parseInt(messageTokens[1]);     
		Vector3 ghostPosition = Vector3f.createFrom(   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[3]),   Float.parseFloat(messageTokens[4]));      
		String ghostType = messageTokens[5];     
		//try to get the ghost at the given position
		//if that NPC doesnt exist, catch the error and make a new one
		//if it does exist, update the npc location
		boolean success=true;
		try{
			ghostNPCs.get(ghostID);
		}
		catch(Exception e)
		{
			System.out.println("Creating an NPC");
			currentNPCCount++;
			success=false;
			try {
				createGhostNPC(ghostID, ghostPosition, ghostType);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}   
		}
		if(success){
			updateGhostNPC(ghostID, ghostPosition);   
		}
		
		/*if(currentNPCCount==0)
		{
			
		}*/
		
	}
	//handle change of state for npc format id, state
	if(messageTokens[0].compareTo("snpc") == 0)   
	{ 
		int ghostID = Integer.parseInt(messageTokens[1]);   
		int state= Integer.parseInt(messageTokens[2]);     
		//Vector3 ghostPosition = Vector3f.createFrom(   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[2]));      
		updateGhostNPCState(ghostID, state);   
	} 
	if(messageTokens[0].compareTo("range") == 0)   //some one else is in range so send in range message
	{ 
		UUID ghostID = UUID.fromString(messageTokens[1]);  
		Vector3 ghostPosition = Vector3f.createFrom(   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[3]),   Float.parseFloat(messageTokens[4]));      
		int npcID=Integer.valueOf(messageTokens[5]);
		GhostInRange(ghostID, ghostPosition, npcID);
		//Vector3 ghostPosition = Vector3f.createFrom(   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[2]));      
		  
	} 
	if(messageTokens[0].compareTo("changedTarget") == 0)
	{
		UUID targetID = UUID.fromString(messageTokens[1]);  
		int npcID=Integer.valueOf(messageTokens[2]);
		npcTargetChanged(targetID, npcID);
		
	}
	if(messageTokens[0].compareTo("ThisIsAnAWorthyGame") == 0)
	{
		try {
			FirstClientMakesSpiders();
			System.out.println("ProtClient Recieved firstclient Message");
		} catch (IOException e) {
			System.out.println("Error in ProtClient! trouble making player first client");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	if(messageTokens[0].compareTo("spiderMoved") == 0)
	{
		int spiderID = Integer.parseInt(messageTokens[1]);     
		Vector3 spiderPosition = Vector3f.createFrom(   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[3]),   Float.parseFloat(messageTokens[4]));      
		String spiderType = messageTokens[5];     
		
		//try to get the ghost at the given position
		//if that NPC doesnt exist, catch the error and make a new one
		//if it does exist, update the npc location
		boolean success=true;
		try{
			spiderNPCs.get(spiderID);
					}
		catch(Exception e)
		{
			//System.out.println("Creating an NPC");
			spiderCount++;
			success=false;
			System.out.println("ProtClient Recieved spider New message, creating new spider");

			try {
				createSpider(spiderID, (Vector3f)spiderPosition, spiderType);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}   
		}
		if(success){
			updateSpiderLocation(spiderID, spiderPosition);   
		}
		
		
	}
	if(messageTokens[0].compareTo("hitSpider") == 0)     
	{  
		
		UUID ghostID = UUID.fromString(messageTokens[1]);
		int spiderId= Integer.valueOf(messageTokens[2]);
		if(game.getIfFirst())
		{
			try {
				deleteHitSpider(spiderId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		if(messageTokens[0].compareTo("hitDragon") == 0)     
		{  
			
			UUID ghostID = UUID.fromString(messageTokens[1]);
			int dragonId= Integer.valueOf(messageTokens[2]);
			try {
				ghostNPCs.get(dragonId).decDragonHealth();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Error gettign the npc");
			}
		
		}
		if(messageTokens[0].compareTo("deadNPC") == 0)     
		{  
			
			UUID ghostID = UUID.fromString(messageTokens[1]);
			int dragonId= Integer.valueOf(messageTokens[2]);
			try {
				deadNPC(dragonId);
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Error getting rid of dragon");
			}
		
		}
			
	
	}

	
}
public void npcTargetChanged(UUID ghostId, int npcID)
{
	ghostNPCs.get(npcID).setTarget(this.getGhost(ghostId).getNode());
	
}
	

public void GhostInRange(UUID ghostID, Vector3 ghostPosition, int NPCindex)
{
	//GhostAvatar avatar = getGhost(ghostID);
	ghostNPCs.get(NPCindex).getNode().lookAt(ghostPosition);
	GhostAvatar avatar = getGhost(ghostID);
	ghostNPCs.get(NPCindex).setTarget(avatar.getNode());
	ghostNPCs.get(NPCindex).changeState(1);
	
}

		public void askForNPCinfo()   
		{ 
			try     
			{ 
				sendPacket(new String("needNPC," + id.toString()));     
			}     catch (IOException e)     
			{ 
				e.printStackTrace();   
			} 
		}
		
		public void updateGhostNPCState(int ghostID, int state)
		{
			ghostNPCs.get(ghostID).changeState(state);
			
		}


		public void sendTargetMoved(int npcIndex, Vector3 location) {  //send message let NPC know player is in range of X, NPC should now be aware of location
				// etc….. 
				try {
					String message = new String("mtarget," + id.toString());
					message += ","+Integer.toString(npcIndex) + "," + location.x() + "," + location.y() + "," + location.z()+","+Integer.toString(npcIndex);;
					sendPacket(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
		public void sendNPCinRange(UUID avatarID, Vector3 location, int npcIndex) {  //send message let NPC know player is in range of X, NPC should now be aware of location
			// etc….. 
			try {
				String message = new String("range," + id.toString());
				message += "," + location.x() + "," + location.y() + "," + location.z()+","+Integer.toString(npcIndex);;
				sendPacket(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
//	public void sendNPCreallyClose(UUID avatarID, Vector3 location, int npcIndex) {  //send message let NPC know player is very close and should change response accordingly
//		// etc….. 
//		try {
//			String message = new String("close," + id.toString());
//			message += "," + location.x() + "," + location.y() + "," + location.z()+","+Integer.toString(npcIndex);
//			
//			sendPacket(message);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		}
		public void sendNPCcollision(UUID avatarID, Vector3 location, int npcIndex) {  //send message let NPC know player has collided, attack or follow should continue
			// etc…..
			
			try {
				String message = new String("collide," + id.toString());
				message += "," + location.x() + "," + location.y() + "," + location.z()+","+Integer.toString(npcIndex);;
				sendPacket(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
	
	public void sendJoinMessage() 
	//  format:  join, localId 
	{ 
		try   
		{ //should the avatar be sent here???? if players can pick avatar, must then change response to join message, or maybe it doesnt matter
			sendPacket(new String("join," + id.toString()));  
		} 
		catch (IOException e)  
		{ 
			e.printStackTrace(); 
		} 
	} 
	public void sendCreateMessage(Vector3 pos) {
		//  format:  (create, localId, x,y,z)  
		try   
		{ 
			String message = new String("create," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();   
			sendPacket(message);  
			}   
		catch (IOException e)  
		{  
			e.printStackTrace(); 
		} 
		}   
	public void sendByeMessage() {  
		//  etc….. 
		try {
			sendPacket(new String("bye," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		} 
	public void sendDetailsForMessage(UUID remId, Vector3 pos) {
		//  etc….. 
		try {
			String message = new String("dsfr," + id.toString());
			message += "," + remId + "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		} 
	public void sendMoveMessage(Vector3 vector3) {  
		// etc….. 
		try {
			String message = new String("move," + id.toString());
			message += "," + vector3.x() + "," + vector3.y() + "," + vector3.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	public void sendChangeState(int NPCid, int state) {  
		// etc….. 
		try {
			String message = new String("snpc," + id.toString());
			message += "," + String.valueOf(NPCid)+","+ String.valueOf(state) ;
			sendPacket(message);
			System.out.println("client send change state!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	public void sendRotateMessage(String rot) {  
		// etc….. 
		try {
			//fix
			//System.out.println("send-rotate message sent");
			
			String message = new String("rotate," + id.toString());
			message += "," + rot;
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	
	public void sendShootMessage() {  
		// etc….. 
		try {
			//System.out.println("shoot");
			String message = new String("shoot," + id.toString());
			message += ",";
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
		public void sendCollectedRingMessage() {  
		// etc….. 
		try {
			//System.out.println("shoot");
			String message = new String("ring," + id.toString());
			message += ",";
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	
		public void sendDropMessage() {  
		// etc….. 
		try {
			//System.out.println("shoot");
			String message = new String("drop," + id.toString());
			message += ",";
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	
	private void createGhostAvatar(UUID ghostID, Vector3 ghostPosition) throws IOException{
		// TODO Auto-generated method stub
			GhostAvatar avatar = new GhostAvatar(ghostID, ghostPosition);
			ghostAvatars.addElement(avatar);
			game.addGhostAvatarToGameWorld((GhostAvatar)avatar.getAvatar());
		}
		private void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub
			GhostAvatar avatar = getGhost(ghostID);
			ghostAvatars.remove(avatar);
			game.removeFromGameWorld(avatar.getAvatar());
	}
		public void updateGhostAvatar(UUID ghostID, Vector3f ghostPos)
		{
			GhostAvatar avatar = getGhost(ghostID);
			avatar.updatePosition(ghostPos);
		}
		
		
		
		public void updateGhostAvatarRot(UUID ghostID, float ghostPos)
		{
			GhostAvatar avatar = getGhost(ghostID);
			avatar.rotate(ghostPos);
		}
		private GhostAvatar getGhost(UUID ghostID){
			Iterator<GhostAvatar> iterator = ghostAvatars.iterator();
			GhostAvatar avatar = null;
			boolean Discovered = false;
			// loop untill avatar is discovered or end of iterator
			while(!Discovered && iterator.hasNext()){
				avatar = iterator.next();
				if(avatar.getID() == ghostID){
					Discovered = true;
				}
			}
			return avatar;
		}
		public void createGhostNPC(int id, Vector3 position, String newType) throws IOException   
		  {  
			  GhostNPC newNPC = new GhostNPC(id, position, newType);      
			  System.out.println("In protoclient, create ghostNPC type is "+newType);
			  ghostNPCs.add(newNPC);   
			  
			  game.addGhostNPCtoGameWorld(newNPC);   
			  } 
		
		  public void updateGhostNPC(int id, Vector3 position)   
		  {  
			  if(ghostNPCs.get(id).getNode().getLocalPosition().x()!=position.x())
				  {
				  	ghostNPCs.get(id).locationHasChanged(position);  
				  
				  	//game.updateSceneryVerticalPosition(ghostNPCs.get(id).getNode());
				  }
			  
			  }  
		  // handle updates to NPC positions   
		  // format: (mnpc,npcID,x,y,z)    if(messageTokens[0].compareTo("mnpc") == 0)   { int ghostID = Integer.parseInt(messageTokens[1]);     Vector3 ghostPosition = Vector3f.createFrom(   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[2]),   Float.parseFloat(messageTokens[2]));      updateGhostNPC(ghostID, ghostPosition);   }   public void askForNPCinfo()   { try     { sendPacket(new String("needNPC," + id.toString()));     }     catch (IOException e)     { e.printStackTrace();   } }
		  public GhostNPC getNPC(int id)
		  {
			  return ghostNPCs.get(id);
		  }
		 public void sendTargetChangedMessage(int NPCid)
		 {
			 try {
					//fix
					//System.out.println("send-rotate message sent");
					Vector3 pos= ghostNPCs.get(NPCid).getNode().getLocalPosition();
					String message = new String("changedTarget," + this.id.toString());
					message += "," + String.valueOf(NPCid);
					message += "," + pos.x() + "," + pos.y() + "," + pos.z();
					
					sendPacket(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			 
		 }
		 public void sendNPCMoveMessage(GhostNPC npc)
		 {
			 try {
					//fix
					//System.out.println("send-rotate message sent");
					Vector3 pos= npc.getNode().getLocalPosition();
					
					String message = new String("NPCmoved," + this.id.toString());
					message += "," + String.valueOf(npc.getID());
					message += "," + pos.x() + "," + pos.y() + "," + pos.z();
					/*if(npc.getState()==2)
					{
						System.out.println("IN prot client, npc location is "+npc.getNode().getLocalPosition()+"\n message= "+message);
					
					}*/
					sendPacket(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
		 }
		 public void deadNPC(int id) {
			 //detach the node from the parent
			 ghostNPCs.get(id).getNode().getParent().detachChild(ghostNPCs.get(id).getNode());
			 game.getEngine().getSceneManager().destroySceneNode(ghostNPCs.get(id).getNode().getName());
			 resetNPCListAfterDeath(id);
		 }
		 
		 public void resetNPCListAfterDeath(int id)
		 {
			 Vector<GhostNPC> tempList=new Vector<GhostNPC>();
			 int tempVecIndex=0;
			 for (int i=0; i< ghostNPCs.size(); i++)
			 {
				 
				 if(i<id)// if at the one to remove
				 {
					 //just add to the list
					 //increase j
					 tempList.add(ghostNPCs.get(i));
					 tempVecIndex++;
				 }
				 else if (i!=id) //ie i is greater than j
				 {

					 //change the npcs id to equal tempVecIndex
					 ghostNPCs.get(i).setIndex(tempVecIndex);
					 //add to the temp vector
					 tempList.add(ghostNPCs.get(i));
					 tempVecIndex++;
				 }
				 
			 }
		 }
		 public void changeState(int NPCid, int newState)
		 {
			 ghostNPCs.get(NPCid).changeState(newState);
		 }
		 public void lookAt(int NPCid, Vector3f loc)
		 {
			 ghostNPCs.get(NPCid).getNode().lookAt(loc);
		 }
		 public void sendDeadNPC(int NPCid)
		 {
				try {
					//fix
					//System.out.println("send-rotate message sent");
					
					String message = new String("deadNPC," + this.id.toString());
					message += "," + String.valueOf(NPCid);
					sendPacket(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
		 }
		public void FirstClientMakesSpiders() throws IOException
		 {
			 System.out.println("recived first client message calling game function");
			game.firstClientSetUp();
			
		 }
		public void addSpider(GhostNPC spider)
		{
			this.spiderNPCs.add(spider);
		}
		
		 public Vector<GhostNPC> getSpiderVect()
		 {
			 return this.spiderNPCs;
		 }
		 public void sendSpiderMovedMessage(GhostNPC spider)
		 
		 {
			 try {
				// System.out.println("sending spider moved");
					//fix
					//System.out.println("send-rotate message sent");
					Vector3 pos= spider.getNode().getLocalPosition();
					
					String message = new String("spiderMoved," + this.id.toString());
					message += "," + String.valueOf(spider.getID());
					message += "," + pos.x() + "," + pos.y() + "," + pos.z();
					/*if(npc.getState()==2)
					{
						System.out.println("IN prot client, npc location is "+npc.getNode().getLocalPosition()+"\n message= "+message);
					
					}*/
					sendPacket(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			 
		 }
	
		 public void createSpider( int spiderID, Vector3f spiderPosition, String spiderType) throws IOException
		 {
			 GhostNPC spider =new GhostNPC(spiderID, spiderPosition, "spider");
			 spider.setSpiderInitLoc(spiderPosition);
			 spiderNPCs.add(spider);
			 System.out.println("******************creating new spider in protClient*******************");
			 game.addGhostNPCtoGameWorld(spider);
		 }
		public void updateSpiderLocation(int spiderID, Vector3 spiderPosition)
		{
			try {
			spiderNPCs.get(spiderID).getNode().setLocalPosition(spiderPosition);
			}catch(Exception e)
			{
				System.out.println("ERROR line 641, couldnt find spider with that id");
			}
		}
		public void sendHitSpiderMessage(int spiderIndex) throws IOException
		{
			
			String message = new String("hitSpider," + this.id.toString());
			message += "," + String.valueOf(this.spiderNPCs.get(spiderIndex).getID());
			sendPacket(message);
		}
		public void deleteHitSpider(int spiderId) throws IOException
		{
			game.regenerateSpider(spiderId);
		}
		public void sendHitDragon(int npcId) throws IOException
		{
			try {
				ghostNPCs.get(npcId).decDragonHealth();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Error gettign the npc");
			}
			String message = new String("hitDragon," + this.id.toString());
			message += "," + String.valueOf(npcId);
			sendPacket(message);
		}
}