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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

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
public class TimeSeriesChart2 extends Composite {
	ArrayList<DataItems> _nor_model_ = null;
	ArrayList<DataItems> _abnor_model_ = null;

	public TimeSeriesChart2(Composite parent, int style, ProtoclPair pp) {
		super(parent, style);

		String chartname = "协议" + pp.getProtocol1() + "/" + "协议"
				+ pp.getProtocol2() + "时间序列" + "(置信度：" + pp.confidence + ")";
		// 传入
		String protocol1 = pp.getProtocol1();
		String protocol2 = pp.getProtocol2();
		JFreeChart chart = createChart(pp.getDataItems1(), pp.getDataItems2(),
				chartname, protocol1, protocol2);
		// JFreeChart chart =
		// createChart(createDataset(ip1,ip2,ip1data,ip2data));

		ChartComposite frame = new ChartComposite(this, SWT.NONE, chart, true);
		frame.setDisplayToolTips(true);
		frame.setHorizontalAxisTrace(false);
		frame.setVerticalAxisTrace(false);
		this.setLayout(new FillLayout());

		// TODO Auto-generated constructor stub
	}

	

	public static JFreeChart createChart(DataItems nor, DataItems abnor,
			String title, String protocol1, String protocol2) {
		// 第一条线
		XYDataset xydataset1 = TimeSeriesChart1.createNormalDataset(nor,
				protocol1);
		JFreeChart jfreechart = ChartFactory.createScatterPlot(title, "时间",
				"值", xydataset1);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();

		XYLineAndShapeRenderer xylineandshaperenderer1 = (XYLineAndShapeRenderer) xyplot
				.getRenderer();
		// 设置数据集线条1，设置线条1的渲染器

		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(
				-4D, -4D, 6D, 6D);
		// 设置异常点提示红点大小

		// 设置不可看到点。
		xylineandshaperenderer1.setSeriesLinesVisible(0, true);
		xylineandshaperenderer1.setBaseShapesVisible(false);
		xylineandshaperenderer1.setSeriesShape(0, double1);
		xylineandshaperenderer1.setSeriesPaint(0, Color.blue);
		xylineandshaperenderer1.setSeriesFillPaint(0, Color.blue);
		xylineandshaperenderer1.setSeriesOutlinePaint(0, Color.blue);
		xylineandshaperenderer1.setSeriesStroke(0, new BasicStroke(0.5F));
	
		// 创造数据集，
		XYDataset xydataset2 = TimeSeriesChart1.createNormalDataset(abnor,
				protocol2);
/*		XYTextAnnotation localXYTextAnnotation = new XYTextAnnotation("aa",10,
				Double.parseDouble(abnor.getElementAt(10).getData()));
		xyplot.addAnnotation(localXYTextAnnotation);*/
		XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer();
		int datasetcount=xyplot.getDatasetCount();
		xyplot.setDataset(datasetcount, xydataset2);
		xyplot.setRenderer(datasetcount ,xylineandshaperenderer2);
/*		System.out.println("DatasetCount="+xyplot.getDatasetCount());
		
		for(int c=0;c<xyplot.getDatasetCount();c++){
			System.out.println("DatasetSeriesCount="+xyplot.getDataset(c).getSeriesCount());
			System.out.println("Dataset["+c+"]="+xyplot.getDataset(c).getSeriesKey(0));
		}*/
		
	/*	// 设置不可见到点。 。
		xylineandshaperenderer2.setBaseShapesVisible(false);
		// 设置可以看见线。
		xylineandshaperenderer2.setSeriesLinesVisible(0, true);
		xylineandshaperenderer2.setSeriesShape(0, double1);
		// 设置线和点的颜色。
		xylineandshaperenderer2.setSeriesPaint(0, Color.green);
		xylineandshaperenderer2.setSeriesFillPaint(0, Color.green);
		xylineandshaperenderer2.setSeriesOutlinePaint(0, Color.green);

		xylineandshaperenderer2.setUseFillPaint(true);
		xylineandshaperenderer2
				.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(0.5F));
		
*/
		xylineandshaperenderer2.setSeriesLinesVisible(0, true);
		xylineandshaperenderer2.setBaseShapesVisible(false);
		xylineandshaperenderer2.setSeriesShape(0, double1);
		xylineandshaperenderer2.setSeriesPaint(0, Color.GREEN);
		xylineandshaperenderer2.setSeriesFillPaint(0, Color.GREEN);
		xylineandshaperenderer2.setSeriesOutlinePaint(0, Color.green);
		xylineandshaperenderer2.setSeriesStroke(0, new BasicStroke(0.5F));
		jfreechart.getLegend().setVisible(true);
		return jfreechart;

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

}
