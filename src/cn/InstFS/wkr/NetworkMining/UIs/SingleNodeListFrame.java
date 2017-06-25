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
import java.io.*;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.PatternMent;
import cn.InstFS.wkr.NetworkMining.Miner.Common.LineElement;
import common.Logger;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.GlobalConfig;
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
	HashMap<String,HashMap<TaskCombination, MinerNodeResults>> resultMaps;
	ArrayList<Map.Entry<TaskCombination, MinerNodeResults> >resultList;
	String sortMethod ="按周期置信度";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					
					NetworkMinerFactory.getInstance();
					SingleNodeOrNodePairMinerFactory singleNodeMinerFactory=SingleNodeOrNodePairMinerFactory.getInstance();
					singleNodeMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
					singleNodeMinerFactory.setTaskRange(TaskRange.SingleNodeRange);
					singleNodeMinerFactory.detect();
					HashMap<TaskCombination, MinerNodeResults> timesMap=NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Times);
					
//					singleNodeMinerFactory.reset();
//					singleNodeMinerFactory.setMiningObject(MiningObject.MiningObject_Traffic);
//					singleNodeMinerFactory.detect();
//					HashMap<TaskCombination, MinerNodeResults> trafficMap=NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Traffic);
					HashMap<String, HashMap<TaskCombination, MinerNodeResults>> map=
							new HashMap<String, HashMap<TaskCombination,MinerNodeResults>>();
					
//					map.put("流量", trafficMap);
					map.put("通信次数", timesMap);
					
				
					JFrame.setDefaultLookAndFeelDecorated(true); 
					SingleNodeListFrame frame = new SingleNodeListFrame(map);
					
					frame.setVisible(true);
					
//					SingleNodeOrNodePairMinerFactory NodePairMinerFactory=SingleNodeOrNodePairMinerFactory.getInstance();
//					NodePairMinerFactory.reset();
//					NodePairMinerFactory.dataPath="C:\\data\\out\\traffic";
//					NodePairMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
//					NodePairMinerFactory.setTaskRange(TaskRange.NodePairRange);
//					NodePairMinerFactory.detect();
//					HashMap<TaskCombination, MinerNodeResults> pairtimesMap=NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Times);
//					
////					
//					HashMap<String, HashMap<TaskCombination, MinerNodeResults>> pairmap=
//							new HashMap<String, HashMap<TaskCombination,MinerNodeResults>>();
//					
////					map.put("流量", trafficMap);
//					pairmap.put("通信次数", pairtimesMap);
//					NodePairListFrame pairFrame = new NodePairListFrame(pairmap);
//					pairFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SingleNodeListFrame(HashMap<String,HashMap<TaskCombination, MinerNodeResults>> resultMaps) {
		this.resultMaps= new HashMap<String,HashMap<TaskCombination, MinerNodeResults>>();
		for(Map.Entry<String, HashMap<TaskCombination, MinerNodeResults>> entry:resultMaps.entrySet())
		{
			HashMap<TaskCombination, MinerNodeResults> map = new HashMap<TaskCombination, MinerNodeResults>();
			for(Map.Entry<TaskCombination, MinerNodeResults> subentry:entry.getValue().entrySet())
			{
				if(subentry.getKey().getTaskRange().compareTo(TaskRange.SingleNodeRange)==0)
				{
					map.put(subentry.getKey(), subentry.getValue());
				}
			}
			this.resultMaps.put(entry.getKey(), map);
		}

		ArrayList<String> miningObjectList = new ArrayList<>(resultMaps.keySet());
		this.resultMap = this.resultMaps.get(miningObjectList.get(0));
//		this.resultMap=this.resultMaps.get("通信次数");
		Logger.log("===============================================================");
		Logger.log("挖掘结果列表显示");
		loadModel();
		initModel();
		initialize();
	}
	public void loadModel() {
		// TODO Auto-generated method stub
//		MiningMethod method = MiningMethod.MiningMethods_PeriodicityMining;
//		miningMethods.add(method);
		resultList=new ArrayList<Map.Entry<TaskCombination, MinerNodeResults>>(resultMap.entrySet());

		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for (MinerNodeResults rslt: resultMap.values()) {
			if (rslt.getRetSM().isHasFreItems()) {
				DataItems freqPatterns = rslt.getRetSM().getPatterns();    // 频繁模式
				List<LineElement> lineElements = rslt.getRetSM().getLineElements();
				final HashMap<Integer, List<List<LineElement>>> modeList = new HashMap<>();
				DataItems freqResult = new DataItems();

				List<String> freqPatternsListTemp = new ArrayList<>(freqPatterns.getData());
				int len = freqPatternsListTemp.size() - 1;
				for (; len >= 0; len--) {
					String tempString = freqPatternsListTemp.get(len);
					List<String> deletedPatterns = new ArrayList<>();
					for (int j = 0; j < len; j++) {
						String tempStringDelete = freqPatternsListTemp.get(j);

						// 判断是否包含在其他模式之中
						if (tempString.contains("," + tempStringDelete + ",")) { // 子串在中间位置
							deletedPatterns.add(tempStringDelete);
						} else if ((tempString.indexOf(tempStringDelete) + tempStringDelete.length()) == tempString.length()  // 子串在末位
								&& tempString.contains("," + tempStringDelete)) {
							deletedPatterns.add(tempStringDelete);
						} else if (tempString.indexOf(tempStringDelete) == 0 && tempString.contains(tempStringDelete + ",")) { // 子串在起始位
							deletedPatterns.add(tempStringDelete);
						}
					}
					DataItem di = new DataItem();
					di.setData(tempString);
					int index = freqPatterns.getData().indexOf(tempString);
					di.setProb(freqPatterns.getProb().get(index));
					freqResult.add1Data(di);

					freqPatternsListTemp.removeAll(deletedPatterns);
					freqPatternsListTemp.remove(tempString);
					len = freqPatternsListTemp.size();

				}
				freqPatterns = freqResult;
				for (double data : freqPatterns.getProb()) {
					statistics.addValue(data);
				}
			}
		}
		double mean = statistics.getMean();
		OutputStreamWriter ow = null;
		try {
			ow = new OutputStreamWriter(
					new FileOutputStream("testResult/频繁模式测试.txt",true), "UTF-8");
			BufferedWriter bw = new BufferedWriter(ow);
			bw.newLine();
			bw.write("平均置信度: "+ mean);
			bw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sort()
	{
		System.out.println("排序"+sortMethod);
		switch(sortMethod)
		{
		case "按ip":
			sortByIP();
			break;
		case "按协议":
			sortByProtocol();
			break;
		case "按周期置信度":
			sortByPeriodicity();
			System.out.println("排序"+sortMethod);
			break;
		case "按异常度":
			sortByOutlies();
			System.out.println("排序"+sortMethod);
			break;
//		case "按频繁规律":
//			sortBySequence();
//			System.out.println("排序"+sortMethod);
//			break;
		}
	}
	private void sortByIP()
	{
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					@Override
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						return o1.getKey().getRange().compareTo(o2.getKey().getRange());
					}
				});
	}
	private void sortByProtocol()
	{
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					@Override
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						return o1.getKey().getProtocol().compareTo(o2.getKey().getProtocol());
					}
				});
	}
	private void sortByPeriodicity()
	{
		System.out.println("why"+sortMethod);
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					@Override
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
						if(o1.getValue().getRetPM().getConfidence()>o2.getValue().getRetPM().getConfidence())
							return -1;
						else if(o1.getValue().getRetPM().getConfidence()<o2.getValue().getRetPM().getConfidence())
							return 1;
						return 0;
					}
				});
	}
//	private void sortBySequence()
//	{
//		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
//				{
//					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
//						System.out.println(o1.getValue().getRetPM().getConfidence());
//						System.out.println(o2.getValue().getRetPM().getConfidence());
//						return (o1.getValue().getRetSM().getAccuracyRatio()-o2.getValue().getRetSM().getAccuracyRatio())>0?-1:1;
//					}
//				});
//	}
	private void sortByOutlies()
	{
		
		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerNodeResults> >()
				{
					@Override
					public int compare(Map.Entry<TaskCombination,MinerNodeResults> o1, Map.Entry<TaskCombination,MinerNodeResults> o2) {  
					
						if(getMax(o1.getValue().getRetOM().getOutDegree()) > getMax(o2.getValue().getRetOM().getOutDegree()))
							return -1;
						else if(getMax(o1.getValue().getRetOM().getOutDegree()) < getMax(o2.getValue().getRetOM().getOutDegree()))
							return 1;
						return 0;
					}
				});
	}
	private void update()
	{
		System.out.println("更新");
		if(listTable!=null)
		{
		scrollPane.remove(listTable);
		System.out.println("remove");
		}
		resultList=new ArrayList<Map.Entry<TaskCombination, MinerNodeResults>>(resultMap.entrySet());
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
		String data[][]=new String[resultList.size()][12];
		for(int i=0;i<resultList.size();i++)
		{
			TaskCombination taskCom =resultList.get(i).getKey();
			MinerNodeResults results = resultList.get(i).getValue();
			data[i][0]="ip: "+taskCom.getRange()+" protocol: "+taskCom.getProtocol();
			data[i][1]=String.format("%5.3f",results.getRetStatistics().getMean());
			data[i][2]=String.format("%5.3f",results.getRetStatistics().getStd());
			data[i][3]=String.format("%5.3f",results.getRetStatistics().getSampleENtropy());
			data[i][4]=String.format("%5.3f",results.getRetStatistics().getComplex());
			data[i][5]=String.format("%d",results.getRetPM().getPeriod());
			if(results.getRetPM().getPeriod()<=0)
				data[i][5]="无";
			data[i][6]=String.format("%5.3f",results.getRetPM().getConfidence());//周期置信度
			data[i][7]=String.valueOf(results.getRetOM().isHasOutlies()==true?"是":"否");//是否有异常
			data[i][8]=String.format("%5.3f",getMax(results.getRetOM().getOutDegree()));
			data[i][9]=String.format("%5.3f",getMin(results.getRetOM().getOutDegree()));
			data[i][10]=String.valueOf(results.getRetSM().isHasFreItems()==true?"是":"否");//是否有频繁项
			data[i][11] = String.valueOf(results.getRetOM().getOutlierAlgo()); // 使用的异常算法
			
		}
	    String colNames[]={"时间序列","平均值","标准差","样本熵","复杂度","周期","周期置信度","是否有异常","最大异常度","最小异常度","是否存在频繁项","异常算法"};
	   
	 
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
//	        	System.out.println("kkkkk");
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
//		scrollPane.add(listTable); 
		scrollPane.setViewportView(listTable);
//		scrollPane.updateUI();
//		scrollPane.repaint();
		
	}
	public void initModel()
	{
		selectPanel.setLayout(new FlowLayout());
		JLabel objectLabel=new JLabel("选择挖掘对象");
		selectPanel.add(objectLabel);
		miningObjectComboBox = new JComboBox<String>();
		ArrayList<String> miningObjectList = new ArrayList<>(resultMaps.keySet());
		for (String s: miningObjectList)
			miningObjectComboBox.addItem(s);

//		miningObjectComboBox.addItem("流量");
//		miningObjectComboBox.addItem("通信次数");
		miningObjectComboBox.setSelectedIndex(0);
//		miningObjectComboBox.addItem("结点出现消失");
		miningObjectComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            {
                switch (event.getStateChange())
                {
                case ItemEvent.SELECTED: 
                    System.out.println("选中" + event.getItem());
                    resultMap=resultMaps.get(event.getItem());
//                    if(event.getItem().equals("流量"))
//                    {
//                    	resultMap=resultMaps.get("流量");
//                    	
//                    }
//                    else if(event.getItem().equals("通信次数"))
//                    {
//                    	resultMap=resultMaps.get("通信次数");
//                    }
//                    else if(event.getItem().equals("结点出现消失"))
//                    {
//                    	//resultMap=resultMaps.get(MiningObject.MiningObject_Traffic);
//                    }
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
		sortTypeComboBox.addItem("按ip");
		sortTypeComboBox.addItem("按协议");
		sortTypeComboBox.addItem("按周期置信度");
		sortTypeComboBox.addItem("按异常度");
		sortTypeComboBox.setSelectedIndex(2);
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
	   
	   update();	  
	}
	
	public void tableChanged()
	 {
	  int row=listTable.getSelectedRow();    
	  //得到所在行的第一个列的值，作为下面事件传递的参数
	  NodeDetailFrame SingleNodeFrame=new NodeDetailFrame(resultList.get(row).getKey());
	  SingleNodeFrame.setTitle( "ip: "+resultList.get(row).getKey().getRange()+" protocol: "+resultList.get(row).getKey().getProtocol()+" "+resultList.get(row).getKey().getMiningObject()+"规律");
	  SingleNodeFrame.setVisible(true);
	  System.out.println("selectrow:"+row);
	 }
	
	void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
	 public double getMax(DataItems di){
		 double max = 0;
		 if(di.getData().isEmpty()){
			 return 0;
		 }
		 for(int i=0;i<di.getData().size();i++){
			 double data = Double.parseDouble(di.getData().get(i));
			 if(data>max){
				 max = data;
			 }
		 }
		 return max;
	 }
	 public double getMin(DataItems di){
		 double min = 1;
		 for(int i=0;i<di.getData().size();i++){
			 double data = Double.parseDouble(di.getData().get(i));
			 if(data<min){
				 min = data;
			 }
		 }
		 return min;
	 }
}
