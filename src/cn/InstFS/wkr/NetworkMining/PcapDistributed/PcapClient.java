//package cn.InstFS.wkr.NetworkMining.PcapDistributed;
//
//import cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt.PcapUtils;
//import java.io.*;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//
///**
// * Created by zsc on 2016/7/5.
// */
//public class PcapClient {
//
//    private ClientInit clientInit = new ClientInit();
//    private ArrayList<File> fileList = new ArrayList<File>();
//    private String DELIMITER = "\r\n";
//    private String filePath = "E:\\pcap";
//    private String outPath = "E:\\57data";
//    private String folderPath;//文件绝对路径：E:\57data\routsrc
//    private String folderName;//文件名称：routesrc或node
//    private int index;//从outPath处将父目录与文件名称切分
//    private String type = "pcap";
//    private int BUF_LEN = 5 * 1024 * 1024;
//
//    //    private DataPanel dataPanel;
//    private String IP;
//    private int port;
//
//    public static void main(String[] args) {
//        PcapClient pcapClient = new PcapClient();
//
//        pcapClient.startConnect();
//        new Thread(pcapClient.new ExecuteTask()).start();
//    }
//
////    public PcapClient(DataPanel dataPanel, String IP, int port) {
////        this.dataPanel = dataPanel;
////        this.IP = IP;
////        this.port = port;
////    }
//
//    private boolean isConnected() {
//        return clientInit.isConnected();
//    }
//
//    private void startConnect() {
//        clientInit.connectWithServer();
//        if (isConnected()) {
//            System.out.println("连接");
//        }
//    }
//
//    private void sendReady() {
//        try {
//            clientInit.sendPcapMsg("Ready");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void getTaskList2(String tasks, String part2) {
//        File file = new File(outPath + part2 + File.separator + tasks.split(DELIMITER)[0]);
//        fileList.add(file);
//    }
//
//    private void getTaskList(String tasks) {
//        String[] str = tasks.split(DELIMITER);
//        for (int i = 0; i < str.length; i++) {
//            File file = new File(filePath + "\\" + str[i]);
//            fileList.add(file);
//        }
//    }
//
//    private String getPart2(String tasks2) {
//        return tasks2.split(DELIMITER)[1];
//    }
//
//    private String getPart1(String tasks) {
//        return tasks.substring(0, tasks.indexOf("-"));
//    }
//
////    private void getTaskList(String tasks, String filePath, String type) {
////        File ff = new File(filePath);
////        if (ff.isFile() && filePath.endsWith(type) && tasks.contains(ff.getName())) {
////            fileList.add(ff);
////        } else if (ff.isDirectory()) {
////            File[] files = ff.listFiles();
////            for (File f : files) {
////                getTaskList(tasks, f.getAbsolutePath(), type);
////            }
////        }
////    }
//
//    class ExecuteTask implements Runnable {
//        private String tasks;
//        private String tasks2;
//        private PcapUtils pcapUtils;
//        private long totalLen = 0L;
//        private String part1;
//        private String part2;
//        private String taskFlag;
//
//        @Override
//        public void run() {
//            try {
//                while (true) {
//                    System.out.println("开始");
//                    sendReady();//先发送Ready
//                    System.out.println("ready已发送");
//                    taskFlag = clientInit.receiveStr();//判断执行哪个任务
//                    if (taskFlag.equals("First")) {
//                        tasks = clientInit.receiveStr();//收到要完成的任务string
//                        if (tasks.equals("Empty")) {
//                            System.out.println("empty");
//                            continue;//所有结果已发送，返回
//                        }
//                        fileList.clear();//清空list
//                        getTaskList(tasks);//生成filelist,子文件夹及子文件目录与服务端一致
//                        part1 = getPart1(tasks);
//                        pcapUtils = new PcapUtils();
//                        pcapUtils.First2Step(fileList, outPath + part1);//执行前两步每次在不同的文件夹下保存结果
//                        System.out.println("执行完毕");
//                        clientInit.sendPcapMsg(tasks);
//                        String str = clientInit.receiveStr();
//                        if (str.equals("Absent")) {
//                            System.out.println("absent...");
//                            //返回结果
//                            sendAllResult(outPath + part1);
//                        } else {
//                            continue;
//                        }
//                    } else if (taskFlag.equals("Last")) {
//                        tasks2 = clientInit.receiveStr();
//                        System.out.println("task2: " + tasks2);
//                        if (tasks2.equals("Empty")) {
//                            System.out.println("empty");
//                            continue;//所有结果已发送，返回
//                        }
//                        part2 = getPart2(tasks2);
//                        receiveResult(outPath + part2);
//                        System.out.println("结束接收");
//                        fileList.clear();
//                        getTaskList2(tasks2, part2);
//                        pcapUtils = new PcapUtils();
//                        pcapUtils.Last2Step(fileList, outPath + part2);
//                        System.out.println("执行完毕");
//                        clientInit.sendPcapMsg(tasks2);
//                        String str = clientInit.receiveStr();
//                        if (str.equals("Absent")) {
//                            System.out.println("absent...");
//                            //返回结果
//                            sendAllResult(outPath + part2);
//                        } else {
//                            continue;
//                        }
//
//                    }
//                }
//            } catch (IOException e) {
//                System.out.println("客户端关闭");
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                clientInit.close();
//            }
//        }
//
//        public void receiveResult(String finalFolderPath) throws IOException {
////            totalLen = clientInit.receiveLong();
////            System.out.println("totalLen: " + totalLen);
//            long beginTime = System.currentTimeMillis();
//            String subFolder;
//            String outPath = finalFolderPath;
//            while (true) {
//                String receiveType = clientInit.receiveMsg();
//                if (receiveType.equals("sendFile")) {
//                    receiveFile(outPath);//仅文件
//                } else if (receiveType.equals("sendFolder")) {
//                    subFolder = clientInit.receiveMsg();//routesrc
//                    System.out.println("subFolder: " + subFolder);
//                    outPath = finalFolderPath + File.separator + subFolder;
//                    //生成子目录
//                    File folder = new File(outPath);
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
//            String finalFilePath;
//
//            try {
//                fileName = clientInit.receiveMsg();
//                finalFilePath = outPath + File.separator + fileName;
//                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFilePath)));
//                length = clientInit.receiveInt();
//                while (length > 0) {
//                    clientInit.receiveFullByte(receiveBuffer, 0, length);//read到length才返回，若用read，可能不到length就返回
//                    dos.write(receiveBuffer, 0, length);
//                    dos.flush();
//                    length = clientInit.receiveInt();
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
//
//        private void sendAllResult(String outPath) throws IOException {
//            File file = new File(outPath);
//            if (file.isFile()) {
//                return;
//            } else if (file.isDirectory()) {
//                File[] files = file.listFiles();
//                getFolderTotalLen(outPath);
//                clientInit.sendLong(totalLen);
//                for (File f : files) {
//                    sendResult(f.getAbsolutePath());
//                }
//            }
//            clientInit.sendPcapMsg("endTransmit");
//        }
//
//        private void preprocess(File folder) {
//            folderPath = folder.getAbsolutePath();//E:/57data/routesrc
//            folderName = folder.getName();//routesrc
//            index = folderPath.length() - folderName.length();
//        }
//
//        private void sendResult(String folderPath) throws IOException {
//            File folder = new File(folderPath);
//            //暂时写死routesrc
//            if (!(folder.getName().equals("routesrc") && taskFlag.equals("Last"))) {
//                preprocess(folder);//得到绝对路径、文件名、index
////            System.out.println("fPath: " + folderPath + "fName: " + folderName + "index: " + index);
//                if (folder.isFile()) {
////                totalLen = folder.length();
////                clientInit.sendLong(totalLen);//sendAllResult中发送，保证发送一次
//                    sendFile(folder);
//                } else {
////                getFolderTotalLen(outPath);//得到totalLen
////                clientInit.sendLong(totalLen);//sendAllResult中发送，保证发送一次
//                    sendFolder(folder);
//                }
//            }
//        }
//
//        private void sendFolder(File folder) {
//            String selectFolderPath = folder.getAbsolutePath().substring(index);//选择的文件夹名字：routesrc或node
//            try {
//                clientInit.sendPcapMsg("sendFolder");
//                clientInit.sendPcapMsg(selectFolderPath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            File[] files = folder.listFiles();
//            ArrayList<File> listFile = new ArrayList<File>();
//            ArrayList<File> listFolder = new ArrayList<File>();
//            for (File file : files) {
//                if (file.isFile()) {
//                    listFile.add(file);
//                } else if (file.isDirectory()) {
//                    listFolder.add(file);
//                }
//            }
//            //转换为foreach
//            for (File file : listFile) {
//                sendFile(file);
//            }
//            for (File file : listFolder) {
//                sendFolder(file);
//            }
//        }
//
//        private void sendFile(File file) {
//            byte[] sendBuffer = new byte[BUF_LEN];
//            int length;
//            try {
//                clientInit.sendPcapMsg("sendFile");
//                clientInit.sendPcapMsg(file.getName());
//                System.out.println("fileName: " + file.getName());
//
//                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
//                length = dis.read(sendBuffer, 0, sendBuffer.length);
//                while (length > 0) {
//                    clientInit.sendInt(length);
//                    clientInit.sendByte(sendBuffer, 0, length);
//                    length = dis.read(sendBuffer, 0, sendBuffer.length);
//                }
//                clientInit.sendInt(length);
//                dis.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private void getFolderTotalLen(String path) {
//            this.totalLen = 0L;
//            File folder = new File(path);
//            getFileLen(folder);
//        }
//
//        private void getFileLen(File folder) {
//            File[] files = folder.listFiles();
//            for (File file : files) {
//                if (file.isFile()) {
//                    this.totalLen += file.length();
//                } else if (file.isDirectory()) {
//                    getFileLen(file);
//                }
//            }
//        }
//    }
//
//}
//
//class ClientInit {
//    private ClientConnectServer pcapClientConnectServerMsg = new ClientConnectServer();
//    //    private ClientConnectServerObject clientConnectServerObject = new ClientConnectServerObject();
//    private static boolean flag;
//    private static boolean isConnected;
//
//    public void close() {
//        pcapClientConnectServerMsg.close();
////        clientConnectServerObject.close();
//    }
//
//    public void connectWithServer() {
//        boolean flag = true;
//        Socket socket1 = null;
////        Socket socket2 = null;
//
//        //先启动客户端，不断尝试连接服务端
//        while (flag) {
//            try {
//                socket1 = new Socket("127.0.0.1", 7777);
////                socket2 = new Socket("127.0.0.1", 7777);
//                if (socket1.getPort() == 7777) {
//                    flag = false;
//                }
//            } catch (IOException e) {
//                System.out.println("服务端未启动，10S后重新尝试连接");
//            } finally {
//                try {
//                    if (flag) {
//                        socket1 = null;
////                        socket2 = null;
//                        Thread.sleep(10000);
//                    } else {
//                        break;
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        pcapClientConnectServerMsg.connectServer(socket1);
////        clientConnectServerObject.connectServer2(socket2);
//    }
//
//    public boolean isConnected() {
//        return isConnected;
//    }
//
//    //发送文件
//    public void sendInt(int len) throws IOException {
//        pcapClientConnectServerMsg.getDosWithServer().writeInt(len);
//    }
//
//    //发送文件长度
//    public void sendLong(long len) throws IOException {
//        pcapClientConnectServerMsg.getDosWithServer().writeLong(len);
//        pcapClientConnectServerMsg.getDosWithServer().flush();
//    }
//
//    public void sendByte(byte[] bytes, int off, int len) throws IOException {
//        pcapClientConnectServerMsg.getDosWithServer().write(bytes, off, len);
//        pcapClientConnectServerMsg.getDosWithServer().flush();
//    }
//
//    //发送Ready信息
//    public void sendPcapMsg(String str) throws IOException {
//        pcapClientConnectServerMsg.getDosWithServer().writeUTF(str);
//        pcapClientConnectServerMsg.getDosWithServer().flush();
//    }
//
//    public int receiveInt() throws IOException {
//        return pcapClientConnectServerMsg.getDisWithServer().readInt();
//    }
//
//    public long receiveLong() throws IOException {
//        return pcapClientConnectServerMsg.getDisWithServer().readLong();
//    }
//
//    public String receiveMsg() throws IOException {
//        return pcapClientConnectServerMsg.getDisWithServer().readUTF();
//    }
//
//    public void receiveFullByte(byte[] bytes, int off, int len) throws IOException {
//        pcapClientConnectServerMsg.getDisWithServer().readFully(bytes, off, len);
//    }
//
//
//    //接收执行指令
//    public String receiveStr() throws IOException, ClassNotFoundException {
//        return pcapClientConnectServerMsg.getDisWithServer().readUTF();
//    }
//}
//
////客户端连接类DataInputStream
//class ClientConnectServer {
//    private DataIO dataIO = new DataIO();
//
//    public void close() {
//        try {
//            dataIO.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void connectServer(Socket socket) {
//        try {
//            dataIO.setSocket(socket);
//            dataIO.setDataInputStream(new DataInputStream(dataIO.getSocket().getInputStream()));
//            dataIO.setDataOutputStream(new DataOutputStream(dataIO.getSocket().getOutputStream()));
//            System.out.println("客户端已连接1");
//        } catch (UnknownHostException e) {
//            System.out.println("服务端未启动");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("服务端未启动");
////            System.exit(1);
//            e.printStackTrace();
//        }
//    }
//
//    public Socket getClientSocket() {
//        return dataIO.getSocket();
//    }
//
//    public DataInputStream getDisWithServer() {
//        return dataIO.getDataInputStream();
//    }
//
//    public DataOutputStream getDosWithServer() {
//        return dataIO.getDataOutputStream();
//    }
//
//}
//
////客户端连接类ObjectInputStream
//class ClientConnectServerObject {
//    private ObjectIO objectIO = new ObjectIO();
//
//    public void close() {
//        try {
//            objectIO.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void connectServer2(Socket socket) {
//        try {
//            objectIO.setSocket(socket);
//            objectIO.setObjectInputStream(new ObjectInputStream(objectIO.getSocket().getInputStream()));
//            objectIO.setObjectOutputStream(new ObjectOutputStream(objectIO.getSocket().getOutputStream()));
//            System.out.println("客户端已连接2");
//        } catch (UnknownHostException e) {
//            System.out.println("服务端未启动");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("服务端未启动");
////            System.exit(1);
//            e.printStackTrace();
//        }
//    }
//
//    public Socket getClientSocket() {
//        return objectIO.getSocket();
//    }
//
//    public ObjectInputStream getDisWithServer() {
//        return objectIO.getObjectInputStream();
//    }
//
//    public ObjectOutputStream getDosWithServer() {
//        return objectIO.getObjectOutputStream();
//    }
//
//}
//
//class DataIO {
//    private Socket socket = null;
//    private DataInputStream dataInputStream = null;
//    private DataOutputStream dataOutputStream = null;
//
//    public DataInputStream getDataInputStream() {
//        return dataInputStream;
//    }
//
//    public void setDataInputStream(DataInputStream dataInputStream) {
//        this.dataInputStream = dataInputStream;
//    }
//
//    public DataOutputStream getDataOutputStream() {
//        return dataOutputStream;
//    }
//
//    public void setDataOutputStream(DataOutputStream dataOutputStream) {
//        this.dataOutputStream = dataOutputStream;
//    }
//
//    public Socket getSocket() {
//        return socket;
//    }
//
//    public void setSocket(Socket socket) {
//        this.socket = socket;
//    }
//
//    public void close() throws IOException {
//        try {
//            if (dataInputStream != null) dataInputStream.close();
//            if (socket != null) socket.close();
//            if (dataOutputStream != null) dataOutputStream.close();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//    }
//}
//
//class ObjectIO {
//    private Socket socket = null;
//    private ObjectInputStream objectInputStream = null;
//    private ObjectOutputStream objectOutputStream = null;
//
//    public ObjectInputStream getObjectInputStream() {
//        return objectInputStream;
//    }
//
//    public void setObjectInputStream(ObjectInputStream objectInputStream) {
//        this.objectInputStream = objectInputStream;
//    }
//
//    public ObjectOutputStream getObjectOutputStream() {
//        return objectOutputStream;
//    }
//
//    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
//        this.objectOutputStream = objectOutputStream;
//    }
//
//    public Socket getSocket() {
//        return socket;
//    }
//
//    public void setSocket(Socket socket) {
//        this.socket = socket;
//    }
//
//    public void close() throws IOException {
//        try {
//            if (objectInputStream != null) objectInputStream.close();
//            if (socket != null) socket.close();
//            if (objectOutputStream != null) objectOutputStream.close();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//    }
//}
