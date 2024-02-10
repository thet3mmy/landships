package gg.landships.landshipsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

public class NetworkSystem {
    static Socket socket;						// socket connecting to the server
    static DataInputStream in;
    static PrintWriter out;
    static Thread networkThread;				// thread where messages are read in
    static int clientId;						// id of this client on the server

    NetworkSystem() throws IOException {
        socket = new Socket("127.0.0.1", 1235);
        in = new DataInputStream(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
    }

    public void send(String string) {
        out.println(string);
        out.flush();
    }

    public void updateEverything() {
        JSONObject update = new JSONObject();
        update.put("clientId", clientId);
        update.put("type", 0);
        update.put("chassisX", LandshipsGame.thisTank.sprite.getX());
        update.put("chassisY", LandshipsGame.thisTank.sprite.getY());
        update.put("turretX", LandshipsGame.thisTank.turret.sprite.getX());
        update.put("turretY", LandshipsGame.thisTank.turret.sprite.getX());
        update.put("chassisRot", LandshipsGame.thisTank.sprite.getRotation());
        update.put("turretRot", LandshipsGame.thisTank.turret.sprite.getRotation());

        send(update.toJSONString());
    }

    public void shootBullet(Vector2 origin, Vector2 direction, float speed) {
        JSONObject update = new JSONObject();
        update.put("clientId", clientId);
        update.put("type", 1);
        update.put("originX", origin.x);
        update.put("originY", origin.y);
        update.put("dirX", direction.x);
        update.put("dirY", direction.y);
        update.put("speed", speed);

        send(update.toJSONString());
    }

    public void start() {
        // create network thread
        NetworkSystem.networkThread = new Thread(() -> {
            try {
                JSONParser parser = new JSONParser();
                JSONObject handshakeObject = (JSONObject) parser.parse(in.readLine());
                clientId = ((Long) handshakeObject.get("clientId")).intValue();

                LandshipsGame.thisTank = LandshipsGame.tanks.get(clientId);
                LandshipsGame.thisTank.sprite.setPosition(0,0);
                LandshipsGame.thisTank.turret.sprite.setPosition(0,64);
                LandshipsGame.thisTank.turret.think();

                LandshipsGame.updateList.add(LandshipsGame.thisTank);
                LandshipsGame.updateList.add(LandshipsGame.thisTank.turret);

                System.out.println(this + ": " + clientId);

                while(true) {
                    String line = in.readLine();

                    JSONObject newMessage = (JSONObject) parser.parse(line);
                    TankChassis netTank = LandshipsGame.tanks.get(((Long)newMessage.get("clientId")).intValue());

                    switch (((Long)newMessage.get("type")).intValue()) {
                        case 0:
                            float newX = ((Double) newMessage.get("chassisX")).floatValue();
                            float newY = ((Double) newMessage.get("chassisY")).floatValue();
                            netTank.sprite.setPosition(newX, newY);

                            float hullRot = ((Double) newMessage.get("chassisRot")).floatValue();
                            float turretRot = ((Double) newMessage.get("turretRot")).floatValue();
                            netTank.sprite.setRotation(hullRot);
                            netTank.turret.sprite.setRotation(turretRot);
                            break;
                        case 1:
                            float originX = ((Double) newMessage.get("originX")).floatValue();
                            float originY = ((Double) newMessage.get("originY")).floatValue();
                            float dirX = ((Double) newMessage.get("dirX")).floatValue();
                            float dirY = ((Double) newMessage.get("dirY")).floatValue();
                            float speed = ((Double) newMessage.get("speed")).floatValue();

                            Gdx.app.postRunnable(() -> {
                                TankShell newShell = new TankShell(
                                        new Vector2(originX, originY),
                                        new Vector2(dirX, dirY),
                                        speed
                                );

                                LandshipsGame.renderLayer0.add(newShell);
                                LandshipsGame.updateList.add(newShell);
                            });
                            break;
                    }
                }
            } catch (NullPointerException | IOException | ParseException ignored){}
        });
        NetworkSystem.networkThread.start();
    }
}
