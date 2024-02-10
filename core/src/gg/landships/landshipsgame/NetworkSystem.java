package gg.landships.landshipsgame;

import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkSystem {
    static Socket socket;						// socket connecting to the server
    static ObjectOutputStream out;				// send objects to the server
    static ObjectInputStream in;				// read objects sent from server
    static Thread networkThread;				// thread where messages are read in
    static int clientId;						// id of this client on the server

    NetworkSystem() {

    }

    public void start() {
        // create network thread
        NetworkSystem.networkThread = new Thread(() -> {
            try {
                NetworkMessage handshake = (NetworkMessage) NetworkSystem.in.readObject();
                NetworkSystem.clientId = handshake.clientId;

                System.out.println(this + " Got client ID " + NetworkSystem.clientId);

                LandshipsGame.thisTank = LandshipsGame.tanks.get(NetworkSystem.clientId);
                LandshipsGame.updateList.add(LandshipsGame.thisTank);
                LandshipsGame.updateList.add(LandshipsGame.thisTank.turret);
                LandshipsGame.thisTank.sprite.setPosition(0,0);

                while(true) {
                    if(Thread.currentThread().isInterrupted()) {
                        NetworkSystem.socket.close();
                        NetworkSystem.in.close();
                        NetworkSystem.out.close();
                        return;
                    }

                    Object newMessage = NetworkSystem.in.readObject();

                    if(!(newMessage instanceof Vector2)) {
                        // If the message recieved from the server
                        // is an update of position or rotation, perform it.
                        if (newMessage instanceof NetworkUpdateMessage) {
                            NetworkUpdateMessage update = (NetworkUpdateMessage) newMessage;

                            LandshipsGame.tanks.get(update.clientId).sprite.setPosition(update.chassisPos.x, update.chassisPos.y);
                            LandshipsGame.tanks.get(update.clientId).turret.sprite.setPosition(update.turretPos.x, update.turretPos.y);
                            LandshipsGame.tanks.get(update.clientId).sprite.setRotation(update.chassisRot);
                            LandshipsGame.tanks.get(update.clientId).turret.sprite.setRotation(update.turretRot);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ClassCastException cce) {
                System.out.println("ClassCastException @ " + this);
            }
        });
        NetworkSystem.networkThread.start();
    }
}
