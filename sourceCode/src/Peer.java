import helper.AddressHelper;
import helper.FileHelper;
import helper.NetAddress;
import helper.NetHelper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Peer {
    private static final String REGISTRY_REQUEST = "registry";
    private static final String LOCAL_HOST = "127.0.0.1";

    //registry with the Index Server
    private static void registryToIndexingServer(String address, int port, String directoryNameName, int peerPort) {
        try {
            Socket socket = new Socket(address, port);
            /*
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            */
            String files = FileHelper.listFiles(directoryNameName);
            if(files.length() > 0) {
                String registryRequest = REGISTRY_REQUEST + " " + String.valueOf(peerPort) + " ";
                registryRequest += files;
                NetHelper.writeOnce(socket, registryRequest);
            }
            //writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(int port, String indexConfigFileName, String directoryName) {
        List<NetAddress> indexPeers = AddressHelper.getAddressFromFile(indexConfigFileName);
        if(indexPeers == null || indexPeers.size() == 0) {
            System.err.println("Error: indexing server config file not found...");
            System.exit(-1);
        }

        for(int i = 0; i < indexPeers.size(); ++i) {
            int indexPort = indexPeers.get(i).getPort();
            registryToIndexingServer(LOCAL_HOST, indexPort, directoryName, port);
        }
        new PeerClient(indexPeers).start();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                System.out.println("Peerserver is listening " + port);
                Socket socket = serverSocket.accept();
                new PeerServer(socket, directoryName).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //default value
        int port = 5000;
        String directoryName = "directory";
        String configFileName = "config.txt";

        //get value from args
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        else if(args.length == 2) {
            directoryName = args[0];
            port = Integer.parseInt(args[1]);
        }
        Peer peer = new Peer();
        peer.init(port, configFileName, directoryName);
    }
}
