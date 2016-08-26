package cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by zsc on 2016/8/22.
 */
public class ParseByDay {
    private String inputPath = "D:\\57data";
    private String outputPath = "D:\\57data";
    private ArrayList<File> fileList = new ArrayList<File>();
    long initTime = System.currentTimeMillis();

    public static void main(String[] args) {
        ParseByDay parseByDay = new ParseByDay();
        parseByDay.setInitTime();
        parseByDay.parseNode();
        parseByDay.parseRoute();
        parseByDay.parseTraffic();
        System.out.println("执行完毕");
    }

    private void setInitTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 4, 1, 0, 0, 0);//初始时间为2016/05/01/0:0:0
        initTime = cal.getTimeInMillis() / 1000 * 1000;
    }

    private void parseNode() {
        String inPath = inputPath + "\\node";
        fileList.clear();
        getFileList(inPath, "txt");

        ExecutorService exec = Executors.newFixedThreadPool(4);
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < fileList.size(); i++) {
            String outPath = outputPath + "\\node\\" + fileList.get(i).getName().substring(0, fileList.get(i).getName().lastIndexOf("."));
            System.out.println("outpath: " + outPath);
            File folder = new File(outPath);
            boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
            NodeGen nodeGen = new NodeGen(fileList.get(i), outPath, initTime);
            results.add(exec.submit(nodeGen));

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

    }

    private void parseRoute() {
        String inPath = inputPath + "\\route";
        fileList.clear();
        getFileList(inPath, "csv");

        ExecutorService exec = Executors.newFixedThreadPool(4);
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < fileList.size(); i++) {
            String outPath = outputPath + "\\route\\" + fileList.get(i).getName().substring(0, fileList.get(i).getName().lastIndexOf("."));
            System.out.println("outpath: " + outPath);
            File folder = new File(outPath);
            boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
            RouteGenerate routeGen = new RouteGenerate(fileList.get(i), outPath, initTime);
            results.add(exec.submit(routeGen));

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
    }

    private void parseTraffic() {
        String inPath = inputPath + "\\traffic";
        fileList.clear();
        getFileList(inPath, "txt");

        ExecutorService exec = Executors.newFixedThreadPool(4);
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < fileList.size(); i++) {
            String outPath = outputPath + "\\traffic\\" + fileList.get(i).getName().substring(0, fileList.get(i).getName().lastIndexOf("."));
            System.out.println("outpath: " + outPath);
            File folder = new File(outPath);
            boolean suc = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
            TrafficGen trafficGen = new TrafficGen(fileList.get(i), outPath, initTime);
            results.add(exec.submit(trafficGen));

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
    }

    private void getFileList(String fPath, String type) {
        File ff = new File(fPath);
        if (ff.isFile() && fPath.endsWith(type)) {
            fileList.add(ff);
        } else if (ff.isDirectory()) {
            File[] files = ff.listFiles();
            for (File f : files) {
                getFileList(f.getAbsolutePath(), type);
            }
        }
    }
}

class NodeGen implements Callable {
    private File file;
    private String outPath;
    private long count;
    private long initTime;
    private String time;
    private ConcurrentHashMap<Long, BufferedWriter> bwMap = new ConcurrentHashMap<Long, BufferedWriter>();

    NodeGen(File file, String outPath, long initTime) {
        this.file = file;
        this.outPath = outPath;
        this.count = 0;
        this.initTime = initTime;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("进入执行");
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bin = new BufferedReader(in);
        String curLine = null;
        while ((curLine = bin.readLine()) != null) {
            time = curLine.split(",")[0];
            count = Long.valueOf(time) / 24;

            if (!bwMap.containsKey(count)) {
                genFileName(count);
                BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outPath + File.separator + genFileName(count) + ".txt"), "UTF-8"));
                bwMap.put(count, bout);
            }
            if (count == 0) {
                bwMap.get(count).write(curLine);
                bwMap.get(count).newLine();
            } else {
                bwMap.get(count).write(curLine.replace(curLine.split(",")[0], String.valueOf(Long.valueOf(time) - count * 24)));
                bwMap.get(count).newLine();
            }

        }
        for (Map.Entry<Long, BufferedWriter> entry : bwMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private long genFileName(long count) {
        return initTime + 86400000 * count;
    }
}

class RouteGenerate implements Callable {
    private File file;
    private String outPath;
    private long count;
    private long initTime;
    private String time;
    private ConcurrentHashMap<Long, BufferedWriter> bwMap = new ConcurrentHashMap<Long, BufferedWriter>();

    RouteGenerate(File file, String outPath, long initTime) {
        this.file = file;
        this.outPath = outPath;
        this.count = 0;
        this.initTime = initTime;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("进入执行");
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bin = new BufferedReader(in);
        String headLine = bin.readLine();
        String curLine = null;
        while ((curLine = bin.readLine()) != null) {
            time = curLine.split(",")[0];
            count = Long.valueOf(time) / 86400;

            if (!bwMap.containsKey(count)) {
                genFileName(count);
                BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outPath + File.separator + genFileName(count) + ".csv"), "UTF-8"));
                bwMap.put(count, bout);
                bwMap.get(count).write(headLine);
                bwMap.get(count).newLine();
            }
            if (count == 0) {
                bwMap.get(count).write(curLine);
                bwMap.get(count).newLine();
            } else {
                bwMap.get(count).write(curLine.replace(curLine.split(",")[0], String.valueOf(Long.valueOf(time) - count * 86400)));
                bwMap.get(count).newLine();
            }

        }
        for (Map.Entry<Long, BufferedWriter> entry : bwMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private long genFileName(long count) {
        return initTime + 86400000 * count;
    }
}

class TrafficGen implements Callable {
    private File file;
    private String outPath;
    private long count;
    private long initTime;
    private String time;
    private ConcurrentHashMap<Long, BufferedWriter> bwMap = new ConcurrentHashMap<Long, BufferedWriter>();

    TrafficGen(File file, String outPath, long initTime) {
        this.file = file;
        this.outPath = outPath;
        this.count = 0;
        this.initTime = initTime;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("进入执行");
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bin = new BufferedReader(in);
        String curLine = null;
        while ((curLine = bin.readLine()) != null) {
            time = curLine.split(",")[0];
            count = Long.valueOf(time) / 24;

            if (!bwMap.containsKey(count)) {
                genFileName(count);
                BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outPath + File.separator + genFileName(count) + ".txt"), "UTF-8"));
                bwMap.put(count, bout);
            }
            if (count == 0) {
                bwMap.get(count).write(curLine);
                bwMap.get(count).newLine();
            } else {
                bwMap.get(count).write(curLine.replace(curLine.split(",")[0], String.valueOf(Long.valueOf(time) - count * 24)));
                bwMap.get(count).newLine();
            }

        }
        for (Map.Entry<Long, BufferedWriter> entry : bwMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private long genFileName(long count) {
        return initTime + 86400000 * count;
    }
}
