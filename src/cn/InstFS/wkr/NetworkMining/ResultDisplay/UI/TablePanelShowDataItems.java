package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import javax.swing.table.TableModel;

public class TablePanelShowDataItems extends JPanel {
	
	JLabel label;
	private JTable table_1;
	private JTable table;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public TablePanelShowDataItems(String title) {	
		setLayout(new BorderLayout());
		label = new JLabel(title);
		add(label,BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane,BorderLayout.CENTER);
		
		table = new JTable((TableModel) null);
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
	}
	public void showDataItems(DataItems datas){	
		DefaultTableModel tmodel = new DefaultTableModel();
		// Add columns
		tmodel.addColumn("时间");
		tmodel.addColumn("值");
		tmodel.addColumn("概率");
		table.setCellSelectionEnabled(false);
//		table.getColumn("时间").setCellRenderer(new MyTableDateCellRenderer());		
//		table.getColumn("时间").setCellEditor(new DateEditor(new JTextField()));
		// Add rows
		if (datas == null || datas.getLength() == 0){
			table.setModel(tmodel);
			return;
		}
		int len = datas.getLength();
		for (int i = 0; i < len; i ++)
			tmodel.addRow(new Object[]{date2String(datas.getTime().get(i)), datas.getData().get(i), datas.getProb().get(i)});
		table.setModel(tmodel);
	}
	private String date2String(Date date){
		if (date == null)
			return "";
		else
			return 	sdf.format(date);
	}
	public void hideColumnTime(){		
		hideColumn("时间");
	}
	public void hideColumnData(){
		hideColumn("值");
	}
	public void hideColumnProb(){
		hideColumn("概率");
	}
	private void hideColumn(String colName){
		try{
		TableColumn col = table.getColumn(colName);
		table.getColumnModel().removeColumn(col);
		}catch(IllegalArgumentException ee){
			
		}
	}
}
//class MyTableDateCellRenderer extends DefaultTableCellRenderer{
//	@Override
//	public Component getTableCellRendererComponent(JTable table, Object value,
//			boolean isSelected, boolean hasFocus, int row, int column) {
//		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
//				row, column);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		setText(sdf.format((Date)value));
//		return this;
//	}
//}
//
//class DateEditor extends DefaultCellEditor{
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	JTextField txtField;
//	Date date;
//	public DateEditor(JTextField textField) {
//		super(textField);
//	}
//	
//	@Override
//	public Object getCellEditorValue() {
//		if (txtField == null)
//			return super.getCellEditorValue();
//		else
//			try {
//				return sdf.parse(txtField.getText());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		return date;
//	}
//	@Override
//	public Component getTableCellEditorComponent(JTable table, Object value,
//			boolean isSelected, int row, int column) {
//		if (value instanceof Date){
//			date = (Date)value;
//			txtField = new JTextField(sdf.format(date));
//			return txtField;
//		}else{
//			txtField = null;
//			date = null;
//			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
//		}
//	}
//	@Override
//	public boolean isCellEditable(EventObject anEvent) {
//		return false;
//	}	
//}