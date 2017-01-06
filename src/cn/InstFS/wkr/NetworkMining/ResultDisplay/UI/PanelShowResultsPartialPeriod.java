package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.NodeSection;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerPM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
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
import java.util.Map;

/**
 * @// TODO: 17-1-3 chen 部分周期显示结果,具体需要修改
 */
public class PanelShowResultsPartialPeriod extends JPanel implements IPanelShowResults {

    private INetworkMiner miner;
    DecimalFormat formatter = new DecimalFormat("0.00");
//	JDesktopPane desktopPane;


    //
    DataItems data=null;
    org.jfree.data.xy.XYSeriesCollection XYSeriesCollection=null;
    XYSeriesCollection selectionSeries=null;
    JFreeChart jfreechart;
    Map<Integer,ArrayList<NodeSection>> map;
    XYPlot plot=null;
    //

    boolean displayed=false;

    public PanelShowResultsPartialPeriod(TaskElement task){
//		this();
        InitMiner(task);
    }
    private void InitMiner(TaskElement task){
        this.miner = NetworkMinerFactory.getInstance().createMiner(task);
        miner.setResultsDisplayer(this);
    }


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
    }




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

                    plot.addDomainMarker(valuemarker3);
                }


            }
        }
    }


    private static XYDataset createDataset(DataItems data, Map<Integer,ArrayList<NodeSection>> map){
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
    @Override
    public void displayMinerResults(MinerResults rets) {
        data = rets.getInputData();
        rets.getRetPartialCycle();

        BorderLayout Layout=new BorderLayout();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
/*		Button b=new Button();
		b.setLabel("a Buttom");
		this.add(b);*/

        //初始化控制器
        map=rets.getRetPartialCycle().getPartialCyclePos();
        int modelnum=map.keySet().size();


        // initModel();






        jfreechart = createChart(createDataset(data,map));
        plot=jfreechart.getXYPlot();
        //plot.getRenderer().setBaseShape(null);

        //initModel();
        PaintPeriodLine();
        PaintDomainMarker();

        ChartPanel chartpanel = new ChartPanel(jfreechart);

        setLayout(Layout);
        this.add(chartpanel);

        Object[] s = map.keySet().toArray();

        for(int i=0;i<modelnum;i++){
            ArrayList<NodeSection> list=map.get(s[i]);
            java.util.Iterator<NodeSection> it=list.iterator();
            String str = new String();
            while(it.hasNext()){
                NodeSection section=it.next();
                str=str+"("+section.begin+","+section.end+")";
            }
        }

    }
    @Override
    public void displayMinerResults() {
        MinerResults rets = miner.getResults();
        displayMinerResults(rets);
    }
}
