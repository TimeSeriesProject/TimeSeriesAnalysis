package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import cn.InstFS.wkr.NetworkMining.Miner.MinerFactorySettings;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.PathMinerFactory;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import org.apache.commons.math3.analysis.function.Min;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arbor vlinyq@gmail.com
 * @date 2016/7/4
 */
public class DialogSettings extends JDialog {
    private MinerFactorySettings settings;

    private JPanel contentPane = new JPanel();
    private JPanel topPane = new JPanel();
    private JPanel bottomPane = new JPanel();
    private JButton buttonSave = new JButton("保存配置");
    private JButton buttonCancel = new JButton("取消");
    private JButton buttonPath = new JButton("选择路径");
    private JTextField dataPath = new JTextField();
    private JLabel labelGranularity = new JLabel("时间粒度");
    private JLabel labelTaskRange = new JLabel("任务范围");
    private JTextField fieldGranularity = new JTextField();
    private JTextField fieldTaskRange = new JTextField();

    private JLabel labelMiningObject = new JLabel("挖掘对象");
    private List<JCheckBox> objectCheckBoxes = new ArrayList<>();

    public DialogSettings(MinerFactorySettings factorySettings, String title){
        this.settings = factorySettings;
        this.settings.setModified(false);
        this.setTitle(title);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSave);
        initLayout();
        addEventListener();
        setData(factorySettings);

    }

    private void initLayout(){
        topPane.setLayout(new GridLayout(0,2,10,5));
        topPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        topPane.add(buttonPath);
        topPane.add(dataPath);
        topPane.add(labelGranularity);
        topPane.add(fieldGranularity);
        topPane.add(labelTaskRange);
        fieldTaskRange.setEditable(false);
        topPane.add(fieldTaskRange);

        topPane.add(labelMiningObject);
        JPanel cbPane = addMiningObjectCB(settings.getMiningObjectList());
        topPane.add(cbPane);


        contentPane.setLayout(new BorderLayout());
        contentPane.add(topPane,BorderLayout.NORTH);
        contentPane.add(bottomPane,BorderLayout.SOUTH);

        bottomPane.add(buttonSave);
        bottomPane.add(buttonCancel);
    }

    private JPanel addMiningObjectCB(List<MiningObject> miningObjectList) {
        JPanel pane = new JPanel();
        for (MiningObject i: miningObjectList) {
            JCheckBox cb = new JCheckBox(i.toString());
            if (settings.getMiningObjectsChecked().contains(i))
                cb.setSelected(true);
            pane.add(cb);
            objectCheckBoxes.add(cb);
        }
        return pane;
    }

    private void addEventListener() {
        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        buttonPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                //
                // disable the "All files" option.
                //
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setMultiSelectionEnabled(true);
                //
                if (chooser.showOpenDialog(buttonPath) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getCurrentDirectory(): "
                            + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : "
                            + chooser.getSelectedFile());
                    dataPath.setText(chooser.getSelectedFile().toString());
                    dataPath.setEditable(false);
                } else {
                    System.out.println("No Selection ");
                }
            }
        });
    }

    private void onSave() {
// add your code here
        boolean flag = false;
        for (JCheckBox cb: objectCheckBoxes){
            if (cb.isSelected()) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            JOptionPane.showMessageDialog(null,"挖掘对象至少选择一个");
            return;
        }
        if (isModified(settings)) {
            getData(settings);
            System.out.println("modified");
            settings.setModified(true);
        }
//        factorySettings.setDataPath(dataPath.getText());

        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void setData(MinerFactorySettings data) {
        dataPath.setText(data.getDataPath());
        fieldGranularity.setText(data.getGranularity());
        fieldTaskRange.setText(data.getTaskRange().toString());
    }

    public void getData(MinerFactorySettings data) {
        data.setDataPath(dataPath.getText());
        data.setGranularity(fieldGranularity.getText());
        List<MiningObject> objectList = settings.getMiningObjectsChecked();
        objectList.clear();
        for (JCheckBox cb: objectCheckBoxes){
            if (cb.isSelected())
                objectList.add(MiningObject.fromString(cb.getText()));
        }
    }

    public boolean isModified(MinerFactorySettings data) {
        if (dataPath.getText() != null ? !dataPath.getText().equals(data.getDataPath()) : data.getDataPath() != null)
            return true;
        if (fieldGranularity.getText() != null ? !fieldGranularity.getText().equals(data.getGranularity()) : data.getGranularity() != null)
            return true;

        List<MiningObject> objectList = settings.getMiningObjectsChecked();
        for (JCheckBox cb: objectCheckBoxes)
                if(cb.isSelected() != objectList.contains(MiningObject.fromString(cb.getText())) )
                    return true;

        /*for (JCheckBox cb: objectCheckBoxes) {
            if (!cb.isSelected())   //未修改时全部checkbox都是selected
                return true;
        }*/
        return false;
    }
    public static void main(String[] args) {
        NetworkMinerFactory.getInstance();

        DialogSettings dialog = new DialogSettings(PathMinerFactory.getInstance(),"test");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
