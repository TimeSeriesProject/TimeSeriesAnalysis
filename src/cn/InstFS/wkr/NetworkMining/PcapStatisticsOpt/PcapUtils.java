package cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.*;

class Node implements Callable {
    private Map.Entry<String, ArrayList<PcapNode>> entry;
    private String outPath;

    Node(Map.Entry<String, ArrayList<PcapNode>> entry, String outPath) {
        this.entry = entry;
        this.outPath = outPath;
    }

    @Override
    public Boolean call() throws Exception {
        Collections.sort(entry.getValue());//排序
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outPath + "\\node\\" + entry.getKey() + ".txt"), "UTF-8");
        BufferedWriter bw = new BufferedWriter(out);
        for (int i = 0; i < entry.getValue().size(); i++) {
            String curLine = entry.getValue().get(i).getTime_s() + "," +
                    entry.getValue().get(i).getFileName() + "," + entry.getValue().get(i).getTraffic();
            bw.write(curLine);
            bw.newLine();
        }
        bw.close();
        return true;
    }
}

class Parser implements Callable {
    private File file = null;
    private long length;
    private long pLength;
    private long position;
    private long part;
    ConcurrentHashMap<RecordKey, Integer> trafficRecords;
    private HashMap<String, BufferedWriter> bws;
    private ConcurrentHashMap<String, BufferedOutputStream> bos;
    private ConcurrentHashMap<String, ArrayList<PcapNode>> nodeMap;
    String path;
    PcapUtils pcapUtils;

    Parser(PcapUtils pcapUtils, File file, ConcurrentHashMap<RecordKey, Integer> trafficRecords, HashMap<String, BufferedWriter> bws, ConcurrentHashMap<String, BufferedOutputStream> bos, ConcurrentHashMap<String, ArrayList<PcapNode>> nodeMap, String path) {
        this.file = file;
        this.pcapUtils = pcapUtils;
        this.trafficRecords = trafficRecords;
        this.bws = bws;
        this.bos = bos;
        this.nodeMap = nodeMap;
        this.path = path;
        this.length = 0;
        this.pLength = 0;
        this.position = 0;
        this.part = 0;
    }

    public long getPart() {
        return part;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public void unmap(final MappedByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    if (getCleanerMethod != null) {
                        getCleanerMethod.setAccessible(true);
                        Object cleaner = getCleanerMethod.invoke(buffer, new Object[0]);
                        Method cleanMethod = cleaner.getClass().getMethod("clean", new Class[0]);
                        if (cleanMethod != null) {
                            cleanMethod.invoke(cleaner, new Object[0]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

//    public void clean(final Object buffer) throws Exception {
//        AccessController.doPrivileged(new PrivilegedAction() {
//            public Object run() {
//                try {
//                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner",new Class[0]);
//                    getCleanerMethod.setAccessible(true);
//                    sun.misc.Cleaner cleaner =(sun.misc.Cleaner)getCleanerMethod.invoke(buffer,new Object[0]);
//                    cleaner.clean();
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//                return null;}});
//
//    }
    public Boolean call() {
        try {
            FileChannel fc = new FileInputStream(file).getChannel();
            length = fc.size();
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".");
            fileName = fileName.substring(0, index);
            if (length <= Integer.MAX_VALUE) {
                MappedByteBuffer is = fc.map(FileChannel.MapMode.READ_ONLY, 0, length);
//                PcapParser.unpack(fc, is, fileName, trafficRecords, bws, nodeMap, path);
                PcapByteParser.unpack(is, fileName, bos, nodeMap, path);

                pcapUtils.setParsedNum(pcapUtils.getParsedNum() + 1);
                System.out.println("getParsedNum()" + pcapUtils.getParsedNum());
                unmap(is);
                fc.close();
            } else {
                long partition = length / (1024 * 1024 * 1024) + 2;
//                long partition = 2;
                part = partition;
                System.out.println("partition: " + partition);
                MappedByteBuffer header = fc.map(FileChannel.MapMode.READ_ONLY, 0, 24);
//                int linkType = PcapParser.hUnpack(header);
                int linkType = PcapByteParser.hUnpack(header);
                setPosition(header.position());//读取文件头后的position24
                for (int i = 1; i <= partition; i++) {
                    pLength = length * i / partition - position;//position初值为0，不用+1，否则fc.map溢出
//                    System.out.println(i + "pLength: " + pLength);
//                    System.out.println(i + "参数position：" + position);
                    MappedByteBuffer is = fc.map(FileChannel.MapMode.READ_ONLY, position, pLength);

//                    position = PcapParser.pUnpack(position, part, i, pLength, linkType, is, fileName, bws, nodeMap, path);
                    position = PcapByteParser.pUnpack(position, part, i, pLength, linkType, is, fileName, bos, nodeMap, path);
//                    System.out.println("执行结束");
                    unmap(is);
                }
                pcapUtils.setParsedNum(pcapUtils.getParsedNum() + 1);
                System.out.println("getParsedNum()" + pcapUtils.getParsedNum());
                fc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

class RouteGen implements Callable {
    private long length;
    private long pLength;
    private long position;
    private long part;
    private String fileName;

    String path;
    String outpath;
    ConcurrentHashMap<RecordKey, Integer> trafficRecords;
    ConcurrentHashMap<RecordKey, Integer> comRecords;
    ArrayList<PcapData> datas = new ArrayList<PcapData>();
    PcapUtils pcapUtils;

    RouteGen(PcapUtils pcapUtils, String path, String outpath, String fileName, ConcurrentHashMap<RecordKey, Integer> trafficRecords, ConcurrentHashMap<RecordKey, Integer> comRecords) {
        this.pcapUtils = pcapUtils;
        this.path = path;
        this.outpath = outpath;
        this.fileName = fileName;
        this.trafficRecords = trafficRecords;
        this.comRecords = comRecords;
    }

    private void updateRecords(PcapData pre) {
        RecordKey tmpKey1 = new RecordKey(pre.getSrcIP(), pre.getDstIP(), pre.getDstPort(), pre.getTime_s() / 3600);
        RecordKey tmpKey2 = new RecordKey(pre.getDstIP(), pre.getSrcIP(), pre.getDstPort(), pre.getTime_s() / 3600);
//    	System.out.println(trafficRecords);
        if (!trafficRecords.containsKey(tmpKey1)) {
            trafficRecords.put(tmpKey1, 0);
            comRecords.put(tmpKey1, 0);
        }
        if (!trafficRecords.containsKey(tmpKey2)) {
            trafficRecords.put(tmpKey2, 0);
            comRecords.put(tmpKey2, 0);
        }
        trafficRecords.put(tmpKey1, trafficRecords.get(tmpKey1) + pre.getTraffic());
        trafficRecords.put(tmpKey2, trafficRecords.get(tmpKey2) + pre.getTraffic());
        comRecords.put(tmpKey1, comRecords.get(tmpKey1) + 1);
        comRecords.put(tmpKey2, comRecords.get(tmpKey2) + 1);
    }

    private void gen() throws IOException {
        System.out.println("进入gen...");
        File f = new File(path);
        String file = f.getName();
        int index = file.lastIndexOf(".");
        file = file.substring(0, index);
        OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(outpath + "\\route" + "\\" + file + ".csv"), "UTF-8");
        BufferedWriter bw = new BufferedWriter(o);
        PcapData pre = null;
        HashSet<String> set = null;
        String curLine;
        curLine = "Time(S),srcIP,dstIP,traffic,hops";
        bw.write(curLine);
        bw.newLine();
        int num = 0;
        int count = 0;

        class NodeAndTTL implements Comparable<NodeAndTTL> {
            String node;
            Integer TTL;

            NodeAndTTL(String node, Integer TTL) {
                this.node = node;
                this.TTL = TTL;
            }

            public int compareTo(NodeAndTTL arg0) {
                return TTL.compareTo(arg0.TTL);
            }
        }
        ArrayList<NodeAndTTL> ttlList = new ArrayList<NodeAndTTL>();

        Collections.sort(datas);
        for (PcapData entry : datas) {
            count++;
//			if(count%100000==0)
//				System.out.println("genroute "+count);
            PcapData data = entry;
//			if(data.getSrcIP().equals("10.0.1.2")&&data.getDstIP().equals("10.0.2.2"))
//			{
//				System.out.println(data);
//			}
            if (pre == null) {
                ttlList.add(new NodeAndTTL(data.getPcapFile(), data.getTTL()));

                pre = entry;
                num = 1;
                continue;
            }
            //记录每一次通信的通信路径，一次通信只记录一次流量。
            if (!pre.getSrcIP().equals(data.getSrcIP()) || !pre.getDstIP().equals(data.getDstIP()) || pre.getSrcPort() != data.getSrcPort() || pre.getDstPort() != data.getDstPort()) {
                updateRecords(pre);
                StringBuilder sb = new StringBuilder();
                sb.append(String.valueOf(pre.getTime_s())).append(",").append(pre.getSrcIP()).append(",").append(pre.getDstIP()).append(",").append(pre.getTraffic()).append(",").append(num);
                Collections.sort(ttlList);
                for (int i = 0; i < ttlList.size(); i++)
                    sb.append(",").append(ttlList.get(i).node).append(":").append(ttlList.get(i).TTL);
                bw.write(sb.toString());

                bw.newLine();
                ttlList.clear();
                ttlList.add(new NodeAndTTL(data.getPcapFile(), data.getTTL()));
                pre = data;
                num = 1;
            } else if ((double) data.getTime_s() + data.getTime_ms() / 1000000.0 > pre.getTime_s() + pre.getTime_ms() / 1000000.0 + 2.0) {
                updateRecords(pre);

                StringBuilder sb = new StringBuilder();
                sb.append(String.valueOf(pre.getTime_s())).append(",").append(pre.getSrcIP()).append(",").append(pre.getDstIP()).append(",").append(pre.getTraffic()).append(",").append(num);
                Collections.sort(ttlList);
                for (int i = 0; i < ttlList.size(); i++)
                    sb.append(",").append(ttlList.get(i).node).append(":").append(ttlList.get(i).TTL);
                bw.write(sb.toString());

                bw.newLine();
                ttlList.clear();
                ttlList.add(new NodeAndTTL(data.getPcapFile(), data.getTTL()));
                pre = data;
                num = 1;
            } else {
                ttlList.add(new NodeAndTTL(data.getPcapFile(), data.getTTL()));
                num++;
            }
        }
        updateRecords(pre);

        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(pre.getTime_s())).append(",").append(pre.getSrcIP()).append(",").append(pre.getDstIP()).append(",").append(pre.getTraffic()).append(",").append(num);
        Collections.sort(ttlList);
        for (int i = 0; i < ttlList.size(); i++)
            sb.append(",").append(ttlList.get(i).node).append(":").append(ttlList.get(i).TTL);
        bw.write(sb.toString());

        ttlList.clear();
        bw.close();
    }

    //初始
//    public Boolean call() {
//        try {
//
//            InputStreamReader in = new InputStreamReader(new FileInputStream(path), "UTF-8");
//            BufferedReader bin = new BufferedReader(in, 5 * 1024 * 1024);
//            String curLine = null;
//            int count = 0;
//
//            while ((curLine = bin.readLine()) != null) {
//                count++;
////				if(count%100000==0)
////					System.out.println("readsrc "+count);
////				System.out.println(curLine);
//                if (curLine.length() < 2)
//                    continue;
//                String str[] = curLine.split(",");
////				System.out.println(str.length);
//                PcapData data = new PcapData();
////				for(int i=0;i<str.length;i++)
////					System.out.println(str[i]);
//                data.setSrcIP(str[0]);
//                data.setDstIP(str[1]);
//                data.setSrcPort(Integer.parseInt(str[2]));
//                data.setDstPort(Integer.parseInt(str[3]));
//                data.setTime_s(Long.parseLong(str[4]));
//                data.setTime_ms(Long.parseLong(str[5]));
//                data.setTTL(Integer.parseInt(str[8]));
//                data.setTraffic(Integer.valueOf(str[7]));
//                data.setPcapFile(str[6]);
//                //if(count<10)
//                datas.add(data);
//            }
//            bin.close();
//            gen();
//            datas = null;
//            System.gc();
//            pcapUtils.setGenedRouteNum(pcapUtils.getGenedRouteNum() + 1);
//            System.out.println("getGenedRouteNum()" + pcapUtils.getGenedRouteNum());
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            //System.out.println("........");
//            e.printStackTrace();
//        }
//        return true;
//    }

    public Boolean call() {
        try {
            String srcIP = fileName.split("_")[0];
            String dstIP = fileName.substring(srcIP.length() + 1, fileName.lastIndexOf("."));
            FileChannel fc = new FileInputStream(path).getChannel();
            length = fc.size();
            if (length <= Integer.MAX_VALUE) {
                MappedByteBuffer is = fc.map(FileChannel.MapMode.READ_ONLY, 0, length);
                PcapRouteGen.genDatas(is, datas, srcIP, dstIP);
                unmap(is);
                fc.close();
            }
            else {
                long partition = length / (1024 * 1024 * 1024) + 2;
//                long partition = 2;
                part = partition;
                System.out.println("partition: " + partition);
                for (int i = 1; i <= partition; i++) {
                    pLength = length * i / partition - position;//position初值为0，不用+1，否则fc.map溢出
//                    System.out.println("pLength: " + pLength);
//                    System.out.println("参数position：" + position);
                    MappedByteBuffer is = fc.map(FileChannel.MapMode.READ_ONLY, position, pLength);

//                    position = PcapParser.pUnpack(position, part, i, pLength, linkType, is, fileName, bws, nodeMap, path);
                    position = PcapRouteGen.pGenDatas(position, part, i, pLength, is, datas, srcIP, dstIP);
                    System.out.println("执行结束");
                    unmap(is);
                }
                fc.close();
            }

            gen();
            datas = null;
            System.gc();
            pcapUtils.setGenedRouteNum(pcapUtils.getGenedRouteNum() + 1);
            System.out.println("getGenedRouteNum()" + pcapUtils.getGenedRouteNum());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //map readline
//    public Boolean call() {
//        try {
//            FileChannel fc = new FileInputStream(new File(path)).getChannel();
//            long length = fc.size();
//            MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//            System.out.println("length: " + length);
//
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < (int) length; i++) {
//                if (buffer.get(i) == 10) {//判断遇到换行符，处理此行数据
//                    String a = new String(sb.toString().substring(0, sb.toString().length() - 1));
////                    System.out.println(sb.toString());
////                    String str[] = a.split(",");
////                    System.out.println("88 " + str[8]);
////				System.out.println(str.length);
//                    PcapData data = new PcapData();
//                    data.setSrcIP(new String(a.split(",")[0]));
//                    data.setDstIP(new String(a.split(",")[1]));
//                    data.setSrcPort(Integer.parseInt(new String(a.split(",")[2])));
//                    data.setDstPort(Integer.parseInt(new String(a.split(",")[3])));
//                    data.setTime_s(Long.parseLong(new String(a.split(",")[4])));
//                    data.setTime_ms(Long.parseLong(new String(a.split(",")[5])));
//                    data.setTTL(Integer.parseInt(new String(a.split(",")[8])));
//                    data.setTraffic(Integer.valueOf(new String(a.split(",")[7])));
//                    data.setPcapFile(new String(a.split(",")[6]));
//                    //if(count<10)
//                    datas.add(data);
//                    sb.delete(0, sb.length());
//                } else if (i == length - 1) {//判断到了最后一行，处理此行数据
//                    sb.append((char) buffer.get(i));
////                    System.out.println(sb.toString());
//                    String str[] = sb.toString().split(",");
////				System.out.println(str.length);
//                    PcapData data = new PcapData();
////				for(int i=0;i<str.length;i++)
////					System.out.println(str[i]);
//                    data.setSrcIP(str[0]);
//                    data.setDstIP(str[1]);
//                    data.setSrcPort(Integer.parseInt(str[2]));
//                    data.setDstPort(Integer.parseInt(str[3]));
//                    data.setTime_s(Long.parseLong(str[4]));
//                    data.setTime_ms(Long.parseLong(str[5]));
//                    data.setTTL(Integer.parseInt(str[8]));
//                    data.setTraffic(Integer.valueOf(str[7]));
//                    data.setPcapFile(str[6]);
//                    //if(count<10)
//                    datas.add(data);
//                } else {//拼接成一行数据
//                    sb.append((char) buffer.get(i));
//                }
//            }
//            sb = null;
//            unmap(buffer);
//            fc.close();
//
//
//            //断点
//            gen();
//            datas = null;
//            System.gc();
//            pcapUtils.setGenedRouteNum(pcapUtils.getGenedRouteNum() + 1);
//            System.out.println("getGenedRouteNum()" + pcapUtils.getGenedRouteNum());
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            //System.out.println("........");
//            e.printStackTrace();
//        }
//        return true;
//    }

    public void unmap(final MappedByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    if (getCleanerMethod != null) {
                        getCleanerMethod.setAccessible(true);
                        Object cleaner = getCleanerMethod.invoke(buffer, new Object[0]);
                        Method cleanMethod = cleaner.getClass().getMethod("clean", new Class[0]);
                        if (cleanMethod != null) {
                            cleanMethod.invoke(cleaner, new Object[0]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

//    //buffer读入
//    public Boolean call() {
//        try {
//            int bufSize = 10 *1024 *1024;//一次读取的字节长度
//
//            InputStreamReader in = new InputStreamReader(new FileInputStream(path), "UTF-8");
//            BufferedReader bin = new BufferedReader(in, 5 * 1024 * 1024);
//            String curLine = null;
//            int count = 0;
//
//            FileChannel fcin = new RandomAccessFile(path, "r").getChannel();
//            ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);
//
//
//
//            String enterStr = "\n";
//            try {
//                byte[] bs = new byte[bufSize];
//                //temp：由于是按固定字节读取，在一次读取中，第一行和最后一行经常是不完整的行，因此定义此变量来存储上次的最后一行和这次的第一行的内容，
//                //并将之连接成完成的一行，否则会出现汉字被拆分成2个字节，并被提前转换成字符串而乱码的问题，数组大小应大于文件中最长一行的字节数
//                byte[] temp = new byte[500];
//                while (fcin.read(rBuffer) != -1) {
//                    int rSize = rBuffer.position();
//                    rBuffer.rewind();
//                    rBuffer.get(bs);
//                    rBuffer.clear();
//
//                    //windows下ascii值13、10是换行和回车，unix下ascii值10是换行
//                    //从开头顺序遍历，找到第一个换行符
//                    int startNum = 0;
//                    int length = 0;
//                    for (int i = 0; i < rSize; i++) {
//                        if (bs[i] == 10) {//找到换行字符
//                            startNum = i;
//                            for (int k = 0; k < 500; k++) {
//                                if (temp[k] == 0) {//temp已经存储了上一次读取的最后一行，因此遍历找到空字符位置，继续存储此次的第一行内容，连接成完成一行
//                                    length = i + k;
//                                    for (int j = 0; j <= i; j++) {
//                                        temp[k + j] = bs[j];
//                                    }
//                                    break;
//                                }
//                            }
//                            break;
//                        }
//                    }
//                    //将拼凑出来的完整的一行转换成字符串
//                    String tempString1 = new String(temp, 0, length + 1, "UTF-8");
//                    //清空temp数组
//                    for (int i = 0; i < temp.length; i++) {
//                        temp[i] = 0;
//                    }
//                    //从末尾倒序遍历，找到第一个换行符
//                    int endNum = 0;
//                    int k = 0;
//                    for (int i = rSize - 1; i >= 0; i--) {
//                        if (bs[i] == 10) {
//                            endNum = i;//记录最后一个换行符的位置
//                            for (int j = i + 1; j < rSize; j++) {
//                                temp[k++] = bs[j];//将此次读取的最后一行的不完整字节存储在temp数组，用来跟下一次读取的第一行拼接成完成一行
//                                bs[j] = 0;
//                            }
//                            break;
//                        }
//                    }
//                    //去掉第一行和最后一行不完整的，将中间所有完整的行转换成字符串
//                    String tempString2 = new String(bs, startNum + 1, endNum - startNum, "UTF-8");
//
//                    //拼接两个字符串
//                    String tempString = tempString1 + tempString2;
////              System.out.print(tempString);
//
//                    int fromIndex = 0;
//                    int endIndex = 0;
//                    while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {
//                        curLine = tempString.substring(fromIndex, endIndex - 1);//按行截取字符串
////                        System.out.print(line);
//                        //写入文件
//                        PcapData data = new PcapData();
////				for(int i=0;i<str.length;i++)
////					System.out.println(str[i]);
//                        data.setSrcIP(curLine.split(",")[0]);
//                        data.setDstIP(curLine.split(",")[1]);
//                        data.setSrcPort(Integer.parseInt(curLine.split(",")[2]));
//                        data.setDstPort(Integer.parseInt(curLine.split(",")[3]));
//                        data.setTime_s(Long.parseLong(curLine.split(",")[4]));
//                        data.setTime_ms(Long.parseLong(curLine.split(",")[5]));
//                        data.setTTL(Integer.parseInt(curLine.split(",")[8]));
//                        data.setTraffic(Integer.valueOf(curLine.split(",")[7]));
//                        data.setPcapFile(curLine.split(",")[6]);
//                        //if(count<10)
//                        datas.add(data);
//                        fromIndex = endIndex + 1;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            bin.close();
//            gen();
//            datas = null;
//            System.gc();
//            pcapUtils.setGenedRouteNum(pcapUtils.getGenedRouteNum() + 1);
//            System.out.println("getGenedRouteNum()" + pcapUtils.getGenedRouteNum());
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            //System.out.println("........");
//            e.printStackTrace();
//        }
//        return true;
//    }
}

/**
 * pcap文件操作类
 *
 * @author wsc
 */
public class PcapUtils {
    private boolean SessionLevel = true;   //判断读取的数据是否是业务层数据
    private ConcurrentHashMap<RecordKey, Integer> trafficRecords = new ConcurrentHashMap<RecordKey, Integer>(); //记录流量
    private ConcurrentHashMap<RecordKey, Integer> comRecords = new ConcurrentHashMap<RecordKey, Integer>();
    TreeMap<RecordKey, Integer> sortedtrafficRecords = new TreeMap<RecordKey, Integer>();
    private ArrayList<File> fileList = new ArrayList<File>();
    private HashMap<String, BufferedWriter> bws = new HashMap<String, BufferedWriter>();
    private ConcurrentHashMap<String, BufferedOutputStream> bos = new ConcurrentHashMap<String, BufferedOutputStream>();
    private ConcurrentHashMap<String, ArrayList<PcapNode>> nodeMap = new ConcurrentHashMap<String, ArrayList<PcapNode>>();

    public enum Status {
        PREPARE("prepare"), PARSE("parse"), GENROUTE("genroute"), END("end");
        private String comment;

        Status(String str) {
            this.comment = str;
        }

        @Override
        public String toString() {
            return comment;
        }
    }

    Status status = Status.PREPARE;
    private int parseSum = 0;
    private int parsedNum = 0;
    private int genRouteSum = 0;
    private int genedRouteNum = 0;
    private int genTrafficSum = 0;
    private int gentedTrafficNum = 0;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getParseSum() {
        return parseSum;
    }

    public void setParseSum(int parseSum) {
        this.parseSum = parseSum;
    }

    public synchronized int getParsedNum() {
        return parsedNum;
    }

    public synchronized void setParsedNum(int parsedNum) {
        this.parsedNum = parsedNum;
    }

    public int getGenRouteSum() {
        return genRouteSum;
    }

    public void setGenRouteSum(int genSum) {
        this.genRouteSum = genSum;
    }

    public synchronized int getGenedRouteNum() {
        return genedRouteNum;
    }

    public synchronized void setGenedRouteNum(int genedNum) {
        this.genedRouteNum = genedNum;
    }

    public synchronized int getGentedTrafficNum() {
        return gentedTrafficNum;
    }

    public synchronized void setGentedTrafficNum(int gentedTrafficNum) {
        this.gentedTrafficNum = gentedTrafficNum;
    }

    public synchronized int getGenTrafficSum() {
        return genTrafficSum;
    }

    public synchronized void setGenTrafficSum(int genTrafficSum) {
        this.genTrafficSum = genTrafficSum;
    }

    public static void main(String[] args) throws IOException, FileNotFoundException {
        long a = System.currentTimeMillis();
        String fpath = "E:\\pcap";
        PcapUtils pcapUtils = new PcapUtils();
        //pcapUtils.readInput(fpath,1);
        pcapUtils.readInput(fpath, "E:\\out");
        long b = System.currentTimeMillis();
        System.out.println("时间：" + (b - a) / 1000);
        //pcapUtils.generateRoute("C:\\data\\out\\routesrc","C:\\data\\out");

    }

    private void generateRoute(String fpath, String outPath) {
//		System.out.println("ggg");
        fileList.clear();
        getFileList(fpath, "bin");
        genRouteSum = fileList.size();
        status = Status.GENROUTE;
        System.out.println(status);
        System.out.println("genSum " + genRouteSum);
        ExecutorService exec = Executors.newFixedThreadPool(1);
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < fileList.size(); i++) {
            RouteGen routeGen = new RouteGen(this, fileList.get(i).getAbsolutePath(), outPath, fileList.get(i).getName(), trafficRecords, comRecords);
//            results.add(exec.submit(routeGen));
            routeGen.call();
        }
//        for (int i = 0; i < results.size(); i++) {
//            try {
//                results.get(i).get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } finally {
//                exec.shutdown();
//            }
//        }
    }

    private void parsePcap(String fpath, String outpath) throws IOException, FileNotFoundException {
        getFileList(fpath, "pcap");
        parseSum = fileList.size();
        status = Status.PARSE;
        System.out.println(status);
        System.out.println("parseSum " + parseSum);
        ExecutorService exec = Executors.newFixedThreadPool(16);
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            Parser parser = new Parser(this, file, trafficRecords, bws, bos, nodeMap, outpath);
            results.add(exec.submit(parser));
//            parser.call();
        }
        for (int i = 0; i < results.size(); i++) {
            try {
                results.get(i).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                exec.shutdown();
            }
        }
        for (Map.Entry<String, BufferedWriter> entry : bws.entrySet()) {

            try {
                entry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, BufferedOutputStream> entry : bos.entrySet()) {

            try {
                entry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateNode(String outpath) {
        //key = 文件名第一个字符
        HashMap<String, ArrayList<PcapNode>> tResult = new HashMap<String, ArrayList<PcapNode>>();
        for (Map.Entry<String, ArrayList<PcapNode>> entry : nodeMap.entrySet()) {
            //合并list,重新维护一个hashmap，判断有无，合并
            if (tResult.containsKey(entry.getKey().split("-")[0])) {
                tResult.get(entry.getKey().split("-")[0]).addAll(entry.getValue());
            } else {
                tResult.put(entry.getKey().split("-")[0], entry.getValue());
            }
        }
//        for (Map.Entry<String, ArrayList<PcapNode>> entry : tResult.entrySet()) {
//            System.out.println("fffff: " + entry.getKey());
//        }
        ExecutorService exec = Executors.newFixedThreadPool(16);
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (Map.Entry<String, ArrayList<PcapNode>> entry : tResult.entrySet()) {
            Node node = new Node(entry, outpath);
            results.add(exec.submit(node));
        }

        for (int i = 0; i < results.size(); i++) {
            try {
                results.get(i).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                exec.shutdown();
            }
        }
        nodeMap = null;
        System.gc();
    }

    private void generateTraffic(String outpath) {

        for (Map.Entry<RecordKey, Integer> entry : trafficRecords.entrySet()) {
            sortedtrafficRecords.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, BufferedWriter> entry : bws.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("sorted" + sortedtrafficRecords.size());
        RecordKey prekey = null;
        OutputStreamWriter o = null;
        BufferedWriter bw = null;
        int trafficsum = 0;
        int comsum = 0;
        StringBuilder curLine = new StringBuilder();
        for (Map.Entry<RecordKey, Integer> entry : sortedtrafficRecords.entrySet()) {
            RecordKey key = entry.getKey();
            if (prekey == null || !prekey.getSrcIp().equals(key.getSrcIp()) || !prekey.getTime().equals(key.getTime()) || !prekey.getDstIp().equals(key.getDstIp())) {
                try {
                    if (prekey != null) {
                        curLine.append("sum:" + trafficsum + ":" + comsum);
                        bw.write(curLine.toString());
                        bw.newLine();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                trafficsum = 0;
                comsum = 0;
                curLine.delete(0, curLine.length());
                curLine.append(key.getTime() + "," + key.getSrcIp() + "," + key.getDstIp() + ",");
            }
            if (prekey == null || !(prekey.getSrcIp()).equals(key.getSrcIp())) {
                try {
                    if (prekey != null) {
                        bw.close();
                    }
                    o = new OutputStreamWriter(new FileOutputStream(outpath + "\\traffic" + "\\" + key.getSrcIp() + ".txt"), "UTF-8");
                    bw = new BufferedWriter(o);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            trafficsum += entry.getValue();
            comsum += comRecords.get(entry.getKey());
            curLine.append(key.getProtocol() + ":" + String.valueOf(entry.getValue()) + ":" + String.valueOf(comRecords.get(entry.getKey())) + ";");
            prekey = key;
        }
        if (prekey != null) {
            curLine.append("sum:" + trafficsum + ":" + comsum);
            try {
                bw.write(curLine.toString());
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void readInput(String fpath, String outpath) throws IOException, FileNotFoundException {

        File folder = new File(outpath + "\\routesrc");
        boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();

        folder = new File(outpath + "\\route");
        suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();

        folder = new File(outpath + "\\node");
        suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();

        folder = new File(outpath + "\\traffic");
        suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
        System.out.println(status);
        System.out.println("parsepcap开始");
        long a = System.currentTimeMillis();
        parsePcap(fpath, outpath);
        long b = System.currentTimeMillis();
        System.out.println("时间1：" + (b - a) / 1000);

        System.out.println("parsepcap结束，node开始");
        generateNode(outpath);
        long c = System.currentTimeMillis();
        System.out.println("时间2：" + (c - b) / 1000);

        System.out.println("node结束，route开始");
        generateRoute(outpath + "\\routesrc", outpath);
        long d = System.currentTimeMillis();
        System.out.println("时间3：" + (d - c) / 1000);

        System.out.println("route结束，traffic开始");
        generateTraffic(outpath);
        long e = System.currentTimeMillis();
        System.out.println("时间3：" + (e - d) / 1000);

        status = Status.END;
        System.out.println("解析结束");

    }

    private void getFileList(String fpath, String type) {
        File ff = new File(fpath);
        if (ff.isFile() && fpath.endsWith(type)) {
            fileList.add(ff);
        } else if (ff.isDirectory()) {
            File[] files = ff.listFiles();
            for (File f : files) {
                String path = f.getPath();
                getFileList(f.getAbsolutePath(), type);
            }
        }
    }

    public PcapUtils() {
    }
}

