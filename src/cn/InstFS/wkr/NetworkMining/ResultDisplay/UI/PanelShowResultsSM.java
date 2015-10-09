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

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridLayout;

public class PanelShowResultsSM extends JPanel implements IPanelShowResults{
	

	private INetworkMiner miner;
	
	private TablePanelShowDataItems tblPatterns;
	private TablePanelShowDataItems tblForcasts_curTime;
	private TablePanelShowDataItems tblForcasts_futureTime;
	private TablePanelShowDataItems tblCurData;
	private TablePanelShowPrecisionRecall tblShowAccuracy;
	
	/**
	 * Create the panel.
	 */
	public PanelShowResultsSM(TaskElement task) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{75, 75, 75, 75, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.4, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		tblCurData = new TablePanelShowDataItems("当前值");
		GridBagConstraints gbc_tblCurData = new GridBagConstraints();
		gbc_tblCurData.fill = GridBagConstraints.BOTH;
		gbc_tblCurData.insets = new Insets(0, 0, 5, 5);
		gbc_tblCurData.gridx = 0;
		gbc_tblCurData.gridy = 0;
		add(tblCurData, gbc_tblCurData);
		
		tblShowAccuracy = new TablePanelShowPrecisionRecall("预测结果");
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 4;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		add(tblShowAccuracy, gbc_panel);
		
		tblForcasts_curTime = new TablePanelShowDataItems("当前预测值");
		GridBagConstraints gbc_tblForcasts_curTime = new GridBagConstraints();
		gbc_tblForcasts_curTime.fill = GridBagConstraints.BOTH;
		gbc_tblForcasts_curTime.insets = new Insets(0, 0, 5, 5);
		gbc_tblForcasts_curTime.gridx = 0;
		gbc_tblForcasts_curTime.gridy = 1;
		add(tblForcasts_curTime, gbc_tblForcasts_curTime);
		tblForcasts_futureTime = new TablePanelShowDataItems("未来预测值");
		GridBagConstraints gbc_tblForcasts_futureTime = new GridBagConstraints();
		gbc_tblForcasts_futureTime.fill = GridBagConstraints.BOTH;
		gbc_tblForcasts_futureTime.insets = new Insets(0, 0, 5, 5);
		gbc_tblForcasts_futureTime.gridx = 0;
		gbc_tblForcasts_futureTime.gridy = 2;
		add(tblForcasts_futureTime, gbc_tblForcasts_futureTime);
		tblPatterns = new TablePanelShowDataItems("序列模式");
		GridBagConstraints gbc_tblPatterns = new GridBagConstraints();
		gbc_tblPatterns.insets = new Insets(0, 0, 0, 5);
		gbc_tblPatterns.fill = GridBagConstraints.BOTH;
		gbc_tblPatterns.gridx = 0;
		gbc_tblPatterns.gridy = 3;
		add(tblPatterns, gbc_tblPatterns);
		
		InitMiner(task);
	}
	
	private void InitMiner(TaskElement task){
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
		if (rslt != null) {
			tblCurData.showDataItems(rslt.getRetSM().getData_curTime());
			tblCurData.hideColumnProb();
			tblForcasts_curTime.showDataItems(rslt.getRetSM().getForcasts_curTime());
			tblForcasts_curTime.hideColumnTime();
			tblForcasts_futureTime.showDataItems(rslt.getRetSM().getForcasts_futureTime());
			tblForcasts_futureTime.hideColumnTime();
			tblPatterns.showDataItems(rslt.getRetSM().getPatterns());
			tblPatterns.hideColumnTime();	
			
			tblShowAccuracy.showMinerResultsPrecisionAndRecall(rslt);
		}else{
			tblCurData.showDataItems(new DataItems());
			tblCurData.hideColumnProb();
			tblForcasts_curTime.showDataItems(new DataItems());
			tblForcasts_curTime.hideColumnTime();
			tblForcasts_futureTime.showDataItems(new DataItems());
			tblForcasts_futureTime.hideColumnTime();
			tblPatterns.showDataItems(new DataItems());
			tblPatterns.hideColumnTime();	
			
//			tblShowAccuracy.showMinerResultsPrecisionAndRecall(rslt);
		}
		
	}
}




