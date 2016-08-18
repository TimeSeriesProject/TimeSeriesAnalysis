package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AssociationAlgorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsFP_Whole;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import associationRules.ProtocolAssociationResult;
import RTreeUtil.KNNTimeSires;
import RTreeUtil.LeafNode;
import RTreeUtil.NonLeafNode;
import RTreeUtil.RTreeIndex;
import RTreeUtil.TimeSeries;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public class ProtocolAssRtree {
	private HashMap<String, HashMap<String, DataItems>> eachProtocolItems;
	private RTreeIndex tree;
	private Map<String,List<ProtocolAssociationResult>> protocolResult;
	public ProtocolAssRtree(HashMap<String, HashMap<String, DataItems>> eachProtocolItems){
		this.eachProtocolItems=eachProtocolItems;
		protocolResult=new HashMap<String, List<ProtocolAssociationResult>>();
		tree=new RTreeIndex();
	}
	
	private void deleteTree(NonLeafNode node){
		if(node.nextTOLeaf){
			List<LeafNode> leaive=node.leafChilds;
			for(LeafNode leaf:leaive){
				deleteTree(leaf);
			}
			leaive.clear();
		}else{
			List<NonLeafNode> nonLeafNodes=node.nonLeafChilds;
			for(NonLeafNode nonLeafNode:nonLeafNodes)
				deleteTree(nonLeafNode);
			nonLeafNodes.clear();
		}
		node.parent=null;
	}
	
	private void deleteTree(LeafNode node){
		node.parent=null;
	}
	
	public MinerResultsFP_Whole miningAssociation(){
		MinerResultsFP_Whole results=new MinerResultsFP_Whole();
		Iterator<Entry<String, HashMap<String, DataItems>>> iterator=eachProtocolItems.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, HashMap<String, DataItems>> entry=iterator.next();
			Iterator<Entry<String, DataItems>> itemIterator=entry.getValue().entrySet().iterator();
			while(itemIterator.hasNext()){
				Entry<String, DataItems> itemEntry=itemIterator.next();
				String name=entry.getKey()+"_"+itemEntry.getKey();
				tree.insert(itemEntry.getValue(), name);
			}
			KNNTimeSires knnSearch=new KNNTimeSires(2, tree.root);
			searchDataItems(entry.getKey(),entry.getValue(), knnSearch,results);
			deleteTree(tree.root);
			tree=new RTreeIndex();
			break;
		}
		return results;
	}
	
	private void searchDataItems(String ip, HashMap<String, DataItems>eachProtocolItems,KNNTimeSires knnSearch,
			MinerResultsFP_Whole results){
		
		Iterator<Entry<String,  DataItems>> iterator=eachProtocolItems.entrySet().iterator();
		List<ProtocolAssociationResult> list=new ArrayList<ProtocolAssociationResult>();
		while(iterator.hasNext()){
			Entry<String, DataItems> itemEntry=iterator.next();
			String name=ip+"_"+itemEntry.getKey();
			TimeSeries query=new TimeSeries(itemEntry.getValue(), name);
			knnSearch.K_search(query);
			for(int i=0;i<knnSearch.getResults().size();i++){
				if(knnSearch.getResults().get(i).name.equals(name)){
					continue;
				}
				String searchProtocl=itemEntry.getKey();
				String similarityProtocol=knnSearch.getResults().get(i).name.split("_")[1];
				ProtocolAssociationResult result=new ProtocolAssociationResult(searchProtocl,similarityProtocol,
						itemEntry.getValue(),knnSearch.getResults().get(i).dataItems,0,
						1.0/knnSearch.getDistResults().get(i));
				list.add(result);
				break;
			}
			results.setProtocolPairList(list);
			DescriptiveStatistics statistics=new DescriptiveStatistics();
//			for(ProtocolAssociationResult proAssResult:results.getProtocolPairList())
//				//statistics.addValue(proAssResult);
		}
	}
}