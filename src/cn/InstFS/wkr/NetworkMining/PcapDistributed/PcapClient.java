package cn.InstFS.wkr.NetworkMining.PcapDistributed;

import cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt.PcapUtils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by zsc on 2016/7/5.
 */
public class PcapClient {

    private ClientInit clientInit = new ClientInit();
    private ArrayList<File> fileList = new ArrayList<File>();
    private String filePath = "E:\\pppp";
    private String outPath = "E:\\57data";
    private String folderPath;
    private String folderName;
    private int index;
    private String type = "pcap";
    private int BUF_LEN = 5 * 1024 * 1024;

//    private DataPanel dataPanel;
    private String IP;
    private int port;

    public static void main(String[] args) {
        PcapClient pcapClient = new PcapClient();

        pcapClient.startConnect();
        new Thread(pcapClient.new ExeFirst2Step()).start();
    }

//    public PcapClient(DataPanel dataPanel, String IP, int port) {
//        this.dataPanel = dataPanel;
//        this.IP = IP;
//        this.port = port;
//    }

    private boolean isConnected() {
        return clientInit.isConnected();
    }

    private void startConnect() {
        clientInit.connectWithServer();
        if (isConnected()) {
            System.out.println("连接");
        }
    }

    private void sendReady() {
        try {
            clientInit.sendMsg("Ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getTaskList(String tasks, String filePath, String type) {
        File ff = new File(filePath);
        if (ff.isFile() && filePath.endsWith(type) && tasks.contains(ff.getName())) {
            fileList.add(ff);
        } else if (ff.isDirectory()) {
            File[] files = ff.listFiles();
            for (File f : files) {
                getTaskList(tasks, f.getAbsolutePath(), type);
            }
        }
        System.out.println("filelist: " + fileList.size());
    }

    class ExeFirst2Step implements Runnable {
        String tasks;
        PcapUtils pcapUtils;
        long totalLen = 0L;

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("开始");
                    sendReady();//先发送Ready
                    tasks = clientInit.receiveData();//收到要完成的任务string
                    getTaskList(tasks, filePath, type);//生成filelist
                    pcapUtils = new PcapUtils();
                    pcapUtils.First2Step(fileList, outPath);//执行前两步
                    System.out.println("执行完毕");
                    //返回结果
                    sendResult(outPath);

                }
            } catch (IOException e) {
                System.out.println("客户端关闭");
//                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                clientInit.close();
            }
        }

        public void preprocess(File folder) {
            folderPath = folder.getAbsolutePath();
            folderName = folder.getName();
            index = folderPath.length() - folderName.length();
        }

        public void sendResult(String outPath) throws IOException {
            File folder = new File(outPath);
            preprocess(folder);//得到路径，文件位置
            System.out.println("fPath: " + folderPath + "fName: " + folderName + "index: " + index);
            long beginTime = 0L;
            long endTime;
            beginTime = System.currentTimeMillis();
            if (folder.isFile()) {
                totalLen = folder.length();
                clientInit.sendLong(totalLen);
                sendFile(folder);
            } else {
                getFolderTotalLen(outPath);//得到totalLen
                clientInit.sendLong(totalLen);
                sendFolder(folder);
            }
            clientInit.sendMsg("endTransmit");

        }

        private void sendFolder(File folder) {
            String selectFolderPath = folder.getAbsolutePath().substring(index);//选择的文件夹名字
            try {
                clientInit.sendMsg("sendFolder");
                clientInit.sendMsg(selectFolderPath);
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

        public void sendFile(File file) {
            System.out.println("进入sendFile");
            byte[] sendBuffer = new byte[BUF_LEN];
            int length;
            try {
                clientInit.sendMsg("sendFile");
                clientInit.sendMsg(file.getName());
                System.out.println("fileName: " + file.getName());

                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                length = dis.read(sendBuffer, 0, sendBuffer.length);
                while (length > 0) {
                    clientInit.sendInt(length);
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

class ClientInit {
    private ClientConnectServer clientConnectServerMsg = new ClientConnectServer();
    private ClientConnectServerObject clientConnectServerObject = new ClientConnectServerObject();
    private static boolean flag;
    private static boolean isConnected;

    public void close() {
        clientConnectServerMsg.close();
        clientConnectServerObject.close();
    }

    public void connectWithServer() {
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
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }


    public boolean isConnected() {
        return isConnected;
    }

    //发送结果
//    public void sendResult(TaskCombinationResult taskCombinationResult) throws IOException {
//        clientConnectServerObject.getDosWithServer().writeObject(taskCombinationResult);
//    }

    //发送文件
    public void sendInt(int len) throws IOException {
        clientConnectServerMsg.getDosWithServer().writeInt(len);
    }

    //发送文件长度
    public void sendLong(long len) throws IOException {
        clientConnectServerMsg.getDosWithServer().writeLong(len);
        clientConnectServerMsg.getDosWithServer().flush();
    }

    public void sendByte(byte[] bytes, int off, int len) throws IOException {
        clientConnectServerMsg.getDosWithServer().write(bytes, off, len);
        clientConnectServerMsg.getDosWithServer().flush();
    }

    //发送Ready信息
    public void sendMsg(String str) throws IOException {
        clientConnectServerMsg.getDosWithServer().writeUTF(str);
        clientConnectServerMsg.getDosWithServer().flush();
    }

    //接收执行指令
    public String receiveData() throws IOException, ClassNotFoundException {
        return clientConnectServerMsg.getDisWithServer().readUTF();
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
