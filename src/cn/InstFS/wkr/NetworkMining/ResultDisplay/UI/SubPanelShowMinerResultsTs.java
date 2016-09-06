package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 * 该面板用于显示多类挖掘结果的通用性信息，如离散化节点、时间粒度、原始序列等
 * @author wangshen
 *
 */
public class SubPanelShowMinerResultsTs extends JPanel {
	MinerResults rslts;

	JLabel lblGranularity;
	JTextArea txtDiscreteNodes;
	ChartPanelShowTs chartTs;
	public String xName;
	public String yName;
	public String ObName;
//	private JCheckBox chckShowOrigDataItems;
	
	DecimalFormat decimalFormat = new DecimalFormat("0.00");
	/**
	 * Create the panel.
	 */
	public SubPanelShowMinerResultsTs(String obName) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblGranularity = new JLabel("时间粒度：");
		GridBagConstraints gbc_lblGranularity = new GridBagConstraints();
		gbc_lblGranularity.anchor = GridBagConstraints.WEST;
		gbc_lblGranularity.gridwidth = 2;
		gbc_lblGranularity.insets = new Insets(0, 0, 0, 5);
		gbc_lblGranularity.gridx = 0;
		gbc_lblGranularity.gridy = 0;
		add(lblGranularity, gbc_lblGranularity);//添加时间粒度标签
		
//		chckShowOrigDataItems = new JCheckBox("显示原始数据（慢）");
		//GridBagConstraints gbc_chckShowOrigDataItems = new GridBagConstraints();
		//gbc_chckShowOrigDataItems.anchor = GridBagConstraints.WEST;
		//gbc_chckShowOrigDataItems.insets = new Insets(0, 0, 0, 5);
		//gbc_chckShowOrigDataItems.gridx = 1;
		//gbc_chckShowOrigDataItems.gridy = 1;
//		add(chckShowOrigDataItems, gbc_chckShowOrigDataItems);
//		chckShowOrigDataItems.addActionListener(new ActionListener() {			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				refreshInputData();
//			}
//		});
		
		chartTs = new ChartPanelShowTs("实际值","序列编号",obName,null);//横纵坐标和标题
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 2;
		add(chartTs, gbc_panel);//添加横纵坐标描述
		
		txtDiscreteNodes = new JTextArea("离散化\r\n区间与值：\r\n");
		txtDiscreteNodes.setEditable(false);
		GridBagConstraints gbc_txtDiscreteNodes = new GridBagConstraints();
		gbc_txtDiscreteNodes.gridheight = 3;
		gbc_txtDiscreteNodes.insets = new Insets(0, 0, 0, 5);
		gbc_txtDiscreteNodes.fill = GridBagConstraints.BOTH;
		gbc_txtDiscreteNodes.gridx = 0;
		gbc_txtDiscreteNodes.gridy = 1;
		add(txtDiscreteNodes, gbc_txtDiscreteNodes);//添加文字描述	
	}
	public SubPanelShowMinerResultsTs() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		lblGranularity = new JLabel("时间粒度：");
		GridBagConstraints gbc_lblGranularity = new GridBagConstraints();
		gbc_lblGranularity.anchor = GridBagConstraints.WEST;
		gbc_lblGranularity.gridwidth = 2;
		gbc_lblGranularity.insets = new Insets(0, 0, 0, 5);
		gbc_lblGranularity.gridx = 0;
		gbc_lblGranularity.gridy = 0;
		add(lblGranularity, gbc_lblGranularity);//添加时间粒度标签

//		chckShowOrigDataItems = new JCheckBox("显示原始数据（慢）");
		//GridBagConstraints gbc_chckShowOrigDataItems = new GridBagConstraints();
		//gbc_chckShowOrigDataItems.anchor = GridBagConstraints.WEST;
		//gbc_chckShowOrigDataItems.insets = new Insets(0, 0, 0, 5);
		//gbc_chckShowOrigDataItems.gridx = 1;
		//gbc_chckShowOrigDataItems.gridy = 1;
//		add(chckShowOrigDataItems, gbc_chckShowOrigDataItems);
//		chckShowOrigDataItems.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				refreshInputData();
//			}
//		});

		chartTs = new ChartPanelShowTs("实际值","序列编号","值",null);//横纵坐标和标题
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 2;
		add(chartTs, gbc_panel);//添加横纵坐标描述

		txtDiscreteNodes = new JTextArea("离散化\r\n区间与值：\r\n");
		txtDiscreteNodes.setEditable(false);
		GridBagConstraints gbc_txtDiscreteNodes = new GridBagConstraints();
		gbc_txtDiscreteNodes.gridheight = 3;
		gbc_txtDiscreteNodes.insets = new Insets(0, 0, 0, 5);
		gbc_txtDiscreteNodes.fill = GridBagConstraints.BOTH;
		gbc_txtDiscreteNodes.gridx = 0;
		gbc_txtDiscreteNodes.gridy = 1;
		add(txtDiscreteNodes, gbc_txtDiscreteNodes);//添加文字描述
	}
	public void displayMinerResults(MinerResults rets){
		this.rslts = rets;
		TaskElement task = rets.getMiner().getTask();
		int granularity = task.getGranularity();
		lblGranularity.setText("时间粒度：" + granularity + "(s)");
		
		DataItems di = null;		
//		if (chckShowOrigDataItems.isSelected()){
//			DataInputUtils diu = new DataInputUtils(task);
//			di = diu.readInput();
//		}else
			di = rets.getInputData();		
		if (di == null)
			di = new DataItems();
		
//		if (di.isDiscretized()){
//			StringBuilder sb = new StringBuilder();
//			Double []nodes = di.getDiscreteNodes();
//			if (nodes != null){
//				int len = nodes.length;
//				sb.append("离散化\r\n区间与值：\r\n");
//				sb.append("<" + formatStrOrNumber(nodes[1]) + ":\t0\r\n");
//				for (int i = 1; i < len - 1; i ++){
//					sb.append("" + formatStrOrNumber(nodes[i]) + " ~ " + formatStrOrNumber(nodes[i+1]) + ":\t" + i + "\r\n");
//				}
//				sb.append(">" + formatStrOrNumber(nodes[len - 1]) + ":\t" + (len-1) + "\r\n");
//				txtDiscreteNodes.setText(sb.toString());
//			}else{
//				Map<String, String> mapStrs = di.getDiscreteStrings();
//				sb.append("离散化\r\n区间与值：\r\n");
//				for(Entry<String, String>mapStr : mapStrs.entrySet()){
//					sb.append(mapStr.getKey() + ":\t" + mapStr.getValue() +"\r\n");
//				}
//				txtDiscreteNodes.setText(sb.toString());
//			}
//			txtDiscreteNodes.setVisible(true);
//		}else
		txtDiscreteNodes.setVisible(false);
		if(rets.getRetPM().getHasPeriod()){
			chartTs.displayPeriod(di,(int)rets.getRetPM().getPeriod(), rets.getRetPM().getFirstPossiblePeriod());
		}
		else{
			chartTs.displayDataItems(di);
		}
		
	}
	
	private String formatStrOrNumber(Double str){
		return decimalFormat.format(str);
	}
	private void refreshInputData(){
		if (rslts == null)
			return;
		DataItems di = null;
		TaskElement task = rslts.getMiner().getTask();
//		if (chckShowOrigDataItems.isSelected()){
//			DataInputUtils diu = new DataInputUtils(task);
//			di = diu.readInput(false, false);
//		}else
			di = rslts.getInputData();	
		chartTs.displayDataItems(di);
	}

}
