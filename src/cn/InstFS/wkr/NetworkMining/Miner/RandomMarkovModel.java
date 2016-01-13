package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap.Iterator;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
/**
 * 
 * @author 艾长青 
 * @time 2015/11/11
 *
 */

public class RandomMarkovModel {

	private DataItems dataItems;
	private TaskElement task;
	private List<String> sequences;
	private List<String> predictSequences;
	int size = 6;
	public static void main(String[] args) {
		TaskElement task = new TaskElement();
		task.setSourcePath("./configs/real-1-39.csv");
		task.setDataSource("Text");
		task.setTaskRange(TaskRange.NodePairRange);
		//task.setFilterCondition("protocol=" + "402");  //402 --- 410都可以
		task.setGranularity(3600);
		task.setMiningObject("traffic");
		task.setDiscreteMethod(DiscreteMethod.自定义端点);
		task.setDiscreteEndNodes("1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000,18000,19000,20000,21000,22000,23000,24000,25000,26000,27000,28000,29000,30000,31000,32000,33000,34000,35000,36000,37000,38000"
				+ ",39000,40000,41000,42000,43000,44000,45000,46000,47000,48000,49000,50000,51000,52000,53000,54000,55000,56000,57000,58000,59000,60000");

		String ip[] = new String[] { "10.0.1.1" };
		IReader reader = new nodePairReader(task, ip);
		DataItems tmp = reader.readInputByText();
		DataItems dataItems = new DataItems();
//		for (int k = 0; k < tmp.getLength(); k++) {
//			DataItem dataItem = tmp.getElementAt(k);
//			dataItem.setData(String.valueOf(Double.valueOf(dataItem.getData()) / 2));
//			dataItems.add1Data(dataItem);
//		}
		
		for(int k=0;k<1000;k++){
			DataItem dataItem=tmp.getElementAt(k);
			dataItems.add1Data(dataItem);
		}

		dataItems = DataPretreatment.aggregateData(dataItems, 3600,
				AggregateMethod.Aggregate_MEAN, false);
//		dataItems = DataPretreatment.toDiscreteNumbersAccordingToWaveform(
//				dataItems, task);
		dataItems=DataPretreatment.toDiscreteNumbers(dataItems, DiscreteMethod.自定义端点, 2, task.getDiscreteEndNodes());
		RandomMarkovModel rmm = new RandomMarkovModel(dataItems,task);
		rmm.predictSequence();
		rmm.display();
		
	}
	public RandomMarkovModel(DataItems dataItems, TaskElement task) {

		this.dataItems = dataItems;
		this.task = task;
		this.sequences = new ArrayList<String>();
		this.sequences.addAll(dataItems.data);
		
//		ParamsSM psm = (ParamsSM)task.getMiningParams();
	}
	/**
	 * 获取预测序列，默认预测6个序列
	 * @return
	 */
	public List<String> predictSequence()
	{
		List<String> resultSequences = new ArrayList<String>();
		
		TreeMap<SecondOrder,HashMap<String,Integer>> treeMap = getPreTwoIterms();
		
		TreeMap<String,HashMap<String,Integer>> singleMap = getPreOneIterms();
		
		TreeMap<SecondOrder,String> mostProbNextTwo = getMostProbNextTwo(treeMap);
		TreeMap<String,String> mostProbNextOne = getMostProNextOne(singleMap);
		
		String first = sequences.get(sequences.size() - 2);
		String second = sequences.get(sequences.size()-1);
		for(int i = 0;i < size;i++)
		{
			SecondOrder so = new SecondOrder();
			so.fir_cluster = first;
			so.sec_cluster = second;
			String rr = "";
			if(mostProbNextTwo.containsKey(so)){
				
				rr = mostProbNextTwo.get(so);
			}
			else if(mostProbNextOne.containsKey(second)){
				rr = mostProbNextOne.get(second);
			}
			else
			{
				System.out.println("序列出现异常，请检查。。。。");
			}
			resultSequences.add(rr);
			first = second;
			second = rr;
		
		}
		predictSequences = resultSequences;
		return resultSequences;
	}
	/**
	 * 统计一阶马尔科夫状态
	 * @return
	 */
	private TreeMap<String, HashMap<String, Integer>> getPreOneIterms() {
		// TODO Auto-generated method stub
		TreeMap<String,HashMap<String,Integer>> singleMap = new TreeMap<String,HashMap<String,Integer>>();
		for(int i = 0;i < sequences.size()-1;i++)
		{
			String first = sequences.get(i);
			String next = sequences.get(i+1);
			
			if(singleMap.containsKey(first))
			{
				HashMap<String,Integer> mp = singleMap.get(first);
				if(mp.containsKey(next))
				{
					mp.put(next, mp.get(next)+1);
				}
				else
				{
					mp.put(next, 1);
				}
			}
			else
			{
				HashMap<String,Integer> mp = new HashMap<String,Integer>();
				mp.put(next, 1);
				singleMap.put(first, mp);
			}
		}	
		return singleMap;
	}
	/**
	 * 统计二阶马尔科夫状态
	 * @return
	 */
	private TreeMap<SecondOrder,HashMap<String,Integer>> getPreTwoIterms()
	{
		TreeMap<SecondOrder,HashMap<String,Integer>> treeMap = new TreeMap<SecondOrder,HashMap<String,Integer>>();
		
		for(int i = 0;i < sequences.size()-2;i++)
		{
			SecondOrder so = new SecondOrder();
			so.fir_cluster = sequences.get(i);
			so.sec_cluster = sequences.get(i+1);
			String next = sequences.get(i+2);
			
			if(treeMap.containsKey(so))
			{
				HashMap<String,Integer> mp = treeMap.get(so);
				if(mp.containsKey(next))
				{
					mp.put(next, mp.get(next)+1);
				}
				else
				{
					mp.put(next, 1);
				}
			}
			else
			{
				HashMap<String,Integer> mp = new HashMap<String,Integer>();
				mp.put(next, 1);
				treeMap.put(so, mp);
			}
		}	
		
		return treeMap;
	}
	/**
	 * 计算一阶马尔科夫最大概率转移状态
	 * @param singleMap
	 * @return
	 */
	private TreeMap<String, String> getMostProNextOne(
			TreeMap<String, HashMap<String, Integer>> singleMap) {
		
		TreeMap<String,String> MostProbNext = new TreeMap<String,String>();
		java.util.Iterator<Entry<String, HashMap<String, Integer>>> tree_it = singleMap.entrySet().iterator();
		while(tree_it.hasNext())
		{
			Entry entry = tree_it.next();
			String first = (String) entry.getKey();
			HashMap<String,Integer> mp = (HashMap<String, Integer>) entry.getValue();
			
//			int sum = 0;
			int single_max = 0;
			String next = "";
			java.util.Iterator<String> hash_it = mp.keySet().iterator();
			while(hash_it.hasNext())
			{
				String key = hash_it.next();
				int value = mp.get(key);
				if(value > single_max)
				{
					single_max = value;
					next = key;
//					sum += value;
				}
			}
			MostProbNext.put(first, next);
		}
		return MostProbNext;
	}
	/**
	 * 计算二阶马尔科夫的最大概率转移状态
	 * @param treeMap
	 * @return
	 */
	private TreeMap<SecondOrder, String> getMostProbNextTwo(
			TreeMap<SecondOrder, HashMap<String, Integer>> treeMap) {
		
		TreeMap<SecondOrder,String> MostProbNext = new TreeMap<SecondOrder,String>();
		java.util.Iterator<Entry<SecondOrder, HashMap<String, Integer>>> tree_it = treeMap.entrySet().iterator();
		while(tree_it.hasNext())
		{
			Entry entry = tree_it.next();
			SecondOrder so = (SecondOrder) entry.getKey();
			HashMap<String,Integer> mp = (HashMap<String, Integer>) entry.getValue();
			
//			int sum = 0;
			int single_max = 0;
			String next = "";
			java.util.Iterator<String> hash_it = mp.keySet().iterator();
			while(hash_it.hasNext())
			{
				String key = hash_it.next();
				int value = mp.get(key);
				if(value > single_max)
				{
					single_max = value;
					next = key;
//					sum += value;
				}
			}
			MostProbNext.put(so, next);
		}
		return MostProbNext;
	}
	/**
	 * 这是预测序列的长度。
	 */
	public void setSize(int s){
		size = s;
	}
	public void display(){
		
		for(int i = 0;i < predictSequences.size();i++)
		{
			System.out.print(predictSequences.get(i)+" ");
		}
		System.out.println();
	}
			
}


class SecondOrder implements Comparable<SecondOrder>{
	
	String fir_cluster = "";
	String sec_cluster = "";
	
	

	@Override  
    public boolean equals(Object obj)  
    { 
		SecondOrder so = (SecondOrder) obj;
		if((this.fir_cluster.compareTo(so.fir_cluster) == 0 && this.sec_cluster.compareTo(so.sec_cluster) == 0))
			return true;
		
		return false;
    }
	 @Override  
    public int hashCode()  
    {  
        // 正整数常量，值为素数  
        int result = 17;  
        
        result += fir_cluster.length()*31 + sec_cluster.length()*157;
       
        return result;  
    }
	@Override
	public int compareTo(SecondOrder o) {
		// TODO Auto-generated method stub
		if(fir_cluster.compareTo(o.fir_cluster) == 0)
			return sec_cluster.compareTo(o.sec_cluster);
		else
			return fir_cluster.compareTo(o.sec_cluster);
	}
}
