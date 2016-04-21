package test;

import java.io.*;
import java.net.Socket;

//perfermance test
public class EvaluationTest {
    public static void testRegistry(int indexNumber, int requestCount) {
        try {
            long curr = System.currentTimeMillis();
            System.out.println("Concurrent request size is: " + requestCount);
            System.out.println("Test begins at: " + curr);

            for(int i = 0; i < requestCount; ++i) {
                int port = 9000 + i % indexNumber;
                Socket socket = new Socket("127.0.0.1", port);
                OutputStream outputStream = socket.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String registryStr = "registry " + port + " 1.txt 2.txt\r\n";
                writer.write(registryStr);
                writer.flush();
            }

            long end = System.currentTimeMillis();
            System.out.println("Test ends at: " + end);
            long cost = end - curr;
            System.out.println("Test takes " + cost + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testSearch(int indexNumber, int requestCount) {
        try {
            long curr = System.currentTimeMillis();
            System.out.println("search operation test");
            System.out.println("Concurrent request size is: " + requestCount);
            System.out.println("Test begins at: " + curr);

            for(int i = 0; i < requestCount; ++i) {
                int port = 9000 + i % indexNumber;
                Socket socket = new Socket("127.0.0.1", port);
                OutputStream outputStream = socket.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String registryStr = "search" + " 1.txt\r\n";
                writer.write(registryStr);
                writer.flush();
                String line = reader.readLine();
                writer.close();
                socket.close();
            }

            long end = System.currentTimeMillis();
            System.out.println("Test ends at: " + end);
            long cost = end - curr;
            System.out.println("Test takes " + cost + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            int port = 9000;
            int requestCount = 500;
            int indexNumber = 4;
            Object object = new Object();

            //testRegistry(4, requestCount);
            //testSearch(indexNumber, requestCount);
            new TestClientObtainFile(indexNumber, requestCount, 1).start();
            new TestClientObtainFile(indexNumber, requestCount, 2).start();
            new TestClientObtainFile(indexNumber, requestCount, 3).start();
            new TestClientObtainFile(indexNumber, requestCount, 4).start();
            new TestClientObtainFile(indexNumber, requestCount, 5).start();
            new TestClientObtainFile(indexNumber, requestCount, 6).start();
            new TestClientObtainFile(indexNumber, requestCount, 7).start();
            new TestClientObtainFile(indexNumber, requestCount, 8).start();

            Thread.sleep(18);
            /*
            List<Thread> threadList = new ArrayList<>();
            for(int i = 0; i < requestCount; ++i) {
                threadList.add(new test.RequestConcurr(port, object));
            }
            */


            //sequence send request test
            /*
            for(int i = 0; i < requestCount; ++i) {
                sendRequest(port);
            }
            */
            //concurrent send requests
            /*
            for(int i = 0; i < requestCount; ++i) {
                threadList.get(i).start();
                threadList.get(i).join();
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class RequestConcurr extends Thread {
    private int port;
    private Object waitObject;

    public RequestConcurr(int port, Object waitObject) {
        this.port = port;
        this.waitObject = waitObject;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket("127.0.0.1", 9000);
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            writer.write("search 1.txt\r\n");
            writer.flush();
            String line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class TestClientObtainFile extends Thread {
    private int id;
    private int indexNumber;
    private int requestCount;
    private static final int BUFFER_SIZE = 1024;

    public TestClientObtainFile(int indexNumber, int requestCount, int id) {
        this.indexNumber = indexNumber;
        this.requestCount = requestCount;
        this.id = id;
    }

    @Override
    public void run() {

        long curr = System.currentTimeMillis();
        //System.out.println("Concurrent request size is: " + requestCount);
        //System.out.println("Test begins at: " + curr + " with id " + id);

        for(int i = 0; i < requestCount; ++i) {
            int port = 8000 + i % 4;
            try {
                Socket socket = new Socket("127.0.0.1", port);
                OutputStream outputStream = socket.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                writer.write("obtain 1.txt\r\n");
                writer.flush();
                //file size: 1KB
                receiveFile(socket, "1.txt");
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        long end = System.currentTimeMillis();
        //System.out.println("Test ends at: " + end + " with id " + id);
        long cost = end - curr;
        System.out.println("Test takes " + cost + "ms " + " with id " + id);
    }

    public static void receiveFile(Socket socket, String fileName) {
        try {
            InputStream inputStream = socket.getInputStream();
            //OutputStream outputStream = socket.getOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            while(true) {
                int n = inputStream.read(buffer);
                //System.out.println("receive " + n + " bytes...");
                if(n > 0){
                    fileOutputStream.write(buffer);
                    //fileOutputStream.flush();
                }
                else {
                    //System.out.println("finished receive file...");
                    inputStream.close();
                    fileOutputStream.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
