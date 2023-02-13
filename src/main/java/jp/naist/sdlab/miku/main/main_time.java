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
import java.sql.Time;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static jp.naist.sdlab.miku.module.CommitUtil.getCommit;
import java.text.SimpleDateFormat;
public class main_time {
    static String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
    public static void main(String[] args) throws Exception {
        List<SATD> satdList = new ArrayList<>();
        FileWriter commitBlameWrite = new FileWriter("commitaddedBlame.csv");
             /*
             * 入力 csvファイルの中身取得
             */
            try{
               Reader in = new FileReader("added_satd.csv");
               Iterable<CSVRecord> records =
                       CSVFormat.EXCEL.withHeader().parse(in);
               for(CSVRecord record : records){
                   SATD satd = new SATD();
                   satd.fileName=record.get("filename");
                   satd.line= Integer.parseInt(record.get("line"));
                   satd.commitId= record.get("commitID");
                   satd.content = record.get("content");
                   satdList.add(satd);
                   System.out.println(satd.fileName);
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
                PersonIdent deletedIdent = satdDeletedCommit.getCommitterIdent();
                Date deletedDate = deletedIdent.getWhen();
                OffsetDateTime deleted_odt = OffsetDateTime.ofInstant(deletedDate.toInstant(), TimeZone.getTimeZone("UTC").toZoneId());



                RevCommit satdAddedCommit = blame(repository, satd.commitId, satd.fileName, satd.line);
                if (satdAddedCommit != null) {
                    PersonIdent AddedIdent = satdAddedCommit.getCommitterIdent();
                    Date AddedDate = AddedIdent.getWhen();
                    OffsetDateTime added_odt = OffsetDateTime.ofInstant(AddedDate.toInstant(), TimeZone.getTimeZone("UTC").toZoneId());

                    //System.out.println("Found in " + satdAddedCommit.getId().getName() + "(" + added_odt + ")");

                    //System.out.println(ChronoUnit.SECONDS.between(deleted_odt, added_odt));
                    //System.out.println(ChronoUnit.DAYS.between(deleted_odt,added_odt));
                    //System.out.println(ChronoUnit.MONTHS.between(deleted_odt, added_odt));
                    // 時間を計測（satdDeletedCommit-satdAddedCommit）

                    commitBlameWrite.write(satdDeletedCommit.getId().getName() + "," + deleted_odt + "," + satdAddedCommit.getId().getName() + "," + added_odt + "," + ChronoUnit.DAYS.between(deleted_odt,added_odt) + "\n");
                }else{
                    commitBlameWrite.write(satdDeletedCommit.getId().getName() + "," + deleted_odt + ",null,null,null\n");

                }

            }
        commitBlameWrite.close();
    }

    public static RevCommit blame(Repository repository, String startCommitId, String fileName, int i) throws IOException, GitAPIException {
        if (startCommitId.equals("e3517ddc41f3c9536a29ef9be4e7dd3104993ab2")&&fileName.equals("org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java")){
            return null;
        }
        if (startCommitId.equals("28e2fc99c0e1bcaffcd4fcfc53a3c19326d33a79")&&fileName.equals("org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/SealedTypesTests.java")){
            return null;
        }
        if (startCommitId.equals("28e2fc99c0e1bcaffcd4fcfc53a3c19326d33a79")&&fileName.equals("org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java")){
            return null;
        }
        if (startCommitId.equals("18a7dc2e684ffa0791a31611369c86d846acba9b")&&fileName.equals("org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/RecordsRestrictedClassTest.java")){
            return null;
        }
        if (startCommitId.equals("18a7dc2e684ffa0791a31611369c86d846acba9b")&&fileName.equals("org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/SealedTypes15Tests.java")){
            return null;
        }
        if (startCommitId.equals("18a7dc2e684ffa0791a31611369c86d846acba9b")&&fileName.equals("org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java")){
            return null;
        }
        boolean reverse = true;

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

        /*
         * 表示
         */
//        int lines = result.getResultContents().size();
//        for (int i = 0; i < lines; i++) {//NOTE: 1行目は0から始まるので注意．
//
//        }
        //System.out.println(result.getResultContents().getString(i-1));//TODO ayasii
        RevCommit commit = result.getSourceCommit(i);
        //System.out.println(commit);
        return commit;
        //NOTE: 月は"authorDate.getMonth()+1"で取れる．0が１月
    }
}