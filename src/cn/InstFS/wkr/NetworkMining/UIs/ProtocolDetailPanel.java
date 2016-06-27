package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.CardLayout;
import javax.swing.JPanel;
import cn.InstFS.wkr.NetworkMining.UIs.PanelShowDetail;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class ProtocolDetailPanel extends JPanel{
	
	DataItems items;
	PanelShowDetail panel = new PanelShowDetail(items);
		
	public ProtocolDetailPanel(DataItems item,int row){		
		this.items = item;
		setLayout(new CardLayout());
		add(panel,Integer.toString(row));
	}
	public void displayResult(DataItems items,int row){
		CardLayout card = (CardLayout) getLayout();
		card.show(this, Integer.toString(row));
		updateUI();
		panel.displayResults(items);
	}
}