package cn.InstFS.wkr.NetworkMining.UIs.Utils;

import java.awt.TrayIcon.MessageType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;

public class UtilsUI {
	public static boolean autoChangeResultsPanel = false;
	
	
	
	public static void InitFactories(){
		NetworkMinerFactory.getInstance();
		UtilsSimulation.instance.Init();
	}
	
	
	public static void showErrMsg(String msg){
		JOptionPane.showMessageDialog(MainFrame.topFrame, msg, "错误！", JOptionPane.ERROR_MESSAGE);
	}
	public static void appendOutput(String str){
		if (MainFrame.topFrame != null)
			MainFrame.topFrame.appendOutput(str);
	}
}
