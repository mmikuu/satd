package jp.naist.sdlab.miku.module.db;


//SQP Import

import jp.naist.sdlab.miku.module.commit.Replace;
import weka.knowledgeflow.Data;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class SATDDatabaseManager extends DatabaseManager {

    public static Statement statement;
    public static Statement statementD;
    public static Statement statementR;
    public static Statement statementSR;



    public SATDDatabaseManager() throws SQLException {
        super();
        initDB();
    }
    public SATDDatabaseManager(String db) throws SQLException {
        super(db);
        initDB();
    }
    public void initDB() throws SQLException {
        //init database
        statement = connection.createStatement();
        statementD = connection.createStatement();
        statementR = connection.createStatement();
        statementSR = connection.createStatement();
        connection.setAutoCommit(false);
        statement.executeUpdate("ALTER TABLE satd_calc_list DROP COLUMN isReplace");
        addColumn();
    }


    public Replace countAddSatd(boolean isParent) throws SQLException {

        ResultSet rsD = statementD.executeQuery(this.createAddDeleteSQL("DELETE", isParent));
        ResultSet rsA = statement.executeQuery(this.createAddDeleteSQL("ADDED", isParent));
        ResultSet rsR = statementR.executeQuery(this.createReplaceSQL(isParent));
        ResultSet rsSR = statementSR.executeQuery(this.createSingleReplaceSQL(isParent));

        return new Replace(rsA,rsD,rsR,rsSR);
    }

    private String createSingleReplaceSQL(boolean isParent) {
        String sql;
        if (isParent){//TODO: Needs to be simplified
            sql = "SELECT COUNT(*) AS count, "
                    + "parent_satd_list.id AS pId, "
                    + "parent_satd_list.hashcode AS pHash, "
                    + "child_satd_list.hashcode AS cHash, "
                    + "parent_satd_list.type AS ptype, "
                    + "commit_list.releasePart AS releasePart "
                    + "FROM parent_satd_list "
                    + "JOIN commit_list ON parent_satd_list.commitId = commit_list.commitId "
                    + "LEFT JOIN child_satd_list ON parent_satd_list.hashcode = child_satd_list.hashcode "
                    + "WHERE child_satd_list.hashcode IS NULL AND parent_satd_list.type = 'REPLACE' "
                    + "GROUP BY commit_list.releasePart ";

        }else{
            sql = "SELECT COUNT(*) AS count, "
                    + "child_satd_list.id AS cId, "
                    + "child_satd_list.hashcode AS cHash, "
                    + "parent_satd_list.hashcode AS pHash, "
                    + "child_satd_list.type AS ctype, "
                    + "commit_list.releasePart AS releasePart "
                    + "FROM child_satd_list "
                    + "JOIN commit_list ON child_satd_list.commitId = commit_list.commitId "
                    + "LEFT JOIN parent_satd_list ON child_satd_list.hashcode = parent_satd_list.hashcode "
                    + "WHERE  parent_satd_list.hashcode IS NULL AND child_satd_list.type = 'REPLACE' "
                    + "GROUP BY commit_list.releasePart ";
        }
        return sql;
    }

    private String createReplaceSQL(boolean isParent) {
        String select, column;
        if (isParent){
            select = "satd_calc_list.pId AS pId, ";
            column = "satd_calc_list.pId";
        }else{
            select = "satd_calc_list.cId AS cId, ";
            column = "satd_calc_list.cId";
        }

        return "SELECT COUNT(*) AS count, "
                + "satd_calc_list.id AS id, "
                + select
                + "commit_list.releasePart AS releasePart "
                + "FROM satd_calc_list "
                + "JOIN commit_list ON " + column + " = commit_list.id AND satd_calc_list.isReplace = '0' "
                + "GROUP BY commit_list.releasePart ";
    }

    public String createAddDeleteSQL(String ADDorDelete, boolean isParent){
        String table;
        if (isParent){
            System.out.println("isParent");
            table = "parent_satd_list";
        }else{
            table = "child_satd_list";
        }
        return  "SELECT COUNT(*) AS count, "
                + table + ".id AS id, "
                + table + ".commitId AS cId, "
                + table + ".type AS type, "
                + "commit_list.releasePart AS releasePart "
                + "FROM " + table + " "
                + "JOIN commit_list ON " + table + ".commitId = commit_list.commitId AND "+ table +".type = '"+ADDorDelete+"' "
                + "GROUP BY commit_list.releasePart ";
    }


    public ResultSet getHashDate() throws SQLException {
        String hash_sql = "SELECT * from satd_calc_list";
        return statement.executeQuery(hash_sql);
    }

    public void addColumn() throws SQLException {
        String column_sql = "ALTER TABLE satd_calc_list ADD isReplace BOOLEAN AFTER calc_leven_long";
        statement.execute(column_sql);
    }

//    satd_calc_list(pId,cId,pContent,cContent,calc_bert,calc_leven,calc_leven_long,isType) VALUES (?,?,?,?,?,?,?,?)";
    public void addDate(int id,boolean isType){
        String isTypeSQL = "update satd_calc_list set isReplace = ? where id = ?";
        try(PreparedStatement ps = connection.prepareStatement(isTypeSQL)){
            ps.setBoolean(1,isType);
            ps.setInt(2,id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
