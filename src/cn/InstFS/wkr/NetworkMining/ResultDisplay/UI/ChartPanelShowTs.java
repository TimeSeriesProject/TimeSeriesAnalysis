package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

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
import org.jfree.util.ShapeUtilities;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ChartPanelShowTs extends JPanel{
	JFreeChart chart;
	Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);
	private ChartPanelShowTs() {
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
	ChartPanelShowTs(String title, String timeAxisLabel, String valueAxisLabel, 
			XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/){
		this();
		chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset);
		ChartPanel p = new ChartPanel(chart);
		add(p, BorderLayout.CENTER);
		
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		
		renderer.setBaseShapesVisible(false);
//		renderer.setBaseShape(itemShape);	// 好像不管用，必须用setSeriesShape
		renderer.setBaseLinesVisible(true);
//		renderer.setBasePaint(new Color(0));	// 好像不管用，必须用setSeriesPaint
		
		itemShape = ShapeUtilities.createDiamond((float) 3);
		renderer.setSeriesShape(0, itemShape);		
		renderer.setSeriesPaint(0, new Color(0,0,0));

		renderer.setSeriesShape(1, itemShape);		
		renderer.setSeriesPaint(1, new Color(0,255,0));
		
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
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
	
	public void displayDataItems(DataItems items){
		if (items == null)
			return;
		TimeSeriesCollection tsc = new TimeSeriesCollection();
		
		TimeSeries ts = new TimeSeries("序列值");
				
		int len = items.getLength();
		for (int i = 0; i < len; i ++){
			DataItem item = items.getElementAt(i);
			Date date = item.getTime();
			double val = Double.parseDouble(item.getData());
			ts.addOrUpdate(items.getTimePeriodOfElement(i), val);			
		}
		tsc.addSeries(ts);
		chart.getXYPlot().setDataset(tsc);
	}

}
