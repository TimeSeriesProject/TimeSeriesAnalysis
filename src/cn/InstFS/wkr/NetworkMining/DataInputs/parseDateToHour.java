package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.Calendar;
import java.util.Date;

import org.apache.xmlbeans.impl.tool.Diff;

public class parseDateToHour {
	private int hour = 0;
	private Date startDate = new Date();
	public parseDateToHour(Date date) {
		// TODO Auto-generated constructor stub
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 4, 1, 0, 0, 0);
		Date date2 = new Date(cal.getTimeInMillis()/1000 * 1000);
		long diff = date.getTime()-date2.getTime();
		long hours = diff/(1000*3600);
		this.hour =(int) hours;
		this.startDate = date2;
	}

	public parseDateToHour(Date date, Date startDate) {
		long diff = date.getTime()-startDate.getTime();
		long hours = diff/(1000*3600);
		this.hour =(int) hours;
		this.startDate = startDate;
	}
	public int getHour(){
		return hour;
	}
	public void  setHour(int hour) {
		this.hour = hour;
	}
	public Date getOriDate(){
		return startDate;
	}
	public void setStartDate(Date starDate){
		this.startDate = starDate;
	}
}
