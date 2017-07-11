package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPM;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.LocalPeriodDetectionWitnDTW;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.NodeSection;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;
import org.openide.loaders.TemplateWizard.Iterator;

public class PanelShowResultsPartialCycle extends JPanel implements IPanelShowResults{
	private INetworkMiner miner;
	DecimalFormat formatter = new DecimalFormat("0.00");
//	JDesktopPane desktopPane;


	//
	DataItems data=null;
	XYSeriesCollection XYSeriesCollection=null;
	XYSeriesCollection selectionSeries=null;
	JFreeChart jfreechart;
	Map<Integer,ArrayList<NodeSection>> map;
	XYPlot plot=null;
	double precision=0,recall=0;
	//

	boolean displayed=false;

	public PanelShowResultsPartialCycle(TaskElement task){
//		this();
		InitMiner(task);
		MinerResults rets = miner.getResults();
		map=rets.getRetPartialCycle().getPartialCyclePos();
		/*if(miner.getTask().getTaskRange().equals(TaskRange.SingleNodeRange))
			evaluate();*/
	}
	public static void main(String args[])
	{
		PanelShowResultsPartialCycle p = new PanelShowResultsPartialCycle(null);
		p.evaluate();
	}
	private void InitMiner(TaskElement task){
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
		JPanel jp1 = new JPanel();
		//jp1.setPreferredSize(new Dimension(500,100));
		JLabel lblGranularity = new JLabel();
		JLabel lblStartTime = new JLabel();
		int granularity = task.getGranularity();
		lblGranularity.setText("时间粒度：" + granularity + "(s)");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时");
		lblStartTime.setText("起始时间:"+ sdf.format(miner.getResults().getInputData().getTime().get(0)) +"    ");
		jp1.add(lblStartTime);
		jp1.add(lblGranularity);
		this.setLayout(new BorderLayout());
		this.add(jp1,BorderLayout.NORTH);
	}
	/**
	 * Create the panel.
	 */
//	private PanelShowResultsPartialCycle() {
//		BorderLayout Layout=new BorderLayout();
//		GridBagLayout gridBagLayout = new GridBagLayout();
//		gridBagLayout.columnWidths = new int[]{450, 0};
////		gridBagLayout.rowHeights = new int[] {0, 1, 0};
//		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
//		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
///*		Button b=new Button();
//		b.setLabel("a Buttom");
//		this.add(b);*/
//
//		//初始化控制器
//		//LocalPeriodDetectionWitnDTW dtw=new LocalPeriodDetectionWitnDTW(data,0.9,0.75,3);
//		// map=dtw.getResult().getPartialCyclePos();
//		int modelnum=map.keySet().size();
//		 jfreechart = createChart(createDataset(data,map));
//
//		plot=jfreechart.getXYPlot();
//		//plot.getRenderer().setBaseShape(null);
//
//		//initModel();
//		PaintPeriodLine();
//		PaintDomainMarker();
//
//
//		ChartPanel chartpanel = new ChartPanel(jfreechart);
//		//1
///*		JPanel controlPanel=new JPanel();
//		controlPanel.setLayout(new FlowLayout());*/
//
//
//		setLayout(Layout);
//				this.add(chartpanel);
//				//2
//	//	this.add(controlPanel,BorderLayout.SOUTH);
//		Object[] s = map.keySet().toArray();
//
//		for(int i=0;i<modelnum;i++){
//			ArrayList<NodeSection> list=map.get(s[i]);
//			java.util.Iterator<NodeSection> it=list.iterator();
//			String str = new String();
//			while(it.hasNext()){
//				NodeSection section=it.next();
//				str=str+"("+section.begin+","+section.end+")";
//			}
//			//3
///*			JLabel jlabel=new JLabel("周期："+s[i]+" 区间："+str+".");
//			controlPanel.add(jlabel);*/
//		}
//
///*				//读取模式生成int[]
//		Object[] s = map.keySet().toArray();
//		int[] model=new int[modelnum];
//		for (int i = 0; i < modelnum; i++)
//		{
//			model[i] = Integer.parseInt("" + s[i]);
//			final JCheckBox b=new JCheckBox("周期："+model[i]);
//			controlPanel.add(b);
//			b.addItemListener(new ItemListener(){
//
//				@Override
//				public void itemStateChanged(ItemEvent e) {
//					// TODO Auto-generated method stub
//					if(b.isSelected()){
//						List serieslist=XYSeriesCollection.getSeries();
//						java.util.Iterator it2=serieslist.iterator();
//						while(it2.hasNext()){
//							XYSeries xyseries=(XYSeries) it2.next();
//							if(xyseries.equals(b.getName().split(":")[1])){
//								selectionSeries.addSeries(xyseries);
//							}
//						}
//						//selectionSeries
//
//					}else{
//						XYSeries xyseries=selectionSeries.getSeries( b.getName().split(":")[1] );
//						selectionSeries.removeSeries(xyseries);
//					}
//				}
//
//			});
//
//			//System.out.println("model:" + Integer.parseInt("" + s[i])+" count"+modelcount[i]);
//		}*/
//
//	}

	private void PaintPeriodLine() {
		// TODO Auto-generated method stub
		//selectionSeries=new XYSeriesCollection();
		XYLineAndShapeRenderer xylineandshaperenderer=(XYLineAndShapeRenderer)plot.getRenderer();
		xylineandshaperenderer.setBaseShapesVisible(false);
		xylineandshaperenderer.setBaseShapesFilled(true);
		xylineandshaperenderer.setDrawOutlines(true);
		Object[] key=map.keySet().toArray();
		for(int i=0;i<map.size();i++){
/*				xylineandshaperenderer.setSeriesLinesVisible(i, true);
				xylineandshaperenderer.setSeriesShapesVisible(i, false);*/
			ArrayList<NodeSection> list=map.get(key[i]);
			java.util.Iterator<NodeSection> it=list.iterator();
			while(it.hasNext()){
				NodeSection nodesection=it.next();
				int k=(nodesection.begin+nodesection.end)/2;
				String str="("+nodesection.begin+","+nodesection.end+")";
				XYPointerAnnotation xypointerannotation1 = new XYPointerAnnotation(("周期："+key[i]+" 区间："+str), k, Double.parseDouble(data.getElementAt(k).getData()), -3.14/2 );
				plot.addAnnotation(xypointerannotation1);

			}


		}


		//XYLineAndShapeRenderer xylineandshaperenderer=(XYLineAndShapeRenderer)plot.getRenderer();
		//xylineandshaperenderer.setShapesVisible()



	}


/*	public void initModel(){
		 XYSeriesCollection=(XYSeriesCollection)plot.getDataset();
		 //selectionSeries=new XYSeriesCollection();
		 Object[] key=map.keySet().toArray();
		 for(int i=0;i<map.size();i++){
			 XYSeries x=new XYSeries(key[i]+"");
			 ArrayList<NodeSection> list=map.get(key[i]);
			 java.util.Iterator<NodeSection> it=list.iterator();
			 while(it.hasNext()){
				 NodeSection nodesection=it.next();

				 for(int j=nodesection.begin;j<nodesection.end;j++){
					 x.add(i,Double.parseDouble(data.getElementAt(i).getData()));
			      }
			 }
			 XYSeriesCollection.addSeries(x);


		 }
		 System.out.println("&&&&"+plot.getDatasetCount());
		 //plot.setDataset(plot.getDatasetCount(),XYSeriesCollection);


	}*/

	/**
	 * 画标记线分割周期
	 */
	private void PaintDomainMarker(){
		Object[] key=map.keySet().toArray();
		for(int i=0;i<map.size();i++){
			ArrayList<NodeSection> list=map.get(key[i]);
			java.util.Iterator<NodeSection> it=list.iterator();
			int b;
			int e=0;
			while(it.hasNext()){
				NodeSection nodesection=it.next();
				b=nodesection.begin;
				e=nodesection.end;
				int p=(int)key[i];
				for(int j=b;j<=e;j+=p){
					ValueMarker valuemarker3 = new ValueMarker(j);
					valuemarker3.setPaint(new Color(100,100,100,250));
					valuemarker3.setStroke(  new   BasicStroke(1,   BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_BEVEL,   0,
							new   float[]{10,   4},   0)  );
					//valuemarker3.setLabel("Close Date (02:15)");
					//valuemarker3.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
					//valuemarker3.setLabelTextAnchor(TextAnchor.TOP_LEFT);
					plot.addDomainMarker(valuemarker3);
				}


			}
		}
	}


	private static XYDataset createDataset(DataItems data,Map<Integer,ArrayList<NodeSection>> map){
		XYSeries xyseries = new XYSeries("原序列");
		XYSeriesCollection XYSeriesCollection=new XYSeriesCollection();





		// 获取正常数据的长度、
		int length = data.getLength();
		int time[] = new int[length];



		Object[] key=map.keySet().toArray();
		int num=0;
		for(int i=0;i<map.size();i++){

			ArrayList<NodeSection> list=map.get(key[i]);
			java.util.Iterator<NodeSection> it=list.iterator();
			int b;
			int e=0;
			while(it.hasNext()){
				num++;
				XYSeries x=new XYSeries("	周期"+num+"");
				NodeSection nodesection=it.next();
				/* int k=(nodesection.begin+nodesection.end)/2;
				 XYPointerAnnotation xypointerannotation1 = new XYPointerAnnotation("wqe", k, Double.parseDouble(data.getElementAt(k).getData()), 3.9269908169872414D );
				 plot.addAnnotation(xypointerannotation1);*/
//						 if((nodesection.begin-e)>1){
//							 x.add(e+1,null);
//						 }
				b=nodesection.begin;
				e=nodesection.end;
				int p=(int)key[i];


				for(int j=b;j<e;j++){
					x.add(j,Double.parseDouble(data.getElementAt(j).getData()));


				}
				XYSeriesCollection.addSeries(x);
			}
			// x.add(e+1,null);



		}

		for (int i = 0; i < length; i++) {
			DataItem temp = new DataItem();

			temp = data.getElementAt(i);

			// System.out.println("DataItem.time=" + temp.getTime().getTime());

			xyseries.add(i,Double.parseDouble(temp.getData())); // 对应的横轴

		}
		XYSeriesCollection.addSeries(xyseries);

		return   XYSeriesCollection;

	}

	private  JFreeChart createChart(XYDataset xydataset)
	{
		JFreeChart jfreechart = ChartFactory.createXYLineChart("局部周期发现", "时间",miner.getTask().getMiningObject(), xydataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);
		XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
/*		xylineandshaperenderer.setSeriesLinesVisible(0, true);
		xylineandshaperenderer.setSeriesShapesVisible(0, false);*/
/*		xylineandshaperenderer.setSeriesLinesVisible(1, true);
		xylineandshaperenderer.setSeriesShapesVisible(1, false);*/


		int last =xydataset.getSeriesCount();
		xylineandshaperenderer.setSeriesPaint(last-1, Color.black);
		xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		xylineandshaperenderer.setDefaultEntityRadius(6);
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
	int crosslen(NodeSection a,NodeSection b,int offset)
	{
		int right = Math.min(a.end+offset, b.end);
		int left = Math.max(a.begin+offset,b.begin);
		if (right>=left)
			return right-left+1;
		return 0;
	}
	public void evaluate()
	{
		try
		{

			OutputStreamWriter ow = new OutputStreamWriter(
					new FileOutputStream("testResult/局部周期测试结果.txt",true), "UTF-8");

			BufferedWriter bw = new BufferedWriter(ow);

			InputStreamReader  in= new InputStreamReader(
					new FileInputStream("局部周期标签.txt"), "UTF-8");
			BufferedReader bin = new BufferedReader(in);
			String ip = miner.getTask().getRange();
			HashMap<Integer,ArrayList<NodeSection>> labelMap = new HashMap<Integer,ArrayList<NodeSection>>();

			String thisLine = null;
			int offset = (int)((miner.getResults().getInputData().getTime().get(0).getTime()-1487865600000L)/1000/3600);
			int rightLen=0;
			int periodLen = 0;
			int detectLen = 0;
			for(Map.Entry<Integer, ArrayList<NodeSection>> entry:map.entrySet())
			{

				for(int j=0;j<entry.getValue().size();j++)

					detectLen +=entry.getValue().get(j).end-entry.getValue().get(j).begin+1;

			}
			while((thisLine = bin.readLine())!=null)
			{
				if(thisLine.contains(ip))
				{
					String seg[]= thisLine.split(" ");
					for(int i=1;i<seg.length;i++)
					{
						String seg2[]= seg[i].split(",");
						String seg3[]= seg2[0].split("-");
						int period = Integer.parseInt(seg2[1]);
//						if(!labelMap.containsKey(period))
//						{
//							ArrayList<NodeSection> list = new ArrayList<NodeSection>();
//							labelMap.put(period, list);
//						}
//						labelMap.get(period).add(new NodeSection(Integer.parseInt(seg3[0])*24,Integer.parseInt(seg3[1])*24-1));
						int left = Integer.parseInt(seg3[0])*24;
						int right = Integer.parseInt(seg3[1])*24-1;
						periodLen +=right-left+1;
						for(Map.Entry<Integer, ArrayList<NodeSection>> entry:map.entrySet())
						{
							if(entry.getKey()!=period)
								continue;
							for(int j=0;j<entry.getValue().size();j++)
								rightLen+=crosslen(entry.getValue().get(j),new NodeSection(left,right),offset);


						}

					}

				}
			}
			recall = 1.0*rightLen/periodLen;
			precision = 1.0*rightLen/detectLen;
			bw.write(ip+" ");
			bw.write("准确率: "+precision);
			bw.write(" 召回率: "+recall);
			bw.newLine();
			bw.close();

		}
		catch(Exception e)
		{

		}
//		System.out.println("god");
		for(Map.Entry<Integer, ArrayList<NodeSection>> entry:map.entrySet())
		{

//			for()
		}
	}
	@Override
	public void displayMinerResults(MinerResults rets) {
		data = rets.getInputData();
		rets.getRetPartialCycle();

//		BorderLayout Layout=new BorderLayout();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
/*		Button b=new Button();
		b.setLabel("a Buttom");
		this.add(b);*/

		//初始化控制器

		int modelnum=map.keySet().size();


		// initModel();





		jfreechart = createChart(createDataset(data,map));
		plot=jfreechart.getXYPlot();
		//plot.getRenderer().setBaseShape(null);

		//initModel();
		PaintPeriodLine();
		PaintDomainMarker();

		/*JPanel jp1 = new JPanel();
		//jp1.setPreferredSize(new Dimension(500,100));
		JLabel evaluate = new JLabel();
		evaluate.setText("准确率："+precision+"  "+"召回率： "+recall);
		jp1.add(evaluate);
//        setLayout(Layout);
		this.add(jp1,BorderLayout.SOUTH);*/

		ChartPanel chartpanel = new ChartPanel(jfreechart);
		//1
/*		JPanel controlPanel=new JPanel();
		controlPanel.setLayout(new FlowLayout());*/



		this.add(chartpanel,BorderLayout.CENTER);
		//2
		//	this.add(controlPanel,BorderLayout.SOUTH);
		Object[] s = map.keySet().toArray();

		for(int i=0;i<modelnum;i++){
			ArrayList<NodeSection> list=map.get(s[i]);
			java.util.Iterator<NodeSection> it=list.iterator();
			String str = new String();
			while(it.hasNext()){
				NodeSection section=it.next();
				str=str+"("+section.begin+","+section.end+")";
			}
			//3
/*			JLabel jlabel=new JLabel("周期："+s[i]+" 区间："+str+".");
			controlPanel.add(jlabel);*/
		}

	/*	if (rets == null)
			return;
		if(displayed==true)
			return;
		int count=0;
		for(Map.Entry<Integer,ArrayList<NodeSection>> entry:rets.getRetPartialCycle().getPartialCyclePos().entrySet())
		{
			String title ="周期长度"+entry.getKey()+"部分周期序列";
			
			ChartPanelShowPartialCycle chartCycle = new ChartPanelShowPartialCycle(title,"","",null);
			chartCycle.displayDataItems(rets.getInputData(), entry.getValue());
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = count++;
			add(chartCycle,gbc_panel);
		}
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		displayed =true;
*/
	}
	@Override
	public void displayMinerResults() {
		MinerResults rets = miner.getResults();
		displayMinerResults(rets);
	}

}