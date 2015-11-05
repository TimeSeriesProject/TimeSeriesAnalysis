package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.StatUtils;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.OracleUtils;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

/**
 * 棰勫鐞嗙被锛屽寘鎷細 1銆佽鍙栨暟鎹�2銆侀澶勭悊锛堝鐢ㄤ簬搴忓垪妯″紡鎸栨帢鐨勨�绂绘暎鍖栤�澶勭悊锛� */
public class SingleNodeTrafficInputs implements IReader {

	TaskElement task;
	OracleUtils conn;
	DataItems data;
	String whichIp = null;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	// private string node;
	public SingleNodeTrafficInputs(TaskElement task) {
		this.task = task;
		if (task.getDataSource().equals("DataBase")) {
			conn = new OracleUtils();
			if (!conn.tryConnect())
				System.out.println("鏁版嵁搴撴棤娉曡繛鎺ワ紒");
			// UtilsUI.showErrMsg("閿熸枻鎷疯彉閿熸枻鎷疯柟閿熸枻鎷烽敓鏂ゆ嫹妯遍敓锟�;
		}

	}
	public void setWhichIp(String ip){
		whichIp = ip;
	}
	public String getWhichIp(){
		return whichIp;
	}
	@Override  //娌℃湁鍙傛暟锛屽垯瀵规墍鏈夌殑缁撴灉杩涜棰勬祴
	public DataItems readInputByText() {

		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line = null;
		try {
			FileInputStream is = new FileInputStream(new File(task.getSourcePath()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			reader.readLine();
			while((line = reader.readLine()) != null)
			{
				String[] values = line.split(","); 
				
				String srcIp = values[1];
				String destIp = values[2];
				if(srcIp.compareTo(whichIp) == 0 || destIp.compareTo(whichIp) == 0)
				{
					lastYear.add(Calendar.HOUR_OF_DAY, 1);
					dataItems.add1Data(lastYear.getTime(), values[3]);
				}
			
				
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
	}

	@Override
	public DataItems readInputByText(String[] conditions) {

		String whichNode = conditions[0];   //寰楀埌闇�棰勬祴鐨刬P
		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line = null;
		try {
			FileInputStream is = new FileInputStream(new File(task.getSourcePath()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			reader.readLine();
			while((line = reader.readLine()) != null)
			{
				String[] values = line.split(","); 
				String srcIp = values[1];
				String destIp = values[2];
				if(srcIp.compareTo(whichIp) == 0 || destIp.compareTo(whichIp) == 0)
				{
					lastYear.add(Calendar.HOUR_OF_DAY, 1);
					dataItems.add1Data(lastYear.getTime(), values[3]);
				}
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
		
	}

	@Override
	public DataItems readInputBySql() {
		String sqlStr = "";   // 閺嶈宓佹导鐘插棘閺夆�娆㈡潻娑滎攽鐠囪褰囬弫鐗堝祦鎼存挷鑵戦惃鍕殶閹癸拷
		// String whichNode = condition;
		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		conn.closeConn();
		ResultSet rs = conn.sqlQuery(sqlStr); // 闇�鏍规嵁鏁版嵁搴撶殑琛ㄦ牸寮忕粰鍑鸿鍙栬鍙�		if (rs == null) {
			System.out.println("no data satisfied data ,please check...");
			return null;
		}
		ResultSetMetaData meta = null;
		int numRecords = 0;
		int condition_num=0;
		try {

			meta = rs.getMetaData();
			int numCols = meta.getColumnCount();
			data = new DataItems();
			while (rs.next()) {
				numRecords++;

				String time = rs.getString(1).trim();
				Date d = parseTime(time); // 灏嗘暟鍊兼椂闂磋浆鍖栦负鏍囧噯鏃堕棿鏍煎紡

				String srcIp = rs.getString(2); // source IP
				String destIp = rs.getString(3); // destination IP
				String traffic = rs.getString(4); // traffic

				if(srcIp.compareTo(whichIp) == 0 || destIp.compareTo(whichIp) == 0)
				{
					lastYear.add(Calendar.HOUR_OF_DAY, 1);
					dataItems.add1Data(lastYear.getTime(), traffic);
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("璇ヨ〃鎬昏褰曟暟锛� + numRecords + " 婊¤冻鏉′欢鐨勮褰曟暟鏈夛細"
				+ condition_num);
		// System.out.println("閿熸枻鎷峰彇閿熸枻鎷烽敓锟� + data.getLength() + "閿熸枻鎷烽敓鏂ゆ嫹褰曢敓鏂ゆ嫹");

		return data;
	}

	@Override
	public DataItems readInputBySql(String condition) {

		String sqlStr = condition; // sql璇彞闇�鏍规嵁瀹為檯鎯呭喌鐨勮〃杩涜涔﹀啓
		String whichNode = condition;
		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		conn.closeConn();
		ResultSet rs = conn.sqlQuery(sqlStr);
		if (rs == null) {
			System.out.println("no data satisfied data ,please check...");
			return null;
		}
		ResultSetMetaData meta = null;
		int numRecords = 0;
		int condition_num = 0;
		try {

			meta = rs.getMetaData();
			int numCols = meta.getColumnCount();
			data = new DataItems();
			while (rs.next()) {
				numRecords++;

				String time = rs.getString(1).trim();
				Date d = parseTime(time); // 灏嗘暟鍊兼椂闂磋浆鍖栦负鏍囧噯鏃堕棿鏍煎紡

				String srcIp = rs.getString(2); // source IP
				String destIp = rs.getString(3); // destination IP
				String traffic = rs.getString(4); // traffic

				if(srcIp.compareTo(whichIp) == 0 || destIp.compareTo(whichIp) == 0)
				{
					lastYear.add(Calendar.HOUR_OF_DAY, 1);
					dataItems.add1Data(lastYear.getTime(), traffic);
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("璇ヨ〃鎬昏褰曟暟锛� + numRecords + " 婊¤冻鏉′欢鐨勮褰曟暟鏈夛細"
				+ condition_num);
		// System.out.println("閿熸枻鎷峰彇閿熸枻鎷烽敓锟� + data.getLength() + "閿熸枻鎷烽敓鏂ゆ嫹褰曢敓鏂ゆ嫹");

		return data;
	}
	private Date parseTime(String timeStr) {
		int difLen = sdf.toPattern().length() - timeStr.length();
		StringBuilder sb = new StringBuilder();
		sb.append(timeStr);
		for (int i = 0; i < difLen; i++)
			sb.append("0");
		try {
			return sdf.parse(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
	
