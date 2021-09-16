package thePackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingQ {
    private final BlockingQueue<File> fileListBlockingQueue = new ArrayBlockingQueue<>(100);
   /* private final ExecutorService cpuService = Executors.newCachedThreadPool();
    private final ExecutorService ioService = Executors.newCachedThreadPool();*/
    private List<File> fileList = new ArrayList<>();
    myMain ui;
    File directory;
    private File returnFile;
    public Thread thread;

    public BlockingQ(myMain ui){
        this.ui = ui;
        //this.directory = directory;
    }
    public BlockingQ(){}

    public void offerFiles(File directory) {
        System.out.println("first thread : "+thread);
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
                System.out.println("thread : "+thread);

            }catch (NullPointerException | InterruptedException e){
                //e.printStackTrace();
                System.out.println("Producer thread interrupted");
            }

            //System.out.println("thread : "+thread);
            getNextFile();

        };
        thread = new Thread(producer,"producer thread");
        thread.start();
    }

    public void getNextFile() {
        System.out.println("thread : "+thread);
        Runnable consumer =() -> {
            while (!fileListBlockingQueue.isEmpty()) {
                try {
                    returnFile = fileListBlockingQueue.take();
                    fileList.add(returnFile);
                } catch (InterruptedException e) {
                    System.out.println("Consumer thread interrupted");
                }
                System.out.println("file list = " + fileList);
            }
            ComparisonLogics comparisonLogics = new ComparisonLogics(this.ui);
            comparisonLogics.combinations(fileList);
           // System.out.println("thread : "+thread);
        };
        thread = new Thread(consumer,"consumer thread");
        thread.start();

    }

    public void end(){
        System.out.println("current thread = "+thread);
       if(thread == null){
            throw new IllegalStateException();
        }
        thread.interrupt();
        System.out.println("producer thread interrupted");
        thread = null;
    }



}
