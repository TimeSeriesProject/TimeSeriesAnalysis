package WaveletUtil;

import java.util.ArrayList;
import java.util.List;

import weka.core.pmml.jaxbbindings.TimeException;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class Mallat {
	private Wavelet wavelet;
	private int layer;
	private DataItems dataItems;
	private double[] CA;
	private List<double[]>CDs;
	private int[] lenArray;
	

	private boolean hasTransfer;
	
	public Mallat(Wavelet wavelet,int layer,DataItems dataItems){
		this.wavelet=wavelet;
		this.layer=layer;
		this.dataItems=dataItems;
		CA=new double[dataItems.getData().size()];
		for(int i=0;i<dataItems.getData().size();i++){
			CA[i]=Double.parseDouble(dataItems.getData().get(i));
		}
		CDs=new ArrayList<double[]>();
		lenArray=new int[layer];
	}
	
	public void waveletTrasfer(){
		
		if(layer>(Math.log(dataItems.getData().size())/Math.log(2))){
			throw new RuntimeException("序列长度无法分解到"+layer+"层");
		}
		double[] nextCA=CA;
		int translayer=layer;
		int filterLen=wavelet.getLength();
		while(translayer>0){
			int decLen=(nextCA.length+filterLen-1)/2;
			lenArray[layer-translayer]=nextCA.length;
			double[] nextCD = new double[decLen];
			nextCA=decompositionDetail(nextCA,nextCD);
			CDs.add(nextCD);
			
			translayer--;
		}
		CA=nextCA;
		hasTransfer=true;
	}
	
	public void waveletInverseTransfer(){
		if(!hasTransfer){
			throw new RuntimeException("序列还未变换，无法逆变换");
		}
		int itranslayer=layer;
		while(itranslayer>0){
			CA=compositionDetail(CA, CDs.get(itranslayer-1),itranslayer-1);
			itranslayer--;
		}
		CDs.clear();
		hasTransfer=false;
	}
	
	/**
	 * 硬阈值去噪
	 */
	public void hardDenoising(){
		if(hasTransfer){
			for(double[] cd:CDs){
				int size=cd.length;
				for(int i=0;i<size;i++){
					cd[i]=0.0;
				}
			}
		}else{
			throw new RuntimeException("还未转换，无法去噪");
		}
	}
	
	private void softDenoising(double[] CD,double threshold){
		double[] tmp=new double[CD.length];
		for(int i=0;i<CD.length;i++){
			tmp[i]=CD[i];
			CD[i]=Math.abs(CD[i])-threshold;
		}
		for(int i=0;i<CD.length;i++){
			CD[i]=(CD[i]+Math.abs(CD[i]))/2;
		}
		for(int i=0;i<CD.length;i++){
			CD[i]=CD[i]*((CD[i]>0)?1:((CD[i]<0)?-1:0));
		}
	}
	
	public void softDenoising(){
		double[] thresholds=new double[layer];
		for(int i=0;i<layer;i++){
			int N=CDs.get(i).length;
			double param=0.0;
			for(int j=0;j<N;j++){
				param+=Math.abs(CDs.get(i)[j]);
			}
			param/=N;
			param/=0.6745;
			thresholds[i]=param;
			softDenoising(CDs.get(i),thresholds[i]);
		}
	}
	
	
	private double[] decompositionDetail(double[] CA,double[] nextCD){
		int dataLen=CA.length;
		int filterLen=wavelet.getLength();
		int decLen=(dataLen+filterLen-1)/2;
		double[] nextCA=new double[decLen];
		for(int n=0;n<decLen;n++){
			nextCA[n]=0;
			nextCD[n]=0;
			for(int k=0;k<filterLen;k++){
				int p=2*n-k+1;
				double tmp;
				if(p<0&&p>=-filterLen+1){
					tmp=CA[-p-1];
				}else if(p>=dataLen&&(p<=dataLen+filterLen-2)){
					tmp = CA[2*dataLen-p-1];
				}else if(p>=0&&p<dataLen){
					tmp=CA[p];
				}else{
					tmp=0;
				}
				nextCA[n]+=tmp*wavelet.getLowFilterDec()[k];
				nextCD[n]+=tmp*wavelet.getHighFilterDec()[k];
			}
		}
		return nextCA;
	}
	
	private double[] compositionDetail(double[] CA,double[] CD,int layer){
		int dataLen=CA.length;
		int filterLen=wavelet.getLength();
		int recLen=lenArray[layer];
		double[] nextCA=new double[recLen];
		for(int n=0;n<recLen;n++){
			nextCA[n]=0;
			for(int k=0; k<dataLen; k++)
	        {
	            int p = n-2*k+filterLen-2;
	            // 信号重构
	            if((p>=0)&&(p<filterLen))
	            {
	            	nextCA[n] += wavelet.getLowFilterRec()[p]*CA[k] + wavelet.getHighFilterRec()[p]*CD[k];
	            }
	        }
		}
		return nextCA;
	}
	
	
	public Wavelet getWavelet() {
		return wavelet;
	}

	public void setWavelet(Wavelet wavelet) {
		this.wavelet = wavelet;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public DataItems getDataItems() {
		return dataItems;
	}

	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
	}

	public double[] getCA() {
		return CA;
	}

	public void setCA(double[] cA) {
		CA = cA;
	}

	public List<double[]> getCDs() {
		if(hasTransfer){
			return CDs;
		}else{
			throw new RuntimeException("还未变换，无法获得小波参数");
		}
		
	}

	public void setCDs(List<double[]> cDs) {
		CDs = cDs;
	}

	public boolean isHasTransfer() {
		return hasTransfer;
	}

	public void setHasTransfer(boolean hasTransfer) {
		this.hasTransfer = hasTransfer;
	}
}
