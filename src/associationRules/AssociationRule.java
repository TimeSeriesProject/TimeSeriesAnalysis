package associationRules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Created by xzbang on 2015/12/22.
 */
public class AssociationRule {
    private int support_threshold = 3;//支持度阈值
    private double confidence_threshold = 0.4;//置信度阈值
    private double interest_threshold = 0.02;//兴趣度阈值
    private int T = 1;//计算T个时间粒度内的关联规则

    private HashMap<Integer,Integer> sourceSymbolSupport = new HashMap<Integer, Integer>();//原序列中符号支持度统计
    private HashMap<Integer,Integer> targetSymbolSupport = new HashMap<Integer, Integer>();//目标序列中符号支持度统计
    private HashMap<Integer,TSFPTree> frequentTrees = new HashMap<Integer,TSFPTree>();//频繁符号对应的TS-FP-TREE
    private HashSet<Rule> rules = new HashSet<Rule>();//生成的关联规则

    private TreeMap<Integer,Integer> sourceSequence;//源符号序列
    private TreeMap<Integer,Integer> targetSequence;//目标符号序列

    public AssociationRule(TreeMap<Integer,Integer> sourceSequence,TreeMap<Integer,Integer> targetSequence){
        this.sourceSequence = sourceSequence;
        this.targetSequence = targetSequence;
    }

    /**
     * 计算T时间内的两元时间序列关联规则
     * 默认源时间序列和目标时间序列长度相同，起始时间相同，时间粒度相同并且均匀分布、一一对应
     * @return 规则列表
     */
    public HashSet<Rule> run(){

        for(int key : sourceSequence.keySet()){
            int value = sourceSequence.get(key);
            int support = 0;
            if(sourceSymbolSupport.containsKey(value)) {
                support=sourceSymbolSupport.get(value);
            }
            support++;
            sourceSymbolSupport.put(value,support);
        }

        int len = targetSequence.size();
        int[] targetSymbol = new int[len];//用于记录目标序列顺序，方便提取当前位置之后的T个符号
        int i = 0;

        for(int key : targetSequence.keySet()){
            int value = targetSequence.get(key);
            targetSymbol[i] = value;
            i++;
            int support = 0;
            if(targetSymbolSupport.containsKey(value)) {
                support=targetSymbolSupport.get(value);
            }
            support++;
            targetSymbolSupport.put(value,support);
        }

        for(int symbol : sourceSymbolSupport.keySet()){
            int support = sourceSymbolSupport.get(symbol);
            if(support>=support_threshold){
                TSFPTree tsfpTree = new TSFPTree(symbol,support);
                frequentTrees.put(symbol,tsfpTree);
            }
        }

//        for (int j = 0;j < len;j++)
//            System.out.println(targetSymbol[j]);

        //生成TS-FP-Tree
        int locate = 0;
        for (int key : sourceSequence.keySet()) {
            int value = sourceSequence.get(key);
            if(!frequentTrees.containsKey(value)){
                locate++;
                continue;
            }
            TSFPTree tsfpTree = frequentTrees.get(value);
            HashSet<Integer> exitsTarget = new HashSet<Integer>();//用于一个T内统一符号只计算一次
            for(int j = 0;j < T;j++){
                int nowLocate = locate+j;
                if(nowLocate>=len)break;
                int targetSymbolValue = targetSymbol[nowLocate];
                if(exitsTarget.contains(targetSymbolValue))continue;
                int targetSupport = 0;
                if(tsfpTree.children.containsKey(targetSymbolValue))
                    targetSupport = tsfpTree.children.get(targetSymbolValue);
                targetSupport++;
                tsfpTree.children.put(targetSymbolValue,targetSupport);
                exitsTarget.add(targetSymbolValue);
            }
            locate++;
        }

        //计算置信度和兴趣度，生成规则
        for(int source : frequentTrees.keySet()){
            TSFPTree tsfpTree = frequentTrees.get(source);
            int sourceSupport = tsfpTree.support;
            for(int target : tsfpTree.children.keySet()){
                int commonSupport = tsfpTree.children.get(target);
                int targetSupport = targetSymbolSupport.get(target);
                if(commonSupport < support_threshold)continue;
                double confidence = commonSupport*1.0/sourceSupport;
                double interest = confidence/targetSupport;
                if(confidence >= confidence_threshold&&interest>=interest_threshold){
                    Rule rule = new Rule(source,target,sourceSupport,targetSupport,commonSupport,confidence,interest);

                    HashSet<Integer> times = new HashSet<Integer>();
                    for(int t : sourceSequence.keySet()){
                        int sourceSymbolic = sourceSequence.get(t);
                        int targetSymbolic = targetSequence.get(t);
                        if((sourceSymbolic == rule.source) && (targetSymbolic == rule.target)) {
                            times.add(t);
                        }
                    }
                    rule.times = times;
                    rules.add(rule);
                }
            }
        }

        return rules;
    }

    public int getT() {
        return T;
    }

    public void setT(int t) {
        T = t;
    }

    public int getSupportThreshold() {
        return support_threshold;
    }

    public void setSupportThreshold(int support_threshold) {
        this.support_threshold = support_threshold;
    }

    public double getConfidenceThreshold() {
        return confidence_threshold;
    }

    public void setConfidenceThreshold(double confidence_threshold) {
        this.confidence_threshold = confidence_threshold;
    }

    public double getInterestThreshold() {
        return interest_threshold;
    }

    public void setInterestThreshold(double interest_threshold) {
        this.interest_threshold = interest_threshold;
    }
}
