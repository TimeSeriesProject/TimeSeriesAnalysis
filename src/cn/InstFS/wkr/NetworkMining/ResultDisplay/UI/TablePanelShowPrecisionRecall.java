package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;

import javax.swing.table.TableModel;

public class TablePanelShowPrecisionRecall extends JPanel {
	private Map<Date, Double[]>datas;
	
	JLabel label;
	private JTable table;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	public TablePanelShowPrecisionRecall(String title) {	
		setLayout(new BorderLayout());
		label = new JLabel(title);
		add(label,BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane,BorderLayout.CENTER);
		
		table = new JTable((TableModel) null);
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
		
		datas = new HashMap<Date, Double[]>();
	}
	public void showMinerResultsPrecisionAndRecall(MinerResults rets){	
		DefaultTableModel tmodel = new DefaultTableModel();
		// Add columns
		tmodel.addColumn("时间");
		tmodel.addColumn("召回率");
		tmodel.addColumn("准确率");
		
		table.setCellSelectionEnabled(false);
//		table.getColumn("时间").setCellRenderer(new MyTableDateCellRenderer());		
//		table.getColumn("时间").setCellEditor(new DateEditor(new JTextField()));
		// Add rows
		if (rets == null)
			return;
		Date d = rets.getDateProcess();
		Double [] recall_precision = new Double[2];
		recall_precision[0] = rets.getRetSM().getRecallRatio();
		recall_precision[1] = rets.getRetSM().getAccuracyRatio();
		// 添加至MAP结构中
		if (d != null)
			datas.put(d, recall_precision);
		// 显示到TABLE中
		insertDatas2TableModel(tmodel);
		table.setModel(tmodel);
		table.getColumn("时间").setPreferredWidth(200);
	}
	private void insertDatas2TableModel(DefaultTableModel tmodel){
		if (datas.size() == 0)
			return;
		TreeSet <Date> datesNew = new TreeSet<Date>(datas.keySet());
		// 先计算平均准确率和召回率，并加至表中
		Collection<Double[]> vals = datas.values();
		Double recall = 0.0;
		Double accuracy = 0.0;
		int size = vals.size();
		for (Double[] val : vals){
			recall += val[0];
			accuracy += val[1];
		}
		recall /= size;
		accuracy /= size;
		tmodel.addRow(new Object []{"平均("+date2String(datesNew.last())+")", recall.toString(),accuracy.toString()});
		// 再将每条记录加进表中
		
		for (Date d : datesNew){
			Double []recall_precision = datas.get(d);
			String str1 = null;
			String str2 = null;
			if (recall_precision[0].toString().length() > 4)
				str1 = recall_precision[0].toString().substring(0,4);
			else
				str1 = recall_precision[0].toString();
			if (recall_precision[1].toString().length() > 4)
				str2 = recall_precision[1].toString().substring(0,4);
			else
				str2 = recall_precision[1].toString();
			tmodel.addRow(new Object []{date2String(d), str1, str2});
		}		
	}
	public void clearData(){
		datas.clear();
	}
	private String date2String(Date date){
		if (date == null)
			return "";
		else
			return 	sdf.format(date);
	}
	private void hideColumn(String colName){
		try{
		TableColumn col = table.getColumn(colName);
		table.getColumnModel().removeColumn(col);
		}catch(IllegalArgumentException ee){
			
		}
	}
}
