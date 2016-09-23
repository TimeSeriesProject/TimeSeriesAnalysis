package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkMinerFactory;

import java.util.ArrayList;

/**
 * @author Arbor vlinyq@gmail.com
 * @version 2016/9/13
 */
public class TaskProgress {
    private static TaskProgress instance;
    private int taskComNum; // taskCombination数量
    private int taskComplete; // 完成的任务数量
    private String phase = "读取数据"; // 挖掘阶段

    private ArrayList<String> errTaskList; //发生异常的TaskCombination

    private TaskProgress() {
        taskComNum = NetworkMinerFactory.getInstance().allCombinationMiners.size();
        taskComplete = 0;
        errTaskList = new ArrayList<>();
    }
    public static TaskProgress getInstance() {
        if (instance!=null) {
            return instance;
        }
        instance = new TaskProgress();
        return instance;
    }

    public int getTaskComNum() {
        return taskComNum;
    }

    public void setTaskComNum(int taskComNum) {
        this.taskComNum = taskComNum;
    }

    public int getTaskComplete() {
        return taskComplete;
    }

    public void setTaskComplete(int taskComplete) {
        this.taskComplete = taskComplete;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void increaseComplete(){
        taskComplete++;
    }

    public void addErrTaskCombination(TaskCombination taskCombination) {
        errTaskList.add(taskCombination.getName());
    }

    public ArrayList<String> getErrTaskList() {
        return errTaskList;
    }

    public void clear() {
        instance = null;
    }

}
