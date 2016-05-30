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

	public CompositeSWTChart(Composite parent, int style) {
		super(parent, style);
		Layout layout = new FillLayout();
		this.setLayout(layout);

		TimeSeriesChart1 c1 = new TimeSeriesChart1(this, SWT.NULL, 0, 0,
				new double[] {}, new double[] {});
		FillLayout fillLayout = (FillLayout) c1.getLayout();
		fillLayout.type = SWT.VERTICAL;
		TimeSeriesChart2 c2 = new TimeSeriesChart2(c1, SWT.NULL, 0, 0,
				new double[] {}, new double[] {});
	}

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1600, 900);
		shell.setLayout(new FillLayout());
		shell.setText("Test for jfreechart running with SWT");

		CompositeSWTChart tab1 = new CompositeSWTChart(shell, SWT.NONE);
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
