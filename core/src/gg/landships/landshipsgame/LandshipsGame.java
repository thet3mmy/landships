package gg.landships.landshipsgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class LandshipsGame extends ApplicationAdapter {
	static SpriteBatch uiBatch;					// renders all text for the UI
	static SpriteBatch batch;					// renders all sprites
	static ShapeRenderer shapeRenderer;			// renders the cross-hair reload effect
	static UISystem uiSystem;					// renders text and cross-hairs

	static NetworkSystem networkSystem;

	static LinkedList<GameObject> renderLayer0;	// bottom layer objects
	static LinkedList<GameObject> renderLayer1; // top layer objects (on top of layer0)
	static LinkedList<GameObject> updateList;	// every object that gets updated
	static LinkedList<TankChassis> tanks;		// tanks that are controlled by NetworkUpdateMessages
	static LandshipsCamera camera;				// camera

	static TankChassis thisTank;				// the tank controlled by this client
	
	@Override
	public void create () {
		// create the sprite batches, etc.
		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		uiSystem = new UISystem();

		// create linked lists
        renderLayer0 = new LinkedList<>();
		renderLayer1 = new LinkedList<>();
		updateList = new LinkedList<>();
		tanks = new LinkedList<>();

		// set up the camera
		camera = new LandshipsCamera();
		camera.setToOrtho(false);

		Gdx.input.setInputProcessor(camera);

		/*
		To get a tank to work properly, when you create the chassis...

		You must add it to the render list
		Add the chassis to the update list
		Add the turret to the update list

		Note that you do not have to add the turret to the render list
		because the chassis does this automatically when it is created.
		If you end up with a turret which is floating on its own with
		no hull in sight it means you did not add the hull to the render
		list, so the turret is just drawing on its own.
		 */

		// fill tanks list with puppets
		for(int i = 0; i < 10; i++) {
			TankChassis newTank = new TankChassis(new Texture("chassistemplate.png"));
			newTank.sprite.setPosition(-99999, -99999);
			newTank.turret.sprite.setPosition(-99999, -99999);

			tanks.add(newTank);
			renderLayer0.add(newTank);
		}

		try {
			networkSystem = new NetworkSystem();
			networkSystem.start();
		} catch (IOException ignored) {}
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.15f, 0.5f, 0.85f, 1);
		camera.update();

		if(thisTank != null) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();

			for (GameObject o : new LinkedList<>(renderLayer0)) {
				o.sprite.draw(batch);
			}

			for (GameObject o : new LinkedList<>(renderLayer1)) {
				o.sprite.draw(batch);
			}

			for (GameObject o : new LinkedList<>(updateList)) {
				o.think();
			}

			for (TankChassis o: new LinkedList<>(tanks)) {
				o.updateTurretPosition();
			}

			batch.end();
			uiSystem.renderUI();
			networkSystem.updateEverything();
		}
	}

	@Override
	public void dispose () {
		NetworkSystem.networkThread.interrupt();

		batch.dispose();
		uiBatch.dispose();
		shapeRenderer.dispose();

		try {
			for(GameObject o: renderLayer0) {
				o.sprite.getTexture().dispose();
			}
			for(GameObject o: renderLayer1) {
				o.sprite.getTexture().dispose();
			}

			NetworkSystem.socket.close();
			NetworkSystem.in.close();
			NetworkSystem.out.close();
		} catch (Exception ignored) {}
	}
}
