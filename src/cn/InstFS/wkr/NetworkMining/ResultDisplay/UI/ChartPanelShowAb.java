package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

/**
 * Created by hidebumi on 2016/3/30.
 */
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
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
import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
public class ChartPanelShowAb extends JPanel{
    JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);
    public static int timeGranunity = 3600;
    private ChartPanelShowAb() {
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
    ChartPanelShowAb(String title, String timeAxisLabel, String valueAxisLabel,
                     XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/){
        this();
//        chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset);
//        ChartPanel p = new ChartPanel(chart);
//        add(p, BorderLayout.CENTER);
//
//
//        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
//
//        renderer.setBaseShapesVisible(false);
////		renderer.setBaseShape(itemShape);	// 好像不管用，必须用setSeriesShape
//        renderer.setBaseLinesVisible(true);
////		renderer.setBasePaint(new Color(0));	// 好像不管用，必须用setSeriesPaint
//
//        itemShape = ShapeUtilities.createDiamond((float) 3);
//        renderer.setSeriesShape(0, itemShape);
//        renderer.setSeriesPaint(0, new Color(255,0,0));
//
//        renderer.setSeriesShape(1, itemShape);
//        renderer.setSeriesPaint(1, new Color(0,255,0));
//
//        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
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
//        TimeSeriesCollection tsc = new TimeSeriesCollection();
//        TimeSeries ts = new TimeSeries("序列值");
        XYSeriesCollection tsc = new XYSeriesCollection();
        XYSeries ts = new XYSeries("序列值");

        int len = items.getLength();
        for (int i = 0; i < len; i ++){
            DataItem item = items.getElementAt(i);
            
            double val = Double.parseDouble(item.getData());
            //ts.addOrUpdate(items.getTimePeriodOfElement(i), val);
            Calendar cal = Calendar.getInstance();
   		 	cal.set(2014, 9, 1, 0, 0, 0);
   		 	Date date1 = cal.getTime();
   		 	Date date2 = item.getTime();
   		 	long diff = date2.getTime()-date1.getTime();
   		 	long hour = diff/(1000*60*60);

            ts.add((int)hour,val);
        }
        tsc.addSeries(ts);
        chart.getXYPlot().setDataset(tsc);
    }
    public static XYDataset createNormalDataset(DataItems normal)
    {
        //获取正常数据的长度、
        int length=normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries("正常点");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //为数据集添加数据

        for (int i = 0; i <length; i++) {
            DataItem temp=new DataItem();
            temp=normal.getElementAt(i);
//            xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData())); // 对应的横轴
//            xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData())); // 对应的横轴
            xyseries.add(i,Double.parseDouble(temp.getData()));
        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    //对异常点进行初始化
    public static XYDataset createAbnormalDataset(DataItems nor,DataItems abnor)
    {  // 统计异常点的长度
        int length=abnor.getLength();
        XYSeries xyseries = new XYSeries("异常点");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        if(nor.getData().size()>0){
        	Date date1 = nor.getTime().get(0);
        	
	        //添加数据值
	
	        for (int i = 0; i < length; i++) {
	
	            DataItem temp=new DataItem();
	            temp=abnor.getElementAt(i);
	//            xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData()));
	//            xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData()));
	   		 	Date date2 = temp.getTime();
	   		 	long diff = date2.getTime()-date1.getTime();
	   		 	long hour = diff/(1000*60*60);
	   		 	long index = hour*3600/timeGranunity;
	            xyseries.add(index,Double.parseDouble(temp.getData()));
	
	        }
	    }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    public static JFreeChart createChart(DataItems nor,DataItems abnor)
    {
        XYDataset xydataset = createNormalDataset(nor);
        //JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("异常点检测", "时间", "值", xydataset);
        JFreeChart jfreechart = ChartFactory.createScatterPlot("异常点检测", "时间", "值", xydataset);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        //xyplot.setDataset(0, xydataset);
        
        //设置异常点提示红点大小        
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-4D, -4D, 6D, 6D);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xylineandshaperenderer.setBaseShapesVisible(false);
        xylineandshaperenderer.setSeriesShape(0, double1);
        xylineandshaperenderer.setSeriesPaint(0, Color.black);
        xylineandshaperenderer.setSeriesFillPaint(0, Color.yellow);
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.gray);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(0.5F));
        xylineandshaperenderer.setSeriesLinesVisible(0, true);
        xyplot.setRenderer(0, xylineandshaperenderer);
        XYDataset xydataset1 = createAbnormalDataset(nor,abnor);
        /*if(nor.getLength() > 0){
        	xydataset1 = createAbnormalDataset(nor,abnor);
        }*/
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
        
        xyplot.setDataset(1, xydataset1);
        
        xyplot.setRenderer(1, xylineandshaperenderer1);
        xylineandshaperenderer1.setSeriesLinesVisible(0, false);
        xylineandshaperenderer1.setSeriesShape(0, double1);
        xylineandshaperenderer1.setSeriesPaint(0, Color.red);
        xylineandshaperenderer1.setSeriesFillPaint(0, Color.red);
        xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.gray);
        xylineandshaperenderer1.setUseFillPaint(true);
        xylineandshaperenderer1.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        xylineandshaperenderer1.setBaseItemLabelsVisible(true);
                
        return jfreechart;
    }
}

