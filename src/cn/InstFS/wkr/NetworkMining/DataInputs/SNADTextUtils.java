package cn.InstFS.wkr.NetworkMining.DataInputs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * 
 * @author aichangqing
 *
 */
public class SNADTextUtils {

	private String textPath="./data/mergeNode/net-1.csv";
	
	
	public SNADTextUtils(String path){
		this.textPath=path;
	}
	public SNADTextUtils(){
		
	}
	public String getTextPath() {
		return textPath;
	}
	public void setTextPath(String textPath) {
		this.textPath = textPath;
	}
	//得到指定节点的数据
	public DataItems readSingleFileData(String whichNode)
	{
		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line = null;
		try {
			FileInputStream is = new FileInputStream(new File(textPath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			reader.readLine();
			while((line = reader.readLine()) != null)
			{
				String[] values = line.split(","); 
				String srcIP = values[1];
				if(srcIP.compareTo(whichNode) == 0)
				{
					lastYear.add(Calendar.HOUR_OF_DAY, 1);
					dataItems.add1Data(lastYear.getTime(), values[3]);
				}
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
	}
	public DataItems readMutilFileData(String whichNode)
	{
		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line = null;
		try {
			File dirFile = new File(textPath);
			for (int i = 0; i < dirFile.list().length; i++) {
				String fileName = dirFile.list()[i];

				if (fileName.endsWith(".csv")) {
					System.out.println(fileName);
					FileInputStream is = new FileInputStream(new File(textPath+"\\"+fileName));
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					reader.readLine();
					while((line = reader.readLine()) != null)
					{
						String[] values = line.split(","); 
						String srcIP = values[1];
						if(srcIP.compareTo(whichNode) == 0)
						{
							lastYear.add(Calendar.HOUR_OF_DAY, 1);
							dataItems.add1Data(lastYear.getTime(), values[3]);
						}
						
					}
					reader.close();
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
	}
	public DataItems readInput(){
		
		DataItems dataItems = new DataItems();
		Calendar lastYear = Calendar.getInstance();
		lastYear.set(2014, 9, 1, 0, 0, 0);
		String line = null;
		try {
			FileInputStream is = new FileInputStream(new File(textPath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			reader.readLine();
			while((line=reader.readLine()) != null){
				String[] values = line.split(","); 
				lastYear.add(Calendar.HOUR_OF_DAY, 1);
				dataItems.add1Data(lastYear.getTime(), values[3]);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataItems;
		
	}
}
