package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

/**
 * Created by hidebumi on 2016/3/30.
 */
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
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
public class ChartPanelShowFI extends JPanel {
    JFreeChart chart;
    Shape itemShape; // = new Ellipse2D.Double(-2,-2, 4, 4);

    private ChartPanelShowFI() {
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

    ChartPanelShowFI(String title, String timeAxisLabel, String valueAxisLabel,
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
        XYSeries xyseries = new XYSeries("原始值");

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
        XYSeries xyseries = new XYSeries("频繁模式");

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

    public static XYDataset createmodeDataset(DataItems normal) {
        //获取正常数据的长度、
        int length = normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries(".");

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

    public static JFreeChart createChart(HashMap<String, ArrayList<DataItems>> nor_model, DataItems nor,HashMap<String,ArrayList<DataItems>> nor_model_mode) {
        XYDataset xydataset = createNormalDataset(nor);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(" 频繁项集挖掘结果", "时间", "值", xydataset);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-2D, -2D, 3D, 3D);
        //设置异常点提示红点大小
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        //设置不可看到点。
        Shape itemShape;
        itemShape = ShapeUtilities.createDiamond((float) 3);
        xylineandshaperenderer.setSeriesLinesVisible(0, false);
        xylineandshaperenderer.setBaseShapesVisible(false);
        xylineandshaperenderer.setSeriesShape(0, itemShape);
        xylineandshaperenderer.setSeriesPaint(0, Color.darkGray);
        xylineandshaperenderer.setSeriesFillPaint(0, Color.yellow);
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.gray);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(2F));
        //设置显示数据点
//        xylineandshaperenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
//        xylineandshaperenderer.setBaseItemLabelsVisible(true);

//        XYDataset xydataset1 = createAbnormalDataset(abnor);
//        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
//        xyplot.setDataset(1, xydataset1);
//        xyplot.setRenderer(1, xylineandshaperenderer1);
//        //设置不可见到点。
//        xylineandshaperenderer1.setBaseShapesVisible(false);
//        //设置可以看见线。
//        xylineandshaperenderer1.setSeriesLinesVisible(0, true);
//        xylineandshaperenderer1.setSeriesShape(0, double1);
//        //设置线和点的颜色。
//        xylineandshaperenderer1.setSeriesPaint(0, Color.black);
//        xylineandshaperenderer1.setSeriesFillPaint(0, Color.black);
//        xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.black);
//        xylineandshaperenderer1.setUseFillPaint(true);
//        xylineandshaperenderer1.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
//        xylineandshaperenderer1.setSeriesStroke(0, new BasicStroke(0.5F));

        //xylineandshaperenderer1.setBaseItemLabelsVisible(true);
        ArrayList<DataItems> one = new ArrayList<DataItems>();
        ArrayList<DataItems> second = new ArrayList<DataItems>();
        ArrayList<DataItems> third = new ArrayList<DataItems>();
        ArrayList<DataItems> fourth = new ArrayList<DataItems>();
        ArrayList<DataItems> fifth = new ArrayList<DataItems>();
        ArrayList<DataItems> sixth = new ArrayList<DataItems>();
        ArrayList<DataItems> seventh = new ArrayList<DataItems>();
        ArrayList<DataItems> eighth = new ArrayList<DataItems>();
        ArrayList<DataItems> ninth = new ArrayList<>();
        ArrayList<DataItems> tenth = new ArrayList<>();
        ArrayList<DataItems> one_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> second_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> third_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> fourth_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> fifth_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> sixth_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> seventh_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> eighth_mode = new ArrayList<DataItems>();
        ArrayList<DataItems> ninth_mode = new ArrayList<>();
        ArrayList<DataItems> tenth_mode = new ArrayList<>();
        for (int i = 0; i < nor_model.size(); i++) {
            int count = 0;
            String key = Integer.toString(i);
            System.out.println(key);
            //ArrayList<DataItems> temp=new ArrayList<DataItems>();
            System.out.println(nor_model.get(key).size());
            if (i == 0) {
                one = nor_model.get(key);
                one_mode=nor_model_mode.get(key);
                for (int j = 0; j < one.size(); j++) {
                    count++;
                    DataItems _nor_model = new DataItems();
                    _nor_model = one.get(j);
                    XYDataset xydataset2 = createmodeDataset(_nor_model);
                    XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1, xydataset2);
                    xyplot.setRenderer(1 + j, xylineandshaperenderer2);
                    //设置不可见到点。
                    xylineandshaperenderer2.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer2.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer2.setSeriesShape(0, double1);
//                    xylineandshaperenderer2.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D));
                    //设置线和点的颜色。
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, -3F);
//                    generalpath.lineTo(0.0F, 3F);
//                    generalpath.lineTo(-3F, 3F);
//                    generalpath.closePath();
//                    xylineandshaperenderer2.setSeriesShape(0, generalpath);
                    xylineandshaperenderer2.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer2.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer2.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer2.setUseFillPaint(true);
                    xylineandshaperenderer2.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(2F));
//                    xylineandshaperenderer2.setBaseItemLabelsVisible(true);


                }
                for(int k=0;k<one_mode.size();k++ )
                {
                    DataItems _nor_model_mode = new DataItems();
                    _nor_model_mode = one_mode.get(k);
                    XYDataset xydataset2_mode = createmodeDataset(_nor_model_mode);
                    XYLineAndShapeRenderer xylineandshaperenderer2_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1+one.size(), xydataset2_mode);
                    xyplot.setRenderer(k + 1+one.size(), xylineandshaperenderer2_mode);
                    //设置不可见到点。
                    xylineandshaperenderer2_mode.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer2_mode.setSeriesLinesVisible(0, false);
//                    xylineandshaperenderer2.setSeriesShape(0, double1);
//                    xylineandshaperenderer2.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D));
                    //设置线和点的颜色。
                    GeneralPath generalpath = new GeneralPath();
                    generalpath.moveTo(-3F, -6F);
                    generalpath.lineTo(-3F, -12F);
//                    generalpath.lineTo(-3F, 3F);
//                    generalpath.closePath();
                    xylineandshaperenderer2_mode.setSeriesShape(0, generalpath);
                    xylineandshaperenderer2_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer2_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer2_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer2_mode.setUseFillPaint(true);
                    xylineandshaperenderer2_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer2_mode.setSeriesStroke(0, new BasicStroke(2F));
                }
            }
            if (i == 1) {
                second = nor_model.get(key);
                second_mode=nor_model_mode.get(key);
                for (int j = 0; j < second.size(); j++) {
                    XYDataset xydataset3 = createmodeDataset(second.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer3 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size(), xydataset3);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size(), xylineandshaperenderer3);
                    //设置不可见到点。
                    xylineandshaperenderer3.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer3.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer3.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer3.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer3.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer3.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer3.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer3.setUseFillPaint(true);
                    xylineandshaperenderer3.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer3.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer3.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<second_mode.size();k++)
                {
                    XYDataset xydataset3_mode = createmodeDataset(second_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer3_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()+second.size(), xydataset3_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()+second.size(), xylineandshaperenderer3_mode);
                    //设置不可见到点。
                    xylineandshaperenderer3_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer3_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();
                    generalpath.moveTo(0.0F, -12F);
                    generalpath.lineTo(3F, -12F);
                    generalpath.moveTo(3F, -12F);
                    generalpath.lineTo(3F, -9F);
                    generalpath.moveTo(3F, -9F);
                    generalpath.lineTo(0F, -9F);
                    generalpath.moveTo(0F, -9F);
                                                                                                                                                                                              generalpath.lineTo(0F, -6F);
                    generalpath.moveTo(0F,-6F);
                    generalpath.lineTo(3F,-6F);

                    xylineandshaperenderer3_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer3_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer3_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer3_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer3_mode.setUseFillPaint(true);
                    xylineandshaperenderer3_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer3_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer3_mode.setBaseItemLabelsVisible(false);
                }
            }
            if (i == 2) {
                third = nor_model.get(key);
                third_mode=nor_model_mode.get(key);
                for (int j = 0; j < third.size(); j++) {
                    XYDataset xydataset4 = createmodeDataset(third.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer4 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size() + second.size()+second_mode.size(), xydataset4);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size() + second.size()+second_mode.size(), xylineandshaperenderer4);
                    //设置不可见到点。
                    xylineandshaperenderer4.setBaseShapesVisible(true);
                    //设置可以看见线。
//                    xylineandshaperenderer4.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -2D, 3D, 3D));
                    xylineandshaperenderer4.setSeriesLinesVisible(0, true);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, -2F);
//                    generalpath.lineTo(2F, 2F);
//                    generalpath.lineTo(-2F, 2F);
//                    generalpath.closePath();
//                    xylineandshaperenderer4.setSeriesShape(0, generalpath);
                    xylineandshaperenderer4.setSeriesShape(0, double1);
                    //设置线和点的颜色。
                    xylineandshaperenderer4.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer4.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer4.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer4.setUseFillPaint(true);
                    xylineandshaperenderer4.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer4.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer4.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<third_mode.size();k++)
                {
                    XYDataset xydataset4_mode = createmodeDataset(third_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer4_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size(), xydataset4_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size(), xylineandshaperenderer4_mode);
                    //设置不可见到点。
                    xylineandshaperenderer4_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer4_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();
                    generalpath.moveTo(-1.5F, -18F);
                    generalpath.lineTo(1.5F, -18F);
                    generalpath.moveTo(1.5F, -18F);
                    generalpath.lineTo(1.5F, -15F);
                    generalpath.moveTo(1.5F, -15F);
                    generalpath.lineTo(-1.5F, -15F);
                    generalpath.moveTo(1.5F, -15F);
                    generalpath.lineTo(1.5F, -12F);
                    generalpath.moveTo(1.5F,-12F);
                    generalpath.lineTo(-1.5F,-12F);

                    xylineandshaperenderer4_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer4_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer4_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer4_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer4_mode.setUseFillPaint(true);
                    xylineandshaperenderer4_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer4_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer4_mode.setBaseItemLabelsVisible(false);
                }
            }
            if (i == 3) {
                fourth = nor_model.get(key);
                fourth_mode=nor_model_mode.get(key);
                for (int j = 0; j < fourth.size(); j++) {
                    XYDataset xydataset5 = createmodeDataset(fourth.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer5 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size(), xydataset5);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size(), xylineandshaperenderer5);
                    //设置不可见到点。
                    xylineandshaperenderer5.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer5.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer5.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer5.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer5.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer5.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer5.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer5.setUseFillPaint(true);
                    xylineandshaperenderer5.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer5.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer5.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<fourth_mode.size();k++)
                {
                    XYDataset xydataset5_mode = createmodeDataset(fourth_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer5_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size(), xydataset5_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size(), xylineandshaperenderer5_mode);
                    //设置不可见到点。
                    xylineandshaperenderer5_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer5_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();
                    generalpath.moveTo(0.0F, -18F);
                    generalpath.lineTo(0.0F, -12F);
                    generalpath.moveTo(-2F, -18F);
                    generalpath.lineTo(-2F, -15F);
                    generalpath.moveTo(-2F, -15F);
                    generalpath.lineTo(2F, -15F);


                    xylineandshaperenderer5_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer5_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer5_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer5_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer5_mode.setUseFillPaint(true);
                    xylineandshaperenderer5_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer5_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer5_mode.setBaseItemLabelsVisible(false);
                }

            }
            if (i == 4) {
                fifth = nor_model.get(key);
                fifth_mode=nor_model_mode.get(key);
                for (int j = 0; j < fifth.size(); j++) {
                    XYDataset xydataset6 = createmodeDataset(fifth.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer6 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size(), xydataset6);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size(), xylineandshaperenderer6);
                    //设置不可见到点。
                    xylineandshaperenderer6.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer6.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer6.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer6.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer6.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer6.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer6.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer6.setUseFillPaint(true);
                    xylineandshaperenderer6.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer6.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer6.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<fifth_mode.size();k++)
                {
                    XYDataset xydataset6_mode = createmodeDataset(fifth_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer6_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()
                            +fifth.size(), xydataset6_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()
                            +fifth.size(), xylineandshaperenderer6_mode);
                    //设置不可见到点。
                    xylineandshaperenderer6_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer6_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();
                    generalpath.moveTo(0.0F, -12F);
                    generalpath.lineTo(3.0F, -12F);
                    generalpath.moveTo(0.0F, -12F);
                    generalpath.lineTo(0.0F, -9F);
                    generalpath.moveTo(0.0F, -9F);
                    generalpath.lineTo(3.0F, -9F);
                    generalpath.moveTo(3.0F,-9F);
                    generalpath.lineTo(3.0F,-6F);
                    generalpath.moveTo(3.0F,-6F);
                    generalpath.lineTo(0.0F,-6F);

                    xylineandshaperenderer6_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer6_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer6_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer6_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer6_mode.setUseFillPaint(true);
                    xylineandshaperenderer6_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer6_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer6_mode.setBaseItemLabelsVisible(false);
                }
            }
            if (i == 5) {
                sixth = nor_model.get(key);
                sixth_mode=nor_model_mode.get(key);
                for (int j = 0; j < sixth.size(); j++) {
                    XYDataset xydataset7 = createmodeDataset(sixth.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer7 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()+fifth.size()+fifth_mode.size(), xydataset7);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()+fifth.size()+fifth_mode.size(), xylineandshaperenderer7);
                    //设置不可见到点。
                    xylineandshaperenderer7.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer7.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer7.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer7.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer7.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer7.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer7.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer7.setUseFillPaint(true);
                    xylineandshaperenderer7.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer7.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer7.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<sixth_mode.size();k++)
                {
                    XYDataset xydataset7_mode = createmodeDataset(sixth_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer7_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size(), xydataset7_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size(), xylineandshaperenderer7_mode);
                    //设置不可见到点。
                    xylineandshaperenderer7_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer7_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();


                    generalpath.moveTo(0.0F, -12F);
                    generalpath.lineTo(3.0F, -12F);
                    generalpath.moveTo(0.0F, -12F);
                    generalpath.lineTo(0.0F, -9F);
                    generalpath.moveTo(0.0F, -9F);
                    generalpath.lineTo(3.0F, -9F);
                    generalpath.moveTo(3.0F, -9F);
                    generalpath.lineTo(3.0F, -6F);
                    generalpath.moveTo(3.0F, -6F);
                    generalpath.lineTo(0.0F, -6F);
                    generalpath.moveTo(0.0F,-6F);
                    generalpath.lineTo(0.0F,-9F);
                    xylineandshaperenderer7_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer7_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer7_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer7_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer7_mode.setUseFillPaint(true);
                    xylineandshaperenderer7_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer7_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer7_mode.setBaseItemLabelsVisible(false);
                }
            }
            //未设置图形

            if (i == 6) {

                seventh = nor_model.get(key);
                seventh_mode=nor_model_mode.get(key);
                for (int j = 0; j < seventh.size(); j++) {
                    XYDataset xydataset8 = createmodeDataset(seventh.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer8 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()+fifth.size()+fifth_mode.size()+sixth.size()+sixth_mode.size(), xydataset8);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()+fifth.size()+fifth_mode.size()+sixth.size()+sixth_mode.size(), xylineandshaperenderer8);
                    //设置不可见到点。
                    xylineandshaperenderer8.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer8.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer8.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer8.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer8.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer8.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer8.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer8.setUseFillPaint(true);
                    xylineandshaperenderer8.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer8.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer8.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<seventh_mode.size();k++)
                {
                    XYDataset xydataset8_mode = createmodeDataset(seventh_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer8_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size()+sixth_mode.size()
                            +seventh.size(), xydataset8_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size()+sixth_mode.size()
                            +seventh.size(), xylineandshaperenderer8_mode);
                    //设置不可见到点。
                    xylineandshaperenderer8_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer8_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();


                    generalpath.moveTo(-1.5F, -18F);
                    generalpath.lineTo(1.5F, -18F);
                    generalpath.moveTo(1.5F, -18F);
                    generalpath.lineTo(1.5F, -15F);
                    generalpath.moveTo(1.5F, -15F);
                    generalpath.lineTo(1.5F, -12F);
                    xylineandshaperenderer8_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer8_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer8_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer8_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer8_mode.setUseFillPaint(true);
                    xylineandshaperenderer8_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer8_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer8_mode.setBaseItemLabelsVisible(false);
                }

            }
            if (i == 7) {
                eighth = nor_model.get(key);
                eighth_mode=nor_model_mode.get(key);
                for (int j = 0; j < eighth.size(); j++) {
                    XYDataset xydataset9 = createmodeDataset(eighth.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer9 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()+fifth.size()+fifth_mode.size()+sixth.size()+sixth_mode.size()
                            +seventh.size()+seventh_mode.size(), xydataset9);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size()+second.size()+second_mode.size()+third.size()+third_mode.size()
                            +fourth.size()+fourth_mode.size()+fifth.size()+fifth_mode.size()+sixth.size()+sixth_mode.size()
                            +seventh.size()+seventh_mode.size(), xylineandshaperenderer9);
                    //设置不可见到点。
                    xylineandshaperenderer9.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer9.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer9.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer9.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer9.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer9.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer9.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer9.setUseFillPaint(true);
                    xylineandshaperenderer9.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer9.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer9.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<eighth_mode.size();k++)
                {
                    XYDataset xydataset9_mode = createmodeDataset(eighth_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer9_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size()+sixth_mode.size()
                            +seventh.size()+seventh_mode.size()
                            +eighth.size(), xydataset9_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size()+sixth_mode.size()
                            +seventh.size()+seventh_mode.size()
                            +eighth.size(), xylineandshaperenderer9_mode);
                    //设置不可见到点。
                    xylineandshaperenderer9_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer9_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();


                    generalpath.moveTo(-1.5F, -18F);
                    generalpath.lineTo(1.5F, -18F);
                    generalpath.moveTo(1.5F, -18F);
                    generalpath.lineTo(1.5F, -15F);
                    generalpath.moveTo(1.5F, -15F);
                    generalpath.lineTo(-1.5F, -15F);
                    generalpath.moveTo(-1.5F,-15F);
                    generalpath.lineTo(-1.5F,-18F);
                    generalpath.moveTo(-1.5F,-15F);
                    generalpath.lineTo(-1.5F,-12F);
                    generalpath.moveTo(-1.5F,-12F);
                    generalpath.lineTo(1.5F,-12F);
                    generalpath.moveTo(1.5F,-12F);
                    generalpath.lineTo(1.5F,-15F);

                    xylineandshaperenderer9_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer9_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer9_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer9_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer9_mode.setUseFillPaint(true);
                    xylineandshaperenderer9_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer9_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer9_mode.setBaseItemLabelsVisible(false);
                }
            }
            if (i==8)
            {
                second = nor_model.get(key);
                second_mode=nor_model_mode.get(key);
                for (int j = 0; j < second.size(); j++) {
                    XYDataset xydataset3 = createmodeDataset(second.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer3 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size(), xydataset3);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size(), xylineandshaperenderer3);
                    //设置不可见到点。
                    xylineandshaperenderer3.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer3.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer3.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer3.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer3.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer3.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer3.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer3.setUseFillPaint(true);
                    xylineandshaperenderer3.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer3.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer3.setBaseItemLabelsVisible(false);


                }
                for(int k=0;k<ninth_mode.size();k++)
                {
                    XYDataset xydataset10_mode = createmodeDataset(ninth_mode.get(k));
                    XYLineAndShapeRenderer xylineandshaperenderer10_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size()+sixth_mode.size()
                            +seventh.size()+seventh_mode.size()
                            +eighth.size()+eighth_mode.size()
                            +ninth.size(), xydataset10_mode);
                    xyplot.setRenderer(k + 1 + one.size()+one_mode.size()
                            +second.size()+second_mode.size()
                            +third.size()+third_mode.size()
                            +fourth.size()+fourth.size()
                            +fifth.size()+fifth_mode.size()
                            +sixth.size()+sixth_mode.size()
                            +seventh.size()+seventh_mode.size()
                            +eighth.size()+eighth_mode.size()
                            +ninth.size(), xylineandshaperenderer10_mode);
                    //设置不可见到点。
                    xylineandshaperenderer10_mode.setBaseShapesVisible(true);
                    //设置可以看见线。

                    xylineandshaperenderer10_mode.setSeriesLinesVisible(0, false);
                    //xylineandshaperenderer3.setSeriesShape(0, double1);
                    GeneralPath generalpath = new GeneralPath();


                    generalpath.moveTo(0.0F, -12F);
                    generalpath.lineTo(3.0F, -12F);
                    generalpath.moveTo(0.0F, -12F);
                    generalpath.lineTo(0.0F, -9F);
                    generalpath.moveTo(0.0F, -9F);
                    generalpath.lineTo(3.0F, -9F);
                    generalpath.moveTo(3.0F,-9F);
                    generalpath.lineTo(3.0F,-12F);
                    generalpath.moveTo(3.0F,-9F);
                    generalpath.lineTo(3.0F,-6F);
                    generalpath.moveTo(3.0F,-6F);
                    generalpath.lineTo(0.0F,-6F);

                    xylineandshaperenderer10_mode.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer10_mode.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer10_mode.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer10_mode.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer10_mode.setUseFillPaint(true);
                    xylineandshaperenderer10_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer10_mode.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer10_mode.setBaseItemLabelsVisible(false);
                }
            }
            if (i==9)
            {
                second = nor_model.get(key);
                second_mode=nor_model_mode.get(key);
                for (int j = 0; j < second.size(); j++) {
                    XYDataset xydataset3 = createmodeDataset(second.get(j));
                    XYLineAndShapeRenderer xylineandshaperenderer3 = new XYLineAndShapeRenderer();
                    xyplot.setDataset(j + 1 + one.size()+one_mode.size(), xydataset3);
                    xyplot.setRenderer(j + 1 + one.size()+one_mode.size(), xylineandshaperenderer3);
                    //设置不可见到点。
                    xylineandshaperenderer3.setBaseShapesVisible(true);
                    //设置可以看见线。
                    xylineandshaperenderer3.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                    xylineandshaperenderer3.setSeriesLinesVisible(0, true);
                    xylineandshaperenderer3.setSeriesShape(0, double1);
//                    GeneralPath generalpath = new GeneralPath();
//                    generalpath.moveTo(0.0F, 6F);
//                    generalpath.lineTo(3F, 6F);
//                    generalpath.moveTo(3F, 6F);
//                    generalpath.lineTo(3F, 3F);
//                    generalpath.moveTo(3F, 3F);
//                    generalpath.lineTo(0F, 3F);
//                    generalpath.moveTo(0F, 3F);
//                    generalpath.lineTo(0F, 0F);
//                    generalpath.moveTo(0F,0F);
//                    generalpath.lineTo(3F,0F);
////                    generalpath.lineTo(-3F, 3F);
////                    generalpath.closePath();
//                    xylineandshaperenderer3.setSeriesShape(0, generalpath);
                    //设置线和点的颜色。
                    xylineandshaperenderer3.setSeriesPaint(0, Color.black);
                    xylineandshaperenderer3.setSeriesFillPaint(0, Color.black);
                    xylineandshaperenderer3.setSeriesOutlinePaint(0, Color.black);
                    xylineandshaperenderer3.setUseFillPaint(true);
                    xylineandshaperenderer3.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer3.setSeriesStroke(0, new BasicStroke(2F));
                    xylineandshaperenderer3.setBaseItemLabelsVisible(false);


                }
            }
            for(int k=0;k<tenth_mode.size();k++)
            {
                XYDataset xydataset11_mode = createmodeDataset(tenth_mode.get(k));
                XYLineAndShapeRenderer xylineandshaperenderer11_mode = new XYLineAndShapeRenderer();
                xyplot.setDataset(k + 1 + one.size()+one_mode.size()
                        +second.size()+second_mode.size()
                        +third.size()+third_mode.size()
                        +fourth.size()+fourth.size()
                        +fifth.size()+fifth_mode.size()
                        +sixth.size()+sixth_mode.size()
                        +seventh.size()+seventh_mode.size()
                        +eighth.size()+eighth_mode.size()
                        +ninth.size()+ninth_mode.size()
                        +tenth.size(), xydataset11_mode);
                xyplot.setRenderer(k + 1 + one.size()+one_mode.size()
                        +second.size()+second_mode.size()
                        +third.size()+third_mode.size()
                        +fourth.size()+fourth.size()
                        +fifth.size()+fifth_mode.size()
                        +sixth.size()+sixth_mode.size()
                        +seventh.size()+seventh_mode.size()
                        +eighth.size()+eighth_mode.size()
                        +ninth.size()+ninth_mode.size()
                        +tenth.size(), xylineandshaperenderer11_mode);
                //设置不可见到点。
                xylineandshaperenderer11_mode.setBaseShapesVisible(true);
                //设置可以看见线。

                xylineandshaperenderer11_mode.setSeriesLinesVisible(0, false);
                //xylineandshaperenderer3.setSeriesShape(0, double1);
                GeneralPath generalpath = new GeneralPath();
                generalpath.moveTo(0.0F, -12F);
                generalpath.lineTo(3.0F, -12F);
                generalpath.moveTo(0.0F, -12F);
                generalpath.lineTo(0.0F, -6F);
                generalpath.moveTo(0.0F, -6F);
                generalpath.lineTo(3.0F, -6F);
                generalpath.moveTo(3.0F,-6F);
                generalpath.lineTo(3.0F,-12F);
                generalpath.moveTo(-3.0F,-12F);
                generalpath.lineTo(-3.0F,-6F);


                xylineandshaperenderer11_mode.setSeriesShape(0, generalpath);
                //设置线和点的颜色。
                xylineandshaperenderer11_mode.setSeriesPaint(0, Color.black);
                xylineandshaperenderer11_mode.setSeriesFillPaint(0, Color.black);
                xylineandshaperenderer11_mode.setSeriesOutlinePaint(0, Color.black);
                xylineandshaperenderer11_mode.setUseFillPaint(true);
                xylineandshaperenderer11_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                xylineandshaperenderer11_mode.setSeriesStroke(0, new BasicStroke(2F));
                xylineandshaperenderer11_mode.setBaseItemLabelsVisible(false);
            }

        }
        jfreechart.getLegend().setVisible(false);
        return jfreechart;
    }
}

