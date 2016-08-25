package cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zsc on 2016/7/28.
 */
public class PcapByteParser {

    public static int hUnpack(MappedByteBuffer is) throws IOException {
        byte[] buffer_4 = new byte[4];
        byte[] buffer_2 = new byte[2];

        PcapHeader header = new PcapHeader();
        is.get(buffer_4);
//        int m = is.read(buffer_4);
//        if (m != 4) {
//            return;
//        }
        reverseByteArray(buffer_4);
        header.setMagic(byteArrayToInt(buffer_4, 0));
//        System.out.println("magic " + header.getMagic());
        is.get(buffer_2);
        reverseByteArray(buffer_2);
        header.setMajor_version(byteArrayToShort(buffer_2, 0));
//        System.out.println("major_version" + header.getMajor_version());
        is.get(buffer_2);
        reverseByteArray(buffer_2);
        header.setMinor_version(byteArrayToShort(buffer_2, 0));
//        System.out.println("minor_version" + header.getMinor_version());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setTimezone(byteArrayToInt(buffer_4, 0));
//        System.out.println("timezone" + header.getTimezone());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setSigflags(byteArrayToInt(buffer_4, 0));
//        System.out.println("sigflags" + header.getSigflags());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setSnaplen(byteArrayToInt(buffer_4, 0));
//        System.out.println("snaplen" + header.getSnaplen());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setLinktype(byteArrayToInt(buffer_4, 0));
//        System.out.println("Linktype" + header.getLinktype());
        return header.getLinktype();
    }

    public static long pUnpack(long position, long part, long i, long pLength, int linkType, MappedByteBuffer is, String fileName, ConcurrentHashMap<String, BufferedOutputStream> bos,  ConcurrentHashMap<String, ArrayList<PcapNode>> nodeMap, String path) throws IOException {
        byte[] temp = fileName.getBytes();
        int length = temp.length;
        byte[] buffer_4 = new byte[4];
        byte[] buffer_3 = new byte[3];
        byte[] buffer_2 = new byte[2];
        byte[] buffer_1 = new byte[1];
        byte[] buffer = new byte[5000];
        byte[] buffer_278 = new byte[27 + length];
        ArrayList<PcapNode> nodeList = new ArrayList<PcapNode>();
        StringBuilder sb = new StringBuilder();
        int datalength;
        int byteLength;
        //参数is从0开始，capacity显示当前片段的容量，执行到最后一次时进行判断
        while (is.hasRemaining() && (is.position() < pLength / 2 || (i == part))) {
            datalength = 0;
            byteLength = 0;
            PcapData data = new PcapData();
            PcapNode node = new PcapNode();
            node.setFileName(fileName);//写入文件名
            is.get(buffer_4);
            if (!is.hasRemaining()) {
                break;
            }
            reverseByteArray(buffer_4);
            System.arraycopy(buffer_4, 0, buffer_278, 0, buffer_4.length);
            byteLength += 4;
            data.setTime_s(byteArrayToLong(buffer_4, 0));
            node.setTime_s(byteArrayToLong(buffer_4, 0) / 3600);//写入时间,按小时
//            System.out.println("Time_s" + data.getTime_s());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);
            byteLength += 4;
            data.setTime_ms(byteArrayToLong(buffer_4, 0));
//            System.out.println("Time_ms" + data.getTime_ms());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
            data.setpLength(byteArrayToInt(buffer_4, 0));
//            System.out.println("pLength" + data.getpLength());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
            data.setLength(byteArrayToInt(buffer_4, 0));
//            System.out.println("Length" + data.getLength());
            if (linkType == 9) {
                is.get(buffer, 0, 2);
                datalength += 2;
                if (buffer[0] != 0 || buffer[1] != 0x21) {
                    is.get(buffer, 0, data.getpLength() - datalength);
                    continue;
                }
            } else if (linkType == 1) {
                is.get(buffer, 0, 14);
                datalength += 14;
                if (buffer[12] != 0x08 || buffer[13] != 0x00) {
                    is.get(buffer, 0, data.getpLength() - datalength);
//			    		 System.out.println(m);
                    continue;
                }
            }
            is.get(buffer_2);
            datalength += 2;
            is.get(buffer_2);
            datalength += 2;
            data.setTraffic(byteArrayToShort(buffer_2, 0));
            System.arraycopy(buffer_2, 0, buffer_278, byteLength, buffer_2.length);
            byteLength += 2;
            node.setTraffic(byteArrayToShort(buffer_2, 0));//写入流量
            nodeList.add(node);
            is.get(buffer_4);//skip in order to get TTL
            datalength += 4;
            is.get(buffer_1);
            datalength += 1;
            data.setTTL(64 - buffer_1[0]);
            System.arraycopy(buffer_1, 0, buffer_278, byteLength, buffer_1.length);
            byteLength += 1;
            is.get(buffer_3);
            datalength += 3;
            is.get(buffer_4);
            datalength += 4;
            data.setSrcIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);
            byteLength += 4;
            is.get(buffer_4);
            datalength += 4;
            data.setDstIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);
            byteLength += 4;
            is.get(buffer_2);
            datalength += 2;
            data.setSrcPort(byteArrayToPort(buffer_2, 0));
            System.arraycopy(buffer_2, 0, buffer_278, byteLength, buffer_2.length);
            byteLength += 2;
            is.get(buffer_2);
            datalength += 2;
            data.setDstPort(byteArrayToPort(buffer_2, 0));
            System.arraycopy(buffer_2, 0, buffer_278, byteLength, buffer_2.length);
            byteLength += 2;

            buffer_4 = intToByte(length);
            reverseByteArray(buffer_4);
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);//filename长度
            byteLength += 4;

            System.arraycopy(temp, 0, buffer_278, byteLength, temp.length);
            is.get(buffer, 0, data.getpLength() - datalength);

            if (data.getTraffic() > 30) {
                BufferedOutputStream bo;
                if (!bos.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                    synchronized (bos) {
                        if (!bos.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                            bo = new BufferedOutputStream(new FileOutputStream(path + "\\routesrc\\" + data.getSrcIP() + "_" + data.getDstIP() + ".bin"));
                            bos.put(data.getSrcIP() + "_" + data.getDstIP(), bo);
                        }
                    }
                }
                synchronized (bo = bos.get(data.getSrcIP() + "_" + data.getDstIP())) {
                    bo.write(buffer_278);
                }
            }
        }
        //当切割文件时，防止覆盖
        if (nodeMap.containsKey(fileName)) {
            nodeMap.get(fileName).addAll(nodeList);
        } else {
            nodeMap.put(fileName, nodeList);
        }
        return (position + is.position());
    }

    public static void unpack(MappedByteBuffer is, String fileName, ConcurrentHashMap<String, BufferedOutputStream> bos, ConcurrentHashMap<String, ArrayList<PcapNode>> nodeMap, String path) throws IOException {
        byte[] temp = fileName.getBytes();
        int length = temp.length;
        byte[] buffer_4 = new byte[4];
        byte[] buffer_3 = new byte[3];
        byte[] buffer_2 = new byte[2];
        byte[] buffer_1 = new byte[1];
        byte[] buffer_20 = new byte[20];
        byte[] buffer = new byte[5000];
        byte[] buffer_278 = new byte[27 + length];
        PcapHeader header = new PcapHeader();
        ArrayList<PcapNode> nodeList = new ArrayList<PcapNode>();
        is.get(buffer_20);
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setLinktype(byteArrayToInt(buffer_4, 0));

        StringBuilder sb = new StringBuilder();
        int datalength;
        int byteLength;
        while (is.hasRemaining()) {
            datalength = 0;
            byteLength = 0;
            PcapData data = new PcapData();
            PcapNode node = new PcapNode();
            node.setFileName(fileName);//写入文件名
            is.get(buffer_4);
//            if (!is.hasRemaining()) {
//                break;
//            }
            reverseByteArray(buffer_4);
            System.arraycopy(buffer_4, 0, buffer_278, 0, buffer_4.length);
            byteLength += 4;
            data.setTime_s(byteArrayToLong(buffer_4, 0));
            node.setTime_s(byteArrayToLong(buffer_4, 0) / 3600);//写入时间,按小时
//            System.out.println("Time_s" + data.getTime_s());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);
            byteLength += 4;
            data.setTime_ms(byteArrayToLong(buffer_4, 0));
//            System.out.println("Time_ms" + data.getTime_ms());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
            data.setpLength(byteArrayToInt(buffer_4, 0));
//            System.out.println("pLength" + data.getpLength());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
            data.setLength(byteArrayToInt(buffer_4, 0));
//            System.out.println("Length" + data.getLength());
            if (header.getLinktype() == 9) {
                is.get(buffer, 0, 2);
                datalength += 2;
                if (buffer[0] != 0 || buffer[1] != 0x21) {
                    is.get(buffer, 0, data.getpLength() - datalength);
                    continue;
                }
            } else if (header.getLinktype() == 1) {
                is.get(buffer, 0, 14);
                datalength += 14;
                if (buffer[12] != 0x08 || buffer[13] != 0x00) {
                    is.get(buffer, 0, data.getpLength() - datalength);
                    continue;
                }
            }
            is.get(buffer_2);
            datalength += 2;
            is.get(buffer_2);
            datalength += 2;
            data.setTraffic(byteArrayToShort(buffer_2, 0));
            System.arraycopy(buffer_2, 0, buffer_278, byteLength, buffer_2.length);
            byteLength += 2;
            node.setTraffic(byteArrayToShort(buffer_2, 0));//写入流量
            //每读入一行，判断是否存在，将流量加起来
            if (nodeList.contains(node)) {
                nodeList.get(nodeList.indexOf(node)).setTraffic(nodeList.get(nodeList.indexOf(node)).getTraffic() + node.getTraffic());
            } else {
                nodeList.add(node);
            }
            is.get(buffer_4);//skip in order to get TTL
            datalength += 4;
            is.get(buffer_1);
            datalength += 1;
            data.setTTL(64 - buffer_1[0]);
            System.arraycopy(buffer_1, 0, buffer_278, byteLength, buffer_1.length);
            byteLength += 1;
            is.get(buffer_3);
            datalength += 3;
            is.get(buffer_4);
            datalength += 4;
            data.setSrcIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);
            byteLength += 4;
            is.get(buffer_4);
            datalength += 4;
            data.setDstIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);
            byteLength += 4;
            is.get(buffer_2);
            datalength += 2;
            data.setSrcPort(byteArrayToPort(buffer_2, 0));
            System.arraycopy(buffer_2, 0, buffer_278, byteLength, buffer_2.length);
            byteLength += 2;
            is.get(buffer_2);
            datalength += 2;
            data.setDstPort(byteArrayToPort(buffer_2, 0));
            System.arraycopy(buffer_2, 0, buffer_278, byteLength, buffer_2.length);
            byteLength += 2;

            buffer_4 = intToByte(length);
            reverseByteArray(buffer_4);
            System.arraycopy(buffer_4, 0, buffer_278, byteLength, buffer_4.length);//filename长度
            byteLength += 4;

            System.arraycopy(temp, 0, buffer_278, byteLength, temp.length);
            is.get(buffer, 0, data.getpLength() - datalength);
            if (data.getTraffic() > 30) {
                BufferedOutputStream bo;
                if (!bos.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                    synchronized (bos) {
                        if (!bos.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                            bo = new BufferedOutputStream(new FileOutputStream(path + "\\routesrc\\" + data.getSrcIP() + "_" + data.getDstIP() + ".bin"));
                            bos.put(data.getSrcIP() + "_" + data.getDstIP(), bo);
                        }
                    }
                }
                synchronized (bo = bos.get(data.getSrcIP() + "_" + data.getDstIP())) {
                    bo.write(buffer_278);
                }
            }
        }
        nodeMap.put(fileName, nodeList);
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
        for (int i = 0; i < 3; i++) {
            sb.append(b[i] + ".");
        }
        sb.append(b[3]);
        return sb.toString();
    }

    public static byte[] intToByte(int number) {
        byte[] abyte = new byte[4];
        // "&" 与（AND），对两个整型操作数中对应位执行布尔代数，两个位都为1时输出1，否则0。
        abyte[0] = (byte) (0xff & number);
        // ">>"右移位，若为正数则高位补0，若为负数则高位补1
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }

    /**
     * 反转数组
     *
     * @param arr
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
