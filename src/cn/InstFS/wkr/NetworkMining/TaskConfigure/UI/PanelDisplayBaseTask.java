package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import  cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.PanelConfigBaseTask;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.PropertiesPanelFactory_wkr;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsClass;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.PanelCreateTask;

import com.l2fprod.common.beans.editor.JCalendarDatePropertyEditor;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
public class PanelDisplayBaseTask extends JPanel implements ITaskDisplayer{
    //���������
    PropertySheetPanel sheet;
    TaskElement task;
    PropertyUI_BaseSetting taskUI;
//    PanelConfigBaseTask taskNew;
    DialogConfigTask dialogConfigBaseTask;
//	String oldTaskName;
    public static int settingModel=0;
    private JTextArea txtArea;
    private Object obj;

    private PropertyChangeListener listener;
    public PanelDisplayBaseTask(TaskElement task) {
        this.task = task;
        settingModel=0;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel menu=new JPanel();
        menu.setLayout(new BorderLayout(0, 0));
        JMenuBar menuBar = new JMenuBar();
        menu.add(menuBar, BorderLayout.NORTH);
//        final DefaultListModel<File> model = new DefaultListModel<>();
//        final JList<File> list = new JList<>(model);
//        final JFileChooser filechooser = new JFileChooser();
//        filechooser.setMultiSelectionEnabled(true);
//        Action select = new AbstractAction("�ļ�·��"){
//            @Override public void actionPerformed(ActionEvent e) {
//                JFrame jf=new JFrame();
//                if(JFileChooser.APPROVE_OPTION == filechooser.showOpenDialog(jf)){
//                    for(File file: filechooser.getSelectedFiles())
//                    {
//                        model.addElement(file);
////                        System.out.println(file.getName());
//                    }
//
//                }
//            }
//        };
//        JMenu mnFile = new JMenu("�ļ�");
//        mnFile.setMnemonic(KeyEvent.VK_F);
//        menuBar.add(mnFile);
//        JMenuItem menuLoad = new JMenuItem("�ļ�·��");
//        menuLoad.setMnemonic(KeyEvent.VK_X);
//        mnFile.add(menuLoad);
//        add(menu);
//        menuLoad.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                chooseFiles();
//            }
//        });
        taskUI = new PropertyUI_BaseSetting(new TaskElement());
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
        sheet.setSize(800,200);
        add(sheet);

//        JTable jt=new JTable(1,2);
////        jpt.add(jt);
//        DefaultTableModel tableModel=new DefaultTableModel(1,2);
//        tableModel.setValueAt("�ı�·��",0,0);
//        jt.setModel(tableModel);
//        jt.setSize(800,10);
//        add(jt);


        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane);
//        scrollPane.setPreferredSize(new Dimension(800,200));
        txtArea = new JTextArea();
        txtArea.setRows(3);
        scrollPane.setViewportView(txtArea);

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

//	    JButton btnNew = new JButton("�½�");
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

        JButton btnSave = new JButton("保  存");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveConfigure(settingModel);
            }
        });
        panel.add(btnSave);
//
	    JButton btnNewCopy = new JButton("高级配置");
	    btnNewCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                openFrameConfigTask();			}
		});
	    panel.add(btnNewCopy);


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
    public void saveConfigure(int settingModel){
        boolean isRunning = false;
        if (task != null){
            isRunning = task.isMining;
            TaskElement.del1Task(task);
        }
        if(settingModel==0)
        {
            task=taskUI.getCore();
            if(task.getMiningMethod().equals(MiningMethod.MiningMethods_FrequenceItemMining))
            {

            }
            else if(task.getMiningMethod().equals(MiningMethod.MiningMethods_PathProbilityMining))
            {
                task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
                task.setMiningAlgo(MiningAlgo.MiningAlgo_averageEntropyPM);
                task.setDiscreteMethod(DiscreteMethod.None);
                task.setGranularity(3600);
                task.setRange("10.0.1.1,10.0.1.2");
            }
            else  if(task.getMiningMethod().equals(MiningMethod.MiningMethods_PeriodicityMining))
            {
            	task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
            	task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
            	task.setDiscreteMethod(DiscreteMethod.None);
            	task.setGranularity(3600);
            	task.setRange("10.0.1.1,10.0.1.2");
            }
            else if(task.getMiningMethod().equals(MiningMethod.MiningMethods_SequenceMining))
            {
            	
            }
            else if(task.getMiningMethod().equals(MiningMethod.MiningMethods_TsAnalysis))
            {
            	task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
            	task.setMiningAlgo(MiningAlgo.MiningAlgo_TEOTSA);
            	task.setGranularity(3600);
            	task.setDiscreteMethod(DiscreteMethod.None);
            	task.setRange("10.0.1.1,10.0.1.2");
            }
        }
        else if(settingModel==1)
        // �½�����
        {
            task = taskUI.getCore();
        }
        task.setSqlStr(txtArea.getText());
        if (TaskElement.add1Task(task, true)){
            JOptionPane.showMessageDialog(this, "����ɹ���");
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
        taskUI = new PropertyUI_BaseSetting(newTask);
        PropertiesPanelFactory_wkr.INSTANCE.update(sheet, taskUI, null);
    }


    public void expandAll(){
        PropertySheetTableModel model = sheet.getTable().getSheetModel();
        int numProp = model.getPropertyCount();
        for (int i = 1; i < numProp; i ++){
            model.getPropertySheetElement(i).toggle();
        }
    }
    private void openFrameConfigTask() {
        JFrame jf=new JFrame();
        dialogConfigBaseTask = new DialogConfigTask(jf);
        dialogConfigBaseTask.setVisible(true);


    }
    private void chooseFiles()
    {
        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.showDialog(new JLabel(), "ѡ���ļ�");
        jfc.setMultiSelectionEnabled(true);
        jfc.showOpenDialog(null);
        jfc.setDragEnabled(true);

        File[] file = jfc.getSelectedFiles();
//        if(file.isDirectory()){
//            System.out.println("�ļ���:"+file.getAbsolutePath());
//        }else if(file.isFile()) {
//            System.out.println("�ļ�:" + file.getAbsolutePath());
//        }
        for (File f:file)
        System.out.println(f.getName());
    }
}
