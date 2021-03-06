package thePackage;

import java.util.concurrent.Callable;

public class Calculations implements Callable<Double> {
    private char[] file1, file2;

    //constructor
    public Calculations(char[] file1, char[] file2){
        this.file1 = file1;
        this.file2 = file2;
    }
    //this is the LCS algorithm
    @Override
    public Double call() throws Exception {
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
        double result = (matches * 2) / (file1.length + file2.length);
        return result;

    }

}
