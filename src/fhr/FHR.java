/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fhr;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 *
 * @author Ammar
 */
public class FHR {

    public static Double fhr[] = new Double[30000];
    public static Double zeros[] = new Double[30000];
    public static Double fhr2[] = new Double[15000];
    public static Double UC[] = new Double[30000];
    public static int linesCount;
    //blv
    public static int BLVcount = 0;
    public static int BLVpeaks[] = new int[55];
    public static int BLVstarts[] = new int[55];
    public static int BLVends[] = new int[55];
    public static String BLVtype[] = new String[55];

    /**
     * @param args the command line arguments
     */
    public FHR() throws IOException {
        //reading the file :        
        BufferedReader br = new BufferedReader(new FileReader("1042.csv"));
        //BufferedReader br2 = new BufferedReader(new FileReader("1001.csv"));
        try {
            StringBuilder sb;
            String line = br.readLine();
            line = br.readLine();       // to delete first line
            //linesCount=(int)br2.lines().count();           
            //fhr= new Double[linesCount+1];

            while (line != null) {
                sb = new StringBuilder();
                sb.append(line);
                //sb.append(System.lineSeparator());
                String[] parts = sb.toString().split(",");
                int index = Integer.parseInt(parts[0]);
                fhr[index] = Double.parseDouble(parts[1]);

                UC[index] = Double.parseDouble(parts[2]);
                line = br.readLine();
                linesCount++;
            }
//            for (int i = 0, j = 0; i < linesCount; i += 16, j++) {
//                fhr2[j] = fhr[i];
//                //System.out.println(j + " | " + fhr2[j]);
//            }
            for (int i = 0; i < linesCount; i++) {
            zeros[i] = 0.0;
        }
            fixZeros();
            //acc();
            BLv();
        } finally {
            br.close();
        }
    }

    public static void BLv() {
        int pointer = 0;
        boolean inBLv = false;
        int BLvStart = 0;
        int peak = 0;
        int bl = 140;
        //while (pointer < linesCount) {
        while (pointer < linesCount) {
            if (inBLv) {
                if (fhr[peak] < fhr[pointer]) {// finding new peak (if any)vinside BLV
                    peak = pointer;
                }
                if (fhr[pointer] < bl) {
                    if (pointer - BLvStart > 480) {//at the end of each BLv
                        inBLv = false;
                        BLVpeaks[BLVcount] = peak;
                        BLVstarts[BLVcount] = BLvStart;
                        BLVends[BLVcount] = pointer;
                        checkBLVType(BLVcount); // to put a value : absent , minimal , moderate or marked
                        //checkBLVType2(BLVcount); // depends on reference
                        System.out.println((BLVcount+1) + "-start:" + BLvStart / 4 + "\t|end:" + pointer / 4 + "\t|t:" + (pointer - BLvStart) / 4 + "s"
                                + "\tp:" + fhr[peak] + " @ " + peak + "(" + (fhr[peak] - bl) + ")" + "\tTYPE:" + BLVtype[BLVcount]);
                        BLVcount++; // BLV counted !

                    } else {// Lower than BL in less than 2 min
                        inBLv = false;
                    }
                }
            } else {
                if (fhr[pointer] > bl) {// at the bigining of blv
                    inBLv = true;
                    BLvStart = pointer;
                    peak = pointer;
                }
            }
            pointer++;
        }
        System.out.println("# of BLVs:" + BLVcount);
    }

    public static void fixZeros() {
        int start = 0;
        int end = 0;
        int sum1 = 0;
        int sum2 = 0;
        int k;
        double avg;
        int cnt = 0;
        
        for (int i = 0; i < linesCount; i++) {
            if (fhr[i] == 0) {
                //System.out.println(++cnt);
                start = i;
                sum1 = 0;
                sum2 = 0;
                avg = 0;
                k = 0;
                while (i < linesCount && fhr[i] == 0) {
                    i++;
                }
                if (start != i) { // to check if it's one zero or field of zeros
                    end = i - 1;
                } else {//one point miising
                    continue; }
                // calculating avg of last 100 before zeros
                for (int j = start - 100; j > 0 && j < start; j++) {
                    sum1 += fhr[j];
                    k++; }
                // calculating avg of last 100 after zeros
                for (int j = end + 1; j < linesCount && j < end + 100; j++) {
                    sum2 += fhr[j];
                    k++;
                }
                avg = (sum1 + sum2) / k;
                for (int j = start; j <= end; j++) {
                    zeros[j] = avg;
                    fhr[j] = avg;
                }
            }
        }
    }

    public static void acc() {
        int counter = 0;
        int window = 0;
        int pointer = 0;
        int AccStartTime = 0;
        int AccAboveMinStartTime = 0;
        int extraBL = 15;
        int peaks[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        boolean isAcc = false;

        //calculating BL
        int cnt = 0;
        double BL = 0;
        for (int i = 0; i < linesCount; i++) {
            // System.out.println( i);
            if (fhr[i] < 160 && fhr[i] > 110) {
                BL += fhr[i];
                cnt++;
            }
        }
        BL = BL / cnt;
        System.out.println("BL :" + BL);
         //System.out.println("test:" + fhr2[20]);

        //finding Accelerations
        while (window + 120 < (linesCount / 16)) {
            //System.out.println("window:" + window);
            if (fhr2[window] < BL + 2 && fhr2[window] > BL - 2
                    && fhr2[window + 120] < BL + 2 && fhr2[window + 120] > BL - 2) {
                if (AccStartTime < window) {// first time
                    //System.out.println("maybe acc:" + window + "| " + fhr2[window]);
                    AccStartTime = window;
                }
            }
            if (fhr2[window] > BL + extraBL) {
                //System.out.println("maybe acc 2 :" + window + "| " + fhr2[window]);

                if (AccAboveMinStartTime + 1 < window) {
                    AccAboveMinStartTime = window;
                    pointer = window;
                    //System.out.println("1");

                } else {
                    //System.out.println("2");
                    isAcc = true;

                    while (fhr2[pointer] >= BL + extraBL && pointer < AccAboveMinStartTime + 120) {
                        if (fhr2[peaks[counter]] < fhr2[pointer]) {
                            //finding peak
                            peaks[counter] = pointer;
                            //System.out.println("new peak :" + pointer + "|" + fhr2[pointer]);
                        }
                        //checking if it still above extraBL
                        if (fhr2[pointer] < BL + extraBL && pointer < AccAboveMinStartTime + extraBL) {
                            isAcc = false;
                            window = pointer;
                            break;
                        }
                        //check if acc or BL variability
                        if (isAcc
                                && fhr2[pointer] > BL + extraBL - 1
                                && fhr2[pointer] < BL + extraBL + 1 //&& pointer<AccAboveMinStartTime+ extraBL
                                )//ACCeleration
                        {
                            System.out.println((counter + 1) + "-Acc @:" + AccStartTime + "|" + AccAboveMinStartTime + "->" + pointer + "|" + peaks[counter]);
                            counter++;
                            window = pointer;
                        } else//BL variability
                        {
                            //System.out.println("BL v at :" + AccStartTime +"to:"+pointer);
                        }

                        pointer++;
                    }
                    window = pointer;
                    continue;
                }
            }
            window++;

        }
        System.out.println("number of accelerations :" + counter);
    }

    public static void checkBLVType(int n) {// depends on the vedio
        double x = fhr[BLVpeaks[n]] - 140;
        if (x < 1) {
            BLVtype[n] = "Absent";
        } else if (x < 5) {
            BLVtype[n] = "Minimal";
        } else if (x < 25) {
            BLVtype[n] = "Moderate";
        } else {
            BLVtype[n] = "Marked";
        }
    }

    public static void checkBLVType2(int n) {// depends on the references
        double x = fhr[BLVpeaks[n]] - 140;
        int duration = BLVends[n] - BLVstarts[n];
        if (x <= 5 && duration < 40 * 4 || x >= 6 && x <= 25) {
            BLVtype[n] = "Moderate";
        } else if (x <= 5 && duration >= 40 * 4 || x < 5 && duration > 80 * 4) {
            BLVtype[n] = "Absent or Minimal";
        } else if (x >= 25 && duration > 10 * 4) {
            BLVtype[n] = "Marked";
        } else {
            BLVtype[n] = "unKown";
        }
    }

    public static void main(String[] args) throws IOException {
        new FHR();
        Points ps = new Points();
        ps.setVisible(true);
    }

}
