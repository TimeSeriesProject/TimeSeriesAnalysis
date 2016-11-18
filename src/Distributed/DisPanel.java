package Distributed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

/**
 * Created by zsc on 2016/10/12.
 * 分布式配置JPanel
 */
public class DisPanel extends JDialog {
    private JPanel panel = new JPanel();
    private JLabel IP = null;
    private JLabel port = null;
    private JTextField IPFiled = null;
    private JTextField portFiled = null;
    private JButton login = null;
    private boolean isDistributed;

    public DisPanel(JFrame parentFrame, String title, boolean isDistributed) {
        super(parentFrame, title, true);
        this.isDistributed = isDistributed;
        try {
            initialize();
        } catch (Exception e) {
            System.out.println("服务端切换为单机");
            e.printStackTrace();
        }
        if (isDistributed) {
            login.setEnabled(false);
            portFiled.setEnabled(false);
        } else {
            login.setEnabled(true);
            portFiled.setEnabled(true);
        }
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addEventListener();
        setContentPane(panel);
        setSize(new Dimension(300, 200));
    }

    private void initialize() throws Exception {
        panel.setLayout(new GridBagLayout());

        //IP
        panel.add(getIP(), new PropertiesGBC(0, 0, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 5));

        //IP框
        panel.add(getIPFiled(), new PropertiesGBC(1, 0, 2, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 5));

        //port
        panel.add(getPort(), new PropertiesGBC(0, 1, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 5));

        //port框
        panel.add(getPortFiled(), new PropertiesGBC(1, 1, 2, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 5));

        //中间空一个面板
        panel.add(new JPanel(), new PropertiesGBC(1, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(1, 0).setInsets(5, 5, 5, 5));

        //login
        panel.add(getLogin(), new PropertiesGBC(2, 2, 1, 1).
                setFill(PropertiesGBC.BOTH).setWeight(0, 0).setInsets(5, 5, 5, 5));
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

    public JTextField getIPFiled() throws Exception {
        if (IPFiled == null) {
            IPFiled = new JTextField(InetAddress.getLocalHost().getHostAddress());
            IPFiled.setEnabled(false);
        }
        return IPFiled;
    }

    public JTextField getPortFiled() {
        if (portFiled == null) {
            portFiled = new JTextField(DisParams.getPort());
        }
        return portFiled;
    }

    public JButton getLogin() {
        if (login == null) {
            login = new JButton("确认");

            login.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login.setEnabled(false);
                    portFiled.setEnabled(false);
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    Server.getInstance().setPort(portFiled.getText().trim());
                                    DisParams.setPort(portFiled.getText().trim());//配置更改，保存
                                    new Thread(Server.getStartInstance()).start();
                                }
                            };
                            new Thread(runnable).start();
                        }
                    });
                    onSave();
                }
            });
        }
        return login;
    }

    private void onSave() {
        dispose();
        isDistributed = true;
    }

    private void addEventListener() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public boolean getIsDis() {
        return isDistributed;
    }
}

