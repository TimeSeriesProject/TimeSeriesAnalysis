package cn.InstFS.wkr.NetworkMining.DataInputs;



import java.awt.List;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

 
import org.jfree.data.gantt.Task;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;


//import ec.tstoolkit.utilities.Comparator;
import java.util.Comparator;


class NodeandHops implements Comparable
{
	int Node;
	int Hops;
	public NodeandHops(int Node,int Hops)
	{
		this.Node	=	Node;
		this.Hops 	=	Hops;
	}
	public int compareTo(Object o) 
	{
		NodeandHops tmp=(NodeandHops) o;
	    int result=(Hops>tmp.Hops)?1:(Hops==tmp.Hops?0:-1);
        return result;
	}
}
class Link implements Comparable
{
	int start;
	int end;
	int time;
	public Link(int start,int end,int time)
	{
		this.start = start;
		this.end   = end;
		this.time  =time;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Link tmp = (Link) o;
		int result = (time>tmp.time)?1:(time==tmp.time?0:-1);
		return result;
	}
}
class Pair implements Comparable
{
	int first;
	int second;
	public Pair(int first,int second)
	{
		this.first = first;
		this.second = second;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Pair tmp =(Pair)o;
		int result = (first>tmp.first)?1:(first==tmp.first?0:-1);
		result = result==0?((second>tmp.second)?1:(second==tmp.second?0:-1)):result;
		return result;
	}
	
}
class TextReader
{
	private String path="";
	private InputStreamReader fr= null;//new InputStreamReader(new FileInputStream(path+"/"+fileName),encoding);
	private BufferedReader bfr=null;//new BufferedReader(fr);
	private String curLine = null;
	private String encoding = "UTF-8";
	public TextReader(String path)
	{
		this.path = path;
		try
		{
			fr= new InputStreamReader(new FileInputStream(path),encoding);
			bfr = new BufferedReader(fr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String readLine()
	{
		try
		{
			curLine=bfr.readLine();
			if(curLine==null)
				bfr.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		return curLine;
	}
}
public class CWNetworkReader  implements IReader{
   
    private ArrayList<Link> linkList = new ArrayList<Link> ();
    private TaskElement task = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		TaskElement task = new TaskElement();
		task.setSourcePath("");
		CWNetworkReader reader = new CWNetworkReader(task);
		//reader.readInputByText("Cluser");
		
	 }
	CWNetworkReader(TaskElement task)
	{
		this.task=task;
	}
	public DataItems readInputByText()
	{
		DataItems dataItems=null;
		return dataItems;
	}
	/**
	 * 计算簇系数
	 * @return
	 */
	private double calCluster(Map<Pair,Integer> matrix)
	{
		double result=0.0;
		Map<Integer,Set<Integer>> neighbourMap= new HashMap<Integer,Set<Integer>> ();
		/**
		 * 获得结点所有邻居
		 */
		for(Map.Entry<Pair, Integer> entry:matrix.entrySet())
		{
			int st = entry.getKey().first;
			int ed = entry.getKey().second;
			if(!neighbourMap.containsKey(st))
			{
				Set<Integer> set = new HashSet<Integer> ();
				neighbourMap.put(st, set);
			}
			if(!neighbourMap.containsKey(ed))
			{
				Set<Integer> set = new HashSet<Integer> ();
				neighbourMap.put(ed, set);
			}
			neighbourMap.get(st).add(ed);
			neighbourMap.get(ed).add(st);
		}
		/**
		 * 计算结点簇系数
		 */
		for(Map.Entry<Integer,Set<Integer>> entry:neighbourMap.entrySet())
		{
			int k = entry.getValue().size();
			int edgenum=0;
			Iterator<Integer> iter = entry.getValue().iterator();
			ArrayList<Integer> list = new ArrayList<Integer>();
			while(iter.hasNext())
			{
				list.add(iter.next());
			}
			for(int i = 0;i<list.size();i++)
			{
				for( int j = 0;j<list.size();j++)
				{
					if(matrix.get(new Pair(i,j)) != null||matrix.get(new Pair(j,i))!=null)
					{
						edgenum++;
					}
					
				}
			}
			result += 2.0*edgenum/(k*(k-1));
		}
		result/=neighbourMap.size();   //平均簇系数
		return result;
		
	}
	private Date second2Date(int second)
	{
		 Calendar cal = Calendar.getInstance();
		 cal.set(2015, 9, 1, 0, 0, 0);
		 cal.add(Calendar.SECOND,second);
		 Date date = cal.getTime();
		 return date;
	}
	private DataItems readClusterByText(String condition)
	{
		String path=task.getSourcePath();
		DataItems dataItems=new DataItems();

		File srcFiles=new File(path);
		try
		{
			for(int i=0;i<srcFiles.list().length;i++)
			{
				
				TextReader textReader = new TextReader(path+"/"+srcFiles.list()[i]);
				String curLine="";
				
				while((curLine=textReader.readLine())!=null)
				{
					
					Map<Integer,Double>	map = new HashMap<Integer,Double>();  //记录结点平均跳数
					//List<Map.Entry<Integer,Double>> mappingList = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
					ArrayList<Map.Entry<Integer,Double>> mappingList = null; 
				    mappingList = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet()); 
					
					String seg[]=curLine.split(",");
					/**
					 * 记录结点平均跳数。
					 */
					for(int j=5;j<seg.length;j++)
					{
						String str[] = seg[j].split(":");
						int node = Integer.valueOf(str[0]);
						int hops = Integer.valueOf(str[1]);
						if(map.containsKey(node))
						{
							map.put(node, (map.get(node)+hops)/2);
						}
						else
							map.put(node, 1.0*hops);
						

					}
					
					/**
					 * 结点排序
					 */
					mappingList = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet()); 
//					ArrayList <Integer>tmpList =new ArrayList<Integer>();
					Collections.sort(mappingList, new Comparator<Map.Entry<Integer,Double>>()
					{
						public int compare(Map.Entry<Integer,Double> mapping1,Map.Entry<Integer,Double> mapping2)
						{
							return mapping1.getValue().compareTo(mapping2.getValue());
						}	
					}); 
					
					/**
					 * 生成路径
					 */
					Iterator<Map.Entry<Integer,Double>> iter = mappingList.iterator();
					if(mappingList.size() > 1)
					{
						int preNode = iter.next().getKey();
						for(;iter.hasNext();)
						{
							int curNode =iter.next().getKey();
							
							preNode = curNode;
							linkList.add(new Link(preNode,curNode,Integer.valueOf(seg[0])));
						}
					}
				}
			}
			Collections.sort(linkList);
			int timeseg = Integer.valueOf(condition);
			int start	=	0;
			int end		=	365*24*60*60-1;	
			int presegnum=0;
			int cursegnum;
			Map<Pair,Integer> matrix =new TreeMap<Pair,Integer>();
			/**
			 * 每个时间段计算簇数，返回序列
			 */
			for(int i=0;i<linkList.size();i++)
			{
				 cursegnum = (linkList.get(i).time-start)/timeseg;
				 if(cursegnum>presegnum)
				 {
					 presegnum=cursegnum;
					 matrix = new TreeMap<Pair,Integer>();
					 
					 Date date = second2Date(timeseg*presegnum);
					 dataItems.add1Data(date, String.valueOf(calCluster(matrix)));
				 }
				 matrix.put(new Pair(linkList.get(i).start,linkList.get(i).end), 1);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		return dataItems;
	}
	@Override
	public DataItems readInputBySql() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public DataItems readInputBySql(String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public DataItems readInputByText(String[] condistions) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
