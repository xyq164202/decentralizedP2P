package helper;

import java.io.File;

public class FileHelper {
    public static String listFiles(String directoryNameName) {
        File directoryName = new File(directoryNameName);
        File[] files= directoryName.listFiles();
        String res = "";
        if(files != null && files.length > 0) {
            for(int i = 0; i < files.length; ++i) {
                res += files[i].getName();
                if(i < files.length - 1) {
                    res += " ";
                }
            }
        }
        return res;
    }
}
