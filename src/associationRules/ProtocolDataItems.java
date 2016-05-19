package associationRules;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

 /**
 * 存储协议名和数据
 * @author Administrator
 *
 */
public class ProtocolDataItems
{
	String protocolName = "";
	DataItems dataItems;
	public ProtocolDataItems(String name,DataItems data)
	{
		protocolName = name;
		dataItems = data;
	}
	public void setProtocolName(String name)
	{
		protocolName = name;
	}
	public void setDataItems(DataItems data)
	{
		dataItems = data;
	}
	public String getProtocolName(){
		return protocolName;
		
	}
	public DataItems getDataItems(){
		return dataItems;
	}
}