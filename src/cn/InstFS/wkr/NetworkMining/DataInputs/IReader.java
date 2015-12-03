package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.List;



public interface IReader {	
	//读取文本文件中的数据
	public DataItems readInputByText();
	//读取数据库中的数据
	public DataItems readInputBySql();
	
	/**
	 * 读取数据库中符合要求的数据  
	 * @param condition 为sql语句形式的数据过滤条件 如 "sip=='10.0.1.1' and dip=='10.0.1.2'"
	 * @return 符合要求的数据
	 */
	public DataItems readInputBySql(String condition);
	
	/**
	 * 读取文本文件中的符合要求的数据
	 * @param condistions 是数组形式的数据过滤条件 
	 * 如  conditions[0]为sip=='10.0.1.1'    conditions[1]为dip=='10.0.1.2'
	 * @return
	 */
	public DataItems readInputByText(String[] condistions);
}
