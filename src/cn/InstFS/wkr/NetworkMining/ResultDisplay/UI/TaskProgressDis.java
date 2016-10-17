package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

/**
 * Created by zsc on 2016/10/13.
 */
public class TaskProgressDis {
    private static TaskProgressDis instance;
    private String phase = "读取数据"; // 挖掘阶段

    private TaskProgressDis() {

    }
    public static TaskProgressDis getInstance() {
        if (instance!=null) {
            return instance;
        }
        instance = new TaskProgressDis();
        return instance;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void clear() {
        instance = null;
    }
}
