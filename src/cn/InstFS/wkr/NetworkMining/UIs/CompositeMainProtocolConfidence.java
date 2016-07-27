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

public class CompositeMainProtocolConfidence extends Composite {
	public int[] tableIndex = null;// 保存协议对左侧的协议索引值
	public ArrayList<ProtoclPair> protocolPairList = null;
	String ip = null;

	public CompositeMainProtocolConfidence(Composite parent, int style,
			MinerResultsFP_Line minerresults) {
		super(parent, style);
		ip = minerresults.getIp();

		this.setLayout(new FillLayout());

		SashForm form = new SashForm(this, SWT.HORIZONTAL | SWT.BORDER);
		form.setLayout(new FillLayout());
		Composite win1 = new Composite(form, SWT.NONE);
		win1.setLayout(new FillLayout());

		CTabFolderChart tab = new CTabFolderChart(form, SWT.NONE,this);
		form.setWeights(new int[] { 250, 800 });

		System.out.println("ip=");

		CompositeTable table = new CompositeTable(win1,
				SWT.BORDER | SWT.SINGLE, ip, minerresults.protocolPairList,
				tab, this);
		tableIndex = new int[table.getTableIndexCount()];

	}

}
