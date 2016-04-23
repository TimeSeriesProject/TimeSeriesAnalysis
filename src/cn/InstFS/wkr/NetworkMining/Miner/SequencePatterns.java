package cn.InstFS.wkr.NetworkMining.Miner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.MergeSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.Segment;
import cn.InstFS.wkr.NetworkMining.DataInputs.WavCluster;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;;
/**
 * 
 * @author 艾长青 
 * @time 2015/11/11
 *
 */
public class SequencePatterns {


	private DataItems dataItems;
	private TaskElement task;
	private List<ArrayList<String>> patterns;
	private int winSize = 100; // 单位为秒
	private int stepSize = 10;
	private int clusterNum = 10;
	private Date minDate = null;
	private double threshold = 0.8;
	private boolean hasPattern=false;

	public static void main(String[] args) {
		TaskElement task = new TaskElement();
		task.setSourcePath("./configs/real-1-11.csv");
		task.setDataSource("Text");
		task.setTaskRange(TaskRange.NodePairRange);
		task.setFilterCondition("protocol=" + "402");  //402 --- 410都可以
		task.setGranularity(3600);
		task.setMiningObject("traffic");

		String ip[] = new String[] { "10.0.1.1", "10.0.1.2" };
		IReader reader = new nodePairReader(task, ip);
		DataItems tmp = reader.readInputByText();
		DataItems dataItems = new DataItems();
		DataItems clusterItems=new DataItems();
		for (int k = 0; k < tmp.getLength(); k++) {
			DataItem dataItem = tmp.getElementAt(k);
			dataItem.setData(String.valueOf(Double.valueOf(dataItem.getData()) / 2));
			dataItems.add1Data(dataItem);
		}

		dataItems = DataPretreatment.aggregateData(dataItems, 3600,
				AggregateMethod.Aggregate_MEAN, false);
		clusterItems = WavCluster.segmentSelfCluster(dataItems);
		
		List<ArrayList<String>> patternsResult = new ArrayList<ArrayList<String>>();
		SequencePatterns sp = new SequencePatterns(clusterItems, task,
				patternsResult);
		
		sp.patternMining();
		//sp.displayResult();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Date date = new Date();
		System.out.println(date);
	}

	public SequencePatterns(DataItems dataItems, TaskElement task,
			List<ArrayList<String>> patterns) {
		this.dataItems = dataItems;
		this.task = task;
		this.patterns = patterns;
		ParamsSM psm = (ParamsSM)task.getMiningParams();
		winSize = (int)psm.getSizeWindow();
	}
	public SequencePatterns() {
	}
	
	/**
	 * 外部使用本类的功能事，需要根据构造函数传递对应的参数，然后代用该函数,即可返回频繁项的结果
	 * 注意1：在划分序列时，我们采用默认windSize = 100，若需改变其大小，请在调用本函数之前调用setWindSize(int)函数
	 * 注意2：在调用本方法之前通过构造函数传递的参数DataItems，需要根据date从小到大排序
	 * @return 
	 */
	public List<ArrayList<String>> patternMining() {
		Date max_date = getMaxDate();
		Date min_date = getMinDate();
		minDate = min_date;
		HashSet<String> clusterLabel = getClusterNum(); // 得到序列聚类的个数
		clusterNum = clusterLabel.size();

//		long timeSpan = (max_date.getTime() - min_date.getTime()) / 1000; // 相减结果是毫秒
		System.out.println("clusterNum:" + clusterNum);
		System.out.println("dataItems.data.size():" + dataItems.data.size());
//		winSize = 100;
		int sample_num = (int) Math.ceil(dataItems.data.size() * 1.0 / winSize);
		System.out.println("sample_num:" + sample_num);
		ArrayList<String> sliceSequence = getSliceSequence(winSize,stepSize); // 将一个样例以字符串形式给出

		System.out.println("样本大小："+sliceSequence.size());
		int length = sliceSequence.get(0).length();
		ArrayList<String> basicSequence = convertHashSetToArray(clusterLabel); // 初始化长度串

		System.out.println("sequenceProb:" + basicSequence.size());

		patterns = getFrequentItemSet(sliceSequence, basicSequence, threshold);
		return patterns;
	}
	/**
	 * 该函数功能是找频繁项
	 * @param sliceSequence
	 * @param BasicSequence
	 * @param thresh
	 * @return
	 */
	private ArrayList<ArrayList<String>> getFrequentItemSet(
			ArrayList<String> sliceSequence,
			ArrayList<String> BasicSequence, double thresh) {
		
		ArrayList<ArrayList<String>> FrequentItemSet = new ArrayList<ArrayList<String>>();

		HashMap<String, ArrayList<SampleIndex>> position = getItemSamplePosition(
				sliceSequence, BasicSequence,thresh);                              //在读取位置时，直接把不满足条件的删掉
		ArrayList<String> sequenceResult = (ArrayList<String>) BasicSequence.clone();
		ArrayList<String> NewestSequence = (ArrayList<String>) BasicSequence.clone();

		int count = 0;
		while (true) {
			
			System.out.println("迭代次数："+(++count) +"  满足条件大小："+NewestSequence.size());
			NewestSequence = getNewFrequentItemsAndJudge(BasicSequence,NewestSequence,
					sliceSequence,position,thresh);
			if(NewestSequence.size() == 0)
				break;
			sequenceResult.addAll(NewestSequence);
		}
		ArrayList<ArrayList<String>> patternResult = convertToStandard(sequenceResult);
		return patternResult;
	}
	/**
	 * 该函数功能是将找出的频繁项转化为用户需要的格式（返回值格式）
	 * @param sequenceResult
	 * @return
	 */
	private ArrayList<ArrayList<String>> convertToStandard(
			ArrayList<String> sequenceResult) {
		
		ArrayList<ArrayList<String>> patternResult = new ArrayList<ArrayList<String>>();
		for(int i = 0;i < sequenceResult.size();i++)
		{
			ArrayList<String> items = new ArrayList<String>();
			String[] sample = sequenceResult.get(i).split(",");
			for(int j = 0;j < sample.length;j++)
			{
				items.add(sample[j]);
			}
			patternResult.add(items);
		}
		return patternResult;
	}
	/**
	 * 产生新的序列，并判断是否为频繁项，返回的结果为满足条件的频繁项
	 * @param basicSequence
	 * @param newestSequence
	 * @param sliceSequence
	 * @param position
	 * @param thresh
	 * @return
	 */
	private ArrayList<String> getNewFrequentItemsAndJudge(
			ArrayList<String> basicSequence,
			ArrayList<String> newestSequence,
			ArrayList<String> sliceSequence,
			HashMap<String, ArrayList<SampleIndex>> position, double thresh) {
		ArrayList<String> new_sequence = new ArrayList<String>();
		
		for(int i = 0;i < newestSequence.size();i++)
		{
			for(int j = 0;j < basicSequence.size();j++)
			{
				
				String first_item = newestSequence.get(i);
				String last_item = basicSequence.get(j);
				if(isSatisfied(first_item,last_item,sliceSequence,position,thresh))
				{
					new_sequence.add(first_item+","+last_item);
				}
			}
		}
		return new_sequence;
	}
	/**
	 * 判断某个具体的序列是否为频繁项，是返回true，否则返回false
	 * @param first_item
	 * @param last_item
	 * @param sliceSequence
	 * @param position
	 * @param thresh
	 * @return
	 */
	private boolean isSatisfied(String first_item, String last_item,
			ArrayList<String> sliceSequence,
			HashMap<String, ArrayList<SampleIndex>> position, double thresh) {
		
		ArrayList<SampleIndex> ps = position.get(first_item);
		if(ps == null)
			return false;
		ArrayList<SampleIndex> asi_list = new ArrayList<SampleIndex>();
		for(int i = 0;i < ps.size();i++)
		{
			int index = ps.get(i).index;
			SampleIndex new_si = new SampleIndex();
			String sample = sliceSequence.get(index);  //根据下标读取样本记录下标
			for(int j = 0;j < ps.get(i).sample_position.size();j++)
			{
				int p = ps.get(i).sample_position.get(j);
				int k = 0,m = 0;
				for(k = p+first_item.length()+1,m = 0;k < sample.length() && m < last_item.length();k++,m++)
				{
					if(sample.charAt(k) != last_item.charAt(m))
						break;
				}
				if(m == last_item.length())
				{
					new_si.sample_position.add(p);
					ps.get(i).sample_position.remove(j);
					j--;
				}
			}
			if(new_si.sample_position.size() > 0)
			{
				new_si.index = index;
				asi_list.add(new_si);
			}
		}
		
		if(asi_list.size()*1.0/sliceSequence.size() > thresh)
		{
			position.put(first_item+","+last_item, asi_list);
			return true;
		}
		return false;
	}
	/**
	 * 得到起始频繁项（单个类）在样本中出现的位置，并返回
	 * @param sliceSequence
	 * @param basicSequence
	 * @param thresh
	 * @return
	 */
	private HashMap<String, ArrayList<SampleIndex>> getItemSamplePosition(
			ArrayList<String> sliceSequence,
			ArrayList<String> basicSequence,double thresh) {

		int length = sliceSequence.get(0).length();
		System.out.println("basic sequence.size:"+basicSequence.size());
		HashMap<String, ArrayList<SampleIndex>> position = new HashMap<String, ArrayList<SampleIndex>>();
		for (int i = 0; i < basicSequence.size(); i++) // 遍历频繁项
		{
			String item = basicSequence.get(i);
			// System.out.println(item.get(0));
			ArrayList<SampleIndex> labelPosition = new ArrayList<SampleIndex>();
//			System.out.println("basic sequence:"+i);
			for (int j = 0; j < sliceSequence.size(); j++) // 遍历样本
			{
				ArrayList<Integer> CP = new ArrayList<Integer>();
				SampleIndex si = new SampleIndex();
				
//				System.out.println("sample:"+sliceSequence.get(j));
				int len = 0;
				while(true)
				{
					int index = sliceSequence.get(j).indexOf(item, len);
//					System.out.println(index);
					if(index == -1)
						break;

					len += item.length();
					si.sample_position.add(index);
					
				}
				
				if(si.sample_position.size() != 0)
				{
					si.index = j;
					labelPosition.add(si);
				}
			}
			if(labelPosition.size()*1.0/sliceSequence.size() > thresh)
			{
				position.put(item, labelPosition);
			}
			else
			{
				basicSequence.remove(i);
				i--;
			}

		}
		return position;
	}
	/**
	 * 为了计算的方便，将基本的频繁项由Set格式转化为ArrayList格式
	 * @param clusterLabel
	 * @return
	 */
	private ArrayList<String> convertHashSetToArray(
			HashSet<String> clusterLabel) {

		ArrayList<String> sequenceProb = new ArrayList<String>();
		java.util.Iterator<String> it = clusterLabel.iterator();
		while (it.hasNext()) {
			
			String str = it.next();
			sequenceProb.add(str);
		}
		return sequenceProb;
	}
	/**
	 * 找出待找频繁项样例的最小时间
	 * @return
	 */
	private Date getMinDate() {

		Date min_date = dataItems.time.get(0);

		for (int i = 1; i < dataItems.getLength(); i++) {
			Date date = dataItems.time.get(i);
			if (min_date.after(date))
				min_date = date;
		}
		return min_date;
	}
	/**
	 *找出待找频繁项样例的最大时间 
	 */
	private Date getMaxDate() {

		Date max_date = dataItems.time.get(0);

		for (int i = 1; i < dataItems.getLength(); i++) {
			Date date = dataItems.time.get(i);
			if (max_date.before(date))
				max_date = date;
		}
		return max_date;
	}
	/**
	 * 得到给出样例中包含多少个类
	 * @return
	 */
	private HashSet<String> getClusterNum() {

		HashSet<String> set = new HashSet<String>();
		for (int i = 0; i < dataItems.data.size(); i++) {
			
			String data = dataItems.data.get(i);
			set.add(data);
		}
		return set;
	}

	/**
	 * 为了计算的方便，将每个样例转化为字符串处理
	 * @param sample_num
	 * @param stepSize 
	 * @return
	 */
	private ArrayList<String> getSliceSequence(int sample_num, int stepSize) {

		ArrayList<String> sliceSequence = new ArrayList<String>();
		int dataLen=dataItems.getLength();
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < dataLen; i+=stepSize) {
			for(int j=0;j<winSize;j++){
				if(i+j>=dataLen){
					break;
				}
				sb.append(dataItems.data.get(i+j)).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sliceSequence.add(sb.toString());
			sb.delete(0, sb.length());
		}
		
//		for(int i = 0;i < sliceSequence.size();i++)
//		{
//			System.out.println(sliceSequence.get(i));
//		}
		return sliceSequence;

	}
	/**
	 * 计算当前产生该类的时间与所有样例中最小时间的差
	 * @param date
	 * @return
	 */
	private Long sparseTime(Date date) {

		long dis = (date.getTime() - minDate.getTime()) / 1000;
		return dis;
	}
	/**
	 * 打印返回的结果
	 */
	public void displayResult() {
//		System.out.println("displayResult....   频繁项数据量为：" + patterns.size());
//		for (int i = 0; i < patterns.size(); i++) {
//			System.out.format("频繁项 %d  ", i);
//			for (int j = 0; j < patterns.get(i).size(); j++) {
//				System.out.print(patterns.get(i).get(j) + ",");
//			}
//			System.out.println();
//		}
	}
	public DataItems getDataItems() {
		return dataItems;
	}

	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
	}

	public TaskElement getTask() {
		return task;
	}

	public void setTask(TaskElement task) {
		this.task = task;
	}

	public boolean isHasPattern() {
		return hasPattern;
	}

	public void setHasPattern(boolean hasPattern) {
		this.hasPattern = hasPattern;
	}

	public List<ArrayList<String>> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<ArrayList<String>> patterns) {
		this.patterns = patterns;
	}

	public int getWinSize() {
		return winSize;
	}
	public void setWinSize(int winSize) {
		this.winSize = winSize;
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getStepSize() {
		return stepSize;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}
	/**
	 * 打印聚类后 每个类标签包含的线段
	 * @param clusterItems 聚类后的DataItems
	 */
	public Map<Integer, List<String>> printClusterLabelTOLines(DataItems clusterItems,DataItems dataItems){
		List<Integer> indexOfClusterItem=new ArrayList<Integer>();
		int i=0;
		for(Date time:clusterItems.getTime()){
			for(;i<dataItems.getLength();i++){
				if(time.equals(dataItems.getTime().get(i))){
					indexOfClusterItem.add(i);
					break;
				}
			}
		}
		for(int index:indexOfClusterItem){
			System.out.print(index+",");
		}
		System.out.println();
		HashMap<Integer, List<String>> map=new HashMap<Integer, List<String>>();
		for(int label=0;label<99;label++){
			for(int itemIndex=0;itemIndex<clusterItems.getLength();itemIndex++){
				if(clusterItems.getData().get(itemIndex).equals(label+"")){
					if(map.containsKey(label)){
						List<String> list=map.get(label);
						if(itemIndex==clusterItems.getLength()-1){
							list.add(indexOfClusterItem.get(itemIndex)+","+dataItems.getLength());
						}else{
							list.add(indexOfClusterItem.get(itemIndex)+","+indexOfClusterItem.get(itemIndex+1));
						}
						map.put(label, list);
					}else{
						List<String> list=new ArrayList<String>();
						if(itemIndex==clusterItems.getLength()-1){
							list.add(indexOfClusterItem.get(itemIndex)+","+dataItems.getLength());
						}else{
							list.add(indexOfClusterItem.get(itemIndex)+","+indexOfClusterItem.get(itemIndex+1));
						}
						map.put(label, list);
					}
				}
			}
		}
		
		
		Iterator<Entry<Integer, List<String>>> iterator=map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Integer, List<String>>entry=iterator.next();
			System.out.print(entry.getKey());
			for(String item:entry.getValue()){
				System.out.print(":"+item);
			}
			System.out.println();
		}
		return map;
	}
	
}
class SampleIndex{
	
	int index = -1;
	ArrayList<Integer> sample_position = new ArrayList<Integer>();
	
}
