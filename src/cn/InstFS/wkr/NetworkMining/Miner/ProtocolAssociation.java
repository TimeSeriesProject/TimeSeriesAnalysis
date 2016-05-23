package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import test.ProtocolAssociationTest;
import lineAssociation.FileOutput;
//import test.ProtocolAssociationTest;
import associationRules.ProtocolAssociationResult;
import associationRules.ProtocolAssociationResultRate;
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
		String path = "D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\DiplomaProject\\data\\rawDataInput";
		ProtocolAssociation pa = new ProtocolAssociation(ProtocolAssociationTest.getData(path),-1,1);
		pa.miningAssociation();
		
	}
	
	double[] supportThresh = {0.4,10};
	int bias = 0;
	int whichAlogrithm = 1;
	String path = "D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\DiplomaProject\\data\\MyResult";
	String currentPath = "";
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
		setThresh(thresh,0.5);   //默认支持度阈值为0.5
	}
	/**
	 * 传递ip_protocol_dataItems的数据格式。
	 * @param data
	 * @param thresh
	 * @param flag
	 */
	public ProtocolAssociation(Map<String,HashMap<String,DataItems>> data,double thresh,int which)
	{
		ip_proData = new HashMap<String,ArrayList<ProtocolDataItems>>();
		convertData(data);
		setWhichAlogrithm(which,1);
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
					double thresh = caculateAssociation(proDataList.get(i).getDataItems(),proDataList.get(j).getDataItems(),whichAlogrithm);
					currentPath = proDataList.get(i).protocolName+"_"+proDataList.get(j).protocolName;
					System.out.format("计算 %s 下的协议 %s 和协议 %s 之间的关联度为 %f \t", 
							ip,proDataList.get(i).protocolName,proDataList.get(j).protocolName,thresh);
					if(thresh > supportThresh[whichAlogrithm-1])
						System.out.println("接受该关联");
					else
						System.out.println("拒绝该关联");
					if(thresh > supportThresh[whichAlogrithm-1])
					{
						ProtocolAssociationResultRate par = new ProtocolAssociationResultRate(proDataList.get(i).protocolName
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
	 * @param whichAlogrithm 
	 * @return
	 */
	private double caculateAssociation(DataItems dataItems,
			DataItems dataItems2, int whichAlogrithm) {
		
		double maxThresh = 0;
		
		List<Double> data1 = normalization(dataItems);
		List<Double> data2 = normalization(dataItems2);
		//自创方法
		if(whichAlogrithm == 1)
		{
			bias = 0;
			for(int k = 0;k < 10;k += 2)
			{
				double thresh = biasAssociation(data1,data2,k);
				if(thresh > maxThresh)
				{
					maxThresh = thresh;
					bias = k;
				}
			}
		}
		//ERP方法
		else if(whichAlogrithm == 2)
		{
			double[][] maxtrix = new double[data1.size()+1][data2.size()+1];
			maxThresh = ERPDistance(data1,data2,0,0,maxtrix);
		}
		return maxThresh;
	}
	/**
	 * 对数据作归一化
	 * @param dataItems
	 * @return
	 */
	private List<Double> normalization(DataItems dataItems) {
		
		List<Double> data = new ArrayList<Double>();
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
				
		for(int i = 0;i < dataItems.data.size();i++)
		{
			double d = Double.parseDouble(dataItems.data.get(i));
			data.add(d);
			if(max < d)
				max = d;
			if(min > d)
				min = d;
		}
		for(int i = 0;i < data.size();i++)
		{
			double d = (data.get(i) - min)/(max - min);
			data.set(i,d);
		}
		return data;
	}
	/**
	 * 计算两个序列的相关程度，返回值为[0,1],值越大表示置信度最高
	 * @param dataItems
	 * @param dataItems2
	 * @param k     //偏置量
	 * @return
	 */
	private double biasAssociation(List<Double> dataItems, List<Double> dataItems2,
			int k) {
		
		int i = k,j = 0;
		int num = 0;
		double sum = 0;
		double mean = 0;
		ArrayList<Double> data = new ArrayList<Double>();
		while(i < dataItems.size() && j < dataItems2.size())
		{
			num++;
			double d = dataItems.get(i)/(0.01 + dataItems2.get(j));
			
			data.add(d);
			sum += d;
			i++;
			j++;
		}
		FileOutput.writeRateData(data,path+"\\"+currentPath);
		mean = sum/num;
		double s = 0;
		for(i = 0;i < data.size();i++)
		{
			s += Math.pow(data.get(i)-mean,2.0);
		}
		s = Math.sqrt(s/(num-1));
		num = 0;
		for(i = 0;i < data.size();i++)
		{
			if(data.get(i) > mean - 0.25*s && data.get(i) < mean+0.25*s)
				num++;
		}
		return num*1.0/data.size();
	}
	/**
	 * 返回两条序列的ERP距离
	 * @param seqX 第一条序列
	 * @param seqY 第二条序列
	 * @param matrix 存储已经计算完成的数据 
	 * @param xSize ySize 充当数组指针
	 * @return ERP距离
	 */
	private double ERPDistance(List<Double> seqX,List<Double>seqY,int xSize,int ySize,double [][]matrix){
		if(xSize<0&&ySize<0){
			return 0;
		}else if(xSize<0){
			int sum=0;
			for(int i=0;i<=ySize;i++){
				sum+=Math.ceil(Math.abs(seqY.get(i)));
			}
			return 1/(sum+0.01);
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=Math.ceil(Math.abs(seqX.get(i)));
			}
			return 1/(sum+0.01);
		}
		
		if(matrix[xSize][ySize] > 0){
			return 1/(matrix[xSize][ySize]+0.01);
		}else{
			double xItem=seqX.get(xSize);
			double yItem=seqY.get(ySize);
			double dis1=ERPDistance(seqX,seqY,xSize-1,ySize-1,matrix)+Math.abs(xItem-yItem);
			double dis2=ERPDistance(seqX, seqY,xSize-1,ySize,matrix)+Math.abs(xItem);
			double dis3=ERPDistance(seqX, seqY,xSize,ySize-1,matrix)+Math.abs(yItem);
			double min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[xSize][ySize]=min;
			return 1/(min+0.01);
		}
	}
	/**
	 * 设置支持度阈值，如果传入的参数小于0，则使用默认阈值，默认值为0.5
	 * @param thresh
	 * @param defaultValue
	 */
	private void setThresh(double thresh, double defaultValue) {
		
		if(thresh < 0)
			supportThresh[whichAlogrithm-1] = defaultValue;
		else
			supportThresh[whichAlogrithm-1] = thresh;
		
	}
	/**
	 * 设置使用哪一种算法，取值为1,2。
	 * @param which
	 * @param i
	 */
	private void setWhichAlogrithm(int which, int defaultValue) {
		// TODO Auto-generated method stub
		if(which != 1 && which != 2)
		{
			whichAlogrithm = defaultValue;
		}
		else
			whichAlogrithm = which;
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
		int noProtocolNum = 0;
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
			if(list.size() == 0)  //当前ip没有协议，则不加入到数据集中
			{
				noProtocolNum++;
				continue;
			}
			
//			System.out.println("ip "+ ip+"  "+list.size());
			ip_proData.put(ip, list);
		}
		System.out.println("过滤掉的ip有："+noProtocolNum);
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
