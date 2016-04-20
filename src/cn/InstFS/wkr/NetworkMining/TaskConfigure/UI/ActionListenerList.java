package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JTextField;



class SetPath implements ActionListener{
	
	String path = "";
	Frame frame = null;
	JTextField txt = null;
	public SetPath(String path,Frame frame,JTextField txt)
	{
		this.path = path;
		this.frame = frame;
		this.txt = txt;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setAcceptAllFileFilterUsed(false);
		if (jfc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			// 解释下这里,弹出个对话框,可以选择要上传的文件,如果选择了,就把选择的文件的绝对路径打印出来,有了绝对路径,通过JTextField的settext就能设置进去了,那个我没写
			path = jfc.getSelectedFile().getAbsolutePath();
			txt.setText(path);
			System.out.println("path:"+path);
//				System.out.println(jfc.getSelectedFile().getAbsolutePath());
		}
	}
}
class ParsePecap implements ActionListener{
	
	String path = "";
	Frame frame = null;
	JTextField txt = null;
	public ParsePecap(String path,Frame frame,JTextField txt)
	{
		this.path = path;
		this.frame = frame;
		this.txt = txt;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			// 解释下这里,弹出个对话框,可以选择要上传的文件,如果选择了,就把选择的文件的绝对路径打印出来,有了绝对路径,通过JTextField的settext就能设置进去了,那个我没写
			path = jfc.getSelectedFile().getAbsolutePath();
			txt.setText(path);
//				System.out.println(jfc.getSelectedFile().getAbsolutePath());
		}
	}
}

public class ActionListenerList {
}
