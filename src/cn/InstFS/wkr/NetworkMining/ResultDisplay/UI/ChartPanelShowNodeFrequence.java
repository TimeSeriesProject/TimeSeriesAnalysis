package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

/**
 * Created by hidebumi on 2016/3/30.
 */
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
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
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
public class ChartPanelShowNodeFrequence extends JPanel {
    JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);

    private ChartPanelShowNodeFrequence() {
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

//   public  ChartPanelShowNodeFrequence(TaskElement task,DataItems dataitems
//                     /*, boolean legend, boolean tooltips, boolean urls*/) {
//        this();
//        chart=createChart(task,dataitems);
//        ChartPanel p = new ChartPanel(chart);
//		add(p, BorderLayout.CENTER);
////       
//    }


    public void setTitle(String title) {
        chart.setTitle(title);
    }

    public void setAxisXLabel(String x) {
        chart.getXYPlot().getDomainAxis().setLabel(x);
    }

    public void setAxisYLabel(String y) {
        chart.getXYPlot().getRangeAxis().setLabel(y);
    }

    public JFreeChart getChart() {
        return chart;
    }
    public ChartPanelShowNodeFrequence(String title, String timeAxisLabel, String valueAxisLabel, 
			TaskElement task,DataItems dataitems/*, boolean legend, boolean tooltips, boolean urls*/){
		this();
		XYDataset dataset;
		dataset =createDataset(task,dataitems);
		
		chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset);
		ChartPanel p = new ChartPanel(chart);
		add(p, BorderLayout.CENTER);
		
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		
		renderer.setBaseShapesVisible(false);
//		renderer.setBaseShape(itemShape);	// 好像不管用，必须用setSeriesShape
		renderer.setBaseLinesVisible(true);
		 XYPlot plot = chart.getXYPlot();
         // 数据轴属性部分
         NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
         rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
         rangeAxis.setAutoRangeIncludesZero(true); //自动生成
         rangeAxis.setUpperMargin(0.20);
         rangeAxis.setLabelAngle(Math.PI / 2.0); 
         rangeAxis.setAutoRange(false);
         // 数据渲染部分 主要是对折线做操作
         renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());  
         plot.setRenderer(renderer);
         //区域渲染部分
  
//		renderer.setBasePaint(new Color(0));	// 好像不管用，必须用setSeriesPaint
		
		itemShape = ShapeUtilities.createDiamond((float) 3);
		for(int i=0;i<dataset.getSeriesCount();i++)
		{
			renderer.setSeriesShape(i, itemShape);		
			renderer.setSeriesPaint(i, Color.GREEN);
		}
		
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
		
    }
    public XYDataset createDataset(TaskElement task,DataItems items) {
    	
    	 Set<String> nodeSet = new HashSet<String>();
    	 Map<String,Integer> nodeMap = new HashMap<String,Integer>();
    	 for(int i=0;i<items.getLength();i++)
    	 {
    		 for(Map.Entry<String, Integer>entry:items.getElementAt(i).getNonNumData().entrySet())
    		 {
    			 nodeSet.add(entry.getKey());
    		 }
    	 }
    	String [] nodeArray =null; 
    	nodeArray=nodeSet.toArray(new String[0]);
    	 int index=0;
    	 for(String node:nodeSet)
    	 {
    		 nodeMap.put(node,index);
    		 index++;
    	 }
    	 TimeSeriesCollection xySeriesCollection = new TimeSeriesCollection();
    	 System.out.println("itemlen"+items.getLength());
         for(int i=0;i<nodeArray.length;i++)
         {
      		TimeSeries ts = new TimeSeries(nodeArray[i]);
      				
      		int len = items.getLength();
      	
        	  for(int j=0;j<items.getLength();j++)
              {
        		  Map<String,Integer> map =items.getElementAt(j).getNonNumData();
        		  if(map.containsKey(nodeArray[i]))
        		  {
        			  double tmp =2.0*(i+1)+1;
        			  ts.addOrUpdate(items.getTimePeriodOfElement(j),tmp);
        			  
//        			  System.out.println(items.getTimePeriodOfElement(j)+" "+j);
//        			  if(j+1<items.getLength())
//        				  ts.addOrUpdate(items.getTimePeriodOfElement(j+1),2*(i+1)+1);
        		  }
        		  else
        		  {
        			  double tmp =2.0*(i+1);
//        			  System.out.println(items.getTimePeriodOfElement(j)+" *"+j);
        			 
        			  ts.addOrUpdate(items.getTimePeriodOfElement(j),tmp);
//        			  if(j+1<items.getLength())
//        			  ts.addOrUpdate(items.getTimePeriodOfElement(j+1).next(),2*(i+1)+1);
        		  }
              }
        	  xySeriesCollection.addSeries(ts);
         }
       return xySeriesCollection;
    }

    public void displayDataItems(DataItems items){
		
	}
   
}

