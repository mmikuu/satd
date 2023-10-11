package jp.naist.sdlab.miku.module.commit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitUtil {
    public static Repository getRepo(GitServiceImpl2 gitService, String url) throws Exception {
        String[] tmp = url.split("/");
        String project = tmp[tmp.length - 1];
        String cloneDir = "repos/" + project;
        return gitService.cloneIfNotExists(cloneDir, url);
    }
    public static  RevCommit getCommit(Git git, Repository repository, String commitId) {
        RevCommit commit;
        //子のcommitをcheckout
        try {
            commit = git.log().add(repository.resolve(commitId)).setMaxCount(1).call().iterator().next();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        return commit;
    }
}
