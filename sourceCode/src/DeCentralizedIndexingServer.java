import helper.NetAddress;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DeCentralizedIndexingServer {
    private int indexPort;
    private Map<String, List<NetAddress>> fileServerAddresses = new Hashtable<>();

    public DeCentralizedIndexingServer(int indexPort) {
        this.indexPort = indexPort;
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(indexPort);
            while(true) {
                System.out.println("Index server is listening " + indexPort);
                Socket socket = serverSocket.accept();
                new IndexServerHelper(socket, fileServerAddresses).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int indexPort = 8000;
        if(args.length > 0) {
            indexPort = Integer.parseInt(args[0]);
        }
        DeCentralizedIndexingServer server = new DeCentralizedIndexingServer(indexPort);
        server.init();
    }
}
