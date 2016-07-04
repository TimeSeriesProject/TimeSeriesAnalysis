package cn.InstFS.wkr.NetworkMining.UIs;


import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.PublicImpl;

import associationRules.ProtoclPair;
import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.SubPanelShowMinerResultsTs;
import cn.InstFS.wkr.NetworkMining.UIs.ProtocolDetailPanel;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResultsFP_Line;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ActionListenerList;
import oracle.net.aso.b;
import oracle.net.aso.p;

//import associationRules.ProtoclPair;

public class SingleIpProtocolAssFrame extends JFrame{
	/**
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
	}*/

	MinerProtocolResults mProtocolResult = null;
	TaskCombination taskCombination=null;
	List<ProtoclPair> protocolPairList = null;
	List<ProtocolAssociationResult> protocolPairList_sim = null; //lyh new
	HashMap<TaskCombination, MinerProtocolResults> resultMap;
	JPanel jPanel = new JPanel();
	JTable listTable_sim; 
	PanelShowAllResults panelShow = new PanelShowAllResults();
	ArrayList<JButton> buttons = new ArrayList<JButton>();
	ChangeCompositeToSwingPanel ccts=null;
	JSplitPane jSplitPaneOne = new JSplitPane();//左右分割
	JSplitPane jSplitPaneTwo = new JSplitPane(JSplitPane.VERTICAL_SPLIT);//上下分割
	JScrollPane jPane;

	
	public SingleIpProtocolAssFrame(TaskCombination taskCombination,
			HashMap<TaskCombination, MinerProtocolResults> resultMaps) {
		
		/**this.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				//dispose();
				System.out.println("SingleIpProtocolAssFrame is closing");
				//ccts.destroyCompositeThread();
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
			
		});*/
				
				
		setBounds(100,100,1500,900);
		this.taskCombination = taskCombination;
		mProtocolResult = resultMaps.get(taskCombination);
		protocolPairList=mProtocolResult.getRetFP().getProtocolPairList();
		protocolPairList_sim = mProtocolResult.getRetSim().getProtocolPairList(); //new
		
		creatTable();
		splitFrame();
		//add(jPanel);
		
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
	 /*ccts = new ChangeCompositeToSwingPanel(
				taskCombination, resultMaps);
		ccts.createComposite();
		this.add(ccts);*/
		
		//这里结束

	}
	public boolean isCellEditable(int row,int column){
		return false;
	}
	public void creatTable(){
		//create table
		System.out.println("create listTable_sim");
		System.out.println(protocolPairList_sim.size());
		String data[][] = new String[protocolPairList_sim.size()][4];
		for(int i=0;i<protocolPairList_sim.size();i++){
			data[i][0] = String.valueOf(i);
			data[i][1] = protocolPairList_sim.get(i).getProtocol1();
			data[i][2] = protocolPairList_sim.get(i).getProtocol2();
			data[i][3] = String.valueOf(protocolPairList_sim.get(i).getConfidence());
		}
		String colNames[] = {"序号","协议1","协议2","置信度"};
		//DefaultTableModel Psimmodel=new DefaultTableModel(data,colNames);
		
		DefaultTableModel model=new DefaultTableModel(data,colNames){
	        public boolean isCellEditable(int row, int column){   
	        	return   false;   
	        };
	     
	      };
		/**listTable_sim = new JTable(data,colNames){  
            @Override  
            public boolean isCellEditable(int row,int column){  
                return false;  
            }  
        };*/
	    listTable_sim = new JTable(model);
        listTable_sim.setPreferredScrollableViewportSize(new Dimension(300, 80));
		listTable_sim.setVisible(true);
	    listTable_sim.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    listTable_sim.setAutoscrolls(true);
	    //listTable_sim.getColumnModel().getColumn(0).setPreferredWidth(40);
	    listTable_sim.addMouseListener(new MouseAdapter(){
	    	public void mouseClicked(MouseEvent e) {
		        
	    		if(e.getClickCount()==2){//点击几次，这里是双击事件
	    			System.out.println("双击跳转");
	    			tableChanged();        
	    		}
	    	}
	    });
	    jPane = new JScrollPane(listTable_sim);
	}
	public void splitFrame(){
		
		//设置jSplitPaneOne，即显示框左边区域
		jSplitPaneOne.setDividerLocation(300);//分隔条的位置
		jSplitPaneOne.setDividerSize(2);//分隔条的大小
		jSplitPaneOne.setLeftComponent(jPane);
		
		//设置jSplitPaneTwo，即显示框右边区域
		jSplitPaneTwo.setDividerLocation(500);
		jSplitPaneTwo.setDividerSize(2);
		
				
		jSplitPaneOne.setRightComponent(jSplitPaneTwo);
		add(jSplitPaneOne);//把分割加入frame中		
	}
	public void tableChanged(){
		int row=listTable_sim.getSelectedRow();    
		  //得到所在行的第一个列的值，作为下面事件传递的参数
		DataItems dataItem1 = protocolPairList_sim.get(row).dataItems1;
		DataItems dataItem2 = protocolPairList_sim.get(row).dataItems2;
		ProtocolDetailPanel panelTop = new ProtocolDetailPanel(dataItem1, row);
		ProtocolDetailPanel panelButton = new ProtocolDetailPanel(dataItem2, row);
		panelTop.displayResult(dataItem1, row);
		panelButton.displayResult(dataItem2, row);
						
		jSplitPaneTwo.setDividerLocation(450);
		jSplitPaneTwo.setDividerSize(2);
				
		jSplitPaneTwo.setTopComponent(panelTop);
		jSplitPaneTwo.setBottomComponent(panelButton);
		
		jSplitPaneTwo.setVisible(true);
		System.out.println("selectrow:"+row);
		
	}

}


