package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import javax.swing.JPanel;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPM;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.NetworkMinerPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.Pair;

public class PanelShowResultsPartialCycle extends JPanel implements IPanelShowResults{
	private INetworkMiner miner;
	DecimalFormat formatter = new DecimalFormat("0.00");
//	JDesktopPane desktopPane;
	
	boolean displayed=false;
	
	public PanelShowResultsPartialCycle(TaskElement task){
		this();		
		InitMiner(task);
	}
	private void InitMiner(TaskElement task){
		this.miner = NetworkMinerFactory.getInstance().createMiner(task);
		miner.setResultsDisplayer(this);
	}
	/**
	 * Create the panel.
	 */
	private PanelShowResultsPartialCycle() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
//		gridBagLayout.rowHeights = new int[] {0, 1, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
		setLayout(gridBagLayout);
		
	}
	
	@Override
	public boolean start() {
		return miner.start();
	}

	@Override
	public boolean stop() {
		return miner.stop();
	}


	@Override
	public void setData(DataItems data) {
		
	}

	@Override
	public TaskElement getTask() {
		return miner.getTask();
	}
	@Override
	public INetworkMiner getMiner() {
		return miner;
	}
	@Override
	public void displayMinerResults(MinerResults rets) {
		if (rets == null)
			return;
		if(displayed==true)
			return;
		int count=0;
		for(Map.Entry<Integer,ArrayList<Pair<Integer,Integer>>> entry:rets.getRetPartialCycle().getPartialCyclePos().entrySet())
		{
			String title ="周期长度"+entry.getKey()+"部分周期序列";
			
			ChartPanelShowPartialCycle chartCycle = new ChartPanelShowPartialCycle(title,"","",null);
			chartCycle.displayDataItems(rets.getInputData(), entry.getValue());
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = count++;
			add(chartCycle,gbc_panel);
		}
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		displayed =true;
		
		//System.out.println(rets.getRetPM().get(i).getFirst()+" "cyclePos.get(index).)	
//		List<String> data =dataItems.getData();
//		int oldSize =data.size();
//		//oldSize =3200;
////			if(data.size()==0)
////				return;
//		int newSize = (int)Math.pow(2,(int) (Math.log(oldSize)/Math.log(2)));
//		if(newSize<oldSize)
//			newSize*=2;
//		System.out.println(oldSize+" "+newSize);
//		
//		double original[] = new double[newSize];
//		for(int i=0;i<newSize;i++)
//		{
//			if(i<oldSize)
//			{
//				original[i]=Double.parseDouble(data.get(i));
//			}
//			else
//				original[i]=0;
//		}
//		 FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
//		 Complex[] result = fft.transform(original, TransformType.FORWARD);
//		 for(int i=(int)(result.length*0.2);i<result.length;i++)
//		 {
//			 result[i]=new Complex(0,0);
//			// result[i+result.length/2]=new Complex(0,0);
//		 }
//		 System.out.println("result "+result.length);
//		 Complex [] denoised = fft.transform(result, TransformType.INVERSE);
//		 List<String> newData =new ArrayList<String>();
//		 for(int i=0;i<oldSize;i++)
//		 {
//			 newData.add(String.valueOf(denoised[i].getReal()));
//		 }
//		 dataItems.setData(newData);
		
//		rets.setInputData(dataItems);
	
	}
	@Override
	public void displayMinerResults() {
		MinerResults rets = miner.getResults();
		displayMinerResults(rets);		
	}
	
}