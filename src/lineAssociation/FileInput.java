package lineAssociation;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by xzbang on 2016/1/22.
 */
public class FileInput {
    public static ArrayList<String> readFile(String path) throws FileNotFoundException {
        ArrayList<String> datas = new ArrayList<String>();
        File file = new File(path);
        if(!file.exists()){
            throw new FileNotFoundException("文件不存在！");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                datas.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return datas;
    }
}
