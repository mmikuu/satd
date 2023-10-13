package jp.naist.sdlab.miku.main;


//SQP Import

import jp.naist.sdlab.miku.module.JudgeIsReplace;
import jp.naist.sdlab.miku.module.commit.Replace;
import jp.naist.sdlab.miku.module.SATDDatabaseManager;

import java.sql.*;
import java.util.Map;


public class MainCountSATD {
    public static SATDDatabaseManager dbManager;
    public static JudgeIsReplace judgeManager;

    public static void main(String[] args) throws Exception {
        dbManager = new SATDDatabaseManager();
        judgeManager = new JudgeIsReplace();

        ResultSet rsH = dbManager.getHashDate();//simiralityのデータを取得
        ManageJudgeReplace(rsH);//similarityをもとにreplaceか判定

        Replace replace = dbManager.count_AD_satd(true);//DBをもとに各TYPE(Add,Delete,Replace)をReleacePartごとに取得
        ManageTypeCount(replace,true);//取得したReplacepartごとに分けた各TYPEを集計

        replace = dbManager.count_AD_satd(false);//DBをもとに各TYPE(Add,Delete,Replace)をReleacePartごとに取得
        ManageTypeCount(replace,false);//取得したReplacepartごとに分けた各TYPEを集計


        Map<String, String> Acount = judgeManager.getAcount();//Acountを取得
        Map<String, String> Dcount = judgeManager.getDcount();//Dcountを取得

        System.out.println("Acount" + Acount);
        System.out.println("Dcount" + Dcount);

        Integer Atr = Integer.valueOf(Acount.get("TR1")) + Integer.valueOf(Acount.get("TR2"));
        Integer Arr = Integer.valueOf(Acount.get("RR1")) + Integer.valueOf(Acount.get("RR2")) + Integer.valueOf(Acount.get("RR3")) + Integer.valueOf(Acount.get("RR4")) + Integer.valueOf(Acount.get("RR5")) + Integer.valueOf(Acount.get("RR6")) + Integer.valueOf(Acount.get("RR7")) + Integer.valueOf(Acount.get("RR8"));
        Integer Dtr = Integer.valueOf(Dcount.get("TR1")) + Integer.valueOf(Dcount.get("TR2"));
        Integer Drr = Integer.valueOf(Dcount.get("RR1")) + Integer.valueOf(Dcount.get("RR2")) + Integer.valueOf(Dcount.get("RR3")) + Integer.valueOf(Dcount.get("RR4")) + Integer.valueOf(Dcount.get("RR5")) + Integer.valueOf(Dcount.get("RR6")) + Integer.valueOf(Dcount.get("RR7")) + Integer.valueOf(Dcount.get("RR8"));

        System.out.println("Atr:" + Atr + " Arr:" + Arr);
        System.out.println("Dtr:" + Dtr + " Drr:" + Drr);
    }

    private static void ManageJudgeReplace(ResultSet rsH) throws SQLException {
        while(rsH.next()){

            int id = rsH.getInt("id");
            double calcBert = rsH.getDouble("calc_bert");
            double calcLeven = rsH.getDouble("calc_leven");
            double distanceLeven = rsH.getDouble("calc_leven_long");

            boolean isReplace = judgeManager.JudgeReplace(id,calcBert,calcLeven,distanceLeven);

            if(isReplace){
                dbManager.addDate(id,true);
            }else{
                dbManager.addDate(id,false);
            }
        }
    }

    private static void ManageTypeCount(Replace replace,boolean isParent) throws SQLException {

        judgeManager.countResultA(replace.rsA);
        judgeManager.countResultD(replace.rsD);
        judgeManager.countResultR(replace.rsR,isParent);
        judgeManager.countResultSR(replace.rsSR,isParent);
    }
}
