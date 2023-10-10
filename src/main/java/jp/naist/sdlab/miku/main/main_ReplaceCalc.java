package jp.naist.sdlab.miku.main;


//SQP Import
import jnr.ffi.annotations.In;
import org.easymock.bytebuddy.description.field.FieldDescription;
import org.jcodings.util.Hash;
import org.jruby.RubyBoolean;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class main_ReplaceCalc {

    public static void main(String[] args) throws Exception {
        //database mysql connect
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/satd_replace_db",
                "me",
                "goma");
        //init database //TODO 今あるSATDのDBを書き換えるだけだから新しくDB作らなくてもいい？
        Statement statement = connection.createStatement();
        connection.setAutoCommit(false);
        statement.executeUpdate("ALTER TABLE satd_calc_list DROP COLUMN isReplace");
        addColumn(statement);
        get_hash_matcged_data(connection,statement);
        Map<String,String> Acount = new HashMap<>();
        Map<String,String> Dcount = new HashMap<>();
        count_AD_satd(connection,statement,true,Acount,Dcount);
        count_AD_satd(connection,statement,false,Acount,Dcount);
        System.out.println("Acount"+Acount);
        System.out.println("Dcount"+Dcount);
        Integer Atr = Integer.valueOf(Acount.get("TR1"))+Integer.valueOf(Acount.get("TR2"));
        Integer Arr = Integer.valueOf(Acount.get("RR1"))+Integer.valueOf(Acount.get("RR2"))+Integer.valueOf(Acount.get("RR3"))+Integer.valueOf(Acount.get("RR4"))+Integer.valueOf(Acount.get("RR5"))+Integer.valueOf(Acount.get("RR6"))+Integer.valueOf(Acount.get("RR7"))+Integer.valueOf(Acount.get("RR8"));
        Integer Dtr = Integer.valueOf(Dcount.get("TR1"))+Integer.valueOf(Dcount.get("TR2"));
        Integer Drr = Integer.valueOf(Dcount.get("RR1"))+Integer.valueOf(Dcount.get("RR2"))+Integer.valueOf(Dcount.get("RR3"))+Integer.valueOf(Dcount.get("RR4"))+Integer.valueOf(Dcount.get("RR5"))+Integer.valueOf(Dcount.get("RR6"))+Integer.valueOf(Dcount.get("RR7"))+Integer.valueOf(Dcount.get("RR8"));
        System.out.println("Atr:"+Atr+" Arr:"+Arr);
        System.out.println("Dtr:"+Dtr+" Drr:"+Drr);
    }
    private static void count_AD_satd(Connection connection, Statement stmt,boolean isParent,Map<String,String> Acount,Map<String,String> Dcount) throws SQLException {
        String added_sql = "SELECT COUNT(*) AS count, "
                + "child_satd_list.id AS id, "
                + "child_satd_list.commitId AS cId, "
                + "child_satd_list.type AS type, "
                + "commit_list.releasePart AS releasePart "
                + "FROM child_satd_list "
                + "JOIN commit_list ON child_satd_list.commitId = commit_list.commitId AND child_satd_list.type = 'ADDED'"
                + "GROUP BY commit_list.releasePart ";

        String deleted_sql = "SELECT COUNT(*) AS count, "
                + "child_satd_list.id AS id, "
                + "child_satd_list.commitId AS cId, "
                + "child_satd_list.type AS type, "
                + "commit_list.releasePart AS releasePart "
                + "FROM child_satd_list "
                + "JOIN commit_list ON child_satd_list.commitId = commit_list.commitId AND child_satd_list.type = 'DELETE'"
                + "GROUP BY commit_list.releasePart ";

        String replace_sql = "SELECT COUNT(*) AS count, "
                + "satd_calc_list.id AS id, "
                + "satd_calc_list.cId AS cId, "
                + "commit_list.releasePart AS releasePart "
                + "FROM satd_calc_list "
                + "JOIN commit_list ON satd_calc_list.cId = commit_list.commitId AND isReplace = '0' "
                + "GROUP BY commit_list.releasePart ";

        if(isParent){
            System.out.println("isParent");
            added_sql =  "SELECT COUNT(*) AS count, "
                    + "parent_satd_list.id AS id, "
                    + "parent_satd_list.commitId AS pId, "
                    + "parent_satd_list.type AS type, "
                    + "commit_list.releasePart AS releasePart "
                    + "FROM parent_satd_list "
                    + "JOIN commit_list ON parent_satd_list.commitId = commit_list.commitId AND parent_satd_list.type = 'ADDED'"
                    + "GROUP BY commit_list.releasePart ";

            deleted_sql = "SELECT COUNT(*) AS count, "
                    + "parent_satd_list.id AS id, "
                    + "parent_satd_list.commitId AS pId, "
                    + "parent_satd_list.type AS type, "
                    + "commit_list.releasePart AS releasePart "
                    + "FROM parent_satd_list "
                    + "JOIN commit_list ON parent_satd_list.commitId = commit_list.commitId AND parent_satd_list.type = 'DELETE'"
                    + "GROUP BY commit_list.releasePart ";

            replace_sql = "SELECT COUNT(*) AS count, "
                    + "satd_calc_list.id AS id, "
                    + "satd_calc_list.pId AS pId, "
                    + "commit_list.releasePart AS releasePart "
                    + "FROM satd_calc_list "
                    + "JOIN commit_list ON satd_calc_list.pId = commit_list.commitId AND isReplace = '0' "
                    + "GROUP BY commit_list.releasePart ";
        }

        ResultSet rsA = stmt.executeQuery(added_sql);
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

        ResultSet rsD = stmt.executeQuery(deleted_sql);
        // カラム数とラベルを表示
        metaData = rsD.getMetaData();
        columnCount = metaData.getColumnCount();
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

        ResultSet rsR = stmt.executeQuery(replace_sql);
        metaData = rsR.getMetaData();
        columnCount = metaData.getColumnCount();
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


    private static void get_hash_matcged_data(Connection conn, Statement stmt) throws SQLException {
        String hash_sql = "SELECT * from satd_calc_list";
        ResultSet resultSet = stmt.executeQuery(hash_sql);

        while(resultSet.next()){
            int id = resultSet.getInt("id");
            double sentence_bert = resultSet.getDouble("calc_bert");
            double leven = resultSet.getDouble("calc_leven");
            double leven_long = resultSet.getDouble("calc_leven_long");
            /* TODO:そもそもマッチしないREPLACEってある可能性ありそうじゃない？
             * REPLACEだけど，片っぽがSATDじゃないからハッシュがおんなじのなくてスルーしているやつとかありそう
             */
            if(leven==1 || sentence_bert >= 0.95 || leven_long >= 0.95){
                addDate(conn,id,sentence_bert,leven,leven_long,true);

            }else{
                addDate(conn,id,sentence_bert,leven,leven_long,false);
//                Acount +=1;
            }
        }
    }

    private static void addColumn(Statement stmt) throws SQLException {
        String column_sql = "ALTER TABLE satd_calc_list ADD isReplace BOOLEAN AFTER calc_leven_long";
        stmt.execute(column_sql);
    }
//    satd_calc_list(pId,cId,pContent,cContent,calc_bert,calc_leven,calc_leven_long,isType) VALUES (?,?,?,?,?,?,?,?)";
    private static void addDate(Connection conn,int id, double sentence_bert,double leven,double leven_long,boolean isType){
        String istype_sql = "update satd_calc_list set isReplace = ? where id = ?";
        try(PreparedStatement ps = conn.prepareStatement(istype_sql)){
            ps.setBoolean(1,isType);
            ps.setInt(2,id);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
