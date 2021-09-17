package thePackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class FilesLogics {
    private final BlockingQueue<File> fileListBlockingQueue = new ArrayBlockingQueue<>(100);
    private List<File> fileList = new ArrayList<>();
    myMain ui;
    private File returnFile;
    private Thread file_producer_thread,file_consumer_thread;

    public FilesLogics(myMain ui){
        this.ui = ui;
        //this.directory = directory;
    }


    public void offerFiles(File directory) {
        System.out.println("first thread : "+file_producer_thread);
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
                System.out.println("thread : "+file_producer_thread);

            }catch (NullPointerException | InterruptedException e){
                //e.printStackTrace();
                System.out.println("Producer thread interrupted");
            }

            //System.out.println("thread : "+thread);
            getNextFile();

        };
        file_producer_thread = new Thread(producer,"producer thread");
        file_producer_thread.start();
    }

    public void getNextFile() {
        System.out.println("thread : "+file_consumer_thread);
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
            comparisonLogics.fileCombinations(fileList);
           // System.out.println("thread : "+thread);
        };
        file_consumer_thread = new Thread(consumer,"consumer thread");
        file_consumer_thread.start();

    }

    public void end(){
        System.out.println("current thread = "+Thread.currentThread());
        Thread.currentThread().interrupt();
    }



}
