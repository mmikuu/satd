package jp.naist.sdlab.miku.main;


//SQP Import

import jp.naist.sdlab.miku.module.ReplaceChecker;
import jp.naist.sdlab.miku.module.ReplaceCounter;
import jp.naist.sdlab.miku.module.ResultSummarizer;
import jp.naist.sdlab.miku.module.commit.Replace;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;

import java.sql.*;
import java.util.Map;

public class MainCountSATD {


    public static void main(String[] args) throws Exception {
        SATDDatabaseManager dbManager = new SATDDatabaseManager();
        ResultSummarizer summarizer = new ResultSummarizer(dbManager);
        summarizer.run();
        //summarizer.countPrint();

    }


}
