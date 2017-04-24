package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

/**
 * @author Arbor vlinyq@gmail.com
 * @version 2016/9/13
 */
import common.Logger;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.util.Random;

public class TaskProgressBar extends JPanel
        implements ActionListener,
        PropertyChangeListener {

    private JProgressBar progressBar;
    //private JButton startButton;
    private JTextArea taskOutput;
    private Task task;
    private TaskProgress taskProgress;

    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            taskProgress = TaskProgress.getInstance();
            Random random = new Random();
            int taskNum = taskProgress.getTaskComNum();
            Logger.log("总任务数", String.valueOf(taskNum));
            progressBar.setMaximum(taskNum);
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            while (progress < taskNum) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(200));
                } catch (InterruptedException ignore) {}
                progress = taskProgress.getTaskComplete();
                setProgress(Math.min(100*progress/taskNum, 100));
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            //startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            if (taskProgress.getErrTaskList().size()!=0) {
                for (String errTask: taskProgress.getErrTaskList()) {
                    taskOutput.append("ERROR TASK:"+ errTask + "\n");
                }
            }

            taskOutput.append("Done!\n");
            taskProgress.clear();
        }
    }

    public TaskProgressBar() {
        super(new BorderLayout());

        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("读取数据中...");

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
//        panel.add(startButton);
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));



    }

    public void startTask() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
        Logger.log("===============================================================");
        Logger.log("启动挖掘进度显示");
    }

    public void clearBar() {
        progressBar.setValue(0);
    }

    public void setBarString(String s) {
        progressBar.setString(s);
    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
//        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            int taskComplete = taskProgress.getTaskComplete();
            progressBar.setValue(taskComplete);
            progressBar.setString(taskProgress.getPhase() +" " + String.format("%d%%", progress));
//            progressBar.setString(taskProgress.getPhase() +" " + String.format("%d%%", 100 * progress / taskProgress.getTaskComNum()));
            taskOutput.append(String.format(
                    "Completed %d/%d of task.\n", taskComplete, taskProgress.getTaskComNum()));
        }
    }


    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TaskProgressBar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new TaskProgressBar();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
