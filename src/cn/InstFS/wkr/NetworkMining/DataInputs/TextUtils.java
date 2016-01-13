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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


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
			writer.write("time,traffic\r\n");
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
	
	public DataItems reaPathDataItemFromFile(String FilePath){
		File file=new File(FilePath);
		DataItems dataItems=new DataItems();
		if(file.exists()){
			Map<String, Double> probMap=new HashMap<String, Double>();
			try {
				FileInputStream fis=new FileInputStream(file);
				BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
				String line=null;
				while((line=reader.readLine())!=null){
					dataItems.data.add(line);
				}
				reader.close();
				fis.close();
			} catch (Exception e) {
				System.exit(0);
			}
			
			return dataItems;
		}else{
			return null;
		}
	}
	
	/**
	 * 将存储在文件中的路由器转移概率提取到Map中， 文件每一行为一条转移概率 ，
	 * 提取后的Map每个Entry为一条转移概率
	 * @param FilePath 文件地址
	 * @return 返回路由器转移概率Map 格式为 Map.Entry<"10,2",0.5> 表示P(2|10)=0.5
	 */
	public Map<String, Double> readMapFromFile(String FilePath){
		System.out.println(FilePath);
		File file=new File(FilePath);
		if(file.exists()){
			Map<String, Double> probMap=new HashMap<String, Double>();
			try {
				FileInputStream fis=new FileInputStream(file);
				BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
				String line=null;
				while((line=reader.readLine())!=null){
					String[] pathProb=line.split(":");
					probMap.put(pathProb[0], Double.parseDouble(pathProb[1]));
				}
				reader.close();
				fis.close();
			} catch (Exception e) {
				System.exit(0);
			}
			
			return probMap;
		}else{
			return null;
		}
	}
	
	/**
	 * 将路由器转移概率存储到文件中
	 * @param map 包含了路由器转移概率  形式为 Map.Entry<"10,2",0.5> 表示P(2|10)=0.5
	 * @param outFilePath 保存转移概率文件地址 每一条Map.Entry 在文件中为单独一行
	 */
	public void writeMap(Map<String, Double> map,String outFilePath){
		File outFile=new File(outFilePath);
		try {
			if(outFile.exists()){
				outFile.delete();
				outFile.createNewFile();
			}else{
				outFile.createNewFile();
			}
			
			FileOutputStream fos=new FileOutputStream(outFile);
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(fos));
			StringBuilder sb=new StringBuilder();
			Iterator<Entry<String, Double>>iterator=map.entrySet().iterator();
			while(iterator.hasNext()){
				sb.delete(0, sb.length());
				Entry<String, Double> entry=iterator.next();
				sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\r\n");
				writer.write(sb.toString());
			}
			fos.flush();
			writer.flush();
			fos.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将List<List<>>写入到文件中每个List作为一列 
	 * @param lists 要写入到文将中的list<List<String>>
	 * @param outFilePath 写入文件的地址 
	 */
	public void writeLists(List<List<String>> lists,String outFilePath){
		File outFile=new File(outFilePath);
		try {
			if(outFile.exists()){
				outFile.delete();
				outFile.createNewFile();
			}else{
				outFile.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(outFile);
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(fos));
			StringBuilder sb=new StringBuilder();
			int ListLength=lists.get(0).size();
			int listWidth=lists.size();
			for(int i=0;i<ListLength;i++){
				sb.delete(0, sb.length());
				for(int j=0;j<listWidth;j++){
					sb.append(",").append(lists.get(j).get(i));
				}
				sb.append("\r\n");
				sb.deleteCharAt(0);
				writer.write(sb.toString());
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