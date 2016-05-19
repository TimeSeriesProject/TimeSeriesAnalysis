package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.CWNetworkReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class NetworkFactory {
	private static NetworkFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	public static HashMap<String, HashMap<String, DataItems>> eachProtocolItems;
	
	NetworkFactory(){
		eachProtocolItems= new HashMap<String, HashMap<String,DataItems>>();
	}
	
	public static NetworkFactory getInstance(){
		if(inst == null){
			isMining = false;
			inst = new NetworkFactory();
		}
		return inst;
	}
	public void mineNetworkRule(){

		/**
		 * 网络直径挖掘
		 */
		TaskElement diameter_task = new TaskElement();
		diameter_task.setSourcePath(dataPath);
		diameter_task.setMiningObject("网络直径");
		CWNetworkReader reader = new CWNetworkReader(diameter_task);
		System.out.println("网络直径");
		DataItems diameter_dataItems = reader.readInputByText();
//		System.out.println();
	
		/**
		 * 网络簇系数挖掘
		 */
		TaskElement cluster_task = new TaskElement();
		cluster_task.setSourcePath(dataPath);
		cluster_task.setMiningObject("网络簇系数");
		reader = new CWNetworkReader(cluster_task);
		System.out.println("网络簇系数");
		DataItems cluster_dataItems = reader.readInputByText();
		
		
	}
}
