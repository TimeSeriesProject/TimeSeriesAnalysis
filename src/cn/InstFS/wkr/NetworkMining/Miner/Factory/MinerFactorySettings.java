package cn.InstFS.wkr.NetworkMining.Miner.Factory;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Arbor vlinyq@gmail.com
 * @date 2016/6/30
 */
public abstract class MinerFactorySettings {
    private String dataPath;
    private String minerType;
    private List<MiningObject> miningObjectList = new ArrayList<>();
    private List<MiningObject> miningObjectsChecked = new ArrayList<>();

    private List<MiningMethod> miningMethodsList = new ArrayList<>();
    private List<MiningMethod> miningMethodsChecked = new ArrayList<>();
    private TaskRange taskRange;
    private String granularity = "3600";
    private String granularityList;
    private boolean isModified = false;
    private boolean isOnlyObjectModified = false;
    private List<MiningObject> miningObjectsAdded = new ArrayList<>();
    private List<MiningObject> miningObjectsDeleted = new ArrayList<>();

    private Date startDate;
    private Date endDate;

    public MinerFactorySettings(String minerType) {
        this.minerType = minerType;
        granularityList = GlobalConfig.getInstance().getGranularityList();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.set(2017, 2, 1, 0, 0, 0);
        cal2.set(2016,4,3,0,0,0);
        startDate = new Date(cal1.getTimeInMillis()/1000 * 1000);
        endDate = new Date(cal2.getTimeInMillis()/1000 * 1000);
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

    public String getMinerType() {
        return minerType;
    }

    public void setMinerType(String minerType) {
        this.minerType = minerType;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public String getGranularityList() {
        return granularityList;
    }

    public void setGranularityList(String granularityList) {
        this.granularityList = granularityList;
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

    public List<MiningObject> getMiningObjectsAdded() {
        return miningObjectsAdded;
    }

    public void setMiningObjectsAdded(List<MiningObject> miningObjectsAdded) {
        this.miningObjectsAdded = miningObjectsAdded;
    }

    public boolean isOnlyObjectModified() {
        return isOnlyObjectModified;
    }

    public void setOnlyObjectModified(boolean onlyObjectModified) {
        isOnlyObjectModified = onlyObjectModified;
    }

    public List<MiningObject> getMiningObjectsDeleted() {
        return miningObjectsDeleted;
    }

    public void setMiningObjectsDeleted(List<MiningObject> miningObjectsDeleted) {
        this.miningObjectsDeleted = miningObjectsDeleted;
    }

    public List<MiningMethod> getMiningMethodsList() {
        return miningMethodsList;
    }

    public void setMiningMethodsList(List<MiningMethod> miningMethodsList) {
        this.miningMethodsList = miningMethodsList;
    }

    public List<MiningMethod> getMiningMethodsChecked() {
        return miningMethodsChecked;
    }

    public void setMiningMethodsChecked(List<MiningMethod> miningMethodsChecked) {
        this.miningMethodsChecked = miningMethodsChecked;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
