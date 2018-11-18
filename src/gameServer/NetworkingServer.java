package gameServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import myGameEngine.NPCcontroller;
import ray.networking.IGameConnection.ProtocolType;
import ray.networking.server.GameConnectionServer; 
import ray.networking.server.IClientInfo;
public class NetworkingServer  
{  
	private GameServerUDP thisUDPServer;  
	private NPCcontroller npcCtrl; 
	private long startTime;
	private long lastUpdateTime;
//	private GameServerTCP thisTCPServer; 

public NetworkingServer(int serverPort, String protocol)   
{ 
	try    
	{ 
		if(protocol.toUpperCase().compareTo("TCP") == 0)    
		{ 
			System.out.println("Got a TCP packet, supposed to be UDP\n");
			
			//thisTCPServer = new GameServerTCP(serverPort);
		}    
		else
		{ 
			thisUDPServer = new GameServerUDP(serverPort);   
			System.out.println("serverport is "+ serverPort);
			System.out.println("Server is ready...");
			startTime = System.nanoTime();     
			lastUpdateTime = startTime;     
			npcCtrl = new NPCcontroller(); 
			thisUDPServer.setNPCcontroller(npcCtrl);
		}   
	}   
	catch (IOException e)    
	{ 
		e.printStackTrace();  
	} 
	  // start NPC control loop    
	npcCtrl.setupNPCs();     
	npcLoop(); 
}  
public void npcLoop()       
//NPC control loop   
{  
	System.out.println("STARTING NPC loop!!");
	while (true)    
	{
		long frameStartTime = System.nanoTime();       
		float elapMilSecs = (frameStartTime-lastUpdateTime)/(1000000.0f);       
		if (elapMilSecs >= 50.0f)       
		{ 
			lastUpdateTime = frameStartTime;
			npcCtrl.updateNPCTime(lastUpdateTime);
			npcCtrl.updateNPCs();
			thisUDPServer.sendNPCinfo();       
		}
		Thread.yield();   
	} 
} 
public static void main(String[] args) throws UnknownHostException   
{ 
	System.out.println("ServerStarted!");
	InetAddress IP = InetAddress.getLocalHost();
	
	System.out.println("Server IP address: "+IP.getHostAddress());
	int port = Integer.parseInt(JOptionPane.showInputDialog("Please input port: "));
	NetworkingServer app =      new NetworkingServer(port, "UDP");
	
	
	System.out.println("Server Port Number: " + port);
	/*System.out.println("In main, \nArgs length is"+ args.length +"\n");
	if(args.length > 1)   
	{ 
		NetworkingServer app =      new NetworkingServer(Integer.parseInt(args[0]), args[1]);
		System.out.println("In if, network server instantiated");
	} 
	NetworkingServer app =      new NetworkingServer(42, "UDP");
*/} 
}