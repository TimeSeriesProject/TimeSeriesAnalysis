package cn.InstFS.wkr.NetworkMining.Results;

import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.MinerFactorySettings;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;

import java.io.*;
import java.nio.file.Files;
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
        dataPath = generateResultPath("configs/algorithmsParams.xml")+"/";
        mkdir(dataPath);
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

    public Object file2Result() {
        Object result = null;
        try {
            FileInputStream fileIn = new FileInputStream(dataPath+fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            result = in.readObject();
        }catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
        return result;
    }

    public void result2File(MinerFactorySettings settings, TaskCombination taskCom, Object MinerResults) {
        this.fileName = genFileName(settings, taskCom);

        try {
            FileOutputStream fileOut = new FileOutputStream(dataPath+fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(MinerResults);
            out.close();
            fileOut.close();
            System.out.println("序列化保存");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public boolean hasFile(MinerFactorySettings settings) {
        File path = new File(dataPath);
        String[] list;
        final String fileName = genFileName(settings);
//        final String reg = ".*"+fileName.substring(fileName.indexOf("_")+1)+"{1}";
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

    public boolean hasFile(MinerFactorySettings settings, TaskCombination taskCom) {
        File path = new File(dataPath);
        String[] list;
        final String fileName = genFileName(settings, taskCom);
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

        /*for (MiningMethod methodChecked: settings.getMiningMethodsChecked()) {
            fileName.append(methodChecked).append(";");
        }
        fileName.deleteCharAt(fileName.length()-1);*/

        fileName.append(".ser");

        return fileName.toString();
    }

    private String genFileName(MinerFactorySettings settings, TaskCombination taskCom) {
        StringBuilder fileName = new StringBuilder();
        String sourceDataFilePath = settings.getDataPath();
        String sourceDataName = sourceDataFilePath.substring(sourceDataFilePath.trim().lastIndexOf("\\")+1);
        File dataFile = new File(sourceDataFilePath);
        if (dataFile.isFile())
            sourceDataName = sourceDataName.substring(0, sourceDataName.lastIndexOf("."));

        System.out.println(settings.getStartDate());
        System.out.println(settings.getEndDate());
        
        fileName.append(settings.getStartDate().getTime()).append("_"); // 起始时间
        fileName.append(settings.getEndDate().getTime()).append("_");    // 终止时间
        fileName.append(settings.getMinerType()).append("_");
        fileName.append(taskCom.getName()).append("_");
        fileName.append(miningObject.toString()).append("_");   // 挖掘对象
        fileName.append(settings.getGranularity()); // 时间粒度


        fileName.append(".ser");

        return fileName.toString();
    }

    /**
     * 根据算法参数生成md5值作为结果存储目录
     * @param paramFile 算法参数文件
     * @return
     */
    private String generateResultPath(String paramFile) {
        String md5 = null;
        try {
            md5 = Md5Util.fileMD5("configs/algorithmsParams.xml");
            System.out.println(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataPath+md5;
    }

    private void mkdir(String dataPath) {
        File dir = new File(dataPath);
        if (dir.exists()) {
            System.out.println(dataPath + "目标目录已经存在");
        }else {
            if (dir.mkdirs()) {
                System.out.println("创建目录" + dataPath + "成功！");

                //将算法参数文件复制入该目录
                try {
                    Files.copy(new File("configs/algorithmsParams.xml").toPath(),new File(dataPath+"/algorithmsParams.xml").toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("创建目录" + dataPath+ "失败！");
            }
        }
    }
}
