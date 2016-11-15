//package cn.InstFS.wkr.NetworkMining.PcapDistributed;
//
//import Distributed.PcapPanel;
//import Distributed.TrafficKey;
//
//import java.io.*;
//import java.net.BindException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.nio.channels.FileChannel;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.*;
//
///**
// * Created by zsc on 2016/7/5.
// */
//public class PcapServer {
//    private PcapPanel pcapPanel;
//    private ArrayList<String> allTasks = new ArrayList<String>();
//    private ConcurrentHashMap<String, String> allTasksTags = new ConcurrentHashMap<String, String>();//带标签，所有不同类型任务
//    private ConcurrentHashMap<String, String> nameMap = new ConcurrentHashMap<String, String>();//文件part
//    private ConcurrentHashMap<String, String> nameMap2 = new ConcurrentHashMap<String, String>();//文件part
//    private ConcurrentHashMap<String, String> combineFile = new ConcurrentHashMap<String, String>();//合并文件,key=最后合并生成的文件，valude=待合并的小文件的文件夹路径
//    private ConcurrentHashMap<String, String> combineFile2 = new ConcurrentHashMap<String, String>();//合并文件,key=最后合并生成的文件，valude=待合并的小文件的文件夹路径
//    private HashSet<String> delFile = new HashSet<String>();//删除文件
//    private HashSet<String> delFile2 = new HashSet<String>();//删除文件
//    private HashMap<String, StringBuilder> tasksMap = new HashMap<String, StringBuilder>();
//    private HashMap<String, String> swapMap = new HashMap<String, String>();//文件名\r\n,路由号
//    private HashMap<String, String> swapMap2 = new HashMap<String, String>();//routesrc/1.1_1.2, 1.1_1.2
//    private ArrayList<String> allTasks2 = new ArrayList<String>();//第二步任务
//    private ConcurrentHashMap<String, String> allTasksTags2 = new ConcurrentHashMap<String, String>();//带标签，所有不同类型任务2
//
//    private String DELIMITER = "\r\n";
//    private String inPath;
//    private String outPath = "D:\\57data";
//    //    private String fileName;
//    private int index;
//    private int BUF_LEN = 5 * 1024 * 1024;
//    private int pcapCount1 = 0;//发送次数
//    private int pcapCount2 = 0;//发送次数
//    private int recCount = 0;
//    private int recCount2 = 0;
//    private int tasksCount = 0;
//
//    private Lock recLock = new ReentrantLock();//接收结果
//    private Lock sendLock = new ReentrantLock();
//    private Lock recLock2 = new ReentrantLock();//接收结果和第一步要分开，否则出bug
//    private Lock sendLock2 = new ReentrantLock();
//
//    public PcapServer() {
//
//    }
//
//    public PcapServer(PcapPanel pcapPanel, String inPath, String outPath) {
//        this.pcapPanel = pcapPanel;
//        this.inPath = inPath;
//        this.outPath = outPath;
//        this.index = outPath.length() + 1;
//    }
//
//    public static void main(String[] args) throws FileNotFoundException {
//        PcapServer pcapServer = new PcapServer();
//        String filePath = "D:\\pcap";
//        pcapServer.genTasks(filePath, "pcap");
//        for (int i = 0; i < pcapServer.allTasks.size(); i++) {
//            System.out.println(pcapServer.allTasks.get(i));
//        }
//        new Thread(pcapServer.new PcapServerStart()).start();
//    }
//
//    public void genTasks(String filePath, String type) {
//        allTasks.clear();
//        allTasksTags.clear();
//        ArrayList<String> fileNames = new ArrayList<String>();
//        getFileList(fileNames, filePath, type);
//
//        for (String name : fileNames) {
//            String key = name.substring(0, name.indexOf("-"));
//            if (tasksMap.containsKey(key)) {
//                tasksMap.get(key).append(name).append(DELIMITER);
//            } else {
//                tasksMap.put(key, new StringBuilder(name).append(DELIMITER));
//            }
//        }
//        for (Map.Entry<String, StringBuilder> entry : tasksMap.entrySet()) {
//            allTasks.add(entry.getValue().toString());
//            allTasksTags.put(entry.getValue().toString(), "n");
//            swapMap.put(entry.getValue().toString(), entry.getKey());
//        }
//        tasksCount = allTasks.size();
//    }
//
//
//    private int getFileList(ArrayList<String> fileNames, String filePath, String type) {
//        int num = 0;
//        File ff = new File(filePath);
//        if (ff.isFile() && filePath.endsWith(type)) {
//            fileNames.add(ff.getName());
//            num += 1;
//        } else if (ff.isDirectory()) {
//            File[] files = ff.listFiles();
//            for (File f : files) {
//                getFileList(fileNames, f.getAbsolutePath(), type);
//            }
//        }
//        return num;
//    }
//
//    public class PcapServerStart implements Runnable {
//        private ServerSocket serverSocket = null;
//        private UserClient dataClient;
//        private boolean start = false;
//
//        @Override
//        public void run() {
//            try {
//                serverSocket = new ServerSocket(7777);
//                start = true;
//            } catch (BindException e) {
//                System.out.println("端口使用中...");
//                System.exit(0);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                while (start) {
//                    Socket dataSocket = serverSocket.accept();//接收dataoutputstream
//                    dataClient = new UserClient(dataSocket);
//                    ParsePcap parsePcap = new ParsePcap(dataClient);//连接
//                    System.out.println("一个客户端已连接！");
//                    new Thread(parsePcap).start();//启动线程
//                }
//            } catch (IOException e) {
//                System.out.println("服务端错误位置");
//                e.printStackTrace();
//            } finally {
//                try {
//                    serverSocket.close();
//                    start = false;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    class ParsePcap implements Runnable {
//        private boolean firstConnected = false;
//        private boolean lastConnected = false;
//        private UserClient userClient;
//        private String dataFromClient = "";
//        private long totalLen;
//        private String finalFolderPath;
//        private String task;
//        private String task2;
//        private String status;
//        private boolean isEmpty = false;
//        private boolean isEmpty2 = false;
//
//
//        ParsePcap(UserClient userClient) {
//            this.userClient = userClient;
//            firstConnected = true;
//        }
//
//        @Override
//        public void run() {
//            try {
//                pcapPanel.getBar().setMaximum(allTasks.size());//进度条最大
//                pcapPanel.getBar().setValue(recCount);
//                pcapPanel.getjLabel().setText("阶段 1/2");
//                while (firstConnected) {
//                    dataFromClient = userClient.receiveMsg();
//                    System.out.println("First接收到ready");
//                    userClient.sendMsg("First");
//                    if (dataFromClient.equals("Ready")) {
//                        //pcapCount1 < 或 =两种情况，只发送一次
//                        sendLock.lock();
//                        try {
//                            if (pcapCount1 < allTasks.size()) {
//                                userClient.sendTask(allTasks.get(pcapCount1));
//                                System.out.println("第" + pcapCount1 + "次已发送" + allTasks.size());
//                                pcapCount1 += 1;
//                                System.out.println("下一次发送：" + pcapCount1);
//                            } else {
//                                int temp = 0;//中途最后一个结果发回来，发送Empty，避免客户端发送ready后接收不到任务造成死锁
//
//                                //找到没有完成的任务
//                                for (Map.Entry<String, String> entry : allTasksTags.entrySet()) {
//                                    temp += 1;
//                                    System.out.println("遍历TaskCombination= " + entry.getKey() +
//                                            " and String = " + entry.getValue());
//                                    if (entry.getValue().equals("n")) {
//                                        userClient.sendTask(entry.getKey());
//                                        System.out.println("第二次发送的task：" + entry.getKey());
//                                        break;
//                                    }
//                                    if (temp == allTasksTags.size()) {
//                                        userClient.sendMsg("Empty");//全部结果已返回，客户端重新待命
//                                        System.out.println("发送Empty");
//                                        isEmpty = true;
//                                    }
//                                }
//                            }
//                        } finally {
//                            sendLock.unlock();
//                        }
//                    }
//
//                    //接收结果
//                    recLock.lock();
//                    try {
//                        if (!isEmpty) {
//                            //判断是否返回已存在结果
//                            task = userClient.receiveMsg();
//                            if (allTasksTags.get(task).equals("y")) {
//                                userClient.sendMsg(status = "Existent");
//                            } else {
//                                userClient.sendMsg(status = "Absent");
//                            }
//
//                            if (status.equals("Absent")) {
//                                status = null;
//                                if (recCount < tasksCount) {
//                                    finalFolderPath = outPath;
//                                    //接收文件
//                                    receiveResult(finalFolderPath);
//                                    updateMap(task);
//                                    recCount += 1;
//                                    pcapPanel.getBar().setValue(recCount);
//                                    pcapPanel.getjLabel().setText("阶段 1/2");
//                                    if (recCount == tasksCount) {
////                                    userClient.close();
//                                        System.out.println("运行结束");
//                                        combineFiles(combineFile);
//                                        System.out.println("文件已合并");
//                                        deleteFile(delFile);
//                                        System.out.println("文件已删除");
//                                        firstConnected = false;
//                                        lastConnected = true;
////                                        recCon.await();//释放cLock，但recLock无法释放!!!
//                                    }
//                                }
//                            } else if (status.equals("Existent")) {
//                                status = null;
//                                if (recCount < tasksCount) {
//                                    continue;
//                                } else if (recCount == tasksCount) {
//                                    System.out.println("运行结束");
//                                    firstConnected = false;
//                                    lastConnected = true;
////                                    recCon.await();
//                                }
//                            }
//                        } else {
//                            System.out.println("运行结束");
//                            firstConnected = false;
//                            lastConnected = true;
////                            recCon.await();
//                        }
//                    } finally {
//                        recLock.unlock();
//                    }
//                }
//
////                recCount = 0;
//                tasksCount = allTasks2.size();
//                pcapPanel.getBar().setMaximum(allTasks2.size());//进度条最大
//                pcapPanel.getBar().setValue(recCount2);
//                pcapPanel.getjLabel().setText("阶段 2/2");
//                //执行后2步
//                while (lastConnected) {
//                    dataFromClient = userClient.receiveMsg();
//                    System.out.println("last接收到ready");
//                    userClient.sendMsg("Last");
//                    if (dataFromClient.equals("Ready")) {
//                        //pcapCount1 < 或 =两种情况，只发送一次
//                        sendLock2.lock();
//                        try {
//                            if (pcapCount2 < allTasks2.size()) {
//                                userClient.sendTask(allTasks2.get(pcapCount2));
//                                System.out.println("第" + pcapCount2 + "次已发送" + allTasks2.size());
//                                sendFileTask(allTasks2.get(pcapCount2).split(DELIMITER)[0]);//发送单个文件,routesrc/10.0.0.1_10.0.0.2.bin
//                                pcapCount2 += 1;
//                                System.out.println("下一次发送：" + pcapCount2);
//                            } else {
//                                int temp = 0;//中途最后一个结果发回来，发送Empty，避免客户端发送ready后接收不到任务造成死锁
//
//                                //找到没有完成的任务
//                                for (Map.Entry<String, String> entry : allTasksTags2.entrySet()) {
//                                    temp += 1;
//                                    System.out.println("遍历TaskCombination= " + entry.getKey() +
//                                            " and String = " + entry.getValue());
//                                    if (entry.getValue().equals("n")) {
//                                        userClient.sendTask(entry.getKey());
//                                        sendFileTask(entry.getKey().split(DELIMITER)[0]);
//                                        System.out.println("第二次发送的task：" + entry.getKey());
//                                        break;
//                                    }
//                                    if (temp == allTasksTags2.size()) {
//                                        userClient.sendMsg("Empty");//全部结果已返回，客户端重新待命
//                                        System.out.println("发送Empty");
//                                        isEmpty2 = true;
//                                    }
//                                }
//                            }
//                        } finally {
//                            sendLock2.unlock();
//                        }
//                    }
//
//                    //接收结果
//                    recLock2.lock();
//                    try {
//                        if (!isEmpty2) {
//                            //判断是否返回已存在结果
//                            task2 = userClient.receiveMsg();
//                            if (allTasksTags2.get(task2).equals("y")) {
//                                userClient.sendMsg(status = "Existent");
//                            } else {
//                                userClient.sendMsg(status = "Absent");
//                            }
//
//                            if (status.equals("Absent")) {
//                                status = null;
//                                if (recCount2 < tasksCount) {
//                                    finalFolderPath = outPath;
//                                    //接收文件
//                                    receiveResult2(finalFolderPath);
//                                    updateMap2(task2);
//                                    recCount2 += 1;
//                                    pcapPanel.getBar().setValue(recCount2);
//                                    pcapPanel.getjLabel().setText("阶段 2/2");
//                                    if (recCount2 == tasksCount) {
//                                        System.out.println("运行结束2.1");
//                                        combineFiles2(combineFile2);
//                                        System.out.println("文件已合并");
//                                        deleteFile(delFile2);
//                                        System.out.println("文件已删除");
//                                        pcapPanel.getjLabel().setText("已完成");
//                                        lastConnected = false;
//                                    }
//                                }
//                            } else if (status.equals("Existent")) {
//                                status = null;
//                                if (recCount2 < tasksCount) {
//                                    continue;
//                                } else if (recCount2 == tasksCount) {
//                                    System.out.println("运行结束2.2");
//                                    lastConnected = false;
//                                }
//                            }
//                        } else {
//                            System.out.println("运行结束2.3");
//                            lastConnected = false;
//                        }
//                    } finally {
//                        recLock2.unlock();
//                    }
//
//                }
//            } catch (IOException e) {
//                System.out.println("发送文件报错");
//                e.printStackTrace();
//            } finally {
//                firstConnected = false;
//                lastConnected = false;
//                try {
//                    userClient.close();
//                    System.out.println("关闭流");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//
//        private void sendFileTask(String task) throws IOException{
//            String finalPath = outPath + File.separator + task;
//            String subFolder = task.substring(0, task.indexOf("\\"));
//            File file = new File(finalPath);
//            sendFolder(subFolder);//发送routesrc文件夹
//            sendFile(file);
//            userClient.sendMsg("endTransmit");//结束任务
//        }
//
//        private void sendFile(File file) {
//            byte[] sendBuffer = new byte[BUF_LEN];
//            int length;
//            try {
//                userClient.sendMsg("sendFile");
//                userClient.sendMsg(file.getName());
//                System.out.println("fileName: " + file.getName());
//
//                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
//                length = dis.read(sendBuffer, 0, sendBuffer.length);
//                while (length > 0) {
//                    userClient.sendInt(length);
//                    userClient.sendByte(sendBuffer, 0, length);
//                    length = dis.read(sendBuffer, 0, sendBuffer.length);
//                }
//                userClient.sendInt(length);
//                dis.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private void sendFolder(String subFolder) {
//            try {
//                userClient.sendMsg("sendFolder");
//                userClient.sendMsg(subFolder);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        //traffic排序
//        private void sortTraffic(String path, String outPath) {
//            try {
//                InputStreamReader in = new InputStreamReader(new FileInputStream(path), "UTF-8");
//                BufferedReader bin = new BufferedReader(in);
//                String curLine;
//                ArrayList<TrafficKey> keys = new ArrayList<TrafficKey>();
//
//                while ((curLine = bin.readLine()) != null) {
//                    String str[] = curLine.split(",");
//                    TrafficKey key = new TrafficKey();
//                    key.setTime(Long.valueOf(str[0]));
//                    key.setSrcIp(str[1]);
//                    key.setDstIp(str[2]);
//                    key.setProtocol(str[3]);
//                    keys.add(key);
//                }
//                bin.close();
//                Collections.sort(keys);
//
//                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outPath), "UTF-8");
//                BufferedWriter bout = new BufferedWriter(out);
//
//                for (TrafficKey key : keys) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(key.getTime()).append(",").append(key.getSrcIp()).append(",").
//                            append(key.getDstIp()).append(",").append(key.getProtocol());
//                    bout.write(sb.toString());
//                    bout.newLine();
//                }
//                bout.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private void combineFiles2(ConcurrentHashMap<String, String> combineFile) throws IOException {
//            for (Map.Entry<String, String> entry : combineFile.entrySet()) {
//                ArrayList<File> fileList = new ArrayList<File>();
//                getFilePath(fileList, entry.getValue(), "txt");//得到待删除文件
//
//                File key = new File(entry.getKey());//D:/57data/traffic/10.0.0.1.txt
//                String name = key.getName();//10.0.0.1.txt
//                File comFile = new File(entry.getValue() + File.separator + name);//合并后暂存为D:/57data/traffic/10.0.0.1/10.0.0.1.txt
//                if (!comFile.exists()) {
//                    comFile.createNewFile();
//                }
//                FileChannel outChannel = new FileOutputStream(comFile).getChannel();
//                FileChannel inChannel;
//                for (File file : fileList) {
//                    inChannel = new FileInputStream(file).getChannel();
//                    inChannel.transferTo(0, inChannel.size(), outChannel);
//                    inChannel.close();
//                }
//                outChannel.close();
//                sortTraffic(comFile.getAbsolutePath(), entry.getKey());//最后生成排序后的txt
//            }
//        }
//
//        private void combineFiles(ConcurrentHashMap<String, String> combineFile) throws IOException {
//            for (Map.Entry<String, String> entry : combineFile.entrySet()) {
//                ArrayList<File> fileList = new ArrayList<File>();
//                getFilePath(fileList, entry.getValue(), "bin");//得到待删除文件
//
//                File outputFile = new File(entry.getKey());
//                if (!outputFile.exists()) {
//                    outputFile.createNewFile();
//                }
//                FileChannel outChannel = new FileOutputStream(outputFile).getChannel();
//                FileChannel inChannel;
//                for (File file : fileList) {
//                    inChannel = new FileInputStream(file).getChannel();
//                    inChannel.transferTo(0, inChannel.size(), outChannel);
//                    inChannel.close();
//                }
//                outChannel.close();
//            }
//        }
//
//        private int getFilePath(ArrayList<File> fileList, String filePath, String type) {
//            int num = 0;
//            File ff = new File(filePath);
//            if (ff.isFile() && filePath.endsWith(type)) {
//                fileList.add(ff);
//                num += 1;
//            } else if (ff.isDirectory()) {
//                File[] files = ff.listFiles();
//                for (File f : files) {
//                    getFilePath(fileList, f.getAbsolutePath(), type);
//                }
//            }
//            return num;
//        }
//
//        private boolean deleteFile(HashSet<String> fileNameList) {
//            boolean flag = false;
//            for (String fileName : fileNameList) {
//                File file = new File(fileName);
//                if (!file.exists()) {
//                    System.out.println("删除文件失败：" + fileName + "文件不存在");
//                    flag = false;
//                } else {
//                    if (file.isFile()) {
//                        flag = deleteFile(fileName);
//                    } else {
//                        flag = deleteDirectory(fileName);
//                    }
//                }
//            }
//            return flag;
//        }
//
//        public boolean deleteFile(String fileName) {
//            File file = new File(fileName);
//            if (file.isFile() && file.exists()) {
//                file.delete();
////                System.out.println("删除单个文件" + fileName + "成功！");
//                return true;
//            } else {
////                System.out.println("删除单个文件" + fileName + "失败！");
//                return false;
//            }
//        }
//
//        public boolean deleteDirectory(String dir) {
////        //如果dir不以文件分隔符结尾，自动添加文件分隔符
////        if (!dir.endsWith(File.separator)) {
////            dir = dir + File.separator;
////        }
//            File dirFile = new File(dir);
//            //如果dir对应的文件不存在，或者不是一个目录，则退出
//            if (!dirFile.exists() || !dirFile.isDirectory()) {
////                System.out.println("删除目录失败" + dir + "目录不存在！");
//                return false;
//            }
//            boolean flag = true;
//            //删除文件夹下的所有文件(包括子目录)
//            File[] files = dirFile.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                //删除子文件
//                if (files[i].isFile()) {
//                    flag = deleteFile(files[i].getAbsolutePath());
//                    if (!flag) {
//                        break;
//                    }
//                }
//                //删除子目录
//                else {
//                    flag = deleteDirectory(files[i].getAbsolutePath());
//                    if (!flag) {
//                        break;
//                    }
//                }
//            }
//
//            if (!flag) {
//                System.out.println("删除目录失败");
//                return false;
//            }
//
//            //删除当前目录
//            if (dirFile.delete()) {
////                System.out.println("删除目录" + dir + "成功！");
//                return true;
//            } else {
////                System.out.println("删除目录" + dir + "失败！");
//                return false;
//            }
//        }
//
//        public String getExtension(String fileName) {
//            return fileName.substring(fileName.lastIndexOf("."));
//        }
//
//        public String getName(String fileName) {
//            return fileName.substring(0, fileName.lastIndexOf("."));
//        }
//
//        public void genPart(String fileName, String type) {
//            if (!type.equals(".bin")) {
//                return;
//            }
//            if (!nameMap.containsKey(fileName)) {
//                nameMap.put(fileName, swapMap.get(task));
//            } else {
//                nameMap.remove(fileName);
//                nameMap.put(fileName, swapMap.get(task));
//            }
//        }
//
//        public void genPart2(String fileName, String type) {
//            if (!type.equals(".txt")) {
//                return;
//            }
//            if (!nameMap2.containsKey(fileName)) {
//                nameMap2.put(fileName, swapMap2.get(task2));
//            } else {
//                nameMap2.remove(fileName);
//                nameMap2.put(fileName, swapMap2.get(task2));
//            }
//        }
//
//        public void receiveResult(String finalFolderPath) throws IOException {
//            totalLen = userClient.receiveLong();
//            System.out.println("totalLen: " + totalLen);
//            long beginTime = System.currentTimeMillis();
//            String subFolder;
//            while (true) {
//                String receiveType = userClient.receiveMsg();
//                if (receiveType.equals("sendFile")) {
//                    receiveFile(finalFolderPath);//仅文件
//                } else if (receiveType.equals("sendFolder")) {
//                    subFolder = userClient.receiveMsg();//发送方的selectFolderPath子目录：routesrc或node
//                    finalFolderPath = outPath + File.separator + subFolder;
//                    //生成子目录
//                    File folder = new File(finalFolderPath);
//                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
//                } else if (receiveType.equals("endTransmit")) {
//                    break;
//                }
//            }
//        }
//
//        private void receiveFile(String outPath) {
//            byte[] receiveBuffer = new byte[BUF_LEN];
//            int length;
//            long passedlen = 0;
//            String fileName;
//            String name;
//            String extension;
//            String finalFilePath;
//            String filePath;
//            String folderPath;
//            String subFolder;
//            String task2;
//
//            try {
//                fileName = userClient.receiveMsg();
//                name = getName(fileName);//得到文件名
//                extension = getExtension(fileName);//得到扩展名
//                genPart(fileName, extension);//得到路由器号作为part
//                //创建文件夹/routesrc/10.0.1.1_10.0.1.2/...
//                //生成合并文件map、删除文件list
//                if (extension.equals(".bin")) {
//                    File folder = new File(outPath + File.separator + name);
//                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
//                    finalFilePath = outPath + File.separator + name + File.separator + name + "_part_" + nameMap.get(fileName) + extension;
////                    System.out.println("part: " + nameMap.get(fileName));
//                    filePath = outPath + File.separator + fileName;//D:/57data/routesrc/10.0.0.1_10.0.0.2.bin
//                    folderPath = outPath + File.separator + name;//D:/57data/routesrc/10.0.0.1_10.0.0.2/
//                    //生成第二步的task
//                    subFolder = outPath.substring(index) + File.separator + fileName;//routesrc/10.0.0.1_10.0.0.2.bin
//                    task2 = subFolder + DELIMITER + name;
//
//                    if (!combineFile.containsKey(filePath)) {
//                        combineFile.put(filePath, folderPath);
//                        delFile.add(folderPath);//待删除的10.0.0.1-10.0.0.2文件夹们
//                        //生成第二步任务list
//                        allTasks2.add(task2);
//                        allTasksTags2.put(task2, "n");
//                        swapMap2.put(task2, name);//routesrc/10.0.0.1_10.0.0.2.bin, 10.0.0.1_10.0.0.2;用于生成part
//                    }
//                } else {
//                    finalFilePath = outPath + File.separator + fileName;
//                }
//
//                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFilePath)));
//                length = userClient.receiveInt();
//                while (length > 0) {
//                    userClient.receiveFullByte(receiveBuffer, 0, length);//read到length才返回，若用read，可能不到length就返回
//                    dos.write(receiveBuffer, 0, length);
//                    dos.flush();
//                    length = userClient.receiveInt();
//                }
//                System.out.println("接收方结束循环");
//                dos.close();
//            } catch (IOException e) {
//                System.out.println("接收文件报错");
//                e.printStackTrace();
//            }
//
//        }
//
//        public void receiveResult2(String finalFolderPath) throws IOException {
//            totalLen = userClient.receiveLong();
//            System.out.println("totalLen: " + totalLen);
//            long beginTime = System.currentTimeMillis();
//            String subFolder;
//            while (true) {
//                String receiveType = userClient.receiveMsg();
//                if (receiveType.equals("sendFile")) {
//                    receiveFile2(finalFolderPath);//仅文件
//                } else if (receiveType.equals("sendFolder")) {
//                    subFolder = userClient.receiveMsg();//发送方的selectFolderPath子目录：route或traffic
//                    finalFolderPath = outPath + File.separator + subFolder;
//                    //生成子目录
//                    File folder = new File(finalFolderPath);
//                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
//                } else if (receiveType.equals("endTransmit")) {
//                    break;
//                }
//            }
//        }
//
//        private void receiveFile2(String outPath) {
//            byte[] receiveBuffer = new byte[BUF_LEN];
//            int length;
//            long passedlen = 0;
//            String fileName;
//            String name;
//            String extension;
//            String finalFilePath;
//            String filePath;
//            String folderPath;
//
//            try {
//                fileName = userClient.receiveMsg();
////                finalFilePath = outPath + File.separator + fileName;
//                name = getName(fileName);//得到文件名
//                extension = getExtension(fileName);//得到扩展名
//                genPart2(fileName, extension);//得到part
//
//
//                if (extension.equals(".txt")) {
//                    File folder = new File(outPath + File.separator + name);
//                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
//                    finalFilePath = outPath + File.separator + name + File.separator + name + "_part_" + nameMap2.get(fileName) + extension;
////                    System.out.println("part: " + nameMap.get(fileName));
//                    filePath = outPath + File.separator + fileName;//D:/57data/traffic/10.0.0.1.txt
//                    folderPath = outPath + File.separator + name;//D:/57data/traffic/10.0.0.1/
//                    if (!combineFile2.containsKey(filePath)) {
//                        combineFile2.put(filePath, folderPath);
//                        delFile2.add(folderPath);//待删除的10.0.0.1-10.0.0.2文件夹们
//                    }
//                } else {
//                    finalFilePath = outPath + File.separator + fileName;
//                }
//
//                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFilePath)));
//                length = userClient.receiveInt();
//                while (length > 0) {
//                    userClient.receiveFullByte(receiveBuffer, 0, length);//read到length才返回，若用read，可能不到length就返回
//                    dos.write(receiveBuffer, 0, length);
//                    dos.flush();
//                    length = userClient.receiveInt();
//                }
//                System.out.println("接收方结束循环");
//                dos.close();
//            } catch (IOException e) {
//                System.out.println("接收文件报错");
//                e.printStackTrace();
//            }
//        }
//
//        private void updateMap(String task) {
//            if (allTasksTags.get(task).equals("n")) {
//                allTasksTags.remove(task);
//                allTasksTags.put(task, "y");//更新标记，表示完成
//            }
//        }
//
//        private void updateMap2(String task2) {
//            if (allTasksTags2.get(task2).equals("n")) {
//                allTasksTags2.remove(task2);
//                allTasksTags2.put(task2, "y");//更新标记，表示完成
//            }
//        }
//    }
//
//}
//
//class UserClient {
//    private Socket socket = null;
//    private DataInputStream disWithClient;
//    private DataOutputStream dosWithClient;
//
//    public UserClient(Socket socket) {
//        this.socket = socket;
//        try {
//            disWithClient = new DataInputStream(socket.getInputStream());
//            dosWithClient = new DataOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void close() throws IOException {
//        try {
//            if (disWithClient != null) disWithClient.close();
//            if (socket != null) socket.close();
//            if (dosWithClient != null) dosWithClient.close();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//    }
//
//
//    public int receiveInt() throws IOException {
//        return disWithClient.readInt();
//    }
//
//    public long receiveLong() throws IOException {
//        return disWithClient.readLong();
//    }
//
//    public String receiveMsg() throws IOException {
//        return disWithClient.readUTF();
//    }
//
//    public void receiveFullByte(byte[] bytes, int off, int len) throws IOException {
//        disWithClient.readFully(bytes, off, len);
//    }
//
//    //发送文件
//    public void sendInt(int len) throws IOException {
//        dosWithClient.writeInt(len);
//    }
//
//    //发送文件长度
//    public void sendLong(long len) throws IOException {
//        dosWithClient.writeLong(len);
//        dosWithClient.flush();
//    }
//
//    public void sendByte(byte[] bytes, int off, int len) throws IOException {
//        dosWithClient.write(bytes, off, len);
//        dosWithClient.flush();
//    }
//
//    public void sendTask(String task) throws IOException {
//        dosWithClient.writeUTF(task);
//        dosWithClient.flush();
//    }
//
//    public void sendMsg(String str) throws IOException {
//        dosWithClient.writeUTF(str);
//        dosWithClient.flush();
//    }
//}
//
