package cn.InstFS.wkr.NetworkMining.UIs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsFP_Line;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;

import java.awt.*;
import java.util.HashMap;

public class ChangeCompositeToSwingPanel extends Panel {
	DisplayThread displayThread;
	private Canvas canvas;
	int CompositeCreateSymbol;
	MinerResultsFP_Line minerresults;

	public ChangeCompositeToSwingPanel(TaskCombination taskCombination2,
			HashMap<TaskCombination, MinerProtocolResults> resultMaps2) {
		minerresults = resultMaps2.get(taskCombination2).getRetFP();
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

				CompositeMainProtocolConfidence ui = new CompositeMainProtocolConfidence(
						shell, SWT.NONE, minerresults);

			}

		});
	}

	// 创建我的界面面板
	/*
	 * public void createComposite() { // TODO Auto-generated method stub
	 * this.CompositeCreateSymbol = 1; }
	 */

	public class DisplayThread extends Thread {
		private Display display;
		Object sem = new Object();
		private int exit = 1;

		public void run() {
			synchronized (sem) {
				display = Display.getDefault();
				sem.notifyAll();
			}
			swtEventLoop();
			// display.dispose();
		}

		public void exitThread() {
			exit = 0;
		}

		private void swtEventLoop() {

			if (display == null) {
				System.out.println("display is null!");
			}
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					while (exit != 0) {

						if (!display.readAndDispatch()) {
							display.sleep();
						}

					}
					display.dispose();
				}

			});

			//
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
