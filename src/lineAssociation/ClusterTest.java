package lineAssociation;

import java.io.*;
import java.util.*;

/**
 * Created by xzbang on 2016/1/19.
 */
public class ClusterTest {
    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("开始测试！");
        //F:\dataset\DiplomaProject\example_distances.dat
        String inputPath = "F:\\dataset\\DiplomaProject\\test\\example_distances.dat";
        String rhoPath = "F:\\dataset\\DiplomaProject\\test\\rho.dat";
        String DELTAPath = "F:\\dataset\\DiplomaProject\\test\\delta.dat";
        String GAMMAPath = "F:\\dataset\\DiplomaProject\\test\\gamma.dat";
        String BEITAPath = "F:\\dataset\\DiplomaProject\\test\\beita.dat";
        String belongPath = "F:\\dataset\\DiplomaProject\\test\\belong.dat";
        String centerPath = "F:\\dataset\\DiplomaProject\\test\\center.dat";
        String outlierPath = "F:\\dataset\\DiplomaProject\\test\\outlier.dat";
        System.out.println("测试数据地址： "+inputPath);
        ArrayList<String> datas = FileInput.readFile(inputPath);
        System.out.println("测试数据读取完毕！");
        int size = datas.size();
        double[][] distances = new double[size][3];
        for(int i=0; i<size; i++){
            String[] strings = datas.get(i).split(" ");
            distances[i][0] = Double.parseDouble(strings[0]);
            distances[i][1] = Double.parseDouble(strings[1]);
            distances[i][2] = Double.parseDouble(strings[2]);
        }
        System.out.println("***************************************************");
        System.out.println("开始运行DPCluster聚类算法！");
        DPCluster dpCluster = new DPCluster(distances);
        dpCluster.run();
        System.out.println("DPCluster聚类算法计算完毕！");
        System.out.println("***************************************************");
        FileOutput.writeFile(rhoPath, dpCluster.getRHO());
        FileOutput.writeFile(dpCluster.getDELTA(),DELTAPath);
        FileOutput.writeFile(dpCluster.getGAMMA(),GAMMAPath);
        FileOutput.writeFile(dpCluster.getBEITA(),BEITAPath);
        FileOutput.writeFileInteger(dpCluster.getBelongClusterCenter(), belongPath);
        FileOutput.writeFile(dpCluster.getClusterCenters(), centerPath);
        FileOutput.writeFile(dpCluster.getOutliers(),outlierPath);
        System.out.println("测试结果输出完毕！");
        System.out.println("测试结果输出地址：\n--RHO: "+rhoPath+"\n--DELTA: "+DELTAPath+"\n--GAMMA: " +GAMMAPath
                +"\n--belongPath: "+belongPath+"\n--centerPath: " +centerPath +"\n--outlierPath: "+outlierPath);
    }
}
