package thePackage;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileProducer {
    private final BlockingQueue<File> fileListBlockingQueue = new ArrayBlockingQueue<>(100);

    public void offerFiles(File directory) {

        Runnable producer = () -> {
            try {
                File[] listOfFiles = directory.listFiles();
                if (listOfFiles != null) {
                    for (File f : listOfFiles) {
                      /*  long bytes = f.length();
                        System.out.println("fileeee "+f);
                        System.out.println("file size is "+bytes);*/
                        if(f.length()>0){
                            fileListBlockingQueue.offer(f);
                        }
                        System.out.println("blocking q = "+fileListBlockingQueue);

                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        };
        Thread producerThread = new Thread(producer,"producer thread");
        producerThread.start();
    }
}
