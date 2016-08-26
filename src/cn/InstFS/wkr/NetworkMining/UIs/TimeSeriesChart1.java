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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYPolygonAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import associationRules.LinePos;
import associationRules.ProtoclPair;

/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class TimeSeriesChart1 extends Composite {

	public Group controller = null;
	public int color = 0;

	int modelnum;
	final Button[] button = null;
	Map<String, Integer> isModelSelected;
	Map<String, ArrayList<XYPolygonAnnotation>> modelAnnotation;
	Map<String, ArrayList<double[]>> modelDataset;
	Map<String, Color> modelColor;

	XYDataset initialDataset;

	JFreeChart chart;

	XYLineAndShapeRenderer barrenderer;

	public TimeSeriesChart1(Composite parent, int style, final ProtoclPair pp) {
		super(parent, style);
		String protocol1 = pp.getProtocol1();
		String protocol2 = pp.getProtocol2();
		final JScrollBar scroller;
		// 归一化两条曲线
		final DataItems dataitems1 = DataItemsNormalization(pp.getDataItems1(),
				1);
		final DataItems dataitems2 = DataItemsNormalization(pp.getDataItems2(),
				0);

		// 初始化原始序列数据集合
		initialDataset = createNormalDataset(dataitems1, dataitems2, protocol1,
				protocol2);

		createLineDataset(dataitems1, dataitems2, pp.getMapAB());

		// 创建原始图
		String chartname =" TimeSeriesChart" ;
		String subtitle ="Confidence("+protocol1+","+protocol2+"):"
				+ pp.confidence  ;
		chart = createChart(initialDataset, pp.getMapAB(), chartname,subtitle,
				protocol1, protocol2);
		JFreeChart initialChart = createChart(initialDataset, pp.getMapAB(),
				chartname, null,protocol1, protocol2);

		GridLayout ParentsLayout = new GridLayout();
		ParentsLayout.numColumns = 1;
		ParentsLayout.horizontalSpacing = SWT.FILL;

		this.setLayout(ParentsLayout);
		this.layout();
		// ParentsLayout.type = SWT.VERTICAL;// 面板垂直显示控件

		barrenderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();

		// 初始化模式
		modelAnnotation = new HashMap<String, ArrayList<XYPolygonAnnotation>>();
		modelColor=new HashMap<String,Color>();

		ChartComposite frame = new ChartComposite(this, SWT.NONE, chart, true);

		frame.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseScrolled(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		frame.setDisplayToolTips(true);
		frame.setHorizontalAxisTrace(false);
		frame.setVerticalAxisTrace(false);

		GridData griddata = new GridData();
		griddata.verticalSpan = 4; // 垂直占4格
		griddata.horizontalAlignment = SWT.FILL;// 水平铺满
		griddata.verticalAlignment = SWT.FILL;// 垂直铺满
		griddata.grabExcessVerticalSpace = true;// 设置垂直抢占
		griddata.grabExcessHorizontalSpace = true;
		frame.setLayoutData(griddata);
		// frame.pack();

		// 初始化模式虚线数据集
		/*
		 * createLineDataset(dataitems1, dataitems2, pp.getMapAB(),
		 * chart.getCategoryPlot(), controller); slidingModelDataset = new
		 * SlidingCategoryDataset(modelDataSet, 0, 30);
		 */
		// 初始化控制器父面板
		controller = new Group(this, SWT.None);
		GridLayout ControlLayout = new GridLayout();
		// ControlLayout.
		ControlLayout.numColumns = 10;
		controller.setLayout(ControlLayout);

		GridData griddata0 = new GridData();
		griddata0.verticalSpan = 2; // 垂直占4格
		griddata0.horizontalAlignment = SWT.FILL;// 水平铺满
		griddata0.verticalAlignment = SWT.FILL;// 垂直铺满
		griddata0.grabExcessVerticalSpace = true;// 设置垂直抢占
		griddata0.grabExcessHorizontalSpace = true;
		frame.setLayoutData(griddata0);
		// controller.setBackground(Color);

		Object[] s = pp.getMapAB().keySet().toArray();
		modelnum = s.length;
		int[] model = new int[modelnum];
		for (int i = 0; i < modelnum; i++)

		{
			model[i] = Integer.parseInt("" + s[i]);
			System.out.println("model:" + Integer.parseInt("" + s[i]));
		}
		model = sort(model);
		isModelSelected = new HashMap<String, Integer>();
		for (int i = 0; i < model.length; i++) {
			isModelSelected.put("" + model[i], 0);
		}

		final Button[] button = new Button[modelnum];
		for (int j = 1; j < modelnum; j++) {
			Color aColor=getColor(color++);
			modelColor.put(""+model[j], aColor);
			button[j] = new Button(controller, SWT.CHECK);
			Label colorLabel=new Label(controller,SWT.NULL);
			colorLabel.setText("———");
			//colorLabel.setForeground(modelColor.get(model[j]));
			button[j].setText("模式:" + model[j]);
			final int temp = j;
			button[j].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// System.out.println("model "+.getText()+"is selected!");
					String key = (button[temp].getText().split(":")[1]);
					// System.out.println("model key="+key);
					if (button[temp].getSelection()) {

						isModelSelected.remove(key);
						isModelSelected.put(key, 1);

						// 绘制线段图形，刷新图表
						paintModelSegment(modelDataset, key);

					} else {
						isModelSelected.remove(key);
						isModelSelected.put(key, 0);
						// 除去模式区域
						ArrayList<XYPolygonAnnotation> aModelList = modelAnnotation
								.get(key);
						Iterator it = aModelList.iterator();
						while (it.hasNext()) {
							barrenderer.removeAnnotation((XYAnnotation) it
									.next());
						}

					}

				}
			});
		}

		ChartComposite frame2 = new ChartComposite(this, SWT.NONE,
				initialChart, true);
		frame2.setLayoutData(griddata);

	}

	/*
	 * 模式线段显示绘图控制，只显示选中的按钮进行绘制线段
	 */
	public void paintModelSegment(
			Map<String, ArrayList<double[]>> IndexOfModelInDataset,
			String modelname) {
		ArrayList<double[]> modeldata;
		System.out.println("keySet=" + IndexOfModelInDataset.keySet());
		ArrayList<XYPolygonAnnotation> list = new ArrayList<XYPolygonAnnotation>();
		if (IndexOfModelInDataset.keySet().contains(modelname)) {
			modeldata = IndexOfModelInDataset.get(modelname);
			Iterator it = modeldata.iterator();

			while (it.hasNext()) {
				XYPolygonAnnotation xypolygonannotation = new XYPolygonAnnotation(
						(double[]) it.next(), null, null,modelColor.get(modelname));// new
																			// Color(200,
																			// 200,
																			// 255,
																			// 100)
				xypolygonannotation.setToolTipText("Count:" + modeldata.size());
				
				list.add(xypolygonannotation);
				barrenderer
						.addAnnotation(xypolygonannotation, Layer.BACKGROUND);
			}


			//
			modelAnnotation.put(modelname, list);

		} else {
			System.out.println("null!");
		}
	}

	/*
	 * 冒泡排序
	 */
	public static int[] sort(int[] values) {
		int temp;
		for (int i = 0; i < values.length; i++) {// 趟数
			for (int j = 0; j < values.length - i - 1; j++) {// 比较次数
				if (values[j] > values[j + 1]) {
					temp = values[j];
					values[j] = values[j + 1];
					values[j + 1] = temp;
				}
			}
		}
		return values;
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
		List<String> datanormolization = new ArrayList<String>();
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
				}
				datanormolization.add(Double.toString(temp2normalization));
			}
			// 把归一化后的List<String> datanormolization 加入DataItems
			ret.setData(datanormolization);

		} else {
			System.out
					.println(" TimeSeriesChart1 function DataItemsNormalization  data is null");
		}
		return ret;

	}

	public static JFreeChart createChart(XYDataset initialDataset,
			Map<String, ArrayList<LinePos>> mapAB, String chartname,String subtitle,
			String protocol1, String protocol2) {

		JFreeChart jfreechart = ChartFactory.createXYLineChart(chartname,
				"Time", "Value", initialDataset);
		if(subtitle!=null){
			jfreechart.addSubtitle(new TextTitle(subtitle));
		}
		
		jfreechart.getLegend().visible = false;

		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		ValueAxis rangeAxis=xyplot.getRangeAxis();
		rangeAxis.setVisible(false);
		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
	//	numberaxis.setVisible(false);
		java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(
				-4D, -4D, 6D, 6D);

		XYLineAndShapeRenderer barrenderer = (XYLineAndShapeRenderer) xyplot
				.getRenderer();

		// 设置不可看到点。
		barrenderer.setSeriesLinesVisible(0, true);
		barrenderer.setBaseShapesVisible(false);
		barrenderer.setSeriesShape(0, double1);
		barrenderer.setSeriesPaint(0, Color.blue);
		barrenderer.setSeriesFillPaint(0, Color.blue);
		barrenderer.setSeriesOutlinePaint(0, Color.blue);
		barrenderer.setSeriesStroke(0, new BasicStroke(0.5F));

		// 显示两条折线中间的那条分界线，y=1
/*		ValueMarker valuemarker = new ValueMarker(1); // 水平线的值
		valuemarker.setLabelOffsetType(LengthAdjustmentType.EXPAND);
		valuemarker.setPaint(Color.black); // 线条颜色
		valuemarker.setStroke(new BasicStroke(1.0F)); // 粗细
		// valuemarker.setLabel("分界线"); //线条上显示的文本
		valuemarker.setLabelFont(new Font("SansSerif", 0, 11)); // 文本格式
		valuemarker.setLabelPaint(Color.red);
		valuemarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		valuemarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		xyplot.addRangeMarker(valuemarker);*/
		// //

		jfreechart.getLegend().setVisible(true);
		return jfreechart;
	}

	/*
	 * 获取两条DataItems曲线虚线线段集合
	 * 
	 * @param mapAB 保存每个模式的所有线段集合
	 */
	public void createLineDataset(DataItems dataitems1, DataItems dataitems2,
			Map<String, ArrayList<LinePos>> mapAB) {
		// 获取模式的种类个数、
		int modelcount = mapAB.keySet().size();
		if (modelcount == 0)
			return;

		long off1 = dataitems1.getElementAt(0).getTime().getTime();
		long off2 = dataitems2.getElementAt(0).getTime().getTime();
		long unit = 0;
		if (dataitems1.getLength() > 0) {
			unit = dataitems1.getElementAt(1).getTime().getTime() - off1;
		} else {
			unit = 3600000;
		}

		modelDataset = new HashMap<String, ArrayList<double[]>>();
		System.out.println("Set：" + mapAB.keySet());
		for (Object se : mapAB.keySet()) {
			// System.out.println("选中了：" + modelname + ", se:" + se + ".");

			// System.out.println("进入：" + " se:" + se);
			int modeltime = 0;
			ArrayList<double[]> aModelIndex = new ArrayList<double[]>();
			ArrayList<LinePos> s = mapAB.get(se);
			int oneModelCount = s.size();

			Iterator it = s.iterator();

			System.out.println("**************");

			while (it.hasNext()) {
				LinePos temp = (LinePos) it.next();
				double[] area = new double[8];

				DataItem d1 = new DataItem();
				d1 = dataitems1.getElementAt(temp.A_start);
				DataItem d2 = new DataItem();
				d2 = dataitems2.getElementAt(temp.B_start);
				// DataItems ds1 = new DataItems();

				area[0] = (d1.getTime().getTime() - off1) / unit;
				area[1] = Double.parseDouble(d1.getData());

				area[2] = (d2.getTime().getTime() - off2) / unit;
				area[3] = Double.parseDouble(d2.getData());

				DataItem d3 = new DataItem();
				d3 = dataitems2.getElementAt(temp.B_end);
				DataItem d4 = new DataItem();
				d4 = dataitems1.getElementAt(temp.A_end);

				area[4] = (d3.getTime().getTime() - off2) / unit;
				area[5] = Double.parseDouble(d3.getData());

				area[6] = (d4.getTime().getTime() - off1) / unit;
				area[7] = Double.parseDouble(d4.getData());

				System.out.println("**********");
				for (int i = 0; i < area.length; i = i + 2) {
					System.out.println("(X,Y):" + area[i] + " " + area[i + 1]);
				}

				aModelIndex.add(area);

			}
			modelDataset.put("" + se, aModelIndex);

		}

		// return defaultcategorydataset;
	}

	/*
	 * 将完整的DataItems保存进数据集
	 */
	public static CategoryDataset createNormalDataset(DataItems normal,
			String protocol1) {
		// 获取正常数据的长度、
		int length = normal.getLength();
		int time[] = new int[length];
		/*
		 * XYSeries xyseries = new XYSeries(protocol1); XYSeriesCollection
		 * xyseriescollection = new XYSeriesCollection();
		 */
		DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
		// 为数据集添加数据

		for (int i = 0; i < length; i++) {
			DataItem temp = new DataItem();

			temp = normal.getElementAt(i);

			// System.out.println("DataItem.time=" + temp.getTime().getTime());

			defaultcategorydataset.addValue(Double.parseDouble(temp.getData()),
					protocol1, "" + i); // 对应的横轴

		}

		return defaultcategorydataset;
	}

	/*
	 * 重载createNormalDataset,传进两个dataItems,放进同一个数据集合中
	 */
	public static XYDataset createNormalDataset(DataItems dataitems1,
			DataItems dataitems2, String protocol1, String protocol2) {
		// 获取正常数据的长度、
		int length1 = dataitems1.getLength();
		int length2 = dataitems2.getLength();

		XYSeries xyseries1 = new XYSeries(protocol1);
		XYSeries xyseries2 = new XYSeries(protocol2);
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();

		// 为数据集添加数据

		for (int i = 0; i < length1; i++) {
			DataItem temp = new DataItem();
			temp = dataitems1.getElementAt(i);
			xyseries1.add(i, Double.parseDouble(temp.getData())); // 对应的横轴

		}
		for (int i = 0; i < length2; i++) {
			DataItem temp = new DataItem();

			temp = dataitems2.getElementAt(i);
			xyseries2.add(i, Double.parseDouble(temp.getData())); // 对应的横轴
		}
		xyseriescollection.addSeries(xyseries1);
		xyseriescollection.addSeries(xyseries2);

		return xyseriescollection;
	}

	// 主调用

	// 取出DataItems类型中的y值，返回XYDataset
	/*
	 * public static DefaultCategoryDataset createmodeDataset(DataItems normal,
	 * long offset1, String seriesName, long unit) { // 获取正常数据的长度、 int length =
	 * normal.getLength(); int time[] = new int[length]; DefaultCategoryDataset
	 * defaultcategorydataset = new DefaultCategoryDataset(); // 为数据集添加数据 long
	 * offset2 = normal.getElementAt(0).getTime().getTime(); int off = (int)
	 * ((offset2 - offset1) / unit); for (int i = 0; i < length; i++) { DataItem
	 * temp = new DataItem();
	 * 
	 * temp = normal.getElementAt(i);
	 * 
	 * if(i==0){ System.out.println("第一个DataItem.time="+temp.getTime()); }
	 * 
	 * 
	 * defaultcategorydataset.addValue(Double.parseDouble(temp.getData()),
	 * seriesName, "" + i + off); // 对应的横轴
	 * 
	 * }
	 * 
	 * return defaultcategorydataset; }
	 */

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
	public static Color getColor(int i) {
		int j = i % 10;
		System.out.println("color" + j);
		switch (j) {
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
