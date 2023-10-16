package jp.naist.sdlab.miku.module.db;

import jp.naist.sdlab.miku.module.SATD;
import jp.naist.sdlab.miku.module.commit.ChangedFile;
import jp.naist.sdlab.miku.module.commit.Chunk;
import jp.naist.sdlab.miku.module.commit.Commit;
import jp.naist.sdlab.miku.module.commit.LineChange;

import java.sql.*;

public class CommitDatabaseManager extends DatabaseManager{
    public Statement statement;
    public CommitDatabaseManager() throws SQLException {
        super();
        initDB();
    }
    public CommitDatabaseManager(String db) throws SQLException {
        super(db);
        initDB();
    }
    public void initDB() throws SQLException {
        //init database
        statement  = connection.createStatement();
        connection.setAutoCommit(false);
        //testでは，消さんでいいかもこのTable
        statement.executeUpdate("DROP TABLE IF EXISTS parent_satd_list");
        statement.executeUpdate("DROP TABLE IF EXISTS child_satd_list");
        statement.executeUpdate("DROP TABLE IF EXISTS chunk_parent_list");
        statement.executeUpdate("DROP TABLE IF EXISTS chunk_child_list");
        statement.executeUpdate("DROP TABLE IF EXISTS commit_list");
    }


    public void addCommitData(Commit childCommit) {
        if(childCommit.parentCommitIds.size()>1){
            return;//対象外
        }

        System.out.println("bbb");
        String commit_sql = "insert into commit_list (commitId,commitDate,releasePart,fileName,commitComment) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(commit_sql)) {
            ps.setString(1, childCommit.commitId);
            // LocalDateTimeからjava.sql.Timestampへ変換
            Timestamp timestamp = Timestamp.valueOf(childCommit.commitDate);
            ps.setTimestamp(2, timestamp);
            System.out.println(timestamp);
            if(childCommit.getRelease()==null){
                System.out.println("test");
                ps.setString(3, "test");
            }else {
                ps.setString(3, childCommit.getRelease());
            }
            System.out.println(childCommit.getRelease());
            ps.setString(4, childCommit.project);
            ps.setString(5, childCommit.commitComment);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (ChangedFile change : childCommit.changedFileList) {
            for (Chunk chunk : change.chunks) {
                if (chunk.getType() == null) {
                    continue;
                }
                LineChange lc = new LineChange(change.newPath, change.oldPath, chunk);
                insertDate(childCommit, lc, false);
                if(childCommit.parentCommitIds.size()!=0){
                    insertDate(childCommit, lc, true);
                }

            }

        }
    }
    private void insertDate(Commit commit, LineChange lc, boolean isParent) {
        String chunk_sql = "insert into chunk_child_list(commitId,fileName,hashcode,type) VALUES(?,?,?,?)";
        if (isParent) {
            chunk_sql = "insert into chunk_parent_list(commitId,fileName,hashcode,type) VALUES(?,?,?,?)";
        }
        // Set chunk data
        try (PreparedStatement ps = connection.prepareStatement(chunk_sql)) {
            ps.setString(1, commit.commitId);
            if (isParent) {
                ps.setString(2, lc.oldPath);
            }else{
                ps.setString(2, lc.newPath);
            }
            ps.setInt(3, lc.hashCode());
            ps.setString(4, lc.getType());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void creatTable(Boolean isParent) throws SQLException {
        if(isParent) {
            //1回だけのテーブル作成の処理やから，isParentのif文の中に書いてる
            String commitTableName = "CREATE TABLE commit_list("
                    + "id INT(100) NOT NULL AUTO_INCREMENT,"
                    + "commitId VARCHAR(64) NOT NULL COLLATE utf8mb4_unicode_ci,"
                    + "commitDate timestamp NOT NULL ,"
                    + "releasePart VARCHAR(10) NOT NULL COLLATE utf8mb4_unicode_ci,"
                    + "fileName VARCHAR(100) NOT NULL COLLATE utf8mb4_unicode_ci,"
                    + "commitComment VARCHAR(1000) NOT NULL COLLATE utf8mb4_unicode_ci ,"
                    + "PRIMARY KEY(id))";
            statement.executeUpdate(commitTableName);
        }
        statement.executeUpdate(this.getSatdTableSQL(isParent));
        statement.executeUpdate(this.getChunkTableSQL(isParent));

    }

    private String getSatdTableSQL(Boolean isParent) {
        String table;
        if(isParent){
            table = "parent_satd_list";
        }else{
            table = "child_satd_list";

        }
        return  "CREATE TABLE "+table+"("
                + "id INT(100) NOT NULL AUTO_INCREMENT,"
                + "commitId VARCHAR(64) NOT NULL COLLATE utf8mb4_unicode_ci,"
                + "fileName VARCHAR(300) NOT NULL COLLATE utf8mb4_unicode_ci,"
                + "lineNo INT(64) NOT NULL,"
                + "content VARCHAR(1000) NOT NULL COLLATE utf8mb4_unicode_ci,"
                + "hashcode INT(64) NOT NULL ,"
                + "type VARCHAR(10) NOT NULL COLLATE utf8mb4_unicode_ci,"
                + "PRIMARY KEY(id))";
    }
    private String getChunkTableSQL(boolean isParent){
        String table;
        if(isParent){
            table = "chunk_parent_list";
        }else{
            table = "chunk_child_list";
        }
        return  "CREATE TABLE " + table + "("
                + "id INT(100) NOT NULL AUTO_INCREMENT,"
                + "commitId VARCHAR(64) NOT NULL COLLATE utf8mb4_unicode_ci,"
                + "fileName VARCHAR(300) NOT NULL COLLATE utf8mb4_unicode_ci,"
                + "hashcode INT(64) NOT NULL ,"
                + "type VARCHAR(10) NOT NULL COLLATE utf8mb4_unicode_ci,"
                + "PRIMARY KEY(id))";
    }

    public void dataUpdate(SATD satd, Boolean isParent) {
        String satd_sql = "insert into child_satd_list(commitId,fileName,lineNo,content,hashcode,type) VALUES(?,?,?,?,?,?)";
        if(isParent) {
            satd_sql = "insert into parent_satd_list(commitId,fileName,lineNo,content,hashcode,type) VALUES(?,?,?,?,?,?)";
        }
        // Set satd data
        try(PreparedStatement ps = connection.prepareStatement(satd_sql)){
            ps.setString(1,satd.commitId);
            ps.setString(2,satd.fileName);
            ps.setInt(3,satd.line);
            ps.setString(4,satd.content);
            ps.setInt(5,satd.pathHash);
            ps.setString(6,satd.getType());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
