package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;
import java.util.Date;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


/**@author LYH
 * @Description 对数据进行滑动平均平滑处理
 * @ 适用于承载路径数据
 * */
public class MovingAverage {
	private int piontK = 5;
	private int dataNum;
	private DataItems dataItems = new DataItems();
	private DataItems newItems = new DataItems();
	public MovingAverage(DataItems di,int k){
		this.dataItems = di;
		this.piontK = k;
		dataNum = di.getLength();
		run();
	}
	public void run(){
		for(int i=0;i<dataNum-piontK;i++){
			ArrayList<Double> subData = new ArrayList<Double>();
			for(int j=i;j<i+piontK;j++){
				subData.add(Double.parseDouble(dataItems.getData().get(j)));
			}
			double mean = genMean(subData);
			Date time = dataItems.getTime().get(i);
			String data = String.valueOf(mean);
			newItems.add1Data(time,data);
		}
		for(int i=dataNum-piontK;i<dataNum;i++){
			newItems.add1Data(dataItems.getElementAt(i));
		}
	}
	public double genMean(ArrayList<Double> a){
		double mean = 0;
		for(int i=0;i<a.size();i++){
			mean += a.get(i);
		}
		mean = mean/a.size();
		return mean;
	}
	public DataItems getNewItems() {
		return newItems;
	}
	public void setNewItems(DataItems newItems) {
		this.newItems = newItems;
	}
	
}

