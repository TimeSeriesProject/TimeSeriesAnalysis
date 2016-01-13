package associationRules;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * Created by xzbang on 2015/12/23.
 */
public class LinearInput {
    public TreeMap<Integer,Double> sourceDatas = new TreeMap<Integer, Double>();
    public TreeMap<Integer,Double> targetDatas = new TreeMap<Integer, Double>();
    public void csvInput(){
        String filePath = "F:\\工作\\57\\data\\annual-domestic-sales-and-advert.csv";
        try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                while((lineTxt = bufferedReader.readLine()) != null){
                    String[] strings = lineTxt.split(",");
                    int key = Integer.parseInt(strings[0]);
                    double sourceValue = Double.parseDouble(strings[1]);
                    double targetValue = Double.parseDouble(strings[2]);
                    sourceDatas.put(key,sourceValue);
                    targetDatas.put(key,targetValue);
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }
    public void dataItemsInput(DataItems sourceDataItems,DataItems targetDataItems){
        List<Date> time = sourceDataItems.time;
        List<String> sourceData = sourceDataItems.data;
        List<String> targetData = targetDataItems.data;
        int len = time.size();
        for(int i = 0;i < len;i++){
            double sourceValue = Double.parseDouble(sourceData.get(i));
            double targetValue = Double.parseDouble(targetData.get(i));
            sourceDatas.put(i,sourceValue);
            targetDatas.put(i,targetValue);
        }
    }
}
