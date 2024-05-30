package gg.landships;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//
// NetServer
//
// This is the game server, it has a server socket to accept connections, a thread pool to run one handler per
// each accepted socket, and it also handles assigning and creating the handler objects. All the actual "work"
// is deferred off to different threads in the pool running the NetServerHandler class.
//

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
        System.out.println("Server opened port 7363");
        netServer.start();
    }

    private void start() throws IOException {
        do {
            Socket sock = ss.accept();
            NetServerHandler h = new NetServerHandler(sock);
            clients.add(h);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", clients.indexOf(h));
            jsonObject.put("msgtype", 0);
            h.getOut().println(jsonObject.toJSONString());
            h.getOut().flush();

            threadPool.execute(h);
        } while(true);
    }
}
