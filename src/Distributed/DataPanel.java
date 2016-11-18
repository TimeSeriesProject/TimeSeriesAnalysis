package Distributed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by zsc on 2016/6/30.
 * 客户端JPanel
 */
public class DataPanel extends JPanel {
    private Client client = null;

    private String IPStr = null;
    private int portNum = 0;
    private String filePath = null;
    private String outPath = null;

    private JLabel IP = null;
    private JLabel port = null;
    private JButton pcapFolder = null;
    private JButton fileFolder = null;
    private JTextField pcapFolderFiled = null;
    private JTextField fileFolderFiled = null;
    private JTextField IPFiled = null;
    private JTextField portFiled = null;
    private JButton login = null;
    private JLabel log = null;
    private static JTextArea logContent = null;
    private JScrollPane logContentJScrollPane = null;

    public DataPanel() {
        initialize();
    }

    private void initialize() {
        this.setBorder(BorderFactory.createTitledBorder("客户端"));

        this.setLayout(new GridBagLayout());
        getLogContent().setEditable(false);

        //IP
        this.add(getIP(), new PropertiesGBC(0, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //IP框
        this.add(getIPFiled(), new PropertiesGBC(1, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));

        //pcap框
        this.add(getPcapFolderFiled(), new PropertiesGBC(2, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 10, 5, 0));

        //pcap
        this.add(getPcapFolder(), new PropertiesGBC(3, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //port
        this.add(getPort(), new PropertiesGBC(0, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //port框
        this.add(getPortFiled(), new PropertiesGBC(1, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 0));

        //file框
        this.add(getFileFolderFiled(), new PropertiesGBC(2, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 10, 5, 0));

        //fileButton
        this.add(getFileFolder(), new PropertiesGBC(3, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //login
        this.add(getLogin(), new PropertiesGBC(3, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //log
        this.add(getLog(), new PropertiesGBC(0, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //log内容
        this.add(getJScrollPane(), new PropertiesGBC(0, 3, 4, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 1).setInsets(5, 5, 5, 0));

    }

    public JLabel getIP() {
        if (IP == null) {
            IP = new JLabel();
            IP.setText("IP地址:");
        }
        return IP;
    }

    public JLabel getPort() {
        if (port == null) {
            port = new JLabel();
            port.setText("端口号:");
        }
        return port;
    }

    public JButton getPcapFolder() {
        if (pcapFolder == null) {
            pcapFolder = new JButton();
            pcapFolder.setText("pcap文件目录");

            pcapFolder.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    jfc.setAcceptAllFileFilterUsed(false);
                    if (jfc.showOpenDialog(DataPanel.this) == JFileChooser.APPROVE_OPTION) {
                        pcapFolderFiled.setText(jfc.getSelectedFile().getAbsolutePath());
                    }
                }
            });
        }
        return pcapFolder;
    }

    public JButton getFileFolder() {
        if (fileFolder == null) {
            fileFolder = new JButton();
            fileFolder.setText("存储目录");

            fileFolder.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    jfc.setAcceptAllFileFilterUsed(false);
                    if (jfc.showSaveDialog(DataPanel.this) == JFileChooser.APPROVE_OPTION) {
                        fileFolderFiled.setText(jfc.getSelectedFile().getAbsolutePath());
                    }
                }
            });
        }
        return fileFolder;
    }

    public JTextField getIPFiled() {
        if (IPFiled == null) {
            IPFiled = new JTextField(DisParams.getIp());
        }
        return IPFiled;
    }

    public JTextField getPortFiled() {
        if (portFiled == null) {
            portFiled = new JTextField(DisParams.getClientPort());
        }
        return portFiled;
    }

    public JTextField getPcapFolderFiled() {
        if (pcapFolderFiled == null) {
            pcapFolderFiled = new JTextField(DisParams.getCpcapPath());
        }
        return pcapFolderFiled;
    }

    public JTextField getFileFolderFiled() {
        if (fileFolderFiled == null) {
            fileFolderFiled = new JTextField(DisParams.getcOutputPath());
        }
        return fileFolderFiled;
    }

    //服务端退出时调用
    public void LoginQuit(){
        if (login.getText().equals("退出")) {
            login.setText("连接");
            getIPFiled().setEditable(true);
            getPortFiled().setEditable(true);
            getPcapFolderFiled().setEditable(true);
            getFileFolderFiled().setEditable(true);
            client.close();
        }
    }

    public JButton getLogin() {
        if (login == null) {
            login = new JButton();
            login.setText("连接");

            login.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String loginText = login.getText();
                    if (loginText.equals("连接")) {
                        login.setText("退出");
                        IPStr = IPFiled.getText().trim();
                        portNum = Integer.parseInt(portFiled.getText().trim());
                        filePath = pcapFolderFiled.getText().trim();
                        outPath = fileFolderFiled.getText().trim();
                        DisParams.setIp(IPFiled.getText().trim());
                        DisParams.setClientPort(portFiled.getText().trim());
                        DisParams.setCpcapPath(pcapFolderFiled.getText().trim());
                        DisParams.setcOutputPath(fileFolderFiled.getText().trim());
                        client = new Client(DataPanel.this, IPStr, portNum, filePath, outPath);
                        getIPFiled().setEditable(false);
                        getPortFiled().setEditable(false);
                        getPcapFolderFiled().setEditable(false);
                        getFileFolderFiled().setEditable(false);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                //另起一个线程用来进行尝试连接，否则swing卡住
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("进入......");
                                        client.startConnect();
                                        if (client.isConnected()) {
                                            new Thread(client.new ReceiveServerMsg()).start();
                                            new Thread(client.new ExecuteTask()).start();
                                        }
                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                    }

                    if (loginText.equals("退出")) {
                        login.setText("连接");
                        getIPFiled().setEditable(true);
                        getPortFiled().setEditable(true);
                        getPcapFolderFiled().setEditable(true);
                        getFileFolderFiled().setEditable(true);
                        client.close();
                        if (!client.isConnected()) {
                            sendLog("客户端退出...");
                        }
                    }
                }
            });
        }
        return login;
    }

    public JLabel getLog() {
        if (log == null) {
            log = new JLabel();
            log.setText("日志:");
        }
        return log;
    }

    public JTextArea getLogContent() {
        if (logContent == null) {
            logContent = new JTextArea();
        }
        return logContent;
    }

    public JScrollPane getJScrollPane() {
        logContentJScrollPane = new JScrollPane(getLogContent());
        return logContentJScrollPane;
    }

    public void sendLog(String str) {
        getLogContent().append(str + "\n");
        getLogContent().setCaretPosition(getLogContent().getText().length());//滚动条自动滚动
    }

}
