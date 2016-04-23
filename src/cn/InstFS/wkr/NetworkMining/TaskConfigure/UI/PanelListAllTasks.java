package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.Component;
import java.awt.Event;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import oracle.net.aso.e;

import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class PanelListAllTasks extends JScrollPane implements ITaskElementEventListener{
	
	public static DefaultListModel lmodel = new DefaultListModel();
	JList listTasks;
	ITaskDisplayer panelDisplayTask;
	Window floatWindow;
	/**
	 * Create the panel.
	 */
	public PanelListAllTasks(ITaskDisplayer panelDisplayTask) {
		this.panelDisplayTask = panelDisplayTask;
		listTasks = new JList();		
		listTasks.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				displaySelectedTask(e);
			}
		});
		listTasks.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				displayTaskConfig(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				undisplayTaskConfig();
			}
		});
		setColumnHeaderView(new JLabel("已配事件列表："));
		setViewportView(listTasks);	
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(listTasks, popupMenu);
		
		JMenuItem menuStart = new JMenuItem("开始挖掘");
		menuStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int []selected = listTasks.getSelectedIndices();
				if (selected == null || selected.length == 0)
					return;
				for (int i = selected.length - 1; i > -1; i --){
					TaskElement ee = (TaskElement)lmodel.get(selected[i]);
					if (ee != null)
						NetworkMinerFactory.getInstance().startMiner(ee);
				}
			}
		});
		popupMenu.add(menuStart);
		
		JMenuItem menuStop = new JMenuItem("停止挖掘");
		menuStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int []selected = listTasks.getSelectedIndices();
				if (selected == null || selected.length == 0)
					return;
				for (int i = selected.length - 1; i > -1; i --){
					TaskElement ee = (TaskElement)lmodel.get(selected[i]);
					if (ee != null)
						NetworkMinerFactory.getInstance().stopMiner(ee);
				}
			}
		});
		popupMenu.add(menuStop);		
		
		JMenuItem menuDel = new JMenuItem("删除");
		menuDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int []selected = listTasks.getSelectedIndices();
				if (selected == null || selected.length == 0)
					return;
				for (int i = selected.length - 1; i > -1; i --){
					TaskElement ee = (TaskElement)lmodel.get(selected[i]);
					TaskElement.del1Task(ee);
				}
			}
		});
		popupMenu.add(menuDel);
		InitUIs();
		
	}
	private void InitUIs(){
		loadAllTasks();
		onCreate();
	}

	public void refreshAllTasks(){
//		listTasks.updateUI();
		lmodel.removeAllElements();
		for(TaskElement ee : TaskElement.allTasks)
			lmodel.addElement(ee);
		Object []tasks =  lmodel.toArray();
		Arrays.sort(tasks);
		lmodel.removeAllElements();
		for (Object task : tasks)
			lmodel.addElement(task);
	}
	public void loadAllTasks(){
		TaskElement.LoadAllTasks();
		lmodel.removeAllElements();
		for(TaskElement ee : TaskElement.allTasks)
			lmodel.addElement(ee);
		listTasks.setModel(lmodel);
		refreshAllTasks();
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
	public TaskElement getSelectedTask(){
		return (TaskElement)listTasks.getSelectedValue();
	}
	private void displaySelectedTask(ListSelectionEvent e){
		if ( panelDisplayTask != null){
			TaskElement task = (TaskElement)listTasks.getSelectedValue();
			panelDisplayTask.displayTask(task);
		}
	}
	private void displayTaskConfig(MouseEvent e){
		
		if (e.isAltDown() && panelDisplayTask != null){
			floatWindow = new Window(MainFrame.topFrame);
			floatWindow.setBounds(0, 0, 600, 500);
			floatWindow.setLocation(e.getLocationOnScreen());
			
			TaskElement ee = (TaskElement)listTasks.getSelectedValue();
			PanelDisplayTask displayer1 = new PanelDisplayTask(ee);
			displayer1.expandAll();
			floatWindow.add(displayer1);			
			floatWindow.setVisible(true);
		}
	}
	private void undisplayTaskConfig(){
		if (floatWindow != null){
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
		TaskElement.addTaskListener(this);
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
//		if ( panelDisplayTask instanceof PanelShowResults&&
//				!task.equals(listConfigs.getSelectedValue()))
//				listConfigs.setSelectedValue(task, true);
	}

}
