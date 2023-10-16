import jdk.jfr.internal.tool.Main;
import jp.naist.sdlab.miku.main.MainCalculatedTime;
import jp.naist.sdlab.miku.module.DiffSATDDetector;
import jp.naist.sdlab.miku.module.SATD;
import jp.naist.sdlab.miku.module.commit.Commit;
import jp.naist.sdlab.miku.module.commit.GitServiceImpl2;
import jp.naist.sdlab.miku.module.db.CommitDatabaseManager;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;
import module.db.CommitDatabaseManagerStub;
import module.db.SATDDatabaseManagerStub;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static util.SatdUtil.getExecutor;

public class CalculatedTimeTest {
    public String testUrl = "https://github.com/mmikuu/CalcTestSatd";
    public String path = "/CalcTestSatd";
    public CommitDatabaseManagerStub commitDBManager;
    public MainCalculatedTime calcSATD;

    public  CalculatedTimeTest() throws Exception {

        calcSATD = new MainCalculatedTime();
        commitDBManager = new CommitDatabaseManagerStub();


    }
    @Test
    public void testDetectSATD() throws Exception {

        Repository repository = calcSATD.getRepo(testUrl);
        Git git = new Git(repository);
        Iterable<RevCommit> log = git.log().call();
        GitServiceImpl2 gitService = new GitServiceImpl2();

        commitDBManager.createTable();

        for(RevCommit commit : log){
            Commit childCommit = gitService.getCommit(testUrl, repository, commit);
            commitDBManager.addCommitData(childCommit);
            DiffSATDDetector executor = getExecutor(testUrl, commit.getName());
            insertData(executor.resultsParent,executor.resultsChild);
        }

    }

    private void insertData(List<SATD> resultsParent, List<SATD> resultsChild) {
        for(SATD satdP : resultsParent){
            commitDBManager.dataUpdate(satdP,true);
        }
        for(SATD satdC : resultsChild){
            commitDBManager.dataUpdate(satdC,false);
        }
    }
}
