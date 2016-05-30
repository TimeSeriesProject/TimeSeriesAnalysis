package cn.InstFS.wkr.NetworkMining.UIs;

import javax.swing.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.Miner.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResultsFP_Line;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;

public class ChangeCompositeToSwingPanel extends Panel {
	DisplayThread displayThread;
	private Canvas canvas;
	//TaskCombination taskCombination;
	//List<ProtoclPair> protocolPairList;
	//HashMap<TaskCombination, MinerProtocolResults> resultMaps;
	MinerResultsFP_Line minerresults;
	//Composite composite=null;

	public ChangeCompositeToSwingPanel(TaskCombination taskCombination2, HashMap<TaskCombination, MinerProtocolResults> resultMaps2) {
		minerresults =resultMaps2.get(taskCombination2).getRetFP();
		displayThread = new DisplayThread();
		displayThread.start();
		canvas = new Canvas();
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		
	}

	public void addNotify() {
		super.addNotify();
		Display dis = displayThread.getDisplay();
		dis.syncExec(new Runnable() {
			public void run() {
				Shell shell = SWT_AWT.new_Shell(displayThread.getDisplay(),
						canvas);
				shell.setLayout(new FillLayout());
				// SWTChartComposite s=new SWTChartComposite(shell,SWT.NONE);
				CompositeProtocolConfidence ui = new CompositeProtocolConfidence(shell, SWT.NONE,minerresults);

			}
		});
	}

	/*public static void main(String[] args) {
		JFrame frame = new JFrame("No Title");
		ChangeCompositeToSwingPanel swingui = new ChangeCompositeToSwingPanel();
		// swingui.add(frame);
		frame.getContentPane().add(swingui);
		// ����frame�Ĵ�СΪ300x200���ҿɼ�Ĭ���ǲ��ɼ��
		frame.setSize(300, 200);
		frame.setVisible(true);
		// ʹ���ϽǵĹرհ�ť��Ч�����û����䣬������ϽǵĹرհ�ťֻ�ܹرմ��ڣ��޷�������
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}*/

	public class DisplayThread extends Thread {
		private Display display;
		Object sem = new Object();

		public void run() {
			synchronized (sem) {
				display = Display.getDefault();
				sem.notifyAll();
			}
			swtEventLoop();
		}

		private void swtEventLoop() {
			while (true) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}

		public Display getDisplay() {
			try {
				synchronized (sem) {
					while (display == null)
						sem.wait();
					return display;
				}
			} catch (Exception e) {
				return null;
			}
		}

	}

}
