package jp.naist.sdlab.miku.main;


//SQP Import

import jp.naist.sdlab.miku.module.ReplaceChecker;
import jp.naist.sdlab.miku.module.ReplaceCounter;
import jp.naist.sdlab.miku.module.commit.Replace;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;

import java.sql.*;
import java.util.Map;

public class MainCountSATD {
    public static SATDDatabaseManager dbManager;
    public static ReplaceCounter replaceCounter;

    public static void main(String[] args) throws Exception {
        dbManager = new SATDDatabaseManager();
        replaceCounter = new ReplaceCounter();

        ResultSet rsH = dbManager.getHashDate();//similarityのデータを取得
        while(rsH.next()) {
            int id = rsH.getInt("id");
            boolean isReplace = checkReplace(rsH);//similarityをもとにreplaceか判定
            dbManager.addDate(id, isReplace);

        }

        Replace replace = dbManager.countAddSatd(true);//DBをもとに各TYPE(Add,Delete,Replace)をReplace Partごとに取得
        countSATD(replace,true);//取得したReplace partごとに分けた各TYPEを集計

        replace = dbManager.countAddSatd(false);//DBをもとに各TYPE(Add,Delete,Replace)をReplace Partごとに取得
        countSATD(replace,false);//取得したReplace partごとに分けた各TYPEを集計

        Map<String, String> countAdd = getCountAddDelete("Add");
        Map<String, String> countDelete = getCountAddDelete("Del");

        countPrint(countAdd,countDelete);

    }

    public static Map<String,String> getCountAddDelete(String AddorDel) {
        if(AddorDel.equals("Add")){
            return  replaceCounter.getAddCount();//Acountを取得
        }else{
            return replaceCounter.getDeleteCount();//Dcountを取得
        }

    }

    public static void countPrint(Map<String, String> countAdd, Map<String, String> countDelete) {

        System.out.println("Acount" + countAdd);
        System.out.println("Dcount" + countDelete);

        int addInTR = Integer.parseInt(countAdd.get("TR1")) + Integer.parseInt(countAdd.get("TR2"));
        int addInRR = Integer.parseInt(countAdd.get("RR1")) + Integer.parseInt(countAdd.get("RR2")) + Integer.parseInt(countAdd.get("RR3")) + Integer.parseInt(countAdd.get("RR4")) + Integer.parseInt(countAdd.get("RR5")) + Integer.parseInt(countAdd.get("RR6")) + Integer.parseInt(countAdd.get("RR7")) + Integer.parseInt(countAdd.get("RR8"));
        int deleteInTR = Integer.parseInt(countDelete.get("TR1")) + Integer.parseInt(countDelete.get("TR2"));
        int deleteInRR = Integer.parseInt(countDelete.get("RR1")) + Integer.parseInt(countDelete.get("RR2")) + Integer.parseInt(countDelete.get("RR3")) + Integer.parseInt(countDelete.get("RR4")) + Integer.parseInt(countDelete.get("RR5")) + Integer.parseInt(countDelete.get("RR6")) + Integer.parseInt(countDelete.get("RR7")) + Integer.parseInt(countDelete.get("RR8"));

        System.out.println("Atr:" + addInTR + " Arr:" + addInRR);
        System.out.println("Dtr:" + deleteInTR + " Drr:" + deleteInRR);
    }

    public static boolean checkReplace(ResultSet rsH) throws SQLException {
        double calcBert = rsH.getDouble("calc_bert");
        double calcLeven = rsH.getDouble("calc_leven");
        double distanceLeven = rsH.getDouble("calc_leven_long");
        return ReplaceChecker.check(calcBert,calcLeven,distanceLeven);
    }

    public static void countSATD(Replace replace, boolean isParent) throws SQLException {
        if(replaceCounter == null){
            System.out.println("replaceCounter is null");
        }
        if(replace.rsA != null){
            try{
                replaceCounter.countResultADD(replace.rsA);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        if(replace.rsD != null) {
            try {
                replaceCounter.countResultDELETE(replace.rsD);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        if(replace.rsR != null){
            try{
                replaceCounter.countResultReplace(replace.rsR,isParent);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        if(replace.rsSR != null){
            try{
                replaceCounter.countResultSingleReplace(replace.rsSR,isParent);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    }
}
