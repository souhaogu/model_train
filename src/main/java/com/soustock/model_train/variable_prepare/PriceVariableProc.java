package com.soustock.model_train.variable_prepare;

import com.soustock.model_train.adapter.StockQuoteAdapter;
import com.soustock.model_train.common.Constants;
import com.soustock.model_train.utils.DateUtity;
import com.soustock.model_train.utils.StringUtity;
import com.soustock.model_train.utils.TextFileWriter;
import com.soustock.model_train.vo.PriceVariableVo;

import java.text.ParseException;
import java.util.*;

/**
 * Created by xuyufei on 2018/8/26.
 */
public class PriceVariableProc {

    /**
     * 往前推的年份
     */
    private final static int VARIABLE_YEAR_NUM = 7;

    /**
     * lABEL的年份
     * @throws Exception
     */
    private final static int LABEL_YEAR_NUM = 3;

    public static void doProc() throws Exception {
        StockQuoteAdapter stockQuoteAdapter = new StockQuoteAdapter();
        try {
            stockQuoteAdapter.open();
            List<String> stockCodeList = stockQuoteAdapter.getAllStockCodesForTrain();
            int total = stockCodeList.size();
            int index = 0;
            for (String stockCode : stockCodeList){
                TreeMap<String, Double> stockQuoteMap = stockQuoteAdapter.getStockQuoteMapOfBehindFuquan(stockCode);
                calcPriceVariableAndSave(stockCode, stockQuoteMap);
                index ++;
                System.out.println(String.format("progress:%d/%d", index, total));
            }
        } finally {
            stockQuoteAdapter.close();
        }
    }

    private static void calcPriceVariableAndSave(String stockCode, TreeMap<String, Double> stockQuoteMap) throws Exception {
        String fileName = Constants.Price_Variable_Directory + "//" + stockCode;
        TextFileWriter textFileWriter = new TextFileWriter(fileName);
        try {
            //若文件已经存在，则跳过
            if (textFileWriter.isExists()) return;

            textFileWriter.beginWrite();
            String maxDate = stockQuoteMap.lastKey();
            String endDate = DateUtity.getPreYear(maxDate, LABEL_YEAR_NUM);
            String minDate = stockQuoteMap.firstKey();
            String bgnDate = DateUtity.getNextYear(minDate, VARIABLE_YEAR_NUM);
            if (bgnDate.compareTo(endDate) < 0) {
                NavigableMap<String, Double> analiableMap = stockQuoteMap.subMap(bgnDate, true, endDate, true);
                for (Map.Entry<String, Double> entry : analiableMap.entrySet()) {
                    String tradeDate = entry.getKey();
                    Double price = entry.getValue();
                    double dIncreasePer = getPriceIncreasePer(tradeDate, price, stockQuoteMap);
                    double dPricePosPer = getPriceTimePer(tradeDate, price, stockQuoteMap);
                    double dPriceTimePer = getPricePosPer(tradeDate, price, stockQuoteMap);
                    String lineStr = tradeDate + "," +
                            StringUtity.doubleToStr(dIncreasePer, 2) + "," +
                            StringUtity.doubleToStr(dPricePosPer, 2) + "," +
                            StringUtity.doubleToStr(dPriceTimePer, 2);
                    textFileWriter.writeLine(lineStr);
                }
            }
        } finally {
            textFileWriter.endWrite();
        }
    }

    /**
     * 价格的位置比例
     * @param tradeDate
     * @param price
     * @param stockQuoteMap
     * @return
     * @throws ParseException
     */
    private static double getPricePosPer(String tradeDate, Double price, TreeMap<String, Double> stockQuoteMap) throws ParseException {
        String dateStrOfSevenYears = DateUtity.getPreYear(tradeDate, VARIABLE_YEAR_NUM);
        String dateStrOfLastYears = DateUtity.getPreYear(tradeDate, 1);
        NavigableMap<String, Double> subMapOfLastSevenYears = stockQuoteMap.subMap(dateStrOfSevenYears, true, dateStrOfLastYears, true);
        double maxPrice = price.doubleValue();
        double minPrice = price.doubleValue();
        for (Double tempPrice: subMapOfLastSevenYears.values()){
            double dTemp = tempPrice.doubleValue();
            if (dTemp > maxPrice){
                maxPrice = dTemp;
            }
            if (dTemp < minPrice){
                minPrice = dTemp;
            }
        }
        return (price - minPrice) * 1.00/(maxPrice - minPrice);
    }

    /**
     * 价格的时间比例
     * @param tradeDate
     * @param price
     * @param stockQuoteMap
     * @return
     * @throws ParseException
     */
    private static double getPriceTimePer(String tradeDate, Double price, TreeMap<String, Double> stockQuoteMap) throws ParseException {
        String dateStrOfSevenYears = DateUtity.getPreYear(tradeDate, VARIABLE_YEAR_NUM);
        String dateStrOfLastYears = DateUtity.getPreYear(tradeDate, 1);
        NavigableMap<String, Double> subMapOfLastSevenYears = stockQuoteMap.subMap(dateStrOfSevenYears, true, dateStrOfLastYears, true);
        int totalDayCount = subMapOfLastSevenYears.size();
        int lowerDayCount = 0;
        for (Double tempPrice: subMapOfLastSevenYears.values()){
            if (tempPrice.compareTo(price) <= 0){
                lowerDayCount ++;
            }

        }
        return lowerDayCount * 1.00/totalDayCount;
    }

    /**
     * 涨跌幅
     * @param tradeDate
     * @param price
     * @param stockQuoteMap
     * @return
     * @throws ParseException
     */
    //    private static double getPriceIncreasePer(String tradeDate, Double price, TreeMap<String, Double> stockQuoteMap) throws Exception {
    //        String dateStrOfNextYear = DateUtity.getNextYear(tradeDate, LABEL_YEAR_NUM);
    //        int dayCount = (int) Math.round(LABEL_YEAR_NUM * 365 * 0.66);
    //        String bgnDate = DateUtity.getPreDay(dateStrOfNextYear, dayCount);
    //        NavigableMap<String, Double> subMapOfLastSevenYears = stockQuoteMap.subMap(bgnDate, true, dateStrOfNextYear, true);
    //        if (subMapOfLastSevenYears.size() > 0) {
    //            double priceSum = 0.0;
    //            int count = 0;
    //            for (Double tempPrice : subMapOfLastSevenYears.values()) {
    //                double dTemp = tempPrice.doubleValue();
    //                priceSum += dTemp;
    //                count++;
    //            }
    //            double dPriceAvg = priceSum / count;
    //            return dPriceAvg * 1.0000 / price - 1.0000;
    //        } else {
    //            double dPriceAvg = stockQuoteMap.floorEntry(dateStrOfNextYear).getValue().doubleValue();
    //            return dPriceAvg * 1.0000 / price - 1.0000;
    //        }
    //    }

    private static double getPriceIncreasePer(String tradeDate, Double price, TreeMap<String, Double> stockQuoteMap) throws Exception {
        String dateStrOfNextYear = DateUtity.getNextYear(tradeDate, LABEL_YEAR_NUM);
        int dayCount = LABEL_YEAR_NUM * 365;
        String bgnDate = DateUtity.getPreDay(dateStrOfNextYear, dayCount);
        NavigableMap<String, Double> subMapOfLastSevenYears = stockQuoteMap.subMap(bgnDate, true, dateStrOfNextYear, true);

        if (subMapOfLastSevenYears.size() > 0) {
            double dMax = price.doubleValue();
            for (Double tempPrice : subMapOfLastSevenYears.values()) {
                double dTemp = tempPrice.doubleValue();
                if (dTemp > dMax){
                    dMax = dTemp;
                }
            }

            if (dMax >= price * 2) {
                double dFuturePrice = dMax * 0.8;
                return dFuturePrice * 1.0000 / price - 1.0000;
            }
        }
        double dPriceOfFuture = stockQuoteMap.floorEntry(dateStrOfNextYear).getValue().doubleValue();
        return dPriceOfFuture * 1.0000 / price - 1.0000;
    }

}
