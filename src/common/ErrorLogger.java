package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.GlobalConfig;

public class ErrorLogger {
	
	static String loggerPath = null;
	static FileWriter writer;
	public static final ErrorLogger INSTANCE = new ErrorLogger();
	public ErrorLogger(){
		
		loggerPath = GlobalConfig.getInstance().getLoggerPath();
		try {
			writer = new FileWriter(new File(loggerPath+File.separator+"errorLog"+System.currentTimeMillis()+".txt"));
		} catch (IOException e) {

			System.out.println("日志存储路径出错。。。。。。");
			e.printStackTrace();
		}
	}
	public static synchronized void log(String content){
		
		try {
			writer.write(content+"\n");
			writer.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public static synchronized void log(String key,String value){
		
		try {
			writer.write(key+":"+value+"\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static synchronized void log(Map<Object, Object> map){
		
		try {
			for(Object obj : map.keySet()){
				writer.write(obj+":"+map.get(obj)+"\n");
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		
		ErrorLogger.log("xyz");
		ErrorLogger.log("xyz","aaa");
	}
}