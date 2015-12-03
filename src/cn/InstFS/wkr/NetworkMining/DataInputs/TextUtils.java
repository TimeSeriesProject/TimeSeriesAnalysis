package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	
	public void writeOutput(DataItems dataItems){
		File file=new File(textPath);
		try {
			if(file.exists()){
				file.delete();
				file.createNewFile();
			}else{
				file.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(file);
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(fos));
			writer.write("time,data\r\n");
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<dataItems.getLength();i++){
				DataItem item=dataItems.getElementAt(i);
				sb.append(item.getTime()).append(",").append(item.getData()).append("\r\n");
				writer.write(sb.toString());
				sb.delete(0, sb.length());
			}
			fos.flush();
			writer.flush();
			fos.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void writeOutput(List<String> outList ,String testFilePath,String trainFilePath){
		File trianFile=new File(trainFilePath);
		File testFile=new File(testFilePath);
		try {
			if(trianFile.exists()){
				trianFile.delete();
				trianFile.createNewFile();
			}else{
				trianFile.createNewFile();
			}
			if(testFile.exists()){
				testFile.delete();
				testFile.createNewFile();
			}else{
				testFile.createNewFile();
			}
			
			int length=outList.size();
			int trainLen=(int)(length*0.9);
			
			FileOutputStream fos=new FileOutputStream(trianFile);
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(fos));
			int attrLength=outList.get(0).split(",").length-1;
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<attrLength;i++){
				sb.append("attr").append(i).append(",");
			}
			sb.append("value").append("\r\n");
			writer.write(sb.toString());
			sb.delete(0, sb.length());
			for(int i=0;i<trainLen;i++){
				sb.append(outList.get(i)).append("\r\n");
				writer.write(sb.toString());
				sb.delete(0, sb.length());
			}
			fos.flush();
			writer.flush();
			fos.close();
			writer.close();
			
			
			fos=new FileOutputStream(testFile);
			writer=new BufferedWriter(new OutputStreamWriter(fos));
			for(int i=0;i<attrLength;i++){
				sb.append("attr").append(i).append(",");
			}
			sb.append("value").append("\r\n");
			writer.write(sb.toString());
			sb.delete(0, sb.length());
			for(int i=trainLen;i<length;i++){
				sb.append(outList.get(i)).append("\r\n");
				writer.write(sb.toString());
				sb.delete(0, sb.length());
			}
			fos.flush();
			writer.flush();
			fos.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}