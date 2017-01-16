package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.GlobalConfig;

public class Logger {
	
	static String loggerPath = null;
	static FileWriter writer;
	public static final Logger INSTANCE = new Logger();
	public Logger(){
		
		loggerPath = GlobalConfig.getInstance().getLoggerPath();
		File file = new File(loggerPath);

		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			writer = new FileWriter(new File(loggerPath+File.separator+System.currentTimeMillis()+".txt"));
		} catch (IOException e) {

			System.out.println("日志存储路径出错。。。。。。");
			e.printStackTrace();
		}
	}
	public static void log(String content){
		
		try {
			SimpleDateFormat df = new SimpleDateFormat("MMM-dd HH:mm:ss", Locale.ENGLISH);//设置日期格式
			writer.write(df.format(new Date()) +" "+content+"\n");
			writer.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public static void log(String key,String value){
		
		try {
			SimpleDateFormat df = new SimpleDateFormat("MMM-dd HH:mm:ss", Locale.ENGLISH);
			writer.write(df.format(new Date()) +" "+key+":"+value+"\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void log(Map<Object, Object> map){
		
		try {
			SimpleDateFormat df = new SimpleDateFormat("MMM-dd HH:mm:ss", Locale.ENGLISH);
			for(Object obj : map.keySet()){
				writer.write(df.format(new Date()) +" "+obj+":"+map.get(obj)+"\n");
			}
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
