package Distributed;

import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPath;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

import java.io.Serializable;

/**
 * Created by zsc on 2016/6/3.
 */
public class TaskCombinationResult implements Serializable {
    private String name;
    private MiningObject miningObject;
    private MinerType minerType;
    private TaskRange taskRange;
    private TaskCombination taskCombination;
    private MinerNodeResults minerNodeResults;
    private MinerResultsPath minerResultsPath;
    private MinerNodeResults minerNetworkResults;//network
    private MinerProtocolResults minerProtocolResults;

//    private HashMap<TaskElement, INetworkMiner> allMiners = new HashMap<TaskElement, INetworkMiner>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MinerType getMinerType() {
        return minerType;
    }

    public void setMinerType(MinerType minerType) {
        this.minerType = minerType;
    }

    public TaskRange getTaskRange() {
        return taskRange;
    }

    public void setTaskRange(TaskRange taskRange) {
        this.taskRange = taskRange;
    }

    public MiningObject getMiningObject() {
        return miningObject;
    }

    public void setMiningObject(MiningObject miningObject) {
        this.miningObject = miningObject;
    }

    public MinerNodeResults getMinerNodeResults() {
        return minerNodeResults;
    }

    public void setMinerNodeResults(MinerNodeResults minerNodeResults) {
        this.minerNodeResults = minerNodeResults;
    }

    public MinerResultsPath getMinerResultsPath() {
        return minerResultsPath;
    }

    public MinerNodeResults getMinerNetworkResults() {
        return minerNetworkResults;
    }

    public void setMinerNetworkResults(MinerNodeResults minerNetworkResults) {
        this.minerNetworkResults = minerNetworkResults;
    }

    public void setMinerResultsPath(MinerResultsPath minerResultsPath) {
        this.minerResultsPath = minerResultsPath;
    }

    public MinerProtocolResults getMinerProtocolResults() {
        return minerProtocolResults;
    }

    public void setMinerProtocolResults(MinerProtocolResults minerProtocolResults) {
        this.minerProtocolResults = minerProtocolResults;
    }

    public TaskCombination getTaskCombination() {
        return taskCombination;
    }

    public void setTaskCombination(TaskCombination taskCombination) {
        this.taskCombination = taskCombination;
    }



//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof TaskCombinationResult)) {
//            return false;
//        }
//        TaskCombinationResult taskCombinationResult = (TaskCombinationResult) o;
//        return taskCombinationResult.getName().equals(name);
//    }
//
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((name == null) ? 0 : name.hashCode());
////        result = result * 31 + ((dataResult == null) ? 0 : dataResult.hashCode());
//        return result;
//    }
}
