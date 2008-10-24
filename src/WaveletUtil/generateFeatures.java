package WaveletUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oracle.net.aso.i;

import org.rosuda.REngine.Rserve.RConnection;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class generateFeatures {
	private DataItems dataItems;
	private String outputFilePrefix;
	private String trainOutputFilePath;
	private String testOutputFilePath;
	private int[] autoCorrelation;
	private double[] items;
	public generateFeatures(){}
	public generateFeatures(DataItems dataItems,String FileName){
		this.dataItems=dataItems;
		this.outputFilePrefix=FileName;
	}
	public void generateItems()throws Exception{
		List<String> items=dataItems.getData();
		setItems(items);
		autoCorrelation=atuoCorrelation(items,100);
		List<String> features=geneFeatures(items);
		TextUtils utils=new TextUtils();
		setTestOutputFilePath("./configs/test_"+outputFilePrefix+".csv");
		setTrainOutputFilePath("./configs/train_"+outputFilePrefix+".csv");
		utils.writeOutput(features, testOutputFilePath, trainOutputFilePath);
	}
	
	public String getOutputFilePrefix() {
		return outputFilePrefix;
	}
	public void setOutputFilePrefix(String outputFilePrefix) {
		this.outputFilePrefix = outputFilePrefix;
	}
	public int[] getAutoCorrelation() {
		return autoCorrelation;
	}
	public void setAutoCorrelation(int[] autoCorrelation) {
		this.autoCorrelation = autoCorrelation;
	}
	public double[] getItems() {
		return items;
	}
	public void setItems(double[] items) {
		this.items = items;
	}
	public void setItems(List<String> items){
		this.items=new double[items.size()];
		for(int i=0;i<items.size();i++){
			this.items[i]=Double.parseDouble(items.get(i));
		}
	}
	public int[] atuoCorrelation(List<String> stringItems,int length) throws Exception{
		double items[]=new double[stringItems.size()];
		for(int i=0;i<stringItems.size();i++){
			items[i]=Double.parseDouble(stringItems.get(i));
		}
		RConnection connection=new RConnection();
		connection.assign("length", length+"");
		connection.assign("items", items);
		connection.eval("atuoCorrelation=acf(items,lag.max=length,plot='FALSE')");
		double[] acf=connection.eval("atuoCorrelation$acf").asDoubles();
		List<Integer> correlationIndex=new ArrayList<Integer>();
		for(int i=0;i<acf.length;i++){
			if(acf[i]>0.30&&acf[i]<0.99999){
				correlationIndex.add(i);
			}
		}
		if(correlationIndex.size()==0){
			for(int i=1;i<=10;i++)
				correlationIndex.add(i);
		}
		if(correlationIndex.size()>20){
			correlationIndex=correlationIndex.subList(0, 20);
		}
		int[] correlationIndexArray=new int[correlationIndex.size()];
		for(int i=0;i<correlationIndex.size();i++){
			correlationIndexArray[i]=correlationIndex.get(i);
		}
		return correlationIndexArray;
	}
	
	public int[] atuoCorrelation(double[] items) throws Exception{
		RConnection connection=new RConnection();
		connection.assign("items", items);
		connection.eval("atuoCorrelation=acf(items,lag.max=24,plot='FALSE')");
		double[] acf=connection.eval("atuoCorrelation$acf").asDoubles();
		List<Integer> correlationIndex=new ArrayList<Integer>();
		for(int i=0;i<acf.length;i++){
			if(acf[i]>0.30&&acf[i]<0.99999){
				correlationIndex.add(i);
			}
		}
		int[] correlationIndexArray=new int[correlationIndex.size()];
		for(int i=0;i<correlationIndex.size();i++){
			correlationIndexArray[i]=correlationIndex.get(i);
		}
		return correlationIndexArray;
	}
	
	private List<String> geneFeatures(List<String> itemlist) {
		String[] items=new String[itemlist.size()];
		for(int i=0;i<itemlist.size();i++){
			items[i]=itemlist.get(i);
		}
		List<String> featureList=new ArrayList<String>();
		
		int lastLengthIndex=autoCorrelation[autoCorrelation.length-1];
		StringBuilder sb=new StringBuilder();
		for(int i=lastLengthIndex;i<items.length;i++){
			for(int j=0;j<autoCorrelation.length;j++){
				sb.append(items[i-autoCorrelation[j]]).append(",");
			}
			sb.append(items[i]);
			featureList.add(sb.toString());
			sb.delete(0, sb.length());
		}
		return featureList;
	}
	
	
	public String getTrainOutputFilePath() {
		return trainOutputFilePath;
	}
	public void setTrainOutputFilePath(String trainOutputFilePath) {
		this.trainOutputFilePath = trainOutputFilePath;
	}
	public String getTestOutputFilePath() {
		return testOutputFilePath;
	}
	public void setTestOutputFilePath(String testOutputFilePath) {
		this.testOutputFilePath = testOutputFilePath;
	}
}
