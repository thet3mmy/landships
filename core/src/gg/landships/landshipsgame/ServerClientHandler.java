package gg.landships.landshipsgame;

import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable {
    Socket socket;
    DataInputStream in;
    PrintWriter out;

    ServerClientHandler(Socket sock) {
        try {
            socket = sock;
            in = new DataInputStream(sock.getInputStream());
            out = new PrintWriter(sock.getOutputStream());

            out.flush();
        } catch (Exception ignored) {}
    }

    @Override
    public void run() {
        try {
            JSONObject handshake = new JSONObject();
            handshake.put("clientId", LandshipsServer.clients.indexOf(this));
            out.println(handshake.toJSONString());
            out.flush();

            while(true) {
                String inLine = in.readLine();
                for(ServerClientHandler client: LandshipsServer.clients) {
                    client.out.println(inLine);
                    client.out.flush();
                }
            }
        } catch (Exception ignored) {
            LandshipsServer.clients.remove(this);
        }
    }
}
