package lineAssociation;

/*
此类已弃用
 */
public class Symbol {
	public int startTime;
	public int endTime;
	public int value;
	public Symbol(){}
	public Symbol(int startTime,int endTime,int value){
		this.startTime=startTime;
		this.endTime=endTime;
		this.value=value;
	}
	public Symbol(int startTime,int value){
		this.startTime=startTime;
		this.value=value;
	}
}
