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

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class MainProc extends JPanel  implements IResultsDisplayer{
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UtilsUI.InitFactories();
		JFrame f = new JFrame();
		f.setContentPane(new MainProc());
		f.setBounds(100,100,800,600);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
	}
	public MainProc() {
		setLayout(new GridLayout());
		JButton btnPM = new JButton("测试周期模式发现");
		add(btnPM);
		btnPM.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				testMinerPM();
			}
		});
	}
	
	public void testMinerPM(){
		TaskElement task = TaskElement.TSAExamplePM;
		TaskElement.add1Task(task, false);
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
