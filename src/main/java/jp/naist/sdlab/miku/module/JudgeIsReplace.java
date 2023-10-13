package jp.naist.sdlab.miku.module;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JudgeIsReplace {

    public static Map<String,String> Acount = new HashMap<>();
    public static Map<String,String> Dcount = new HashMap<>();

    public static boolean JudgeReplace(int id, double calcBert, double calcLeven,double distanceLeven) throws SQLException {

        if(distanceLeven==1 || calcBert >= 0.95 || calcLeven >= 0.95){
            System.out.println("Replace ==true");
            return true;

        }else{
            System.out.println("Replace ==false");
            return false;
        }
    }

    public  static void countResultSR(ResultSet rsSR, boolean isParent) throws SQLException {
        ResultSetMetaData metaData = rsSR.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            System.out.print(label + "\t");
        }
        System.out.println();  // 改行
        while (rsSR.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = rsSR.getString(i);
                System.out.print(value + "\t");
            }
            System.out.println();  // 改行
            String value = rsSR.getString(1);
            String label = rsSR.getString(7);
            if(isParent){
                String preValue = Dcount.get(label);
                if(preValue==null){
                    preValue = "0";
                }
                Integer i = Integer.valueOf( Integer.parseInt(preValue)+Integer.parseInt(value) );
                value = i.toString();
                System.out.println("Dcount["+label+"] 値:"+preValue);
                Dcount.put(label,value);
            }else{
                String preValue = Acount.get(label);
                if(preValue==null){
                    preValue = "0";
                }
                Integer i = Integer.valueOf( Integer.parseInt(preValue)+Integer.parseInt(value) );
                value = i.toString();
                System.out.println("Acount["+label+"] 値:"+preValue);
                Acount.put(label,value);
            }
        }
        System.out.println();  // 改行
    }

    public static void countResultA(ResultSet rsA) throws SQLException {
        // カラム数とラベルを表示
        ResultSetMetaData metaData = rsA.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            System.out.print(label + "\t");
        }
        System.out.println();  // 改行
        // データを表示
        while (rsA.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = rsA.getString(i);
                System.out.print(value + "\t");
            }
            System.out.println();
            String value = rsA.getString(1);
            String label = rsA.getString(5);
            if(Acount.get(label) != null ){
                String preValue = Acount.get(label);
                Integer i = Integer.valueOf(Integer.parseInt(value)+Integer.parseInt(preValue));
                value = i.toString();
            }
            Acount.put(label,value);
        }
        System.out.println();  // 改行
    }


    public static void countResultD(ResultSet rsD) throws SQLException {
        // カラム数とラベルを表示
        ResultSetMetaData metaData = rsD.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            System.out.print(label + "\t");
        }
        System.out.println();  // 改行
        // データを表示
        while (rsD.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = rsD.getString(i);
                System.out.print(value + "\t");
            }
            System.out.println();
            String value = rsD.getString(1);
            String label = rsD.getString(5);
            if(Dcount.get(label) != null ){
                String preValue = Dcount.get(label);
                Integer i = Integer.valueOf( Integer.parseInt(value)+ Integer.parseInt(preValue));
                value = i.toString();
            }
            Dcount.put(label,value);
        }
        System.out.println();  // 改行
    }

    public static void countResultR(ResultSet rsR,boolean isParent) throws SQLException {
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
            String value = rsR.getString(1);
            String label = rsR.getString(4);
            if(isParent){
                String preValue = Dcount.get(label);
                if(preValue==null){
                    preValue = "0";
                }
                Integer i = Integer.valueOf( Integer.parseInt(preValue)+Integer.parseInt(value) );
                value = i.toString();
                System.out.println("Dcount["+label+"] 値:"+preValue);
                Dcount.put(label,value);
            }else{
                String preValue = Acount.get(label);
                if(preValue==null){
                    preValue = "0";
                }
                Integer i = Integer.valueOf( Integer.parseInt(preValue)+Integer.parseInt(value) );
                value = i.toString();
                System.out.println("Acount["+label+"] 値:"+preValue);
                Acount.put(label,value);
            }
        }
        System.out.println();  // 改行
    }

    public Map<String,String> getAcount() {
        return this.Acount;
    }
    public Map<String,String> getDcount() {
        return this.Dcount;
    }

}
