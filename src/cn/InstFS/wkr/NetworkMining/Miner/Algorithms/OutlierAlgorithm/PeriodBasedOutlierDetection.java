package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import oracle.net.aso.i;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.DTW;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.NormalDistributionTest;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPM;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMperiodBasedParams;
import ec.tstoolkit.utilities.Id;

public class PeriodBasedOutlierDetection implements IMinerOM{
	DataItems di = new DataItems();
	DataItems PMdi = new DataItems();
	MinerResultsPM RetPM = new MinerResultsPM();
	DataItems disItems = new DataItems();
	HashMap<Integer, Double> dtwDis1 = new HashMap<Integer,Double>();//存储dtw绝对距离
	HashMap<Integer, Double> dtwDis2 = new HashMap<Integer,Double>();//存储dtw相对距离
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
		PMdi = RetPM.getDistributePeriod();
	}
	public PeriodBasedOutlierDetection(OMperiodBasedParams oMperiodBasedParams,DataItems di,MinerResultsPM RetPM){
		this.di = di;
		this.RetPM = RetPM;
		PMdi = RetPM.getDistributePeriod();
		this.GuassK = oMperiodBasedParams.getGuassK();
		this.threshold = oMperiodBasedParams.getThreshold();
	}
	@Override
	public void TimeSeriesAnalysis() {
		comDTWdistance();
//		comDistance();
		comDegree();
		genOutliers();		
	}
	public void comDTWdistance(){
		comDTWdismap();
		for(int i=0;i<dtwDis1.size();i++){
			disList1.add(dtwDis1.get(i));
			disList2.add(dtwDis2.get(i));
		}
	}
	/**
	 * 计算动态弯曲距离
	 * 填充 HashMap<Integer, Double> dtwDis
	 * */
	public void comDTWdismap(){
		HashMap<Integer, DataItems> diMap = new HashMap<Integer, DataItems>();
		
		int period = PMdi.getLength();
		int perNum = di.getLength()/period;
		for(int i=0;i<perNum;i++){
			DataItems item = new DataItems();
			for(int j=0;j<period;j++){
				item.add1Data(di.getElementAt(i*period+j));
			}
			diMap.put(i, item);
			DTW dtw = new DTW(item, PMdi);
			int[][] warpingPath = dtw.getWarpingPath();
			List<Double> disList1 = comDTWPathDis1(warpingPath, item, PMdi);
			List<Double> disList2 = comDTWPathDis1(warpingPath, item, PMdi);
			for(int j=0;j<disList1.size();j++){
				dtwDis1.put(i*period+j, disList1.get(j));
				dtwDis2.put(i*period+j, disList2.get(j));
			}
		}
		//最后一个不满一个周期的数据

		if(period*perNum<di.getLength()){

			DataItems item = new DataItems();
			DataItems PMitem = new DataItems();
			int j=0;
			for(int i=period*perNum;i<di.getLength();i++){
				item.add1Data(di.getElementAt(i));
				PMitem.add1Data(PMdi.getElementAt(j));
				j++;
			}
			diMap.put(perNum+1, item);
			DTW dtw = new DTW(item, PMitem);
			int[][] warpingPath = dtw.getWarpingPath();	
			List<Double> disList1 = comDTWPathDis1(warpingPath, item, PMdi);
			List<Double> disList2 = comDTWPathDis1(warpingPath, item, PMdi);
			for(int k=0;k<disList1.size();k++){
				dtwDis1.put(perNum*period+k, disList1.get(k));
				dtwDis2.put(perNum*period+k, disList2.get(k));
			}
		}
		
	}
	/**
	 * 计算dtw对应路径
	 * */
	public HashMap<Integer, List<Integer>> comDTWPath(int[][] warpingPath){
		HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();//记录点的对应关系				
		for(int i=0;i<warpingPath.length;i++){
			int index1 = warpingPath[i][0];
			int index2 = warpingPath[i][1];
			List<Integer> list;
			if(map.containsKey(index1)){
				list = map.get(index1);
			}else{
				list = new ArrayList<Integer>();
			}
			list.add(index2);
			map.put(index1, list);
		}
		
		
		return map;
	}
	/**计算dtw的绝对距离*/
	public List<Double> comDTWPathDis1(int[][] warpingPath,DataItems item,DataItems PMdi){
		HashMap<Integer, List<Integer>> map = comDTWPath(warpingPath);
		List<Double> disList = new ArrayList<Double>();
		for(int i=0;i<map.size();i++){
			double dis = 0;
			double val = Double.parseDouble(item.getData().get(i));
			for(int j=0;j<map.get(i).size();j++){
				double val2 = Double.parseDouble(PMdi.getData().get(j));
				dis += Math.abs(val2-val);
			}
			dis = dis/map.get(i).size();
			disList.add(dis);
		}	
		return disList;
	}
	/**计算dtw的相对距离*/
	public List<Double> comDTWPathDis2(int[][] warpingPath,DataItems item,DataItems PMdi){
		HashMap<Integer, List<Integer>> map = comDTWPath(warpingPath);
		List<Double> disList = new ArrayList<Double>();
		for(int i=0;i<map.size();i++){
			double dis = 0;
			double val = Double.parseDouble(item.getData().get(i));
			for(int j=0;j<map.get(i).size();j++){
				double val2 = Double.parseDouble(PMdi.getData().get(j));
				dis += Math.abs(val2-val)/val2;
			}
			dis = dis/map.get(i).size();
			disList.add(dis);
		}	
		return disList;
	}
	/**
	 * 计算垂直距离的相对距离dis1和绝对距离dis2
	 * */
	public void comDistance(){
		//计算每个点到周期的距离
		for(int i=0;i<di.getLength();i++){
			Date time =  di.getTime().get(i);
			double data = Double.parseDouble(di.getData().get(i));			
			double perioddata = Double.parseDouble(PMdi.getData().get((int) (i%RetPM.getPeriod())));
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
	 * 计算曲线之间的动态弯曲距离*/
	
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
		for(int i=0;i<di.getLength();i++){			
			Date time = di.getTime().get(i);
			double degree;
			double val = (disList1.get(i)-mean)/stdeviation;
			val = val < 0 ? 0.0 : val/6;
			degree = Math.min(val, disList2.get(i)*0.6);
			degree = degree<1 ? degree : 1.0;
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
