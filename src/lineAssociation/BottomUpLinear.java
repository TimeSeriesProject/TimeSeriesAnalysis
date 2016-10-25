package lineAssociation;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jnetpcap.util.Length;

import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;

/**
 * Created by xzbang on 2015/12/29.
 */
public class BottomUpLinear {

    private TreeMap<Integer,Double> datas = new TreeMap<Integer, Double>();//原始数据

    private TreeMap<Integer, Linear> linears = new TreeMap<Integer, Linear>();  //拟合后的线段

    private double mergerPrice = 0.025; //线段两两合并代价阈值

    private double compressionRatio = 0.65;//压缩率

    public BottomUpLinear(TreeMap<Integer, Double> datas){
        this.datas = datas;
    }

    public BottomUpLinear(TreeMap<Integer, Double> datas, AssociationRuleLineParams arp){
        this.datas = datas;
        if(arp != null){
        	mergerPrice = arp.getMergerPrice();
        	compressionRatio = arp.getCompressionRatio();
        }
    }

    public void run(){
        normalize(datas);//归一化
        System.out.println("数据归一化完毕！");
        initializeLinear();//两两连线
        System.out.println("线段两两拟合完毕！");
        mergeLinear();//合并线段
        System.out.println("合并线段完毕！mergerPrice="+mergerPrice);
        
        
    }

    /**
     * 计算合并代价
     * @param first
     * @param last
     * @return
     */
    private double computeCost(Linear first, Linear last) {
        int startTime = first.startTime;
        int endTime = last.startTime+last.span;        
        double startValue = datas.get(startTime);
        double endValue = datas.get(endTime);        
        double slope = (endValue-startValue)/(endTime-startTime);
        double b = startValue - slope*startTime;
        double cost = 0;
        double dis = (endValue-startValue)*(endValue-startValue)*100+(endTime-startTime)*(endTime-startTime);
        for(int i = startTime;i <= endTime;i++){
        	//int midle = startTime+first.span;
        	
        	double midleValue  = datas.get(i);
            //cost+=Math.abs(startValue+slope*(i-startTime)-datas.get(i));//点到直线的距离
            cost+=Math.abs(slope*i+b-midleValue)/Math.sqrt(slope*slope+1);//点到直线的垂直距离
//            cost+=(startValue+slope*(i-startTime)-datas.get(i))*(startValue+slope*(i-startTime)-datas.get(i));
        }
//        return cost;
        cost = cost/Math.sqrt(dis);
//        return cost/(endTime-startTime-1);
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
        /*if((nowLength*1.0)/datas.size() < compressionRatio)
            minNode = null;*/
        return minNode;
    }

    /**
     * 初始化线段表，两两之间进行连线，并用Linear表示
     */
    private void initializeLinear(){
        int len = datas.size();
        if(len<=1)
        	return;

        int firstTime = datas.firstKey();
        for(int time : datas.keySet()){
            
        	if(firstTime == time)
            	continue;
            double firstValue = datas.get(firstTime);
            double lastValue = datas.get(time);
            double slope = (lastValue - firstValue)/(time - firstTime);//计算斜率，斜率大小随变量单位变化
//            double slope = lastValue-firstValue;//将差值当做斜率，避免受时间粒度的影响
            Linear linear = new Linear(Math.atan(slope),firstTime,time-firstTime,firstValue);
            linear.hspan = lastValue-firstValue;
            linears.put(firstTime,linear);
            firstTime = time;
        }
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
     * 合并线段表
     * 寻找左右两边合并代价最小的线段进行合并？
     */
    private void mergeLinear(){
        Node head = new Node(-1.0);
        Node now = head;
        Linear first=null,last=null;
        List<Double> costList = new ArrayList<Double>(); //用于测试
        for(int i : linears.keySet()){
            Linear lin = linears.get(i);
            if(first==null){
                first = lin;
                continue;
            }
            last = lin;
            //用于测试
            if(first.startTime>=20 && first.startTime<25){
            	System.out.println("startTime at"+first.startTime);
            }
            
            double cost = computeCost(first,last);
            if(first.startTime>=235 && first.startTime<247){
            	System.out.println("startTime at"+first.startTime);
            	for(int j = first.startTime;j<last.startTime+last.span;j++){
            		System.out.println("("+j+","+datas.get(j)+")");           		
            	}
//            	System.out.println("cost:"+minNode.cost);
            }
//            costList.add(cost);//用于测试
            System.out.println("线段"+first.startTime+"和线段"+last.startTime+"的合并代价:"+cost);
            Node node = new Node(cost);
            node.first = first;
            node.second = last;
            node.before = now;
            now.after = node;
            now = node;
            first = last;
            last = null;
        }

        int a = 0;
        Node minNode = findMinNode(head);
        while(minNode!=null){
        	if(minNode.first.startTime>=178 && minNode.first.startTime<188){
            	System.out.println("startTime at"+minNode.first.startTime);
            	for(int i=minNode.first.startTime;i<minNode.second.startTime+minNode.second.span;i++){
            		System.out.println("("+i+","+datas.get(i)+")");           		
            	}
            	System.out.println("cost:"+minNode.cost);
            }
            mergeNode(minNode);
           /*a++;
           if(a == 135){
        	   System.out.println("a:"+a);
           }
           System.out.println("a:"+a);*/
            
            minNode = findMinNode(head);
        }
        //合并线段长度很小的node
        //mergeAtLeastNum(head);
        
        linears = new TreeMap<Integer, Linear>();
        now =head.after;
        linears.put(now.first.startTime,now.first);
        while(now!=null&&now.second!=null){
        	
            linears.put(now.second.startTime,now.second);
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
            int len = now.first.span+now.second.span;
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
        Linear first = minNode.first,second = minNode.second;
        double newSlope = (datas.get(second.span+second.startTime)-first.startValue)/(second.span+second.startTime-first.startTime);
        Linear newLinear = new Linear(Math.atan(newSlope),first.startTime,second.span+second.startTime-first.startTime,first.startValue);
        newLinear.hspan = datas.get(second.span+second.startTime)-first.startValue;
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
        Linear first = minNode.first,second = minNode.second;
                
        double newSlope = (datas.get(second.span+second.startTime)-first.startValue)/(second.span+second.startTime-first.startTime);
        Linear newLinear = new Linear(Math.atan(newSlope),first.startTime,second.span+second.startTime-first.startTime,first.startValue);
        newLinear.hspan = datas.get(second.span+second.startTime)-first.startValue;
        before.second=newLinear;
        before.after=after;
        if(minNode.before.first != null)
        {
        	 double cost = computeCost(minNode.before.first,minNode.before.second);
             minNode.before.cost = cost;
        }
       
        if (after != null) {
        	 
             /*double cost = computeCost(minNode.after.first,minNode.after.second);
             minNode.after.cost = cost;
             minNode.cost = cost;*/
            after.first = newLinear;
            after.before = before;
            
            double cost = computeCost(minNode.after.first,minNode.after.second);
            minNode.after.cost = cost;
            minNode.cost = cost;
        }
    }


    public void setDatas(TreeMap<Integer, Double> datas) {
        this.datas = datas;
    }

    public TreeMap<Integer, Linear> getLinears() {
        return linears;
    }

    public void setMergerPrice(double mergerPrice) {
        this.mergerPrice = mergerPrice;
    }
}

/**
 * 双向保留原方向顺序链表
 */
class Node{
    public double cost;
    public Linear first,second;
    public Node before,after;
    public Node(double cost){
        this.cost = cost;
    }
}