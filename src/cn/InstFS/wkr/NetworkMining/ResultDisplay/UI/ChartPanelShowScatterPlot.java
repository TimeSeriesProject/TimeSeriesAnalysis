package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ObjectList;
import org.jfree.util.ShapeUtilities;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ChartPanelShowScatterPlot extends JPanel{
	JFreeChart chart;
	private Shape itemShape;
	public ChartPanelShowScatterPlot() {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 15));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("微软雅黑",Font.BOLD,12));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("微软雅黑",Font.BOLD,12));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
				
		setLayout(new BorderLayout());
		
	}
	ChartPanelShowScatterPlot(String title, String timeAxisLabel, String valueAxisLabel, 
			XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/){
		this();
//		chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset);
		chart = ChartFactory.createScatterPlot(title, timeAxisLabel, valueAxisLabel, dataset);
		ChartPanel p = new ChartPanel(chart);
		add(p, BorderLayout.CENTER);
		
		XYPlot plot = chart.getXYPlot();
		XYPlot plot1=chart.getXYPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		XYLineAndShapeRenderer renderer1=(XYLineAndShapeRenderer) plot1.getRenderer();
		renderer.setBaseShapesVisible(true);
//		renderer.setBaseShape(itemShape);	// 好像不管用，必须用setSeriesShape
		renderer.setBaseLinesVisible(true);
//		renderer.setBasePaint(new Color(0));	// 好像不管用，必须用setSeriesPaint
		
		itemShape = ShapeUtilities.createDiamond((float) 3);
		renderer.setSeriesShape(0, itemShape);
		renderer.setSeriesPaint(0, Color.red);

		renderer.setSeriesShape(1, itemShape);
		renderer.setSeriesPaint(1, new Color(0,255,0));

		renderer.setSeriesShape(2, itemShape);
		renderer.setSeriesPaint(2, Color.black);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new DecimalFormat("#.00"), new DecimalFormat("#.00")));
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
	
	public void displayDataItems(DataItems items,DataItems items1,DataItems items2, String lineLabel,String lineLabel2,String lineLabel3){
		if(items == null)
			return;
		XYSeries series = new XYSeries(lineLabel);
		int len = items.getLength();
		List<String>datas = items.getData();
		for (int i = 0; i < len; i ++)
			series.add(i+1, Double.parseDouble(datas.get(i)));
		XYSeries series1 = new XYSeries(lineLabel2);
		int len1 = items.getLength();
		List<String>datas1 = items1.getData();
		for (int i = 0; i < len1; i ++)
			series1.add(i+1, Double.parseDouble(datas1.get(i)));
		XYSeriesCollection dataset = new XYSeriesCollection();

		XYSeries series2 = new XYSeries(lineLabel3);
		int len2 = items.getLength();
		List<String>datas2 = items2.getData();
		for (int i = 0; i < len2; i ++)
			series2.add(i+1, Double.parseDouble(datas2.get(i)));
		dataset.addSeries(series);
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		chart.getXYPlot().setDataset(dataset);

//		dataset1.addSeries(series1);
//		chart.getXYPlot().setDataset(1,dataset1);
		updateUI();
		validate();

	}
	public void displayDataItems(Double [] items, String lineLabel){
		if(items == null)
			return;
		XYSeries series = new XYSeries(lineLabel);
		int len = items.length;
		for (int i = 0; i < len; i ++)
			series.add(i+1, items[i]);
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		chart.getXYPlot().setDataset(dataset);
	}
	public void displayDataItems(String title,String timeAxisLabel,String valueAxisLabel,String lineLabel,Double [] items){
		if(items == null)
			return;
		XYSeries series = new XYSeries(lineLabel);
		int len = items.length;
		for (int i = 0; i < len; i ++)
			series.add(i+1, items[i]);
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		chart.setTitle(title);
		XYPlot plot = chart.getXYPlot();
		plot.setDataset(dataset);
		NumberAxis valeAxis = (NumberAxis)plot.getRangeAxis();
		NumberAxis timeAxis = (NumberAxis)plot.getDomainAxis();
		valeAxis.setLabel(valueAxisLabel);
		timeAxis.setLabel(timeAxisLabel);
	}
//	public static void main(String []args){
//		JFrame f = new JFrame();
//		ChartPanelShowScatterPlot p = new ChartPanelShowScatterPlot("title","x","y",null);
//		f.setContentPane(p);
//		f.setVisible(true);
//		f.setSize(800, 600);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		p.displayDataItems(null);
//	}

}
