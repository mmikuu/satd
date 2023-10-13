package jp.naist.sdlab.miku.module;


//SQP Import

import jp.naist.sdlab.miku.module.commit.Replace;

import java.sql.*;


public class SATDDatabaseManager {
    public static Connection connection;
    public static Statement statement;
    public static Statement statementD;
    public static Statement statementR;
    public static Statement statementSR;




    public SATDDatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/satd_replace_db",
                "me",
                "goma");
        //init database
        statement = connection.createStatement();
        statementD = connection.createStatement();
        statementR = connection.createStatement();
        statementSR = connection.createStatement();
        connection.setAutoCommit(false);
        statement.executeUpdate("ALTER TABLE satd_calc_list DROP COLUMN isReplace");
        addColumn();
    }


    public  static Replace count_AD_satd(boolean isParent) throws SQLException {
        String added_sql = "SELECT COUNT(*) AS count, "
                + "child_satd_list.id AS id, "
                + "child_satd_list.commitId AS cId, "
                + "child_satd_list.type AS type, "
                + "commit_list.releasePart AS releasePart "
                + "FROM child_satd_list "
                + "JOIN commit_list ON child_satd_list.commitId = commit_list.commitId AND child_satd_list.type = 'ADDED' "
                + "GROUP BY commit_list.releasePart ";

        String deleted_sql = "SELECT COUNT(*) AS count, "
                + "child_satd_list.id AS id, "
                + "child_satd_list.commitId AS cId, "
                + "child_satd_list.type AS type, "
                + "commit_list.releasePart AS releasePart "
                + "FROM child_satd_list "
                + "JOIN commit_list ON child_satd_list.commitId = commit_list.commitId AND child_satd_list.type = 'DELETE' "
                + "GROUP BY commit_list.releasePart ";

        String replace_sql = "SELECT COUNT(*) AS count, "
                + "satd_calc_list.id AS id, "
                + "satd_calc_list.cId AS cId, "
                + "commit_list.releasePart AS releasePart "
                + "FROM satd_calc_list "
                + "JOIN commit_list ON satd_calc_list.cId = commit_list.commitId AND satd_calc_list.isReplace = '0' "
                + "GROUP BY commit_list.releasePart ";

        String singleReplace_sql = "SELECT COUNT(*) AS count, "
                + "child_satd_list.id AS cId, "
                + "child_satd_list.hashcode AS cHash, "
                + "parent_satd_list.hashcode AS pHash, "
                + "parent_satd_list.type AS ptype, "
                + "child_satd_list.type AS ctype, "
                + "commit_list.releasePart AS releasePart "
                + "FROM child_satd_list "
                + "JOIN commit_list  ON child_satd_list.commitId = commit_list.commitId "
                + "LEFT JOIN parent_satd_list ON child_satd_list.hashcode = parent_satd_list.hashcode AND child_satd_list.type = 'REPLACE' AND parent_satd_list.type = 'REPLACE' "
                + "WHERE  parent_satd_list.hashcode = 'null' "
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
                    + "JOIN commit_list ON satd_calc_list.pId = commit_list.commitId AND satd_calc_list.isReplace = '0' "
                    + "GROUP BY commit_list.releasePart ";

            singleReplace_sql = "SELECT COUNT(*) AS count, "
                    + "parent_satd_list.id AS pId, "
                    + "parent_satd_list.hashcode AS pHash, "
                    + "child_satd_list.hashcode AS cHash, "
                    + "parent_satd_list.type AS ptype, "
                    + "child_satd_list.type AS ctype, "
                    + "commit_list.releasePart AS releasePart "
                    + "FROM parent_satd_list "
                    + "JOIN commit_list ON parent_satd_list.commitId = commit_list.commitId "
                    + "LEFT JOIN child_satd_list ON child_satd_list.hashcode = parent_satd_list.hashcode AND parent_satd_list.type = 'REPLACE' AND child_satd_list.type = 'REPLACE' "
                    + "WHERE child_satd_list.hashcode = 'null' "
                    + "GROUP BY commit_list.releasePart ";

        }

        ResultSet  rsA = statement.executeQuery(added_sql);
        ResultSet rsD = statementD.executeQuery(deleted_sql);
        ResultSet rsR = statementR.executeQuery(replace_sql);
        ResultSet rsSR = statementSR.executeQuery(singleReplace_sql);

        return new Replace(rsA,rsD,rsR,rsSR);
    }


    public static ResultSet getHashDate() throws SQLException {
        String hash_sql = "SELECT * from satd_calc_list";
        ResultSet resultSet = statement.executeQuery(hash_sql);
        return resultSet;
    }

    private static void addColumn() throws SQLException {
        String column_sql = "ALTER TABLE satd_calc_list ADD isReplace BOOLEAN AFTER calc_leven_long";
        statement.execute(column_sql);
    }

//    satd_calc_list(pId,cId,pContent,cContent,calc_bert,calc_leven,calc_leven_long,isType) VALUES (?,?,?,?,?,?,?,?)";
    public static void addDate(int id,boolean isType){
        String istype_sql = "update satd_calc_list set isReplace = ? where id = ?";
        try(PreparedStatement ps = connection.prepareStatement(istype_sql)){
            ps.setBoolean(1,isType);
            ps.setInt(2,id);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
