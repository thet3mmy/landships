package gg.landships;

import java.util.LinkedList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.ScreenUtils;

//
// Game
//
// This class contains the controlling logic that combines all of the different classes together
// I try to minimize this java file as much as I can to keep it simple
//

public class Game extends ApplicationAdapter {
	public static LandshipsRenderer renderer;
	public static LandshipsCamera camera;
	public static LandshipsInputController iController;
	public static InputMultiplexer multiplexer;

	public static NetController controller;
	public static Thread netThread;

	public static TankBase tank;
	public static LinkedList<NetTankBase> netTanks;

	@Override
	public void create () {
		camera = new LandshipsCamera();						// this does all of our setup for us
		renderer = new LandshipsRenderer(camera);			// set up our renderer, bind camera
		iController = new LandshipsInputController();		// controls stuff like scrolling
		multiplexer = new InputMultiplexer(iController);	// lets us combine input sources.

		Gdx.input.setInputProcessor(multiplexer);			// use our multiplexer

		// here we will test everything
		tank = new TankBase();
		renderer.addEntity(tank);
		renderer.addEntity(new NetTankBase());

		// set the camera to focus on the tank if we are in focused mode
		camera.setFocus(tank);

		netTanks = new LinkedList<>();
		for(int i = 0; i < 8; i++) {
			netTanks.add(new NetTankBase());
		}

		// Finally, start the networking engine on the clientside
		netThread = new Thread(controller);
		netThread.start();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);				// clear our screen, black bg...
		renderer.update();									// updates our camera for us!!!

		renderer.begin();									// start rendering things normally
		renderer.render();									// draw all of the entities
		renderer.end();										// stop rendering now

		renderer.updateEnts();								// update AFTER so that TankTurrets work
	}
	
	@Override
	public void dispose () {
		
	}
}
