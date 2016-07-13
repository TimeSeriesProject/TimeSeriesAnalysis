package cn.InstFS.wkr.NetworkMining.Miner;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import org.apache.commons.math3.analysis.function.Min;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arbor vlinyq@gmail.com
 * @date 2016/6/30
 */
public abstract class MinerFactorySettings {
    private String dataPath;
    private List<MiningObject> miningObjectList = new ArrayList<>();
    private List<MiningObject> miningObjectsChecked = new ArrayList<>();
    private TaskRange taskRange;
    private String granularity = "3600";
    private boolean isModified = false;

    public MinerFactorySettings() {

    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public TaskRange getTaskRange() {
        return taskRange;
    }

    public void setTaskRange(TaskRange taskRange) {
        this.taskRange = taskRange;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }
    public List<MiningObject> getMiningObjectList() {
        return miningObjectList;
    }

    public void setMiningObjectList(List<MiningObject> miningObjectList) {
        this.miningObjectList = miningObjectList;
    }
    public List<MiningObject> getMiningObjectsChecked() {
        return miningObjectsChecked;
    }

    public void setMiningObjectsChecked(List<MiningObject> miningObjectsChecked) {
        this.miningObjectsChecked = miningObjectsChecked;
    }
    public boolean isModified() {
        return isModified;
    }
    public void setModified(boolean modified) {
        isModified = modified;
    }
}
