package associationRules;

import java.util.HashSet;

/**
 * Created by xzbang on 2015/12/22.
 */
public class Rule {
    public int source;              //源序列中的符号
    public int target;              //目标序列中的符号
    public int sourceSupport;       //源符号支持度
    public int targetSupport;       //目的符号支持度
    public int commonSupport;       //两个符号共同支持度
    public double confidence;       //置信度
    public double interest;         //兴趣度

    public HashSet<Integer> times = new HashSet<Integer>();

    public Rule(){}

    public Rule(int source,
                int target,
                int sourceSupport,
                int targetSupport,
                int commonSupport,
                double confidence,
                double interest){
        this.source = source;
        this.target = target;
        this.sourceSupport = sourceSupport;
        this.targetSupport = targetSupport;
        this.commonSupport = commonSupport;
        this.confidence = confidence;
        this.interest = interest;
    }

    @Override
    public String toString(){
        return source + "-->" + target;
    }

    public String toDetailString(){
        return "source: " + source + "; " +
                "target: " + target + "; " +
                "sourceSupport: " + sourceSupport + "; " +
                "targetSupport: " + targetSupport + "; " +
                "commonSupport: " + commonSupport + "; " +
                "confidence: " + confidence + "; " +
                "interest: " + interest;
    }

    public String printTimes(){
        StringBuilder sb = new StringBuilder();
        for(int t : times){
            sb.append(",").append(t);
        }
        sb.append("]");
        return sb.toString().replaceFirst(",","[");
    }
}
