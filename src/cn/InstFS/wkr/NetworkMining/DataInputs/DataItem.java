package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.Date;
import java.util.Map;

public class DataItem implements Comparable<DataItem>{
	private Date time;
	private String data;
	private Double prob;
	private Map<String, Integer> NonNumData;
	
	public Map<String, Integer> getNonNumData() {
		return NonNumData;
	}
	public void setNonNumData(Map<String, Integer> nonNumData) {
		NonNumData = nonNumData;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (time != null)
			sb.append(DataInputUtils.sdf.format(time)).append("\t");
		sb.append(data);
		sb.append("\t" + prob);
		return sb.toString();
	}
	@Override
	public int compareTo(DataItem item) {
		if(!this.getTime().equals(item.getTime()))
			return this.getTime().compareTo(item.getTime());
		else
			return this.getData().compareTo(item.getData());
	}
	public Double getProb() {
		return prob;
	}
	public void setProb(Double prob) {
		this.prob = prob;
	};
}
