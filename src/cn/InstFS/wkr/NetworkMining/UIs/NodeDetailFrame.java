
package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class NodeDetailFrame extends JFrame{

	public static void main(String[] args) {
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
	TaskCombination taskCombination;
	PanelShowAllResults   panelShow = new PanelShowAllResults();
	ArrayList<JButton> buttons= new ArrayList<JButton>();
	public NodeDetailFrame(TaskCombination taskCombination)
	{
		this.taskCombination=taskCombination;
		
		initModel();
		initialize();
	}
	
	public void initModel() {
		// TODO Auto-generated method stub
//		NetworkMinerFactory networkMinerFactory =NetworkMinerFactory.getInstance();
	
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
////		
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
		if(buttons.size()>0)
			buttons.get(0).doClick();
	}

}
