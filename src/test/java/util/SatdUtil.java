package util;

import jp.naist.sdlab.miku.module.DiffSATDDetector;
import jp.naist.sdlab.miku.module.commit.Commit;
import jp.naist.sdlab.miku.module.commit.GitServiceImpl2;
import jp.naist.sdlab.miku.module.commit.GitUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class SatdUtil {
    public static DiffSATDDetector getExecutor(String testUrl, String testCommitId) throws Exception {
        GitServiceImpl2 gitService = new GitServiceImpl2();
        Repository testRepo = GitUtil.getRepo(gitService, testUrl);
        Git git = new Git(testRepo);
        RevCommit commit = GitUtil.getCommit(git, testRepo, testCommitId);

        DiffSATDDetector executor = new DiffSATDDetector(testUrl, testRepo, gitService, "repos/CalcTestSatd/");
        Commit childCommit = gitService.getCommit(testUrl, testRepo, commit);

        executor.detectSATD(childCommit);
        return executor;
    }
}
