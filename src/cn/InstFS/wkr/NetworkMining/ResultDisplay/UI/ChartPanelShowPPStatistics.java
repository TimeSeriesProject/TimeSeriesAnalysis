package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.text.html.HTMLDocument.Iterator;

import oracle.net.aso.p;

import org.eclipse.swt.custom.ST;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.TextAnchor;
import org.openide.nodes.Children.Map;

public class ChartPanelShowPPStatistics extends JPanel{
	public ChartPanelShowPPStatistics(){
		super();
	}
	public static JFreeChart createChart(HashMap<String, String> map){
		CategoryDataset dataSet=createDataset(map) ;
		JFreeChart jFreeChart = ChartFactory.createBarChart("各路径概率分布", "路径", "概率", dataSet, PlotOrientation.VERTICAL, false, false, false);
		CategoryPlot plot = jFreeChart.getCategoryPlot();
		plot.setBackgroundPaint(Color.LIGHT_GRAY); //设置背景颜色
		plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);//横轴显示在下端(柱子竖直)或左侧(柱子水平)
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT); //数值显示在下端(柱子水平)或左侧(柱子竖直)
		CategoryAxis categoryAxis=plot.getDomainAxis();//获得横坐标
	    categoryAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));//设置横坐标字体
	    ValueAxis categoryAxis2=plot.getRangeAxis();
	    categoryAxis2.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
	    //categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);//设置角度
	    categoryAxis.setCategoryLabelPositions(CategoryLabelPositions .createUpRotationLabelPositions(Math.PI/6));//设置角度
	    //GradientPaint gradientpaint=new GradientPaint(0.0F,0.0F,Color.blue, 0.0F,0.0F,new Color(0,0,64));
	    BarRenderer renderer = new BarRenderer();
	    renderer.setBarPainter(new StandardBarPainter());
	    renderer.setBaseOutlinePaint(Color.red);
	    renderer.setDrawBarOutline(false);
        renderer.setSeriesPaint(0, new Color(255,0,0));//柱子的颜色为青色
        renderer.setSeriesOutlinePaint(0,Color.BLACK);//边框为黑色
      
        renderer.setMaximumBarWidth(0.05);  // 设置柱子宽度       
        //plot.setForegroundAlpha(1.0f); // 设置柱的透明度
        renderer.setItemMargin(0.1);//组内柱子间隔为组宽的10%
        //显示每个柱的数值，并修改该数值的字体属性
	    renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());  
	    renderer.setBaseItemLabelsVisible(true); 
	    //默认的数字显示在柱子中，通过如下两句可调整数字的显示  
	    //注意：此句很关键，若无此句，那数字的显示会被覆盖，给人数字没有显示出来的问题  
	    renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
	    renderer.setItemLabelAnchorOffset(0.1);  
	    
	    //设置每个路径所包含的平行柱的之间距离  
	    renderer.setItemMargin(1);  
	    plot.setRenderer(renderer); 
	    
	    //设置最高的一个柱与图片顶端的距离(最高柱的10%)
	    ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.2);
		return jFreeChart;
	}
	public static  CategoryDataset createDataset(HashMap<String, String> map){
		DefaultCategoryDataset dataSet=new DefaultCategoryDataset();
		java.util.Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			HashMap.Entry entry = (HashMap.Entry)  iter.next();
			dataSet.addValue(Double.parseDouble((String)entry.getValue()),"",(String)entry.getKey());
		}
		return dataSet;
	}
}

