package myGameEngine;
import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
public class AliveController extends AbstractController
{
private float cycleTime = 700.0f; // default cycle time
private float totalTime = 0.0f;
private float direction = 1.0f;
private int breathe = 1;
private float scaleRate = .008f; // growth per second
@Override
protected void updateImpl(float elapsedTimeMillis)
{ totalTime += elapsedTimeMillis;
float scaleAmt = 1.0f + direction * scaleRate;

if (totalTime > cycleTime)
{ 
//every cycle
breathe += 1;
totalTime = 0.0f;
direction = -direction;
if(breathe == 5) {
	breathe = 1;
}
}

if(breathe == 1) {
for (Node n : super.controlledNodesList)
{ 
n.moveUp(0.001f);
Vector3 curScale = n.getLocalScale();
curScale = Vector3f.createFrom(curScale.x()*scaleAmt, curScale.y(), curScale.z());
n.setLocalScale(curScale);
}
}

else if(breathe == 3 ) {
for (Node n : super.controlledNodesList)
{ 
n.moveDown(0.001f);
Vector3 curScale = n.getLocalScale();
curScale = Vector3f.createFrom(curScale.x()*scaleAmt, curScale.y(), curScale.z());
n.setLocalScale(curScale);
}	
}
else if(breathe == 2 ) {
	for (Node n : super.controlledNodesList)
	{ 
		Vector3 curScale = n.getLocalScale();
		curScale = Vector3f.createFrom(curScale.x()*scaleAmt, curScale.y(), curScale.z());
		n.setLocalScale(curScale);
	n.moveDown(0.0005f);
	}	
}

else if(breathe == 4 ) {
	for (Node n : super.controlledNodesList)
	{ 
		Vector3 curScale = n.getLocalScale();
		curScale = Vector3f.createFrom(curScale.x()*scaleAmt, curScale.y(), curScale.z());
		n.setLocalScale(curScale);
	n.moveUp(0.0005f);
	}	
}



}
}