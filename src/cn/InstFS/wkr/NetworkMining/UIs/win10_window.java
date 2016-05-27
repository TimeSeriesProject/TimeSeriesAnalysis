package cn.InstFS.wkr.NetworkMining.UIs;

import cn.InstFS.wkr.NetworkMining.Miner.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.DialogSettingTask;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ProcessBarShow;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    boolean isNetworkStructureMined=false;
    boolean isSingleNodeMined=false;
    boolean isNodePairMined = false;
    
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
		NetworkMinerFactory networkMinerFactory =NetworkMinerFactory.getInstance();
		NetworkFactory networkFactory = NetworkFactory.getInstance();
		networkFactory.dataPath="C:\\data\\out\\route";
		networkFactory.reset();
		networkFactory.setMiningObject(MiningObject.MiningObject_Cluster);
		networkFactory.detect();
		
		HashMap<TaskCombination, MinerNodeResults> clusterMap = NetworkMinerFactory.getInstance().startAllNetworkStructrueMiners(MiningObject.MiningObject_Cluster);
		
		networkStructureresultMaps.put(MiningObject.MiningObject_Cluster.toString(),clusterMap);
		
		networkFactory.reset();
		networkFactory.setMiningObject(MiningObject.MiningObject_Diameter);
		networkFactory.detect();
		
		HashMap<TaskCombination, MinerNodeResults> diameter = NetworkMinerFactory.getInstance().startAllNetworkStructrueMiners(MiningObject.MiningObject_Diameter);
		networkStructureresultMaps.put(MiningObject.MiningObject_Diameter.toString(),diameter);
		isNetworkStructureMined=true;
	}
	public void mineSingleNode()
	{
		NetworkMinerFactory.getInstance();
		SingleNodeOrNodePairMinerFactory singleNodeMinerFactory=SingleNodeOrNodePairMinerFactory.getInstance();
		singleNodeMinerFactory.dataPath="C:\\data\\out\\traffic";
		singleNodeMinerFactory.reset();
		singleNodeMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
		singleNodeMinerFactory.setTaskRange(TaskRange.SingleNodeRange);
		singleNodeMinerFactory.detect();
		singleNoderesultMaps.put(MiningObject.MiningObject_Times.toString(), NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Times));
		
		singleNodeMinerFactory.reset();
		singleNodeMinerFactory.setMiningObject(MiningObject.MiningObject_Traffic);
		singleNodeMinerFactory.detect();
		singleNoderesultMaps.put(MiningObject.MiningObject_Traffic.toString(),NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Traffic));
		isSingleNodeMined=true;
	}
	public void mineNodePair()
	{
		NetworkMinerFactory.getInstance();
		SingleNodeOrNodePairMinerFactory nodePairMinerFactory=SingleNodeOrNodePairMinerFactory.getInstance();
		nodePairMinerFactory.dataPath="C:\\data\\out\\traffic";
		nodePairMinerFactory.reset();
		nodePairMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
		nodePairMinerFactory.setTaskRange(TaskRange.NodePairRange);
		nodePairMinerFactory.detect();
		nodePairresultMaps.put(MiningObject.MiningObject_Times.toString(),NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Times));
		
		nodePairMinerFactory.reset();
		nodePairMinerFactory.setMiningObject(MiningObject.MiningObject_Traffic);
		nodePairMinerFactory.setTaskRange(TaskRange.NodePairRange);
		nodePairMinerFactory.detect();
		nodePairresultMaps.put(MiningObject.MiningObject_Traffic.toString(),NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Traffic));
		isNodePairMined=true;
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
            	if(isNetworkStructureMined==false)
            		mineNetworkStructor();
            	WholeNetworkFrame wholeNetworkFrame = new WholeNetworkFrame(networkStructureresultMaps);
            	wholeNetworkFrame.setVisible(true);
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
            	if(isSingleNodeMined==false)
            		mineSingleNode();
            	SingleNodeListFrame singleNodeListFrame = new SingleNodeListFrame(singleNoderesultMaps);
            	singleNodeListFrame.setVisible(true);
            }
        });


        //显示多业务按钮响应
        bMultiServer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub


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
            	if(isNodePairMined==false)
            		mineNodePair();
            	NodePairListFrame nodePairListFrame = new NodePairListFrame(nodePairresultMaps);
            	nodePairListFrame.setVisible(true);
				
            }
        });
        //挖掘网络结构按钮设置
        bMinNet.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
        	if(isNetworkStructureMined==false)
        		mineNetworkStructor();

        }
    });
        //挖掘节点规律按钮设置
        bMinNode.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
        	if(isSingleNodeMined==false)
        		mineSingleNode();
			
        }
    });
        //挖掘链路规律按钮设置
        bMinlinkRoad.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
        	if(isNodePairMined==false)
        		mineNodePair();
		

        }
    });
        //挖掘承载路径规律按钮设置
        bMinNetLoad.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub


        }
    });
        //挖掘多业务规律按钮设置
        bMinMultiServer.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub


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
