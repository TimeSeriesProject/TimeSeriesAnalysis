package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PeriodAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerPM;
import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ERPDistencePM implements IMinerPM {
	private int[][] distMatrix;
	private DataItems di; //当前时间区间的数据集
	private DataItems oriDi;
	private Double minEntropy = Double.MAX_VALUE;  
    private Double []entropies;   //存储每个可能周期的平均熵或平均ERP距离
    private HashMap<Integer, Double[]> predictValuesMap;
    private HashMap<Integer, Double[]> minPredictValuesMap;
    private HashMap<Integer, Double[]> maxPredictValuesMap;
   	private double threshold;  //是否具有周期的阈值
   	private int longestPeriod;
   	private int lastNumIndexInPeriod;//最后一个数在周期中的位置
   	private Boolean hasPeriod; //是否有周期
	private int predictPeriod;   //周期长度
	private List<Integer> existPeriod;
	private DataItems itemsInPeriod;  //一个周期内的items
	private DataItems minItemsInPeriod;
	private DataItems maxItemsInPeriod;
	private double confidence;
	
	
	private Map<String, List<Integer>> existPeriodOfNonNumDataItems;
	private Map<String, Boolean> hasPeriodOfNonNumDataItms;
	private Map<String, Integer> predictPeriodOfNonNumDataItems;
	private Map<String, Map<Integer, Double[]>> predictValuesMapOfNonNumDataItems;
	private Map<String, DataItems> itemsInperiodMapOfNonNumDataitems;

	
	public ERPDistencePM(DataItems di){
		this.di=di;
		hasPeriod = false;
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap= new HashMap<>();
		minPredictValuesMap= new HashMap<>();
		maxPredictValuesMap= new HashMap<>();
		existPeriod=new ArrayList<Integer>();
		if(!di.isAllDataIsDouble()){
			existPeriodOfNonNumDataItems=new HashMap<String, List<Integer>>();
			hasPeriodOfNonNumDataItms=new HashMap<String, Boolean>();
			predictPeriodOfNonNumDataItems=new HashMap<String, Integer>();
			predictValuesMapOfNonNumDataItems=new HashMap<String, Map<Integer,Double[]>>();
			itemsInperiodMapOfNonNumDataitems=new HashMap<String, DataItems>();
		}
	}
	
	public ERPDistencePM(){
		hasPeriod = false;
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap= new HashMap<>();
		minPredictValuesMap= new HashMap<>();
		maxPredictValuesMap= new HashMap<>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public ERPDistencePM(PMparam pmParam){
		hasPeriod = false;
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap= new HashMap<>();
		minPredictValuesMap= new HashMap<>();
		maxPredictValuesMap= new HashMap<>();
		existPeriod=new ArrayList<Integer>();
		this.threshold=pmParam.getThreshold();
		this.longestPeriod=pmParam.getLongestPeriod();
	}
	public ERPDistencePM(double threshold,int longestPeriod){
		hasPeriod = false;
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap= new HashMap<>();
		minPredictValuesMap= new HashMap<>();
		maxPredictValuesMap= new HashMap<>();
		existPeriod=new ArrayList<Integer>();
		this.threshold = threshold;
		this.longestPeriod = longestPeriod;
	}
	public void predictPeriod(){
		int numItems=di.getLength();
		if(numItems==0){
			return;
		}
		int maxPeriod = (numItems/2>longestPeriod)?longestPeriod:(numItems/2);
		if(di.isAllDataIsDouble()){
			List<String> seq=new ArrayList<String>();
			for(int i=0;i<numItems;i++){
				seq.add((Double.parseDouble(di.getData().get(i)))+"");
			}

			generateManHatonEntroy(seq,numItems);
			seq.clear();
			for(int i=0;i<numItems;i++){
				seq.add((Double.parseDouble(oriDi.getData().get(i)))+"");
			}
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
		int maxPeriod = (numItems/2>longestPeriod)?longestPeriod:(numItems/2);
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
		int maxPeriod = (numItems/2>longestPeriod)?longestPeriod:(numItems/2);
		int period=1;
		entropies=new Double[maxPeriod];
		List<String> standardList=new ArrayList<String>();
		ArrayList<String> seqList=new ArrayList<String>();
		double Entropy;
		while((period+1)<= maxPeriod){
			period++;	//周期递加
			standardList.clear();
			Entropy=0.0;
			//generateStandardList(seq, period, standardList);
			for(int i=0;i<period;i++){
				seqList.clear();
				for(int j=0;j<numItems/period;j++){
					seqList.add(seq.get(j*period+i));
				}
				//double en=MHTDistance(standardList, seqList);
				double en=VarianceOfList(seqList);
				Entropy+=en;
			}
			double diff=Entropy;
			entropies[period-1]=diff;
			//System.out.println("period "+period+"'s diff is "+diff);
		}
	}
	
	private double MHTDistance(List<String> seqX,List<String>seqY){
		double distance=0;
		for(int i=0;i<seqX.size();i++){
			distance+=Math.abs(Double.parseDouble(seqX.get(i))-Double.parseDouble(seqY.get(i)));
		}
		return distance;
	}
	
	private double VarianceOfList(List<String> list){
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(String item:list){
			statistics.addValue(Double.parseDouble(item));
		}
		return statistics.getStandardDeviation();
	}
	
	/**
	 * 返回两条序列的ERP距离
	 * @param seqX 第一条序列
	 * @param seqY 第二条序列
	 * @param matrix 存储已经计算完成的数据 
	 * @param xSize ySize 充当数组指针
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
	
	private void isPeriodExist(int maxPeriod,String item,List<String>seq){
		itemsInPeriod=new DataItems();
		existPeriod=new ArrayList<Integer>();
		predictValuesMap= new HashMap<>();
		Map<Integer, Double> ratio=new HashMap<Integer, Double>();
		hasPeriod=false;
		for(int i=1;i<maxPeriod;i++){
			if(isPeriod(entropies, i+1)){
				if(i==1){
					ratio.put(2, entropies[2]/entropies[1]);
				}else if(i==entropies.length-1){
					ratio.put(i+1, entropies[i-1]/entropies[i]);
				}else{
					ratio.put(i+1, Math.min(entropies[i-1]/entropies[i],entropies[i+1]/entropies[i]));
				}
				hasPeriod=true;
				existPeriod.add(i+1);
				Double[] predictValues= new Double[i + 1];
				Double[] minPredictValues= new Double[i + 1];
				Double[] maxPredictValues= new Double[i + 1];
				for(int index=0;index<=i;index++){
					predictValues[index]=0.0;
					minPredictValues[index]=Double.MAX_VALUE;
					maxPredictValues[index]=Double.MIN_VALUE;
				}
				int con = di.getLength() - di.getLength()%(i+1);	// 最后一个不完整周期不计入
				for(int j=0;j< con;j++){
					double value=Double.parseDouble(seq.get(j));
					predictValues[j%(i+1)]+=value;
					if(minPredictValues[j%(i+1)]>value){
						minPredictValues[j%(i+1)]=value;
					}
					if(maxPredictValues[j%(i+1)]<value){
						maxPredictValues[j%(i+1)]=value;
					}
				}
				for(int j=0;j<(i+1);j++){
					predictValues[j]/=(di.getLength()/(i+1));
				}
				predictValuesMap.put((i+1), predictValues);
				minPredictValuesMap.put((i+1),minPredictValues);
				maxPredictValuesMap.put((i+1),maxPredictValues);
			}
		}
		double ratios=0;
		int possiPeriod=0;
		for(Integer key:ratio.keySet()){
			if(ratio.get(key)>ratios){
				ratios=ratio.get(key);
				possiPeriod=key;
			}
		}
		predictPeriod=possiPeriod;
		confidence=ratios;        //该检测周期的置信度
		
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<minEntropy){
				minEntropy=entropies[i];
			}
		}
		if(hasPeriod){
			itemsInPeriod=new DataItems();
			minItemsInPeriod=new DataItems();
			maxItemsInPeriod=new DataItems();
			Double[] predictValues=predictValuesMap.get(predictPeriod);
			Double[] minPredictValues=minPredictValuesMap.get(predictPeriod);
			Double[] maxPredictValues=maxPredictValuesMap.get(predictPeriod);
			if(predictValues==null){
				predictValues= new Double[predictPeriod];
				minPredictValues= new Double[predictPeriod];
				maxPredictValues= new Double[predictPeriod];
				for(int index=0;index<predictPeriod;index++){
					predictValues[index]=0.0;
					minPredictValues[index]=Double.MAX_VALUE;
					maxPredictValues[index]=Double.MIN_VALUE;
				}
				for(int j=0;j<di.getLength();j++){
					double value=Double.parseDouble(seq.get(j));
					predictValues[j%(predictPeriod)]+=value;
					if(minPredictValues[j%(predictPeriod)]>value){
						minPredictValues[j%(predictPeriod)]=value;
					}
					if(maxPredictValues[j%(predictPeriod)]<value){
						maxPredictValues[j%(predictPeriod)]=value;
					}
				}
				for(int j=0;j<(predictPeriod);j++){
					predictValues[j]/=(di.getLength()/(predictPeriod));
				}
				predictValuesMap.put(predictPeriod, predictValues);
				minPredictValuesMap.put(predictPeriod, minPredictValues);
				maxPredictValuesMap.put(predictPeriod, maxPredictValues);
			}
			
			for(int i=0;i<possiPeriod;i++){
				itemsInPeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
				minItemsInPeriod.add1Data(di.getTime().get(i),minPredictValues[i]+"");
				maxItemsInPeriod.add1Data(di.getTime().get(i),maxPredictValues[i]+"");
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
	
	private boolean maxThanNeighbor(Double[] Entropies,int index,boolean isnext,int origin){
		boolean isMaxThanNeighbor=false;
		
		
		if(index==2){
			if(Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*threshold){
				isMaxThanNeighbor=true;
			}
		}else if(index==Entropies.length){
			if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*threshold){
				isMaxThanNeighbor=true;
			}
		}else{
			if(isnext){
				if(origin-index==1){
					if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*threshold)
						isMaxThanNeighbor=true;
				}else if(origin-index==-1){
					if(Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*threshold)
						isMaxThanNeighbor=true;
				}
			}else if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*threshold&&
            		Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*threshold){
            	isMaxThanNeighbor=true;
			}
		}
		return isMaxThanNeighbor;
	}
	
	private boolean isPeriod(Double[] Entropies,int index){

		if(maxThanNeighbor(Entropies, index,false,index)){
			if(nextPeriod(Entropies, index)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	private boolean nextPeriod(Double[] Entropies,int index){
		int i=index;
		boolean period=true;
		int num=0;
		while(i<=Entropies.length&&num<4){
			if(i==2){
				if(!(maxThanNeighbor(Entropies, i,false,i)||maxThanNeighbor(Entropies, i+1,true,i))){
					period=false;
					break;
				}
			}else if(i==(Entropies.length)){
				
				if(!(maxThanNeighbor(Entropies, i,false,i)||maxThanNeighbor(Entropies, i-1,true,i))){
					period=false;
					break;
				}
			}else{
				if(!(maxThanNeighbor(Entropies, i,false,i)||maxThanNeighbor(Entropies, i-1,true,i)||
						maxThanNeighbor(Entropies, i+1,true,i))){
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
			predictValuesMapOfNonNumDataItems= new HashMap<>();
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
	public Map<String, Map<Integer, Double[]>> getPredictValuesMapOfNonNumDataItems() {
		return predictValuesMapOfNonNumDataItems;
	}
	public void setPredictValuesMapOfNonNumDataItems(
			Map<String, Map<Integer, Double[]>> predictValuesMapOfNonNumDataItems) {
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
	
	public DataItems getMinItemsInPeriod() {
		return minItemsInPeriod;
	}

	public void setMinItemsInPeriod(DataItems minItemsInPeriod) {
		this.minItemsInPeriod = minItemsInPeriod;
	}

	public DataItems getMaxItemsInPeriod() {
		return maxItemsInPeriod;
	}

	public void setMaxItemsInPeriod(DataItems maxItemsInPeriod) {
		this.maxItemsInPeriod = maxItemsInPeriod;
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
	public void setOriginDataItems(DataItems dataItems) {
		this.oriDi=dataItems;
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
	
	/*public int[] getPreidctValues(){
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
	}*/
	
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

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
}
