package com.soustock.model_train.utils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by xuyufei on 2018/8/26.
 *
 */
public class TextFileWriter {

//    private FileOutputStream outputStream = null;
//    private BufferedOutputStream bufferedOutputStream = null;
    private FileWriter fileWriter = null;
    private BufferedWriter bufferedWriter = null;
    private final static Charset defaultCharset = Charset.forName("UTF-8");
    private String fileName;
    public TextFileWriter(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
    }

    public boolean isExists(){
        File file = new File(fileName);
        return file.exists();
    }

    public void beginWrite() throws Exception {
        File file = new File(fileName);
        if (file.exists()){
            throw new Exception("file already exits." + fileName);
        }

        String tmpFileName = fileName + ".tmp";
        File tmpFile = new File(tmpFileName);
        if (tmpFile.exists()){
            tmpFile.delete();
        }

        fileWriter = new FileWriter(tmpFile);
        bufferedWriter = new BufferedWriter(fileWriter);
//        outputStream = new FileOutputStream(tmpFile);
//        bufferedOutputStream = new BufferedOutputStream(outputStream);
    }

    public void writeLine(String lineStr) throws IOException {
        bufferedWriter.write(lineStr);
        bufferedWriter.newLine();
    }

    public void endWrite() throws IOException {
        if (null != bufferedWriter) {
            bufferedWriter.flush();
            bufferedWriter.close();
        }

        if (null != fileWriter) {
            fileWriter.close();
        }

        String tmpFileName = fileName + ".tmp";
        File tmpFile = new File(tmpFileName);
        if (tmpFile.exists()){
            tmpFile.renameTo(new File(fileName));
        }
    }
}
