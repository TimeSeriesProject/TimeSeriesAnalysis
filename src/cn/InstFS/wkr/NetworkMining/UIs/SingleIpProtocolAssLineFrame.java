package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import lineAssociation.Linear;
import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultAssLine;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
/**
 * @author LYH
 * @description 用于测试关联规则线段化结果
 * */
public class SingleIpProtocolAssLineFrame extends JFrame{
	MinerProtocolResults mProtocolResult = null;
	TaskCombination taskCombination=null;
	List<ProtoclPair> protocolPairList = null;
	List<TreeMap<Integer, Linear>> linesList = new ArrayList<TreeMap<Integer,Linear>>();
	ArrayList<JButton> buttons = new ArrayList<JButton>();
	PanelShowResultAssLine panelShowResultAssLine = null;
	DataItems dataItems1 = new DataItems();
	JFreeChart chart;
	public SingleIpProtocolAssLineFrame(){}
	public SingleIpProtocolAssLineFrame(TaskCombination taskCombination,
			HashMap<TaskCombination, MinerProtocolResults> resultMaps) {
		this();
		this.setBounds(100,100,1500,900);
		this.taskCombination = taskCombination;
		mProtocolResult = resultMaps.get(taskCombination);
		protocolPairList=mProtocolResult.getRetFP().getProtocolPairList();
		linesList = mProtocolResult.getRetFP().getLinesList();
		dataItems1 = protocolPairList.get(0).getDataItems1();
		display();
	}
	//
	public void initModel(){
		List<TaskElement> taskList = taskCombination.getTasks();

        for(int i=0;i<taskList.size();i++)
        {
            final TaskElement task = taskList.get(i);
            //panelShow.onTaskAdded(task);
            JButton button = new JButton("序列线段化");
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    //panelShow.displayTask(task);
                	panelShowResultAssLine = new PanelShowResultAssLine(dataItems1, linesList);
                	chart = panelShowResultAssLine.createChart(dataItems1, linesList);
                }
            });
            buttons.add(button);
        }
	}
	public void initialize(){
		setBounds(100, 100, 1500, 900);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.getContentPane().setVisible(true);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        panelShowResultAssLine = new PanelShowResultAssLine(dataItems1, linesList);
    	chart = panelShowResultAssLine.createChart(dataItems1, linesList);
        
    	JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(2);
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        leftPanel.setLayout(null);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        ChartPanel chartpanel = new ChartPanel(chart);
        rightPanel.add(chartpanel);
        rightPanel.setVisible(true);
        for (int i=0;i<buttons.size();i++)
        {
            JButton button = buttons.get(i);

            button.setBounds(38, 51+i*100, 134, 27);
            leftPanel.add(button);
        }        
        getContentPane().add(splitPane);
        if(buttons.size()>0)
            buttons.get(0).doClick();
    }
	public void display(){
		dataItems1 = normalize(dataItems1);
		panelShowResultAssLine = new PanelShowResultAssLine(dataItems1, linesList);
    	chart = panelShowResultAssLine.createChart(dataItems1, linesList);
    	ChartPanel chartpanel = new ChartPanel(chart);
    	add(chartpanel);
    	this.setVisible(true);
	}
	private DataItems normalize(DataItems di){
		DataItems dataItems = new DataItems();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for(int i=0;i<di.getLength();i++){
			double data = Double.parseDouble(di.getData().get(i));
			if(data>max){
				max = data;
			}else if(data<min){
				min = data;
			}
		}
		for(int i=0;i<di.getLength();i++){
			double data = Double.parseDouble(di.getData().get(i));
			data = (data-min)/(max-min);
			Date time = di.getTime().get(i);
			dataItems.add1Data(time,String.valueOf(data));
		}
		return dataItems;
	}

}
