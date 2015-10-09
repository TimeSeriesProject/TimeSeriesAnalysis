package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DateFormatter;


import cn.InstFS.wkr.NetworkMining.UIs.SimulationUIs.JDateTimeTextField;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.toedter.calendar.JDateChooser;

public class JDatePropertyEditor extends AbstractPropertyEditor{
//	JDateChooser editor;
//	private Date date;
	JFormattedTextField editor;
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
//	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public JDatePropertyEditor() {
		editor = new JFormattedTextField();
		editor.setInputVerifier(new MyDateTimeVerifier());
		editor.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
//		MyDateTimeFormatter f= new MyDateTimeFormatter();
//		setFormatter(f);
//		setInputVerifier(new MyDateTimeVerifier());
		editor.setValue(new Date());	// 这步会自动寻找Formatter()，即DateFormatter
		AbstractFormatter formatter = editor.getFormatter();
		if (formatter.getClass().equals(DateFormatter.class)){
			DateFormatter df = (DateFormatter)formatter;
			df.setFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		}
		editor.setColumns(20);
		editor.setToolTipText("格式：yyyy-MM-dd HH:mm:ss，例如：2015-01-01 14:00:00");		
	}

	@Override
	public void setValue(Object value) {
		// Date oldValue = (Date)editor.getValue();
		editor.setValue(value);
		// 曾经，我觉得下面这行必须要，然而，现在我觉得可要可不要
		// firePropertyChange(oldValue, value);
	}

	@Override
	public Object getValue() {
		// return super.getValue();
		try {
			editor.commitEdit();
		} catch (ParseException e) {
			System.out.println("格式错误：" + editor.getText());
		}
		// 把毫秒设置为0
		Date d = (Date) editor.getValue();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
		
//		return editor.getValue();
	}
	
//	@Override
//	public String getAsText() {
//	    return editor.getText();
//	}
//
//	@Override
//	public void setAsText(String text) throws IllegalArgumentException {
//		editor.setText(text);
//	}

	@Override
	public String[] getTags() {
		return null;
	}

	@Override
	public Component getCustomEditor() {
		return editor;
	}

	@Override
	public boolean supportsCustomEditor() {
		return true;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}


}

class MyDateTimeVerifier extends InputVerifier{
	@Override
	public boolean verify(JComponent input) {
		JFormattedTextField txtField = (JFormattedTextField)input;
		AbstractFormatter formatter = txtField.getFormatter();
		if (formatter != null){
			String txt = txtField.getText();
			if (txt == null || txt.length() == 0)
				return true;
			try{
				formatter.stringToValue(txt);
				return true;
			}catch(Exception ee){
				return false;
			}
		}
		return true;
	}
	@Override
	public boolean shouldYieldFocus(JComponent input) {
		return verify(input);
	}
}
