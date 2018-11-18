package myProtocolClient;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Matrix3;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class GhostAvatar  { 
	private UUID id;  
	private SceneNode node;  
	private Entity entity;  
	private Vector3 startPosition;
	public GhostAvatar(UUID id, Vector3 position)   
	{ 
		this.id = id;
		//setPosition(position);
		this.startPosition=position;
		

	}  
// accessors and setters for id, node, entity, and position  . . . } 
	public UUID getID() {
		
		// TODO Auto-generated method stub
		return id;
	}
	public Vector3 getStartPosition()
	{
		return this.startPosition;
	}
	public void updatePosition(Vector3f ghostPos) {
		// TODO Auto-generated method stub
		this.node.setLocalPosition(ghostPos);
	}
	
	public void rotate(float value) {
		this.node.yaw(Degreef.createFrom(value));
	}
	
	public void updateRotation(Matrix3 ghostPos) {
		// TODO Auto-generated method stub
		this.node.setLocalRotation(ghostPos);
	}
	
	public Object getAvatar() {
		// TODO Auto-generated method stub
		return this;
	}
	public void setNode(SceneNode ghostN) {
		// TODO Auto-generated method stub
		this.node=ghostN;
	}
	public void setEntity(Entity ghostE) {
		// TODO Auto-generated method stub
		this.entity=ghostE;
	}
	//function to set position of avatar
	public void setPosition( Vector3 position)
	{
		node.setLocalPosition(position);
	}
	public Vector3 getPosition()
	{
		return node.getLocalPosition();
	}
	
	public SceneNode getNode() {
		return this.node;
	}
	
	public Matrix3 getRotation()
	{
		return node.getWorldRotation();
	}
}