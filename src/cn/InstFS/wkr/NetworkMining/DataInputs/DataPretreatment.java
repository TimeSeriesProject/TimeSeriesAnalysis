package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Array;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;












import org.eclipse.swt.widgets.MessageBox;

import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
//import org.hamcrest.Matcher;
//import weka.clusterers.SimpleKMeans;
//import weka.core.Instances;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;

public class DataPretreatment {
	
	//获取给定毫秒数之后的时间
	public static Date getDateAfter(Date curTime, int milliSeconds){
		Calendar cal = Calendar.getInstance();
		try{
		cal.setTime(curTime);
		}catch(Exception e){
			System.out.println("");
		}
		cal.add(Calendar.MILLISECOND, milliSeconds);
		return cal.getTime();
	}
	/**
	 * 数据聚合
	 * @param valsArrayD 数据数组
	 * @param method 聚合方法
	 * @return 聚合后的数据
	 */
	static private Double aggregateDoubleVals(Double[] valsArrayD,
			AggregateMethod method) {
		int len = valsArrayD.length;
		double []valsArray = new double[len];
		for (int i = 0; i < len; i ++)
			valsArray[i] = valsArrayD[i];
		switch (method) {
		case Aggregate_MAX:
			return StatUtils.max(valsArray);
		case Aggregate_MEAN:
			return StatUtils.mean(valsArray);
		case Aggregate_MIN:
			return StatUtils.min(valsArray);
		case Aggregate_SUM:
			return StatUtils.sum(valsArray);
		default:
			return 0.0;
		}
	}
	
	/**
	 * 判断val所代表的值处于哪个区间（0~len-1）中，并以字符串形式返回这个序号
	 * @param discreteNodes	端点值
	 * @param len	端点数（为了避免每次调用函数时都提取一下数组长度）
	 * @param val	值
	 * @return
	 */
	private static String getIndexOfData(int len, double val, Double[] discreteNodes){
		if (val < discreteNodes[0])
			return discreteNodes[0]+"";
		if (val < discreteNodes[1])
			return discreteNodes[0]+"";
		for (int i = 1; i < len - 1; i ++)
			if (val >= discreteNodes[i] && val < discreteNodes[i+1])
				return discreteNodes[i]+"";
		return discreteNodes[len-1]+"";
	}
	
	/**
	 * 将路径信息转换为路径概率信息  即每个时间段中每条路径经过的概率
	 * @param dataItems
	 * @return
	 */
	public static DataItems changeDataToProb(DataItems dataItems){
		DataItems dataOut=new DataItems();
		dataOut.setIsAllDataDouble(dataItems.getIsAllDataDouble());
		int len = dataItems.getLength();
		if (dataItems == null ||  len == 0)
			return dataOut;
		List<Date> times = dataItems.getTime();
		List<Map<String, Integer>> datas=dataItems.getNonNumData();
		Iterator<Map.Entry<String, Integer>> mapIter=null;
		for(Map<String, Integer> map:datas){
			Map<String, Double> probMap=new HashMap<String, Double>();
			Map<String, Double> transMap=new HashMap<String, Double>();
			transPathMapTotranslateProbMap(map,transMap);
			mapIter=map.entrySet().iterator();
			while(mapIter.hasNext()){
				Entry<String,Integer> entry=mapIter.next();
				double pathPossi=getPathProb(transMap,entry.getKey());
				probMap.put(entry.getKey(), pathPossi);
			}
			dataOut.getProbMap().add(probMap);
		}
		dataOut.setTime(times);
		dataOut.setProb(dataItems.getProb());
		dataOut.setVarSet(dataItems.getVarSet());
		return dataOut;
	}
	/**
	 * 获取DataItems路径中路由器的转移概率,并存储转移概率到文件中，
	 * 文件名为路径的“源IP-目的IP”
	 * @param dataItems
	 * @return 转移概率
	 */
	public static Map<String, Double> translateProbilityOfData(DataItems dataItems){
		Map<String, Double> transProbMap=new HashMap<String, Double>();
		String fileName;
		int len = dataItems.getLength();
		if (dataItems == null ||  len == 0)
			return null;
		List<Map<String, Integer>> datas=dataItems.getNonNumData();
		String firstPath=datas.get(0).keySet().iterator().next();
		String[] firstPathNodes=firstPath.split(",");
		fileName="translateProbOf"+firstPathNodes[0]+"-"+firstPathNodes[firstPathNodes.length-1]+".csv";
		Map<String, Integer> routeMap=new HashMap<String, Integer>();
		for(Map<String, Integer> map:datas){
			Iterator<Map.Entry<String, Integer>> mapIter=map.entrySet().iterator();
			while(mapIter.hasNext()){
				Entry<String, Integer> entry=mapIter.next();
				if(routeMap.containsKey(entry.getKey())){
					int value=map.get(entry.getKey());
					routeMap.remove(entry.getKey());
					value+=entry.getValue();
					routeMap.put(entry.getKey(), value);
				}else{
					routeMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		transPathMapTotranslateProbMap(routeMap,transProbMap);
		TextUtils utils=new TextUtils();
		utils.writeMap(transProbMap, "./configs/"+fileName);
		return transProbMap;
	}
	/**
	 * 求出所有通信路径中的状态转移概率
	 * @param map 通信路径Map 每条路径是Key值，路径出现的次数是Value值
	 * @param probMap 返回的路由节点转移概率 如Entry<"A,B",0.5>  表示从A路由到B路由的概率为0.5
	 */
	private static void transPathMapTotranslateProbMap(Map<String, Integer> map,Map<String, Double> probMap){
		int sum=sumMap(map);
		Iterator<Entry<String, Integer>>iterator=map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Integer>entry=iterator.next();
			String path=entry.getKey();
			if(((entry.getValue()*1.0)/(sum*1.0))<0.005){
				continue;
			}
			String[] pathNodes=path.split(","); //路径上的节点
			//路径概率计算服从一阶Markov模型 所以 P(A,B,C,D,E)=P(A)*P(B|A)*P(C|B)*P(D|C)*P(E|D)
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<pathNodes.length-1;i++){
				sb.delete(0, sb.length());
				sb.append(pathNodes[i]);
				String node=pathNodes[i];
				while(pathNodes[i+1].equals("*")){
					sb.append(",").append("*");
					i++;
				}
				sb.append(",").append(pathNodes[i+1]);
				if(!probMap.containsKey(sb.toString())){
					int nodeNum=containsNodesPathNum(map,node);
					int pathNum=containsNodesPathNum(map,sb.toString());
					probMap.put(sb.toString(),(pathNum*1.0)/(nodeNum*1.0));
				}
			}
		}
	}
	
	/**
	 * 根据历史路径信息，计算给定路径出现的概率
	 * @param map 路径的hashMap 历史路径
	 * @param path 要计算出现概率的路径
	 * @return 该路径的概率
	 */
	public static double getPathProb(Map<String, Double> map,String path){
		String[] pathNodes=path.split(","); //路径上的节点
		//路径概率计算服从一阶Markov模型 所以 P(A,B,C,D,E)=P(A)*P(B|A)*P(C|B)*P(D|C)*P(E|D)
		StringBuilder sb=new StringBuilder();
		double prob=1.0;
		for(int i=0;i<pathNodes.length-1;i++){
			sb.delete(0, sb.length());
			sb.append(pathNodes[i]);
			while(pathNodes[i+1].equals("*")){
				sb.append(",").append("*");
				i++;
			}
			sb.append(",").append(pathNodes[i+1]);
			if(map.containsKey(sb.toString())){
				prob*=map.get(sb.toString());
			}else{
				return 0;
			}
		}
		return prob;
	}
	
	/**
	 * 判断指定路径节点在所有路径中出现的次数
	 * @param map 路径 map
	 * @param node 指定的路径节点
	 * @return 节点在路径中出现
	 */
	private static int containsNodesPathNum(Map<String, Integer>map,String node){
		int pathsNum=0;
		String[] nodes=node.split(",");
		boolean isContain=false;
		Iterator<Map.Entry<String, Integer>> iterator=map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Integer>entry=iterator.next();
			String key=entry.getKey();  //路径
			int value=entry.getValue();
			String[] pathNodes=key.split(",");   
			
			//测试路径key中是否包含给定的节点@node
			isContain=false;
			for(int i=0;i<pathNodes.length-nodes.length+1;i++){
				for(int j=0;j<nodes.length;j++){
					if(!pathNodes[i+j].equals(nodes[j])){
						isContain=false;
						break;
					}
					isContain=true;
				}
				if(isContain){
					break;
				}
			}
			if(isContain){
				pathsNum+=value;
			}
		}
		return pathsNum;
	}
	
	/**
	 * 判断所有路径的条数
	 * @param map 存储路径的hashMap
	 * @return 路径总条数
	 */
	private static int sumMap(Map<String, Integer> map){
		Iterator<Map.Entry<String, Integer>> iter=map.entrySet().iterator();
		int sum=0;
		while (iter.hasNext()) {
			sum+=iter.next().getValue();
		}
		return sum;
	}
	/**
	 * 计算路径中经过多少个位置路由器节点
	 * @param path 经过的路径
	 * @return 位置路由器节点数
	 */
	private static int blankRouteNum(String path){
		String[] nodes=path.split(",");
		int routeNum=0;
		for(String node:nodes){
			if (node.equals("*")) {
				routeNum++;
			}
		}
		return routeNum;
	}
	
	public static DataItems normalization(DataItems di){
		DataItems dataout=new DataItems();
		dataout.setTime(di.getTime());
		if(di==null||di.getTime()==null||di.getData()==null||di.getLength()==0)
			return dataout;
		List<String> datas=di.getData();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(String data:datas){
			statistics.addValue(Double.parseDouble(data));
		}
		double mean=statistics.getMean();
		double minMaxSpan=statistics.getMax()-statistics.getMin();
		
		List<String> normData=new ArrayList<String>();
		for(String data:datas){
			normData.add(((Double.parseDouble(data)-mean)*1000)/minMaxSpan+"");
		}
		dataout.setData(normData);
		return dataout;
	}
	//datItems在相同的时间粒度上的聚合
	public static DataItems aggregateData(DataItems di,int granularity,
			AggregateMethod method,boolean isDiscreteOrNonDouble){	
		
		if(granularity ==3600)
			return di;
		DataItems dataOut = new DataItems();
		dataOut.setIsAllDataDouble(di.getIsAllDataDouble());
		int len = di.getLength();
		if (di == null || di.getTime() == null || di.getData() == null || len == 0)
			return dataOut;
		
		List<Date> times = di.getTime();
		List<String> datas = di.getData();
		Date t1 = times.get(0);
		Date t2 = getDateAfter(t1, granularity * 1000);
		Map<String,Integer> valsStr = new HashMap<String, Integer>(); // 字符串的聚合结果
		Set<String> varSet=new HashSet<String>();                     //字符串的集合
		List<Double> vals = new ArrayList<Double>(); 	// 数值的聚合结果
		
//		Date t = t1;									// 聚合后的时间点
		int i=0;
		int  flag=0;
		for(;!t1.after(times.get(times.size()-1));t1=t2,t2 = getDateAfter(t2, granularity * 1000))
		{
			
			while(i<times.size()&&times.get(i).before(t2))
			{
				if(i>0&&times.get(i).before(times.get(i-1)))
				{
					System.out.println("序列未排序，请先排序！");
//					JOptionPane.showMessageDialog((new MainFrame()).topFrame, "序列未排序");
				
				}
				if (isDiscreteOrNonDouble){	// 离散值或字符串
					if(valsStr.containsKey(datas.get(i))){
						int originValue=valsStr.get(datas.get(i));
						valsStr.remove(datas.get(i));
						int newValue=originValue+1;
						valsStr.put(datas.get(i), newValue);
					}else{
						valsStr.put(datas.get(i), 1);
					}
					varSet.add(datas.get(i));
				}
				else{			// 若为连续值，则加至vals中，后续一起聚合
					try{
						double data= Double.parseDouble(datas.get(i));
						vals.add(data);
					}catch(Exception e){}					
				}
				i++;
			}
			//一个时间粒度内的值读完了，则建立新的值
			if(isDiscreteOrNonDouble){
//				StringBuilder sb = new StringBuilder();
//				for (String valStr : valsStr)
//					sb.append(valStr+" ");
//				if (sb.length() > 0)
//					dataOut.add1Data(t1, sb.toString().trim());
//				else
//					dataOut.add1Data(t1, "");
				dataOut.add1Data(t1, valsStr);
				flag++;
				valsStr.clear();
			}else{
				Double[] valsArray = vals.toArray(new Double[0]);
				
				if (valsArray.length > 0){
					Double val = aggregateDoubleVals(valsArray, method);
					dataOut.add1Data(t1, val.toString());
				}
				else
				{
					dataOut.add1Data(t1,String.valueOf(0.0));
				}
				vals.clear();
			}
		}			
		if(varSet.size()!=0){
			dataOut.setVarSet(varSet);
		}
		return dataOut;
	}
	
	//datItems在相同的时间粒度上的聚合
		public static DataItems aggregateData1(DataItems di,int granularity,
				AggregateMethod method,boolean isDiscreteOrNonDouble){	
			
			if(granularity ==3600)
				return di;
			DataItems dataOut = new DataItems();
			dataOut.setIsAllDataDouble(di.getIsAllDataDouble());
			int len = di.getLength();
			if (di == null || di.getTime() == null || di.getData() == null || len == 0)
				return dataOut;
			
			List<Date> times = di.getTime();
			List<String> datas = di.getData();
			Date t1 = times.get(0);
			Date t2 = getDateAfter(t1, granularity * 1000);
			Map<String,Integer> valsStr = new HashMap<String, Integer>(); // 字符串的聚合结果
			Set<String> varSet=new HashSet<String>();                     //字符串的集合
			List<Double> vals = new ArrayList<Double>(); 	// 数值的聚合结果
			
//			Date t = t1;									// 聚合后的时间点
			int i=0;
			int  flag=0;
			for(;!t1.after(times.get(times.size()-1));t1=t2,t2 = getDateAfter(t2, granularity * 1000))
			{
				
				while(i<times.size()&&times.get(i).before(t2))
				{
					if(i>0&&times.get(i).before(times.get(i-1)))
					{
						System.out.println("序列未排序，请先排序！");
//						JOptionPane.showMessageDialog((new MainFrame()).topFrame, "序列未排序");
					
					}
					if (isDiscreteOrNonDouble){	// 离散值或字符串
						if(valsStr.containsKey(datas.get(i))){
							int originValue=valsStr.get(datas.get(i));
							valsStr.remove(datas.get(i));
							int newValue=originValue+1;
							valsStr.put(datas.get(i), newValue);
						}else{
							valsStr.put(datas.get(i), 1);
						}
						varSet.add(datas.get(i));
					}
					else{			// 若为连续值，则加至vals中，后续一起聚合
						try{
							double data= Double.parseDouble(datas.get(i));
							vals.add(data);
						}catch(Exception e){}					
					}
					i++;
				}
				//一个时间粒度内的值读完了，则建立新的值
				if(isDiscreteOrNonDouble){
//					StringBuilder sb = new StringBuilder();
//					for (String valStr : valsStr)
//						sb.append(valStr+" ");
//					if (sb.length() > 0)
//						dataOut.add1Data(t1, sb.toString().trim());
//					else
//						dataOut.add1Data(t1, "");
					dataOut.add1Data(t1, valsStr);
					flag++;
					valsStr.clear();
				}else{
					Double[] valsArray = vals.toArray(new Double[0]);
					
					if (valsArray.length > 0){
						Double val = aggregateDoubleVals(valsArray, method);
						dataOut.add1Data(t1, val.toString());
					}
					else
					{
						dataOut.add1Data(t1,String.valueOf(0.0));
					}
					vals.clear();
				}
			}			
			if(varSet.size()!=0){
				dataOut.setVarSet(varSet);
			}
			return dataOut;
		}
	/**
	 * 根据discreteMethod,对该数据进行离散化
	 * @param discreteMethod	离散化方法
	 * @param numDims			离散后的维数
	 * @param endNodes			自定义端点，仅在自定义离散化方法条件下有效
	 * @return
	 */
	public static DataItems toDiscreteNumbers(DataItems dataItems,DiscreteMethod discreteMethod, int numDims, String endNodes){
		DataItems newDataItems = null;
		switch (discreteMethod) {
		case 各区间数值范围相同:
			newDataItems = toDiscreteNumbersAccordingToMean3Sigma(dataItems,numDims);
			break;
		case 各区间数据点数相同:
			newDataItems = toDiscreteNumbersAccordingToPercentile(dataItems,numDims);
			break;
		case 自定义端点:
			newDataItems = toDiscreteNumbersAccordingToCustomNodes(dataItems,endNodes);
			break;
		case None://不做离散化,直接返回
		default:
			newDataItems = dataItems;
		}
		return newDataItems;
		
	}
	
	/**
	 * 根据用户指定的节点进行离散化
	 * @param endNodes 用户指定的界点
	 * @return 离散化后的DataItems
	 */
	private static DataItems toDiscreteNumbersAccordingToCustomNodes(DataItems dataItems,String endNodes){
		DataItems newDataItems = new DataItems();
		newDataItems.setIsAllDataDouble(dataItems.getIsAllDataDouble());
		newDataItems.setVarSet(dataItems.getVarSet());
		if (endNodes == null || endNodes.length() == 0)
			return newDataItems;
		String []nodesStr = endNodes.split(",");
		int numDims = nodesStr.length;
		Double[] discreteNodes=new Double[numDims];
		for (int i = 0; i < numDims; i ++)
			discreteNodes[i] = Double.parseDouble(nodesStr[i]);
		newDataItems.setDiscreteNodes(discreteNodes);
		int len = dataItems.getLength();
		for (int i = 0; i < len; i ++){		
			if(dataItems.isAllDataIsDouble()){
		    	newDataItems.add1Data(dataItems.getTime().get(i),
		        getIndexOfData(numDims,Double.parseDouble(dataItems.getData().get(i)),newDataItems.getDiscreteNodes()));
			}else{
				Map<String, Integer> map=dataItems.getNonNumData().get(i);
				Map<String, Integer> discreMap=new HashMap<String, Integer>();
				Iterator<Map.Entry<String, Integer>> iter=map.entrySet().iterator();
				while(iter.hasNext()){
					Map.Entry<String, Integer>entry=iter.next();
					discreMap.put(entry.getKey(),
					(int) Double.parseDouble(getIndexOfData(numDims,entry.getValue(),newDataItems.getDiscreteNodes())));
				}
				newDataItems.add1Data(dataItems.getTime().get(i), discreMap);
			}
		}
		return newDataItems;
	}
	/**
	 * 将区间[mean-3*sigma，mean+3*sigma]平均划分为numDims个区间，离散化得到的dataItems。
	 * @param numDims	离散后的取值数
	 * @return	已经离散化的dataItems数据
	 */
	private static DataItems toDiscreteNumbersAccordingToMean3Sigma(DataItems dataItems,int numDims){
		DataItems newDataItems=new DataItems();
		newDataItems.setIsAllDataDouble(dataItems.getIsAllDataDouble());
		Double minVal = Double.MAX_VALUE;
		Double maxVal = Double.MIN_VALUE;
		
		List<String> datas=dataItems.getData();
		int length=datas.size();
		
		// 首先，判断取值个数，如果仅为20个值以下，则直接将值作为离散值
		boolean isDiscrete = dataItems.isDiscrete();
		
		// 直接当离散值处理
		if (!isDiscrete){
			if(!dataItems.isAllDataIsDouble()){
				throw new RuntimeException("非数值型数据不能离散化");
			}
			DescriptiveStatistics statistics=new DescriptiveStatistics();
			double mean = 0.0;
			double std = 0.0;
			for(String data:datas){
				statistics.addValue(Double.parseDouble(data));
			}
			mean=statistics.getMean();
			std=statistics.getStandardDeviation();
			minVal=mean-4*std;
			maxVal=mean+4*std;
			
			Double[] discreteNodes=new Double[numDims];
			for (int i = 0; i < numDims; i ++){
				discreteNodes[i] = Math.ceil(minVal + (maxVal - minVal) * i / numDims);
			}
			for (int i = 0; i < length; i ++){
				DataItem item = dataItems.getElementAt(i);
				Double val = null;
				val = Double.parseDouble(item.getData());
				if (val != null){
					String ind = getIndexOfData(numDims,val,discreteNodes);
					newDataItems.add1Data(item.getTime(), ind);
				}
			}
			newDataItems.setDiscreteNodes(new Double[numDims]);
			return newDataItems;
		}else{
			return dataItems;
		}
		
	}
	/**
	 * 根据分位点来进行离散化
	 * @param numDims
	 * @return
	 */
	private static DataItems toDiscreteNumbersAccordingToPercentile(DataItems dataItems,int numDims){
		
		DataItems newDataItems=new DataItems();
		List<String> datas=dataItems.getData();
		int length=datas.size();
		
		boolean isDiscrete = dataItems.isDiscrete();
		if (isDiscrete){	// 直接当离散值处理
			return dataItems;
		}else{				// 连续值，需要进行离散化
			double step = 1.0 / numDims * length; 
			int ind = 0;
			int ind_step = (int) ((ind + 1) * step - 1);
			Double[] discreteNodes = new Double[numDims];
			DataItems sortedItems = DataItems.sortByDoubleValue(dataItems);
			discreteNodes[0] = Double.parseDouble(datas.get(0));
			if(!dataItems.isAllDataIsDouble()){
				throw new RuntimeException("非数值型数据不能离散化");
			}
			datas=sortedItems.getData();
			for (int i = 0; i < length; i ++){
				Double val = Double.parseDouble(datas.get(i));
				if (i > ind_step){
					discreteNodes[ind + 1] = val;
					ind ++;
					ind_step = (int) ((ind + 1) * step - 1);
				}				
			}

			for (int i = 0; i < length; i ++){				
				newDataItems.add1Data(sortedItems.getTime().get(i),
						getIndexOfData(numDims, Double.parseDouble(datas.get(i)),discreteNodes));
			}
			newDataItems.setDiscreteNodes(discreteNodes);
			return newDataItems;
		}
	}
	public static DataItems toDiscreteNumbersAccordingToWaveform(DataItems dataItems,TaskElement task)
	{
		
		return WavCluster.waveTest(dataItems,task);
	}
	
	
	public static DataItems toDiscreteNumbersAccordingToSegment(DataItems dataItems,TaskElement task)
	{
		return WavCluster.segmentTest(dataItems, task);
	}
}