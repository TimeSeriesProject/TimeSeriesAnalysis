package Distributed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by zsc on 2016/8/24.
 * 分布式pcap解析服务端JPanel
 */

public class PcapPanel extends JPanel {
    private JButton inJB = null;
    private JButton outJB = null;
    private JTextField inText = null;
    private JTextField outText = null;
    private JButton beginDig = null;
    private JLabel jLabel = null;
    private JProgressBar bar = null;

    //解析全部or部分
    private boolean parseAll = DisParams.isParseAll();
    private JRadioButton parseAllBtn = null;
    private JRadioButton parseNewBtn = null;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JLabel selectFile = new JLabel("选择解析文件：");

    public PcapPanel() {
        initialize();
    }

    private void initialize() {
        this.setBorder(BorderFactory.createTitledBorder("pcap解析服务端"));

        this.setLayout(new GridBagLayout());

        //ButtonGroup 只add即可，最终JPanel添加选择组件
        buttonGroup.add(getParseAllBtn());
        buttonGroup.add(getParseNewBtn());

        //输入路径
        this.add(getInText(), new PropertiesGBC(0, 0, 3, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));

        //选择输入按钮
        this.add(getInJB(), new PropertiesGBC(3, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //输出路径
        this.add(getOutText(), new PropertiesGBC(0, 1, 3, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));

        //选择输出按钮
        this.add(getOutJB(), new PropertiesGBC(3, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //开始解析
        this.add(getBeginDig(), new PropertiesGBC(3, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //选择按钮
        this.add(getSelectFile(), new PropertiesGBC(0, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //选择解析所有文件
        this.add(getParseAllBtn(), new PropertiesGBC(1, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //选择解析新添文件
        this.add(getParseNewBtn(), new PropertiesGBC(2, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //中间空一个面板
        this.add(new JPanel(), new PropertiesGBC(0, 3, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 1).setInsets(5, 5, 5, 0));

        //label
        this.add(getjLabel(), new PropertiesGBC(0, 4, 4, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //进度条，设置高度ipad
        this.add(getBar(), new PropertiesGBC(0, 5, 4, 1).setIpad(400, 10).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));
    }

    public JButton getInJB() {
        if (inJB == null) {
            inJB = new JButton();
            inJB.setText("选择pcap文件目录");
            inJB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    jfc.setAcceptAllFileFilterUsed(false);
                    if (jfc.showOpenDialog(PcapPanel.this) == JFileChooser.APPROVE_OPTION) {
                        inText.setText(jfc.getSelectedFile().getAbsolutePath());
                    }
                }
            });
        }
        return inJB;
    }

    public JButton getOutJB() {
        if (outJB == null) {
            outJB = new JButton();
            outJB.setText("选择存储目录");
            outJB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    jfc.setAcceptAllFileFilterUsed(false);
                    if (jfc.showSaveDialog(PcapPanel.this) == JFileChooser.APPROVE_OPTION) {
                        outText.setText(jfc.getSelectedFile().getAbsolutePath());
                    }
                }
            });
        }
        return outJB;
    }

    public JTextField getInText() {
        if (inText == null) {
            inText = new JTextField();
            inText.setText(DisParams.getPcapPathDis());
        }
        return inText;
    }

    public JTextField getOutText() {
        if (outText == null) {
            outText = new JTextField();
            outText.setText(DisParams.getOutputPathDis());
        }
        return outText;
    }

    public JButton getBeginDig() {
        if (beginDig == null) {
            beginDig = new JButton();
            beginDig.setText("开始解析");
            beginDig.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (inText.getText().trim().compareTo("") == 0 || inText.getText().trim() == null
                            || outText.getText().trim().compareTo("") == 0 || outText.getText().trim() == null) {
                        JOptionPane.showMessageDialog(null, "请先选择解析文件目录和存放路径!");
                        System.out.println("请先选择解析文件目录和存放路径");
                        System.out.println("in: " + inText.getText() + "  out: " + outText.getText());
                    } else {
                        beginDig.setEnabled(false);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                //另起一个线程用来进行尝试连接，否则swing卡住
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        long a = System.currentTimeMillis();
//                                        System.out.println("进入......");
                                        Server.getInstance().initPcap(PcapPanel.this, inText.getText().trim(), outText.getText().trim());
                                        DisParams.setPcapPathDis(inText.getText().trim());
                                        DisParams.setOutputPathDis(outText.getText().trim());
                                        DisParams.setParseAll(parseAll);
                                        Server.getInstance().getPcapTaskNum(inText.getText().trim(), parseAll);
//                                        System.out.println("longlist任务个数： " + Server.getInstance().getTasks().size());
                                        for (Map.Entry<Long, ArrayList<File>> entry : Server.getInstance().getTasks().entrySet()) {
//                                            for (int i = 0; i < entry.getValue().size(); i++) {
//                                                System.out.println("time: " + entry.getKey());
//                                                System.out.println("list: " + entry.getValue().get(i));
//                                            }
                                            Server.getInstance().genTasks(entry.getKey(), entry.getValue());
                                            Server.getInstance().awakePcap();
                                            Server.getInstance().setIsPcapRunning(true);
                                            //等待结束
                                            Server.getInstance().awaitList();
                                            System.out.println("结束等待，进行下一次");
                                        }
                                        long b = System.currentTimeMillis();
                                        System.out.println("pcap总时间： " + (b - a));

                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                    }
                }
            });
        }
        return beginDig;
    }

    public JLabel getjLabel() {
        if (jLabel == null) {
            jLabel = new JLabel();
            jLabel.setText("未开始");
        }
        return jLabel;
    }

    public JProgressBar getBar() {
        if (bar == null) {
            bar = new JProgressBar(JProgressBar.HORIZONTAL);
            bar.setStringPainted(true);
        }
        return bar;
    }

    public JRadioButton getParseAllBtn() {
        if (parseAllBtn == null) {
            parseAllBtn = new JRadioButton("解析全部文件", DisParams.isParseAll());
            parseAllBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parseAll = true;
                }
            });
        }
        return parseAllBtn;
    }

    public JRadioButton getParseNewBtn() {
        if (parseNewBtn == null) {
            parseNewBtn = new JRadioButton("解析新添加文件", !DisParams.isParseAll());
            parseNewBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parseAll = false;
                }
            });
        }
        return parseNewBtn;
    }

    public JLabel getSelectFile() {
        if (selectFile == null) {
            selectFile = new JLabel("选择解析文件：");
        }
        return selectFile;
    }
}


