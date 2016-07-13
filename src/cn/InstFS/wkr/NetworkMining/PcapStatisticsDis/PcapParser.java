package cn.InstFS.wkr.NetworkMining.PcapStatisticsDis;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PcapParser {

    public static int hUnpack(MappedByteBuffer is) throws IOException {
        byte[] buffer_4 = new byte[4];
        byte[] buffer_2 = new byte[2];

        PcapHeader header = new PcapHeader();
        is.get(buffer_4);
//        int m = is.read(buffer_4);
//        if (m != 4) {
//            return;
//        }
        int count = 0;
        reverseByteArray(buffer_4);
        header.setMagic(byteArrayToInt(buffer_4, 0));
        System.out.println("magic " + header.getMagic());
        is.get(buffer_2);
        reverseByteArray(buffer_2);
        header.setMajor_version(byteArrayToShort(buffer_2, 0));
        System.out.println("major_version" + header.getMajor_version());
        is.get(buffer_2);
        reverseByteArray(buffer_2);
        header.setMinor_version(byteArrayToShort(buffer_2, 0));
        System.out.println("minor_version" + header.getMinor_version());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setTimezone(byteArrayToInt(buffer_4, 0));
        System.out.println("timezone" + header.getTimezone());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setSigflags(byteArrayToInt(buffer_4, 0));
        System.out.println("sigflags" + header.getSigflags());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setSnaplen(byteArrayToInt(buffer_4, 0));
        System.out.println("snaplen" + header.getSnaplen());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setLinktype(byteArrayToInt(buffer_4, 0));
        System.out.println("Linktype" + header.getLinktype());
        return header.getLinktype();
    }

    public static void pUnpack(long i, long pLength, int linkType, MappedByteBuffer is, String fileName, HashMap<String, BufferedWriter> bws, String path) throws IOException {
        byte[] buffer_4 = new byte[4];
        byte[] buffer_3 = new byte[3];
        byte[] buffer_2 = new byte[2];
        byte[] buffer_1 = new byte[1];
        byte[] buffer = new byte[5000];
        StringBuilder sb = new StringBuilder();
        int datalength;
        //参数is从0开始，capacity显示当前片段的容量，执行到最后一次时进行判断
        while (is.hasRemaining() && (is.position() < pLength / 2 || (i == Parser.getPart()))) {
            datalength = 0;
            PcapData data = new PcapData();
            is.get(buffer_4);
            if (!is.hasRemaining()) {
                break;
            }
            reverseByteArray(buffer_4);
            data.setTime_s(byteArrayToLong(buffer_4, 0));
//            System.out.println("Time_s" + data.getTime_s());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
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
            is.get(buffer_4);//skip in order to get TTL
            datalength += 4;
            is.get(buffer_1);
            datalength += 1;
            data.setTTL(64 - buffer_1[0]);
            is.get(buffer_3);
            datalength += 3;
            is.get(buffer_4);
            datalength += 4;
            data.setSrcIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            is.get(buffer_4);
            datalength += 4;
            data.setDstIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            is.get(buffer_2);
            datalength += 2;
            data.setSrcPort(byteArrayToPort(buffer_2, 0));
            is.get(buffer_2);
            datalength += 2;
            data.setDstPort(byteArrayToPort(buffer_2, 0));
            is.get(buffer, 0, data.getpLength() - datalength);

            if (data.getTraffic() > 30) {
                BufferedWriter bw;
                if (!bws.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                    synchronized (bws) {
                        if (!bws.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                            OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(path + "\\routesrc\\" + data.getSrcIP() + "_" + data.getDstIP() + ".txt"), "UTF-8");
                            bw = new BufferedWriter(o);
                            bws.put(data.getSrcIP() + "_" + data.getDstIP(), bw);
                        }
                    }
                }
                String curLine = new String();
                synchronized (bw = bws.get(data.getSrcIP() + "_" + data.getDstIP())) {
                    curLine = data.getSrcIP() + "," + data.getDstIP() + "," + data.getSrcPort() + "," + data.getDstPort() + "," + data.getTime_s() + "," + data.getTime_ms() + "," + fileName + "," + data.getTraffic() + "," + data.getTTL();
                    bw.write(curLine);
//			        	if(data.getSrcIP().equals("10.0.10.2")&&data.getDstIP().equals("10.0.2.2"))
//			        	{
//			        		System.out.println(curLine);
//
//			        	}
                    bw.newLine();
                }

            }
        }
        Parser.setPosition(Parser.getPosition() + is.position());
        System.out.println("is.position：" + is.position());
    }

    public static void unpack(FileChannel fc, MappedByteBuffer is, String fileName, ConcurrentHashMap<RecordKey, Integer> records, HashMap<String, BufferedWriter> bws, String path) throws IOException {
        byte[] buffer_4 = new byte[4];
        byte[] buffer_3 = new byte[3];
        byte[] buffer_2 = new byte[2];
        byte[] buffer_1 = new byte[1];
        byte[] buffer_100 = new byte[100];
        byte[] buffer_10 = new byte[10];
        byte[] buffer = new byte[5000];
        PcapHeader header = new PcapHeader();
        is.get(buffer_4);
//        int m = is.read(buffer_4);
//        if (m != 4) {
//            return;
//        }
        int count = 0;
        reverseByteArray(buffer_4);
        header.setMagic(byteArrayToInt(buffer_4, 0));
        System.out.println("magic " + header.getMagic());
        is.get(buffer_2);
        reverseByteArray(buffer_2);
        header.setMajor_version(byteArrayToShort(buffer_2, 0));
        System.out.println("major_version" + header.getMajor_version());
        is.get(buffer_2);
        reverseByteArray(buffer_2);
        header.setMinor_version(byteArrayToShort(buffer_2, 0));
        System.out.println("minor_version" + header.getMinor_version());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setTimezone(byteArrayToInt(buffer_4, 0));
        System.out.println("timezone" + header.getTimezone());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setSigflags(byteArrayToInt(buffer_4, 0));
        System.out.println("sigflags" + header.getSigflags());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setSnaplen(byteArrayToInt(buffer_4, 0));
        System.out.println("snaplen" + header.getSnaplen());
        is.get(buffer_4);
        reverseByteArray(buffer_4);
        header.setLinktype(byteArrayToInt(buffer_4, 0));
        System.out.println("Linktype" + header.getLinktype());
        StringBuilder sb = new StringBuilder();
        int datalength;
        long num = 0;
        while (is.hasRemaining()) {
            datalength = 0;
            PcapData data = new PcapData();
            is.get(buffer_4);
            if (!is.hasRemaining()) {
                break;
            }
            count++;
//			    if(count%100000==0)
//			    	System.out.println(count);
            reverseByteArray(buffer_4);
            data.setTime_s(byteArrayToLong(buffer_4, 0));
//            System.out.println("Time_s" + data.getTime_s());
            is.get(buffer_4);
            reverseByteArray(buffer_4);
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
//			    		 System.out.println(m);
                    continue;
                }
            }
            is.get(buffer_2);
            datalength += 2;
            is.get(buffer_2);
            datalength += 2;
            data.setTraffic(byteArrayToShort(buffer_2, 0));
            is.get(buffer_4);//skip in order to get TTL
            datalength += 4;
            is.get(buffer_1);
            datalength += 1;
            data.setTTL(64 - buffer_1[0]);
            is.get(buffer_3);
            datalength += 3;
            is.get(buffer_4);
            datalength += 4;
            data.setSrcIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            is.get(buffer_4);
            datalength += 4;
            data.setDstIP(byteArrayToIP(buffer_4, sb));
            sb.delete(0, sb.length());
            is.get(buffer_2);
            datalength += 2;
            data.setSrcPort(byteArrayToPort(buffer_2, 0));
            is.get(buffer_2);
            datalength += 2;
            data.setDstPort(byteArrayToPort(buffer_2, 0));
            is.get(buffer, 0, data.getpLength() - datalength);
            if (data.getTraffic() > 30) {
                BufferedWriter bw;
                if (!bws.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                    synchronized (bws) {
                        if (!bws.containsKey(data.getSrcIP() + "_" + data.getDstIP())) {
                            OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(path + "\\routesrc\\" + data.getSrcIP() + "_" + data.getDstIP() + ".txt"), "UTF-8");
                            bw = new BufferedWriter(o);
                            bws.put(data.getSrcIP() + "_" + data.getDstIP(), bw);
                        }
                    }
                }
                String curLine = new String();
                synchronized (bw = bws.get(data.getSrcIP() + "_" + data.getDstIP())) {
                    curLine = data.getSrcIP() + "," + data.getDstIP() + "," + data.getSrcPort() + "," + data.getDstPort() + "," + data.getTime_s() + "," + data.getTime_ms() + "," + fileName + "," + data.getTraffic() + "," + data.getTTL();
                    bw.write(curLine);
//			        	if(data.getSrcIP().equals("10.0.10.2")&&data.getDstIP().equals("10.0.2.2"))
//			        	{
//			        		System.out.println(curLine);
//			        		
//			        	}
                    bw.newLine();
                }

            }
        }
        fc.close();
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
