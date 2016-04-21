package helper;

import java.io.*;
import java.net.Socket;

public class NetHelper {

    public static void writeOnce(Socket socket, String content) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(content);
            writer.write("\r\n");
            writer.flush();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readLine(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
