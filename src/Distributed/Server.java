package Distributed;

import cn.InstFS.wkr.NetworkMining.Miner.Factory.*;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerNode;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt.ParseByDay;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.TaskProgressDis;
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
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;


/**
 * Created by zsc on 2016/5/20.
 */
public class Server {
    private static ServerStart serverStart;
    private int taskCount = 0;//发送次数
    private int totalCount = 0;
    private String port = "";
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
    private boolean isPcapRunning = false;//判断是否正在运行，解除挂起状态

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

    //PcapServer
    private PcapPanel pcapPanel;
    private TaskPanel taskPanel;//分布式任务进度条
    private TaskProgressDis taskProgressDis = TaskProgressDis.getInstance();
    private ArrayList<String> allTasks = new ArrayList<String>();
    private ArrayList<String> allTasks2 = new ArrayList<String>();//第二步任务
    private ConcurrentHashMap<String, String> allTasksTags = new ConcurrentHashMap<String, String>();//带标签，所有不同类型任务
    private ConcurrentHashMap<String, String> allTasksTags2 = new ConcurrentHashMap<String, String>();//带标签，所有不同类型任务2
    private ConcurrentHashMap<String, String> nameMap = new ConcurrentHashMap<String, String>();//文件part
    private ConcurrentHashMap<String, String> nameMap2 = new ConcurrentHashMap<String, String>();//文件part
    private ConcurrentHashMap<String, String> combineFile = new ConcurrentHashMap<String, String>();//合并文件,key=最后合并生成的文件，valude=待合并的小文件的文件夹路径
    private ConcurrentHashMap<String, String> combineFile2 = new ConcurrentHashMap<String, String>();//合并文件,key=最后合并生成的文件，valude=待合并的小文件的文件夹路径
    private HashSet<String> delFile = new HashSet<String>();//删除文件
    private HashSet<String> delFile2 = new HashSet<String>();//删除文件
    private HashMap<String, StringBuilder> tasksMap = new HashMap<String, StringBuilder>();
    private HashMap<String, String> swapMap = new HashMap<String, String>();//文件名\r\n,路由号
    private HashMap<String, String> swapMap2 = new HashMap<String, String>();//routesrc/1.1_1.2, 1.1_1.2

    private String DELIMITER = "\r\n";
    private String inPath;
    private String outPath = "D:\\57data";
    //    private String fileName;
    private int index;
    private int BUF_LEN = 5 * 1024 * 1024;
    private int pcapCount1 = 0;//发送次数
    private int pcapCount2 = 0;//发送次数
    private int recCount = 0;
    private int recCount2 = 0;
    private int tasksCount = 0;
    private String date;
    private ParseByDay parseByDay;

    private Lock recLock = new ReentrantLock();//接收结果
    private Lock sendLock = new ReentrantLock();
    private Lock recLock2 = new ReentrantLock();//接收结果和第一步要分开，否则出bug
    private Lock sendLock2 = new ReentrantLock();

    private Lock pcapLock = new ReentrantLock();
    private Condition pcapCon = pcapLock.newCondition();

    private ReadWriteLock isPcapRunningLock = new ReentrantReadWriteLock();

    private boolean combineAndDelete = false;//判断是否完成合并删除
    private boolean combineAndDelete2 = false;//判断是否完成合并删除
    private Lock comAndDel = new ReentrantLock();//合并删除操作加锁

    private static class Holder {
        private static Server server = new Server();
    }

    private Server() {

    }


    public static void closeServer() {
        if (serverStart != null) {
            serverStart.stop();
        }
    }

    public static Server getInstance() {
        return Holder.server;
    }

    public static ServerStart getStartInstance() {
        if (serverStart != null) {
            return serverStart;
        }
        serverStart = Server.getInstance().new ServerStart();
        return serverStart;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void clearSingleResult() {
        System.out.println("清空结果");
        singleNodeTimes.clear();
        singleNodeTraffic.clear();
        singleNodeDisapearEmerge.clear();
        singleNodeResultMaps.clear();
    }
    public void clearPairResult() {
        System.out.println("清空结果");
        nodePairTimes.clear();
        nodePairTraffic.clear();
        nodePairResultMaps.clear();

    }
    public void clearPathResult() {
        System.out.println("清空结果");
        pathTimes.clear();
        pathTraffic.clear();
        pathResultMaps.clear();

    }
    public void clearNetResult() {
        System.out.println("清空结果");
        networkCluster.clear();
        networkDiameter.clear();
        networkResultMaps.clear();
    }
    public void clearProResult() {
        System.out.println("清空结果");
        protocolTraffic.clear();
        protocolResultMaps.clear();
    }

    //判断是否存在以保存的结果
    public boolean isExistedResult() {
        System.out.println("进入isexisted");
        boolean isExisted = false;
        //判断是否有已存储的结果
        switch (tempList.get(0).getMinerType()) {

            case MiningType_SinglenodeOrNodePair:
                if (tempList.get(0).getTaskRange().equals(TaskRange.SingleNodeRange)) {
                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("singlenode1存在结果");
                            isExisted = true;
                        }
                    }

                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("singlenode2存在结果");
                            isExisted = true;
                        }
                    }

                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("singlenode3存在结果");
                            isExisted = true;
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
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            isExisted = true;
                        }
                    }

                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("nodepair存在结果");
                            isExisted = true;
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
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        System.out.println("protocol存在结果");
                        isExisted = true;
                    }
                }

                break;

            case MiningType_Path:
                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = PathMinerFactory.getInstance();
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        isExisted = true;
                    }
                }

                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = PathMinerFactory.getInstance();
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        isExisted = true;
                    }
                }
                break;

            case MiningTypes_WholeNetwork:
                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Cluster.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        isExisted = true;
                    }
                }

                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Diameter.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        isExisted = true;
                    }
                }
                break;
        }
        return isExisted;
    }

    public boolean getExistedResult() {
        System.out.println("进入existed");
        boolean isExisted = false;
        //判断是否有已存储的结果
        switch (tempList.get(0).getMinerType()) {

            case MiningType_SinglenodeOrNodePair:
                if (tempList.get(0).getTaskRange().equals(TaskRange.SingleNodeRange)) {
                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                singleNodeTimes.put(tempList.get(i), resultNode);
                                //进度条
                                taskPanel.getBar().setValue(singleNodeTimes.size());
                                taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                        String.format("%d%%", 100 * singleNodeTimes.size() / totalCount));
                                taskPanel.getLog().append(String.format(
                                        "Completed %d/%d of task.\n", singleNodeTimes.size(), totalCount));
                                taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                NetworkMinerFactory.getInstance().
                                        showNodeMinersDis(MiningObject.MiningObject_Times, singleNodeTimes);
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("singlenode1存在结果");
                            singleNodeResultMaps.put(MiningObject.MiningObject_Times.toString(), singleNodeTimes);//存结果
                            isExisted = true;
                            awakeNode();
                        }
                    }

                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                singleNodeTraffic.put(tempList.get(i), resultNode);
                                //进度条
                                taskPanel.getBar().setValue(singleNodeTimes.size() + singleNodeTraffic.size());
                                taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                        String.format("%d%%", 100 * (singleNodeTimes.size() + singleNodeTraffic.size()) / totalCount));
                                taskPanel.getLog().append(String.format(
                                        "Completed %d/%d of task.\n", (singleNodeTimes.size() + singleNodeTraffic.size()), totalCount));
                                taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());
                                NetworkMinerFactory.getInstance().
                                        showNodeMinersDis(MiningObject.MiningObject_Traffic, singleNodeTraffic);
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("singlenode2存在结果");
                            singleNodeResultMaps.put(MiningObject.MiningObject_Traffic.toString(), singleNodeTraffic);//存结果
                            isExisted = true;
                            awakeNode();
                        }
                    }

                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                singleNodeDisapearEmerge.put(tempList.get(i), resultNode);
                                taskPanel.getBar().setValue(singleNodeTimes.size() + singleNodeTraffic.size() + singleNodeDisapearEmerge.size());
                                taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                        String.format("%d%%", 100 * (singleNodeTimes.size() + singleNodeTraffic.size() + singleNodeDisapearEmerge.size()) / totalCount));
                                taskPanel.getLog().append(String.format(
                                        "Completed %d/%d of task.\n", (singleNodeTimes.size() + singleNodeTraffic.size() + singleNodeDisapearEmerge.size()), totalCount));
                                taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());
                                NetworkMinerFactory.getInstance().
                                        showNodeMinersDis(MiningObject.MiningObject_NodeDisapearEmerge, singleNodeDisapearEmerge);
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("singlenode3存在结果");
                            singleNodeResultMaps.put(MiningObject.MiningObject_NodeDisapearEmerge.toString(), singleNodeDisapearEmerge);//存结果
                            isExisted = true;
                            awakeNode();
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
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                nodePairTimes.put(tempList.get(i), resultNode);
                                //进度条
                                taskPanel.getBar().setValue(nodePairTimes.size());
                                taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                        String.format("%d%%", 100 * nodePairTimes.size() / totalCount));
                                taskPanel.getLog().append(String.format(
                                        "Completed %d/%d of task.\n", nodePairTimes.size(), totalCount));
                                taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                NetworkMinerFactory.getInstance().
                                        showNodeMinersDis(MiningObject.MiningObject_Times, nodePairTimes);
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            nodePairResultMaps.put(MiningObject.MiningObject_Times.toString(), nodePairTimes);//存结果
                            isExisted = true;
                            awakeNode();
                        }
                    }

                    if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                        int count = 0;
                        for (int i = 0; i < tempList.size(); i++) {
                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                            MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                            if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                                MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                                nodePairTraffic.put(tempList.get(i), resultNode);
                                //进度条
                                taskPanel.getBar().setValue(nodePairTimes.size() + nodePairTraffic.size());
                                taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                        String.format("%d%%", 100 * (nodePairTimes.size() + nodePairTraffic.size()) / totalCount));
                                taskPanel.getLog().append(String.format(
                                        "Completed %d/%d of task.\n", (nodePairTimes.size() + nodePairTraffic.size()), totalCount));
                                taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                NetworkMinerFactory.getInstance().
                                        showNodeMinersDis(MiningObject.MiningObject_Traffic, nodePairTraffic);
                                count += 1;
                            }
                        }
                        if (count == tempList.size()) {
                            System.out.println("nodepair存在结果");
                            nodePairResultMaps.put(MiningObject.MiningObject_Traffic.toString(), nodePairTraffic);//存结果
                            isExisted = true;
                            awakeNode();
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
                            //进度条
                            taskPanel.getBar().setValue(protocolTraffic.size());
                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                    String.format("%d%%", 100 * protocolTraffic.size() / totalCount));
                            taskPanel.getLog().append(String.format(
                                    "Completed %d/%d of task.\n", protocolTraffic.size(), totalCount));
                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                            NetworkMinerFactory.getInstance().
                                    showProtocolMinersDis(MiningObject.MiningObject_Traffic, protocolTraffic);
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        System.out.println("protocol存在结果");
                        protocolResultMaps = protocolTraffic;//存结果
                        isExisted = true;
                        awakeProtocol();
                    }
                }

                break;

            case MiningType_Path:
                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = PathMinerFactory.getInstance();
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            MinerResultsPath resultPath = (MinerResultsPath) resultsFile.file2Result();
                            pathTimes.put(tempList.get(i), resultPath);
                            //进度条
                            taskPanel.getBar().setValue(pathTimes.size());
                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                    String.format("%d%%", 100 * pathTimes.size() / totalCount));
                            taskPanel.getLog().append(String.format(
                                    "Completed %d/%d of task.\n", pathTimes.size(), totalCount));
                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                            NetworkMinerFactory.getInstance().
                                    showPathMinersDis(MiningObject.MiningObject_Times, pathTimes);
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        pathResultMaps.put(MiningObject.MiningObject_Times.toString(), pathTimes);//存结果
                        isExisted = true;
                        awakePath();
                    }
                }

                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = PathMinerFactory.getInstance();
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            MinerResultsPath resultPath = (MinerResultsPath) resultsFile.file2Result();
                            pathTraffic.put(tempList.get(i), resultPath);
                            //进度条
                            taskPanel.getBar().setValue(pathTimes.size() + pathTraffic.size());
                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                    String.format("%d%%", 100 * (pathTimes.size() + pathTraffic.size()) / totalCount));
                            taskPanel.getLog().append(String.format(
                                    "Completed %d/%d of task.\n", (pathTimes.size() + pathTraffic.size()), totalCount));
                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                            NetworkMinerFactory.getInstance().
                                    showPathMinersDis(MiningObject.MiningObject_Traffic, pathTraffic);
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        pathResultMaps.put(MiningObject.MiningObject_Traffic.toString(), pathTraffic);//存结果
                        isExisted = true;
                        awakePath();
                    }
                }
                break;

            case MiningTypes_WholeNetwork:
                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Cluster.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                            networkCluster.put(tempList.get(i), resultNode);
                            //进度条
                            taskPanel.getBar().setValue(networkCluster.size());
                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                    String.format("%d%%", 100 * networkCluster.size() / totalCount));
                            taskPanel.getLog().append(String.format(
                                    "Completed %d/%d of task.\n", networkCluster.size(), totalCount));
                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                            NetworkMinerFactory.getInstance().
                                    showNetWorkMinersDis(MiningObject.MiningObject_Cluster, networkCluster);
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        networkResultMaps.put(MiningObject.MiningObject_Cluster.toString(), networkCluster);//存结果
                        isExisted = true;
                        awakeNet();
                    }
                }

                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Diameter.toString())) {
                    int count = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
                        if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
                            networkDiameter.put(tempList.get(i), resultNode);
                            //进度条
                            taskPanel.getBar().setValue(networkCluster.size() + networkDiameter.size());
                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                    String.format("%d%%", 100 * (networkCluster.size() + networkDiameter.size()) / totalCount));
                            taskPanel.getLog().append(String.format(
                                    "Completed %d/%d of task.\n", (networkCluster.size() + networkDiameter.size()), totalCount));
                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                            NetworkMinerFactory.getInstance().
                                    showNetWorkMinersDis(MiningObject.MiningObject_Diameter, networkDiameter);
                            count += 1;
                        }
                    }
                    if (count == tempList.size()) {
                        networkResultMaps.put(MiningObject.MiningObject_Diameter.toString(), networkDiameter);//存结果
                        isExisted = true;
                        awakeNet();
                    }
                }
                break;
        }
        return isExisted;
    }

    public void getSingleOrPairCount(SingleNodeOrNodePairMinerFactoryDis singleNodeOrNodePairMinerFactoryDis, MiningObject miningObject, ArrayList<String> list) {
        singleNodeOrNodePairMinerFactoryDis.reset();
        singleNodeOrNodePairMinerFactoryDis.setMiningObject(miningObject);
        totalCount = singleNodeOrNodePairMinerFactoryDis.getCount(list);
    }

    public void getPathCount(PathMinerFactoryDis pathMinerFactoryDis, MiningObject miningObject, ArrayList<String> list) {
        pathMinerFactoryDis.reset();
        pathMinerFactoryDis.setMiningObject(miningObject);
        totalCount = pathMinerFactoryDis.getCount(list);
    }

    public void getNetCount(NetworkFactoryDis networkFactoryDis, MiningObject miningObject, ArrayList<String> list) {
        networkFactoryDis.reset();
        networkFactoryDis.setMiningObject(miningObject);
        totalCount = networkFactoryDis.getCount(list);
    }

    public void getProCount(ProtocolAssMinerFactoryDis protocolAssMinerFactoryDis, MiningObject miningObject, ArrayList<String> list) {
        protocolAssMinerFactoryDis.reset();
        protocolAssMinerFactoryDis.setMiningObject(miningObject);
        totalCount = protocolAssMinerFactoryDis.getCount(list);
    }

    public void SingleNodeOrNodePair(SingleNodeOrNodePairMinerFactoryDis singleNodeOrNodePairMinerFactoryDis, MiningObject miningObject) {
        genSingleNodeTask(singleNodeOrNodePairMinerFactoryDis, miningObject);
        initMap(MinerType.MiningType_SinglenodeOrNodePair);
//        initBar();
        //判断是否存在结果，不必启动客户端
        if (!isExistedResult()) {
            awakeThread();
            setIsRunning(true);
            isSingleNodeOver(miningObject);
        } else {
            System.out.println("存在结果");
            getExistedResult();
        }
    }

    public void path(PathMinerFactoryDis pathMinerFactoryDis, MiningObject miningObject) {
        genPathTask(pathMinerFactoryDis, miningObject);
        initMap(MinerType.MiningType_Path);
//        initBar();
        if (!isExistedResult()) {
            awakeThread();
            setIsRunning(true);
            isPathOver(miningObject);
        } else {
            System.out.println("存在结果");
            getExistedResult();
        }
    }

    public void network(NetworkFactoryDis networkFactoryDis, MiningObject miningObject) {
        genNetworkTask(networkFactoryDis, miningObject);
        initMap(MinerType.MiningTypes_WholeNetwork);
//        initBar();
        if (!isExistedResult()) {
            awakeThread();
            setIsRunning(true);
            isNetworkOver(miningObject);
        } else {
            System.out.println("存在结果");
            getExistedResult();
        }
    }

    public void protocol(ProtocolAssMinerFactoryDis protocolAssMinerFactoryDis, MiningObject miningObject) {
        genProtocoTask(protocolAssMinerFactoryDis, miningObject);
        initMap(MinerType.MiningType_ProtocolAssociation);
//        initBar();
        if (!isExistedResult()) {
            awakeThread();
            setIsRunning(true);
            isProtocolOver(miningObject);
        } else {
            System.out.println("存在结果");
            getExistedResult();
        }
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
        System.out.println("totalcount 长度: " + totalCount);
        tempList = new ArrayList<TaskCombination>();
        for (int i = 0; i < combinationList.getTaskCombinationList().size(); i++) {
            if (combinationList.getTaskCombinationList().get(i).getMinerType().equals(minerType)) {
                tempList.add(combinationList.getTaskCombinationList().get(i));
                allCombinationTasks.put(combinationList.getTaskCombinationList().get(i), "n");
            }
        }
        taskCount = 0;
//        taskCount.set(0);
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

    //PcapServer方法

    public void setIsPcapRunning(boolean isRunning) {
        isPcapRunningLock.writeLock().lock();
        try {
            this.isPcapRunning = isRunning;
        } finally {
            isPcapRunningLock.writeLock().unlock();
        }
    }

    public void awakePcap() {
        pcapLock.lock();
        try {
            pcapCon.signalAll();
        } finally {
            pcapLock.unlock();
        }
    }

    public void initPcap(PcapPanel pcapPanel, String inPath, String outPath) {
        this.pcapPanel = pcapPanel;
        this.inPath = inPath;
        this.outPath = outPath;
        this.index = outPath.length() + 1;
    }

    public void initTask(TaskPanel taskPanel) {
        this.taskPanel = taskPanel;
    }

    public void initBar() {
        taskPanel.getBar().setValue(0);
        taskPanel.getBar().setString("读取数据中...");
        taskPanel.getBar().setMaximum(totalCount);
    }

    public void genTasks(String filePath, String type) {
        allTasks = new ArrayList<String>();
        allTasks2 = new ArrayList<String>();//第二步任务
        allTasksTags = new ConcurrentHashMap<String, String>();//带标签，所有不同类型任务
        allTasksTags2 = new ConcurrentHashMap<String, String>();//带标签，所有不同类型任务2
        nameMap = new ConcurrentHashMap<String, String>();//文件part
        nameMap2 = new ConcurrentHashMap<String, String>();//文件part
        combineFile = new ConcurrentHashMap<String, String>();//合并文件,key=最后合并生成的文件，valude=待合并的小文件的文件夹路径
        combineFile2 = new ConcurrentHashMap<String, String>();//合并文件,key=最后合并生成的文件，valude=待合并的小文件的文件夹路径
        delFile = new HashSet<String>();//删除文件
        delFile2 = new HashSet<String>();//删除文件
        tasksMap = new HashMap<String, StringBuilder>();
        swapMap = new HashMap<String, String>();//文件名\r\n,路由号
        swapMap2 = new HashMap<String, String>();//routesrc/1.1_1.2, 1.1_1.2
        pcapCount1 = 0;//发送次数
        pcapCount2 = 0;//发送次数
        recCount = 0;
        recCount2 = 0;
        combineAndDelete = false;

        ArrayList<String> fileNames = new ArrayList<String>();
        getFileList(fileNames, filePath, type);

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
            allTasksTags.put(entry.getValue().toString(), "n");
            swapMap.put(entry.getValue().toString(), entry.getKey());
        }
        tasksCount = allTasks.size();

        //删除outpath下的routsrc
        File folder = new File(outPath + File.separator + "routesrc");
        if (folder.exists() && folder.isDirectory()) {
            deleteFile(folder.getAbsolutePath());
        }

    }

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

    private int getFileList(ArrayList<String> fileNames, String filePath, String type) {
        int num = 0;
        File ff = new File(filePath);
        if (ff.isFile() && filePath.endsWith(type)) {
            fileNames.add(ff.getName());
            num += 1;
        } else if (ff.isDirectory()) {
            File[] files = ff.listFiles();
            for (File f : files) {
                getFileList(fileNames, f.getAbsolutePath(), type);
            }
        }
        return num;
    }

    //启动服务端
    class ServerStart implements Runnable {
        private ServerSocket serverSocket = null;
        private UserClient dataClient;
        private UserClientObject resultClient;
        private UserClient pcapClient;
        private boolean start = false;

        public void run() {
            try {
                serverSocket = new ServerSocket(Integer.valueOf(port));
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
                    Socket pcapSocket = serverSocket.accept();//pcap_dataoutputstream
                    dataClient = new UserClient(dataSocket);
                    resultClient = new UserClientObject(resultSocket);
                    pcapClient = new UserClient(pcapSocket);
                    ReceiveMsg receiveMsg = new ReceiveMsg(dataClient, resultClient);//连接
//                    ReceiveResult receiveResult = new ReceiveResult(resultClient);//连接
                    ParsePcap parsePcap = new ParsePcap(pcapClient);
                    System.out.println("一个客户端已连接！");
                    isRunningLock.readLock().lock();
                    try {
                        if (isRunning) {
                            receiveMsg.setSuspend(false);
                        }
                    } finally {
                        isRunningLock.readLock().unlock();
                    }
                    //pcap是否运行
                    isPcapRunningLock.readLock().lock();
                    try {
                        if (isPcapRunning) {
                            parsePcap.setPcapSuspend(false);
                        }
                    } finally {
                        isPcapRunningLock.readLock().unlock();
                    }
                    new Thread(receiveMsg).start();//启动线程
//                    new Thread(receiveResult).start();//启动线程
                    new Thread(parsePcap).start();//启动线程
                }
            } catch (IOException e) {
                System.out.println("服务端错误位置");
//                e.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                    start = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (dataClient != null) {
                    dataClient.close();
                }
                if (resultClient != null) {
                    resultClient.close();
                }
                if (pcapClient != null) {
                    pcapClient.close();
                }
                start = false;
            } catch (IOException e) {
                e.printStackTrace();
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
        private TaskCombinationResult taskCombinationResult;

        ReceiveMsg(UserClient userClient, UserClientObject userClientObject) {
            this.userClient = userClient;
            this.userClientObject = userClientObject;
            isConnected = true;
        }

        public void setSuspend(boolean suspend) {
            isSuspend = suspend;
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
//                    switch (tempList.get(0).getMinerType()) {
//
//                        case MiningType_SinglenodeOrNodePair:
//                            if (tempList.get(0).getTaskRange().equals(TaskRange.SingleNodeRange)) {
//                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
//                                    int count = 0;
//                                    for (int i = 0; i < tempList.size(); i++) {
//                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
//                                            singleNodeTimes.put(tempList.get(i), resultNode);
//                                            NetworkMinerFactory.getInstance().
//                                                    showNodeMinersDis(MiningObject.MiningObject_Times, singleNodeTimes);
//                                            count += 1;
//                                        }
//                                    }
//                                    if (count == tempList.size()) {
//                                        singleNodeResultMaps.put(MiningObject.MiningObject_Times.toString(), singleNodeTimes);//存结果
//                                        awakeNode();
//                                        isSuspend = true;
//                                        continue;
//                                    }
//                                }
//
//                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
//                                    int count = 0;
//                                    for (int i = 0; i < tempList.size(); i++) {
//                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
//                                            singleNodeTraffic.put(tempList.get(i), resultNode);
//                                            NetworkMinerFactory.getInstance().
//                                                    showNodeMinersDis(MiningObject.MiningObject_Traffic, singleNodeTraffic);
//                                            count += 1;
//                                        }
//                                    }
//                                    if (count == tempList.size()) {
//                                        singleNodeResultMaps.put(MiningObject.MiningObject_Traffic.toString(), singleNodeTraffic);//存结果
//                                        awakeNode();
//                                        isSuspend = true;
//                                        continue;
//                                    }
//                                }
//
//                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge.toString())) {
//                                    int count = 0;
//                                    for (int i = 0; i < tempList.size(); i++) {
//                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
//                                            singleNodeDisapearEmerge.put(tempList.get(i), resultNode);
//                                            NetworkMinerFactory.getInstance().
//                                                    showNodeMinersDis(MiningObject.MiningObject_NodeDisapearEmerge, singleNodeDisapearEmerge);
//                                            count += 1;
//                                        }
//                                    }
//                                    if (count == tempList.size()) {
//                                        singleNodeResultMaps.put(MiningObject.MiningObject_NodeDisapearEmerge.toString(), singleNodeDisapearEmerge);//存结果
//                                        awakeNode();
//                                        isSuspend = true;
//                                        continue;
//                                    }
//                                }
//                            }
//
//                            //nodePair
//                            if (tempList.get(0).getTaskRange().equals(TaskRange.NodePairRange)) {
//                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
//                                    int count = 0;
//                                    for (int i = 0; i < tempList.size(); i++) {
//                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
//                                            nodePairTimes.put(tempList.get(i), resultNode);
//                                            NetworkMinerFactory.getInstance().
//                                                    showNodeMinersDis(MiningObject.MiningObject_Times, nodePairTimes);
//                                            count += 1;
//                                        }
//                                    }
//                                    if (count == tempList.size()) {
//                                        nodePairResultMaps.put(MiningObject.MiningObject_Times.toString(), nodePairTimes);//存结果
//                                        awakeNode();
//                                        isSuspend = true;
//                                        continue;
//                                    }
//                                }
//
//                                if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
//                                    int count = 0;
//                                    for (int i = 0; i < tempList.size(); i++) {
//                                        MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                        MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                        if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                            MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
//                                            nodePairTraffic.put(tempList.get(i), resultNode);
//                                            NetworkMinerFactory.getInstance().
//                                                    showNodeMinersDis(MiningObject.MiningObject_Traffic, nodePairTraffic);
//                                            count += 1;
//                                        }
//                                    }
//                                    if (count == tempList.size()) {
//                                        nodePairResultMaps.put(MiningObject.MiningObject_Traffic.toString(), nodePairTraffic);//存结果
//                                        awakeNode();
//                                        isSuspend = true;
//                                        continue;
//                                    }
//                                }
//                            }
//                            break;
//
//                        case MiningType_ProtocolAssociation:
//                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
//                                int count = 0;
//                                for (int i = 0; i < tempList.size(); i++) {
//                                    MinerFactorySettings settings = ProtocolAssMinerFactory.getInstance();
//                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                    if (resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                        MinerProtocolResults resultNode = (MinerProtocolResults) resultsFile.file2Result();
//                                        protocolTraffic.put(tempList.get(i), resultNode);
//                                        NetworkMinerFactory.getInstance().
//                                                showProtocolMinersDis(MiningObject.MiningObject_Traffic, protocolTraffic);
//                                        count += 1;
//                                    }
//                                }
//                                if (count == tempList.size()) {
//                                    System.out.println("protocol存在结果");
//                                    protocolResultMaps = protocolTraffic;//存结果
//                                    awakeProtocol();
//                                    isSuspend = true;
//                                    continue;
//                                }
//                            }
//
//                        break;
//
//                        case MiningType_Path:
//                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Times.toString())) {
//                                int count = 0;
//                                for (int i = 0; i < tempList.size(); i++) {
//                                    MinerFactorySettings settings = PathMinerFactory.getInstance();
//                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                        MinerResultsPath resultPath = (MinerResultsPath) resultsFile.file2Result();
//                                        pathTimes.put(tempList.get(i), resultPath);
//                                        NetworkMinerFactory.getInstance().
//                                                showPathMinersDis(MiningObject.MiningObject_Times, pathTimes);
//                                        count += 1;
//                                    }
//                                }
//                                if (count == tempList.size()) {
//                                    pathResultMaps.put(MiningObject.MiningObject_Times.toString(), pathTimes);//存结果
//                                    awakePath();
//                                    isSuspend = true;
//                                    continue;
//                                }
//                            }
//
//                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())) {
//                                int count = 0;
//                                for (int i = 0; i < tempList.size(); i++) {
//                                    MinerFactorySettings settings = PathMinerFactory.getInstance();
//                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                        MinerResultsPath resultPath = (MinerResultsPath) resultsFile.file2Result();
//                                        pathTraffic.put(tempList.get(i), resultPath);
//                                        NetworkMinerFactory.getInstance().
//                                                showPathMinersDis(MiningObject.MiningObject_Traffic, pathTraffic);
//                                        count += 1;
//                                    }
//                                }
//                                if (count == tempList.size()) {
//                                    pathResultMaps.put(MiningObject.MiningObject_Traffic.toString(), pathTraffic);//存结果
//                                    awakePath();
//                                    isSuspend = true;
//                                    continue;
//                                }
//                            }
//                            break;
//
//                        case MiningTypes_WholeNetwork:
//                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Cluster.toString())) {
//                                int count = 0;
//                                for (int i = 0; i < tempList.size(); i++) {
//                                    MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                        MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
//                                        networkCluster.put(tempList.get(i), resultNode);
//                                        NetworkMinerFactory.getInstance().
//                                                showNetWorkMinersDis(MiningObject.MiningObject_Cluster, networkCluster);
//                                        count += 1;
//                                    }
//                                }
//                                if (count == tempList.size()) {
//                                    networkResultMaps.put(MiningObject.MiningObject_Cluster.toString(), networkCluster);//存结果
//                                    awakeNet();
//                                    isSuspend = true;
//                                    continue;
//                                }
//                            }
//
//                            if (tempList.get(0).getMiningObject().equals(MiningObject.MiningObject_Diameter.toString())) {
//                                int count = 0;
//                                for (int i = 0; i < tempList.size(); i++) {
//                                    MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                    MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                    if(resultsFile.hasFile(settings, tempList.get(i))) { // 已有挖掘结果存储，则不重新启动miner
//                                        MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
//                                        networkDiameter.put(tempList.get(i), resultNode);
//                                        NetworkMinerFactory.getInstance().
//                                                showNetWorkMinersDis(MiningObject.MiningObject_Diameter, networkDiameter);
//                                        count += 1;
//                                    }
//                                }
//                                if (count == tempList.size()) {
//                                    networkResultMaps.put(MiningObject.MiningObject_Diameter.toString(), networkDiameter);//存结果
//                                    awakeNet();
//                                    isSuspend = true;
//                                    continue;
//                                }
//                            }
//                            break;
//
//                    }


                    dataFromClient = userClient.receiveReady();//接收Ready,@Deprecated在完成后的第二次循环阻塞
                    if (dataFromClient.equals("Ready")) {
                        /*for(Map.Entry<TaskCombination, String> entry : allCombinationTasks.entrySet()){
                            userClientObject.sendObject(entry.getKey());
                        }*/
                        countLock.lock();
                        try {
                            if (taskCount < tempList.size()) {
//                                isSuspend = true;

                                long a = System.currentTimeMillis();
                                System.out.println("第" + taskCount + "次发送" + tempList.size());
                                userClientObject.sendObject(tempList.get(taskCount));
                                long b = System.currentTimeMillis();
                                System.out.println("发送时间：" + (b - a));
                                taskCount += 1;
                                System.out.println("第" + taskCount + "次即将开始");
                            } else {
                                int temp = 0;//中途最后一个结果发回来，强行再发一次最后一个任务，防止客户端没有ready卡死
                                //线程加锁，防止其他线程调用Map
//                                isSuspend = true;
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

                    //接收结果
                    taskCombinationResult = (TaskCombinationResult) userClientObject.receiveObject();
                    System.out.println("接收结果");
                    if (taskCombinationResult.getMinerType() != null) {
                        switch (taskCombinationResult.getMinerType()) {

                            case MiningType_SinglenodeOrNodePair:
                                //单节点
                                if (taskCombinationResult.getTaskRange().equals(TaskRange.SingleNodeRange)) {
                                    if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)) {
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
                                            //进度条
                                            taskPanel.getBar().setValue(singleNodeTimes.size());
                                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                    String.format("%d%%", 100 * singleNodeTimes.size() / totalCount));
                                            taskPanel.getLog().append(String.format(
                                                    "Completed %d/%d of task.\n", singleNodeTimes.size(), totalCount));
                                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());
                                            resultLock.lock();
                                            try {
                                                NetworkMinerFactory.getInstance().
                                                        showNodeMinersDis(MiningObject.MiningObject_Times, singleNodeTimes);
                                            } finally {
                                                resultLock.unlock();
                                            }
                                            if (singleNodeTimes.size() == allCombinationTasks.size() && singleNodeTimeFlag) {
                                                singleNodeResultMaps.put(MiningObject.MiningObject_Times.toString(), singleNodeTimes);
                                                singleNodeTimeFlag = false;//完成，后面未执行完的抛弃
                                                setIsRunning(false);
                                                setIsPcapRunning(false);
                                                isSuspend = true;
                                                awakeNode();
                                            }
//                                            else {
//                                                awakeSingleThread();
//                                            }
                                        } else {
                                            if (!isRunning) {
                                                isSuspend = true;
                                            }
                                        }
                                    }

                                    if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)) {
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
                                            //进度条
                                            taskPanel.getBar().setValue(singleNodeTimes.size() + singleNodeTraffic.size());
                                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                    String.format("%d%%", 100 * (singleNodeTimes.size() + singleNodeTraffic.size()) / totalCount));
                                            taskPanel.getLog().append(String.format(
                                                    "Completed %d/%d of task.\n", (singleNodeTimes.size() + singleNodeTraffic.size()), totalCount));
                                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());
                                            resultLock.lock();
                                            try {
                                                NetworkMinerFactory.getInstance().
                                                        showNodeMinersDis(MiningObject.MiningObject_Traffic, singleNodeTraffic);
                                            } finally {
                                                resultLock.unlock();
                                            }
                                            if (singleNodeTraffic.size() == allCombinationTasks.size() && singleNodeTrafficFlag) {
                                                singleNodeResultMaps.put(MiningObject.MiningObject_Traffic.toString(), singleNodeTraffic);
                                                singleNodeTrafficFlag = false;
                                                setIsRunning(false);
                                                setIsPcapRunning(false);
                                                isSuspend = true;
                                                awakeNode();
                                            }
//                                            else {
//                                                awakeSingleThread();
//                                            }
                                        } else {
                                            if (!isRunning) {
                                                isSuspend = true;
                                            }
                                        }
                                    }

                                    //节点出现消失
                                    if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge)) {
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
                                            //进度条
                                            taskPanel.getBar().setValue(singleNodeTimes.size() + singleNodeTraffic.size() + singleNodeDisapearEmerge.size());
                                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                    String.format("%d%%", 100 * (singleNodeTimes.size() + singleNodeTraffic.size() + singleNodeDisapearEmerge.size()) / totalCount));
                                            taskPanel.getLog().append(String.format(
                                                    "Completed %d/%d of task.\n", (singleNodeTimes.size() + singleNodeTraffic.size() + singleNodeDisapearEmerge.size()), totalCount));
                                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());
                                            resultLock.lock();
                                            try {
                                                NetworkMinerFactory.getInstance().
                                                        showNodeMinersDis(MiningObject.MiningObject_NodeDisapearEmerge, singleNodeDisapearEmerge);
                                            } finally {
                                                resultLock.unlock();
                                            }
                                            if (singleNodeDisapearEmerge.size() == allCombinationTasks.size() && singleNodeDisapearEmergeFlag) {
                                                singleNodeResultMaps.put(MiningObject.MiningObject_NodeDisapearEmerge.toString(), singleNodeDisapearEmerge);
                                                singleNodeDisapearEmergeFlag = false;
                                                setIsRunning(false);
                                                setIsPcapRunning(false);
                                                isSuspend = true;
                                                awakeNode();
                                            }
//                                            else {
//                                                awakeSingleThread();
//                                            }
                                        }  else {
                                            if (!isRunning) {
                                                isSuspend = true;
                                            }                                        }
                                    }
                                }

                                //节点对
                                if (taskCombinationResult.getTaskRange().equals(TaskRange.NodePairRange)){
                                    if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)) {
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
                                            //进度条
                                            taskPanel.getBar().setValue(nodePairTimes.size());
                                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                    String.format("%d%%", 100 * nodePairTimes.size() / totalCount));
                                            taskPanel.getLog().append(String.format(
                                                    "Completed %d/%d of task.\n", nodePairTimes.size(), totalCount));
                                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                            resultLock.lock();
                                            try {
                                                NetworkMinerFactory.getInstance().
                                                        showNodeMinersDis(MiningObject.MiningObject_Times, nodePairTimes);
                                            } finally {
                                                resultLock.unlock();
                                            }
                                            if (nodePairTimes.size() == allCombinationTasks.size() && nodePairTimeFlag) {
                                                nodePairResultMaps.put(MiningObject.MiningObject_Times.toString(), nodePairTimes);
                                                nodePairTimeFlag = false;//完成，后面未执行完的抛弃
                                                setIsRunning(false);
                                                setIsPcapRunning(false);
                                                isSuspend = true;
                                                awakeNode();
                                            }
//                                            else {
//                                                awakeSingleThread();
//                                            }
                                        } else {
                                            if (!isRunning) {
                                                isSuspend = true;
                                            }                                        }
                                    }

                                    if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)) {
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
                                            //进度条
                                            taskPanel.getBar().setValue(nodePairTimes.size() + nodePairTraffic.size());
                                            taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                    String.format("%d%%", 100 * (nodePairTimes.size() + nodePairTraffic.size()) / totalCount));
                                            taskPanel.getLog().append(String.format(
                                                    "Completed %d/%d of task.\n", (nodePairTimes.size() + nodePairTraffic.size()), totalCount));
                                            taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                            resultLock.lock();
                                            try {
                                                NetworkMinerFactory.getInstance().
                                                        showNodeMinersDis(MiningObject.MiningObject_Traffic, nodePairTraffic);
                                            } finally {
                                                resultLock.unlock();
                                            }
                                            if (nodePairTraffic.size() == allCombinationTasks.size() && nodePairTrafficFlag) {
                                                nodePairResultMaps.put(MiningObject.MiningObject_Traffic.toString(), nodePairTraffic);
                                                nodePairTrafficFlag = false;
                                                setIsRunning(false);
                                                setIsPcapRunning(false);
                                                isSuspend = true;
                                                awakeNode();
                                            }
//                                            else {
//                                                awakeSingleThread();
//                                            }
                                        } else {
                                            if (!isRunning) {
                                                isSuspend = true;
                                            }                                        }
                                    }
                                }
                                break;

                            case MiningType_ProtocolAssociation:
                                if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)) {
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
                                                    newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerProtocolResults());
                                                } finally {
                                                    resultLock.unlock();
                                                }
                                                updateMap(tempList.get(i));//更新状态
                                            }
                                        }
                                        //进度条
                                        taskPanel.getBar().setValue(protocolTraffic.size());
                                        taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                String.format("%d%%", 100 * protocolTraffic.size() / totalCount));
                                        taskPanel.getLog().append(String.format(
                                                "Completed %d/%d of task.\n", protocolTraffic.size(), totalCount));
                                        taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                        resultLock.lock();
                                        try {
                                            NetworkMinerFactory.getInstance().
                                                    showProtocolMinersDis(MiningObject.MiningObject_Traffic, protocolTraffic);
                                        } finally {
                                            resultLock.unlock();
                                        }
                                        if (protocolTraffic.size() == allCombinationTasks.size() && protocolTrafficFlag) {
                                            protocolResultMaps = protocolTraffic;
                                            protocolTrafficFlag = false;
                                            setIsRunning(false);
                                            setIsPcapRunning(false);
                                            isSuspend = true;
                                            awakeProtocol();
                                        }
//                                        else {
//                                            awakeSingleThread();
//                                        }
                                    } else {
                                        if (!isRunning) {
                                            isSuspend = true;
                                        }                                    }
                                }
                                break;

                            case MiningType_Path:
                                if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)) {
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
                                        //进度条
                                        taskPanel.getBar().setValue(pathTimes.size());
                                        taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                String.format("%d%%", 100 * pathTimes.size() / totalCount));
                                        taskPanel.getLog().append(String.format(
                                                "Completed %d/%d of task.\n", pathTimes.size(), totalCount));
                                        taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                        resultLock.lock();
                                        try {
                                            NetworkMinerFactory.getInstance().
                                                    showPathMinersDis(MiningObject.MiningObject_Times, pathTimes);
                                        } finally {
                                            resultLock.unlock();
                                        }
                                        if (pathTimes.size() == allCombinationTasks.size() && pathTimeFlag) {
                                            pathResultMaps.put(MiningObject.MiningObject_Times.toString(), pathTimes);//通信次数结果
                                            pathTimeFlag = false;//完成，后面未执行完的抛弃
                                            setIsRunning(false);
                                            setIsPcapRunning(false);
                                            isSuspend = true;
                                            awakePath();
                                        }
//                                        else {
//                                            awakeSingleThread();
//                                        }
                                    } else {
                                        if (!isRunning) {
                                            isSuspend = true;
                                        }                                    }
                                }

                                if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)) {
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
                                        //进度条
                                        taskPanel.getBar().setValue(pathTimes.size() + pathTraffic.size());
                                        taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                String.format("%d%%", 100 * (pathTimes.size() + pathTraffic.size()) / totalCount));
                                        taskPanel.getLog().append(String.format(
                                                "Completed %d/%d of task.\n", (pathTimes.size() + pathTraffic.size()), totalCount));
                                        taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                        resultLock.lock();
                                        try {
                                            NetworkMinerFactory.getInstance().
                                                    showPathMinersDis(MiningObject.MiningObject_Traffic, pathTraffic);
                                        } finally {
                                            resultLock.unlock();
                                        }
                                        if (pathTraffic.size() == allCombinationTasks.size() && pathTrafficFlag) {
                                            pathResultMaps.put(MiningObject.MiningObject_Traffic.toString(), pathTraffic);
                                            pathTrafficFlag = false;
                                            setIsRunning(false);
                                            setIsPcapRunning(false);
                                            isSuspend = true;
                                            awakePath();
                                        }
//                                        else {
//                                            awakeSingleThread();
//                                        }
                                    } else {
                                        if (!isRunning) {
                                            isSuspend = true;
                                        }                                    }
                                }
                                System.out.println("跳出switch");
                                break;

                            case MiningTypes_WholeNetwork:

                                if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Cluster)) {
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
                                        //进度条
                                        taskPanel.getBar().setValue(networkCluster.size());
                                        taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                String.format("%d%%", 100 * networkCluster.size() / totalCount));
                                        taskPanel.getLog().append(String.format(
                                                "Completed %d/%d of task.\n", networkCluster.size(), totalCount));
                                        taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                        resultLock.lock();
                                        try {
                                            NetworkMinerFactory.getInstance().
                                                    showNetWorkMinersDis(MiningObject.MiningObject_Cluster, networkCluster);
                                        } finally {
                                            resultLock.unlock();
                                        }
                                        if (networkCluster.size() == allCombinationTasks.size() && networkClusterFlag) {
                                            networkResultMaps.put(MiningObject.MiningObject_Cluster.toString(), networkCluster);//通信次数结果
                                            networkClusterFlag = false;//完成，后面未执行完的抛弃
                                            setIsRunning(false);
                                            setIsPcapRunning(false);
                                            isSuspend = true;
                                            awakeNet();
                                        }
//                                        else {
//                                            awakeSingleThread();
//                                        }
                                    } else {
                                        if (!isRunning) {
                                            isSuspend = true;
                                        }                                    }
                                }

                                if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Diameter)) {
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
                                        //进度条
                                        taskPanel.getBar().setValue(networkCluster.size() + networkDiameter.size());
                                        taskPanel.getBar().setString(taskProgressDis.getPhase() +" " +
                                                String.format("%d%%", 100 * (networkCluster.size() + networkDiameter.size()) / totalCount));
                                        taskPanel.getLog().append(String.format(
                                                "Completed %d/%d of task.\n", (networkCluster.size() + networkDiameter.size()), totalCount));
                                        taskPanel.getLog().setCaretPosition( taskPanel.getLog().getText().length());//滚动条自动滚动
                                        resultLock.lock();
                                        try {
                                            NetworkMinerFactory.getInstance().
                                                    showNetWorkMinersDis(MiningObject.MiningObject_Diameter, networkDiameter);
                                        } finally {
                                            resultLock.unlock();
                                        }
                                        if (networkDiameter.size() == allCombinationTasks.size() && networkDiameterFlag) {
                                            networkResultMaps.put(MiningObject.MiningObject_Diameter.toString(), networkDiameter);
                                            networkDiameterFlag = false;
                                            setIsRunning(false);
                                            setIsPcapRunning(false);
                                            isSuspend = true;
                                            awakeNet();
                                        }
//                                        else {
//                                            awakeSingleThread();
//                                        }
                                    } else {
                                        if (!isRunning) {
                                            isSuspend = true;
                                        }                                    }
                                }
                                System.out.println("跳出nnnnnswitch");

                                break;

                            default:
                                break;
                        }
                    }
//                    else {
//                        System.out.println("返回为空...");
//                        isRunningLock.readLock().lock();
//                        try {
//                            if (isRunning) {
//                                System.out.println("还在执行，唤起...");
//                                awakeSingleThread();
//                            }
//                        } finally {
//                            isRunningLock.readLock().unlock();
//                        }
//                    }



                }
            } catch (IOException e) {
                System.out.println("接收ready关闭");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                System.out.println("进入finall");
                isConnected = false;
                try {
                    userClient.close();
                    userClientObject.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("再唤起一次...");
//                awakeSingleThread();
            }
        }
    }

    //接收objectoutputstream的封装
//    class ReceiveResult implements Runnable {
//        private boolean isConnected = false;
//        private UserClientObject userClientObject;
//        private TaskCombinationResult taskCombinationResult;
//
//        ReceiveResult(UserClientObject userClientObject) {
//            this.userClientObject = userClientObject;
//            isConnected = true;
//        }
//
//        public void updateMap(TaskCombination task) {
//            //map加锁,改为concurrent
////            mapLock.writeLock().lock();
////            try {
//            if (allCombinationTasks.get(task).equals("n")) {
//                allCombinationTasks.remove(task);
//                allCombinationTasks.put(task, "y");//更新标记，表示完成
//            }
//
//               /* for (Map.Entry<TaskCombination, String> entry : allCombinationTasks.entrySet()) {
//                    System.out.println("key= " + entry.getKey().getName()
//                            + " and value= " + entry.getValue());
//                }*/
////            } finally {
////                mapLock.writeLock().unlock();
////            }
//        }
//
//        @Override
//        public void run() {
//            NetworkMinerFactory.getInstance();
//            //接收任务完成后得到的结果
//            try {
//                while (isConnected) {
//                    taskCombinationResult = (TaskCombinationResult) userClientObject.receiveObject();
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            if (taskCombinationResult.getMinerType() != null) {
//                                switch (taskCombinationResult.getMinerType()) {
//
//                                    case MiningType_SinglenodeOrNodePair:
//                                        //单节点
//                                        if (taskCombinationResult.getTaskRange().equals(TaskRange.SingleNodeRange)) {
//                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)
//                                                    && singleNodeTimeFlag) {
//                                                if (singleNodeTimes.size() < allCombinationTasks.size()) {
//                                                    singleNodeTimeFlag = true;
//                                                    for (int i = 0; i < tempList.size(); i++) {
//                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                            resultLock.lock();
//                                                            try {
//                                                                singleNodeTimes.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                            } finally {
//                                                                resultLock.unlock();
//                                                            }
//                                                            updateMap(tempList.get(i));//更新状态
//                                                        }
//                                                    }
//                                                    NetworkMinerFactory.getInstance().
//                                                            showNodeMinersDis(MiningObject.MiningObject_Times, singleNodeTimes);
//                                                    if (singleNodeTimes.size() == allCombinationTasks.size() && singleNodeTimeFlag) {
//                                                        singleNodeResultMaps.put(MiningObject.MiningObject_Times.toString(), singleNodeTimes);
//                                                        singleNodeTimeFlag = false;//完成，后面未执行完的抛弃
//                                                        setIsRunning(false);
//                                                        awakeNode();
//                                                    } else {
//                                                        awakeSingleThread();
//                                                    }
//                                                }
//                                            }
//
//                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
//                                                    && singleNodeTrafficFlag) {
//                                                if (singleNodeTraffic.size() < allCombinationTasks.size()) {
//                                                    singleNodeTrafficFlag = true;
//                                                    for (int i = 0; i < tempList.size(); i++) {
//                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                            resultLock.lock();
//                                                            try {
//                                                                singleNodeTraffic.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                            } finally {
//                                                                resultLock.unlock();
//                                                            }
//                                                            updateMap(tempList.get(i));//更新状态
//                                                        }
//                                                    }
//                                                    NetworkMinerFactory.getInstance().
//                                                            showNodeMinersDis(MiningObject.MiningObject_Traffic, singleNodeTraffic);
//                                                    if (singleNodeTraffic.size() == allCombinationTasks.size() && singleNodeTrafficFlag) {
//                                                        singleNodeResultMaps.put(MiningObject.MiningObject_Traffic.toString(), singleNodeTraffic);
//                                                        singleNodeTrafficFlag = false;
//                                                        setIsRunning(false);
//                                                        awakeNode();
//                                                    } else {
//                                                        awakeSingleThread();
//                                                    }
//                                                }
//                                            }
//
//                                            //节点出现消失
//                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge)
//                                                    && singleNodeDisapearEmergeFlag) {
//                                                if (singleNodeDisapearEmerge.size() < allCombinationTasks.size()) {
//                                                    singleNodeDisapearEmergeFlag = true;
//                                                    for (int i = 0; i < tempList.size(); i++) {
//                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                            resultLock.lock();
//                                                            try {
//                                                                singleNodeDisapearEmerge.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                            } finally {
//                                                                resultLock.unlock();
//                                                            }
//                                                            updateMap(tempList.get(i));//更新状态
//                                                        }
//                                                    }
//                                                    NetworkMinerFactory.getInstance().
//                                                            showNodeMinersDis(MiningObject.MiningObject_NodeDisapearEmerge, singleNodeDisapearEmerge);
//                                                    if (singleNodeDisapearEmerge.size() == allCombinationTasks.size() && singleNodeDisapearEmergeFlag) {
//                                                        singleNodeResultMaps.put(MiningObject.MiningObject_NodeDisapearEmerge.toString(), singleNodeDisapearEmerge);
//                                                        singleNodeDisapearEmergeFlag = false;
//                                                        setIsRunning(false);
//                                                        awakeNode();
//                                                    } else {
//                                                        awakeSingleThread();
//                                                    }
//                                                }
//                                            }
//                                        }
//
//                                        //节点对
//                                        if (taskCombinationResult.getTaskRange().equals(TaskRange.NodePairRange)){
//                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)
//                                                    && nodePairTimeFlag) {
//                                                if (nodePairTimes.size() < allCombinationTasks.size()) {
//                                                    nodePairTimeFlag = true;
//                                                    for (int i = 0; i < tempList.size(); i++) {
//                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                            resultLock.lock();
//                                                            try {
//                                                                nodePairTimes.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                            } finally {
//                                                                resultLock.unlock();
//                                                            }
//                                                            updateMap(tempList.get(i));//更新状态
//                                                        }
//                                                    }
//                                                    NetworkMinerFactory.getInstance().
//                                                            showNodeMinersDis(MiningObject.MiningObject_Times, nodePairTimes);
//                                                    if (nodePairTimes.size() == allCombinationTasks.size() && nodePairTimeFlag) {
//                                                        nodePairResultMaps.put(MiningObject.MiningObject_Times.toString(), nodePairTimes);
//                                                        nodePairTimeFlag = false;//完成，后面未执行完的抛弃
//                                                        setIsRunning(false);
//                                                        awakeNode();
//                                                    } else {
//                                                        awakeSingleThread();
//                                                    }
//                                                }
//                                            }
//
//                                            if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
//                                                    && nodePairTrafficFlag) {
//                                                if (singleNodeTraffic.size() < allCombinationTasks.size()) {
//                                                    nodePairTrafficFlag = true;
//                                                    for (int i = 0; i < tempList.size(); i++) {
//                                                        if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                            resultLock.lock();
//                                                            try {
//                                                                nodePairTraffic.put(tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                                MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                                                MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                                newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNodeResults());
//                                                            } finally {
//                                                                resultLock.unlock();
//                                                            }
//                                                            updateMap(tempList.get(i));//更新状态
//                                                        }
//                                                    }
//                                                    NetworkMinerFactory.getInstance().
//                                                            showNodeMinersDis(MiningObject.MiningObject_Traffic, nodePairTraffic);
//                                                    if (nodePairTraffic.size() == allCombinationTasks.size() && nodePairTrafficFlag) {
//                                                        nodePairResultMaps.put(MiningObject.MiningObject_Traffic.toString(), nodePairTraffic);
//                                                        nodePairTrafficFlag = false;
//                                                        setIsRunning(false);
//                                                        awakeNode();
//                                                    } else {
//                                                        awakeSingleThread();
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        break;
//
//                                    case MiningType_ProtocolAssociation:
//                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
//                                                && protocolTrafficFlag) {
//                                            if (protocolTraffic.size() < allCombinationTasks.size()) {
//                                                protocolTrafficFlag = true;
//                                                for (int i = 0; i < tempList.size(); i++) {
//                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                        resultLock.lock();
//                                                        try {
//                                                            protocolTraffic.put(tempList.get(i), taskCombinationResult.getMinerProtocolResults());
//                                                            /* 挖掘完成，保存结果文件 */
//                                                            MinerFactorySettings settings = ProtocolAssMinerFactory.getInstance();
//                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                            newResultsFile.result2File(settings, tempList.get(i),  taskCombinationResult.getMinerProtocolResults());
//                                                        } finally {
//                                                            resultLock.unlock();
//                                                        }
//                                                        updateMap(tempList.get(i));//更新状态
//                                                    }
//                                                }
//                                                NetworkMinerFactory.getInstance().
//                                                        showProtocolMinersDis(MiningObject.MiningObject_Traffic, protocolTraffic);
//                                                if (protocolTraffic.size() == allCombinationTasks.size() && protocolTrafficFlag) {
//                                                    protocolResultMaps = protocolTraffic;
//                                                    protocolTrafficFlag = false;
//                                                    setIsRunning(false);
//                                                    awakeProtocol();
//                                                } else {
//                                                    awakeSingleThread();
//                                                }
//                                            }
//                                        }
//                                        break;
//
//                                    case MiningType_Path:
//                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Times)
//                                                && pathTimeFlag) {
//                                            if (pathTimes.size() < allCombinationTasks.size()) {
//                                                pathTimeFlag = true;
//                                                for (int i = 0; i < tempList.size(); i++) {
//                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                        resultLock.lock();
//                                                        try {
//                                                            pathTimes.put(tempList.get(i), taskCombinationResult.getMinerResultsPath());
//                                                            /* 挖掘完成，保存结果文件 */
//                                                            MinerFactorySettings settings = PathMinerFactoryDis.getInstance();
//                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerResultsPath());
//                                                        } finally {
//                                                            resultLock.unlock();
//                                                        }
//                                                        updateMap(tempList.get(i));//更新状态
//                                                    }
//                                                }
//                                                NetworkMinerFactory.getInstance().
//                                                        showPathMinersDis(MiningObject.MiningObject_Times, pathTimes);
//                                                if (pathTimes.size() == allCombinationTasks.size() && pathTimeFlag) {
//                                                    pathResultMaps.put(MiningObject.MiningObject_Times.toString(), pathTimes);//通信次数结果
//                                                    pathTimeFlag = false;//完成，后面未执行完的抛弃
//                                                    setIsRunning(false);
//                                                    awakePath();
//                                                } else {
//                                                    awakeSingleThread();
//                                                }
//                                            }
//                                        }
//
//                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Traffic)
//                                                && pathTrafficFlag) {
//                                            if (pathTraffic.size() < allCombinationTasks.size()) {
//                                                pathTrafficFlag = true;
//                                                for (int i = 0; i < tempList.size(); i++) {
//                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                        resultLock.lock();
//                                                        try {
//                                                            pathTraffic.put(tempList.get(i), taskCombinationResult.getMinerResultsPath());
//                                                            /* 挖掘完成，保存结果文件 */
//                                                            MinerFactorySettings settings = PathMinerFactoryDis.getInstance();
//                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerResultsPath());
//                                                        } finally {
//                                                            resultLock.unlock();
//                                                        }
//                                                        updateMap(tempList.get(i));//更新状态
//                                                    }
//                                                }
//                                                NetworkMinerFactory.getInstance().
//                                                        showPathMinersDis(MiningObject.MiningObject_Traffic, pathTraffic);
//                                                if (pathTraffic.size() == allCombinationTasks.size() && pathTrafficFlag) {
//                                                    pathResultMaps.put(MiningObject.MiningObject_Traffic.toString(), pathTraffic);
//                                                    pathTrafficFlag = false;
//                                                    setIsRunning(false);
//                                                    awakePath();
//                                                } else {
//                                                    awakeSingleThread();
//                                                }
//                                            }
//                                        }
//                                        System.out.println("跳出switch");
//                                        break;
//
//                                    case MiningTypes_WholeNetwork:
//
//                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Cluster)
//                                                && networkClusterFlag) {
//                                            if (networkCluster.size() < allCombinationTasks.size()) {
//                                                networkClusterFlag = true;
//                                                for (int i = 0; i < tempList.size(); i++) {
//                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                        resultLock.lock();
//                                                        try {
//                                                            networkCluster.put(tempList.get(i), taskCombinationResult.getMinerNetworkResults());
//                                                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNetworkResults());
//                                                        } finally {
//                                                            resultLock.unlock();
//                                                        }
//                                                        updateMap(tempList.get(i));//更新状态
//                                                    }
//                                                }
//                                                NetworkMinerFactory.getInstance().
//                                                        showNetWorkMinersDis(MiningObject.MiningObject_Cluster, networkCluster);
//                                                if (networkCluster.size() == allCombinationTasks.size() && networkClusterFlag) {
//                                                    networkResultMaps.put(MiningObject.MiningObject_Cluster.toString(), networkCluster);//通信次数结果
//                                                    networkClusterFlag = false;//完成，后面未执行完的抛弃
//                                                    setIsRunning(false);
//                                                    awakeNet();
//                                                } else {
//                                                    awakeSingleThread();
//                                                }
//                                            }
//                                        }
//
//                                        if (taskCombinationResult.getMiningObject().equals(MiningObject.MiningObject_Diameter)
//                                                && networkDiameterFlag) {
//                                            if (networkDiameter.size() < allCombinationTasks.size()) {
//                                                networkDiameterFlag = true;
//                                                for (int i = 0; i < tempList.size(); i++) {
//                                                    if (tempList.get(i).getName().equals(taskCombinationResult.getName())) {
//                                                        resultLock.lock();
//                                                        try {
//                                                            networkDiameter.put(tempList.get(i), taskCombinationResult.getMinerNetworkResults());
//                                                            MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(tempList.get(i));
//                                                            MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(tempList.get(i).getMiningObject()));
//                                                            newResultsFile.result2File(settings, tempList.get(i), taskCombinationResult.getMinerNetworkResults());
//                                                        } finally {
//                                                            resultLock.unlock();
//                                                        }
//                                                        updateMap(tempList.get(i));//更新状态
//                                                    }
//                                                }
//                                                NetworkMinerFactory.getInstance().
//                                                        showNetWorkMinersDis(MiningObject.MiningObject_Diameter, networkDiameter);
//                                                if (networkDiameter.size() == allCombinationTasks.size() && networkDiameterFlag) {
//                                                    networkResultMaps.put(MiningObject.MiningObject_Diameter.toString(), networkDiameter);
//                                                    networkDiameterFlag = false;
//                                                    setIsRunning(false);
//                                                    awakeNet();
//                                                } else {
//                                                    awakeSingleThread();
//                                                }
//                                            }
//                                        }
//                                        System.out.println("跳出nnnnnswitch");
//
//                                        break;
//
//                                    default:
//                                        break;
//                                }
//                            } else {
//                                System.out.println("返回为空...");
//                                isRunningLock.readLock().lock();
//                                try {
//                                    if (isRunning) {
//                                        System.out.println("还在执行，唤起...");
//                                        awakeSingleThread();
//                                    }
//                                } finally {
//                                    isRunningLock.readLock().unlock();
//                                }
//                            }
//                        }
//                    };
//                    new Thread(runnable).start();
//                }
//            } catch (IOException e) {
//                System.out.println("服务端关闭IO");
////                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                isConnected = false;
//                try {
//                    userClientObject.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }

    class ParsePcap implements Runnable {
        private boolean firstConnected = false;
        private boolean lastConnected = false;
        private UserClient userClient;
        private String dataFromClient = "";
        private long totalLen;
        private String finalFolderPath;
        private String task;
        private String task2;
        private String status;
        private boolean isEmpty = false;
        private boolean isEmpty2 = false;
        private boolean isPcapSuspend = true;
        private boolean isConnected = true;


        ParsePcap(UserClient userClient) {
            this.userClient = userClient;
            firstConnected = true;
        }

        public void setPcapSuspend(boolean suspend) {
            isPcapSuspend = suspend;
        }

        @Override
        public void run() {
            try {
                while (isConnected) {
                    if (isPcapSuspend) {
                        pcapLock.lock();
                        try {
                            System.out.println("pcap阻塞");
                            pcapCon.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            pcapLock.unlock();
                        }
                    }
                    pcapPanel.getBar().setValue(recCount);
                    pcapPanel.getBar().setMaximum(allTasks.size());//进度条最大
                    pcapPanel.getjLabel().setText("阶段 1/3");
                    long a = System.currentTimeMillis();
                    while (firstConnected) {
                        dataFromClient = userClient.receiveMsg();
                        System.out.println("First接收到ready");
                        userClient.sendMsg("First");
                        if (dataFromClient.equals("Ready")) {
                            //pcapCount1 < 或 =两种情况，只发送一次
                            sendLock.lock();
                            try {
                                if (pcapCount1 < allTasks.size()) {
                                    userClient.sendTask(allTasks.get(pcapCount1));
                                    System.out.println("第" + pcapCount1 + "次已发送" + allTasks.size());
                                    pcapCount1 += 1;
                                    System.out.println("下一次发送：" + pcapCount1);
                                } else {
                                    int temp = 0;//中途最后一个结果发回来，发送Empty，避免客户端发送ready后接收不到任务造成死锁

                                    //找到没有完成的任务
                                    for (Map.Entry<String, String> entry : allTasksTags.entrySet()) {
                                        temp += 1;
                                        System.out.println("遍历TaskCombination= " + entry.getKey() +
                                                " and String = " + entry.getValue());
                                        if (entry.getValue().equals("n")) {
                                            userClient.sendTask(entry.getKey());
                                            System.out.println("第二次发送的task：" + entry.getKey());
                                            break;
                                        }
                                        if (temp == allTasksTags.size()) {
                                            userClient.sendMsg("Empty");//全部结果已返回，客户端重新待命
                                            System.out.println("发送Empty");
                                            isEmpty = true;
                                        }
                                    }
                                }
                            } finally {
                                sendLock.unlock();
                            }
                        }

                        //接收结果
//                        recLock.lock();
//                        try {
                            if (!isEmpty) {
                                //判断是否返回已存在结果，若不存在，则接收
                                task = userClient.receiveMsg();
                                if (allTasksTags.get(task).equals("y")) {
                                    userClient.sendMsg(status = "Existent");
                                } else {
                                    userClient.sendMsg(status = "Absent");
                                }

                                if (status.equals("Absent")) {
                                    status = null;
                                    if (recCount < tasksCount) {
                                        finalFolderPath = outPath;
                                        //接收文件
                                        receiveResult(finalFolderPath);
                                        updateMap(task);
                                        recCount += 1;
                                        pcapPanel.getBar().setValue(recCount);
                                        pcapPanel.getjLabel().setText("阶段 1/3");
                                        if (recCount == tasksCount) {
//                                    userClient.close();
                                            System.out.println("运行结束");
                                            comAndDel.lock();
                                            try {
                                                combineFiles(combineFile);
                                                System.out.println("文件已合并");
                                                deleteFile(delFile);
                                                System.out.println("文件已删除");
                                                combineAndDelete = true;
                                                firstConnected = false;
                                                lastConnected = true;
                                            } finally {
                                                comAndDel.unlock();
                                            }
//                                        recCon.await();//释放cLock，但recLock无法释放!!!
                                        }
                                    }
                                } else if (status.equals("Existent")) {
                                    status = null;
                                    if (recCount < tasksCount) {
                                        continue;
                                    } else if (recCount == tasksCount) {
                                        comAndDel.lock();
                                        try {
                                            if (!combineAndDelete) {
                                                combineFiles(combineFile);
                                                System.out.println("文件已合并");
                                                deleteFile(delFile);
                                                System.out.println("文件已删除");
                                                combineAndDelete = true;
                                                firstConnected = false;
                                                lastConnected = true;
                                                System.out.println("运行结束2");
                                            } else {
                                                firstConnected = false;
                                                lastConnected = true;
                                                System.out.println("运行结束2");
                                            }
                                        } finally {
                                            comAndDel.unlock();
                                        }

//                                    recCon.await();
                                    }
                                }
                            } else {
                                comAndDel.lock();
                                try {
                                    if (!combineAndDelete) {
                                        combineFiles(combineFile);
                                        System.out.println("文件已合并");
                                        deleteFile(delFile);
                                        System.out.println("文件已删除");
                                        combineAndDelete = true;
                                        firstConnected = false;
                                        lastConnected = true;
                                        System.out.println("运行结束3");
                                    } else {
                                        firstConnected = false;
                                        lastConnected = true;
                                        System.out.println("运行结束3");
                                    }
                                } finally {
                                    comAndDel.unlock();
                                }
//                            recCon.await();
                            }
//                        } finally {
//                            recLock.unlock();
//                        }
                    }

                    tasksCount = allTasks2.size();
                    pcapPanel.getBar().setValue(recCount2);
                    pcapPanel.getBar().setMaximum(tasksCount);//进度条最大
                    pcapPanel.getjLabel().setText("阶段 2/3");
                    //执行后2步
                    while (lastConnected) {
                        dataFromClient = userClient.receiveMsg();
                        System.out.println("last接收到ready");
                        userClient.sendMsg("Last");
                        if (dataFromClient.equals("Ready")) {
                            //pcapCount1 < 或 =两种情况，只发送一次
                            sendLock2.lock();
                            try {
                                if (pcapCount2 < allTasks2.size()) {
                                    userClient.sendTask(allTasks2.get(pcapCount2));
                                    System.out.println("第" + pcapCount2 + "次已发送" + allTasks2.size());
                                    sendFileTask(allTasks2.get(pcapCount2).split(DELIMITER)[0]);//发送单个文件,routesrc/10.0.0.1_10.0.0.2.bin
                                    pcapCount2 += 1;
                                    System.out.println("下一次发送：" + pcapCount2);
                                } else {
                                    int temp = 0;//中途最后一个结果发回来，发送Empty，避免客户端发送ready后接收不到任务造成死锁

                                    //找到没有完成的任务
                                    for (Map.Entry<String, String> entry : allTasksTags2.entrySet()) {
                                        temp += 1;
                                        System.out.println("遍历TaskCombination= " + entry.getKey() +
                                                " and String = " + entry.getValue());
                                        if (entry.getValue().equals("n")) {
                                            userClient.sendTask(entry.getKey());
                                            sendFileTask(entry.getKey().split(DELIMITER)[0]);
                                            System.out.println("第二次发送的task：" + entry.getKey());
                                            break;
                                        }
                                        if (temp == allTasksTags2.size()) {
                                            userClient.sendMsg("Empty");//全部结果已返回，客户端重新待命
                                            System.out.println("发送Empty");
                                            isEmpty2 = true;
                                        }
                                    }
                                }
                            } finally {
                                sendLock2.unlock();
                            }
                        }

                        //接收结果
//                        recLock2.lock();
//                        try {
                            if (!isEmpty2) {
                                //判断是否返回已存在结果
                                task2 = userClient.receiveMsg();
                                System.out.println("alltaskstags2: " + allTasksTags2.size());
                                if (allTasksTags2.get(task2).equals("y")) {
                                    userClient.sendMsg(status = "Existent");
                                    System.out.println("发送existent");
                                } else {
                                    userClient.sendMsg(status = "Absent");
                                    System.out.println("发送absent");
                                }

                                if (status.equals("Absent")) {
                                    status = null;
                                    if (recCount2 < tasksCount) {
                                        finalFolderPath = outPath;
                                        //接收文件
                                        receiveResult2(finalFolderPath);
                                        updateMap2(task2);
                                        recCount2 += 1;
                                        pcapPanel.getBar().setValue(recCount2);
                                        pcapPanel.getjLabel().setText("阶段 2/3");
                                        if (recCount2 == tasksCount) {
                                            System.out.println("运行结束2.1");
                                            comAndDel.lock();
                                            try {
                                                combineFiles2(combineFile2);
                                                System.out.println("文件已合并");
                                                deleteFile(delFile2);
                                                System.out.println("文件已删除");
                                                combineAndDelete2 = true;
                                                lastConnected = false;
                                                firstConnected = true;
                                                isEmpty = isEmpty2 = false;
                                                isPcapSuspend = true;
                                                setIsPcapRunning(false);
                                                setIsRunning(false);
                                            } finally {
                                                comAndDel.unlock();
                                            }

                                            getModifiedTime(inPath, "pcap");
                                            pcapPanel.getBar().setValue(0);
                                            pcapPanel.getBar().setMaximum(3);
                                            pcapPanel.getjLabel().setText("阶段 3/3");
                                            parseByDay = new ParseByDay(outPath, outPath, date);
                                            parseByDay.initDataByDay();
                                            parseByDay.parseNode();
                                            pcapPanel.getBar().setValue(1);
                                            parseByDay.parseRoute();
                                            pcapPanel.getBar().setValue(2);
                                            parseByDay.parseTraffic();
                                            pcapPanel.getBar().setValue(3);
                                            pcapPanel.getjLabel().setText("全部完成");
                                            pcapPanel.getBeginDig().setEnabled(true);
                                            long b = System.currentTimeMillis();
                                            System.out.println("time.... " + (b - a));
                                        }
                                    }
                                } else if (status.equals("Existent")) {
                                    status = null;
                                    if (recCount2 < tasksCount) {
                                        continue;
                                    } else if (recCount2 == tasksCount) {
                                        comAndDel.lock();
                                        try {
                                            if (!combineAndDelete2) {
                                                combineFiles2(combineFile2);
                                                System.out.println("文件已合并");
                                                deleteFile(delFile2);
                                                System.out.println("文件已删除");
                                                combineAndDelete2 = true;
                                                lastConnected = false;
                                                firstConnected = true;
                                                isEmpty = isEmpty2 = false;
                                                isPcapSuspend = true;
                                                setIsPcapRunning(false);
                                                setIsRunning(false);
                                            } else {
                                                lastConnected = false;
                                                firstConnected = true;
                                                isEmpty = isEmpty2 = false;
                                                isPcapSuspend = true;
                                                setIsPcapRunning(false);
                                                setIsRunning(false);
                                            }
                                            System.out.println("运行结束2.2");
                                            long b = System.currentTimeMillis();
                                            System.out.println("time.... " + (b - a));
                                        } finally {
                                            comAndDel.unlock();
                                        }

                                    }
                                }
                            } else {
                                comAndDel.lock();
                                try {
                                    if (!combineAndDelete2) {
                                        combineFiles2(combineFile2);
                                        System.out.println("文件已合并");
                                        deleteFile(delFile2);
                                        System.out.println("文件已删除");
                                        combineAndDelete2 = true;
                                        lastConnected = false;
                                        firstConnected = true;
                                        isEmpty = isEmpty2 = false;
                                        isPcapSuspend = true;
                                        setIsPcapRunning(false);
                                        setIsRunning(false);
                                    } else {
                                        lastConnected = false;
                                        firstConnected = true;
                                        isEmpty = isEmpty2 = false;
                                        isPcapSuspend = true;
                                        setIsPcapRunning(false);
                                        setIsRunning(false);
                                    }
                                    System.out.println("运行结束2.3");
                                    long b = System.currentTimeMillis();
                                    System.out.println("time.... " + (b - a));
                                } finally {
                                    comAndDel.unlock();
                                }
                            }
//                        } finally {
//                            recLock2.unlock();
//                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("发送文件报错");
                e.printStackTrace();
            } finally {
                System.out.println("进入pf");
//                firstConnected = false;
//                lastConnected = false;
//                isPcapSuspend = true;
//                setIsPcapRunning(false);//只有在完成任务后才设为false,与task保持同步
                isConnected = false;
                try {
                    userClient.close();
                    System.out.println("关闭流");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        //得到最后修改时间
        private void getModifiedTime(String fPath, String type) {
            File ff = new File(fPath);
            if (ff.isFile() && fPath.endsWith(type)) {
                long time = ff.lastModified();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                date = sdf.format(new Date(time));
                return;
            } else if (ff.isDirectory()) {
                File[] files = ff.listFiles();
                for (File f : files) {
                    getModifiedTime(f.getAbsolutePath(), type);
                }
            }
        }

        private void sendFileTask(String task) throws IOException{
            String finalPath = outPath + File.separator + task;
            String subFolder = task.substring(0, task.indexOf("\\"));
            File file = new File(finalPath);
            sendFolder(subFolder);//发送routesrc文件夹
            sendFile(file);
            userClient.sendMsg("endTransmit");//结束任务
        }

        private void sendFile(File file) {
            byte[] sendBuffer = new byte[BUF_LEN];
            int length;
            try {
                userClient.sendMsg("sendFile");
                userClient.sendMsg(file.getName());
                System.out.println("fileName: " + file.getName());

                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                length = dis.read(sendBuffer, 0, sendBuffer.length);
                while (length > 0) {
                    userClient.sendInt(length);
                    userClient.sendByte(sendBuffer, 0, length);
                    length = dis.read(sendBuffer, 0, sendBuffer.length);
                }
                userClient.sendInt(length);
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendFolder(String subFolder) {
            try {
                userClient.sendMsg("sendFolder");
                userClient.sendMsg(subFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //traffic排序
        private void sortTraffic(String path, String outPath) {
            try {
                InputStreamReader in = new InputStreamReader(new FileInputStream(path), "UTF-8");
                BufferedReader bin = new BufferedReader(in);
                String curLine;
                ArrayList<TrafficKey> keys = new ArrayList<TrafficKey>();

                while ((curLine = bin.readLine()) != null) {
                    String str[] = curLine.split(",");
                    TrafficKey key = new TrafficKey();
                    key.setTime(Long.valueOf(str[0]));
                    key.setSrcIp(str[1]);
                    key.setDstIp(str[2]);
                    key.setProtocol(str[3]);
                    keys.add(key);
                }
                bin.close();
                Collections.sort(keys);

                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outPath), "UTF-8");
                BufferedWriter bout = new BufferedWriter(out);

                for (TrafficKey key : keys) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(key.getTime()).append(",").append(key.getSrcIp()).append(",").
                            append(key.getDstIp()).append(",").append(key.getProtocol());
                    bout.write(sb.toString());
                    bout.newLine();
                }
                bout.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void combineFiles2(ConcurrentHashMap<String, String> combineFile) throws IOException {
            for (Map.Entry<String, String> entry : combineFile.entrySet()) {
                ArrayList<File> fileList = new ArrayList<File>();
                getFilePath(fileList, entry.getValue(), "txt");//得到待删除文件

                File key = new File(entry.getKey());//D:/57data/traffic/10.0.0.1.txt
                String name = key.getName();//10.0.0.1.txt
                File comFile = new File(entry.getValue() + File.separator + name);//合并后暂存为D:/57data/traffic/10.0.0.1/10.0.0.1.txt,未排序，从0-1942 0-1942 0-1942
                if (!comFile.exists()) {
                    comFile.createNewFile();
                }
                FileChannel outChannel = new FileOutputStream(comFile).getChannel();
                FileChannel inChannel;
                for (File file : fileList) {
                    inChannel = new FileInputStream(file).getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inChannel.close();
                }
                outChannel.close();
                sortTraffic(comFile.getAbsolutePath(), entry.getKey());//最后生成排序后的txt
            }
        }

        private void combineFiles(ConcurrentHashMap<String, String> combineFile) throws IOException {
            for (Map.Entry<String, String> entry : combineFile.entrySet()) {
                ArrayList<File> fileList = new ArrayList<File>();
                getFilePath(fileList, entry.getValue(), "bin");//得到待删除文件

                File outputFile = new File(entry.getKey());
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                FileChannel outChannel = new FileOutputStream(outputFile).getChannel();
                FileChannel inChannel;
                for (File file : fileList) {
                    inChannel = new FileInputStream(file).getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inChannel.close();
                }
                outChannel.close();
            }
        }

        private int getFilePath(ArrayList<File> fileList, String filePath, String type) {
            int num = 0;
            File ff = new File(filePath);
            if (ff.isFile() && filePath.endsWith(type)) {
                fileList.add(ff);
                num += 1;
            } else if (ff.isDirectory()) {
                File[] files = ff.listFiles();
                for (File f : files) {
                    getFilePath(fileList, f.getAbsolutePath(), type);
                }
            }
            return num;
        }

        private void deleteFile(HashSet<String> fileNameList) {
            for (String fileName : fileNameList) {
                File file = new File(fileName);
                if (file.isDirectory()) {
                    deleteFile(fileName);
                } else {
                    System.out.println("不是文件夹");
                }
            }
        }

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

        public String getExtension(String fileName) {
            return fileName.substring(fileName.lastIndexOf("."));
        }

        public String getName(String fileName) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }

        public void genPart(String fileName, String type) {
            if (!type.equals(".bin")) {
                return;
            }
            if (!nameMap.containsKey(fileName)) {
                nameMap.put(fileName, swapMap.get(task));
            } else {
                nameMap.remove(fileName);
                nameMap.put(fileName, swapMap.get(task));
            }
        }

        public void genPart2(String fileName, String type) {
            if (!type.equals(".txt")) {
                return;
            }
            if (!nameMap2.containsKey(fileName)) {
                nameMap2.put(fileName, swapMap2.get(task2));
            } else {
                nameMap2.remove(fileName);
                nameMap2.put(fileName, swapMap2.get(task2));
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
                    receiveFile(finalFolderPath);//仅文件
                } else if (receiveType.equals("sendFolder")) {
                    subFolder = userClient.receiveMsg();//发送方的selectFolderPath子目录：routesrc或node
                    finalFolderPath = outPath + File.separator + subFolder;
                    //生成子目录
                    File folder = new File(finalFolderPath);
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
            String name;
            String tempName;
            String extension;
            String finalFilePath;
            String filePath;
            String folderPath;
            String subFolder;
            String task2;

            try {
                fileName = userClient.receiveMsg();//得到 文件名.扩展名 10.0.1.1_10.0.1.2.bin
                name = getName(fileName);//得到文件名10.0.1.1_10.0.1.2
                tempName = name + "_temp";
                extension = getExtension(fileName);//得到扩展名bin
                genPart(fileName, extension);//得到路由器号作为part
                //创建文件夹/routesrc/10.0.1.1_10.0.1.2/...
                //生成合并文件map、删除文件list
                if (extension.equals(".bin")) {
                    File folder = new File(outPath + File.separator + tempName);
                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
                    finalFilePath = outPath + File.separator + tempName + File.separator + name + "_part_" + nameMap.get(fileName) + extension;
//                    System.out.println("part: " + nameMap.get(fileName));
                    filePath = outPath + File.separator + fileName;//D:/57data/routesrc/10.0.0.1_10.0.0.2.bin,用于合并文件
                    folderPath = outPath + File.separator + tempName;//D:/57data/routesrc/10.0.0.1_10.0.0.2_temp/文件夹后面加temp
                    //生成第二步的task
                    subFolder = outPath.substring(index) + File.separator + fileName;//routesrc/10.0.0.1_10.0.0.2.bin
                    task2 = subFolder + DELIMITER + name;

                    if (!combineFile.containsKey(filePath)) {
                        combineFile.put(filePath, folderPath);
                        delFile.add(folderPath);//待删除的10.0.0.1_10.0.0.2文件夹里面的所有文件
                        System.out.println("待删除的routesrc： " + folderPath);
                        //生成第二步任务list
                        allTasks2.add(task2);
                        allTasksTags2.put(task2, "n");
                        swapMap2.put(task2, name);//routesrc/10.0.0.1_10.0.0.2.bin, 10.0.0.1_10.0.0.2;用于生成part
                    }
                } else {
                    finalFilePath = outPath + File.separator + fileName;//接收node文件
                }

                //接收文件
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFilePath)));
                length = userClient.receiveInt();
                while (length > 0) {
                    userClient.receiveFullByte(receiveBuffer, 0, length);//read到length才返回，若用read，可能不到length就返回
                    dos.write(receiveBuffer, 0, length);
                    dos.flush();
                    length = userClient.receiveInt();
                }
                System.out.println("接收方结束循环");
                dos.close();
            } catch (IOException e) {
                System.out.println("接收文件报错");
                e.printStackTrace();
            }

        }

        public void receiveResult2(String finalFolderPath) throws IOException {
            totalLen = userClient.receiveLong();
            System.out.println("totalLen: " + totalLen);
            long beginTime = System.currentTimeMillis();
            String subFolder;
            while (true) {
                String receiveType = userClient.receiveMsg();
                if (receiveType.equals("sendFile")) {
                    receiveFile2(finalFolderPath);//仅文件
                } else if (receiveType.equals("sendFolder")) {
                    subFolder = userClient.receiveMsg();//发送方的selectFolderPath子目录：route或traffic
                    finalFolderPath = outPath + File.separator + subFolder;
                    //生成子目录
                    File folder = new File(finalFolderPath);
                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
                } else if (receiveType.equals("endTransmit")) {
                    break;
                }
            }
        }

        private void receiveFile2(String outPath) {
            byte[] receiveBuffer = new byte[BUF_LEN];
            int length;
            long passedlen = 0;
            String fileName;
            String name;
            String tempName;
            String extension;
            String finalFilePath;
            String filePath;
            String folderPath;

            try {
                fileName = userClient.receiveMsg();
//                finalFilePath = outPath + File.separator + fileName;
                name = getName(fileName);//得到文件名
                tempName = name + "_temp";
                extension = getExtension(fileName);//得到扩展名
                genPart2(fileName, extension);//得到part


                if (extension.equals(".txt")) {
                    File folder = new File(outPath + File.separator + tempName);
                    boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
                    finalFilePath = outPath + File.separator + tempName + File.separator + name + "_part_" + nameMap2.get(fileName) + extension;
//                    System.out.println("part: " + nameMap.get(fileName));
                    filePath = outPath + File.separator + fileName;//D:/57data/traffic/10.0.0.1.txt
                    folderPath = outPath + File.separator + tempName;//D:/57data/traffic/10.0.0.1/
                    if (!combineFile2.containsKey(filePath)) {
                        combineFile2.put(filePath, folderPath);
                        delFile2.add(folderPath);//待删除的10.0.0.1-10.0.0.2文件夹们
                    }
                } else {
                    finalFilePath = outPath + File.separator + fileName;
                }

                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFilePath)));
                length = userClient.receiveInt();
                while (length > 0) {
                    userClient.receiveFullByte(receiveBuffer, 0, length);//read到length才返回，若用read，可能不到length就返回
                    dos.write(receiveBuffer, 0, length);
                    dos.flush();
                    length = userClient.receiveInt();
                }
                System.out.println("接收方结束循环");
                dos.close();
            } catch (IOException e) {
                System.out.println("接收文件报错");
                e.printStackTrace();
            }
        }

        private void updateMap(String task) {
            if (allTasksTags.get(task).equals("n")) {
                allTasksTags.remove(task);
                allTasksTags.put(task, "y");//更新标记，表示完成
            }
        }

        private void updateMap2(String task2) {
            if (allTasksTags2.get(task2).equals("n")) {
                allTasksTags2.remove(task2);
                allTasksTags2.put(task2, "y");//更新标记，表示完成
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

    //PcapServer
    public int receiveInt() throws IOException {
        return disWithClient.readInt();
    }

    public long receiveLong() throws IOException {
        return disWithClient.readLong();
    }

    public String receiveMsg() throws IOException {
        return disWithClient.readUTF();
    }

    public void receiveFullByte(byte[] bytes, int off, int len) throws IOException {
        disWithClient.readFully(bytes, off, len);
    }

    //发送文件
    public void sendInt(int len) throws IOException {
        dosWithClient.writeInt(len);
    }

    //发送文件长度
    public void sendLong(long len) throws IOException {
        dosWithClient.writeLong(len);
        dosWithClient.flush();
    }

    public void sendByte(byte[] bytes, int off, int len) throws IOException {
        dosWithClient.write(bytes, off, len);
        dosWithClient.flush();
    }

    public void sendTask(String task) throws IOException {
        dosWithClient.writeUTF(task);
        dosWithClient.flush();
    }

    public void sendMsg(String str) throws IOException {
        dosWithClient.writeUTF(str);
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

