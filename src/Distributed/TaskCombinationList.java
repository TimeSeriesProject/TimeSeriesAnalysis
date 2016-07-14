package Distributed;

import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

import java.io.*;
import java.util.*;

/**
 * Created by zsc on 2016/5/31.
 */
public class TaskCombinationList implements Serializable {
    private static List<TaskCombination> taskCombinationList;

    public List<TaskCombination> getTaskCombinationList() {
        return taskCombinationList;
    }

    public static void clearTaskCombinationList(){
        taskCombinationList.clear();
    }

    public static boolean addTaskOnly(TaskCombination task, boolean saveToFile) {
        if (taskCombinationList == null) {
            taskCombinationList = new ArrayList<TaskCombination>();
        }
        taskCombinationList.add(task);
        return true;
    }

    public static boolean addListenerOnly(TaskCombination task, boolean saveToFile){
        notifyTaskListener(ITaskElementEventListener.TASK_ADD, task, ITaskElementEventListener.TASK_MODIFY_ELSE);
        return true;
    }

    public static boolean add1Task(TaskCombination task, boolean saveToFile) {
        if (taskCombinationList == null)
            taskCombinationList = new ArrayList<TaskCombination>();
        if (taskCombinationList.contains(task)) {
            return modify1Task(task, ITaskElementEventListener.TASK_MODIFY_ELSE);
        } else {
            taskCombinationList.add(task);
            notifyTaskListener(ITaskElementEventListener.TASK_ADD, task, ITaskElementEventListener.TASK_MODIFY_ELSE);
            return true;
        }
    }

    public static boolean display1Task(TaskElement task, int displayType) {
        notifyTaskListener(ITaskElementEventListener.TASK_DISPLAY, task, ITaskElementEventListener.TASK_MODIFY_ELSE);
        return true;
    }

    public static boolean modify1Task(TaskCombination task, int modify_type) {
        if (!taskCombinationList.contains(task))
            taskCombinationList.add(task);
        notifyTaskListener(ITaskElementEventListener.TASK_MODIFY, task, modify_type);
        return true;
    }

    private static void notifyTaskListener(int taskEventType, TaskElement task, int modify_type) {
        Iterator<ITaskElementEventListener> it = TaskElement.listeners.iterator();
        while (it.hasNext()) {
            ITaskElementEventListener listener = it.next();
            if (taskEventType == ITaskElementEventListener.TASK_ADD)
                listener.onTaskAdded(task);
            else if (taskEventType == ITaskElementEventListener.TASK_DEL)
                listener.onTaskDeleted(task);
            else if (taskEventType == ITaskElementEventListener.TASK_DISPLAY)
                listener.onTaskToDisplay(task);
            else if (taskEventType == ITaskElementEventListener.TASK_MODIFY)
                listener.onTaskModified(task, modify_type);
        }
    }

    private static void notifyTaskListener(int taskEventType, TaskCombination task, int modify_type) {
        Iterator<ITaskElementEventListener> it = TaskElement.listeners.iterator();
        while (it.hasNext()) {
            ITaskElementEventListener listener = it.next();
            if (taskEventType == ITaskElementEventListener.TASK_ADD)
                listener.onTaskAdded(task);
            else if (taskEventType == ITaskElementEventListener.TASK_DEL)
                listener.onTaskDeleted(task);
            else if (taskEventType == ITaskElementEventListener.TASK_DISPLAY)
                listener.onTaskToDisplay(task);
            else if (taskEventType == ITaskElementEventListener.TASK_MODIFY)
                listener.onTaskModified(task, modify_type);
        }
    }

}
