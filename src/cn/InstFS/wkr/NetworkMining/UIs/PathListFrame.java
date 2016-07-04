package cn.InstFS.wkr.NetworkMining.UIs;

import cn.InstFS.wkr.NetworkMining.Miner.*;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.title.MatteHeaderPainter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author Arbor
 * @date 2016/5/27
 */
public class PathListFrame extends JFrame{

    ArrayList<JButton> buttons= new ArrayList<JButton>();
    JTable listTable = new JTable();
    JScrollPane scrollPane;
    JComboBox<String> sortTypeComboBox;
    JComboBox<String> miningObjectComboBox;
    JPanel selectPanel = new JPanel();
    HashMap<TaskCombination, MinerResultsPath> resultMap;
    HashMap<String,HashMap<TaskCombination, MinerResultsPath>> resultMaps;  //String区分通信次数、流量
    ArrayList<Map.Entry<TaskCombination, MinerResultsPath> >resultList;
    String sortMethod ="按ip";
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {


                    NetworkMinerFactory.getInstance();
                    PathMinerFactory pathMinerFactory=PathMinerFactory.getInstance();
                    pathMinerFactory.dataPath="E:\\parsePcap\\route\\10.0.1.2_10.0.2.2.csv";
                    pathMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
                    pathMinerFactory.detect();
                    HashMap<TaskCombination, MinerResultsPath> timesMap=NetworkMinerFactory.getInstance().startAllPathMiners(MiningObject.MiningObject_Times);

					pathMinerFactory.reset();
					pathMinerFactory.setMiningObject(MiningObject.MiningObject_Traffic);
					pathMinerFactory.detect();
					HashMap<TaskCombination, MinerResultsPath> trafficMap=NetworkMinerFactory.getInstance().startAllPathMiners(MiningObject.MiningObject_Traffic);
                    HashMap<String, HashMap<TaskCombination, MinerResultsPath>> map=
                            new HashMap<String, HashMap<TaskCombination,MinerResultsPath>>();

					map.put("流量", trafficMap);
                    map.put("通信次数", timesMap);


                    JFrame.setDefaultLookAndFeelDecorated(true);
                    PathListFrame frame = new PathListFrame(map);

                    frame.setVisible(true);

//					PathMinerFactory NodePairMinerFactory=PathMinerFactory.getInstance();
//					NodePairMinerFactory.reset();
//					NodePairMinerFactory.dataPath="C:\\data\\out\\traffic";
//					NodePairMinerFactory.setMiningObject(MiningObject.MiningObject_Times);
//					NodePairMinerFactory.setTaskRange(TaskRange.NodePairRange);
//					NodePairMinerFactory.detect();
//					HashMap<TaskCombination, MinerResultsPath> pairtimesMap=NetworkMinerFactory.getInstance().startAllNodeMiners(MiningObject.MiningObject_Times);
//					
////					
//					HashMap<String, HashMap<TaskCombination, MinerResultsPath>> pairmap=
//							new HashMap<String, HashMap<TaskCombination,MinerResultsPath>>();
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
    public PathListFrame(HashMap<String,HashMap<TaskCombination, MinerResultsPath>> resultMaps) {
        this.resultMaps= new HashMap<String,HashMap<TaskCombination, MinerResultsPath>>();
        for(Map.Entry<String, HashMap<TaskCombination, MinerResultsPath>> entry:resultMaps.entrySet())
        {
            HashMap<TaskCombination, MinerResultsPath> map = new HashMap<TaskCombination, MinerResultsPath>();
            for(Map.Entry<TaskCombination, MinerResultsPath> subentry:entry.getValue().entrySet())
            {
                if(subentry.getKey().getTaskRange().compareTo(TaskRange.NodePairRange)==0)
                {
                    map.put(subentry.getKey(), subentry.getValue());
                }
            }
            this.resultMaps.put(entry.getKey(), map);
        }
        ArrayList<String> miningObjectList = new ArrayList<>(resultMaps.keySet());
        this.resultMap = this.resultMaps.get(miningObjectList.get(0));
//        this.resultMap=this.resultMaps.get("通信次数");
        loadModel();
        initModel();
        initialize();
    }
    public void loadModel() {
        // TODO Auto-generated method stub
//		MiningMethod method = MiningMethod.MiningMethods_PeriodicityMining;
//		miningMethods.add(method);
        resultList=new ArrayList<Map.Entry<TaskCombination, MinerResultsPath>>(resultMap.entrySet());
    }
    private void sort()
    {
        System.out.println("排序"+sortMethod);
        switch(sortMethod)
        {
            case "按ip":
                sortByIP();
                break;
            case "按最小周期":
                sortByPeriodicity();
                System.out.println("排序"+sortMethod);
                break;
            case "按最大异常度":
                sortByOutlies();
                System.out.println("排序"+sortMethod);
                break;
            default:
                break;
//		case "按频繁规律":
//			sortBySequence();
//			System.out.println("排序"+sortMethod);
//			break;
        }
    }
    private void sortByIP()
    {
        Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerResultsPath> >()
        {
            @Override
            public int compare(Map.Entry<TaskCombination,MinerResultsPath> o1, Map.Entry<TaskCombination,MinerResultsPath> o2) {
                return o1.getKey().getRange().compareTo(o2.getKey().getRange());
            }
        });
    }
    private void sortByProtocol()
    {
        Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerResultsPath> >()
        {
            @Override
            public int compare(Map.Entry<TaskCombination,MinerResultsPath> o1, Map.Entry<TaskCombination,MinerResultsPath> o2) {
                return o1.getKey().getProtocol().compareTo(o2.getKey().getProtocol());
            }
        });
    }
    private void sortByPeriodicity()
    {
        Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerResultsPath> >()
        {
            @Override
            public int compare(Map.Entry<TaskCombination,MinerResultsPath> o1, Map.Entry<TaskCombination,MinerResultsPath> o2) {
                if(o1.getValue().getMinPeriod()>o2.getValue().getMinPeriod())
                    return -1;
                else if(o1.getValue().getMinPeriod()<o2.getValue().getMinPeriod())
                    return 1;
                return 0;
            }
        });
    }
    //	private void sortBySequence()
//	{
//		Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerResultsPath> >()
//				{
//					public int compare(Map.Entry<TaskCombination,MinerResultsPath> o1, Map.Entry<TaskCombination,MinerResultsPath> o2) {  
//						System.out.println(o1.getValue().getRetPM().getConfidence());
//						System.out.println(o2.getValue().getRetPM().getConfidence());
//						return (o1.getValue().getRetSM().getAccuracyRatio()-o2.getValue().getRetSM().getAccuracyRatio())>0?-1:1;
//					}
//				});
//	}
    private void sortByOutlies()
    {

        Collections.sort(resultList,new Comparator<Map.Entry<TaskCombination, MinerResultsPath> >()
        {
            @Override
            public int compare(Map.Entry<TaskCombination,MinerResultsPath> o1, Map.Entry<TaskCombination,MinerResultsPath> o2) {

                if(o1.getValue().getMaxOutliesConfidence()>o2.getValue().getMaxOutliesConfidence())
                    return -1;
                else if(o1.getValue().getMaxOutliesConfidence()<o2.getValue().getMaxOutliesConfidence())
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
        resultList=new ArrayList<Map.Entry<TaskCombination, MinerResultsPath>>(resultMap.entrySet());
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
        int size = resultList.size();
        String data[][]=new String[size][7];
        int i = 0;

        for (Map.Entry<TaskCombination, MinerResultsPath> entry : resultList) {
            TaskCombination taskComb = entry.getKey();
            MinerResultsPath retPath = entry.getValue();

            data[i][0] = taskComb.getName();
            data[i][1] = String.valueOf(retPath.getHasPeriod()?"是":"否");
            if (!retPath.getHasPeriod()) {
                data[i][2]="无";
                data[i][3]="无";
            } else {
                data[i][2] = String.format("%d",retPath.getMaxPeriod());
                data[i][3] = String.format("%d",retPath.getMinPeriod());
            }
            data[i][4] = String.valueOf(retPath.getHasOutlies()?"是":"否");
            if (!retPath.getHasOutlies()) {
                data[i][5] = "无";
                data[i][6] = "无";
            } else {
                data[i][5] = String.format("%5d",retPath.getMaxOutliesConfidence());
                data[i][6] = String.format("%5d",retPath.getMinOutliesConfidence());
            }

            i++;
        }

        String colNames[]={"时间序列","是否含有周期","最大周期值","最小周期值","是否有异常","最大异常度","最小异常度"};


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
//        miningObjectComboBox.addItem("流量");
//        miningObjectComboBox.addItem("通信次数");
        miningObjectComboBox.setSelectedIndex(0);
        miningObjectComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            {
                switch (event.getStateChange())
                {
                    case ItemEvent.SELECTED:
                        System.out.println("选中" + event.getItem());
                        resultMap=resultMaps.get(event.getItem());
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
        sortTypeComboBox.addItem("按最小周期");
        sortTypeComboBox.addItem("按最大异常度");
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

        update();
    }

    public void tableChanged()
    {
        int row=listTable.getSelectedRow();
        //得到所在行的第一个列的值，作为下面事件传递的参数
        PathDetailFrame PathFrame=new PathDetailFrame(resultList.get(row).getKey());
        PathFrame.setTitle( "ip: "+resultList.get(row).getKey().getRange()+" 路径 "+resultList.get(row).getKey().getMiningObject()+"规律");
        PathFrame.setVisible(true);
        System.out.println("selectrow:"+row);
    }

    void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("路径规律挖掘");
        setBounds(100, 100, 1500, 900);
        try {
//			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();

            UIManager.setLookAndFeel( new SubstanceBusinessBlackSteelLookAndFeel());
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

}
