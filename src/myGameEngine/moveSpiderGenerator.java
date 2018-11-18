package myGameEngine;

import java.util.Random;

import myProtocolClient.ProtocolClient;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
public class moveSpiderGenerator extends AbstractController
{
private float cycleTime = 200.0f; // default cycle time
private float totalTime = 0.0f;
private PhysicsObject g;
ProtocolClient protClient;
public moveSpiderGenerator(	  ProtocolClient pC)
{
	protClient=pC;
}

@Override
protected void updateImpl(float elapsedTimeMillis)
{ 
totalTime += elapsedTimeMillis;

if(totalTime >= cycleTime) {
	int i=0;
for (Node n : super.controlledNodesList)
{ 
g = n.getPhysicsObject();
g.applyForce(randomize(), 0,randomize(),randomize(),0,0);
boolean success=true;
try {
protClient.sendSpiderMovedMessage(protClient.getSpiderVect().get(i));
}catch(Exception e)
{
	success=false;
	int realIndex=findRightIndex(n);
	if(realIndex!=-1)
	{
		i=realIndex;
		protClient.sendSpiderMovedMessage(protClient.getSpiderVect().get(i));
		i++;
	}
}
if(success)
	{
	i++;
	}
	
}

totalTime = 0;
}




}
public int randomize() {
	Random r = new Random();  
	int rand = (int) (-15+r.nextInt(31));
	//System.out.println(rand);
	return rand;
}
public int findRightIndex(Node node)
{
	for(int i=0; i< protClient.getSpiderVect().size(); i++)
	{
		if(protClient.getSpiderVect().get(i).getNode()==node)
		{
			return i;
		}
	}
	
	return -1;
}
}

