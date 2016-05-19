package lineAssociation;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by xzbang on 2016/1/22.
 */
public class FileOutput {

    public static void writeFileLinear(Map<Integer, Linear> map,String path,Linear lastPoint){
        {
            File file = new File(path);
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(file);
                for(int i : map.keySet()){
                    pw.append(map.get(i).toDetailString());
                    pw.append("\n");
                }
                pw.append(lastPoint.toDetailString());
                pw.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                if(pw!=null){
                    pw.close();
                }
            }
        }
    }

    public static void writeFile(Map<Integer,Double> map,String path){
        File file = new File(path);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
            for(int i : map.keySet()){
                pw.append(String.valueOf(i));
                pw.append(',');
                pw.append(String.valueOf(map.get(i)));
                pw.append("\n");
            }
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(pw!=null){
                pw.close();
            }
        }
    }

    public static void writeFile(String path,Map<Double,Integer> map){
        TreeMap<Integer,Double> treeMap = new TreeMap<Integer, Double>();
        for(double d : map.keySet()){
            treeMap.put(map.get(d),d);
        }
        writeFile(treeMap,path);
    }

    public static void writeFileInteger(Map<Integer,Integer> map,String path){
        File file = new File(path);
       /* //避免覆盖旧文件，对旧文件重命名
        if(file.exists()){
            File parent = file.getParentFile();
            int fileNum = parent.listFiles().length;
            String[] paths = path.split("\\.");
//            file.renameTo(new File(paths[0]+fileNum+"\\."+paths[1]));
            file.renameTo(new File("123.dat"));
        }
        file = new File(path);*/
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
            for(int i : map.keySet()){
                pw.append(String.valueOf(i));
                pw.append(',');
                pw.append(String.valueOf(map.get(i)));
                pw.append("\n");
            }
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(pw!=null){
                pw.close();
            }
        }
    }

    public static void writeFile(Set<Integer> set,String path){
        File file = new File(path);
       /* //避免覆盖旧文件，对旧文件重命名
        if(file.exists()){
            File parent = file.getParentFile();
            int fileNum = parent.listFiles().length;
            String[] paths = path.split("\\.");
//            file.renameTo(new File(paths[0]+fileNum+"\\."+paths[1]));
            file.renameTo(new File("123.dat"));
        }
        file = new File(path);*/
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
            for(int i : set){
                pw.append(String.valueOf(i));
                pw.append("\n");
            }
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(pw!=null){
                pw.close();
            }
        }
    }
    public static void writeRateData(ArrayList<Double> data,String path){
    	
    	File file = new File(path);
       
         PrintWriter pw = null;
         try {
             pw = new PrintWriter(file);
             for(int i = 0;i < data.size();i++){
                 pw.append(String.valueOf(i)+","+String.valueOf(data.get(i)));
                 pw.append("\n");
             }
             pw.flush();
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }finally {
             if(pw!=null){
                 pw.close();
             }
         }
    }
}
