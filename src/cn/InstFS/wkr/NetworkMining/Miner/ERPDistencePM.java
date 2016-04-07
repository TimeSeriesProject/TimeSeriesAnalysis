package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.net.aso.k;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import ec.nbdemetra.x13.ui.X13ViewFactory.DTablesFactory;

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
		int maxPeriod = (numItems/2>300)?300:(numItems/2);
		if(di.isAllDataIsDouble()){
			List<String> seq=new ArrayList<String>();
			for(int i=0;i<numItems;i++){
				seq.add((int)(Double.parseDouble(di.getData().get(i)))+"");
			}
//			for(String i:seq){
//				System.out.println(i);
//			}
			//System.out.println();
			generateManHatonEntroy(seq,numItems);
			isPeriodExist(maxPeriod,null,seq);
		}else{
			List<Map<String, Integer>> nonnumData=di.getNonNumData();
			Set<String> itemSet=di.getVarSet();
			int  total=0;
			for(String item:itemSet){
				total=0;
				List<String> seq=new ArrayList<String>();
				for(Map<String, Integer>map:nonnumData){
					if(map.containsKey(item)){
						int value=map.get(item);
						total+=value;
						seq.add(value+"");
					}else{
						seq.add("0");
					}
				}
				if(total<200)
					continue;
				System.out.println(item+":"+total);
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
		int maxPeriod = (numItems/2>300)?300:(numItems/2);
		int period=1;
		entropies=new Double[maxPeriod];
		List<String> standardList=new ArrayList<String>();
		ArrayList<String> seqList=new ArrayList<String>();
		double Entropy;
		while((period+1)<= maxPeriod){
			period++;	//周期递加
			standardList.clear();
			Entropy=0.0;
			ErpDistMatrix=new double[period][period];
			generateStandardList(seq, period, standardList);
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
				double en=ERPDistance(standardList, seqList, period-1,period-1, ErpDistMatrix);
				Entropy+=en;
			}
			double diff=Entropy;
			entropies[period-1]=diff;
			System.out.println("period "+period+"'s diff is "+diff);
		}
	}
	
	/**
	 * 计算没个时间段的曼哈顿值
	 * @param seq 
	 * @param numItems
	 */
	private void generateManHatonEntroy(List<String> seq,int numItems){
		int maxPeriod = (numItems/2>300)?300:(numItems/2);
		int period=1;
		entropies=new Double[maxPeriod];
		List<String> standardList=new ArrayList<String>();
		ArrayList<String> seqList=new ArrayList<String>();
		double Entropy;
		while((period+1)<= maxPeriod){
			period++;	//周期递加
			standardList.clear();
			Entropy=0.0;
			generateStandardList(seq, period, standardList);
			for(int i=0;i<numItems/period;i++){
				seqList.clear();
				for(int j=0;j<period;j++){
					seqList.add(seq.get(i*period+j));
				}
				double en=MHTDistance(standardList, seqList);
				Entropy+=en;
			}
			double diff=Entropy;
			entropies[period-1]=diff;
			System.out.println("period "+period+"'s diff is "+diff);
		}
	}
	
	private double MHTDistance(List<String> seqX,List<String>seqY){
		double distance=0;
		for(int i=0;i<seqX.size();i++){
			distance+=Math.abs(Double.parseDouble(seqX.get(i))-Double.parseDouble(seqY.get(i)));
		}
		return distance;
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
				sum+=Math.ceil(Math.abs(Double.parseDouble(seqY.get(i))));
			}
			return sum;
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=Math.ceil(Math.abs(Double.parseDouble(seqX.get(i))));
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
	
//	private void geneNormList(List<String> seq,List<String> normList){
//		DescriptiveStatistics meanStatistics=new DescriptiveStatistics();
//		int length=seq.size();
//		List<Double> items=new ArrayList<Double>();
//		for(String item:seq){
//			items.add(Double.parseDouble(item));
//			meanStatistics.addValue(Double.parseDouble(item));
//		}
//		
//		double stdMean=meanStatistics.getMean();
//		double min=meanStatistics.getMin();
//		meanStatistics.clear();
//		for(int i=0;i<length;i++){
//			items.set(i, items.get(i)-min);
//			
//		}
//		for(int i=0;i<items.size();i++){
//			normList.add((items.get(i)/(stdMean-min+0.1)*10)+"");
//		}
//	}
	
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
		hasPeriod=false;
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
			if(key<Period)
				Period=key;
		}
		
		int multiple=1;
		boolean isSuccess=true;
		while((multiple+1)*Period<maxPeriod){
			if(entropies[multiple*Period-1]-entropies[(multiple+1)*Period-1]>=(-entropies[multiple*Period-1]*0.2)){
				multiple++;
			}else{
				isSuccess=false;
				break;
			}
		}
		if(!isSuccess){
			Period=multiple*Period;
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
			if(predictValues==null){
				predictValues=new Integer[predictPeriod];
				for(int index=0;index<predictPeriod;index++){
					predictValues[index]=0;
				}
				for(int j=0;j<di.getLength();j++){
					predictValues[j%(predictPeriod)]+=(int)Double.parseDouble(seq.get(j));
				}
				for(int j=0;j<(predictPeriod);j++){
					predictValues[j]/=(di.getLength()/(predictPeriod));
				}
			}
			predictValuesMap.put(predictPeriod, predictValues);
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
		if(index==2){
			if(Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*0.2){
				if(!nextPeriod(Entropies, index)){
					period=false;
				}
			}else{
				period=false;
			}
		}else if(index==Entropies.length){
			if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*0.2){
				if(!nextPeriod(Entropies, index)){
					period=false;
				}
			}else{
				period=false;
			}
		}else{
            if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*0.2&&
            		Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*0.2){
            	if(!nextPeriod(Entropies, index)){
					period=false;
				}
			}else{
				period=false;
			}
		}
		return period;
	}
	
	private boolean nextPeriod(Double[] Entropies,int index){
		int i=index;
		boolean period=true;
		int num=0;
		while(i<=Entropies.length&&num<3){
			if(i==2){
				if(Entropies[i-1]-Entropies[i]>0){
					period=false;
					break;
				}
			}else if(i==(Entropies.length)){
				if(Entropies[i-1]-Entropies[i-2]>0){
					period=false;
					break;
				}
			}else{
				if(Entropies[i-1]-Entropies[i-2]>0||Entropies[i-1]-Entropies[i]>0){
					period=false;
					break;
				}
			}
			i+=index;
			num++;
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
