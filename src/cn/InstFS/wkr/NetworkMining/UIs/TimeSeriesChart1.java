package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
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
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.LocalPeriodDetectionWitnDTW;
import associationRules.LinePos;
import associationRules.ProtoclPair;

/**
 * 
 * @author 顺
 *
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

	XYDataset normalizationDataset;//

	JFreeChart chart;

	XYLineAndShapeRenderer barrenderer;

	public TimeSeriesChart1(Composite parent, int style, final ProtoclPair pp) {
		super(parent, style);
		String protocol1 = pp.getProtocol1();
		String protocol2 = pp.getProtocol2();
		final JScrollBar scroller;
		// 归一化两条曲线，只显示线段化得数据点

		final DataItems dataitems1 = DataItemsNormalization(pp.getDataItems1(),
				1);
		final DataItems dataitems2 = DataItemsNormalization(pp.getDataItems2(),
				0);

		// 原始序列数据集
		DataItems d1 = pp.getDataItems1();
		DataItems d2 = pp.getDataItems2();
		XYDataset initialDataset = createNormalDataset(d1, d2, protocol1,
				protocol2);
		// 创建关联挖掘图表的数据集，包含两条原始序列和模式序列数据
		createLineDataset(dataitems1, dataitems2, pp.getLineDataItems2(),
				pp.getLineDataItems1(), protocol1, protocol2, pp.getMapAB());

		// 创建原始图
		String chartname = "时间序列";
		String subtitle = "Confidence(" + protocol1 + "," + protocol2 + "):"
				+ String.format("%.4f", pp.confidence);
		chart = createChart(normalizationDataset, pp.getMapAB(), chartname,
				subtitle, protocol1, protocol2);
		JFreeChart initialChart = createChart(initialDataset, pp.getMapAB(),
				"原始图", null, protocol1, protocol2);
		// 原图显示坐标轴
		// ((XYPlot) initialChart.getPlot()).getRangeAxis().setVisible(true);

		GridLayout ParentsLayout = new GridLayout();
		ParentsLayout.numColumns = 1;
		ParentsLayout.horizontalSpacing = SWT.FILL;

		this.setLayout(ParentsLayout);
		this.layout();
		// ParentsLayout.type = SWT.VERTICAL;// 面板垂直显示控件

		barrenderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();

		// 初始化模式
		modelAnnotation = new HashMap<String, ArrayList<XYPolygonAnnotation>>();
		modelColor = new HashMap<String, Color>();

		ChartComposite frame = new ChartComposite(this, SWT.NONE, chart, true);
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

		// 初始化控制器父面板
		controller = new Group(this, SWT.None);
		GridLayout ControlLayout = new GridLayout();
		// ControlLayout.
		ControlLayout.numColumns = 16;
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
		int[] modelcount = new int[modelnum];
		for (int i = 0; i < modelnum; i++)

		{
			model[i] = Integer.parseInt("" + s[i]);
			modelcount[i] = pp.getMapAB().get(s[i]).size();
			// System.out.println("model:" + Integer.parseInt("" +
			// s[i])+" count"+modelcount[i]);
		}

		/*
		 * model 模式 modelcount 模式次数 此段对模式次数进行排序
		 */

		for (int i = 0; i < modelnum; i++) {
			int maxmodel = model[i];
			int maxmodelcount = modelcount[i];
			for (int j = i; j < modelnum; j++) {
				if (modelcount[j] > maxmodelcount) {
					maxmodel = model[j];
					model[j] = model[i];
					model[i] = maxmodel;

					maxmodelcount = modelcount[j];
					modelcount[j] = modelcount[i];
					modelcount[i] = maxmodelcount;
				}
			}
		}

		isModelSelected = new HashMap<String, Integer>();
		for (int i = 0; i < model.length; i++) {
			isModelSelected.put("" + model[i], 0);
		}

		final Button[] button = new Button[modelnum];
		for (int j = 0; j < modelnum; j++) {
			Color aColor = getColor(color++);
			modelColor.put("" + model[j], aColor);
			button[j] = new Button(controller, SWT.CHECK);
			Label colorLabel = new Label(controller, SWT.NULL);
			button[j].setText("模式:" + model[j] + "(" + modelcount[j] + ")");
			colorLabel.setText("——	");
			RGB rgb = new RGB(aColor.getRed(), aColor.getGreen(),
					aColor.getBlue());

			org.eclipse.swt.graphics.Color swtcolor = new org.eclipse.swt.graphics.Color(
					this.getDisplay(), rgb);
			colorLabel.setForeground(swtcolor);

			swtcolor.dispose();

			final int temp = j;
			button[j].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// System.out.println("model "+.getText()+"is selected!");
					// 注意：易错点 button文本为"模式:5(10)"
					// 意思为模式5出现了10次，以下挖出模式key,直接根据“（”会报错，解决办法使用\\(
					String key = (button[temp].getText().split(":")[1]
							.split("\\(")[0]);
					// System.out.println("key ="+key);
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

	/**
	 * @功能：模式线段显示绘图控制，只显示选中的按钮进行绘制线段
	 */
	public void paintModelSegment(
			Map<String, ArrayList<double[]>> IndexOfModelInDataset,
			String modelname) {
		ArrayList<double[]> modeldata;
		// System.out.println("keySet=" + IndexOfModelInDataset.keySet());
		ArrayList<XYPolygonAnnotation> list = new ArrayList<XYPolygonAnnotation>();
		if (IndexOfModelInDataset.keySet().contains(modelname)) {
			modeldata = IndexOfModelInDataset.get(modelname);
			Iterator it = modeldata.iterator();

			while (it.hasNext()) {
				XYPolygonAnnotation xypolygonannotation = new XYPolygonAnnotation(
						(double[]) it.next(), new BasicStroke(0.5F), new Color(
								255, 255, 255, 100), modelColor.get(modelname));// new
				// Color(200,
				// 200,
				// 255,
				// 100)
				xypolygonannotation.setToolTipText("Count:" + modeldata.size());

				list.add(xypolygonannotation);
				barrenderer
						.addAnnotation(xypolygonannotation, Layer.BACKGROUND);
				// xypolygonannotation.
			}

			//
			modelAnnotation.put(modelname, list);

		} else {
			System.out.println("null!");
		}
	}

	/**
	 * @功能：冒泡排序
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

	/**
	 * @功能：通过start end 取出DataItems上的线段保存进一个新的DataItems并返回
	 */
	public static DataItems getDataItems(DataItems dataitems, int start, int end) {
		DataItems ret = new DataItems();
		// ret.setItems(items);

		for (int i = start; i <= end; i++) {

			ret.add1Data(dataitems.getElementAt(i));
		}

		return ret;

	}

	/**
	 * @功能：归一化DataItems中的List<String>,并且曲线向上平移addanumber个单位长度
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

	/**
	 * @功能：创建主图
	 * @param initialDataset
	 * @param mapAB
	 * @param chartname
	 * @param subtitle
	 * @param protocol1
	 * @param protocol2
	 * @return
	 */
	public static JFreeChart createChart(XYDataset initialDataset,
			Map<String, ArrayList<LinePos>> mapAB, String chartname,
			String subtitle, String protocol1, String protocol2) {

		JFreeChart jfreechart = ChartFactory.createXYLineChart(chartname,
				"Time", "Value", initialDataset);
		if (subtitle != null) {
			jfreechart.addSubtitle(new TextTitle(subtitle));
		}

		jfreechart.getLegend().visible = false;

		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		ValueAxis rangeAxis = xyplot.getRangeAxis();
		rangeAxis.setVisible(false);
		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		// numberaxis.setVisible(false);
		java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(
				-4D, -4D, 6D, 6D);

		XYLineAndShapeRenderer barrenderer = (XYLineAndShapeRenderer) xyplot
				.getRenderer();

		// 设置不可看到点。
		barrenderer.setSeriesLinesVisible(0, true);
		barrenderer.setBaseShapesVisible(false);
		barrenderer.setSeriesShape(0, double1);
		Color color = new Color(0, 0, 255, 150);
		barrenderer.setSeriesPaint(0, color);
		barrenderer.setSeriesFillPaint(0, color);
		barrenderer.setSeriesOutlinePaint(0, color);
		barrenderer.setSeriesStroke(0, new BasicStroke(0.5F));

		jfreechart.getLegend().setVisible(true);
		return jfreechart;
	}

	/**
	 * @功能：获取两条DataItems曲线虚线线段集合
	 * @param dataitems1
	 * @param dataitems2
	 * @param protocol1
	 * @param protocol2
	 * @param mapAB
	 *            保存每个模式的所有线段集合
	 */
	public void createLineDataset(DataItems dataitems1, DataItems dataitems2,
			DataItems indexdataitems1, DataItems indexdataitems2,
			String protocol1, String protocol2,
			Map<String, ArrayList<LinePos>> initmapAB) {

		Map<String, ArrayList<LinePos>> mapAB = new HashMap<String, ArrayList<LinePos>>();
		mapAB.putAll(initmapAB);

		// 获取模式的种类个数、
		int modelcount = mapAB.keySet().size();
		if (modelcount == 0)
			return;

		long off1 = dataitems1.getElementAt(0).getTime().getTime();
		long off2 = dataitems2.getElementAt(0).getTime().getTime();
		long off = off1 > off2 ? off2 : off1;
		// 設置时间粒度unit
		long unit = 0;
		if (dataitems1.getLength() > 0) {
			unit = dataitems1.getElementAt(1).getTime().getTime() - off;
		} else {
			unit = 3600000;
		}// 设置结束

		/*
		 * XYSeries modelxyseries1 = new XYSeries(protocol1);// 保存曲线1的模式线段
		 * XYSeries modelxyseries2 = new XYSeries(protocol2);// 保存曲线2的模式线段
		 */
		XYSeries initseries1 = new XYSeries(protocol1);// 保存曲线1，包含模式线段线段化（线段化：同一个模式线段首位相连）过后的结束
		XYSeries initseries2 = new XYSeries(protocol2);// 保存曲线2，包含模式线段线段化（线段化：同一个模式线段首位相连）过后的结束

		int initseries1start = 0;// 标记曲线1的索引
		int initseries2start = 0;

		modelDataset = new HashMap<String, ArrayList<double[]>>();// 最后要将模式线段的区域保存在次数据集

		XYSeriesCollection LineDataset = new XYSeriesCollection();

		// 先进性相邻模式合并
		/*
		 * for (Object se : mapAB.keySet()) { ArrayList<LinePos> s =
		 * SegmentDataMerging(mapAB.get(se));// mapAB..remove(se);
		 * mapAB.put(""+se, s); }
		 */
		// 功能：保存线段化后的模式线段数据
		Iterator<Entry<String, ArrayList<LinePos>>> mapit = mapAB.entrySet()
				.iterator();
		Map<String, ArrayList<LinePos>> CopyMapAB = new HashMap<String, ArrayList<LinePos>>();
		while (mapit.hasNext()) {
			Entry e = mapit.next();
			String se = (String) e.getKey();
			ArrayList<double[]> aModelIndex = new ArrayList<double[]>();
			ArrayList<LinePos> s = SegmentDataMerging(mapAB.get(se));//
			mapit.remove();// ///?????
			CopyMapAB.put(se, s);

			Iterator it = s.iterator();

			while (it.hasNext()) {
				LinePos temp = (LinePos) it.next();
				double[] area = new double[8];

				DataItem d1 = new DataItem();

				d1 = dataitems1.getElementAt(temp.A_start);
				area[0] = temp.A_start;
				area[1] = Double.parseDouble(d1.getData());

				// modelxyseries1.add(area[0], area[1]);

				d1 = dataitems1.getElementAt(temp.A_end);
				area[6] = temp.A_end;
				area[7] = Double.parseDouble(d1.getData());
				// modelxyseries1.add(area[6], area[7]);
				// initseries1start=temp.A_end;
				// modelxyseries1.add(area[6], null);// 防止不同线段连接

				d1 = dataitems2.getElementAt(temp.B_start);
				area[2] = temp.B_start;
				area[3] = Double.parseDouble(d1.getData());
				// modelxyseries2.add(area[2], area[3]);

				d1 = dataitems2.getElementAt(temp.B_end);
				area[4] = temp.B_end;
				area[5] = Double.parseDouble(d1.getData());
				// modelxyseries2.add(area[4], area[5]);
				// modelxyseries2.add(area[4], null);// 防止不同线段连接

				// 遍历模式中序列的点
				// 取出曲线1中不是模式内的所有点，加入数据集modelxyseries1

				aModelIndex.add(area);

			}

			modelDataset.put("" + se, aModelIndex);

		}
		//
		mapAB.putAll(CopyMapAB);

		// 排序：将所有的 不同的模式的模式线段进行排序，保存在allModelList
		ArrayList<LinePos> allModelList = new ArrayList<LinePos>();
		for (Object se : mapAB.keySet()) {
			allModelList.addAll(mapAB.get(se));
		}

		// 对ArrayList<LinePos> 进行排序：根据linPos.A_start
		Comparator<LinePos> comparator = new Comparator<LinePos>() {
			public int compare(LinePos s1, LinePos s2) {
				// 先排年龄
				if (s1.A_start > s2.A_start) {
					return 1;
				} else if (s1.A_start < s2.A_start) {
					return -1;
				} else {
					return 0;
				}
			}
		};
		Collections.sort(allModelList, comparator);
		// 排序：结束

		// 功能：遍历allModelList，保存模式线段线段化后的结果
		Iterator itall = allModelList.iterator();
		Iterator<String> indexit1 = indexdataitems1.data.iterator();
		Iterator<String> indexit2 = indexdataitems2.data.iterator();
		initseries1start = (indexit1.hasNext()) ? (Integer.parseInt(indexit1
				.next())) : -1;
		initseries2start = (indexit2.hasNext()) ? (Integer.parseInt(indexit2
				.next())) : -1;
		while (itall.hasNext() && (initseries1start >= 0)
				&& (initseries2start >= 0)) {
			LinePos linpos = (LinePos) itall.next();
			// 遍历曲线1 的 模式线段前的原始线段，保存进initseries1

			while (initseries1start <= linpos.A_start) {
				initseries1.add(
						initseries1start,
						Double.parseDouble(dataitems1.getElementAt(
								initseries1start).getData()));
				initseries1start = (indexit1.hasNext()) ? (Integer
						.parseInt(indexit1.next())) : -1;
			}
			// 保存模式线段 initseries1
			// 此时initseries1start=linpos.A_start+1
			while (initseries1start < linpos.A_end) {
				initseries1start = (indexit1.hasNext()) ? (Integer
						.parseInt(indexit1.next())) : -1;// 跳过模式线段中间的点
			}
			/*
			 * initseries1.add(linpos.A_start, Double.parseDouble(dataitems1
			 * .getElementAt(linpos.A_start).getData()));
			 */

			initseries1.add(linpos.A_end, Double.parseDouble(dataitems1
					.getElementAt(linpos.A_end).getData()));
			initseries1start = (indexit1.hasNext()) ? (Integer
					.parseInt(indexit1.next())) : -1;
			;// 此时initseriesstart=linpos.A_end+1
			// 遍历曲线2 的 模式线段前的原始线段，保存进initseries2
			while (initseries2start <= linpos.B_start) {
				initseries2.add(
						initseries2start,
						Double.parseDouble(dataitems2.getElementAt(
								initseries2start).getData()));
				// initseries2start++;
				initseries2start = (indexit2.hasNext()) ? (Integer
						.parseInt(indexit2.next())) : -1;//
			}
			// 保存模式线段 initseries2
			while (initseries2start < linpos.B_end) {
				initseries2start = (indexit2.hasNext()) ? (Integer
						.parseInt(indexit2.next())) : -1;// 跳过模式线段中间的点
			}

			// initseries2start=(indexit2.hasNext())?
			// (Integer.parseInt(indexit2.next())):-1;//此时initseriesstart就是linpos.B_start
			initseries2.add(linpos.B_end, Double.parseDouble(dataitems2
					.getElementAt(linpos.B_end).getData()));
			// initseries2start = linpos.B_end;
			initseries2start = (indexit2.hasNext()) ? (Integer
					.parseInt(indexit2.next())) : -1;// 此时initseriesstart就是linpos.B_end
		}

		// 功能：结束

		/*
		 * LineDataset.addSeries(modelxyseries1);
		 * LineDataset.addSeries(modelxyseries2);
		 */
		LineDataset.addSeries(initseries1);
		LineDataset.addSeries(initseries2);

		normalizationDataset = LineDataset;
		// return defaultcategorydataset;
	}

	/**
	 * @功能：模式线段合并，即对线段的ArrayList<LinePos> 
	 *                                    按照开始点进行排序，然后将[t1,t2][t2,t3]这种模式合并成[t1,t3
	 *                                    ]
	 * 
	 * 
	 */
	public ArrayList<LinePos> SegmentDataMerging(ArrayList<LinePos> olds) {
		ArrayList<LinePos> news = new ArrayList<LinePos>();
		LinePos t1;
		LinePos t2;
		LinePos mergeLinPos = new LinePos();
		int IsMergeLinPosNull = 1;

		// 对ArrayList<LinePos> olds进行排序：根据linPos.A_start
		Comparator<LinePos> comparator = new Comparator<LinePos>() {
			public int compare(LinePos s1, LinePos s2) {
				// 先排
				if (s1.A_start > s2.A_start) {
					return 1;
				} else if (s1.A_start < s2.A_start) {
					return -1;
				} else {
					return 0;
				}
			}
		};
		Collections.sort(olds, comparator);
		// 排序结束
		// 功能：同一种模式线段相连，也进行合并
		Iterator newit = news.iterator();
		Iterator oldit = olds.iterator();

		int index = 0;
		while (oldit.hasNext()) {
			t1 = (LinePos) oldit.next();
			if (IsMergeLinPosNull != 1) {

				if ((mergeLinPos.A_end == t1.A_start)
						&& (mergeLinPos.B_end == t1.B_start)) {
					mergeLinPos.A_end = t1.A_end;
					mergeLinPos.B_end = t1.B_end;
				} else {
					LinePos temp = new LinePos();
					temp.A_start = mergeLinPos.A_start;
					temp.A_end = mergeLinPos.A_end;
					temp.B_start = mergeLinPos.B_start;
					temp.B_end = mergeLinPos.B_end;
					news.add(temp);
					IsMergeLinPosNull = 1;
				}
			} else {
				mergeLinPos.A_start = t1.A_start;
				mergeLinPos.A_end = t1.A_end;
				mergeLinPos.B_start = t1.B_start;
				mergeLinPos.B_end = t1.B_end;
				IsMergeLinPosNull = 0;
			}

		}// 功能：结束

		return news;

	}

	/**
	 * @功能：将完整的DataItems保存进数据集
	 * @param normal
	 * @param protocol1
	 * @return
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

	/**
	 * @功能：重载createNormalDataset,传进两个dataItems,放进同一个数据集合中
	 * @param dataitems1
	 * @param dataitems2
	 * @param protocol1
	 * @param protocol2
	 * @return
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
		// 序列1数据集合
		for (int i = 0; i < length1; i++) {
			DataItem temp = new DataItem();
			temp = dataitems1.getElementAt(i);
			xyseries1.add(i, Double.parseDouble(temp.getData())); // 对应的横轴

		}
		// 序列2数据集合
		for (int i = 0; i < length2; i++) {
			DataItem temp = new DataItem();

			temp = dataitems2.getElementAt(i);
			xyseries2.add(i, Double.parseDouble(temp.getData())); // 对应的横轴
		}
		xyseriescollection.addSeries(xyseries1);
		xyseriescollection.addSeries(xyseries2);

		return xyseriescollection;
	}

	/**
	 * @功能：获取颜色，下标从0开始
	 * @param i
	 * @return
	 */
	public static Color getColor(int i) {
		int j = i % 10;
		System.out.println("color" + j);
		switch (j) {
		case 1:
			return (new Color(173, 137, 118, 200));
		case 0:
			return (new Color(230, 155, 3, 200));
		case 3:
			return (new Color(209, 73, 78, 200));
		case 4:
			return (new Color(18, 53, 85, 200));
		case 5:
			return (new Color(225, 238, 210, 200));
		case 6:
			return (new Color(180, 141, 1, 200));
		case 7:
			return (new Color(1, 165, 175, 200));
		case 9:
			return (new Color(53, 44, 10, 200));
		case 8:
			return (new Color(255, 255, 255, 200));
		case 2:
			return (new Color(36, 169, 225, 200));
		default:
			return (new Color(0, 0, 0, 200));
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

}
