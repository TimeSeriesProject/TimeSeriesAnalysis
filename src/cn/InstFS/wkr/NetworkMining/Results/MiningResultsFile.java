package cn.InstFS.wkr.NetworkMining.Results;

import cn.InstFS.wkr.NetworkMining.Miner.MinerFactorySettings;
import cn.InstFS.wkr.NetworkMining.Miner.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.UIs.WholeNetworkFrame;
import com.sun.javafx.tk.Toolkit;
import com.sun.jmx.snmp.Timestamp;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author Arbor vlinq@gmail.com
 * @date 2016/7/13
 */
public class MiningResultsFile {
    private String dataPath = "./result/";
    private String fileName;
    private MiningObject miningObject; //区分通信次数、流量为不同文件


    public MiningResultsFile(MiningObject miningObject) {
        this.miningObject = miningObject;
    }

    public void resultMap2File(MinerFactorySettings settings, HashMap resultMap) {
        this.fileName = genFileName(settings);

        try {
            FileOutputStream fileOut = new FileOutputStream(dataPath+fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(resultMap);
            out.close();
            fileOut.close();
            System.out.println("序列化保存");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public HashMap file2ResultMap() {
        HashMap resultMap = new HashMap<>();
        try {
            FileInputStream fileIn = new FileInputStream(dataPath+fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            resultMap = (HashMap)in.readObject();
        }catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }

        return resultMap;
    }

    public boolean hasFile(MinerFactorySettings settings) {
        File path = new File(dataPath);
        String[] list;
        final String fileName = genFileName(settings);
        list = path.list(new FilenameFilter() {
            private Pattern pattern = Pattern.compile(fileName);
            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        });
        if (list.length != 0) {
            this.fileName = list[0];
            return true;
        }
        else
            return false;
    }

    private String genFileName(MinerFactorySettings settings) {
        StringBuilder fileName = new StringBuilder();
        String sourceDataFilePath = settings.getDataPath();
        String sourceDataName = sourceDataFilePath.substring(sourceDataFilePath.trim().lastIndexOf("\\")+1);
        File dataFile = new File(sourceDataFilePath);
        if (dataFile.isFile())
            sourceDataName = sourceDataName.substring(0, sourceDataName.lastIndexOf("."));

        fileName.append(dataFile.lastModified()).append("_"); // 源文件最后更改时间
        fileName.append(sourceDataName).append("_");    // 源文件名
        fileName.append(settings.getMinerType()).append("_");
        fileName.append(miningObject.toString()).append("_");   // 挖掘对象
        fileName.append(settings.getGranularity()); // 时间粒度

        fileName.append(".ser");

        return fileName.toString();
    }
}
