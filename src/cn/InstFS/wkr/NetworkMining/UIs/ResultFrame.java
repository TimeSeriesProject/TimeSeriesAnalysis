package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.button.ClassicButtonShaper;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;
import org.jvnet.substance.skin.FieldOfWheatSkin;
import org.jvnet.substance.skin.MistAquaSkin;
import org.jvnet.substance.skin.NebulaBrickWallSkin;
import org.jvnet.substance.skin.SaharaSkin;
import org.jvnet.substance.skin.SubstanceAutumnLookAndFeel;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;
import org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel;
import org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel;
import org.jvnet.substance.skin.SubstanceCremeLookAndFeel;
import org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel;
import org.jvnet.substance.skin.SubstanceMagmaLookAndFeel;
import org.jvnet.substance.skin.SubstanceSaharaLookAndFeel;
import org.jvnet.substance.skin.SubstanceSkin;
import org.jvnet.substance.theme.SubstanceBottleGreenTheme;
import org.jvnet.substance.title.FlatTitlePainter;
import org.jvnet.substance.title.MatteHeaderPainter;
import org.jvnet.substance.utils.SubstanceConstants.ImageWatermarkKind;
import org.jvnet.substance.watermark.SubstanceImageWatermark;

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.PeriodMinerFactory;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.IPanelShowResults;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsFP;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsPM;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsPP;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsSM;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public abstract class ResultFrame extends JFrame {

//	private JPanel contentPane;

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
	 * Create the frame.
	 */
	
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
	public ResultFrame() {
		
		loadModel();
		initModel();
		initialize();
	}
	public  abstract void loadModel() ;
		// TODO Auto-generated method stub
	public abstract void initModel();
	
    void initialize() {
		
		setBounds(100, 100, 1120, 763);
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
			JButton button = new JButton(method.toString());
			
			button.setBounds(38, 51+i*100, 134, 27);
			leftPanel.add(button);
			
			addPopup(button, popupMenus.get(miningObjectIndex).get(i),i);
			buttons.add(button);
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
							for (int i=0;i<miningMethods.size();i++)  
							{
								System.out.println("ssss");
								delPopup(buttons.get(i),i);
							}
							popupListeners.clear();
							for (int i=0;i<miningMethods.size();i++)  
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
