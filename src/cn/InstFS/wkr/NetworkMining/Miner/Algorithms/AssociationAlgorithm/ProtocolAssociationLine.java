package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AssociationAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.ibatis.jdbc.Null;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
//import test.ProtocolAssociationTest;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsFP_Line;
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;
import lineAssociation.BottomUpLinear;
import lineAssociation.ClusterWrapper;
import lineAssociation.DPCluster;
import lineAssociation.FindRules;
import lineAssociation.Linear;
import lineAssociation.Rule;
import lineAssociation.SymbolNode;
import associationRules.LinePos;
import associationRules.ProtoclPair;

public class ProtocolAssociationLine {

	AssociationRuleLineParams arp = null;
	/**
	public static void main(String[] args)
	{
		String path = "D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\DiplomaProject\\data\\rawDataInput";
		ProtocolAssociationLine pal = new ProtocolAssociationLine(ProtocolAssociationTest.getData(path));
		pal.miningAssociation();
	}
	*/
	HashMap<String,ArrayList<ProtocolDataItems>> ip_proData ;
	List<TreeMap<Integer,Linear>> linesPosList = new ArrayList<TreeMap<Integer,Linear>>();
	public ProtocolAssociationLine(HashMap<String,ArrayList<ProtocolDataItems>> pdi,AssociationRuleLineParams arp){
		
		ip_proData = new HashMap<String,ArrayList<ProtocolDataItems>>();
		this.arp = arp;
	}
	/**
	 * 传递ip_protocol_dataItems的数据格式。
	 * @param data
	 * @param thresh
	 * @param flag
	 */
	public ProtocolAssociationLine(Map<String,HashMap<String,DataItems>> data,AssociationRuleLineParams arp)
	{
		ip_proData = new HashMap<String,ArrayList<ProtocolDataItems>>();
		this.arp = arp;
		convertData(data);
		
	}
	/**
	 * 挖掘ip下协议之间的关联
	 */
	public MinerResultsFP_Line miningAssociation()
	{
		if(ip_proData == null)
		{
			System.out.println("待挖掘数据为空，请先载入数据！");
			System.exit(0);
		}
		if(ip_proData.size() != 1)
		{
			System.out.println("该方法只处理一个ip下的协议的关联性");
		}
		MinerResultsFP_Line mr_fp_l = new MinerResultsFP_Line();
//		Map<String,List<ProtocolAssociationResult>> resultMap = new TreeMap<String,List<ProtocolAssociationResult>>();
		Iterator<String> ip_iter = ip_proData.keySet().iterator();
		while(ip_iter.hasNext())
		{
			String ip = ip_iter.next();
			mr_fp_l.setIp(ip);
			List<ProtocolDataItems> proDataList2 = ip_proData.get(ip);
			List<ProtocolDataItems> proDataList = compressData(proDataList2);
			
			List<TreeMap<Integer,SymbolNode>> linesList = new ArrayList<TreeMap<Integer,SymbolNode>>();
			List<DataItems> lineList = new ArrayList<DataItems>();
			linesPosList = new ArrayList<TreeMap<Integer,Linear>>();
			for(int i = 0;i < proDataList.size();i++)
			{
				TreeMap<Integer,Double> sourceData_i = convertDataToTreeMap(proDataList.get(i));
				System.out.println("开始运行自底向上线段拟合算法！");
//				System.out.println("***"+sourceData_i);
		        BottomUpLinear bottomUpLinear_i = new BottomUpLinear(sourceData_i,arp);
		        bottomUpLinear_i.run();
		        TreeMap<Integer, Linear> linears = bottomUpLinear_i.getLinears();  //linears的格式为:key:线段起始位置，Linear：span表示该线段的长度
		        DataItems lineData = getLineData(linears);
		        lineList.add(lineData);
		        //过滤短线段
//		        Iterator<Map.Entry<Integer, Linear>> it = linears.entrySet().iterator();
////		        System.out.println("过滤前线段条数："+linears.size());
//		        while(it.hasNext())
//		        {
//		        	Map.Entry<Integer, Linear> entry =it.next();
//		        	Linear linear =entry.getValue();
//		        	if(linear.span<=1)
//		        		it.remove();
//		        		
//		        }
//		        System.out.println("过滤后线段条数："+linears.size());
		        linesPosList.add(linears);  
		        System.out.println("**"+linears);
		        System.out.println("开始运行DPCluster聚类算法！");
		        ClusterWrapper clusterWrapper_i = new ClusterWrapper(linears,arp);
//		        DPCluster dpCluster_i = clusterWrapper_i.run();
//		        Map<Integer,Integer> map_i = dpCluster_i.getBelongClusterCenter();
		        Map<Integer,Integer> map_i = clusterWrapper_i.run();
		        TreeMap<Integer,SymbolNode> symbols_i = getSymbols(map_i,i); 
		        linesList.add(symbols_i);
		        System.out.println("DPCluster聚类算法计算完毕！");
		        System.out.println("***************************************************");
			}
			double max_confidence = 0.0;
			List<ProtoclPair> resultList = new ArrayList<ProtoclPair>();
			for(int i = 0;i < linesList.size();i++)
			{
				HashMap<Integer,TreeMap<Integer,SymbolNode>> symbolSeries = new HashMap<Integer, TreeMap<Integer, SymbolNode>>();
				symbolSeries.put(i,linesList.get(i));
				for(int j = i+1;j < linesList.size();j++)
				{
					symbolSeries.put(j,linesList.get(j));
					FindRules findRules = new FindRules(symbolSeries);
					findRules.run();
					
					ProtoclPair pp = new ProtoclPair(proDataList.get(i).getProtocolName(),
							proDataList.get(j).getProtocolName(),
							proDataList.get(i).getDataItems(),proDataList.get(j).getDataItems());
					
					
					int son = 0,father_len = 0,son_len = 0;
					
					System.out.println(findRules.rulesSet.size());
					
					HashMap<String,ArrayList<LinePos>> mapAB = new HashMap<String,ArrayList<LinePos>>();  //记录序列1与序列2 各个关联符号所在的位置 A,B表示在哪个序列上，12表示序列的比较顺序
					HashMap<String,ArrayList<LinePos>> mapBA = new HashMap<String,ArrayList<LinePos>>();
					
					int symbol = 1;
					double sum_confidence = 0;
					for(Rule rule : findRules.rulesSet){
						
						symbol++;
						sum_confidence += rule.con;
						ArrayList<LinePos> alp = new ArrayList<LinePos>();
						if(rule.after.belong_series == i)
						{
							System.out.println(rule.parent_node_time_map);
							for(int father :rule.parent_node_time_map.keySet()){
								
								
								son = rule.parent_node_time_map.get(father);  //对应 after
								son_len = linesPosList.get(i).get(son).span;
								father_len = linesPosList.get(j).get(father).span;
								
								LinePos lp = new LinePos();
								lp.A_start = father;
								lp.A_end = father+father_len;
								
								lp.B_start = son;
								lp.B_end = son+son_len;
								alp.add(lp);
								
//								assi.add(String.valueOf(father)+","+String.valueOf(father+father_len));
//								assj.add(String.valueOf(son)+","+String.valueOf(son+son_len));
							}
							mapAB.put(String.valueOf(symbol),alp);
							
						}
						else if(rule.after.belong_series == j){ 
							
							for(int father :rule.parent_node_time_map.keySet()){
								
								son = rule.parent_node_time_map.get(father);
								son_len = linesPosList.get(j).get(son).span;
								father_len = linesPosList.get(i).get(father).span;
								
								LinePos lp = new LinePos();
								lp.A_start = son;
								lp.A_end = son+son_len; 
								
								lp.B_start = father;
								lp.B_end = father+father_len;
								alp.add(lp);
							}
							mapBA.put(String.valueOf(symbol),alp);
						}
						
					}
					pp.setConfidence(sum_confidence/(symbol-1));
					pp.setMapAB(mapAB);
					pp.setMapBA(mapBA);
					pp.setLineDataItems1(lineList.get(i));
					pp.setLineDataItems2(lineList.get(j));
					resultList.add(pp);
					
					if(max_confidence < pp.confidence)
						max_confidence = pp.confidence;
					symbolSeries.remove(j);
					System.out.println("*********************一轮结束****************************");
				}
			}
			mr_fp_l.setConfidence(max_confidence);
			mr_fp_l.protocolPairList = resultList;
			mr_fp_l.setLinesList(linesPosList);
			//找序列的关联信息
	        //计算两个符号化序列的关联度
		}
		return mr_fp_l;
	}
	
	private DataItems getLineData(TreeMap<Integer, Linear> linears) {
		
		DataItems lineData = new DataItems();
		int lastPoint = 0;
		Iterator<Integer> iter = linears.keySet().iterator();
		while(iter.hasNext()) {
			
			int key = iter.next();
			lineData.data.add(String.valueOf(key));
			lastPoint = key+linears.get(key).span;
		}
		lineData.data.add(String.valueOf(lastPoint));
		return lineData;
	}
	private TreeMap<Integer, Double> convertDataToTreeMap(
			ProtocolDataItems protocolDataItems) {
		
		TreeMap<Integer, Double> sourceData = new TreeMap<Integer, Double>();
		for(int i = 0;i < protocolDataItems.getDataItems().data.size();i++)
		{
			sourceData.put(i,Double.parseDouble(protocolDataItems.getDataItems().data.get(i)));
		}
		return sourceData;
	}
	private TreeMap<Integer, SymbolNode> getSymbols(Map<Integer, Integer> map,int series) {

		TreeMap<Integer, SymbolNode> symbolMap = new TreeMap<Integer, SymbolNode>();
		for(int i:map.keySet())
		{
           int center = map.get(i);
           if(center==-2)     //异常点跳过
            	continue;
            else if(center==-1)
            	center = i;
            SymbolNode symbolNode = new SymbolNode(center,series);
            symbolMap.put(i,symbolNode);  //哪个样本点归属那个中心，中心代表类别
            
		}
		return symbolMap;
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
	/**
	 * 功能：将数据的常数进行压缩，保证数据点在400以内
	 * @param proDataList
	 * @return
	 */
	private List<ProtocolDataItems> compressData(
			List<ProtocolDataItems> proDataList) {

		List<ProtocolDataItems> compressData = new ArrayList<ProtocolDataItems>();
		for(int i = 0;i < proDataList.size();i++)
		{
			ProtocolDataItems pdi = proDataList.get(i);
			if(pdi.dataItems.getLength() > 400)
			{
				int len = pdi.dataItems.getLength()/200;
				
				DataItems newData = new DataItems();
				for(int j = 0;j < pdi.dataItems.getLength();j += len)
				{
					int size = pdi.dataItems.getLength() < j+len?pdi.dataItems.getLength():j+len;
					int trafficSum  = 0;
					for(int k = j;k < size;k++)
					{
//						trafficSum += Integer.parseInt(pdi.dataItems.data.get(k));
						trafficSum += Double.parseDouble(pdi.dataItems.data.get(k));
					}
					newData.add1Data(pdi.dataItems.time.get(j), trafficSum+"");
				}
				ProtocolDataItems new_pdi = new ProtocolDataItems(pdi.protocolName,newData);
				compressData.add(new_pdi);
			}
			else{
				compressData.add(pdi);
			}
		}
		return compressData;
	}
}