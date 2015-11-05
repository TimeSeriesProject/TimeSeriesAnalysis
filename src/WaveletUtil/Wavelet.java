package WaveletUtil;

public enum Wavelet {
	Wavelet_db1("db1"),
	Wavelet_db2("db2"),
	Wavelet_db3("db3"),
	Wavelet_db4("db4");
	private String value;
	private int length;
	private double[] lowFilterDec;
	private double[] highFilterDec;
	private double[] lowFilterRec;
	private double[] highFilterRec;
	
	@Override
	public String toString() {
		return value;
	}
	private Wavelet(String value) {
		this.value=value;
	}
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public double[] getLowFilterDec() {
		return lowFilterDec;
	}
	public void setLowFilterDec(double[] lowFilterDec) {
		this.lowFilterDec = lowFilterDec;
	}
	public double[] getHighFilterDec() {
		return highFilterDec;
	}
	public void setHighFilterDec(double[] highFilterDec) {
		this.highFilterDec = highFilterDec;
	}
	public double[] getLowFilterRec() {
		return lowFilterRec;
	}
	public void setLowFilterRec(double[] lowFilterRec) {
		this.lowFilterRec = lowFilterRec;
	}
	public double[] getHighFilterRec() {
		return highFilterRec;
	}
	public void setHighFilterRec(double[] highFilterRec) {
		this.highFilterRec = highFilterRec;
	}
	private static Wavelet createWaveletByName(String value){
		if(value.equals("db1")){
			Wavelet wavelet=Wavelet_db1;
			wavelet.setLength(2);
			double[] lowFilterDec=new double[2];
			lowFilterDec[0]=0.7071;
			lowFilterDec[1]=0.7071;
			double[] lowFilterRec=new double[2];
			lowFilterRec[0]=0.7071;
			lowFilterRec[1]=0.7071;
			double[] highFilterDec=new double[2];
			highFilterDec[0]=-0.7071;
			highFilterDec[1]=0.7071;
			double[] highFilterRec=new double[2];
			highFilterRec[0]=0.7071;
			highFilterRec[1]=-0.7071;
			wavelet.setLowFilterDec(lowFilterDec);
			wavelet.setHighFilterDec(highFilterDec);
			wavelet.setLowFilterRec(lowFilterRec);
			wavelet.setHighFilterRec(highFilterRec);
			return wavelet;
		}else if(value.equals("db3")){
			Wavelet wavelet=Wavelet_db3;
			wavelet.setLength(6);
			double[] lowFilterDec=new double[]{0.0352,-0.0854,-0.1350,0.4599,0.8069,0.3327};
			double[] lowFilterRec=new double[]{0.3327,0.8069,0.4599,-0.1350,-0.0854,0.0352};
			double[] highFilterDec=new double[]{-0.3327,0.8069,-0.4599,-0.1350,0.0854,0.0352};
			double[] highFilterRec=new double[]{0.0352,0.0854,-0.1350,-0.4599,0.8069,-0.3327};
			wavelet.setLowFilterDec(lowFilterDec);
			wavelet.setHighFilterDec(highFilterDec);
			wavelet.setLowFilterRec(lowFilterRec);
			wavelet.setHighFilterRec(highFilterRec);
			return wavelet;
		}else if(value.equals("db2")){
			Wavelet wavelet=Wavelet_db2;
			wavelet.setLength(4);
			double[] lowFilterDec=new double[]{-0.1294,0.2241,0.8365,0.4830};
			double[] lowFilterRec=new double[]{0.4830,0.8365,0.2241,-0.1294};
			double[] highFilterDec=new double[]{-0.4830,0.8365,-0.2241,-0.1294};
			double[] highFilterRec=new double[]{-0.1294,-0.2241,0.8365,-0.4830};
			wavelet.setLowFilterDec(lowFilterDec);
			wavelet.setHighFilterDec(highFilterDec);
			wavelet.setLowFilterRec(lowFilterRec);
			wavelet.setHighFilterRec(highFilterRec);
			return wavelet;
		}else if(value.equals("db4")){
			Wavelet wavelet=Wavelet_db1;
			wavelet.setLength(8);
			double[] lowFilterDec=new double[]{-0.0106,0.0329,0.0308,-0.1870,-0.0280,0.6309,0.7148,0.2304};
			double[] lowFilterRec=new double[]{0.2304,0.7148,0.6309,-0.0280,-0.1870,0.0308,0.0329,-0.0106};
			double[] highFilterDec=new double[]{-0.2304,0.7148,-0.6309,-0.0280,0.1870,0.0308,-0.0329,-0.0106};
			double[] highFilterRec=new double[]{ -0.0106,-0.0329,0.0308,0.1870,-0.0280,-0.6309,0.7148,-0.2304};
			wavelet.setLowFilterDec(lowFilterDec);
			wavelet.setHighFilterDec(highFilterDec);
			wavelet.setLowFilterRec(lowFilterRec);
			wavelet.setHighFilterRec(highFilterRec);
			return wavelet;
		}else{
			return Wavelet_db4;
		}
	}
	
	public static Wavelet WaveletFromString(String value){
		return createWaveletByName(value);
	}

}
