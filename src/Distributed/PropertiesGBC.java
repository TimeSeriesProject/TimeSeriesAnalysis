package Distributed;

import java.awt.*;

/**
 * Created by zsc on 2016/5/20.
 * <p>
 * GridBagConstraints的设置
 */
public class PropertiesGBC extends GridBagConstraints {
    //初始化左上角位置
    public PropertiesGBC(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
    }

    //初始化左上角位置和所占行数和列数
    public PropertiesGBC(int gridx, int gridy, int gridwidth, int gridheight) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;//组件的横向宽度，也就是指组件占用的列数；
        this.gridheight = gridheight;//组件的纵向长度，也就是指组件占用的行数；
    }

    //对齐方式
    public PropertiesGBC setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    //是否拉伸及拉伸方向
    public PropertiesGBC setFill(int fill) {
        //如果显示区域比组件的区域大的时候，可以用来控制组件的行为。
        //控制组件是垂直填充，还是水平填充，或者两个方向一起填充；
        this.fill = fill;
        return this;
    }

    //x和y方向上的增量
    public PropertiesGBC setWeight(double weightx, double weighty) {
        this.weightx = weightx;//指行的权重，告诉布局管理器如何分配额外的水平空间；同时也是最初的比例,设置为0，不变化
        this.weighty = weighty;//指列的权重，告诉布局管理器如何分配额外的垂直空间；
        return this;
    }

    //外部填充
    public PropertiesGBC setInsets(int distance) {
        this.insets = new Insets(distance, distance, distance, distance);
        return this;
    }

    //外填充
    public PropertiesGBC setInsets(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    //内填充，设置组件的最小大小，保证组件不会收缩到ipadx,ipady所确定的大小以下
    public PropertiesGBC setIpad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }
}
