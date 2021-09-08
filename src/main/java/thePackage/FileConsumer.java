package thePackage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileConsumer {

    private FileProducer producer;
    private List<File> fileList = new ArrayList<>();
    private File file1, file2;

    public FileConsumer()
    {

    }

    public void getFiles() {
      /*  fileList = producer.returnFileList();
        System.out.println("file list form consumer = "+fileList);

        for(int i = 0; i < fileList.size(); i++){
            for(int j = i+1 ; j <= fileList.size(); j++){
                file1 = fileList.get(i);
                file2 = fileList.get(j);
                System.out.println("file 1 = "+file1 + " file 2 = "+file2);
            }
        }*/
    }

    public double calcSimilarity(char[] file1, char[] file2){
        int[][] subsolutions = new int[file1.length + 1][file2.length + 1];
        Boolean[][] directionLeft = new Boolean[file1.length + 1][file2.length + 1];
        subsolutions[0][0] = 0;

        for(int i = 1; i <= file1.length ;i++) {
            for(int j = 1; j <= file2.length; j++)
            {
                if(file1[i - 1] == file2[j - 1])
                    subsolutions[i][j] = subsolutions[i-1][j-1] +1;
                else if(subsolutions[i-1][j] > subsolutions[i][j-1])
                {
                    subsolutions[i][j] = subsolutions[i-1][j];
                    directionLeft[i][j] = true;
                }
                else
                {
                    subsolutions[i][j] = subsolutions[i][j - 1];
                    directionLeft[i][j] = false;
                }
            }
        }
        double matches = 0;
        int i = file1.length;
        int j = file2.length;

        while( i > 0 && j > 0)
        {
            if(file1[i - 1] == file2[j - 1])
            {
                matches += 1;
                i -= 1;
                j -= 1;
            }
            else if(directionLeft[i][j])
                i -= 1;

            else
                j -= 1;
        }
        System.out.println("matches = "+matches);
        System.out.println("file1 length = "+file1.length);
        System.out.println("file2 length = "+file2.length);
        double result = (matches * 2) / (file1.length + file2.length);
        System.out.println("similarity score = "+result);
        return result;

    }

}
