package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ChartPanelShowPartialCycle extends JPanel{
	JFreeChart chart;
	XYPlot xyplot;
	Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);
	public  ChartPanelShowPartialCycle() {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 15));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 10));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 10));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
				
		setLayout(new BorderLayout());
		
	}
	public ChartPanelShowPartialCycle(String title, String timeAxisLabel, String valueAxisLabel, 
			XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/){
		this();
		//chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset);
		chart = ChartFactory.createScatterPlot(title, timeAxisLabel, valueAxisLabel, dataset);
		xyplot = chart.getXYPlot();
		ChartPanel p = new ChartPanel(chart);
		add(p, BorderLayout.CENTER);
		 
	}
	
	public void setTitle(String title){
		chart.setTitle(title);
	}
	public void setAxisXLabel(String x){
		chart.getXYPlot().getDomainAxis().setLabel(x);
	}
	public void setAxisYLabel(String y){
		chart.getXYPlot().getRangeAxis().setLabel(y);
	}
	public JFreeChart getChart(){
		return chart;
	}
	
	public void displayDataItems(DataItems items,ArrayList<Pair<Integer,Integer>> list){
		if (items == null)
			return;
		//TimeSeriesCollection tsc = new TimeSeriesCollection();//时间显示
		XYSeriesCollection tsc = new XYSeriesCollection();//时间显示
		
		//TimeSeries ts = new TimeSeries("序列值");//生成序列图
		XYSeries ts = new XYSeries("序列值");//生成序列图
				
		int len = items.getLength();
		
		for (int i = 0; i < len; i ++){
			DataItem item = items.getElementAt(i);
			Date date = item.getTime();
			double val = Double.parseDouble(item.getData());
			ts.add(i, val);
		}
		tsc.addSeries(ts);
		for(int i=0;i<list.size();i++)
		{
			XYSeries tmpts = new XYSeries("");
			Pair<Integer,Integer> p =list.get(i);
			int st =p.getFirst();
			int ed =p.getSecond();
			for(int j=st;j<=ed;j++)
			{
				
				double val =Double.parseDouble(items.getElementAt(j).getData());
				tmpts.add(j,val);
			}
			//tmpts.add(st);
			tsc.addSeries(tmpts);
		}
		xyplot.setDataset(tsc);
		XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();
        xyplot.setRenderer(xylineandshaperenderer2);
        Shape itemShape = ShapeUtilities.createDiamond((float) 0);
        xylineandshaperenderer2.setBaseShapesVisible(false);
        xylineandshaperenderer2.setBaseLinesVisible(true);
		xylineandshaperenderer2.setSeriesShape(0, itemShape);
		xylineandshaperenderer2.setSeriesPaint(0, new Color(0,0,0));
		for(int i=0;i<list.size();i++)
		{
			xylineandshaperenderer2.setSeriesShape(i+1, itemShape);
			xylineandshaperenderer2.setSeriesPaint(i+1, new Color(255,0,0));
			xylineandshaperenderer2.setSeriesShapesVisible(i+1, true);
		}

        //设置不可见到点。
        xylineandshaperenderer2.setBaseShapesVisible(false);
        xylineandshaperenderer2.setSeriesShapesVisible(0, true);
	}
}