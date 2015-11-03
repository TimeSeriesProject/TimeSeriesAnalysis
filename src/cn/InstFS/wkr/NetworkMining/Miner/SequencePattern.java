package cn.InstFS.wkr.NetworkMining.Miner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class SequencePattern {

	private DataItems dataItems;
	private TaskElement task;
	private List<ArrayList<String>> patterns;
	private long winSize = 0; //单位没秒
	private int clusterNum = 0;
	public static void main(String[] args)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
		
		Date date1 = new Date("2015-10-19");
//		sleep(10000);
		Date date2 = new Date("2015-10-20");
		System.out.println(date2.getTime() - date1.getTime());
	}
	public SequencePattern(DataItems dataItems,TaskElement task,List<ArrayList<String>> patterns){
		
		this.dataItems = dataItems;
		this.task = task;
		this.patterns = patterns;
	}
	public void patternMining()
	{
		HashSet<String> clusterLabel  = getClusterNum();   //得到序列聚类的个数
		clusterNum = clusterLabel.size();
//		long diff = d1.getTime() - d2.getTime();
		Date max_date = getMaxDate();
		Date min_date = getMinDate();
		long distance = (max_date.getTime() - min_date.getTime())/1000;  //相减结果是毫秒
		int sample_num = (int)Math.ceil(distance*1.0/winSize);
		
		ArrayList<ArrayList<String>> sliceSequence = getSliceSequence(sample_num);   //将一个样例以字符串形式给出

		int length = sliceSequence.get(0).size();
		ArrayList<ArrayList<String>> sequenceProb = convertHashSetToArray(clusterLabel);  //初始化长度串
		patterns = getFrequentItemSet(sliceSequence,sequenceProb,0.25);
		
		
	}
	private ArrayList<ArrayList<String>> getFrequentItemSet(ArrayList<ArrayList<String>> sliceSequence,
			ArrayList<ArrayList<String>> sequenceProb,double thresh) {
		
		ArrayList<ArrayList<String>> FrequentItemSet = new ArrayList<ArrayList<String>>();
		
		HashMap<String,ArrayList<ArrayList<Integer>>> position = getItemSamplePosition(sliceSequence,sequenceProb);
		ArrayList<ArrayList<String>> sequenceResult = new ArrayList<ArrayList<String>>();
		
		while(true)
		{
			sequenceProb = removeNoItems(sequenceProb,sliceSequence,position,thresh);
			if(sequenceProb.size() == 0)
				break;
			else
				sequenceResult.addAll(sequenceProb);
			ArrayList<ArrayList<String>> tmp_sequence = getNewFrequentItems(sequenceResult);
			
		}
		return sequenceResult;
	}
	private ArrayList<ArrayList<String>> getNewFrequentItems(
			ArrayList<ArrayList<String>> sequenceProb) {
		
		ArrayList<ArrayList<String>> tmp_sequence =  new ArrayList<ArrayList<String>>();
		for(int i = 0;i < sequenceProb.size();i++)
		{
			for(int j = 0;j < sequenceProb.size();j++)
			{
				//顺序
				ArrayList<String> tmp_ = new ArrayList<String>();
				for(int k = 0;k < sequenceProb.get(i).size();k++)
				{
					String t = sequenceProb.get(i).get(k);
					tmp_.add(t);
				}
				for(int k = 0;k < sequenceProb.get(j).size();k++)
				{
					String t = sequenceProb.get(j).get(k);
					tmp_.add(t);
				}
				//逆序
				ArrayList<String> tmp_inverse = new ArrayList<String>();
				for(int k = 0;k < sequenceProb.get(j).size();k++)
				{
					String t = sequenceProb.get(j).get(k);
					tmp_.add(t);
				}
				for(int k = 0;k < sequenceProb.get(i).size();k++)
				{
					String t = sequenceProb.get(i).get(k);
					tmp_.add(t);
				}
				tmp_sequence.add(tmp_);
				tmp_sequence.add(tmp_inverse);
			}
		}
		return tmp_sequence;
	}
	private ArrayList<ArrayList<String>> removeNoItems(ArrayList<ArrayList<String>> sequenceProb,
			ArrayList<ArrayList<String>> sliceSequence,
			HashMap<String, ArrayList<ArrayList<Integer>>> position,
			double thresh) {
		for(int i = 0;i < sequenceProb.size();i++)
		{
			ArrayList<String> item = sequenceProb.get(i);
			if(isExceedThresh(item,sliceSequence,position,thresh))
				continue;
			else
			{
				sequenceProb.remove(i);  //删除第i个不满足条件的伪频繁项
				i--;
			}
		}
		return sequenceProb;
		
	}
	private boolean isExceedThresh(ArrayList<String> item,
			ArrayList<ArrayList<String>> sliceSequence, HashMap<String, ArrayList<ArrayList<Integer>>> position,double thresh) {
		
		int count = 0;
		if(position.containsKey(item))
		{
			ArrayList<ArrayList<Integer>> sample_p = position.get(item);
			for(int i = 0;i < sample_p.size();i++)
			{
				for(int j = 0;j < sample_p.get(i).size();j++)
				{
					int p = sample_p.get(i).get(j);
					if(isContain(p,item,sliceSequence.get(i))){
						
						count++;
						break;
					}
				}
			}
			if(count*1.0/sample_p.size() > thresh)
				return true;
		}
		
		return false;
	}
	private boolean isContain(int p, ArrayList<String> item,
			ArrayList<String> arrayList) {
		
		int i = 0,j = 0;
		for(i = 0,j = p;i < item.size() && j < arrayList.size();i++,j++)
		{
			if(item.get(i).compareTo(arrayList.get(j)) == 0)
				continue;
			else
				break;
		}
		if(i == item.size())
			return true;
		return false;
	}
	private HashMap<String, ArrayList<ArrayList<Integer>>> getItemSamplePosition(
			ArrayList<ArrayList<String>> sliceSequence,
			ArrayList<ArrayList<String>> sequenceProb) {
		
		int length = sliceSequence.get(0).size();
		
		HashMap<String,ArrayList<ArrayList<Integer>>> position = new HashMap<String,ArrayList<ArrayList<Integer>>>();
		for(int i = 0;i < sequenceProb.size();i++)  // 遍历频繁项
		{
			ArrayList<String> item = sequenceProb.get(i);
			ArrayList<ArrayList<Integer>> labelPosition = new ArrayList<ArrayList<Integer>>();
			
			for(int j = 0;j < sliceSequence.size();j++)   // 遍历样本
			{
				ArrayList<Integer> CP = new ArrayList<Integer>();
				
				for(int k = 0;k < sliceSequence.get(j).size();k++)    // 遍历每个样本的序列
				{
					if(item.get(0).compareTo(sliceSequence.get(j).get(k)) == 0)
					{
						CP.add(k);
						
					}
					
					
				}
				labelPosition.add(CP);
			}
			
			position.put(item.get(0), labelPosition);
			
			
			
		}
		return position;
	}
	private ArrayList<ArrayList<String>> convertHashSetToArray(HashSet<String> clusterLabel) {
		
		ArrayList<ArrayList<String>> sequenceProb = new ArrayList<ArrayList<String>>();
		java.util.Iterator<String> it = clusterLabel.iterator();
		while(it.hasNext())
		{
			ArrayList<String> item = new ArrayList<String>();
			String str = it.next();
			item.add(str);
			sequenceProb.add(item);
		}
		return sequenceProb;
	}
	private Date getMinDate() {
		
		Date min_date = dataItems.time.get(0);
		
		for(int i = 1;i < dataItems.getLength();i++)
		{
			Date date = dataItems.time.get(i);
			if(min_date.after(date))
				min_date = date;
		}
		return min_date;
	}
	private Date getMaxDate() {
		
		Date max_date = dataItems.time.get(0);
		
		for(int i = 1;i < dataItems.getLength();i++)
		{
			Date date = dataItems.time.get(i);
			if(max_date.before(date))
				max_date = date;
		}
		return max_date;
	}
	
	private HashSet<String> getClusterNum() {
		
		HashSet<String> set = new HashSet<String>();
		for(int i = 0;i < dataItems.getLength();i++)
		{
			Date date = dataItems.time.get(i);
			Long time = sparseTime(date);
			String data = dataItems.data.get(i);
			set.add(data);
		}
		return set;
	}
	private ArrayList<ArrayList<String>> getSliceSequence(int sample_num) {
		
		ArrayList<ArrayList<String>> sliceSequence = new ArrayList<ArrayList<String>>();
		
		for(int i = 0;i < dataItems.getLength();i++)
		{
			Date date = dataItems.time.get(i);
			String data = dataItems.data.get(i);
			
			Long time = sparseTime(date);
			int index = (int) (time/winSize);
			if(index >= sliceSequence.size())
			{
				ArrayList<String> sequence = new ArrayList<String>();
				sequence.add(dataItems.data.get(i));
				sliceSequence.add(sequence);
			}
			else
			{
				sliceSequence.get(index).add(dataItems.data.get(i));
			}
		}
		return sliceSequence;
	}
	private Long sparseTime(Date date) {
		
		return null;
	}
}
