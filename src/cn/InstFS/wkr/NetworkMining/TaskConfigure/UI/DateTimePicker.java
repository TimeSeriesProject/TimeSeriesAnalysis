package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Arbor vlinyq@gmail.com
 * @version  2016/8/2
 */
public class DateTimePicker extends JPanel{
    private JDateChooser dateChooser;
    private JSpinner timeSpinner;
    public DateTimePicker() {
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(editor);
        timeSpinner.setValue(new Date());
        add(dateChooser);
        add(timeSpinner);
        dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println(getDateTime());
            }
        });
        timeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println(getDateTime());
            }
        });
    }

    public Date getDateTime() {
        Date dateTime = null;
        Date date = dateChooser.getDate();
        Date time = (Date) timeSpinner.getValue();

        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
        String timeString = new SimpleDateFormat("HH-mm").format(((Date) timeSpinner.getValue()).getTime());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        try {
            dateTime = sf.parse(dateString+" "+timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    private void writeObject(ObjectOutputStream s){
        try {
            s.defaultWriteObject();
            s.writeObject(timeSpinner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        JFrame jf = new JFrame();
        DateTimePicker date = new DateTimePicker();
        jf.add(date);
//        date.getDateTime();
        jf.setVisible(true);

        try {
            FileOutputStream fileOut = new FileOutputStream("datepicker");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(date);
            out.close();
            fileOut.close();
            System.out.println("序列化保存");
        } catch (IOException i) {
            i.printStackTrace();
        }

    }
}

class JChooser extends JDateChooser implements Serializable{
    public JChooser() {
        super();
    }

    private void writeObject(final ObjectOutputStream s){

    }
}
