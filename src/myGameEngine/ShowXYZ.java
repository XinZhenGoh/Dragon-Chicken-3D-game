package myGameEngine;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

//import com.jogamp.opengl.util.texture.TextureState;

import ray.rage.Engine;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.ManualObject;
import ray.rage.scene.ManualObjectSection;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.util.BufferUtil;
import ray.rage.asset.texture.*;
import ray.rage.asset.material.*;


public class ShowXYZ {
	
	    private Engine eng;
	    private SceneManager sm;
	    private ManualObject xAxis;
	    private ManualObject yAxis;
	    private ManualObject zAxis;
	    private ManualObjectSection axisSec;
	    private Material mat;
	    private Texture tex;
	    private TextureState tstate;
	    
	    public ShowXYZ(SceneManager s) throws IOException {
	        sm = s;
	        
	        //make the xyz axis individually and manually
	        makeXLine();
	        makeYLine();
	        makeZLine();
	        
	        //set primitive as lines
	        xAxis.setPrimitive(Primitive.LINES);
	        yAxis.setPrimitive(Primitive.LINES);
	        zAxis.setPrimitive(Primitive.LINES);
	        
	        //make nodes and attach the objects (axis)
	        SceneNode xNode = sm.getRootSceneNode().createChildSceneNode(xAxis.getName() + "Node");
	        xNode.attachObject(xAxis);
	        SceneNode yNode= sm.getRootSceneNode().createChildSceneNode(yAxis.getName() + "Node");
	        yNode.attachObject(yAxis);
	        SceneNode zNode = sm.getRootSceneNode().createChildSceneNode(zAxis.getName() + "Node");
	        yNode.attachObject(zAxis);
	        
	   
	    }
	    
	    private void makeXLine() throws IOException{
	        xAxis = sm.createManualObject("myXAxis");
	        ManualObjectSection axisSec = xAxis.createManualSection("xAxisSection");
	        xAxis.setGpuShaderProgram(sm.getRenderSystem().
	        getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
	        float[] xVertices = new float[] {
	                -1.0f, 0.0f, 0.0f,
	                1.0f, 0.0f, 0.0f,
	        };
	        //similiar to pyramid code, given inclass:
	        int [] indices = new int[] {0,1};
	        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(xVertices);
	        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
	        
	        axisSec.setVertexBuffer(vertBuf);
	        axisSec.setIndexBuffer(indexBuf);
	        
	        //modified fromfrom Gordon's blog page:
	        mat = sm.getMaterialManager().getAssetByPath("default.mtl");
	        mat.setEmissive(Color.PINK);
	        tex = sm.getTextureManager().getAssetByPath(mat.getTextureFilename());
	        tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
	        tstate.setTexture(tex);
	        axisSec.setRenderState(tstate);
	        axisSec.setMaterial(mat);
	        }
	        
	    private void makeYLine() throws IOException{
	        yAxis = sm.createManualObject("myYAxis");
	        ManualObjectSection axisSec = yAxis.createManualSection("yAxisSection");
	        yAxis.setGpuShaderProgram(sm.getRenderSystem().
	        getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
	        float[] yVertices = new float[] {
	                0.0f, 1.0f, 0.0f,
	                0.0f, -1.0f, 0.0f,
	        };
	        
	        //similiar to pyramid code, given inclass:
	        int [] indices = new int[] {0,1};
	        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(yVertices);
	        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
	        axisSec.setVertexBuffer(vertBuf);
	        axisSec.setIndexBuffer(indexBuf);
	        
	        //modified from Gordon's blog page:
	        mat = sm.getMaterialManager().getAssetByPath("default.mtl");
	        mat.setEmissive(Color.BLUE);
	        tex = sm.getTextureManager().getAssetByPath(mat.getTextureFilename());
	        tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
	        tstate.setTexture(tex);
	        axisSec.setRenderState(tstate);
	        axisSec.setMaterial(mat);
	        }
	    
	    private void makeZLine() throws IOException{
	        zAxis = sm.createManualObject("myZAxis");
	        ManualObjectSection axisSec = zAxis.createManualSection("zAxisSection");
	        zAxis.setGpuShaderProgram(sm.getRenderSystem().
	        getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
	        float[] zVertices = new float[] {
	                0.0f, 0.0f, 1.0f,
	                0.0f, 0.0f, -1.0f,
	        };
	        
	      //similiar to pyramid code, given inclass:
	        int [] indices = new int[] {0,1};
	        
	        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(zVertices);
	        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
	        axisSec.setVertexBuffer(vertBuf);
	        axisSec.setIndexBuffer(indexBuf);
	        //modified fromfrom Gordon's blog page:
	        mat = sm.getMaterialManager().getAssetByPath("default.mtl");
	        mat.setEmissive(Color.GREEN);
	        tex = sm.getTextureManager().getAssetByPath(mat.getTextureFilename());
	        tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
	        tstate.setTexture(tex);
	        axisSec.setRenderState(tstate);
	        axisSec.setMaterial(mat);
	    } 
	    public ManualObject getXAxis()
	    {
	    	return xAxis;
	    }
	    public ManualObject getYAxis()
	    {
	    	return yAxis;
	    }
	    public ManualObject getZAxis()
	    {
	    	return zAxis;
	    }
}
	        
	        
	
	