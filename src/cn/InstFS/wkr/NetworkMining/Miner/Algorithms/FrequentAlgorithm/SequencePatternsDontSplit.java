package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.FrequentAlgorithm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oracle.net.aso.a;
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
import cn.InstFS.wkr.NetworkMining.Miner.Common.LineElement;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import ec.tstoolkit.utilities.Comparator;
/**
 * 
 * @author 艾长青 
 * @time 2016/04/13
 *
 */
public class SequencePatternsDontSplit {
	
	private DataItems dataItems;
	private TaskElement task;
	private List<ArrayList<String>> patterns;
	private int winSize = 100; // 单位为秒
	private int stepSize = 10;
	private Date minDate = null;
	private double threshold;
	private boolean hasFreItems=false;

	public SequencePatternsDontSplit(ParamsSM paramsSM) {
		
		this.winSize=paramsSM.getSMparam().getSizeWindow();
		this.stepSize=paramsSM.getSMparam().getStepWindow();
		this.threshold=paramsSM.getSMparam().getMinSupport();
		
	}
	public SequencePatternsDontSplit(DataItems dataItems, TaskElement task,
			List<ArrayList<String>> patterns,ParamsSM paramsSM) {
		this.dataItems = dataItems;
		this.task = task;
		this.patterns = patterns;
		
		this.winSize=paramsSM.getSMparam().getSizeWindow();
		this.stepSize=paramsSM.getSMparam().getStepWindow();
		this.threshold=paramsSM.getSMparam().getMinSupport();
	}
	
	/**
	 * 外部使用本类的功能事，需要根据构造函数传递对应的参数，然后代用该函数,即可返回频繁项的结果
	 * 注意1：在划分序列时，我们采用默认windSize = 100，若需改变其大小，请在调用本函数之前调用setWindSize(int)函数
	 * 注意2：在调用本方法之前通过构造函数传递的参数DataItems，需要根据date从小到大排序
	 * @return 
	 */
	public List<ArrayList<String>> patternMining() {
		HashSet<String> clusterLabel = getClusterNum(); // 得到序列聚类的个数
		String sliceSequence = getSliceSequence(); // 将一个样例以字符串形式给出
		ArrayList<String> basicSequence = convertHashSetToArray(clusterLabel); // 初始化长度串
		patterns = getFrequentItemSet(sliceSequence, basicSequence, threshold);
		return patterns;
	}
	/**
	 * 该函数功能是找频繁项
	 * @param sliceSequence  原始的时间序列
	 * @param BasicSequence  时间序列的基本元素
	 * @param thresh         频繁项的阈值
	 * @return 频繁项 LIst<List<String>>类型
	 */
	private ArrayList<ArrayList<String>> getFrequentItemSet(String sliceSequence,
			ArrayList<String> BasicSequence, double thresh) {
		
		//计算各个频繁序列可能出现的位置
		HashMap<String, ArrayList<Integer>> position = getItemSamplePosition(
				sliceSequence, BasicSequence,thresh);
		
		//得到满足条件的频繁项
		ArrayList<String> sequenceResult = new ArrayList<String>();
		//储备项集
		ArrayList<String> NewestSequence = (ArrayList<String>)BasicSequence.clone();
		while (true) {
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
	 * @param basicSequence  所有频繁单项集
	 * @param newestSequence 新增的频繁项集
	 * @param sliceSequence  原始时间序列
	 * @param position   频繁项集在原始时间序列位置
	 * @param thresh     频繁项集的阈值
	 * @return  所有新增的频繁项集
	 */
	private ArrayList<String> getNewFrequentItemsAndJudge(
			ArrayList<String> basicSequence,
			ArrayList<String> newestSequence,
			String sliceSequence,
			HashMap<String, ArrayList<Integer>> position, double thresh) {
		
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
	 * @param first_item 已检测的频繁项
	 * @param last_item  first_item+last_item为储备频繁项
	 * @param sliceSequence   原始时间序列
	 * @param position 频繁项在原始时间序列中的存储位置
	 * @param thresh  频繁项阈值
	 * @return  所有满足阈值的储备频繁项
	 */
	private boolean isSatisfied(String first_item, String last_item,
			String sliceSequence,HashMap<String, ArrayList<Integer>> position, double thresh) {
		
		ArrayList<Integer> ps = position.get(first_item);
		if(ps == null)
			return false;
		ArrayList<Integer> asi_list = new ArrayList<Integer>();
		int m = 0;
		int nextPos=-1;
		for(int i = 0;i < ps.size();i++)
		{
			int index = ps.get(i);
			if(index <= nextPos)
				continue;
			int j = 0;
			for(j = index+first_item.length()+1,m = 0;j < sliceSequence.length() && m < last_item.length();j++,m++)
			{
				if(sliceSequence.charAt(j) != last_item.charAt(m))
					break;
			}
			if(m == last_item.length())
			{
				asi_list.add(index);
				nextPos=index+first_item.length() + last_item.length()+1;
			}
			
		}
		if(asi_list.size()*1.0 > thresh)
		{
			hasFreItems=true;
			position.put(first_item+","+last_item, asi_list);
			return true;
		}
		return false;
	}
	/**
	 * 统计单一频繁项在样本中出现的位置
	 * @param sliceSequence 原始时间序列
	 * @param basicSequence 单一项集
	 * @param thresh        频繁阈值
	 * @return              单一频繁项集在原始时间序列中出现的位置
	 */
	private HashMap<String, ArrayList<Integer>> getItemSamplePosition(String sliceSequence,
			ArrayList<String> basicSequence,double thresh) {

		System.out.println("basic sequence.size:"+basicSequence.size());
		
		HashMap<String, ArrayList<Integer>> position = new HashMap<String, ArrayList<Integer>>();
		for (int i = 0; i < basicSequence.size(); i++) // 遍历频繁项
		{
			String item = basicSequence.get(i);
			if(item.length() == 0)
				continue;
			ArrayList<Integer> labelPosition = new ArrayList<Integer>();
			
			int len = 0;
			while(true)
			{
				int index = sliceSequence.indexOf(item, len);
				if(index == -1)
					break;
				if( (index >=  1 && sliceSequence.charAt(index-1) != ',') || 
						(index + item.length() < sliceSequence.length() && sliceSequence.charAt(index+item.length()) != ','))
				{
					len = index + 1;
					continue;
				}
				len = index + item.length();
				labelPosition.add(index);
			}
			if(labelPosition.size()>= thresh)
				position.put(item, labelPosition);
			else
			{
				basicSequence.remove(i);
				i--;
				if(i < 0)
					i = 0;
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
	private String getSliceSequence() {

		
		int dataLen=dataItems.getLength();
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < dataLen; i+=stepSize) {
			
			sb.append(dataItems.data.get(i)).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();

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
		System.out.println("displayResult....   频繁项数据量为：" + patterns.size());
		for (int i = 0; i < patterns.size(); i++) {
			System.out.format("频繁项 %d  ", i);
			for (int j = 0; j < patterns.get(i).size(); j++) {
				System.out.print(patterns.get(i).get(j) + ",");
			}
			System.out.println();
		}
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
		return map;
	}
	public boolean isHasFreItems() {
		return hasFreItems;
	}
	public void setHasFreItems(boolean hasFreItems) {
		this.hasFreItems = hasFreItems;
	}
	public List<LineElement> getLineElement(
			Map<Integer, List<String>> frequentItem) {

		List<LineElement> list = new ArrayList<LineElement>();
		
		Iterator<Integer> iter = frequentItem.keySet().iterator();
		while(iter.hasNext()){
			
			int label = iter.next();
			List<String> eList = frequentItem.get(label);
			for(int i = 0;i < eList.size();i++)
			{
				String[] strs = eList.get(i).split(",");
				LineElement le = new LineElement(label,Integer.parseInt(strs[0]),Integer.parseInt(strs[1]));
				list.add(le);
			}
		}
		class compareCmp implements java.util.Comparator<LineElement>{

			@Override
			public int compare(LineElement o1, LineElement o2) {
				
				return o1.getStart() - o2.getStart();
			}
			
		}
		Collections.sort(list, new compareCmp());
		return list;
	}
}
