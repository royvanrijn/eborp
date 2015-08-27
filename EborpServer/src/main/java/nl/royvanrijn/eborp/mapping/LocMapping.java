package nl.royvanrijn.eborp.mapping;

//import mdsj.MDSJ;

import java.util.Arrays;

public class LocMapping {
    public static void main(String[] args) {

        //Input of the MDS algorithm is a map of all distances relative to each other
        String[][] data = new String[][] {
                {"L1","0","10","12", "10", "20", "10"},
                {"L2","10","0","12", "15", "18", "13"},
                {"L3","12","12","0", "12", "22", "18"},
                {"RP1","10", "15", "12", "0", "23", "8"},
                {"RP2","20", "18", "22", "23", "0", "19"},
                {"RP3","10", "13", "18", "8", "19", "0"},
                /*
                * Locatie 1, gemiddelde afstanden: 10, 20, 10
                * Locatie 2, gemiddelde afstanden: 15, 18, 13
                * Locatie 3, gemiddelde afstanden: 12, 22, 18
                */
        };

        // Turn into a table without label (needed for algorithm):
        double[][] input = new double[data.length][data[0].length-1];
        for(int i = 0; i<data.length; i++) {
            for(int x = 1; x<data[i].length; x++) {
                double d = Double.parseDouble(data[i][x]);
                input[i][x-1] = d;
            }
        }

        //Output the table for clarity:
        for(int i = 0; i<input.length; i++) {
            System.out.println(Arrays.toString(input[i]));
        }

        int n=input[0].length;    // number of data objects
//        double[][] output = MDSJ.stressMinimization(input);
        //double[][] output= MDSJ.classicalScaling(input); // apply MDS
        for(int i=0; i<n; i++) {  // output all coordinates
//            System.out.println(data[i][0] +"\t"+((int)output[0][i])+"\t"+(int)(output[1][i]));
        }
    }
}