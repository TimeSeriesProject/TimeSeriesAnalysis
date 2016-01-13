package associationRules;

import associationRules.*;

import java.util.HashSet;
import java.util.TreeMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * Created by xzbang on 2015/12/23.
 */
public class AssociationRuleMain {
    public HashSet<DiscreteMap> sourceMaps;
    public HashSet<DiscreteMap> targetMaps;
    public HashSet<Rule> rules;

    public void miningRules(DataItems sourceDataItems,DataItems targetDataItems){
        //初始化数据
        LinearInput linearInput = new LinearInput();
        linearInput.dataItemsInput(sourceDataItems, targetDataItems);

        //对源序列进行预处理
        BottomUpLinear bottomUpLinearSource = new BottomUpLinear(linearInput.sourceDatas);
        bottomUpLinearSource.initializeLinear();
        SymbolicLinear sourceSymbolicLinear = new SymbolicLinear(bottomUpLinearSource.getLinears());
        TreeMap<Integer,Integer> sourceSequence = sourceSymbolicLinear.symbolic();//符号化之后的原序列
        sourceMaps = sourceSymbolicLinear.getMaps();//原序列符号化含义

        //对目标序列进行预处理
        BottomUpLinear bottomUpLinearTarget = new BottomUpLinear(linearInput.targetDatas);
        bottomUpLinearTarget.initializeLinear();
        SymbolicLinear targetSymbolicLinear = new SymbolicLinear(bottomUpLinearTarget.getLinears());
        TreeMap<Integer,Integer> targetSequence = targetSymbolicLinear.symbolic();//符号化之后的目标序列
        targetMaps = targetSymbolicLinear.getMaps();//目标序列符号化含义

        //计算关联规则
        AssociationRule associationRule = new AssociationRule(sourceSequence,targetSequence);
        rules = associationRule.run();

        //输出
//        System.out.println("source symbol: " + sourceMaps);
//        System.out.println("target symbol: " + targetMaps);
//        for(Rule rule : rules){
//            System.out.println(rule.toDetailString());
//            System.out.println("times: "+rule.printTimes());
//        }
    }
}
