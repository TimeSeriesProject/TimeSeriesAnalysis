package cn.InstFS.wkr.NetworkMining.PcapDistributed;

import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zsc on 2016/7/5.
 */
public class PcapServer {
    private ArrayList<String> fileNames = new ArrayList<String>();
    private ArrayList<String> allTasks = new ArrayList<String>();
    private HashMap<String, StringBuilder> tasksMap = new HashMap<String, StringBuilder>();
    private ConcurrentHashMap<String, DataOutputStream> dosMap = new ConcurrentHashMap<String, DataOutputStream>();
    private String DELIMITER = "\r\n";
    private String outPath = "D:\\57data";
    private String fileName;
    private int BUF_LEN = 5 * 1024 * 1024;
    private int count = 0;//发送次数
    private Lock recLock = new ReentrantLock();
    private Condition recCon = recLock.newCondition();


    private Lock countLock = new ReentrantLock();


    public static void main(String[] args) throws FileNotFoundException {
        PcapServer pcapServer = new PcapServer();
        String filePath = "D:\\pcap";
        pcapServer.genTasks(filePath, "pcap");
        for (int i = 0; i < pcapServer.allTasks.size(); i++) {
            System.out.println(pcapServer.allTasks.get(i));
        }
        new Thread(pcapServer.new PcapServerStart()).start();
    }


    private void genTasks(String filePath, String type) {
        getFileList(filePath, type);
        for (String name : fileNames) {
            String key = name.substring(0, name.indexOf("-"));
            if (tasksMap.containsKey(key)) {
                tasksMap.get(key).append(name).append(DELIMITER);
            } else {
                tasksMap.put(key, new StringBuilder(name).append(DELIMITER));
            }
        }
        for (Map.Entry<String, StringBuilder> entry : tasksMap.entrySet()) {
            allTasks.add(entry.getValue().toString());
        }
    }


    private int getFileList(String filePath, String type) {
        int num = 0;
        File ff = new File(filePath);
        if (ff.isFile() && filePath.endsWith(type)) {
            fileNames.add(ff.getName());
            num += 1;
        } else if (ff.isDirectory()) {
            File[] files = ff.listFiles();
            for (File f : files) {
                getFileList(f.getAbsolutePath(), type);
            }
        }
        return num;
    }

    class PcapServerStart implements Runnable {
        private ServerSocket serverSocket = null;
        private UserClient dataClient;
        private UserClientObject resultClient;
        private boolean start = false;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(7777);
                start = true;
            } catch (BindException e) {
                System.out.println("端口使用中...");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (start) {
                    Socket dataSocket = serverSocket.accept();//接收dataoutputstream
                    Socket resultSocket = serverSocket.accept();//接收objectoutputstream
                    dataClient = new UserClient(dataSocket);
                    resultClient = new UserClientObject(resultSocket);
                    ParsePcap parsePcap = new ParsePcap(dataClient);//连接
//                    GenRoute genRoute = new GenRoute(resultClient);//连接
                    System.out.println("一个客户端已连接！");
                    new Thread(parsePcap).start();//启动线程
//                    new Thread(genRoute).start();//启动线程
                }
            } catch (IOException e) {
                System.out.println("服务端错误位置");
                e.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                    start = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ParsePcap implements Runnable {
        private boolean isConnected = false;
        private UserClient userClient;
        private String dataFromClient = "";
        private long totalLen;
        private String finalFolderPath;

        ParsePcap(UserClient userClient) {
            this.userClient = userClient;
            isConnected = true;

        }

        @Override
        public void run() {
            try {
                while (isConnected) {
                    dataFromClient = userClient.receiveMsg();
                    System.out.println("接收到ready");
                    if (dataFromClient.equals("Ready")) {
                        countLock.lock();
                        try {
                            if (count < allTasks.size()) {
                                userClient.sendTask(allTasks.get(count));
                                System.out.println("第" + count + "次已发送" + allTasks.size());
                                count += 1;
                                System.out.println("下一次：" + count);
                            }
                        } finally {
                            countLock.unlock();
                        }
                    }

                    finalFolderPath = outPath;
                    //接收文件
                    receiveResult(finalFolderPath);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                isConnected = false;
                try {
                    userClient.close();
                    for (Map.Entry<String, DataOutputStream> entry : dosMap.entrySet()) {
                        try {
                            entry.getValue().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void receiveResult(String finalFolderPath) throws IOException {
            totalLen = userClient.receiveLong();
            System.out.println("totalLen: " + totalLen);
            long beginTime = System.currentTimeMillis();
            String subFolder;
            while (true) {
                String receiveType = userClient.receiveMsg();
                if (receiveType.equals("sendFile")) {
                    System.out.println("进入sendFile");
                    recLock.lock();
                    try {
                        receiveFile(finalFolderPath);//仅文件
                    } finally {
                        recLock.unlock();
                    }
                } else if (receiveType.equals("sendFolder")) {
                    System.out.println("进入sendFolder");
                    subFolder = userClient.receiveMsg();//发送方的selectFolderPath子目录
                    finalFolderPath = outPath + File.separator + subFolder;
                    //生成子目录
                    File folder = new File(finalFolderPath);
                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
                } else if (receiveType.equals("endTransmit")) {
                    System.out.println("跳出");
                    break;
                }
            }
        }

        private void receiveFile(String outPath) {
            byte[] receiveBuffer = new byte[BUF_LEN];
            int length;
            long passedlen = 0;

            try {
                fileName = userClient.receiveMsg();
                String finalFilePath = outPath + File.separator + fileName;
                System.out.println("finalFilePath: " + finalFilePath);

                DataOutputStream dos;
                if (!dosMap.containsKey(fileName)) {
                    dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFilePath, true)));
                    dosMap.put(fileName, dos);
                }
                dos = dosMap.get(fileName);

                length = userClient.receiveInt();
                while (length > 0) {
                    userClient.receiveFullByte(receiveBuffer, 0, length);//read到length才返回，若用read，可能不到length就返回
                    dos.write(receiveBuffer, 0, length);
                    dos.flush();
                    length = userClient.receiveInt();
                }
                System.out.println("接收方结束循环");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    class GenRoute implements Runnable {
        @Override
        public void run() {

        }
    }

}

class UserClient {
    private Socket socket = null;
    private DataInputStream disWithClient;
    private DataOutputStream dosWithClient;

    public UserClient(Socket socket) {
        this.socket = socket;
        try {
            disWithClient = new DataInputStream(socket.getInputStream());
            dosWithClient = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        try {
            if (disWithClient != null) disWithClient.close();
            if (socket != null) socket.close();
            if (dosWithClient != null) dosWithClient.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public int receiveInt() throws IOException {
        return disWithClient.readInt();
    }

    public long receiveLong() throws IOException {
        return disWithClient.readLong();
    }

    public String receiveMsg() throws IOException {
        return disWithClient.readUTF();
    }

    public int receiveByte(byte[] bytes) throws IOException {
        return disWithClient.read(bytes);
    }

    public void receiveFullByte(byte[] bytes, int off, int len) throws IOException {
        disWithClient.readFully(bytes, off, len);
    }

    public void sendTask(String task) throws IOException {
        dosWithClient.writeUTF(task);
        dosWithClient.flush();
    }
}

class UserClientObject {
    private Socket socket = null;
    private ObjectInputStream disWithClient;
    private ObjectOutputStream dosWithClient;

    public UserClientObject(Socket socket) {
        this.socket = socket;
        try {
            dosWithClient = new ObjectOutputStream(socket.getOutputStream());
            disWithClient = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        try {
            if (disWithClient != null) disWithClient.close();
            if (socket != null) socket.close();
            if (dosWithClient != null) dosWithClient.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void sendObject(TaskCombination task) throws IOException {
        dosWithClient.writeObject(task);
        dosWithClient.flush();
    }

    public Object receiveObject() throws IOException, ClassNotFoundException {
        return disWithClient.readObject();
    }

}

class Tasks {
    private int partition = 0;
    private int part = 0;
}

/*
class TasksThread implements Callable {
    private InputStream is = null;
    private HashSet<String> bws;

    TasksThread(InputStream is, HashSet<String> bws) {
        this.is = is;
        this.bws = bws;
    }

    public Boolean call() {
        try {
            PcapTasks.unpackTasks(is, bws);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
*/

