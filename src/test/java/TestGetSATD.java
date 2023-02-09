import jp.naist.sdlab.miku.module.CommandExecutor;
import jp.naist.sdlab.miku.module.SATD;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.Test;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static jp.naist.sdlab.miku.main.main_yuta.formatter;
import static jp.naist.sdlab.miku.main.main_yuta.releaseDates;

public class TestGetSATD {

    @Test
    public void testRead() throws Exception {
        GitService gitService = new GitServiceImpl();
        String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";

        String[] tmp = url.split("/");
        String project = tmp[tmp.length-1];
        String cloneDir = "repos/"+project;
        Map<String, List<SATD>> satdPerRelease = new LinkedHashMap<>();

        Repository repository = gitService.cloneIfNotExists(cloneDir, url);
        Git git = new Git(repository);
        RevCommit commit = getCommit(repository, "a6b58c287bb3c58c6af5675a97355c9ff80c8ee6");

//        LocalDateTime commitDate = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
//        System.out.println(commitDate);
        LocalDateTime commitDate = LocalDateTime.ofInstant(commit.getCommitterIdent().getWhen().toInstant(),
                ZoneId.systemDefault());

        commitDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (int i = 0; i < releaseDates.size()-1; i++) {
            LocalDateTime releaseStartDate = LocalDate.parse(releaseDates.get(i), formatter).atStartOfDay();
            LocalDateTime releaseEndDate = LocalDate.parse(releaseDates.get(i+1), formatter).atStartOfDay();
            if (commitDate.isBefore(releaseStartDate) ) {
                break;
            }else if (commitDate.isBefore(releaseEndDate)){

                List<SATD> allSATDs = satdPerRelease.getOrDefault(releaseDates.get(i), new ArrayList<>());
                List<SATD> results = CommandExecutor.runCommand(commit.getId().getName(), Paths.get(cloneDir).toAbsolutePath(), "git", "diff", "--no-ext-diff", "--unified=0", "--no-prefix", "-a", "-w", commit.getName() + "^.." + commit.getName());
                allSATDs.addAll(results);
                System.out.println(commitDate.toLocalDate());
                System.out.println(releaseStartDate);
                System.out.println(releaseEndDate);
                satdPerRelease.put(releaseDates.get(i), allSATDs);

            }else {
                continue;
            }


        }

    }
    public RevCommit getCommit(Repository repo, String commitId) throws IOException {
        ObjectId evalCommitId = repo.resolve(commitId);
        try (RevWalk walk = new RevWalk(repo)) {
            RevCommit evalCommit = walk.parseCommit(evalCommitId);
            walk.parseCommit(evalCommit.getParent(0).getId());
            walk.dispose();
            return evalCommit;
        }
    }
}
