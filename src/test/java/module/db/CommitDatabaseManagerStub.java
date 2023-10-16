package module.db;

import jp.naist.sdlab.miku.module.SATD;
import jp.naist.sdlab.miku.module.commit.ChangedFile;
import jp.naist.sdlab.miku.module.commit.Chunk;
import jp.naist.sdlab.miku.module.commit.Commit;
import jp.naist.sdlab.miku.module.commit.LineChange;
import jp.naist.sdlab.miku.module.db.CommitDatabaseManager;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;
import util.CommitTest;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CommitDatabaseManagerStub extends CommitDatabaseManager {
    public static final String db = "test_db";

    public CommitDatabaseManagerStub() throws SQLException {
        super(db);
    }
    // TODO 　ここでcreateTableを呼び出す？どうやってcreateTableする？
    public void createTable() throws SQLException {
        super.creatTable(true);
        super.creatTable(false);
    }

    @Override
    public void addCommitData(Commit childCommit) {
        CommitTest commitTest;
        try {
            commitTest = new CommitTest();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (childCommit.parentCommitIds.size() > 1) {
            return;//対象外
        }
        String commit_sql = "insert into commit_list (commitId,commitDate,releasePart,fileName,commitComment) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(commit_sql)) {
            ps.setString(1, childCommit.commitId);
            // LocalDateTimeからjava.sql.Timestampへ変換
            Timestamp timestamp = Timestamp.valueOf(childCommit.commitDate);
            ps.setTimestamp(2, timestamp);
            ps.setString(3, commitTest.getTestRelease(childCommit));
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
                if (childCommit.parentCommitIds.size() != 0) {
                    insertDate(childCommit, lc, true);
                }

            }

        }
    }
}