package cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;


/**
 * Created by zsc on 2016/7/27.
 */
public class PcapRouteGen {

    public static void genDatas(MappedByteBuffer is, ArrayList<PcapData> datas) throws IOException {
        byte[] buffer_4 = new byte[4];
        byte[] buffer_2 = new byte[2];
        byte[] buffer_1 = new byte[1];
        byte[] buffer_8 = new byte[8];
        byte[] name;
        String strName;
        int length;
        StringBuilder sb = new StringBuilder();
        while (is.hasRemaining()) {
            PcapData data = new PcapData();

            is.get(buffer_4);
            data.setTime_s(byteArrayToLong(buffer_4, 0));

            is.get(buffer_4);
            data.setTime_ms(byteArrayToLong(buffer_4, 0));

            is.get(buffer_2);
            data.setTraffic(byteArrayToShort(buffer_2, 0));

            is.get(buffer_1);
            data.setTTL(64 - buffer_1[0]);

            is.get(buffer_4);
            data.setSrcIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());

            is.get(buffer_4);
            data.setDstIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());

//            is.get(buffer_8);
//            data.setSrcIP(srcIP);
//            data.setDstIP(dstIP);

            is.get(buffer_2);
            data.setSrcPort(byteArrayToPort(buffer_2, 0));

            is.get(buffer_2);
            data.setDstPort(byteArrayToPort(buffer_2, 0));

            is.get(buffer_4);
            length = byteArrayToInt(buffer_4, 0);

            name = new byte[length];
            is.get(name);
            strName = new String(name).intern();//指向常量池中的string
            data.setPcapFile(strName);
            datas.add(data);

//            BufferedWriter bw;
//                if (!bws.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
//                    synchronized (bws) {
//                        if (!bws.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
//                            OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(outpath + "\\routesrc\\" + data.getSrcIP() + "_" + data.getDstIP() + ".txt"), "UTF-8");
//                            bw = new BufferedWriter(o);
//                            bws.put(data.getSrcIP() + "_" + data.getDstIP(), bw);
//                        }
//                    }
//                }
//                String curLine = new String();
//                synchronized (bw = bws.get(data.getSrcIP() + "_" + data.getDstIP())) {
//                    curLine = data.getSrcIP() + "," + data.getDstIP() + "," + data.getSrcPort() + "," + data.getDstPort() + "," + data.getTime_s() + "," + data.getTime_ms() + "," + data.getPcapFile() + "," + data.getTraffic() + "," + data.getTTL();
//                    bw.write(curLine);
////			        	if(data.getSrcIP().equals("10.0.10.2")&&data.getDstIP().equals("10.0.2.2"))
////			        	{
////			        		System.out.println(curLine);
////
////			        	}
//                    bw.newLine();
//                }
        }
    }

    public static long pGenDatas(long position, long part, long i, long pLength, MappedByteBuffer is, ArrayList<PcapData> datas) throws IOException {
        byte[] buffer_4 = new byte[4];
        byte[] buffer_2 = new byte[2];
        byte[] buffer_1 = new byte[1];
        byte[] buffer_8 = new byte[8];
        byte[] name;
        int length;
        StringBuilder sb = new StringBuilder();
        while (is.hasRemaining() && (is.position() < pLength / 2 || (i == part))) {
            PcapData data = new PcapData();

            is.get(buffer_4);
            data.setTime_s(byteArrayToLong(buffer_4, 0));

            is.get(buffer_4);
            data.setTime_ms(byteArrayToLong(buffer_4, 0));

            is.get(buffer_2);
            data.setTraffic(byteArrayToShort(buffer_2, 0));

            is.get(buffer_1);
            data.setTTL(64 - buffer_1[0]);

            is.get(buffer_4);
            data.setSrcIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());

            is.get(buffer_4);
            data.setDstIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());

//            is.get(buffer_8);
//            data.setSrcIP(srcIP);
//            data.setDstIP(dstIP);

            is.get(buffer_2);
            data.setSrcPort(byteArrayToPort(buffer_2, 0));

            is.get(buffer_2);
            data.setDstPort(byteArrayToPort(buffer_2, 0));

            is.get(buffer_4);
            length = byteArrayToInt(buffer_4, 0);

            name = new byte[length];
            is.get(name);
            data.setPcapFile(new String(name));
            datas.add(data);
        }
        return (position + is.position());
    }

    private static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    private static long byteArrayToLong(byte[] b, int offset) {
        long value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            long temp = b[i + offset] & 0x00000000000000FF;
            value += temp << shift;
        }
        if (value < 0) {
            System.out.println("here");
        }
        return value;
    }

    private static short byteArrayToShort(byte[] b, int offset) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (2 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    private static int byteArrayToPort(byte[] b, int offset) {
        int port = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (2 - i - 1) * 8;
            port += (b[i + offset] & 0x000000FF) << shift;
        }
        return port;
    }

    private static String byteArrayToIP(byte[] b, StringBuilder sb) {
        String str;
        for (int i = 0; i < 3; i++) {
            sb.append(b[i] + ".");
        }
        sb.append(b[3]);
        str = new String(sb.toString()).intern();//指向常量池中的string
        return str;
    }
}
