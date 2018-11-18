package myProtocolClient;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import ray.rage.Engine;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.ManualObject;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.controllers.RotationController;
import ray.rml.Degreef;

public class RandomGen {
	private Engine eng;
	private SceneManager sm;
	private MyGame game;
    private SceneNode randNode;
    private SceneNode a1,a2,a3,a4,a5,a6,a7,a8,a9,a10;
    SceneNode t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16,t17,t18,t19,t20;
    SceneNode i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12,i13,i14,i15,i16,i17,i18,i19,i20;
	SceneNode p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16,p17,p18,p19,p20;
	SceneNode k1,k2,k3,k4,k5,k6,k7,k8,k9,k10,k11,k12,k13,k14,k15,k16,k17,k18,k19,k20;
	SceneNode l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12,l13,l14,l15,l16,l17,l18,l19,l20;
    private Material mat;
    
	
	public RandomGen(Engine e, SceneManager s, MyGame g) throws IOException
	{
		eng=e;
		sm=s;
		game=g;
		
     	mat = eng.getMaterialManager().createManualAsset("PlaneMaterial"+s);
   	    mat= sm.getMaterialManager().getAssetByPath("trees.mtl");
        //mat.setEmissive(Color.GREEN.brighter().brighter().brighter().brighter());

        t1 = generate1(eng, sm, t1,"z1");
        t2 = generate1(eng, sm, t2,"z2");
        t3 = generate1(eng, sm, t3,"z3");
        t4 = generate1(eng, sm, t4,"z4");
        t5 = generate1(eng, sm, t5,"z5");
        t6 = generate1(eng, sm, t6,"z6");
        t7 = generate1(eng, sm, t7,"z7");
        t8 = generate1(eng, sm, t8,"z8");
        t9 = generate1(eng, sm, t9,"z9");
        t11 = generate2(eng, sm, t11,"z11");
        t12 = generate2(eng, sm, t12,"z12");
        t13 = generate2(eng, sm, t13,"z13");
        t14 = generate2(eng, sm, t14,"z14");
        t15 = generate2(eng, sm, t15,"z15");
        t16 = generate2(eng, sm, t16,"z16");    
        t17 = generate2(eng, sm, t17,"z17");
        t18 = generate2(eng, sm, t18,"z18");
        t19 = generate2(eng, sm, t19,"z19");
        t20 = generate2(eng, sm, t20,"z20");  
	    
        i1 = generate3(eng, sm, i1,"i1");
        i2 = generate3(eng, sm, i2,"i2");
        i3 = generate3(eng, sm, i3,"i3");
        i4 = generate3(eng, sm, i4,"i4");
        i5 = generate3(eng, sm, i5,"i5");
        i6 = generate3(eng, sm, i6,"i6");
        i7 = generate3(eng, sm, i7,"i7");
        i8 = generate3(eng, sm, i8,"i8");
        i9 = generate3(eng, sm, i9,"i9");
        i10 = generate(eng, sm, i10,"i10");
        i11 = generate(eng, sm, i11,"i11");
        i12 = generate(eng, sm, i12,"i12");
        i13 = generate(eng, sm, i13,"i13");
        i14 = generate(eng, sm, i14,"i14");
        i15 = generate(eng, sm, i15,"i15");
        i16 = generate(eng, sm, i16,"i16");
        i17 = generate(eng, sm, i17,"i17");
        i18 = generate(eng, sm, i18,"i18");
        i19 = generate(eng, sm, i19,"i19");
        i20 = generate(eng, sm, i20,"i20");
        
        p1 = generate4(eng, sm, p1,"p1");
        p2 = generate4(eng, sm, p2,"p2");
        p3 = generate4(eng, sm, p3,"p3");
        p4 = generate4(eng, sm, p4,"p4");
        p5 = generate4(eng, sm, p5,"p5");
        p6 = generate4(eng, sm, p6,"p6");
        p7 = generate4(eng, sm, p7,"p7");
        p8 = generate4(eng, sm, p8,"p8"); 
        p9 = generate4(eng, sm, p9,"p9");
        p10 = generate5(eng, sm, p10,"p10");
        p11 = generate5(eng, sm, p11,"p11");
        p12 = generate5(eng, sm, p12,"p12");
        p13 = generate5(eng, sm, p13,"p13");
        p14 = generate5(eng, sm, p14,"p14");
        p15 = generate5(eng, sm, p15,"p15");
        p16 = generate5(eng, sm, p16,"p16");
        p17 = generate5(eng, sm, p17,"p17");
        p18 = generate5(eng, sm, p18,"p18");
        p19 = generate5(eng, sm, p19,"p19");
        p20 = generate5(eng, sm, p20,"p20");

        RotationController rc = new RotationController();
        rc.addNode(t1);
        rc.addNode(t2);
        rc.addNode(t3);
        rc.addNode(t4);
        rc.addNode(t5);
        rc.addNode(t6);
        rc.addNode(t7);
        rc.addNode(t8);
        rc.addNode(t9);

        s.addController(rc);
        g.hearts.addElement(a1);
	}
	
	public SceneNode generate(Engine eng, SceneManager sm,SceneNode sn,String s) throws IOException {
		Entity prizeCubes = sm.createEntity("prizeBall"+s, "heart_lp.obj");
        prizeCubes.setPrimitive(Primitive.TRIANGLES);
        sn = sm.getRootSceneNode().createChildSceneNode(s + "Prize");
        sn.attachObject(prizeCubes);
        sn.moveBackward(randomize());
        sn.moveRight(randomize());
        sn.moveForward(randomize());
        sn.moveLeft(randomize());
        
        sn.scale(0.03f, 0.03f,0.07f);
        sn.moveUp(1f);
        TextureManager tm = eng.getTextureManager();
        Texture mTexture = tm.getAssetByPath("red.jpeg");

        RenderSystem rs = sm.getRenderSystem();
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(mTexture);
        prizeCubes.setRenderState(state);
      
		return sn;	
        
	}
	
	
	public SceneNode generate1(Engine eng, SceneManager sm,SceneNode sn,String s) throws IOException {
        Entity prizeCubes = sm.createEntity("prizeBall"+s, "rosted_chicken_01.obj");
        prizeCubes.setPrimitive(Primitive.TRIANGLES);
        sn = sm.getRootSceneNode().createChildSceneNode(s + "Prize");
        sn.attachObject(prizeCubes);
        sn.moveBackward(randomize());
        sn.moveRight(randomize());
        sn.moveForward(randomize());
        sn.moveLeft(randomize());
        
        sn.scale(0.5f, 0.5f,0.5f);
        sn.moveUp(1f);
        TextureManager tm = eng.getTextureManager();
        Texture mTexture = tm.getAssetByPath("roasted_chicken_01.png");

        RenderSystem rs = sm.getRenderSystem();
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(mTexture);
        prizeCubes.setRenderState(state);
      
		return sn;	
	}
	
	  private double[] toDoubleArray(float[] arr)
	  { if (arr == null) return null;
	  int n = arr.length;
	  double[] ret = new double[n];
	  for (int i = 0; i < n; i++)
	  { ret[i] = (double)arr[i];
	  }
	  return ret;
	  }
	
	
	public SceneNode generate2(Engine eng, SceneManager sm,SceneNode sn,String s) throws IOException {
        Entity prizeCubes = sm.createEntity("prizeBall"+s, "trees.obj");
        prizeCubes.setPrimitive(Primitive.TRIANGLES);
        sn = sm.getRootSceneNode().createChildSceneNode(s + "Prize");
        sn.attachObject(prizeCubes);
        sn.moveBackward(randomize());
        sn.moveRight(randomize());
        sn.moveForward(randomize());
        sn.moveLeft(randomize());
        
        sn.scale(1f, 1f,1f);
        sn.moveUp(1f);
   	    mat= sm.getMaterialManager().getAssetByPath("trees.mtl");
        mat.setEmissive(Color.GREEN.brighter());
    	prizeCubes.setMaterial(mat);
        TextureManager tm = eng.getTextureManager();
        Texture mTexture = tm.getAssetByPath("grass.jpg");

        RenderSystem rs = sm.getRenderSystem();
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(mTexture);
        //prizeCubes.setRenderState(state);
        mat.setEmissive(Color.GREEN.darker().darker().darker().darker().darker().darker());
        prizeCubes.setMaterial(mat);
		return sn;	
	}
	
	public SceneNode generate3(Engine eng, SceneManager sm,SceneNode sn,String s) throws IOException {
        Entity prizeCubes = sm.createEntity("prizeBall"+s, "trees2.obj");
        prizeCubes.setPrimitive(Primitive.TRIANGLES);
        sn = sm.getRootSceneNode().createChildSceneNode(s + "Prize");
        sn.attachObject(prizeCubes);
        sn.moveBackward(randomize());
        sn.moveRight(randomize());
        sn.moveForward(randomize());
        sn.moveLeft(randomize());
        sn.scale(1f, 1f,1f);
        sn.moveUp(5f);
   	    mat= sm.getMaterialManager().getAssetByPath("trees.mtl");
        mat.setEmissive(Color.GREEN.brighter());
    	prizeCubes.setMaterial(mat); 
        TextureManager tm = eng.getTextureManager();
        Texture mTexture = tm.getAssetByPath("grass.jpg");

        RenderSystem rs = sm.getRenderSystem();
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(mTexture);
        //prizeCubes.setRenderState(state);
        mat.setEmissive(Color.GREEN.darker().darker().darker().darker().darker().darker());
        prizeCubes.setMaterial(mat);
		return sn;	
	}
	
	public SceneNode generate4(Engine eng, SceneManager sm,SceneNode sn,String s) throws IOException {
        Entity prizeCubes = sm.createEntity("prizeBall"+s, "trees3.obj");
        prizeCubes.setPrimitive(Primitive.TRIANGLES);
        sn = sm.getRootSceneNode().createChildSceneNode(s + "Prize");
        sn.attachObject(prizeCubes);
        sn.moveBackward(randomize());
        sn.moveRight(randomize());
        sn.moveForward(randomize());
        sn.moveLeft(randomize());
        sn.scale(1f, 1f,1f);
        sn.moveUp(5f);
   	    mat= sm.getMaterialManager().getAssetByPath("trees.mtl");
        mat.setEmissive(Color.GREEN.brighter());
    	prizeCubes.setMaterial(mat); 
        TextureManager tm = eng.getTextureManager();
        Texture mTexture = tm.getAssetByPath("grass.jpg");

        RenderSystem rs = sm.getRenderSystem();
        TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(mTexture);
        //prizeCubes.setRenderState(state);
        mat.setEmissive(Color.GREEN.darker().darker().darker().darker().darker().darker());
        prizeCubes.setMaterial(mat);
		return sn;	
	}
	
	public SceneNode generate5(Engine eng, SceneManager sm,SceneNode sn,String s) throws IOException {
        Entity prizeCubes = sm.createEntity("prizeBall"+s, "trees4.obj");
        prizeCubes.setPrimitive(Primitive.TRIANGLES);
        sn = sm.getRootSceneNode().createChildSceneNode(s + "Prize");
        sn.attachObject(prizeCubes);
        sn.moveBackward(randomize());
        sn.moveRight(randomize());
        sn.moveForward(randomize());
        sn.moveLeft(randomize());
        sn.scale(1f, 1f,1f);
        sn.moveUp(5f);
   	    mat= sm.getMaterialManager().getAssetByPath("trees.mtl");
        mat.setEmissive(Color.GREEN.brighter());
    	prizeCubes.setMaterial(mat); 
        TextureManager tm = eng.getTextureManager();
        Texture mTexture = tm.getAssetByPath("grass.jpg");

        RenderSystem rs = sm.getRenderSystem();
        TextureState  state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
        state.setTexture(mTexture);
        //prizeCubes.setRenderState(state);
        mat.setEmissive(Color.GREEN.darker().darker().darker().darker().darker().darker());
        prizeCubes.setMaterial(mat);
		return sn;	
	}
	
	
	
	public float randomize() {
		Random r = new Random();  
		float rand = 1.0f + r.nextFloat() * (50.0f - 15.0f);
		return rand;
	}
}
