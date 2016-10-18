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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.loaders.TemplateWizard.Iterator;

public class PanelShowResultsPartialCycle extends JPanel implements IPanelShowResults{
	private INetworkMiner miner;
	DecimalFormat formatter = new DecimalFormat("0.00");
//	JDesktopPane desktopPane;
	
	
	//
	DataItems data=null;
	XYSeriesCollection XYSeriesCollection=null;
	XYSeriesCollection selectionSeries=null;
	Map<Integer,ArrayList<NodeSection>> map;
	XYPlot plot=null;
	//
	
	boolean displayed=false;
	
	public PanelShowResultsPartialCycle(TaskElement task){
		this();		
		InitMiner(task);
	}
	private void InitMiner(TaskElement task){
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
	}
	/**
	 * Create the panel.
	 */
	private PanelShowResultsPartialCycle() {
		BorderLayout Layout=new BorderLayout();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
/*		Button b=new Button();
		b.setLabel("a Buttom");
		this.add(b);*/
		
		
				//构造数据
		initData();
		
		//初始化控制器
		LocalPeriodDetectionWitnDTW dtw=new LocalPeriodDetectionWitnDTW(data,0.9,0.9,3);
		int modelnum=dtw.map.keySet().size();
		 map=dtw.map;
		 
		 initModel();
		


		
		
		
		JFreeChart jfreechart = createChart(createDataset(data));
		plot=jfreechart.getXYPlot();
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		JPanel controlPanel=new JPanel();
		controlPanel.setLayout(new FlowLayout());

		
		setLayout(Layout);
				this.add(chartpanel);
		this.add(controlPanel,BorderLayout.SOUTH);
		Object[] s = map.keySet().toArray();
		
		for(int i=0;i<modelnum;i++){
			ArrayList<NodeSection> list=map.get(s[i]);
			java.util.Iterator<NodeSection> it=list.iterator();
			String str = new String();
			while(it.hasNext()){
				NodeSection section=it.next();
				str=str+"("+section.begin+","+section.end+")";
			}
			JLabel jlabel=new JLabel("周期："+s[i]+" 区间："+str+".");
			controlPanel.add(jlabel);
		}
		 
/*				//读取模式生成int[]
		Object[] s = map.keySet().toArray();
		int[] model=new int[modelnum];
		for (int i = 0; i < modelnum; i++)
		{
			model[i] = Integer.parseInt("" + s[i]);
			final JCheckBox b=new JCheckBox("周期："+model[i]);
			controlPanel.add(b);
			b.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					if(b.isSelected()){
						List serieslist=XYSeriesCollection.getSeries();
						java.util.Iterator it2=serieslist.iterator();
						while(it2.hasNext()){
							XYSeries xyseries=(XYSeries) it2.next();
							if(xyseries.equals(b.getName().split(":")[1])){
								selectionSeries.addSeries(xyseries);
							}
						}
						//selectionSeries
						
					}else{
						XYSeries xyseries=selectionSeries.getSeries( b.getName().split(":")[1] );
						selectionSeries.removeSeries(xyseries);
					}
				}
				
			});
			
			//System.out.println("model:" + Integer.parseInt("" + s[i])+" count"+modelcount[i]);
		}*/
		
	}
	
	public void initData(){
		data=new DataItems();
		double[] da={ 3,4,5,6,1,2,3,4,5,5,5,5,5,5,4,4,4,3,2,1, 2,3,4,5,5,5,5,5,4,4,4,4,1,2,3, 1,7,8,9,0,33,44,55,6,7,66,8,2,3,4,5,6,7};
		for(int i=0;i<da.length;i++){
			DataItem d=new DataItem();
			d.setData(""+da[i]);
			data.add1Data(d);
		}
		
	
	}
	
	public void initModel(){
		 XYSeriesCollection=new XYSeriesCollection();
		 selectionSeries=new XYSeriesCollection();
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
	}
	
	private static XYDataset createDataset(DataItems data){
		XYSeries xyseries = new XYSeries("Series 1");
		XYSeriesCollection XYSeriesCollection=new XYSeriesCollection();
			
			
			
			
				// 获取正常数据的长度、
				int length = data.getLength();
				int time[] = new int[length];


				for (int i = 0; i < length; i++) {
					DataItem temp = new DataItem();

					temp = data.getElementAt(i);

					// System.out.println("DataItem.time=" + temp.getTime().getTime());

					xyseries.add(i,Double.parseDouble(temp.getData())); // 对应的横轴

				}

				XYSeriesCollection.addSeries(xyseries);
			
			
			
	
		
		
		return   XYSeriesCollection;
		
	}
	
	private static JFreeChart createChart(XYDataset xydataset)
	{
		JFreeChart jfreechart = ChartFactory.createXYLineChart("时间序列", "time", "data", xydataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);
		XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
		xylineandshaperenderer.setSeriesLinesVisible(0, true);
		xylineandshaperenderer.setSeriesShapesVisible(0, false);
		xylineandshaperenderer.setSeriesLinesVisible(1, false);
		xylineandshaperenderer.setSeriesShapesVisible(1, true);
		xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		xylineandshaperenderer.setDefaultEntityRadius(6);
		xyplot.setRenderer(xylineandshaperenderer);
		jfreechart.getLegend().setVisible(false);
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
		if (rets == null)
			return;
		if(displayed==true)
			return;
		int count=0;
		for(Map.Entry<Integer,ArrayList<Pair<Integer,Integer>>> entry:rets.getRetPartialCycle().getPartialCyclePos().entrySet())
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
		
		//System.out.println(rets.getRetPM().get(i).getFirst()+" "cyclePos.get(index).)	
//		List<String> data =dataItems.getData();
//		int oldSize =data.size();
//		//oldSize =3200;
////			if(data.size()==0)
////				return;
//		int newSize = (int)Math.pow(2,(int) (Math.log(oldSize)/Math.log(2)));
//		if(newSize<oldSize)
//			newSize*=2;
//		System.out.println(oldSize+" "+newSize);
//		
//		double original[] = new double[newSize];
//		for(int i=0;i<newSize;i++)
//		{
//			if(i<oldSize)
//			{
//				original[i]=Double.parseDouble(data.get(i));
//			}
//			else
//				original[i]=0;
//		}
//		 FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
//		 Complex[] result = fft.transform(original, TransformType.FORWARD);
//		 for(int i=(int)(result.length*0.2);i<result.length;i++)
//		 {
//			 result[i]=new Complex(0,0);
//			// result[i+result.length/2]=new Complex(0,0);
//		 }
//		 System.out.println("result "+result.length);
//		 Complex [] denoised = fft.transform(result, TransformType.INVERSE);
//		 List<String> newData =new ArrayList<String>();
//		 for(int i=0;i<oldSize;i++)
//		 {
//			 newData.add(String.valueOf(denoised[i].getReal()));
//		 }
//		 dataItems.setData(newData);
		
//		rets.setInputData(dataItems);
	
	}
	@Override
	public void displayMinerResults() {
		MinerResults rets = miner.getResults();
		displayMinerResults(rets);		
	}
	
}