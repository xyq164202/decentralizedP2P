import helper.NetHelper;

import java.net.Socket;
import java.io.*;

public class PeerServer extends Thread {
    private Socket clientSocket;
    private static final int BUFFER_SIZE = 1024;
    private String directory = "";

    public PeerServer(Socket clientSocket, String directory) {
        this.clientSocket = clientSocket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            /*
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String request = reader.readLine();
            */
            String request = NetHelper.readLine(clientSocket);

            System.out.println("request: " + request);

            String[] reqStrs = request.split(" ");
            String fileName = reqStrs[1];
            download(clientSocket, fileName);
            //reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(Socket clientSocket, String fileName) {
        try {
            String wholeFileName = directory + File.separator + fileName;
            FileInputStream inputStream = new FileInputStream(wholeFileName);
            OutputStream outputStream = clientSocket.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            byte[] buffer = new byte[BUFFER_SIZE];
            System.out.println("Start downloading...");
            while (true) {
                int n = inputStream.read(buffer);
                if (n > 0) {
                    outputStream.write(buffer);
                    outputStream.flush();
                } else {
                    System.out.println("Finish downloading...");
                    inputStream.close();
                    outputStream.close();
                    break;
                }
            }
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
