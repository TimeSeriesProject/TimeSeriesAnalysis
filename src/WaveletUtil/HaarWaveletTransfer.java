package WaveletUtil;

import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class HaarWaveletTransfer {
	private int layer;
	private DataItems dataItems;
	private int[] signalParams;
	private int[] ISignalParams;
	private boolean hasTransfer;
	public HaarWaveletTransfer(DataItems dataItems,int layer){
		this.dataItems=dataItems;
		this.layer=layer;
		signalParams=new int[dataItems.getLength()];
		ISignalParams=new int[dataItems.getLength()];
		hasTransfer=false;
	}
	
	public HaarWaveletTransfer(){
		hasTransfer=false;
	}

	/**
	 * 离散Haar小波变换
	 */
	public void HaarDWT(){
		if(layer>(Math.log(dataItems.getLength())/Math.log(2))){
			throw new RuntimeException("序列长度无法分解到"+layer+"层");
		}
		int size=dataItems.getLength();
		List<String> data=dataItems.getData();
		for(int i=0;i<size;i++){
			signalParams[i]=Integer.parseInt(data.get(i));
		}
		int[] tempSignal=new int[size];
		int t=size;
		while(layer>0){
			t=t/2;
			for(int i=0;i<t;i++){
				tempSignal[i]=(int)((signalParams[2*i]+signalParams[2*i+1])/1.414);
				tempSignal[t+i]=(int)((signalParams[2*i]-signalParams[2*i+1])/1.414);
			}
			for(int i=0;i<size;i++){
				signalParams[i]=tempSignal[i];
			}
			layer--;
		}
		hasTransfer=true;
	}
	/**
	 * 离散  Haar 小波逆变换
	 */
	public void HasrIDWT(){
		if(!hasTransfer){
			throw new RuntimeException("还未小波变换，无法进行逆变换");
		}
		int size=dataItems.getLength();
		for(int i=0;i<size;i++){
			ISignalParams[i]=signalParams[i];
		}
		int t=(int)(size/Math.pow(2, layer));
		int[] tempSignal=new int[size];
		while(layer>0){
			
			for(int i=0;i<t;i++){
				tempSignal[2*i]=(int)((signalParams[i]+signalParams[t+i])*0.7071);
				tempSignal[2*i+1]=(int)((signalParams[i]-signalParams[t+i])*0.7071);
			}
			for(int i=0;i<size;i++){
				signalParams[i]=tempSignal[i];
			}
			t*=2;
			layer--;
			
		}
	}
	
	
	
	
	
	public int getLayer() {
		return layer;
	}

	public int[] getSignalParams() {
		return signalParams;
	}

	public void setSignalParams(int[] signalParams) {
		this.signalParams = signalParams;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public DataItems getDataItems() {
		return dataItems;
	}

	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
		if(signalParams==null||signalParams.length==0){
			signalParams=new int[dataItems.getLength()];
		}
		if(ISignalParams==null||ISignalParams.length==0){
			ISignalParams=new int[dataItems.getLength()];
		}
	}
}
