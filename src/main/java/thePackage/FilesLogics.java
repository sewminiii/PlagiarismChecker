package thePackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class FilesLogics {
    private final BlockingQueue<File> fileListBlockingQueue = new LinkedBlockingQueue<>();
    private List<File> fileList = new ArrayList<>();
    private myMain ui;
    private File returnFile;
    private Thread file_producer_thread,file_consumer_thread;

    //constructor
    public FilesLogics(myMain ui){
        this.ui = ui;
    }

    //this method is for adding files inside directory to the blockingQueue
    public void offerFiles(File directory) {
        System.out.println("first thread : "+file_producer_thread);
        Runnable producer = () -> {
            try {
                //get the file list into an File type array
                File[] listOfFiles = directory.listFiles();
                if (listOfFiles != null) {
                    //check whether the file is empty or not
                    for (File f : listOfFiles) {
                        if(f.length()>0){
                            //if the file is not empty add to the blockingQueue
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
            getNextFile();

        };
        //start the thread
        file_producer_thread = new Thread(producer,"producer thread");
        file_producer_thread.start();
    }

    //this method is for get the files from the blockingQueue
    public void getNextFile() {
        System.out.println("thread : "+file_consumer_thread);
        Runnable consumer =() -> {
            //loop until blockingQueue becomes empty
            while (!fileListBlockingQueue.isEmpty()) {
                try {
                    //get the file from queue and add them to a List
                    /* this has done because blockingQueues should not be passed as a parameter or constructor.
                    *  blockingQueue should not be returned by any method.
                    * No other class should be aware od its existence */
                    returnFile = fileListBlockingQueue.take();
                    fileList.add(returnFile);
                } catch (InterruptedException e) {
                    System.out.println("Consumer thread interrupted");
                }
                System.out.println("file list = " + fileList);
            }
            //pass that List of files to another class
            ComparisonLogics comparisonLogics = new ComparisonLogics(this.ui);
            comparisonLogics.fileCombinations(fileList);
        };
        //start the thread
        file_consumer_thread = new Thread(consumer,"consumer thread");
        file_consumer_thread.start();

    }

    public void end(){
        //stop the thread
        System.out.println("current thread = "+Thread.currentThread());
        Thread.currentThread().interrupt();
    }
}
