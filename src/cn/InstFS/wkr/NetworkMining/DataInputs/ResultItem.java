package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;

public class ResultItem 
{
	private ArrayList<Integer> pointList =new ArrayList<Integer>();
	private int cluster;
	public ArrayList<Integer> getPointList() {
		return pointList;
	}
	/**
	 * 复制内容而不是引用
	 * @param pointList
	 */
	public void setPointList(ArrayList<Integer> pointList) {
		for(int i=0;i<pointList.size();i++)
			this.pointList.add(pointList.get(i));
	}
	public int getCluster() {
		return cluster;
	}
	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
	
}
