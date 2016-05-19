package lineAssociation;

import java.util.*;

/**
 * Created by xzbang on 2016/1/18.
 */
public class DPCluster {

    private double t = 0.1;//用于确定参数截断距离dc
    private double centerLine = -1.0;//聚类中心划界线，为 -1 时通过函数computeCenterLine自动确定
    private int way = 1;//聚类中心线自动确定方法选择( 1:高斯 or 2:间隔 )
    private double gaosi = 0.0;//聚类中心线自动确定方法选择高斯分布方法时有效，表示中心线距离均值gaosi倍标准差
    private boolean multiOrMin = false;//求gamma时采用相乘或求最小的方式(true表示相乘，false表示求最小)

    private double dc;//截断距离，通过参数t和函数computeDc确定
    private int N;//数据点个数


    private double[][] distancesInput;

    //距离索引，半角矩阵
    private HashMap<Integer,HashMap<Integer,Double>> completeDistances = new HashMap<Integer, HashMap<Integer, Double>>();
    private ArrayList<Double> distances = new ArrayList<Double>();//两用list，一用于对所有距离排序，二用于对RHO值排序

    private HashMap<Double,Integer> RHO = new HashMap<Double, Integer>();
    private TreeMap<Integer,Double> DELTA = new TreeMap<Integer, Double>();
    private TreeMap<Integer,Double> GAMMA = new TreeMap<Integer, Double>();
    private TreeMap<Integer,Double> BEITA = new TreeMap<Integer, Double>();//异常度，BEITA=DELTA/RHO；

    //各个数据点的归属聚类中心，-1表示聚类中心，-2表示异常点，其他表示相应归属的聚类中心
    private TreeMap<Integer,Integer> belongClusterCenter = new TreeMap<Integer, Integer>();
    private TreeSet<Integer> clusterCenters = new TreeSet<Integer>();//聚类中心
    private TreeSet<Integer> outliers = new TreeSet<Integer>();//异常点

    public DPCluster(double[][] distancesInput){
        this.distancesInput = distancesInput;
    }

    public DPCluster(double[][] distancesInput,double centerLine){
        this.distancesInput = distancesInput;
        this.centerLine = centerLine;
    }

    public DPCluster(double[][] distancesInput,double centerLine,double t){
        this.distancesInput = distancesInput;
        this.centerLine = centerLine;
        this.t=t;
    }

    protected void run(){
        dispalyParameter();//打印聚类参数信息
        computeDc();
        System.out.println("截断距离dc计算完毕！dc="+dc+";N="+N);
        computeCompleteDistances();
        System.out.println("距离索引completeDistances计算完毕！");
        computeRHO();
        System.out.println("RHO值计算完毕！");
        computeDELTA();
        System.out.println("DELTA值计算完毕！");
        computeGAMMA();
        System.out.println("GAMMA值计算完毕！");
        computeBEITA();
        System.out.println("BEITA值计算完毕！");
        computeCenters();
        System.out.println("聚类中心与异常点划分完毕！centerLine="+centerLine);
        System.out.println("各个map中的数据条数：\n--RHO: "+RHO.size()+"\n--DELTA: "+DELTA.size()
                +"\n--GAMMA: "+GAMMA.size()+"\n--clusterCenters: "+clusterCenters.size()
                +"\n--outliers: "+outliers.size());
    }

    /**
     * 通过参数t，确定截断距离dc的值
     * 首先计算所有的点两两之间的距离，然后排序，dc=distances.get(f(Mt))
     * M为距离数(N(N-1)/2),f()为四舍五入取整函数，N为点的个数
     */
    private void computeDc(){
        int disize = distancesInput.length;
        for(int i=0; i<disize; i++){
            distances.add(distancesInput[i][2]);
        }
        Collections.sort(distances);
        int M = distances.size();
        N =(int)(Math.sqrt(0.25 + 2.0*M)+0.5);
        dc = distances.get((int)(M*t));
        distances = null;//释放内存
    }

    /**
     * 计算并保存距离hashmap,方便计算时索引
     */
    private void computeCompleteDistances(){
        int disize = distancesInput.length;
//        System.out.println("disize="+disize);
        for(int i=0; i<disize; i++){
            if(!completeDistances.containsKey((int)distancesInput[i][0])){
                completeDistances.put((int)distancesInput[i][0],new HashMap<Integer, Double>());
            }
            completeDistances.get((int)distancesInput[i][0]).put((int)distancesInput[i][1],distancesInput[i][2]);
            //补上最后一个数据点，用于后面计算数值时统计key值
            if(!completeDistances.containsKey((int)distancesInput[i][1])) {
                completeDistances.put((int) distancesInput[i][1], new HashMap<Integer, Double>());
            }
        }

    }

    /**
     * 计算RHO值，用treemap排序
     */
    private void computeRHO(){
        distances = new ArrayList<Double>();
        for(int i : completeDistances.keySet()){
            double rho = 0.0;
            for(int j : completeDistances.keySet()){
                double distance = Double.MAX_VALUE;
                try {
                    if (i < j) {
                        distance = completeDistances.get(i).get(j);
                    } else if (i > j) {
                        distance = completeDistances.get(j).get(i);
                    } else {
                        continue;
                    }
                }catch(NullPointerException e){
                    System.out.println("i="+i+",j="+j);
                    throw e;
                }
                rho += Math.exp(-Math.pow((distance/dc),2));
            }
            while(RHO.containsKey(rho)){ //避免重复，强行区分
                rho+=0.000000001;
            }
            RHO.put(rho,i);
            distances.add(rho);
//            System.out.println("当前rho进度："+i);
        }
        Collections.sort(distances);
    }

    /**
     * 计算DELTA值，并根据其中获取DELTA时相应的数据点确定归属聚类中心
     */
    private void computeDELTA(){
        int size = distances.size();
        for(int x = size-1;x>=0;x--){
            double delta = -1.0;
            int belongCC = -1;
            double d = distances.get(x);
            for(int y = size-1;y>=0;y--){
                double e = distances.get(y);
                if(d==e)break;
                int i = RHO.get(d),j = RHO.get(e);
                double distance = 0.0;
                if(i>j) distance = completeDistances.get(j).get(i);
                else distance = completeDistances.get(i).get(j);
                if(delta==-1.0||distance < delta){
                    delta = distance;
                    if(distance<=dc)belongCC=j;
                }
            }
            if(delta==-1.0){
                HashMap<Integer,Double> firstDists = completeDistances.get(RHO.get(d));
                double max = 0.0;
                for(int z : firstDists.keySet()){
                    if(firstDists.get(z)>max)max=firstDists.get(z);
                }
                delta = max;
            }
            DELTA.put(RHO.get(d),delta);
            belongClusterCenter.put(RHO.get(d),belongCC);
        }
        distances = null;       //释放内存；
    }

    /**
     * 计算GAMMA值，GAMMA = RHO*DELTA;
     * 参与计算前先对两个变量进行MIN_MAX归一化
     */
    private void computeGAMMA(){
        double rhomin=Double.MAX_VALUE,rhomax = Double.MIN_VALUE,deltamin=Double.MAX_VALUE,deltamax = Double.MIN_VALUE;
        for(double d : RHO.keySet()){
            if(d>rhomax)rhomax = d;
            if(d<rhomin)rhomin = d;
        }
        for(int i : DELTA.keySet()){
            double delta = DELTA.get(i);
            if(delta > deltamax) deltamax = delta;
            if(delta < deltamin) deltamin = delta;
        }
        for(double d : RHO.keySet()){
            double delta = DELTA.get(RHO.get(d));
            double gamma = 0.0;
            if(multiOrMin) {
                gamma = ((d - rhomin) / (rhomax - rhomin)) * ((delta - deltamin) / (deltamax - deltamin));
            }else {
                gamma = Math.min(((d - rhomin) / (rhomax - rhomin)), ((delta - deltamin) / (deltamax - deltamin)));
            }
            if(gamma<0.001)gamma=0.001;
            GAMMA.put(RHO.get(d),gamma);
        }
    }

    /**
     * 计算GAMMA值，GAMMA = DELTA/RHO;
     * 参与计算前先对两个变量进行MIN_MAX归一化
     */
    private void computeBEITA(){
        double rhomin=Double.MAX_VALUE,rhomax = Double.MIN_VALUE,deltamin=Double.MAX_VALUE,deltamax = Double.MIN_VALUE;
        for(double d : RHO.keySet()){
            if(d>rhomax)rhomax = d;
            if(d<rhomin)rhomin = d;
        }
        for(int i : DELTA.keySet()){
            double delta = DELTA.get(i);
            if(delta > deltamax) deltamax = delta;
            if(delta < deltamin) deltamin = delta;
        }
        for(double d : RHO.keySet()){
            double delta = DELTA.get(RHO.get(d));
            double beita = 0.0;
            beita = Math.log(((delta - deltamin) / (deltamax - deltamin)) / ((d + 1))+1);
            if(beita<0.001)beita=0.001;

            BEITA.put(RHO.get(d),beita);
        }
    }

    /**
     * 如果传入了centerLine参数，根据参数确定聚类中心
     * 否则通过computeCenterLine函数自动确定
     */
    private void computeCenters(){
        if(centerLine==-1.0) {
            computeCenterLine();
        }
        //将一部分潜在聚类中心划分为异常点
        for(int i : GAMMA.keySet()){
            double gamma = GAMMA.get(i);
            int belongCC = belongClusterCenter.get(i);
            if(gamma>=centerLine&&belongCC==-1)clusterCenters.add(i);
            else if(gamma<centerLine&&belongCC==-1){
                outliers.add(i);
                belongClusterCenter.put(i,-2);
            }
        }
        //将未识别为潜在聚类中心但归属聚类中心为异常点的数据点判定为异常点，将其他点映射到聚类中心
        for(int i : GAMMA.keySet()){
            int belongCC = belongClusterCenter.get(i);
            if(belongCC<0)continue;
            while(belongClusterCenter.get(belongCC)>=0){
                belongCC = belongClusterCenter.get(belongCC);
            }
            if(belongClusterCenter.get(belongCC)==-2){
                belongClusterCenter.put(i,-2);
                outliers.add(i);
            }else{
                belongClusterCenter.put(i,belongCC);
            }
        }
    }

    /**
     * 自动确定聚类中心点分割线
     * 方法一：对GAMMA值计算高斯分布，GAMMA值距离均值超出三倍标准差为分割线
     * 方法二：将GAMMA值按N个桶分割，对每个区间向上取整计数，第一个为空且剩余数据点小于1%时为分割线
     * N为数据点个数
     */
    private void computeCenterLine(){
        if(way==1) {
            double sum = 0.0, squareSum = 0.0, mean = 0.0, stdvar = 0.0;
            int size = GAMMA.size();
            for (int i : GAMMA.keySet()) {
                sum += GAMMA.get(i);
                squareSum += (GAMMA.get(i) * GAMMA.get(i));
            }
            mean = sum / size;
            stdvar = Math.sqrt(squareSum / size - mean * mean);
            centerLine = mean + gaosi * stdvar;
        }else{
//            int size = GAMMA.size()/20;
            int size = 100;
            int[] bucket = new int[size+1];
            for(int i : GAMMA.keySet()){
                bucket[(int)Math.ceil(GAMMA.get(i)*size)]++;
            }
            int sum=0,i=0;
            while(i<=size){
                if(bucket[i]!=0){
                    sum+=bucket[i];
                    i++;
                }else{
                    break;
                }
            }
            centerLine = (i*1.0)/size;
        }
    }

    /**
     * 查找与key对应的直线最近的直线编号
     * @param key
     * @return
     */
    public int findMinSimLinear(int key){
        double minDist = Double.MAX_VALUE;
        int value = 0;
        for(int i : completeDistances.keySet()){
            HashMap<Integer,Double> distMap = completeDistances.get(i);
            if(i != key){
                if(distMap.containsKey(key)&&minDist>distMap.get(key)){
                    minDist = distMap.get(key);
                    value = i;
                }
            }else{
                for(int j : distMap.keySet()){
                    if(minDist > distMap.get(j)){
                        minDist = distMap.get(j);
                        value = j;
                    }
                }
            }
        }
        return value;
    }

    /**
     * 打印聚类参数信息
     */
    private void dispalyParameter(){
        System.out.println("-----<<<聚类参数信息>>>-----");
        System.out.println("dc选择前置参数t: " + t);
        System.out.println("聚类中心线自动确定方法选择( 1:高斯 or 2:间隔 )way: " + way);
        System.out.println("若way为1，中心线距离均值gaosi倍标准差gaosi: " + gaosi);
        System.out.println("求gamma方式(true表示相乘，false表示求最小)multiOrMin: " + multiOrMin);
        System.out.println("-----<<<参数打印完毕>>>-----");
    }

    public HashMap<Double, Integer> getRHO() {
        return RHO;
    }

    public TreeMap<Integer, Double> getDELTA() {
        return DELTA;
    }

    public TreeMap<Integer, Double> getGAMMA() {
        return GAMMA;
    }

    public TreeMap<Integer, Double> getBEITA() {
        return BEITA;
    }

    public TreeMap<Integer, Integer> getBelongClusterCenter() {
        return belongClusterCenter;
    }

    public TreeSet<Integer> getClusterCenters() {
        return clusterCenters;
    }

    public TreeSet<Integer> getOutliers() {
        return outliers;
    }

    public double getCenterLine() {
        return centerLine;
    }

    public void setCenterLine(double centerLine) {
        this.centerLine = centerLine;
    }

    public void setT(double t) {
        this.t = t;
    }
}