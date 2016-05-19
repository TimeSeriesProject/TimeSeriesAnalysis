package lineAssociation;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by xzbang on 2016/3/4.
 */
public class TestBULinear {
    public static void main(String[] args) throws FileNotFoundException {
        String inputPath = "F:\\dataset\\DiplomaProject\\javaInputData\\real37_out.csv";
        System.out.println("测试数据地址： "+inputPath);
        ArrayList<String> datas = FileInput.readFile(inputPath);
        System.out.println("测试数据读取完毕！");
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
        TreeMap<Integer, Linear> linears = bottomUpLinear.getLinears();
        Linear lastPoint = new Linear(0.0,sourceDatas.lastKey(),0,sourceDatas.get(sourceDatas.lastKey()));
        System.out.println("自底向上线段拟合算法计算完毕！");

        String linearPath = "F:\\dataset\\DiplomaProject\\linearTest\\linear.dat";
        FileOutput.writeFileLinear(linears, linearPath, lastPoint);
    }
}
