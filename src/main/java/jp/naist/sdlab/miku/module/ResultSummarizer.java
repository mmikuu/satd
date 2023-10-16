package jp.naist.sdlab.miku.module;

import jp.naist.sdlab.miku.module.commit.Replace;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ResultSummarizer {
    private SATDDatabaseManager dbManager;
    private ReplaceCounter replaceCounter;

    public Map<String, String> countAdd;
    public Map<String, String> countDelete;


    public ResultSummarizer(SATDDatabaseManager dbManager) throws SQLException {
        this.dbManager = dbManager;
        this.replaceCounter = new ReplaceCounter();
        this.countAdd = new HashMap<>();
        this.countDelete = new HashMap<>();
    }

    private void storeReplaceData(SATDDatabaseManager dbManager) throws SQLException {
        ResultSet rsH = dbManager.getHashDate();//similarityのデータを取得
        while(rsH.next()) {
            int id = rsH.getInt("id");
            boolean isReplace = checkReplace(rsH);//similarityをもとにreplaceか判定
            dbManager.addDate(id, isReplace);

        }

    }

    public void run() throws SQLException {
        storeReplaceData(dbManager);

        Replace replace = dbManager.countAddSatd(true);//DBをもとに各TYPE(Add,Delete,Replace)をReplace Partごとに取得
        countSATD(replace,true);//取得したReplace partごとに分けた各TYPEを集計
        updateReplace(replace.rsR,true);
        updateReplace(replace.rsSR,true);



        replace = dbManager.countAddSatd(false);//DBをもとに各TYPE(Add,Delete,Replace)をReplace Partごとに取得
        countSATD(replace,false);//取得したReplace partごとに分けた各TYPEを集計
        updateReplace(replace.rsR,false);
        updateReplace(replace.rsSR,false);

        countAdd = getCountAddDelete( "Add");
        countDelete = getCountAddDelete( "Del");

    }

    private void updateReplace(ResultSet rs,boolean isParent) throws SQLException {
        while(rs.next()){
            int Id;
            if(isParent){
                Id = rs.getInt("pid");
            }else{
                Id = rs.getInt("cId");
            }

            System.out.println("aaa");
            dbManager.addResultDate(Id,rs,isParent);
        }
    }

    private Map<String,String> getCountAddDelete(String AddorDel) {
        if(AddorDel.equals("Add")){
            return  replaceCounter.getAddCount();//Acountを取得
        }else{
            return replaceCounter.getDeleteCount();//Dcountを取得
        }

    }

    public void countPrint() {

        System.out.println("Acount" + countAdd);
        System.out.println("Dcount" + countDelete);

        int addInTR = Integer.parseInt(countAdd.get("TR1")) + Integer.parseInt(countAdd.get("TR2"));
        int addInRR = Integer.parseInt(countAdd.get("RR1")) + Integer.parseInt(countAdd.get("RR2")) + Integer.parseInt(countAdd.get("RR3")) + Integer.parseInt(countAdd.get("RR4")) + Integer.parseInt(countAdd.get("RR5")) + Integer.parseInt(countAdd.get("RR6")) + Integer.parseInt(countAdd.get("RR7")) + Integer.parseInt(countAdd.get("RR8"));
        int deleteInTR = Integer.parseInt(countDelete.get("TR1")) + Integer.parseInt(countDelete.get("TR2"));
        int deleteInRR = Integer.parseInt(countDelete.get("RR1")) + Integer.parseInt(countDelete.get("RR2")) + Integer.parseInt(countDelete.get("RR3")) + Integer.parseInt(countDelete.get("RR4")) + Integer.parseInt(countDelete.get("RR5")) + Integer.parseInt(countDelete.get("RR6")) + Integer.parseInt(countDelete.get("RR7")) + Integer.parseInt(countDelete.get("RR8"));

        System.out.println("Atr:" + addInTR + " Arr:" + addInRR);
        System.out.println("Dtr:" + deleteInTR + " Drr:" + deleteInRR);
    }

    private boolean checkReplace(ResultSet rsH) throws SQLException {
        double calcBert = rsH.getDouble("calc_bert");
        double calcLeven = rsH.getDouble("calc_leven_long");
        double distanceLeven = rsH.getDouble("calc_leven");
        return ReplaceChecker.check(calcBert,calcLeven,distanceLeven);
    }

    private void countSATD(Replace replace, boolean isParent) throws SQLException {
        replaceCounter.countResultADD(replace.rsA);
        replaceCounter.countResultDELETE(replace.rsD);
        replaceCounter.countResultReplace(replace.rsR,isParent);
        replaceCounter.countResultSingleReplace(replace.rsSR,isParent);
    }
}
