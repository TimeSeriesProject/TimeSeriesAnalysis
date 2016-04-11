
package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;

/**
 * 
 * @author chenwei
 *
 */
public class MergeSegment
{
	
	class MergeSegmentNode
	{
		int index=0;
		int ptindex[] = new int[3];
		MergeSegmentNode left =null;
		MergeSegmentNode right =null;
		double error =0.0;
	}
	double rate =1;
	int size=0;
	MergeSegmentNode[] nodes;
	DataItem[] dataItemArray;
	void buildHeap()
	{
		size= dataItemArray.length-2;
		nodes= new MergeSegmentNode[size+1];
		for(int i=1;i<=size;i++)
			nodes[i] = new MergeSegmentNode();
		for(int i=1;i<=size;i++)
		{
			for(int j=0;j<3;j++)
			{
//				System.out.println("j"+nodes[i].ptindex.length);
				nodes[i].ptindex[j]=i-1+j;
				
			}
			updateerror(nodes[i]);
			nodes[i].index=i;
			if(i>1)
				nodes[i].left=nodes[i-1];
			if(i<size)
				nodes[i].right=nodes[i+1];
		}
		for(int i= size/2;i>=1;i--)
			fixdown(nodes[i]);
	}
	void extractMin()
	{
		if(size<=0)
			return ;
		MergeSegmentNode x =nodes[1];
		nodes[1]=nodes[size];
		nodes[1].index=1;
		size--;
		fixdown(nodes[1]);
		MergeSegmentNode left =x.left;
		MergeSegmentNode right =x.right;
		if(left!=null)
		{
			left.ptindex[2]=x.ptindex[2];
			left.right=x.right;
//			System.out.println("god"+x.index);
//			System.out.println("godend"+dataItemArray[left.ptindex[1]]);
			update(left);
			
			
		}
		if(right!=null)
		{
			right.ptindex[0]=x.ptindex[0];
			right.left=x.left;
			update(right);
		}
		dataItemArray[x.ptindex[1]]=null;
		
	}
	void fixup(MergeSegmentNode x)
	{
		int index =x.index;
		while(index/2>=1&&nodes[index/2].error>nodes[index].error)
		{
			/**
			 * 和父亲结点交互位置，同时index属性要和下标一致
			 */
			MergeSegmentNode t=nodes[index/2];
			nodes[index/2]=nodes[index];
			nodes[index]=t;
			nodes[index/2].index=index/2;
			nodes[index].index=index;
			index/=2;
		}
	}
	void fixdown(MergeSegmentNode x)
	{
		int index=x.index;
		while((index*2<=size&&nodes[index].error>nodes[2*index].error)||(index*2+1<=size&&nodes[index].error>nodes[2*index+1].error))
		{
			int cindex=2*index;
			if(index*2<=size&&nodes[index].error>nodes[2*index].error) //可省略
				cindex=2*index;
			if(index*2+1<=size&&nodes[2*index+1].error<nodes[2*index].error)
				cindex=2*index+1;
			MergeSegmentNode t=nodes[index];
			nodes[index]=nodes[cindex];
			nodes[cindex]=t;
			nodes[index].index=index;
			nodes[cindex].index=cindex;
			index=cindex;
		}
	}
	void updateerror(MergeSegmentNode node)
	{
		double x[] =new double[3];
		double y[] =new double[3];
		for(int i=0;i<3;i++)
		{
//			System.out.println("try"+node.ptindex[i]);
			y[i]=Double.valueOf(dataItemArray[node.ptindex[i]].getData());
			x[i]=dataItemArray[node.ptindex[i]].getTime().getTime();
//			System.out.println("try"+node.ptindex[i]);
		}
		
		node.error= Math.abs( y[1] - ( (x[1]-x[0])/(x[2]-x[0])*(y[2]-y[0]) + y[0]) );
	}
	void update(MergeSegmentNode node)
	{ 
		double perror=node.error;
		updateerror(node);
		if(node.error<perror)
			fixup(node);
		else if(node.error>perror)
			fixdown(node);
		
	}
	/**
	 * 利用堆进行线段合并，每次找出最小代价合并，并更新堆
	 * @param dataItems
	 * @param rate
	 */
	public MergeSegment(DataItems dataItems,double rate)
	{
		dataItemArray= new DataItem[dataItems.getLength()];
		this.rate=rate;
//		System.out.println("orgin");
		
		for(int i=0;i<dataItems.getLength();i++)
		{
			dataItemArray[i]=dataItems.getElementAt(i);
//			System.out.print(dataItemArray[i].getData());
//			if(i<dataItems.getLength()-1)
//				System.out.print(",");
		}
//		System.out.println();
		for(int i=0;i<dataItems.getLength();i++)
		{
			dataItemArray[i]=dataItems.getElementAt(i);
//			System.out.print(dataItemArray[i].getTime().getTime()/1000/3600);
//			if(i<dataItems.getLength()-1)
//				System.out.print(",");
		}
		buildHeap();
		while(size>dataItemArray.length*rate)
		{
			extractMin();
		}
//		System.out.println();
	}
	/**
	 * 获取合并过后的点
	 * @return
	 */
	public DataItems getMergedDataItems()
	{
		DataItems dataItems = new DataItems();
		for(int i=0;i<dataItemArray.length;i++)
		{
			if(dataItemArray[i]!=null)
			{
				dataItems.add1Data(dataItemArray[i]);
			}
		}
		return dataItems;
	}
	public ArrayList<Pattern> getPatterns()
	{
		ArrayList<Pattern> patList = new ArrayList<Pattern>();
		if(dataItemArray.length==0)
			return patList;
		
		int pre = -1;
		double maxSpan=Double.MIN_VALUE,maxSlope=Double.MIN_VALUE,maxAverage=Double.MIN_VALUE;  //存储最大时间间隔，最大流量变化的绝对值
		double minSpan=Double.MAX_VALUE,minSlope=Double.MAX_VALUE,minAverage=Double.MAX_VALUE;
		for(int i=0;i<dataItemArray.length;i++)
		{
			if(dataItemArray[i]!=null)
			{
				
//				System.out.print(dataItemArray[i].getData()+",");
				if(pre!=-1)
				{
					double x1,x2,y1,y2;
					x1=dataItemArray[pre].getTime().getTime();
					y1=Double.valueOf(dataItemArray[pre].getData());
					x2=dataItemArray[i].getTime().getTime();
					y2=Double.valueOf(dataItemArray[i].getData());
					
					Pattern pat =new Pattern();
					pat.setAverage((y1+y2)/2);
					pat.setSpan(Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)));
					pat.setSlope(Math.atan((y2-y1)/(x2-x1)));
					pat.setStart(pre);;
					pat.setEnd(i);
					patList.add(pat);
					if(pat.getAverage()>maxAverage)
						maxAverage =pat.getAverage();
					if(pat.getAverage()<minAverage)
						minAverage =pat.getAverage();
					if(pat.getSpan()>maxSpan)
						maxSpan = pat.getSpan();
					if(pat.getSpan()<minSpan)
						minSpan = pat.getSpan();
					if(pat.getSlope()>maxSlope)
						maxSlope =pat.getSlope();
					if(pat.getSlope()<minSlope)
						minSlope =pat.getSlope();
					
				}
				pre= i;
			}
		}
		
		for(int i=0;i<patList.size();i++)
		{
			Pattern pat = patList.get(i);
			pat.setAverage((pat.getAverage()-minAverage)/(maxAverage-minAverage));
			pat.setSlope((pat.getSlope()-minSlope)/(maxSlope-minSlope));
			pat.setSpan((pat.getSpan()-minSpan)/(maxSpan-minSpan));
			patList.set(i,pat);
//			System.out.println(seg.slope+",");
		}
		return patList;
	}
	/**
	 * 得到合并后的线段，并作min-max归一化
	 * @return
	 */
	public ArrayList<Segment> getSegmentList()
	{
		ArrayList<Segment> segList = new ArrayList<Segment>();
		if(dataItemArray.length==0)
			return segList;
		
		int pre = -1;
		double maxLength=Double.MIN_VALUE,maxSlope=Double.MIN_VALUE,maxCentery=Double.MIN_VALUE;  //存储最大时间间隔，最大流量变化的绝对值
		double minLength=Double.MAX_VALUE,minSlope=Double.MAX_VALUE,minCentery=Double.MAX_VALUE;
//		System.out.println("result");
		for(int i=0;i<dataItemArray.length;i++)
		{
			if(dataItemArray[i]!=null)
			{
				
//				System.out.print(dataItemArray[i].getData()+",");
				if(pre!=-1)
				{
					double x1,x2,y1,y2;
					x1=dataItemArray[pre].getTime().getTime()/1000;
					y1=Double.valueOf(dataItemArray[pre].getData());
					x2=dataItemArray[i].getTime().getTime()/1000;
					y2=Double.valueOf(dataItemArray[i].getData());
					
					Segment seg =new Segment();
					seg.setCentery((y1+y2)/2);
					seg.setLength(Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)));
					seg.setSlope(Math.atan((y2-y1)/(x2-x1)));
					
					seg.setStartTime(dataItemArray[pre].getTime());
					seg.setEndTime(dataItemArray[i].getTime());
					seg.getPointList().add(pre);
					seg.getPointList().add(i);
					segList.add(seg);
					if(seg.centery>maxCentery)
						maxCentery =seg.centery;
					if(seg.centery<minCentery)
						minCentery =seg.centery;
					if(seg.length>maxLength)
						maxLength = seg.length;
					if(seg.length<minLength)
						minLength = seg.length;
					if(seg.slope>maxSlope)
						maxSlope =seg.slope;
					if(seg.slope<minSlope)
						minSlope =seg.slope;
					
				}
				pre= i;
			}
		}
//		System.out.println();
		for(int i=0;i<dataItemArray.length;i++)
		{
			if(dataItemArray[i]!=null)
			{
//				System.out.print(dataItemArray[i].getTime().getTime()/3600/1000+",");
			}
		}
//		System.out.println();
//		System.out.println("god"+minSlope+" "+maxSlope);
		
		for(int i=0;i<segList.size();i++)
		{
			Segment seg = segList.get(i);
			seg.setCentery((seg.getCentery()-minCentery)/(maxCentery-minCentery));
//			seg.setSlope(Math.atan((seg.getSlope()-minSlope)/(maxSlope-minSlope));
			if(seg.slope>0)
				seg.slope+=30*2*Math.PI/360;
			else
				seg.slope-=30*2*Math.PI/360;
			
			seg.setLength((seg.getLength()-minLength)/(maxLength-minLength));
			segList.set(i,seg);
//			System.out.println(seg.slope+",");
		}
		return segList;
	}
	
}