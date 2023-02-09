package jp.naist.sdlab.miku.main;


import org.apache.commons.csv.CSVPrinter;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class main_time {
    static String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
    public static void main(String[] args) throws Exception {
        List<String> filename = new ArrayList<>();
        List<String> line = new ArrayList<>();
        List<String> commitID = new ArrayList<>();
        List<String> content = new ArrayList<>();


            /*
             * 入力 csvファイルの中身取得
             */
            try{
               Reader in = new FileReader("deleted_satd.csv");
               Iterable<CSVRecord> records =
                       CSVFormat.EXCEL.withHeader().parse(in);
               for(CSVRecord record : records){
                   filename.add(record.get("filename"));
                   line.add(record.get("line"));
                   commitID.add(record.get("commitID"));
                   content.add(record.get("content"));
                   System.out.println(filename.get(0));
               }
            }catch(IOException e){
                System.out.println(e);
            }
            catch(Exception e){
                System.out.println(e);
            }
            GitService gitService = new GitServiceImpl();
            String[] tmp = url.split("/");
            String project = tmp[tmp.length - 1];
            String cloneDir = "repos/" + project;
            Repository repository = gitService.cloneIfNotExists(cloneDir, url);


            for(int j = 0; j<filename.size()-1;j++) {

                String startCommitId = commitID.get(j);
                startCommitId=startCommitId.replace(" ","");
                String fileName = filename.get(j);

                System.out.println(startCommitId);
                boolean reverse = false;

                Git git = new Git(repository);
                ObjectId startCommit = repository.resolve(startCommitId);//ここを開始拠点とする
                /*
                 * ブレーム
                 */
                BlameCommand blamer = git.blame();
                if (!reverse) {//普通のBlame
                    blamer.setStartCommit(startCommit);
                } else {//リバースの場合
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
                    if (commit == null) continue;
                    PersonIdent authorIdent = commit.getAuthorIdent();
                    Date authorDate = authorIdent.getWhen();
                    System.out.println("Line: " + i + ": " + commit + "(" + authorDate.toString() + ")");
                    //NOTE: 月は"authorDate.getMonth()+1"で取れる．0が１月
                }
            }

    }
}