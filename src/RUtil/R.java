package RUtil;

import java.util.List;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class R {
	private RConnection interpreteR;
	
	public RConnection connectRserve(){
		if(interpreteR==null||!interpreteR.isConnected()){
			try {
				interpreteR=new RConnection();
				interpreteR.eval("library(fUnitRoots)");
			} catch (RserveException e) {
				e.printStackTrace();
				throw new RuntimeException("连接到Rserve出错");
			}
		}
		return this.interpreteR;
	}
	
	public void closeRserve(){
		if(interpreteR!=null&&interpreteR.isConnected()){
			interpreteR.close();
		}
	}
	
	/**
     * 单位根检验
     * @return 序列平稳时差分次数 即I(i),I(0)表示序列是平稳的,I(1)表示序列差分一次后平稳。。。
     */
    public int adfTest(List<Double> seq){
    	int stationOrder=0;
    	if(seq.size()<2){
    		return 0;
    	}
    	 final double[] memoryArray = new double[seq.size()];
    	 for(int i=0; i<seq.size(); i++) {
             memoryArray[i] = seq.get(i);
         }
    	 try {
    		 interpreteR.assign("seq", memoryArray);
    		 interpreteR.assign("diffSeq",memoryArray);
    		 boolean isStationary=false;
    		 for(stationOrder =0;stationOrder<=2;stationOrder++){
    			 if(stationOrder>=1){
    				 interpreteR.eval("diffSeq<-diff(diffSeq)");
    			 }
    			 for(int lag=1;lag<=2;lag++){
    				 interpreteR.eval("unitRootTestNC<-unitrootTest(diffSeq,lag="+lag+",type='nc',title='none constant none trend',description='lag "+lag+"')");
//    				 interpreteR.eval("unitRootTestC<-unitrootTest(seq,lag="+lag+",type='c',title='constant',description='lag "+lag+"')");
//    				 interpreteR.eval("unitRootTestCT<-unitrootTest(seq,lag="+lag+",type='ct',title='constant and trend',description='lag "+lag+"')");
    				 
    				 double PValueOfNC=interpreteR.eval("unitRootTestNC@test$p.value[2]").asDouble();
//    				 double PValueOfC=interpreteR.eval("unitRootTestC@test$p.value[2]").asDouble();
//    				 double PValueOfCT=interpreteR.eval("unitRootTestCT@test$p.value[2]").asDouble();
//    				 if(PValueOfNC<=0.05||PValueOfC<=0.05||PValueOfCT<=0.05){
//    					 return stationOrder;
//    				 }
    				 if(PValueOfNC<=0.01){
    					 return stationOrder;
    				 }
    				 
    			 }
    		 }
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("ADF 求解出错");
		}
    	return stationOrder;
    }
    
    /**
     * 求解序列的自回归参数和偏回归参数，以确定ARIMA模型p和q
     * @param stationOrder 序列的平稳阶数
     * @return int[p,q]
     */
    public int[] autoCoeAndPartAutoCoe(List<Double>seq,int stationOrder){
    	int[] modelParams=new int[2]; 
    	if(seq.size()<2){
    		return null;
    	}
    	final double[] memoryArray = new double[seq.size()];
    	for(int i=0; i<seq.size(); i++) {
            memoryArray[i] = seq.get(i);
        }
    	try {
    		interpreteR.assign("seq", memoryArray);
    		interpreteR.eval("diffSeq<-seq");
    		for(int i=1;i<=stationOrder;i++){
        		interpreteR.eval("diffSeq<-diff(diffSeq)");
        	}
    		interpreteR.eval("acf<-acf(diffSeq,lag.max=100,plot='FALSE')");
    		interpreteR.eval("pacf<-pacf(diffSeq,lag.max=100,plot='FALSE')");
    		interpreteR.eval("seqLen<-length(diffSeq)");
    		double seqLen=interpreteR.eval("seqLen").asDouble();
    		double[] acf=interpreteR.eval("acf$acf").asDoubles();
    		double[] pacf=interpreteR.eval("pacf$acf").asDoubles();
    		int acfBeyondStd=0;              //标记acf系数超出2倍标准差的个数   当超出的个数为5时 acf拖尾
    		int pacfBeyondStd=0;             //标记pacf系数超出2倍标准差的个数   当超出的个数为5时 pacf拖尾
    		int firstAcfcoeWithinStd=0;        //标记acf系数第一次出现在2倍方差之内的下标
    		int firstPcacfcoeWithinStd=0;      //标记pacf系数第一次出现在2倍方差之内的下标
    		
    		for(int i=0;i<100;i++){
    			if(Math.abs(acf[i+1])>(2.0/Math.sqrt(seqLen))){
    				acfBeyondStd++;
    			}else if(firstAcfcoeWithinStd==0){
    				firstAcfcoeWithinStd=i+1;
    			}
    			if(Math.abs(pacf[i])>(2.0/Math.sqrt(seqLen))){
    				pacfBeyondStd++;
    			}else if(firstPcacfcoeWithinStd==0){
    				firstPcacfcoeWithinStd=i+1;
    			}
    		}
    		if(pacfBeyondStd<=5){
    			modelParams[0]=firstPcacfcoeWithinStd;
    			modelParams[1]=0;
    		}else if(acfBeyondStd<=5){
    			modelParams[0]=0;
    			modelParams[1]=firstAcfcoeWithinStd;
    		}else{
    			//若模型既不是AR模型又不是Ma模型，则是ARMA模型，通过AIC信息准则获取ARMA p和q的阶
    			double max=Double.MAX_VALUE;
    			firstPcacfcoeWithinStd=(firstPcacfcoeWithinStd<=5)?firstPcacfcoeWithinStd:5;
    			firstAcfcoeWithinStd=(firstAcfcoeWithinStd<=5)?firstAcfcoeWithinStd:5;
    			for(int i=1;i<=firstPcacfcoeWithinStd;i++){
    				for(int j=1;j<=firstAcfcoeWithinStd;j++){
    					String model="ar<-arima(x=diffSeq,order=c(" + i + "," + stationOrder + ","+ j + "))";
    					interpreteR.eval(model);
    					double aic=interpreteR.eval("ar$aic").asDouble();
    					if(max>aic){
    						max=aic;
    						modelParams[0]=i;
    						modelParams[1]=j;
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("ARIMA p和q参数求解出错");
		}
    	return modelParams;
    }

	public RConnection getInterpreteR() {
		return interpreteR;
	}

	public void setInterpreteR(RConnection interpreteR) {
		this.interpreteR = interpreteR;
	}
}
