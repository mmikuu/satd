package jp.naist.sdlab.miku.main;
import static org.junit.Assert.*;

import jp.naist.sdlab.miku.module.CommandExecutor;
import jp.naist.sdlab.miku.module.SATD;
import jp.naist.sdlab.miku.module.commit.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jruby.ext.nkf.Command;
import org.junit.Assert;
import org.junit.Test;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffConfig;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.HistogramDiff;

public class CalcTest {
    public   GitServiceImpl2 gitService = new GitServiceImpl2();
    public  Repository repo;
    public  String  testUrl = "https://github.com/mmikuu/CalcTestSatd";
    public  CommandExecutor executor ;
    public  int lineNo = 11;
    public  String testCommitId ="bd2a0d30047aff33b8ce9533c5bc6c57f1d340dd";
    public  String context = "/* TODO";
    public  String fileName = "src/Main.java";

    @Test
    public void satdCount() throws Exception {
        main_satdtime satd = new main_satdtime();
        repo = getRepo(testUrl);
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/satd_replace_db",
                "me",
                "goma");
        //init database
        Statement statement  = connection.createStatement();
        List<String> releaseDates = Arrays.asList("2023-10-9", "2023-10-12");
        executor = new CommandExecutor(testUrl, repo, "repos/CalcTestSatd/",connection,statement,releaseDates);
        TestCalcSatd();

    }

    private  Repository getRepo(String url) throws Exception {
        String[] tmp = url.split("/");
        String project = tmp[tmp.length - 1];
        String cloneDir = "repos/" + project;
        Repository repository = gitService.cloneIfNotExists(cloneDir, url);
        return repository;
    }

    public void TestCalcSatd() throws IOException, InterruptedException {
//        Comment comment = Calcexpect(testCommitId,context,fileName);
//        SATD exepect = new SATD(testCommitId,fileName,comment,false);
        executor.runCommand(testCommitId);
        System.out.println(executor.resultsParent);
        System.out.println(executor.resultsChild);
        Assert.assertEquals("no match",executor.resultsChild.get(0).getContent(),context);

    }

    private Comment Calcexpect(String testCommitId,String context,String fileName) {
        RevCommit revCommit = executor.getCommit(testCommitId);
        Commit childCommit = gitService.getCommit(testUrl, repo, revCommit);
        Chunk chunk = childCommit.changedFileList.get(0).chunks.get(0);
        LineChange lc = new LineChange(fileName, fileName, chunk);
        Comment comment = new  Comment(lineNo, context, lc, fileName);
        return comment;
    }
}
