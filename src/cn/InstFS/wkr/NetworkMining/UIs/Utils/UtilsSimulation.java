package cn.InstFS.wkr.NetworkMining.UIs.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.DataInputs.OracleUtils;
import cn.InstFS.wkr.NetworkMining.UIs.SimulationUIs.PanelControlSimulationTime;

public enum UtilsSimulation {
	
	instance;
	
	private String configFile = "./configs/config_simulations.xml";	
	
	private boolean isPaused = true;
	private boolean useSimulatedData = false;
	private Date curTime ;
	private int forcastWindowSizeInSeconds = 5;
	private double maxDaysToRead = 1;
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Timer timer;
	
	public void Init(){
		setCurTime(new Date());
		loadSimulationSettings();
		stopTimer();
	}
	private void startTimer(){
		stopTimer();
		timer = new Timer();
		timer.scheduleAtFixedRate(new SimulationTimerTask(), 0, getForcastWindowSizeInSeconds() * 1000);
	}
	private void stopTimer(){
		if (timer != null)
			timer.cancel();
		timer = null;
	}
	private Properties toProperties(){ 
		Properties props = new Properties();
		props.put("useSimulatedData", ""+isUseSimulatedData());
		props.put("curTime", sdf.format(getCurTime()));
		props.put("forcastWindowSizeInSeconds", ""+getForcastWindowSizeInSeconds());
		props.put("maxDaysToRead", ""+getMaxDaysToRead());
		return props;
	}
	private void fromProperties(Properties props){
		setUseSimulatedData(new Boolean(props.getProperty("useSimulatedData")));
		try {
			setCurTime(sdf.parse(props.getProperty("curTime")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		setForcastWindowSizeInSeconds(Integer.parseInt(props.getProperty("forcastWindowSizeInSeconds")));
		setMaxDaysToRead(Double.parseDouble(props.getProperty("maxDaysToRead")));
	}
	public void saveSimulationSettings(){
		File f = new File(configFile);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(configFile);
			Properties props = toProperties();
			props.storeToXML(fos, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (fos != null)
				try{fos.close();}catch(Exception ee){}
		}
	}
	public void loadSimulationSettings(){
		Properties props = new Properties();
		File f = new File(configFile);
		FileInputStream fis = null;
		if (!f.exists()){
			saveSimulationSettings();
		}
		try {
			props = new Properties();
			fis = new FileInputStream(f);
			props.loadFromXML(fis);
			fromProperties(props);		
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (fis != null)
				try{fis.close();}catch(Exception ee){}
		}
		
	}
		
	public Date getStartTime(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurTime());
		cal.add(Calendar.SECOND, - (int)(getMaxDaysToRead() * 24 * 60 *60));
		return cal.getTime();
	}
	public Date getCurTime(){
		return curTime;
	}	
	public Date getCurrentStart(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurTime());
		cal.add(Calendar.SECOND, - getForcastWindowSizeInSeconds());
		return cal.getTime();
	}
	public Date getCurrentEnd(){
		return getCurTime();
	}
	public Date getFutureStart(){
		return getCurTime();
	}
	public Date getFutureEnd(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurTime());
		cal.add(Calendar.SECOND, getForcastWindowSizeInSeconds());
		return cal.getTime();
	}
	
	public void forward(){
		if (isPaused)
			return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurTime());
		cal.add(Calendar.SECOND, getForcastWindowSizeInSeconds());
		setCurTime(cal.getTime());
		saveSimulationSettings();
		PanelControlSimulationTime.instance.updateSimulationParams();
	}
	public void backward(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurTime());
		cal.add(Calendar.SECOND, - getForcastWindowSizeInSeconds());
		setCurTime(cal.getTime());
		saveSimulationSettings();
	}
	public int getForcastWindowSizeInSeconds() {
		return forcastWindowSizeInSeconds;
	}
	public void setForcastWindowSizeInSeconds(int forcastWindowSizeInSeconds) {
		this.forcastWindowSizeInSeconds = forcastWindowSizeInSeconds;
	}
	public double getMaxDaysToRead() {
		return maxDaysToRead;
	}
	public void setMaxDaysToRead(double maxDaysToRead) {
		this.maxDaysToRead = maxDaysToRead;
	}
	public boolean isUseSimulatedData() {
		return useSimulatedData;
	}
	public void setUseSimulatedData(boolean useSimulatedData) {
		this.useSimulatedData = useSimulatedData;
	}
	public void setCurTime(Date curTime) {
		this.curTime = curTime;
	}
	public boolean isPaused() {
		return isPaused;
	}
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
		if (!isPaused)
			startTimer();
	}
	
}
class SimulationTimerTask extends TimerTask{

	@Override
	public void run() {
		UtilsSimulation.instance.forward();		
	}
	
}
