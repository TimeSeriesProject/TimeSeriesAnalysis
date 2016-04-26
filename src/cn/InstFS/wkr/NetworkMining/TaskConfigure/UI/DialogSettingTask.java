package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.SimpleBeanInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import cn.InstFS.wkr.NetworkMining.UIs.Utils.PropertiesPanelFactory_wkr;
import com.l2fprod.common.demo.PropertySheetMain;
import com.l2fprod.common.demo.PropertySheetPage3;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.l2fprod.common.util.ResourceManager;

import ec.tstoolkit.design.IntValue;

public class DialogSettingTask extends JDialog {

    private JPanel contentPane;
    TaskElement task;
    PropertyUI_TaskElement taskUI;
    private JTextArea txtArea;
    DialogConfigTask dialogConfigTask;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DialogSettingTask frame = new DialogSettingTask(null);
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
    public DialogSettingTask(JFrame frame) {
        super(frame);
        setModal(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
//		contentPane.setLayout(new FlowLayout());
        setContentPane(contentPane);

        setTitle("基本事件序列配置");


        contentPane.add(new PanelConfigBaseTask(), BorderLayout.CENTER);
//        super(frame);
//        setModal(false);
//        contentPane = new JPanel();
//        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        setBounds(100, 100, 800, 600);
//        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//        contentPane.setLayout(new BorderLayout(0, 0));
//        setContentPane(contentPane);
//        JTable jt=new JTable(19,2);
//        jt.setEnabled(false);
//        DefaultTableModel tableModel=new DefaultTableModel(19,2);
//        TaskElement tk=new TaskElement();
//        //为table赋值
//        tableModel.setValueAt("任务名字",0,0);
//        tableModel.setValueAt(tk.getTaskName(),0,1);
//        tableModel.setValueAt("说明",1,0);
//        tableModel.setValueAt(tk.getComments(),1,1);
//        tableModel.setValueAt("挖掘对象",2,0);
//        tableModel.setValueAt(tk.getMiningObject(),2,1);
//        tableModel.setValueAt("离散化方法",3,0);
//        tableModel.setValueAt(tk.getDiscreteMethod(),3,1);
//        tableModel.setValueAt("离散化维数",4,0);
//        tableModel.setValueAt(tk.getDiscreteDimension(),4,1);
//        tableModel.setValueAt("离散化后采用的端点",5,0);
//        tableModel.setValueAt(tk.getDiscreteEndNodes(),5,1);
//        tableModel.setValueAt("挖掘算法",6,0);
//        tableModel.setValueAt(tk.getMiningAlgo(),6,1);
//        tableModel.setValueAt("任务范围",7,0);
//        tableModel.setValueAt(tk.getTaskRange(),7,1);
//        tableModel.setValueAt("选择节点值",8,0);
//        tableModel.setValueAt(tk.getRange(),8,1);
//        tableModel.setValueAt("时间粒度",9,0);
//        tableModel.setValueAt(tk.getGranularity(),9,1);
//        tableModel.setValueAt("数据聚合方法",10,0);
//        tableModel.setValueAt(tk.getAggregateMethod(),10,1);
//        tableModel.setValueAt("过滤条件",11,0);
//        tableModel.setValueAt(tk.getFilterCondition(),11,1);
//        tableModel.setValueAt("挖掘算法",12,0);
//        tableModel.setValueAt(tk.getMiningMethod(),12,1);
//        tableModel.setValueAt("挖掘参数",13,0);
//        tableModel.setValueAt(tk.getMiningParams(),13,1);
//        tableModel.setValueAt("起始时间",14,0);
//        tableModel.setValueAt(tk.getDateStart(),14,1);
//        tableModel.setValueAt("结束时间",15,0);
//        tableModel.setValueAt(tk.getDateEnd(),15,1);
//        tableModel.setValueAt("数据来源",16,0);
//        tableModel.setValueAt(tk.getDataSource(),16,1);
//        tableModel.setValueAt("文本路径",17,0);
//        tableModel.setValueAt(tk.getSourcePath(),17,1);
//        tableModel.setValueAt("数据库",18,0);
//        tableModel.setValueAt(tk.getSqlStr(),18,1);
//        jt.setModel(tableModel);
//        contentPane.add(jt);
//        add(jt,BorderLayout.CENTER);
        //设置输入框
//        JScrollPane scrollPane = new JScrollPane();
//        add(scrollPane);
//
//        txtArea = new JTextArea();
//        txtArea.setRows(3);
//        scrollPane.setViewportView(txtArea);
        //设置按钮
//        JPanel panel = new JPanel();
//        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
//
//        JButton btnSave = new JButton("保存");
//        btnSave.setVisible(true);
//        panel.add(btnSave);
//        btnSave.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                TaskElement task=new TaskElement();
//                taskUI = new PropertyUI_TaskElement(new TaskElement());
//                saveConfigure(task,taskUI);
//            }
//        });
//        JButton btnSetting = new JButton("高级设置");
//        btnSetting.setVisible(true);
//        panel.add(btnSetting);
//        btnSetting.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                openFrameConfigTask();
//
//            }
//        });
//        contentPane.add(panel, BorderLayout.SOUTH);
//
////        panel.add(btnSave);
//
//        setTitle("事件默认序列配置");
//
//
////        contentPane.add(new PanelConfigTask(), BorderLayout.CENTER);
////		contentPane.add(new PanelConfigTask());
//    }
//    public void saveConfigure(TaskElement task, PropertyUI_TaskElement taskUI){
//        boolean isRunning = false;
//        if (task != null){
//            isRunning = task.isMining;
//            TaskElement.del1Task(task);
//        }
//
//        // 新建任务
//        task = taskUI.getCore();
////        task.setSqlStr(txtArea.getText());
//        if (TaskElement.add1Task(task, true)){
//            JOptionPane.showMessageDialog(this, "保存成功！");
//            taskUI.setCore(new TaskElement(task));
//            if (isRunning)
//                NetworkMinerFactory.getInstance().startMiner(task);
//        }
//    }
//    private void openFrameConfigTask() {
//
//        JFrame jf=new JFrame();
//        dialogConfigTask = new DialogConfigTask(jf );
//        dialogConfigTask.setVisible(true);
//
//
//    }

    }
}
