package jp.naist.sdlab.miku.main;

import jp.naist.sdlab.miku.module.SATD;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class main_repair {
    static String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
    public static void main(String[] args) throws Exception {
        List<String> satdRepair = new ArrayList<>();
        int repairCount = 0;
        //FileWriter commitBlameWrite = new FileWriter("commitBlame.csv");
        /*
         * 入力 csvファイルの中身取得
         */
        try{
            Reader in = new FileReader("commitBlame.csv");
            Iterable<CSVRecord> records =
                    CSVFormat.EXCEL.withHeader().parse(in);
            for(CSVRecord record : records){
                satdRepair.add(record.get("days"));
                System.out.println(satdRepair.get(0));
            }
        } catch(Exception e){
            System.out.println(e);
        }
        for(String days : satdRepair){
            if (days.equals("0")) {
                repairCount +=1;
            }
        }
        System.out.println(repairCount);
    }


    }
