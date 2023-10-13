package jp.naist.sdlab.miku.module.commit;

import java.sql.ResultSet;

public class Replace {

    public ResultSet rsA;
    public ResultSet rsD;
    public ResultSet rsR;
    public ResultSet rsSR;
    public Replace(ResultSet rsA, ResultSet rsD, ResultSet rsR, ResultSet rsSR){
        this.rsA = rsA;
        this.rsD = rsD;
        this.rsR = rsR;
        this.rsSR = rsSR;
    }

}
