package gg.landships.landshipsgame;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerClientHandler implements Runnable {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    ServerClientHandler(Socket sock) {
        try {
            socket = sock;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
        } catch (Exception ignored) {}
    }

    @Override
    public void run() {
        try {
            NetworkMessage handshake = new NetworkMessage();
            handshake.clientId = LandshipsServer.clients.indexOf(this);

            out.writeObject(handshake);
            out.flush();

            while(true) {
                Object o = in.readObject();

                NetworkUpdateMessage message = (NetworkUpdateMessage) o;
                for(ServerClientHandler sch: LandshipsServer.clients) {
                    sch.out.writeObject(message);
                    sch.out.flush();
                }
            }
        } catch (Exception ignored) {
            LandshipsServer.clients.remove(this);
        }
    }
}
