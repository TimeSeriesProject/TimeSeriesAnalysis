package lineAssociation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import associationRules.ProtocolAssociationResult;
import associationRules.ProtocolDataItems;
import associationRules.ProtocolLineData;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class LineProtocolAssociation {

	/**
	 * 数据结构定义
	 */
	HashMap<String,ArrayList<ProtocolDataItems>> ip_proData ;
	Map<String,List<ProtocolLineData>> midData ;
	public static void main(String[] args)
	{
		LineProtocolAssociation m = new LineProtocolAssociation();
		m.mingProtocolAssocition();
	}

	
	public LineProtocolAssociation()
	{
		
	}
	public LineProtocolAssociation(Map<String,HashMap<String,DataItems>> data)
	{
		ip_proData = new HashMap<String,ArrayList<ProtocolDataItems>>();
		midData = new HashMap<String,List<ProtocolLineData>>();
		convertData(data);
		data.clear();
	}
	private Map<String, List<ProtocolAssociationResult>> mingProtocolAssocition() {
		
		if(ip_proData == null)
		{
			System.out.println("待挖掘数据为空，请先载入数据！");
			System.exit(0);
		}
		Map<String,List<ProtocolAssociationResult>> resultMap = new TreeMap<String,List<ProtocolAssociationResult>>();
		
		segmentCluster();
		
		Iterator<String> ip_iter = midData.keySet().iterator();
		
		while(ip_iter.hasNext())
		{
			String ip = ip_iter.next();
			List<ProtocolLineData> proDataList = midData.get(ip);
			
//			singleIpAssociation();
			List<ProtocolAssociationResult> resultList = new ArrayList<ProtocolAssociationResult>();
			for(int i = 0;i < proDataList.size();i++)
			{
				for(int j = i+1;j < proDataList.size();j++)
				{
					
				}	
				
			}
			if(resultList.size() != 0)
				resultMap.put(ip, resultList);
			
		}
		return resultMap;
	}
	private void segmentCluster(){
		
		
		/**
		 * 将每个ip的协议序列线段化并聚类
		 */
		Iterator<String> ip_iter = ip_proData.keySet().iterator();
		while(ip_iter.hasNext())
		{
			String ip = ip_iter.next();
			List<ProtocolDataItems> proDataList = ip_proData.get(ip);
			List<ProtocolLineData> midList = new ArrayList<ProtocolLineData>();;
			for(int i = 0;i < proDataList.size();i++)
			{
				
				TreeMap<Integer,Double> sourceDatas = dataItemsConvertMap(proDataList.get(i));
				System.out.println("开始运行自底向上线段拟合算法！");
		        BottomUpLinear bottomUpLinear = new BottomUpLinear(sourceDatas);
		        bottomUpLinear.run();
		        TreeMap<Integer, Linear> linears = bottomUpLinear.getLinears();
		        Linear lastPoint = new Linear(0.0,sourceDatas.lastKey(),0,sourceDatas.get(sourceDatas.lastKey()));
		        System.out.println("自底向上线段拟合算法计算完毕！");
		        System.out.println("***************************************************");


		        System.out.println("开始运行DPCluster聚类算法！");
		        ClusterWrapper clusterWrapper = new ClusterWrapper(linears);
		        DPCluster dpCluster = clusterWrapper.run();
		        System.out.println("DPCluster聚类算法计算完毕！");
		        System.out.println("***************************************************");
		        Map<Integer,Integer> map = dpCluster.getBelongClusterCenter();
		        ProtocolLineData pld = new ProtocolLineData(proDataList.get(i).getProtocolName(),map);
		        midList.add(pld);
		        
			}
			midData.put(ip,midList);
		}
		ip_proData.clear();
	}
	private TreeMap<Integer, Double> dataItemsConvertMap(
			ProtocolDataItems protocolDataItems) {
		
		TreeMap<Integer,Double> sourceDatas = new TreeMap<Integer,Double>();
		
		 for(int i=0; i<protocolDataItems.getDataItems().getData().size(); i++){
	           
	          int key = i;
	          double value = Double.parseDouble(protocolDataItems.getDataItems().getData().get(i));
	          sourceDatas.put(key,value);
	     }
		return sourceDatas;
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
