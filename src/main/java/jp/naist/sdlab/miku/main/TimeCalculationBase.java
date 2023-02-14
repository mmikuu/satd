package jp.naist.sdlab.miku.main;


import jp.naist.sdlab.miku.module.SATD;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Math.abs;
import static jp.naist.sdlab.miku.module.CommitUtil.getCommit;

public abstract class TimeCalculationBase {
    String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
    String inputFile;
    String outputFile;

    String startRelease;
    String lastRelease;
    boolean reverse;
    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ZZZ");

    public TimeCalculationBase(String inputFile, String outputFile, String startRelease, String lastRelease, boolean reverse){
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.startRelease = startRelease;
        this.lastRelease = lastRelease;
        this.reverse = reverse;
    }

    public void run() throws Exception {
        List<SATD> satdList = new ArrayList<>();
        FileWriter commitBlameWrite = new FileWriter(outputFile);
         /*
         * 入力 csvファイルの中身取得
         */
        try{
           Reader in = new FileReader(inputFile);
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
        OffsetDateTime releaseStart = OffsetDateTime.ofInstant(df.parse(startRelease).toInstant(), TimeZone.getTimeZone("UTC").toZoneId());
        OffsetDateTime releaseEnd = OffsetDateTime.ofInstant(df.parse(lastRelease).toInstant(), TimeZone.getTimeZone("UTC").toZoneId());


        for(SATD satd: satdList) {

            RevCommit satdDeletedCommit = getCommit(repository, satd.commitId);
            System.out.println("Find this SATD in " + satd.commitId +" : "+ satd.fileName + " @ " + satd.line +" : " + satd.content );
            PersonIdent deletedIdent = satdDeletedCommit.getCommitterIdent();
            Date deletedDate = deletedIdent.getWhen();
            OffsetDateTime deleted_odt = OffsetDateTime.ofInstant(deletedDate.toInstant(), TimeZone.getTimeZone("UTC").toZoneId());

            if(!(deleted_odt.isAfter(releaseStart)&&deleted_odt.isBefore(releaseEnd))){
                continue;
            }

            RevCommit satdAddedCommit = blame(repository, satd.commitId, satd.fileName, satd.line);
            if (satdAddedCommit != null) {

                PersonIdent AddedIdent = satdAddedCommit.getCommitterIdent();
                Date AddedDate = AddedIdent.getWhen();
                OffsetDateTime added_odt = OffsetDateTime.ofInstant(AddedDate.toInstant(), TimeZone.getTimeZone("UTC").toZoneId());

                // 時間を計測（satdDeletedCommit-satdAddedCommit）
                if(!(added_odt.isAfter(releaseStart)&&added_odt.isBefore(releaseEnd))){
                    continue;
                }
                commitBlameWrite.write(satdDeletedCommit.getId().getName() + "," + deleted_odt + "," + satdAddedCommit.getId().getName() + "," + added_odt + "," + abs(ChronoUnit.DAYS.between(added_odt,deleted_odt)) + "\n");
                System.out.println("jujujuju");
            }else{
                commitBlameWrite.write(satdDeletedCommit.getId().getName() + "," + deleted_odt + ",null,null,null\n");

            }

        }
        commitBlameWrite.close();
    }




    public RevCommit blame(Repository repository, String startCommitId, String fileName, int i) throws IOException, GitAPIException {
        if (shouldSkip(startCommitId, fileName)){
            return null;
        }


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
        //blamer.setFollowFileRenames(true);//いつかOnにしたい
        Runtime.getRuntime().gc();//Garbage collection
        BlameResult result = blamer.call();

        return result.getSourceCommit(i);
        //NOTE: 月は"authorDate.getMonth()+1"で取れる．0が１月
    }

    private boolean shouldSkip(String startCommitId, String fileName) {
        if (startCommitId.equals("e3517ddc41f3c9536a29ef9be4e7dd3104993ab2")&&fileName.equals("org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java")){
            return true;
        }
        if (startCommitId.equals("28e2fc99c0e1bcaffcd4fcfc53a3c19326d33a79")&&fileName.equals("org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/SealedTypesTests.java")){
            return true;
        }
        if (startCommitId.equals("28e2fc99c0e1bcaffcd4fcfc53a3c19326d33a79")&&fileName.equals("org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java")){
            return true;
        }
        if (startCommitId.equals("18a7dc2e684ffa0791a31611369c86d846acba9b")&&fileName.equals("org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/RecordsRestrictedClassTest.java")){
            return true;
        }
        if (startCommitId.equals("18a7dc2e684ffa0791a31611369c86d846acba9b")&&fileName.equals("org.eclipse.jdt.core.tests.compiler/src/org/eclipse/jdt/core/tests/compiler/regression/SealedTypes15Tests.java")){
            return true;
        }
        if (startCommitId.equals("18a7dc2e684ffa0791a31611369c86d846acba9b")&&fileName.equals("org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/parser/Parser.java")){
            return true;
        }
        if (startCommitId.equals("fa7aa0006a3c6d1734561abd7cd691bbcd0b2746")&&fileName.equals("org.eclipse.jdt.compiler.tool.tests/src/org/eclipse/jdt/compiler/tool/tests/CompilerToolTests.java")){
            return true;
        }
        if (startCommitId.equals("c960670d342ed783ee2c0a56da2b91dd180d5c4e")&&fileName.equals("org.eclipse.jdt.compiler.tool.tests/src/org/eclipse/jdt/compiler/tool/tests/CompilerToolTests.java")){
            return true;
        }
        return false;
    }
}