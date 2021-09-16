package thePackage;

import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class ComparisonLogics {
    private final ExecutorService cpuService = Executors.newCachedThreadPool();
    private final ExecutorService ioService = Executors.newCachedThreadPool();
    private char[] charArray1, charArray2;
    private int counter = 0;
    myMain ui;
    //Thread thread;
    public double result;

    //public ComparisonLogics(){}

    public ComparisonLogics(myMain ui){
        this.ui = ui;
    }

    public void combinations(List<File> fileList){

        System.out.println("file list size = "+fileList.size());
        cpuService.execute(() -> {
            for(int i = 0; i < fileList.size(); i++) {
                for (int j = i + 1; j < fileList.size(); j++) {
                    File file1 = fileList.get(i);
                    File file2 = fileList.get(j);
                    counter = counter +1;
                    System.out.println("file 1 = " + file1 + " file 2 = " + file2);
                    try {
                        convertToCharArray(file1,file2);
                        calcProgress(fileList.size(),counter);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void calcProgress(int noOfFiles, int count){
        double noOfComparison = 0.5 * (noOfFiles * noOfFiles - noOfFiles);
        double progress = count / noOfComparison;
        Formatter formatter = new Formatter();
        formatter.format("%.2f", progress);

        Platform.runLater(() -> {
            this.ui.displayProgress(progress);
        });
    }

    public void convertToCharArray(File file1, File file2) throws ExecutionException, InterruptedException {

        StringBuilder theStringForFile1;
        Scanner scanner1;
        try {
            scanner1 = new Scanner(file1);
            theStringForFile1 = new StringBuilder(scanner1.nextLine());
            while (scanner1.hasNextLine()) {
                theStringForFile1.append("\n").append(scanner1.nextLine());
            }
            charArray1 = theStringForFile1.toString().toCharArray();
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //calling call() method in Calculations class and get the return value (similarity score)
        Future<Double> future = cpuService.submit(new Calculations(charArray1,charArray2));

        try {
            result = future.get();

            System.out.println("result in future : "+future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        displayResults(file1,file2,result);
        writeToCSV(file1,file2,result);

    }

    public void displayResults(File f1, File f2, double similarity){
        if(similarity > 0.5){
            String fileName1 = f1.getName();
            String fileName2 = f2.getName();
            ComparisonResult comparisonResults = new ComparisonResult(fileName1,fileName2,similarity);
            Platform.runLater(() ->{
                ui.displayResults(comparisonResults);
            });

        }
    }

    public void writeToCSV(File f1, File f2, double similarity){
        ioService.execute(()->{
            try (PrintWriter writer = new PrintWriter(new FileWriter("results.csv",true))) {
                String fileName1 = f1.getName();
                String fileName2 = f2.getName();

                StringBuilder sb = new StringBuilder();
                System.out.println("write similarity : "+similarity);
                sb.append(fileName1);
                sb.append(',');
                sb.append(fileName2);
                sb.append(',');
                sb.append(similarity);
                sb.append('\n');

                writer.write(sb.toString());

            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void end(){
        System.out.println("shut down cpu intensive thread pool");
        System.out.println("shut down IO intensive thread pool");
        cpuService.shutdownNow();
        ioService.shutdownNow();
    }

}
