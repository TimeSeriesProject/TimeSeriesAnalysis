package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import cn.InstFS.wkr.NetworkMining.PcapStatistics.PcapUtils;

public class ProcessBarShow implements Runnable {

	JFrame frame = null;
	JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL);

	JButton jb_input = new JButton("打开待解析的pecap文件目录");
	JButton jb_output = new JButton("选择保存路径");

	JButton beginDig = new JButton("开始解析");

	JTextField inputTxtfile = new JTextField();
	JTextField outputTxtfile = new JTextField();
	String inputPath = "";
	String outputPath = "";
	JPanel jpanel = new JPanel();
	PecapParse pp = null;
	boolean haseBegin = false;
	JLabel jlable = new JLabel("尚未开始");
	int phrase = 0;
	String currentPhrase = "尚未开始";

	JPanel topPanel = new JPanel();
	JPanel midPanel = new JPanel();

	public ProcessBarShow() {

		frame = new JFrame("测试进度条");
		// 创建一条垂直进度条
		initLayout();
		addButtonActionLister();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (Exception exe) {
			exe.printStackTrace();
		}
		Timer timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (haseBegin) {
					System.out.println("开始解析。。。。。。");
					// 以任务的当前完成量设置进度条的value
					if(pp.pu.getStatus().name().compareTo("PREPARE") == 0) {
						
						currentPhrase = "正在准备";
					}
					else if (pp.pu.getStatus().name().compareTo("PARSE") == 0) {
						if (pp.pu.getParseSum() == 0) {
							phrase = 0;
							bar.setMaximum(pp.pu.getParseSum());
							currentPhrase = "当前阶段为解析pecap包阶段 1/2";
							System.out.println("任务总数："+pp.pu.getParseSum());
//							jlable.setText(currentPhrase);
						}
						
						int num = (int) Math.round(pp.pu.getParsedNum()*1.0/pp.pu.getParseSum()*100);
						bar.setValue(num);
						jlable.setText(currentPhrase);
					} else if (pp.pu.getStatus().name().compareTo("GENROUTE") == 0) {
						
						bar.setMaximum(pp.pu.getGenRouteSum());
						currentPhrase = "当前阶段为解析路由阶段 2/2";
						int num = (int) Math.round(pp.pu.getGenedRouteNum()*1.0/pp.pu.getGenRouteSum()*100);
						bar.setValue(num);
						jlable.setText(currentPhrase);
					}
					else if(pp.pu.getStatus().name().compareTo("END") == 0) {
					
						haseBegin = false;
					}

				}

			}
		});
		timer.start();
		// init();
	}

	private void initLayout() {

		createTopPanel();
		createMiddlePanel();

		JPanel panelContainer = new JPanel();
		panelContainer.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		c1.weightx = 1.0;
		c1.weighty = 1.0;

		c1.fill = GridBagConstraints.BOTH;
		panelContainer.add(topPanel, c1);

		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.gridy = 1;
		c2.weightx = 1.0;
		c2.weighty = 0;
		c2.fill = GridBagConstraints.HORIZONTAL;
		panelContainer.add(midPanel, c2);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panelContainer.setOpaque(true);
		frame.setSize(new Dimension(480, 320));
		frame.setContentPane(panelContainer);
		frame.setVisible(true);
	}

	private void createTopPanel() {

		JLabel sourceLabel = new JLabel("添加路径：");
		sourceLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		sourceLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// inputTxtfile.setBounds(10, 10, 20, 5);
		// inputTxtfile.setBounds(10, 20, 20, 5);

		JPanel jpanel = new JPanel();
		// jpanel.setLayout(new GridLayout(2,2));

		jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
		jpanel.add(inputTxtfile);
		jpanel.add(jb_input);
		jpanel.add(Box.createRigidArea(new Dimension(10, 10)));
		jpanel.add(outputTxtfile);
		jpanel.add(jb_output);
		jpanel.add(Box.createRigidArea(new Dimension(10, 10)));

		JPanel sourceListPanel = new JPanel();
		sourceListPanel.setLayout(new BoxLayout(sourceListPanel,
				BoxLayout.Y_AXIS));
		sourceListPanel.add(sourceLabel);
		sourceListPanel.add(jpanel);
		sourceListPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		sourceListPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		topPanel.add(sourceListPanel);

		frame.add(topPanel);

	}

	private void createMiddlePanel() {

		// JLabel sourceLabel = new JLabel("：");
		// sourceLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		// sourceLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel jpanelBegin = new JPanel();
		jpanelBegin.setLayout(new BoxLayout(jpanelBegin, BoxLayout.X_AXIS));
		jpanelBegin.add(beginDig);
		jpanel.add(Box.createRigidArea(new Dimension(5, 10)));
		// jpanelBegin.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 5));

		JPanel jpanel = new JPanel();
		jpanel.setLayout(new GridLayout(1, 2));
		jpanel.add(jlable);
		jpanel.add(bar);
		bar.setStringPainted(true);  
		jpanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 5));
		// jpanel.add(jlable);
		JPanel sourceListPanel = new JPanel();
		sourceListPanel.setLayout(new BoxLayout(sourceListPanel,
				BoxLayout.Y_AXIS));
		sourceListPanel.add(jpanelBegin);
		sourceListPanel.add(jpanel);
		sourceListPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		sourceListPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		midPanel.add(sourceListPanel);

		frame.add(midPanel);
	}

	public void addButtonActionLister() {

		jb_input.addActionListener(new SetPath(inputPath, frame, inputTxtfile));

		jb_output.addActionListener(new SetPath(outputPath, frame,
				outputTxtfile));

		beginDig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if(inputTxtfile.getText().compareTo("") == 0 || inputTxtfile.getText() == null
						|| outputTxtfile.getText().compareTo("") == 0 || outputTxtfile.getText() == null)
				{
					JOptionPane.showMessageDialog( null,"请先选择解析文件目录和存放路径!");
//					MessageBox("这是一个最简单的消息框！");
					System.out.println("请先选择解析文件目录和存放路径");
					System.out.println("in:" + inputTxtfile.getText() + "  out:"+ outputTxtfile.getText());
				}
						
				else if(!haseBegin)
				{
					haseBegin = true;
					pp = new PecapParse(inputTxtfile.getText(), outputTxtfile
							.getText());
					ExecutorService exec = Executors.newFixedThreadPool(1);
					exec.submit(pp);
				}
				
			}

		});
	}

	@Override
	public void run() {

	}

	public static void main(String[] args) {
		ProcessBarShow pbs = new ProcessBarShow();
		// pbs.setVisible(true);
	}

}

class PecapParse implements Callable {

	String inputPath = "";
	String outputPath = "";
	PcapUtils pu = new PcapUtils();

	public PecapParse(String in, String out) {
		inputPath = in;
		outputPath = out;
	}

	@Override
	public Boolean call() throws Exception {

		pu.readInput(inputPath, outputPath);

		return true;
	}

}