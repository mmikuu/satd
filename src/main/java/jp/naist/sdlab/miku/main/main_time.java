package jp.naist.sdlab.miku.main;


import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;


import java.io.FileReader;
import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.lang.Exception;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.TimeZone;

public class main_time {
    static String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
    public static void main(String[] args) {
        try {
            /*
             * 入力 csvファイルの中身取得
             */
            try{
               Reader in = new FileReader("added_satd.csv");
               Iterable<CSVRecord> records =
                       CSVFormat.EXCEL.withHeader().parse(in);
               for(CSVRecord record : records){
                   String filename = record.get("filename");
                   String line = record.get("line");
                   String commitID = record.get("commitID");
                   String content = record.get("content");

                   System.out.println(filename);
                   System.out.println(line);
                   System.out.println(commitID);
                   System.out.println(content);
               }
            }catch(IOException e){
                System.out.println(e);
            }
            catch(Exception e){
                System.out.println(e);
            }

            String startCommitId = "a6b58c287bb3c58c6af5675a97355c9ff80c8ee6";
            String fileName = "org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/util/JRTUtil.java";
            boolean reverse = false;
            /*
             * 前準備
             */
            //Clone用（無くても良い）
            GitService gitService = new GitServiceImpl();
            String[] tmp = url.split("/");
            String project = tmp[tmp.length-1];
            String cloneDir = "repos/"+project;
            Repository repository = gitService.cloneIfNotExists(cloneDir, url);

            /*
             * 処理
             */
            Git git = new Git(repository);
            ObjectId startCommit = repository.resolve(startCommitId);//ここを開始拠点とする
            /*
             * ブレーム
             */
            BlameCommand blamer = git.blame();
            if (!reverse){//普通のBlame
                blamer.setStartCommit(startCommit);
            }else{//リバースの場合
                blamer.reverse(startCommit, repository.resolve("HEAD"));
            }
            blamer.setFilePath(fileName);
            BlameResult result = blamer.call();

            /*
             * 表示
             */
            int lines = result.getResultContents().size();
            for (int i = 0; i < lines; i++) {//NOTE: 1行目は0から始まるので注意．
                System.out.println(result.getResultContents().getString(i));
                RevCommit commit = result.getSourceCommit(i);
                if (commit==null) continue;
                PersonIdent authorIdent = commit.getAuthorIdent();
                Date authorDate = authorIdent.getWhen();
                System.out.println("Line: " + i + ": " + commit +"("+authorDate.toString()+")");
                //NOTE: 月は"authorDate.getMonth()+1"で取れる．0が１月
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}