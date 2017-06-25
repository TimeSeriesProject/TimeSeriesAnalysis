package cn.InstFS.wkr.NetworkMining.UIs;

import Distributed.*;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AlgorithmsManager;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.*;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPath;
import cn.InstFS.wkr.NetworkMining.Params.ParamsAPI;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.TaskProgressBar;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.TaskProgress;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.TaskProgressDis;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.*;
import common.Logger;
import org.apache.commons.logging.Log;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/20.
 */
public class win10_window extends JFrame {
    public  static  int choosenState=0;
    Container ct;
    //创建背景面板。
    BackgroundPanel bgp;
    HashMap<String,HashMap<TaskCombination, MinerNodeResults>> networkStructureresultMaps = new HashMap<String,HashMap<TaskCombination, MinerNodeResults>>();
    HashMap<String,HashMap<TaskCombination, MinerNodeResults>> singleNoderesultMaps = new HashMap<String,HashMap<TaskCombination, MinerNodeResults>>();
    HashMap<String,HashMap<TaskCombination, MinerNodeResults>> nodePairresultMaps = new HashMap<String,HashMap<TaskCombination, MinerNodeResults>>();
    HashMap<String, HashMap<TaskCombination, MinerResultsPath>> pathResultsMaps= new HashMap<String, HashMap<TaskCombination,MinerResultsPath>>();
    //    HashMap<String, HashMap<TaskCombination, MinerProtocolResults>> protocolResultsMaps = new HashMap<>();
    HashMap<TaskCombination, MinerProtocolResults> protocolResultsMaps = new HashMap<>();
    boolean isNetworkStructureMined=false;
    boolean isSingleNodeMined=false;
    boolean isNodePairMined = false;
    boolean isPathMined = false;
    boolean isProtocolAssMined = false;
    boolean isDisNetworkStructureMined=false;
    boolean isDisSingleNodeMined=false;
    boolean isDisNodePairMined = false;
    boolean isDisPathMined = false;
    boolean isDisProtocolAssMined = false;
    boolean isDistributed = false;
//    static Server Server.getInstance() = Server.getInstance();

    public boolean isNetworkStructureMined() {
        return isNetworkStructureMined;
    }
    public void setNetworkStructureMined(boolean isNetworkStructureMined) {
        this.isNetworkStructureMined = isNetworkStructureMined;
    }
    public boolean isSingleNodeMined() {
        return isSingleNodeMined;
    }
    public void setSingleNodeMined(boolean isSingleNodeMined) {
        this.isSingleNodeMined = isSingleNodeMined;
    }
    public boolean isNodePairMined() {
        return isNodePairMined;
    }
    public void setNodePairMined(boolean isNodePairMined) {
        this.isNodePairMined = isNodePairMined;
    }

    public void distribute() {
        Logger.log("分布式启动");
//        if (!isDistributed) {
//            System.out.println("启动分布式");
//            server = Server.getInstance();
//            new Thread(Server.getStartInstance()).start();
//            isDistributed = true;
//        } else {
//            System.out.println("分布式已启动");
//        }
        System.out.println("dis: " + isDistributed);
        DisPanel disPanel = new DisPanel(this,"分布式设置", isDistributed);
        disPanel.setVisible(true);
        isDistributed = disPanel.getIsDis();
    }

    public void single() {
        Logger.log("启动单机版");
        System.out.println("启动单机版");
        Server.closeServer();//服务关闭
        isDistributed = false;
    }

    public void genDialog(TaskPanel taskPanel) {
        JDialog jDialog = new JDialog(this, "任务挖掘进度",true);
        jDialog.setContentPane(taskPanel);
        jDialog.setSize(new Dimension(500, 400));
        jDialog.setVisible(true);

    }

    public void mineNetworkStructor()
    {
        networkStructureresultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        final NetworkFactory networkFactory = NetworkFactory.getInstance();
        final List<MiningObject> miningObjectList = networkFactory.getMiningObjectsChecked();
        final JComponent newContentPane = new TaskProgressBar();
        newContentPane.setOpaque(true);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (MiningObject ob: miningObjectList) {
                            networkFactory.reset();
                            networkFactory.setMiningObject(ob);
                            networkFactory.detect();
                        }
                        TaskProgressBar bar = (TaskProgressBar) newContentPane;
                        bar.startTask();

                        for (MiningObject ob: miningObjectList) {
                            TaskProgress.getInstance().setPhase(ob.toString());
                            HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
                            objectMap = NetworkMinerFactory.getInstance().startAllNetworkStructrueMiners(ob);
                            networkStructureresultMaps.put(ob.toString(), objectMap);
                        }
                        TaskProgress.getInstance().clear();
                        WholeNetworkFrame wholeNetworkFrame = new WholeNetworkFrame(networkStructureresultMaps);
                        wholeNetworkFrame.setVisible(true);
                    }
                };
                new Thread(runnable).start();
            }
        });

        JDialog dialog = new JDialog(this, "网络结构任务挖掘进度", true);
        dialog.setContentPane(newContentPane);
        dialog.pack();
        dialog.setVisible(true);

        isNetworkStructureMined=true;
    }

    public void mineNetworkStructorDis(String str)
    {
        ArrayList<String> list = new ArrayList<String>();//统计任务总个数
        networkStructureresultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        NetworkFactoryDis networkFactoryDis = NetworkFactoryDis.getInstance();
        List<MiningObject> miningObjectList = networkFactoryDis.getMiningObjectsChecked();

        //得到任务总数
        for (MiningObject ob : miningObjectList) {
            Server.getInstance().getNetCount(networkFactoryDis, ob, list);
        }
        //先清空结果文件
        Server.getInstance().clearNetResult();
        //初始化进度条
        Server.getInstance().initBar();
        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            TaskProgressDis.getInstance().setPhase(str + ob.toString());
            Server.getInstance().network(networkFactoryDis, ob);
        }

        isDisNetworkStructureMined=true;
    }

    public void mineSingleNode()
    {
        long a = System.currentTimeMillis();
        singleNoderesultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        final SingleNodeOrNodePairMinerFactory singleNodeMinerFactory = SingleNodeOrNodePairMinerFactory.getInstance();
        final List<MiningObject> miningObjectList = singleNodeMinerFactory.getMiningObjectsChecked();

       /* JFrame frame = new JFrame("任务挖掘进度");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);*/

        final JComponent newContentPane = new TaskProgressBar();
        newContentPane.setOpaque(true);



        /*frame.setContentPane(newContentPane);
        frame.pack();
        frame.setVisible(true);*/

        // detect生成taskCombination及CombinationMiner
        /*for (MiningObject ob: miningObjectList) {
            singleNodeMinerFactory.reset();
            singleNodeMinerFactory.setMiningObject(ob);
            singleNodeMinerFactory.detect();
        }
        TaskProgressBar bar = (TaskProgressBar) newContentPane;
        bar.startTask();*/

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Logger.log("===============================================================");
                        Logger.log("节点任务挖掘启动");
                        for (MiningObject ob: miningObjectList) {
                            singleNodeMinerFactory.reset();
                            singleNodeMinerFactory.setMiningObject(ob);
                            singleNodeMinerFactory.detect();
                        }
                        TaskProgressBar bar = (TaskProgressBar) newContentPane;
                        bar.startTask();

                        for (MiningObject ob: miningObjectList) {
                            TaskProgress.getInstance().setPhase(ob.toString());
                            HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
                            objectMap = NetworkMinerFactory.getInstance().startAllNodeMiners(ob);
                            singleNoderesultMaps.put(ob.toString(), objectMap);
                        }
                        TaskProgress.getInstance().clear();
                        SingleNodeListFrame singleNodeListFrame = new SingleNodeListFrame(singleNoderesultMaps);
                        singleNodeListFrame.setVisible(true);
                    }
                };
                new Thread(runnable).start();
            }
        });

        JDialog dialog = new JDialog(this, "节点任务挖掘进度", true);
        dialog.setContentPane(newContentPane);
        dialog.pack();
        dialog.setVisible(true);


        isSingleNodeMined=true;
        long b = System.currentTimeMillis();
        System.out.println("单机版单节点挖掘完毕，用时：" + (b - a) / 1000);

    }

    public void mineSingleNodeDis(String str)
    {
        long a = System.currentTimeMillis();
        ArrayList<String> list = new ArrayList<String>();//统计任务总个数singleNoderesultMaps.clear();
        singleNoderesultMaps.clear();

        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        SingleNodeOrNodePairMinerFactoryDis singleNodeOrNodePairMinerFactoryDis = SingleNodeOrNodePairMinerFactoryDis.getInstance();
        List<MiningObject> miningObjectList = singleNodeOrNodePairMinerFactoryDis.getMiningObjectsChecked();

        //得到任务总数
        for (MiningObject ob : miningObjectList) {
            Server.getInstance().getSingleOrPairCount(singleNodeOrNodePairMinerFactoryDis, ob, list);
        }
        //先清空结果文件
        Server.getInstance().clearSingleResult();
        //初始化进度条
        Server.getInstance().initBar();
        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            TaskProgressDis.getInstance().setPhase(str + ob.toString());
            Server.getInstance().SingleNodeOrNodePair(singleNodeOrNodePairMinerFactoryDis, ob);
        }

        isDisSingleNodeMined=true;
        long b = System.currentTimeMillis();
        System.out.println("分布式单节点挖掘完毕，用时：" + (b - a) / 1000);
    }

    public void mineNodePair()
    {
        nodePairresultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        final SingleNodeOrNodePairMinerFactory nodePairMinerFactory = SingleNodeOrNodePairMinerFactory.getPairInstance();
        final List<MiningObject> miningObjectList = nodePairMinerFactory.getMiningObjectsChecked();
        final JComponent newContentPane = new TaskProgressBar();
        newContentPane.setOpaque(true);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (MiningObject ob: miningObjectList) {
                            nodePairMinerFactory.reset();
                            nodePairMinerFactory.setMiningObject(ob);
                            nodePairMinerFactory.detect();
                        }
                        TaskProgressBar bar = (TaskProgressBar) newContentPane;
                        bar.startTask();

                        for (MiningObject ob: miningObjectList) {
                            TaskProgress.getInstance().setPhase(ob.toString());
                            HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
                            objectMap = NetworkMinerFactory.getInstance().startAllNodeMiners(ob);
                            nodePairresultMaps.put(ob.toString(), objectMap);
                        }
                        TaskProgress.getInstance().clear();
                        NodePairListFrame nodePairListFrame = new NodePairListFrame(nodePairresultMaps);
                        nodePairListFrame.setVisible(true);
                    }
                };
                new Thread(runnable).start();
            }
        });

        JDialog dialog = new JDialog(this, "链路任务挖掘进度", true);
        dialog.setContentPane(newContentPane);
        dialog.pack();
        dialog.setVisible(true);

        isNodePairMined=true;
    }

    public void mineNodePairDis(String str)
    {
        ArrayList<String> list = new ArrayList<String>();//统计任务总个数
        nodePairresultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        SingleNodeOrNodePairMinerFactoryDis singleNodeOrNodePairMinerFactoryDis = SingleNodeOrNodePairMinerFactoryDis.getPairInstance();
        singleNodeOrNodePairMinerFactoryDis.setTaskRange(TaskRange.NodePairRange);
        List<MiningObject> miningObjectList = singleNodeOrNodePairMinerFactoryDis.getMiningObjectsChecked();

        //得到任务总数
        for (MiningObject ob : miningObjectList) {
            Server.getInstance().getSingleOrPairCount(singleNodeOrNodePairMinerFactoryDis, ob, list);
        }
        //先清空结果文件
        Server.getInstance().clearPairResult();
        //初始化进度条
        Server.getInstance().initBar();
        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            TaskProgressDis.getInstance().setPhase(str + ob.toString());
            Server.getInstance().SingleNodeOrNodePair(singleNodeOrNodePairMinerFactoryDis, ob);
        }

        isDisNodePairMined=true;
    }

    public void mineProtocolAss() {
        protocolResultsMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        final ProtocolAssMinerFactory protocolAssMinerFactory = ProtocolAssMinerFactory.getInstance();
        final List<MiningObject> miningObjectList = protocolAssMinerFactory.getMiningObjectsChecked();
        final JComponent newContentPane = new TaskProgressBar();
        newContentPane.setOpaque(true);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (MiningObject ob: miningObjectList) {
                            protocolAssMinerFactory.reset();
                            protocolAssMinerFactory.setMiningObject(ob);
                            protocolAssMinerFactory.detect();
                        }
                        TaskProgressBar bar = (TaskProgressBar) newContentPane;
                        bar.startTask();

                        for (MiningObject ob: miningObjectList) {
                            TaskProgress.getInstance().setPhase(ob.toString());
                            HashMap<TaskCombination, MinerProtocolResults> objectMap = new HashMap<>();
                            objectMap = NetworkMinerFactory.getInstance().startAllProtocolMiners(ob);
                            protocolResultsMaps = objectMap;
                        }
                        TaskProgress.getInstance().clear();
                        AssociationIpListFrame frame = new AssociationIpListFrame(protocolResultsMaps);
                        frame.setVisible(true);
                    }
                };
                new Thread(runnable).start();
            }
        });

        JDialog dialog = new JDialog(this, "多业务任务挖掘进度", true);
        dialog.setContentPane(newContentPane);
        dialog.pack();
        dialog.setVisible(true);

        isProtocolAssMined=true;
    }

    public void mineProtocolAssDis(String str) {
        ArrayList<String> list = new ArrayList<String>();//统计任务总个数
        protocolResultsMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        ProtocolAssMinerFactoryDis protocolAssMinerFactoryDis = ProtocolAssMinerFactoryDis.getInstance();
        List<MiningObject> miningObjectList = protocolAssMinerFactoryDis.getMiningObjectsChecked();
        //得到任务总数
        for (MiningObject ob : miningObjectList) {
            Server.getInstance().getProCount(protocolAssMinerFactoryDis, ob, list);
        }
        //先清空结果文件
        Server.getInstance().clearProResult();
        //初始化进度条
        Server.getInstance().initBar();
        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            TaskProgressDis.getInstance().setPhase(str + ob.toString());
            Server.getInstance().protocol(protocolAssMinerFactoryDis, ob);
        }

        isDisProtocolAssMined=true;
    }

    private void settingsWholeNetwork() {
        Logger.log("网络结构规律挖掘配置");
        NetworkMinerFactory.getInstance();
        if (!isDistributed) {
            DialogSettings dialog = new DialogSettings(this, NetworkFactory.getInstance(), "网络结构规律挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        } else {
            DialogSettings dialog = new DialogSettings(this, NetworkFactoryDis.getInstance(), "网络结构规律挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    private void settingsPath() {
        Logger.log("承载路径规律挖掘配置");
        NetworkMinerFactory.getInstance();
        if (!isDistributed) {
            DialogSettings dialog = new DialogSettings(this, PathMinerFactory.getInstance(), "承载路径规律挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        } else {
            DialogSettings dialog = new DialogSettings(this, PathMinerFactoryDis.getInstance(), "承载路径规律挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    private void settingsSingleNode() {
        Logger.log("节点规律挖掘配置");
        NetworkMinerFactory.getInstance();
        if (!isDistributed) {
            SingleNodeOrNodePairMinerFactory.getInstance().setTaskRange(TaskRange.SingleNodeRange);
//            DialogSettings dialog = new DialogSettings(SingleNodeOrNodePairMinerFactory.getInstance(),"节点规律挖掘配置");
            DialogSettings dialog = new DialogSettings(this, SingleNodeOrNodePairMinerFactory.getInstance(), "结点");
        /*DialogSettingTest dialog = new DialogSettingTest("节点");*/
            dialog.pack();
            dialog.setVisible(true);
        } else {
            SingleNodeOrNodePairMinerFactoryDis.getInstance().setTaskRange(TaskRange.SingleNodeRange);
//            DialogSettings dialog = new DialogSettings(SingleNodeOrNodePairMinerFactory.getInstance(),"节点规律挖掘配置");
            DialogSettings dialog = new DialogSettings(this, SingleNodeOrNodePairMinerFactoryDis.getInstance(), "结点");
        /*DialogSettingTest dialog = new DialogSettingTest("节点");*/
            dialog.pack();
            dialog.setVisible(true);
        }

        /*JFrame jf = new JFrame();
        DateTimePicker date = new DateTimePicker();
        JPanel jp = date.getPane();
        jf.add(jp);
//        date.getDateTime();
        jf.setVisible(true);*/
    }

    private void settingsNodePair() {
        Logger.log("链路规律挖掘配置");
        NetworkMinerFactory.getInstance();
        if (!isDistributed) {
            DialogSettings dialog = new DialogSettings(this, SingleNodeOrNodePairMinerFactory.getPairInstance(), "链路规律挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        } else {
            DialogSettings dialog = new DialogSettings(this, SingleNodeOrNodePairMinerFactoryDis.getPairInstance(),"链路规律挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        }

    }

    private void settingsProtocolAss() {
        Logger.log("多业务关联挖掘配置");
        NetworkMinerFactory.getInstance();
        if (!isDistributed) {
            DialogSettings dialog = new DialogSettings(this, ProtocolAssMinerFactory.getInstance(), "多业务关联挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        } else {
            DialogSettings dialog = new DialogSettings(this, ProtocolAssMinerFactoryDis.getInstance(), "多业务关联挖掘配置");
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    public void minePath() {
        pathResultsMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        final PathMinerFactory pathMinerFactory = PathMinerFactory.getInstance();
        final List<MiningObject> miningObjectList = pathMinerFactory.getMiningObjectsChecked();
        final JComponent newContentPane = new TaskProgressBar();
        newContentPane.setOpaque(true);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (MiningObject ob: miningObjectList) {
                            pathMinerFactory.reset();
                            pathMinerFactory.setMiningObject(ob);
                            pathMinerFactory.detect();
                        }
                        TaskProgressBar bar = (TaskProgressBar) newContentPane;
                        bar.startTask();

                        for (MiningObject ob: miningObjectList) {
                            TaskProgress.getInstance().setPhase(ob.toString());
                            HashMap<TaskCombination, MinerResultsPath> objectMap = new HashMap<>();;
                            objectMap = NetworkMinerFactory.getInstance().startAllPathMiners(ob);
                            pathResultsMaps.put(ob.toString(), objectMap);
                        }
                        TaskProgress.getInstance().clear();
                        PathListFrame pathListFrame = new PathListFrame(pathResultsMaps);
                        pathListFrame.setVisible(true);
                    }
                };
                new Thread(runnable).start();
            }
        });

        JDialog dialog = new JDialog(this, "路径任务挖掘进度", true);
        dialog.setContentPane(newContentPane);
        dialog.pack();
        dialog.setVisible(true);

        isPathMined = true;
    }

    public void minePathDis(String str) {
        ArrayList<String> list = new ArrayList<String>();//统计任务总个数
        pathResultsMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        PathMinerFactoryDis pathMinerFactoryDis = PathMinerFactoryDis.getInstance();
        List<MiningObject> miningObjectList = pathMinerFactoryDis.getMiningObjectsChecked();
        //得到任务总数
        for (MiningObject ob : miningObjectList) {
            Server.getInstance().getPathCount(pathMinerFactoryDis, ob, list);
        }
        //先清空结果文件
        Server.getInstance().clearPathResult();
        //初始化进度条
        Server.getInstance().initBar();
        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            TaskProgressDis.getInstance().setPhase(str + ob.toString());
            Server.getInstance().path(pathMinerFactoryDis, ob);
        }

        isDisPathMined = true;
    }

    public void mineAll() {
        final JComponent newContentPane = new TaskProgressBar();
        newContentPane.setOpaque(true);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        long a = System.currentTimeMillis();
                        List<MiningObject> miningObjectList;
                        TaskProgressBar bar = (TaskProgressBar) newContentPane;
                        /* 网络结构任务 */
                        if (!isNetworkStructureMined) {
                            bar.clearBar();
                            bar.setBarString("网络结构：读取数据...");
                            networkStructureresultMaps.clear();
                            NetworkMinerFactory.getInstance().allCombinationMiners.clear();

                            final NetworkFactory networkFactory = NetworkFactory.getInstance();
                            miningObjectList = networkFactory.getMiningObjectsChecked();

                            for (MiningObject ob: miningObjectList) {
                                networkFactory.reset();
                                networkFactory.setMiningObject(ob);
                                networkFactory.detect();
                            }
                            bar.startTask();

                            for (MiningObject ob: miningObjectList) {
                                TaskProgress.getInstance().setPhase("网络结构： "+ob.toString());
                                HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
                                objectMap = NetworkMinerFactory.getInstance().startAllNetworkStructrueMiners(ob);
                                networkStructureresultMaps.put(ob.toString(), objectMap);
                            }
                            TaskProgress.getInstance().clear();
                            isNetworkStructureMined=true;
                        }

                        /* 路径任务 */
                        if (!isPathMined) {
                            bar.clearBar();
                            bar.setBarString("承载路径：读取数据...");
                            pathResultsMaps.clear();
                            NetworkMinerFactory.getInstance().allCombinationMiners.clear();
                            final PathMinerFactory pathMinerFactory = PathMinerFactory.getInstance();
                            miningObjectList = pathMinerFactory.getMiningObjectsChecked();

                            for (MiningObject ob: miningObjectList) {
                                pathMinerFactory.reset();
                                pathMinerFactory.setMiningObject(ob);
                                pathMinerFactory.detect();
                            }
                            bar.startTask();

                            for (MiningObject ob: miningObjectList) {
                                TaskProgress.getInstance().setPhase("路径： "+ob.toString());
                                HashMap<TaskCombination, MinerResultsPath> objectMap = new HashMap<>();;
                                objectMap = NetworkMinerFactory.getInstance().startAllPathMiners(ob);
                                pathResultsMaps.put(ob.toString(), objectMap);
                            }
                            TaskProgress.getInstance().clear();
                            isPathMined = true;
                        }

                        /* 节点任务挖掘 */
                        if (!isSingleNodeMined) {
                            bar.clearBar();
                            bar.setBarString("节点：读取数据...");
                            singleNoderesultMaps.clear();
                            NetworkMinerFactory.getInstance().allCombinationMiners.clear();

                            final SingleNodeOrNodePairMinerFactory singleNodeMinerFactory = SingleNodeOrNodePairMinerFactory.getInstance();
                            miningObjectList = singleNodeMinerFactory.getMiningObjectsChecked();

                            for (MiningObject ob: miningObjectList) {
                                singleNodeMinerFactory.reset();
                                singleNodeMinerFactory.setMiningObject(ob);
                                singleNodeMinerFactory.detect();
                            }
                            bar.startTask();

                            for (MiningObject ob: miningObjectList) {
                                TaskProgress.getInstance().setPhase("节点： "+ob.toString());
                                HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
                                objectMap = NetworkMinerFactory.getInstance().startAllNodeMiners(ob);
                                singleNoderesultMaps.put(ob.toString(), objectMap);
                            }
                            TaskProgress.getInstance().clear();
                            isSingleNodeMined = true;
                        }

                        /* 链路任务 */
                        if (!isNodePairMined) {
                            bar.clearBar();
                            bar.setBarString("链路：读取数据...");
                            nodePairresultMaps.clear();
                            NetworkMinerFactory.getInstance().allCombinationMiners.clear();
                            final SingleNodeOrNodePairMinerFactory nodePairMinerFactory = SingleNodeOrNodePairMinerFactory.getPairInstance();
                            miningObjectList = nodePairMinerFactory.getMiningObjectsChecked();

                            for (MiningObject ob: miningObjectList) {
                                nodePairMinerFactory.reset();
                                nodePairMinerFactory.setMiningObject(ob);
                                nodePairMinerFactory.detect();
                            }
                            bar.startTask();

                            for (MiningObject ob: miningObjectList) {
                                TaskProgress.getInstance().setPhase("链路： "+ob.toString());
                                HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
                                objectMap = NetworkMinerFactory.getInstance().startAllNodeMiners(ob);
                                nodePairresultMaps.put(ob.toString(), objectMap);
                            }
                            TaskProgress.getInstance().clear();
                            isNodePairMined = true;
                        }

                        /* 多业务 */
                        if (!isProtocolAssMined) {
                            bar.clearBar();
                            bar.setBarString("多业务：读取数据...");
                            protocolResultsMaps.clear();
                            NetworkMinerFactory.getInstance().allCombinationMiners.clear();
                            final ProtocolAssMinerFactory protocolAssMinerFactory = ProtocolAssMinerFactory.getInstance();
                            miningObjectList = protocolAssMinerFactory.getMiningObjectsChecked();

                            for (MiningObject ob: miningObjectList) {
                                protocolAssMinerFactory.reset();
                                protocolAssMinerFactory.setMiningObject(ob);
                                protocolAssMinerFactory.detect();
                            }
                            bar.startTask();

                            for (MiningObject ob: miningObjectList) {
                                TaskProgress.getInstance().setPhase("多业务： "+ob.toString());
                                HashMap<TaskCombination, MinerProtocolResults> objectMap = new HashMap<>();
                                objectMap = NetworkMinerFactory.getInstance().startAllProtocolMiners(ob);
                                protocolResultsMaps = objectMap;
                            }
                            TaskProgress.getInstance().clear();
                            isProtocolAssMined = true;
                        }
                        long b = System.currentTimeMillis();
                        System.out.println("总时间 " + (b - a));
                    }
                };
                new Thread(runnable).start();
            }
        });

        if (isNetworkStructureMined && isPathMined && isSingleNodeMined && isNodePairMined && isProtocolAssMined) {
            JOptionPane.showMessageDialog(null,"全部挖掘完毕","提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JDialog dialog = new JDialog(this, "全部任务挖掘进度", true);
            dialog.setContentPane(newContentPane);
            dialog.pack();
            dialog.setVisible(true);
        }

    }

    public void mineAllDis() {
        final TaskPanel taskPanel = new TaskPanel();
        taskPanel.setOpaque(true);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        long a = System.currentTimeMillis();
                        /* 网络结构任务 */
                        if (!isDisNetworkStructureMined) {
                            mineNetworkStructorDis("网络结构： ");
                        }

                        /* 路径任务 */
                        if (!isDisPathMined) {
                            minePathDis("路径： ");
                        }

                        /* 节点任务挖掘 */
                        if (!isDisSingleNodeMined) {
                            mineSingleNodeDis("节点： ");
                        }

                        /* 链路任务 */
                        if (!isDisNodePairMined) {
                            mineNodePairDis("链路： ");
                        }

                        /* 多业务 */
                        if (!isDisProtocolAssMined) {
                            mineProtocolAssDis("多业务： ");
                        }
                        long b = System.currentTimeMillis();
                        System.out.println("总时间 " + (b - a));
                    }
                };
                new Thread(runnable).start();
            }
        });

        if (isDisNetworkStructureMined && isDisPathMined && isDisSingleNodeMined && isDisNodePairMined && isDisProtocolAssMined) {
            JOptionPane.showMessageDialog(null,"全部挖掘完毕","提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JDialog dialog = new JDialog(this, "全部任务挖掘进度", true);
            dialog.setContentPane(taskPanel);
            dialog.setSize(new Dimension(500, 400));
            dialog.setVisible(true);
        }

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
//					UIManager.setLookAndFeel( new  SubstanceBusinessBlackSteelLookAndFeel());
//					JFrame.setDefaultLookAndFeelDecorated(true);
//					JDialog.setDefaultLookAndFeelDecorated(true);
                    win10_window frame = new win10_window();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public win10_window() {
        init();
//        this.setBounds(0,0,1000,1080);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        this.setResizable(false);

//        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    }
    public void init()
    {
        //没有任务时初始化

        Logger.log("系统启动");
        JFrame.setDefaultLookAndFeelDecorated(true);
        //设置主题
//         SubstanceLookAndFeel.setCurrentTheme(new SubstanceBottleGreenTheme());
        //设置按钮外观
//         SubstanceLookAndFeel.setSkin(new NebulaBrickWallSkin());
//         SubstanceLookAndFeel.setCurrentButtonShaper(new  org.jvnet.substance.button.ClassicButtonShaper());
//         //设置水印
        // SubstanceLookAndFeel.setCurrentWatermark(new SubstanceBinaryWatermark());
//         //设置边框

//         SubstanceSkin skin = new SaharaSkin().withWatermark(watermark); //初始化有水印的皮肤

//         UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
//         SubstanceLookAndFeel.setSkin(skin); //设置皮肤


//         SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());
//         //设置渐变渲染
//         SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());
//         //设置标题
//         SubstanceLookAndFeel.setCurrentTitlePainter( new MatteHeaderPainter());
        try {
//			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();

            UIManager.setLookAndFeel( new SubstanceBusinessBlackSteelLookAndFeel());
            JFrame.setDefaultLookAndFeelDecorated(true);

            SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());
            //设置渐变渲染
            SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());
            //设置标题
            SubstanceLookAndFeel.setCurrentTitlePainter( new MatteHeaderPainter());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        setTitle("网络规律挖掘模拟器");


        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setLayout(null);
        ct=this.getContentPane();
        this.setLayout(null);
        final JButton bNetwork=new JButton("网络结构");
        bNetwork.setBackground( new Color(102,205,170));
        bNetwork.setBounds(100,20,200,200);
        setIcon("img\\deploy_network_Icon.png",bNetwork);
        bNetwork.setMargin(new Insets(0, 0, 0, 0));
//        bNetwork.setEnabled(false);

        final JButton bNetLoad=new JButton("承载路径");
        bNetLoad.setBackground( new Color(102,205,170));
        bNetLoad.setBounds(100,230,200,200);
        setIcon("img\\deploy_netRoad_Icon.png",bNetLoad);
//        bNetLoad.setEnabled(false);

        final JButton bNode=new JButton("节点");
        bNode.setBackground(new Color(102, 205, 170));
        bNode.setBounds(100,440,200,200);
        setIcon("img\\deploy_node_Icon.png",bNode);
//        bNode.setEnabled(false);

        final JButton blinkRoad=new JButton("链路");
        blinkRoad.setBounds(100,650,200,200);
        setIcon("img\\deploy_linkRoad_Icon.png",blinkRoad);
//        blinkRoad.setEnabled(false);

        final JButton bMultiServer=new JButton("多业务");
//        bMultiServer.setBackground( new Color(102,205,170));
        bMultiServer.setBounds(310,650,200,200);
        setIcon("img\\deploy_mul_server_Icon.png",bMultiServer);
//        bMultiServer.setEnabled(false);

        //中间一列
        final JButton bPcap=new JButton("PCAP 解析");
        bPcap.setBackground( new Color(178,234,34));
        bPcap.setBounds(310,20,410,200);
        setIcon("img\\file.png",bPcap);

        final JButton bAll=new JButton("挖掘全部");
        bAll.setBackground(Color.YELLOW);
        bAll.setBounds(310,230,410,200);
        setIcon("img\\MinAll.png",bAll);
//        final JButton bSetting=new JButton("设置");
//        bSetting.setBackground(Color.orange);
//        bSetting.setBounds(138,824,200,100);

        final JButton bSingle = new JButton("单机版");
        bSingle.setBounds(310,440, 200, 200);
        setIcon("img\\standalone_version_IconG.png", bSingle);

        final JButton bDis = new JButton("分布式");
        bDis.setBounds(520, 440, 200, 200);
        setIcon("img\\distributed_version_Icon.png", bDis);

        //最后一列
        final JButton bMinNet=new JButton("挖掘网络结构");
        bMinNet.setBounds(730,20,200,200);
        setIcon("img\\dig_network_Icon.png",bMinNet);
        final JButton bMinNetLoad=new JButton("挖掘承载路径");
        bMinNetLoad.setBackground( new Color(102,205,170));
        bMinNetLoad.setBounds(730,230,200,200);
        setIcon("img\\dig_netRoad_Icon.png",bMinNetLoad);

        final JButton bMinNode=new JButton("挖掘节点");
        bMinNode.setBackground(new Color(102, 205, 170));
        bMinNode.setBounds(730,440,200,200);
        setIcon("img\\dig_node_Icon.png",bMinNode);

        final JButton bMinlinkRoad=new JButton("挖掘链路");
        bMinlinkRoad.setBounds(730,650,200,200);
        setIcon("img\\dig_linkRoad_Icon.png",bMinlinkRoad);

        final JButton bMinMultiServer=new JButton("挖掘多业务");
        bMinMultiServer.setBackground( new Color(102,205,170));
        bMinMultiServer.setBounds(520,650,200,200);
        setIcon("img\\dig_mul_server_Icon.png",bMinMultiServer);

        final JButton bReloadParams = new JButton("重加载算法参数");
        bReloadParams.setBounds(940,230,410,200);
//        bgp=new BackgroundPanel((new ImageIcon("img\\background.png")).getImage());
//        bgp.setBounds(0,0,1920,1080);
        ct.add(bNetwork);
        ct.add(bAll);
        ct.add(blinkRoad);
        ct.add(bMinlinkRoad);
        ct.add(bMinMultiServer);
        ct.add(bMinNet);
        ct.add(bMinNetLoad);
        ct.add(bMinNode);
        ct.add(bMultiServer);
        ct.add(bNetLoad);
        ct.add(bNode);
        ct.add(bPcap);
        ct.add(bDis);
        ct.add(bSingle);
        ct.add(bReloadParams);
//        ct.add(bgp);
        ct.setBackground(new Color(0,90,171));

        //设置边框
        bNetwork.setMargin(new Insets(0, 0, 0, 0));
        bAll.setMargin(new Insets(0, 0, 0, 0));
        blinkRoad.setMargin(new Insets(0, 0, 0, 0));
        bMinlinkRoad.setMargin(new Insets(0, 0, 0, 0));
        bMinMultiServer.setMargin(new Insets(0, 0, 0, 0));
        bMinNet.setMargin(new Insets(0, 0, 0, 0));
        bMinNetLoad.setMargin(new Insets(0, 0, 0, 0));
        bMinNode.setMargin(new Insets(0, 0, 0, 0));
        bMultiServer.setMargin(new Insets(0, 0, 0, 0));
        bNetLoad.setMargin(new Insets(0, 0, 0, 0));
        bNode.setMargin(new Insets(0, 0, 0, 0));
        bPcap.setMargin(new Insets(0, 0, 0, 0));
        bSingle.setMargin(new Insets(0, 0, 0, 0));
        bDis.setMargin(new Insets(0, 0, 0, 0));
//        bNetwork.setMargin(new Insets(0, 0, 0, 0));


        bReloadParams.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isNetworkStructureMined=false;
                isSingleNodeMined=false;
                isNodePairMined = false;
                isPathMined = false;
                isProtocolAssMined = false;
                isDisNetworkStructureMined=false;
                isDisSingleNodeMined=false;
                isDisNodePairMined = false;
                isDisPathMined = false;
                isDisProtocolAssMined = false;
                isDistributed = false;

                ParamsAPI.getInstance().resetAllParams();
                AlgorithmsManager.getInstance().resetAlgorithmsChooser();
            }
        });
        //单机版按钮响应
        bSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                single();
                setIcon("img\\standalone_version_IconG.png", bSingle);
                setIcon("img\\distributed_version_Icon.png", bDis);
            }
        });

        //分布式按钮响应
        bDis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                distribute();
                if (isDistributed) {
                    setIcon("img\\standalone_version_Icon.png", bSingle);
                    setIcon("img\\distributed_version_IconG.png", bDis);
                }
            }
        });

        //显示网络结构按钮响应
        bNetwork.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                settingsWholeNetwork();
                if (NetworkFactory.getInstance().isModified())
                    isNetworkStructureMined = false;
                if (NetworkFactoryDis.getInstance().isModified())
                    isDisNetworkStructureMined = false;
            	/*if(isNetworkStructureMined==false)
            		mineNetworkStructor();
            	WholeNetworkFrame wholeNetworkFrame = new WholeNetworkFrame(networkStructureresultMaps);
            	wholeNetworkFrame.setVisible(true);*/
            }
        });
        //Pcap 解析响应
        bPcap.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDistributed) {
                    ProcessBarShow processBarShow = new ProcessBarShow(win10_window.this);
                } else if (isDistributed) {
//                    PcapFrame pcapFrame = new PcapFrame();
//                    pcapFrame.init();
                    final PcapPanel pcapPanel = new PcapPanel();
                    JDialog jDialog = new JDialog(win10_window.this, "pcap解析",true);
                    jDialog.setContentPane(pcapPanel);
                    jDialog.setSize(new Dimension(500, 400));
                    jDialog.setVisible(true);
                }
            }
        });
        //显示承载路径设置响应
        bNetLoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                settingsPath();
                if (PathMinerFactory.getInstance().isModified())
                    isPathMined = false;
                if (PathMinerFactoryDis.getInstance().isModified())
                    isDisPathMined = false;

            }
        });			//设置按钮响应
//        bSetting.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // TODO Auto-generated method stub
//                if(choosenState==0)
//                    bSetting.setEnabled(false);
//                JFrame jf=new JFrame();
//                DialogSettingTask dialogSettingTask=new DialogSettingTask(jf);
//                dialogSettingTask.setVisible(true);
//
//
//
//            }
//        });
        //显示节点规律设置响应
        bNode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                settingsSingleNode();
                if (SingleNodeOrNodePairMinerFactory.getInstance().isModified())
                    isSingleNodeMined = false;

                if (SingleNodeOrNodePairMinerFactoryDis.getInstance().isModified()) {
                    isDisSingleNodeMined = false;
                }

                /*JFrame jf=new JFrame();
                DialogSettingTask dialogSettingTask=new DialogSettingTask(jf);
                dialogSettingTask.setVisible(true);*/

            	/*if(isSingleNodeMined==false)
            		mineSingleNode();
            	SingleNodeListFrame singleNodeListFrame = new SingleNodeListFrame(singleNoderesultMaps);
            	singleNodeListFrame.setVisible(true);*/
            }
        });


        //显示多业务按钮响应
        bMultiServer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                settingsProtocolAss();
                if (ProtocolAssMinerFactory.getInstance().isModified())
                    isProtocolAssMined = false;

                if (ProtocolAssMinerFactoryDis.getInstance().isModified())
                    isDisProtocolAssMined = false;

            }
        });
        //全部挖掘按钮响应
        bAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDistributed) {
                    mineAll();
                } else if (isDistributed) {
                    mineAllDis();
                }
            }
        });
        //显示链路规律按钮设置
        blinkRoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                settingsNodePair();
                if (SingleNodeOrNodePairMinerFactory.getPairInstance().isModified())
                    isNodePairMined = false;
                if (SingleNodeOrNodePairMinerFactoryDis.getPairInstance().isModified())
                    isDisNodePairMined = false;
            }
        });
        //挖掘网络结构按钮设置
        bMinNet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDistributed) {
                    if(!isNetworkStructureMined){
                        mineNetworkStructor();
                    } else {
                        WholeNetworkFrame wholeNetworkFrame = new WholeNetworkFrame(networkStructureresultMaps);
                        wholeNetworkFrame.setVisible(true);
                    }
                } else if (isDistributed) {
                    if (!isDisNetworkStructureMined) {
                        final TaskPanel taskPanel = new TaskPanel();
                        taskPanel.setOpaque(true);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        mineNetworkStructorDis("");
                                        Server.getInstance().showNetworkResult();//分布式结果显示
                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                        genDialog(taskPanel);
                    } else {
                        Server.getInstance().showNetworkResult();//分布式结果显示
                    }
                }
            }
        });
        //挖掘节点规律按钮设置
        bMinNode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDistributed) {
                    if(!isSingleNodeMined){
                        mineSingleNode();
                    } else {
                        SingleNodeListFrame singleNodeListFrame = new SingleNodeListFrame(singleNoderesultMaps);
                        singleNodeListFrame.setVisible(true);
                    }
                } else if (isDistributed) {
                    if (!isDisSingleNodeMined) {
                        final TaskPanel taskPanel = new TaskPanel();
                        taskPanel.setOpaque(true);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        mineSingleNodeDis("");
                                        Server.getInstance().showSingleNodeResult();//分布式结果显示
                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                        genDialog(taskPanel);
//                        mineSingleNodeDis();
//                        Server.getInstance().showSingleNodeResult();//分布式结果显示
                    } else {
                        Server.getInstance().showSingleNodeResult();//分布式结果显示
                    }

                }
            }
        });
        //挖掘链路规律按钮设置
        bMinlinkRoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDistributed) {
                    if(!isNodePairMined){
                        mineNodePair();
                    } else {
                        NodePairListFrame nodePairListFrame = new NodePairListFrame(nodePairresultMaps);
                        nodePairListFrame.setVisible(true);
                    }
                } else if (isDistributed) {
                    if (!isDisNodePairMined) {
                        final TaskPanel taskPanel = new TaskPanel();
                        taskPanel.setOpaque(true);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        mineNodePairDis("");
                                        Server.getInstance().showNodePairResult();
                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                        genDialog(taskPanel);
                    } else {
                        Server.getInstance().showNodePairResult();
                    }
                }
            }
        });
        //挖掘承载路径规律按钮设置
        bMinNetLoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDistributed) {
                    if (!isPathMined){
                        minePath();
                    } else {
                        PathListFrame pathListFrame = new PathListFrame(pathResultsMaps);
                        pathListFrame.setVisible(true);
                    }
                } else if (isDistributed) {
                    // TODO Auto-generated method stub
                    if (!isDisPathMined) {
                        final TaskPanel taskPanel = new TaskPanel();
                        taskPanel.setOpaque(true);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        minePathDis("");
                                        Server.getInstance().showPathResult();//分布式结果显示
                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                        genDialog(taskPanel);
                    } else {
                        Server.getInstance().showPathResult();//分布式结果显示
                    }
                }
            }
        });
        //挖掘多业务规律按钮设置
        bMinMultiServer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDistributed) {
                    // TODO Auto-generated method stub
                    if (!isProtocolAssMined){
                        mineProtocolAss();
                    } else {
                        AssociationIpListFrame frame = new AssociationIpListFrame(protocolResultsMaps);
                        frame.setVisible(true);
                    }
                } else if (isDistributed) {
                    // TODO Auto-generated method stub
                    if (!isDisProtocolAssMined) {
                        final TaskPanel taskPanel = new TaskPanel();
                        taskPanel.setOpaque(true);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        mineProtocolAssDis("");
                                        Server.getInstance().showProtocolResult();
                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                        genDialog(taskPanel);
                    } else {
                        Server.getInstance().showProtocolResult();
                    }
                }
            }
        });
    }
    class BackgroundPanel extends JPanel
    {
        Image im;
        public BackgroundPanel(Image im)
        {
            this.im=im;
            this.setOpaque(true);
        }
        //Draw the back ground.
        public void paintComponent(Graphics g)
        {
            super.paintComponents(g);
            g.drawImage(im,0,0,this.getWidth(),this.getHeight(),this);

        }
    }
    public void setIcon(String file, JButton iconButton) {
        ImageIcon icon = new ImageIcon(file);
        Image temp = icon.getImage().getScaledInstance(iconButton.getWidth()+10,
                iconButton.getHeight(), icon.getImage().SCALE_DEFAULT);
        icon = new ImageIcon(temp);
        iconButton.setIcon(icon);
    }
//    protected void makebutton(JButton button,
//                              GridBagLayout gridbag,
//                              GridBagConstraints c) {
////	    	Button button = new Button(name);
//        gridbag.setConstraints(button, c);
//        add(button);
//    }
}
