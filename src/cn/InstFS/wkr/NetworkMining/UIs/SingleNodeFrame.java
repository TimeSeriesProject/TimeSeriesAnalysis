package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.GridLayout;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.FlowLayout;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPopupMenu;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.button.ClassicButtonShaper;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceSaharaLookAndFeel;
import org.jvnet.substance.title.FlatTitlePainter;

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.PeriodMinerFactory;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
//class Menu
public class SingleNodeFrame extends ResultFrame{
	
	//Map<TaskElement, INetworkMiner> model;
//	private ArrayList<>
	//private model;
	
  
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//SingleNodeFrame frame = new SingleNodeFrame();
					//frame.setVisible(true);
					JFrame.setDefaultLookAndFeelDecorated(true); 
					NetworkMinerFactory networkMinerFactory =NetworkMinerFactory.getInstance();
					PeriodMinerFactory periodMinerFactory = PeriodMinerFactory.getInstance();
					periodMinerFactory.dataPath="C:/data/out/traffic/";
					periodMinerFactory.minerAllPeriods();
					
					networkMinerFactory.startAllMiners();
					SingleNodeFrame window = new SingleNodeFrame();
					window.setTitle("网络规律挖掘");
//					window.setModel(networkMinerFactory.allMiners);
					//window.loadModel();
					
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	@Override
	public void loadModel() {
		// TODO Auto-generated method stub
//		MiningMethod method = MiningMethod.MiningMethods_PeriodicityMining;
//		miningMethods.add(method);
		miningMethods.add(MiningMethod.MiningMethods_PeriodicityMining);
		miningMethods.add(MiningMethod.MiningMethods_FrequenceItemMining);
		miningMethods.add(MiningMethod.MiningMethods_SequenceMining);
		miningMethods.add(MiningMethod.MiningMethods_TsAnalysis);
		miningObjects.add("traffic");
		miningObjects.add("通信次数");
//		miningObjects.add("通信跳数");
	}

	@Override
	public void initModel()
	{
		NetworkMinerFactory networkMinerFactory =NetworkMinerFactory.getInstance();
		Map<TaskElement, INetworkMiner> allMiners = networkMinerFactory.allMiners;
		Map<TaskElement, INetworkMiner> miners=new HashMap<TaskElement, INetworkMiner> ();
		for(Map.Entry<TaskElement, INetworkMiner> entry:allMiners.entrySet()) //得到需要的任务
		{
			TaskElement task = entry.getKey();
			if(task.getTaskRange().compareTo(TaskRange.SingleNodeRange)==0) //比较的是顺序
			{
				miners.put(entry.getKey(),entry.getValue());
			}
		}
		
		System.out.println(miningObjects.size());
		System.out.println(miningMethods.size());
		for(int i=0;i<miningObjects.size();i++)
		{
			ArrayList<JPopupMenu> list = new ArrayList<JPopupMenu>();
			final PanelShowAllResults   panelShow = new PanelShowAllResults();
			panelShowList.add(panelShow);
			for(int j=0;j<miningMethods.size();j++)
			{
				JPopupMenu popMenu = new JPopupMenu(); 
				
				Map<TaskElement, INetworkMiner> tmpminers=new HashMap<TaskElement, INetworkMiner> ();
				TreeMap<String,TreeMap<String,TaskElement>> taskTree = new TreeMap<String,TreeMap<String,TaskElement>>();
				for(Map.Entry<TaskElement, INetworkMiner> entry:miners.entrySet()) //得到需要的任务
				{
					
					TaskElement task = entry.getKey();
					if(entry.getKey().getMiningObject().equals(miningObjects.get(i))&&entry.getKey().getMiningMethod().equals(miningMethods.get(j)))
					{
						tmpminers.put(entry.getKey(),entry.getValue());
//						ipSet.add(task.getRange());
					}
				}
			
				for(Map.Entry<TaskElement, INetworkMiner> entry:tmpminers.entrySet()) 
				{
					TaskElement task = entry.getKey();
					if(!taskTree.containsKey(task.getRange()))
					{
						TreeMap<String,TaskElement> subTaskTree = new TreeMap<String,TaskElement>();
						taskTree.put(task.getRange(),subTaskTree);
					}
					taskTree.get(task.getRange()).put(task.getProtocol(), task);
				}
				
				for(Entry<String, TreeMap<String, TaskElement>>  entry:taskTree.entrySet())
				{
					String ip =entry.getKey();
					int len =ip.length();   //字符居中对齐
					int w =(28-len)/2;
					String pad=String.format("%"+w+"s", " ");
					String str =pad+ip+pad;
					JMenu subMenu = new JMenu(str);

					for(Entry<String, TaskElement> subEntry:entry.getValue().entrySet())
					{
						String protocol =subEntry.getKey();
						int len1 =protocol.length();   //字符居中对齐
						int w1 =(20-len1)/2;
						String pad1=String.format("%"+w1+"s", " ");
						String str1 =pad1+protocol+pad1;
						JMenuItem item = new JMenuItem(str1);
						final TaskElement task = subEntry.getValue();
						
						panelShow.onTaskAdded(task);
						
						item.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								panelShow.displayTask(task);
//							
							}
						});
						subMenu.add(item);
					}
					popMenu.add(subMenu);
				}
			
				list.add(popMenu);
			}
			popupMenus.add(list);
		}
		System.out.println("ff"+popupMenus.get(0).get(0).getComponentCount());
		
	}
	
}
