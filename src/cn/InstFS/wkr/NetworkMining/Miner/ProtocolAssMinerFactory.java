package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;

public class ProtocolAssMinerFactory {
	private static ProtocolAssMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	private HashMap<String, HashMap<String, DataItems>> eachProtocolItems;
	private ProtocolAssMinerFactory(){
		eachProtocolItems=new HashMap<String, HashMap<String,DataItems>>();
	}
	
	public static ProtocolAssMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new ProtocolAssMinerFactory();
		}
		return inst;
	}
	
	public void mineAllAssociations(){
		if(isMining)
			return;
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
		
		//TODO 
		//new protocolAssociation
	}
	
	
	private void parseFile(File dataFile,nodePairReader reader){
		String ip=dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
		//事先读取每一个IP上，每一个协议的DataItems
		HashMap<String, DataItems> rawDataItems=
						reader.readEachProtocolDataItems(dataFile.getAbsolutePath());
		eachProtocolItems.put(ip, rawDataItems);
	}
}
