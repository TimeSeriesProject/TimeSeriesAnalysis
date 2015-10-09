package cn.InstFS.wkr.NetworkMining.DataInputs;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Properties;

import oracle.sql.CLOB;


public class OracleUtils {
	public static String configFile = "./configs/DB_CONFIG.xml";
	
	public static String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static String DB_URL = "jdbc:oracle:thin:@128.0.0.103:1521/ORCL";
	public static String DB_TABLE = "事件1";
	public static String DB_USER = "rdfusr";
	public static String DB_PASS = "wangshen";
	Connection conn = null;
	/**
	 * 尝试读取配置。如果没有配置文件，则将默认配置保存为配置文件
	 */
	public OracleUtils() {
		Field []fields = OracleUtils.class.getFields();
		
		Properties props = new Properties();
		File f = new File(configFile);
		if (!f.exists()){
			new File(f.getParent()).mkdirs();
			// save configs
			for (Field field : fields){
				if (!field.getName().equals("configFile"))
					try {
						props.put(field.getName(), field.get(this));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
			}
			try {
				props.storeToXML(new FileOutputStream(f), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{	// load configs
			try {
				props.loadFromXML(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Enumeration names =  props.propertyNames();
			while(names.hasMoreElements()){
				String name = names.nextElement().toString();
				Field field = null;
				try {
					field = OracleUtils.class.getField(name);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				if (field != null)
					try {
						field.set(this, props.get(name));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
			}	
		}
	}
	public void setDB_TABLE(String db_table){
		this.DB_TABLE = db_table;
	}
	public void setConnParams(String DB_URL, String DB_TABLE, String DB_USER, String DB_PASS){
		this.DB_URL = DB_URL;
		this.DB_TABLE = DB_TABLE.toUpperCase();
		this.DB_USER = DB_USER;
		this.DB_PASS = DB_PASS;
	}
	public boolean tryConnect(){
		if (openConn()){
			closeConn();
			return true;
		}
		return false;
	}
	public static boolean tryConnectStatic(String DB_URL, String DB_TABLE, String DB_USER, String DB_PASS){
		OracleUtils ou = new OracleUtils();
		ou.setConnParams(DB_URL, DB_TABLE, DB_USER, DB_PASS);
		return ou.tryConnect();
	}
	public boolean openConn(){
		try {
			Class.forName(DB_DRIVER);
			if (conn == null || conn.isClosed())
				conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);				
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public boolean isOpen(){
		if (conn == null)
			return false;
		try {
			return !conn.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}
	public void closeConn(){
		try {
			if (conn != null && !conn.isClosed())
				conn.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn = null;
	}
	private void createTable(String DB_TABLE){
//		try {
//			Statement stmt = conn.createStatement();
//			stmt.executeUpdate("CREATE TABLE "+DB_TABLE+" (" +
//					MailElements.OracleSqlStringForCreateTable + ")");
//			stmt.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}		
	}
	public ResultSet sqlQuery(String str){
		openConn();
		ResultSet rs = null;
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(str);
//			stmt.close();
		} catch (SQLException e) {
//			e.printStackTrace();
//			CommonUtils.writeLog(e.getLocalizedMessage());
		}
		return rs;		
	}
	public int sqlUpdate(String str){
		openConn();
		int rs = -1;		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeUpdate(str);	
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
//			CommonUtils.writeLog(e.getLocalizedMessage());
		}
		return rs;		
	}
	/**
	 * 将一个字符串经过一定的处理，使得可以在SQL语句中使用。其实只需要将一个单引号变成两个单引号
	 * @param str1
	 * @return
	 */
	public static String toOracleStr(String str1){
		String ret = str1;
		ret = ret.replace("'", "''");
		return "'"+ret+"'";
	}
//	@Override
//	public void saveMail(MailElements me) {
//		if (conn == null){
//			if(!openConn()){
//				return;
//			}
//		}
//		try {
//			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ALL_TABLES WHERE TABLE_NAME='"+DB_TABLE+"'");
//			ResultSet rs = ps.executeQuery();
//			if (!rs.next()){	// 需要创建表
//				createTable(DB_TABLE);
//			}else
//				while(rs.next());
//			rs.close();
//			ps.close();
//			conn.setAutoCommit(true);
//			String sql = me.getOracleStringForInsertRecord(DB_TABLE);
////			Statement stmt = conn.createStatement();
////			stmt.executeUpdate(sql);
//			CLOB clob = CLOB.createTemporary(conn, true, 1);
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			
//			clob = CLOB.createTemporary(conn, true, 1);
//			clob.setString(1, me.toString(me.receivers));
//			stmt.setClob(1, clob);
//			
//			clob = CLOB.createTemporary(conn, true, 1);
//			clob.setString(1, me.toString(me.content));
//			stmt.setClob(2, clob);
//			
//			clob = CLOB.createTemporary(conn, true, 1);
//			clob.setString(1, me.toString(me.attachments));
//			stmt.setClob(3, clob);
//			
//			clob = CLOB.createTemporary(conn, true, 1);
//			clob.setString(1, me.toString(me.headers));
//			stmt.setClob(4, clob);
//			
//			clob = CLOB.createTemporary(conn, true, 1);
//			clob.setString(1, me.toString(me.videoType));
//			stmt.setClob(5, clob);
//			if (stmt.executeUpdate() > 0){
////				System.out.println("添加记录成功！");
//			}
//			stmt.close();
//			
//		
//		} catch (SQLException e) {
//			System.out.println(e.getLocalizedMessage());
//		}
//	}

}
