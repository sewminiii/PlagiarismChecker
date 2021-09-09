package thePackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileProducer{
    private final BlockingQueue<File> fileListBlockingQueue = new ArrayBlockingQueue<>(100);
   /* private final ExecutorService cpuService = Executors.newCachedThreadPool();
    private final ExecutorService ioService = Executors.newCachedThreadPool();*/
    private List<File> fileList = new ArrayList<>();
    private File returnFile;

    private Thread producerThread;

    public void offerFiles(File directory) {
        Runnable producer = () -> {
            try {
                File[] listOfFiles = directory.listFiles();
                if (listOfFiles != null) {
                    for (File f : listOfFiles) {
                        if(f.length()>0){
                            fileListBlockingQueue.put(f);
                        }
                        System.out.println("blocking q = "+fileListBlockingQueue);
                    }
                }
            }catch (NullPointerException | InterruptedException e){
                e.printStackTrace();
            }
            getNextFile();

        };
        producerThread = new Thread(producer,"producer thread");
        producerThread.start();
    }

    public void getNextFile() {
        Runnable consumer =() -> {
            while (!fileListBlockingQueue.isEmpty()) {
                try {
                    returnFile = fileListBlockingQueue.take();
                    fileList.add(returnFile);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("file list = " + fileList);
            }
            ComparisonLogics comparisonLogics = new ComparisonLogics();
            comparisonLogics.combinations(fileList);
        };
        producerThread = new Thread(consumer,"consumer thread");
        producerThread.start();

    }

    public void end(){
        System.out.println("call shut down in logic");
        producerThread.interrupt();
    }



}
