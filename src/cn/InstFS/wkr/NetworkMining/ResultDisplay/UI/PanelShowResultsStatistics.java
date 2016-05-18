package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.data.xy.XYDataset;

import cn.InstFS.wkr.NetworkMining.DataInputs.CWNetworkReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class PanelShowResultsStatistics extends JPanel implements IPanelShowResults{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	PanelShowResultsStatistics(TaskElement task)
	{
		this.setLayout(new BorderLayout());
		CWNetworkReader reader =new CWNetworkReader(task);
		DataItems dataItems =reader.readInputByText();
		dataItems.setGranularity(task.getGranularity());
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		setLayout(gridBagLayout);
		
		JPanel ts= new ChartPanelShowStatistics("时间序列","时间",task.getMiningObject(),null);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(ts, gbc_panel_1);
		
		JPanel panel = new JPanel(new GridLayout(2, 2));
		JLabel avg = new JLabel("平均值:");
		JLabel std= new JLabel("方差:");
		JLabel entropy = new JLabel("样本熵:");
//		lblPeriodFeature = new JLabel("特征值:");
		panel.add(avg);
		panel.add(std);
		panel.add(entropy);
		
//		panel.add(lblPeriodFeature);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		
		
		
		switch(task.getMiningObject())
		{
//		case "网络簇系数": 
//			 showTs= new ChartPanelShowTs(task.getMiningObject()+"时间序列", "时间",task.getMiningObject(),null);
//			 showTs.displayDataItems(dataItems);
//			 add(showTs);
//			 break;
//		case "网络直径":
//			 showTs = new ChartPanelShowTs(task.getMiningObject()+"时间序列", "时间", task.getMiningObject(),null);
//			 add(showTs);
//			 showTs.displayDataItems(dataItems);
//			 break;
//		case "结点出现消失":
//			 ChartPanelShowNodeFrequence showNF = new ChartPanelShowNodeFrequence(task.getMiningObject()+"时间序列", "时间",task.getMiningObject(), task,dataItems);
//			 add(showNF);
//			 break;
		}
	}
	public XYDataset createDataSet(TaskElement task)
	{
		
		return null;
		
	}
	@Override
	public void displayMinerResults(MinerResults rslt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayMinerResults() {
		// TODO Auto-generated method stub
		
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
