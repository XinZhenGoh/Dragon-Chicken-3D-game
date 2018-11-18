package myGameEngine;

import ray.rml.Vector3;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import ray.rml.Vector3f;

public class NPCcontroller {
	private int numNPCs;
	private int spiderCount;
	private final int maxNPCs=5;
	private Vector<NPC> NPClist=new Vector<NPC>();
	private Vector<NPC> SpiderList=new Vector<NPC>();
	

	public NPCcontroller()
	{
		numNPCs=0;
		spiderCount=0;
	}
	public Vector<NPC> getSpiderList()
	{
		return SpiderList;
	}
	public void changeNPCState(int index, int newState)
	{
		NPClist.get(index).setState(newState);
	}
	
	public void setupNPCs()
	{
		System.out.println("set up NPC");
		//create first NPC
		//int random=getRandomNumberInRange(100, 1000);
		NPC smaug=new NPC("dragon");
		smaug.initLocation();
		smaug.setState(0);
		numNPCs++;
		this.NPClist.add( smaug);
		System.out.println("Npclist size is"+this.NPClist.size());
		
		//add more npc's here in the same way
	}
	public static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	//add a spider to the spider vector
	//increase npc count
	public void addNewSpider(NPC spider, int tempId)
	{
		if( tempId> spiderCount)
		{
			System.out.println("error line 57 NPC controller, spider count index is off\n spider count:"+this.spiderCount+"   index:"+tempId);
		}
		SpiderList.add(spider);
		AddSpider();
	}
	public void AddSpider()
	{
		spiderCount++;
	}
	public void AddNPC()
	{
		if(numNPCs+1<=maxNPCs)
		{
			numNPCs++;
		}
	}
	public void updateNPCs()
	{
		for( int i=0; i<numNPCs; i++)
		{
			NPClist.get(i).updateLocation();
		}
	}
	public void updateNPCTime(long time)
	{
		for( int i=0; i<numNPCs; i++)
		{
			NPClist.get(i).timeChange(time);
			
		}
	}
	public void setNPCTarget(int npcIndex, Vector3 loc, UUID newTargetID)
	{
		NPClist.get(npcIndex).setTargetID(newTargetID);
		NPClist.get(npcIndex).createTarget(loc);
	}
	public void updateNPCTargetLocation(int npcIndex, Vector3 loc, UUID targetID)
	{
		if(NPClist.get(npcIndex).getTargetID()==targetID)
		{
			NPClist.get(npcIndex).updateTargetLoc(loc);
		}
		
	}
	public Vector3f getSpiderNPC(int index)
	{
		return (Vector3f) SpiderList.get(index).getLocation();
	}
	public Vector3f getNPC(int index)
	{
		return (Vector3f) NPClist.get(index).getLocation();
	}
	public int getNumNPCs()
	{
		return numNPCs;
	}
	public void InRange(UUID clientID, Vector3 position, int NPCid)
	{
		NPClist.get(NPCid).setTargetID(clientID);
		NPClist.get(NPCid).createTarget(position);
		
	}
	public void changeNPCTarget(int NPCId, UUID newTargetID, Vector3 targetLoc)
	{
		try{
			NPClist.get(NPCId).changeTarget(newTargetID, targetLoc);
		}catch (Exception e)
		{
			System.out.println("NPClist size is "+ NPClist.size()+ "   error with id "+NPCId+"targetID "+newTargetID);
		
		}
	}
	public String getNPCType(int NPCID)
	{
		return NPClist.get(NPCID).getType();
	}
	public void removeNPCReset(int npcIndex)
	{

		//remove from vector
		NPClist.remove(npcIndex);
		numNPCs-=1;
	}
	public void changNPCLoc(Vector3f loc, int id)
	{
		NPClist.get(id).changeLocation(loc);
	}
		
	public void changSpiderLoc(Vector3f loc, int id)
	{
		SpiderList.get(id).changeLocation(loc);
	}
		
	public int getSpiderCountr()
	{
		return this.spiderCount; 
	}
	public int getDragonHealth(int npcID)
	{
		return NPClist.get(npcID).getDragonHealth();
	}
	public void decDragonHealth(int npcID)
	{
		NPClist.get(npcID).decDragonHealth();
	}
	
	
}
