package cn.InstFS.wkr.NetworkMining.UIs.Utils;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.swing.JTextField;

public class JNumberTextField extends JTextField {
	Number value;
	char []allowedChars;
	static char []allowedChars_int = new char[]{'0','1','2','3','4','5','6','7','8','9'};
	static char []allowedChars_double = new char[]{'.', '0','1','2','3','4','5','6','7','8','9'};
	int []allowedCodes = new int []{8, 127, 37, 39};	// backspace, delete, <-, ->
	Field field;	// 类的成员
	public JNumberTextField(Field field) {
		this.field = field;
		if (field.getType().equals(double.class))
			allowedChars = allowedChars_double;
		else 
			allowedChars = allowedChars_int;
		if (isInt(field))
			setText("0");
		else
			setText("0.0");
		setColumns(10);
		Arrays.sort(allowedChars);
	}
	
	@Override
	protected void processKeyEvent(KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_TYPED){
			super.processKeyEvent(e);
			return;
		}
		if (Arrays.binarySearch(allowedChars, e.getKeyChar()) > -1 || Arrays.binarySearch(allowedCodes, e.getKeyCode()) > -1)
			super.processKeyEvent(e);
		else
			return;
	}
	public Number getValue() {
		if (isInt(field))
			value = Integer.parseInt(getText());
		else 
			value = Double.parseDouble(getText());
		return value;
	}
	public void setValue(Number value) {
		this.value = value;
		setText(value.toString());
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	
	private boolean isInt(Field field){
		if (field.getType().equals(int.class))
			return true;
		else 
			return false;
	}
	
	public void setAllowedChars(char[] allowedChars) {
		this.allowedChars = allowedChars;
		Arrays.sort(this.allowedChars);
	}
}
