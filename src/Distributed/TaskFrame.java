package Distributed;

import javax.swing.*;
import java.awt.*;

/**
 * Created by zsc on 2016/10/9.
 * @Deprecated
 */
public class TaskFrame extends JFrame {
    private final TaskPanel taskPanel = new TaskPanel();
    public static void main(String[] args) {
        new TaskFrame().init();
    }

    public void init(){
        JFrame.setDefaultLookAndFeelDecorated(true);

        this.setTitle("任务挖掘进度");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(400, 300));
        this.add(taskPanel);
        this.setResizable(true);
        this.setVisible(true);
    }
}
