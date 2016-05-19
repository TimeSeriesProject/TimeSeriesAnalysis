package lineAssociation;


import java.util.TreeMap;

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

    public BottomUpLinear(TreeMap<Integer, Double> datas, double mergerPrice){
        this.datas = datas;
        this.mergerPrice = mergerPrice;
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
        double cost = 0;
        for(int i = startTime;i <= endTime;i++){
            cost+=Math.sqrt((startValue+slope*(i-startTime)-datas.get(i))*(startValue+slope*(i-startTime)-datas.get(i)));
//            cost+=(startValue+slope*(i-startTime)-datas.get(i))*(startValue+slope*(i-startTime)-datas.get(i));
        }
        return cost;
//        return cost/(endTime-startTime+1);
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
        for(int i : linears.keySet()){
            Linear lin = linears.get(i);
            if(first==null){
                first = lin;
                continue;
            }
            last = lin;
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

        linears = new TreeMap<Integer, Linear>();
        now =head.after;
        linears.put(now.first.startTime,now.first);
        while(now!=null&&now.second!=null){
        	
            linears.put(now.second.startTime,now.second);
            now = now.after;
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
        if (after != null) {
            after.first = newLinear;
            after.before = before;
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