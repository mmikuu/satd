package jp.naist.sdlab.miku.module;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;

public class Filereader {
    // CSVファイルを2次元配列に読み込み
    public static void main(String[] args) {
        try {
            Reader in = new FileReader("added_satd.csv");
            Iterable<CSVRecord> records =
                    CSVFormat.EXCEL.withHeader().parse(in);
            for (CSVRecord record : records) {
                String filename = record.get("filename");
                String line = record.get("line");
                String commitID = record.get("commitID");
                String content = record.get("content");

                System.out.println(filename);
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
