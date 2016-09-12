package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author LYH
 * @decription 节点的出现与消失的线段化
 * */
public class NodeAMent {
	DataItems dataItems = new DataItems();
	private TreeMap<Integer,Double> datas = new TreeMap<Integer, Double>();//原始数据
	List<NodeAPatter> patters = new ArrayList<NodeAPatter>();
	private int label;//0,1
	public NodeAMent(DataItems di,int label){
		this.dataItems = di;
		this.label = label;
		run();
	}
	public void run(){
		//转换数据格式
		datas = dataItemsConvertMap(dataItems);		
		//生成线段模式
		gainPatters();
	}
	/**
	 * 转换dataItems中data的数据格式为：
	 * Map(i,data(i))*/
	private TreeMap<Integer, Double> dataItemsConvertMap(DataItems di) {
		
		TreeMap<Integer,Double> sourceDatas = new TreeMap<Integer,Double>();
		
		 for(int i=0; i<di.getData().size(); i++){
	           
	          int key = i;
	          double value = Double.parseDouble(di.getData().get(i));
	          sourceDatas.put(key,value);
	     }
		return sourceDatas;
	}
	/**
	 * @title gainPatters
	 * @decription 生成线段模式
	 * @return List<NodeAPatter>*/
	public void gainPatters(){		
		int start = 0;
		int end = 0;
		while(end<datas.size()){	
			int len = 0;
			if(datas.get(end)==label){				
				start = end;
				end++;
				len++;
			}else{
				end++;
				continue;
			}
			while(end<datas.size() && datas.get(end)==label){
				len++;
				end++;
			}
			NodeAPatter nodePatter = new NodeAPatter(len,start,end-1,label);
			patters.add(nodePatter);			
		}		
	}
	public DataItems getDataItems() {
		return dataItems;
	}
	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
	}
	public TreeMap<Integer, Double> getDatas() {
		return datas;
	}
	public void setDatas(TreeMap<Integer, Double> datas) {
		this.datas = datas;
	}
	public List<NodeAPatter> getPatters() {
		return patters;
	}
	public void setPatters(List<NodeAPatter> patters) {
		this.patters = patters;
	}
	public int getLabel() {
		return label;
	}
	public void setLabel(int label) {
		this.label = label;
	}
		
}
