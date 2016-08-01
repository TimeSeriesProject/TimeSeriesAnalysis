package cn.InstFS.wkr.NetworkMining.UIs;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2013, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------------
 * SWTTimeSeriesDemo.java
 * ---------------------
 * (C) Copyright 2006-2009, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Henry Proudhon (henry.proudhon AT ensmp.fr);
 *
 * Changes
 * -------
 * 30-Jan-2007 : New class derived from TimeSeriesDemo.java (HP);
 * 
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import ca.pfv.spmf.algorithms.frequentpatterns.lcm.Dataset;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowFI;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowFP;
import associationRules.LinePos;
import associationRules.ProtoclPair;

/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class TimeSeriesChart1 extends Composite {

	ArrayList<DataItems> _nor_model_ = null;
	ArrayList<DataItems> _abnor_model_ = null;

	public TimeSeriesChart1(Composite parent, int style, ProtoclPair pp) {
		super(parent, style);
		// 归一化两条曲线
		DataItems dataitems1 = DataItemsNormalization(pp.getDataItems1(), 1);
		DataItems dataitems2 = DataItemsNormalization(pp.getDataItems2(), 0);

		/*
		 * _nor_model_保存DataItem1对应的曲线线段 _abnor_model_保存DataItem2对应的曲线线段 LinePos
		 * 标注两条曲线每一个key线段的起始位置和终点位置 由于去掉了标红功能，此处注释掉
		 */
		/*
		 * _nor_model_ = new ArrayList<DataItems>(); _abnor_model_ = new
		 * ArrayList<DataItems>();
		 * 
		 * 
		 * // LinePos 标注两条曲线每一个key线段的起始位置和终点位置 ArrayList<LinePos> linepos1 = new
		 * ArrayList<LinePos>();
		 * 
		 * for (Object se : pp.getMapAB().keySet()) { //ArrayList<LinePos>
		 * linepos1 = pp.getMapAB().get(se); // System.out.println(" key :" + se
		 * + "	" + "value" + s.size()); // linepos1.addAll(s);
		 * 
		 * Iterator lineposIt1 = linepos1.iterator(); while
		 * (lineposIt1.hasNext()) { LinePos temp1 = (LinePos) lineposIt1.next();
		 * _nor_model_.add(this.getDataItems(dataitems1, (temp1).A_start,
		 * temp1.A_end)); // System.out.println(" A_start=" + (temp1).A_start +
		 * "  " // + "A_end=" + temp1.A_end);
		 * _abnor_model_.add(this.getDataItems(dataitems2, (temp1).B_start,
		 * temp1.B_end)); // System.out.println(" B_start=" + (temp1).B_start +
		 * "  " // + "B_end=" + temp1.B_end); //System.out.println(""); } }
		 */
		String chartname = "协议" + pp.getProtocol1() + "/" + "协议"
				+ pp.getProtocol2() + "时间序列" + "(置信度：" + pp.confidence + ")";
		JFreeChart chart = createChart2(_nor_model_, _abnor_model_, dataitems1,
				dataitems2, pp.getMapAB(), chartname, pp.getProtocol1(),
				pp.getProtocol2());

		ChartComposite frame = new ChartComposite(this, SWT.NONE, chart, true);
		frame.setDisplayToolTips(true);
		frame.setHorizontalAxisTrace(false);
		frame.setVerticalAxisTrace(false);
		this.setLayout(new FillLayout());
	}

	/*
	 * getDataItems 功能：通过start end 取出DataItems上的线段保存进一个新的DataItems并返回
	 */
	public static DataItems getDataItems(DataItems dataitems, int start, int end) {
		DataItems ret = new DataItems();
		// ret.setItems(items);

		for (int i = start; i <= end; i++) {

			ret.add1Data(dataitems.getElementAt(i));
		}

		return ret;

	}

	/*
	 * 归一化DataItems中的List<String>,并且曲线向上平移addanumber个单位长度
	 */
	public DataItems DataItemsNormalization(DataItems data, int addanumber) {
		DataItems ret = new DataItems();
		ret.setTime(data.getTime());
		ret.setNonNumData(data.getNonNumData());
		ret.setProbMap(data.getProbMap());
		ret.setVarSet(data.getVarSet());
		ret.setProb(data.getProb());
		ret.setDiscreteNodes(data.getDiscreteNodes());
		ret.setGranularity(data.getGranularity());

		ret.setDiscreteStrings(data.getDiscreteStrings());

		// ret.s

		List<String> datanormolization = new ArrayList<String>();
		// List<String> data=d.getData();
		Iterator it = data.data.iterator();
		// 找出最大值最小值
		if (it.hasNext()) {
			double min = Double.valueOf((String) it.next()).doubleValue();
			double max = min;
			while (it.hasNext()) {
				double temp = Double.valueOf((String) it.next()).doubleValue();
				if (max < temp) {
					max = temp;
				} else if (min > temp) {
					min = temp;
				}
			}
			// 最大值减最小值提前计算，提高效率
			double t = max - min;
			// 重置迭代器
			it = data.data.iterator();
			while (it.hasNext()) {
				double temp2 = Double.valueOf((String) it.next()).doubleValue();
				double temp2normalization = 0;

				if (t != 0) {
					temp2normalization = (temp2 - min) / t + addanumber;
					// System.out.println("temp2normalization="+temp2normalization);
				}
				datanormolization.add(Double.toString(temp2normalization));
			}

			// 把归一化后的List<String> datanormolization 加入DataItems
			ret.setData(datanormolization);

		} else {
			System.out
					.println(" TimeSeriesChart1 function DataItemsNormalization  data is null");
		}

		// int max=data.

		return ret;

	}

	/*
	 * 创建图表函数
	 * 
	 * @param _nor_model：保存DataItems1曲线标红线段集
	 * 
	 * @param _abnor_model：保存DataItems2曲线标红线段集
	 */
	public static JFreeChart createChart2(ArrayList<DataItems> _nor_model,
			ArrayList<DataItems> _abnor_model, DataItems nor, DataItems abnor,
			Map<String, ArrayList<LinePos>> mapAB, String chartname,
			String protocol1, String protocol2) {
		XYDataset xydataset = createNormalDataset(nor, protocol1);
		JFreeChart jfreechart = ChartFactory.createXYLineChart(chartname, "时间",
				"值", xydataset);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(
				-4D, -4D, 6D, 6D);
		// 设置异常点提示红点大小
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot
				.getRenderer();
		// 设置不可看到点。
		xylineandshaperenderer.setSeriesLinesVisible(0, true);
		xylineandshaperenderer.setBaseShapesVisible(false);
		xylineandshaperenderer.setSeriesShape(0, double1);
		xylineandshaperenderer.setSeriesPaint(0, Color.blue);
		xylineandshaperenderer.setSeriesFillPaint(0, Color.red);
		xylineandshaperenderer.setSeriesOutlinePaint(0, Color.gray);
		xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(0.5F));
		// 设置显示数据点
		// xylineandshaperenderer.setBaseItemLabelGenerator(new
		// StandardXYItemLabelGenerator());
		// xylineandshaperenderer.setBaseItemLabelsVisible(true);

		XYDataset xydataset1 = createNormalDataset(abnor, protocol2);
		// xydataset1.
		XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
		xyplot.setDataset(xyplot.getDatasetCount() + 1, xydataset1);
		xyplot.setRenderer(xyplot.getDatasetCount() + 1,
				xylineandshaperenderer1);
		// 设置不可见到点。
		xylineandshaperenderer1.setBaseShapesVisible(false);
		// 设置可以看见线。
		xylineandshaperenderer1.setSeriesLinesVisible(0, true);
		xylineandshaperenderer1.setSeriesShape(0, double1);
		// 设置线和点的颜色。
		xylineandshaperenderer1.setSeriesPaint(0, Color.orange);
		xylineandshaperenderer1.setSeriesFillPaint(0, Color.orange);
		xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.orange);
		xylineandshaperenderer1.setUseFillPaint(true);
		
		xylineandshaperenderer1
				.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		xylineandshaperenderer1.setSeriesStroke(0, new BasicStroke(0.5F));

		// //标红代码处
		/*
		 * for (int i = 0; i < _nor_model.size(); i++) { XYDataset xydataset2 =
		 * createmodeDataset(_nor_model.get(i), "_nor_model" + i);
		 * XYLineAndShapeRenderer xylineandshaperenderer2 = new
		 * XYLineAndShapeRenderer(); xyplot.setDataset(i + 2, xydataset2);
		 * xyplot.setRenderer(2 + i, xylineandshaperenderer2); // 设置不可见到点。
		 * xylineandshaperenderer2.setBaseShapesVisible(false); // 设置可以看见线。
		 * xylineandshaperenderer2.setSeriesLinesVisible(0, true);
		 * xylineandshaperenderer2.setSeriesShape(0, double1); // 设置线和点的颜色。
		 * xylineandshaperenderer2.setSeriesPaint(0, Color.red);
		 * xylineandshaperenderer2.setSeriesFillPaint(0, Color.red);
		 * xylineandshaperenderer2.setSeriesOutlinePaint(0, Color.red);
		 * xylineandshaperenderer2.setUseFillPaint(true);
		 * xylineandshaperenderer2 .setBaseItemLabelGenerator(new
		 * StandardXYItemLabelGenerator());
		 * xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(2.5F));
		 * 
		 * } for (int i = 0; i < _abnor_model.size(); i++) { XYDataset
		 * xydataset3 = createmodeDataset(_abnor_model.get(i), "_abnor_model" +
		 * i); XYLineAndShapeRenderer xylineandshaperenderer3 = new
		 * XYLineAndShapeRenderer(); xyplot.setDataset(i + 2 +
		 * _nor_model.size(), xydataset3); xyplot.setRenderer(i + 2 +
		 * _nor_model.size(), xylineandshaperenderer3); // 设置不可见到点。
		 * xylineandshaperenderer3.setBaseShapesVisible(false); // 设置可以看见线。
		 * xylineandshaperenderer3.setSeriesLinesVisible(0, true);
		 * xylineandshaperenderer3.setSeriesShape(0, double1); // 设置线和点的颜色。
		 * xylineandshaperenderer3.setSeriesPaint(0, Color.red);
		 * xylineandshaperenderer3.setSeriesFillPaint(0, Color.red);
		 * xylineandshaperenderer3.setSeriesOutlinePaint(0, Color.red);
		 * xylineandshaperenderer3.setUseFillPaint(true);
		 * xylineandshaperenderer3 .setBaseItemLabelGenerator(new
		 * StandardXYItemLabelGenerator());
		 * xylineandshaperenderer3.setSeriesStroke(0, new BasicStroke(2.5F));
		 * 
		 * }
		 */
		// 标红代码结束
		// 这里开始画虚线
		// /////////////////////////////////
		// 建立数据集

		XYDataset xydataset4 = createLineDataset(nor, abnor, mapAB, xyplot);

		// 显示两条折线中间的那条分界线，y=1
		ValueMarker valuemarker = new ValueMarker(1); // 水平线的值
		valuemarker.setLabelOffsetType(LengthAdjustmentType.EXPAND);
		valuemarker.setPaint(Color.green); // 线条颜色
		valuemarker.setStroke(new BasicStroke(1.0F)); // 粗细
		// valuemarker.setLabel("分界线"); //线条上显示的文本
		valuemarker.setLabelFont(new Font("SansSerif", 0, 11)); // 文本格式
		valuemarker.setLabelPaint(Color.red);
		valuemarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		valuemarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		xyplot.addRangeMarker(valuemarker);
		// //

		jfreechart.getLegend().setVisible(false);
		return jfreechart;
	}

	/*
	 * 获取两条DataItems曲线虚线线段集合
	 * 
	 * @param mapAB 保存每个模式的所有线段集合
	 */
	public static XYDataset createLineDataset(DataItems dataitems1,
			DataItems dataitems2, Map<String, ArrayList<LinePos>> mapAB,
			XYPlot xyplot) {
		// 获取模式的种类个数、
		int modelcount = mapAB.keySet().size();	
		long off1 = dataitems1.getElementAt(0).getTime().getTime();
		long off2 = dataitems2.getElementAt(0).getTime().getTime();
		long unit = 0;
		if(dataitems1.getLength()>0){
			unit=dataitems1.getElementAt(1).getTime().getTime()-off1;
		}else{
			unit=3600000;
		}
				
				
		

		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		// 为数据集添加数据

		int colorindex = 0;
		int renderercount = 2;
		for (Object se : mapAB.keySet()) {
			ArrayList<LinePos> s = mapAB.get(se);
			int oneModelCount = s.size();

			ArrayList<DataItems> dslist = new ArrayList<DataItems>();
			Iterator it = s.iterator();

			System.out.println("**************");
			System.out.println("模式=" + se + ":次数=" + oneModelCount);

			XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
			// 设置不可见到点。
			xylineandshaperenderer.setBaseShapesVisible(false);
			// 设置可以看见线。
			xylineandshaperenderer.setSeriesLinesVisible(0, true);
			// xylineandshaperenderer.setSeriesShape(0, double1); //看不懂
			// 设置线和点的颜色。
			xylineandshaperenderer.setSeriesPaint(0, getcolor(colorindex % 9));
			xylineandshaperenderer.setSeriesFillPaint(0,
					getcolor(colorindex % 9));
			xylineandshaperenderer.setSeriesOutlinePaint(0,
					getcolor(colorindex % 9));
			colorindex++;
			xylineandshaperenderer.setUseFillPaint(true);
			xylineandshaperenderer
					.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());

			xylineandshaperenderer.setBaseShapesVisible(false);
			xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(1.0F, 1,
					1, 1.0F, new float[] { 15F, 12F }, 0.0F)); // 虚线生效处
			xylineandshaperenderer
					.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
			xylineandshaperenderer.setBaseItemLabelsVisible(false);

			while (it.hasNext()) {
				LinePos temp = (LinePos) it.next();
				/*
				 * System.out.println("A_start:"+temp.A_start+" "+"A_end:"+temp.
				 * A_end);
				 * System.out.println("B_start:"+temp.B_start+" "+"B_end:"
				 * +temp.B_end); System.out.println(" ");
				 */
				/*
				 * XYSeries xyseries1 = new XYSeries(se+"起点线段"+(i)); XYSeries
				 * xyseries2 = new XYSeries(se+"终点线段"+i);
				 */
				// i++;
				DataItem d1 = new DataItem();
				d1 = dataitems1.getElementAt(temp.A_start);
				DataItem d2 = new DataItem();
				d2 = dataitems2.getElementAt(temp.B_start);
				DataItems ds1 = new DataItems();
				ds1.add1Data(d1);
				ds1.add1Data(d2);
				// dslist.add(ds1);
				XYDataset xydataset1 = createmodeDataset(ds1, off1,
						se.toString() + 1,unit);

				DataItem d3 = new DataItem();
				d3 = dataitems1.getElementAt(temp.A_end);
				DataItem d4 = new DataItem();
				d4 = dataitems2.getElementAt(temp.B_end);
				DataItems ds2 = new DataItems();
				ds2.add1Data(d3);
				ds2.add1Data(d4);
				// dslist.add(ds2);
				XYDataset xydataset2 = createmodeDataset(ds2, off1,
						se.toString() + 2,unit);

				int datasetCount = xyplot.getDatasetCount();
				XYTextAnnotation localXYTextAnnotation = null;
				int modelx = (temp.A_start + temp.B_end) / 2;
				double modely = (Double.parseDouble(dataitems2.getElementAt(
						temp.A_start).getData()) + Double
						.parseDouble(dataitems1.getElementAt(temp.B_end)
								.getData())) / 2;
				System.out.println("se=" + se + "::" + "x=" + modelx + "y="
						+ modely);
				localXYTextAnnotation = new XYTextAnnotation("" + se + ":"
						+ oneModelCount, modelx, modely);
				xyplot.addAnnotation(localXYTextAnnotation);
				System.out.println("datasetCount=" + datasetCount);
				xyplot.setDataset(datasetCount + 1, xydataset1);
				xyplot.setRenderer(datasetCount+1, xylineandshaperenderer);
				xyplot.setDataset(datasetCount + 2, xydataset2);
				xyplot.setRenderer(datasetCount+2, xylineandshaperenderer);
				

			}
//			/xyplot.setRenderer(renderercount++, xylineandshaperenderer);
			//renderercount++;

			// System.out.println(" (点数)dslist.size()="+ dslist.size()*2);

			/*
			 * for (int i1 = 0; i1 < dslist.size(); i1++) {
			 * 
			 * XYDataset xydataset = createmodeDataset(dslist.get(i1),off1,
			 * se.toString());
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * //return xyseriescollection;
			 * 
			 * 
			 * int datasetCount = xyplot.getDatasetCount(); //
			 * System.out.println("@@@@@@@@@@@datasetCount="+datasetCount);
			 * XYTextAnnotation localXYTextAnnotation = null;
			 * localXYTextAnnotation = new XYTextAnnotation(""+se, 30, 1);
			 * xyplot.addAnnotation(localXYTextAnnotation);
			 * xyplot.setDataset(datasetCount+1 , xydataset);
			 * xyplot.setRenderer(datasetCount+1 , xylineandshaperenderer);
			 * 
			 * }
			 */

		}
		return xyseriescollection;
	}

	

	/*
	 * 将完整的DataItems保存进数据集
	 */
	public static XYDataset createNormalDataset(DataItems normal,
			String protocol1) {
		// 获取正常数据的长度、
		int length = normal.getLength();
		int time[] = new int[length];
		XYSeries xyseries = new XYSeries(protocol1);
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();

		// 为数据集添加数据

		for (int i = 0; i < length; i++) {
			DataItem temp = new DataItem();

			temp = normal.getElementAt(i);

			System.out.println("DataItem.time=" + temp.getTime().getTime());

			xyseries.add(i, Double.parseDouble(temp.getData())); // 对应的横轴

		}
		xyseriescollection.addSeries(xyseries);
		return xyseriescollection;
	}

	// 主调用

	// 取出DataItems类型中的y值，返回XYDataset
	public static XYDataset createmodeDataset(DataItems normal, long offset1,
			String protocol2,long unit) {
		// 获取正常数据的长度、
		int length = normal.getLength();
		int time[] = new int[length];
		XYSeries xyseries = new XYSeries(protocol2);
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		// 为数据集添加数据
		long offset2 = normal.getElementAt(0).getTime().getTime();
		int off = (int) ((offset2 - offset1) / unit);
		for (int i = 0; i < length; i++) {
			DataItem temp = new DataItem();

			temp = normal.getElementAt(i);
			/*
			 * if(i==0){ System.out.println("第一个DataItem.time="+temp.getTime());
			 * }
			 */

			xyseries.add(i + off, Double.parseDouble(temp.getData())); // 对应的横轴

		}
		xyseriescollection.addSeries(xyseries);
		return xyseriescollection;
	}

	// 对异常点进行初始化
	/*
	 * public static XYDataset createAbnormalDataset(DataItems abnor) { //
	 * 统计异常点的长度 int length = abnor.getLength(); XYSeries xyseries = new
	 * XYSeries("频繁模式");
	 * 
	 * XYSeriesCollection xyseriescollection = new XYSeriesCollection();
	 * 
	 * // 添加数据值
	 * 
	 * for (int i = 0; i < length; i++) {
	 * 
	 * DataItem temp = new DataItem(); temp = abnor.getElementAt(i);
	 * xyseries.add((double) temp.getTime().getTime(),
	 * Double.parseDouble(temp.getData())); // 为什么要添加两遍？ xyseries.add((double)
	 * temp.getTime().getTime(), Double.parseDouble(temp.getData()));
	 * 
	 * } xyseriescollection.addSeries(xyseries); return xyseriescollection; }
	 */
	// 到这里结束

	/*
	 * 获取颜色，下标从0开始
	 */
	public static Color getcolor(int i) {
		switch (i) {
		case 1:
			return Color.red;
		case 2:
			return Color.blue;
		case 3:
			return Color.cyan;
		case 4:
			return Color.gray;
		case 5:
			return Color.green;
		case 6:
			return Color.magenta;
		case 7:
			return Color.orange;
		case 8:
			return Color.yellow;
		case 9:
			return Color.white;
		case 0:
			return Color.pink;
		default:
			return Color.black;
		}
	}

	public static void main(String[] args) {

		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(600, 300);
		shell.setLayout(new FillLayout());
		shell.setText("Time series demo  ");
		DataItems dataitem = new DataItems();

		// TimeSeriesChart1 s=new TimeSeriesChart1(shell, SWT.NULL,dataitem);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public class MyDataSet extends XYSeriesCollection {

	}

}
