package associationRules;

import java.util.Map;

public class ProtocolLineData {

	String protocolName = "";
	Map<Integer,Integer> data = null;
	public ProtocolLineData(String name,Map<Integer,Integer> map)
	{
		protocolName = name;
		data = map;
	}
	public void setName(String name)
	{
		protocolName = name;
	}
	public void setData(Map<Integer,Integer> d)
	{
		data = d;
	}
	public String getName(){
		return protocolName;
	}
	public Map<Integer,Integer> getData(){
		return data;
	}
}
