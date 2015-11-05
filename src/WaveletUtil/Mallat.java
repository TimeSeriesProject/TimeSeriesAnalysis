package WaveletUtil;

import java.util.ArrayList;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class Mallat {
	private Wavelet wavelet;
	private int layer;
	private DataItems dataItems;
	private double[] CA;
	private List<double[]>CDs;
	private boolean hasTransfer;
	
	public Mallat(Wavelet wavelet,int layer,DataItems dataItems){
		this.wavelet=wavelet;
		this.layer=layer;
		this.dataItems=dataItems;
		for(int i=0;i<dataItems.getLength();i++){
			CA[i]=Integer.parseInt(dataItems.getData().get(i));
		}
		CDs=new ArrayList<double[]>();
	}
	
	public void waveletTrasfer(){
		
		if(layer>(Math.log(dataItems.getLength())/Math.log(2))){
			throw new RuntimeException("序列长度无法分解到"+layer+"层");
		}
		double[] nextCA=CA;
		int translayer=layer;
		while(translayer>0){
			double[] nextCD = null;
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
			CA=compositionDetail(CA, CDs.get(itranslayer-1));
			itranslayer--;
		}
		CDs.clear();
		hasTransfer=false;
	}
	
	private double[] decompositionDetail(double[] CA,double[] nextCD){
		int dataLen=CA.length;
		int filterLen=wavelet.getLength();
		int decLen=(dataLen+filterLen-1)/2;
		double[] nextCA=new double[decLen];
		nextCD=new double[decLen];
		for(int n=0;n<decLen;n++){
			nextCA[n]=0;
			nextCD[n]=0;
			for(int k=0;k<filterLen;k++){
				int p=2*n-k;
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
				nextCA[n]=tmp*wavelet.getLowFilterDec()[k];
				nextCD[n]=tmp*wavelet.getHighFilterDec()[k];
			}
		}
		return nextCA;
	}
	
	private double[] compositionDetail(double[] CA,double[] CD){
		int dataLen=CA.length;
		int filterLen=wavelet.getLength();
		int recLen=2*dataLen-filterLen+1;
		double[] nextCA=new double[recLen];
		for(int n=0;n<recLen;n++){
			nextCA[n]=0;
			for(int k=0; k<dataLen; k++)
	        {
	            int p = n-2*k+filterLen-1;
	            // 信号重构
	            if((p>=0)&&(p<filterLen))
	            {
	            	nextCA[n] += wavelet.getLowFilterRec()[p]*CA[k] + wavelet.getHighFilterRec()[p]*CD[k];
	            }
	        }
		}
		return nextCA;
	}
}
