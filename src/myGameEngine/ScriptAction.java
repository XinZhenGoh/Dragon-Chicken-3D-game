package myGameEngine;

import net.java.games.input.Event;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import myProtocolClient.MyGame;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Light;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;

public class ScriptAction extends AbstractInputAction{
	private SceneManager sm;
	KeyboardControls kbCon;
	//might need to instantiate
	File scriptFile3;
	ScriptEngine jsEngine;
	MyGame g;
	SceneNode dolphin;
	//float speed;
	
	public ScriptAction(MyGame g) { 
	scriptFile3 = g.getScript();
	dolphin = g.getDolphin();
	//speed = g.getSpeed();
	} // constructor
	
	private void runScript()
    { try
    { FileReader fileReader = new FileReader(scriptFile3);
    jsEngine.eval(fileReader);
    fileReader.close();
    }
    catch (FileNotFoundException e1)
    { System.out.println(scriptFile3 + " not found " + e1); }
    catch (IOException e2)
    { System.out.println("IO problem with " + scriptFile3 + e2); }
    catch (ScriptException e3)
    { System.out.println("Script Exception in " + scriptFile3 + e3); }
    catch (NullPointerException e4)
    { System.out.println ("Null ptr exception reading in runscript " + scriptFile3 + e4); }
    }
	
	

	public void performAction(float time, Event e) {
		// TODO Auto-generated method stub
        ScriptEngineManager factory = new ScriptEngineManager();
        java.util.List<ScriptEngineFactory> list = factory.getEngineFactories();
        jsEngine = factory.getEngineByName("js");
        // set up the script that associates the light color with the space bar
        scriptFile3 = new File("UpdateSettings.js");
        
	    runScript();
	    
		Invocable invocableEngine = (Invocable) jsEngine;
		//get the light to be updated
		// invoke the script function

		try
		{ 
		invocableEngine.invokeFunction("updateStats", dolphin); }
		catch (ScriptException e1)
		{ System.out.println("ScriptException in " + scriptFile3 + e1); }
		catch (NoSuchMethodException e2)
		{ System.out.println("No such method in " + scriptFile3 + e2); }
		catch (NullPointerException e3)
		{ System.out.println ("Null ptr exception reading " + scriptFile3 + e3); }
	}
 }