package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;

import lineAssociation.Linear;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class PanelShowResultAssLine extends JPanel{

	public PanelShowResultAssLine(){
		// 创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("微软雅黑",Font.BOLD,12));
        // 设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 10));
        // 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("微软雅黑",Font.BOLD,12));
        // 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);

        setLayout(new BorderLayout());
	}
	public PanelShowResultAssLine(DataItems oriDataItems,List<TreeMap<Integer, Linear>> linesList){
		this();		
	}
	public XYDataset createOriDataset(DataItems oriDataItems){
		XYSeries xyseries = new XYSeries("线段化");
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        for(int i=0;i<oriDataItems.getLength();i++){
        	double data = Double.parseDouble(oriDataItems.getData().get(i));
        	xyseries.add(i,data);
        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
	}
	public XYDataset createLineDataset(TreeMap<Integer, Linear> lines,DataItems oriDataItems){
		
        XYSeries xyseries = new XYSeries("线段化");
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //为数据集添加数据
        Iterator it = lines.entrySet().iterator();
        while(it.hasNext()){
        	Map.Entry<Integer, Linear> entry = (Map.Entry<Integer, Linear>)it.next();
        	int start = entry.getKey();
        	double data = Double.parseDouble(oriDataItems.getData().get(start));
        	xyseries.add(start,data);
        }
        int len = oriDataItems.getLength();
        xyseries.add(len-1,Double.parseDouble(oriDataItems.getData().get(len-1)));
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
	}
	public JFreeChart createChart(DataItems oriDataItems,List<TreeMap<Integer, Linear>> linesList){
		XYDataset xydataset0 = createOriDataset(oriDataItems);
		XYDataset xydataset1 = createLineDataset(linesList.get(0), oriDataItems);
		JFreeChart jfreechart = ChartFactory.createScatterPlot("关联规则线段化", "序列编号", "流量", xydataset0);
		XYPlot xyPlot = jfreechart.getXYPlot();
		xyPlot.setDataset(0, xydataset0);
		xyPlot.setDataset(1, xydataset1);
		
		NumberAxis numberaxis = (NumberAxis) xyPlot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        NumberAxis xAxis=(NumberAxis)xyPlot.getDomainAxis();
        xAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-2D, -2D, 3D, 3D);
		
        //设置原始数据显示
        XYLineAndShapeRenderer xyLineAndShapeRenderer0 = new XYLineAndShapeRenderer();
        Shape itemShape = ShapeUtilities.createDiamond((float) 3);
        xyLineAndShapeRenderer0.setSeriesLinesVisible(0, true);
        xyLineAndShapeRenderer0.setBaseShapesVisible(false);
        xyLineAndShapeRenderer0.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D));
        xyLineAndShapeRenderer0.setSeriesPaint(0, Color.black);
        xyLineAndShapeRenderer0.setSeriesFillPaint(0, Color.black);
        xyLineAndShapeRenderer0.setSeriesOutlinePaint(0, Color.black);
        xyLineAndShapeRenderer0.setSeriesStroke(0, new BasicStroke(0.5F));
        xyPlot.setRenderer(0,xyLineAndShapeRenderer0);
        //设置线段显示
        XYLineAndShapeRenderer xylineandshaperenderer1 =new XYLineAndShapeRenderer();        
        xylineandshaperenderer1.setSeriesLinesVisible(0, true);
        xylineandshaperenderer1.setBaseShapesVisible(false);
        xylineandshaperenderer1.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D));
        xylineandshaperenderer1.setSeriesPaint(0, Color.RED);
        xylineandshaperenderer1.setSeriesFillPaint(0, Color.RED);
        xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.RED);
        xylineandshaperenderer1.setSeriesStroke(0, new BasicStroke(0.8F));
        xyPlot.setRenderer(1,xylineandshaperenderer1);
        
        return jfreechart;
	}
}
