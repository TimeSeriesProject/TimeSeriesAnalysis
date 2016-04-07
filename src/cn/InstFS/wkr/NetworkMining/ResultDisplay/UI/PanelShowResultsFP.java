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
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

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

public class PanelShowResultsFP extends JPanel implements IPanelShowResults {


    private INetworkMiner miner;

    private TablePanelShowDataItems tblPatterns;
    private TablePanelShowDataItems tblForcasts_curTime;
    private TablePanelShowDataItems tblForcasts_futureTime;
    private TablePanelShowDataItems tblCurData;
    private TablePanelShowPrecisionRecall tblShowAccuracy;
    ChartPanelShowTs chart1;
    ChartPanelShowTs chart2;
    private int num=0;
//    ChartPanelShowFP chart2;


    /**
     * Create the panel.
     */
    public PanelShowResultsFP(TaskElement task) {
//		pane.setLayout(new GridLayout(3, 3));
        setLayout(new GridLayout(2,3));
//		chart1 = new ChartPanelShowTs("原始值", "时间", "值", null);
//		chart2 = new ChartPanelShowTs("预测值", "时间", "", null);
//
//		add(chart1);
//		add(chart2);
        chart1 = new ChartPanelShowTs("频繁模式", "时间", "值", null);
        chart2 =new ChartPanelShowTs("第二种模式","时间","值",null);
        add(chart1);
        add(chart2);
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
        if (rslt == null || rslt.getRetFP() == null ||
                !rslt.getMiner().getClass().equals(NetworkMinerFP.class))
            return;
        DataItems nor=null;
    	DataItems abnor=null;
    	if(num==0){
    		if(rslt.getRetFP().getOriginItems()==null||rslt.getRetFP().getOriginItems().size()==0){
        		chart1.displayDataItems(nor);
                chart2.displayDataItems(abnor);
        	}else{
        		nor = rslt.getRetFP().getOriginItems().get(0);
                abnor=rslt.getRetFP().getOriginItems().get(1);
                num++;
                chart1.displayDataItems(nor);
                chart2.displayDataItems(abnor);
        	}
            
            HashMap<String,ArrayList<DataItems>> f_model_nor= new HashMap<>();
            HashMap<String,ArrayList<DataItems>> f_model_abnor=new HashMap<>();
            Map<Integer, List<String>> freq = rslt.getRetFP().getAssociateRules();
            HashMap<String, ArrayList<String>> nor_model = new HashMap<>();
            for (Integer key : freq.keySet()) {
                String skey = key.toString();
                ArrayList<String> astring = new ArrayList<>();
                for (String s : freq.get(key)) {
                    astring.add(s);
                }
                nor_model.put(skey, astring);
            }
            //显示结果
            int ccount=0;
            for(String key:nor_model.keySet()) {
                if(ccount<6) {
                    ArrayList<DataItems> nor_data = new <DataItems>ArrayList();
                    ArrayList<DataItems> abnor_data = new <DataItems>ArrayList();
                    ArrayList<String> model_line = nor_model.get(key);
                    for (int j = 0; j < model_line.size(); j++) {
                        String temp = model_line.get(j);
                        String[] temp_processData = temp.split(",");
                        int first = 0;
                        int last = 0;
                        DataItems nor_line = new DataItems();
                        DataItems abnor_line = new DataItems();
                        if (temp_processData[0] != null) {
                            String firstString = temp_processData[0];
                            first = Integer.parseInt(firstString);
                        }
                        if (temp_processData.length > 1) {
                            String endString = temp_processData[1];
                            last = Integer.parseInt(endString);
                        }
                        for (int k = first; k <= last; k++) {
                            DataItem dataItemAbnor = new DataItem();
                            if (k == nor.getLength())
                                break;
                            DataItem tempItem = new DataItem();
                            tempItem.setTime(nor.getElementAt(k).getTime());
                            dataItemAbnor.setData(abnor.getElementAt(k).getData());
                            dataItemAbnor.setTime(abnor.getElementAt(k).getTime());
                            tempItem.setData(nor.getElementAt(k).getData());


                            nor_line.add1Data(tempItem);
                            abnor_line.add1Data(dataItemAbnor);
                        }
                        nor_data.add(nor_line);
                        abnor_data.add(abnor_line);


                    }

                    JFreeChart jf = ChartPanelShowFP.createChart(nor_data, abnor_data, nor, abnor);
                    ChartPanel chartpanel = new ChartPanel(jf);
                    remove(chart1);
                    remove(chart2);
                    add(chartpanel);
                    repaint();
                    validate();
                    f_model_abnor.put(key, abnor_data);
                    abnor_data.clear();
                    nor_data.clear();
                    ccount++;
                }
                else
                    break;
            }
    	}
    }
}





