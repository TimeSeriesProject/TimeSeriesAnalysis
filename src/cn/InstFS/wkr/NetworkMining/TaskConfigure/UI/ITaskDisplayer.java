package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public interface ITaskDisplayer {
	static int DISPLAY_CONFIG = 1;
	static int DISPLAY_RESULTS = 2;
	static int DISPLAY_BOTH = 3;
	
	int getDisplayType();
	void displayTask(TaskElement task);
}
