import helper.NetAddress;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class PeerClient extends Thread {
    private List<NetAddress> indexServerAddresses;
    private static final int BUFFER_SIZE = 1024;
    private static final String OBTAIN_REQUEST = "search";
    private static final String LOCAL_HOST = "127.0.0.1";

    public PeerClient(List<NetAddress> indexServerAddresses) {
        this.indexServerAddresses = indexServerAddresses;
    }

    public static void download(Socket socket, String fileName) {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            while(true) {
                int n = inputStream.read(buffer);
                if(n > 0){
                    fileOutputStream.write(buffer);
                }
                else {
                    inputStream.close();
                    fileOutputStream.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        String fileName = null;
        while(true) {
            System.out.println("Input file name please:");
            fileName = in.nextLine();
            String request = OBTAIN_REQUEST + " " + fileName;
            int size = indexServerAddresses.size();
            int hashValue = fileName.hashCode();
            if(hashValue < 0) {
                hashValue = hashValue * (-1);
            }
            NetAddress indexServer = indexServerAddresses.get(hashValue % size);
            int indexPort = indexServer.getPort();
            //System.out.println("Chooose indexing server " + indexPort);
            try {
                System.out.println("Connecting to " + indexPort);
                Socket clientSocket = new Socket(LOCAL_HOST, indexPort);
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outputStream = clientSocket.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                writer.write(request);
                writer.write("\r\n");
                writer.flush();
                //System.out.println("send: " + request);
                String line = reader.readLine();
                //System.out.println("receive: " + line);
                String[] strs = line.split(" ");
                if(strs[0].equals("-1")) {
                    System.err.println("File not found");
                }
                else {
                    //file server peers
                    List<NetAddress> peers = new ArrayList<>();
                    for(int i = 1; i < strs.length; ++i) {
                        String peerStr = strs[i];
                        String[] peerStrs = peerStr.split("-");
                        NetAddress peer = new NetAddress();
                        peer.setAddress(peerStrs[0]);
                        peer.setPort(Integer.parseInt(peerStrs[1]));
                        peers.add(peer);
                    }
                    dealUserInput(peers, request, fileName, in);
                }
                writer.close();
            } catch (Exception e) {
                System.err.println("File server creashes, choose a new file server");
            }
        }
    }
    public void dealUserInput(List<NetAddress> peers, String request, String fileName, Scanner in) {
        try {
            System.out.println("File server avalible: " + peers);
            System.out.print("Input the sequence of the file server(0, 1, 2...):");
            int indexFileServer = Integer.parseInt(in.nextLine());
            NetAddress serverPeer = peers.get(indexFileServer);

            Socket fileServerSocket = new Socket(serverPeer.getAddress(), serverPeer.getPort());
            OutputStream outStream = fileServerSocket.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
            bufferedWriter.write(request);
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
            download(fileServerSocket, fileName);
            bufferedWriter.close();
            fileServerSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}