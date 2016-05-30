package cn.InstFS.wkr.NetworkMining.UIs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabFolderListener;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public  class CTabFolderChart extends CTabFolder {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	Display display = Display.getCurrent();
	Composite form = null;
	int i;
	Map<CTabItem,Integer> map=null;

	public CTabFolderChart(Composite form, int style) {

		super(form, style);
		this.form = form;
		map=new HashMap<CTabItem,Integer>();

		// �����Զ���ѡ�����
		// final CTabFolder folder = new CTabFolder(form, SWT.BORDER);
		// ����ѡ��Ĳ��֣�ͨ��ֵ����ó��ֳ���󻯺���С�������
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		// ���ø��ӵ�ѡ���Ҳ���Ǵ���Բ�ǵ�ѡ���ǩ
		this.setSimple(false);
		// ����δѡ�б�ǩ��ͼ��͹رհ�ť��״̬
		this.setUnselectedImageVisible(true);
		this.setUnselectedCloseVisible(true);
		// ����ǰ��ɫ�ͱ���ɫ
		this.setSelectionForeground(display.getSystemColor(SWT.COLOR_WHITE));
		this.setSelectionBackground(display
				.getSystemColor(SWT.COLOR_DARK_GREEN));
		// ��ʾ��󻯺���С����ť
		this.setMinimizeVisible(true);
		this.setMaximizeVisible(true);
		
		
		
		
		// ������һ��ѡ���ǩ����
		//createTabItem("Tab1",0);
		// item.setData(mainpage);
	}

	


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public  void createTabItem(String tabItemName,int index) {

			CTabItem mainTabItem = new CTabItem(this, SWT.CLOSE);
			map.put(mainTabItem, index);
			//���ü���Tabʵ��һ��TabItem�رպ�MainUI�Ŀɴ�ʹ�ܱ�־tableIndex��Ϊ0
			mainTabItem.addDisposeListener(new DisposeListener(){

				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					// TODO Auto-generated method stub
					
					//System.out.println("index="+map.get(arg0.widget));
					CompositeProtocolConfidence.tableIndex[map.get(arg0.widget)]=0;
					map.remove(arg0.widget);
				}
				
			});
			mainTabItem.setText(tabItemName);
			this.setSelection(mainTabItem);
			// �����tab ��ʾ�Ľ���
			CompositeSWTChart tab1item = new CompositeSWTChart(this, SWT.NONE);
			// MainPage mainPage = new MainPage(this, SWT.NONE);
			mainTabItem.setControl(tab1item);
			// �������
	}
	
	

}
