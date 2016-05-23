package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.BorderLayout;

import javax.swing.*;

import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.PropertiesPanelFactory_wkr;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsClass;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.PanelCreateTask;

import com.l2fprod.common.beans.editor.JCalendarDatePropertyEditor;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class PanelDisplayTask extends JPanel implements ITaskDisplayer{
	//配置任务表
	PropertySheetPanel sheet;
	TaskElement task;
	PropertyUI_TaskElement taskUI;
	PanelConfigTask taskNew;
//	String oldTaskName;
	
	private JTextArea txtArea;
	private Object obj;
	
	private PropertyChangeListener listener;
	public PanelDisplayTask(TaskElement task) {
		this.task = task;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		taskUI = new PropertyUI_TaskElement(new TaskElement());
//		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(MiningMethod.class);
//		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(DiscreteMethod.class);
//		CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(AggregateMethod.class);
		
		PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(MiningMethod.class);
		PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(DiscreteMethod.class);
		PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(AggregateMethod.class);
		PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(MiningAlgo.class);
		PropertiesPanelFactory_wkr.INSTANCE.registerEnumEditor(TaskRange.class);
		CustomPropertyEditorRegistry.INSTANCE.getRegistry().unregisterEditor(Date.class);
		CustomPropertyEditorRegistry.INSTANCE.getRegistry().registerEditor(Date.class, JDatePropertyEditor.class);	
		
//		((JCalendarDatePropertyEditor)CustomPropertyEditorRegistry.INSTANCE.getRegistry().getEditor(Date.class)).setDateFormatString("yyyy-MM-dd HH:mm:ss");

//		sheet.setEditorFactory(CustomPropertyEditorRegistry.INSTANCE.getRegistry());
//		sheet.setRendererFactory(CustomPropertyRendererFactory.INSTANCE.getRegistry());

		sheet  = PropertiesPanelFactory_wkr.INSTANCE.createPanel(taskUI);
		sheet.setSorting(false);
	    sheet.setSortingCategories(true);
	    add( sheet);
	    
		
	    
	    
	    JScrollPane scrollPane = new JScrollPane();
	    add(scrollPane);
	    
	    txtArea = new JTextArea();
	    txtArea.setRows(3);
	    scrollPane.setViewportView(txtArea);
	    
	    JPanel panel = new JPanel();
	    add(panel);
	    panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	    
//	    JButton btnNew = new JButton("新建");
//	    btnNew.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
////				taskUI = new PropertyUI_TaskElement();
////				PropertiesPanelFactory_wkr.INSTANCE.update(sheet, taskUI, null);
//				JFrame jf=new JFrame();
//				try {
//					PanelCreateTask pct=new PanelCreateTask(jf);
//					pct.setVisible(true);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
//	    panel.add(btnNew);
	    
	    JButton btnSave = new JButton("保存");
	    btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveConfigure();
			}
		});
	    panel.add(btnSave);
//
//	    JButton btnNewCopy = new JButton("复制");
//	    btnNewCopy.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				doNewTaskOfCopy();
//			}
//		});
//	    panel.add(btnNewCopy);
	    
	    
	    listener = new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				showSqlStr();
			}
		};
	    if (task != null)
	    	displayTask(task);		    
	}

	private void showSqlStr(){
		String sqlStr = taskUI.getCore().generateSqlStr();
		txtArea.setText(sqlStr);
	}
	
	
	@Override
	public int getDisplayType() {
		return ITaskDisplayer.DISPLAY_CONFIG;
	}

	@Override
	public void displayTask(TaskElement task) {
		if (task == null)
			return;
		else{
			this.task = task;
			TaskElement t = new TaskElement(task);
//			oldTaskName = task.getTaskName();
			taskUI.setCore(t);
			PropertiesPanelFactory_wkr.INSTANCE.update(sheet, taskUI, listener);
			txtArea.setText(task.getSqlStr());
		}
	}
	public void saveConfigure(){
		boolean isRunning = false;	
		if (task != null){
			isRunning = task.isMining;
			TaskElement.del1Task(task);
		}
		
		// 新建任务
		task = taskUI.getCore();
		task.setSqlStr(txtArea.getText());
		if (TaskElement.add1Task(task, true)){
			JOptionPane.showMessageDialog(this, "保存成功！");			
			taskUI.setCore(new TaskElement(task));
			if (isRunning)
				NetworkMinerFactory.getInstance().startMiner(task);
		}
		PropertiesPanelFactory_wkr.INSTANCE.update(sheet, taskUI, listener);		
	}
	public void doNewTaskOfCopy(){
		TaskElement newTask = new TaskElement(task);
		newTask.setTaskName(newTask.getTaskName() + "_Copy");
		task = null;
		taskUI = new PropertyUI_TaskElement(newTask);
		PropertiesPanelFactory_wkr.INSTANCE.update(sheet, taskUI, null);
	}
	
	
	public void expandAll(){
		PropertySheetTableModel model = sheet.getTable().getSheetModel();
		int numProp = model.getPropertyCount();
		for (int i = 1; i < numProp; i ++){
			model.getPropertySheetElement(i).toggle();
		}
	}
}
