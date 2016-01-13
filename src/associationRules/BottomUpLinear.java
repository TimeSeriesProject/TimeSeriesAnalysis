package associationRules;

import java.util.TreeMap;

/**
 * Created by xzbang on 2015/12/21.
 */
public class BottomUpLinear {

    private TreeMap<Integer,Double> datas = new TreeMap<Integer, Double>();//原始数据

    private TreeMap<Integer, Linear> linears = new TreeMap<Integer, Linear>();//拟合后的线段

    private double mergerPrice = 0.023; //线段两两合并代价阈值

    public BottomUpLinear(){}

    public BottomUpLinear(TreeMap<Integer,Double> datas){
        this.datas = datas;
    }

    public BottomUpLinear(TreeMap<Integer,Double> datas,double mergerPrice){
        this.datas = datas;
        this.mergerPrice = mergerPrice;
    }

    /**
     * 初始化线段表，两两之间进行连线，并用Linear表示
     */
    public void initializeLinear(){
        int len = datas.size();
        if(len<=1)return;

//        normalize(datas);//归一化

        int firstTime = datas.firstKey();
        for(int time : datas.keySet()){
            if(firstTime==time)continue;
            Linear linear = new Linear();
            linear.setStartTime(firstTime);
            linear.setSpan(time-firstTime);
            double firstValue = datas.get(firstTime);
            double lastValue = datas.get(time);
//            double slope = (lastValue-firstValue)/(time-firstTime);//计算斜率，斜率大小随变量单位变化
            double slope = lastValue-firstValue;//将差值当做斜率，避免受时间粒度的影响
            linear.setSlope(slope);
            linear.setStartValue(firstValue);
            linears.put(firstTime,linear);
            firstTime=time;
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
            if(value<min)min=value;
            if(value>max)max=value;
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
