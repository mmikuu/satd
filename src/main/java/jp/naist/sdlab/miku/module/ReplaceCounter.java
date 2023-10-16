package jp.naist.sdlab.miku.module;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReplaceCounter {

    public Map<String,String> Acount;
    public Map<String,String> Dcount;
    public ReplaceCounter(){
        Acount = new HashMap<>();
        Dcount = new HashMap<>();
    }
    public void countResultADD(ResultSet rs) throws SQLException {
        countResult(rs, Acount);
    }
    public void countResultDELETE(ResultSet rs) throws SQLException {
        countResult(rs, Dcount);
    }
    public void countResult(ResultSet rs, Map<String, String> count) throws SQLException {
        // カラム数とラベルを表示
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            System.out.print(label + "\t");
        }
        System.out.println();  // 改行
        // データを表示
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                System.out.print(value + "\t");
            }
            System.out.println();
            String value = rs.getString(1);
            String label = rs.getString(5);
            if(count.get(label) != null ){
                String preValue = count.get(label);
                int i = Integer.parseInt(value) + Integer.parseInt(preValue);
                value = Integer.toString(i);
            }
            count.put(label,value);
        }
        System.out.println();  // 改行
    }

    public void countResultSingleReplace(ResultSet rsSR, boolean isParent) throws SQLException {
        countResultReplace(rsSR, isParent, 1, 6);

    }
    public void countResultReplace(ResultSet rsR, boolean isParent) throws SQLException {
        countResultReplace(rsR, isParent, 1, 5);
    }

    public void countResultReplace(ResultSet rsR, boolean isParent, int valueIndex, int labelIndex) throws SQLException {
        ResultSetMetaData metaData = rsR.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            System.out.print(label + "\t");
        }
        System.out.println();  // 改行
        while (rsR.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = rsR.getString(i);
                System.out.print(value + "\t");
            }
            System.out.println();  // 改行
            String value = rsR.getString(valueIndex);
            String label = rsR.getString(labelIndex);
            if(isParent){
                String preValue = Dcount.get(label);
                if(preValue==null){
                    preValue = "0";
                }
                int i = Integer.parseInt(preValue) + Integer.parseInt(value);
                value = Integer.toString(i);
                System.out.println("Dcount["+label+"] 値:"+preValue);
                Dcount.put(label,value);
            }else{
                String preValue = Acount.get(label);
                if(preValue==null){
                    preValue = "0";
                }
                int i = Integer.parseInt(preValue) + Integer.parseInt(value);
                value = Integer.toString(i);
                System.out.println("Acount["+label+"] 値:"+preValue);
                Acount.put(label,value);
            }
        }
        System.out.println();  // 改行
    }

    public Map<String,String> getAddCount() {
        return this.Acount;
    }
    public Map<String,String> getDeleteCount() {
        return this.Dcount;
    }
}
