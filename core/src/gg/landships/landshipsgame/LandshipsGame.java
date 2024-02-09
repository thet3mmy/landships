package gg.landships.landshipsgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
	static SpriteBatch uiBatch;
	static SpriteBatch batch;
	static ShapeRenderer shapeRenderer;
	static UISystem uiSystem;

	static Socket socket;
	static ObjectOutputStream out;
	static ObjectInputStream in;
	static Thread networkThread;
	static int clientId;

	static LinkedList<GameObject> renderLayer0;
	static LinkedList<GameObject> renderLayer1;
	static LinkedList<GameObject> updateList;
	static LinkedList<TankChassis> tanks;
	static LandshipsCamera camera;

	static TankChassis thisTank;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		uiSystem = new UISystem();

		try {
			socket = new Socket("127.0.0.1", 1235);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (Exception ignored) {}

        renderLayer0 = new LinkedList<>();
		renderLayer1 = new LinkedList<>();
		updateList = new LinkedList<>();
		tanks = new LinkedList<>();

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

		for(int i = 0; i < 5; i++) {
			TankChassis newTank = new TankChassis();
			newTank.sprite.setPosition(-99999, -99999);
			newTank.turret.sprite.setPosition(-99999, -99999);

			tanks.add(newTank);
			renderLayer0.add(newTank);
		}

		networkThread = new Thread(() -> {
            try {
				NetworkMessage handshake = (NetworkMessage) in.readObject();
				clientId = handshake.clientId;

				System.out.println(this + " Got client ID " + clientId);

				thisTank = tanks.get(clientId);
				updateList.add(thisTank);
				updateList.add(thisTank.turret);
				thisTank.sprite.setPosition(0,0);

				while(true) {
					if(Thread.currentThread().isInterrupted()) {
						socket.close();
						in.close();
						out.close();
						return;
					}

					NetworkUpdateMessage update = (NetworkUpdateMessage) in.readObject();

                    tanks.get(update.clientId).sprite.setPosition(update.chassisPos.x, update.chassisPos.y);
					tanks.get(update.clientId).turret.sprite.setPosition(update.turretPos.x, update.turretPos.y);
					tanks.get(update.clientId).sprite.setRotation(update.chassisRot);
					tanks.get(update.clientId).turret.sprite.setRotation(update.turretRot);
				}
			} catch (Exception ignored) {}
        });
		networkThread.start();
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

			batch.end();
			uiSystem.renderUI();

			try {
				updateServer();
			} catch (IOException ignored) {
			}
		}
	}

	public void updateServer() throws IOException {
		NetworkUpdateMessage newMessage = new NetworkUpdateMessage();
		newMessage.clientId = clientId;

		newMessage.chassisPos = new Vector2(thisTank.sprite.getX(), thisTank.sprite.getY());
		newMessage.turretPos = new Vector2(thisTank.turret.sprite.getX(), thisTank.turret.sprite.getY());
		newMessage.chassisRot = thisTank.sprite.getRotation();
		newMessage.turretRot = thisTank.turret.sprite.getRotation();

		out.writeObject(newMessage);
		out.flush();
	}

	@Override
	public void dispose () {
		networkThread.interrupt();

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

			socket.close();
			in.close();
			out.close();
		} catch (Exception ignored) {}
	}
}
