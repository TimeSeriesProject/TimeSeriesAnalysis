package associationRules;

/**
 * Created by xzbang on 2015/12/21.
 */
public class Linear {

    private double slope=0.0;//线段斜率
    private int startTime=0;//线段起始时间点
    private int span=0;//线段时间跨度
    private double startValue = 0.0;//线段起始值

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public double getStartValue() {
        return startValue;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }


}
