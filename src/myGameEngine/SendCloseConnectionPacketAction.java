package myGameEngine;

import myProtocolClient.ProtocolClient;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class SendCloseConnectionPacketAction extends AbstractInputAction{
boolean isClientConnected;
ProtocolClient protClient;

	public  SendCloseConnectionPacketAction(ProtocolClient pC, boolean b)
	{
		isClientConnected=b;
		protClient=pC;
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		// TODO Auto-generated method stub
		if(protClient != null && isClientConnected == true)    
		{ 
			System.out.println("This client leaving, window closed");
			protClient.sendByeMessage();  
		} 
	} 
} 

