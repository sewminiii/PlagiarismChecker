package thePackage;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class myMain extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }
    
    private final TableView<ComparisonResult> resultTable = new TableView<>();
    private final ProgressBar progressBar = new ProgressBar();
    
    @Override
    public void start(Stage stage)
    {
        stage.setTitle("Plagiarism Checker");
        stage.setMinWidth(600);

        // Create toolbar
        Button compareBtn = new Button("Compare...");
        Button stopBtn = new Button("Stop");
        ToolBar toolBar = new ToolBar(compareBtn, stopBtn);
        
        // Set up button event handlers.
        compareBtn.setOnAction(event -> crossCompare(stage));
        stopBtn.setOnAction(event -> stopComparison());
        
        // Initialise progressbar
        progressBar.setProgress(0.0);
        
        TableColumn<ComparisonResult,String> file1Col = new TableColumn<>("File 1");
        TableColumn<ComparisonResult,String> file2Col = new TableColumn<>("File 2");
        TableColumn<ComparisonResult,String> similarityCol = new TableColumn<>("Similarity");
        
        // The following tell JavaFX how to extract information from a ComparisonResult 
        // object and put it into the three table columns.
        file1Col.setCellValueFactory(
            (cell) -> new SimpleStringProperty(cell.getValue().getFile1()) );
            
        file2Col.setCellValueFactory(   
            (cell) -> new SimpleStringProperty(cell.getValue().getFile2()) );
            
        similarityCol.setCellValueFactory(  
            (cell) -> new SimpleStringProperty(
                String.format("%.1f%%", cell.getValue().getSimilarity() * 100.0)) );
          
        // Set and adjust table column widths.
        file1Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        file2Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        similarityCol.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));            
        
        // Add the columns to the table.
        resultTable.getColumns().add(file1Col);
        resultTable.getColumns().add(file2Col);
        resultTable.getColumns().add(similarityCol);

        // Add the main parts of the UI to the window.
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(resultTable);
        mainBox.setBottom(progressBar);
        Scene scene = new Scene(mainBox);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
    private void crossCompare(Stage stage)
    {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        dc.setTitle("Choose directory");
        File directory = dc.showDialog(stage);
        
        System.out.println("Comparing files within " + directory + "...");

        //SEND THIS DIRECTORY TO ANOTHER FUNCTION OR CLASS AND FIND NON EMPTY FILES AND PUT THEM INTO A BLOCKING QUEUE
        FilesLogics filesLogics = new FilesLogics(this);
        filesLogics.offerFiles(directory);
    }
    public void displayResults(ComparisonResult obj){
        System.out.println("print file 1: "+obj.getFile1());
        System.out.println("print file 2: "+obj.getFile2());
        System.out.println("print similarity: "+obj.getSimilarity());

        resultTable.getItems().add(obj);
    }
    public void displayProgress(double progress){
        System.out.println("progress = "+progress);
        progressBar.setProgress(progress);
    }
    
    private void stopComparison()
    {
        System.out.println("Stopping comparison...");
        ComparisonLogics calcObj = new ComparisonLogics(new myMain());
        FilesLogics prodObj = new FilesLogics(this);
        calcObj.end();
        prodObj.end();

    }

    /*public void displayResults(File f1, File f2, double similarity){
        if(similarity > 0.5){
            String fileName1 = f1.getName();
            String fileName2 = f2.getName();
            ComparisonResult comparisonResults = new ComparisonResult(fileName1,fileName2,similarity);
            Platform.runLater(() ->{
                displayResults2(comparisonResults);
            });

        }
    }*/
}
