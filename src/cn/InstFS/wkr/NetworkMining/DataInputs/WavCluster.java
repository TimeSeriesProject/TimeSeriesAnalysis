package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

/**
 * 波形聚类
 * @author chenwei
 *
 */
public class WavCluster {
	
	/**
	 * 得到所有结点对的通信数据
	 * @param task
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static ArrayList<DataItems> getNodepairSamples(TaskElement task,Date startDate,Date endDate)
	{
		ArrayList<DataItems> list = new ArrayList<DataItems>();
		nodePairReader reader= new nodePairReader(task,new String[2]);
		String conditions[]=new String[0];
		Map<String,DataItems> ipPairItems = new HashMap<String,DataItems>();
		
		ipPairItems=reader.readAllPairBetween(startDate, endDate);
		for(Map.Entry<String, DataItems> entry:ipPairItems.entrySet())
		{
			
			DataItems tmp = entry.getValue();
			DataItems dataItems=new DataItems();
			for(int k=0;k<tmp.getLength();k++)
			{
				DataItem dataItem = tmp.getElementAt(k);
				dataItem.setData(String.valueOf(Double.valueOf(dataItem.getData())/2));
				dataItems.add1Data(dataItem);
			}
			dataItems=DataPretreatment.aggregateData(dataItems,3600,AggregateMethod.Aggregate_SUM,false);
			list.add(dataItems);
		}
		return list;
	}
	/**
	 * 得到所有单结点的通信数据
	 * @param task
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static ArrayList<DataItems>  getSingleNodeSamples(TaskElement task,Date startDate,Date endDate)
	{
		ArrayList<DataItems> list = new ArrayList<DataItems>();
		ArrayList <String> ips = new ArrayList<String> ();
		for(int i=1;i<=10;i++)
			for(int j=1;j<=6;j++)
				ips.add("10.0."+i+"."+j);
		for(int i =0;i<ips.size();i++)
		{
			String ip[] = new String[]{ips.get(i)};
			nodePairReader reader = new nodePairReader(task,ip);
			DataItems tmp = reader.readInputBetween(startDate,endDate);
			DataItems dataItems=new DataItems();
			for(int k=0;k<tmp.getLength();k++)
			{
				DataItem dataItem = tmp.getElementAt(k);
				
				dataItems.add1Data(dataItem);
			}
			
			dataItems=DataPretreatment.aggregateData(dataItems,3600,AggregateMethod.Aggregate_SUM,false);
			System.out.println("i "+i);
			System.out.println("list add "+dataItems.getLength());
			list.add(dataItems);
			
		}
		return list;
	}
	/**
	 * 对单结点以及结点对所有协议分别进行线段聚类训练
	 */
	public static void segmentTrainAll()
	{
		TaskElement task = new TaskElement();
		task.setSourcePath("E:/javaproject/NetworkMiningSystem/smtpPcap");
		task.setDataSource("Text");
		task.setMiningObject("traffic");
		/**
		 * 设置时间
		 */
		Calendar cal=Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		Date startDate=cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR,100);
		Date endDate=cal.getTime();
		
		for(TaskRange taskRange:TaskRange.values())
		{
//			System.out.println(taskRange);
			task.setTaskRange(taskRange);
			for(int procol=402;procol<=410;procol++)
			{
				task.setFilterCondition("protocol="+procol);
				task.setGranularity(3600);
				segmentTrain(task,0.3,100,startDate,endDate);
			}
			
		}
	}
	/*
	 * 对某个数据集进行线段聚类训练
	 */
	public static void segmentTrain(TaskElement task,double compratio,int clusternum,Date startDate,Date endDate)
	{
		
		String fileName="segment";
		fileName +=	task.getTaskRange().toString();
		Pattern p= Pattern.compile(".*protocol\\s*=(\\d{3}).*");
		Matcher match =p.matcher(task.getFilterCondition());
		match.find();
		fileName+=match.group(1)+task.getGranularity();
		ArrayList<DataItems> list;
		ArrayList<ArrayList<Double>>instances= new ArrayList<ArrayList<Double>>();
		switch(task.getTaskRange())
		{
		case NodePairRange:
		{
			list=getNodepairSamples(task,startDate,endDate);
			
			for(int i=0;i<list.size();i++)
			{
				DataItems dataItems = list.get(i);
				ArrayList<Segment> seglist = new ArrayList<Segment>();
				MergeSegment mergeSegment=new MergeSegment(dataItems,compratio);
				seglist=mergeSegment.getSegmentList();
				for(int j=0;j<seglist.size();j++)
				{
					ArrayList<Double>  instance = new ArrayList<Double>();
					instance.add(seglist.get(j).getCentery());
					instance.add(seglist.get(j).getLength());
					instance.add(seglist.get(j).getSlope());
					instances.add(instance);
				}
			}

			Kmeans(instances,clusternum,fileName,false);
//			System.out.println("listsize "+list.size());
//			ArrayList<ArrayList<Double>>instances=null;
//			segmentRunTrain(list,instances,fileName,0.3);
			break;
		}
		
		case SingleNodeRange:
		{
			
			list=getSingleNodeSamples(task,startDate,endDate);
			for(int i=0;i<list.size();i++)
			{
				DataItems dataItems = list.get(i);
				ArrayList<Segment> seglist = new ArrayList<Segment>();
				MergeSegment mergeSegment=new MergeSegment(dataItems,compratio);
				seglist=mergeSegment.getSegmentList();
				for(int j=0;j<seglist.size();j++)
				{
					ArrayList<Double>  instance = new ArrayList<Double>();
					instance.add(seglist.get(j).getCentery());
					instance.add(seglist.get(j).getLength());
					instance.add(seglist.get(j).getSlope());
					instances.add(instance);
				}
			}

			Kmeans(instances,clusternum,fileName,false);
		}
		case WholeNetworkRange:break;
		default: break;
		}
	}
	/*
	 * 对单结点以及结点对所有协议分别进行波形聚类训练
	 */
	public static void waveTrainAll()
	{
		TaskElement task = new TaskElement();
		task.setSourcePath("E:/javaproject/NetworkMiningSystem/smtpPcap");
		task.setDataSource("Text");
		task.setMiningObject("traffic");
		/**
		 * 设置时间
		 */
		Calendar cal=Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		Date startDate=cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR,100);
		Date endDate=cal.getTime();
		
		for(TaskRange taskRange:TaskRange.values())
		{
//			System.out.println(taskRange);
			task.setTaskRange(taskRange);
			for(int procol=402;procol<=410;procol++)
			{
				task.setFilterCondition("protocol="+procol);
				task.setGranularity(3600);
				waveTrain(task,6,100,startDate,endDate);
			}
			
		}
	}
	/**
	 * 对某个数据集进行波形聚类训练
	 * @param task
	 * @param windowsize
	 * @param clusternum
	 * @param startDate
	 * @param endDate
	 */
	public static void waveTrain(TaskElement task,int windowsize,int clusternum,Date startDate,Date endDate)
	{
		String fileName= task.getTaskRange().toString();
		Pattern p= Pattern.compile(".*protocol\\s*=(\\d{3}).*");
		Matcher match =p.matcher(task.getFilterCondition());
		match.find();
		fileName+=match.group(1)+task.getGranularity();
		
		ArrayList<DataItems> list=null;
		ArrayList<ArrayList<Double>>instances;
		
		switch(task.getTaskRange())
		{
		case NodePairRange:
		{
			list=getNodepairSamples(task,startDate,endDate);
//			System.out.println("listsize "+list.size());		
			break;
		}
		
		case SingleNodeRange:
		{
			list = getSingleNodeSamples(task,startDate,endDate);
			break;
		}
		case WholeNetworkRange:break;
		default: break;
		}
		
		ArrayList<Double> instance;
		instances = new ArrayList<ArrayList<Double>>();
		for(int i=0;i<list.size();i++)
		{
			for(int j=0;j<list.get(i).getLength()&&(j+windowsize-1)<list.get(i).getLength();j++)
			{
				instance = new ArrayList<Double>();
				for(int k=j;k<j+windowsize;k++)
					instance.add(Double.valueOf(list.get(i).getElementAt(k).getData()));
				instances.add(instance);
			}
		}
		Kmeans(instances,clusternum,fileName,false);
	}
	/**
	 * kmeans训练
	 * @param instances
	 * @param clusternum
	 * @param fileName
	 * @param preserveOrder
	 * @return kmeans对象
	 */
	public static SimpleKMeans Kmeans(ArrayList<ArrayList<Double>>instances,int clusternum,String fileName,boolean preserveOrder)
	{
		changesample2arff(instances,fileName+".arff");
		System.out.println(instances.size());
		SimpleKMeans  kMeans= new SimpleKMeans(); 
		try
		{
			ArffLoader arffloader	=	new	ArffLoader();
			arffloader.setFile(new File(fileName+".arff"));
			Instances dataset	=	arffloader.getDataSet();
//			DistanceFunction disFun = new	ManhattanDistance();
			kMeans.setDistanceFunction(new EuclideanDistance() );
			kMeans.setNumClusters(clusternum);
			kMeans.setMaxIterations(100);
			kMeans.setPreserveInstancesOrder(preserveOrder);
			kMeans.buildClusterer(dataset);
			kMeans.clusterInstance(dataset.get(0));
			
			SerializationHelper.write(fileName+".model", kMeans);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
//		changesample2arff(instances,fileName+".arff");
		return kMeans;
	}
	/**
	 * 对于某个时间序列用训练好的线段聚类模型测试
	 * @param dataItems
	 * @param task
	 * @return
	 */
	public static DataItems segmentTest(DataItems dataItems,TaskElement task)
	{
		DataItems result = new DataItems(); 
		ArrayList<ArrayList<Double>> clustersCenter = new ArrayList<ArrayList<Double>>();
		int size =6;
		String fileName="segment";
		fileName+= task.getTaskRange().toString();
		Pattern p= Pattern.compile(".*protocol\\s*=(\\d{3}).*");
		Matcher match =p.matcher(task.getFilterCondition());
		match.find();
		fileName+=match.group(1)+task.getGranularity();
		SimpleKMeans  kMeans= new SimpleKMeans(); 
		try
		{
	
			kMeans =(SimpleKMeans) SerializationHelper.read(fileName+".model");

			ArrayList<ArrayList<Double>> tmpInstances =new ArrayList<ArrayList<Double>>();
			
			
			ArrayList<Segment> seglist = new ArrayList<Segment>();
			MergeSegment mergeSegment=new MergeSegment(dataItems,0.3);
			seglist=mergeSegment.getSegmentList();
			
			for(int j=0;j<seglist.size();j++)
			{
				ArrayList<Double>  instance = new ArrayList<Double>();
				instance.add(seglist.get(j).getCentery());
				instance.add(seglist.get(j).getLength());
				instance.add(seglist.get(j).getSlope());
				tmpInstances.add(instance);
			}
			
			if(tmpInstances.size()==0)
				return result;
			System.out.println("why");
			WavCluster.changesample2arff(tmpInstances,"segmenttestwave.arff");
			ArffLoader arffloader	=	new	ArffLoader();
			arffloader.setFile(new File("segmenttestwave.arff"));
			Instances testInstances = arffloader.getDataSet();
			DataItem[] dataItemArray = new DataItem[testInstances.size()];
		
			for(int i=0;i<testInstances.size();i++)
			{

				dataItemArray[i]=new DataItem();	
				dataItemArray[i].setData(String.valueOf(kMeans.clusterInstance(testInstances.get(i))));
//				System.out.println(kMeans.clusterInstance(testInstances.get(i)));
				System.out.println(dataItemArray[i]+",");
			}
//			System.out.println(dataItemArray[i]+",");
			for(int i=0;i<testInstances.size();i++)
				result.add1Data(dataItemArray[i]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			throw new RuntimeException(e);
			System.exit(0);
		}
		return result;
	}
	/**
	 * 对某个时间序列，用对应的训练好的波形聚类模型测试
	 * @param dataItems
	 * @param task
	 * @return
	 */
	public static DataItems waveTest(DataItems dataItems,TaskElement task)
	{
		DataItems result = new DataItems(); 
		ArrayList<ArrayList<Double>> clustersCenter = new ArrayList<ArrayList<Double>>();
		int size =6;
		String fileName=task.getTaskRange().toString();
		
		Pattern p= Pattern.compile(".*protocol\\s*=(\\d{3}).*");
		Matcher match =p.matcher(task.getFilterCondition());
		match.find();
		fileName+=match.group(1)+task.getGranularity();
		SimpleKMeans  kMeans= new SimpleKMeans(); 
		try
		{

			kMeans =(SimpleKMeans) SerializationHelper.read(fileName+".model");
			ArrayList<ArrayList<Double>> tmpInstances =new ArrayList<ArrayList<Double>>();
			DataItem[] dataItemArray = new DataItem[dataItems.getLength()];
			for(int i=0;i<dataItems.getLength()&&(i+size-1)<dataItems.getLength();i+=size)
			{
				ArrayList <Double> vector = new ArrayList<Double>();
				DataItem dataItem = new DataItem();
				dataItem.setTime(dataItems.getElementAt(i).getTime());
				dataItemArray[i/size]= dataItem;
				for(int j=i;j<i+size;j++)
				{
					vector.add(Double.valueOf(dataItems.getElementAt(j).getData()));
					
				}
				tmpInstances.add(vector);
	
			}
			if(tmpInstances.size()==0)
				return result;
			System.out.println("why");
			WavCluster.changesample2arff(tmpInstances,"testwave.arff");
			ArffLoader arffloader	=	new	ArffLoader();
			arffloader.setFile(new File("testwave.arff"));
			Instances testInstances = arffloader.getDataSet();		
			for(int i=0;i<testInstances.size();i++)
			{

				
				dataItemArray[i].setData(String.valueOf(kMeans.clusterInstance(testInstances.get(i))));
//				System.out.println(kMeans.clusterInstance(testInstances.get(i)));
				System.out.println(dataItemArray[i]+",");
			}
//			System.out.println(dataItemArray[i]+",");
			for(int i=0;i<testInstances.size();i++)
				result.add1Data(dataItemArray[i]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			throw new RuntimeException(e);
			System.exit(0);
		}
		return result;
	}
	/**
	 * 对某个时间序列进行线段聚类，并返回聚类结果
	 * @param dataItems
	 * @param task
	 * @return
	 */
	public static DataItems segmentSelfCluster(DataItems dataItems)
	{
		DataItems result = new DataItems(); 
		ArrayList<ArrayList<Double>> instances =new ArrayList<ArrayList<Double>>();
		SimpleKMeans kMeans;
		ArrayList<Segment> seglist = new ArrayList<Segment>();
		MergeSegment mergeSegment=new MergeSegment(dataItems,0.3);
		seglist=mergeSegment.getSegmentList();
		for(int j=0;j<seglist.size();j++)
		{
			ArrayList<Double>  instance = new ArrayList<Double>();
			instance.add(seglist.get(j).getCentery());
			instance.add(seglist.get(j).getLength());
			instance.add(seglist.get(j).getSlope()*1000000000);
			instances.add(instance);
		}
		if(instances.size()==0)
			return result;
		kMeans = Kmeans(instances,10,"segmentSelfCluster",true);
		try
		{
			int labels[]=kMeans.getAssignments();
			
			for(int i=0;i<labels.length;i++)
			{
				
				DataItem dataItem =new DataItem();	
				dataItem.setData(String.valueOf(labels[i]));
				dataItem.setTime(seglist.get(i).getStartTime());
				result.add1Data(dataItem);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			throw new RuntimeException(e);
			System.exit(0);
		}
		return result;
	}
	/**
	 * 得到聚类后的结果，以及每个符号对应的原始点序列
	 * @param dataItems
	 * @return
	 */
	public static ArrayList<ResultItem> getSegmentSelfClusterDetailResult(DataItems dataItems,HashMap<Integer,ArrayList<Integer>> clusterInstances)
	{
		ArrayList<ResultItem> result = new ArrayList<ResultItem>(); 
		ArrayList<ArrayList<Double>> instances =new ArrayList<ArrayList<Double>>();
		SimpleKMeans kMeans;
		ArrayList<Segment> segList = new ArrayList<Segment>();
		MergeSegment mergeSegment=new MergeSegment(dataItems,0.3);
		
		segList=mergeSegment.getSegmentList();
		for(int j=0;j<segList.size();j++)
		{
			ArrayList<Double>  instance = new ArrayList<Double>();
			instance.add(segList.get(j).getCentery());
			instance.add(segList.get(j).getLength());
			instance.add(segList.get(j).getSlope()*2);
			instances.add(instance);
		}
		if(instances.size()==0)
			return result;
		kMeans = Kmeans(instances,20,"segmentSelfCluster",true);
		
		try
		{
			int labels[]=kMeans.getAssignments();
			
			for(int i=0;i<labels.length;i++)
			{
				
				ResultItem resultItem =new ResultItem();	
				resultItem.setPointList(segList.get(i).getPointList());
				resultItem.setCluster(labels[i]);
				result.add(resultItem);
				if(!clusterInstances.containsKey(labels[i]))
				{
					ArrayList<Integer> list =new ArrayList<Integer>();
					clusterInstances.put(labels[i], list);
				}
				clusterInstances.get(labels[i]).add(i);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			throw new RuntimeException(e);
			System.exit(0);
		}
		return result;
	}
	/**
	 * 对某个时间序列进行波形聚类，并返回聚类结果
	 * @param dataItems
	 * @param task
	 * @return
	 */
	public static DataItems waveSelfCluster(DataItems dataItems)
	{
		DataItems result = new DataItems(); 
		ArrayList<ArrayList<Double>> instances =new ArrayList<ArrayList<Double>>();
		int size =6;
		DataItem[] dataItemArray = new DataItem[dataItems.getLength()];
		for(int i=0;i<dataItems.getLength()&&(i+size-1)<dataItems.getLength();i+=size)
		{
			ArrayList <Double> vector = new ArrayList<Double>();
			DataItem dataItem = new DataItem();
			dataItem.setTime(dataItems.getElementAt(i).getTime());
			dataItemArray[i/size]= dataItem;
			for(int j=i;j<i+size;j++)
			{
				vector.add(Double.valueOf(dataItems.getElementAt(j).getData()));
				
			}
			instances.add(vector);
		}
		if(instances.size()==0)
			return result;
		SimpleKMeans  kMeans= Kmeans(instances,20,"waveSelfCluster",true); 
		try
		{
			int labels[]=kMeans.getAssignments();
			
			for(int i=0;i<labels.length;i++)
			{	
				dataItemArray[i].setData(String.valueOf(labels[i]));
				result.add1Data(dataItemArray[i]);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
	//		throw new RuntimeException(e);
			System.exit(0);
		}
		return result;
	}
	/**
	 * 将训练集转换成arff文件
	 * @param instances
	 * @param path
	 */
	public static void changesample2arff(ArrayList<ArrayList<Double>> instances,String path)
	{
		try
		{
			OutputStreamWriter ow = new OutputStreamWriter(
					new FileOutputStream(path), "UTF-8");
	
			BufferedWriter bw = new BufferedWriter(ow);
	
			bw.write("@relation "+path);
			bw.newLine();
			if(instances.size()>0)
			{
				for(int i =0;i<instances.get(0).size();i++)
				{
					bw.write("@attribute " + i + " numeric");
					bw.newLine();
				}
			}
			bw.write("@DATA");
			bw.newLine();
			for(int i=0;i<instances.size();i++)
			{
				StringBuilder sb=new StringBuilder();
//				sb.append("{");
				List<Double> instance = instances.get(i);
				for(int j=0;j<instance.size();j++)
				{
					sb.append(instance.get(j)+",");
				}
				sb.deleteCharAt(sb.length()-1);
//				sb.append("}");
				bw.write(sb.toString());
				bw.newLine();
			}
			
			bw.flush();
			bw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	public static void main(String args[])
	{

//		segmentTrainAll();
//		waveTrainAll();
		/**
		 * 测试用程序段，暂不要删除
		 */
		TaskElement task = new TaskElement();
		task.setSourcePath("E:/javaproject/NetworkMiningSystem/smtpPcap");
		task.setDataSource("Text");
		task.setTaskRange(TaskRange.SingleNodeRange);
		task.setFilterCondition("protocol="+"402");
		task.setGranularity(3600);
		task.setMiningObject("traffic");
		ArrayList <String> ips = new ArrayList<String> ();
		
		Calendar cal=Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		Date startDate=cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR,100);
		Date endDate=cal.getTime();
		
		for(int i=4;i<=4;i++)
			for(int j=1;j<=1;j++)
				ips.add("10.0."+i+"."+j);
		for(int i =0;i<ips.size();i++)
		{
				String ip[] = new String[]{ips.get(i)};
				nodePairReader reader = new nodePairReader(task,ip);
				DataItems tmp = reader.readInputBetween(startDate, endDate);
				System.out.println("why"+tmp.getLength());
				DataItems dataItems=new DataItems();
				for(int k=0;k<tmp.getLength();k++)
				{
					DataItem dataItem = tmp.getElementAt(k);
					dataItem.setData(String.valueOf(Double.valueOf(dataItem.getData())/2));
					dataItems.add1Data(dataItem);
				}
				
				dataItems=DataPretreatment.aggregateData(dataItems,3600,AggregateMethod.Aggregate_SUM,false);
				System.out.println("i "+i);
				System.out.println("list add "+dataItems.getLength());
				HashMap<Integer,ArrayList<Integer>> clusterInstances = new HashMap<Integer,ArrayList<Integer>>();
				ArrayList<ResultItem> result = getSegmentSelfClusterDetailResult(dataItems,clusterInstances);
				System.out.println(dataItems.getData());
				for(int l=0;l<clusterInstances.get(0).size();l++)
				{
					result.get(clusterInstances.get(0).get(l)).getPointList();
					result.get(clusterInstances.get(0).get(l)).getCluster();
				}
				System.out.println(segmentSelfCluster(dataItems).getData());
//				list.add(dataItems);
			
			}

		System.out.println("over");

	}
}


