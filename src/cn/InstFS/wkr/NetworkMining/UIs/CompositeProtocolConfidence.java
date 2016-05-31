package cn.InstFS.wkr.NetworkMining.UIs;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TreeItem;
import associationRules.ProtoclPair;
import java.util.ArrayList;



import cn.InstFS.wkr.NetworkMining.Miner.MinerResultsFP_Line;

public class CompositeProtocolConfidence extends Composite {
	public static int[] tableIndex = null;
	public ArrayList<ProtoclPair> protocolPairList=null;
	String ip=null;
	public CompositeProtocolConfidence(Composite parent, int style, MinerResultsFP_Line minerresults) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		// protocolPairList=minerresults.protocolPairList;
		 ip=minerresults.getIp();
		
		// �������
/*		int[] protocol1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		int[] protocol2 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		double[] confidence = { 0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2,
				0.9, 0.5, 0.5 };*/
		this.setLayout(new FillLayout());

		// �����������������ʽΪˮƽ����
		SashForm form = new SashForm(this, SWT.HORIZONTAL | SWT.BORDER);
		form.setLayout(new FillLayout());
		// TopMenu topmenu = new TopMenu(shell, SWT.BAR);
		// �������Ҳ�����
		Composite win1 = new Composite(form, SWT.NONE);
		win1.setLayout(new FillLayout());

		CTabFolderChart tab = new CTabFolderChart(form, SWT.NONE);
		form.setWeights(new int[] { 237, 821 });

		System.out.println("ip=");

		CompositeTable table = new CompositeTable(win1, SWT.BORDER | SWT.SINGLE,
				ip, minerresults.protocolPairList, tab);
		tableIndex = new int[table.getTableIndexCount()];

	}

}
