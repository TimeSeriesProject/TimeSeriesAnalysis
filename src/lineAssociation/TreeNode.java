package lineAssociation;

import java.util.*;

/**
 * Created by xzbang on 2016/3/15.
 */
public class TreeNode {
    public SymbolNode symbolNode;
    public int support_num;//当前节点与所有祖先节点同时出现的支持度计数
    public HashSet<Integer> parent_time_list;//当前节点与父亲节点同时出现时父亲节点的时间列表
    public HashSet<Integer> node_time_list;//当前节点与父亲节点同时出现时当前节点的时间列表
    public HashMap<Integer,Integer> parent_node_time_map;//父亲节点与当前节点的映射表
    public HashMap<SymbolNode,TreeNode> children_list;//子节点列表

    //构造函数中没有的两个量
    public Set<Integer> parent_series_set = new HashSet<Integer>();
    public ArrayList<SymbolNode> parent_symbol_list = new ArrayList<SymbolNode>();

    public TreeNode(){}
    public TreeNode(SymbolNode symbolNode,
                    int support_num){
        this.symbolNode = symbolNode;
        this.support_num = support_num;
        this.parent_time_list = new HashSet<Integer>();
        this.node_time_list = new HashSet<Integer>();
        this.children_list = new HashMap<SymbolNode,TreeNode>();
        this.parent_node_time_map = new HashMap<Integer,Integer>();
    }
    public TreeNode(SymbolNode symbolNode,
                    int support_num,
                    HashSet<Integer> parent_time_list,
                    HashSet<Integer> node_time_list,
                    HashMap<SymbolNode,TreeNode> children_list,
                    HashMap<Integer,Integer> parent_node_time_map){
        this.symbolNode = symbolNode;
        this.support_num = support_num;
        this.parent_time_list = parent_time_list;
        this.node_time_list = node_time_list;
        this.children_list = children_list;
        this.parent_node_time_map = parent_node_time_map;
    }
}
