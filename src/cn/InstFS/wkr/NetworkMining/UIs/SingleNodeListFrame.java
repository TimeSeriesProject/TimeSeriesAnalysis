package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import oracle.net.aso.a;

import org.apache.ibatis.annotations.Update;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import cn.InstFS.wkr.NetworkMining.Miner.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class SingleNodeListFrame extends JFrame {


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
	JTable listTable = new JTable();
	 JScrollPane scrollPane;
	 JComboBox<String> sortTypeComboBox;
	 JComboBox<String> miningObjectComboBox;
	JPanel selectPanel = new JPanel();
	HashMap<TaskCombination, MinerNodeResults> resultMap;
	HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>> resultMaps;
	ArrayList<Map.Entry<TaskCombination, MinerNodeResults> >resultList;
	String sortMethod ="按周期规律";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NetworkMinerFactory.getInstance();
					SingleNodeOrNodePairMinerFactory freMinerFactory=SingleNodeOrNodePairMinerFactory.getInstance();
					freMinerFactory.dataPath="C:\\data\\out\\traffic";
					freMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
					freMinerFactory.setTaskRange(TaskRange.SingleNodeRange);
					freMinerFactory.detect();
					HashMap<TaskCombination, MinerNodeResults> resultMap = NetworkMinerFactory.getInstance().startAllNodeMiners();
					HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>> tmpresultMaps = new HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>>();
					tmpresultMaps.put(MiningObject.MiningObject_Times,resultMap);
					System.out.println("size "+resultMap.size());
					JFrame.setDefaultLookAndFeelDecorated(true); 
					SingleNodeListFrame frame = new SingleNodeListFrame(tmpresultMaps);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SingleNodeListFrame(HashMap<MiningObject,HashMap<TaskCombination, MinerNodeResults>> resultMaps) {
		this.resultMaps=resultMaps;
		this.resultMap=resultMaps.get(MiningObject.MiningObject_Times);
		loadModel();
		initModel();
		initialize();
	}
	public void loadModel() {
		// TODO Auto-generated method stub
//		MiningMethod method = MiningMethod.MiningMethods_PeriodicityMining;
//		miningMethods.add(method);
		resultList=new ArrayList<Map.Entry<TaskCombination, MinerNodeResults>>(resultMap.entrySet());
	}
	private void sort()
	{
		System.out.println("排序"+sortMethod);
		switch(sortMethod)
		{
		case "按周期规律":
			sortByPeriodicity();
			System.out.println("排序"+sortMethod);
			break;
		case "按异常规律":
			sortByOutlies();
			System.out.println("排序"+sortMethod);
			break;
		case "按频繁规律":
			sortBySequence();
			System.out.println("排序"+sortMethod);
			break;
		}
	}
	private void sortByPeriodicity()
	{
		System.out.println("why"+sortMethod);
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						System.out.println(o1.getValue().getRetPM().getConfidence());
						System.out.println(o2.getValue().getRetPM().getConfidence());
						return (o1.getValue().getRetPM().getConfidence()-o2.getValue().getRetPM().getConfidence())>0?-1:1;
					}
				});
	}
	private void sortBySequence()
	{
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						System.out.println(o1.getValue().getRetPM().getConfidence());
						System.out.println(o2.getValue().getRetPM().getConfidence());
						return (o1.getValue().getRetSM().getAccuracyRatio()-o2.getValue().getRetSM().getAccuracyRatio())>0?-1:1;
					}
				});
	}
	private void sortByOutlies()
	{
		System.out.println("why"+sortMethod);
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						System.out.println(o1.getValue().getRetPM().getConfidence());
						System.out.println(o2.getValue().getRetPM().getConfidence());
						return (o1.getValue().getRetOM().getConfidence()-o2.getValue().getRetOM().getConfidence())>0?-1:1;
					}
				});
	}
	private void update()
	{
		System.out.println("更新");
		if(listTable!=null)
		scrollPane.remove(listTable);
		resultList=new ArrayList<Map.Entry<TaskCombination, MinerNodeResults>>(resultMap.entrySet());
		sort();
		createTable();
	}
	private void createTable()
	{
		String data[][]=new String[resultList.size()][8];
		for(int i=0;i<resultList.size();i++)
		{
			TaskCombination taskCom =resultList.get(i).getKey();
			MinerNodeResults results = resultList.get(i).getValue();
			data[i][0]="ip "+taskCom.getRange()+"protocol "+taskCom.getProtocol();
			data[i][1]=String.valueOf(results.getRetStatistics().getMean());
			data[i][2]=String.valueOf(results.getRetStatistics().getStd());
			data[i][3]=String.valueOf(results.getRetStatistics().getSampleENtropy());
			data[i][4]=String.valueOf(results.getRetStatistics().getComplex());
			data[i][5]=String.valueOf(results.getRetPM().getPeriod());
			data[i][6]=String.valueOf(results.getRetOM().isHasOutlies()==true?"是":"否");
			data[i][7]=String.valueOf(results.getRetSM().isHasFreItems()==true?"是":"否");
			
			
		}
	    String colNames[]={"时间序列名","平均值","标准差","样本熵","复杂度","周期","是否有异常","是否存在频繁项"};
	   
	 
	    DefaultTableModel model=new DefaultTableModel(data,colNames){
	        public   boolean   isCellEditable(int   row,   int   column)   
	         {   
	         return   false;   
	         };
	     
	      };    
	    listTable = new JTable(model);
	    listTable.addMouseListener(new MouseAdapter(){
	       
	    public void mouseClicked(MouseEvent e) {
	        
	          if(e.getClickCount()==2){//点击几次，这里是双击事件
	        	System.out.println("kkkkk");
	           tableChanged();        
	          }
	       }
	      });
//		    listTable.addMouseListener(new MouseAdapter(){
//		    public void mouseClicked(final MouseEvent e) {
//		    	  if (SwingUtilities.isRightMouseButton(e)) {
//		    	 int row = listTable.getSelectedRow(); // 获得当前选中的行号
//		         System.out.println(row);
//		         listTable.getComponent(row);
//		         
//		    	  }
//		    	} 
//		     });
	    final JPopupMenu popupMenu = new JPopupMenu();
	    JMenuItem menu = new JMenuItem("显示详细结果");
	    popupMenu.add(menu);
	    menu.addActionListener(new ActionListener()
		{
	    	
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tableChanged();
//								
			}
		});
	    listTable.addMouseListener(new MouseAdapter() {
	    	 
            public void mouseReleased(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    if (evt.isPopupTrigger()) {
                        //  取得右键点击所在行
                        int row = evt.getY() / listTable.getRowHeight();
                        listTable.setRowSelectionInterval(row,row);
                        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                }
            }
        });  
		   
	}
	public void initModel()
	{
		selectPanel.setLayout(new FlowLayout());
		JLabel objectLabel=new JLabel("选择挖掘对象");
		selectPanel.add(objectLabel);
		miningObjectComboBox = new JComboBox<String>();
		miningObjectComboBox.addItem("流量");
		miningObjectComboBox.addItem("通信次数");
		miningObjectComboBox.setSelectedIndex(1);
//		miningObjectComboBox.addItem("结点出现消失");
		miningObjectComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            {
                switch (event.getStateChange())
                {
                case ItemEvent.SELECTED: 
                    System.out.println("选中" + event.getItem());
                    if(event.getItem().equals("流量"))
                    {
                    	resultMap=resultMaps.get(MiningObject.MiningObject_Traffic);
                    	
                    }
                    else if(event.getItem().equals("通信次数"))
                    {
                    	resultMap=resultMaps.get(MiningObject.MiningObject_Times);
                    }
                    else if(event.getItem().equals("结点出现消失"))
                    {
                    	//resultMap=resultMaps.get(MiningObject.MiningObject_Traffic);
                    }
                    update();
                    break;
                case ItemEvent.DESELECTED:
                    System.out.println("取消选中" + event.getItem());
                    break;
                }
            }
        });
		selectPanel.add(miningObjectComboBox);
		
		JLabel sortLabel= new JLabel("选择排序方式");
		sortTypeComboBox = new JComboBox<String>();
		sortTypeComboBox.addItem("按周期规律");
		sortTypeComboBox.addItem("按异常规律");
		sortTypeComboBox.addItem("按频繁规律");
		sortTypeComboBox.setSelectedIndex(0);
		sortTypeComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            { System.out.println("选中");
                switch (event.getStateChange())
                {
                case ItemEvent.SELECTED: 
                    System.out.println("选中" + event.getItem());
                    sortMethod=event.getItem().toString();
                    System.out.println(sortMethod);
                    update();
                    break;
                case ItemEvent.DESELECTED:
                    System.out.println("取消选中" + event.getItem());
                    break;
                }
            }
        });
		selectPanel.add(sortLabel);
		selectPanel.add(sortTypeComboBox);
	    scrollPane = new JScrollPane();
	   
	    createTable();	  
	    scrollPane.setViewportView(listTable);
	}
	
	public void tableChanged()
	 {
	  int row=listTable.getSelectedRow();    
	  //得到所在行的第一个列的值，作为下面事件传递的参数
	  SingleNodeFrame SingleNodeFrame=new SingleNodeFrame(resultList.get(row).getKey());
	  SingleNodeFrame.setVisible(true);
	  System.out.println("selectrow:"+row);
	 }
	
	void initialize() {
//		setDefaultCloseOperation(JFrame.);
		setTitle("结点属性规律");
		setBounds(100, 100, 1500, 900);
		try { 
//			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
           
			    UIManager.setLookAndFeel( new  SubstanceBusinessBlackSteelLookAndFeel());
	            JFrame.setDefaultLookAndFeelDecorated(true);  
               
	            SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());  
	            //设置渐变渲染   
	            SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());  
	            //设置标题  
	            SubstanceLookAndFeel.setCurrentTitlePainter( new MatteHeaderPainter());     
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }
		getContentPane().add(selectPanel,BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	 void addPopup(Component component, final JPopupMenu popup) {
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

		}
}
