package com.soustock.model_train.adapter;

import com.soustock.model_train.common.Constants;
import com.soustock.model_train.utils.DateUtity;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xuyufei on 2018/8/27.
 * 获取股票行情
 */
public class StockQuoteAdapter {

    private Connection conn = null;

    public void open() throws SQLException {
        conn = DriverManager.getConnection(Constants.STOCK_QUOTE_CONNECT_STR);
    }

    /**
     * 获得所有需要建模的股票代码
     *
     * @return
     * @throws ParseException
     * @throws SQLException
     */
    public List<String> getAllStockCodesForTrain() throws ParseException, SQLException {
        List<String> stockCodeList = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        try {
            String todayStr = "20180810";
            String dateStrOfEightYears = DateUtity.getPreYear(todayStr, 8);
            String sql = String.format("SELECT stock_code FROM stock_basic WHERE list_date < '%s'", dateStrOfEightYears);
            //String sql = String.format("SELECT stock_code FROM stock_basic WHERE stock_code = 'SH600000' and list_date < '%s'", dateStrOfEightYears);
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String stockCode = rs.getString("stock_code");
                stockCodeList.add(stockCode);
            }
            return stockCodeList;
        } finally {
            if (rs != null)
                rs.close();
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * 得到某个股票的复权价格列表(后复权)
     *
     * @param stockCode
     * @return
     * @throws SQLException
     */
    public TreeMap<String, Double> getStockQuoteMapOfBehindFuquan(String stockCode) throws SQLException {
        TreeMap<String, Double> originPriceMap = getStockQuoteMapOfOrigin(stockCode);
        TreeMap<String, Double> fuquanFactorMap = getFuquanFactorMap(stockCode);

        TreeMap<String, Double> resultMap = new TreeMap<>();
        for (Map.Entry<String, Double> entry : originPriceMap.entrySet()) {
            String tradeDate = entry.getKey();
            Double originPrice = entry.getValue();

            double factor = 1.0;
            Map.Entry<String, Double> factorEntry = fuquanFactorMap.floorEntry(tradeDate);
            if (factorEntry != null) {
                factor = factorEntry.getValue();
            }
            resultMap.put(tradeDate, originPrice * factor);
        }
        return resultMap;
    }


    /**
     * 获得某个股票的原始价格
     *
     * @param stockCode
     * @return
     * @throws SQLException
     */
    private TreeMap<String, Double> getStockQuoteMapOfOrigin(String stockCode) throws SQLException {
        TreeMap<String, Double> stockQuoteMap = new TreeMap<>();
        String sql = String.format("select trade_date, close_price from day_quote where stock_code = '%s' order by trade_date asc", stockCode);
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String dateStr = rs.getString("trade_date");
                Double closePrice = rs.getDouble("close_price");
                stockQuoteMap.put(dateStr, closePrice);
            }
            return stockQuoteMap;
        } finally {
            if (rs != null)
                rs.close();
            if (statement != null) {
                statement.close();
            }
        }
    }


    /**
     * 获得某个股票的复权因子
     *
     * @param stockCode
     * @return
     * @throws SQLException
     */
    private TreeMap<String, Double> getFuquanFactorMap(String stockCode) throws SQLException {
        TreeMap<String, Double> fuquanMap = new TreeMap<>();
        String sql = String.format("select trade_date, factor FROM fuquan_factor where stock_code = '%s' order by trade_date asc", stockCode);
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String dateStr = rs.getString("trade_date");
                Double factor = rs.getDouble("factor");
                fuquanMap.put(dateStr, factor);
            }
            return fuquanMap;
        } finally {
            if (rs != null)
                rs.close();
            if (statement != null) {
                statement.close();
            }
        }
    }


    public void close() throws SQLException {
        if (null != conn) {
            conn.close();
        }
    }

}
