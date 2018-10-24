package com.soustock.model_train.model_calc;

import com.soustock.model_train.common.Constants;
import com.soustock.model_train.utils.StringUtity;
import com.soustock.model_train.utils.TextFileReader;
import com.soustock.model_train.vo.PriceVariableVo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyufei on 2018/9/2.
 */
public class IVProc {

    private final static double dRegion = 0.01;

    public static void doCalc() throws Exception {
        IvCalc pricePosPerCalc = new IvCalc(1.0, 0.0, 0.05);
        IvCalc priceTimePerCalc = new IvCalc(1.0, 0.0, 0.05);

        List<String> fileStrList = getFilePathList();
        for (String filePathStr: fileStrList) {
            TextFileReader textFileReader = new TextFileReader(filePathStr);
            try {
                textFileReader.beginReader();
                String lineStr = textFileReader.readLine();
                while (!StringUtity.isNullOrEmpty(lineStr)) {
                    PriceVariableVo priceVariableVo = PriceVariableVo.parse(lineStr);
                    if (priceVariableVo != null) {
                        boolean bGood = priceVariableVo.getdIncreasePer() > 0.3;
                        pricePosPerCalc.addSample(priceVariableVo.getdPricePosPer(), bGood);
                        priceTimePerCalc.addSample(priceVariableVo.getdPriceTimePer(), bGood);
                    }
                    lineStr = textFileReader.readLine();
                }
            } finally {
                textFileReader.endRead();
            }
        }

        System.out.println("price pos per iv:" + pricePosPerCalc.getIv());
        System.out.println("price time per iv:" + priceTimePerCalc.getIv());
    }

    private static List<String> getFilePathList(){
        List<String> filePathList = new ArrayList<>();
        File file = new File(Constants.Price_Variable_Directory);
        String[] fileStrArr = file.list();
        for (String fileStr: fileStrArr){
            filePathList.add(Constants.Price_Variable_Directory + "\\" + fileStr);
        }
        return filePathList;
    }

}
