package lineAssociation;

import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TreeMap;

/**
 * 对数据进行县短话和聚类
 * Created by xzbang on 2016/1/22.
 */
public class ClusterMain {
    public static void main(String[] args) throws IOException {

        String ancestorPath = "D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\DiplomaProject\\data";
        //# 300059，600570，000062，601991，601857
        String parentPath = "300059";
        File file = new File(ancestorPath+"\\"+parentPath);
        if(!file.exists())file.createNewFile();

        System.out.println("开始测试！");
//        int start =
        //F:\dataset\DiplomaProject\example_distances.dat
        String inputPath = "D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\DiplomaProject\\data\\javaInputData\\"+parentPath+"_out.csv";
        String rhoPath = ancestorPath+"\\"+parentPath+"\\"+"rho.dat";//dpcluster中的局部密度值
        String DELTAPath = ancestorPath+"\\"+parentPath+"\\"+"delta.dat";//dpcluster中的密度比他大的数据点中距离他最近的距离
        String GAMMAPath = ancestorPath+"\\"+parentPath+"\\"+"gamma.dat";//dpcluster中聚类中心点度量参数
        String BEITAPath = ancestorPath+"\\"+parentPath+"\\"+"beita.dat";//dpcluster中异常度度量参数
        String belongPath = ancestorPath+"\\"+parentPath+"\\"+"belong.dat";//每个数据点归属的聚类中心点
        String centerPath = ancestorPath+"\\"+parentPath+"\\"+"center.dat";//聚类中心点
        String outlierPath = ancestorPath+"\\"+parentPath+"\\"+"outlier.dat";//异常点
        String linearPath = ancestorPath+"\\"+parentPath+"\\"+"linear.dat";//每条线段的参数信息
        ArrayList<String> datas = FileInput.readFile(inputPath);
        System.out.println("测试数据读取完毕！");


        //解析原始输入数据
        int size = datas.size();
//        double[][] distances = new double[size][3];
        TreeMap<Integer,Double> sourceDatas = new TreeMap<Integer, Double>();
        for(int i=0; i<size; i++){
            String[] strings = datas.get(i).split(",");
            int key = Integer.parseInt(strings[0]);
            double value = Double.parseDouble(strings[1]);
            sourceDatas.put(key,value);
        }
        System.out.println("***************************************************");


        System.out.println("开始运行自底向上线段拟合算法！");
        BottomUpLinear bottomUpLinear = new BottomUpLinear(sourceDatas);
        bottomUpLinear.run();
        TreeMap<Integer, Linear> linears = bottomUpLinear.getLinears();  //linears的格式为:key:线段其实位置，Linear：span表示该线段的长度
        
        Linear lastPoint = new Linear(0.0,sourceDatas.lastKey(),0,sourceDatas.get(sourceDatas.lastKey()));  //why?
        System.out.println("自底向上线段拟合算法计算完毕！");
        System.out.println("***************************************************");


        System.out.println("开始运行DPCluster聚类算法！");
        ClusterWrapper clusterWrapper = new ClusterWrapper(linears);
        DPCluster dpCluster = clusterWrapper.run();
        System.out.println("DPCluster聚类算法计算完毕！");
        System.out.println("***************************************************");


        FileOutput.writeFileLinear(linears,linearPath,lastPoint);
        FileOutput.writeFile(rhoPath, dpCluster.getRHO());
        FileOutput.writeFile(dpCluster.getDELTA(),DELTAPath);
        FileOutput.writeFile(dpCluster.getGAMMA(),GAMMAPath);
        FileOutput.writeFile(dpCluster.getBEITA(),BEITAPath);
        FileOutput.writeFileInteger(dpCluster.getBelongClusterCenter(), belongPath);
        FileOutput.writeFile(dpCluster.getClusterCenters(), centerPath);
        FileOutput.writeFile(dpCluster.getOutliers(),outlierPath);
        System.out.println("测试结果输出完毕！");

    }
}
