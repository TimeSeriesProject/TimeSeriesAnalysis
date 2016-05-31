package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.*;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;
import com.sun.jna.platform.unix.X11;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.openide.nodes.Children;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridLayout;

public class PanelShowResultsPP extends JPanel implements IPanelShowResults {


    private INetworkMiner miner;

    private TablePanelShowDataItems tblPatterns;
    private TablePanelShowDataItems tblForcasts_curTime;
    private TablePanelShowDataItems tblForcasts_futureTime;
    private TablePanelShowDataItems tblCurData;
    private TablePanelShowPrecisionRecall tblShowAccuracy;
    ChartPanelShowTs chart1;
    ChartPanelShowTs chart2;
    //    ChartPanelShowFP chart2;
    int count=0;


    /**
     * Create the panel.
     */
    public PanelShowResultsPP(TaskElement task) {
//		pane.setLayout(new GridLayout(3, 3));
//        setLayout(new GridLayout(2,3));
//		chart1 = new ChartPanelShowTs("原始值", "时间", "值", null);
//		chart2 = new ChartPanelShowTs("预测值", "时间", "", null);
//
//		add(chart1);
//		add(chart2);
        setLayout(new GridLayout(0, 1, 0, 0));
        chart1 = new ChartPanelShowTs("路径挖掘", "时间", "值", null);
//        chart2 =new ChartPanelShowTs("第二种模式","时间","值",null);
        add(chart1);
//        add(chart2);
//        add(chart1);
//        add(chart1);
//        add(chart1);
//        add(chart1);
//
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
        else if(count==0)
        {

                JPanel jp=new JPanel();
//            DataItems nor = rslt.getRetFP().getOriginItems().get(0);
//            System.out.println(rslt.getRetFP().getOriginItems().size());
//            DataItems abnor=rslt.getRetFP().getOriginItems().get(1);
//            chart1.displayDataItems(nor);
//            chart2.displayDataItems(abnor);
//            add(chart1);
//            add(chart2);
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
            int layout=sortPeriod.size();
            if(layout <= 3) {
                jp.setLayout(new GridLayout(1, 3));
            }
            else {
//                setLayout(null);
                jp.setLayout(new GridLayout(2, 3));
            }
            add(jp);
            int pathcount=0;
//            Object[] sortKey =  sortPeriod.keySet().toArray();
//            Arrays.sort(sortKey);
//            HashMap<Integer,ArrayList<String>> sort_Period=new HashMap<>();
//            for(int i=0;i<sortKey.length;i++)
//            {
//                int tempkey=sortKey[i];
//            }
            for(Integer key:sortPeriod.keySet())
                {
                    int numPeriod=key;
                    int numfirstPeriod=0;
                    if(pathcount<6) {
                        ArrayList<String> oneSeries = sortPeriod.get(key);
                        ArrayList<DataItems> pathNor=new ArrayList<>();
                        ArrayList<String> pathname=new ArrayList<String>();
                        for(int i=0;i<oneSeries.size();i++)
                        {
                            String tempName=oneSeries.get(i);
                            for(String pathkey:pathDataItems.keySet())
                            {
                                if(pathkey.equals(tempName))
                                {
                                	pathname.add(pathkey);
                                    pathNor.add(pathDataItems.get(pathkey));
                                    numfirstPeriod=firstPeriod.get(pathkey);
                                }
                            }
                        }
                        pathcount++;
                        DataItems nor=new DataItems();
                        DataItems abnor=new DataItems();


                        JFreeChart jf = ChartPanelShowPP.createChart(pathNor,pathname, nor, abnor,numPeriod,numfirstPeriod);
                        ChartPanel chartpanel = new ChartPanel(jf);
//                        remove(chart2);
                        jp.add(chartpanel);
                        repaint();
                        validate();
                    }

                }

//            HashMap<String,ArrayList<DataItems>> f_model_nor= new HashMap<>();
//            HashMap<String,ArrayList<DataItems>> f_model_abnor=new HashMap<>();
//            Map<Integer, List<String>> freq = rslt.getRetFP().getAssociateRules();
//            if(freq!=null) {
//                HashMap<String, ArrayList<String>> nor_model = new HashMap<>();
//                for (Integer key : freq.keySet()) {
////				System.out.println(key);
//                    String skey = key.toString();
//                    ArrayList<String> astring = new ArrayList<>();
//                    for (String s : freq.get(key)) {
////					System.out.println(s);
//                        astring.add(s);
//                    }
//                    nor_model.put(skey, astring);

//                int ccount = 0;
//                for (String key : nor_model.keySet()) {
////                ChartPanelShowTs chart3;
////                ChartPanelShowFP chart4;
//                    if (ccount < 6) {
//                        ArrayList<DataItems> nor_data = new <DataItems>ArrayList();
//                        ArrayList<DataItems> abnor_data = new <DataItems>ArrayList();
//                        ArrayList<String> model_line = nor_model.get(key);
//                        for (int j = 0; j < model_line.size(); j++) {
//                            String temp = model_line.get(j);
//                            String[] temp_processData = temp.split(",");
//                            int first = 0;
//                            int last = 0;
////                    System.out.println(first);
////                    System.out.println(last);
//                            DataItems nor_line = new DataItems();
//                            DataItems abnor_line = new DataItems();
//                            if (temp_processData[0] != null) {
//                                String firstString = temp_processData[0];
//                                first = Integer.parseInt(firstString);
//                            }
//                            if (temp_processData.length > 1) {
//                                String endString = temp_processData[1];
//                                last = Integer.parseInt(endString);
//                            }
//                            for (int k = first; k <= last; k++) {
//                                DataItem dataItemAbnor = new DataItem();
////                                if (k == nor.getLength())
////                                    break;
////                                DataItem tempItem = new DataItem();
//////                        System.out.println(nor.getElementAt(k).getTime());
////                                tempItem.setTime(nor.getElementAt(k).getTime());
//////                        System.out.println(nor.getElementAt(k).getData());
////                                dataItemAbnor.setData(abnor.getElementAt(k).getData());
////                                dataItemAbnor.setTime(abnor.getElementAt(k).getTime());
////                                tempItem.setData(nor.getElementAt(k).getData());
//
//
////                                nor_line.add1Data(tempItem);
//                                abnor_line.add1Data(dataItemAbnor);
//                            }
//                            nor_data.add(nor_line);
//                            abnor_data.add(abnor_line);
//
//
//                        }
//                        DataItems nor=new DataItems();
//                        DataItems abnor=new DataItems();
//                        JFreeChart jf = ChartPanelShowFP.createChart(nor_data, abnor_data, nor, abnor);
//                        ChartPanel chartpanel = new ChartPanel(jf);
//                        remove(chart1);
////                        remove(chart2);
//                        add(chartpanel);
//                        repaint();
//                        validate();
//                        f_model_abnor.put(key, abnor_data);
//                        abnor_data.clear();
//                        nor_data.clear();
//                        ccount++;
//                    } else
//                        break;
//                }
//                count++;
//
//            }

//            JFreeChart jf=ChartPanelShowFI.createChart(f_model_nor,nor);
//            ChartPanel chartpanel = new ChartPanel(jf);
//            remove(chart1);
//            add(chartpanel);
//            repaint();
//            validate();

//
//		}
            count++;
        }
    }
}





