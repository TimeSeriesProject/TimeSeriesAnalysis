package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import oracle.net.aso.p;


public class LinePattern {
	private DataItems dataItems = new DataItems();     //时间序列 
	private TreeMap<Integer,Double> datas = new TreeMap<Integer, Double>();//原始数据
    private TreeMap<Integer, Pattern> linesMap = new TreeMap<Integer, Pattern>();  //拟合后的线段
	private List<Pattern> patterns = new ArrayList<Pattern>();       // 线段模式	
	private double mergerPrice = 0.05;      //合并代价阈值
	private double compressionRatio = 0.65;//压缩率
	public LinePattern(DataItems di,double mergerPrice){
		dataItems = di;
		this.mergerPrice = mergerPrice;
		run();
	}
	
	public void run(){
		datas = dataItemsConvertMap(dataItems);
		normalize(datas);//归一化
        System.out.println("数据归一化完毕！");
        initializeLinear();//两两连线
        System.out.println("线段两两拟合完毕！");
        mergeLinear();//合并线段
        System.out.println("合并线段完毕！mergerPrice="+mergerPrice);
        for(Map.Entry<Integer, Pattern> entry : linesMap.entrySet()){
        	patterns.add(entry.getValue());
        }
        comAngle(patterns);
        
    }
	/**
	 * 转换dataItems中data的数据格式为：
	 * Map(i,data(i))*/
	private TreeMap<Integer, Double> dataItemsConvertMap(DataItems di) {
		
		TreeMap<Integer,Double> sourceDatas = new TreeMap<Integer,Double>();
		
		 for(int i=0; i<di.getData().size(); i++){
	           
	          int key = i;
	          double value = Double.parseDouble(di.getData().get(i));
	          sourceDatas.put(key,value);
	     }
		return sourceDatas;
	}
	/**
     * 对原始数据进行归一化，采用MIN_MAX归一化方法
     * @param ls 原始数据
     */
	public void normalize(TreeMap<Integer,Double> ls){
        double min=Double.MAX_VALUE,max = Double.MIN_VALUE;
        for(int i : ls.keySet()){
            double value = ls.get(i);
            if(value < min)
            	min=value;
            if(value > max)
            	max=value;
        }
        for (int i : ls.keySet()){
            double value = ls.get(i);
            value = (value-min)/(max-min);
            ls.put(i,value);
        }
    }
    /**
     * 初始化线段表，两两之间进行连线，并用Linear表示
     */
    private void initializeLinear(){
        int len = datas.size();
        if(len<=1)
        	return;

        int firstTime = datas.firstKey();
        //for(int time : datas.keySet()){
        for(int time=0;time<datas.size()-1;time++){    
        	if(firstTime == time)
            	continue;
            double firstValue = datas.get(firstTime);
            double lastValue = datas.get(time);
            //double slope = (lastValue - firstValue)/(time - firstTime);//计算斜率，斜率大小随变量单位变化
            double height = lastValue - firstValue;
            double length = time - firstTime;
            Pattern pattern = new Pattern(firstTime,firstTime+1,time - firstTime,Math.atan2(height,length),firstValue);
            pattern.setHspan(lastValue-firstValue); 
            linesMap.put(firstTime,pattern);
            firstTime = time;
        }
    }
	/**
     * 计算合并代价
     * @param first
     * @param last
     * @return
     */
    private double computeCost(Pattern first, Pattern last) {
        int start = first.getStart();
        int end = last.getStart()+last.getLen();
        double startValue = datas.get(start);
        double endValue = datas.get(end);
        double slope = (endValue-startValue)/(end-start);
        double cost = 0;
        for(int i = start;i <= end;i++){
            cost+=Math.sqrt((startValue+slope*(i-start)-datas.get(i))*(startValue+slope*(i-start)-datas.get(i)));
        }
        return cost;
    }


    /**
     * 查找最小合并代价节点
     * @param head
     * @return
     */
    private Node findMinNode(Node head) {
        Node minNode = null;
        double minCost = Double.MAX_VALUE;
        Node now = head;
        int nowLength = 0;
        while(now.after!=null){
            now=now.after;
            nowLength++;
            if(now.cost <= mergerPrice && now.cost < minCost){
                minCost=now.cost;
                minNode=now;
            }
        }
        nowLength++;
        if((nowLength*1.0)/datas.size() < compressionRatio)
            minNode = null;
        return minNode;
    }




    

    /**
     * 合并线段表
     * 寻找左右两边合并代价最小的线段进行合并？
     */
    private void mergeLinear(){
        Node head = new Node(-1.0);
        Node now = head;
        Pattern first=null,last=null;
        for(int i : linesMap.keySet()){
        	Pattern pattern = linesMap.get(i);
            if(first==null){
                first = pattern;
                continue;
            }
            last = pattern;
            double cost = computeCost(first,last);
            Node node = new Node(cost);
            node.first = first;
            node.second = last;
            node.before = now;
            now.after = node;
            now = node;
            first = last;
            last = null;
        }

        Node minNode = findMinNode(head);
        while(minNode!=null){
            mergeNode(minNode);
            minNode = findMinNode(head);
        }
        //合并线段长度很小的node
        mergeAtLeastNum(head);
        
        linesMap = new TreeMap<Integer, Pattern>();
        now =head.after;
        linesMap.put(now.first.getStart(),now.first);
        while(now!=null&&now.second!=null){
        	
        	linesMap.put(now.second.getStart(),now.second);
            now = now.after;
        }
    }
    /**
     * 将线段长度很小的合并到长的线段中去
     * author:艾长青
     * @param head
     */
    private void mergeAtLeastNum(Node head) {
		
    	//找到长度为1的节点
    	Node minLengthNode = findMinLength(head);
        while(minLengthNode!=null){
        	mergeMinLengthNode(minLengthNode);
        	minLengthNode = findMinLength(head);
        }
	}
	/**
     * @param head
     * @return
     * author:艾长青
     */
	private Node findMinLength(Node head) {
		
        Node now = head;
        while(now.after!=null){
            now=now.after;
            int len = now.first.getLen()+now.second.getLen();
            if(len < 4)
            	return now;
        }
        return null;
	}
	 /**
     * @param minLengthNode
     * author:艾长青
     */
    private void mergeMinLengthNode(Node minNode) {
		
    	Node before = minNode.before,after = minNode.after;
        Pattern first = minNode.first,second = minNode.second;
        //double newSlope = (datas.get(second.getSpan()+second.getStart())-first.getStartValue())/(second.getSpan()+second.getStart()-first.getStart());
        double height = datas.get(second.getLen()+second.getStart())-first.getStartValue();
        double length = second.getLen()+second.getStart()-first.getStart();
        Pattern newLinear = new Pattern(first.getStart(),first.getStart()+first.getLen(),second.getLen()+second.getStart()-first.getStart(),Math.atan2(height,length),first.getStartValue());
        newLinear.setHspan(datas.get(second.getLen()+second.getStart())-first.getStartValue());
        before.second=newLinear;
        before.after=after;
        if (after != null) {
            after.first = newLinear;
            after.before = before;
        }
	}

	/**
     * 合并最小代价节点
     * @param minNode
     */
    private void mergeNode(Node minNode) {
        Node before = minNode.before,after = minNode.after;
        Pattern first = minNode.first,second = minNode.second;
        //double newSlope = (datas.get(second.getSpan()+second.getStart())-first.getStartValue())/(second.getSpan()+second.getStart()-first.getStart());
        double height = datas.get(second.getLen()+second.getStart())-first.getStartValue();
        double length = second.getLen()+second.getStart()-first.getStart();
        Pattern newLinear = new Pattern(first.getStart(),first.getStart()+(first.getLen()),second.getLen()+second.getStart()-first.getStart(),Math.atan2(height,length),first.getStartValue());
        newLinear.setHspan(datas.get(second.getLen()+second.getStart())-first.getStartValue());
        before.second=newLinear;
        before.after=after;
        if (after != null) {
            after.first = newLinear;
            after.before = before;
        }
    }


    /**
     * 计算Pattern中的angle,与前一条线段的夹角
     * */
    public void comAngle(List<Pattern> pList){
    	pList.get(0).setAngle(pList.get(0).getSlope());
    	for(int i=1;i<pList.size();i++){
    		double angle = Math.PI - pList.get(i).getSlope() + pList.get(i-1).getSlope();
    		pList.get(i).setAngle(angle);
    	}
    }
    public void setDatas(TreeMap<Integer, Double> datas) {
        this.datas = datas;
    }

    public TreeMap<Integer, Pattern> getLinears() {
        return linesMap;
    }

    public void setMergerPrice(double mergerPrice) {
        this.mergerPrice = mergerPrice;
    }

	public List<Pattern> getPatterns() {
		return patterns;
	}
    
}

/**
 * 双向保留原方向顺序链表
 */
class Node{
    public double cost;
    public Pattern first,second;
    public Node before,after;
    public Node(double cost){
        this.cost = cost;
    }
}
