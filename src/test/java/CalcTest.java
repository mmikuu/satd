import jp.naist.sdlab.miku.main.MainCalculatedTime;
import jp.naist.sdlab.miku.module.DiffSATDDetector;
import jp.naist.sdlab.miku.module.SATD;
import jp.naist.sdlab.miku.module.commit.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.soap.SAAJResult;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class CalcTest {

    private DiffSATDDetector getExecutor(String testUrl, String testCommitId) throws Exception {
        GitServiceImpl2 gitService = new GitServiceImpl2();
        Repository testRepo = GitUtil.getRepo(gitService, testUrl);
        Git git = new Git(testRepo);
        RevCommit commit = GitUtil.getCommit(git, testRepo, testCommitId);


        DiffSATDDetector executor = new DiffSATDDetector(testUrl, testRepo, gitService, "repos/CalcTestSatd/");
        Commit childCommit = gitService.getCommit(testUrl, testRepo, commit);

        executor.detectSATD(childCommit);
        return executor;
    }


    @Test
    public void testDetectAddedSATD() throws Exception {
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="f4366773967d694d739b502fedd2055b17981947";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(0, executor.resultsParent.size());//Delete=0
        Assert.assertEquals(2, executor.resultsChild.size());//Added=2

        //1つ目
        SATD resultSATD1 = executor.resultsChild.get(0);
        Assert.assertEquals("/* TODO", resultSATD1.content);
        Assert.assertTrue(resultSATD1.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD1.type);

        //2つ目
        SATD resultSATD2 = executor.resultsChild.get(1);
        Assert.assertEquals("        * bug 1", resultSATD2.content);
        Assert.assertTrue(resultSATD2.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD2.type);

    }



    @Test
    public void testDetectDeletedSATD() throws Exception {
        String testCommitId = "68e5ea621ac9b428e0e5c2ef2c579eee1cce957a";

    }


}
