package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
	List<Double> disList = new ArrayList<Double>();
	List<Double> disList1 = new ArrayList<Double>();
	List<Double> disList2 = new ArrayList<Double>();
	private DataItems outlines = new DataItems(); //异常点
	private DataItems outDegree = new DataItems(); //异常度
	private double GuassK = 3.0;
	private double threshold = 0.8;
	public PeriodBasedOutlierDetection(DataItems di,MinerResultsPM RetPM){
		this.di = di;
		this.RetPM = RetPM;
	}
	@Override
	public void TimeSeriesAnalysis() {
		comDistance();
		comDegree();
//		comDistance2();
//		comDegree2();
		genOutliers();		
	}
	/**
	 * 计算垂直距离的相对距离dis1和绝对距离dis2
	 * */
	public void comDistance(){
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
				dis2 = dis1/(perioddata);
			}			
			disList1.add(dis1);
			disList2.add(dis2);
		}			
	}
	/**
	 * 计算垂直的高斯距离
	 * */
	public void comDistance2(){
		HashMap<Integer, List<Double>> periodMap = new HashMap<Integer, List<Double>>();
		HashMap<Integer, List<Double>> map = new HashMap<Integer, List<Double>>();
		//统计
		for(int i=0;i<di.getLength();i++){
			List<Double> list;
			int index = (int) (i%(RetPM.getPeriod()));
			if(!periodMap.containsKey(index)){
				list = new ArrayList<Double>();
			}else{
				list = periodMap.get(index);
			}
			list.add(Double.parseDouble(di.getData().get(i)));
			periodMap.put(index, list);
		}
		//计算高斯分布
		for(int i=0;i<periodMap.size();i++){
			List<Double> guass = new ArrayList<Double>();
			List<Double> list = periodMap.get(i);
			NormalDistributionTest normalDistributionTest = new NormalDistributionTest(list);
			double mean = normalDistributionTest.getMean();
			double stv = normalDistributionTest.getStdeviation();
			guass.add(mean);
			guass.add(stv);
			map.put(i, guass);
		}
		//计算距离
		for(int i=0;i<di.getLength();i++){
			int index = (int) (i%(RetPM.getPeriod()));
			double data = Double.parseDouble(di.getData().get(i));
			double mean = map.get(index).get(0);
			double stv = map.get(index).get(1);
			double dis = Math.abs(data-mean)/stv;
			disList.add(dis);
		}
	}
	/**
	 * 根据dis1和dis2计算异常度
	 * */
	public void comDegree(){
		//对距离进行高斯拟合
		NormalDistributionTest normalDistributionTest = new NormalDistributionTest(disList1);
		double mean = normalDistributionTest.getMean();
		double stdeviation = normalDistributionTest.getStdeviation();
		//计算异常度
		for(int i=0;i<disList1.size();i++){			
			Date time = di.getTime().get(i);
			double degree;
			double val = (disList1.get(i)-mean)/stdeviation;
			val = val < 0 ? 0.0 : val/6;
			degree = Math.min(val, disList2.get(i)*0.6);
			outDegree.add1Data(time, String.valueOf(degree));			
		}	
	}
	/**
	 * 根据dis计算异常度
	 * */
	public void comDegree2(){
		for(int i=0;i<disList.size();i++){
			double degree = disList.get(i)/5;
			Date time = di.getTime().get(i);
			outDegree.add1Data(time, String.valueOf(degree));
		}
	}
	public void genOutliers(){
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
