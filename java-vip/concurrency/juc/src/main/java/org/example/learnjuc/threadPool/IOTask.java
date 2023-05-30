package org.example.learnjuc.threadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * 作者：周瑜大都督
 */
public class IOTask implements Runnable {

    //整体执行时间，包括在队列中等待的时间
    Vector<Long> wholeTimeList;
    //真正执行时间
    Vector<Long> runTimeList;

    private long initStartTime = 0;

    public IOTask(Vector<Long> runTimeList, Vector<Long> wholeTimeList) {
        initStartTime = System.currentTimeMillis();
        this.runTimeList = runTimeList;
        this.wholeTimeList = wholeTimeList;
    }

    public void readAndWrite() throws IOException {
        File sourceFile = new File("D:/test.txt");
        BufferedReader input = new BufferedReader(new FileReader(sourceFile));
        String line = null;
        while((line = input.readLine()) != null){
            //System.out.println(line);
        }
        input.close();
    }

    public void run() {
        long start = System.currentTimeMillis();
        try {
            readAndWrite();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();


        long wholeTime = end - initStartTime;
        long runTime = end - start;
        wholeTimeList.add(wholeTime);
        runTimeList.add(runTime);
        System.out.println("单个线程花费时间：" + (end - start));
    }
}
