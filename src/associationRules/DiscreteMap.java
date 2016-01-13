package associationRules;

/**
 * Created by xzbang on 2015/12/23.
 */
public class DiscreteMap {
    public int symbol;
    public double start;
    public double end;
    public double realStart;
    public double realEnd;

    public DiscreteMap(){}
    public DiscreteMap(int symbol,double start,double end,double realStart,double realEnd){
        this.symbol = symbol;
        this.start = start;
        this.end = end;
        this.realStart = realStart;
        this.realEnd = realEnd;
    }

    public String toString(){
        return "(symbol: " + symbol + "; " +
                "start: " + start + "; " +
                "end: " + end + "; " +
                "realStart: " + realStart + "; " +
                "realEnd: " + realEnd + ")";
    }
}
