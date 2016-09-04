package Distributed;

import cn.InstFS.wkr.NetworkMining.Miner.Factory.*;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerNode;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Results.MiningResultsFile;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.UIs.*;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPath;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;


/**
 * Created by zsc on 2016/5/20.
 */
public class Server {
    private static Server server;
    private static ServerStart serverStart;
    private int count = 0;//发送次数
    private boolean singleNodeTimeFlag = true;//判断是否生成result界面
    private boolean singleNodeTrafficFlag = true;//判断是否生成result界面
    private boolean singleNodeDisapearEmergeFlag = true;//判断是否生成result界面
    private boolean nodePairTimeFlag = true;
    private boolean nodePairTrafficFlag = true;
    private boolean pathTimeFlag = true;
    private boolean pathTrafficFlag = true;
    private boolean networkClusterFlag = true;
    private boolean networkDiameterFlag = true;
    private boolean protocolTrafficFlag = true;

    private boolean isRunning = false;//判断是否正在运行，解除挂起状态

    private ConcurrentHashMap<TaskCombination, String> allCombinationTasks = new ConcurrentHashMap<TaskCombination, String>();//带标签，所有不同类型任务
    //用于得到List<TaskCombination>,保存了全部的任务，并不断添加，Factory中allCombinationMiners服务端没有，客户端要clear
    private TaskCombinationList combinationList = new TaskCombinationList();
    private List<TaskCombination> tempList;
    private HashMap<TaskCombination, MinerNodeResults> singleNodeTimes = new HashMap<TaskCombination, MinerNodeResults>();
    private HashMap<TaskCombination, MinerNodeResults> singleNodeTraffic = new HashMap<TaskCombination, MinerNodeResults>();
    private HashMap<TaskCombination, MinerNodeResults> singleNodeDisapearEmerge = new HashMap<TaskCombination, MinerNodeResults>();
    private HashMap<TaskCombination, MinerNodeResults> nodePairTimes = new HashMap<TaskCombination, MinerNodeResults>();
    private HashMap<TaskCombination, MinerNodeResults> nodePairTraffic = new HashMap<TaskCombination, MinerNodeResults>();
    private HashMap<TaskCombination, MinerResultsPath> pathTimes = new HashMap<TaskCombination, MinerResultsPath>();
    private HashMap<TaskCombination, MinerResultsPath> pathTraffic = new HashMap<TaskCombination, MinerResultsPath>();
    private HashMap<TaskCombination, MinerNodeResults> networkCluster = new HashMap<TaskCombination, MinerNodeResults>();
    private HashMap<TaskCombination, MinerNodeResults> networkDiameter = new HashMap<TaskCombination, MinerNodeResults>();
    private HashMap<TaskCombination, MinerProtocolResults> protocolTraffic = new HashMap<TaskCombination, MinerProtocolResults>();


    private HashMap<String, HashMap<TaskCombination, MinerNodeResults>> singleNodeResultMaps = new HashMap<String, HashMap<TaskCombination, MinerNodeResults>>();
    private HashMap<String, HashMap<TaskCombination, MinerNodeResults>> nodePairResultMaps = new HashMap<String, HashMap<TaskCombination, MinerNodeResults>>();
    private HashMap<String, HashMap<TaskCombination, MinerResultsPath>> pathResultMaps = new HashMap<String, HashMap<TaskCombination, MinerResultsPath>>();
    private HashMap<String, HashMap<TaskCombination, MinerNodeResults>> networkResultMaps = new HashMap<String, HashMap<TaskCombination, MinerNodeResults>>();
    private HashMap<TaskCombination, MinerProtocolResults> protocolResultMaps = new HashMap<TaskCombination, MinerProtocolResults>();

    //    private ReadWriteLock mapLock = new ReentrantReadWriteLock();
    private Lock countLock = new ReentrantLock();
    private ReadWriteLock isRunningLock = new ReentrantReadWriteLock();
    private Lock resultLock = new ReentrantLock();
    private Lock cLock = new ReentrantLock();
    private Condition condition = cLock.newCondition();
    private Lock pathLock = new ReentrantLock();
    private Condition pathCon = pathLock.newCondition();
    private Lock nodeLock = new ReentrantLock();
    private Condition nodeCon = nodeLock.newCondition();
    private Lock netLock = new ReentrantLock();
    private Condition netCon = netLock.newCondition();
    private Lock proLock = new ReentrantLock();
    private Condition proCon = proLock.newCondition();

    long beginTime;
    long endTime;

    private Server() {

    }

    public static Server getInstance() {
        if (server != null) {
            return server;
        }
        server = new Server();
        return server;
    }

    public static ServerStart getStartInstance() {
        if (serverStart != null) {
            return serverStart;
        }
        serverStart = Server.getInstance().new ServerStart();
        return serverStart;
    }

    public void SingleNodeOrNodePair(SingleNodeOrNodePairMinerFactoryDis singleNodeOrNodePairMinerFactoryDis, MiningObject miningObject) {
        genSingleNodeTask(singleNodeOrNodePairMinerFactoryDis, miningObject);
        initMap(MinerType.MiningType_SinglenodeOrNodePair);
        awakeThread();
        setIsRunning(true);
        isSingleNodeOver(miningObject);
    }

    public void path(PathMinerFactoryDis pathMinerFactoryDis, MiningObject miningObject) {
        genPathTask(pathMinerFactoryDis, miningObject);
        initMap(MinerType.MiningType_Path);
        awakeThread();
        setIsRunning(true);
        isPathOver(miningObject);
    }

    public void network(NetworkFactoryDis networkFactoryDis, MiningObject miningObject) {
        genNetworkTask(networkFactoryDis, miningObject);
        initMap(MinerType.MiningTypes_WholeNetwork);
        awakeThread();
        setIsRunning(true);
        isNetworkOver(miningObject);
    }

    public void protocol(ProtocolAssMinerFactoryDis protocolAssMinerFactoryDis, MiningObject miningObject) {
        genProtocoTask(protocolAssMinerFactoryDis, miningObject);
        initMap(MinerType.MiningType_ProtocolAssociation);
        awakeThread();
        setIsRunning(true);
        isProtocolOver(miningObject);
    }


    public void isSingleNodeOver(MiningObject miningObject) {
        if (miningObject.equals(MiningObject.MiningObject_Times) ||
                miningObject.equals(MiningObject.MiningObject_Traffic) ||
                miningObject.equals(MiningObject.MiningObject_NodeDisapearEmerge)) {
            awaitNode();
        }
    }

    public void isNetworkOver(MiningObject miningObject) {
        if (miningObject.equals(MiningObject.MiningObject_Cluster) ||
                miningObject.equals(MiningObject.MiningObject_Diameter)) {
            awaitNet();
        }
    }

    public void isProtocolOver(MiningObject miningObject) {
        if (miningObject.equals(MiningObject.MiningObject_Traffic)) {
            awaitProtocol();
        }
    }

    public void isPathOver(MiningObject miningObject) {
        if (miningObject.equals(MiningObject.MiningObject_Times) ||
                miningObject.equals(MiningObject.MiningObject_Traffic)) {
            awaitPath();
        }
    }

    private void awakeThread() {
        cLock.lock();
        try {
            condition.signalAll();
        } finally {
            cLock.unlock();
        }
    }

    private void awakeSingleThread() {
        cLock.lock();
        try {
            condition.signal();
        } finally {
            cLock.unlock();
        }
    }

    private void awaitPath(){
        pathLock.lock();
        try {
            System.out.println("path等待结果完成...");
            pathCon.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pathLock.unlock();
        }
    }

    private void awakePath(){
        pathLock.lock();
        try {
            pathCon.signal();
        } finally {
            pathLock.unlock();
        }
    }

    private void awaitNode(){
        nodeLock.lock();
        try {
            System.out.println("node等待结果完成...");
            nodeCon.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            nodeLock.unlock();
        }
    }

    private void awakeNode(){
        nodeLock.lock();
        try {
            System.out.println("node唤醒...");
            nodeCon.signal();
        } finally {
            nodeLock.unlock();
        }
    }

    private void awaitNet(){
        netLock.lock();
        try {
            System.out.println("net等待结果完成...");
            netCon.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            netLock.unlock();
        }
    }

    private void awakeNet(){
        netLock.lock();
        try {
            netCon.signal();
        } finally {
            netLock.unlock();
        }
    }

    private void awaitProtocol(){
        proLock.lock();
        try {
            System.out.println("protocol等待结果完成...");
            proCon.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            proLock.unlock();
        }
    }

    private void awakeProtocol() {
        proLock.lock();
        try {
            proCon.signal();
        } finally {
            proLock.unlock();
        }
    }

    public void setIsRunning(boolean isRunning) {
        isRunningLock.writeLock().lock();
        try {
            this.isRunning = isRunning;
        } finally {
            isRunningLock.writeLock().unlock();
        }
    }

    private void genSingleNodeTask(SingleNodeOrNodePairMinerFactoryDis singleNodeOrNodePairMinerFactoryDis, MiningObject miningObject) {
        singleNodeOrNodePairMinerFactoryDis.reset();
        singleNodeOrNodePairMinerFactoryDis.setMiningObject(miningObject);
        singleNodeOrNodePairMinerFactoryDis.detect();
    }

    private void genNetworkTask(NetworkFactoryDis networkFactoryDis, MiningObject miningObject) {
        networkFactoryDis.reset();
        networkFactoryDis.setMiningObject(miningObject);
        networkFactoryDis.detect();
    }

    private void genProtocoTask(ProtocolAssMinerFactoryDis protocolAssMinerFactoryDis, MiningObject miningObject) {
        protocolAssMinerFactoryDis.reset();
        protocolAssMinerFactoryDis.setMiningObject(miningObject);
        protocolAssMinerFactoryDis.detect();
    }

    private void genPathTask(PathMinerFactoryDis pathMinerFactoryDis, MiningObject miningObject) {
        pathMinerFactoryDis.reset();
        pathMinerFactoryDis.setMiningObject(miningObject);
        pathMinerFactoryDis.detect();
    }

    //将所有TaskCombination放到map中
    private void initMap(MinerType minerType) {
        allCombinationTasks.clear();//先清空,每次任务只发送要执行的任务
        System.out.println("CombinationList 长度: " + combinationList.getTaskCombinationList().size());
        tempList = new ArrayList<TaskCombination>();
        for (int i = 0; i < combinationList.getTaskCombinationList().size(); i++) {
            if (combinationList.getTaskCombinationList().get(i).getMinerType().equals(minerType)) {
                tempList.add(combinationList.getTaskCombinationList().get(i));
                allCombinationTasks.put(combinationList.getTaskCombinationList().get(i), "n");
            }
        }
        count = 0;
        TaskCombinationList.clearTaskCombinationList();
        /*for (Map.Entry<TaskCombination, String> entry : allCombinationTasks.entrySet()) {
            System.out.println("初始化后 TaskCombination= " + entry.getKey().getName().hashCode() +
                    " and String = " + entry.getValue());
        }*/
    }

    public void showPathResult() {
        System.out.println("显示path结果....");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                PathListFrame frame = new PathListFrame(pathResultMaps);
                frame.setVisible(true);
            }
        });
    }

    public void showNetworkResult() {
        System.out.println("显示network结果....");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                WholeNetworkFrame frame = new WholeNetworkFrame(networkResultMaps);
                frame.setVisible(true);
            }
        });
    }

    public void showSingleNodeResult() {
        System.out.println("显示singleNode结果....");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                SingleNodeListFrame frame = new SingleNodeListFrame(singleNodeResultMaps);
                frame.setVisible(true);
            }
        });
    }

    public void showNodePairResult() {
        System.out.println("显示NodePair结果....");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                NodePairListFrame frame = new NodePairListFrame(nodePairResultMaps);
                frame.setVisible(true);
            }
        });
    }

    public void showProtocolResult() {
        System.out.println("显示Protocol结果....");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                AssociationIpListFrame frame = new AssociationIpListFrame(protocolResultMaps);
                frame.setVisible(true);
            }
        });
    }

    //启动服务端
    class ServerStart implements Runnable {
        private ServerSocket serverSocket = null;
        private UserClient dataClient;
        private UserClientObject resultClient;
        private boolean start = false;

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
                    ReceiveMsg receiveMsg = new ReceiveMsg(dataClient, resultClient);//连接
                    ReceiveResult receiveResult = new ReceiveResult(resultClient);//连接
                    System.out.println("一个客户端已连接！");
                    isRunningLock.readLock().lock();
                    try {
                        if (isRunning) {
                            receiveMsg.setSuspend(false);
                        }
                    } finally {
                        isRunningLock.readLock().unlock();
                    }
                    new Thread(receiveMsg).start();//启动线程
                    new Thread(receiveResult).start();//启动线程
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

    //接收dataoutputstream的封装
    class ReceiveMsg implements Runnable {
        private boolean isConnected = false;
        private boolean isSuspend = true;
        private UserClient userClient;
        private UserClientObject userClientObject;
        private String dataFromClient = "";

        ReceiveMsg(UserClient userClient, UserClientObject userClientObject) {
            this.userClient = userClient;
            this.userClientObject = userClientObject;
            isConnected = true;
        }

        public void setSuspend(boolean suspend) {
            isSuspend = suspend;
        }

        public void run() {
            try {
                beginTime = System.currentTimeMillis();
                while (isConnected) {
                    if (isSuspend) {
                        cLock.lock();
                        try {
                            System.out.println("服务端接收Ready线程已挂起");
                            condition.await();
                            System.out.println("服务端接收Ready唤醒");
                            isSuspend = false;//跳过挂起
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            cLock.unlock();
                        }
                    }


                    //判断是否有已存储的结果
                    switch (tempList.get(0).getMinerType()) {

                        case MiningType_SinglenodeOrNodePair:
                            if (tempList.get(0).getTaskRange().equals(TaskRange.SingleNodeRange)) {
                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
                                    int count = 0;
                                    for (int i = 0; i < tempList.size(); i++) {
                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                            singleNodeTimes.put(tempList.get(i), resultNode);
                                            NetworkMinerFactory.getInstance().
                                                    showNodeMinersDis(MiningObject.MiningObject_Times, singleNodeTimes);
                                            count += 1;
                                        }
                                    }
                                    if (count == tempList.size()) {
                                        singleNodeResultMaps.put(MiningObject.MiningObject_Times.toString(), singleNodeTimes);//存结果
                                        awakeNode();
                                        isSuspend = true;
                                        continue;
                                    }
                                }

                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                                    int count = 0;
                                    for (int i = 0; i < tempList.size(); i++) {
                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                            singleNodeTraffic.put(tempList.get(i), resultNode);
                                            NetworkMinerFactory.getInstance().
                                                    showNodeMinersDis(MiningObject.MiningObject_Traffic, singleNodeTraffic);
                                            count += 1;
                                        }
                                    }
                                    if (count == tempList.size()) {
                                        singleNodeResultMaps.put(MiningObject.MiningObject_Traffic.toString(), singleNodeTraffic);//存结果
                                        awakeNode();
                                        isSuspend = true;
                                        continue;
                                    }
                                }

                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge.toString())) {
                                    int count = 0;
                                    for (int i = 0; i < tempList.size(); i++) {
                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                            singleNodeDisapearEmerge.put(tempList.get(i), resultNode);
                                            NetworkMinerFactory.getInstance().
                                                    showNodeMinersDis(MiningObject.MiningObject_NodeDisapearEmerge, singleNodeDisapearEmerge);
                                            count += 1;
                                        }
                                    }
                                    if (count == tempList.size()) {
                                        singleNodeResultMaps.put(MiningObject.MiningObject_NodeDisapearEmerge.toString(), singleNodeDisapearEmerge);//存结果
                                        awakeNode();
                                        isSuspend = true;
                                        continue;
                                    }
                                }
                            }

                            //nodePair
                            if (tempList.get(0).getTaskRange().equals(TaskRange.NodePairRange)) {
                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
                                    int count = 0;
                                    for (int i = 0; i < tempList.size(); i++) {
                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                            nodePairTimes.put(tempList.get(i), resultNode);
                                            NetworkMinerFactory.getInstance().
                                                    showNodeMinersDis(MiningObject.MiningObject_Times, nodePairTimes);
                                            count += 1;
                                        }
                                    }
                                    if (count == tempList.size()) {
                                        nodePairResultMaps.put(MiningObject.MiningObject_Times.toString(), nodePairTimes);//存结果
                                        awakeNode();
                                        isSuspend = true;
                                        continue;
                                    }
                                }

                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                                    int count = 0;
                                    for (int i = 0; i < tempList.size(); i++) {
                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                            nodePairTraffic.put(tempList.get(i), resultNode);
                                            NetworkMinerFactory.getInstance().
                                                    showNodeMinersDis(MiningObject.MiningObject_Traffic, nodePairTraffic);
                                            count += 1;
                                        }
                                    }
                                    if (count == tempList.size()) {
                                        nodePairResultMaps.put(MiningObject.MiningObject_Traffic.toString(), nodePairTraffic);//存结果
                                        awakeNode();
                                        isSuspend = true;
                                        continue;
                                    }
                                }
                            }
                            break;

                        case MiningType_ProtocolAssociation:
                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                                int count = 0;
                                for (int i = 0; i < tempList.size(); i++) {
                                    MinerFactorySettings settings = ProtocolAssMinerFactory.getInstance();
                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                    if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                        MinerProtocolResults resultNode = (MinerProtocolResults) resultsFile.file2Result();
                                        protocolTraffic.put(tempList.get(i), resultNode);
                                        NetworkMinerFactory.getInstance().
                                                showProtocolMinersDis(MiningObject.MiningObject_Traffic, protocolTraffic);
                                        count += 1;
                                    }
                                }
                                if (count == tempList.size()) {
                                    System.out.println("protocol存在结果");
                                    protocolResultMaps = protocolTraffic;//存结果
                                    awakeProtocol();
                                    isSuspend = true;
                                    continue;
                                }
                            }

                        break;

                        case MiningType_Path:
                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
                                int count = 0;
                                for (int i = 0; i < tempList.size(); i++) {
                                    MinerFactorySettings settings = PathMinerFactory.getInstance();
                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                        MinerResultsPath resultPath = (MinerResultsPath) resultsFile.file2Result();
                                        pathTimes.put(tempList.get(i), resultPath);
                                        NetworkMinerFactory.getInstance().
                                                showPathMinersDis(MiningObject.MiningObject_Times, pathTimes);
                                        count += 1;
                                    }
                                }
                                if (count == tempList.size()) {
                                    pathResultMaps.put(MiningObject.MiningObject_Times.toString(), pathTimes);//存结果
                                    awakePath();
                                    isSuspend = true;
                                    continue;
                                }
                            }

                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                                int count = 0;
                                for (int i = 0; i < tempList.size(); i++) {
                                    MinerFactorySettings settings = PathMinerFactory.getInstance();
                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                        MinerResultsPath resultPath = (MinerResultsPath) resultsFile.file2Result();
                                        pathTraffic.put(tempList.get(i), resultPath);
                                        NetworkMinerFactory.getInstance().
                                                showPathMinersDis(MiningObject.MiningObject_Traffic, pathTraffic);
                                        count += 1;
                                    }
                                }
                                if (count == tempList.size()) {
                                    pathResultMaps.put(MiningObject.MiningObject_Traffic.toString(), pathTraffic);//存结果
                                    awakePath();
                                    isSuspend = true;
                                    continue;
                                }
                            }
                            break;

                        case MiningTypes_WholeNetwork:
                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Cluster.toString())) {
                                int count = 0;
                                for (int i = 0; i < tempList.size(); i++) {
                                    MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                        MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                        networkCluster.put(tempList.get(i), resultNode);
                                        NetworkMinerFactory.getInstance().
                                                showNetWorkMinersDis(MiningObject.MiningObject_Cluster, networkCluster);
                                        count += 1;
                                    }
                                }
                                if (count == tempList.size()) {
                                    networkResultMaps.put(MiningObject.MiningObject_Cluster.toString(), networkCluster);//存结果
                                    awakeNet();
                                    isSuspend = true;
                                    continue;
                                }
                            }

                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Diameter.toString())) {
                                int count = 0;
                                for (int i = 0; i < tempList.size(); i++) {
                                    MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                        MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                        networkDiameter.put(tempList.get(i), resultNode);
                                        NetworkMinerFactory.getInstance().
                                                showNetWorkMinersDis(MiningObject.MiningObject_Diameter, networkDiameter);
                                        count += 1;
                                    }
                                }
                                if (count == tempList.size()) {
                                    networkResultMaps.put(MiningObject.MiningObject_Diameter.toString(), networkDiameter);//存结果
                                    awakeNet();
                                    isSuspend = true;
                                    continue;
                                }
                            }
                            break;

                    }


                    dataFromClient = userClient.receiveReady();//接收Ready,@Deprecated在完成后的第二次循环阻塞
                    if (dataFromClient.equals("Ready")) {
                        /*for(Map.Entry<TaskCombination, String> entry : allCombinationTasks.entrySet()){
                            userClientObject.sendObject(entry.getKey());
                        }*/
                        countLock.lock();
                        try {
                            if (count < tempList.size()) {
                                isSuspend = true;

                                long a = System.currentTimeMillis();
                                System.out.println("第" + count + "次发送" + tempList.size());
                                userClientObject.sendObject(tempList.get(count));
                                long b = System.currentTimeMillis();
                                System.out.println("发送时间：" + (b - a));
                                count += 1;
                                System.out.println("第" + count + "次即将开始");
                            }
                        } finally {
                            countLock.unlock();
                        }

                        countLock.lock();
                        try {
                            if (count == tempList.size()) {
                                int temp = 0;//中途最后一个结果发回来，强行再发一次最后一个任务，防止客户端没有ready卡死
                                //线程加锁，防止其他线程调用Map
                                isSuspend = true;
                                //                            mapLock.readLock().lock();
                                //                            try {
                                long a = System.currentTimeMillis();
                                //找到没有完成的任务

                                for (Map.Entry<TaskCombination, String> entry : allCombinationTasks.entrySet()) {
                                    temp += 1;
                                    System.out.println("当前TaskCombination= " + entry.getKey().getName() +
                                            " and String = " + entry.getValue());
                                    if (entry.getValue().equals("n")) {
                                        userClientObject.sendObject(entry.getKey());
//                                        isSuspend = false;
                                        System.out.println("第二次发送的task：" + entry.getKey().getName());
                                        break;
                                    }
                                    if (temp == allCombinationTasks.size()) {
                                        userClientObject.sendObject(entry.getKey());
                                    }
                                }

                                long b = System.currentTimeMillis();
                                System.out.println("第二次发送时间：" + (b - a));
                                //                            } finally {
                                //                                mapLock.readLock().unlock();
                                //                            }
                            }
                        } finally {
                            countLock.unlock();
                        }
                    }
                    System.out.println("执行完毕");
                }
            } catch (IOException e) {
                System.out.println("接收ready关闭");
                e.printStackTrace();
            } finally {
                isConnected = false;
                try {
                    userClient.close();
                    userClientObject.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("再唤起一次...");
                awakeSingleThread();
            }
        }
    }

    //接收objectoutputstream的封装
    class ReceiveResult implements Runnable {
        private boolean isConnected = false;
        private UserClientObject userClientObject;
        private TaskCombinationResult taskCombinationResult;

        ReceiveResult(UserClientObject userClientObject) {
            this.userClientObject = userClientObject;
            isConnected = true;
        }

        public void updateMap(TaskCombination task) {
            //map加锁,改为concurrent
//            mapLock.writeLock().lock();
//            try {
            if (allCombinationTasks.get(task).equals("n")) {
                allCombinationTasks.remove(task);
                allCombinationTasks.put(task, "y");//更新标记，表示完成
            }

               /* for (Map.Entry<TaskCombination, String> entry : allCombinationTasks.entrySet()) {
                    System.out.println("key= " + entry.getKey().getName()
                            + " and value= " + entry.getValue());
                }*/
//            } finally {
//                mapLock.writeLock().unlock();
//            }
        }

        @Override
        public void run() {
            NetworkMinerFactory.getInstance();
            //接收任务完成后得到的结果
            try {
                while (isConnected) {
                    taskCombinationResult = (TaskCombinationResult) userClientObject.receiveObject();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (taskCombinationResult.getMinerType() != null) {
                                switch (taskCombinationResult.getMinerType()) {

                                    case MiningType_SinglenodeOrNodePair:
                                        //单节点
                                        if (taskCombinationResult.getTaskRange().equals(TaskRange.SingleNodeRange)) {
                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)
                                                    && singleNodeTimeFlag) {
                                                if (singleNodeTimes.size() < allCombinationTasks.size()) {
                                                    singleNodeTimeFlag = true;
                                                    for (int i = 0; i < tempList.size(); i++) {
                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                            resultLock.lock();
                                                            try {
                                                                singleNodeTimes.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                            } finally {
                                                                resultLock.unlock();
                                                            }
                                                            updateMap(tempList.get(i));//更新状态
                                                        }
                                                    }
                                                    NetworkMinerFactory.getInstance().
                                                            showNodeMinersDis(MiningObject.MiningObject_Times, singleNodeTimes);
                                                    if (singleNodeTimes.size() == allCombinationTasks.size() && singleNodeTimeFlag) {
                                                        singleNodeResultMaps.put(MiningObject.MiningObject_Times.toString(), singleNodeTimes);
                                                        singleNodeTimeFlag = false;//完成，后面未执行完的抛弃
                                                        setIsRunning(false);
                                                        awakeNode();
                                                    } else {
                                                        awakeSingleThread();
                                                    }
                                                }
                                            }

                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
                                                    && singleNodeTrafficFlag) {
                                                if (singleNodeTraffic.size() < allCombinationTasks.size()) {
                                                    singleNodeTrafficFlag = true;
                                                    for (int i = 0; i < tempList.size(); i++) {
                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                            resultLock.lock();
                                                            try {
                                                                singleNodeTraffic.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                            } finally {
                                                                resultLock.unlock();
                                                            }
                                                            updateMap(tempList.get(i));//更新状态
                                                        }
                                                    }
                                                    NetworkMinerFactory.getInstance().
                                                            showNodeMinersDis(MiningObject.MiningObject_Traffic, singleNodeTraffic);
                                                    if (singleNodeTraffic.size() == allCombinationTasks.size() && singleNodeTrafficFlag) {
                                                        singleNodeResultMaps.put(MiningObject.MiningObject_Traffic.toString(), singleNodeTraffic);
                                                        singleNodeTrafficFlag = false;
                                                        setIsRunning(false);
                                                        awakeNode();
                                                    } else {
                                                        awakeSingleThread();
                                                    }
                                                }
                                            }

                                            //节点出现消失
                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge)
                                                    && singleNodeDisapearEmergeFlag) {
                                                if (singleNodeDisapearEmerge.size() < allCombinationTasks.size()) {
                                                    singleNodeDisapearEmergeFlag = true;
                                                    for (int i = 0; i < tempList.size(); i++) {
                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                            resultLock.lock();
                                                            try {
                                                                singleNodeDisapearEmerge.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                            } finally {
                                                                resultLock.unlock();
                                                            }
                                                            updateMap(tempList.get(i));//更新状态
                                                        }
                                                    }
                                                    NetworkMinerFactory.getInstance().
                                                            showNodeMinersDis(MiningObject.MiningObject_NodeDisapearEmerge, singleNodeDisapearEmerge);
                                                    if (singleNodeDisapearEmerge.size() == allCombinationTasks.size() && singleNodeDisapearEmergeFlag) {
                                                        singleNodeResultMaps.put(MiningObject.MiningObject_NodeDisapearEmerge.toString(), singleNodeDisapearEmerge);
                                                        singleNodeDisapearEmergeFlag = false;
                                                        setIsRunning(false);
                                                        awakeNode();
                                                    } else {
                                                        awakeSingleThread();
                                                    }
                                                }
                                            }
                                        }

                                        //节点对
                                        if (taskCombinationResult.getTaskRange().equals(TaskRange.NodePairRange)){
                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)
                                                    && nodePairTimeFlag) {
                                                if (nodePairTimes.size() < allCombinationTasks.size()) {
                                                    nodePairTimeFlag = true;
                                                    for (int i = 0; i < tempList.size(); i++) {
                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                            resultLock.lock();
                                                            try {
                                                                nodePairTimes.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                            } finally {
                                                                resultLock.unlock();
                                                            }
                                                            updateMap(tempList.get(i));//更新状态
                                                        }
                                                    }
                                                    NetworkMinerFactory.getInstance().
                                                            showNodeMinersDis(MiningObject.MiningObject_Times, nodePairTimes);
                                                    if (nodePairTimes.size() == allCombinationTasks.size() && nodePairTimeFlag) {
                                                        nodePairResultMaps.put(MiningObject.MiningObject_Times.toString(), nodePairTimes);
                                                        nodePairTimeFlag = false;//完成，后面未执行完的抛弃
                                                        setIsRunning(false);
                                                        awakeNode();
                                                    } else {
                                                        awakeSingleThread();
                                                    }
                                                }
                                            }

                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
                                                    && nodePairTrafficFlag) {
                                                if (singleNodeTraffic.size() < allCombinationTasks.size()) {
                                                    nodePairTrafficFlag = true;
                                                    for (int i = 0; i < tempList.size(); i++) {
                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                            resultLock.lock();
                                                            try {
                                                                nodePairTraffic.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
                                                            } finally {
                                                                resultLock.unlock();
                                                            }
                                                            updateMap(tempList.get(i));//更新状态
                                                        }
                                                    }
                                                    NetworkMinerFactory.getInstance().
                                                            showNodeMinersDis(MiningObject.MiningObject_Traffic, nodePairTraffic);
                                                    if (nodePairTraffic.size() == allCombinationTasks.size() && nodePairTrafficFlag) {
                                                        nodePairResultMaps.put(MiningObject.MiningObject_Traffic.toString(), nodePairTraffic);
                                                        nodePairTrafficFlag = false;
                                                        setIsRunning(false);
                                                        awakeNode();
                                                    } else {
                                                        awakeSingleThread();
                                                    }
                                                }
                                            }
                                        }
                                        break;

                                    case MiningType_ProtocolAssociation:
                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
                                                && protocolTrafficFlag) {
                                            if (protocolTraffic.size() < allCombinationTasks.size()) {
                                                protocolTrafficFlag = true;
                                                for (int i = 0; i < tempList.size(); i++) {
                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                        resultLock.lock();
                                                        try {
                                                            protocolTraffic.put(tempList.get(i), taskCombinationResult.getMinerProtocolResults());
                                                            /* 挖掘完成，保存结果文件 */
                                                            MinerFactorySettings settings = ProtocolAssMinerFactory.getInstance();
                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                            newResultsFile.result2File(settings, tempList.get(i),  taskCombinationResult.getMinerProtocolResults());
                                                        } finally {
                                                            resultLock.unlock();
                                                        }
                                                        updateMap(tempList.get(i));//更新状态
                                                    }
                                                }
                                                NetworkMinerFactory.getInstance().
                                                        showProtocolMinersDis(MiningObject.MiningObject_Traffic, protocolTraffic);
                                                if (protocolTraffic.size() == allCombinationTasks.size() && protocolTrafficFlag) {
                                                    protocolResultMaps = protocolTraffic;
                                                    protocolTrafficFlag = false;
                                                    setIsRunning(false);
                                                    awakeProtocol();
                                                } else {
                                                    awakeSingleThread();
                                                }
                                            }
                                        }
                                        break;

                                    case MiningType_Path:
                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)
                                                && pathTimeFlag) {
                                            if (pathTimes.size() < allCombinationTasks.size()) {
                                                pathTimeFlag = true;
                                                for (int i = 0; i < tempList.size(); i++) {
                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                        resultLock.lock();
                                                        try {
                                                            pathTimes.put(tempList.get(i), taskCombinationResult.getMinerResultsPath());
                                                            /* 挖掘完成，保存结果文件 */
                                                            MinerFactorySettings settings = PathMinerFactoryDis.getInstance();
                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerResultsPath());
                                                        } finally {
                                                            resultLock.unlock();
                                                        }
                                                        updateMap(tempList.get(i));//更新状态
                                                    }
                                                }
                                                NetworkMinerFactory.getInstance().
                                                        showPathMinersDis(MiningObject.MiningObject_Times, pathTimes);
                                                if (pathTimes.size() == allCombinationTasks.size() && pathTimeFlag) {
                                                    pathResultMaps.put(MiningObject.MiningObject_Times.toString(), pathTimes);//通信次数结果
                                                    pathTimeFlag = false;//完成，后面未执行完的抛弃
                                                    setIsRunning(false);
                                                    awakePath();
                                                } else {
                                                    awakeSingleThread();
                                                }
                                            }
                                        }

                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
                                                && pathTrafficFlag) {
                                            if (pathTraffic.size() < allCombinationTasks.size()) {
                                                pathTrafficFlag = true;
                                                for (int i = 0; i < tempList.size(); i++) {
                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                        resultLock.lock();
                                                        try {
                                                            pathTraffic.put(tempList.get(i), taskCombinationResult.getMinerResultsPath());
                                                            /* 挖掘完成，保存结果文件 */
                                                            MinerFactorySettings settings = PathMinerFactoryDis.getInstance();
                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerResultsPath());
                                                        } finally {
                                                            resultLock.unlock();
                                                        }
                                                        updateMap(tempList.get(i));//更新状态
                                                    }
                                                }
                                                NetworkMinerFactory.getInstance().
                                                        showPathMinersDis(MiningObject.MiningObject_Traffic, pathTraffic);
                                                if (pathTraffic.size() == allCombinationTasks.size() && pathTrafficFlag) {
                                                    pathResultMaps.put(MiningObject.MiningObject_Traffic.toString(), pathTraffic);
                                                    pathTrafficFlag = false;
                                                    setIsRunning(false);
                                                    awakePath();
                                                } else {
                                                    awakeSingleThread();
                                                }
                                            }
                                        }
                                        System.out.println("跳出switch");
                                        break;

                                    case MiningTypes_WholeNetwork:

                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Cluster)
                                                && networkClusterFlag) {
                                            if (networkCluster.size() < allCombinationTasks.size()) {
                                                networkClusterFlag = true;
                                                for (int i = 0; i < tempList.size(); i++) {
                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                        resultLock.lock();
                                                        try {
                                                            networkCluster.put(tempList.get(i), taskCombinationResult.getMinerNetworkResults());
                                                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNetworkResults());
                                                        } finally {
                                                            resultLock.unlock();
                                                        }
                                                        updateMap(tempList.get(i));//更新状态
                                                    }
                                                }
                                                NetworkMinerFactory.getInstance().
                                                        showNetWorkMinersDis(MiningObject.MiningObject_Cluster, networkCluster);
                                                if (networkCluster.size() == allCombinationTasks.size() && networkClusterFlag) {
                                                    networkResultMaps.put(MiningObject.MiningObject_Cluster.toString(), networkCluster);//通信次数结果
                                                    networkClusterFlag = false;//完成，后面未执行完的抛弃
                                                    setIsRunning(false);
                                                    awakeNet();
                                                } else {
                                                    awakeSingleThread();
                                                }
                                            }
                                        }

                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Diameter)
                                                && networkDiameterFlag) {
                                            if (networkDiameter.size() < allCombinationTasks.size()) {
                                                networkDiameterFlag = true;
                                                for (int i = 0; i < tempList.size(); i++) {
                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
                                                        resultLock.lock();
                                                        try {
                                                            networkDiameter.put(tempList.get(i), taskCombinationResult.getMinerNetworkResults());
                                                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNetworkResults());
                                                        } finally {
                                                            resultLock.unlock();
                                                        }
                                                        updateMap(tempList.get(i));//更新状态
                                                    }
                                                }
                                                NetworkMinerFactory.getInstance().
                                                        showNetWorkMinersDis(MiningObject.MiningObject_Diameter, networkDiameter);
                                                if (networkDiameter.size() == allCombinationTasks.size() && networkDiameterFlag) {
                                                    networkResultMaps.put(MiningObject.MiningObject_Diameter.toString(), networkDiameter);
                                                    networkDiameterFlag = false;
                                                    setIsRunning(false);
                                                    awakeNet();
                                                } else {
                                                    awakeSingleThread();
                                                }
                                            }
                                        }
                                        System.out.println("跳出nnnnnswitch");

                                        break;

                                    default:
                                        break;
                                }
                            } else {
                                System.out.println("返回为空...");
                                isRunningLock.readLock().lock();
                                try {
                                    if (isRunning) {
                                        System.out.println("还在执行，唤起...");
                                        awakeSingleThread();
                                    }
                                } finally {
                                    isRunningLock.readLock().unlock();
                                }
                            }
                        }
                    };
                    new Thread(runnable).start();
                }
            } catch (IOException e) {
                System.out.println("服务端关闭IO");
//                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                isConnected = false;
                try {
                    userClientObject.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
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


    public String receiveReady() throws IOException {
        return disWithClient.readUTF();
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

