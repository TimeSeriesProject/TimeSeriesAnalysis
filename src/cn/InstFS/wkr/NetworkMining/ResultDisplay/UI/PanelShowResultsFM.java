package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.Font;

import javax.swing.*;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
//import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerTSA;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerFM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.text.SimpleDateFormat;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.GridLayout;
import java.util.Timer;

public class PanelShowResultsFM extends JPanel implements IPanelShowResults {
    private Date now;    // 指示现在时间，但有可能不是真正的现在时间
    private INetworkMiner miner;
    private Timer timer;
    JDesktopPane desktopPane;
    int count = 0;
    ChartPanelShowTs chart1;
    ChartPanelShowTs chart3;
    ChartPanelShowAb chart2;
    ChartPanelShowPre chart4;
    private String obName;
    private String xName;
    private String yName;
    /**
     * Create the panel.
     */
    private PanelShowResultsFM() {
        InitChartScheme();
        setLayout(new GridLayout(0, 1, 0, 0));
        chart1 = new ChartPanelShowTs("原始值", "序列编号", obName, null);
//        chart2 = new ChartPanelShowAb("预测值", "序列编号", "", null);
        chart3= new ChartPanelShowTs("预测值", "序列编号", obName, null);
        add(chart1);
        add(chart3);

    }
    public PanelShowResultsFM(TaskElement task) {
        this();
        obName=task.getMiningObject();
        InitMiner(task);

    }

    private void InitMiner(TaskElement task) {
        this.miner = NetworkMinerFactory.getInstance().createMiner(task);
        miner.setResultsDisplayer(this);
    }



    private void InitChartScheme() {
        StandardChartTheme sct = new StandardChartTheme("");
        sct.setExtraLargeFont(new Font("微软雅黑",Font.BOLD,12));
        sct.setLargeFont(new Font("微软雅黑",Font.BOLD,12));
        sct.setRegularFont(new Font("微软雅黑",Font.BOLD,12));
        sct.setSmallFont(new Font("微软雅黑",Font.BOLD,12));
        ChartFactory.setChartTheme(sct);
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
    public void displayMinerResults() {
        if (this.isVisible()) {
//			displayOrigTimeSeries();
        }
        displayMinerResults(miner.getResults());

    }

    private void displayOrigTimeSeries() {
        DataInputUtils diu = new DataInputUtils(miner.getTask());
        DataItems di;
        if (now == null)
            now = new Date(-1000, 1, 1);
        di = diu.readInputAfter(now);

        if (di != null) {
            List<Date> timeStr = di.getTime();
            List<String> dataStr = di.getData();

            int numData = di.getData().size();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            XYDataset dataset = chart1.getChart().getXYPlot().getDataset();
            if (dataset == null) {
                dataset = new TimeSeriesCollection();
                chart1.getChart().getXYPlot().setDataset(dataset);
            }
            TimeSeriesCollection tsc = (TimeSeriesCollection) dataset;
            TimeSeries ts = null;
            if (tsc.getSeriesCount() == 0) {
                ts = new TimeSeries(miner.getTask().getMiningObject());
                tsc.addSeries(ts);
            } else
                ts = tsc.getSeries(0);
            int numNewData = 0;
            for (int i = 0; i < numData; i++) {
                Date time = null;
                time = timeStr.get(i);
                if (now != null && time.after(now)) {
                    numNewData++;
                    ts.addOrUpdate(new Second(time), Double.parseDouble(dataStr.get(i)));
                }
            }
            if (ts.getItemCount() > 0) {
                now = ts.getTimePeriod(ts.getItemCount() - 1).getEnd();
            }
            System.out.println("共显示" + ts.getItemCount() + "个数据点！(新增" + numNewData + "个)。");
//			chart1.getXYPlot().setDataset(tsc);
        }
    }

    @Override
    public void displayMinerResults(MinerResults rslts) {
        if (rslts == null || rslts.getRetFM() == null ||
                !rslts.getMiner().getClass().equals(NetworkMinerFM.class))
            return;

        if (count == 0) {
            yName=obName;

//            DataItems  = rslts.getRetOM().getOutlies();
			DataItems predictItems = rslts.getRetFM().getPredictItems();
            DataItems oriItems = rslts.getInputData();
            chart1.displayDataItems(oriItems);

			if(predictItems!=null)
			{
				
				JFreeChart jf=ChartPanelShowPre.createChart(oriItems,predictItems,yName);
				ChartPanel chartpanel = new ChartPanel(jf);
				chart1.displayDataItems(oriItems);
				remove(chart1);
				remove(chart3);
				add(chart1);
				add(chartpanel);
				count++;

			}

            repaint();
            revalidate();

        }

    }

    class ShowTSA_TimerTask extends TimerTask {
        @Override
        public void run() {

        }
    }
}