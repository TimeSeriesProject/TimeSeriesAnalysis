package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.NormalDistributionTest;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPM;
import ec.tstoolkit.utilities.Id;

public class PeriodBasedOutlierDetection implements IMinerOM{
	DataItems di = new DataItems();
	MinerResultsPM RetPM = new MinerResultsPM();
	DataItems disItems = new DataItems();
	List<Double> disList1 = new ArrayList<Double>();
	List<Double> disList2 = new ArrayList<Double>();
	private DataItems outlines = new DataItems(); //异常点
	private DataItems outDegree = new DataItems(); //异常度
	private double GuassK = 3.0;
	private double threshold = 0.6;
	public PeriodBasedOutlierDetection(DataItems di,MinerResultsPM RetPM){
		this.di = di;
		this.RetPM = RetPM;
	}
	@Override
	public void TimeSeriesAnalysis() {
		
		//计算每个点到周期的距离
		for(int i=0;i<di.getLength();i++){
			Date time =  di.getTime().get(i);
			double data = Double.parseDouble(di.getData().get(i));			
			double perioddata = Double.parseDouble(RetPM.getDistributePeriod().getData().get((int) (i%RetPM.getPeriod())));
			double dis1 = Math.abs(data-perioddata);
			double dis2;
			if(data==0 || perioddata==0){
				dis2 = 0.5;
			}else{
				dis2 = dis1/(perioddata+data);
			}		
			disList1.add(dis1);
			disList2.add(dis2);
		}
		//对距离进行高斯拟合
		NormalDistributionTest normalDistributionTest = new NormalDistributionTest(disList1);
		double mean = normalDistributionTest.getMean();
		double stdeviation = normalDistributionTest.getStdeviation();
		//计算异常度
		for(int i=0;i<disList1.size();i++){			
			Date time = di.getTime().get(i);
			double degree;
			double val = (disList1.get(i)-mean)/stdeviation;
			val = val < 0 ? 0.0 : val/5;
			degree = Math.min(val, disList2.get(i)*0.6);
			outDegree.add1Data(time, String.valueOf(degree));			
		}
		//计算异常点
		for(int i=0;i<outDegree.getLength();i++){
			if(Double.parseDouble(outDegree.getData().get(i))>threshold){
				outlines.add1Data(di.getElementAt(i));
			}
		}
	}

	@Override
	public DataItems getOutlies() {
		// TODO Auto-generated method stub
		return outlines;
	}

	@Override
	public List<DataItems> getOutlinesSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataItems getOutDegree() {
		// TODO Auto-generated method stub
		return outDegree;
	}
	
}
