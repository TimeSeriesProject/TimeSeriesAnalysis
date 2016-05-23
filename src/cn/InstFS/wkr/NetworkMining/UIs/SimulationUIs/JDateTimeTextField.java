package cn.InstFS.wkr.NetworkMining.UIs.SimulationUIs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DateFormatter;

import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;

import com.sun.jna.platform.win32.OaIdl.DATE;

public class JDateTimeTextField extends JFormattedTextField {

	public JDateTimeTextField(Date d) {
		setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
//		MyDateTimeFormatter f= new MyDateTimeFormatter();
//		setFormatter(f);
		setInputVerifier(new MyDateTimeVerifier());
		setValue(new Date());	// 这步会自动寻找Formatter()，即DateFormatter
		AbstractFormatter formatter = getFormatter();
		if (formatter.getClass().equals(DateFormatter.class)){
			DateFormatter df = (DateFormatter)formatter;
			df.setFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		}
		setColumns(20);
		setToolTipText("格式：yyyy-MM-dd HH:mm:ss，例如：2015-01-01 14:00:00");	
	}
}
class MyDateTimeFormatter extends AbstractFormatter{
	private final SimpleDateFormat sdf = new SimpleDateFormat();// = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public Object stringToValue(String text) throws ParseException {
		return sdf.parse(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value == null)
			return sdf.format(UtilsSimulation.instance.getCurTime());
		if (!value.getClass().equals(DATE.class))
			throw new ParseException("时间格式不正确！", 0);
		Date d = (Date)value;
		return sdf.format(d);
	}
}
class MyDateTimeVerifier extends InputVerifier{
	@Override
	public boolean verify(JComponent input) {
		JDateTimeTextField txtField = (JDateTimeTextField)input;
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
