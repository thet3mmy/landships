package gg.landships.landshipsgame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LandshipsServer {
    public static Executor threadPool;
    public static ServerSocket serverSocket;
    public static LinkedList<ServerClientHandler> clients;

    public static void main(String[] args) throws IOException {
        threadPool = Executors.newFixedThreadPool(15);
        serverSocket = new ServerSocket(1235);
        clients = new LinkedList<>();

        while(true) {
            Socket socket = serverSocket.accept();
            ServerClientHandler handler = new ServerClientHandler(socket);
            clients.add(handler);

            threadPool.execute(handler);
        }
    }
}
