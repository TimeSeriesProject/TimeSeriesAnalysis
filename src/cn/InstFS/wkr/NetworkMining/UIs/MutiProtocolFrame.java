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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.button.ClassicButtonShaper;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.skin.SubstanceSaharaLookAndFeel;
import org.jvnet.substance.title.FlatTitlePainter;
import org.jvnet.substance.title.MatteHeaderPainter;

import cn.InstFS.wkr.NetworkMining.Miner.FrequentItemMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.OutliesMinerFactory;
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
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
//class Menu
public class MutiProtocolFrame extends JFrame{
	
	//Map<TaskElement, INetworkMiner> model;
//	private ArrayList<>
	//private model;
	
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
	int ipIndex=0;
	int protocolIndex=0;
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
					
//					PeriodMinerFactory periodMinerFactory = PeriodMinerFactory.getInstance();
//					periodMinerFactory.dataPath="C:/data/out/traffic/";
//					periodMinerFactory.minerAllPeriods();
					
//					networkMinerFactory.startAllMiners();
					
//					
//					PeriodMinerFactory periodMinerFactory=PeriodMinerFactory.getInstance();
//					periodMinerFactory.dataPath="C:/data/out/traffic/";
//					periodMinerFactory.minerAllPeriods();
//					NetworkMinerFactory.getInstance().startAllMiners(MiningMethod.MiningMethods_PeriodicityMining);
					
//					OutliesMinerFactory outliesMinerFactory =OutliesMinerFactory.getInstance();
//					outliesMinerFactory.dataPath="C:/data/out/traffic/";
//					outliesMinerFactory.detectOutlies();
//					NetworkMinerFactory.getInstance().startAllMiners(MiningMethod.MiningMethods_OutliesMining);
					
//					FrequentItemMinerFactory freItemsFactory=FrequentItemMinerFactory.getInstance();
//					freItemsFactory.dataPath="C:/data/out/traffic/";
//					freItemsFactory.detectFrequntItems();
//					NetworkMinerFactory.getInstance().startAllMiners(MiningMethod.MiningMethods_SequenceMining);

					
					MutiProtocolFrame window = new MutiProtocolFrame();
//					window.setTitle("网络规律挖掘");
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
	MutiProtocolFrame()
	{
		loadModel();
		initModel();
		
		initialize();
		System.out.println("ddddd");
	}
	
	public void loadModel() {
		// TODO Auto-generated method stub
//		MiningMethod method = MiningMethod.MiningMethods_PeriodicityMining;
//		miningMethods.add(method);
		miningMethods.add(MiningMethod.MiningMethods_FrequenceItemMining);
		miningObjects.add("traffic");
		miningObjects.add("通信次数");
//		miningObjects.add("通信跳数");
	}

	
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
		for(int i=0;i<miningMethods.size();i++)
		{
			MiningMethod method = miningMethods.get(i);
			JButton button = new JButton(method.toString());
			
			button.setBounds(38, 51+buttons.size()*80, 150, 27);
			
			
			addPopup(button, popupMenus.get(miningObjectIndex).get(buttons.size()),buttons.size());
			buttons.add(button);
		}
		System.out.println("ff"+popupMenus.get(0).get(0).getComponentCount());
		
	}
	
    void initialize() {
		this.setTitle(("多业务关联分析"));
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
		splitPane.setDividerLocation(220);
		splitPane.setDividerSize(2);
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(null);
		
		splitPane.setLeftComponent(leftPanel);
		
		
		for (int i=0;i<buttons.size();i++)  
		{
			
			leftPanel.add(buttons.get(i));
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
							for (int i=0;i<buttons.size();i++)  
							{
								System.out.println("ssss");
								delPopup(buttons.get(i),i);
							}
							popupListeners.clear();
							for (int i=0;i<buttons.size();i++)  
							{
								if(popupMenus.get(miningObjectIndex).get(i).getComponentCount()>0)
									addPopup(buttons.get(i),popupMenus.get(miningObjectIndex).get(i),i);
							}
							
						}
						
					}
			
				});
	}

	  void addPopup(Component component, final JPopupMenu popup,int index) {
			MouseListener popupListener ;
			component.addMouseListener(popupListener=new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					showMenu(e);
				}
				public void mouseReleased(MouseEvent e) {
					showMenu(e);
				}
				private void showMenu(MouseEvent e) {
					popup.show(e.getComponent(), 0, e.getComponent().getHeight());
					
				}
			});
			popupListeners.put(index,popupListener);

		}

		  void delPopup(Component component,int index) {
			
			System.out.println("p"+popupListeners.size());
			component.removeMouseListener(popupListeners.get(index));
			
		}
}
