package myGameEngine;
import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
public class ShootingController extends AbstractController
{

private float speed = 2.5f;

@Override
protected void updateImpl(float elapsedTimeMillis)
{ 

for (Node n : super.controlledNodesList)
{ 
n.moveForward(speed);
}
}
}