package cn.InstFS.wkr.NetworkMining.PcapDistributed;

import cn.InstFS.wkr.NetworkMining.PcapStatistics.PcapData;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.PcapHeader;

import java.io.*;
import java.util.HashSet;

/**
 * Created by zsc on 2016/7/5.
 */
public class PcapTasks {
    public static void unpackTasks(InputStream is, HashSet<String> bws) throws IOException {
        byte[] buffer_20 = new byte[20];
        byte[] buffer_16 = new byte[16];
        byte[] buffer_8 = new byte[8];
        byte[] buffer_4 = new byte[4];
        byte[] buffer_2 = new byte[2];
        byte[] buffer = new byte[5000];
        PcapHeader header = new PcapHeader();
        int m = is.read(buffer_4);
        if (m != 4) {
            return;
        }
        m = is.read(buffer_16);
        m = is.read(buffer_4);
        reverseByteArray(buffer_4);
        header.setLinktype(byteArrayToInt(buffer_4, 0));
        System.out.println("Linktype" + header.getLinktype());
        StringBuilder sb = new StringBuilder();
        int datalength;
        while (m > 0) {
            datalength = 0;
            PcapData data = new PcapData();
            m = is.read(buffer_4);
            if (m < 0) {
                break;
            }
            m = is.read(buffer_4);

            m = is.read(buffer_4);
            reverseByteArray(buffer_4);
            data.setpLength(byteArrayToInt(buffer_4, 0));
            m = is.read(buffer_4);
            reverseByteArray(buffer_4);

            if (header.getLinktype() == 9) {
                m = is.read(buffer, 0, 2);
                datalength += 2;
                if (buffer[0] != 0 || buffer[1] != 0x21) {
                    m = is.read(buffer, 0, data.getpLength() - datalength);
                    continue;
                }
            } else if (header.getLinktype() == 1) {
                m = is.read(buffer, 0, 14);
                datalength += 14;
                if (buffer[12] != 0x08 || buffer[13] != 0x00) {
                    m = is.read(buffer, 0, data.getpLength() - datalength);
//			    		 System.out.println(m);
                    continue;
                }
            }
            m = is.read(buffer_2);
            datalength += 2;
            m = is.read(buffer_2);
            datalength += 2;
            data.setTraffic(byteArrayToShort(buffer_2, 0));
            m = is.read(buffer_8);//skip in order to read TTL
            datalength += 8;


            m = is.read(buffer_4);
            datalength += 4;
            data.setSrcIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());

            m = is.read(buffer_4);
            datalength += 4;
            data.setDstIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());

            m = is.read(buffer_4);
            datalength += 4;
            is.read(buffer, 0, data.getpLength() - datalength);
            if (data.getTraffic() > 30) {
                if (!bws.contains(data.getSrcIP() + "_" + data.getDstIP())) {
                    synchronized (bws) {
                        bws.add(data.getSrcIP() + "_" + data.getDstIP());
                    }
                }
            }
        }
        is.close();
    }

    private static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
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

    private static String byteArrayToIP(byte[] b, StringBuilder sb) {
        for (int i = 0; i < 3; i++) {
            sb.append(b[i]).append(".");
        }
        sb.append(b[3]);
        return sb.toString();
    }

    /**
     * 反转数组
     */
    private static void reverseByteArray(byte[] arr) {
        byte temp;
        int n = arr.length;
        for (int i = 0; i < n / 2; i++) {
            temp = arr[i];
            arr[i] = arr[n - 1 - i];
            arr[n - 1 - i] = temp;
        }
    }
}
