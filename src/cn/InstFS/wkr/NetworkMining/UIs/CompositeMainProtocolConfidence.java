package cn.InstFS.wkr.NetworkMining.UIs;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import associationRules.ProtoclPair;
import java.util.ArrayList;

import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsFP_Line;

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
		//win1用于存放左边的表格
		Composite win1 = new Composite(form, SWT.NONE);
		win1.setLayout(new FillLayout());
		//tab用于存放右边的标签页
		CTabFolderChart tab = new CTabFolderChart(form, SWT.NONE,this);
		form.setWeights(new int[] { 250, 800 });

		System.out.println("ip=");

		CompositeTable table = new CompositeTable(win1,
				SWT.BORDER | SWT.SINGLE, ip, minerresults.protocolPairList,
				tab, this);
		tableIndex = new int[table.getTableIndexCount()];

	}

}
