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

public class SequencePattern {

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
		task.setFilterCondition("protocol=" + "402");
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
		SequencePattern sp = new SequencePattern(dataItems, task,
				patternsResult);
		sp.patternMining();
		sp.displayResult();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Date date = new Date();
		System.out.println(date);
	}

	public SequencePattern(DataItems dataItems, TaskElement task,
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
		ArrayList<ArrayList<String>> sliceSequence = getSliceSequence(sample_num); // 将一个样例以字符串形式给出

		int length = sliceSequence.get(0).size();
		ArrayList<ArrayList<String>> sequenceProb = convertHashSetToArray(clusterLabel); // 初始化长度串

		System.out.println("sequenceProb:" + sequenceProb.size());

		patterns = getFrequentItemSet(sliceSequence, sequenceProb, 0.25);
	}

	private ArrayList<ArrayList<String>> getFrequentItemSet(
			ArrayList<ArrayList<String>> sliceSequence,
			ArrayList<ArrayList<String>> sequenceProb, double thresh) {

		ArrayList<ArrayList<String>> FrequentItemSet = new ArrayList<ArrayList<String>>();

		HashMap<String, ArrayList<ArrayList<Integer>>> position = getItemSamplePosition(
				sliceSequence, sequenceProb);
		// System.out.println("position:"+position);
		ArrayList<ArrayList<String>> sequenceResult = new ArrayList<ArrayList<String>>();

		while (true) {
			
			sequenceProb = removeNoItems(sequenceProb, sliceSequence, position,
					thresh);
//			System.out.println(sequenceProb.size());
			if(sequenceProb != null && sequenceProb.size() > 0)
				uniqueItem(sequenceProb,sequenceResult);
			System.out.println("删除后：" + sequenceProb.size());
			if (sequenceProb.size() == 0)
				break;
			else {
				sequenceResult.addAll(sequenceProb);
				noRepeaedItem(sequenceResult);
			}
			sequenceProb = getNewFrequentItems(sequenceResult);

		}
		return sequenceResult;
	}

	private void uniqueItem(ArrayList<ArrayList<String>> sequenceProb,
			ArrayList<ArrayList<String>> sequenceResult) {
		
		for(int i = 0;i < sequenceProb.size();i++)
		{
			for(int  j = 0;j < sequenceResult.size();j++)
			{
				int a = sequenceProb.get(i).size();
				a = sequenceResult.get(j).size();
				if(sequenceProb.get(i).size() == sequenceResult.get(j).size())
				{
					boolean isSame = true;
					for(int k = 0;k < sequenceProb.get(i).size();k++)
					{
						if(sequenceProb.get(i).get(k).compareTo(sequenceResult.get(j).get(k)) != 0)
						{
							isSame = false;
							break;
						}
					}
					if(isSame)
					{
						sequenceProb.remove(i);
						i--;
						break;
					}
					
				}
			}
		}
		
	}

	private void noRepeaedItem(ArrayList<ArrayList<String>> sequenceResult) {

		// 去重相同的频繁项
		for (int i = 0; i < sequenceResult.size(); i++) {
			for (int j = i + 1; j < sequenceResult.size(); j++) {
				if (sequenceResult.get(i).size() == sequenceResult.get(j)
						.size()) {
					boolean isSame = true;
					for (int k = 0; k < sequenceResult.get(i).size(); k++) {
						if (sequenceResult.get(i).get(k)
								.compareTo(sequenceResult.get(j).get(k)) == 0)
							continue;
						else {
							isSame = false;
							break;
						}
					}
					if (isSame) {
						sequenceResult.remove(j);
						j--;
					}

				}
			}
		}
	}

	private ArrayList<ArrayList<String>> getNewFrequentItems(
			ArrayList<ArrayList<String>> sequenceProb) {
		System.out.println("getNewFrequentItems .....   sequenceProb:"
				+ sequenceProb.size());
		ArrayList<ArrayList<String>> tmp_sequence = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < sequenceProb.size(); i++) {
			for (int j = 0; j < sequenceProb.size(); j++) {
				// 顺序
				ArrayList<String> tmp_ = new ArrayList<String>();
				for (int k = 0; k < sequenceProb.get(i).size(); k++) {
					String t = sequenceProb.get(i).get(k);
					tmp_.add(t);
				}
				for (int k = 0; k < sequenceProb.get(j).size(); k++) {
					String t = sequenceProb.get(j).get(k);
					tmp_.add(t);
				}
				// 逆序
				ArrayList<String> tmp_inverse = new ArrayList<String>();
				for (int k = 0; k < sequenceProb.get(j).size(); k++) {
					String t = sequenceProb.get(j).get(k);
					tmp_inverse.add(t);
				}
				for (int k = 0; k < sequenceProb.get(i).size(); k++) {
					String t = sequenceProb.get(i).get(k);
					tmp_inverse.add(t);
				}
				if (tmp_.size() <= winSize) {

					tmp_sequence.add(tmp_);
				}
				if (tmp_inverse.size() <= winSize)
					tmp_sequence.add(tmp_inverse);
			}
		}
		// 去重相同的频繁项
		for (int i = 0; i < tmp_sequence.size(); i++) {
			for (int j = i + 1; j < tmp_sequence.size(); j++) {
				if (tmp_sequence.get(i).size() == tmp_sequence.get(j).size()) {
					boolean isSame = true;
					for (int k = 0; k < tmp_sequence.get(i).size(); k++) {
						if (tmp_sequence.get(i).get(k)
								.compareTo(tmp_sequence.get(j).get(k)) == 0)
							continue;
						else {
							isSame = false;
							break;
						}
					}
					if (isSame) {
						tmp_sequence.remove(j);
						j--;
					}

				}
			}
		}
		return tmp_sequence;
	}

	private ArrayList<ArrayList<String>> removeNoItems(
			ArrayList<ArrayList<String>> sequenceProb,
			ArrayList<ArrayList<String>> sliceSequence,
			HashMap<String, ArrayList<ArrayList<Integer>>> position,
			double thresh) {
		for (int i = 0; i < sequenceProb.size(); i++) {
			ArrayList<String> item = sequenceProb.get(i);
			if (isExceedThresh(item, sliceSequence, position, thresh))
				continue;
			else {
				sequenceProb.remove(i); // 删除第i个不满足条件的伪频繁项
				i--;
			}
		}
		return sequenceProb;

	}

	private boolean isExceedThresh(ArrayList<String> item,
			ArrayList<ArrayList<String>> sliceSequence,
			HashMap<String, ArrayList<ArrayList<Integer>>> position,
			double thresh) {

		int count = 0;
		String label = item.get(0);
		if (position.containsKey(label)) {
			ArrayList<ArrayList<Integer>> sample_p = position.get(label);
			for (int i = 0; i < sample_p.size(); i++) {
				for (int j = 0; j < sample_p.get(i).size(); j++) {
					int p = sample_p.get(i).get(j);
					if (isContain(p, item, sliceSequence.get(i))) {

						count++;
						break;
					}
				}
			}
			if (count * 1.0 / sample_p.size() > thresh) {
				System.out.println("满足条件频繁项：" + item.toString());
				System.out.format("count:%d  sample:%d  value:%f\n", count,
						sample_p.size(), count * 1.0 / sample_p.size());
				return true;
			}
		}

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
			ArrayList<ArrayList<String>> sliceSequence,
			ArrayList<ArrayList<String>> sequenceProb) {

		int length = sliceSequence.get(0).size();

		HashMap<String, ArrayList<ArrayList<Integer>>> position = new HashMap<String, ArrayList<ArrayList<Integer>>>();
		for (int i = 0; i < sequenceProb.size(); i++) // 遍历频繁项
		{
			ArrayList<String> item = sequenceProb.get(i);
			// System.out.println(item.get(0));
			ArrayList<ArrayList<Integer>> labelPosition = new ArrayList<ArrayList<Integer>>();

			for (int j = 0; j < sliceSequence.size(); j++) // 遍历样本
			{
				ArrayList<Integer> CP = new ArrayList<Integer>();

				for (int k = 0; k < sliceSequence.get(j).size(); k++) // 遍历每个样本的序列
				{
					if (item.get(0).compareTo(sliceSequence.get(j).get(k)) == 0) {
						CP.add(k);

					}
				}
				labelPosition.add(CP);
			}

			position.put(item.get(0), labelPosition);

		}
		return position;
	}

	private ArrayList<ArrayList<String>> convertHashSetToArray(
			HashSet<String> clusterLabel) {

		ArrayList<ArrayList<String>> sequenceProb = new ArrayList<ArrayList<String>>();
		java.util.Iterator<String> it = clusterLabel.iterator();
		while (it.hasNext()) {
			ArrayList<String> item = new ArrayList<String>();
			String str = it.next();
			item.add(str);
			sequenceProb.add(item);
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

	private ArrayList<ArrayList<String>> getSliceSequence(int sample_num) {

		ArrayList<ArrayList<String>> sliceSequence = new ArrayList<ArrayList<String>>();

		// System.out.println("dataItemsP:"+dataItems.getLength());
		for (int i = 0; i < dataItems.getLength(); i++) {
			Date date = dataItems.time.get(i);
			String data = dataItems.data.get(i);

			Long time = sparseTime(date);
			int index = (int) (i / winSize);
			if (index >= sliceSequence.size()) {
				ArrayList<String> sequence = new ArrayList<String>();
				sequence.add(dataItems.data.get(i));
				sliceSequence.add(sequence);
			} else {
				sliceSequence.get(index).add(dataItems.data.get(i));
			}
		}
		// for(int i = 0 ;i < sliceSequence.size();i++)
		// {
		// // System.out.println();
		// for(int j = 0;j < sliceSequence.get(i).size();j++)
		// {
		// System.out.print(sliceSequence.get(i).get(j)+",");
		// }
		// System.out.println();
		// }
		return sliceSequence;
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
