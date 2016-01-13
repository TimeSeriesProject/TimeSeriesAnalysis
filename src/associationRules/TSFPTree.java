package associationRules;

import java.util.HashMap;

/**
 * Created by xzbang on 2015/12/22.
 */
public class TSFPTree {
    public int symbol;
    public int support;
    HashMap<Integer,Integer> children = new HashMap<Integer, Integer>();
    public TSFPTree(int symbol,int support){
        this.symbol=symbol;
        this.support = support;
    }
}
