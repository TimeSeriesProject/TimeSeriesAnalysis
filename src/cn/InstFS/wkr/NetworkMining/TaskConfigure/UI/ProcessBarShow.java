package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

import Distributed.DisParams;
import Distributed.PropertiesGBC;
import cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt.PcapUtils;

public class ProcessBarShow extends JDialog {

	JFrame frame = null;
	JDialog jdialog = null;
	JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL);

	JButton jb_input = new JButton("选择pcap文件目录");
	JButton jb_output = new JButton("选择存储目录");

	JButton beginDig = new JButton("开始解析");

	JTextField inputTxtfile = new JTextField(DisParams.getPcapPath());
	JTextField outputTxtfile = new JTextField(DisParams.getOutputPath());
	String inputPath = "";
	String outputPath = "";
	PcapParse pcapParse = null;
	boolean hasBegin = false;
	JLabel jlable = new JLabel("尚未开始");
	int phrase = 0;//用于设置最大长度，保证只设置一次
	String currentPhrase = "准备开始";

	//解析全部or部分
	boolean parseAll = DisParams.isParseAll();
	JRadioButton parseAllBtn = new JRadioButton("解析全部文件", DisParams.isParseAll());
	JRadioButton parseNewBtn = new JRadioButton("解析新添加文件", !DisParams.isParseAll());
	ButtonGroup buttonGroup = new ButtonGroup();
	JLabel selectFile = new JLabel("选择解析文件：");

	public ProcessBarShow() {}

	public ProcessBarShow(JFrame jframe) {
		jdialog = new JDialog(jframe,"pcap包解析进度监控",true);
		addButtonActionLister();
		initLayout();
	}

	private void initLayout() {
		JPanel panelContainer = new JPanel();
		panelContainer.setBorder(BorderFactory.createTitledBorder("pcap解析单机版"));
		bar.setStringPainted(true);

		//ButtonGroup 只add即可，最终JPanel添加选择组件
		buttonGroup.add(parseAllBtn);
		buttonGroup.add(parseNewBtn);

		panelContainer.setLayout(new GridBagLayout());
		//输入路径
		panelContainer.add(inputTxtfile, new PropertiesGBC(0, 0, 3, 1).
				setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));

		//选择输入按钮
		panelContainer.add(jb_input, new PropertiesGBC(3, 0, 1, 1).
				setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

		//输出路径
		panelContainer.add(outputTxtfile, new PropertiesGBC(0, 1, 3, 1).
				setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));

		//选择输出按钮
		panelContainer.add(jb_output, new PropertiesGBC(3, 1, 1, 1).
				setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

		//开始解析
		panelContainer.add(beginDig, new PropertiesGBC(3, 2, 1, 1).
				setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

		//选择按钮
		panelContainer.add(selectFile, new PropertiesGBC(0, 2, 1, 1).
				setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

		//选择解析所有文件
		panelContainer.add(parseAllBtn, new PropertiesGBC(1, 2, 1, 1).
				setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

		//选择解析新添文件
		panelContainer.add(parseNewBtn, new PropertiesGBC(2, 2, 1, 1).
				setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

		//中间空一个面板
		panelContainer.add(new JPanel(), new PropertiesGBC(0, 3, 1, 1).
				setFill(PropertiesGBC.BOTH).setWeight(1, 1).setInsets(5, 5, 5, 0));

		//label
		panelContainer.add(jlable, new PropertiesGBC(0, 4, 4, 1).
				setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

		//进度条，设置高度ipad
		panelContainer.add(bar, new PropertiesGBC(0, 5, 4, 1).setIpad(400, 10).
				setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));

		jdialog.setSize(new Dimension(500, 400));
		jdialog.setContentPane(panelContainer);
		jdialog.setLocationRelativeTo(null); //使对话框居中
		jdialog.setVisible(true);
	}

	public void addButtonActionLister() {

		parseAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parseAll = true;
			}
		});//解析全部

		parseNewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parseAll = false;
			}
		});//解析部分

		jb_input.addActionListener(new SetPath(inputPath, frame, inputTxtfile));//设置pcap路径

		jb_output.addActionListener(new SetPath(outputPath, frame, outputTxtfile));//设置输出路径

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
						
				else if(!hasBegin)
				{
					hasBegin = true;//启动标志
					beginDig.setEnabled(false);
					pcapParse = new PcapParse(inputTxtfile.getText(), outputTxtfile
							.getText(), parseAll);
					DisParams.setPcapPath(inputTxtfile.getText().trim());//配置更改，保存
					DisParams.setOutputPath(outputTxtfile.getText().trim());
					DisParams.setParseAll(parseAll);
					ExecutorService exec = Executors.newFixedThreadPool(1);
					exec.submit(pcapParse);
					timer.start();//启动timer，用于显示进度条
				}
				
			}

		});


	}

	public static void main(String[] args) {
		ProcessBarShow pbs = new ProcessBarShow(null);
	}

	Timer timer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (hasBegin) {
				// 以任务的当前完成量设置进度条的value
				if(pcapParse.pcapUtils.getStatus().name().compareTo("PREPARE") == 0) {
					
					currentPhrase = "即将开始";
					jlable.setText(currentPhrase);
//					System.out.println("当前处于准备阶段");
				}
				else if (pcapParse.pcapUtils.getStatus().name().compareTo("PARSE") == 0) {
					

						bar.setMaximum(pcapParse.pcapUtils.getParseSum());
//						System.out.println("任务总数："+pcapParse.pcapUtils.getParseSum());
						currentPhrase = String.format("总任务 %d/%d: 阶段 1/3",
								pcapParse.pcapUtils.getTaskCount(), pcapParse.pcapUtils.getTaskSum());
					
//					int num = (int) Math.round(pp.pcapUtils.getParsedNum()*1.0/pp.pcapUtils.getParseSum()*100);
					int num = pcapParse.pcapUtils.getParsedNum();
					bar.setValue(num);
					jlable.setText(currentPhrase);
				} else if (pcapParse.pcapUtils.getStatus().name().compareTo("GENROUTE") == 0) {
					

						bar.setMaximum(pcapParse.pcapUtils.getGenRouteSum());
						currentPhrase = String.format("总任务 %d/%d: 阶段 2/3",
								pcapParse.pcapUtils.getTaskCount(), pcapParse.pcapUtils.getTaskSum());
						phrase = 2;
//						System.out.println("任务总数："+pcapParse.pcapUtils.getGenRouteSum());

					
//					int num = (int) Math.round(pp.pcapUtils.getGenedRouteNum()*1.0/pp.pcapUtils.getGenRouteSum()*100);
					int num = pcapParse.pcapUtils.getGenedRouteNum();
					bar.setValue(num);
					jlable.setText(currentPhrase);
				} else if (pcapParse.pcapUtils.getStatus().name().compareTo("PARSEBYDAY") == 0) {


						bar.setValue(0);
						bar.setMaximum(pcapParse.pcapUtils.getParsebydaySum());
						currentPhrase = String.format("总任务 %d/%d: 阶段 3/3",
								pcapParse.pcapUtils.getTaskCount(), pcapParse.pcapUtils.getTaskSum());
						phrase = 3;
//						System.out.println("任务总数阶段3："+pcapParse.pcapUtils.getParsebydaySum());


//					int num = (int) Math.round(pp.pcapUtils.getGenedRouteNum()*1.0/pp.pcapUtils.getGenRouteSum()*100);
					int num = pcapParse.pcapUtils.getParsebydayNum();
					bar.setValue(num);
					jlable.setText(currentPhrase);
				} else if (pcapParse.pcapUtils.getStatus().name().compareTo("END") == 0) {

					hasBegin = false;
					bar.setValue(pcapParse.pcapUtils.getParsebydaySum());
					currentPhrase = "解析结束";
					bar.setValue(100);
					jlable.setText(currentPhrase);
					beginDig.setEnabled(true);
//					timer.stop();
				}

			}

		}
	});

}

class PcapParse implements Callable {

	String inputPath = "";
	String outputPath = "";
	boolean parseAll;
	PcapUtils pcapUtils = new PcapUtils();

	public PcapParse(String in, String out, boolean parseAll) {
		this.inputPath = in;
		this.outputPath = out;
		this.parseAll = parseAll;
	}

	@Override
	public Boolean call() throws Exception {
		pcapUtils.startParse(inputPath, outputPath, parseAll);
		return true;
	}

}
