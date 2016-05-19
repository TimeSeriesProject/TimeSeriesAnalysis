package lineAssociation;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by xzbang on 2016/3/17.
 */
public class RuleMain {
    public static void main(String[] args) throws IOException {
        HashMap<Integer,TreeMap<Integer,SymbolNode>> symbolSeries = new HashMap<Integer, TreeMap<Integer, SymbolNode>>();

        String ancestorPath = "D:\\Java&Android\\workspace_aa\\TimeSeriesAnalysis\\DiplomaProject\\data";

        String[] paths = {"300059","600570","000062","601991","601857"};

        System.out.println("开始测试！");
        int pathsize = paths.length;
        for(int g = 0; g < pathsize;g++){
            String parentPath = paths[g];
            File file = new File(ancestorPath+"\\"+parentPath);
            if(!file.exists())file.createNewFile();

            String belongPath = ancestorPath + "\\" + parentPath + "\\" + "belong.dat";

            ArrayList<String> belong = FileInput.readFile(belongPath);
            TreeMap<Integer,SymbolNode> symbols = new TreeMap<Integer, SymbolNode>();
            for(String b : belong){
                String[] strings = b.split(",");
                int center = Integer.parseInt(strings[1]);
                if(center==-2)     //异常点跳过
                	continue;
                else if(center==-1)
                	center = Integer.parseInt(strings[0]);
                SymbolNode symbolNode = new SymbolNode(center,g);
                symbols.put(Integer.parseInt(strings[0]),symbolNode);  //哪个样本点归属那个中心，中心代表类别
            }
            symbolSeries.put(g,symbols);

        }
        FindRules findRules = new FindRules(symbolSeries);
        findRules.run();
        System.out.println(findRules.rulesSet.size());
        HashMap<Integer,Integer> numMap = new HashMap<Integer, Integer>();
        for(Rule rule : findRules.rulesSet){
            int i = rule.before.size()+1;
            if(numMap.containsKey(i)){
                numMap.put(i,numMap.get(i)+1);
            }else{
                numMap.put(i,1);
            }
            System.out.println(rule);
        }
        System.out.println(numMap);
        System.out.println("测试结果输出完毕！");
    }
}
