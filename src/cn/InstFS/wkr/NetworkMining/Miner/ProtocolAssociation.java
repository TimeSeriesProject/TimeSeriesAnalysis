package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * 
 * @author 艾长青
 * @version 初始版本
 *
 */
public class ProtocolAssociation {

	/**
	 * 该主函数主要是为了测试使用，正式使用时，可以删掉该主函数
	 * @param args
	 */
	public static void main(String[] args)
	{
		ProtocolAssMinerFactory paf = ProtocolAssMinerFactory.getInstance();
		paf.mineAllAssociations();
		ProtocolAssociation pa = new ProtocolAssociation(paf.eachProtocolItems,0.6,true);
		pa.miningAssociation();
		
	}
	double supportThresh = 0;
	int bias = 0;
	HashMap<String,ArrayList<ProtocolDataItems>> ip_proData ;
	/**
	 * 直接传入已经处理好的结果
	 * @param pdi
	 * @param thresh
	 */
	public ProtocolAssociation(HashMap<String,ArrayList<ProtocolDataItems>> pdi,double thresh)
	{
		ip_proData = pdi;
		ip_proData = new HashMap<String,ArrayList<ProtocolDataItems>>();
		setThresh(thresh,0.5);
	}
	/**
	 * 传递ip_protocol_dataItems的数据格式。
	 * @param data
	 * @param thresh
	 * @param flag
	 */
	public ProtocolAssociation(Map<String,HashMap<String,DataItems>> data,double thresh,boolean flag)
	{
		ip_proData = new HashMap<String,ArrayList<ProtocolDataItems>>();
		convertData(data);
		setThresh(-1,0.5);
		
	}
	
	/**
	 * 挖掘ip下协议之间的关联
	 */
	public Map<String,List<ProtocolAssociationResult>> miningAssociation()
	{
		if(ip_proData == null)
		{
			System.out.println("待挖掘数据为空，请先载入数据！");
			System.exit(0);
		}
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
	/**
	 * 计算两协议的关联性
	 * @param dataItems
	 * @param dataItems2
	 * @return
	 */
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
			double d = Double.parseDouble(dataItems.data.get(i))/(0.1 + Double.parseDouble(dataItems.data.get(j)));
			data.add(d);
			sum += d;
			i++;
			j++;
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
	/**
	 * 设置支持度阈值，如果传入的参数小于0，则使用默认阈值，默认值为0.5
	 * @param thresh
	 * @param defaultValue
	 */
	private void setThresh(double thresh, double defaultValue) {
		
		if(thresh < 0)
			supportThresh = defaultValue;
		else
			supportThresh = thresh;
		
	}
	/**
	 * 将map<ip,map<protocol,DataItems>>数据格式 转化为map<ip,List<class>>数据格式，方便处理
	 * @param data
	 */
	public void convertData(Map<String,HashMap<String,DataItems>> data)
	{
		if(ip_proData == null)
		{
			System.out.println("变量申请失败");
		}
		if(data == null)
		{
			System.out.println("数据为空，请检查输入");
		}
		Iterator<String> ip_iter = data.keySet().iterator();
		while(ip_iter.hasNext())
		{
			String ip = ip_iter.next();
			Map<String,DataItems> pro_map = data.get(ip);
			
			ArrayList<ProtocolDataItems> list = new ArrayList<ProtocolDataItems>();
			Iterator<String> pro_iter = pro_map.keySet().iterator();
			while(pro_iter.hasNext())
			{
				String protocol = pro_iter.next();
				ProtocolDataItems pdi = new ProtocolDataItems(protocol,pro_map.get(protocol));
				list.add(pdi);
			}
			if(list.size() == 0)
			{
				continue;
			}
			System.out.println("ip "+ ip+"  "+list.size());
			ip_proData.put(ip, list);
		}
	}
}

/**
 * 存储协议名和数据
 * @author Administrator
 *
 */
class ProtocolDataItems
{
	String protocolName = "";
	DataItems dataItems;
	public ProtocolDataItems(String name,DataItems data)
	{
		protocolName = name;
		dataItems = data;
	}
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
