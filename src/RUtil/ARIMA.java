package RUtil;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.Rserve.RConnection;

public class ARIMA {
	private List<Double> seq;
    private int p,q,d;
    private RConnection interpreteR;
    private int forecastSize;

    /**
     * Creates an ARIMA predictor with given parameters.
     *
     * @param p first argument for ARIMA(p,d,q) model
     * @param d second argument for ARIMA(p,d,q) model
     * @param q thrid argument for ARIMA(p,d,q) model
     * @param forecastSize number of elements to calculate in advance when
     * <code>predictVector</code> method was called.
     */
    public ARIMA(int p,int d,int q,int forecastSize,RConnection interpreteR) {
        this.p = p;
        this.q = q;
        this.d = d;
        this.interpreteR=interpreteR;
        this.seq = new ArrayList<Double>();
        this.forecastSize = forecastSize;
    }

	/**
     * Creates an ARIMA predictor with given parameters to forecast next value.
     *
     * @param p first argument for ARIMA(p,d,q) model
     * @param d second argument for ARIMA(p,d,q) model
     * @param q thrid argument for ARIMA(p,d,q) model
     * <code>predictVector</code> method was called.
     */
    public ARIMA(int p,int d,int q,RConnection interpreteR) {
        this(p,d,q,1,interpreteR);
    }


    public double predict() {
        if(this.seq.size()<2) return 0;
        final double[] memoryArray = new double[this.seq.size()];
        for(int i=0; i<this.seq.size(); i++) {
            memoryArray[i] = this.seq.get(i);
        }
        try {
            this.interpreteR.assign("x", memoryArray);
            this.interpreteR.eval("ar<-arima(x=x,order=c(" + p + "," + d + ","
                + q + "))");
            this.interpreteR.eval("f<-predict(ar, n.ahead=1)");
            return this.interpreteR.eval("f$pred[1]").asDouble();
        } catch(Exception e) {
            return Double.NaN;
        }
    }


    public double[] predictVector() {
        final double[] memoryArray = new double[this.seq.size()];
        if(this.seq.size()<2) return memoryArray;
        for(int i=0; i<this.seq.size(); i++) {
            memoryArray[i] = this.seq.get(i);
        }

        double forecast[] = new double[this.forecastSize];
        try {
            this.interpreteR.assign("x", memoryArray);
            this.interpreteR.eval("ar<-arima(x=x,order=c(" + p + "," + d + ","
                    + q + "))");
            this.interpreteR.eval("f<-predict(ar,n.ahead=" 
                    + this.forecastSize +  ")");
            for(int i=1; i<=this.forecastSize; i++) {
                forecast[i-1] = this.interpreteR.eval("f$pred[" + i + "]").asDouble();
            } 
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
        return forecast;
    }
    
    public RConnection getInterpreteR() {
		return interpreteR;
	}

	public void setInterpreteR(RConnection interpreteR) {
		this.interpreteR = interpreteR;
	}

	public int getForecastSize() {
		return forecastSize;
	}

	public void setForecastSize(int forecastSize) {
		this.forecastSize = forecastSize;
	}

	public List<Double> getSeq() {
		return seq;
	}
    
    public void setSeq(double[] vals) {
       for(int i=0; i<vals.length; i++) {
           this.seq.add(vals[i]);
       }
    }

    public void setSeq(List<Double> vals) {
        this.seq.addAll(vals);
    }

    public void addSeq(double val) {
        this.seq.add(val);
    }

    public void reset() {
        this.seq.clear();
    }

    public String toString() {
        return "ARIMA(" + p + "," + d + "," + q + ")";
    }
}
