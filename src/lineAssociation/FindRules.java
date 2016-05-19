package lineAssociation;

import java.util.*;

/**
 * Created by xzbang on 2016/3/15.
 */
public class FindRules {
    public HashMap<Integer,TreeMap<Integer,SymbolNode>> symbolSeries;
    public HashMap<Integer,TreeNode> treeSeries = new HashMap<Integer, TreeNode>();
    public List<Rule> rulesSet = new ArrayList<Rule>();
    public HashMap<SymbolNode,Integer> symbolNodeSup = new HashMap<SymbolNode, Integer>();    //
    public ArrayList<TreeNode> current = new ArrayList<TreeNode>();

    public int T = 1;//时间约束值
    public int tnum = 148;//原始时间序列长度
    public int minsupnum = 8;//支持度计数阈值
    public double minsup = minsupnum*1.0/tnum;//支持度阈值
//    public double minsup = 0.08;
    public double mincon = 0.0;//置信度阈值
    public double mininf = 0.0;//兴趣度阈值

    public FindRules(){}
    public FindRules(HashMap<Integer,TreeMap<Integer,SymbolNode>> symbolSeries){
       
    	this.symbolSeries = symbolSeries;
        for(int i : symbolSeries.keySet()){
            SymbolNode headSymbol = new SymbolNode(-1,i);
            TreeNode headTree = new TreeNode(headSymbol,0);
            treeSeries.put(i,headTree);
        }
        //设置相关支持度
        int sum = 0;
        for(int key:symbolSeries.keySet()){
        	sum += symbolSeries.get(key).size();
        }
        tnum = (int) Math.ceil(sum/symbolSeries.size());
        minsupnum = (int) Math.ceil(tnum*0.05);
        minsup = minsupnum*1.0/tnum;
        
    }

    public void run(){
        beforeCreate();   //单挑时间序列没有达到支持度的聚类符号去掉。
        createTree();     //构造两两序列间的模式树
        cutTree();        //剪枝
//        extendTree();     //将二元关联扩展到多元关联规则挖掘
    }

    /**
     * 预处理
     * 删除支持度计数较小的符号模式
     */
    public void beforeCreate(){
        //统计
        for(int i : symbolSeries.keySet()) {
            TreeMap<Integer, SymbolNode> isymbol = symbolSeries.get(i);
            TreeNode itree = treeSeries.get(i);
            for(int k : isymbol.keySet()){
                SymbolNode symbolNode = isymbol.get(k);
                if(itree.children_list.containsKey(symbolNode)){
                    TreeNode childtree = itree.children_list.get(symbolNode);
                    childtree.node_time_list.add(k);
                    childtree.support_num++;   //? 频繁项统计
                }else{
                    TreeNode childtree = new TreeNode(symbolNode,1);
                    childtree.node_time_list.add(k);
                    itree.children_list.put(symbolNode,childtree);
                }
            }
        }
        //删除
        for(int i : treeSeries.keySet()){
            TreeMap<Integer, SymbolNode> isymbol = symbolSeries.get(i);
            TreeNode itree = treeSeries.get(i);
            Set<SymbolNode> delete = new HashSet<SymbolNode>();
            for(SymbolNode symbolNode : itree.children_list.keySet()){
                TreeNode childtree = itree.children_list.get(symbolNode);
                if((childtree.support_num*1.0/tnum) < minsup){   //若不满足要求，则去掉该节点
                    for(int j : childtree.node_time_list){
                        isymbol.remove(j);
                    }
                    delete.add(symbolNode);
                }else{
                    symbolNodeSup.put(symbolNode,childtree.support_num);
                }
            }
            for(SymbolNode symbolNode : delete){
                itree.children_list.remove(symbolNode);
            }
        }
    }

    /**
     * 构造初始的频繁模式树
     */
    public void createTree(){
    	
        for(int i : symbolSeries.keySet()){
            TreeMap<Integer,SymbolNode> isymbol = symbolSeries.get(i);
            TreeNode itree = treeSeries.get(i);
            for(int j : symbolSeries.keySet()){
                if(i==j)
                	continue;
                TreeMap<Integer,SymbolNode> jsymbol = symbolSeries.get(j);
                for(int k : isymbol.keySet()){
                    SymbolNode isymbolNode = isymbol.get(k);
                    TreeNode ichildNode = itree.children_list.get(isymbolNode);
                    for(int r = k;r <= k+T;r++){
                        if(jsymbol.containsKey(r)){
                            SymbolNode jsymbolNode = jsymbol.get(r);
                            if(ichildNode.children_list.containsKey(jsymbolNode)){
                                TreeNode jchNode = ichildNode.children_list.get(jsymbolNode);
                                if(!jchNode.node_time_list.contains(r)&&!jchNode.parent_time_list.contains(k)){
                                    jchNode.node_time_list.add(r);
                                    jchNode.parent_time_list.add(k);
                                    jchNode.parent_node_time_map.put(k,r);
                                    jchNode.support_num++;
                                }
                            }else{
                                TreeNode jchNode = new TreeNode(jsymbolNode,1);
                                jchNode.parent_symbol_list.add(isymbolNode);
                                jchNode.parent_series_set.add(isymbolNode.belong_series);
                                jchNode.node_time_list.add(r);
                                jchNode.parent_time_list.add(k);
                                jchNode.parent_node_time_map.put(k,r);
                                ichildNode.children_list.put(jsymbolNode,jchNode);
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * 剪枝
     */
    public void cutTree(){
        for(int i : treeSeries.keySet()) {
            TreeMap<Integer, SymbolNode> isymbol = symbolSeries.get(i);
            TreeNode itree = treeSeries.get(i);
            for(SymbolNode isymbolNode : itree.children_list.keySet()){
                TreeNode ichildTree = itree.children_list.get(isymbolNode);
                Set<SymbolNode> delete = new HashSet<SymbolNode>();
                for(SymbolNode jsymbolNode : ichildTree.children_list.keySet()){
                    TreeNode jchTree = ichildTree.children_list.get(jsymbolNode);
                    if((jchTree.support_num*1.0/tnum)<minsup){
                        delete.add(jsymbolNode);
                    }else{
                        double con = jchTree.support_num*1.0/ichildTree.support_num;
//                        if(con < mincon)delete.add(jsymbolNode);//不能基于置信度进行剪枝
                        if(con >= mincon){
                            double inf = con*tnum/symbolNodeSup.get(jsymbolNode);
//                            if(inf<mininf)delete.add(jsymbolNode);//不能基于兴趣度进行剪枝
                            if(inf >= mininf){
                                ArrayList<SymbolNode> before = new ArrayList<SymbolNode>(jchTree.parent_symbol_list);
                                Rule rule = new Rule(before,jsymbolNode,jchTree.support_num,jchTree.support_num*1.0/tnum,con,inf);
                                rule.parent_node_time_map = jchTree.parent_node_time_map;//添加规则映射表
                                rulesSet.add(rule);
                                current.add(jchTree);
                                //test
                                if(rule.before.get(0).equals(new SymbolNode(124,0))&&rule.after.equals(new SymbolNode(100,1))){
                                    System.out.println(new TreeSet<Integer>(jchTree.parent_time_list));
                                    System.out.println(new TreeSet<Integer>(jchTree.node_time_list));
                                }
                            }
                        }
                    }
                }

                for(SymbolNode symbolNode : delete){
                    ichildTree.children_list.remove(symbolNode);
                }
            }
        }
    }

    /**
     * 扩展子树
     */
    public void extendTree(){

        ArrayList<TreeNode> next = new ArrayList<TreeNode>();
        while(current.size()!=0) {
            for (TreeNode treeNode : current) {
                TreeNode blTree = treeSeries.get(treeNode.symbolNode.belong_series);
                TreeNode ichildTree = blTree.children_list.get(treeNode.symbolNode);
                for(SymbolNode jsymbolNode : ichildTree.children_list.keySet()){
                    if(treeNode.parent_series_set.contains(jsymbolNode.belong_series))continue;
                    TreeNode jchNode = ichildTree.children_list.get(jsymbolNode);
                    int sup_num = 0;
                    HashSet<Integer> new_parent = new HashSet<Integer>();
                    HashSet<Integer> new_node = new HashSet<Integer>();
                    HashMap<Integer,Integer> new_parent_node = new HashMap<Integer, Integer>();

                    for(int i : treeNode.node_time_list){
                        if(jchNode.parent_time_list.contains(i)){
                            sup_num++;
                            new_parent.add(i);
                            int r = jchNode.parent_node_time_map.get(i);
                            new_node.add(r);
                            new_parent_node.put(i,r);
                        }
                    }
                    double sup = sup_num*1.0/tnum;
                    if(sup<minsup)continue;
                    else {

                        TreeNode newChild = new TreeNode(jsymbolNode,sup_num,new_parent,new_node,new HashMap<SymbolNode,TreeNode>(),new_parent_node);

                        //添加前置条件列表
                        Set<Integer> new_parent_series_set = new HashSet<Integer>();
                        ArrayList<SymbolNode> new_parent_symbol_list = new ArrayList<SymbolNode>();
                        new_parent_series_set.addAll(treeNode.parent_series_set);
                        new_parent_series_set.add(treeNode.symbolNode.belong_series);
                        new_parent_symbol_list.addAll(treeNode.parent_symbol_list);
                        new_parent_symbol_list.add(treeNode.symbolNode);
                        newChild.parent_series_set=new_parent_series_set;
                        newChild.parent_symbol_list = new_parent_symbol_list;

                        treeNode.children_list.put(jsymbolNode,newChild);
                        next.add(newChild);

                        double con = sup_num * 1.0 / treeNode.support_num;
                        double inf = con / sup;

                        if(con<mincon||inf<mininf)continue;
                        else{
                            ArrayList<SymbolNode> before = new ArrayList<SymbolNode>(newChild.parent_symbol_list);
                            Rule rule = new Rule(before,jsymbolNode,sup_num,sup,con,inf);
                            rulesSet.add(rule);//输出规则
                        }
                    }

                }
            }
            current = next;
            next = new ArrayList<TreeNode>();
        }
    }

}
