package lineAssociation;

/**
 * Created by xzbang on 2015/12/21.
 */
public class Linear {

    public double theta=0.0;//线段倾斜角度
    public int startTime=0;//线段起始时间点
    public int span=0;//线段时间跨度
    public double hspan=0;//线段高度跨度
    public double startValue = 0.0;//线段起始值
    public double normTheta = 0.0;//归一化后的线段倾斜角度
    public double normSpan = 0.0;//归一化后的线段时间跨度
    public double normHspan = 0.0;//归一化后的线段高度跨度
    public double normStartValue = 0.0;//归一化后的线段起始值

    public Linear(){}
    /**
     * 斜率，起始点，序列跨度，起始点值
     * @param theta
     * @param startTime
     * @param span
     * @param startValue
     */
    public Linear(double theta, int startTime, int span, double startValue){
        this.theta = theta;
        this.startTime = startTime;
        this.span = span;
        this.startValue = startValue;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(String.valueOf(startTime));
        sb.append(",").append(theta).append(",").append(span).append(",").append(startValue);
        return sb.toString();
    }

    public String toDetailString(){
        StringBuilder sb = new StringBuilder(String.valueOf(startTime));
        sb.append(",").append(theta).append(",").append(span).append(",").append(startValue);
        sb.append(",").append(normTheta).append(",").append(normSpan).append(",").append(normStartValue);
        return sb.toString();
    }
}
