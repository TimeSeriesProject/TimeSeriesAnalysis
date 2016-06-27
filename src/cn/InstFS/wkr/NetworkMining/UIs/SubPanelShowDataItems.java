package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowTs;

public class SubPanelShowDataItems extends JPanel{
	ChartPanelShowTs chartTs;
	JLabel lblGranularity;
	public SubPanelShowDataItems(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblGranularity = new JLabel("时间粒度：3600s");
		GridBagConstraints gbc_lblGranularity = new GridBagConstraints();
		gbc_lblGranularity.anchor = GridBagConstraints.WEST;
		gbc_lblGranularity.gridwidth = 2;
		gbc_lblGranularity.insets = new Insets(0, 0, 0, 5);
		gbc_lblGranularity.gridx = 0;
		gbc_lblGranularity.gridy = 0;
		add(lblGranularity, gbc_lblGranularity);//添加时间粒度标签
		
		chartTs = new ChartPanelShowTs("实际值","时间","值",null);//横纵坐标和标题
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 2;
		add(chartTs, gbc_panel);//添加横纵坐标描述
	}
	public void displayDataItems(DataItems di){
		chartTs.displayDataItems(di);
	}
}
