package cn.InstFS.wkr.NetworkMining.UIs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Label;

import associationRules.ProtoclPair;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public class CompositeSWTChart extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */

	SashForm s = null;
	private Table table;
	Button b = null;
/*	DataItems dataitems1=null;
	DataItems dataitems2=null;*/

	public CompositeSWTChart(Composite parent, int style,ProtoclPair pp) {
		super(parent, style);
		Layout layout = new FillLayout(SWT.VERTICAL);
		this.setLayout(layout);
		//这里调用图表1
/*		 this.dataitems1= pp.getDataItems1();
		 this.dataitems2=pp.getDataItems2();*/
		//////////////////////
		/////////////////

		TimeSeriesChart1 c1 = new TimeSeriesChart1(this, SWT.NULL, pp);
		// System.out.println("CompositeSWTChart 调用 TimeSeriesChart1 结束");
		 
		 
/*		FillLayout fillLayout = (FillLayout) c1.getLayout();
		fillLayout.type = SWT.VERTICAL;*/
		//TimeSeriesChart2 c2 = new TimeSeriesChart2(this, SWT.NULL, pp);

	}

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1600, 900);
		shell.setLayout(new FillLayout());
		shell.setText("Test for jfreechart running with SWT");
		
		//这里需要传一个DataItems dataitems
		//CompositeSWTChart tab1 = new CompositeSWTChart(shell, SWT.NONE);
		/*
		 * ChartComposite frame = new ChartComposite(shell, SWT.NONE, chart,
		 * true);
		 */
		// frame.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
