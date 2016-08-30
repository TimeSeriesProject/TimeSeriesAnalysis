package Distributed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by zsc on 2016/6/30.
 */
public class DataPanel extends JPanel {
    private Client client = null;

    private String IPStr = null;
    private int portNum = 0;

    private JLabel IP = null;
    private JLabel port = null;
    private JTextField IPAddress = null;
    private JTextField portAddress = null;
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
        this.add(getIPAddress(), new PropertiesGBC(1, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 0, 5, 0));

        //port
        this.add(getPort(), new PropertiesGBC(2, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 10, 5, 0));

        //port框
        this.add(getPortAddress(), new PropertiesGBC(3, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 0, 5, 0));

        //login
        this.add(getLogin(), new PropertiesGBC(4, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //log
        this.add(getLog(), new PropertiesGBC(0, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 0));

        //log内容
        this.add(getJScrollPane(), new PropertiesGBC(0, 2, 5, 1).
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

    public JTextField getIPAddress() {
        if (IPAddress == null) {
            IPAddress = new JTextField("127.0.0.1");
        }
        return IPAddress;
    }

    public JTextField getPortAddress() {
        if (portAddress == null) {
            portAddress = new JTextField("7777");
        }
        return portAddress;
    }

    //服务端退出时调用
    public void LoginQuit(){
        if (login.getText().equals("退出")) {
            login.setText("连接");
            getIPAddress().setEditable(true);
            getPortAddress().setEditable(true);
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
                        IPStr = IPAddress.getText();
                        portNum = Integer.parseInt(portAddress.getText());
                        client = new Client(DataPanel.this, IPStr, portNum);
                        getIPAddress().setEditable(false);
                        getPortAddress().setEditable(false);
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
                                        }
                                    }
                                };
                                new Thread(runnable).start();
                            }
                        });
                    }

                    if (loginText.equals("退出")) {
                        login.setText("连接");
                        getIPAddress().setEditable(true);
                        getPortAddress().setEditable(true);
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
