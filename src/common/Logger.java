package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.GlobalConfig;

public class Logger {
	
	static String loggerPath = null;
	static FileWriter writer;
	public static final Logger INSTANCE = new Logger();
	public Logger(){
		
		loggerPath = GlobalConfig.getInstance().getLoggerPath();
		try {
			writer = new FileWriter(new File(loggerPath+File.separator+System.currentTimeMillis()+".txt"));
		} catch (IOException e) {

			System.out.println("日志存储路径出错。。。。。。");
			e.printStackTrace();
		}
	}
	public static void log(String content){
		
		try {
			writer.write(content+"\n");
			writer.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public static void log(String key,String value){
		
		try {
			writer.write(key+":"+value+"\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		
		Logger.log("xyz");
		Logger.log("xyz","aaa");
	}
}
