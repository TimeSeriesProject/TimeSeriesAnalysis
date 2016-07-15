package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.Week;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class DataItems implements Serializable{
	public List<Date> time;
	public List<String> data;
	public List<Map<String, Integer>> NonNumData; //非数值型DataItems 各时间粒度的items出现次数
	public List<Map<String, Double>>  probMap;
	public Set<String> varSet;                    //非数值型DataItems items集合

	public List<Double> prob;
	private int granularity;
	

	private boolean isDiscrete=false;
	private int isAllDataDouble=0;
	
	private Double []discreteNodes;
	private Map<String, String>discreteStrings;
	public DataItems() {
		time = new ArrayList<Date>();
		data = new ArrayList<String>();	
		NonNumData=new ArrayList<Map<String,Integer>>();
		probMap=new ArrayList<Map<String,Double>>();
		varSet=new HashSet<String>();
		setProb(new ArrayList<Double>());
		setGranularity(0);
	}
	
	public RegularTimePeriod getTimePeriodOfElement(int i){
		if (getLength() < 2)
			return new Day();
		Date d1 = getElementAt(1).getTime();
		Date d2 = getElementAt(0).getTime();
		if (d1 == null || d2 == null)
			return new Day();
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(d1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(d2);
		
		Date curTime = getElementAt(i).getTime();
		if (getGranularity() == 0){
			if (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH))
				return new Month(curTime);
			if (cal1.getWeeksInWeekYear() != cal2.getWeeksInWeekYear())
				return new Week(curTime);
			if(cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR))
				return new Day(curTime);
			if (cal1.get(Calendar.HOUR_OF_DAY) != cal2.get(Calendar.HOUR_OF_DAY))
				return new Hour(curTime);
			if (cal1.get(Calendar.MINUTE) != cal2.get(Calendar.MINUTE))
				return new Minute(curTime);
			if (cal1.get(Calendar.SECOND) != cal2.get(Calendar.SECOND))
				return new Second(curTime);
			return new Day(curTime);		
		}else{
			if (getGranularity() < 60)
				return new Second(curTime);
			else if (getGranularity() < 60 * 60)
				return new Minute(curTime);
			else if (getGranularity() < 24 * 60 * 60)
				return new Hour(curTime);
			else if (getGranularity() < 7 * 24 * 60 * 60)
				return new Week(curTime);
			else if (getGranularity() < 30 * 24 * 60 * 60)
				return new Month(curTime);
			return new Day(curTime);			
		}
			
	}

	public boolean isDiscretized(){
		if(getDiscreteNodes() != null && getDiscreteNodes().length > 1)
			return true;
		else if (getDiscreteStrings() != null && getDiscreteStrings().size() > 0)
			return true;
		return false;
	}
	
	//返回DataItems维度
	public int getDiscretizedDimension(){
		if(getDiscreteNodes() != null && getDiscreteNodes().length > 1)
			return discreteNodes.length;
		else if (getDiscreteStrings() != null && getDiscreteStrings().size() > 0)
			return discreteStrings.size();
		return 0;
	}
	
	public void add1Data(DataItem di) {
		this.time.add(di.getTime());
		if(di.getData()!=null){
			this.data.add(di.getData());
		}else if(di.getNonNumData()!=null){
	    	this.NonNumData.add(di.getNonNumData());
		}else if(di.getProbData()!=null){
			this.probMap.add(di.getProbData());
		}else{
			throw new RuntimeException("di和dataItems不匹配");
		}
		this.prob.add(di.getProb());
	}
	
	public void add1Data(Date time, String data){
		this.time.add(time);
		this.data.add(data);
		this.getProb().add(0.0);
	}
	
	
	public void add1Data(Date time, Map<String, Integer> data){
		this.time.add(time);
		Map<String,Integer> map=new HashMap<String, Integer>();
		map.putAll(data);
		this.NonNumData.add(map);
		this.getProb().add(0.0);
	}
	
	public void add1Data(Map<String, Double> data,Date time){
		this.time.add(time);
		Map<String, Double> map=new HashMap<String, Double>();
		map.putAll(data);
		this.probMap.add(map);
		this.getProb().add(0.0);
	}
	
	public int getIsAllDataDouble() {
		if(isAllDataDouble==0){
			isAllDataIsDouble();
		}
		return isAllDataDouble;
	}

	public void setIsAllDataDouble(int isAllDataDouble) {
		this.isAllDataDouble = isAllDataDouble;
	}

	public int getLength(){
		return Math.max(time.size(), Math.max(Math.max(data.size(), NonNumData.size()),probMap.size()));
	}
	
	public DataItem getElementAt(int i ){
		DataItem ii = new DataItem();
		ii.setTime(time.get(i));
		if(data.size()>i){
	    	ii.setData(data.get(i));
		}else if(NonNumData.size()>i){
			ii.setNonNumData(NonNumData.get(i));
		}else if(probMap.size()>i){
			ii.setProbData(probMap.get(i));
		}else{
			throw new RuntimeException("get element at index i,i超出dataite界限");
		}
		if (prob.size()!=0)
			ii.setProb(prob.get(i));
		return ii;
	}	
	public List<Date> getTime() {
		return time;
	}
	public List<String> getData() {
		return data;
	}
	public void setTime(List<Date> time) {
		this.time = time;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	public List<Double> getProb() {
		return prob;
	}
	public void setProb(List<Double> prob) {
		this.prob = prob;
	}
	/**
	 * 将源DataItems清空，并填入items
	 * @param items
	 */
	public void setItems(DataItem []items){
		if(items.length<=0){
			return;
		}
		time.clear();
		data.clear();
		prob.clear();
		
		if(items[0].getData()!=null){
			for (DataItem item :items){
				time.add(item.getTime());
				data.add(item.getData());
				prob.add(item.getProb());
			}
		}else if(items[0].getNonNumData()!=null){
			for (DataItem item :items){
				time.add(item.getTime());
				NonNumData.add(item.getNonNumData());
				prob.add(item.getProb());
			}
		}else if(items[0].getProbData()!=null){
			for (DataItem item :items){
				time.add(item.getTime());
				probMap.add(item.getProbData());
				prob.add(item.getProb());
			}
		}
	}
	
	public Date getLastTime(){
		if (time.size() > 0)
			return time.get(time.size() - 1);
		else
			return null;
	}
	
	
	
	
	public boolean isAllDataIsDouble(){
		if(isAllDataDouble==0){
			List<String>datas = getData();
			if(datas.size()==0&&(getNonNumData().size()>0||getProbMap().size()>0)){
				isAllDataDouble=-1;
				return false;
			}
			for(String data: datas)
				try{
					Double.parseDouble(data);
				}catch(Exception e){
					isAllDataDouble=-1;
					return false;
				}
			isAllDataDouble=1;
			return true;
		}
		return (isAllDataDouble>0)?true:false;	
	}
	
	/**
	 * 判断该序列是否为离散值序列。
	 * <p>如果是，则将离散值及其对应的编号存入mapStr中，并返回true</p>
	 * <p>否则，返回false</p>
	 * @param mapStr	可为空
	 * @return
	 */
	public boolean isDiscrete(){
		int numDiffVals = 0;
		Map<String, String> mapStr= new HashMap<String, String>();
		List<String> datas = getData();
		for (String data:datas){
			if(!mapStr.containsKey(data)){
				mapStr.put(data, "" +numDiffVals);
				numDiffVals ++;
				if (numDiffVals > 20){
					isDiscrete=false;
					return false;
				}
			}
		}
		isDiscrete=true;
		setDiscreteStrings(mapStr);
		generateDiscreteNodes();
		return true;
	}
	
	/**
	 * 获取离散后的节点值
	 * @return discrete Nodes in String type
	 */
	public String discreteNodes(){
		if(discreteNodes==null||discreteNodes.length==0){
			return null;
		}
		StringBuilder sb=new StringBuilder();
		int length=discreteNodes.length;
		for(int i=0;i<length;i++){
			sb.append(discreteNodes[i]);
			sb.append(",");
		}
		String nodes=sb.toString();
		return nodes.substring(0, nodes.length()-1);
	}
	
	//当items原本就为离散化条件下，生成discreteNode
	private void generateDiscreteNodes(){
		Collection<String> nodes=getDiscreteStrings().keySet();
		discreteNodes=new Double[nodes.size()];
		int index=0;
		for(String node:nodes){
			discreteNodes[index]=Double.parseDouble(node);
			index++;
		}
	}

//	/**
//	 * 判断val所代表的值处于哪个区间（0~len-1）中，并以字符串形式返回这个序号
//	 * @param discreteNodes	端点值
//	 * @param len	端点数（为了避免每次调用函数时都提取一下数组长度）
//	 * @param val	值
//	 * @return
//	 */
//	private String getIndexOfData(int len, double val){
//		if (val < discreteNodes[0])
//			return getDiscreteNodes()[0]+"";
//		if (val < discreteNodes[1])
//			return getDiscreteNodes()[0]+"";
//		for (int i = 1; i < len - 1; i ++)
//			if (val >= discreteNodes[i] && val < discreteNodes[i+1])
//				return getDiscreteNodes()[i]+"";
//		return getDiscreteNodes()[len-1]+"";
//	}
//	/**
//	 * 根据discreteMethod,对该数据进行离散化
//	 * @param discreteMethod	离散化方法
//	 * @param numDims			离散后的维数
//	 * @param endNodes			自定义端点，仅在自定义离散化方法条件下有效
//	 * @return
//	 */
//	public DataItems toDiscreteNumbers(DiscreteMethod discreteMethod, int numDims, String endNodes){
//		DataItems newDataItems = null;
//		switch (discreteMethod) {
//		case 各区间数值范围相同:
//			newDataItems = this.toDiscreteNumbersAccordingToMean3Sigma(numDims);
//			break;
//		case 各区间数据点数相同:
//			newDataItems = this.toDiscreteNumbersAccordingToPercentile(numDims);
//			break;
//		case 自定义端点:
//			newDataItems = this
//					.toDiscreteNumbersAccordingToCustomNodes(endNodes);
//			break;
//		case None://不做离散化,直接返回
//		default:
//			newDataItems = this;
//		}
//		newDataItems.setDiscreteNodes(this.getDiscreteNodes());
//		newDataItems.setDiscreteStrings(this.getDiscreteStrings());
//		return newDataItems;
//		
//	}
//	
//	/**
//	 * 根据用户指定的节点进行离散化
//	 * @param endNodes 用户指定的界点
//	 * @return 离散化后的DataItems
//	 */
//	private DataItems toDiscreteNumbersAccordingToCustomNodes(String endNodes){
//		DataItems newDataItems = new DataItems();
//		if (endNodes == null || endNodes.length() == 0)
//			return newDataItems;
//		String []nodesStr = endNodes.split(",");
//		int numDims = nodesStr.length;
//		setDiscreteNodes(new Double[nodesStr.length]);
//		for (int i = 0; i < getDiscreteNodes().length; i ++)
//			getDiscreteNodes()[i] = Double.parseDouble(nodesStr[i]);
//		
//		List<String>datas = this.getData();
//		int len = this.getLength();
//		for (int i = 0; i < len; i ++){				
//			newDataItems.add1Data(this.getTime().get(i), ""+getIndexOfData(numDims, Double.parseDouble(datas.get(i))));
//		}
//		return newDataItems;
//	}
//	/**
//	 * 将区间[mean-3*sigma，mean+3*sigma]平均划分为numDims个区间，离散化得到的dataItems。
//	 * @param numDims	离散后的取值数
//	 * @return	已经离散化的dataItems数据
//	 */
//	private DataItems toDiscreteNumbersAccordingToMean3Sigma(int numDims){
//		DataItems newDataItems=new DataItems();
//		Double minVal = Double.MAX_VALUE;
//		Double maxVal = Double.MIN_VALUE;
//		
//		int length=this.getLength();
//		List<String> datas=this.getData();
//		// 首先，判断取值个数，如果仅为20个值以下，则直接将值作为离散值
//		setDiscreteStrings(new HashMap<String, String>());
//		boolean isDiscrete = isDiscrete();
//				
//		if (isDiscrete){	// 直接当离散值处理
//			for (int i = 0; i < length; i ++){
//				DataItem item = this.getElementAt(i);
//				newDataItems.add1Data(item.getTime(), getDiscreteStrings().get(item.getData()));
//			}
//			generateDiscreteNodes();
//		}else{				// 连续值，需要进行离散化。
//			// 先把DOUBLE 型取值取出来
//			List<Double>doubles = new ArrayList<Double>();
//			int numNonDouble = 0;
//			for(String data:datas){
//				try{
//					Double val = Double.parseDouble(data);
//					doubles.add(val);
//				}catch(Exception e){
//					numNonDouble ++;
//					if (numNonDouble > 10)
//						break;
//				}				
//			}
//			if (numNonDouble > 10)	// 说明这就是个字符串序列（因为非数值型取值的个数超过20个），因此，不进行离散化处理，直接返回输入数据
//				return this;
//			// 下面以mean-3*sigma作为最小值，以mean+3*sigma作为最大值
//			int numDouble = doubles.size();
//			double mean1 = 0.0;
//			double std1 = 0.0;
//			for (int i = 0; i < numDouble; i++)
//				mean1 += doubles.get(i);
//			mean1/= numDouble;
//			for (int i = 0; i < numDouble; i ++)
//				std1 += (doubles.get(i) - mean1) * (doubles.get(i)-mean1);
//			std1/=(numDouble-1);
//			minVal=mean1-3*std1;
//			maxVal=mean1+3*std1;
//			
//			setDiscreteNodes(new Double[numDims]);
//			int numNodes = getDiscreteNodes().length;
//			for (int i = 0; i < numDims; i ++){
//				getDiscreteNodes()[i] = minVal + (maxVal - minVal) * i / numDims;
//			}
//			for (int i = 0; i < length; i ++){
//				DataItem item = this.getElementAt(i);
//				Double val = null;
//				try{
//					val = Double.parseDouble(item.getData());
//				}catch(Exception e){}
//				if (val != null){
//					String ind = getIndexOfData(numNodes, val);
//					newDataItems.add1Data(item.getTime(), ind);
//				}
//			}
//		}
//		return newDataItems;
//	}
//	/**
//	 * 根据分位点来进行离散化
//	 * @param numDims
//	 * @return
//	 */
//	private DataItems toDiscreteNumbersAccordingToPercentile(int numDims){
//		DataItems newDataItems=new DataItems();
//		
//		int length=this.getLength();
//		
//		// 首先，判断取值个数，如果仅为20个值以下，则直接将值作为离散值
//		setDiscreteStrings(new HashMap<String, String>());
//		boolean isDiscrete = isDiscrete(getDiscreteStrings());
//		if (isDiscrete){	// 直接当离散值处理
//			for (int i = 0; i < length; i ++){
//				DataItem item = this.getElementAt(i);
//				newDataItems.add1Data(item.getTime(), getDiscreteStrings().get(item.getData()));
//			}
//			generateDiscreteNodes();
//		}else{				// 连续值，需要进行离散化
//			int numNonDouble = 0;
//			double step = 1.0 / numDims * length; 
//			int ind = 0;
//			int ind_step = (int) ((ind + 1) * step - 1);
//			discreteNodes = new Double[numDims];
//			DataItems sortedItems = DataInputUtils.sortByDoubleValue(this);
//			List<String> datas=sortedItems.getData();
//			discreteNodes[0] = Double.parseDouble(datas.get(0));
//			// 获取分位点
//			for (int i = 0; i < length; i ++){
//				try{
//					Double val = Double.parseDouble(datas.get(i));
//					if (i > ind_step) {
//						discreteNodes[ind + 1] = val;
//						ind ++;
//						ind_step = (int) ((ind + 1) * step - 1);
//					}
//				}catch(Exception e){
//					numNonDouble ++;
//					if (numNonDouble > 10)
//						break;
//				}				
//			}
//			// 得到新序列值
//			datas = this.getData();
//			for (int i = 0; i < length; i ++){				
//				newDataItems.add1Data(this.getTime().get(i), ""+getIndexOfData(numDims, Double.parseDouble(datas.get(i))));
//			}
//			if (numNonDouble > 10)	// 说明这就是个取值数超过20个的字符串离散值序列，不处理
//				return newDataItems;
//		}
//		return newDataItems;
//	}


	public static DataItems sortByDoubleValue(DataItems input){
		DataItems output = new DataItems();
		int len = input.getLength();
		ItemDouble []items = new ItemDouble[len];	
		List<Date>times = input.getTime();
		List<String>vals = input.getData();
		try{
			for (int i = 0; i < len; i ++){
				items[i] = new ItemDouble();
				items[i].setTime(times.get(i));
				items[i].setData(Double.parseDouble(vals.get(i)));
			}
		}catch(Exception e){
			UtilsUI.appendOutput("字符串转换为Double型报错！");
			return output;
		}
		Arrays.sort(items);
		for (int i = 0; i < len; i ++){
			output.add1Data(items[i].getTime(), items[i].getData().toString());
		}
		return output;
	}
	
	public static DataItems sortByTimeValue(DataItems input){
		DataItems output = new DataItems();
		output.setIsAllDataDouble(input.getIsAllDataDouble());
		int len = input.getLength();
		ItemTime []items = new ItemTime[len];	
		List<Date>times = input.getTime();
		if(input.data!=null&&input.data.size()>0){
			List<String>vals = input.getData();
			for (int i = 0; i < len; i ++){
				items[i] = new ItemTime();
				items[i].setTime(times.get(i));
				items[i].setData(vals.get(i));
			}
			Arrays.sort(items);
			for (int i = 0; i < len; i ++){
				output.add1Data(items[i].getTime(), items[i].getData());
			}
		}else if(input.NonNumData!=null&&input.NonNumData.size()>0){
			List<Map<String, Integer>>vals = input.getNonNumData();
			for (int i = 0; i < len; i ++){
				items[i] = new ItemTime();
				items[i].setTime(times.get(i));
				items[i].setNonNumData(vals.get(i));
			}
			Arrays.sort(items);
			for (int i = 0; i < len; i++){
				output.add1Data(items[i].getTime(), items[i].getNonNumData());
			}
		}
		output.setVarSet(input.getVarSet());
		output.setIsAllDataDouble(input.getIsAllDataDouble());
		return output;
	}
	
	public Double[] getDiscreteNodes() {
		return discreteNodes;
	}

	public void setDiscreteNodes(Double [] discreteNodes) {
		this.discreteNodes = discreteNodes;
	}

	public Map<String, String> getDiscreteStrings() {
		return discreteStrings;
	}

	public void setDiscreteStrings(Map<String, String> discreteStrings) {
		this.discreteStrings = discreteStrings;
	}

	public int getGranularity() {
		return granularity;
	}

	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
	
	public List<Map<String, Integer>> getNonNumData() {
		return NonNumData;
	}
	

	public List<Map<String, Double>> getProbMap() {
		return probMap;
	}

	public void setProbMap(List<Map<String, Double>> probMap) {
		this.probMap = probMap;
	}

	public void setNonNumData(List<Map<String, Integer>> nonNumData) {
		NonNumData = nonNumData;
	}
	public Set<String> getVarSet() {
		return varSet;
	}

	public void setVarSet(Set<String> varSet) {
		this.varSet = varSet;
	}
}
