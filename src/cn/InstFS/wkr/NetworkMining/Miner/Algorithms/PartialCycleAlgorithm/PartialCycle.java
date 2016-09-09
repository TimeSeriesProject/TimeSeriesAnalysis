package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.Pair;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;

public class PartialCycle {

	private DataItems dataItems = new DataItems();
	private MinerResults results;
	private int minCycleLen=24;
	public class partialCycleSegment
	{
		int start;
		int end;
		int cycleLen;
		double avgDis;
		public partialCycleSegment(int start,int end,int cycleLen,double avgDis)
		{
			this.start		=start;
			this.end		=end;
			this.cycleLen	=cycleLen;
			this.avgDis		=avgDis;
		}
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public int getCycleLen() {
			return cycleLen;
		}
		public void setCycleLen(int cycleLen) {
			this.cycleLen = cycleLen;
		}
		public double getAvgDis() {
			return avgDis;
		}
		public void setAvgDis(double avgDis) {
			this.avgDis = avgDis;
		}
		@Override
		public String toString()
		{
			return "("+start+","+end+","+cycleLen+","+avgDis+")";
		}
		
	}
	public DataItems getDataItems() {
		return dataItems;
	}
	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
	}
	public PartialCycle(MinerResults results)
	{
		this.results =results;
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
		 for(int i=(int)(result.length*0.2);i<result.length;i++)
		 {
			 result[i]=new Complex(0,0);
			// result[i+result.length/2]=new Complex(0,0);
		 }
		 System.out.println("result "+result.length);
		 Complex [] denoised = fft.transform(result, TransformType.INVERSE);
		 List<String> newData =new ArrayList<String>();
		 for(int i=0;i<oldSize;i++)
		 {
			 newData.add(String.valueOf(denoised[i].getReal()));
		 }
		 dataItems.setData(newData);
	}
	private boolean isSimilar(double array[],double avg,int start,int len)
	{
		double partialAvg =0;
		double sum=0;
		for(int i=start;i<start+2*len;i++)
		{
			sum+=array[i];
		}
		partialAvg =sum/(2*len);
		int simBestNum=0;
		int simSubNum=0;
		int simSubSubNum=0;
		for(int i=start;i<start+len;i++)
		{
			if(i+len>=array.length)
				break;
			double dif = Math.abs((array[i]-array[i+len]));
			double  relativeError = 2*dif/(array[i]+array[i+len]);
			if((dif/partialAvg<0.1||relativeError<0.1 )/*&&dif/avg<0.8*/)
			{
				simBestNum++;
			}
			if((relativeError<0.2 )||dif/partialAvg<0.2)
			{
				simSubNum++;
			}
			if(relativeError<0.5||dif/partialAvg<0.5)
				simSubSubNum++;
		}
		if(1.0*simBestNum/len>0.60 &&1.0*simSubNum/len>0.80&&1.0*simSubSubNum>0.9)
			return true;
		return false;
	}
	private double calDis(double array[],double avg,int start,int len)
	{
		double sum=0;
		double dif=0;
		for(int i=start;i<start+len;i++)
		{
			if(i+len>=array.length)
				dif = Math.abs(array[i]);
			else
				dif = Math.abs((array[i]-array[i+len]));
			sum+=dif;
		}
		return sum/len;
	}
	private boolean isCross(partialCycleSegment seg1,partialCycleSegment seg2)
	{
		
		Pair<Integer,Integer> pair1,pair2;
		pair1 = new Pair<Integer,Integer>(seg1.getStart(),seg1.getEnd());
		pair2 = new Pair<Integer,Integer>(seg2.getStart(),seg2.getEnd());
		double right =Math.max(1.0*pair1.getSecond(),1.0*pair2.getSecond());
		int len1 = pair1.getSecond()-pair1.getFirst();
		int len2 = pair2.getSecond()-pair2.getFirst();
		if(len1<len2*0.8||len1>len2*1.25)
			return false;
		int minLen = Math.min(len1, len2);
		if(pair1.getFirst()<pair2.getFirst())
		{
			if((pair1.getSecond()-pair2.getFirst())>0.8*minLen)
			{
				return true;
			}
			else return false;
		}
		else
		{
			if((pair2.getSecond()-pair1.getFirst())>0.8*minLen)
			{
				return true;
			}
			else
				return false;
		}
		
	}
	public void run()
	{
		if(dataItems.getLength()==0)
			return;
	//	FourierFilter();
		List<String> data =dataItems.getData();
		int size = data.size();
		double original[] = new double[size];
		for(int i=0;i<size;i++)
			original[i]=Double.parseDouble(data.get(i));
		
		int maxCycleLen = dataItems.getLength()/4;
		double avg=0;
		double sum=0;
		//计算平均误差
		for(int i=0;i<size-1;i++)
		{
			sum+=Math.abs(original[i+1]-original[i]);
		}
		avg=sum/size;
		int cycleLen;
		int start=0;
		int num=0;
		double disSum=0;  //计算曼哈顿距离和
//		ArrayList<Pair<Integer,Integer>> cyclePos = new ArrayList<Pair<Integer,Integer>>();
//		ArrayList<Integer> cycleLenList = new ArrayList<Integer>();
//		ArrayList<Double> avgDis = new ArrayList<Double>();
		ArrayList<partialCycleSegment>partialCycleSegmentList = new ArrayList<partialCycleSegment>();
		for(cycleLen= minCycleLen;cycleLen<maxCycleLen;cycleLen++)
		{
			start=0;
			num=1;
			disSum=0;
			int j=0;
			
			for(;j<size&&j+2*cycleLen<=size;j+=cycleLen)
			{
				if(isSimilar(original,avg,j,cycleLen))
				{
					num++;
					disSum+=calDis(original,avg,j,cycleLen);
				}
				else
				{
					if(num>2)
					{
//						cyclePos.add(new Pair<Integer,Integer>(start,j+cycleLen-1));
//						cycleLenList.add(cycleLen);
//						avgDis.add(disSum/num);
						partialCycleSegment seg = new partialCycleSegment(start,j+cycleLen-1,cycleLen,disSum/num);
						partialCycleSegmentList.add(seg);
					}
					start=j+cycleLen;
					num=1;
					disSum=0;
				}
			}
			if(num>2)
			{
				partialCycleSegment seg = new partialCycleSegment(start,j+cycleLen-1,cycleLen,disSum/num);
				partialCycleSegmentList.add(seg);
			}
		}
		ArrayList<Integer> flag = new ArrayList<Integer>();
		for(int i=0;i<partialCycleSegmentList.size();i++)
			flag.add(0);
		for(int i=0;i<partialCycleSegmentList.size();i++)
		{
			
			for(int j=0;j<partialCycleSegmentList.size();j++)
			{
				if(i==j)
					continue;
				if(isCross(partialCycleSegmentList.get(i),partialCycleSegmentList.get(j)))
				{
					if(partialCycleSegmentList.get(i).getAvgDis()>partialCycleSegmentList.get(j).getAvgDis())
					flag.set(i,1);
					else
						flag.set(j, 1);
				}
			}
		
		}
		ArrayList<partialCycleSegment>tmppartialCycleSegmentList = new ArrayList<partialCycleSegment>();
		for(int i =0;i<flag.size();i++)
		{
			if(flag.get(i)!=1)
			{
//				results.getRetPartialCycle().getPartialCyclePos().add(cyclePos.get(i));
//				results.getRetPartialCycle().getCycleLengthList().add(cycleLenList.get(i));
				tmppartialCycleSegmentList.add(partialCycleSegmentList.get(i));
			}
		}
		System.out.println("over");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		DataItems dataItems= new DataItems();
//		//MinerResults mr = new MinerResults(null);
		PartialCycle pc = new PartialCycle(null);
//		pc.run();
	//	pc.FourierFilter();
		partialCycleSegment seg1 = pc.new partialCycleSegment(0,1811,453,5.480684326710817);
		partialCycleSegment seg2 = pc.new partialCycleSegment(0,1815,454,6.476321585903083);
	    boolean is = pc.isCross(seg1,seg2);
	    System.out.println(is);
	}

}
