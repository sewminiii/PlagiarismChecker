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
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
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
            combinations();
        };
        producerThread = new Thread(consumer,"consumer thread");
        producerThread.start();

    }
    //use thread pools!!!!!!!!!!
    public void combinations(){
        System.out.println("file list size = "+fileList.size());
        threadPool.execute(() -> {
            for(int i = 0; i < fileList.size(); i++) {
                for (int j = i + 1; j < fileList.size(); j++) {
                    File file1 = fileList.get(i);
                    File file2 = fileList.get(j);
                    System.out.println("file 1 = " + file1 + " file 2 = " + file2);
                    convertToCharArray(file1,file2);
                }
            }
        });
    }
    public void convertToCharArray(File file1, File file2){
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
        Calculations consumer = new Calculations();
        double result = consumer.calcSimilarity(charArray1,charArray2);
        displayResults(file1,file2,result);
        writeToCSV(file1,file2,result);
    }

    public void writeToCSV(File f1, File f2, double similarity){
        try (PrintWriter writer = new PrintWriter(new FileWriter("results.csv",true))) {
            String fileName1 = f1.getName();
            String fileName2 = f2.getName();

            StringBuilder sb = new StringBuilder();

            sb.append(fileName1);
            sb.append(',');
            sb.append(fileName2);
            sb.append(',');
            sb.append(similarity);
            sb.append('\n');

            writer.write(sb.toString());

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ComparisonResult> displayResults(File f1, File f2, double similarity){
        List<ComparisonResult> displayList = new ArrayList<>();
        if(similarity > 0.5){
            String fileName1 = f1.getName();
            String fileName2 = f2.getName();
            displayList.add(new ComparisonResult(fileName1,fileName2,similarity));
        }
        myMain main = new myMain();
        main.displayResults(displayList);
        return displayList;
    }


}
