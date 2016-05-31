package cn.InstFS.wkr.NetworkMining.UIs;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.Miner.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResultsFP_Line;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

//import associationRules.ProtoclPair;

public class SingleIpProtocolAssFrame extends JFrame {

	public static void main(String[] args) {

		// ProtoclPair pp = new ProtoclPair();
		// TODO Auto-generated method stub
		TaskCombination taskCombination=new TaskCombination();
		
		MinerProtocolResults minerProtocolResults=new MinerProtocolResults();
		HashMap<TaskCombination, MinerProtocolResults> resultMaps=new HashMap<TaskCombination, MinerProtocolResults>();
		resultMaps.put(taskCombination, minerProtocolResults);
		SingleIpProtocolAssFrame frame = new SingleIpProtocolAssFrame( taskCombination, resultMaps);
		//SingleIpProtocolAssFrame frame = new SingleIpProtocolAssFrame(); 
		// 设置frame的大小为300x200，且可见默认是不可见的
		frame.setSize(9, 200);
		frame.setVisible(true);
		// 使右上角的关闭按钮生效，如果没有这句，点击右上角的关闭按钮只能关闭窗口，无法结束进程
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	MinerProtocolResults mProtocolResult = null;
	TaskCombination taskCombination=null;
	List<ProtoclPair> protocolPairList = null;
	PanelShowAllResults panelShow = new PanelShowAllResults();
	ArrayList<JButton> buttons = new ArrayList<JButton>();
	 ChangeCompositeToSwingPanel ccts=null;

	public static DefaultListModel lmodel = new DefaultListModel();
	JList listTasks;
	//System.out.println("rtyuio");
	public SingleIpProtocolAssFrame(TaskCombination taskCombination,
			HashMap<TaskCombination, MinerProtocolResults> resultMaps) {
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("SingleIpProtocolAssFrame is closing");
				ccts.destroyCompositeThread();
				try {
					//this.finalize();
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
				
				
		this.setBounds(100,100,1500,900);
		this.taskCombination = taskCombination;
		mProtocolResult = resultMaps.get(taskCombination);
		protocolPairList=mProtocolResult.getRetFP().getProtocolPairList();
		

		// 按置信度排序显示列表
/*		Collections.sort(mProtocolResult.getRetFP().protocolPairList,
				new Comparator<ProtoclPair>() {
					@Override
					public int compare(ProtoclPair o1, ProtoclPair o2) {
						System.out.println(o1.getConfidence());
						System.out.println(o2.getConfidence());
						if (o1.getConfidence() > o2.getConfidence())
							return -1;
						else if (o1.getConfidence() < o2.getConfidence())
							return 1;
						else
							return 0;
					}
				});*/

		/*
		 * @作者：顺子
		 * 
		 * @功能：初始化界面->显示协议对置信度和相关时间序列图表
		 */
		// 从这里开始
	 ccts = new ChangeCompositeToSwingPanel(
				taskCombination, resultMaps);
		ccts.createComposite();
		this.add(ccts);
		
		//这里结束

	}


}
