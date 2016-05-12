package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;

public class ProtocolAssMinerFactory {
	private static ProtocolAssMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	public Map<String, HashMap<String, DataItems>> eachProtocolItems;
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
		/**
		 * 参数说明
		 * 第一个参数为待处理的数据，第二个参数为阈值/支持度，第三个参数为选用的方法数，可选值为{1，2}。
		 * 阈值的设置跟所选的方法有关，如果选择方法1，则阈值范围为[0,1]，选用方法2，理论上值为所有正数
		 */
		ProtocolAssociation pa = new ProtocolAssociation(eachProtocolItems, thresh, whichAlogrithm);
		Map<String,List<ProtocolAssociationResult>> protocolResult = pa.miningAssociation();
		return protocolResult;
//		return null;
	}
	
	
	private void parseFile(File dataFile,nodePairReader reader){
		String ip=dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
		//���ȶ�ȡÿһ��IP�ϣ�ÿһ��Э���DataItems
		HashMap<String, DataItems> rawDataItems=
						reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		eachProtocolItems.put(ip, rawDataItems);
	}
}
