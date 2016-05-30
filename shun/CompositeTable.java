package cn.InstFS.wkr.NetworkMining.UIs;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import associationRules.ProtoclPair;



public class CompositeTable extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent、
	 * 
	 * @param style
	 */
	Label iplabel = null;
	Table table = null;
	CTabFolderChart tab = null;
	TableItem[] itemList=null;
	 int[] index=null;
	 
	 

	public CompositeTable(Composite parent, int style, String ip, List<ProtoclPair> protocolPairList, CTabFolderChart tab) {
		super(parent, style);
		this.tab = tab;
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		this.setLayout(layout);
		

		iplabel = new Label(this, SWT.NULL);
		iplabel.setText(ip);
		createTable(this, SWT.NULL, protocolPairList);
		

	}



	private void createTable(Composite composite, int style, List<ProtoclPair> protocolPairList) {
		// TODO Auto-generated method stub
		// ���ֱ��
		GridData griddata = new org.eclipse.swt.layout.GridData();
		griddata.horizontalAlignment = SWT.FILL;
		griddata.grabExcessHorizontalSpace = true;
		griddata.grabExcessVerticalSpace = true;
		griddata.verticalAlignment = SWT.FILL;

		// �������
		table = new Table(composite, SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLayoutData(griddata);
		table.setLinesVisible(true);
		//位每个表格项设置监听 ，更新tab打开标志
		table.addListener(SWT.MouseDoubleClick, new Listener(){
		
			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				//TableItem[] itemList=table.getItems();
				int itemIndex=table.getSelectionIndex();
				if(CompositeProtocolConfidence.tableIndex[itemIndex]==0){
					//问题出在这
					tab.createTabItem(itemList[itemIndex].getText(0)+"/"+itemList[itemIndex].getText(1),itemIndex);
					CompositeProtocolConfidence.tableIndex[itemIndex]=1;
				}
				
			}
			
		});
		// ������ͷ�ַ�
		final String[] tableheader = {"序号", "协议名","协议名","置信度" };
		//TableSorter tablesorter=new TableSorter();
		for (int i = 0; i < tableheader.length; i++) {
			final TableColumn c1 = new TableColumn(table, SWT.None);
			c1.setText(tableheader[i]);
			
		
		}
		
		

		// 添加数据
		for (int i = 1; i < protocolPairList.lastIndexOf(protocolPairList); i++) {
			final TableItem t = new TableItem(table, SWT.None);
			t.setText(new String[] { "协议" + protocolPairList.get(i-1).getProtocol1(), "协议" + protocolPairList.get(i-1).getProtocol2(), "" + protocolPairList.get(i-1).confidence });
			//tab.createTabItem(t.getText(0));
		}
		itemList =table.getItems();
		// 重新刷新表格
		for (int i = 0; i < tableheader.length; i++) {
			table.getColumn(i).pack();
		}
		

	}

	public static void main(String[] args) {
		// TableCoposite table=new TableComposite();
		
		
		
	}



	public int getTableIndexCount() {
		// TODO Auto-generated method stub
		return table.getItemCount();
	}


	
}
