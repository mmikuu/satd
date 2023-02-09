package jp.naist.sdlab.miku.main;


import jp.naist.sdlab.miku.module.SATD;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

import static jp.naist.sdlab.miku.module.CommitUtil.getCommit;

public class main_time {
    static String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
    public static void main(String[] args) throws Exception {
        List<SATD> satdList = new ArrayList<>();



            /*
             * 入力 csvファイルの中身取得
             */
            try{
               Reader in = new FileReader("deleted_satd.csv");
               Iterable<CSVRecord> records =
                       CSVFormat.EXCEL.withHeader().parse(in);
               for(CSVRecord record : records){
                   SATD satd = new SATD();
                   satd.fileName=record.get("filename");
                   satd.line= Integer.parseInt(record.get("line"));
                   satd.commitId= record.get("commitID");
                   satd.content = record.get("content");
                   satdList.add(satd);
               }
            } catch(Exception e){
                System.out.println(e);
            }
            GitService gitService = new GitServiceImpl();
            String[] tmp = url.split("/");
            String project = tmp[tmp.length - 1];
            String cloneDir = "repos/" + project;
            Repository repository = gitService.cloneIfNotExists(cloneDir, url);


            for(SATD satd: satdList) {
                RevCommit satdDeletedCommit = getCommit(repository, satd.commitId);
                System.out.println("Find this SATD in " + satd.commitId +" : "+ satd.fileName + " @ " + satd.line +" : " + satd.content );
                RevCommit satdAddedCommit = blame(repository, satd.commitId, satd.fileName, satd.line);
                PersonIdent authorIdent = satdAddedCommit.getCommitterIdent();
                Date authorDate = authorIdent.getWhen();
                System.out.println("Found in " + satdAddedCommit.getId().getName() + "(" + authorDate.toString() + ")");
                //TODO: 時間を計測（satdDeletedCommit-satdAddedCommit）
            }

    }

    public static RevCommit blame(Repository repository, String startCommitId, String fileName, int i) throws IOException, GitAPIException {
        boolean reverse = false;

        Git git = new Git(repository);
        /*
         * ブレーム
         */
        BlameCommand blamer = git.blame();
        if (!reverse) {//普通のBlame
            ObjectId startCommit = repository.resolve(startCommitId+"^");//ここを開始拠点とする
            blamer.setStartCommit(startCommit);
        } else {//リバースの場合
            ObjectId startCommit = repository.resolve(startCommitId);//ここを開始拠点とする
            blamer.reverse(startCommit, repository.resolve("HEAD"));
        }
        blamer.setFilePath(fileName);
        BlameResult result = blamer.call();
        System.out.println(fileName);
        /*
         * 表示
         */
//        int lines = result.getResultContents().size();
//        for (int i = 0; i < lines; i++) {//NOTE: 1行目は0から始まるので注意．
//
//        }
        System.out.println(result.getResultContents().getString(i));
        RevCommit commit = result.getSourceCommit(i);
        return commit;
        //NOTE: 月は"authorDate.getMonth()+1"で取れる．0が１月
    }
}