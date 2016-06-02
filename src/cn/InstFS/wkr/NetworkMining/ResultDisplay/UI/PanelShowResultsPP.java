package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.*;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PanelShowResultsPP extends JPanel implements IPanelShowResults {

    private INetworkMiner miner;
    private MiningMethod miningMethod;
    ChartPanelShowTs chart1;
    int count=0;

    /**
     * Create the panel.
     */
    public PanelShowResultsPP(TaskElement task) {

        setLayout(new GridLayout(0, 1, 0, 0));
        chart1 = new ChartPanelShowTs("路径挖掘", "时间", "值", null);
        add(chart1);
        miningMethod = task.getMiningMethod();
        InitMiner(task);
    }

    private void InitMiner(TaskElement task) {
        this.miner = NetworkMinerFactory.getInstance().createMiner(task);
        miner.setResultsDisplayer(this);
    }

    public void showResults() {
        // TODO
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
        MinerResults rets = miner.getResults();
        displayMinerResults(rets);

    }

    @Override
    public void displayMinerResults(MinerResults rslt) {
        if (rslt == null || rslt.getRetPath() == null)
            return;
        else if(count==0 && miningMethod.equals(MiningMethod.MiningMethods_PeriodicityMining))
        {
            JScrollPane jsp = new JScrollPane();
            JPanel jp=new JPanel();

            remove(chart1);

            HashMap<String, MinerResultsPM> resultPM = rslt.getRetPath().getRetPM();

            HashMap<String,Integer> period = new HashMap<>();
            HashMap<String,Integer> firstPeriod = new HashMap<>();
            HashMap<String,DataItems> pathDataItems = new HashMap<>();

            for (Map.Entry<String, MinerResultsPM> entry : resultPM.entrySet()) {
                String pathname = entry.getKey();
                MinerResultsPM result = entry.getValue();
                period.put(pathname, (int) result.getPeriod());
                firstPeriod.put(pathname, result.getFirstPossiblePeriod());
                pathDataItems.put(pathname,result.getDistributePeriod());
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }


            HashMap<Integer,ArrayList<String>> sortPeriod=new HashMap<>();
            int min=100000000;
            while (!period.isEmpty()) {
                ArrayList<String> periodName=new ArrayList<>();
                //找出最小周期值
                for (String key : period.keySet()) {
                    int temp = period.get(key);
                    if (temp <= min)
                        min = temp;
                }
                //将最小周期值所对应的节点名字储存起来
                for(String key:period.keySet())
                {

                    if(period.get(key)==min)
                    {
                        periodName.add(key);
                    }
                }
                //删除最小周期值
                for(int i=0;i<periodName.size();i++)
                {
                    period.remove(periodName.get(i));
                }
                sortPeriod.put(min,periodName);
                min=1000000000;
            }

            jp.setLayout(new GridLayout(0,2));
            jsp.setViewportView(jp);
            add(jsp);

            for(Integer key:sortPeriod.keySet())
            {
                int numPeriod=key;
                int numfirstPeriod=0;// TODO: 2016/5/31 多路径下最可能子周期值
                ArrayList<String> oneSeries = sortPeriod.get(key);
                ArrayList<DataItems> pathNor=new ArrayList<>();
                ArrayList<String> pathname=new ArrayList<String>();
                for(int i=0;i<oneSeries.size();i++)
                {
                    if (i%3 == 0 && i!=0) { //限制一张表中最多3条周期相同的路径
                        DataItems nor=new DataItems();
                        DataItems abnor=new DataItems();
                        JFreeChart jf = ChartPanelShowPP.createChart(pathNor,pathname, nor, abnor,numPeriod,numfirstPeriod);
                        ChartPanel chartpanel = new ChartPanel(jf);
                        jp.add(chartpanel);

                        pathNor = new ArrayList<>();
                        pathname = new ArrayList<>();
                    }
                    String tempName=oneSeries.get(i);
                    pathname.add(tempName);
                    pathNor.add(pathDataItems.get(tempName));
                    numfirstPeriod = firstPeriod.get(tempName);
                }
                DataItems nor=new DataItems();
                DataItems abnor=new DataItems();
                JFreeChart jf = ChartPanelShowPP.createChart(pathNor,pathname, nor, abnor,numPeriod,numfirstPeriod);
                ChartPanel chartpanel = new ChartPanel(jf);
                jp.add(chartpanel);
            }
            repaint();
            validate();
            count++;
        }else if (count==0 && miningMethod.equals(MiningMethod.MiningMethods_OutliesMining)) {
            JScrollPane jsp = new JScrollPane();
            JPanel jp=new JPanel();
            remove(chart1);


            HashMap<String, MinerResultsOM> resultOM = rslt.getRetPath().getRetOM();
            HashMap<String, DataItems> oriItems = rslt.getRetPath().getPathOriDataItems();
            for (Map.Entry<String, MinerResultsOM> entry: resultOM.entrySet()) {
                String pathName = entry.getKey();
                MinerResultsOM result = entry.getValue();
                DataItems outliesItems = result.getOutlies();
                DataItems oriData = oriItems.get(pathName);
                ChartPanelShowTs chart = new ChartPanelShowTs("路径"+pathName+"原始值", "时间", "值", null);
                chart.displayDataItems(oriData);
                jp.add(chart);
                JFreeChart jf;
                if (!result.isHasOutlies()) {
                    jf = ChartPanelShowAb.createChart(new DataItems(),new DataItems());
                } else if (result.isIslinkDegree()) {
                    jf = ChartPanelShowAbd.createChart(outliesItems);
                } else {
                    jf = ChartPanelShowAb.createChart(oriData, outliesItems);
                }
                ChartPanel chartpanel = new ChartPanel(jf);
                jp.add(chartpanel);

            }
            jp.setLayout(new GridLayout(0,2));
            jsp.setViewportView(jp);
            add(jsp);
            repaint();
            validate();
            count++;
        }
    }
}





