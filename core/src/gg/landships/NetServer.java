package gg.landships;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetServer {
    public static ServerSocket ss;
    public static ExecutorService threadPool;
    public static LinkedList<NetServerHandler> clients;

    NetServer() throws IOException {
        ss = new ServerSocket(7363);
        threadPool = Executors.newFixedThreadPool(20);
        clients = new LinkedList<>();
    }

    public static void main(String[] args) throws IOException {
        NetServer netServer = new NetServer();
        netServer.start();
        System.out.println("Server opened port 7363");
    }

    private void start() throws IOException {
        do {
            Socket sock = ss.accept();
            NetServerHandler h = new NetServerHandler(sock);
            clients.add(h);
            threadPool.execute(h);
        } while(true);
    }
}
