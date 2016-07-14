package Distributed;

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.MinerResultsPath;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zsc on 2016/6/3.
 */
public class TaskCombinationResult implements Serializable {
    private String name;
    private MiningObject miningObject;
    private MinerType minerType;
    private TaskCombination taskCombination;
    private MinerNodeResults minerNodeResults;
    private MinerResultsPath minerResultsPath;
    private MinerNodeResults minerNodeResults2;//network

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

    public MinerNodeResults getMinerNodeResults2() {
        return minerNodeResults2;
    }

    public void setMinerNodeResults2(MinerNodeResults minerNodeResults2) {
        this.minerNodeResults2 = minerNodeResults2;
    }

    public void setMinerResultsPath(MinerResultsPath minerResultsPath) {
        this.minerResultsPath = minerResultsPath;
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
