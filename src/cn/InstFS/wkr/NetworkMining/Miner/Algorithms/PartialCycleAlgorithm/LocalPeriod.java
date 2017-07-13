package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PeriodAlgorithm.ERPDistencePM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPartialCycle;

/**
 * 生成局部周期，存储到MinerResultsPartialCycle中
 */

public class LocalPeriod{
	/**
	 * 待预测时序数据
	 */
	DataItems dataItems = new DataItems();//原始数据
	/**
	 * 挖掘结果
	 */
	private MinerResultsPartialCycle result = new MinerResultsPartialCycle();//挖掘结果
	HashMap<Integer,ArrayList<NodeSection>> partialCyclePos = new HashMap<Integer,ArrayList<NodeSection>>();//
	boolean hasPartialCycle = false;
	private double threshold;
	//	private int longestPeriod;
	private int minWindowSize = 30;
	private int minPeriod=20;
	private int maxPeriod=300;
	private int preSimNum=-1;
	private double stdev;  //序列标准差
	private double mean;
	private Set<Integer> cycleCandidate =new TreeSet<Integer>();
	public LocalPeriod(){}
	public LocalPeriod(DataItems di,double threshold,int maxPeriod){
		this();
		this.dataItems = di;
		this.threshold = threshold;
		this.maxPeriod = maxPeriod;
//		this.longestPeriod = longestPeriod;
		//FourierFilter();
		run();
	}

	public void FourierFilter()
	{

		List<String> data =dataItems.getData();
		int oldSize =data.size();
		//oldSize =3200;
//			if(data.size()==0)
//				return;
		int newSize = (int)Math.pow(2,(int) (Math.log(oldSize)/Math.log(2)));
		if(newSize<oldSize)
			newSize*=2;
		System.out.println(oldSize+" "+newSize);

		double original[] = new double[newSize];
		for(int i=0;i<newSize;i++)
		{
			if(i<oldSize)
			{
				original[i]=Double.parseDouble(data.get(i));
			}
			else
				original[i]=0;
		}
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] result = fft.transform(original, TransformType.FORWARD);
//		 for(int i=(int)(result.length*0.2);i<result.length;i++)
//		 {
//			 result[i]=new Complex(0,0);
//			// result[i+result.length/2]=new Complex(0,0);
//		 }
		class Node implements Comparable<Node>
		{
			int i;
			double v;
			Node(int i,double v)
			{
				this.i=i;
				this.v=v;
			}
			@Override
			public int compareTo(Node arg0) {
				// TODO Auto-generated method stub
				if(v<arg0.v)
					return -1;
				else if(v==arg0.v)
					return 0;
				else return 1;

			}

		}
		ArrayList<Node> list =new ArrayList<Node>();
		for(int i=0;i<oldSize;i++)
		{
			Node node =new Node(i,result[i].abs());
			list.add(node);
			//System.out.println("i "+i+" "+result[i].abs());

		}
		Collections.sort(list);
		int num=(int)(list.size()*0.03);
		if(num<minPeriod)
			num=minPeriod;
		if(num>list.size())
			num=list.size();
		for(int i=oldSize-num;i<oldSize;i++)  //获得候选周期
		{
			//System.out.println("i "+list.get(i).i+" "+list.get(i).v);
			if(list.get(i).i==0)
				continue;
			int tmp = result.length/list.get(i).i;
			if(tmp-1>=minPeriod)
				cycleCandidate.add(tmp-1);
			if(tmp>=minPeriod)
				cycleCandidate.add(tmp);
			if(tmp+1>=minPeriod)
				cycleCandidate.add(tmp+1);
		}
//		 System.out.println("result "+result.length);
//		 Complex [] denoised = fft.transform(result, TransformType.INVERSE);
//		 List<String> newData =new ArrayList<String>();
//		 for(int i=0;i<oldSize;i++)
//		 {
//			 newData.add(String.valueOf(denoised[i].getReal()));
//		 }
//		 dataItems.setData(newData);
	}
	/**
	 * 计算两段线段是否具有相似性，若相似，则认为该长度len可能为一个局部周期长度
	 * @param array 时间序列数据
	 * @param pre 需要判断的第一条线段的起始点
	 * @param start 需要判断的第二条线段的起始点
	 * @param len 周期长度
	 * @return 若两段线段是周期的，返回true，否则返回false
	 */
	private boolean isSimilar(double array[],int pre,int start,int len)
	{
		double partialAvg =0;
		double sum=0;
		for(int i=start;i<start+2*len;i++)
		{
			sum+=array[i];
		}
		partialAvg =sum/(2*len)+0.000000001;
		int simBestNum=0;
//		int simSubNum=0;
		int firstSimNum=0;
//		int firstSimSubNum=0;
		if(preSimNum==-1)
		{
			for(int i=start;i<start+len;i++)
			{
				if(i+len>=array.length)
					break;
				double dif = Math.abs((array[i]-array[i+len]));
				double  relativeError = 2*dif/(array[i]+array[i+len]+0.000001);
				double stderror= dif/(stdev+0.000001);
				if((relativeError<threshold &&stderror<1)/*&&dif/avg<0.8*/)
				{
					simBestNum++;
					if(i==start)
						firstSimNum=1;
				}
//				if((relativeError<0.8 ))
//				{
//					simSubNum++;
//					if(i==start)
//						firstSimSubNum=1;
//				}

			}
		}
		else
		{
			//计算首尾两点
			for(int i=start;i<start+len;i+=len-1)
			{
				if(i+len>=array.length)
					break;
				double dif = Math.abs((array[i]-array[i+len]));
				double  relativeError = 2*dif/(array[i]+array[i+len]+0.000001);
				double stderror= dif/(stdev+0.000001);
				if(relativeError<threshold &&stderror<1/*&&dif/avg<0.8*/)
				{

					if(i==start)
						firstSimNum=1;
					if(i==start+len-1)
						simBestNum++;
				}
//				if((relativeError<0.8 ))
//				{
//
//					if(i==start)
//						firstSimSubNum=1;
//					if(i==start+len-1)
//						simSubNum++;
//				}
			}

			simBestNum+=preSimNum;

		}
		preSimNum=simBestNum-firstSimNum;
		int headSimNum=0;
//		for(int i=0;i<len;i++)
//		{
//			if(start+len+i>=array.length)
//				break;
//			double dif =Math.abs(array[pre+i]-array[start+i+len]);
//			double  relativeError = 2*dif/(array[pre+i]+array[start+i+len]+0.000000001);
//			if(relativeError<0.2)
//				headSimNum++;
//		}
		if(1.0*simBestNum/len>0.80 )
		{

			return true;
		}
		return false;
	}

	/**
	 * 计算长度len是否为一个局部周期
	 * @param array 时间序列数据
	 * @param st 线段起始点
	 * @param ed 线段终止点
	 * @param len 周期长度
     * @param flag 是否在周期序列上
     */
	private void calPeriod(double array[],int st,int ed,int len,boolean flag[])
	{
		int pre=st;
		int num=1;
		int i;
		preSimNum=-1;
		for(i=st;i+2*len-1<=ed;)
		{
			if(isSimilar(array,pre,i,len))
			{
				num++;
				preSimNum=-1;
				i+=len;
				continue;
			}

			if(num==1)
			{
				pre=i+1;
				num=1;
				i++;
				continue;
			}

			preSimNum=-1;

			if(num<3)
			{
				pre=i+len;
				num=1;
				i+=len;
				continue;
			}
			for(int j=pre;j<=i+len-1;j++)
				flag[j]=true;
			if(!partialCyclePos.containsKey(len)){
				ArrayList<NodeSection> nodeList = new ArrayList<NodeSection>();
				NodeSection nodeSection = new NodeSection(pre, i+len-1);
				nodeList.add(nodeSection);
				partialCyclePos.put(len, nodeList);
				hasPartialCycle = true;
			}else{
				ArrayList<NodeSection> nodeList = new ArrayList<NodeSection>();
				nodeList = partialCyclePos.get(len);
				NodeSection nodeSection = new NodeSection(pre, i+len-1);
				nodeList.add(nodeSection);
				partialCyclePos.put(len, nodeList);
				hasPartialCycle = true;
			}
			pre=i+len;
			num=1;
			i+=len;
		}
		if(num>=2)
		{
			for(int j=pre;j<=i+len-1;j++)
				flag[j]=true;
			if(!partialCyclePos.containsKey(len)){
				ArrayList<NodeSection> nodeList = new ArrayList<NodeSection>();
				NodeSection nodeSection = new NodeSection(pre, i+len-1);
				nodeList.add(nodeSection);
				partialCyclePos.put(len, nodeList);
				hasPartialCycle = true;
			}else{
				ArrayList<NodeSection> nodeList = new ArrayList<NodeSection>();
				nodeList = partialCyclePos.get(len);
				NodeSection nodeSection = new NodeSection(pre, i+len-1);
				nodeList.add(nodeSection);
				partialCyclePos.put(len, nodeList);
				hasPartialCycle = true;
			}
		}
	}

	/**
	 * 生成局部周期序列，存储序列的位置和线段（起始点和终止点）
	 */
	public void run(){
		double array[]= new double [dataItems.getLength()];


		//归一化
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(int i=0;i<dataItems.getLength();i++)
		{
			statistics.addValue(Double.parseDouble(dataItems.getElementAt(i).getData()));
		}
		stdev=statistics.getStandardDeviation();
		mean=statistics.getMean();

		for(int i=0;i<dataItems.getLength();i++)
		{
			array[i]=(Double.parseDouble(dataItems.getElementAt(i).getData()));
		}

		ArrayList<NodeSection> sectionList = new ArrayList<NodeSection>();
		//	ArrayList<NodeSection> newSectionList = new ArrayList<NodeSection>();
		boolean [] flag = new boolean[dataItems.getLength()];
		for(int i=0;i<dataItems.getLength();i++)
			flag[i]=false;

		sectionList.add(new NodeSection(0,dataItems.getLength()-1));

		//Iterator<Integer> iter = cycleCandidate.iterator();
		for(int len=minPeriod;len<=maxPeriod;len++)
		{

//			newSectionList.clear();
			for(int i=0;i<sectionList.size();i++)
			{
				calPeriod(array,sectionList.get(i).begin,sectionList.get(i).end,len,flag);
			}
			int pre=-1;
			int i;
			sectionList.clear();
			for(i=0;i<dataItems.getLength();i++)
			{
				if(!flag[i])
				{
					if(pre==-1)
						pre=i;
				}
				else if(pre!=-1)
				{
					sectionList.add(new NodeSection(pre,i-1));
					pre=-1;
				}
			}
			if(pre!=-1)
				sectionList.add(new NodeSection(pre,i-1));
		}

	}
	public MinerResultsPartialCycle getResult() {
		result.setPartialCyclePos(partialCyclePos);
		result.setHasPartialCycle(hasPartialCycle);
		return result;

	}
	public int getMinWindowSize() {
		return minWindowSize;
	}
	public void setMinWindowSize(int minWindowSize) {
		this.minWindowSize = minWindowSize;
	}

}
