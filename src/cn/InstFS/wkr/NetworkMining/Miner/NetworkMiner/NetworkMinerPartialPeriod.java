package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

import java.util.Timer;

/**
 * @// TODO: 17-1-3 chen  
 */
public class NetworkMinerPartialPeriod implements INetworkMiner {

    Timer timer;
    MinerResults results;
    IResultsDisplayer displayer;

    boolean isRunning;
    TaskElement task;
    public IsOver isOver;
    IReader reader;

    public NetworkMinerPartialPeriod(TaskElement task,IReader reader) {
        this.task = task;
        this.reader=reader;
        results = new MinerResults(this);
        isOver=new IsOver();
    }

    @Override
    public boolean start() {
        System.out.println("PanelShowResultsPartialPeriod   timer starting");
        return true;
    }

    @Override
    public boolean stop() {

        isRunning = false;
        task.setRunning(isRunning);
        UtilsUI.appendOutput(task.getTaskName() + " -- stopped");
        return true;
    }

    @Override
    public boolean isAlive() {
        return isRunning;
    }
    @Override
    public boolean isOver() {
        return isOver.isIsover();
    }
    @Override
    public TaskElement getTask() {
        return task;
    }
    @Override
    public MinerResults getResults() {
        return results;
    }
    @Override
    public void setResultsDisplayer(IResultsDisplayer displayer) {
        this.displayer = displayer;
    }
    public boolean isRunning(){
        return isRunning;
    }
}
