package gg.landships;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//
// NetServerHandler
//
// This is run for every connected client to the NetServer, and it does all the heavy lifting
// It runs in a thread, remember that...
//

public class NetServerHandler implements Runnable {
    private boolean running;
    private Socket socket;
    private PrintWriter out;
    private DataInputStream in;

    NetServerHandler(Socket s) throws IOException {
        socket = s;
        out = new PrintWriter(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        running = true;
        try {
            while(running) {
                // Read the line coming in from the user
                String line = in.readLine();
                System.out.println(line);

                // Resend this to everyone
                for(NetServerHandler h: NetServer.clients) {
                    if(h != this) {
                        // Don't send it to ourselves
                        // If it's null end the connection
                        if(line == null) {
                            stop();
                            break;
                        }

                        // send it to this client (iterate)
                        h.getOut().println(line);
                        h.getOut().flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }

    public PrintWriter getOut() {
        return out;
    }

    public DataInputStream getIn() {
        return in;
    }
}
