package com.soustock.model_train.vo;

import com.soustock.model_train.utils.StringUtity;

/**
 * Created by xuyufei on 2018/9/2.
 * 价格变量Vo
 */
public class PriceVariableVo {

    private String tradeDate;
    private double dIncreasePer;
    private double dPricePosPer;
    private double dPriceTimePer;

    public double getdIncreasePer() {
        return dIncreasePer;
    }

    public double getdPricePosPer() {
        return dPricePosPer;
    }

    public double getdPriceTimePer() {
        return dPriceTimePer;
    }

    public String getTradeDate() {
        return tradeDate;
    }


    private PriceVariableVo(String tradeDate, double dIncreasePer,double dPricePosPer,double dPriceTimePer){
        this.tradeDate = tradeDate;
        this.dIncreasePer = dIncreasePer;
        this.dPricePosPer = dPricePosPer;
        this.dPriceTimePer = dPriceTimePer;
    }

    public static PriceVariableVo parse(String str){
        if (!StringUtity.isNullOrEmpty(str)){
            String[] arr = str.split(",");
            if (arr.length == 4){
                String trade_date = arr[0];
                double dIncreasePer = Double.parseDouble(arr[1]);
                double dPricePosPer = Double.parseDouble(arr[2]);
                double dPriceTimePer = Double.parseDouble(arr[3]);
                return new PriceVariableVo(trade_date, dIncreasePer, dPricePosPer, dPriceTimePer);
            }
        }
        return null;
    }

    public static String toStr(PriceVariableVo priceVariableVo){
        return priceVariableVo.getTradeDate() + "," +
                StringUtity.doubleToStr(priceVariableVo.dIncreasePer, 4) + "," +
                StringUtity.doubleToStr(priceVariableVo.dPricePosPer, 4) + "," +
                StringUtity.doubleToStr(priceVariableVo.dPriceTimePer, 4);
    }
}

