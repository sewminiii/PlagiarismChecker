package thePackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileProducer{

    private final BlockingQueue<File> fileListBlockingQueue = new ArrayBlockingQueue<>(100);
    private List<File> fileList = new ArrayList<>();
    private File returnFile;
    char[] charArray1, charArray2;
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
    //use thread pools!!!!!!!!!!
    public void returnFileList(){
        System.out.println("file list size = "+fileList.size());
        for(int i = 0; i < fileList.size(); i++) {
            for (int j = i + 1; j < fileList.size(); j++) {
                File file1 = fileList.get(i);
                File file2 = fileList.get(j);
                System.out.println("file 1 = " + file1 + " file 2 = " + file2);

                //for file 1
                StringBuilder theStringForFile1;
                Scanner scanner1;
                try {
                    scanner1 = new Scanner(file1);

                    theStringForFile1 = new StringBuilder(scanner1.nextLine());
                    while (scanner1.hasNextLine()) {
                        theStringForFile1.append("\n").append(scanner1.nextLine());
                    }
                    charArray1 = theStringForFile1.toString().toCharArray();
                    for (char c : charArray1)
                        System.out.print(c);
                    System.out.println("");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //for file 2
                StringBuilder theStringForFile2 = new StringBuilder();
                Scanner scanner2 = null;
                try {
                    scanner2 = new Scanner(file2);
                    theStringForFile2 = new StringBuilder(scanner2.nextLine());
                    while (scanner2.hasNextLine()) {
                        theStringForFile2.append("\n").append(scanner2.nextLine());
                    }
                    charArray2 = theStringForFile2.toString().toCharArray();
                    for (char c : charArray2)
                        System.out.print(c);
                    System.out.println("");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //call similarity method
                FileConsumer consumer = new FileConsumer();
                consumer.calcSimilarity(charArray1,charArray2);
            }
        }

    }


}
