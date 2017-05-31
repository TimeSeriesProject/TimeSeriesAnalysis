package cn.InstFS.wkr.NetworkMining.Miner.Factory;

import Distributed.TaskCombinationList;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AlgorithmsChooser;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AlgorithmsManager;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import common.ErrorLogger;
import common.Logger;


import java.io.File;
import java.util.*;

/**
 * Created by zsc on 2016/6/2.
 */
public class SingleNodeOrNodePairMinerFactoryDis extends MinerFactorySettings {
    private static SingleNodeOrNodePairMinerFactoryDis inst;
    private static SingleNodeOrNodePairMinerFactoryDis pairInst;
    public static boolean isMining=false;

    public String dataPath;
    public String rootPath;

    private MiningObject miningObject;
    private TaskRange taskRange = TaskRange.SingleNodeRange;
    private MiningMethod method;

    private SingleNodeOrNodePairMinerFactoryDis(String minertype){
        super(minertype);
        dataPath = GlobalConfig.getInstance().getDataPath();
        rootPath = GlobalConfig.getInstance().getDataPath();
        List<MiningObject> miningObjectList = this.getMiningObjectList();
        miningObjectList.add(MiningObject.MiningObject_Times);
        miningObjectList.add(MiningObject.MiningObject_Traffic);
        if (minertype.equals("节点规律挖掘"))
            miningObjectList.add(MiningObject.MiningObject_NodeDisapearEmerge);

        List<MiningObject> miningObjectCheck = this.getMiningObjectsChecked();
        miningObjectCheck.addAll(miningObjectList);
    }

    public static SingleNodeOrNodePairMinerFactoryDis getInstance(){
        if(inst==null){
            isMining=false;
            inst=new SingleNodeOrNodePairMinerFactoryDis("节点规律挖掘");
        }
        return inst;
    }

    public static SingleNodeOrNodePairMinerFactoryDis getPairInstance() {
        if(pairInst==null){
            isMining=false;
            pairInst=new SingleNodeOrNodePairMinerFactoryDis("链路规律挖掘");
            pairInst.setTaskRange(TaskRange.NodePairRange);
        }
        return pairInst;
    }

    public MiningMethod getMethod() {
        return method;
    }
    public void setMethod(MiningMethod method) {
        this.method = method;
    }
    public TaskRange getTaskRange() {
        return taskRange;
    }
    public void setTaskRange(TaskRange taskRange) {
        this.taskRange = taskRange;
    }
    public MiningObject getMiningObject() {
        return miningObject;
    }
    public void setMiningObject(MiningObject miningObject) {
        this.miningObject = miningObject;
    }
    public String getDataPath() {
        return dataPath;
    }
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    public void reset(){
        isMining=false;
    }
    public void detect(){

        if(isMining)
            return;

        isMining=true;
        File dataDirectory = null;
        if(MiningObject.MiningObject_NodeDisapearEmerge.toString().equals(miningObject.toString()))
        {
            dataDirectory = new File(dataPath + "\\node");
        }
        else{
            dataDirectory = new File(dataPath + "\\traffic");
        }

        nodePairReader reader=new nodePairReader();
//		if(dataDirectory.isFile()){
//			parseFile(dataDirectory,reader);
//		}else{

        Logger.log("挖掘类型", getMinerType());
        Logger.log("挖掘对象", miningObject.toString());
        Logger.log("源数据读取目录", dataDirectory.getPath());
        Logger.log("数据读取起止时间", getStartDate().toString() + "--" + getEndDate().toString());
        Logger.log("数据处理时间粒度", getGranularity()+"s");
        Logger.log("源数据读取开始");

        File[] dataDirs=dataDirectory.listFiles();
        boolean flag = false; // 标记选择的路径是否已到具体节点目录
        for(int i=0;i<dataDirs.length;i++){
            //按天必须是文件夹
            if(dataDirs[i].isDirectory())
                parseFile(dataDirs[i],reader);
            else { // 路径下为文件，而非目录
                flag = true;
                break;
            }
        }

        if (flag) {
            parseFile(dataDirectory, reader);
        }

    }

    private void parseFile(File dataFile,nodePairReader reader){
        String ip=dataFile.getName();//.substring(0, dataFile.getName().lastIndexOf("."));
        //事先读取每一个IP上，每一个协议的DataItems
        //if(ip.equals("10.0.13.2"))
        //System.out.println();
        int granularity= Integer.parseInt(this.getGranularity());
        if(taskRange.toString().equals(TaskRange.SingleNodeRange.toString())){
            HashMap<String, DataItems> rawDataItems=null;
            boolean isNodeDisapearEmerge = false;

            switch (miningObject) {
                case MiningObject_Traffic:
                    //rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
                    //rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(),reader.getTask().getIsReadBetween(),reader.getTask().getDateStart(),reader.getTask().getDateEnd());
                    /**2016/7/14
                     * @author LYH
                     * 用于测试读取时间区间数据，单节点挖掘
                     * **/
//				Calendar cal1 = Calendar.getInstance();
//				Calendar cal2 = Calendar.getInstance();
//				cal1.set(2014, 9, 1, 0, 0, 0);
//				cal2.set(2014,11,20,0,0,0);
                    Date date1 = getStartDate();
                    Date date2 = getEndDate();
                    Logger.log("源数据读取子目录", dataFile.getPath());
                    rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(),true,date1,date2,3600);

                    break;
                case MiningObject_Times:
                    //rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath());
                    //rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath(),reader.getTask().getIsReadBetween(),reader.getTask().getDateStart(),reader.getTask().getDateEnd());
                    /**2016/7/14
                     * @author LYH
                     * 用于测试读取时间区间数据，单节点挖掘
                     * **/
                    Date date3 = getStartDate();
                    Date date4 = getEndDate();
                    Logger.log("源数据读取子目录", dataFile.getPath());
                    rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath(),true,date3,date4,3600);
                    break;
                case MiningObject_NodeDisapearEmerge:
                    Date date5 = getStartDate();
                    Date date6 = getEndDate();
                    Logger.log("源数据读取子目录", dataFile.getPath());
                    rawDataItems = reader.readEachNodeDisapearEmergeDataItems(dataFile.getAbsolutePath(),true,date5,date6,3600);
                    isNodeDisapearEmerge = true;
//				if(ip.compareTo("4") == 0 ||ip.compareTo("3") == 0 ||ip.compareTo("0") == 0 ||
//						ip.compareTo("10") == 0 ||ip.compareTo("6") == 0 ||ip.compareTo("5") == 0 ||
//						ip.compareTo("7") == 0 || ip.compareTo("1") == 0)
//					System.out.println(rawDataItems.get("AllTraffic").getData().toString());
                    break;
                default:
                    break;
            }

            Logger.log("生成挖掘任务集合");
            for(String protocol:rawDataItems.keySet()){
                DataItems dataItems=rawDataItems.get(protocol);
                if(!isDataItemSparse(dataItems) || isNodeDisapearEmerge){
                    TaskCombination taskCombination=new TaskCombination();
                    taskCombination.setTaskRange(taskRange);
                    taskCombination.getTasks().add(generateTask(taskRange, granularity,
                            dataFile, protocol, ip, MiningMethod.MiningMethods_Statistics));
                    taskCombination.getTasks().add(generateTask(taskRange, granularity,
                            dataFile, protocol, ip, MiningMethod.MiningMethods_PeriodicityMining));
                    taskCombination.getTasks().add(generateTask(taskRange, granularity,
                            dataFile,protocol, ip,MiningMethod.MiningMethods_PartialCycle));
                    taskCombination.getTasks().add(generateTask(taskRange, granularity,
                            dataFile, protocol, ip, MiningMethod.MiningMethods_PartialPeriod));
                    taskCombination.getTasks().add(generateTask(taskRange, granularity,
                            dataFile, protocol, ip, MiningMethod.MiningMethods_OutliesMining));
                    taskCombination.getTasks().add(generateTask(taskRange, granularity,
                            dataFile, protocol, ip, MiningMethod.MiningMethods_SequenceMining));
                    taskCombination.getTasks().add(generateTask(taskRange, granularity,
                            dataFile, protocol, ip, MiningMethod.MiningMethods_PredictionMining));
                    taskCombination.setMiningObject(miningObject.toString());
                    taskCombination.setDataItems(dataItems);
                    taskCombination.setProtocol(protocol);
                    taskCombination.setRange(ip);
                    taskCombination.setName();
                    taskCombination.setMinerType(MinerType.MiningType_SinglenodeOrNodePair);
                    TaskCombinationList.addTaskOnly(taskCombination, false);
                    Logger.log("添加TaskCombination", taskCombination.getName());
//                    ErrorLogger.log(ip,"data.size:"+dataItems.data.size()+" time.size:"+dataItems.time.size()+" lenth:"+dataItems.getLength());
                }
            }
        }else if(taskRange.toString().equals(TaskRange.NodePairRange.toString())){
            HashMap<String, Map<String, DataItems>> ipPairRawDataItems=null;
            switch (miningObject) {
                case MiningObject_Traffic:
                    //ipPairRawDataItems=reader.readEachIpPairProtocolTrafficDataItems(dataFile.getAbsolutePath());

                    Date date1 = getStartDate();
                    Date date2 = getEndDate();
                    Logger.log("源数据读取子目录", dataFile.getPath());
                    ipPairRawDataItems=reader.readEachIpPairProtocolTrafficDataItems(dataFile.getAbsolutePath(),true,date1,date2,3600);
                    break;
                case MiningObject_Times:
                    //ipPairRawDataItems=reader.readEachIpPairProtocolTimesDataItems(dataFile.getAbsolutePath());
                    Date date3 = getStartDate();
                    Date date4 = getEndDate();
                    Logger.log("源数据读取子目录", dataFile.getPath());
                    ipPairRawDataItems=reader.readEachIpPairProtocolTimesDataItems(dataFile.getAbsolutePath(),true,date3,date4,3600);
                    break;
                default:
                    break;
            }

            Logger.log("生成挖掘任务集合");
            for(String ipPair:ipPairRawDataItems.keySet()){
                Map<String, DataItems> itemsMap=ipPairRawDataItems.get(ipPair);
                for(String protocol:itemsMap.keySet()){
                    DataItems dataItems=itemsMap.get(protocol);
                    if(!isDataItemSparse(dataItems)){
                        TaskCombination taskCombination=new TaskCombination();
                        taskCombination.setTaskRange(taskRange);
                        taskCombination.getTasks().add(generateTask(taskRange, granularity,
                                dataFile, protocol, ipPair, MiningMethod.MiningMethods_Statistics));
                        taskCombination.getTasks().add(generateTask(taskRange, granularity,
                                dataFile, protocol, ipPair, MiningMethod.MiningMethods_PeriodicityMining));
                        taskCombination.getTasks().add(generateTask(taskRange, granularity,
                                dataFile,protocol, ipPair,MiningMethod.MiningMethods_PartialCycle));
                        taskCombination.getTasks().add(generateTask(taskRange, granularity,
                                dataFile, protocol, ipPair, MiningMethod.MiningMethods_PartialPeriod));
                        taskCombination.getTasks().add(generateTask(taskRange, granularity,
                                dataFile, protocol, ipPair, MiningMethod.MiningMethods_OutliesMining));
                        taskCombination.getTasks().add(generateTask(taskRange, granularity,
                                dataFile, protocol, ipPair, MiningMethod.MiningMethods_SequenceMining));
                        taskCombination.getTasks().add(generateTask(taskRange, granularity,
                                dataFile, protocol, ipPair, MiningMethod.MiningMethods_PredictionMining));
                        taskCombination.setMiningObject(miningObject.toString());
                        taskCombination.setDataItems(dataItems);
                        taskCombination.setProtocol(protocol);
                        taskCombination.setRange(ipPair);
                        taskCombination.setName();
                        taskCombination.setMinerType(MinerType.MiningType_SinglenodeOrNodePair);
                        TaskElement.add1Task(taskCombination, false);
                        Logger.log("添加TaskCombination", taskCombination.getName());
                    }
                }
            }
        }
    }

    public TaskElement generateTask(TaskRange taskRange,int granularity,File dataFile,String protocol,
                                    String ipOrPair,MiningMethod method){
        TaskElement task = new TaskElement();
        task.setDataSource("File");
        task.setGranularity(granularity);
        task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
        task.setSourcePath(dataFile.getPath());
        task.setTaskRange(taskRange);
        task.setRange(ipOrPair.replace('-', ','));
        task.setDiscreteMethod(DiscreteMethod.None);
        task.setMiningMethod(method);

        AlgorithmsChooser chooser = AlgorithmsManager.getInstance().getAlgoChooserFromManager(MinerType.MiningType_SinglenodeOrNodePair, taskRange);
        String name=null;

        switch (method) {
            case MiningMethods_OutliesMining:
                task.setMiningAlgo(chooser.getOmAlgo());
                name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_异常检测_auto";
                task.setTaskName(name);
                task.setComments("挖掘  "+ipOrPair+" 上,协议"+protocol+"的异常");
                break;
            case MiningMethods_PeriodicityMining:
                task.setMiningAlgo(chooser.getPmAlgo());
                name = ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_周期挖掘_auto";
                task.setTaskName(name);
                task.setComments("挖掘  "+ipOrPair+",粒度为"+granularity+"s 的协议"+protocol+"的周期规律");
                break;
            case MiningMethods_SequenceMining:
                name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_auto_频繁模式挖掘";
                task.setTaskName(name);
                task.setComments("挖掘  "+ipOrPair+" 上,协议为"+protocol+"的频繁模式");
                break;
            case MiningMethods_Statistics:
                name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_统计_auto";
                task.setTaskName(name);
                task.setComments("挖掘  "+ipOrPair+" 上,协议"+protocol+"的统计");
                break;
            case MiningMethods_PartialCycle:
                name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_局部周期_auto";
                task.setTaskName(name);
                task.setComments("挖掘  "+ipOrPair+" 上,协议为"+protocol+"的局部周期");
                break;
            case MiningMethods_PartialPeriod:
                name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_部分周期_auto";
                task.setTaskName(name);
                task.setComments("挖掘  "+ipOrPair+" 上,协议为"+protocol+"的部分周期");
                break;
            case MiningMethods_PredictionMining:
                task.setMiningAlgo(chooser.getFmAlgo());
                name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_预测_auto";
                task.setTaskName(name);
                task.setComments("预测  "+ipOrPair+" 上,协议为"+protocol+"的未来趋势");
                break;
            default:
                break;
        }
        task.setMiningObject(miningObject.toString());
        task.setProtocol(protocol);
        return task;
    }
    /**
     * 判断给定的时间序列是否稀疏，（稀疏即意味着时间序列大于50%的值都是0） 如果稀疏返回True
     * @param dataItems 时间序列
     * @return true if 时间序列稀疏  否则返回 false
     */
    private boolean isDataItemSparse(DataItems dataItems){
        int length=dataItems.getLength();
        int sparseNum=0;
        for(int i=0;i<length;i++){
            if(dataItems.getData().get(i).equals("0")){
                sparseNum+=1;
            }
        }

        if(sparseNum*1.0/length>=0.5)
            return true;
        else
            return false;
    }
    public int getCount(ArrayList<String> list){
        if(isMining)
            return list.size();

        isMining=true;
        File dataDirectory = null;
        if(MiningObject.MiningObject_NodeDisapearEmerge.toString().equals(miningObject.toString()))
        {
            dataDirectory = new File(dataPath + "\\node");
        }
        else{
            dataDirectory = new File(dataPath + "\\traffic");
        }

        nodePairReader reader=new nodePairReader();
//		if(dataDirectory.isFile()){
//			parseFile(dataDirectory,reader);
//		}else{

        File[] dataDirs=dataDirectory.listFiles();
        boolean flag = false; // 标记选择的路径是否已到具体节点目录
        for(int i=0;i<dataDirs.length;i++){
            //按天必须是文件夹
            if(dataDirs[i].isDirectory())
                parseFile2(dataDirs[i],reader, list);
            else { // 路径下为文件，而非目录
                flag = true;
                break;
            }
        }

        if (flag) {
            parseFile2(dataDirectory, reader, list);
        }
        return list.size();
    }

    private void parseFile2(File dataFile,nodePairReader reader, ArrayList<String> list){
        String ip=dataFile.getName();//.substring(0, dataFile.getName().lastIndexOf("."));
        //事先读取每一个IP上，每一个协议的DataItems
        //if(ip.equals("10.0.13.2"))
        //System.out.println();
        int granularity= Integer.parseInt(this.getGranularity());
        if(taskRange.toString().equals(TaskRange.SingleNodeRange.toString())){
            HashMap<String, DataItems> rawDataItems=null;
            boolean isNodeDisapearEmerge = false;

            switch (miningObject) {
                case MiningObject_Traffic:
                    //rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
                    //rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(),reader.getTask().getIsReadBetween(),reader.getTask().getDateStart(),reader.getTask().getDateEnd());
                    /**2016/7/14
                     * @author LYH
                     * 用于测试读取时间区间数据，单节点挖掘
                     * **/
//				Calendar cal1 = Calendar.getInstance();
//				Calendar cal2 = Calendar.getInstance();
//				cal1.set(2014, 9, 1, 0, 0, 0);
//				cal2.set(2014,11,20,0,0,0);
                    Date date1 = getStartDate();
                    Date date2 = getEndDate();
                    rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(),true,date1,date2,3600);

                    break;
                case MiningObject_Times:
                    //rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath());
                    //rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath(),reader.getTask().getIsReadBetween(),reader.getTask().getDateStart(),reader.getTask().getDateEnd());
                    /**2016/7/14
                     * @author LYH
                     * 用于测试读取时间区间数据，单节点挖掘
                     * **/
				/*Calendar cal3 = Calendar.getInstance();
				Calendar cal4 = Calendar.getInstance();
				cal3.set(2014, 9, 1, 2, 0, 0);
				cal4.set(2014,9,3,2,0,0);
				Date date3 = cal3.getTime();
				Date date4 = cal4.getTime();*/
                    Date date3 = getStartDate();
                    Date date4 = getEndDate();
                    rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath(),true,date3,date4,3600);
                    break;
                case MiningObject_NodeDisapearEmerge:
                    Date date5 = getStartDate();
                    Date date6 = getEndDate();
                    rawDataItems = reader.readEachNodeDisapearEmergeDataItems(dataFile.getAbsolutePath(),true,date5,date6,3600);
                    isNodeDisapearEmerge = true;
//				if(ip.compareTo("4") == 0 ||ip.compareTo("3") == 0 ||ip.compareTo("0") == 0 ||
//						ip.compareTo("10") == 0 ||ip.compareTo("6") == 0 ||ip.compareTo("5") == 0 ||
//						ip.compareTo("7") == 0 || ip.compareTo("1") == 0)
//					System.out.println(rawDataItems.get("AllTraffic").getData().toString());
                    break;
                default:
                    break;
            }
            for(String protocol:rawDataItems.keySet()){
                DataItems dataItems=rawDataItems.get(protocol);
                if(!isDataItemSparse(dataItems) || isNodeDisapearEmerge){
                    list.add("");
                }
            }
        }else if(taskRange.toString().equals(TaskRange.NodePairRange.toString())){
            HashMap<String, Map<String, DataItems>> ipPairRawDataItems=null;
            switch (miningObject) {
                case MiningObject_Traffic:
                    //ipPairRawDataItems=reader.readEachIpPairProtocolTrafficDataItems(dataFile.getAbsolutePath());

                    Date date1 = getStartDate();
                    Date date2 = getEndDate();
                    ipPairRawDataItems=reader.readEachIpPairProtocolTrafficDataItems(dataFile.getAbsolutePath(),true,date1,date2,3600);
                    break;
                case MiningObject_Times:
                    //ipPairRawDataItems=reader.readEachIpPairProtocolTimesDataItems(dataFile.getAbsolutePath());
                    Date date3 = getStartDate();
                    Date date4 = getEndDate();
                    ipPairRawDataItems=reader.readEachIpPairProtocolTimesDataItems(dataFile.getAbsolutePath(),true,date3,date4,3600);
                    break;
                default:
                    break;
            }

            for(String ipPair:ipPairRawDataItems.keySet()){
                Map<String, DataItems> itemsMap=ipPairRawDataItems.get(ipPair);
                for(String protocol:itemsMap.keySet()){
                    DataItems dataItems=itemsMap.get(protocol);
                    if(!isDataItemSparse(dataItems)){
                       list.add("");
                    }
                }
            }
        }
    }

}
