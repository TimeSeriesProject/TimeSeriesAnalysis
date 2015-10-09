package cn.InstFS.wkr.NetworkMining.UIs.Utils;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.swing.JTextField;

public class JStringTextField extends JTextField {
	String value;
	char []allowedChars = new char[]{0,1,2,3,4,5,6,7,8,9};
	Field field;	// 类的成员
	public JStringTextField(Field field) {
		this.field = field;
		setColumns(10);
	}
	
	@Override
	protected void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);
	}
	public String getValue() {
		value = getText();
		return value;
	}
	public void setValue(String value) {
		setText(value);
		this.value = value;
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
}
