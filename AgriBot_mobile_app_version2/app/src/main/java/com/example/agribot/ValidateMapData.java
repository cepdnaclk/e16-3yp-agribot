package com.example.agribot;

public class ValidateMapData {

    public static boolean validate(String rowLen, String seedGap, String rowNo, String rowGap) {

        int rowLenInt = Integer.parseInt(rowLen);
        int seedGapInt = Integer.parseInt(seedGap);
        int rowNoInt = Integer.parseInt(rowNo);
        int rowGapInt = Integer.parseInt(rowGap);

        int minRowLen = 100;
        int maxRowLen = 10000;
        int minRowGap = 10;
        int maxRowGap = 500;
        int minRowNo = 1;
        int maxRowNo = 500;
        int minSeedGap = 10;
        int maxSeedGap = 500;

        if (rowLenInt < minRowLen || rowLenInt > maxRowLen) return false;

        if (seedGapInt < minSeedGap || seedGapInt > maxSeedGap) return false;

        if (rowNoInt < minRowNo || rowNoInt > maxRowNo) return false;

        if (rowGapInt < minRowGap || rowGapInt > maxRowGap) return false;

        return true;
    }

    public static boolean isDataEmpty(String rowLen, String seedGap, String rowNo, String rowGap) {

        if (rowLen.equals("") || seedGap.equals("") || rowNo.equals("") || rowGap.equals("")) {
            return true;
        }
        return false;
    }
}
