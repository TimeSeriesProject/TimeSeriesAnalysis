package cn.InstFS.wkr.NetworkMining.tests;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

public class testJDateChooser extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					testJDateChooser frame = new testJDateChooser();
					frame.setVisible(true);
					
					
					
//					JFrame localJFrame = new JFrame("JCalendar");
//				    JCalendar localJCalendar = new JCalendar();
//				    localJFrame.getContentPane().add(localJCalendar);
//				    localJFrame.pack();
//				    localJFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public testJDateChooser() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JDateChooser chooser = new JDateChooser(new Date());
		chooser.setDateFormatString("yy-MM-dd HH:mm:ss");
		contentPane.add(chooser);
	}

}
