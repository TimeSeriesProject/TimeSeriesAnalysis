package Distributed;

import javax.swing.*;
import java.awt.*;

/**
 * Created by zsc on 2016/8/24.
 * @Deprecated
 */
public class PcapFrame extends JFrame{
    private final PcapPanel pcapPanel = new PcapPanel();
//    public static void main(String[] args) {
//        new PcapFrame().init();
//    }

    public void init(){
        JFrame.setDefaultLookAndFeelDecorated(true);

        this.setTitle("网络规律挖掘解析pcap");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(400, 300));
        this.add(pcapPanel);
        this.setResizable(true);
        this.setVisible(true);
    }
}
