package gameServer;


import java.io.IOException; import java.net.InetAddress; 
import java.lang.Object;
import java.util.UUID;

import myGameEngine.NPC;
import myGameEngine.NPCcontroller;
import myProtocolClient.GhostAvatar;
import ray.networking.server.GameConnectionServer; 
import ray.networking.server.IClientInfo;
import ray.rml.Vector3;
import ray.rml.Vector3f; 
public class GameServerUDP extends GameConnectionServer<UUID>  
{   
	private boolean firstClientSet;		//if there is a single client
	 private NPCcontroller npcCtrl; 
	public GameServerUDP(int localPort) throws IOException    
	{ 
		super(localPort, ProtocolType.UDP); 
		//System.out.println("In constructor");
		firstClientSet=false;
	}
	@Override   
	public void processPacket(Object o, InetAddress senderIP, int senderPort)
	{     	//System.out.println("process Packet\n Mesage="+(String)o);
		String message = (String) o;     
		String[] msgTokens = message.split(",");
		
		if(msgTokens.length > 0)     
		{   
			// case where server receives a JOIN message   
			// format:  join,localid  
			if(msgTokens[0].compareTo("join") == 0)  
			{    
				try    
				{ 
					IClientInfo ci;         
					ci = getServerSocket().createClientInfo(senderIP, senderPort);    
					UUID clientID = UUID.fromString(msgTokens[1]);    
					addClient(ci, clientID);    
					sendJoinedMessage(clientID, true);   
					if(!firstClientSet)
					{
						firstClientSet=true;
						System.out.println("found the first client in Server");
						sendGenerateClientBasedNPCs(clientID);
						 
						
						
					}
					//else
					//{
				//		relayCurrentNPCInformation(clientID);
				//	}
				}    
				catch (IOException e)    
				{ 
					System.out.println("IOexception in process packets");
					e.printStackTrace();
					
				} 
			}   
			// case where server receives a CREATE message   
			// format:  create,localid,x,y,z  
			if(msgTokens[0].compareTo("create") == 0)  
			{    
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};   
				sendCreateMessages(clientID, pos);   
				sendWantsDetailsMessages(clientID);  
			}   
			// case where server receives a BYE message  
			// format:  bye,localid  
			if(msgTokens[0].compareTo("bye") == 0)  
			{ 
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);   
				removeClient(clientID);  
				}  
			// case where server receives a DETAILS-FOR message  
			if(msgTokens[0].compareTo("dsfr") == 0)  
			{ 
				UUID senderID = UUID.fromString(msgTokens[1]);
				UUID targetID = UUID.fromString(msgTokens[2]);
				String[] position = {msgTokens[3],msgTokens[4],msgTokens[5]};
				sndDetailsMsg(senderID,targetID, position);

			}  
			// case where server receives a MOVE message  
			if(msgTokens[0].compareTo("move") == 0)  
			{  
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] position = {msgTokens[2],msgTokens[3],msgTokens[4]};
				sendMoveMessages(clientID,position); 
				
				
			}
			
			if(msgTokens[0].compareTo("rotate") == 0)  
				// rec. “move...”    
			{  
				//  etc…..  
				//System.out.println("Rotate message");
				UUID clientID = UUID.fromString(msgTokens[1]);
				//fix here
				float x = Float.valueOf(msgTokens[2]);	

				sendRotateMessage(clientID,msgTokens[2]);
			}
			
			if(msgTokens[0].compareTo("shoot") == 0)     
			{  
				//System.out.println("shoot received");
				UUID clientID = UUID.fromString(msgTokens[1]);
				//GhostAvatar avatar = getGhost(ghostID);
				//game.ghostFire(avatar);
				sendShootMessage(clientID);
			}
			
			if(msgTokens[0].compareTo("ring") == 0)     
			{  
				//System.out.println("shoot received");
				UUID clientID = UUID.fromString(msgTokens[1]);
				//GhostAvatar avatar = getGhost(ghostID);
				//game.ghostFire(avatar);
				sendRingMessage(clientID);
			}
			
			if(msgTokens[0].compareTo("drop") == 0)     
			{  
				//System.out.println("shoot received");
				UUID clientID = UUID.fromString(msgTokens[1]);
				//GhostAvatar avatar = getGhost(ghostID);
				//game.ghostFire(avatar);
				sendDropMessage(clientID);
			}
			
			
			 if(msgTokens[0].compareTo("needNPC") == 0)   {
				 
				  sendNPCinfo();
				  
			}    
			  if(msgTokens[0].compareTo("mtarget") == 0)   {
					 
				 //target has moved, update value for that NPC
				  UUID clientID = UUID.fromString(msgTokens[1]);
				  String[] position = {msgTokens[2],msgTokens[3],msgTokens[4]};
				  int npcID= Integer.parseInt(msgTokens[5]);
				  
				  
			}    
			  if(msgTokens[0].compareTo("collide") == 0)   {
				  //get who it collided with
				  //check current state done in range call
					  // . . . 
				  System.out.println("recieved collide message");
					  
			  } 
			  if(msgTokens[0].compareTo("range") == 0) {
				//get who is in range w
				  //check current state done in range 

					UUID clientID = UUID.fromString(msgTokens[1]);
					 String[] position = {msgTokens[2],msgTokens[3],msgTokens[4]};
					int npcID=Integer.valueOf(msgTokens[5]);
					sendInRangeMessages(clientID, position, npcID); 
				  System.out.println("recieved inrange message");
			  }
			  if(msgTokens[0].compareTo("close") == 0)   
			  {
				  UUID clientID = UUID.fromString(msgTokens[1]);
				  String[] position = {msgTokens[2],msgTokens[3],msgTokens[4]};
				  int npcID= Integer.parseInt(msgTokens[5]);
				 npcCtrl.changeNPCState(npcID, 2);
				 Vector3 targetPos=(Vector3) Vector3f.createFrom((float) Float.valueOf(position[0]), (float)Float.valueOf(position[1]), (float) Float.valueOf(position[2]));
				 npcCtrl.setNPCTarget(npcID, targetPos, clientID);
				 //send close to message to let them know who is 
				 System.out.println("recieved gettingCloser message");
				  
			  }
				// case where server receives a change state message 
				if(msgTokens[0].compareTo("snpc") == 0)  
				{  
					 UUID clientID = UUID.fromString(msgTokens[1]);
					int id= Integer.valueOf(msgTokens[2]);
					int state=Integer.valueOf(msgTokens[3]);
					sendStateChangedMessage(clientID, id, state); 
					System.out.println("server recieved change state!");
					
					
				}
				if(msgTokens[0].compareTo("deadNPC") == 0)  
				{  
					UUID clientID = UUID.fromString(msgTokens[1]);
					int id= Integer.valueOf(msgTokens[2]);
					removeDeadNPC(id);
					try {
						sendNPCDeadMessage(id, clientID);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(msgTokens[0].compareTo("changedTarget")==0)
				{  
					UUID clientID = UUID.fromString(msgTokens[1]);
					int id= Integer.valueOf(msgTokens[2]);
					String[] position = {msgTokens[2],msgTokens[3],msgTokens[4]};
					Vector3 pos=Vector3f.createFrom(Float.valueOf(position[0]),  Float.valueOf(position[0]), Float.valueOf(position[0]));
					//removeDeadNPC(id);
					try {
						 sendChangedTargetMessage(id, clientID, pos);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(msgTokens[0].compareTo("NPCmoved")==0)
				{ 
					UUID clientID = UUID.fromString(msgTokens[1]);
					int id= Integer.valueOf(msgTokens[2]);
					String[] position = {msgTokens[3],msgTokens[4],msgTokens[5]};
					Vector3 pos=Vector3f.createFrom(Float.valueOf(position[0]),  Float.valueOf(position[1]), Float.valueOf(position[2]));
					try {
						sendNPCMoved (id, clientID, pos);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//forward the "spider created message" or spider moved
				//if the client has no record of this spider it makes a new one with the given location
				//this also adds the npc to the spider vector
				if(msgTokens[0].compareTo("spiderMoved")==0)
				{
					UUID clientID = UUID.fromString(msgTokens[1]);
					int id= Integer.valueOf(msgTokens[2]);
					String[] position = {msgTokens[3],msgTokens[4],msgTokens[5]};
					Vector3 pos=Vector3f.createFrom(Float.valueOf(position[0]),  Float.valueOf(position[1]), Float.valueOf(position[2]));
					boolean success=true;
					try {
						npcCtrl.getSpiderList().get(id);
					}catch(Exception e)
					{
						addNewNPCtoVector( id, (Vector3f)pos);
						success=false;
					}
					
					try {
						SendSpiderMoved (id, clientID, (Vector3f) pos); //clients will check if th
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				if(msgTokens[0].compareTo("hitSpider")==0)
				{
					UUID clientID = UUID.fromString(msgTokens[1]);
					int spiderId= Integer.valueOf(msgTokens[2]);
					try {
						
					
					forwardHitSpider(spiderId, clientID);
					}catch(Exception e)
					{
						
					}

				}
				if(msgTokens[0].compareTo("hitDragon")==0)
				{
					UUID clientID = UUID.fromString(msgTokens[1]);
					int npcId= Integer.valueOf(msgTokens[2]);
					try {
						
					
					forwardHitDrag(npcId, clientID);
					}catch(Exception e)
					{
						
					}
				}
		
			  }
		}    


	
public void sendJoinedMessage(UUID clientID, boolean success)
{   
	//  format:  join, success   or   join, failure   
	try    
	{  
		String message = new String("join,");       
		if (success)  message += "success";
		else  message += "failure";
		sendPacket(message, clientID);   
		}    
	catch (IOException e)  
	{  
		e.printStackTrace(); 
		} 
	
}   
public void sendCreateMessages(UUID clientID, String[] position) 
{  
	//  format:  create, remoteId, x, y, z   
	try    
	{ 
		String message = new String("create," + clientID.toString());     
		message += "," + position[0];     
		message += "," + position[1];     
		message += "," + position[2];     
		forwardPacketToAll(message, clientID);   
		}    
	catch (IOException e)   
	{ 
		e.printStackTrace(); 
	} 
} 
	

	 
public void sndDetailsMsg(UUID clientID, UUID remoteId, String[] position) {   
	try {
		String message = new String("dmsg," + clientID);
		message += "," + position[0];
		message += "," + position[1];
		message += "," + position[2];
		sendPacket(message, remoteId);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}   
public void sendWantsDetailsMessages(UUID clientID) {  
	//  etc….. 
	try {
		String message = new String("dsfr," + clientID.toString());
		forwardPacketToAll(message, clientID);
	} catch (IOException e) {
		e.printStackTrace();
	}
}   
public void sendMoveMessages(UUID clientID, String[] position) {  
	try {
		String message = new String("move," + clientID.toString());
		message += "," + position[0];
		message += "," + position[1];
		message += "," + position[2];
		forwardPacketToAll(message, clientID);
	} catch (IOException e) {
		e.printStackTrace();
	}
} 
 
public void sendByeMessages(UUID clientID) {  
	try {
		String message = new String("bye," + clientID.toString());
		forwardPacketToAll(message, clientID);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

public void sendRotateMessage(UUID clientID, String rot) {  
	// etc….. 
	try {
		//fix
		String message = new String("rotate," + clientID.toString());
		message += "," + rot;
		forwardPacketToAll(message,clientID);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

public void sendShootMessage(UUID clientID) {  
	// etc….. 
	try {
		//System.out.println("shoot");
		String message = new String("shoot," + clientID.toString());
		message += ",";
		forwardPacketToAll(message,clientID);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	
public void sendRingMessage(UUID clientID) {  
	// etc….. 
	try {
		//System.out.println("shoot");
		String message = new String("ring," + clientID.toString());
		message += ",";
		forwardPacketToAll(message,clientID);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

public void sendDropMessage(UUID clientID) {  
	// etc….. 
	try {
		//System.out.println("shoot");
		String message = new String("drop," + clientID.toString());
		message += ",";
		forwardPacketToAll(message,clientID);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}




public void sendInRangeMessages(UUID clientID, String[] position, int NPCid) 
{  
	//  format:  create, remoteId, x, y, z   
	try    
	{ 
		String message = new String("range," + clientID.toString());     
		message += "," + position[0];     
		message += "," + position[1];     
		message += "," + position[2];
		message+=","+ String.valueOf(NPCid);
		forwardPacketToAll(message, clientID);
		
		Vector3 pos= (Vector3) Vector3f.createFrom((float) Float.valueOf(position[0]), (float) Float.valueOf(position[1]), (float) Float.valueOf(position[2]));
		npcCtrl.InRange(clientID, pos, NPCid);
		
		}    
	catch (IOException e)   
	{ 
		e.printStackTrace(); 
	} 
} 
public void sendStateChangedMessage(UUID clientID, int NPCid, int state)
{
try {
	//fix
	String message = new String("snpc," + String.valueOf(NPCid));
	message += "," + String.valueOf(NPCid);
	npcCtrl.changeNPCState(NPCid, state);
	forwardPacketToAll(message,clientID);
} catch (IOException e) {
	e.printStackTrace();
}

}
public void removeDeadNPC(int id)
{
//remove the npc from the vector
npcCtrl.removeNPCReset(id);


}
public void setNPCcontroller(NPCcontroller n)
{
npcCtrl=n;
}
public void sendNPCinfo() 
//informs clients of new NPC positions  
{  
//System.out.println("sending mnpc message");
  for (int i=0; i<npcCtrl.getNumNPCs(); i++)     
  { 
	  try        
	  { 
		  String message = new String("mnpc," + Integer.toString(i));           
	  

		  message += "," + (npcCtrl.getNPC(i)).x();
		  message += "," + (npcCtrl.getNPC(i)).y();
		  message += "," + (npcCtrl.getNPC(i)).z();
		  message += "," + (npcCtrl.getNPCType(i));
		  
		  sendPacketToAll(message);      
		  //  . . .  
		  // also additional cases for receiving messages about NPCs, such as:    
		 
	  }catch(Exception e) {
	} 
	  
  }
}
public void sendNPCDeadMessage (int npcID, UUID senderId) throws IOException
{
String message = new String("deadNPC," + senderId.toString()+","+Integer.toString(npcID));    
forwardPacketToAll(message,senderId);
}
public void sendChangedTargetMessage (int npcID, UUID targetId, Vector3 loc) throws IOException
{
npcCtrl.changeNPCTarget(npcID, targetId, loc);
String message = new String("changedTarget," + targetId.toString()+","+Integer.toString(npcID));    
forwardPacketToAll(message,targetId);
}
public void sendNPCMoved (int npcID, UUID senderId, Vector3 loc) throws IOException
{
//update this npc vector
//
npcCtrl.changNPCLoc((Vector3f)loc, npcID);
String message = new String("mnpc," +Integer.toString(npcID) +","+ loc.x()+","+ loc.y()+","+loc.z()+","+ npcCtrl.getNPCType(npcID));    
forwardPacketToAll(message,senderId);
}
//called only when recieve a spider created message
//NEVER called otherwise
//ID is just used to check for missing packets
public void addNewNPCtoVector( int npcId, Vector3f pos)
{
	NPC s= new NPC("spider");
	s.changeLocation(pos);
	npcCtrl.addNewSpider(s, npcId);
	 System.out.println("adding new spider to vector");
	
}
public void SendSpiderMoved (int id, UUID senderID, Vector3f pos) throws IOException//clients will check if th
{
	npcCtrl.changSpiderLoc((Vector3f)pos, id);
	String message = new String("spiderMoved," +Integer.toString(id) +","+ pos.x()+","+ pos.y()+","+pos.z()+","+ "spider");    
	forwardPacketToAll(message,senderID);
}
public void sendGenerateClientBasedNPCs(UUID clientID) throws IOException
{
	String message = new String("ThisIsAnAWorthyGame,Mr.Grader"); 
	
	sendPacket(message, clientID);	
	 System.out.println("Found first client in server, sending message now");
}
public void forwardHitSpider(int spiderId, UUID clientID) throws IOException {
	
	String message = new String("hitSpider," +clientID.toString()+","+Integer.toString(spiderId));    
	forwardPacketToAll(message,clientID);
//relayCurrentNPCInformation(clientID);
}
public void forwardHitDrag(int npcId, UUID clientID) throws IOException {
	
	String message = new String("hitDragon," +clientID.toString()+","+Integer.toString(npcId));    
	npcCtrl.decDragonHealth(npcId);
	forwardPacketToAll(message,clientID);
//relayCurrentNPCInformation(clientID);
}
}
