package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	
	public ERPDistencePM(){
		hasPeriod = false;	
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public ERPDistencePM(Double threshold){
		hasPeriod = false;	
		predictPeriod=1;		
		this.threshold=threshold;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public void predictPeriod(){
		int numItems=di.getLength();
		if(numItems==0){
			return;
		}
		List<String> seqX=new ArrayList<String>();
		List<String> seqY=new ArrayList<String>();
		int[][] ErpDistMatrix;

		for(int i=0;i<numItems;i++){
			seqX.add((int)Double.parseDouble(di.getData().get(i))+"");
	    	seqY.add((int)Double.parseDouble(di.getData().get(i))+"");
		}
		setDistMatrix(seqX);
		int maxPeriod = numItems/2;
		int period=1;
		seqX.remove(numItems-1);
		seqY.remove(0);
		entropies=new Double[maxPeriod];
		while((period+1)<= maxPeriod){
			period++;	//周期递加
			seqX.remove(seqX.size()-1);
			seqY.remove(0);
			ErpDistMatrix=new int[numItems-period][numItems-period];
			int seqDistance=ERPDistance(seqX, seqY, seqX.size()-1, seqY.size()-1, ErpDistMatrix, period);
			double diff=(seqDistance*1.0)/(numItems-period);
			entropies[period-1]=diff;
			System.out.println("period "+period+"'s diff is "+diff);
		}
		isPeriodExist(maxPeriod);
	}
	
	/**
	 * 设置序列中各元素间的ERP距离 对角线上的ERP距离设为无穷大
	 * @param items 序列
	 */
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
	
	/**
	 * 返回两条序列的ERP距离
	 * @param seqX 第一条序列
	 * @param seqY 第二条序列
	 * @param offset 第二条序列是平移offset个单位得到的
	 * @return ERP距离
	 */
	private int ERPDistance(List<String> seqX,List<String>seqY,int xSize,int ySize,int [][]matrix,int offset){
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
	
	private void isPeriodExist(int maxPeriod){
		int minPeriod=0;
		Double maxEntropy=Double.MAX_VALUE;
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<threshold){
				if(entropies[i]<maxEntropy){
					minPeriod=(i+1);
					maxEntropy=entropies[i];
				}
				hasPeriod=true;
				existPeriod.add(i+1);
				Integer[] predictValues=new Integer[i+1];
				for(int j=0;j<di.getLength();j++){
					predictValues[j%(i+1)]+=(int)Double.parseDouble(di.getData().get(j));
				}
				for(int j=0;j<(i+1);j++){
					predictValues[j]/=(di.getLength()/(i+1));
				}
				predictValuesMap.put((i+1), predictValues);
			}
		}
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<minEntropy){
				minEntropy=entropies[i];
			}
		}
		if(hasPeriod){
			itemsInPeriod=new DataItems();
			Integer[] predictValues=predictValuesMap.get(minPeriod);
			for(int i=0;i<minPeriod;i++){
				itemsInPeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
			}
		}
	}
	
	@Override
	public boolean hasPeriod() {
		return hasPeriod;
	}
	
	@Override
	public void setDataItems(DataItems dataItems) {
		this.di=dataItems;
	}
	
	@Override
	public int getPredictPeriod() {
		return predictPeriod;
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
