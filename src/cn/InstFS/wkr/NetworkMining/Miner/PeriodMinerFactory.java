package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class PeriodMinerFactory {
	private static PeriodMinerFactory inst;
	public static boolean isMining=false;
	public String filePath;
	private PeriodMinerFactory(){}
	public static PeriodMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new PeriodMinerFactory();
		}
		return inst;
	}
	
	public void minerAllPeriods(){
		if(isMining)
			return;
		isMining=true;
		File rootDirectory=new File(filePath);
		if(rootDirectory.isFile()){
			
		}else{
			for(File file:rootDirectory.listFiles()){
				
				
			}
		}
	}
	
	private void parseFile(File file){
		String fileName=file.getName();
		String ip=fileName.substring(0, fileName.lastIndexOf("."));
		int[] granularities={3600,3600*24,3600*24*7};
		for(int granularity:granularities){
			TaskElement task=new TaskElement();
			
			task.setDataSource("Text");
			//TODO
			TaskElement.add1Task(task, true);
		}
	}
}
