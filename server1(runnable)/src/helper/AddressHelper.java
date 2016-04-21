package helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AddressHelper {
    //read address from configuration file
    public static List<NetAddress> getAddressFromFile(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            List<NetAddress> netAddressList = new ArrayList<>();
            while(true) {
                String line = bufferedReader.readLine();
                if(line != null && line.length() > 0) {
                    String[] strs = line.split("-");
                    NetAddress netAddress = new NetAddress();
                    netAddress.setAddress(strs[0]);
                    netAddress.setPort(Integer.parseInt(strs[1]));
                    netAddressList.add(netAddress);
                }
                else {
                    break;
                }
            }
            return netAddressList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
