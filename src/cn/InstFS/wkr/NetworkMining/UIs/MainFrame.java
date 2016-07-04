package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowAllResults;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.DialogConfigTask;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.DialogSettingTask;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.PanelConfigTask;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.PanelListAllTasks;
import cn.InstFS.wkr.NetworkMining.UIs.SimulationUIs.PanelControlSimulationTime;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

import com.l2fprod.common.swing.LookAndFeelTweaks;
import org.jfree.chart.JFreeChart;

public class MainFrame extends JFrame {
	public static MainFrame topFrame;
	public static int numTxtOutput = 10;
	public static int ii;
	DialogConfigTask dialogConfigTask;
	
	private PanelListAllTasks panelListAllEvents;
	private PanelShowAllResults panelShowResults;
	
	
	private JPanel contentPane;
	private JTextArea txtOutput;
	private JCheckBox chckAutoShowResults;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {		
					UtilsUI.InitFactories();
//					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//					UIManager.setLookAndFeel("com.l2fprod.common.swing.plaf.metal.MetalLookAndFeelAddons");
//					LookAndFeelTweaks.tweak();
					MainFrame frame = new MainFrame();
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
	public MainFrame() {
		MainFrame.topFrame = this;
		setTitle("网络挖掘规律模拟器");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
				
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		toolBar.setSize(100, 20);
		
		JMenu mnFile = new JMenu("文件(F)");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);
		
		JMenuItem menuExit = new JMenuItem("退出(X)");
		menuExit.setMnemonic(KeyEvent.VK_X);
		menuExit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}
		});
		mnFile.add(menuExit);
		JMenuItem menuLoad=new JMenuItem("加载文件（L)");
		menuLoad.setMnemonic(KeyEvent.VK_L);
		menuLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFiles();
			}
		});
		mnFile.add(menuLoad);
		JMenu mnTools = new JMenu("任务(T)");
		mnTools.setMnemonic(KeyEvent.VK_T);
		menuBar.add(mnTools);
		
		JMenuItem menuConfigEvent = new JMenuItem("配置任务(P)");
		menuConfigEvent.setMnemonic(KeyEvent.VK_P);
		menuConfigEvent.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame jf=new JFrame();
				DialogSettingTask dialogSettingTask=new DialogSettingTask(jf);
				dialogSettingTask.setVisible(true);
			}
		});
		
		JMenuItem menuStartMiningAll = new JMenuItem("全部开始挖掘(S)");
		menuStartMiningAll.setMnemonic(KeyEvent.VK_S);
		menuStartMiningAll.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkMinerFactory.getInstance().startAllMiners();
			}
		});
		mnTools.add(menuStartMiningAll);
		
		JMenuItem menuStopMiningAll = new JMenuItem("全部停止挖掘(E)");
		menuStopMiningAll.setMnemonic(KeyEvent.VK_E);
		menuStopMiningAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkMinerFactory.getInstance().stopAllMiners();
			}
		});
		mnTools.add(menuStopMiningAll);
		
		JSeparator separator = new JSeparator();
		mnTools.add(separator);
		mnTools.add(menuConfigEvent);
		
		JMenu mnView = new JMenu("视图(V)");
		mnView.setMnemonic(KeyEvent.VK_V);
		menuBar.add(mnView);
		
		chckAutoShowResults = new JCheckBox("自动显示结果");
		chckAutoShowResults.setSelected(UtilsUI.autoChangeResultsPanel);
		chckAutoShowResults.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				UtilsUI.autoChangeResultsPanel = chckAutoShowResults.isSelected();
			}
		});
		mnView.add(chckAutoShowResults);
		
		toolBar.add(new PanelControlSimulationTime());
		
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(200);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		panelShowResults = new PanelShowAllResults();
		splitPane.setRightComponent(panelShowResults);
				
		panelListAllEvents = new PanelListAllTasks(panelShowResults);
		splitPane.setLeftComponent(panelListAllEvents);
		
		JScrollPane panelStatus = new JScrollPane();
		contentPane.add(panelStatus, BorderLayout.SOUTH);
		txtOutput = new JTextArea();
		txtOutput.setRows(3);
		txtOutput.setLineWrap(true);
		txtOutput.setEnabled(false);
		panelStatus.setViewportView(txtOutput);
		initUIs();
	}
	private void initUIs(){
		chckAutoShowResults.setSelected(UtilsUI.autoChangeResultsPanel);
		
	}
	private void exitProgram(){
		System.exit(0);
	}
	private void openFrameConfigTask() {

			dialogConfigTask = new DialogConfigTask(this);
			dialogConfigTask.setVisible(true);


	}
	public void appendOutput(String str){
		if (!str.startsWith("\r\n") && txtOutput.getText().length() != 0)
			str = "\r\n" + str;
		txtOutput.append(str);
		int numLine = txtOutput.getLineCount();
		if (numLine > numTxtOutput)
		{
			int numLineDel = numLine - numTxtOutput;
			synchronized (txtOutput) {
				for (int i = numLineDel - 1; i > -1; i--) {
					int offStart;
					int offEnd;
					try {
						offStart = txtOutput.getLineStartOffset(i);
						offEnd = txtOutput.getLineEndOffset(i);
						txtOutput.replaceRange("", offStart, offEnd);
					} catch (BadLocationException e) {
						break;
					}
				}
			}
			
		}
	}
//	public void refreshAllTasks(){
//		panelListAllEvents.refreshAllTasks();
//	}
	public TaskElement getSelectedTask(){
		return panelListAllEvents.getSelectedTask();
	}
	private void chooseFiles()
	{

		JFileChooser jfc=new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
		jfc.showDialog(new JLabel(), "选择文件");
		File file=jfc.getSelectedFile();
		if(file.isDirectory()){
			System.out.println("文件夹:"+file.getAbsolutePath());
		}else if(file.isFile()){
			System.out.println("文件:"+file.getAbsolutePath());
		}
//		showProgress sh=new showProgress("加载",1,100);
//		Thread t = new Thread(sh);
//		t.start();

//		Thread thread = new Thread(new Runnable() {
//			public void run() {
//				try {
//					showProgress();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		thread.start();

		System.out.println(jfc.getSelectedFile().getName());


	}

//

	}




