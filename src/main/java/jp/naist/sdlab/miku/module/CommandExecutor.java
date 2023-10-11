package jp.naist.sdlab.miku.module;

import jp.naist.sdlab.miku.module.commit.*;
import jp.naist.se.commentlister.FileAnalyzer;
import jp.naist.se.commentlister.reader.CommentReader;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import org.jruby.RubyProcess;
import satd_detector.core.utils.SATDDetector;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.plaf.nimbus.State;

public class CommandExecutor {
    public int AddedCount = 0;
    public int DeletedCount = 0;

    public String url;
    public Repository repository;
    Git git;
    GitServiceImpl2 gitService;
    private String repoPath;
    public List<SATD> resultsParent;
    public List<SATD> resultsChild;
    public Connection connection;
    public Statement statement;
    public List<String> releaseDates;

    public CommandExecutor(String url, Repository repository, String repoPath, Connection conn, Statement stmt,List<String> releaseDates) {
        this.url = url;
        this.repository = repository;
        this.git = new Git(repository);
        this.gitService = new GitServiceImpl2();
        this.repoPath = repoPath;
        this.resultsParent = new ArrayList<>();
        this.resultsChild = new ArrayList<>();
        this.connection = conn;
        this.statement = stmt;
        this.releaseDates = releaseDates;


    }

    public void runCommand(String commitId) throws IOException, InterruptedException {
        System.out.println(commitId);
        this.resultsParent = new ArrayList<>();
        this.resultsChild = new ArrayList<>();
//        if (!commitId.equals("907f50c0bd8abce7aa6051e48f2fed4e1bb60a7f")) return;
        RevCommit commit;//checkoutするために
        RevCommit[] parents;//親をcheckoutするために
        this.checkout(commitId);
        RevCommit revCommit = this.getCommit(commitId);
        parents = revCommit.getParents();
        if (parents.length == 1) {
            Commit childCommit = gitService.getCommit(this.url, repository, revCommit);
            addCommitData(childCommit);
            detectSATD(childCommit, false, this.resultsChild);
            //親のcommitをcheckout
            this.checkout(parents[0].getName());
            detectSATD(childCommit, true, this.resultsParent);
        } else if (parents.length == 0) {
            Commit childCommit = gitService.getCommit(this.url, repository, revCommit);
            addCommitData(childCommit);
            detectSATD(childCommit, false, this.resultsChild);
        }
    }

    private void addCommitData(Commit childCommit) {
        if(childCommit.getRelease() != null) {
            String commit_sql = "insert into commit_list (commitId,commitDate,releasePart,fileName,commitComment) VALUES(?,?,?,?,?)";
            try (PreparedStatement ps = connection.prepareStatement(commit_sql)) {
                ps.setString(1, childCommit.commitId);
                // LocalDateTimeからjava.sql.Timestampへ変換
                Timestamp timestamp = Timestamp.valueOf(childCommit.commitDate);
                ps.setTimestamp(2, timestamp);
                System.out.println(timestamp);
                ps.setString(3, childCommit.getRelease());
                System.out.println(childCommit.getRelease());
                ps.setString(4, childCommit.project);
                ps.setString(5, childCommit.commitComment);
                ps.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void detectSATD(Commit commit, boolean isParent, List<SATD> results) throws IOException, InterruptedException {
        Map<String, Map<Integer, LineChange>> changedLinesInFilesInChildRevision = markLines(commit, isParent);
        for (Map.Entry<String, Map<Integer, LineChange>> i : changedLinesInFilesInChildRevision.entrySet()) {
            Map<String, Map<Integer, Comment>> commentsPerFile = detectComment(i.getKey(), i.getValue());
            if (commentsPerFile != null) {
                for (Map<Integer, Comment> comments : commentsPerFile.values()) {
                    for (Comment comment : comments.values()) {
                        if (comment != null) {
                            analyzeComment(commit.commitId, comment, isParent, results);
                        }
                    }
                }
            }
        }
    }

    private void analyzeComment(String commitId, Comment comment, boolean isParent, List<SATD> results) throws InterruptedException {
        StreamGobbler outputGobbler = new StreamGobbler(commitId, comment, isParent, results);
        outputGobbler.start();
        outputGobbler.join();
    }


    private void checkout(String commitId) {
        try {
            gitService.checkout(repository, commitId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public  RevCommit getCommit(String commitId) {
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

    public Map<String, Map<Integer, LineChange>> markLines(Commit commit, boolean isParent) throws IOException {
        Map<String, Map<Integer, LineChange>> map = new HashMap<>();
        for (ChangedFile change : commit.changedFileList) {
            Map<Integer, LineChange> changedLines = new HashMap<>();//子の変更された行数を格納
            int startNo;
            int endNo;
            for (Chunk chunk : change.chunks) {
                if (chunk.getType() == null) {
                    continue;
                }
                LineChange lc = new LineChange(change.newPath, change.oldPath, chunk);
                if (isParent) {
                    startNo = chunk.getOldStartNo()+1;
                    endNo = chunk.getOldEndNo();
//                    insertDate(commit,lc,true);
                } else {
                    startNo = chunk.getNewStartNo()+1;
                    endNo = chunk.getNewEndNo();
//                    insertDate(commit,lc,false);
                }
                for (int i = startNo; i <= endNo; i++) {
                    changedLines.put(i, lc);
                }
            }
            if (isParent) {
                map.put(change.oldPath, changedLines);
            } else {
                map.put(change.newPath, changedLines);
            }
        }
        return map;
    }

    private void insertDate(Commit commit,LineChange lc,boolean isParent) {
        String chunk_sql = "insert into chunk_child_list(commitId,fileName,hashcode,type) VALUES(?,?,?,?)";
        if (isParent) {
            chunk_sql = "insert into chunk_parent_list(commitId,fileName,hashcode,type) VALUES(?,?,?,?)";
        }
        // Set chunk data
        try (PreparedStatement ps = connection.prepareStatement(chunk_sql)) {
            ps.setString(1, commit.commitId);
            if (isParent) {
                ps.setString(2, lc.oldPath);
            }else{
                ps.setString(2, lc.newPath);
            }
            ps.setInt(3, lc.hashCode());
            ps.setString(4, lc.getType());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


        public Map<String, Map<Integer, Comment>> detectComment(String path, Map<Integer, LineChange> changedLines) throws IOException {
        if (path.endsWith("/dev/null"))
            return null;
        Map<String, Map<Integer, Comment>> map = new HashMap<>();
        //Get comment
        File Af = new File(this.repoPath + path);
        CommentReader cr = FileAnalyzer.extractComments(Af.toPath());//java cpp 以外のファイルは解析されない
        if (cr == null) {//ファイルが消された場合
            return null;
        }
        Map<Integer, Comment> lineNoComment = new HashMap<>();
        while (cr.next()) {
            String[] commentLines = cr.getText().split("\n");
            int startline = cr.getLine();
            int endline = cr.getLine() + commentLines.length - 1; //確認用　けしてもOK
            for (int rowInComments = 0; rowInComments < commentLines.length; rowInComments++) {
                int line = startline + rowInComments;
                LineChange lc = changedLines.get(line);
                if (lc == null) {
                    continue;
                }
                if (lc.getEditType() != null && !lc.getEditType().equals(Edit.Type.EMPTY)) {
                    String context = commentLines[rowInComments];
                    Comment comment = new Comment(line, context, lc, path);
                    lineNoComment.put(line, comment);
                }
            }
        }
        map.put(path, lineNoComment);
        return map;
    }

    public static class StreamGobbler extends Thread {
        InputStream is;
        public List<SATD> satdList;
        String commitId;
        SATDDetector detector;
        String path;
        boolean isParent;
        Comment comment;

        private StreamGobbler(String commitId, Comment comment, boolean isParent, List<SATD> satdList) {
            this.satdList = satdList;
            this.commitId = commitId;
            this.detector = new SATDDetector();
            this.comment = comment;
            this.isParent = isParent;
            this.path = comment.filepath;
        }

        @Override
        public void run() {
            SATD satd = detect(comment);
            if (satd != null) {//表示してるだけ
                satdList.add(satd);//全体に追加
//                System.out.println("comment start-----------------------------------------");
//                System.out.println("filename:" + this.path);
//                System.out.println(comment.toString());
//                System.out.println("comment end-----------------------------------------");
//                System.out.println("SATD start-----------------------------------------");
//                System.out.println(satd.toString());
//                System.out.println("SATD end-----------------------------------------");

            }
        }

        public SATD detect(Comment comment) {
            boolean result = this.detector.isSATD(comment.context);
            if (result) {
                return new SATD(this.commitId, this.path, comment, isParent);
            }
            return null;
        }
    }
}

