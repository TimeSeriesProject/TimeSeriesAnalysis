package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ERPDistencePM implements IMinerPM {
	private int[][] distMatrix;
	private DataItems di; //当前时间区间的数据集
	private Double minEntropy = Double.MAX_VALUE;  
    private Double []entropies;   //存储每个可能周期的平均熵或平均ERP距离
    private HashMap<Integer, Integer[]> predictValuesMap;
   	private double threshold;  //是否具有周期的阈值
   	private int lastNumIndexInPeriod;//最后一个数在周期中的位置
   	private Boolean hasPeriod; //是否有周期
	private int predictPeriod;   //周期长度
	private List<Integer> existPeriod;
	private DataItems itemsInPeriod;  //一个周期内的items
	
	
	private Map<String, List<Integer>> existPeriodOfNonNumDataItems;
	private Map<String, Boolean> hasPeriodOfNonNumDataItms;
	private Map<String, Integer> predictPeriodOfNonNumDataItems;
	private Map<String, Map<Integer, Integer[]>> predictValuesMapOfNonNumDataItems;
	private Map<String, DataItems> itemsInperiodMapOfNonNumDataitems;

	
	public ERPDistencePM(DataItems di){
		this.di=di;
		hasPeriod = false;
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
		if(!di.isAllDataIsDouble()){
			existPeriodOfNonNumDataItems=new HashMap<String, List<Integer>>();
			hasPeriodOfNonNumDataItms=new HashMap<String, Boolean>();
			predictPeriodOfNonNumDataItems=new HashMap<String, Integer>();
			predictValuesMapOfNonNumDataItems=new HashMap<String, Map<Integer,Integer[]>>();
			itemsInperiodMapOfNonNumDataitems=new HashMap<String, DataItems>();
		}
	}
	
	public ERPDistencePM(){
		hasPeriod = false;
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public void predictPeriod(){
		int numItems=di.getLength();
		if(numItems==0){
			return;
		}
		int maxPeriod = (numItems/2>300)?300:numItems/2;
		if(di.isAllDataIsDouble()){
			List<String> seq=new ArrayList<String>();
			for(int i=0;i<numItems;i++){
				seq.add((int)Double.parseDouble(di.getData().get(i))+"");
			}
			generateEntroy(seq,numItems);
			isPeriodExist(maxPeriod,null,seq);
		}else{
			List<Map<String, Integer>> nonnumData=di.getNonNumData();
			Set<String> itemSet=di.getVarSet();
			for(String item:itemSet){
				List<String> seq=new ArrayList<String>();
				for(Map<String, Integer>map:nonnumData){
					if(map.containsKey(item)){
						int value=map.get(item);
						seq.add(value+"");
					}else{
						seq.add("0");
					}
				}
				System.out.println(item);
				generateEntroy(seq, numItems);
				isPeriodExist(maxPeriod,item,seq);
			}
		}
	}
	/**
	 * 计算没个时间段的ERP值，以ERP的时间段作为周期值
	 * @param seq 
	 * @param numItems
	 */
	private void generateEntroy(List<String> seq,int numItems){
		double[][] ErpDistMatrix;
		int maxPeriod = (numItems/2>300)?300:numItems/2;
		int period=1;
		entropies=new Double[maxPeriod];
		List<String> standardList=new ArrayList<String>();
		ArrayList<String> seqList=new ArrayList<String>();
		double Entropy;
		while((period+1)<= maxPeriod){
			period++;	//周期递加
			Entropy=0.0;
			standardList.clear();
			generateStandardList(seq, period, standardList);
			ErpDistMatrix=new double[period][period];
			for(int i=0;i<numItems/period;i++){
				seqList.clear();
				for(int j=0;j<period;j++){
					seqList.add(seq.get(i*period+j));
				}
				for(int k=0;k<period;k++){
					for (int j = 0; j < period; j++) {
						ErpDistMatrix[k][j]=-1;
					}
				}
				Entropy+=ERPDistance(standardList, seqList, period-1,period-1, ErpDistMatrix);
			}
			double diff=Entropy/period;
			entropies[period-1]=diff;
			System.out.println("period "+period+"'s diff is "+diff);
		}
	}
	
	
	
	/**
	 * 返回两条序列的ERP距离
	 * @param seqX 第一条序列
	 * @param seqY 第二条序列
	 * @param offset 第二条序列是平移offset个单位得到的
	 * @return ERP距离
	 */
	
	private double ERPDistance(List<String> seqX,List<String>seqY,int xSize,int ySize,double [][]matrix){
		if(xSize<0&&ySize<0){
			return 0;
		}else if(xSize<0){
			int sum=0;
			for(int i=0;i<=ySize;i++){
				sum+=Math.ceil(Double.parseDouble(seqY.get(i)));
			}
			return sum;
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=Math.ceil(Double.parseDouble(seqX.get(i)));
			}
			return sum;
		}
		
		if(matrix[xSize][ySize]>=0){
			return matrix[xSize][ySize];
		}else{
			double xItem=Double.parseDouble(seqX.get(xSize));
			double yItem=Double.parseDouble(seqY.get(ySize));
			double dis1=ERPDistance(seqX,seqY,xSize-1,ySize-1,matrix)+Math.abs(xItem-yItem);
			double dis2=ERPDistance(seqX, seqY,xSize-1,ySize,matrix)+Math.abs(xItem);
			double dis3=ERPDistance(seqX, seqY,xSize,ySize-1,matrix)+Math.abs(yItem);
			double min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[xSize][ySize]=min;
			return min;
		}
		
	}
	
	/**
	 * 生成指定周期内的标准序列
	 * @param seq 原序列
	 * @param length 标准序列长度
	 * @param standardList 标准序列
	 */
	private void generateStandardList(List<String> seq,int length,List<String> standardList){
		int seqLen=seq.size();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(int i=0;i<length;i++){
			statistics.clear();
			for(int j=0;j<seqLen/length;j++){
				statistics.addValue(Double.parseDouble(seq.get(j*length+i)));
			}
			standardList.add(statistics.getMean()+"");
		}
	}
	
	/**
	 * 序列未切分的ERP距离
	 * private int ERPDistance(List<String> seqX,List<String>seqY,int xSize,int ySize,int [][]matrix,int offset){
		if(xSize<0&&ySize<0){
			return 0;
		}else if(xSize<0){
			int sum=0;
			for(int i=0;i<=ySize;i++){
				sum+=Integer.parseInt(seqY.get(i));
			}
			return sum;
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=Integer.parseInt(seqX.get(i));
			}
			return sum;
		}
		
		if(matrix[xSize][ySize]!=0){
			return matrix[xSize][ySize];
		}else{
			int xItem=Integer.parseInt(seqX.get(xSize));
			int yItem=Integer.parseInt(seqY.get(ySize));
			int dis1=ERPDistance(seqX,seqY,xSize-1,ySize-1,matrix,offset)+distMatrix[xSize][ySize+offset];
			int dis2=ERPDistance(seqX, seqY,xSize-1,ySize,matrix,offset)+Math.abs(xItem);
			int dis3=ERPDistance(seqX, seqY,xSize,ySize-1,matrix,offset)+Math.abs(yItem);
			int min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[xSize][ySize]=min;
			return min;
		}
	}
	**
	*序列未切分设置Matrix
	private void setDistMatrix(List<String> items) {
		int length=items.size();
		distMatrix=new int[length][length];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				if(i==j)
					distMatrix[i][j]=7000*10;
				else
					distMatrix[i][j]=Math.abs((int)Double.parseDouble(items.get(i))-
							(int)Double.parseDouble(items.get(j)));
			}
		}
	}
	
	**
	*未切分线段的ＥＲＰ距离计算公式
	*private int ERPDistance(List<String> seqX,List<String>seqY,int xSize,int ySize,int [][]matrix,int offset){
		if(xSize<0&&ySize<0){
			return 0;
		}else if(xSize<0){
			int sum=0;
			for(int i=0;i<=ySize;i++){
				sum+=Integer.parseInt(seqY.get(i));
			}
			return sum;
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=Integer.parseInt(seqX.get(i));
			}
			return sum;
		}
		
		if(matrix[xSize][ySize]!=0){
			return matrix[xSize][ySize];
		}else{
			int xItem=Integer.parseInt(seqX.get(xSize));
			int yItem=Integer.parseInt(seqY.get(ySize));
			int dis1=ERPDistance(seqX,seqY,xSize-1,ySize-1,matrix,offset)+distMatrix[xSize][ySize+offset];
			int dis2=ERPDistance(seqX, seqY,xSize-1,ySize,matrix,offset)+Math.abs(xItem);
			int dis3=ERPDistance(seqX, seqY,xSize,ySize-1,matrix,offset)+Math.abs(yItem);
			int min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[xSize][ySize]=min;
			return min;
		}
	}
	 */
	
	private void isPeriodExist(int maxPeriod,String item,List<String>seq){
		itemsInPeriod=new DataItems();
		existPeriod=new ArrayList<Integer>();
		predictValuesMap=new HashMap<Integer, Integer[]>();
		for(int i=1;i<maxPeriod;i++){
			if(isPeriod(entropies, i+1)){
				hasPeriod=true;
				existPeriod.add(i+1);
				Integer[] predictValues=new Integer[i+1];
				for(int index=0;index<=i;index++){
					predictValues[index]=0;
				}
				for(int j=0;j<di.getLength();j++){
					predictValues[j%(i+1)]+=(int)Double.parseDouble(seq.get(j));
				}
				for(int j=0;j<(i+1);j++){
					predictValues[j]/=(di.getLength()/(i+1));
				}
				predictValuesMap.put((i+1), predictValues);
			}
		}
		int Period=maxPeriod;
		Set<Integer> keyset=predictValuesMap.keySet();
		for(Integer key:keyset){
			if(key<=Period){
				Period=key;
			}
		}
		predictPeriod=Period;
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<minEntropy){
				minEntropy=entropies[i];
			}
		}
		if(hasPeriod){
			itemsInPeriod=new DataItems();
			Integer[] predictValues=predictValuesMap.get(Period);
			for(int i=0;i<Period;i++){
				itemsInPeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
			}
			
		}else{
			itemsInPeriod=null;
			predictPeriod=-1;
			existPeriod=null;
			predictValuesMap=null;
		}
		
		if(item!=null){
			hasPeriodOfNonNumDataItms.put(item, hasPeriod);
			itemsInperiodMapOfNonNumDataitems.put(item, itemsInPeriod);
			existPeriodOfNonNumDataItems.put(item, existPeriod);
			predictPeriodOfNonNumDataItems.put(item, predictPeriod);
			predictValuesMapOfNonNumDataItems.put(item, predictValuesMap);
		}
	}
	
	private boolean isPeriod(Double[] Entropies,int index){
		boolean period=true;
		int i=index;
		if(i>(0.33*Entropies.length))
			return false;
		while(i<=Entropies.length){
			if(i==2){
				if(Entropies[i-1]-Entropies[i]<0){
					period=false;
					break;
				}
			}else if(i==(Entropies.length)){
				if(Entropies[i-1]-Entropies[i-2]<0){
					period=false;
					break;
				}
			}else{
				if(Entropies[i-1]-Entropies[i-2]>=0||Entropies[i-1]-Entropies[i]>=0){
					period=false;
					break;
				}
			}
			i+=index;
		}
		return period;
	}
	
	@Override
	public boolean hasPeriod() {
		return hasPeriod;
	}
	
	@Override
	public void setDataItems(DataItems dataItems) {
		this.di=dataItems;
		if(!di.isAllDataIsDouble()){
			existPeriodOfNonNumDataItems=new HashMap<String, List<Integer>>();
			hasPeriodOfNonNumDataItms=new HashMap<String, Boolean>();
			predictPeriodOfNonNumDataItems=new HashMap<String, Integer>();
			predictValuesMapOfNonNumDataItems=new HashMap<String, Map<Integer,Integer[]>>();
			itemsInperiodMapOfNonNumDataitems=new HashMap<String, DataItems>();
		}
	}
	
	@Override
	public int getPredictPeriod() {
		return predictPeriod;
	}
	
	public Map<String, List<Integer>> getExistPeriodOfNonNumDataItems() {
		return existPeriodOfNonNumDataItems;
	}
	public void setExistPeriodOfNonNumDataItems(
			Map<String, List<Integer>> existPeriodOfNonNumDataItems) {
		this.existPeriodOfNonNumDataItems = existPeriodOfNonNumDataItems;
	}
	public Map<String, Boolean> getHasPeriodOfNonNumDataItms() {
		return hasPeriodOfNonNumDataItms;
	}
	public void setHasPeriodOfNonNumDataItms(
			Map<String, Boolean> hasPeriodOfNonNumDataItms) {
		this.hasPeriodOfNonNumDataItms = hasPeriodOfNonNumDataItms;
	}
	public Map<String, Integer> getPredictPeriodOfNonNumDataItems() {
		return predictPeriodOfNonNumDataItems;
	}
	public void setPredictPeriodOfNonNumDataItems(
			Map<String, Integer> predictPeriodOfNonNumDataItems) {
		this.predictPeriodOfNonNumDataItems = predictPeriodOfNonNumDataItems;
	}
	public Map<String, Map<Integer, Integer[]>> getPredictValuesMapOfNonNumDataItems() {
		return predictValuesMapOfNonNumDataItems;
	}
	public void setPredictValuesMapOfNonNumDataItems(
			Map<String, Map<Integer, Integer[]>> predictValuesMapOfNonNumDataItems) {
		this.predictValuesMapOfNonNumDataItems = predictValuesMapOfNonNumDataItems;
	}
	@Override
	public DataItems getItemsInPeriod() {
		return itemsInPeriod;
	}
	
	@Override
	public Double getMinEntropy() {
		return minEntropy;
	}
	
	@Override
	public Double[] getEntropies() {
		return entropies;
	}
	
	@Override
	public int getFirstPossiblePeriod() {
		if(hasPeriod()){
     		return existPeriod.get(0);
		}else{
			return -1;
		}
		
	}
	
	@Override
	public int getLastNumberIndexInperiod() {
		if(hasPeriod()){
	    	lastNumIndexInPeriod=(di.getLength()-1)%(predictPeriod);
	    	return lastNumIndexInPeriod;
		}else{
			return -1;
		}
	}
	
	public int[] getPreidctValues(){
		if(hasPeriod()){
			Integer[] IntegerValues=predictValuesMap.get(predictPeriod);
			int[] intValues=new int[IntegerValues.length];
			for(int i=0;i<IntegerValues.length;i++){
				intValues[i]=IntegerValues[i];
			}
			return intValues;
		}else{
			return null;
		}
	}
	
	public Map<String, DataItems> getItemsInperiodMapOfNonNumDataitems() {
		return itemsInperiodMapOfNonNumDataitems;
	}
	public void setItemsInperiodMapOfNonNumDataitems(
			Map<String, DataItems> itemsInperiodMapOfNonNumDataitems) {
		this.itemsInperiodMapOfNonNumDataitems = itemsInperiodMapOfNonNumDataitems;
	}
	
	public DataItems getDi(){
		return di;
	}
	
	public void setThreshold(double Threshold){
		this.threshold=Threshold;
	}
	
	public double getThreshold(){
		return threshold;
	}
}
