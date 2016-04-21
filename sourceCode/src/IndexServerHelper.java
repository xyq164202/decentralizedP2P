import helper.NetAddress;
import helper.NetHelper;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class IndexServerHelper extends Thread {
    private Socket socket;
    private Map<String, List<NetAddress>> fileServerAddresses;
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String REGISTRY_REQUEST = "registry";
    private static final String SEARCH_REQUEST = "search";

    public IndexServerHelper(Socket socket, Map<String, List<NetAddress>> fileServerAddresses) {
        this.socket = socket;
        this.fileServerAddresses = fileServerAddresses;
    }

    public synchronized void registry(String[] strs) {
        int port = Integer.parseInt(strs[1]);
        for(int i = 2; i < strs.length; ++i) {
            String fileName = strs[i];
            NetAddress netAddress = new NetAddress();
            netAddress.setAddress(LOCAL_HOST);
            netAddress.setPort(port);
            List<NetAddress> value = fileServerAddresses.get(fileName);
            if(value == null) {
                value = new ArrayList<>();
                value.add(netAddress);
            }
            else {
                value.add(netAddress);
            }
            fileServerAddresses.put(fileName, value);
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Registry success...");
    }
    public synchronized void search(String[] strs) {
            String fileName = strs[1];
            List<NetAddress> netAddresss = fileServerAddresses.get(fileName);
            String stringReply = "";
            if (netAddresss == null || netAddresss.size() == 0) {
                stringReply += "-1";
            } else {
                stringReply += "0 ";
                for (int i = 0; i < netAddresss.size(); ++i) {
                    stringReply += netAddresss.get(i).getAddress();
                    stringReply += "-";
                    stringReply += netAddresss.get(i).getPort();
                    stringReply += " ";
                }
            }
            System.out.println("Reply: " + stringReply);
            NetHelper.writeOnce(socket, stringReply);
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            //System.out.println("wait to read...");
            String line = reader.readLine();
            System.out.println("Receive: " + line);
            String[] requestStrs = line.split(" ");
            if(requestStrs[0].equals(REGISTRY_REQUEST)) {
                registry(requestStrs);
            }
            else if(requestStrs[0].equals(SEARCH_REQUEST)){
                search(requestStrs);
            }
            else {
                System.err.println("Illegal request: " + requestStrs[0]);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}