package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.SimpleBeanInfo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import com.l2fprod.common.demo.PropertySheetMain;
import com.l2fprod.common.demo.PropertySheetPage3;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.l2fprod.common.util.ResourceManager;

import ec.tstoolkit.design.IntValue;

public class DialogConfigTask extends JDialog {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DialogConfigTask frame = new DialogConfigTask(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DialogConfigTask(JFrame frame) {
		super(frame);
		setModal(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
//		contentPane.setLayout(new FlowLayout());
		setContentPane(contentPane);
		
		setTitle("事件序列配置");

		
		contentPane.add(new PanelConfigTask(), BorderLayout.CENTER);
//		contentPane.add(new PanelConfigTask());
	}
	
	
}