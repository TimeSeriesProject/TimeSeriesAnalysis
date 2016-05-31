package cn.InstFS.wkr.NetworkMining.UIs;

import javax.swing.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
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
	 static int CompositeCreateSymbol;
	MinerResultsFP_Line minerresults;

	// Composite composite=null;

	public ChangeCompositeToSwingPanel(TaskCombination taskCombination2,
			HashMap<TaskCombination, MinerProtocolResults> resultMaps2) {
		minerresults = resultMaps2.get(taskCombination2).getRetFP();
		displayThread = new DisplayThread();
		CompositeCreateSymbol=0;
		displayThread.start();
		canvas = new Canvas();
		canvas.addComponentListener(new ComponentListener(){

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				System.out.println("rtyuiyftrtuiouyteryuiouyrt");
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);

	}
	public void destroyCompositeThread(){
		System.out.println("begin to stop thread...");
		displayThread.exitThread();
		System.out.println("finish to  stop thread...");
	}

	public void addNotify() {
		super.addNotify();
		Display dis = displayThread.getDisplay();
		dis.syncExec(new Runnable() {
			public void run() {
				Shell shell = SWT_AWT.new_Shell(displayThread.getDisplay(),
						canvas);


				shell.setLayout(new FillLayout());
				if(CompositeCreateSymbol==1){
					CompositeProtocolConfidence ui = new CompositeProtocolConfidence(
						shell, SWT.NONE, minerresults);
					CompositeCreateSymbol=0;

					
				}

			}

		
		});
	}
	//创建我的界面面板
	public void createComposite() {
				// TODO Auto-generated method stub
		this.CompositeCreateSymbol=1;
			}
	/*
	 * public static void main(String[] args) { JFrame frame = new
	 * JFrame("No Title"); ChangeCompositeToSwingPanel swingui = new
	 * ChangeCompositeToSwingPanel(); // swingui.add(frame);
	 * frame.getContentPane().add(swingui); //
	 * ����frame�Ĵ�СΪ300x200���ҿɼ�Ĭ���ǲ��ɼ�� frame.setSize(300, 200);
	 * frame.setVisible(true); //
	 * ʹ���ϽǵĹرհ�ť��Ч�����û����䣬������ϽǵĹرհ�ťֻ�ܹرմ��ڣ��޷�������
	 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 * 
	 * }
	 */

	public class DisplayThread extends Thread {
		private Display display;
		Object sem = new Object();
		private int exit=1;

		public void run() {
			synchronized (sem) {
				display = Display.getDefault();
				sem.notifyAll();
			}
			swtEventLoop();
			// display.dispose();
		}
		public void exitThread(){
			exit=0;
		}

		private void swtEventLoop() {
			while (exit!=0) {
				System.out.println("swt running...");
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			display.dispose();
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
