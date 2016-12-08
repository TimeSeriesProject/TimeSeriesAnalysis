package Distributed;

import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPath;
import cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt.PcapUtils;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zsc on 2016/5/20.
 */
public class Client {
    private ClientInit clientInit = new ClientInit();
    private DataPanel dataPanel;
    private String IP;
    private int port;

    //pcapClient
//    private ClientInit clientInit = new ClientInit();
    private ArrayList<File> fileList = new ArrayList<File>();
    private String DELIMITER = "\r\n";
    private String filePath;
    private String outPath;
    private String folderPath;//文件绝对路径：E:\57data\routsrc
    private String folderName;//文件名称：routesrc或node
    private int index;//从outPath处将父目录与文件名称切分
    private String type = "pcap";
    private int BUF_LEN = 5 * 1024 * 1024;


//    public static void main(String[] args) {
//        Client client = new Client();
//        client.startConnect();
//        new Thread(client.new ReceiveServerMsg()).start();
//    }

    public Client(DataPanel dataPanel, String IP, int port, String filePath, String outPath) {
        this.dataPanel = dataPanel;
        this.IP = IP;
        this.port = port;
        this.filePath = filePath;
        this.outPath = outPath;
    }

    //连接服务端
    public void startConnect() {
        clientInit.connectWithServer(dataPanel, IP, port);
        if (isConnected()) {
            System.out.println("连接");
            dataPanel.sendLog("客户端已连接");//控制台输出
        }
    }

    //判断是否连接上，决定输出
    public boolean isConnected(){
        return clientInit.isConnected();
    }

    //退出
    public void close() {
        clientInit.close();
        clientInit.setFlag(false);
//        clientInit.setConnected(false);//暂时不用
    }

    //发送ready，表示准备就绪，当前无任务执行
//    public void sendReady() {
//        try {
//            clientInit.sendMsg("Ready");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //pcapClient方法
//    private boolean isConnected() {
//        return clientInit.isConnected();
//    }

//    private void startConnect() {
//        clientInit.connectWithServer();
//        if (isConnected()) {
//            System.out.println("连接");
//        }
//    }

//    private void sendReady() {
//        try {
//            clientInit.sendPcapMsg("Ready");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void getTaskList2(String tasks, String part2) {
        File file = new File(outPath + part2 + File.separator + tasks.split(DELIMITER)[0]);
        fileList.add(file);
    }

    private void getTaskList(String tasks) {
        String[] str = tasks.split(DELIMITER);
        for (int i = 0; i < str.length; i++) {
            File file = new File(filePath + "\\" + str[i]);//得到E:/pcap/0-0.pcap等等
            fileList.add(file);
        }
    }

    private String getPart2(String tasks2) {
        return tasks2.split(DELIMITER)[1];
    }

    private String getPart1(String tasks) {
        return tasks.substring(tasks.lastIndexOf("\\") - 1, tasks.lastIndexOf("\\"));//得到sub文件夹，例如0,1...
//        return tasks.substring(0, tasks.indexOf("-"));
    }

    //接收信息封装
    class ReceiveServerMsg implements Runnable {
        TaskCombination taskCombination;
        long a;
        long b;

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("开始");
                    dataPanel.sendLog("开始");//控制台输出
//                    sendReady();//先发送Ready
                    clientInit.sendMsg("Ready");//先发送Ready
                    taskCombination = (TaskCombination) clientInit.receiveObj();//收到要完成的任务class
                    NetworkMinerFactory.getInstance();
                    TaskCombinationList.addListenerOnly(taskCombination, false);
                    switch (taskCombination.getMinerType()) {

                        case MiningType_SinglenodeOrNodePair:
                            dataPanel.sendLog("正在执行node中..." );//控制台输出
                            HashMap<TaskCombination, MinerNodeResults> nodeResults = NetworkMinerFactory.getInstance().
                                    startAllNodeMinersDis(MiningObject.fromString(taskCombination.getMiningObject()));
                            TaskCombinationResult nodeTask = new TaskCombinationResult();
                            for (Map.Entry<TaskCombination, MinerNodeResults> entry : nodeResults.entrySet()) {
                                nodeTask.setName(entry.getKey().getName());
                                nodeTask.setMinerType(entry.getKey().getMinerType());
                                nodeTask.setTaskRange(entry.getKey().getTaskRange());
                                nodeTask.setMiningObject(MiningObject.fromString(entry.getKey().getMiningObject()));
                                nodeTask.setMinerNodeResults(entry.getValue());
                            }
                            a = System.currentTimeMillis();
                            clientInit.sendResult(nodeTask);//将结果发送回去
                            System.out.println("node结果已返回");
                            dataPanel.sendLog("node结果已返回");//控制台输出
                            b = System.currentTimeMillis();
                            System.out.println("node返回时间：" + (b - a));
                            dataPanel.sendLog("node返回时间：" + (b - a));//控制台输出
                            break;

                        case MiningType_ProtocolAssociation:
                            dataPanel.sendLog("正在执行protocol中..." );//控制台输出
                            HashMap<TaskCombination, MinerProtocolResults> protocolResults = NetworkMinerFactory.getInstance().
                                    startAllProtocolMinersDis(MiningObject.fromString(taskCombination.getMiningObject()));
                            TaskCombinationResult protocolTask = new TaskCombinationResult();
                            for (Map.Entry<TaskCombination, MinerProtocolResults> entry : protocolResults.entrySet()) {
                                protocolTask.setName(entry.getKey().getName());
                                protocolTask.setMinerType(entry.getKey().getMinerType());
                                protocolTask.setMiningObject(MiningObject.fromString(entry.getKey().getMiningObject()));
                                protocolTask.setMinerProtocolResults(entry.getValue());
                            }
                            a = System.currentTimeMillis();
                            clientInit.sendResult(protocolTask);//将结果发送回去
                            System.out.println("protocol结果已返回");
                            dataPanel.sendLog("protocol结果已返回");//控制台输出
                            b = System.currentTimeMillis();
                            System.out.println("protocol返回时间：" + (b - a));
                            dataPanel.sendLog("protocol返回时间：" + (b - a));//控制台输出
                            break;

                        case MiningType_Path:
                            dataPanel.sendLog("正在执行path中..." );//控制台输出
                            HashMap<TaskCombination, MinerResultsPath> pathResults = NetworkMinerFactory.getInstance().
                                    startAllPathMinersDis(MiningObject.fromString(taskCombination.getMiningObject()));
                            System.out.println("miningobject: " + MiningObject.fromString(taskCombination.getMiningObject()));
                            TaskCombinationResult pathTask = new TaskCombinationResult();
                            for (Map.Entry<TaskCombination, MinerResultsPath> entry : pathResults.entrySet()) {
                                pathTask.setName(entry.getKey().getName());
                                pathTask.setMinerType(entry.getKey().getMinerType());
                                pathTask.setMiningObject(MiningObject.fromString(entry.getKey().getMiningObject()));
                                pathTask.setMinerResultsPath(entry.getValue());
                            }
                            a = System.currentTimeMillis();
                            clientInit.sendResult(pathTask);//将结果发送回去
                            System.out.println("path结果已返回");
                            dataPanel.sendLog("path结果已返回");//控制台输出
                            b = System.currentTimeMillis();
                            System.out.println("path返回时间：" + (b - a));
                            dataPanel.sendLog("path返回时间：" + (b - a));//控制台输出

                            break;

                        case MiningTypes_WholeNetwork:
                            dataPanel.sendLog("正在执行network中..." );//控制台输出
                            HashMap<TaskCombination, MinerNodeResults> networkResults = NetworkMinerFactory.getInstance().
                                    startAllNetworkStructrueMinersDis(MiningObject.fromString(taskCombination.getMiningObject()));
                            TaskCombinationResult networkTask = new TaskCombinationResult();
                            for (Map.Entry<TaskCombination, MinerNodeResults> entry : networkResults.entrySet()) {
                                networkTask.setName(entry.getKey().getName());
                                networkTask.setMinerType(entry.getKey().getMinerType());
                                networkTask.setMiningObject(MiningObject.fromString(entry.getKey().getMiningObject()));
                                networkTask.setMinerNetworkResults(entry.getValue());
                            }
                            a = System.currentTimeMillis();
                            clientInit.sendResult(networkTask);//将结果发送回去
                            System.out.println("network结果已返回");
                            dataPanel.sendLog("network结果已返回");//控制台输出
                            b = System.currentTimeMillis();
                            System.out.println("network结果已返回：" + (b - a));
                            dataPanel.sendLog("network结果已返回：" + (b - a));//控制台输出
                            break;

                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("客户端关闭");
                dataPanel.sendLog("客户端关闭");
                dataPanel.LoginQuit();
//                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                clientInit.closeTask();
            }
        }
    }

    //pcapClient
    class ExecuteTask implements Runnable {
        private String tasks;
        private String tasks2;
        private PcapUtils pcapUtils;
        private long totalLen = 0L;
        private String part1;
        private String part2;
        private String taskFlag;

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("开始");
                    clientInit.sendPcapMsg("Ready");//先发送Ready
                    System.out.println("Pcap_ready已发送");
                    taskFlag = clientInit.receiveStr();//判断执行哪个任务
                    if (taskFlag.equals("First")) {
                        tasks = clientInit.receiveStr();//收到要完成的任务string
                        System.out.println("接收的任务" + tasks);
                        if (tasks.equals("Empty")) {
                            System.out.println("empty");
                            continue;//所有结果已发送，返回
                        }
                        fileList.clear();//清空文件list
                        getTaskList(tasks);//生成filelist,子文件夹及子文件目录与服务端一致
                        part1 = getPart1(tasks);//发送来的一个任务代表一个路由节点的所有pcap文件，part1代表路由节点的名称
                        System.out.println("part1: " + part1);
                        pcapUtils = new PcapUtils();
                        dataPanel.sendLog("正在执行First2Step");
                        pcapUtils.First2Step(fileList, outPath + part1);//执行前两步每次在不同的文件夹下保存结果
                        dataPanel.sendLog("First2Step执行完毕");
                        System.out.println("执行完毕");
                        clientInit.sendPcapMsg(tasks);//将tasks返回，服务端判断是否存在此文件
                        String str = clientInit.receiveStr();
                        if (str.equals("Absent")) {
                            System.out.println("absent...");
                            //返回结果
                            sendAllResult(outPath + part1);
                            dataPanel.sendLog("结果已返回");
                        } else {
                            continue;
                        }
                    } else if (taskFlag.equals("Last")) {
                        tasks2 = clientInit.receiveStr();
                        System.out.println("task2: " + tasks2);
                        if (tasks2.equals("Empty")) {
                            System.out.println("empty");
                            continue;//所有结果已发送，返回
                        }
                        part2 = getPart2(tasks2);//得到10.0.0.1_10.0.0.2
                        System.out.println("part2: " + part2);
                        //删除已存在的所有文件
                        File ff = new File(outPath + part2);
                        if (ff.exists() && ff.isDirectory()) {
                            System.out.println("删除文件last");
                            deleteFile(outPath + part2);
                        }
                        receiveResult(outPath + part2);//得到E:/57data/10.0.0.1_10.0.0.2
                        System.out.println("结束接收");
                        fileList.clear();
                        getTaskList2(tasks2, part2);
                        pcapUtils = new PcapUtils();
                        dataPanel.sendLog("正在执行Last2Step");
                        pcapUtils.Last2Step(fileList, outPath + part2);
                        dataPanel.sendLog("Last2Step执行完毕");
                        System.out.println("执行完毕");
                        clientInit.sendPcapMsg(tasks2);
                        String str = clientInit.receiveStr();
                        if (str.equals("Absent")) {
                            System.out.println("absent...");
                            //返回结果
                            sendAllResult(outPath + part2);
                            dataPanel.sendLog("结果已返回");
                        } else {
                            continue;
                        }

                    }
                }
            } catch (IOException e) {
                System.out.println("pcap客户端关闭");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                clientInit.closePcap();
            }
        }

        //递归删除文件夹下所有文件
        public void deleteFile(String fileName) {
            File file = new File(fileName);
            if (file.exists()) {
                if (file.isFile()){
                    file.delete();
                } else if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        //删除子文件
                        deleteFile(files[i].getAbsolutePath());
                    }
                    file.delete();
                }
            } else {
                System.out.println("文件不存在");
            }
        }

        public void receiveResult(String finalFolderPath) throws IOException {
//            totalLen = clientInit.receiveLong();
//            System.out.println("totalLen: " + totalLen);
            long beginTime = System.currentTimeMillis();
            String subFolder;
            String outPath = finalFolderPath;
            while (true) {
                String receiveType = clientInit.receiveMsg();
                if (receiveType.equals("sendFile")) {
                    receiveFile(outPath);//仅文件
                } else if (receiveType.equals("sendFolder")) {
                    subFolder = clientInit.receiveMsg();//routesrc
                    System.out.println("subFolder: " + subFolder);
                    outPath = finalFolderPath + File.separator + subFolder;
                    //生成子目录
                    File folder = new File(outPath);
                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
                } else if (receiveType.equals("endTransmit")) {
                    break;
                }
            }
        }

        private void receiveFile(String outPath) {
            byte[] receiveBuffer = new byte[BUF_LEN];
            int length;
            long passedlen = 0;
            String fileName;
            String finalFilePath;

            try {
                fileName = clientInit.receiveMsg();
                finalFilePath = outPath + File.separator + fileName;
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFilePath)));
                length = clientInit.receiveInt();
                while (length > 0) {
                    clientInit.receiveFullByte(receiveBuffer, 0, length);//read到length才返回，若用read，可能不到length就返回
                    dos.write(receiveBuffer, 0, length);
                    dos.flush();
                    length = clientInit.receiveInt();
                }
                System.out.println("接收方结束循环");
                dos.close();
            } catch (IOException e) {
                System.out.println("接收文件报错");
                e.printStackTrace();
            }

        }


        private void sendAllResult(String outPath) throws IOException {
            File file = new File(outPath);
            if (file.isFile()) {
                return;
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                getFolderTotalLen(outPath);
                clientInit.sendLong(totalLen);
                for (File f : files) {
                    sendResult(f.getAbsolutePath());
                }
            }
            clientInit.sendPcapMsg("endTransmit");
        }

        private void preprocess(File folder) {
            folderPath = folder.getAbsolutePath();//E:/57data/routesrc
            folderName = folder.getName();//routesrc
            index = folderPath.length() - folderName.length();
        }

        private void sendResult(String folderPath) throws IOException {
            File folder = new File(folderPath);
            //暂时写死routesrc
            if (!(folder.getName().equals("routesrc") && taskFlag.equals("Last"))) {
                preprocess(folder);//得到绝对路径、文件名、index
//            System.out.println("fPath: " + folderPath + "fName: " + folderName + "index: " + index);
                if (folder.isFile()) {
//                totalLen = folder.length();
//                clientInit.sendLong(totalLen);//sendAllResult中发送，保证发送一次
                    sendFile(folder);
                } else {
//                getFolderTotalLen(outPath);//得到totalLen
//                clientInit.sendLong(totalLen);//sendAllResult中发送，保证发送一次
                    sendFolder(folder);
                }
            }
        }

        private void sendFolder(File folder) {
            String selectFolderPath = folder.getAbsolutePath().substring(index);//选择的文件夹名字：routesrc或node
            try {
                clientInit.sendPcapMsg("sendFolder");
                clientInit.sendPcapMsg(selectFolderPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File[] files = folder.listFiles();
            ArrayList<File> listFile = new ArrayList<File>();
            ArrayList<File> listFolder = new ArrayList<File>();
            for (File file : files) {
                if (file.isFile()) {
                    listFile.add(file);
                } else if (file.isDirectory()) {
                    listFolder.add(file);
                }
            }
            //转换为foreach
            for (File file : listFile) {
                sendFile(file);
            }
            for (File file : listFolder) {
                sendFolder(file);
            }
        }

        private void sendFile(File file) {
            byte[] sendBuffer = new byte[BUF_LEN];
            int length;
            try {
                clientInit.sendPcapMsg("sendFile");
                clientInit.sendPcapMsg(file.getName());
                System.out.println("fileName: " + file.getName());

                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                length = dis.read(sendBuffer, 0, sendBuffer.length);
                while (length > 0) {
                    clientInit.sendInt(length);//发送length是为了最后length为-1，作为结束的判断，因此每次都要发送length
                    clientInit.sendByte(sendBuffer, 0, length);
                    length = dis.read(sendBuffer, 0, sendBuffer.length);
                }
                clientInit.sendInt(length);
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getFolderTotalLen(String path) {
            this.totalLen = 0L;
            File folder = new File(path);
            getFileLen(folder);
        }

        private void getFileLen(File folder) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    this.totalLen += file.length();
                } else if (file.isDirectory()) {
                    getFileLen(file);
                }
            }
        }
    }
}

//初始化连接
class ClientInit {
    private ClientConnectServer clientConnectServerMsg = new ClientConnectServer();
    private ClientConnectServerObject clientConnectServerObject = new ClientConnectServerObject();
    private ClientConnectServer pcapClientConnectServerMsg = new ClientConnectServer();
    private static boolean flag;
    private static boolean isConnected;

    public void close(){
        clientConnectServerMsg.close();
        clientConnectServerObject.close();
        pcapClientConnectServerMsg.close();
    }

    public void closeTask(){
        clientConnectServerMsg.close();
        clientConnectServerObject.close();
    }

    public void closePcap(){
        pcapClientConnectServerMsg.close();
    }

    public void connectWithServer(DataPanel dataPanel, String IP, int port) {

        flag = true;
        isConnected = false;
        Socket socket1 = null;
        Socket socket2 = null;
        Socket socket3 = null;
        final DataPanel dataPanel1 = dataPanel;

        //先启动客户端，不断尝试连接服务端
        while (flag) {
            try {
                socket1 = new Socket(IP, port);
                socket2 = new Socket(IP, port);
                socket3 = new Socket(IP, port);
                if (socket1.getPort() == port && String.valueOf(socket1.getInetAddress()).equals("/" + IP)
                        && socket2.getPort() == port && String.valueOf(socket2.getInetAddress()).equals("/" + IP)
                        && socket3.getPort() == port && String.valueOf(socket3.getInetAddress()).equals("/" + IP)) {
                    System.out.println("客户端启动...");
                    clientConnectServerMsg.connectServer(socket1);
                    clientConnectServerObject.connectServer2(socket2);
                    pcapClientConnectServerMsg.connectServer(socket3);
                    flag = false;
                    isConnected = true;
                }
            } catch (IOException e) {
                System.out.println("服务端未启动，10S后重新尝试连接");
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dataPanel1.sendLog("服务端未启动，10S后重新尝试连接");
                    }
                });
            } finally {
                try {
                    if (flag) {
                        socket1 = null;
                        socket2 = null;
                        socket3 = null;
                        Thread.sleep(10000);
                    } else {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

  /*  public void connectWithServer() {
        boolean flag = true;
        Socket socket1 = null;
        Socket socket2 = null;

        //先启动客户端，不断尝试连接服务端
        while (flag) {
            try {
                socket1 = new Socket("127.0.0.1", 7777);
                socket2 = new Socket("127.0.0.1", 7777);
                if (socket1.getPort() == 7777) {
                    flag = false;
                }
            } catch (IOException e) {
                System.out.println("服务端未启动，10S后重新尝试连接");
            } finally {
                try {
                    if (flag) {
                        socket1 = null;
                        socket2 = null;
                        Thread.sleep(10000);
                    } else {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        clientConnectServerMsg.connectServer(socket1);
        clientConnectServerObject.connectServer2(socket2);
    }*/

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

//    public void setConnected(boolean connected) {
//        isConnected = connected;
//    }

    public boolean isConnected() {
        return isConnected;
    }

    //发送结果
    public void sendResult(TaskCombinationResult taskCombinationResult) throws IOException {
        clientConnectServerObject.getDosWithServer().writeObject(taskCombinationResult);
    }

    //发送Ready信息
    public void sendMsg(String str) throws IOException {
        clientConnectServerMsg.getDosWithServer().writeUTF(str);
    }

    //接收执行指令
    public Object receiveObj() throws IOException, ClassNotFoundException {
        return clientConnectServerObject.getDisWithServer().readObject();
    }

    //pcapClient
//    public boolean isConnected() {
//        return isConnected;
//    }

    //发送文件
    public void sendInt(int len) throws IOException {
        pcapClientConnectServerMsg.getDosWithServer().writeInt(len);
    }

    //发送文件长度
    public void sendLong(long len) throws IOException {
        pcapClientConnectServerMsg.getDosWithServer().writeLong(len);
        pcapClientConnectServerMsg.getDosWithServer().flush();
    }

    public void sendByte(byte[] bytes, int off, int len) throws IOException {
        pcapClientConnectServerMsg.getDosWithServer().write(bytes, off, len);
        pcapClientConnectServerMsg.getDosWithServer().flush();
    }

    //发送Ready信息
    public void sendPcapMsg(String str) throws IOException {
        pcapClientConnectServerMsg.getDosWithServer().writeUTF(str);
        pcapClientConnectServerMsg.getDosWithServer().flush();
    }

    public int receiveInt() throws IOException {
        return pcapClientConnectServerMsg.getDisWithServer().readInt();
    }

    public long receiveLong() throws IOException {
        return pcapClientConnectServerMsg.getDisWithServer().readLong();
    }

    public String receiveMsg() throws IOException {
        return pcapClientConnectServerMsg.getDisWithServer().readUTF();
    }

    public void receiveFullByte(byte[] bytes, int off, int len) throws IOException {
        pcapClientConnectServerMsg.getDisWithServer().readFully(bytes, off, len);
    }


    //接收执行指令
    public String receiveStr() throws IOException, ClassNotFoundException {
        return pcapClientConnectServerMsg.getDisWithServer().readUTF();
    }
}

//客户端连接类DataInputStream
class ClientConnectServer {
    private DataIO dataIO = new DataIO();

    public void close() {
        try {
            dataIO.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectServer(Socket socket) {
        try {
            dataIO.setSocket(socket);
            dataIO.setDataInputStream(new DataInputStream(dataIO.getSocket().getInputStream()));
            dataIO.setDataOutputStream(new DataOutputStream(dataIO.getSocket().getOutputStream()));
            System.out.println("客户端已连接1");
        } catch (UnknownHostException e) {
            System.out.println("服务端未启动");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("服务端未启动");
//            System.exit(1);
            e.printStackTrace();
        }
    }

    public Socket getClientSocket() {
        return dataIO.getSocket();
    }

    public DataInputStream getDisWithServer() {
        return dataIO.getDataInputStream();
    }

    public DataOutputStream getDosWithServer() {
        return dataIO.getDataOutputStream();
    }

}

//客户端连接类ObjectInputStream
class ClientConnectServerObject {
    private ObjectIO objectIO = new ObjectIO();

    public void close() {
        try {
            objectIO.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectServer2(Socket socket) {
        try {
            objectIO.setSocket(socket);
            objectIO.setObjectInputStream(new ObjectInputStream(objectIO.getSocket().getInputStream()));
            objectIO.setObjectOutputStream(new ObjectOutputStream(objectIO.getSocket().getOutputStream()));
            System.out.println("客户端已连接2");
        } catch (UnknownHostException e) {
            System.out.println("服务端未启动");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("服务端未启动");
//            System.exit(1);
            e.printStackTrace();
        }
    }

    public Socket getClientSocket() {
        return objectIO.getSocket();
    }

    public ObjectInputStream getDisWithServer() {
        return objectIO.getObjectInputStream();
    }

    public ObjectOutputStream getDosWithServer() {
        return objectIO.getObjectOutputStream();
    }

}

class DataIO {
    private Socket socket = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public void setDataInputStream(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void close() throws IOException {
        try {
            if (dataInputStream != null) dataInputStream.close();
            if (socket != null) socket.close();
            if (dataOutputStream != null) dataOutputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}

class ObjectIO {
    private Socket socket = null;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void close() throws IOException {
        try {
            if (objectInputStream != null) objectInputStream.close();
            if (socket != null) socket.close();
            if (objectOutputStream != null) objectOutputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
