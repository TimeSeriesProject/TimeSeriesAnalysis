package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 创建读取股票数据类
 * @author wsc
 *
 */
public class TextUtils{
	private String textPath="./configs/stockprice.csv";
	private FileInputStream is=null;
	BufferedReader reader=null;
	
	public TextUtils(String path){
		this.textPath=path;
	}
	public TextUtils(){
		
	}
	
	
	
	public String getTextPath() {
		return textPath;
	}
	public void setTextPath(String textPath) {
		this.textPath = textPath;
	}
	public DataItems readInput(){
		DataItems dataItems=new DataItems();
		Calendar lastYear=Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line=null;
		try {
			if(is==null){
				is=new FileInputStream(new File(textPath));
			}
			if(reader==null){
				reader=new BufferedReader(new InputStreamReader(is));
			}
			reader.readLine();
			while((line=reader.readLine())!=null){
				String[] values=line.split(",");
				lastYear.add(Calendar.HOUR_OF_DAY, 1);
				dataItems.add1Data(lastYear.getTime(), values[1]);
			}
			reader.close();
			reader=null;
			is=null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
	}
	
	public String readByrow(){
		String line=null;
		try {
			if(is==null){
				is=new FileInputStream(new File(textPath));
			}
			if(reader==null){
				reader=new BufferedReader(new InputStreamReader(is));
			}
			line=reader.readLine();
			if(line==null){
				reader.close();
				reader=null;
				is=null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
}