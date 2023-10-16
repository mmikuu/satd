package util;

import jp.naist.sdlab.miku.module.commit.Commit;
import org.eclipse.jgit.revwalk.RevCommit;

import java.sql.SQLException;

public class CommitTest extends Commit {
    public CommitTest() throws SQLException {
        super();
    }
    public  String  getTestRelease(Commit childCommit){
        String release = childCommit.getRelease();
        if(release == null){
            release = "test_"+childCommit.commitId;
            return release;
        }
        return release;
    }
}
