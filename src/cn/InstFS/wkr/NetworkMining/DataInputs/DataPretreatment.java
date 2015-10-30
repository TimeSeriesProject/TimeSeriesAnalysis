package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
//import org.hamcrest.Matcher;












import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;

public class DataPretreatment {
	
	//获取给定毫秒数之后的时间
	private static Date getDateAfter(Date curTime, int milliSeconds){
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
	
	//datItems在相同的时间粒度上的聚合
	public static DataItems aggregateData(DataItems di,int granularity,
			AggregateMethod method,boolean isDiscreteOrNonDouble){	
		
		DataItems dataOut = new DataItems();
		int len = di.getLength();
		if (di == null || di.getTime() == null || di.getData() == null || len == 0)
			return dataOut;
		
		List<Date> times = di.getTime();
		List<String> datas = di.getData();
		Date t1 = times.get(0);
		Date t2 = getDateAfter(t1, granularity * 1000);
		TreeSet<String> valsStr = new TreeSet<String>();// 字符串的聚合结果
		List<Double> vals = new ArrayList<Double>(); 	// 数值的聚合结果
		Date t = t1;									// 聚合后的时间点
		for (int i = 0; i < len; i++){
			Date time = times.get(i);
			if (time.equals(t2) || time.after(t2)){	// 若一个时间粒度内的值读完了，则建立新的值
				if(isDiscreteOrNonDouble){
					StringBuilder sb = new StringBuilder();
					for (String valStr : valsStr)
						sb.append(valStr+" ");
					if (sb.length() > 0)
						dataOut.add1Data(t, sb.toString().trim());
					valsStr.clear();
				}else{
					Double[] valsArray = vals.toArray(new Double[0]);
					if (valsArray.length > 0){
						Double val = aggregateDoubleVals(valsArray, method);
						dataOut.add1Data(t, val.toString());
					}
					vals.clear();
				}
				t1 = t2;
				t2 = getDateAfter(t2, granularity * 1000);
				t = time;
			}			
			if (!time.before(t1)){
				if (isDiscreteOrNonDouble)	// 离散值或字符串
					valsStr.add(datas.get(i));
				else{			// 若为连续值，则加至vals中，后续一起聚合
					try{
						double data= Double.parseDouble(datas.get(i));
						vals.add(data);
					}catch(Exception e){}					
				}
			}else if (time.before(t1))	// 这个不可能出现的，因为前期是按照时间顺序取的数据
				JOptionPane.showMessageDialog(MainFrame.topFrame, "哇，按时间先后顺序取数据有问题！");
		}	
		// 把最后一个时间段内的数据加进去
		if (isDiscreteOrNonDouble && valsStr.size() > 0){
			StringBuilder sb = new StringBuilder();
			for (String valStr : valsStr)
				sb.append(valStr + " ");
			if (sb.length() > 0)
				dataOut.add1Data(t, sb.toString().trim());
			valsStr.clear();
		}else if (!isDiscreteOrNonDouble && vals.size() > 0){
			Double[] valsArray = vals.toArray(new Double[0]);
			if (valsArray.length > 0){
				Double val = aggregateDoubleVals(valsArray, method);
				dataOut.add1Data(t, val.toString());
			}					
			vals.clear();
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
		if (endNodes == null || endNodes.length() == 0)
			return newDataItems;
		String []nodesStr = endNodes.split(",");
		int numDims = nodesStr.length;
		Double[] discreteNodes=new Double[numDims];
		for (int i = 0; i < numDims; i ++)
			discreteNodes[i] = Double.parseDouble(nodesStr[i]);
		newDataItems.setDiscreteNodes(discreteNodes);
		List<String> datas = dataItems.getData();
		int len = datas.size();
		for (int i = 0; i < len; i ++){				
			newDataItems.add1Data(dataItems.getTime().get(i),
		    getIndexOfData(numDims,Double.parseDouble(datas.get(i)),newDataItems.getDiscreteNodes()));
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
				discreteNodes[i] = minVal + (maxVal - minVal) * i / numDims;
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
		DataItems result = new DataItems(); 
		ArrayList<ArrayList<Double>> clustersCenter = new ArrayList<ArrayList<Double>>();
		int size =0;
		String fileName= task.getTaskRange().toString();
		Pattern p= Pattern.compile(".*协议\\s*=(\\d{3}).*");
		Matcher match =p.matcher(task.getFilterCondition());
		match.find();
		fileName+=match.group(1)+task.getGranularity();
		try
		{
			InputStreamReader ir = new InputStreamReader (new FileInputStream(fileName),"UTF-8");
			BufferedReader br    = new BufferedReader ( ir);
			String curLine =null;
		    size = Integer.valueOf(br.readLine());
			
			while((curLine = br.readLine())!=null)
			{
				String []num = curLine.split(" ");
				ArrayList<Double> center = new ArrayList<Double>();
				for(int i = 0 ;i<num.length;i++)
				{
					center.add(Double.valueOf(num[i]));
				}
				clustersCenter.add(center);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		for(int i=0;i<dataItems.getLength()&&(i+size-1)<dataItems.getLength();i+=size)
		{
			ArrayList <Double> vector = new ArrayList<Double>();
			DataItem dataItem = new DataItem();
			dataItem.setTime(dataItems.getElementAt(i).getTime());
			for(int j=i;j<i+size;j++)
			{
				vector.add(Double.valueOf(dataItems.getElementAt(j).getData()));
			}
			Double min = Double.MAX_VALUE;
			int index  =0;
			for(int j=0;j<clustersCenter.size();j++)
			{
				Double tmp = erpDistance(vector,clustersCenter.get(j));
				if(tmp<min)
				{
					min = tmp;
					index = j;
				}
			}
			dataItem.setData(String.valueOf(index));
			result.add1Data(dataItem);
		}
		return result;
	}
	public static void trainAll()
	{
		TaskElement task = new TaskElement();
		task.setSourcePath("E:\\javaproject\\NetworkMiningSystem\\HTTPPcap");
		
		for(TaskRange taskRange:TaskRange.values())
		{
//			System.out.println(taskRange);
			task.setTaskRange(taskRange);
			for(int procol=402;procol<=410;procol++)
			{
				task.setFilterCondition("协议="+procol);
				task.setGranularity(3600);
				train(task,10000);
			}
			
//			task.setDateStart(dateStart);
//			task.setDateEnd(dateEnd);
//			task.set
		}
	}
	public static void train(TaskElement task,double threshold)
	{
		String fileName= task.getTaskRange().toString();
		Pattern p= Pattern.compile(".*协议\\s*=(\\d{3}).*");
		Matcher match =p.matcher(task.getFilterCondition());
		match.find();
		fileName+=match.group(1)+task.getGranularity();
		switch(task.getTaskRange())
		{
		case NodePairRange:
		{
			ArrayList<DataItems> list = new ArrayList<DataItems>();
			ArrayList <String> ips = new ArrayList<String> ();
			for(int i=1;i<=10;i++)
				for(int j=1;j<=6;j++)
					ips.add("10.0.0."+i+"."+j);
			for(int i =0;i<ips.size();i++)
				for(int j=i+1;j<ips.size();j++)
				{
					String ip[] = new String[]{ips.get(i),ips.get(j)};
					IReader reader = new nodePairReader(task,ip);
					DataItems tmp = reader.readInputByText();
					DataItems dataItems = reader.readInputByText();
					for(int k=0;k<tmp.getLength();k++)
					{
						DataItem dataItem = tmp.getElementAt(k);
						dataItem.setData(String.valueOf(Double.valueOf(dataItem.getData())/2));
						dataItems.add1Data(dataItem);
					}
					list.add(dataItems);
					
				}
			runTrain(list,fileName,threshold);
			break;
		}
		case SingleNodeRange:break;
		case WholeNetworkRange:break;
		default: break;
		}
	}
	private static void runTrain(ArrayList <DataItems> list,String fileName,double threshold)
	{
		int windowSizeMin = 6;
		int windowSizeMax =  24;
		ArrayList<Double> instance;
		ArrayList<ArrayList<Double>>instances = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> clusterCenter = null;
		
//		ArrayList<ArrayList<ArrayList<Double>>> cluserList = new ArrayList<ArrayList<ArrayList<Double>>>();
		/**
		 * 每个窗口训练一次得到最佳窗口
		 */
		int min= Integer.MAX_VALUE;
		int optsize = windowSizeMin;
		for(int size = windowSizeMin;size<=windowSizeMax;size++)
		{
			
			for(int i=0;i<list.size();i++)
			{
				for(int j=0;j<list.get(i).getLength()&&(j+size-1)<list.get(i).getLength();j++)
				{
					instance = new ArrayList<Double>();
					for(int k=j;k<j+size;k++)
						instance.add(Double.valueOf(list.get(i).getElementAt(j).getData()));
					instances.add(instance);
				}
			}
			ArrayList<ArrayList<Double>> tmpclusterCenter = new ArrayList<ArrayList<Double>>();
			int tmp = singlePathCluster(instances,tmpclusterCenter,threshold);
			if(tmp<min)
			{
				min = tmp;
				clusterCenter = tmpclusterCenter;
				optsize = size;
			}
//			cluserList.add(clusterCenter);
		}
		/**
		 * 将窗口大小与类中心写入文件
		 * 首行为窗口大小，以下每行一个类中心向量
		 */
		try
		{
			OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
			BufferedWriter bw     = new BufferedWriter(ow);
			bw.write(optsize);
			for(int i =0 ;i<clusterCenter.size();i++)
			{
				bw.newLine();
				StringBuilder sb = new StringBuilder(); 
				for(int j = 0;j<clusterCenter.get(i).size();j++)
				{
					sb.append(clusterCenter.get(i).get(j)+" ");
				}
				sb =sb.deleteCharAt(sb.length()-1);
				
				bw.write(sb.toString());
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/**
	 * singlepath聚类
	 * @param instances       训练集
	 * @param clustersCenter 类中心列表
	 * @param threshold       阈值
	 * @return
	 */
	private static int singlePathCluster(ArrayList<ArrayList<Double>>instances,ArrayList<ArrayList<Double>> clustersCenter,double threshold)
	{
		double num=0;
		/**
		 * 存储每个簇有哪些instacece，只存索引
		 */
		ArrayList<ArrayList<Integer>> clusersInstanceList = new ArrayList<ArrayList<Integer>>();
		if(instances.size()>0)
		{
			clustersCenter.add(instances.get(0));
			ArrayList <Integer> list = new ArrayList<Integer>();
			list.add(0);
			clusersInstanceList.add(list);
		}
		for(int i=1;i<instances.size();i++)
		{
			double mindis =Double.MAX_VALUE ;
			double tmpdis=0.0;
			int index = 0;
			for(int j=0;j<clustersCenter.size();j++)
			{
				tmpdis = erpDistance(clustersCenter.get(j),instances.get(i));
				if(tmpdis<mindis)
				{
					mindis = tmpdis;
					index =j;
				}
				if(mindis>threshold)
				{
					/**
					 * 加入新簇
					 */
					clustersCenter.add(instances.get(i));
					ArrayList <Integer> list = new ArrayList<Integer>();
					list.add(i);
					clusersInstanceList.add(list);
				}
				else
				{
					/**
					 * 重新计算center
					 */
					clusersInstanceList.get(index).add(i);	//将该结点加入到簇
					double n = clusersInstanceList.get(index).size();
					 
					for(int k=0;k<clustersCenter.get(index).size();k++)
					{
						double tmp = (clustersCenter.get(index).get(k)*(n-1)+instances.get(i).get(k))/n;
						clustersCenter.get(index).set(k,tmp);
					}
				}
//				mindis = tmpdis<mindis?tmpdis:mindis;
			}
		}
		return clustersCenter.size();
	}
	private static double erpDistance(List<Double>list1,List<Double>list2)
	{
		double dp[][] = new double[list1.size()+1][list2.size()+1];
		dp[0][0]=0;
		for(int i=0;i<list1.size();i++)
			for(int j =0 ;j<list2.size();j++)
			{
				dp[i][j]=Double.MAX_VALUE;
				double tmp;
				if(i-1>=0&&j-1>=0)
				{
					tmp = dp[i-1][j-1]+Math.abs(list1.get(i)-list2.get(j));
					dp[i][j] =tmp<dp[i][j]?tmp:dp[i][j];
				}
				else if(i-1>=0)
				{
					tmp = dp[i-1][j]+list1.get(i);
					dp[i][j] = tmp<dp[i][j]?tmp:dp[i][j];
				}
				else if(j-1>=0)
				{
					tmp = dp[i][j-1]+list2.get(j);
					dp[i][j] = tmp<dp[i][j]?tmp:dp[i][j];
				}
			}
		
		return dp[list1.size()][list2.size()];
	}
	
	public static void main(String args[])
	{
		TaskElement task = new TaskElement();
		System.out.println(TaskRange.NodePairRange);
		Pattern p= Pattern.compile(".*协议\\s*=(\\d{3}).*");
		Matcher match = p.matcher("协议=402");
		match.find();
		System.out.println(match.group(1));
//		NodePairReader.
//		TaskElement task = new TaskElement();
		task.setSourcePath("E:\\javaproject\\NetworkMiningSystem\\HTTPPcap");
		task.setDataSource("Text");
		task.setTaskRange(TaskRange.NodePairRange);
		task.setFilterCondition("协议="+"402");
		task.setGranularity(3600);
		train(task,10000);
			
//		trainAll();
//		toDiscreteNumbersAccordingToWaveform
	}
}
