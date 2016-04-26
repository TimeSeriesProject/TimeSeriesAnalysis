package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;
import javax.swing.ListModel;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import java.awt.Component;

public class PanelConfigBaseTask extends JPanel {
    private PanelDisplayBaseTask panelDisplayConfig;
    private PanelListAllTasks panelListAllEvents;
    private JList listConfigs;
    private JTextField textField;
	
/*	
	public static void main(String []args){
		PanelConfigEvent pp = new PanelConfigEvent();
		JFrame frame = new JFrame();
		frame.setContentPane(pp);
		frame.setBounds(100, 100, 600, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}*/
    /**
     * Create the panel.
     */
    public PanelConfigBaseTask() {
        setLayout(new BorderLayout(0,0));
//		JSplitPane splitPane = new JSplitPane();
        JPanel splitPane=new JPanel();
        splitPane.setLayout(new BorderLayout());
//		splitPane.setDividerLocation(0);
        add(splitPane);

        TaskElement NewTask = new TaskElement();
        panelDisplayConfig = new PanelDisplayBaseTask(NewTask);
//		splitPane.setRightComponent(panelDisplayConfig);
        splitPane.add(panelDisplayConfig,BorderLayout.CENTER);
//		panelListAllEvents = new PanelListAllTasks(panelDisplayConfig);
//		splitPane.setLeftComponent(panelListAllEvents);

//		InitUIs();
    }
    public void InitUIs(){
        panelListAllEvents.loadAllTasks();
//		panelListAllEvents.onCreate();
    }





}
