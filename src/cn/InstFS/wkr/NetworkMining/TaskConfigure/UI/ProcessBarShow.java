package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import cn.InstFS.wkr.NetworkMining.PcapStatistics.PcapUtils;

public class ProcessBarShow extends JDialog implements Callable {

	JFrame frame = null;
//	JDialog jdialog = null;
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
	String currentPhrase = "准备开始";

	JPanel topPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel bottomPanel = new JPanel();
	public ProcessBarShow() {}
	public ProcessBarShow(JFrame jframe) {
		
//		super(jframe);
		frame = new JFrame();
		frame.setTitle("pcap包解析进度监控");
//		frame.setVisible(true);
//		frame.setModal(true);
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //添加监听事件
        /**
		frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {//监听退出事件
		   	public void windowClosing(WindowEvent e) {
		   		frame.dispose();
		   		//释放当前窗口资源，并且设置为不可见
		   	}
		});
        */
//		frame = new JFrame("测试进度条");
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		// 创建一条垂直进度条
		initLayout();
		addButtonActionLister();
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
////			SwingUtilities.updateComponentTreeUI(frame);
//			SwingUtilities.updateComponentTreeUI(frame);
//		} catch (Exception exe) {
//			exe.printStackTrace();
//		}
		
		// init();
	}

	private void initLayout() {

		createTopPanel();
		createMiddlePanel();

		JPanel panelContainer = new JPanel();
//		panelContainer.setLayout(new GridBagLayout());
		panelContainer.setLayout(new GridLayout(3,1));

//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		panelContainer.setOpaque(true);
		frame.setSize(new Dimension(400, 320));
		panelContainer.add(topPanel,BorderLayout.NORTH);
		panelContainer.add(midPanel,BorderLayout.CENTER);
		panelContainer.add(bottomPanel,BorderLayout.SOUTH);
		frame.setContentPane(panelContainer);
		frame.setLocationRelativeTo(null); //使对话框居中
		frame.setVisible(true);
	}

	private void beginParsePcap()
	{
		frame = new JFrame();
		frame.setTitle("pcap包解析进度监控");
//		frame.setModal(true);
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //添加监听事件
        
		frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {//监听退出事件
		   	public void windowClosing(WindowEvent e) {
		   		frame.dispose();
		   		//释放当前窗口资源，并且设置为不可见
		   	}
		});
        
//		frame = new JFrame("测试进度条");
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		// 创建一条垂直进度条
		initLayout();
		addButtonActionLister();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			SwingUtilities.updateComponentTreeUI(frame);
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (Exception exe) {
			exe.printStackTrace();
		}
		
	}
	private void createTopPanel() {

//		JLabel sourceLabel = new JLabel("添加路径：");
//		Font font = new Font("宋体",Font.BOLD,12);
//		sourceLabel.setFont(font);
//		sourceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
//		frame.add(sourceLabel,BorderLayout.NORTH);
		
		
		JPanel jp_center_left = new JPanel();
//		jp_center_left.setLayout(new GridLayout(2,1));
//		jp_center_left.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jp_center_left.setLayout(null);
		inputTxtfile.setBounds(new Rectangle(10, 10, 150, 25));
		jp_center_left.add(inputTxtfile);
//		jp_center_left.add(Box.createRigidArea(new Dimension(5, 5)));
		outputTxtfile.setBounds(new Rectangle(10, 40, 150, 25));
		jp_center_left.add(outputTxtfile);
//		jp_center_left.add(Box.createRigidArea(new Dimension(5, 5)));

		JPanel jp_center_right = new JPanel();
//		jp_center_right.setLayout(new GridLayout(2,1));
		jp_center_right.setLayout(null);
		jb_input = new JButton("选择pcap文件目录");
		jb_input.setBounds(new Rectangle(5, 10, 150, 25));
		jp_center_right.add(jb_input);
		jb_output = new JButton("选择存储目录");
		jb_output.setBounds(new Rectangle(5, 40, 150, 25));
		jp_center_right.add(jb_output);
//		JPanel topPanel = new JPanel();
//		topPanel.setBounds(5, 5, 100, 100);
		
		jp_center_left.setAutoscrolls(true);
		jp_center_right.setAutoscrolls(true);
		topPanel.setLayout(new GridLayout(1,2));
//		topPanel.setBounds(100, 100, 200, 200);
//		topPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		topPanel.add(jp_center_left);
		topPanel.add(jp_center_right);
//		frame.add(topPanel);
//		topPanel.add(jp_center);

//		frame.add(topPanel);

	}

	private void createMiddlePanel() {

		// JLabel sourceLabel = new JLabel("：");
		// sourceLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		// sourceLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel jpanelBegin = new JPanel();
		jpanelBegin.setLayout(null);
		beginDig.setBounds(new Rectangle(80, 120, 150, 25));
		jpanelBegin.add(beginDig);
//		jpanel.add(Box.createRigidArea(new Dimension(5, 10)));
		// jpanelBegin.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 5));
//		midPanel.setLayout(new GridLayout(2,1));
		midPanel.add(beginDig);
		
//		JPanel jpanel = new JPanel();
//		bottomPanel.setLayout(new GridLayout(2, 1));
		bottomPanel.setLayout(null);
		jlable.setBounds(new Rectangle(50, 10, 150, 25));
		bottomPanel.add(jlable);
		bar.setBounds(new Rectangle(50, 30, 150, 25));
		bottomPanel.add(bar);
		bar.setStringPainted(true);  
//		jpanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		// jpanel.add(jlable);
		
//		bottomPanel.add(jpanel);
//		midPanel.add(bar);
//		midPanel.add(jpanel);
//		sourceListPanel.setAlignmentY(Component.TOP_ALIGNMENT);
//		sourceListPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
//		midPanel.setBounds(200, 200, 400, 400);
//		midPanel.add(sourceListPanel);

//		frame.add(midPanel,BorderLayout.SOUTH);
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
					timer.start();
				}
				
			}

		});
	}

	public static void main(String[] args) {
		ProcessBarShow pbs = new ProcessBarShow(null);
		// pbs.setVisible(true);
	}

	Timer timer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (haseBegin) {
//				System.out.println("开始解析。。。。。。");
				// 以任务的当前完成量设置进度条的value
				if(pp.pu.getStatus().name().compareTo("PREPARE") == 0) {
					
					currentPhrase = "即将开始";
					System.out.println("当前处于准备阶段");
				}
				else if (pp.pu.getStatus().name().compareTo("PARSE") == 0) {
					
//					phrase = 0;
					if(phrase == 0)
					{
						bar.setMaximum(pp.pu.getParseSum());
						System.out.println("任务总数："+pp.pu.getParseSum());
						currentPhrase = "当前阶段为解析pecap包阶段 1/2";
						phrase = 1;
					}
					
//					int num = (int) Math.round(pp.pu.getParsedNum()*1.0/pp.pu.getParseSum()*100);
					int num = pp.pu.getParsedNum();
					bar.setValue(num);
					jlable.setText(currentPhrase);
				} else if (pp.pu.getStatus().name().compareTo("GENROUTE") == 0) {
					
					if(phrase == 0 || phrase == 1)
					{
						bar.setMaximum(pp.pu.getGenRouteSum());
						currentPhrase = "当前阶段为解析路由阶段 2/2";
						System.out.println("任务总数："+pp.pu.getGenRouteSum());
					}
					
//					int num = (int) Math.round(pp.pu.getGenedRouteNum()*1.0/pp.pu.getGenRouteSum()*100);
					int num = pp.pu.getGenedRouteNum();
					bar.setValue(num);
					jlable.setText(currentPhrase);
				}
				else if(pp.pu.getStatus().name().compareTo("END") == 0) {
				
					haseBegin = false;
					currentPhrase = "解析结束";
					jlable.setText(currentPhrase);
//					timer.stop();
				}

			}

		}
	});
	@Override
	public Object call() throws Exception {
		
		beginParsePcap();
		// TODO Auto-generated method stub
		return true;
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
