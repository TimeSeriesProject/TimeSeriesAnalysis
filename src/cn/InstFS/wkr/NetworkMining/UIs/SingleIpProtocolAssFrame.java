
package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.DataInputs.CWNetworkReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowNodeFrequence;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowTs;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
//import associationRules.ProtoclPair;

public class SingleIpProtocolAssFrame extends JFrame{

	public static void main(String[] args) {
		
//		ProtoclPair pp = new ProtoclPair();
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					JFrame.setDefaultLookAndFeelDecorated(true); 
					SingleNodeOrNodePairMinerFactory periodMinerFactory=SingleNodeOrNodePairMinerFactory.getInstance();
//					periodMinerFactory.setMiningObject("通信次数");
					periodMinerFactory.setTaskRange(TaskRange.SingleNodeRange);
					periodMinerFactory.setMethod(MiningMethod.MiningMethods_PeriodicityMining);
					periodMinerFactory.detect();
					NetworkMinerFactory.getInstance().startAllMiners();

					
//					SingleNodeFrame window = new SingleNodeFrame(TaskElement.allTasks);
					System.out.println();
					
//					window.setModel(networkMinerFactory.allMiners);
					//window.loadModel();
					
//					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	MinerProtocolResults mProtocolResult = null;
	TaskCombination taskCombination;
	PanelShowAllResults   panelShow = new PanelShowAllResults();
	ArrayList<JButton> buttons= new ArrayList<JButton>();
	
	public static DefaultListModel lmodel = new DefaultListModel();
	JList listTasks;
	
	public SingleIpProtocolAssFrame(TaskCombination taskCombination, HashMap<TaskCombination, MinerProtocolResults> resultMaps)
	{
		this.taskCombination = taskCombination;
		mProtocolResult = resultMaps.get(taskCombination);
		initModel();
		initialize();
		//按置信度排序显示列表
		Collections.sort(mProtocolResult.getRetFP().protocolPairList,new Comparator<ProtoclPair>()
				{
					@Override
					public int compare(ProtoclPair o1, ProtoclPair o2) {  
						System.out.println(o1.getConfidence());
						System.out.println(o2.getConfidence());
						if(o1.getConfidence() > o2.getConfidence())
							return -1;
						else if(o1.getConfidence() < o2.getConfidence())
							return 1;
						else 
							return 0;
					}
				});
	}
	
	public void initModel() {
		// TODO Auto-generated method stub
//		NetworkMinerFactory networkMinerFactory = NetworkMinerFactory.getInstance();
		listTasks.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
//				displaySelectedTask(e);
			}
		});
		listTasks.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
//				displayTaskConfig(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
//				undisplayTaskConfig();
			}
		});
//		setColumnHeaderView(new JLabel("已配事件列表："));
//		setViewportView(listTasks);	
		
		List<String> protocolPairList = new ArrayList<String>();
		String tmp = "";
		for(int i = 0;i < mProtocolResult.getRetFP().protocolPairList.size(); i++)
		{
			tmp = mProtocolResult.getRetFP().protocolPairList.get(i).getProtocol1()+"_"+mProtocolResult.getRetFP().protocolPairList.get(i).getProtocol2()
					+"("+String.format("%5.3",mProtocolResult.getRetFP().protocolPairList.get(i).getConfidence())+")";
			protocolPairList.add(tmp);
		}
		List<TaskElement> taskList = taskCombination.getTasks();
//		java.util.List<TaskElement> a = taskCombination.getTasks()
		
		for(int i=0;i<taskList.size();i++)
		{
//			if(taskList.get(i).getMiningMethod()==MiningMethod.MiningMethods_OutliesMining||taskList.get(i).getMiningMethod()==MiningMethod.MiningMethods_Statistics)
//				continue;
			final TaskElement task = taskList.get(i);
			panelShow.onTaskAdded(task);
			JButton button = new JButton(taskList.get(i).getMiningMethod().toString());
			button.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					panelShow.displayTask(task);
				}
			});
			buttons.add(button);
		}
	}
	private void initialize() {
//		this.setTitle("结点属性变化规律");
		setBounds(100, 100, 1500, 900);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		
		this.getContentPane().setVisible(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
//		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(200);
		splitPane.setDividerSize(2);
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(null);
		
		splitPane.setLeftComponent(leftPanel);
		
		
		for (int i=0;i<buttons.size();i++)  
		{
			JButton button = buttons.get(i);
			
			button.setBounds(38, 51+i*100, 134, 27);
			leftPanel.add(button);
		}
		splitPane.setRightComponent(panelShow);;
		getContentPane().add(splitPane);
		
	}

}
