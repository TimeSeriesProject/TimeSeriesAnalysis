package cn.InstFS.wkr.NetworkMining.UIs;

import cn.InstFS.wkr.NetworkMining.Miner.*;
import cn.InstFS.wkr.NetworkMining.Results.MiningResultsFile;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.DialogSettingTask;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.DialogSettings;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ProcessBarShow;
import org.apache.commons.math3.analysis.function.Min;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.jfree.data.gantt.Task;
import org.jvnet.substance.*;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.NebulaBrickWallSkin;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.theme.SubstanceBottleGreenTheme;
import org.jvnet.substance.title.MatteHeaderPainter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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
	public void mineNetworkStructor()
	{
        networkStructureresultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        NetworkFactory networkFactory = NetworkFactory.getInstance();
        List<MiningObject> miningObjectList = networkFactory.getMiningObjectsChecked();

        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            MiningResultsFile resultsFile = new MiningResultsFile(ob);
            HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
            if (resultsFile.hasFile(networkFactory)) {  // 已有，直接读取
                objectMap = (HashMap<TaskCombination, MinerNodeResults>) resultsFile.file2ResultMap();
                networkStructureresultMaps.put(ob.toString(), objectMap);
                NetworkMinerFactory.getInstance().taskCombinationAdd2allMiner(objectMap); //由读取的结果objectMap添加相应的miner，以供显示
            } else {    // 没有，则重新挖掘并保存
                networkFactory.reset();
                networkFactory.setMiningObject(ob);
                networkFactory.detect();
                objectMap = NetworkMinerFactory.getInstance().startAllNetworkStructrueMiners(ob);
                networkStructureresultMaps.put(ob.toString(), objectMap);
                MiningResultsFile newResultsFile = new MiningResultsFile(ob);
                newResultsFile.resultMap2File(networkFactory, objectMap);
            }
        }

		isNetworkStructureMined=true;
	}
	public void mineSingleNode()
	{
        singleNoderesultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        SingleNodeOrNodePairMinerFactory singleNodeMinerFactory = SingleNodeOrNodePairMinerFactory.getInstance();
        List<MiningObject> miningObjectList = singleNodeMinerFactory.getMiningObjectsChecked();

        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            MiningResultsFile resultsFile = new MiningResultsFile(ob);
            HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
            if (resultsFile.hasFile(singleNodeMinerFactory)) {  // 已有，直接读取
                objectMap = (HashMap<TaskCombination, MinerNodeResults>) resultsFile.file2ResultMap();
                singleNoderesultMaps.put(ob.toString(), objectMap);
                NetworkMinerFactory.getInstance().taskCombinationAdd2allMiner(objectMap); //由读取的结果objectMap添加相应的miner，以供显示
            } else {    // 没有，则重新挖掘并保存
                singleNodeMinerFactory.reset();
                singleNodeMinerFactory.setMiningObject(ob);
                singleNodeMinerFactory.detect();
                objectMap = NetworkMinerFactory.getInstance().startAllNodeMiners(ob);
                singleNoderesultMaps.put(ob.toString(), objectMap);
                MiningResultsFile newResultsFile = new MiningResultsFile(ob);
                newResultsFile.resultMap2File(singleNodeMinerFactory, objectMap);
            }
        }

		isSingleNodeMined=true;
	}
	public void mineNodePair()
	{
        nodePairresultMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        SingleNodeOrNodePairMinerFactory nodePairMinerFactory = SingleNodeOrNodePairMinerFactory.getPairInstance();
        List<MiningObject> miningObjectList = nodePairMinerFactory.getMiningObjectsChecked();

        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            MiningResultsFile resultsFile = new MiningResultsFile(ob);
            HashMap<TaskCombination, MinerNodeResults> objectMap = new HashMap<>();
            if (resultsFile.hasFile(nodePairMinerFactory)) {  // 已有，直接读取
                objectMap = (HashMap<TaskCombination, MinerNodeResults>) resultsFile.file2ResultMap();
                nodePairresultMaps.put(ob.toString(), objectMap);
                NetworkMinerFactory.getInstance().taskCombinationAdd2allMiner(objectMap); //由读取的结果objectMap添加相应的miner，以供显示
            } else {    // 没有，则重新挖掘并保存
                nodePairMinerFactory.reset();
                nodePairMinerFactory.setMiningObject(ob);
                nodePairMinerFactory.detect();
                objectMap = NetworkMinerFactory.getInstance().startAllNodeMiners(ob);
                nodePairresultMaps.put(ob.toString(), objectMap);
                MiningResultsFile newResultsFile = new MiningResultsFile(ob);
                newResultsFile.resultMap2File(nodePairMinerFactory, objectMap);
            }
        }

		isNodePairMined=true;
	}

    public void mineProtocolAss() {
        protocolResultsMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        ProtocolAssMinerFactory protocolAssMinerFactory = ProtocolAssMinerFactory.getInstance();
        List<MiningObject> miningObjectList = protocolAssMinerFactory.getMiningObjectsChecked();

        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            MiningResultsFile resultsFile = new MiningResultsFile(ob);
            HashMap<TaskCombination, MinerProtocolResults> objectMap = new HashMap<>();
            if (resultsFile.hasFile(protocolAssMinerFactory)) {  // 已有，直接读取
                objectMap = (HashMap<TaskCombination,MinerProtocolResults>) resultsFile.file2ResultMap();
                protocolResultsMaps = objectMap;
                NetworkMinerFactory.getInstance().taskCombinationAdd2allMiner(objectMap); //由读取的结果objectMap添加相应的miner，以供显示
            } else {    // 没有，则重新挖掘并保存
                protocolAssMinerFactory.reset();
                protocolAssMinerFactory.setMiningObject(ob);
                protocolAssMinerFactory.detect();
                objectMap = NetworkMinerFactory.getInstance().startAllProtocolMiners(ob);
                protocolResultsMaps =objectMap;
                MiningResultsFile newResultsFile = new MiningResultsFile(ob);
                newResultsFile.resultMap2File(protocolAssMinerFactory, objectMap);
            }
        }

        isProtocolAssMined=true;
    }

    private void settingsWholeNetwork() {
        NetworkMinerFactory.getInstance();
        DialogSettings dialog = new DialogSettings(NetworkFactory.getInstance(), "网络结构规律挖掘配置");
        dialog.pack();
        dialog.setVisible(true);
    }

    private void settingsPath() {
        NetworkMinerFactory.getInstance();
        DialogSettings dialog = new DialogSettings(PathMinerFactory.getInstance(), "承载路径规律挖掘配置");
        dialog.pack();
        dialog.setVisible(true);
    }

    private void settingsSingleNode() {
        NetworkMinerFactory.getInstance();
        SingleNodeOrNodePairMinerFactory.getInstance().setTaskRange(TaskRange.SingleNodeRange);
        DialogSettings dialog = new DialogSettings(SingleNodeOrNodePairMinerFactory.getInstance(),"节点规律挖掘配置");
        dialog.pack();
        dialog.setVisible(true);
    }

    private void settingsNodePair() {
        NetworkMinerFactory.getInstance();
        SingleNodeOrNodePairMinerFactory.getPairInstance().setTaskRange(TaskRange.NodePairRange);
        DialogSettings dialog = new DialogSettings(SingleNodeOrNodePairMinerFactory.getPairInstance(),"链路规律挖掘配置");
        dialog.pack();
        dialog.setVisible(true);
    }

    private void settingsProtocolAss() {
        NetworkMinerFactory.getInstance();
        DialogSettings dialog = new DialogSettings(ProtocolAssMinerFactory.getInstance(), "多业务关联挖掘配置");
        dialog.pack();
        dialog.setVisible(true);
    }

    public void minePath() {
        pathResultsMaps.clear();
        NetworkMinerFactory.getInstance().allCombinationMiners.clear();

        PathMinerFactory pathMinerFactory = PathMinerFactory.getInstance();
        List<MiningObject> miningObjectList = pathMinerFactory.getMiningObjectsChecked();

        // 判断是否含有该挖掘对象结果文件
        for (MiningObject ob: miningObjectList) {
            MiningResultsFile resultsFile = new MiningResultsFile(ob);
            HashMap<TaskCombination, MinerResultsPath> objectMap = new HashMap<>();
            if (resultsFile.hasFile(pathMinerFactory)) {  // 已有，直接读取
                objectMap = (HashMap<TaskCombination, MinerResultsPath>) resultsFile.file2ResultMap();
                pathResultsMaps.put(ob.toString(), objectMap);
                NetworkMinerFactory.getInstance().taskCombinationAdd2allMiner(objectMap); //由读取的结果objectMap添加相应的miner，以供显示
            } else {    // 没有，则重新挖掘并保存
                pathMinerFactory.reset();
                pathMinerFactory.setMiningObject(ob);
                pathMinerFactory.detect();
                objectMap = NetworkMinerFactory.getInstance().startAllPathMiners(ob);
                pathResultsMaps.put(ob.toString(), objectMap);
                MiningResultsFile newResultsFile = new MiningResultsFile(ob);
                newResultsFile.resultMap2File(pathMinerFactory, objectMap);
            }
        }

        isPathMined = true;
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
//        this.setResizable(false);

//        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    }
    public void init()
    {
        //没有任务时初始化


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



        setTitle("网络规律挖掘模拟器");


        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setLayout(null);
        ct=this.getContentPane();
        this.setLayout(null);
        final JButton bNetwork=new JButton("网络结构");
        bNetwork.setBackground( new Color(102,205,170));
        bNetwork.setBounds(100,120,200,200);
        setIcon("img\\network.png",bNetwork);
        bNetwork.setMargin(new Insets(0, 0, 0, 0));
//        bNetwork.setEnabled(false);
        
        final JButton bNetLoad=new JButton("承载路径");
        bNetLoad.setBackground( new Color(102,205,170));
        bNetLoad.setBounds(100,330,200,200);
        setIcon("img\\netLoad.png",bNetLoad);
//        bNetLoad.setEnabled(false);
        
        final JButton bNode=new JButton("节点");
        bNode.setBackground(new Color(102, 205, 170));
        bNode.setBounds(100,540,200,200);
        setIcon("img\\node.png",bNode);
//        bNode.setEnabled(false);

        final JButton blinkRoad=new JButton("链路");
        blinkRoad.setBounds(100,750,200,200);
        setIcon("img\\road.png",blinkRoad);
//        blinkRoad.setEnabled(false);
        
        final JButton bMultiServer=new JButton("多业务");
//        bMultiServer.setBackground( new Color(102,205,170));
        bMultiServer.setBounds(310,750,200,200);
        setIcon("img\\multi.png",bMultiServer);
//        bMultiServer.setEnabled(false);
        
        //中间一列
        final JButton bPcap=new JButton("PCAP 解析");
        bPcap.setBackground( new Color(178,234,34));
        bPcap.setBounds(310,120,410,200);
        setIcon("img\\file.png",bPcap);

        final JButton bAll=new JButton("挖掘全部");
        bAll.setBackground(Color.YELLOW);
        bAll.setBounds(310,330,410,200);
        setIcon("img\\MinAll.png",bAll);
//        final JButton bSetting=new JButton("设置");
//        bSetting.setBackground(Color.orange);
//        bSetting.setBounds(138,824,200,100);
        //最后一列
        final JButton bMinNet=new JButton("挖掘网络结构");
        bMinNet.setBounds(730,120,200,200);
        setIcon("img\\min_network.png",bMinNet);
        final JButton bMinNetLoad=new JButton("挖掘承载路径");
        bMinNetLoad.setBackground( new Color(102,205,170));
        bMinNetLoad.setBounds(730,330,200,200);
        setIcon("img\\min_netLoad.png",bMinNetLoad);

        final JButton bMinNode=new JButton("挖掘节点");
        bMinNode.setBackground(new Color(102, 205, 170));
        bMinNode.setBounds(730,540,200,200);
        setIcon("img\\min_node.png",bMinNode);

        final JButton bMinlinkRoad=new JButton("挖掘链路");
        bMinlinkRoad.setBounds(730,750,200,200);
        setIcon("img\\min_road.png",bMinlinkRoad);

        final JButton bMinMultiServer=new JButton("挖掘多业务");
        bMinMultiServer.setBackground( new Color(102,205,170));
        bMinMultiServer.setBounds(520,750,200,200);
        setIcon("img\\min_multi.png",bMinMultiServer);

        bgp=new BackgroundPanel((new ImageIcon("img\\background.png")).getImage());
        bgp.setBounds(0,0,1920,1080);
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
        ct.add(bgp);

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
//        bNetwork.setMargin(new Insets(0, 0, 0, 0));


        //显示网络结构按钮响应
        bNetwork.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                settingsWholeNetwork();
                if (NetworkFactory.getInstance().isModified())
                    isNetworkStructureMined = false;
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
                // TODO Auto-generated method stub
            	

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

            }
        });
        //全部挖掘按钮响应
        bAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
            	if(isSingleNodeMined==false)
            		mineSingleNode();
            	if(isNodePairMined==false)
            		mineNodePair();
            	if(isNetworkStructureMined==false)
            		mineNetworkStructor();
            
            	
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
            }
        });
        //挖掘网络结构按钮设置
        bMinNet.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
        	if(!isNetworkStructureMined)
        		mineNetworkStructor();
            WholeNetworkFrame wholeNetworkFrame = new WholeNetworkFrame(networkStructureresultMaps);
            wholeNetworkFrame.setVisible(true);

        }
    });
        //挖掘节点规律按钮设置
        bMinNode.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
        	if(!isSingleNodeMined)
        		mineSingleNode();
            SingleNodeListFrame singleNodeListFrame = new SingleNodeListFrame(singleNoderesultMaps);
            singleNodeListFrame.setVisible(true);
			
        }
    });
        //挖掘链路规律按钮设置
        bMinlinkRoad.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
        	if(!isNodePairMined)
        		mineNodePair();
            NodePairListFrame nodePairListFrame = new NodePairListFrame(nodePairresultMaps);
            nodePairListFrame.setVisible(true);

        }
    });
        //挖掘承载路径规律按钮设置
        bMinNetLoad.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (!isPathMined)
                minePath();
            PathListFrame pathListFrame = new PathListFrame(pathResultsMaps);
            pathListFrame.setVisible(true);

        }
    });
        //挖掘多业务规律按钮设置
        bMinMultiServer.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (!isProtocolAssMined)
                mineProtocolAss();
            AssociationIpListFrame frame = new AssociationIpListFrame(protocolResultsMaps);
            frame.setVisible(true);
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
