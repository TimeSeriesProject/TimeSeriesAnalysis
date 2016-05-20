
package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowNodeFrequence;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowTs;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class SingleNodeFrame extends JFrame{

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
	public SingleNodeFrame(TaskCombination taskCombination)
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
		this.setTitle("结点属性变化规律");
		setBounds(100, 100, 1500, 900);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
		
		
		for (int i=0;i<buttons.size();i++)  
		{
			JButton button = buttons.get(i);
			
			button.setBounds(38, 51+i*100, 134, 27);
			leftPanel.add(button);
		}
            //System.out.println(s + ", ordinal " + s.ordinal());  
		splitPane.setRightComponent(panelShow);;
		getContentPane().add(splitPane);
		
	}

}
