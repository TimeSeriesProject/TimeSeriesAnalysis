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
public class ChartPanelShowPP extends JPanel {
    JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);

    private ChartPanelShowPP() {
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

    ChartPanelShowPP(String title, String timeAxisLabel, String valueAxisLabel,
                     XYDataset dataset/*, boolean legend, boolean tooltips, boolean urls*/) {
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

    public void displayDataItems(DataItems items) {
        if (items == null)
            return;
        TimeSeriesCollection tsc = new TimeSeriesCollection();

        TimeSeries ts = new TimeSeries("序列值");

        int len = items.getLength();
        for (int i = 0; i < len; i++) {
            DataItem item = items.getElementAt(i);
            Date date = item.getTime();
            double val = Double.parseDouble(item.getData());
            ts.addOrUpdate(items.getTimePeriodOfElement(i), val);
        }
        tsc.addSeries(ts);
        chart.getXYPlot().setDataset(tsc);
    }

    public static XYDataset createNormalDataset(DataItems normal) {
        //获取正常数据的长度、
        int length = normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries("");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //为数据集添加数据

        for (int i = 0; i < length; i++) {
            DataItem temp = new DataItem();
            temp = normal.getElementAt(i);
            xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData())); // 对应的横轴

        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }

    //对异常点进行初始化
    public static XYDataset createAbnormalDataset(DataItems abnor) {  // 统计异常点的长度
        int length = abnor.getLength();
        XYSeries xyseries = new XYSeries("");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();


        //添加数据值

        for (int i = 0; i < length; i++) {

            DataItem temp = new DataItem();
            temp = abnor.getElementAt(i);
            xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData()));
            xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData()));

        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }

    public static XYDataset createmodeDataset(DataItems normal,String name) {
        //获取正常数据的长度、
        int length = normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries(name);

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //为数据集添加数据

        for (int i = 0; i < length; i++) {
            DataItem temp = new DataItem();
            temp = normal.getElementAt(i);
            xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData())); // 对应的横轴

        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }

    public static JFreeChart createChart(ArrayList<DataItems> _nor_model,ArrayList<String> name, DataItems nor, DataItems abnor,int period,int firstperiod) {
        XYDataset xydataset = createNormalDataset(nor);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("周期值: "+period+"    "+"最可能子周期值 : "+firstperiod, "时间", "值", xydataset);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-4D, -4D, 6D, 6D);
        //设置异常点提示红点大小
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        //设置不可看到点。
        xylineandshaperenderer.setSeriesLinesVisible(0, true);
        xylineandshaperenderer.setBaseShapesVisible(false);
        xylineandshaperenderer.setSeriesShape(0, double1);
        xylineandshaperenderer.setSeriesPaint(0, Color.white);
        xylineandshaperenderer.setSeriesFillPaint(0, Color.white);
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.white);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(0.5F));
        //设置显示数据点
//        xylineandshaperenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
//        xylineandshaperenderer.setBaseItemLabelsVisible(true);

        XYDataset xydataset1 = createAbnormalDataset(abnor);
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
        xyplot.setDataset(1, xydataset1);
        xyplot.setRenderer(1, xylineandshaperenderer1);
        //设置不可见到点。
        xylineandshaperenderer1.setBaseShapesVisible(false);
        //设置可以看见线。
        xylineandshaperenderer1.setSeriesLinesVisible(0, true);
        xylineandshaperenderer1.setSeriesShape(0, double1);
        //设置线和点的颜色。
        xylineandshaperenderer1.setSeriesPaint(0, Color.white);
        xylineandshaperenderer1.setSeriesFillPaint(0, Color.white);
        xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.white);
        xylineandshaperenderer1.setUseFillPaint(true);
        xylineandshaperenderer1.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        xylineandshaperenderer1.setSeriesStroke(0, new BasicStroke(0.5F));

        //xylineandshaperenderer1.setBaseItemLabelsVisible(true);
        for (int i = 0; i < _nor_model.size(); i++) {
            if(i==0) {
                XYDataset xydataset2 = createmodeDataset(_nor_model.get(0),name.get(0));
                XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 2, xydataset2);
                xyplot.setRenderer(2 , xylineandshaperenderer2);
                //设置不可见到点。
                xylineandshaperenderer2.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer2.setSeriesLinesVisible(0, true);
                xylineandshaperenderer2.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer2.setSeriesPaint(0, Color.red);
                xylineandshaperenderer2.setSeriesFillPaint(0, Color.red);
                xylineandshaperenderer2.setSeriesOutlinePaint(0, Color.red);
                xylineandshaperenderer2.setUseFillPaint(true);
                xylineandshaperenderer2.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==1) {
                XYDataset xydataset3 = createmodeDataset(_nor_model.get(1),name.get(1));
                XYLineAndShapeRenderer xylineandshaperenderer3 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 3, xydataset3);
                xyplot.setRenderer(3 , xylineandshaperenderer3);
                //设置不可见到点。
                xylineandshaperenderer3.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer3.setSeriesLinesVisible(0, true);
                xylineandshaperenderer3.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer3.setSeriesPaint(0, Color.black);
                xylineandshaperenderer3.setSeriesFillPaint(0, Color.black);
                xylineandshaperenderer3.setSeriesOutlinePaint(0, Color.black);
                xylineandshaperenderer3.setUseFillPaint(true);
                xylineandshaperenderer3.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer3.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==2) {
                XYDataset xydataset4 = createmodeDataset(_nor_model.get(2),name.get(2));
                XYLineAndShapeRenderer xylineandshaperenderer4 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 4, xydataset4);
                xyplot.setRenderer(4 , xylineandshaperenderer4);
                //设置不可见到点。
                xylineandshaperenderer4.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer4.setSeriesLinesVisible(0, true);
                xylineandshaperenderer4.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer4.setSeriesPaint(0, Color.orange);
                xylineandshaperenderer4.setSeriesFillPaint(0, Color.orange);
                xylineandshaperenderer4.setSeriesOutlinePaint(0, Color.orange);
                xylineandshaperenderer4.setUseFillPaint(true);
                xylineandshaperenderer4.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer4.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==3) {
                XYDataset xydataset5 = createmodeDataset(_nor_model.get(3),name.get(3));
                XYLineAndShapeRenderer xylineandshaperenderer5 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 5, xydataset5);
                xyplot.setRenderer(5 , xylineandshaperenderer5);
                //设置不可见到点。
                xylineandshaperenderer5.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer5.setSeriesLinesVisible(0, true);
                xylineandshaperenderer5.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer5.setSeriesPaint(0, Color.blue);
                xylineandshaperenderer5.setSeriesFillPaint(0, Color.blue);
                xylineandshaperenderer5.setSeriesOutlinePaint(0, Color.blue);
                xylineandshaperenderer5.setUseFillPaint(true);
                xylineandshaperenderer5.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer5.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==4) {
                XYDataset xydataset6 = createmodeDataset(_nor_model.get(4),name.get(4));
                XYLineAndShapeRenderer xylineandshaperenderer6 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 6, xydataset6);
                xyplot.setRenderer(6 , xylineandshaperenderer6);
                //设置不可见到点。
                xylineandshaperenderer6.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer6.setSeriesLinesVisible(0, true);
                xylineandshaperenderer6.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer6.setSeriesPaint(0, Color.green);
                xylineandshaperenderer6.setSeriesFillPaint(0, Color.green);
                xylineandshaperenderer6.setSeriesOutlinePaint(0, Color.green);
                xylineandshaperenderer6.setUseFillPaint(true);
                xylineandshaperenderer6.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer6.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==5) {
                XYDataset xydataset7 = createmodeDataset(_nor_model.get(5),name.get(5));
                XYLineAndShapeRenderer xylineandshaperenderer7 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 7, xydataset7);
                xyplot.setRenderer(7 , xylineandshaperenderer7);
                //设置不可见到点。
                xylineandshaperenderer7.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer7.setSeriesLinesVisible(0, true);
                xylineandshaperenderer7.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer7.setSeriesPaint(0, Color.yellow);
                xylineandshaperenderer7.setSeriesFillPaint(0, Color.yellow);
                xylineandshaperenderer7.setSeriesOutlinePaint(0, Color.yellow);
                xylineandshaperenderer7.setUseFillPaint(true);
                xylineandshaperenderer7.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer7.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==6) {
                XYDataset xydataset8 = createmodeDataset(_nor_model.get(6),name.get(6));
                XYLineAndShapeRenderer xylineandshaperenderer8 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 8, xydataset8);
                xyplot.setRenderer(8 , xylineandshaperenderer8);
                //设置不可见到点。
                xylineandshaperenderer8.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer8.setSeriesLinesVisible(0, true);
                xylineandshaperenderer8.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer8.setSeriesPaint(0, Color.cyan);
                xylineandshaperenderer8.setSeriesFillPaint(0, Color.cyan);
                xylineandshaperenderer8.setSeriesOutlinePaint(0, Color.cyan);
                xylineandshaperenderer8.setUseFillPaint(true);
                xylineandshaperenderer8.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer8.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==7) {
                XYDataset xydataset9 = createmodeDataset(_nor_model.get(7),name.get(7));
                XYLineAndShapeRenderer xylineandshaperenderer9 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 9, xydataset9);
                xyplot.setRenderer(9 , xylineandshaperenderer9);
                //设置不可见到点。
                xylineandshaperenderer9.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer9.setSeriesLinesVisible(0, true);
                xylineandshaperenderer9.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer9.setSeriesPaint(0, Color.gray);
                xylineandshaperenderer9.setSeriesFillPaint(0, Color.gray      );
                xylineandshaperenderer9.setSeriesOutlinePaint(0, Color.gray      );
                xylineandshaperenderer9.setUseFillPaint(true);
                xylineandshaperenderer9.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer9.setSeriesStroke(0, new BasicStroke(1.5F));
            }
            if(i==8) {
                XYDataset xydataset10 = createmodeDataset(_nor_model.get(8),name.get(8));
                XYLineAndShapeRenderer xylineandshaperenderer10 = new XYLineAndShapeRenderer();
                xyplot.setDataset( 10, xydataset10);
                xyplot.setRenderer(10, xylineandshaperenderer10);
                //设置不可见到点。
                xylineandshaperenderer10.setBaseShapesVisible(false);
                //设置可以看见线。
                xylineandshaperenderer10.setSeriesLinesVisible(0, true);
                xylineandshaperenderer10.setSeriesShape(0, double1);
                //设置线和点的颜色。
                xylineandshaperenderer10.setSeriesPaint(0, Color.pink);
                xylineandshaperenderer10.setSeriesFillPaint(0, Color.pink);
                xylineandshaperenderer10.setSeriesOutlinePaint(0, Color.pink);
                xylineandshaperenderer10.setUseFillPaint(true);
                xylineandshaperenderer10.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer10.setSeriesStroke(0, new BasicStroke(1.5F));
            }


        }

//        jfreechart.getLegend().setVisible(false);
        return jfreechart;
    }
}

