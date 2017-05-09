package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;



/**
 * his class implements the Dynamic Time Warping algorithm
 * given two sequences
 * <pre>
 *   X = x1, x2,..., xi,..., xn
 *   Y = y1, y2,..., yj,..., ym	    
 */
public class DTW {
	
	DataItems ORIdi = new DataItems();
	DataItems PMdi = new DataItems();
	int len;
	private double[] seq1;
	private double[] seq2;
	private int[][] warpingPath;//存储路径
	       
	private int n;
	private int m;
	private int K;
	       
	private double warpingDistance;
	       
	/**
	 * Constructor
	 *
	 * @param query         
	 * @param templete     
	*/	  
	public DTW(double[] a1,double[] a2){
		this.seq1 = a1;
		this.seq2 = a2;
		n = seq1.length;       
		m = seq2.length;
		K = 1;
		               
		warpingPath = new int[n + m][2];        // max(n, m) <= K < n + m
		warpingDistance = 0.0;
		               
		this.compute();

	}
	public DTW(DataItems di,DataItems PMdi){
		this.ORIdi = di;
	    this.PMdi = PMdi;
	    len = PMdi.getLength();
	    seq1 = changeToArray(ORIdi);
	    seq2 = changeToArray(PMdi);
	    n = seq1.length;       
		m = seq2.length;
		K = 1;
		               
		warpingPath = new int[n + m][2];        // max(n, m) <= K < n + m
		warpingDistance = 0.0;
		               
		this.compute();
	}
	/**
	 * 把DataItems数据转换为数组模式
	 * 下标从1开始
	 */
	public double[] changeToArray(DataItems di){
	    double[] ret = new double[len];
	    for(int i=0;i<di.getLength();i++){
	    	double data = Double.parseDouble(di.getData().get(i));
	    	ret[i] = data;
	    }
	    return ret;
	}
	public void compute() {
	    double accumulatedDistance = 0.0;
	               
	    double[][] d = new double[n][m];        // local distances
	    double[][] D = new double[n][m];        // global distances
	               
	    for (int i = 0; i < n; i++) {
	       for (int j = 0; j < m; j++) {
	           d[i][j] = distanceBetween(seq1[i], seq2[j]);
	       }
	   }
	               
	   D[0][0] = d[0][0];
	               
	   for (int i = 1; i < n; i++) {
	       D[i][0] = d[i][0] + D[i - 1][0];
	   }
	   for (int j = 1; j < m; j++) {
	       D[0][j] = d[0][j] + D[0][j - 1];
	   }
	               
	   for (int i = 1; i < n; i++) {
	      for (int j = 1; j < m; j++) {
	          accumulatedDistance = Math.min(Math.min(D[i-1][j], D[i-1][j-1]), D[i][j-1]);
	          accumulatedDistance += d[i][j];
	          D[i][j] = accumulatedDistance;
	      }
	   }
	   accumulatedDistance = D[n - 1][m - 1];
	
	   int i = n - 1;
	   int j = m - 1;
	   int minIndex = 1;
	       
	   warpingPath[K - 1][0] = i;
	   warpingPath[K - 1][1] = j;
	               
	   while ((i + j) != 0) {
		   if (i == 0) {
			   j -= 1;
		   } else if (j == 0) {
			   i -= 1;
		   } else {        // i != 0 && j != 0
			   double[] array = { D[i - 1][j], D[i][j - 1], D[i - 1][j - 1] };
			   minIndex = this.getIndexOfMinimum(array);
			   if (minIndex == 0) {
				   i -= 1;
			   } else if (minIndex == 1) {
				   j -= 1;
			   } else if (minIndex == 2) {
				   i -= 1;
				   j -= 1;
			   }
		   } // end else
		   K++;
		   warpingPath[K - 1][0] = i;
		   warpingPath[K - 1][1] = j;
	   } // end while
	   warpingDistance = accumulatedDistance / K;
	               
	   this.reversePath(warpingPath);
	}
	       
	/**
	 * Changes the order of the warping path (increasing order)
	 *
	 * @param path  the warping path in reverse order
	 */
	protected void reversePath(int[][] path) {
		int[][] newPath = new int[K][2];
	    for (int i = 0; i < K; i++) {
	    	for (int j = 0; j < 2; j++) {
	    		newPath[i][j] = path[K - i - 1][j];
	    	}
	    }
	    warpingPath = newPath;
	}
	/**
	 * Returns the warping distance
	 *
	 * @return
	 */
	public double getDistance() {
	     return warpingDistance;
	}
	       
	/**
	 * Computes a distance between two points
	 *
	 * @param p1    the point 1
	 * @param p2    the point 2
	 * @return              the distance between two points
	 */
	protected double distanceBetween(double p1, double p2) {
	    return (p1 - p2) * (p1 - p2);
	}
	
	/**
	 * Finds the index of the minimum element from the given array
	 *
	 * @param array         the array containing numeric values
	 * @return              the min value among elements
	 */
	protected int getIndexOfMinimum(double[] array) {
		int index = 0;
	    double val = array[0];
	
	    for (int i = 1; i < array.length; i++) {
	        if (array[i] < val) {
	            val = array[i];
	            index = i;
	        }
	    }
	    return index;
	}
	
	/**
	 *  Returns a string that displays the warping distance and path
	 */
	public String toString() {
		String retVal = "Warping Distance: " + warpingDistance + "\n";
	    retVal += "Warping Path: {";
	    for (int i = 0; i < K; i++) {
	         retVal += "(" + warpingPath[i][0] + ", " +warpingPath[i][1] + ")";
	         retVal += (i == K - 1) ? "}" : ", ";
	                       
	    }
	    return retVal;
	}
	
	
	public int[][] getWarpingPath() {
		return warpingPath;
	}
	public void setWarpingPath(int[][] warpingPath) {
		this.warpingPath = warpingPath;
	}
	public double getWarpingDistance() {
		return warpingDistance;
	}
	public void setWarpingDistance(double warpingDistance) {
		this.warpingDistance = warpingDistance;
	}
	/**
	 * Tests this class
	 *
	 * @param args  ignored
	 */
	public static void main(String[] args) {
	    double[] n1 = {1,1,1,10,2,3};
	    double[] n2 = {1,1,1,2,10,3};
	    DTW dtw = new DTW(n1, n2);
	    System.out.println(dtw);
	}
} 

/*import java.util.ArrayList;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class DTW {
	DataItems ORIdi = new DataItems();
	DataItems PMdi = new DataItems();
    int len;
    double[] arr1;//原始数据
    double[] arr2;//周期数据
    double[][] distance;
    double[][] dtw;
    List<Integer[]> path = new ArrayList<Integer[]>();//存储路径
	public DTW(DataItems di,DataItems PMdi){
		this.ORIdi = di;
		this.PMdi = PMdi;
		len = PMdi.getLength();
		arr1 = changeToArray(ORIdi);
		arr2 = changeToArray(PMdi);
	}
	public DTW(double[] arr1,double[] arr2){
		this.arr1 = arr1;
		this.arr2 = arr2;
	}
	*//**
	 * 把DataItems数据转换为数组模式
	 * 下标从1开始
	 * *//*
	public double[] changeToArray(DataItems di){
		double[] ret = new double[len+1];
		ret[0]=0;
		for(int i=0;i<di.getLength();i++){
			double data = Double.parseDouble(di.getData().get(i));
			ret[i+1] = data;
		}
		return ret;
	}
	*//**
	 * 计算dtw矩阵
	 * 下标从1开始
	 * *//*
	public void comDTW(){
		//初始化DTW矩阵
		dtw = new double[arr1.length][arr2.length];
		//初始化distance矩阵
		initDistance();
		//开始计算dtw矩阵
		dtw[0][0]=0;
		for(int i=1;i<arr1.length;i++){
			for(int j=1;j<arr2.length;j++){
				dtw[i][j] = Math.min(Math.min(dtw[i][j-1]+distance[i][j], dtw[i-1][j])+distance[i][j], dtw[i][j]+2*distance[i][j]);	
				
			}
		}
		for(int i=1;i<arr1.length;i++){
			for(int j=1;j<arr2.length;j++){
				System.out.print(dtw[i][j]+" ");
			}
			System.out.println();
		}
	}
	*//**
	 * 初始化distance矩阵
	 * 下标从1开始
	 * *//*
	public void initDistance(){
		distance = new double[arr1.length][arr2.length];
		for(int i=1;i<arr1.length;i++){
			for(int j=1;j<arr2.length;j++){				
				distance[i][j] = Math.abs(arr1[i]-arr2[j]);
			}
		}
	}
	
	*//**用于测试*//*
	public static void main(String[] args){
		double[] arr1 = {0,1,2,3,4,0};
		double[] arr2 = {0,0,1,2,3,4};
		DTW dtw = new DTW(arr1, arr2);
		dtw.comDTW();
	}
}

*/