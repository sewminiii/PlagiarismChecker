package thePackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileProducer{

    private final BlockingQueue<File> fileListBlockingQueue = new ArrayBlockingQueue<>(100);
    private List<File> fileList = new ArrayList<>();
    private File returnFile;
    private File file1, file2;
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
            returnFileList();
        };

        producerThread = new Thread(consumer,"consumer thread");
        producerThread.start();

    }

    public void returnFileList(){
        System.out.println("file list size = "+fileList.size());
        for(int i = 0; i < fileList.size(); i++) {
            for (int j = i + 1; j < fileList.size(); j++) {
                file1 = fileList.get(i);
                file2 = fileList.get(j);
                System.out.println("file 1 = " + file1 + " file 2 = " + file2);
            }
        }

    }


}
