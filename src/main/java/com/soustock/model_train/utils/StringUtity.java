package com.soustock.model_train.utils;

/**
 * Created by xuyufei on 2018/8/26.
 */
public class StringUtity {

    public static String doubleToStr(double value, int digits){
        String formatStr = "%." + digits + "f";
        String result = String.format(formatStr, value);
        return result;
    }

    public static boolean isNullOrEmpty(String lineStr){
        return (lineStr == null)||(lineStr.length()==0);
    }

//    public static void main(String[] args){
//        System.out.println(doubleToStr(0.223343, 3));
//    }
}
