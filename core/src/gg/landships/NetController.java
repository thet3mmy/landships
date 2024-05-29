package gg.landships;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class NetController implements Runnable {
    public static Socket socket;
    public static PrintWriter out;
    public static DataInputStream in;

    NetController() throws UnknownHostException, IOException {
        socket = new Socket("127.0.0.1", 7363);
        out = new PrintWriter(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        do {
            try {
                String line = in.readLine();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject)parser.parse(line);

                int pid = ((Long)jsonObject.get("id")).intValue();
                System.out.println("NetController: " + pid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while(true);
    }
}
