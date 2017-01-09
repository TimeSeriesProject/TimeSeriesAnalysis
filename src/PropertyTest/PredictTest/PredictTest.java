package PropertyTest.PredictTest;
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
import java.text.DecimalFormat;

import org.apache.commons.math3.util.Precision;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.netbeans.CLIHandler.Args;
import org.omg.CORBA.PRIVATE_MEMBER;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsOM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class PredictTest {
	static int testsize=8;
	public PredictTest(){
	
	}
	
	public DataItems getTestpredictData(DataItems dataItems){
		List<Date> time = dataItems.getTime();
		List<String> data = dataItems.getData();
		List<Date> tTime = new ArrayList<>();
		List<String> tData = new ArrayList<>();
		for (int i=0; i<(dataItems.getLength()-testsize); i++) {
			tTime.add(time.get(i));
			tData.add(data.get(i));
		}
		DataItems tDataItems = new DataItems();
		tDataItems.setData(tData);
		tDataItems.setTime(tTime);
		return tDataItems;
	}//获得除去最后8个数据的原始数据
	public List<String> getTestRealData(DataItems dataItems){
		List<String> realtestData = new ArrayList<>();
		List<String> data = dataItems.getData();
		for (int i=(dataItems.getLength()-testsize); i<dataItems.getLength(); i++) {
			realtestData.add(data.get(i));//
		}
		return realtestData;
		
	}//获得原始数据的最后8个数据
	
	
	
	
	
	
	public static double predictTestMD(List<String> pdata,List<String> ttData){
		 double forcastresult=0.0;
		 System.out.println(pdata.size());
		 if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			forcastresult+=(Double.valueOf(ttData.get(i))-Double.valueOf(pdata.get(i)));
		 }
		 forcastresult=forcastresult/pdata.size();
		 }
		 return forcastresult;
	}//平均误差
	
	
	public static double predictTestMAD(List<String> pdata,List<String> ttData){
		 double forcastresult=0.0;
		 if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			forcastresult+=Math.abs(Double.valueOf(ttData.get(i))-Double.valueOf(pdata.get(i)));
		 }
		 forcastresult=forcastresult/pdata.size();
		 }
		 return forcastresult;
	}//绝对值平均误差
	
	
	public static double predictTestMPE(List<String> pdata,List<String> ttData){
		 double forcastresult=0.0;
		 if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			 if(Double.valueOf(ttData.get(i))!=0.0)
			forcastresult+=(Double.valueOf(ttData.get(i))-Double.valueOf(pdata.get(i)))/Double.valueOf(ttData.get(i));
			 else
				 forcastresult+=(Double.valueOf(ttData.get(i))-Double.valueOf(pdata.get(i)))/0.0001;//如果实际数据为0，就除以0.0001
				 
		 }
		 forcastresult=forcastresult/pdata.size();
		 }
		 return forcastresult;
	}//相对误差平均值
	
	public static double predictTestMAPE(List<String> pdata,List<String> ttData){
		 double forcastresult=0.0;
		 if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			 if(Double.valueOf(ttData.get(i))!=0.0)
			 forcastresult+=Math.abs((Double.valueOf(ttData.get(i))-Double.valueOf(pdata.get(i)))/Double.valueOf(ttData.get(i)));
			 else
			 forcastresult+=Math.abs((Double.valueOf(ttData.get(i))-Double.valueOf(pdata.get(i)))/0.0001);
		 }
		 forcastresult=forcastresult/pdata.size();
		 }
		 return forcastresult;
	}//相对误差绝对值平均值
	
	

	
	public static double predictTestVariance(List<String> pdata,List<String> ttData){
		 double forcastresult=0.0;
		 if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			forcastresult+=(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)))*
					(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)));
		 }
		 forcastresult=forcastresult/pdata.size();
		 }
		 return forcastresult;
		 
	}//变异数
	
	
	public static double predictTestRMS(List<String> pdata,List<String> ttData){
		 double forcastresult=0.0;
		 if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			forcastresult+=(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)))*
					(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)));
		 }
		 forcastresult=Math.sqrt((forcastresult)/pdata.size());
		 }
		 return forcastresult;
	}//均方根误差
	
	
	public static double predictTestRRMS(List<String> pdata,List<String> ttData){
		 double forcastresult=0.0;
		 if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			forcastresult+=(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i))/Double.valueOf(pdata.get(i)))*
					(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i))/Double.valueOf(pdata.get(i)));
		 }
		 forcastresult=Math.sqrt((forcastresult)/pdata.size());
		 }
		 return forcastresult;
	}//相对均方根误差
	
	
	public static double predictTestSIU(List<String> pdata,List<String> ttData){
		double x1=0.0;
		double x2=0.0; 
		double x3=0.0;
		double forcastresult=0.0;
		if(pdata.size()>0){
		 for(int i=0;i<testsize;i++){
			x1+=(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)))*
					(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)));
			x2+=Double.valueOf(ttData.get(i))*Double.valueOf(ttData.get(i));
			x3+=Double.valueOf(pdata.get(i))-Double.valueOf(pdata.get(i));
		 }
		 x1=Math.sqrt((x1)/pdata.size());
		 x2=Math.sqrt((x2)/pdata.size());
		 x3=Math.sqrt((x3)/pdata.size());
		 forcastresult=x1/(x2+x3);
		}
		 return forcastresult;
	}//赛尔u系数
	
	
	/*public static double predictTestCC(List<String> pdata,List<String> ttData){
		double x1=0.0;
		double x2=0.0; 
		double x3=0.0;
		double forcastresult=0.0;
		 for(int i=0;i<pdata.size();i++){
			x1+=(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)))*
					(Double.valueOf(pdata.get(i))-Double.valueOf(ttData.get(i)));
			x2+=Double.valueOf(ttData.get(i));
		 }
		 x2=x2/pdata.size();//实际数据的平均值
		 for(int i=0;i<pdata.size();i++){
			 x3+=(Double.valueOf(ttData.get(i))-x2)*(Double.valueOf(ttData.get(i))-x2);	
			 }
		 
		 forcastresult=Math.sqrt((1-(x1/x3)));
		 return forcastresult;
	}//相关系数*/
	
	
	
	
	
	
	
	 public static void appendWriteRet(String retPath,String Ip,String protocal,String miningobject,double[] forcastresult) {
	        BufferedWriter bw = null;
	        DecimalFormat df=new DecimalFormat("0.0000");
	        try {
	        	bw = new BufferedWriter(new OutputStreamWriter(
	        			new FileOutputStream(new File(retPath), true)));
	        	bw.write(Ip+","+protocal+","+miningobject);
	        	for(int i=0;i<forcastresult.length;i++)
	        		bw.write(","+df.format(forcastresult[i]));
	        	bw.newLine();
	        	bw.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 public static void resultWrite(List<String> pdata,List<String> ttData,
			                        String taskRange,String taskProtocol,String taskMiningObject){
		 
		    String retPath = "result/predictTest.cvs";
		    double[] forcastresult = null;
		    forcastresult=new double[testsize];
			forcastresult[0]=predictTestMD(pdata, ttData);
			forcastresult[1]=predictTestMAD(pdata, ttData);//绝对值平均误差-测试开始并返回测试结果
			forcastresult[2]=predictTestMPE(pdata, ttData);//相对误差平均值-测试开始并返回测试结果
			forcastresult[3]=predictTestMAPE(pdata, ttData);//相对误差绝对值平均值-测试开始并返回测试结果
			forcastresult[4]=predictTestVariance(pdata, ttData);//变异数-测试开始并返回测试结果
			forcastresult[5]=predictTestRMS(pdata, ttData);//均方根误差-测试开始并返回测试结果
			forcastresult[6]=predictTestRRMS(pdata, ttData);//相对均方根误差-测试开始并返回测试结果
			forcastresult[7]=predictTestSIU(pdata, ttData);//赛尔U系数-测试开始并返回测试结果
			appendWriteRet(retPath, taskRange,taskProtocol,taskMiningObject, forcastresult);//测试结果写进文件
			 /*
			        	bw.write("预测任务名字"+","+"平均误差(MD)"+","+"绝对值平均误差(MAD)"+","+"相对误差平均值(MPE)"+","+"相对误差绝对值平均值(MAPE)"+","+
			        			"变异数(Variance)"+","+"均方根误差(RMS)"+","+"相对均方根误差(RRMS)"+","+"赛尔U系数(SIU)");
			        }*/
	 }

}
