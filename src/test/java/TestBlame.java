import jp.naist.sdlab.miku.main.main_time;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

public class TestBlame {

    @Test
    public void testBlameN001() throws Exception {
        String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
        GitService gitService = new GitServiceImpl();
        String[] tmp = url.split("/");
        String project = tmp[tmp.length - 1];
        String cloneDir = "repos/" + project;
        Repository repository = gitService.cloneIfNotExists(cloneDir, url);
        String commitId = "28e2fc99c0e1bcaffcd4fcfc53a3c19326d33a79";
        String fileName = "org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/LocalStaticsTest_15.java";
        main_time.blame(repository, commitId, fileName, 1);
    }

    @Test
    public void testBlameN002() throws Exception {
        String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
        GitService gitService = new GitServiceImpl();
        String[] tmp = url.split("/");
        String project = tmp[tmp.length - 1];
        String cloneDir = "repos/" + project;
        Repository repository = gitService.cloneIfNotExists(cloneDir, url);
        String commitId = "28e2fc99c0e1bcaffcd4fcfc53a3c19326d33a79";
        String fileName = "org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/LocalStaticsTest.java";
        main_time.blame(repository, commitId, fileName, 1);
    }
}
