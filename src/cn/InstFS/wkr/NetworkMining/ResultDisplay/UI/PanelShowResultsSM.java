package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import cn.InstFS.wkr.NetworkMining.Miner.Common.LineElement;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerSM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import org.junit.Assert;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.*;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.openide.nodes.Children;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PanelShowResultsSM extends JPanel implements IPanelShowResults {


	private INetworkMiner miner;

	private TablePanelShowDataItems tblPatterns;
	private TablePanelShowDataItems tblForcasts_curTime;
	private TablePanelShowDataItems tblForcasts_futureTime;
	private TablePanelShowDataItems tblCurData;
	private TablePanelShowPrecisionRecall tblShowAccuracy;
	ChartPanelShowTs chart1;
	ChartPanelShowFI chart2;
	int count = 0;
	public HashMap<String, ArrayList<DataItems>> f_model_nor = new HashMap<>();
	public DataItems nnor = new DataItems();
	public HashMap<String, ArrayList<DataItems>> f_model_nor_mode = new HashMap<>();
	public int firstclick = 0;
	public ArrayList<JCheckBox> box = new ArrayList<>();
	public int []modelindex=new int[10];
	public ArrayList<JCheckBox> checkboxArr=new ArrayList<>();
	public ArrayList<JLabel> labelArr=new ArrayList<>();
	public String xName;
	public String yName;
	public String ObName;



	/**
	 * Create the panel.
	 */
	public PanelShowResultsSM(TaskElement task) {
//		GridBagLayout gridBagLayout = new GridBagLayout();
//		gridBagLayout.columnWidths = new int[]{0, 0, 0};
//		gridBagLayout.rowHeights = new int[]{75, 75, 75, 75, 0};
//		gridBagLayout.columnWeights = new double[]{1.0, 0.4, Double.MIN_VALUE};
//		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
//		setLayout(gridBagLayout);
//		tblCurData = new TablePanelShowDataItems("当前值");
//		GridBagConstraints gbc_tblCurData = new GridBagConstraints();
//		gbc_tblCurData.fill = GridBagConstraints.BOTH;
//		gbc_tblCurData.insets = new Insets(0, 0, 5, 5);
//		gbc_tblCurData.gridx = 0;
//		gbc_tblCurData.gridy = 0;
////		add(tblCurData, gbc_tblCurData);
//
//		tblShowAccuracy = new TablePanelShowPrecisionRecall("预测结果");
//		GridBagConstraints gbc_panel = new GridBagConstraints();
//		gbc_panel.gridheight = 4;
//		gbc_panel.insets = new Insets(0, 0, 5, 0);
//		gbc_panel.fill = GridBagConstraints.BOTH;
//		gbc_panel.gridx = 1;
//		gbc_panel.gridy = 0;
////		add(tblShowAccuracy, gbc_panel);
//
//		tblForcasts_curTime = new TablePanelShowDataItems("当前预测值");
//		GridBagConstraints gbc_tblForcasts_curTime = new GridBagConstraints();
//		gbc_tblForcasts_curTime.fill = GridBagConstraints.BOTH;
//		gbc_tblForcasts_curTime.insets = new Insets(0, 0, 5, 5);
//		gbc_tblForcasts_curTime.gridx = 0;
//		gbc_tblForcasts_curTime.gridy = 1;
////		add(tblForcasts_curTime, gbc_tblForcasts_curTime);
//		tblForcasts_futureTime = new TablePanelShowDataItems("未来预测值");
//		GridBagConstraints gbc_tblForcasts_futureTime = new GridBagConstraints();
//		gbc_tblForcasts_futureTime.fill = GridBagConstraints.BOTH;
//		gbc_tblForcasts_futureTime.insets = new Insets(0, 0, 5, 5);
//		gbc_tblForcasts_futureTime.gridx = 0;
//		gbc_tblForcasts_futureTime.gridy = 2;
////		add(tblForcasts_futureTime, gbc_tblForcasts_futureTime);
//		tblPatterns = new TablePanelShowDataItems("序列模式");
//		GridBagConstraints gbc_tblPatterns = new GridBagConstraints();
//		gbc_tblPatterns.insets = new Insets(0, 0, 0, 5);
//		gbc_tblPatterns.fill = GridBagConstraints.BOTH;
//		gbc_tblPatterns.gridx = 0;
//		gbc_tblPatterns.gridy = 3;
////		add(tblPatterns, gbc_tblPatterns);
////		InitChartScheme();
		ObName=task.getMiningObject();
		setLayout(new BorderLayout());
//		chart1 = new ChartPanelShowTs("原始值", "序列编号", "值", null);
//		chart2 = new ChartPanelShowTs("预测值", "序列编号", "", null);
//
//		add(chart1);
//		add(chart2);
		chart1 = new ChartPanelShowTs("频繁模式", "序列编号", ObName, null);
		add(chart1);
//
		InitMiner(task);

	}

	private void InitMiner(TaskElement task) {
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
	}

	public void showResults() {
		// TODO
	}

	@Override
	public boolean start() {
		return miner.start();
	}

	@Override
	public boolean stop() {
		return miner.stop();
	}


	@Override
	public void setData(DataItems data) {


	}


	@Override
	public TaskElement getTask() {
		return miner.getTask();
	}

	@Override
	public INetworkMiner getMiner() {
		return miner;
	}


	@Override
	public void displayMinerResults() {
		MinerResults rets = miner.getResults();
		displayMinerResults(rets);

	}

	@Override
	public void displayMinerResults(MinerResults rslt) {
		final DataItems nor = rslt.getInputData();

		System.out.println("length:" + nor.getLength() + "lastTime:" + nor.getLastTime());


		int cc = 0;
		Map<Integer, List<String>> freq = rslt.getRetSM().getFrequentItem(); //Integer对应线段类标签，List为该类所有线段的起始点
		DataItems freqPatterns = rslt.getRetSM().getPatterns();	// 频繁模式
		List<LineElement> lineElements = rslt.getRetSM().getLineElements();

		final HashMap<Integer, List<List<LineElement>>> modeList = new HashMap<>();

		if (rslt == null || rslt.getRetSM() == null ||
				!rslt.getMiner().getClass().equals(NetworkMinerSM.class))
			return;
		else if (count == 0) {


			long startTime = System.currentTimeMillis();
			if (freq != null && count == 0) {

				List<String>freqPatternsList = freqPatterns.getData();
				int key = 0;
				for (String pattern: freqPatternsList) {
					ArrayList<DataItems> nor_data = new ArrayList<DataItems>(); // 存储符合某一频繁模式的所有线段
					ArrayList<List<LineElement>> nor_data_List = new ArrayList<>();

					String[] indexList = pattern.split(",");
					for (int i = 0; i < lineElements.size() - indexList.length;){

						boolean match = true;
						ArrayList<DataItems> temp = new ArrayList<>();
						ArrayList<LineElement> tempList = new ArrayList<>();
						for (int j = 0; j < indexList.length; j++){
							LineElement line = lineElements.get(i+j);
							if (line.getLabel() != Integer.parseInt(indexList[j])) {
								match = false;
								break;
							}
							int start = line.getStart();
							int end = line.getEnd();

							if (start < nor.getLength()) {
								if (end < nor.getLength())
									tempList.add(line);
								else if (end == nor.getLength()) {
									line.setEnd(end-1);
									tempList.add(line);
								}
							}

						}
						if (match) {
							nor_data_List.add(tempList);
							i += indexList.length;
						} else {
							i++;
						}
					}

					modeList.put(key, nor_data_List);
					key++;
				}

				final JPanel checkboxPanel = new JPanel();
				//设置监听复选框。
				JCheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10;
				JLabel l1, l2, l3, l4, l5, l6, l7, l8, l9, l10;
				checkboxPanel.setLayout(new FlowLayout());
				cb1 = new JCheckBox("模式一");
				cb2 = new JCheckBox("模式二");
				cb3 = new JCheckBox("模式三");
				cb4 = new JCheckBox("模式四");
				cb5 = new JCheckBox("模式五");
				cb6 = new JCheckBox("模式六");
				cb7 = new JCheckBox("模式七");
				cb8 = new JCheckBox("模式八");
				cb9 = new JCheckBox("模式九");
				cb10 = new JCheckBox("模式十");
				checkboxArr.add(cb1);
				checkboxArr.add(cb2);
				checkboxArr.add(cb3);
				checkboxArr.add(cb4);
				checkboxArr.add(cb5);
				checkboxArr.add(cb6);
				checkboxArr.add(cb7);
				checkboxArr.add(cb8);
				checkboxArr.add(cb9);
				checkboxArr.add(cb10);

				l1 = new JLabel("—");
				l2 = new JLabel("—");
				l3 = new JLabel("—");
				l4 = new JLabel("—");
				l5 = new JLabel("—");
				l6 = new JLabel("—");
				l7 = new JLabel("—");
				l8 = new JLabel("—");
				l9 = new JLabel("—");
				l10 = new JLabel("—");
				l1.setForeground(new Color(220, 87, 19));
				l1.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l2.setForeground(new Color(107, 194, 53));
				l2.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l3.setForeground(new Color(29, 131, 8));
				l3.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l10.setForeground(new Color(69, 137, 148));
				l10.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l4.setForeground(new Color(3, 22, 52));
				l4.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l5.setForeground(new Color(0, 90, 171));
				l5.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l6.setForeground(new Color(3, 101, 100));
				l6.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l7.setForeground(new Color(255, 66, 93));
				l7.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l8.setForeground(new Color(32, 90, 9));
				l8.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l9.setForeground(new Color(224, 208, 0));
				l9.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				l10.setForeground(new Color(55, 99, 53));
				l10.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
				labelArr.add(l1);
				labelArr.add(l2);
				labelArr.add(l3);
				labelArr.add(l4);
				labelArr.add(l5);
				labelArr.add(l6);
				labelArr.add(l7);
				labelArr.add(l8);
				labelArr.add(l9);
				labelArr.add(l10);
				for(int i=0;i<f_model_nor.size();i++)
				{
					if(i<10) {
						box.add(checkboxArr.get(i));
						checkboxPanel.add(checkboxArr.get(i));
						checkboxPanel.add(labelArr.get(i));
					}
					else
						break;
				}
				yName = ObName;

				int[] temp = new int[10];
				HashMap<String, ArrayList<DataItems>> temp_f_model_nor = new HashMap<>();
				HashMap<String, ArrayList<DataItems>> temp_f_model_nor_mode = new HashMap<>();

				JFreeChart jf = ChartPanelShowFI.createChart(temp_f_model_nor, nor, temp_f_model_nor_mode, temp, yName);
				ChartPanel chartpanel = new ChartPanel(jf);
				remove(chart1);
				add(chartpanel, BorderLayout.CENTER);
				repaint();
				validate();
				add(checkboxPanel, BorderLayout.SOUTH);
				//设置监听器
				for (int j = 0; j < box.size(); j++) {
					box.get(j).addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {

							for (int k = 0; k < box.size(); k++) {
								if (box.get(k).isSelected()) {
									modelindex[k] = 1;
								} else
									modelindex[k] = 0;
							}

							removeAll();
//								JFreeChart jf1 = ChartPanelShowFI.createChart(f_model_nor, nor, f_model_nor_mode, modelindex,yName);
							JFreeChart jf1 = ChartPanelShowFI.createChart(modeList, nor, modelindex, yName);
							ChartPanel chartpanel1 = new ChartPanel(jf1);
							add(chartpanel1, BorderLayout.CENTER);
							add(checkboxPanel, BorderLayout.SOUTH);
							repaint();
							validate();
						}
					});
					count++;
				}
				long endTime = System.currentTimeMillis();
				System.out.println(endTime - startTime + "ms");

			}
		}
	}
}







