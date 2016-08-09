package cn.InstFS.wkr.NetworkMining.UIs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class TSATest extends JPanel  implements IResultsDisplayer{
	public static void main(String[] args){
		UtilsUI.InitFactories();
		JFrame f = new JFrame();
		f.setContentPane(new TSATest());
		f.setBounds(100,100,800,600);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public TSATest(){
		setLayout(new GridLayout());
		JButton btnPM = new JButton("测试TSA");
		add(btnPM);
		btnPM.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				testMinerTSA();
			}
		});
	}
	
	public void testMinerTSA(){
		TaskElement task = TaskElement.TSAExample1;
		TaskElement.add1Task(task,false);	
		//TaskElement.add1Task(task, false);
		INetworkMiner miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);//设置result display
		miner.start();
	}
	
	@Override
	public void displayMinerResults(MinerResults rslts) {
		Date date = rslts.getDateProcess();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JOptionPane.showMessageDialog(this, sdf.format(date));
	}
}
