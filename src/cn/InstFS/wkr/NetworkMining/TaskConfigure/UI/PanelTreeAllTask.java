package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class PanelTreeAllTask extends JScrollPane implements
		ITaskElementEventListener {

	public static DefaultListModel lmodel = new DefaultListModel();
	public static List<DefaultMutableTreeNode> miningType = new ArrayList<DefaultMutableTreeNode>();
	public static JList listTasks = new JList();
	ITaskDisplayer panelDisplayTask;
	Window floatWindow;
	JPopupMenu popupMenu = new JPopupMenu();
	public static JTree jtree = new JTree();
	/**
	 * Create the panel.
	 */
	public PanelTreeAllTask(ITaskDisplayer panelDisplayTask) {
		this.panelDisplayTask = panelDisplayTask;
//		listTasks = new JList();
		
		listTasks.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
//				displayTaskConfig(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
//				undisplayTaskConfig();
			}
		});
		
		updateLister();
		setColumnHeaderView(new JLabel("已配事件列表："));
//		setViewportView(new JPanel().add(jtree));
	
		
		
		addPopup(listTasks, popupMenu);

		JMenuItem menuStart = new JMenuItem("开始挖掘");
		menuStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();  
				TreeNode[] path = node.getPath();
				String name = "";
				name = "["+path[0].toString();
				for(int i = 1;i < path.length;i++)
				{
					name += ", "+path[i].toString();
					
				}
				name += "]";
				TaskElement ee = findTask(name);
				if (ee != null)
				{
					System.out.println("成功找到待开始的task");
					NetworkMinerFactory.getInstance().startMiner(ee);
				}
			}
		});
		popupMenu.add(menuStart);

		JMenuItem menuStop = new JMenuItem("停止挖掘");
		menuStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();  
				TreeNode[] path = node.getPath();
				String name = "";
				name = "["+path[0].toString();
				for(int i = 1;i < path.length;i++)
				{
					name += ", "+path[i].toString();
					
				}
				name += "]";
				TaskElement ee = findTask(name);
				
				if (ee != null)
				{
					System.out.println("成功找到待停止的task");
					NetworkMinerFactory.getInstance().stopMiner(ee);
				}
				
			}
		});
		popupMenu.add(menuStop);

		JMenuItem menuDel = new JMenuItem("删除");
		menuDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();  
				TreeNode[] path = node.getPath();
				String name = "";
				name = "["+path[0].toString();
				for(int i = 1;i < path.length;i++)
				{
					name += ", "+path[i].toString();
					
				}
				name += "]";
				TaskElement ee = findTask(name);
				if (ee != null)
				{
					System.out.println("成功找到待删除的task");
					TaskElement.del1Task(ee);
					TreeNode parentNode = node.getParent();
					while(parentNode != null && parentNode.getChildCount() == 1)
					{
						node = (DefaultMutableTreeNode) parentNode;
						parentNode = parentNode.getParent();
					}
					
					((DefaultTreeModel) jtree.getModel()).removeNodeFromParent(node); 
				}
			}
		});
		popupMenu.add(menuDel);
		InitUIs();

	}

	private void InitUIs() {
		loadAllTasks();
		onCreate();
	}

	public void refreshAllTasks() {
		// listTasks.updateUI();
		Object[] tasks = TaskElement.allTasks.toArray();
		Arrays.sort(tasks);
		lmodel.removeAllElements();
		for (Object task : tasks)
			lmodel.addElement(task);

		System.out.println("lmodel:"+lmodel.size());
		miningType.clear();
		convertToTree();
		jtree.removeAll();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("规律挖掘");
		for(int i = 0;i < miningType.size();i++)
			root.add(miningType.get(i));
		jtree = new JTree(root);
		setViewportView(new JPanel().add(jtree));
		updateLister();
	}

	public void loadAllTasks() {
		TaskElement.LoadAllTasks();
		lmodel.removeAllElements();
		for (TaskElement ee : TaskElement.allTasks)
			lmodel.addElement(ee);
//		listTasks.setModel(lmodel);
//		refreshAllTasks();
		
		miningType.clear();
		convertToTree();
		jtree.removeAll();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("规律挖掘");
		for(int i = 0;i < miningType.size();i++)
			root.add(miningType.get(i));
		jtree = new JTree(root);
		setViewportView(new JPanel().add(jtree));
		updateLister();
	}
	public void updateLister(){
		
		jtree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
//				displayTaskConfig(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
//				undisplayTaskConfig();
			}
		});
		jtree.setEditable(false);
		jtree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				
				displaySelectedTask(e);
				TreePath treePath = e.getNewLeadSelectionPath();//获得根节点到选着节点的路径
				DefaultMutableTreeNode node =  (DefaultMutableTreeNode) treePath.getLastPathComponent();
				System.out.println("treePath:"+treePath.toString()+" node:"+node.toString());
//				jlRightDef.setText(node.toString());
//				jRightScrollPane.setViewportView(jlRightDef);
			}
		});
		addPopup(jtree,popupMenu);
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	public TaskElement getSelectedTask() {
		return (TaskElement) listTasks.getSelectedValue();
	}

	private TaskElement findTask(String str){
		
		str = str.substring(1, str.length()-1);
		String[] arr = str.split(", ");
		if(arr.length < 5)
			return null;
		//规律挖掘_挖掘类型_Ip_协议_粒度
		for(int i = 0;i < arr.length;i++)
		{
			arr[i] = arr[i].trim();
		}
		//ip_协议_粒度_挖掘类型
		TaskElement task_c = null;
		String name = arr[1]+"_"+arr[2]+"_"+arr[3]+"_"+arr[4];
		System.out.println("挖掘项名称："+name);
		for(int i = 0;i < TaskElement.allTasks.size();i++)
		{
			if(TaskElement.allTasks.get(i).getMiningMethod().toString().compareTo(arr[1]) ==0
					&& TaskElement.allTasks.get(i).getRange().compareTo(arr[2]) == 0 
					&&TaskElement.allTasks.get(i).getMiningObject().compareTo(arr[3]) == 0
					&&TaskElement.allTasks.get(i).getGranularity() == Integer.parseInt(arr[4]))
			{
				task_c = TaskElement.allTasks.get(i);
				break;
			}
		}
		if(task_c != null)
		{
			System.out.println("成功找到挖掘项");
		}
		return task_c;
	}
	private void displaySelectedTask(TreeSelectionEvent e) {
		
		TreePath treePath = e.getPath();
		
		TaskElement task_c = findTask(treePath.toString());
		
		if(task_c != null)
		{
			System.out.println("成功找到挖掘项");
		}
//		
		if (panelDisplayTask != null && task_c != null) {
			panelDisplayTask.displayTask(task_c);
		}
	}

	private void displayTaskConfig(MouseEvent e) {

		System.out.println("监听鼠标移动。。。");
		if (e.isAltDown() && panelDisplayTask != null) {
//		if (e.isAltDown() && panelDisplayTask != null) {
			floatWindow = new Window(MainFrame.topFrame);
			floatWindow.setBounds(0, 0, 600, 500);
			floatWindow.setLocation(e.getLocationOnScreen());

//			TaskElement ee = (TaskElement) listTasks.getSelectedValue();
			TreePath treePath = jtree.getSelectionPath();
			System.out.println("lujing:"+treePath.toString());
			String str = treePath.toString();
			str = str.substring(1, str.length()-1);
			String[] arr = str.split(",");
			if(arr.length < 5)
			{
				System.out.println("不是符合要求的路径");
				return ;
			}
			//规律挖掘_挖掘类型_Ip_协议_粒度
			for(int i = 0;i < arr.length;i++)
			{
				arr[i] = arr[i].trim();
			}
			//ip_协议_粒度_挖掘类型
			TaskElement task_c = null;
			String name = arr[1]+"_"+arr[2]+"_"+arr[3]+"_"+arr[4];
			System.out.println("挖掘项名称："+name);
			for(int i = 0;i < TaskElement.allTasks.size();i++)
			{
				if(TaskElement.allTasks.get(i).getTaskName().compareTo(name) == 0)
				{
					task_c = TaskElement.allTasks.get(i);
					break;
				}
			}
			if(task_c != null)
			{
				System.out.println("成功找到挖掘项");
			}
			
			System.out.println(task_c.toString());
			PanelDisplayTask displayer1 = new PanelDisplayTask(task_c);
			displayer1.expandAll();
			floatWindow.add(displayer1);
			floatWindow.setVisible(true);
		}
	}

	private void undisplayTaskConfig() {
		if (floatWindow != null) {
			floatWindow.dispose();
			floatWindow = null;
		}
	}

	public void setPanelDisplayTask(ITaskDisplayer panelDisplayTask) {
		this.panelDisplayTask = panelDisplayTask;
	}

	@Override
	public void onTaskAdded(TaskElement task) {
		if (!lmodel.contains(task))
			lmodel.addElement(task);
		refreshAllTasks();
	}

	@Override
	public void onTaskDeleted(TaskElement task) {
		if (lmodel.contains(task))
			lmodel.removeElement(task);
	}

	@Override
	public void onTaskModified(TaskElement task, int modify_type) {
		if (lmodel.contains(task))
			refreshAllTasks();
	}

	@Override
	public void onCreate() {

//		TaskElement.addTaskListener(this);
	}

	@Override
	public void onClosing() {
		TaskElement.removeTaskListener(this);
	}

	@Override
	public void onTaskMiningToStart(TaskElement task) {
	}

	@Override
	public void onTaskMiningToStop(TaskElement task) {
	}

	@Override
	public void onTaskToDisplay(TaskElement task) {
		// if ( panelDisplayTask instanceof PanelShowResults&&
		// !task.equals(listConfigs.getSelectedValue()))
		// listConfigs.setSelectedValue(task, true);
	}
	
	@Override
	public void onTaskAdded(TaskCombination task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskAddedDis(TaskCombination task) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onTaskDeleted(TaskCombination task) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTaskModified(TaskCombination task, int modify_type) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTaskToDisplay(TaskCombination task) {
		// TODO Auto-generated method stub
		
	}

	public void convertToTree() {

		TaskElement.LoadAllTasks();
		Collections.sort(TaskElement.allTasks);

		TreeMap<String, TreeMap<String, TreeMap<String, ArrayList<TaskElement>>>> mineType_Ip_protocol_granularity = new TreeMap<String, TreeMap<String, TreeMap<String, ArrayList<TaskElement>>>>();

		for (int i = 0; i < TaskElement.allTasks.size(); i++) {
			
			//ip_协议_粒度_挖掘类型
			String[] currentConfig = new String[4];
			currentConfig[0] = TaskElement.allTasks.get(i).getMiningMethod().toString();
			currentConfig[2] = TaskElement.allTasks.get(i).getMiningObject();
			currentConfig[1] = TaskElement.allTasks.get(i).getRange();
			currentConfig[3] = String.valueOf(TaskElement.allTasks.get(i).getGranularity());
//			String[] currentConfig = TaskElement.allTasks.get(i).getTaskName()
//					.split("_");
			System.out.println("task Name:"
					+ currentConfig[0]+"_"+currentConfig[1]+"_"+currentConfig[2]+"_"+currentConfig[3]);

			if (currentConfig.length < 4)
				continue;
			if (mineType_Ip_protocol_granularity.containsKey(currentConfig[0])) {
				if (mineType_Ip_protocol_granularity.get(currentConfig[0])
						.containsKey(currentConfig[1])) {
					if (mineType_Ip_protocol_granularity.get(currentConfig[0])
							.get(currentConfig[1])
							.containsKey(currentConfig[2])) {
						mineType_Ip_protocol_granularity.get(currentConfig[0])
								.get(currentConfig[1]).get(currentConfig[2])
								.add(TaskElement.allTasks.get(i));
					} else {
						ArrayList<TaskElement> at = new ArrayList<TaskElement>();
						at.add(TaskElement.allTasks.get(i));
						mineType_Ip_protocol_granularity.get(currentConfig[0])
								.get(currentConfig[1])
								.put(currentConfig[2], at);
					}
				} else {
					ArrayList<TaskElement> at = new ArrayList<TaskElement>();
					at.add(TaskElement.allTasks.get(i));
					TreeMap<String, ArrayList<TaskElement>> ha = new TreeMap<String, ArrayList<TaskElement>>();
					ha.put(currentConfig[2], at);
					mineType_Ip_protocol_granularity.get(currentConfig[0]).put(
							currentConfig[1], ha);
				}
			} else {
				ArrayList<TaskElement> at = new ArrayList<TaskElement>();
				at.add(TaskElement.allTasks.get(i));
				TreeMap<String, ArrayList<TaskElement>> ha = new TreeMap<String, ArrayList<TaskElement>>();
				ha.put(currentConfig[2], at);
				TreeMap<String, TreeMap<String, ArrayList<TaskElement>>> hha = new TreeMap<String, TreeMap<String, ArrayList<TaskElement>>>();
				hha.put(currentConfig[1], ha);
				mineType_Ip_protocol_granularity.put(currentConfig[0], hha);
			}
		}

		Iterator<String> type_iter = mineType_Ip_protocol_granularity.keySet()
				.iterator();
		while (type_iter.hasNext()) {
			String type = type_iter.next();
			TreeMap<String, TreeMap<String, ArrayList<TaskElement>>> hha = mineType_Ip_protocol_granularity
					.get(type);

			DefaultMutableTreeNode root = new DefaultMutableTreeNode(type);
			miningType.add(root);
			Iterator<String> ip_iter = hha.keySet().iterator();
			while (ip_iter.hasNext()) {
				String ip = ip_iter.next();
				TreeMap<String, ArrayList<TaskElement>> ha = hha.get(ip);
				DefaultMutableTreeNode ipNode = new DefaultMutableTreeNode(ip);
				root.insert(ipNode, root.getChildCount());
				Iterator<String> protocol_iter = ha.keySet().iterator();
				while (protocol_iter.hasNext()) {
					String protocol = protocol_iter.next();
					DefaultMutableTreeNode protocolNode = new DefaultMutableTreeNode(
							protocol);
					ipNode.insert(protocolNode, ipNode.getChildCount());
					for (int i = 0; i < ha.get(protocol).size(); i++) {
						String granularity = String.valueOf(ha.get(protocol)
								.get(i).getGranularity());
						DefaultMutableTreeNode granularityNode = new DefaultMutableTreeNode(
								granularity);
						protocolNode.insert(granularityNode,
								protocolNode.getChildCount());

					}
				}
			}

		}

	}
}
