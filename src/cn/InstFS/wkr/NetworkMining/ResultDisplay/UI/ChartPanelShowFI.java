package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

/**
 * Created by hidebumi on 2016/3/30.
 */
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

import cn.InstFS.wkr.NetworkMining.Miner.Common.LineElement;
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
import cn.InstFS.wkr.NetworkMining.DataInputs.parseDateToHour;
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
//        TimeSeriesCollection tsc = new TimeSeriesCollection();
//        TimeSeries ts = new TimeSeries("序列值");

        XYSeriesCollection tsc = new XYSeriesCollection();
        XYSeries ts = new XYSeries("序列值");

        int len = items.getLength();
        for (int i = 0; i < len; i++) {
            DataItem item = items.getElementAt(i);
            Date date = item.getTime();
            double val = Double.parseDouble(item.getData());
            parseDateToHour pHour = new parseDateToHour(date);
            int hour = pHour.getHour();
            //ts.addOrUpdate(items.getTimePeriodOfElement(i), val);
            ts.add(hour,val);
        }
        tsc.addSeries(ts);
        chart.getXYPlot().setDataset(tsc);
    }

    public static XYDataset createNormalDataset(DataItems normal, Date startDate) {
        //获取正常数据的长度、
        int length = normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries("原始值");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //为数据集添加数据

        for (int i = 0; i < length; i++) {
            DataItem temp = new DataItem();
            temp = normal.getElementAt(i);
            /*parseDateToHour pHour = new parseDateToHour(temp.getTime(), startDate);
            int hour = pHour.getHour();*/
//            xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData())); // 对应的横轴
            xyseries.add(i, Double.parseDouble(temp.getData())); // 对应的横轴
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
            parseDateToHour pHour = new parseDateToHour(temp.getTime());
            int hour = pHour.getHour();
//            xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData()));
//            xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData()));
            xyseries.add(hour, Double.parseDouble(temp.getData())); // 对应的横轴
        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }

    public static XYDataset createmodeDataset(DataItems normal, Date startDate) {
        //获取正常数据的长度、
        int length = normal.getLength();
        int time[] = new int[length];
        XYSeries xyseries = new XYSeries(".");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //为数据集添加数据

        for (int i = 0; i < length; i++) {
            DataItem temp = new DataItem();
            temp = normal.getElementAt(i);
            parseDateToHour pHour = new parseDateToHour(temp.getTime(), startDate);
            int hour = pHour.getHour();
            //xyseries.add((double) temp.getTime().getTime(), Double.parseDouble(temp.getData())); // 对应的横轴
            xyseries.add(hour, Double.parseDouble(temp.getData())); // 对应的横轴

        }
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }

    public static XYDataset createmodeDataset(DataItems nor, List<LineElement> mode) {
        XYSeries xyseries = new XYSeries(".");

        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        int start = mode.get(0).getStart();
        int end = mode.get(mode.size()-1).getEnd();
        for (int i = start; i <= end; i++) {
            DataItem temp = nor.getElementAt(i);
            xyseries.add(i, Double.parseDouble(temp.getData()));
        }
        /*for (LineElement: mode) {
            String[] modeList =  s.split(";");
            int start = Integer.parseInt(modeList[0].split(",")[0]);
            String[] lastLine = modeList[modeList.length-1].split(":");
            int end = Integer.parseInt(lastLine[lastLine.length-1]);

            for (int i = start; i <= end; i++) {
                DataItem temp = nor.getElementAt(i);
                xyseries.add(i, Double.parseDouble(temp.getData()));
            }
        }*/
        xyseriescollection.addSeries(xyseries);
        return xyseriescollection;
    }

    public static JFreeChart createChart(HashMap<String, ArrayList<DataItems>> nor_model, DataItems nor, HashMap<String, ArrayList<DataItems>> nor_model_mode,int []modelindex,String yName) {
        Date startDate = nor.getTime().get(0);
        XYDataset xydataset = createNormalDataset(nor, startDate);
//        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(" 频繁项集挖掘结果", "序列编号", "值", xydataset);
        JFreeChart jfreechart = ChartFactory.createScatterPlot(" 频繁项集挖掘结果", "序列编号", yName, xydataset);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        NumberAxis xAxis=(NumberAxis)xyplot.getDomainAxis();
        xAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-2D, -2D, 3D, 3D);
        //设置异常点提示红点大小
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        //设置不可看到点。
        Shape itemShape;
        itemShape = ShapeUtilities.createDiamond((float) 3);
        xylineandshaperenderer.setSeriesLinesVisible(0, true);
        xylineandshaperenderer.setBaseShapesVisible(false);
        xylineandshaperenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D));
        xylineandshaperenderer.setSeriesPaint(0, Color.black);
        xylineandshaperenderer.setSeriesFillPaint(0, Color.black);
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.black);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(0.5F));

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
        for (int i = 0; i < nor_model.size(); i++) {    // 循环不同的频繁模式
            int count = 0;
            String key = Integer.toString(i);
            System.out.println("kkkkk " + key);
            System.out.println(nor_model.get(key).size());
            if (i == 0) {
                if (modelindex[i] == 1) {
                    one = nor_model.get(key);

                    for (int j = 0; j < one.size(); j++) {
                        count++;
                        DataItems _nor_model = new DataItems();
                        _nor_model = one.get(j);
                        XYDataset xydataset2 = createmodeDataset(_nor_model, startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1, xydataset2);
                        xyplot.setRenderer(1 + j, xylineandshaperenderer2);
                        //设置不可见到点。
                        xylineandshaperenderer2.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer2.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer2.setSeriesShape(0, double1);
                        xylineandshaperenderer2.setSeriesPaint(0, new Color(220, 87, 19));
                        xylineandshaperenderer2.setSeriesFillPaint(0, new Color(220, 87, 19));
                        xylineandshaperenderer2.setSeriesOutlinePaint(0, new Color(220, 87, 19));
                        xylineandshaperenderer2.setUseFillPaint(true);
                        xylineandshaperenderer2.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(2.5F));
                    }
                    /*DataItems _nor_model_1 = new DataItems();
                    for (int k = 0; k < one_mode.size(); k++) {
                        DataItems _nor_model_mode = new DataItems();
                        _nor_model_mode = one_mode.get(k);
                        for (int j = 0; j < _nor_model_mode.getLength(); j++) {
                            _nor_model_1.add1Data(_nor_model_mode.getElementAt(j));
                        }
                    }
                    XYDataset xydataset2_mode = createmodeDataset(_nor_model_1);
                    XYLineAndShapeRenderer xylineandshaperenderer2_mode = new XYLineAndShapeRenderer();
                    xyplot.setDataset(2, xydataset2_mode);
                    xyplot.setRenderer(2, xylineandshaperenderer2_mode);
                    //设置不可见到点。
                    xylineandshaperenderer2_mode.setBaseShapesVisible(false);
                    //设置可以看见线。
                    xylineandshaperenderer2_mode.setSeriesLinesVisible(0, false);

                    GeneralPath generalpath = new GeneralPath();
                    generalpath.moveTo(-3F, -6F);
                    generalpath.lineTo(-3F, -12F);
                    xylineandshaperenderer2_mode.setSeriesShape(0, generalpath);
                    xylineandshaperenderer2_mode.setSeriesPaint(0, Color.red);
                    xylineandshaperenderer2_mode.setSeriesFillPaint(0, Color.red);
                    xylineandshaperenderer2_mode.setSeriesOutlinePaint(0, Color.red);
                    xylineandshaperenderer2_mode.setUseFillPaint(true);
                    xylineandshaperenderer2_mode.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                    xylineandshaperenderer2_mode.setSeriesStroke(0, new BasicStroke(2F));*/
                }
            }
            if (i == 1) {
                if (modelindex[1] == 1) {
                    second = nor_model.get(key);

                    for (int j = 0; j < second.size(); j++) {
                        XYDataset xydataset3 = createmodeDataset(second.get(j), startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer3 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size() , xydataset3);
                        xyplot.setRenderer(j + 1 + one.size() , xylineandshaperenderer3);
                        //设置不可见到点。
                        xylineandshaperenderer3.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer3.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer3.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer3.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer3.setSeriesPaint(0, new Color(107, 194, 53));
                        xylineandshaperenderer3.setSeriesFillPaint(0, new Color(107, 194, 53));
                        xylineandshaperenderer3.setSeriesOutlinePaint(0, new Color(107, 194, 53));
                        xylineandshaperenderer3.setUseFillPaint(true);
                        xylineandshaperenderer3.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer3.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer3.setBaseItemLabelsVisible(false);
                    }
                }
            }
            if (i == 2) {
                if (modelindex[i] == 1) {
                    third = nor_model.get(key);

                    for (int j = 0; j < third.size(); j++) {
                        XYDataset xydataset4 = createmodeDataset(third.get(j), startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer4 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size() , xydataset4);
                        xyplot.setRenderer(j + 1 + one.size() + second.size() , xylineandshaperenderer4);
                        //设置不可见到点。
                        xylineandshaperenderer4.setBaseShapesVisible(false);

                        xylineandshaperenderer4.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer4.setSeriesShape(0, double1);
                        //设置线和点的颜色。
                        xylineandshaperenderer4.setSeriesPaint(0, new Color(29, 131, 8));
                        xylineandshaperenderer4.setSeriesFillPaint(0, new Color(29, 131, 8));
                        xylineandshaperenderer4.setSeriesOutlinePaint(0, new Color(29, 131, 8));
                        xylineandshaperenderer4.setUseFillPaint(true);
                        xylineandshaperenderer4.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer4.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer4.setBaseItemLabelsVisible(false);


                    }
                }
            }

            if (i == 3) {
                if (modelindex[i] == 1) {
                    fourth = nor_model.get(key);

                    for (int j = 0; j < fourth.size(); j++) {
                        XYDataset xydataset5 = createmodeDataset(fourth.get(j), startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer5 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size() + second.size()  + third.size() , xydataset5);
                        xyplot.setRenderer(j + 1 + one.size() + second.size()  + third.size() , xylineandshaperenderer5);
                        //设置不可见到点。
                        xylineandshaperenderer5.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer5.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer5.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer5.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer5.setSeriesPaint(0, new Color(3, 22, 52));
                        xylineandshaperenderer5.setSeriesFillPaint(0, new Color(3, 22, 52));
                        xylineandshaperenderer5.setSeriesOutlinePaint(0, new Color(3, 22, 52));
                        xylineandshaperenderer5.setUseFillPaint(true);
                        xylineandshaperenderer5.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer5.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer5.setBaseItemLabelsVisible(false);


                    }
                }

            }
            if (i == 4) {
                if (modelindex[i] == 1) {
                    fifth = nor_model.get(key);

                    for (int j = 0; j < fifth.size(); j++) {
                        XYDataset xydataset6 = createmodeDataset(fifth.get(j), startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer6 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size() + third.size()
                                + fourth.size() , xydataset6);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size() , xylineandshaperenderer6);
                        //设置不可见到点。
                        xylineandshaperenderer6.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer6.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer6.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer6.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer6.setSeriesPaint(0, new Color(0, 90, 171));
                        xylineandshaperenderer6.setSeriesFillPaint(0, new Color(0, 90, 171));
                        xylineandshaperenderer6.setSeriesOutlinePaint(0, new Color(0, 90, 171));
                        xylineandshaperenderer6.setUseFillPaint(false);
                        xylineandshaperenderer6.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer6.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer6.setBaseItemLabelsVisible(false);


                    }

                }
            }

            if (i == 5) {
                if (modelindex[i] == 1) {
                    sixth = nor_model.get(key);
                    for (int j = 0; j < sixth.size(); j++) {
                        XYDataset xydataset7 = createmodeDataset(sixth.get(j),startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer7 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size() , xydataset7);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size() , xylineandshaperenderer7);
                        //设置不可见到点。
                        xylineandshaperenderer7.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer7.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer7.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer7.setSeriesShape(0, double1);
                        //设置线和点的颜色。
                        xylineandshaperenderer7.setSeriesPaint(0, new Color(3, 101, 100));
                        xylineandshaperenderer7.setSeriesFillPaint(0, new Color(3, 101, 100));
                        xylineandshaperenderer7.setSeriesOutlinePaint(0, new Color(3, 101, 100));
                        xylineandshaperenderer7.setUseFillPaint(true);
                        xylineandshaperenderer7.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer7.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer7.setBaseItemLabelsVisible(false);


                    }
                }
            }

            //未设置图形

            if (i == 6) {
                if (modelindex[i] == 1) {
                    seventh = nor_model.get(key);

                    for (int j = 0; j < seventh.size(); j++) {
                        XYDataset xydataset8 = createmodeDataset(seventh.get(j),startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer8 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size() + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size() , xydataset8);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size() , xylineandshaperenderer8);
                        //设置不可见到点。
                        xylineandshaperenderer8.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer8.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer8.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer8.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer8.setSeriesPaint(0, new Color(255, 66, 93));
                        xylineandshaperenderer8.setSeriesFillPaint(0, new Color(255, 66, 93));
                        xylineandshaperenderer8.setSeriesOutlinePaint(0, new Color(255, 66, 93));
                        xylineandshaperenderer8.setUseFillPaint(true);
                        xylineandshaperenderer8.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer8.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer8.setBaseItemLabelsVisible(false);


                    }

                }
            }


            if (i == 7) {
                if (modelindex[i] == 1) {
                    eighth = nor_model.get(key);

                    for (int j = 0; j < eighth.size(); j++) {
                        XYDataset xydataset9 = createmodeDataset(eighth.get(j),startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer9 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size() + sixth.size()
                                + seventh.size() , xydataset9);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size() , xylineandshaperenderer9);
                        //设置不可见到点。
                        xylineandshaperenderer9.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer9.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer9.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer9.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer9.setSeriesPaint(0, new Color(32, 90, 9));
                        xylineandshaperenderer9.setSeriesFillPaint(0, new Color(32, 90, 9));
                        xylineandshaperenderer9.setSeriesOutlinePaint(0, new Color(32, 90, 9));
                        xylineandshaperenderer9.setUseFillPaint(true);
                        xylineandshaperenderer9.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer9.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer9.setBaseItemLabelsVisible(false);


                    }

                }
            }
            if (i == 8) {
                if (modelindex[i] == 1) {
                    ninth = nor_model.get(key);

                    for (int j = 0; j < ninth.size(); j++) {
                        XYDataset xydataset10 = createmodeDataset(ninth.get(j),startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer10 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() , xydataset10);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size() + third.size() +
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() , xylineandshaperenderer10);
                        //设置不可见到点。
                        xylineandshaperenderer10.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer10.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer10.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer10.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer10.setSeriesPaint(0, new Color(224, 208, 0));
                        xylineandshaperenderer10.setSeriesFillPaint(0, new Color(224, 208, 0));
                        xylineandshaperenderer10.setSeriesOutlinePaint(0, new Color(224, 208, 0));
                        xylineandshaperenderer10.setUseFillPaint(true);
                        xylineandshaperenderer10.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer10.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer10.setBaseItemLabelsVisible(false);


                    }

                }
            }
            if (i == 9) {
                if (modelindex[i] == 1) {
                    tenth = nor_model.get(key);

                    for (int j = 0; j < tenth.size(); j++) {
                        XYDataset xydataset11 = createmodeDataset(tenth.get(j),startDate);
                        XYLineAndShapeRenderer xylineandshaperenderer11 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size() + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() +ninth.size(), xydataset11);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() +ninth.size(), xylineandshaperenderer11);
                        //设置不可见到点。
                        xylineandshaperenderer11.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer11.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer11.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer11.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer11.setSeriesPaint(0, new Color(55, 99, 53));
                        xylineandshaperenderer11.setSeriesFillPaint(0, new Color(55, 99, 53));
                        xylineandshaperenderer11.setSeriesOutlinePaint(0, new Color(55, 99, 53));
                        xylineandshaperenderer11.setUseFillPaint(true);
                        xylineandshaperenderer11.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer11.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer11.setBaseItemLabelsVisible(false);


                    }

                }
            }
        }

        jfreechart.getLegend().setVisible(false);
        return jfreechart;
    }



    public static JFreeChart createChart(HashMap<Integer, List<List<LineElement>>> nor_model, DataItems nor,int []modelindex, String yName) {
        Date startDate = nor.getTime().get(0);
        XYDataset xydataset = createNormalDataset(nor, startDate);
//        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(" 频繁项集挖掘结果", "序列编号", "值", xydataset);
        JFreeChart jfreechart = ChartFactory.createScatterPlot(" 频繁项集挖掘结果", "序列编号", yName, xydataset);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        NumberAxis xAxis=(NumberAxis)xyplot.getDomainAxis();
        xAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,12));
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-2D, -2D, 3D, 3D);
        //设置异常点提示红点大小
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        //设置不可看到点。
        Shape itemShape;
        itemShape = ShapeUtilities.createDiamond((float) 3);
        xylineandshaperenderer.setSeriesLinesVisible(0, true);
        xylineandshaperenderer.setBaseShapesVisible(false);
        xylineandshaperenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D));
        xylineandshaperenderer.setSeriesPaint(0, Color.black);
        xylineandshaperenderer.setSeriesFillPaint(0, Color.black);
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.black);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(0.5F));

        List<List<LineElement>> one = new ArrayList<>();
        List<List<LineElement>> second = new ArrayList<>();
        List<List<LineElement>> third = new ArrayList<>();
        List<List<LineElement>> fourth = new ArrayList<>();
        List<List<LineElement>> fifth = new ArrayList<>();
        List<List<LineElement>> sixth = new ArrayList<>();
        List<List<LineElement>> seventh = new ArrayList<>();
        List<List<LineElement>> eighth = new ArrayList<>();
        List<List<LineElement>> ninth = new ArrayList<>();
        List<List<LineElement>> tenth = new ArrayList<>();
        for (int i = 0; i < nor_model.size(); i++) {    // 循环不同的频繁模式
            int count = 0;
            String key = Integer.toString(i);
            System.out.println("kkkkk " + key);
            System.out.println(nor_model.get(i).size());
            if (i == 0) {
                if (modelindex[i] == 1) {
                    one = nor_model.get(i);

                    for (int j = 0; j < one.size(); j++) {
                        count++;
                        List<LineElement> _nor_model;
                        _nor_model = one.get(j);
                        XYDataset xydataset2 = createmodeDataset(nor, _nor_model);
                        XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1, xydataset2);
                        xyplot.setRenderer(1 + j, xylineandshaperenderer2);
                        //设置不可见到点。
                        xylineandshaperenderer2.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer2.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer2.setSeriesShape(0, double1);
                        xylineandshaperenderer2.setSeriesPaint(0, new Color(220, 87, 19));
                        xylineandshaperenderer2.setSeriesFillPaint(0, new Color(220, 87, 19));
                        xylineandshaperenderer2.setSeriesOutlinePaint(0, new Color(220, 87, 19));
                        xylineandshaperenderer2.setUseFillPaint(true);
                        xylineandshaperenderer2.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(2.5F));
                    }
                }
            }
            if (i == 1) {
                if (modelindex[1] == 1) {
                    second = nor_model.get(i);

                    for (int j = 0; j < second.size(); j++) {
                        XYDataset xydataset3 = createmodeDataset(nor, second.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer3 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size() , xydataset3);
                        xyplot.setRenderer(j + 1 + one.size() , xylineandshaperenderer3);
                        //设置不可见到点。
                        xylineandshaperenderer3.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer3.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer3.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer3.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer3.setSeriesPaint(0, new Color(107, 194, 53));
                        xylineandshaperenderer3.setSeriesFillPaint(0, new Color(107, 194, 53));
                        xylineandshaperenderer3.setSeriesOutlinePaint(0, new Color(107, 194, 53));
                        xylineandshaperenderer3.setUseFillPaint(true);
                        xylineandshaperenderer3.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer3.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer3.setBaseItemLabelsVisible(false);
                    }
                }
            }
            if (i == 2) {
                if (modelindex[i] == 1) {
                    third = nor_model.get(i);

                    for (int j = 0; j < third.size(); j++) {
                        XYDataset xydataset4 = createmodeDataset(nor,third.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer4 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size() , xydataset4);
                        xyplot.setRenderer(j + 1 + one.size() + second.size() , xylineandshaperenderer4);
                        //设置不可见到点。
                        xylineandshaperenderer4.setBaseShapesVisible(false);

                        xylineandshaperenderer4.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer4.setSeriesShape(0, double1);
                        //设置线和点的颜色。
                        xylineandshaperenderer4.setSeriesPaint(0, new Color(29, 131, 8));
                        xylineandshaperenderer4.setSeriesFillPaint(0, new Color(29, 131, 8));
                        xylineandshaperenderer4.setSeriesOutlinePaint(0, new Color(29, 131, 8));
                        xylineandshaperenderer4.setUseFillPaint(true);
                        xylineandshaperenderer4.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer4.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer4.setBaseItemLabelsVisible(false);


                    }
                }
            }

            if (i == 3) {
                if (modelindex[i] == 1) {
                    fourth = nor_model.get(i);

                    for (int j = 0; j < fourth.size(); j++) {
                        XYDataset xydataset5 = createmodeDataset(nor,fourth.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer5 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size() + second.size()  + third.size() , xydataset5);
                        xyplot.setRenderer(j + 1 + one.size() + second.size()  + third.size() , xylineandshaperenderer5);
                        //设置不可见到点。
                        xylineandshaperenderer5.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer5.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer5.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer5.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer5.setSeriesPaint(0, new Color(3, 22, 52));
                        xylineandshaperenderer5.setSeriesFillPaint(0, new Color(3, 22, 52));
                        xylineandshaperenderer5.setSeriesOutlinePaint(0, new Color(3, 22, 52));
                        xylineandshaperenderer5.setUseFillPaint(true);
                        xylineandshaperenderer5.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer5.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer5.setBaseItemLabelsVisible(false);


                    }
                }

            }
            if (i == 4) {
                if (modelindex[i] == 1) {
                    fifth = nor_model.get(i);

                    for (int j = 0; j < fifth.size(); j++) {
                        XYDataset xydataset6 = createmodeDataset(nor, fifth.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer6 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size() + third.size()
                                + fourth.size() , xydataset6);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size() , xylineandshaperenderer6);
                        //设置不可见到点。
                        xylineandshaperenderer6.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer6.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer6.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer6.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer6.setSeriesPaint(0, new Color(0, 90, 171));
                        xylineandshaperenderer6.setSeriesFillPaint(0, new Color(0, 90, 171));
                        xylineandshaperenderer6.setSeriesOutlinePaint(0, new Color(0, 90, 171));
                        xylineandshaperenderer6.setUseFillPaint(false);
                        xylineandshaperenderer6.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer6.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer6.setBaseItemLabelsVisible(false);


                    }

                }
            }

            if (i == 5) {
                if (modelindex[i] == 1) {
                    sixth = nor_model.get(i);
                    for (int j = 0; j < sixth.size(); j++) {
                        XYDataset xydataset7 = createmodeDataset(nor, sixth.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer7 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size() , xydataset7);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size() , xylineandshaperenderer7);
                        //设置不可见到点。
                        xylineandshaperenderer7.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer7.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer7.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer7.setSeriesShape(0, double1);
                        //设置线和点的颜色。
                        xylineandshaperenderer7.setSeriesPaint(0, new Color(3, 101, 100));
                        xylineandshaperenderer7.setSeriesFillPaint(0, new Color(3, 101, 100));
                        xylineandshaperenderer7.setSeriesOutlinePaint(0, new Color(3, 101, 100));
                        xylineandshaperenderer7.setUseFillPaint(true);
                        xylineandshaperenderer7.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer7.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer7.setBaseItemLabelsVisible(false);


                    }
                }
            }

            //未设置图形

            if (i == 6) {
                if (modelindex[i] == 1) {
                    seventh = nor_model.get(i);

                    for (int j = 0; j < seventh.size(); j++) {
                        XYDataset xydataset8 = createmodeDataset(nor, seventh.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer8 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size() + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size() , xydataset8);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size() , xylineandshaperenderer8);
                        //设置不可见到点。
                        xylineandshaperenderer8.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer8.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer8.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer8.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer8.setSeriesPaint(0, new Color(255, 66, 93));
                        xylineandshaperenderer8.setSeriesFillPaint(0, new Color(255, 66, 93));
                        xylineandshaperenderer8.setSeriesOutlinePaint(0, new Color(255, 66, 93));
                        xylineandshaperenderer8.setUseFillPaint(true);
                        xylineandshaperenderer8.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer8.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer8.setBaseItemLabelsVisible(false);


                    }

                }
            }


            if (i == 7) {
                if (modelindex[i] == 1) {
                    eighth = nor_model.get(i);

                    for (int j = 0; j < eighth.size(); j++) {
                        XYDataset xydataset9 = createmodeDataset(nor, eighth.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer9 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size() + sixth.size()
                                + seventh.size() , xydataset9);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size() , xylineandshaperenderer9);
                        //设置不可见到点。
                        xylineandshaperenderer9.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer9.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer9.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer9.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer9.setSeriesPaint(0, new Color(32, 90, 9));
                        xylineandshaperenderer9.setSeriesFillPaint(0, new Color(32, 90, 9));
                        xylineandshaperenderer9.setSeriesOutlinePaint(0, new Color(32, 90, 9));
                        xylineandshaperenderer9.setUseFillPaint(true);
                        xylineandshaperenderer9.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer9.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer9.setBaseItemLabelsVisible(false);


                    }

                }
            }
            if (i == 8) {
                if (modelindex[i] == 1) {
                    ninth = nor_model.get(i);

                    for (int j = 0; j < ninth.size(); j++) {
                        XYDataset xydataset10 = createmodeDataset(nor, ninth.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer10 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() , xydataset10);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size() + third.size() +
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() , xylineandshaperenderer10);
                        //设置不可见到点。
                        xylineandshaperenderer10.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer10.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer10.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer10.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer10.setSeriesPaint(0, new Color(224, 208, 0));
                        xylineandshaperenderer10.setSeriesFillPaint(0, new Color(224, 208, 0));
                        xylineandshaperenderer10.setSeriesOutlinePaint(0, new Color(224, 208, 0));
                        xylineandshaperenderer10.setUseFillPaint(true);
                        xylineandshaperenderer10.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer10.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer10.setBaseItemLabelsVisible(false);


                    }

                }
            }
            if (i == 9) {
                if (modelindex[i] == 1) {
                    tenth = nor_model.get(i);

                    for (int j = 0; j < tenth.size(); j++) {
                        XYDataset xydataset11 = createmodeDataset(nor, tenth.get(j));
                        XYLineAndShapeRenderer xylineandshaperenderer11 = new XYLineAndShapeRenderer();
                        xyplot.setDataset(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size() + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() +ninth.size(), xydataset11);
                        xyplot.setRenderer(j + 1 + one.size()  + second.size()  + third.size()
                                + fourth.size()  + fifth.size()  + sixth.size()
                                + seventh.size()  + eighth.size() +ninth.size(), xylineandshaperenderer11);
                        //设置不可见到点。
                        xylineandshaperenderer11.setBaseShapesVisible(false);
                        //设置可以看见线。
                        xylineandshaperenderer11.setSeriesShape(0, new java.awt.geom.Rectangle2D.Double(-2D, -4.5D, 4D, 9D));

                        xylineandshaperenderer11.setSeriesLinesVisible(0, true);
                        xylineandshaperenderer11.setSeriesShape(0, double1);

                        //设置线和点的颜色。
                        xylineandshaperenderer11.setSeriesPaint(0, new Color(55, 99, 53));
                        xylineandshaperenderer11.setSeriesFillPaint(0, new Color(55, 99, 53));
                        xylineandshaperenderer11.setSeriesOutlinePaint(0, new Color(55, 99, 53));
                        xylineandshaperenderer11.setUseFillPaint(true);
                        xylineandshaperenderer11.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                        xylineandshaperenderer11.setSeriesStroke(0, new BasicStroke(2.5F));
                        xylineandshaperenderer11.setBaseItemLabelsVisible(false);


                    }

                }
            }
        }

        jfreechart.getLegend().setVisible(false);
        return jfreechart;
    }


}


