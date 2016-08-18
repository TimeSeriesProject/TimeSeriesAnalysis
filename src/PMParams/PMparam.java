package PMParams;

import org.jdom.Element;

public class PMparam {
	
	private int longestPeriod;       //可判定的最长周期
	private double threshold;        //判定是否具有周期的阈值
	
	public PMparam(){}
	
	public PMparam(int longestPeriod,double threshold){
		this.longestPeriod=longestPeriod;
		this.threshold=threshold;
	}
	
	public PMparam(Element element){
		String param = "";
	   	
    	param = element.getChildText("longestPeriod");
    	if(param != null){
    		longestPeriod = Integer.parseInt(param);
    	}
    	
    	param = element.getChildText("threshold");
    	if(param != null){
    		threshold = Double.parseDouble(param);
    	}
	}

	public int getLongestPeriod() {
		return longestPeriod;
	}

	public void setLongestPeriod(int longestPeriod) {
		this.longestPeriod = longestPeriod;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	
}
