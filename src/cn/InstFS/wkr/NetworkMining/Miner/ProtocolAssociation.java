package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * 
 * @author 艾长青
 * @version 初始版本
 *
 */
public class ProtocolAssociation {

	public static void main(String[] args)
	{
		
	}
	double supportThresh = 0;
	int bias = 0;
	Map<String,List<ProtocolDataItems>> ip_proData ;
	public ProtocolAssociation(Map<String,List<ProtocolDataItems>> pdi,double thresh)
	{
		ip_proData = pdi;
		setThresh(thresh,0.5);
	}
	/**
	 * 设置支持度阈值，如果传入的参数小于0，则使用默认阈值，默认值为0.5
	 * @param thresh
	 * @param defaultValue
	 */
	public void setThresh(double thresh, double defaultValue) {
		
		if(thresh < 0)
			supportThresh = defaultValue;
		else
			supportThresh = thresh;
		
	}
	/**
	 * 挖掘ip下协议之间的关联
	 */
	public Map<String,List<ProtocolAssociationResult>> miningAssociation()
	{
		
		Map<String,List<ProtocolAssociationResult>> resultMap = new TreeMap<String,List<ProtocolAssociationResult>>();
		Iterator<String> ip_iter = ip_proData.keySet().iterator();
		while(ip_iter.hasNext())
		{
			String ip = ip_iter.next();
			List<ProtocolDataItems> proDataList = ip_proData.get(ip);
			List<ProtocolAssociationResult> resultList = new ArrayList<ProtocolAssociationResult>();
			for(int i = 0;i < proDataList.size();i++)
			{
				for(int j = i+1;j < proDataList.size();j++)
				{
					double thresh = caculateAssociation(proDataList.get(i).getDataItems(),proDataList.get(j).getDataItems());
					if(thresh > supportThresh)
					{
						ProtocolAssociationResult par = new ProtocolAssociationResult(proDataList.get(i).protocolName
								,proDataList.get(j).protocolName,proDataList.get(i).getDataItems(),
								proDataList.get(j).getDataItems(),thresh,bias);
						resultList.add(par);
						
					}
				}	
				
			}
			if(resultList.size() != 0)
				resultMap.put(ip, resultList);
			
		}
		return resultMap;
	}
	private double caculateAssociation(DataItems dataItems,
			DataItems dataItems2) {
		
		bias = 0;
		double maxThresh = 0;
		for(int k = 0;k < 10;k += 2)
		{
			double thresh = biasAssociation(dataItems,dataItems2,k);
			if(thresh > maxThresh)
			{
				maxThresh = thresh;
				bias = k;
			}
		}
		return maxThresh;
	}
	/**
	 * 计算两个序列的相关程度，返回值为[0,1],值越大表示置信度最高
	 * @param dataItems
	 * @param dataItems2
	 * @param k     //偏置量
	 * @return
	 */
	private double biasAssociation(DataItems dataItems, DataItems dataItems2,
			int k) {
		
		int i = k,j = 0;
		int num = 0;
		double sum = 0;
		double mean = 0;
		ArrayList<Double> data = new ArrayList<Double>();
		while(i < dataItems.data.size() && j < dataItems2.data.size())
		{
			num++;
			i++;
			j++;
			double d = Double.parseDouble(dataItems.data.get(i))/(0.1 + Double.parseDouble(dataItems.data.get(j)));
			data.add(d);
			sum += d;
			
		}
		mean = sum/num;
		double s = 0;
		for(i = 0;i < data.size();i++)
		{
			s += Math.pow(data.get(i)-mean,2.0);
		}
		s += Math.sqrt(s/(num-1));
		num = 0;
		for(i = 0;i < data.size();i++)
		{
			if(data.get(i) > mean - 2*s && data.get(i) < mean+2*s)
				num++;
		}
		
		return num*1.0/data.size();
	}
}
class ProtocolAssociationResult{
	
	double support = 0.0;
	int bias = 0;
	String protocol1 = "";
	String protocol2 = "";
	DataItems dataItems1;
	DataItems dataItems2;
	public ProtocolAssociationResult(String p1,String p2,DataItems data1,DataItems data2,double s,int k)
	{
		protocol1 = p1;
		protocol2 = p2;
		dataItems1 = data1;
		dataItems2 = data2;
		support = s;
		bias = k;
	}
	
}
class ProtocolDataItems
{
	String protocolName = "";
	DataItems dataItems;
	public void setProtocolName(String name)
	{
		protocolName = name;
	}
	public void setDataItems(DataItems data)
	{
		dataItems = data;
	}
	public String getProtocolName(){
		return protocolName;
		
	}
	public DataItems getDataItems(){
		return dataItems;
	}
}
