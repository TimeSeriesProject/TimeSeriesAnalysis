package PropertyTest.OutlinesTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Precision;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.netbeans.CLIHandler.Args;
import org.omg.CORBA.PRIVATE_MEMBER;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsOM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class OutliersTest {
	String srcPath = null;
	String testDataPath = null;
	List<Integer> labels = new ArrayList<Integer>();
	MinerResultsOM retOM = new MinerResultsOM();
	String ip = null;
	Date startDate = new Date();
	String retPath = null;
	private double precision = 0;
	private double recall = 0;
	public OutliersTest(String srcPath,String testDataPath){
		this.srcPath = srcPath;
		this.testDataPath = testDataPath;
	}
	public OutliersTest(MinerResultsOM retOM,String ip,Date startDate){
		this.retOM = retOM;
		this.ip = ip;
		this.startDate = startDate;
		genLabels();
	}
	//对yahoo数据格式进行转换
	public void genTestData(){
		BufferedReader br = null;
		BufferedWriter bw = null;
		try{
			InputStream in = new FileInputStream(new File(srcPath));
			InputStreamReader inr = new InputStreamReader(in);
			br = new BufferedReader(inr);
			
			OutputStream os = new FileOutputStream(testDataPath);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
//			bw.write("time,"+"sip,"+"dip,"+"pro");
//			bw.newLine();
			String line = br.readLine();
			while((line = br.readLine()) != null){
				String[] temp = line.split(",");
				String time = temp[0];
				String value = temp[1];
				String label = temp[2];
				labels.add(Integer.parseInt(label));
				bw.write(time+",");
				bw.write("10.0.1.2,10.0.2.2,");
				bw.write("9:"+value+":1;");
				bw.write("sum:"+value+":1");
//				bw.write(label);
				bw.newLine();
			}
			bw.flush();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	//获取labels标签
	public void genLabels(){
		String srclabelpath = "D:/57Data/outlierTest/data/real_"+ ip +".csv";
		BufferedReader br = null;
		try{
			InputStream in = new FileInputStream(srclabelpath);
			InputStreamReader inr = new InputStreamReader(in);
			br = new BufferedReader(inr);
		
			String line = br.readLine();
			while((line = br.readLine()) != null){
				String[] temp = line.split(",");
				String label = temp[2];
				labels.add(Integer.parseInt(label));				
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	//计算异常检测的评价指标，准确率和召回率   点异常
	public void evaluatIndicator(){
		DataItems outliers = retOM.getOutlies();
		//获取算法异常点和标签异常的下标
		List<Integer> outlierIndex = new ArrayList<Integer>();
		List<Integer> labelsIndex = new ArrayList<Integer>();
		for(int i=0;i<outliers.getLength();i++){
			long diff = outliers.getTime().get(i).getTime()-startDate.getTime();
			long index = diff/(3600*1000);
			outlierIndex.add((int)index+1);
		}
		for(int i=0;i<labels.size();i++){
			if(labels.get(i)==1)
				labelsIndex.add(i);
		}
		//计算准确率和召回率
		double TP = 0;
		for(int i=0;i<labelsIndex.size();i++){
			int index = labelsIndex.get(i);
			for(int j=0;j<outlierIndex.size();j++){
				if(outlierIndex.get(j)==index){
					TP++;
					break;
				}
			}
		}
		if(labelsIndex.size()>0){
			precision = TP/outlierIndex.size();
		}
		if(outlierIndex.size()>0){
			recall = TP/labelsIndex.size();
		}		
	}
	//计算异常检测的评价指标，准确率和召回率   线段异常
	public void evaluatIndicator2(){
		List<DataItems> outliersSet = retOM.getOutlinesSet();
		List<List<Integer>> outIndexSet = new ArrayList<List<Integer>>();//异常线段中的点的下标
		List<Integer> labelsIndex = new ArrayList<Integer>();//正确的异常点的下标
		for(int i=0;i<outliersSet.size();i++){
			List<Integer> aline = new ArrayList<Integer>();
			DataItems outliers = outliersSet.get(i);
			for(int j=0;j<outliers.getLength();j++){
				long diff = outliers.getTime().get(j).getTime()-startDate.getTime();
				long index = diff/(3600*1000);
				aline.add((int)index+1);
			}
			outIndexSet.add(aline);
		}
		for(int i=0;i<labels.size();i++){
			if(labels.get(i)==1)
				labelsIndex.add(i);
		}
		
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();//每条线段包含多少个正确的异常点   <index,number>
		HashSet<Integer> TP = new HashSet<Integer>();
		for(int i=0;i<outIndexSet.size();i++){
			map.put(i, 0);//初始化map
			for(int j=0;j<labelsIndex.size();j++){
				if(outIndexSet.get(i).contains(labelsIndex.get(j))){
					TP.add(labelsIndex.get(j));
					int num = map.get(i);
					num = num+1;
					map.put(i, num);					
				}				
			}
		}
		
		//统计tureLine
		double tureLine = 0;//包含正确异常点的线段个数		
		for(int i=0;i<outIndexSet.size();i++){
			if(map.get(i)>0)
				tureLine += 1;
		}
		
		//计算准确率和召回率
		if(outIndexSet.size()>0){
			precision = tureLine/outIndexSet.size();
		}
		if(labelsIndex.size()>0){
			double a=TP.size();
			double b=labelsIndex.size();
			recall = a/b;
//			recall = TP.size()/labelsIndex.size();
		}		
	}
	 public static void appendWriteRet(String retPath,String ip,double precision,double recall) {
	       
	        BufferedWriter bw = null;
	        try {
	        	bw = new BufferedWriter(new OutputStreamWriter(
	        			new FileOutputStream(new File(retPath), true)));
	        	bw.write("数据"+ip+"--");
	        	bw.write("准确率:"+precision+",");
	        	bw.write("召回率:"+recall);
	        	bw.newLine();
	        	bw.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public static void main(String[] args){
		/*String rootPath = "F:/异常检测/yahoo网络时间序列异常数据/data";
		File fileDir = new File(rootPath);
		File[] fileList = fileDir.listFiles();*/
		for(int i=1;i<=70;i++){
			String srcPath = "D:/57Data/outlierTest/data/real_"+i+".csv";
			String testDataPath = "D:/57Data/outlierTest/traffic/"+i+"/1462032000000.txt"; 
			OutliersTest outlinesTest = new OutliersTest(srcPath, testDataPath);
			outlinesTest.genTestData();
		}
		
		System.out.println("数据转换结束");
	}
	
}
