package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;

public class ProtocolAssMinerFactory {
	private static ProtocolAssMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	public static HashMap<String, HashMap<String, DataItems>> eachProtocolItems;
	
	ProtocolAssMinerFactory(){
		eachProtocolItems= new HashMap<String, HashMap<String,DataItems>>();
	}
	
	public static ProtocolAssMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new ProtocolAssMinerFactory();
		}
		return inst;
	}
	public HashMap<String, HashMap<String,DataItems>> getData(){
		return eachProtocolItems;
	}
	public Map<String,List<ProtocolAssociationResult>> mineAllAssociations(double thresh,int whichAlogrithm){
		
		if(isMining)
			return null;
		isMining=true;
		File dataDirectory=new File(dataPath);
		nodePairReader reader=new nodePairReader();
		if(dataDirectory.isFile()){
			parseFile(dataDirectory,reader);
		}else{
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<dataDirs.length;i++){
				parseFile(dataDirs[i],reader);
			}
		}
		Map<String,List<ProtocolAssociationResult>> protocolResult = null;
		if(whichAlogrithm == 1 || whichAlogrithm == 2)
		{
			ProtocolAssociation pa = new ProtocolAssociation(eachProtocolItems, thresh, whichAlogrithm);
			protocolResult = pa.miningAssociation();
		}
		else if(whichAlogrithm == 3)
		{
			ProtocolAssociationLine pa = new ProtocolAssociationLine(eachProtocolItems);
			protocolResult = pa.miningAssociation();
		}
		return protocolResult;
	}
	
	
	private void parseFile(File dataFile,nodePairReader reader){
		String ip=dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
		HashMap<String, DataItems> rawDataItems=
						reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		eachProtocolItems.put(ip, rawDataItems);
	}
	
	public static void main(String[] args){
		inst=getInstance();
		Map<String,List<ProtocolAssociationResult>> map=inst.mineAllAssociations(0.6,1);
		Iterator<Entry<String, List<ProtocolAssociationResult>>> iterator=map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, List<ProtocolAssociationResult>> entry=iterator.next();
			System.out.println(entry.getKey());
			List<ProtocolAssociationResult> list=entry.getValue();
			StringBuilder sb=new StringBuilder();
			for(ProtocolAssociationResult result:list){
				List<String> data1=result.dataItems1.getData();
				List<String> data2=result.dataItems2.getData();
				
				sb.delete(0, sb.length());
				for(String item:data1)
					sb.append(",").append(item);
				sb.deleteCharAt(0);
				System.out.println(sb.toString());
				sb.delete(0, sb.length());
				for(String item:data2)
					sb.append(",").append(item);
				sb.deleteCharAt(0);
				System.out.println(sb.toString());
			}
			
		}
	}
	
}
