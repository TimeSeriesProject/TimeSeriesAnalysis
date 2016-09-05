package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * @author LYH
 * 显示异常点和异常度*/
public class ChartPanelShowAbc extends JPanel{
	JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);
    public static int timeGranunity = 3600;
    private ChartPanelShowAbc() {
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
    ChartPanelShowAbc(String title, String timeAxisLabel, String valueAxisLabel,
                      XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/){
        this();
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
            Date date = item.getTime();
            double val = Double.parseDouble(item.getData());
            //ts.addOrUpdate(items.getTimePeriodOfElement(i), val);
            ts.add(i,val);
        }
        tsc.addSeries(ts);
        chart.getXYPlot().setDataset(tsc);
    }
    
    //获取原始数据集
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
            //xyseries.add((double) temp.getTime().getTime(),Double.parseDouble(temp.getData())); // 对应的横轴
            xyseries.add(i,Double.parseDouble(temp.getData()));
        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    
    //获取异常度数据集
    public static XYDataset createAbnormalDataset(DataItems abnor)
    {  // 统计异常点的长度
        int length=abnor.getLength();
        XYSeries xyseries = new XYSeries("异常度");
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        //添加数据值
        for (int i = 0; i < length; i++) {

            DataItem temp=new DataItem();
            temp=abnor.getElementAt(i);
            xyseries.add(i,Double.parseDouble(temp.getData()));
        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }
    
    //获取异常点数据集
    public static XYDataset createAbnormalDataset1(DataItems nor,DataItems abnor)
    {  // 统计异常点的长度
        int length=abnor.getLength();
        XYSeries xyseries = new XYSeries("异常点");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        //添加数据值

        if(nor.getData().size()>0){
        	Date date1 = nor.getTime().get(0);
        	
	        //添加数据值
	
	        for (int i = 0; i < length; i++) {
	
	            DataItem temp=new DataItem();
	            temp=abnor.getElementAt(i);	
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
    public static JFreeChart createChart(DataItems oriItems,DataItems outdegree,DataItems outItems)
    {

        //设置异常点提示红点大小
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-4D, -4D, 6D, 6D);

        XYDataset xydataset = createNormalDataset(oriItems);//原始值
        XYDataset xydataset1 = createAbnormalDataset(outdegree);//异常度
        XYDataset xyDataset2 = createAbnormalDataset1(oriItems,outItems);//异常点
        
        JFreeChart jfreechart = ChartFactory.createScatterPlot("异常度检测", "时间", "值", null);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setDomainPannable(true);
        xyplot.setOrientation(PlotOrientation.VERTICAL);
       
        NumberAxis numberaxis1 = new NumberAxis("异常度");
        xyplot.setRangeAxis(1, numberaxis1);
        numberaxis1.setAutoTickUnitSelection(false);//数据轴的数据标签是否自动确定
        //numberaxis1.setTickUnit(new NumberTickUnit(1D));  //y轴单位间隔为1
        numberaxis1.setRange(0,10);
        numberaxis1.setUpperMargin(1);
        xyplot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        xyplot.setDataset(0, xydataset);
        xyplot.setDataset(1, xydataset1);
        xyplot.setDataset(2, xyDataset2);
        
        //设置同一个横轴显示两组数据。
        xyplot.mapDatasetToRangeAxis(1, 1);
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        
        //设置原始数据显示方式
        XYLineAndShapeRenderer xylineandshaperenderer0 = new XYLineAndShapeRenderer(); //绑定xydataset,原始数据
        xyplot.setDataset(0, xydataset);
        xyplot.setRenderer(0, xylineandshaperenderer0);
        xylineandshaperenderer0.setSeriesShapesVisible(0,false);
        xylineandshaperenderer0.setSeriesLinesVisible(0, true);
        xylineandshaperenderer0.setSeriesShape(0, double1);
        xylineandshaperenderer0.setSeriesPaint(0, Color.black);
        xylineandshaperenderer0.setSeriesFillPaint(0, Color.black);
        xylineandshaperenderer0.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
        xylineandshaperenderer0.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        
        //设置异常度显示方法
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();//绑定xydataset2,异常度显示        
        xyplot.setRenderer(1, xylineandshaperenderer1);
        xylineandshaperenderer1.setSeriesShapesVisible(0,false);
        xylineandshaperenderer1.setSeriesLinesVisible(0, true);
        xylineandshaperenderer1.setSeriesShape(0, double1);
        xylineandshaperenderer1.setSeriesPaint(0, new Color(65,105,225));
        xylineandshaperenderer1.setSeriesFillPaint(0, new Color(65,105,225));
        xylineandshaperenderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}:({1} , {2})", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new DecimalFormat("#.00")));
        
        //设置异常点显示方式
        XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();//绑定xydataset1,异常点显示
        xyplot.setRenderer(2, xylineandshaperenderer2);
        xylineandshaperenderer2.setSeriesLinesVisible(0, false);
        xylineandshaperenderer2.setSeriesShape(0, double1);
        xylineandshaperenderer2.setSeriesPaint(0, Color.red);
        xylineandshaperenderer2.setSeriesFillPaint(0, Color.red);
        xylineandshaperenderer2.setSeriesOutlinePaint(0, Color.gray);
        xylineandshaperenderer2.setUseFillPaint(true);
        xylineandshaperenderer2.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        xylineandshaperenderer2.setBaseItemLabelsVisible(true);
        return jfreechart;
    }
}
