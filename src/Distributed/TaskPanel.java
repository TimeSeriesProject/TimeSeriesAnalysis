package Distributed;

import javax.swing.*;
import java.awt.*;

/**
 * Created by zsc on 2016/10/8.
 */
public class TaskPanel extends JPanel {
    private static Server server = Server.getInstance();

    private JProgressBar progressBar = null;
    private JTextArea taskOutput = null;
    private JScrollPane textJScrollPane = null;

    public TaskPanel() {
        initialize();
    }

    private void initialize() {
        server.initTask(TaskPanel.this);
//        this.setBorder(BorderFactory.createTitledBorder("任务挖掘进度"));

        this.setLayout(new GridBagLayout());
        getLog().setEditable(false);


        //进度条，设置高度ipad
        this.add(getBar(), new PropertiesGBC(0, 0, 1, 1).setIpad(400, 10).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 5));

        //日志
        this.add(getJScrollPane(), new PropertiesGBC(0, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 1).setInsets(5, 5, 5, 5));

    }

    public JProgressBar getBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setString("读取数据中...");
        }
        return progressBar;
    }

    public JTextArea getLog() {
        if (taskOutput == null) {
            taskOutput = new JTextArea();
        }
        return taskOutput;
    }

    public JScrollPane getJScrollPane() {
        textJScrollPane = new JScrollPane(getLog());
        return textJScrollPane;
    }
}
