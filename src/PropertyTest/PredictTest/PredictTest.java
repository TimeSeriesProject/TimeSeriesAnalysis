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
	static int testsize=10;
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
	}//获得除去最后testsize个数据的原始数据
	public List<String> getTestRealData(DataItems dataItems){
		List<String> realtestData = new ArrayList<>();
		List<String> data = dataItems.getData();
		for (int i=(dataItems.getLength()-testsize); i<dataItems.getLength(); i++) {
			realtestData.add(data.get(i));//
		}
		return realtestData;
		
	}//获得原始数据的最后testsize个数据
	
	public static double predictTestMAPE(List<String> pdata,List<String> ttData){//pdata为预测值，ttdata为实际值
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
	}//相对误差平均值
	
	
	
	

	
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
	
	
	
	
	
	
	
	
	
	 public static void appendWriteRet(String retPath,String Ip,String protocal,String miningobject,double forcastresult1,double forcastresult2,boolean isHasPeriod) {
	        BufferedWriter bw = null;
	        DecimalFormat df=new DecimalFormat("0.0000");
	        try {
	        	bw = new BufferedWriter(new OutputStreamWriter(
	        			new FileOutputStream(new File(retPath), true)));
	        	bw.write(Ip+","+protocal+","+miningobject+","+df.format(forcastresult1)+","+df.format(forcastresult2)+",");
	        	if(isHasPeriod)
	        		bw.write("有");
	        	else
	        		bw.write("无");
	        	bw.newLine();
	        	bw.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 public static void resultWrite(List<String> pdata,List<String> ttData,
			                        String taskRange,String taskProtocol,String taskMiningObject,boolean isHasPeriod){
		 
		    String retPath = "IP间predictTest-default.cvs";
		    double forcastresult1=0.0;
		    double forcastresult2=0.0;
			forcastresult1=predictTestMAPE(pdata, ttData);//相对误差绝对值平均值-测试开始并返回测试结果
			forcastresult2=predictTestSIU(pdata,ttData);
			appendWriteRet(retPath, taskRange,taskProtocol,taskMiningObject, forcastresult1,forcastresult2,isHasPeriod);//测试结果写进文件
			 /*
			        	bw.write("预测任务名字"+","+"平均误差(MD)"+","+"绝对值平均误差(MAD)"+","+"相对误差平均值(MPE)"+","+"相对误差绝对值平均值(MAPE)"+","+
			        			"变异数(Variance)"+","+"均方根误差(RMS)"+","+"相对均方根误差(RRMS)"+","+"赛尔U系数(SIU)");
			        }*/
	 }

}
