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
import java.util.Enumeration;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import oracle.net.aso.a;

import org.apache.ibatis.annotations.Update;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import cn.InstFS.wkr.NetworkMining.Miner.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResultsFP_Line;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.ProtocolAssMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class AssociationIpListFrame extends JFrame {


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
	HashMap<TaskCombination, MinerProtocolResults> resultMap;
	HashMap<MiningObject,HashMap<TaskCombination, MinerProtocolResults>> resultMaps;
	ArrayList<Map.Entry<TaskCombination, MinerProtocolResults> > resultList;
	String sortMethod = "按ip协议整体置信度排序";
	String mingObj = "";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
//					ProtocolAssMinerFactory.getInstance();
					NetworkMinerFactory.getInstance();
					ProtocolAssMinerFactory protocolAssFactory = ProtocolAssMinerFactory.getInstance();
					protocolAssFactory.dataPath="D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\data\\out\\traffic";
					protocolAssFactory.setMiningObject(MiningObject.MiningObject_Traffic);
//					protocolAssFactory.setTaskRange(TaskRange.SingleNodeRange);
					protocolAssFactory.detect();
					
					HashMap<TaskCombination, MinerProtocolResults> resultMap = NetworkMinerFactory.getInstance().startAllProtocolMiners(MiningObject.MiningObject_Traffic);
					
					System.out.println("size &&&"+resultMap.size());
					
					JFrame.setDefaultLookAndFeelDecorated(true); 
					AssociationIpListFrame frame = new AssociationIpListFrame(resultMap);
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
	public AssociationIpListFrame(HashMap<TaskCombination, MinerProtocolResults> tmpResultMap) {
		
		resultMap = tmpResultMap;
		loadModel();
		initModel();
		initialize();
	}

	public void loadModel() {
		// TODO Auto-generated method stub
//		MiningMethod method = MiningMethod.MiningMethods_PeriodicityMining;
//		miningMethods.add(method);
		resultList = new ArrayList<Map.Entry<TaskCombination, MinerProtocolResults>>(resultMap.entrySet());
	}
	
	private void update()
	{
		System.out.println("更新显示结果");
		if(listTable!=null)
		{
			//		scrollPane.remove(listTable);
			System.out.println("remove");
		}
		resultList = new ArrayList<Map.Entry<TaskCombination, MinerProtocolResults>>(resultMap.entrySet());
		sort();
		createTable();
		
	}
	public void fitTableColumns(JTable myTable)
    {
         myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         JTableHeader header = myTable.getTableHeader();
         int rowCount = myTable.getRowCount();
         Enumeration columns = myTable.getColumnModel().getColumns();
         while(columns.hasMoreElements())
         {
             TableColumn column = (TableColumn)columns.nextElement();
             int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
             int width = (int)header.getDefaultRenderer().getTableCellRendererComponent
             (myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
             for(int row = 0; row < rowCount; row++)
             {
                 int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent
                 (myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                 width = Math.max(width, preferedWidth);
             }
             header.setResizingColumn(column); // 此行很重要
             column.setWidth(width+myTable.getIntercellSpacing().width);
         }
    }
    
	private void createTable()
	{
		String data[][]=new String[resultList.size()][3];
		for(int i=0;i<resultList.size();i++)
		{
			TaskCombination taskCom = resultList.get(i).getKey();
			MinerResultsFP_Line results = resultList.get(i).getValue().getRetFP();
			
			data[i][0]= results.getIp();
			data[i][1]= String.format("%5.3f",results.getConfidence());
			data[i][2]=String.format("%5d",results.protocolPairList.size());
			
		}
	    String colNames[]={"ip","整体置信度","关联协议对数目"};
	   
	 
	    DefaultTableModel model=new DefaultTableModel(data,colNames){
	        public   boolean   isCellEditable(int   row,   int   column)   
	        {   
	        	return   false;   
	        };
	     
	      };    
	     
	    listTable = new JTable(model);
	    listTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    listTable.setAutoscrolls(true);
	    listTable.getColumnModel().getColumn(0).setPreferredWidth(180);
//	    listTable.setRowHeight(35);
	    listTable.addMouseListener(new MouseAdapter(){
	       
	    	public void mouseClicked(MouseEvent e) {
	        
	    		if(e.getClickCount()==2){//点击几次，这里是双击事件
	    			System.out.println("双击跳转");
	    			tableChanged();        
	    		}
	    	}
	      });

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
//		scrollPane.add(listTable); 
		scrollPane.setViewportView(listTable);
		
	}
	public void initModel()
	{
		System.out.println("initModel....");

		JLabel sortLabel= new JLabel("选择排序方式");
		sortTypeComboBox = new JComboBox<String>();
		sortTypeComboBox.addItem("按ip排序");
		sortTypeComboBox.addItem("按ip协议整体置信度排序");
		sortTypeComboBox.setSelectedIndex(1);
		sortTypeComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            { 
            	System.out.println("选中");
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
	   update();	  
	}
	
	public void tableChanged()
	{
	  int row=listTable.getSelectedRow();    
	  //得到所在行的第一个列的值，作为下面事件传递的参数
	  SingleIpProtocolAssFrame singleIpProtocol = new SingleIpProtocolAssFrame(resultList.get(row).getKey(),resultMap);
	  singleIpProtocol.setTitle( "ip: "+resultList.get(row).getKey().getRange()+" protocol: "+resultList.get(row).getKey().getProtocol()+" "+resultList.get(row).getKey().getMiningObject()+"规律");
	  singleIpProtocol.setVisible(true);
	  System.out.println("selectrow:"+row);
	 }
	
	void initialize() {
		System.out.println("initialize....");
		setTitle("ip协议之间的关联");
		setBounds(100, 100, 1500, 900);
		try { 
           
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
	 private void sort()
		{
			System.out.println("排序"+sortMethod);
			switch(sortMethod)
			{
			case "按ip排序":
				sortByIP();
				break;
			case "按ip协议整体置信度排序":
				sortByProtocolConfidence();
				break;
			default :
				sortByProtocolConfidence();  //默认按部分关联度排序
				break;
			}
		}
		private void sortByIP()
		{
			Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerProtocolResults> >()
					{
						@Override
						public int compare(Map.Entry<TaskCombination,MinerProtocolResults> o1, Map.Entry<TaskCombination,MinerProtocolResults> o2) {  
							System.out.println(o1.getValue().getRetFP().getIp());
							System.out.println(o2.getValue().getRetFP().getIp());
							return o1.getValue().getRetFP().getIp().compareTo(o2.getValue().getRetFP().getIp());
						}
					});
		}
		private void sortByProtocolConfidence()
		{
			Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerProtocolResults> >()
					{
						@Override
						public int compare(Map.Entry<TaskCombination,MinerProtocolResults> o1, Map.Entry<TaskCombination,MinerProtocolResults> o2) {  
							System.out.println(o1.getValue().getRetFP().getConfidence());
							System.out.println(o2.getValue().getRetFP().getConfidence());
							if(o1.getValue().getRetFP().getConfidence() > o2.getValue().getRetFP().getConfidence())
								return -1;
							else if(o1.getValue().getRetFP().getConfidence() < o2.getValue().getRetFP().getConfidence())
								return 1;
							else 
								return 0;
						}
					});
		}

}
