package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPM;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JCheckBox;

public class PanelShowResultsPM extends JPanel implements IPanelShowResults{
	private INetworkMiner miner;
	DecimalFormat formatter = new DecimalFormat("0.00");
//	JDesktopPane desktopPane;
	
	SubPanelShowMinerResultsTs subPanel = new SubPanelShowMinerResultsTs("值");
	
	ChartPanelShowScatterPlot chartDistribute;
	JLabel lblIsPeriod;
	JLabel lblPeriodValue;
	JLabel lblFirstPossiblePeriod;
	private String obName;
	private String xName;
	private String yName;
//	JLabel lblPeriodFeature;
	private JCheckBox chckShowFeatureVal;
	
	
	public PanelShowResultsPM(TaskElement task){
		obName=task.getMiningObject();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
		setLayout(gridBagLayout);

		subPanel = new SubPanelShowMinerResultsTs(obName);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(subPanel, gbc_panel_1);

		//以下用来显示文字描述一栏
		JPanel panel = new JPanel(new GridLayout(0, 3));
		lblIsPeriod = new JLabel("是否周期:");
		lblPeriodValue = new JLabel("周期值:");
		lblFirstPossiblePeriod = new JLabel("最小的可能周期:");
//		lblPeriodFeature = new JLabel("特征值:");
		panel.add(lblIsPeriod);
		panel.add(lblPeriodValue);
		panel.add(lblFirstPossiblePeriod);
//		panel.add(lblPeriodFeature);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);

		//设置检查框
		chckShowFeatureVal = new JCheckBox("显示特征值");
		panel.add(chckShowFeatureVal);
		chckShowFeatureVal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshFeatureVal();
			}
		});
		yName=obName;
		//显示周期内分布
		chartDistribute = new ChartPanelShowScatterPlot("周期内分布", "周期内的点", obName, null);
		GridBagConstraints gbc_chartDistribute = new GridBagConstraints();
		gbc_chartDistribute.fill = GridBagConstraints.BOTH;
		gbc_chartDistribute.gridx = 0;
		gbc_chartDistribute.gridy = 2;
		add(chartDistribute, gbc_chartDistribute);
		InitMiner(task);
	}
	private void InitMiner(TaskElement task){
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
	}
	/**
	 * Create the panel.
	 */
	private PanelShowResultsPM() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		
		subPanel = new SubPanelShowMinerResultsTs("值");
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(subPanel, gbc_panel_1);
		
		//以下用来显示文字描述一栏
		JPanel panel = new JPanel(new GridLayout(0, 3));
		lblIsPeriod = new JLabel("是否周期:");
		lblPeriodValue = new JLabel("周期值:");
		lblFirstPossiblePeriod = new JLabel("最小的可能周期:");
//		lblPeriodFeature = new JLabel("特征值:");
		panel.add(lblIsPeriod);
		panel.add(lblPeriodValue);
		panel.add(lblFirstPossiblePeriod);
//		panel.add(lblPeriodFeature);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		
		//设置检查框
		chckShowFeatureVal = new JCheckBox("显示特征值");
		panel.add(chckShowFeatureVal);
		chckShowFeatureVal.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshFeatureVal();
			}
		});
		yName=obName;
		//显示周期内分布
		chartDistribute = new ChartPanelShowScatterPlot("周期内分布", "周期内的点", obName, null);
		GridBagConstraints gbc_chartDistribute = new GridBagConstraints();
		gbc_chartDistribute.fill = GridBagConstraints.BOTH;
		gbc_chartDistribute.gridx = 0;
		gbc_chartDistribute.gridy = 2;
		add(chartDistribute, gbc_chartDistribute);
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
	public void displayMinerResults(MinerResults rets) {
		if (rets == null)
			return;
		subPanel.displayMinerResults(rets);//显示时间序列图像
		
		//以下显示中间说明栏信息
		if (rets.getMiner() == null||
				!rets.getMiner().getClass().equals(NetworkMinerPM.class))
			return;
		
		if (this.isVisible()){
			MinerResultsPM retPM = null;
			if (rets != null)
				retPM = rets.getRetPM();
			if (retPM == null)
				return;
			if(retPM.getHasPeriod()){
				lblIsPeriod.setText("是否周期：是");
				lblPeriodValue.setText("周期值："+retPM.getPeriod() + 
						"(特征值:" + formatter.format(retPM.getFeatureValue()) + ")");
				lblFirstPossiblePeriod.setText("最小的可能周期:" + retPM.getFirstPossiblePeriod() +
						"(特征值:" + formatter.format(retPM.getFeatureValues()[retPM.getFirstPossiblePeriod() - 1]) + ")");
//				lblPeriodFeature.setText("特征值：" + formatter.format(retPM.getFeatureValue()) + 
//						"（门限：" + formatter.format(((ParamsPM)rets.getMiner().getTask().getMiningParams()).getPeriodThreshold()) + "）");
				DataItems items = retPM.getDistributePeriod();
			}else{
				lblIsPeriod.setText("是否周期：否");
				lblPeriodValue.setText("周期值：无");
				lblFirstPossiblePeriod.setText("最小的可能周期:");
//				lblPeriodFeature.setText("特征值：");
				chartDistribute.displayDataItems(new DataItems(), new DataItems(),new DataItems(),"周期","最大值","最小值");
			}
			refreshFeatureVal();
			
		}		
	}
	private void refreshFeatureVal(){
		MinerResults rets = miner.getResults();
		if (rets == null)
			return;
		if (chckShowFeatureVal.isSelected())
			chartDistribute.displayDataItems(rets.getRetPM().getFeatureValues(), "周期性特征值");
		else
			chartDistribute.displayDataItems(rets.getRetPM().getDistributePeriod(),rets.getRetPM().getMaxDistributePeriod(),rets.getRetPM().getMinDistributePeriod(), "周期","最大值","最小值");
	}
	@Override
	public void displayMinerResults() {
		MinerResults rets = miner.getResults();
		displayMinerResults(rets);
	}
	
}