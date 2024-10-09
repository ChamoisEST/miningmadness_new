package com.chamoisest.miningmadness.util;

import java.text.DecimalFormat;

public class TextUtil {
    public static String formatNumber(int number){
        DecimalFormat df = new DecimalFormat("#.0");

        if(number < 1000) return "" + number;
        else if(number < 1000000){
            return df.format((double)number/1000) + "k";
        }else{
            return df.format((double)number/1000000) + "m";
        }
    }
}
