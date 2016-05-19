package lineAssociation;

import java.util.HashSet;

/**
 * Created by xzbang on 2016/3/17.
 */
public class SymbolNode {

    public int node_name;
    public int belong_series;

    public SymbolNode(){}
    public SymbolNode(int node_name,int belong_series){
        this.node_name=node_name;
        this.belong_series=belong_series;
    }
    
    public boolean equals(SymbolNode sn){
        if(node_name==sn.node_name&&belong_series==sn.belong_series)
            return true;
        else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SymbolNode snode = (SymbolNode) obj;

        return this.equals(snode);
    }

    public int hashCode(){
        return node_name*10000+belong_series;
    }

    public String toString(){
        return node_name+"("+belong_series+")";
    }

    //test
    public static void main(String[] args) {
        SymbolNode symbolNode1 = new SymbolNode(23,1);
        SymbolNode symbolNode2 = new SymbolNode(23,1);
        HashSet<SymbolNode> sns = new HashSet<SymbolNode>();
        sns.add(symbolNode1);
        System.out.println(sns.contains(symbolNode2));
        System.out.println(symbolNode1==symbolNode2);
    }
}
