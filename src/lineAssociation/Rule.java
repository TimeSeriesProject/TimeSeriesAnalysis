package lineAssociation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xzbang on 2016/3/17.
 */
public class Rule {
    public ArrayList<SymbolNode> before = new ArrayList<SymbolNode>();
    public SymbolNode after;
    public int supnum;//支持度计数
    public double sup;//支持度
    public double con;//置信度
    public double inf;//兴趣度
    public HashMap<Integer,Integer> parent_node_time_map;//父亲节点与当前节点的映射表

    public Rule(ArrayList<SymbolNode> before, SymbolNode after, int supnum, double sup, double con, double inf) {
        this.before = before;
        this.after = after;
        this.supnum = supnum;
        this.sup = sup;
        this.con = con;
        this.inf = inf;
    }

    public Rule() {
    }

    public String toString(){
        return "("+before+"->"+after+","+supnum+","+sup+","+con+","+inf+")";
    }
}
