package cn.InstFS.wkr.NetworkMining.UIs.SimulationUIs;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JCheckBox;

public class PanelControlSimulationTime extends JPanel {
	public static PanelControlSimulationTime instance;
	private JTextField txtForcastWindowSizeInSeconds;
	private JDateTimeTextField txtCurTime;
	private JButton btnStartSimulation;
	private JButton btnPauseSimulation;
	private JButton btnDoSet;
	private JButton btnCancelSet;
	private JCheckBox chckUseSimData;
	/**
	 * Create the panel.
	 */
	public PanelControlSimulationTime() {
		instance = this;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{10, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		chckUseSimData = new JCheckBox("用测试数据");
		chckUseSimData.setToolTipText("该数据仅用于测试功能是否正常！");
		chckUseSimData.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				UtilsSimulation.instance.setUseSimulatedData(chckUseSimData.isSelected());
			}
		});
		GridBagConstraints gbc_chckUseSimData = new GridBagConstraints();
		gbc_chckUseSimData.insets = new Insets(0, 0, 0, 5);
		gbc_chckUseSimData.gridx = 0;
		gbc_chckUseSimData.gridy = 0;
		add(chckUseSimData, gbc_chckUseSimData);
		
		btnStartSimulation = new JButton("开始");
		btnStartSimulation.setToolTipText("开始仿真");
		GridBagConstraints gbc_btnStartSimulation = new GridBagConstraints();
		gbc_btnStartSimulation.insets = new Insets(0, 0, 0, 5);
		gbc_btnStartSimulation.gridx = 1;
		gbc_btnStartSimulation.gridy = 0;
		add(btnStartSimulation, gbc_btnStartSimulation);
		btnStartSimulation.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				doStartSimulation();
			}
		});
		
		btnPauseSimulation = new JButton("暂停");
		btnPauseSimulation.setToolTipText("暂停仿真");
		GridBagConstraints gbc_btnPauseSimulation = new GridBagConstraints();
		gbc_btnPauseSimulation.insets = new Insets(0, 0, 0, 5);
		gbc_btnPauseSimulation.gridx = 2;
		gbc_btnPauseSimulation.gridy = 0;
		add(btnPauseSimulation, gbc_btnPauseSimulation);
		btnPauseSimulation.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				doStopSimulation();
			}
		});
		
		JLabel lblTimeGap = new JLabel("时间间隔(s):");
		GridBagConstraints gbc_lblTimeGap = new GridBagConstraints();
		gbc_lblTimeGap.fill = GridBagConstraints.BOTH;
		gbc_lblTimeGap.insets = new Insets(0, 0, 0, 5);
		gbc_lblTimeGap.gridx = 3;
		gbc_lblTimeGap.gridy = 0;
		add(lblTimeGap, gbc_lblTimeGap);
		
		txtForcastWindowSizeInSeconds = new JTextField();
		GridBagConstraints gbc_txtForcastWindowSizeInSeconds = new GridBagConstraints();
		gbc_txtForcastWindowSizeInSeconds.fill = GridBagConstraints.BOTH;
		gbc_txtForcastWindowSizeInSeconds.insets = new Insets(0, 0, 0, 5);
		gbc_txtForcastWindowSizeInSeconds.gridx = 4;
		gbc_txtForcastWindowSizeInSeconds.gridy = 0;
		add(txtForcastWindowSizeInSeconds, gbc_txtForcastWindowSizeInSeconds);
		txtForcastWindowSizeInSeconds.setColumns(10);
		txtForcastWindowSizeInSeconds.addFocusListener(new FocusListener() {
			String txt;
			@Override
			public void focusLost(FocusEvent e) {
				if (txt.equals(txtForcastWindowSizeInSeconds.getText()))
					onSimulationSettingsFocused(false);
				else
					onSimulationSettingsFocused(true);
			}			
			@Override
			public void focusGained(FocusEvent e) {
				onSimulationSettingsFocused(true);
				txt = txtForcastWindowSizeInSeconds.getText();
			}
		});
		
		JLabel lblCurTime = new JLabel("当前时间:");
		GridBagConstraints gbc_lblCurTime = new GridBagConstraints();
		gbc_lblCurTime.fill = GridBagConstraints.BOTH;
		gbc_lblCurTime.insets = new Insets(0, 0, 0, 5);
		gbc_lblCurTime.gridx = 5;
		gbc_lblCurTime.gridy = 0;
		add(lblCurTime, gbc_lblCurTime);
		
		txtCurTime = new JDateTimeTextField(UtilsSimulation.instance.getCurTime());
		GridBagConstraints gbc_txtCurTime = new GridBagConstraints();
		gbc_txtCurTime.insets = new Insets(0, 0, 0, 5);
		gbc_txtCurTime.fill = GridBagConstraints.BOTH;
		gbc_txtCurTime.gridx = 6;
		gbc_txtCurTime.gridy = 0;
		add(txtCurTime, gbc_txtCurTime);
		txtCurTime.setColumns(10);
		txtCurTime.addFocusListener(new FocusListener() {	
			Date d;
			@Override
			public void focusLost(FocusEvent e){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dNew = null;
				try {
					dNew = sdf.parse(txtCurTime.getText());
				} catch (ParseException e1) {
					JOptionPane.showMessageDialog(txtCurTime, "时间格式错误！","时间格式错误！",
							JOptionPane.ERROR_MESSAGE);
				}
				Date curTime = UtilsSimulation.instance.getCurTime();
				if (d.equals(dNew))
					onSimulationSettingsFocused(false);
				else if (!UtilsSimulation.instance.isPaused() &&dNew.before(curTime)){
					JOptionPane.showMessageDialog(txtCurTime, "运行时，该时间只能比当前时间晚！\r\n请先暂停再进行修改！","当前时间为：" + 
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(curTime), JOptionPane.ERROR_MESSAGE);
					onSimulationSettingsFocused(false);
				}else
					onSimulationSettingsFocused(true);
			}			
			@Override
			public void focusGained(FocusEvent e) {
				onSimulationSettingsFocused(true);
				d = (Date) txtCurTime.getValue();
			}
		});
		
		btnDoSet = new JButton("设置");
		btnDoSet.setEnabled(false);
		GridBagConstraints gbc_btnDoSet = new GridBagConstraints();
		gbc_btnDoSet.insets = new Insets(0, 0, 0, 5);
		gbc_btnDoSet.gridx = 7;
		gbc_btnDoSet.gridy = 0;
		add(btnDoSet, gbc_btnDoSet);
		btnDoSet.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				doSetSimulationSettings();
				btnDoSet.setEnabled(false);
				btnCancelSet.setEnabled(false);
			}
		});
		
		btnCancelSet = new JButton("取消");
		btnCancelSet.setEnabled(false);
		GridBagConstraints gbc_btnCancelSet = new GridBagConstraints();
		gbc_btnCancelSet.gridx = 8;
		gbc_btnCancelSet.gridy = 0;
		add(btnCancelSet, gbc_btnCancelSet);
		btnCancelSet.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				btnDoSet.setEnabled(false);
				btnCancelSet.setEnabled(false);
				updateSimulationParams();
			}
		});
		updateSimulationParams();
	}
	private void onSimulationSettingsFocused(boolean isTxtModified){
		if (!txtCurTime.isEditable())
			return;
		btnDoSet.setEnabled(isTxtModified);
		btnCancelSet.setEnabled(isTxtModified);
		updateSimulationParams();
	}
	public void updateSimulationParams(){
		if (btnDoSet.isEnabled())
			return;
		txtForcastWindowSizeInSeconds.setText("" + UtilsSimulation.instance.getForcastWindowSizeInSeconds());
		txtCurTime.setValue(UtilsSimulation.instance.getCurTime());
		boolean isPaused = UtilsSimulation.instance.isPaused();
		btnStartSimulation.setEnabled(isPaused);
		btnPauseSimulation.setEnabled(!isPaused);
		txtCurTime.setEditable(isPaused);
		txtForcastWindowSizeInSeconds.setEditable(isPaused);
		chckUseSimData.setSelected(UtilsSimulation.instance.isUseSimulatedData());
	}
	public void doSetSimulationSettings(){
		String wSizeStr = txtForcastWindowSizeInSeconds.getText();
		try{
			int wSize = Integer.parseInt(wSizeStr);	
			UtilsSimulation.instance.setForcastWindowSizeInSeconds(wSize);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "必须为int型！", "时间间隔设置有误！", JOptionPane.ERROR_MESSAGE);
			txtForcastWindowSizeInSeconds.requestFocus();
			return;
		}
		Date curTime = (Date) txtCurTime.getValue();	
		UtilsSimulation.instance.setCurTime(curTime);
		UtilsSimulation.instance.saveSimulationSettings();
		UtilsSimulation.instance.setUseSimulatedData(chckUseSimData.isSelected());
	}
	public void doStartSimulation(){	
		if (btnDoSet.isEnabled()){
			int ret = JOptionPane.showConfirmDialog(this, "是否保存设置?");
			if (ret == JOptionPane.CANCEL_OPTION)
				return;
			if (ret == JOptionPane.YES_OPTION)
				doSetSimulationSettings();
		}		
		UtilsSimulation.instance.setPaused(false);
		updateSimulationParams();	
		
	}
	public void doStopSimulation(){
		UtilsSimulation.instance.setPaused(true);
		updateSimulationParams();
	}
	

}
