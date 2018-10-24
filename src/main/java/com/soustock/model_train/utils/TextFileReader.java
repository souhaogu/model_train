package com.soustock.model_train.utils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by xuyufei on 2018/8/26.
 *
 */
public class TextFileReader {

    private FileReader fileReader = null;
    private BufferedReader bufferedReader = null;
    private final static Charset defaultCharset = Charset.forName("UTF-8");
    private String fileName;
    public TextFileReader(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
    }

    public boolean isExists(){
        File file = new File(fileName);
        return file.exists();
    }

    public void beginReader() throws Exception {
        File file = new File(fileName);
        if (!file.exists()){
            throw new Exception("file is not exits." + fileName);
        }

        fileReader = new FileReader(file);
        bufferedReader = new BufferedReader(fileReader);
    }

    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    public void endRead() throws IOException {
        if (null != bufferedReader) {
            bufferedReader.close();
        }

        if (null != fileReader) {
            fileReader.close();
        }
    }
}
