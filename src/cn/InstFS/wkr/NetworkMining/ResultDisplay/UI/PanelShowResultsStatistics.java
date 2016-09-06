package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.data.xy.XYDataset;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class PanelShowResultsStatistics extends JPanel implements IPanelShowResults{

	ChartPanelShowStatistics ts;
	JLabel avg ;
	JLabel std ;
	JLabel entropy ;
	JLabel complex ;
	private INetworkMiner miner;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private void InitMiner(TaskElement task){
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
	}
	PanelShowResultsStatistics(TaskElement task)
	{
		InitMiner(task);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		setLayout(gridBagLayout);
		
		ts= new ChartPanelShowStatistics("时间序列","序列编号",task.getMiningObject(),null);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(ts, gbc_panel_1);
		
		JPanel panel = new JPanel(new FlowLayout());
		avg = new JLabel("平均值:");
		std= new JLabel("标准差:");
		entropy = new JLabel("样本熵:");
		complex = new JLabel("复杂度");
//		lblPeriodFeature = new JLabel("特征值:");
		panel.add(avg);
		panel.add(std);
		panel.add(entropy);
		panel.add(complex);
//		panel.add(lblPeriodFeature);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
	}
	public XYDataset createDataSet(TaskElement task)
	{
		
		return null;
		
	}
	@Override
	public void displayMinerResults(MinerResults rslt) {
		// TODO Auto-generated method stub
		ts.displayDataItems(rslt.getInputData());
		String pad="";
		for(int i=0;i<10;i++)
			pad+=" ";
		avg.setText("平均值 ："+String.format("  %.3f",rslt.getRetStatistics().getMean())+pad);
		std.setText("标准差 ："+String.format("  %.3f",rslt.getRetStatistics().getStd())+pad);
		entropy.setText("样本熵 ："+String.format("  %.3f",rslt.getRetStatistics().getSampleENtropy())+pad);
		complex.setText("复杂度 ："+String.format("  %.3f",rslt.getRetStatistics().getComplex())+pad);
		updateUI();
	}

	@Override
	public void displayMinerResults() {
		// TODO Auto-generated method stub
		MinerResults rslt = miner.getResults();
		displayMinerResults(rslt);
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setData(DataItems data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaskElement getTask() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INetworkMiner getMiner() {
		// TODO Auto-generated method stub
		return null;
	}

}
