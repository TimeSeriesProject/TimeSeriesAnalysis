package RTreeUtil;

import java.io.File;
import java.util.HashMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;

public class Test {
	public static void main(String[] args){
		String dataPath="F:\\parse\\rawDataInput";
		RTreeIndex tree=new RTreeIndex();
		File dataDirectory=new File(dataPath);
		nodePairReader reader=new nodePairReader();
		if(dataDirectory.isFile()){
			parseFile(dataDirectory,reader,tree);
		}else{
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<dataDirs.length;i++){
				parseFile(dataDirs[i],reader,tree);
			}
		}
		System.out.println("build tree over");
		KNNTimeSires knnSearch=new KNNTimeSires(2, tree.root);
		dataDirectory=new File(dataPath);
		if(dataDirectory.isFile()){
			searchDataItems(dataDirectory,reader,knnSearch);
		}else{
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<dataDirs.length;i++){
				searchDataItems(dataDirs[i],reader,knnSearch);
			}
		}
	}
	
	private static void parseFile(File dataFile,nodePairReader reader,RTreeIndex tree){
		String ip=dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
		//事先读取每一个IP上，每一个协议的DataItems
		HashMap<String, DataItems> rawDataItems=
						reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		for(String protocol:rawDataItems.keySet()){
			String name=ip+"_"+protocol;
			DataItems dataItems=rawDataItems.get(protocol);
			dataItems=DataPretreatment.aggregateData(dataItems, 3600, 
					AggregateMethod.Aggregate_SUM,false);
			tree.insert(dataItems,name);
		}
	}
	
	private static void searchDataItems(File dataFile,nodePairReader reader,KNNTimeSires knnSearch){
		String ip=dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
		//事先读取每一个IP上，每一个协议的DataItems
		HashMap<String, DataItems> rawDataItems=
						reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		for(String protocol:rawDataItems.keySet()){
			String name=ip+"_"+protocol;
			DataItems dataItems=rawDataItems.get(protocol);
			dataItems=DataPretreatment.aggregateData(dataItems, 3600, 
					AggregateMethod.Aggregate_SUM,false);
			TimeSeries query=new TimeSeries(dataItems, name);
			knnSearch.K_search(query);
			//System.out.println(name+":");
			for(int i=0;i<knnSearch.getResults().size();i++){
				System.out.println(i+": "+name+"-->"+knnSearch.getResults().get(i).name);
			}
			knnSearch.clear();
		}
	}
}
