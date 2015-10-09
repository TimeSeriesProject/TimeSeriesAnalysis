package cn.InstFS.wkr.NetworkMining.DataInputs;

import org.rosuda.REngine.Rserve.RConnection;

public class TestR {
	public static void main(String[] args){
		try{
			RConnection conn=new RConnection();
			conn.eval("library(TSA)");
			conn.eval("oil.price<-read.table('RWTCm.csv',header=TRUE,sep=',',row.names='Date')");
			conn.eval("data(oil.price)");
			conn.eval("diff<-diff(oil.price)");
			}catch(Exception exception){
				exception.printStackTrace();
			}
	}
}
