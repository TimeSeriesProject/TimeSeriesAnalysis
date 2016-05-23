package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;



public class SqlUtils{
	
	public static String configFileString="./configs/configuration.xml";
	
	//尝试读取配置文件，若配置文件没有配置，则置为默认值
	public static String DB_DIRVER="com.mysql.jdbc.Driver";
	public static String DB_URL="jdbc:mysql://localhost:3306/57suo?useUnicode=true&amp;characterEncoding=UTF-8";
	public static String DB_USER="root";
	public static String DB_PASSWD="dmlab";
	public static String DB_TABLE = "事件";
	
	private Connection connection=null;
	
	public SqlUtils(){
		/**
		 * 尝试读取配置文件，若配置文件不存在则读取默认值
		 */
		File configFile=new File(configFileString);
		Properties properties=new Properties();
		if(!configFile.exists()){
			new File(configFile.getParent()).mkdir();//创建config文件夹
			Field[] fields=SqlUtils.class.getFields();
			for(Field field:fields){
				if(!field.getName().equals("configFileString")){					
					try {
						properties.put(field.getName(), field.get(this));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}catch(IllegalArgumentException e){
						e.printStackTrace();
					}
					
				}
			}
			try {
				properties.storeToXML(new FileOutputStream(configFile), null);
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else{
			try {
				properties.loadFromXML(new FileInputStream(configFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}
			Enumeration<?> names=properties.propertyNames();
			while (names.hasMoreElements()) {
				String name=names.nextElement().toString();
				try {
					Field field=SqlUtils.class.getField(name);
					if(field!=null){
						field.set(this, properties.get(name));
					}
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}catch (IllegalAccessException  e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	public void setParams(String url,String user,String passwd){
		this.DB_URL=url;
		this.DB_USER=user;
		this.DB_PASSWD=passwd;
	}
	
	public Boolean tryConnect(){
		if(openConnect()){
			closeConnect();
			return true;
		}
		return false;
	}
	
	public Boolean isOpen(){
		if(connection==null){
			return  false;
		}
		try{
			return !connection.isClosed();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean openConnect(){
		try{
			Class.forName(DB_DIRVER);
			if(connection==null||connection.isClosed()){
				connection=DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
			}
			return true;
		}catch(ClassNotFoundException e){
			e.printStackTrace();
			return false;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public void closeConnect(){
		try {
			if(connection!=null&&!connection.isClosed()){
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet querySql(String sql){
		openConnect();
		ResultSet resultSet=null;
		try {
			Statement statement=connection.createStatement();
			resultSet=statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}
	
}