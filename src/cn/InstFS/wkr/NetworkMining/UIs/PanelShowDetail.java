package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.UIs.SubPanelShowDataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.ChartPanelShowTs;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.SubPanelShowMinerResultsTs;

public class PanelShowDetail extends JPanel{
	SubPanelShowDataItems subPanel ;
	ChartPanelShowTs chartTs ;
	public PanelShowDetail(DataItems items) {
		this();
	}
	public PanelShowDetail(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		
		//添加subPanel
		subPanel = new SubPanelShowDataItems();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		add(subPanel, gbc_panel_1);
	}
	public void displayResults(DataItems items){
		subPanel.displayDataItems(items);
	}
}
