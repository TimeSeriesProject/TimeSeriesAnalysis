package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.NodeSection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialPeriodAlgorithm.Pair;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerPM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPartialPeriod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @// TODO: 17-1-3 chen 部分周期显示结果,具体需要修改
 */
public class PanelShowResultsPartialPeriod extends JPanel implements
		IPanelShowResults {

	private INetworkMiner miner;
	DecimalFormat formatter = new DecimalFormat("0.00");
	// JDesktopPane desktopPane;

	DataItems data = null;
	org.jfree.data.xy.XYSeriesCollection XYSeriesCollection = null;
	XYSeriesCollection selectionSeries = null;
	JFreeChart jfreechart;
	Map<String, ArrayList<Pair>> resultMap;
	XYPlot plot = null;

	boolean displayed = false;

	public PanelShowResultsPartialPeriod(TaskElement task) {
		// this();
		InitMiner(task);
	}

	private void InitMiner(TaskElement task) {
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
	}


	/**
	 * 画标记线分割周期
	 */
	private void PaintDomainMarker(DataItems data,
			Map<String, ArrayList<Pair>> map, XYPlot plot) {
		// 部分周期序列
		if (map == null || map == null)
			return;
		for (Map.Entry<String, ArrayList<Pair>> entry : map.entrySet()) {
			Iterator<Pair> it = entry.getValue().iterator();

			ValueMarker valuemarker = new ValueMarker(it.hasNext() ? it.next()
					.getBegin() : 0);
			valuemarker.setPaint(new Color(100, 100, 100, 250));
			valuemarker.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL, 0, new float[] { 10, 4 }, 0));
			plot.addDomainMarker(valuemarker);

			ValueMarker valuemarkerEnd = new ValueMarker(entry.getValue()
					.size() == 0 ? 0 : entry.getValue()
					.get(entry.getValue().size() - 1).getEnd());
			valuemarkerEnd.setPaint(new Color(100, 100, 100, 250));
			valuemarkerEnd.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL, 0, new float[] { 10, 4 }, 0));
			plot.addDomainMarker(valuemarkerEnd);
		}

		/*
		 * Object[] key = map.keySet().toArray(); for (int i = 0; i <
		 * map.size(); i++) { ArrayList<NodeSection> list = map.get(key[i]);
		 * java.util.Iterator<NodeSection> it = list.iterator(); int b; int e =
		 * 0; while (it.hasNext()) { NodeSection nodesection = it.next(); b =
		 * nodesection.begin; e = nodesection.end; int p = (int) key[i]; for
		 * (int j = b; j <= e; j += p) { ValueMarker valuemarker3 = new
		 * ValueMarker(j); valuemarker3.setPaint(new Color(100, 100, 100, 250));
		 * valuemarker3.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
		 * BasicStroke.JOIN_BEVEL, 0, new float[] { 10, 4 }, 0));
		 * 
		 * plot.addDomainMarker(valuemarker3); }
		 * 
		 * } }
		 */
	}

	/**
	 * 读取数据，生成Dataset
	 * 
	 * @param data
	 * @param map
	 * @return
	 */
	private static XYDataset createDataset(DataItems data,
			Map<String, ArrayList<Pair>> map) {
		if (map == null || data == null) {
			System.out.println("部分周期显示：map==null或者data==null");
			return null;
		}
		XYSeries initSeries = new XYSeries("原序列");
		XYSeriesCollection XYSeriesCollection = new XYSeriesCollection();

		// 部分周期序列
		int modelnum=1;
		for (Map.Entry<String, ArrayList<Pair>> entry : map.entrySet()) {
			XYSeries xyseries = new XYSeries("模式："+(modelnum++));
			//
			Iterator<Pair> it = entry.getValue().iterator();
			while (it.hasNext()) {
				Pair p = it.next();
				for (int i = p.getBegin(); i <= p.getEnd(); i++) {
					xyseries.add(i,
							Double.parseDouble(data.getElementAt(i).getData()));
				}
				xyseries.add(p.getEnd() + 1, null);// 避免部分周期因为在同一个数据集中间连起来
			}
				XYSeriesCollection.addSeries(xyseries);
		}
		// 原始序列
		for (int i = 0; i < data.getLength(); i++) {
			DataItem temp = new DataItem();
			temp = data.getElementAt(i);
			initSeries.add(i, Double.parseDouble(temp.getData())); // 对应的横轴
		}
		XYSeriesCollection.addSeries(initSeries);
		return XYSeriesCollection;
	}

	private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart("部分周期发现", "时间",
				miner.getTask().getMiningObject(), xydataset,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);
		XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();

		int last = xydataset != null ? xydataset.getSeriesCount() : 1;
		xylineandshaperenderer.setSeriesPaint(last - 1, Color.black);
		xylineandshaperenderer
				.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		xylineandshaperenderer.setDefaultEntityRadius(6);
		 xylineandshaperenderer.setShapesVisible(false);
		xyplot.setRenderer(xylineandshaperenderer);
		jfreechart.getLegend().setVisible(true);
		return jfreechart;
	}

	@Override
	public boolean start() {
		return miner.start();
	}

	@Override
	public boolean stop() {
		return miner.stop();
	}

	@Override
	public void setData(DataItems data) {

	}

	@Override
	public TaskElement getTask() {
		return miner.getTask();
	}

	@Override
	public INetworkMiner getMiner() {
		return miner;
	}

	@Override
	public void displayMinerResults(MinerResults rets) {
		data = rets.getInputData();
		BorderLayout Layout = new BorderLayout();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 450, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 1.0 };

		resultMap = rets.getRetPartialPeriod().getResult();
		jfreechart = createChart(createDataset(data, resultMap));
		plot = jfreechart.getXYPlot();
		PaintDomainMarker(data, resultMap, plot);

		ChartPanel chartpanel = new ChartPanel(jfreechart);
		setLayout(Layout);
		this.add(chartpanel);
	}

	@Override
	public void displayMinerResults() {
		MinerResults rets = miner.getResults();
		displayMinerResults(rets);
	}
}
