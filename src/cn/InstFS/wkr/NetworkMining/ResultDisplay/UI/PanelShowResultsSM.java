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
		DataItems nor=rslt.getInputData();
		//System.out.println("nor.size:"+nor.getLength());
		System.out.println("length:"+nor.getLength()+"lastTime:"+nor.getLastTime());
		
		HashMap<String,ArrayList<DataItems>> f_model_nor= new HashMap<>();
		
		int cc=0;
		Map<Integer, List<String>> freq = rslt.getRetSM().getFrequentItem();
//		System.out.println("normal model");
//		for (Integer key : freq.keySet()) {
//			System.out.println(key);
//			for (String s : freq.get(key))
//				System.out.print(s+" ");
//			System.out.println("");
//		}
//		System.out.println("freq size "+freq.size());
		if (rslt == null || rslt.getRetSM() == null ||
				!rslt.getMiner().getClass().equals(NetworkMinerSM.class))
			return;
		else if(count==0)
		{
			DataItems nnor=new DataItems();
			

			 long startTime=System.currentTimeMillis();
			if(freq!=null&&count==0){
				HashMap<String,ArrayList<DataItems>> f_model_nor_mode=new HashMap<>();

				HashMap<String, ArrayList<String>> nor_model = new HashMap<>();
				for (Integer key : freq.keySet()) {
//				System.out.println(key);
					String skey = key.toString();
					ArrayList<String> astring = new ArrayList<>();
					for (String s : freq.get(key)) {
					//System.out.println(s);
						astring.add(s);
					}
					nor_model.put(skey, astring);
					//System.out.println(skey);
				}
				//显示结果
//				System.out.println("f-normal-model");
//				for (String key : nor_model.keySet()) {
//					System.out.println(key);
//					for (String s : nor_model.get(key))
//						System.out.print(s+" ");
//					System.out.println("");
//				}
//				System.out.println("normal  "+nor_model.size());
				for (int i = 0; i < nor_model.size(); i++) {
					String key = Integer.toString(i);
					ArrayList<DataItems> nor_data = new ArrayList<DataItems>();
					ArrayList<DataItems> nor_data_mode=new ArrayList<DataItems>();

					ArrayList<String> model_line = nor_model.get(key);
					for (int j = 0; j < model_line.size(); j++) {
						String temp = model_line.get(j);
						String[] temp_processData = temp.split(",");
						int first = 0;
						int last = 0;
//						System.out.println(first);
//						System.out.println(last);
						DataItems nor_line = new DataItems();
//					DataItems abnor_line=new DataItems();
						DataItems nor_line_mode=new DataItems();

						if (temp_processData[0] != null) {
							String firstString = temp_processData[0];
							first = Integer.parseInt(firstString);
						}
						if (temp_processData.length > 1) {
							String endString = temp_processData[1];
							last = Integer.parseInt(endString);
						}
						//System.out.println(first+" "+last);
//						for (int k = first; k <= last; k++) {
//							if (k == nor.getLength())
//								break;
//							DataItem tempItem = new DataItem();
//							tempItem.setTime(nor.getElementAt(k).getTime());
//							tempItem.setData(nor.getElementAt(k).getData());
//							nor_line.add1Data(tempItem);
//						}
						DataItem tempItem = new DataItem();
						DataItem tempMode=new DataItem();
						
						/*System.out.println("nor.length:"+nor.data.size());
						Assert.assertTrue(nor.data.size() == nor.time.size());						
						System.out.println("first:"+first+" last:"+last);
						System.out.println("data.size:"+nor.data.size()+" time.size:"+nor.time.size());*/
						if(first<nor.getLength()){
							
							tempItem.setTime(nor.getElementAt(first).getTime());							
							tempItem.setData(nor.getElementAt(first).getData());
							
							nor_line.add1Data(tempItem);
							nnor.add1Data(tempItem);
						}
						
						if(last<nor.getLength()) {
	
							tempItem.setTime(nor.getElementAt(last-1).getTime());
							tempItem.setData(nor.getElementAt(last-1).getData());
	
							nor_line.add1Data(tempItem);
							nnor.add1Data(tempItem);
						}
//					else {
//						tempItem.setTime(nor.getElementAt(last - 1).getTime());
//						tempItem.setData(nor.getElementAt(last - 1).getData());
////						tempMode.setTime(nor.getElementAt(last - 1).getTime());
////						tempMode.setData(nor.getElementAt(last - 1).getData());
//						nor_line.add1Data(tempItem);
//						nnor.add1Data(tempItem);
//					}
//
					if(first<nor.getLength()&&last<nor.getLength()){
						tempMode.setTime(nor.getElementAt((first + last) / 2).getTime());
						tempMode.setData(Math.abs(Double.valueOf
								(nor.getElementAt(last-1).getData())+Double.valueOf(nor.getElementAt(first).getData()))/2	+ "");
					}
					//tempMode.setTime(nor.getElementAt((first + last) / 2).getTime());
					//tempMode.setData(Math.abs(Double.valueOf
					//		(nor.getElementAt(last-1).getData())+Double.valueOf(nor.getElementAt(first).getData()))/2	+ "");
					if(tempMode.getData()!=null){
						nor_line_mode.add1Data(tempMode);
						nor_data.add(nor_line);
						nor_data_mode.add(nor_line_mode);
					}


//						nor_data.add(nor_line);
					}
					f_model_nor.put(key, nor_data);
					f_model_nor_mode.put(key,nor_data_mode);

					if(f_model_nor.size()==nor_model.size())
					{
						//System.out.println("ppppppppp"+f_model_nor.size());
						JFreeChart jf = ChartPanelShowFI.createChart(f_model_nor, nnor,f_model_nor_mode);
						ChartPanel chartpanel = new ChartPanel(jf);
						remove(chart1);
						add(chartpanel);
						repaint();
						validate();
						count++;
					}
					long endTime=System.currentTimeMillis();
					System.out.println(endTime-startTime+"ms");
				}
			}
		}
	}
}







