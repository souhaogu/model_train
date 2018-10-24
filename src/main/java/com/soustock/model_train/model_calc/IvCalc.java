package com.soustock.model_train.model_calc;

import com.soustock.model_train.utils.StringUtity;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xuyufei on 2018/9/2.
 */
public class IvCalc {

    private static class GoodBadPair {

        private int goodNum = 0;
        private int badNum = 0;

        public int getGoodNum() {
            return goodNum;
        }

        public void setGoodNum(int goodNum) {
            this.goodNum = goodNum;
        }

        public int getBadNum() {
            return badNum;
        }

        public void setBadNum(int badNum) {
            this.badNum = badNum;
        }
    }

    private int goodTotalNum = 0;
    private int badTotalNum = 0;

    private TreeMap<Double, GoodBadPair> treeMap = new TreeMap<>();

    public IvCalc(double dMax, double dMin, double dWidth) {
        double dVal = dMin;
        while (dVal <= dMax) {
            treeMap.put(dVal, new GoodBadPair());
            dVal += dWidth;
        }
    }

    public void addSample(double dVar, boolean isGood) {
        if (dVar < 0.0){
            dVar = 0.0;
        }
        Map.Entry<Double, GoodBadPair> entry = treeMap.floorEntry(dVar);
        if (entry != null) {
            GoodBadPair goodBadPair = entry.getValue();
            if (isGood) {
                goodBadPair.setGoodNum(goodBadPair.getGoodNum() + 1);
                goodTotalNum++;
            } else {
                goodBadPair.setBadNum(goodBadPair.getBadNum() + 1);
                badTotalNum++;
            }
        }
    }

    public double getIv() throws Exception {
        double dIv = 0.0;
        for (Map.Entry<Double, GoodBadPair> entry : treeMap.entrySet()) {
            int dGoodNum = entry.getValue().getGoodNum();
            int dBadNum = entry.getValue().getBadNum();
            if ((dGoodNum <= 0) || (dGoodNum <= 0)) {
                throw new Exception("sample num is zero, good:" + dGoodNum + ", bad:" + dBadNum);
            }
            double dGoodPer = dGoodNum * 1.0000 / goodTotalNum;
            double dBadPer = dBadNum * 1.0000 / badTotalNum;
            double dOdds = dGoodNum * 1.0000 / dBadNum;
            double dWOE = Math.log(dGoodPer / dBadPer);
            System.out.println(String.format("key:%s, good:%d, bad:%d, good per vs bad per: %s, odds: %s, WOE: %s",
                    StringUtity.doubleToStr(entry.getKey(), 2),
                    dGoodNum,
                    dBadNum,
                    StringUtity.doubleToStr(dGoodPer * 1.0000 / dBadPer, 4),
                    StringUtity.doubleToStr(dOdds, 4),
                    StringUtity.doubleToStr(dWOE, 4)));
            dIv += dWOE * (dGoodPer - dBadPer);
        }
        return dIv;
    }
}
