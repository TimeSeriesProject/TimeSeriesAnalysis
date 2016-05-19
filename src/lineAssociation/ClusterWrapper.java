package lineAssociation;


import java.util.TreeMap;

/**
 * Created by xzbang on 2016/1/18.
 */
public class ClusterWrapper {
    private TreeMap<Integer,Linear> linears;
    private double[][] distancesInput; //DP-Cluster数据输入，半角矩阵

    public ClusterWrapper(TreeMap<Integer,Linear> linears){
        this.linears = linears;
    }

    /**
     * 对线段中的‘起始值’、‘时间跨度’、‘倾斜角度’归一化
     * 归一化方法采用MIN_MAX归一化
     */
    private void normalize() {
    	
        Linear linearMax = new Linear(Double.MIN_VALUE,0,Integer.MIN_VALUE,Double.MIN_VALUE);
        Linear linearMin = new Linear(Double.MAX_VALUE,0,Integer.MAX_VALUE,Double.MAX_VALUE);
        linearMax.hspan=linearMin.hspan=0.0;
        for(int i : linears.keySet()){
            Linear linear = linears.get(i);
            if(linear.theta > linearMax.theta)
            	linearMax.theta = linear.theta;
            if(linear.span > linearMax.span)
            	linearMax.span = linear.span;
            if(linear.startValue > linearMax.startValue)
            	linearMax.startValue = linear.startValue;
            if(linear.theta < linearMin.theta)
            	linearMin.theta = linear.theta;
            if(linear.span < linearMin.span)
            	linearMin.span = linear.span;
            if(linear.startValue < linearMin.startValue)
            	linearMin.startValue = linear.startValue;
            if(linear.hspan > linearMax.hspan)
            	linearMax.hspan = linear.hspan;
            if(linear.hspan < linearMin.hspan)
            	linearMin.hspan = linear.hspan;
        }
        for(int i : linears.keySet()) {
            Linear linear = linears.get(i);
//            linear.normTheta = (linear.theta-linearMin.theta)/(linearMax.theta-linearMin.theta);//[0,1]
            linear.normTheta = linear.theta/Math.max(Math.abs(linearMax.theta),Math.abs(linearMin.theta));//[-1,1]
            linear.normSpan = (linear.span-linearMin.span)*1.0/(linearMax.span-linearMin.span);//[0,1]
            linear.normStartValue = (linear.startValue-linearMin.startValue)/(linearMax.startValue-linearMin.startValue);//[0,1]
            linear.normHspan = linear.hspan/Math.max(Math.abs(linearMax.hspan),Math.abs(linearMin.hspan));//[-1,1]
//            linear.normTheta = (Math.exp(linear.normTheta)/(1+Math.exp(linear.normTheta))-0.5)*2*(1+Math.E)/(Math.E-1);
        }
        System.out.println("linearMax: "+linearMax.toDetailString());
        System.out.println("linearMin: "+linearMin.toDetailString());
    }

    /**
     * 将线段时间序列转化为聚类算法的输入，即二维数组
     */
    private void computeDistancesInput(){
        int lsize = linears.size();
        distancesInput = new double[lsize*(lsize-1)/2][3];
        int k = 0;
        for(int i : linears.keySet()){
            for(int j : linears.keySet()){
                if(j <= i)
                	continue;
                distancesInput[k][0] = i;
                distancesInput[k][1] = j;
                distancesInput[k][2] = Similarity.getDistance(linears.get(i),linears.get(j));
                k++;
            }
        }
    }

    public DPCluster run(){
        normalize();
        computeDistancesInput();
        DPCluster dpCluster = new DPCluster(distancesInput);
        dpCluster.run();
        return dpCluster;
    }
}
