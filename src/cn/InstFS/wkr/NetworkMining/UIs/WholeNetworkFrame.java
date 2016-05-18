package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import cn.InstFS.wkr.NetworkMining.DataInputs.CWNetworkReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowNodeFrequence;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowTs;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class WholeNetworkFrame extends JFrame{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					JFrame.setDefaultLookAndFeelDecorated(true); 
					NetworkMinerFactory networkMinerFactory =NetworkMinerFactory.getInstance();
					SingleNodeOrNodePairMinerFactory periodMinerFactory = SingleNodeOrNodePairMinerFactory.getInstance();
					WholeNetworkFrame window = new WholeNetworkFrame();
					
//					window.setModel(networkMinerFactory.allMiners);
					//window.loadModel();
					
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	protected ArrayList<MiningMethod> miningMethods = new ArrayList<MiningMethod>();
	protected ArrayList<String>     miningObjects = new ArrayList<String>();
	ArrayList<ArrayList<JPopupMenu>> popupMenus=new ArrayList<ArrayList<JPopupMenu>>();
	ArrayList<JPopupMenu> currentPopupMenus=new ArrayList<JPopupMenu>();
	ArrayList<JButton> buttons= new ArrayList<JButton>();
	ArrayList<PanelShowAllResults> panelShowList = new ArrayList<PanelShowAllResults>();
	JTabbedPane tabbedPane;
	protected int miniMethodIndex=0;
	protected int miningObjectIndex=0;
	Map<Integer,MouseListener> popupListeners= new   HashMap<Integer,MouseListener>(); //弹出菜单监听器
	ArrayList<JPanel> statisticsPanels = new ArrayList<JPanel>();
	ArrayList<ArrayList<TaskElement>> taskList = new ArrayList<ArrayList<TaskElement>> ();
	int ipIndex=0;
	int protocolIndex=0;
	public WholeNetworkFrame()
	{
		loadModel();
		initModel();
		initialize();
	}
	public void loadModel() {
		// TODO Auto-generated method stub
		
		miningMethods.add(MiningMethod.MiningMethods_PeriodicityMining);
		miningMethods.add(MiningMethod.MiningMethods_SequenceMining);
		miningMethods.add(MiningMethod.MiningMethods_OutliesMining);
		miningMethods.add(MiningMethod.MiningMethods_PredictionMining);
		miningMethods.add(MiningMethod.MiningMethods_Statistics);
		miningObjects.add("网络簇系数");
		miningObjects.add("网络直径");
//		miningObjects.add("结点出现消失");
	}
	
	
	public void initModel() {
		// TODO Auto-generated method stub
		NetworkMinerFactory networkMinerFactory =NetworkMinerFactory.getInstance();
		Map<TaskElement, INetworkMiner> allMiners = networkMinerFactory.allMiners;
		Map<TaskElement, INetworkMiner> miners=new HashMap<TaskElement, INetworkMiner> ();
		
		
		for(Map.Entry<TaskElement, INetworkMiner> entry:allMiners.entrySet()) //得到需要的任务
		{
			TaskElement task = entry.getKey();
			if(task.getTaskRange().compareTo(TaskRange.WholeNetworkRange)==0) //比较的是顺序
			{
				miners.put(entry.getKey(),entry.getValue());
			}
		}
		
		System.out.println(miningObjects.size());
		System.out.println(miningMethods.size());
		for(int i=0;i<miningObjects.size();i++)
		{
			ArrayList<JPopupMenu> list = new ArrayList<JPopupMenu>();
			ArrayList<TaskElement> tasks = new ArrayList<TaskElement> ();
			taskList.add(tasks);
			final PanelShowAllResults   panelShow = new PanelShowAllResults();
			panelShowList.add(panelShow);
			TaskElement task=null;
			for(int j=0;j<miningMethods.size();j++)
			{
				if(miningMethods.get(j).compareTo(MiningMethod.MiningMethods_Statistics)==0)
				{
					task = new TaskElement();
					tasks.add(task);
					task.setSourcePath("C:/data/out/route");
					task.setMiningObject(miningObjects.get(i));
					task.setMiningMethod(MiningMethod.MiningMethods_Statistics);
					task.setGranularity(3600);
					task.setTaskRange(TaskRange.WholeNetworkRange);
					task.setTaskName(miningObjects.get(i)+"_"+MiningMethod.MiningMethods_Statistics+"_"+task.getGranularity());
					panelShow.onTaskAdded(task);
					
					System.out.println("get");
				}
				else
				{
					Map<TaskElement, INetworkMiner> tmpminers=new HashMap<TaskElement, INetworkMiner> ();
					
					for(Map.Entry<TaskElement, INetworkMiner> entry:miners.entrySet()) //得到需要的任务
					{
						
						task = entry.getKey();
						if(entry.getKey().getMiningObject().equals(miningObjects.get(i))&&entry.getKey().getMiningMethod().equals(miningMethods.get(j)))
						{
							
							panelShow.onTaskAdded(task);
						}
					}
					
				}
				tasks.add(task);
				
			}
			
		}
		for(int i=0;i<miningMethods.size();i++)
		{
			JButton button = new JButton(miningMethods.get(i).toString());
			buttons.add(button);
			final int index=i;
			button.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					panelShowList.get(tabbedPane.getSelectedIndex()).displayTask(taskList.get(tabbedPane.getSelectedIndex()).get(index));
				}
			});
		}
	
	}
private void initialize() {
		this.setTitle("网络结构变化规律");
		setBounds(100, 100, 1500, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try { 
//			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
           
			    UIManager.setLookAndFeel( new  SubstanceBusinessBlackSteelLookAndFeel());
	            JFrame.setDefaultLookAndFeelDecorated(true);  
	            //设置主题   
//	            SubstanceLookAndFeel.setCurrentTheme(new SubstanceBottleGreenTheme());  
	            //设置按钮外观  
//	            SubstanceLookAndFeel.setSkin(new NebulaBrickWallSkin());
//	            SubstanceLookAndFeel.setCurrentButtonShaper(new  org.jvnet.substance.button.ClassicButtonShaper());  
//	            //设置水印  
//	           // SubstanceLookAndFeel.setCurrentWatermark(new SubstanceBinaryWatermark());  
//	            //设置边框  
	           
//                SubstanceSkin skin = new SaharaSkin().withWatermark(watermark); //初始化有水印的皮肤

//                UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
//                SubstanceLookAndFeel.setSkin(skin); //设置皮肤
              
               
	            SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());  
	            //设置渐变渲染   
	            SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());  
	            //设置标题  
	            SubstanceLookAndFeel.setCurrentTitlePainter( new MatteHeaderPainter());     
			
			 
			
			 
   
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }
//		this.getContentPane().setBackground(Color.RED);
		this.getContentPane().setVisible(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
//		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(200);
		splitPane.setDividerSize(2);
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(null);
		
		splitPane.setLeftComponent(leftPanel);
		
		
		for (int i=0;i<miningMethods.size();i++)  
		{
			MiningMethod method = miningMethods.get(i);
			JButton button = buttons.get(i);
			
			button.setBounds(38, 51+i*100, 134, 27);
			leftPanel.add(button);
		}
            //System.out.println(s + ", ordinal " + s.ordinal());  
		
		getContentPane().add(splitPane);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPane);
		for(int i=0;i<miningObjects.size();i++)
		{
			JPanel panel = panelShowList.get(i);
			tabbedPane.addTab(miningObjects.get(i), null, panel, null);
			
		}
	
		System.out.println(miningMethods.size());
		tabbedPane.addChangeListener(new ChangeListener()
				{

					@Override
					public void stateChanged(ChangeEvent e) {
						// TODO Auto-generated method stub
						if(miningObjectIndex!=tabbedPane.getSelectedIndex())
						{
							miningObjectIndex=tabbedPane.getSelectedIndex();
							
						}
					}
			
				});
	}

}
