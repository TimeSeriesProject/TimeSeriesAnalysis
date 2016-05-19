package lineAssociation;


/**
 * Created by xzbang on 2016/1/18.
 */
public class Similarity {
    public static double getDistance(Linear l1,Linear l2){

//        return getEuclideanDistance(l1,l2);
//        return getManhattanDistance(l1,l2);
//        return getSigmoidDistance(l1,l2);
//        return getNewDistance(l1,l2);
//        return getXiaoDistance(l1,l2);
//        return getXiao2Distance(l1, l2);
//        return getXiao3Distance(l1, l2);
        return getXiao4Distance(l1, l2);
//        return getXiao5Distance(l1, l2);
//        return getSinDistance(l1,l2);
//        return getSin2Distance(l1,l2);
//        return getSin3Distance(l1, l2);
//        return getChebyshevDistance(l1,l2);
    }

    private static double getEuclideanDistance(Linear l1,Linear l2){
        return Math.sqrt(Math.pow((l1.normTheta-l2.normTheta),2)
                +Math.pow((l1.normSpan-l2.normSpan),2)
                +Math.pow((l1.normStartValue-l2.normStartValue),2));
    }

    private static double getManhattanDistance(Linear l1,Linear l2){
        return Math.abs(l1.normTheta-l2.normTheta)
                +Math.abs(l1.normSpan-l2.normSpan)
                +Math.abs(l1.normStartValue-l2.normStartValue);
    }

    private static double getSinDistance(Linear l1,Linear l2){
        if(l1.normTheta*l2.normTheta>=0) {
            return Math.max(Math.max(Math.abs(Math.sin(Math.PI * l1.normTheta / 2) - Math.sin(Math.PI * l2.normTheta / 2)),
                            Math.abs(l1.normSpan - l2.normSpan)),
                    Math.abs(l1.normStartValue - l2.normStartValue));
        }else{
            return 100.0;
        }
    }

    private static double getSin2Distance(Linear l1,Linear l2){

            return Math.max(Math.max(Math.abs(Math.sin(Math.PI * l1.normTheta / 2) - Math.sin(Math.PI * l2.normTheta / 2)),
                            Math.abs(l1.normSpan - l2.normSpan)),
                    Math.abs(l1.normStartValue - l2.normStartValue));

    }

    private static double getSin3Distance(Linear l1,Linear l2){

        if(l1.normTheta*l2.normTheta>=0) {
            return Math.max(Math.abs(Math.sin(Math.PI * l1.normTheta / 2) - Math.sin(Math.PI * l2.normTheta / 2)),
                            Math.abs(l1.normSpan - l2.normSpan));
        }else{
            return 100.0;
        }

    }

    private static double getChebyshevDistance(Linear l1,Linear l2){
        return Math.max(Math.max(Math.abs(l1.normTheta-l2.normTheta),
                Math.abs(l1.normSpan-l2.normSpan)),
                Math.abs(l1.normStartValue-l2.normStartValue));
    }

    private static double getNewDistance(Linear l1,Linear l2){
        if(l1.normTheta*l2.normTheta>0) {
            return Math.abs(l1.normTheta - l2.normTheta)
                    + Math.abs(l1.normSpan - l2.normSpan)
                    + Math.abs(l1.normStartValue - l2.normStartValue);
        }else{
            return 100.0;
        }
    }

    private static double getXiaoDistance(Linear l1,Linear l2){
        if(l1.normTheta*l2.normTheta>0) {
            return Math.max(Math.abs(l1.normTheta - l2.normTheta)/(Math.min(Math.abs(l1.normTheta),Math.abs(l2.normTheta))+0.001),
                    Math.abs(l1.normSpan - l2.normSpan)/(Math.min(l1.normSpan,l2.normSpan)+0.001));
        }else{
            return 10000000.0;
        }
    }

    private static double getXiao2Distance(Linear l1,Linear l2){
        if(l1.normTheta*l2.normTheta>0) {
            return Math.abs(l1.normTheta - l2.normTheta)/(Math.min(Math.abs(l1.normTheta),Math.abs(l2.normTheta))+0.00001)+
                    Math.abs(l1.normSpan - l2.normSpan)/(Math.min(l1.normSpan,l2.normSpan)+0.00001);
        }else{
            return 10000000.0;
        }
    }

    private static double getXiao5Distance(Linear l1,Linear l2){
        if(l1.normTheta*l2.normTheta>0) {
            return Math.abs(l1.normTheta - l2.normTheta)+
                    Math.abs(l1.normSpan - l2.normSpan)/(Math.min(l1.normSpan,l2.normSpan)+0.00001);
        }else{
            return 10000000.0;
        }
    }

    private static double getXiao3Distance(Linear l1,Linear l2){
        if(l1.normTheta*l2.normTheta>0) {
            return Math.abs(l1.normHspan - l2.normHspan)/(Math.min(Math.abs(l1.normHspan),Math.abs(l2.normHspan))+0.00001)+
                    Math.abs(l1.normSpan - l2.normSpan)/(Math.min(l1.normSpan,l2.normSpan)+0.00001);
        }else{
            return 10000000.0;
        }
    }

    private static double getXiao4Distance(Linear l1,Linear l2){
        if(l1.normTheta*l2.normTheta>0) {
            return Math.abs(l1.normHspan - l2.normHspan)+
                   Math.abs(l1.normSpan - l2.normSpan);
        }else{
            return 1.0;
        }
    }

    private static double getSigmoidDistance(Linear l1, Linear l2){
        return 4*(Math.abs(Math.exp(l1.normTheta)/(1+Math.exp(l1.normTheta))
                -Math.exp(l2.normTheta)/(1+Math.exp(l2.normTheta))))
                +Math.abs(l1.normSpan-l2.normSpan);
    }

    public static void main(String[] args) {
        Linear linear1 = new Linear(0.2,10,3,0.6);
        Linear linear2 = new Linear(0.1,10,3,0.6);
        Linear linearMax = new Linear(0.13665665867841337,0,164,1.0);
        Linear linearMin = new Linear(-0.2696697661917023,0,1,0.0027502750275027526);

        linear1.normTheta = linear1.theta;
//        linear1.normTheta = (linear1.theta-linearMin.theta)/(linearMax.theta-linearMin.theta);
        linear1.normTheta = linear1.theta/Math.max(Math.abs(linearMax.theta),Math.abs(linearMin.theta));//[-1,1]
        linear1.normSpan = (linear1.span-linearMin.span)*1.0/(linearMax.span-linearMin.span);
        linear1.normStartValue = (linear1.startValue-linearMin.startValue)/(linearMax.startValue-linearMin.startValue);

        linear2.normTheta = linear2.theta;
//        linear2.normTheta = (linear2.theta-linearMin.theta)/(linearMax.theta-linearMin.theta);
        linear2.normTheta = linear2.theta/Math.max(Math.abs(linearMax.theta),Math.abs(linearMin.theta));//[-1,1]
        linear2.normSpan = (linear2.span-linearMin.span)*1.0/(linearMax.span-linearMin.span);
        linear2.normStartValue = (linear2.startValue-linearMin.startValue)/(linearMax.startValue-linearMin.startValue);

        linear1.normTheta = (Math.exp(linear1.normTheta)/(1+Math.exp(linear1.normTheta))-0.5)*2*(1+Math.E)/(Math.E-1);
        linear2.normTheta = (Math.exp(linear2.normTheta)/(1+Math.exp(linear2.normTheta))-0.5)*2*(1+Math.E)/(Math.E-1);

        System.out.println("linear1: "+linear1.toDetailString());
        System.out.println("linear2: "+linear2.toDetailString());
        System.out.println("similarity: "+getDistance(linear1,linear2));
    }
}
