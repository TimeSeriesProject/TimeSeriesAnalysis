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

public class SingleNodeAbnormal extends JPanel implements IResultsDisplayer{

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setContentPane(new SingleNodeAbnormal());
		f.setBounds(100,100,800,600);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	public SingleNodeAbnormal(){
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
		TaskElement task = TaskElement.TSAExample;   //完成相关的设置
		TaskElement.add1Task(task,false);  //加入到任务列表中，并对其进行监听
		INetworkMiner miner = NetworkMinerFactory.getInstance().createMiner(task);  //返回挖掘内容
		miner.setResultsDisplayer(this);//设置result display，this代表的含义
		miner.start();
	}
	@Override
	public void displayMinerResults(MinerResults rslt) {
		// TODO Auto-generated method stub
		Date date = rslt.getDateProcess();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JOptionPane.showMessageDialog(this, sdf.format(date));
	}

	

}
