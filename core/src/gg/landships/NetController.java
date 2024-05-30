package gg.landships;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//
// NetController
//
// This class is basically the network engine for the game client. It handles encoding and decoding
// the other client's packets/messages which are relayed/mirrored from the server, and it applies the
// effects of these events onto the puppet NetTank(Base/Turret) objects that we previously assigned.
//
//
// Part of this class is actually intended to run in a thread, hence the implementation of Runnable.
// The only part that is threaded in under the run() method, and it handles the DECODING of messages.
// Encoding of message updates is under the sendUpdateToServer() method.
//
// I will heavily comment and explain this file, because my net code implementations are a little bit
// shakier than I would really like and as a result it's pretty botched and 100% needs further explanation.
//

public class NetController implements Runnable {
    public static Socket socket;                // The socket that represents our connection to the server
    public static PrintWriter out;              // We use this to send messages up to the server
    public static DataInputStream in;           // We use this to receive messages from the server to process
    public static int id = -1;                  // We need this number to properly form our packets (-1 = unassigned)

    public static LinkedList<NetTankBase> netTanks;

    NetController() throws IOException {
        // Connect and set up the readers and writers
        socket = new Socket("127.0.0.1", 7363);
        out = new PrintWriter(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        // Create the puppet tank list
        netTanks = new LinkedList<>();
        for(int i = 0; i < 8; i++) {
            NetTankBase b = new NetTankBase();
            Game.renderer.addEntity(b);
            netTanks.add(b);
        }

        System.out.println("NetController: Running and connected...");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        do {
            try {
                String line = in.readLine();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(line);

                // get the ID from the message
                int pid = ((Long) jsonObject.get("id")).intValue();

                // if this is our first msg from the server, then
                // we have to remember the id, so we can reuse it.
                if(id == -1)
                    id = pid;

                // let's grab the message type
                int type = ((Long) jsonObject.get("msgtype")).intValue();

                // Execute based on msg type
                switch(type) {
                    case 1:
                        // Get all the data from the JSON string
                        float px = ((Double) jsonObject.get("posx")).floatValue();
                        float py = ((Double) jsonObject.get("posy")).floatValue();
                        float ang = ((Double) jsonObject.get("ang")).floatValue();
                        float tang = ((Double) jsonObject.get("tang")).floatValue();

                        // Next apply all the data we just read onto the puppet tank
                        NetTankBase b = netTanks.get(pid);
                        b.getSprite().setRotation(ang);
                        b.getSprite().setPosition(px, py);
                        b.turret.getSprite().setRotation(tang);
                        break;
                }
            } catch (Exception e) {
                System.err.println("Exception in NetController loop");
            }
        } while(true);
    }

    public void sendUpdateToServer() {
        // In this method we are going to create a JSONObject, where we will
        // put data into it. We get this data from the current controlled tank, and
        // we also have to get the previously saved ID that we received from the server,
        // so the other clients who will receive this packet know which puppet to move.

        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("msgtype", 1);
        obj.put("posx", Game.tank.getTruePos().x);
        obj.put("posy", Game.tank.getTruePos().y);
        obj.put("ang", Game.tank.getSprite().getRotation());
        obj.put("tang", Game.tank.turret.getSprite().getRotation());

        out.println(obj.toJSONString());
        out.flush();
    }
}
