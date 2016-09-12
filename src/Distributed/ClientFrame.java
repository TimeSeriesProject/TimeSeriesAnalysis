package Distributed;

import javax.swing.*;
import java.awt.*;

/**
 * Created by zsc on 2016/6/30.
 */
public class ClientFrame extends JFrame{
    private final DataPanel dataPanel = new DataPanel();
    public static void main(String[] args) {
        new ClientFrame().init();
    }

    public void init(){
        JFrame.setDefaultLookAndFeelDecorated(true);

        this.setTitle("网络规律挖掘模拟器");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(600, 500));
        this.add(dataPanel);
        this.setResizable(true);
        this.setVisible(true);
    }
}
