package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;

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
		ChartPanelShowTs showTs=null;
		switch(task.getMiningObject())
		{
		case "网络簇系数": 
			 showTs= new ChartPanelShowTs(task.getMiningObject()+"时间序列", "时间",task.getMiningObject(),null);
			 showTs.displayDataItems(dataItems);
			 add(showTs);
			 break;
		case "网络直径":
			 showTs = new ChartPanelShowTs(task.getMiningObject()+"时间序列", "时间", task.getMiningObject(),null);
			 add(showTs);
			 showTs.displayDataItems(dataItems);
			 break;
		case "结点出现消失":
			 ChartPanelShowNodeFrequence showNF = new ChartPanelShowNodeFrequence(task.getMiningObject()+"时间序列", "时间",task.getMiningObject(), task,dataItems);
			 add(showNF);
			 break;
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
