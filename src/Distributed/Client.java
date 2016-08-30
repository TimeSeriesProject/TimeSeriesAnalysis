package Distributed;

import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPath;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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

//    public static void main(String[] args) {
//        Client client = new Client();
//        client.startConnect();
//        new Thread(client.new ReceiveServerMsg()).start();
//    }

    public Client(DataPanel dataPanel, String IP, int port) {
        this.dataPanel = dataPanel;
        this.IP = IP;
        this.port = port;
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
    public void sendReady() {
        try {
            clientInit.sendMsg("Ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    sendReady();//先发送Ready
                    taskCombination = (TaskCombination) clientInit.receiveData();//收到要完成的任务class
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
                                networkTask.setMinerNodeResults2(entry.getValue());
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
                clientInit.close();
            }
        }
    }
}

//初始化连接
class ClientInit {
    private ClientConnectServer clientConnectServerMsg = new ClientConnectServer();
    private ClientConnectServerObject clientConnectServerObject = new ClientConnectServerObject();
    private static boolean flag;
    private static boolean isConnected;

    public void close(){
        clientConnectServerMsg.close();
        clientConnectServerObject.close();
    }

    public void connectWithServer(DataPanel dataPanel, String IP, int port) {

        flag = true;
        isConnected = false;
        Socket socket1 = null;
        Socket socket2 = null;
        final DataPanel dataPanel1 = dataPanel;

        //先启动客户端，不断尝试连接服务端
        while (flag) {
            try {
                socket1 = new Socket(IP, port);
                socket2 = new Socket(IP, port);
                if (socket1.getPort() == port && String.valueOf(socket1.getInetAddress()).equals("/" + IP)
                        && socket2.getPort() == port && String.valueOf(socket2.getInetAddress()).equals("/" + IP)) {
                    System.out.println("客户端启动...");
                    clientConnectServerMsg.connectServer(socket1);
                    clientConnectServerObject.connectServer2(socket2);
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
    public Object receiveData() throws IOException, ClassNotFoundException {
        return clientConnectServerObject.getDisWithServer().readObject();
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
