package cn.InstFS.wkr.NetworkMining.Miner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;







import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class SequencePattern_m {

	private DataItems dataItems;
	private TaskElement task;
	private List<ArrayList<String>> patterns;
	private long winSize = 100; // 单位为秒
	private int clusterNum = 0;
	private Date minDate = null;

	public static void main(String[] args) {
		TaskElement task = new TaskElement();
		task.setSourcePath("D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\data\\smtpPcap");
		task.setDataSource("Text");
		task.setTaskRange(TaskRange.NodePairRange);
		task.setFilterCondition("protocol=" + "402");  //402 --- 410都可以
		task.setGranularity(3600);
		task.setMiningObject("traffic");

		String ip[] = new String[] { "10.0.1.1", "10.0.1.5" };
		IReader reader = new nodePairReader(task, ip);
		DataItems tmp = reader.readInputByText();
		DataItems dataItems = new DataItems();
		for (int k = 0; k < tmp.getLength(); k++) {
			DataItem dataItem = tmp.getElementAt(k);
			dataItem.setData(String.valueOf(Double.valueOf(dataItem.getData()) / 2));
			dataItems.add1Data(dataItem);
		}

		dataItems = DataPretreatment.aggregateData(dataItems, 3600,
				AggregateMethod.Aggregate_MEAN, false);
		dataItems = DataPretreatment.toDiscreteNumbersAccordingToWaveform(
				dataItems, task);
		// System.out.println("data.size:"+dataItems.data.size());

		List<ArrayList<String>> patternsResult = new ArrayList<ArrayList<String>>();
		SequencePattern_m sp = new SequencePattern_m(dataItems, task,
				patternsResult);
		sp.patternMining();
		sp.displayResult();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Date date = new Date();
		System.out.println(date);
	}

	public SequencePattern_m(DataItems dataItems, TaskElement task,
			List<ArrayList<String>> patterns) {

		this.dataItems = dataItems;
		this.task = task;
		this.patterns = patterns;
	}

	public void patternMining() {
		
		Date max_date = getMaxDate();
		Date min_date = getMinDate();
		minDate = min_date;
		HashSet<String> clusterLabel = getClusterNum(); // 得到序列聚类的个数
		clusterNum = clusterLabel.size();
		// long diff = d1.getTime() - d2.getTime();

		long distance = (max_date.getTime() - min_date.getTime()) / 1000; // 相减结果是毫秒
		System.out.println("clusterNum:" + clusterNum);
		System.out.println("distance:" + distance);
		winSize = 100;
		int sample_num = (int) Math.ceil(dataItems.data.size() * 1.0 / winSize);
		System.out.println("sample_num:" + sample_num);
		ArrayList<String> sliceSequence = getSliceSequence(sample_num); // 将一个样例以字符串形式给出

		String[] strSequence = sliceSequence.get(0).split(",");
		int length = strSequence.length;
		ArrayList<String> sequenceProb = convertHashSetToArray(clusterLabel); // 初始化长度串

		System.out.println("sequenceProb:" + sequenceProb.size());

		patterns = getFrequentItemSet(sliceSequence, sequenceProb, 0.25);
	}

	private ArrayList<ArrayList<String>> getFrequentItemSet(
			ArrayList<String> sliceSequence,
			ArrayList<String> sequenceProb, double thresh) {

		ArrayList<String> FrequentItemSet = new ArrayList<String>();

		HashMap<String, ArrayList<ArrayList<Integer>>> position = getItemSamplePosition(
				sliceSequence, sequenceProb);
		ArrayList<String> sequenceResult = new ArrayList<String>();

		while (true) {
			
			sequenceProb = removeNoItems(sequenceProb, sliceSequence, position,
					thresh);
//			System.out.println(sequenceProb.size());
			if(sequenceProb != null && sequenceProb.size() > 0)
				uniqueItem(sequenceProb,sequenceResult);
			System.out.println("有效的频繁项为：" + sequenceProb.size());
			if (sequenceProb.size() == 0)
				break;
			
			ArrayList<String> new_sequence = getNewFrequentItems(sequenceProb,sequenceResult);
			System.out.println("new_sequence:"+new_sequence.size());
			sequenceResult.addAll(sequenceProb);
			noRepeaedItem(sequenceResult);
			sequenceProb = new_sequence;
		}
		ArrayList<ArrayList<String>> result = convertToArrArr(sequenceResult);
		return result;
	}

	

	

	private ArrayList<String> getNewFrequentItems(
			ArrayList<String> sequenceProb, ArrayList<String> sequenceResult) {
		System.out.println("getNewFrequentItems .....   sequenceProb:"
				+ sequenceProb.size() +"  sequenceResult:"+sequenceResult.size());
		ArrayList<String> tmp_sequence = new ArrayList<String>();
		
		for (int i = 0; i < sequenceProb.size(); i++) {
			for (int j = 0; j < sequenceResult.size(); j++) {
				
				String tmp_ = "";
				tmp_ = sequenceProb.get(i)+","+sequenceResult.get(j);
				tmp_sequence.add(tmp_);
				
				tmp_ = sequenceResult.get(j)+","+sequenceProb.get(i);
				tmp_sequence.add(tmp_);
			}
		}
		for(int i = 0;i < sequenceProb.size();i++)
		{
			for(int j = 0;j < sequenceProb.size();j++)
			{
				String tmp_ = "";
				tmp_ = sequenceProb.get(i)+","+sequenceProb.get(j);
				tmp_sequence.add(tmp_);
				
				tmp_ = sequenceProb.get(j)+","+sequenceProb.get(i);
				tmp_sequence.add(tmp_);
			}
		}
		return tmp_sequence;
	}

	private ArrayList<String> removeNoItems(
			ArrayList<String> sequenceProb,ArrayList<String> sliceSequence,
			HashMap<String, ArrayList<ArrayList<Integer>>> position,
			double thresh) {
		
		System.out.println("removeNoItems.....");
		System.out.println("去重.....");
		for(int i = 0;i < sequenceProb.size();i++)
		{
			for(int j = i+1;j < sequenceProb.size();j++)
			{
				if(sequenceProb.get(i).compareTo(sequenceProb.get(j)) == 0)
				{
					sequenceProb.remove(j);
					j--;
				}
			}
		}
		System.out.println("去重之后剩余量为："+sequenceProb.size());
		System.out.println("搜索频繁项.....");
		for (int i = 0; i < sequenceProb.size(); i++) {
			String item = sequenceProb.get(i);
			System.out.println("item:"+item);
			if (isExceedThresh(item, sliceSequence, position, thresh))
				continue;
			else {
				sequenceProb.remove(i); // 删除第i个不满足条件的伪频繁项
				i--;
			}
		}
		return sequenceProb;

	}

	private boolean isExceedThresh(String item,
			ArrayList<String> sliceSequence,
			HashMap<String, ArrayList<ArrayList<Integer>>> position,
			double thresh) {

		int count = 0;
		for(int i = 0;i < sliceSequence.size();i++)
		{
			if(sliceSequence.get(i).contains(item))
				count++;
		}
		if (count * 1.0 / sliceSequence.size() > thresh) 
			return true;
			
//		if (position.containsKey(item)) {
//			ArrayList<ArrayList<Integer>> sample_p = position.get(item);
//			for (int i = 0; i < sample_p.size(); i++) {
//				
//				if(sample_p.contains(item))
//					count++;
//			}
//			if (count * 1.0 / sample_p.size() > thresh) {
//				System.out.println("满足条件频繁项：" + item.toString());
//				System.out.format("count:%d  sample:%d  value:%f\n", count,
//						sample_p.size(), count * 1.0 / sample_p.size());
//				return true;
//			}
//		}

		return false;
	}

	private boolean isContain(int p, ArrayList<String> item,
			ArrayList<String> arrayList) {

		int i = 0, j = 0;
		for (i = 0, j = p; i < item.size() && j < arrayList.size(); i++, j++) {
			if (item.get(i).compareTo(arrayList.get(j)) == 0)
				continue;
			else
				break;
		}
		if (i == item.size())
			return true;
		return false;
	}

	private HashMap<String, ArrayList<ArrayList<Integer>>> getItemSamplePosition(
			ArrayList<String> sliceSequence,
			ArrayList<String> sequenceProb) {


		HashMap<String, ArrayList<ArrayList<Integer>>> position = new HashMap<String, ArrayList<ArrayList<Integer>>>();
		for (int i = 0; i < sequenceProb.size(); i++) // 遍历频繁项
		{
			String item = sequenceProb.get(i);
			ArrayList<ArrayList<Integer>> labelPosition = new ArrayList<ArrayList<Integer>>();

			for (int j = 0; j < sliceSequence.size(); j++) // 遍历样本
			{
				ArrayList<Integer> CP = new ArrayList<Integer>();
				int len = 0;
//				System.out.println("len:"+sliceSequence.get(j).length());
				while(len < sliceSequence.get(j).length())
				{
					int index = sliceSequence.get(j).indexOf(item, len);
					if(index == -1)
						break;
					CP.add(index);
					len = index+item.length();
				}
				labelPosition.add(CP);
			}

			position.put(item, labelPosition);
		}
		return position;
	}

	private ArrayList<String> convertHashSetToArray(
			HashSet<String> clusterLabel) {

		ArrayList<String> sequenceProb = new ArrayList<String>();
		java.util.Iterator<String> it = clusterLabel.iterator();
		while (it.hasNext()) {
			ArrayList<String> item = new ArrayList<String>();
			String str = it.next();
			sequenceProb.add(str);
		}
		return sequenceProb;
	}

	private Date getMinDate() {

		Date min_date = dataItems.time.get(0);

		for (int i = 1; i < dataItems.getLength(); i++) {
			Date date = dataItems.time.get(i);
			if (min_date.after(date))
				min_date = date;
		}
		return min_date;
	}

	private Date getMaxDate() {

		Date max_date = dataItems.time.get(0);

		for (int i = 1; i < dataItems.getLength(); i++) {
			Date date = dataItems.time.get(i);
			if (max_date.before(date))
				max_date = date;
		}
		return max_date;
	}

	private HashSet<String> getClusterNum() {

		HashSet<String> set = new HashSet<String>();
		for (int i = 0; i < dataItems.getLength(); i++) {
			Date date = dataItems.time.get(i);
			Long time = sparseTime(date);
			String data = dataItems.data.get(i);
			set.add(data);
		}
		return set;
	}
	private void uniqueItem(ArrayList<String> sequenceProb,
			ArrayList<String> sequenceResult) {
		
		for(int i = 0;i < sequenceProb.size();i++)
		{
			for(int  j = 0;j < sequenceResult.size();j++)
			{
				if(sequenceResult.get(j).contains(sequenceProb.get(i)))
				{
					sequenceProb.remove(i);
					i--;
					break;
				}
			}
		}
		
	}

	private void noRepeaedItem(ArrayList<String> sequenceResult) {

		// 去重相同的频繁项
		for (int i = 0; i < sequenceResult.size(); i++) {
			for (int j = i + 1; j < sequenceResult.size(); j++) {
				if (sequenceResult.get(i).compareTo(sequenceResult.get(j)) == 0)
				{
					sequenceResult.remove(j);
					j--;
					break;
				}
			}
		}
	}
	private ArrayList<String> getSliceSequence(int sample_num) {

		ArrayList<String> sliceSequence = new ArrayList<String>();

		for (int i = 0; i < dataItems.getLength(); i++) {
			Date date = dataItems.time.get(i);
			String data = dataItems.data.get(i);

			Long time = sparseTime(date);
			int index = (int) (i / winSize);
			if (index >= sliceSequence.size()) {
				String sequence = dataItems.data.get(i);
				sliceSequence.add(sequence);
			} else {
				
				sliceSequence.set(index,sliceSequence.get(index)+","+dataItems.data.get(i));
			}
		}
		for(int i = 0;i < sliceSequence.size();i++)
			System.out.println(sliceSequence.get(i));
		return sliceSequence;
	}
	private ArrayList<ArrayList<String>> convertToArrArr(
			ArrayList<String> sequenceResult) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		for(int i = 0;i < sequenceResult.size();i++)
		{
			String[] str = sequenceResult.get(i).split(",");
			ArrayList<String> tmp = new ArrayList<String>();
			for(int j = 0;j < str.length;j++)
			{
				tmp.add(str[j]);
			}
			result.add(tmp);
		}
		return result;
	}
	private Long sparseTime(Date date) {

		long dis = (date.getTime() - minDate.getTime()) / 1000;
		return dis;
	}

	private void displayResult() {
		System.out.println("displayResult....   频繁项数据量为：" + patterns.size());
		for (int i = 0; i < patterns.size(); i++) {
			System.out.format("频繁项 %d  ", i);
			for (int j = 0; j < patterns.get(i).size(); j++) {
				System.out.print(patterns.get(i).get(j) + "	");
			}
			System.out.println();
		}
	}
}
