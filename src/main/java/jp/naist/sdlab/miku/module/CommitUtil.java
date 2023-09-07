package jp.naist.sdlab.miku.module;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;

public class CommitUtil {
    public static RevCommit getCommit(Repository repo, String commitId) throws IOException {
        ObjectId evalCommitId = repo.resolve(commitId);
        try (RevWalk walk = new RevWalk(repo)) {
            RevCommit evalCommit = walk.parseCommit(evalCommitId);
            walk.parseCommit(evalCommit.getParent(0).getId());
            walk.dispose();
            return evalCommit;
        }
    }
}
