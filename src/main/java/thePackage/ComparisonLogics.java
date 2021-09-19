package thePackage;

import javafx.application.Platform;

import java.io.*;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class ComparisonLogics {

    private final ExecutorService cpuService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
    private final ExecutorService ioService = Executors.newCachedThreadPool();

    private char[] charArray1, charArray2;
    private int counter = 0;
    private myMain ui;
    public double result;
    public ComparisonLogics(myMain ui){
        this.ui = ui;
    }

    //this method is for making the file combinations
    //a file should be compared with each file but should avoid redundant
    /* that is achieved by,
     * in the list, each file should compare only with the files which are in the highest indexes
     * than the comparing file's index of the list
     * Assume, there are 3 files in the list, so the file in the index 0 should be compared with only index 1 and 2,
     * and the file in the index 1 should be compared with only index 2
     * because index 1 has already compared with index 0 in the previous round
     * */
    public void fileCombinations(List<File> fileList){
        cpuService.execute(() -> {
            for(int i = 0; i < fileList.size(); i++) {
                for (int j = i + 1; j < fileList.size(); j++) {
                    File file1 = fileList.get(i);
                    File file2 = fileList.get(j);
                    counter = counter +1;
                    //System.out.println("file 1 = " + file1 + " file 2 = " + file2);
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

    //this method is for put the file contents to a char array in order to pass for comparing
    public void convertToCharArray(File file1, File file2) throws ExecutionException, InterruptedException {
        StringBuilder theStringForFile1;
        Scanner scanner1;
        try {
            //read the file one and put them into a char array
            scanner1 = new Scanner(file1);
            theStringForFile1 = new StringBuilder(scanner1.nextLine());
            while (scanner1.hasNextLine()) {
                theStringForFile1.append("\n").append(scanner1.nextLine());
            }
            charArray1 = theStringForFile1.toString().toCharArray();
            scanner1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder theStringForFile2 = new StringBuilder();
        Scanner scanner2 = null;
        try {
            //read the file two and put them into another char array
            scanner2 = new Scanner(file2);
            theStringForFile2 = new StringBuilder(scanner2.nextLine());
            while (scanner2.hasNextLine()) {
                theStringForFile2.append("\n").append(scanner2.nextLine());
            }
            charArray2 = theStringForFile2.toString().toCharArray();
            scanner2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //calling call() method in Calculations class and get the return value (similarity score)
        Future<Double> future = cpuService.submit(new Calculations(charArray1, charArray2));

        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        displayResults(file1, file2, result);
        writeToCSV(file1, file2, result);

    }

    //this method is for passing the comparison results to the main class in order to display in the interface
    public void displayResults(File f1, File f2, double similarity){
        //check the similarity score
        //if the score is greater than 0.5 only the results are displayed
        if(similarity > 0.5){
            String fileName1 = f1.getName();
            String fileName2 = f2.getName();
            ComparisonResult comparisonResults = new ComparisonResult(fileName1,fileName2,similarity);
            //call a method in main class
            Platform.runLater(() ->{
                ui.displayResults(comparisonResults);
            });

        }
    }
    //this method is for calculate the current progress
    public void calcProgress(int noOfFiles, int count){
        cpuService.execute(() -> {
            //get the number of comparisons by no of files in the list to be compared
            double noOfComparison = 0.5 * (noOfFiles * noOfFiles - noOfFiles);
            //count is the current comparison number which is comparing at the moment
            double progress = count / noOfComparison;
            Formatter formatter = new Formatter();
            formatter.format("%.2f", progress);
            //call a method in main class
            Platform.runLater(() -> {
                this.ui.displayProgress(progress);
            });
        });

    }

    //this method is for writing the results to a csv file
    public void writeToCSV(File f1, File f2, double similarity){
        ioService.execute(() -> {
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
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    //stop thread pools
    public void end(){
        Thread.currentThread().interrupt();
        this.cpuService.shutdownNow();
        System.out.println("shut down cpu intensive thread pool");
        this.ioService.shutdownNow();
        System.out.println("shut down io intensive thread pool");
    }

}
