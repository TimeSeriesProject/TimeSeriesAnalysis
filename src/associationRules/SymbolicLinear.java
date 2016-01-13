package associationRules;

import associationRules.DiscreteMap;
import associationRules.Linear;

import java.util.HashSet;
import java.util.TreeMap;

/**
 * Created by xzbang on 2015/12/22.
 */
public class SymbolicLinear {

    private TreeMap<Integer, Linear> linears;
    private double max;
    private HashSet<DiscreteMap> maps;

    public SymbolicLinear(TreeMap<Integer,Linear> linears){
        this.linears = linears;
        normalize(linears);
        buildMaps();
    }

    /**
     * 对线段进行符号化
     * 针对只有两点间的线段，由于时间跨度都一致，所以只考虑斜率
     * 采用近似等频离散化方法，离散区间：[1:[1,0.4),2:[0.4,0.1),3:[0.1,0],4:(0,-0.1],5:(-0.1,-0.4],6:(-0.4,-1]].
     * @return 符号化之后的数据集，符号用整形表示
     */
    public TreeMap<Integer,Integer> symbolic(){
        TreeMap<Integer,Integer> result = new TreeMap<Integer, Integer>();

        for(int i : linears.keySet()){
            Linear linear = linears.get(i);
            double slope = linear.getSlope();
            int sym = 0;
            for(DiscreteMap discreteMap : maps){
                if(slope<=discreteMap.end){
                    if(slope>discreteMap.start||(slope==-1.0&&slope == discreteMap.start)) {
                        sym = discreteMap.symbol;
                        break;
                    }
                }
            }
//            if(slope>0.4)sym=1;
//            else if(slope>0.1)sym=2;
//            else if(slope>=0)sym=3;
//            else if(slope>=-0.1)sym=4;
//            else if(slope>=-0.4)sym=5;
//            else sym=6;
            result.put(i,sym);
        }

        return result;
    }

    private void buildMaps(){
        maps = new HashSet<DiscreteMap>();
        double[] value = {-1.0,-0.4,-0.1,0.0,0.1,0.4,1.0};
        for(int i = 1;i <=6;i++){
            DiscreteMap discreteMap = new DiscreteMap(i,value[i-1],value[i],max*value[i-1],max*value[i]);
            maps.add(discreteMap);
        }
    }

    public HashSet<DiscreteMap> getMaps(){
        return maps;
    }

    private void normalize(TreeMap<Integer,Linear> ls){
        max = Integer.MIN_VALUE;
        for(int i : ls.keySet()){
            Linear linear = ls.get(i);
            double slope = linear.getSlope();
            max = max > Math.abs(slope)?max:Math.abs(slope);
        }
        for(int i : ls.keySet()) {
            Linear linear = ls.get(i);
            double slope = linear.getSlope();
            slope = slope/max;
            linear.setSlope(slope);
        }
    }
}
