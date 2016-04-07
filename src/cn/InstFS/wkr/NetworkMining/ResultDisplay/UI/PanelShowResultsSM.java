package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

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

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridLayout;

public class PanelShowResultsSM extends JPanel implements IPanelShowResults {


	private INetworkMiner miner;

	private TablePanelShowDataItems tblPatterns;
	private TablePanelShowDataItems tblForcasts_curTime;
	private TablePanelShowDataItems tblForcasts_futureTime;
	private TablePanelShowDataItems tblCurData;
	private TablePanelShowPrecisionRecall tblShowAccuracy;
	ChartPanelShowTs chart1;
	ChartPanelShowFI chart2;
	int count=0;


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
		setLayout(new GridLayout(0, 1, 0, 0));
//		chart1 = new ChartPanelShowTs("原始值", "时间", "值", null);
//		chart2 = new ChartPanelShowTs("预测值", "时间", "", null);
//
//		add(chart1);
//		add(chart2);
		chart1 = new ChartPanelShowTs("频繁模式", "时间", "值", null);
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
		if (rslt == null || rslt.getRetTSA() == null ||
				!rslt.getMiner().getClass().equals(NetworkMinerSM.class))
			return;
		else if(count==0)
		{

			DataItems nor = rslt.getInputData();
			chart1.displayDataItems(nor);
//			HashMap<String,ArrayList<DataItems>> f_model_nor= new HashMap<>();
//			Map<Integer, List<String>> freq = rslt.getRetSM().getFrequentItem();
//			if(freq!=null){
//				HashMap<String, ArrayList<String>> nor_model = new HashMap<>();
//				for (Integer key : freq.keySet()) {
////				System.out.println(key);
//					String skey = key.toString();
//					ArrayList<String> astring = new ArrayList<>();
//					for (String s : freq.get(key)) {
////					System.out.println(s);
//						astring.add(s);
//					}
//					nor_model.put(skey, astring);
////				System.out.println("_____________________");
//				}
//				//显示结果
//				for (String key : nor_model.keySet()) {
//					System.out.println(key);
//					for (String s : nor_model.get(key))
//						System.out.println(s);
//					System.out.println("____________________");
//				}
////			DataItems nor = new DataItems();
//				System.out.println(nor_model.size());
////				System.out.println(nor_model.get("0").size());
////				System.out.println(nor_model.get("1").size());
//				for (int i = 0; i < nor_model.size(); i++) {
//					String key = Integer.toString(i);
//					ArrayList<DataItems> nor_data = new <DataItems>ArrayList();
//
//					ArrayList<String> model_line = nor_model.get(key);
//					for (int j = 0; j < model_line.size(); j++) {
//						String temp = model_line.get(j);
//						String[] temp_processData = temp.split(",");
//						int first = 0;
//						int last = 0;
//						System.out.println(first);
//						System.out.println(last);
//						DataItems nor_line = new DataItems();
////					DataItems abnor_line=new DataItems();
//						if (temp_processData[0] != null) {
//							String firstString = temp_processData[0];
//							first = Integer.parseInt(firstString);
//						}
//						if (temp_processData.length > 1) {
//							String endString = temp_processData[1];
//							last = Integer.parseInt(endString);
//						}
//						for (int k = first; k <= last; k++) {
////						DataItem dataItemAbnor=new DataItem();
//							if (k == nor.getLength())
//								break;
//							DataItem tempItem = new DataItem();
//							System.out.println(nor.getElementAt(k).getTime());
//							tempItem.setTime(nor.getElementAt(k).getTime());
//							System.out.println(nor.getElementAt(k).getData());
////						dataItemAbnor.setData(abnor.getElementAt(k).getData());
////						dataItemAbnor.setTime(abnor.getElementAt(k).getTime());
//							tempItem.setData(nor.getElementAt(k).getData());
//
//
//							nor_line.add1Data(tempItem);
////						abnor_line.add1Data(dataItemAbnor);
//						}
//						nor_data.add(nor_line);
////					abnor_data.add(abnor_line);
//
//					}
//					f_model_nor.put(key, nor_data);
////					nor_data.clear();
//					if(f_model_nor.size()==nor_model.size())
//					{
//						System.out.println("ppppppppp"+f_model_nor.size());
//						JFreeChart jf = ChartPanelShowFI.createChart(f_model_nor, nor);
//						ChartPanel chartpanel = new ChartPanel(jf);
//						remove(chart1);
//						add(chartpanel);
//						repaint();
//						validate();
//						count++;
//					}
//				
//
//			}
				
//					else{
//			tblCurData.showDataItems(new DataItems());
//			tblCurData.hideColumnProb();
//			tblForcasts_curTime.showDataItems(new DataItems());
//			tblForcasts_curTime.hideColumnTime();
//			tblForcasts_futureTime.showDataItems(new DataItems());
//			tblForcasts_futureTime.hideColumnTime();
//			tblPatterns.showDataItems(new DataItems());
//			tblPatterns.hideColumnTime();
//
//			tblShowAccuracy.showMinerResultsPrecisionAndRecall(rslt);
//		}
//			}
		}
	}
}





