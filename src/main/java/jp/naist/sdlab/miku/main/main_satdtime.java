package jp.naist.sdlab.miku.main;

import jp.naist.sdlab.miku.module.CommandExecutor;
import jp.naist.sdlab.miku.module.SATD;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//SQP Import
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class main_satdtime {
    // urlはCommandExecutorも変更する！
    static String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
//    static String url = "https://github.com/eclipse-platform/eclipse.platform.ui.git";
//    static String url = "https://github.com/eclipse-platform/eclipse.platform.swt.git";


    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static Map<String, Integer> TotalReleaseCommit = new LinkedHashMap<>();
    public static List<String> releaseDates = Arrays.asList("2016-06-22", "2017-06-28", "2018-06-27", "2018-09-19", "2018-12-19", "2019-03-20", "2019-06-19", "2019-09-19", "2019-12-18", "2020-03-18", "2020-06-17", "2020-09-16", "2020-12-16", "2021-03-17", "2021-06-16");//, "2020-06-16", "2020-06-16", "2021-09-15", "2021-12-08", "2022-03-16"

    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        String[] tmp = url.split("/");
        String project = tmp[tmp.length - 1];
        String cloneDir = "repos/" + project;
        Map<String, List<SATD>> satdPerRelease = new LinkedHashMap<>();
        int AddedCommit = 0;
        Repository repository = gitService.cloneIfNotExists(cloneDir, url);
        Git git = new Git(repository);
        Iterable<RevCommit> log = git.log().call();
        List<SATD>allSatd = new ArrayList<>();
        CommandExecutor executor = new CommandExecutor(url, repository, "repos/eclipse.jdt.core/");
        FileWriter addedSatdPFileWriter = new FileWriter("satdParentDate.csv",true);
        FileWriter addedSatdCFileWriter = new FileWriter("satdChildDate.csv",true);
        for (RevCommit commit : log) {
//            LocalDateTime commitDate = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
            LocalDateTime commitDate = LocalDateTime.ofInstant(commit.getCommitterIdent().getWhen().toInstant(),
                    ZoneId.systemDefault());
            commitDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            for (int i = 0; i < releaseDates.size() - 1; i++) {
                LocalDateTime releaseStartDate = LocalDate.parse(releaseDates.get(i), formatter).atStartOfDay();
                LocalDateTime releaseEndDate = LocalDate.parse(releaseDates.get(i + 1), formatter).atStartOfDay();
                if (commitDate.isBefore(releaseStartDate)) {
                    break;
                } else if (commitDate.isBefore(releaseEndDate)) {
                    AddedCommit = TotalReleaseCommit.getOrDefault(releaseDates.get(i), 0);
                    AddedCommit += 1;
                    TotalReleaseCommit.put(releaseDates.get(i), AddedCommit);
                    List<SATD> eachSATDs = satdPerRelease.getOrDefault(releaseDates.get(i), new ArrayList<>());
                    executor.runCommand(commit.getId().getName());
                    //TODO:Type:REPLACEの場合　類似度の計算を行う関数を作成する
                    matchHash(executor.resultsParent, executor.resultsChild,addedSatdPFileWriter,addedSatdCFileWriter);
                    eachSATDs.addAll(executor.resultsParent);
                    eachSATDs.addAll(executor.resultsChild);
                    satdPerRelease.put(releaseDates.get(i), eachSATDs);
                }
            }
        }

        writeResult(satdPerRelease);
        printCounts(satdPerRelease);
    }

    private static void matchHash(List<SATD> resultsParent, List<SATD> resultsChild,FileWriter addedSatdPFileWriter,FileWriter addedSatdCFileWriter ) throws IOException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DriverManager.getConnection(
                    "jdbc:mysql//localhost:3306/samehash_db",
                    "me",
                    "goma");
        } catch (SQLException e){
            System.out.println("データベース接続エラー");
        }

        for(SATD satdP: resultsParent){
//            System.out.println("P: "+satdP);
            for(SATD satdC : resultsChild){
//                System.out.println("C: "+satdC);
                if(satdP.getHash() == satdC.getHash()){
                    addedSatdPFileWriter.write(satdP.content + "\n");
                    addedSatdCFileWriter.write(satdC.content + "\n");
                    addedSatdPFileWriter.flush();
                    addedSatdCFileWriter.flush();
                    System.out.println("=============================");
                    System.out.println("Parent");
                    System.out.println(satdP.getHash());
                    System.out.println(satdP.toString());
                    System.out.println("-----------------------------");
                    System.out.println("Child");
                    System.out.println(satdC.getHash());
                    System.out.println(satdC.toString());
                    System.out.println("=============================");
                }
            }
        }
    }

    private static void printCounts(Map<String, List<SATD>> satdPerRelease) throws IOException {

        FileWriter releaseAddedFileWriter = new FileWriter("releaseAddedSatd.csv");
        FileWriter releaseDeletedFileWriter = new FileWriter("releaseDeletedSatd.csv");
        Map<String, Integer> satdAddedRelease = new LinkedHashMap<>();
        Map<String, Integer> satdDeletedRelease = new LinkedHashMap<>();
        for (String dates : releaseDates) {
            if (dates.equals("2021-06-16")) {
                break;
            }
            List<SATD> satdList = satdPerRelease.get(dates);
            for (SATD totalsatd : satdList) {
                switch (totalsatd.type) {
                    case ADDED:
                        Integer AddedCount = satdAddedRelease.getOrDefault(dates, 0);
                        AddedCount += 1;
                        satdAddedRelease.put(dates, AddedCount);
                    case DELETED:
                        Integer DeletedCount = satdDeletedRelease.getOrDefault(dates, 0);
                        DeletedCount += 1;
                        satdDeletedRelease.put(dates, DeletedCount);
                }
            }
            System.out.println(dates + " : " + satdAddedRelease.get(dates));
            releaseAddedFileWriter.write(satdAddedRelease.get(dates) + "\n");
            System.out.println(dates + " : " + satdDeletedRelease.get(dates));
            releaseDeletedFileWriter.write(satdDeletedRelease.get(dates).toString() + "\n");
            System.out.println("COMMI_TOTAL " + dates + " : " + TotalReleaseCommit.get(dates));

        }
    }


    private static void writeResult(Map<String, List<SATD>> satdPerRelease) throws IOException {
        FileWriter addedCountFileWriter = new FileWriter("added_satd.csv");
        FileWriter deletedCountFileWriter = new FileWriter("deleted_satd.csv");
        FileWriter replacedCountFileWriter = new FileWriter("replace_satd.csv");
        addedCountFileWriter.write("release,type,commitID,filename,line,content\n");
        deletedCountFileWriter.write("release,type,commitID,filename,line,content\n");
        replacedCountFileWriter.write("release,type,commitID,filename,line,content\n");

        int addedSatdNumber = 0;
        Set<String> addedSatdCommits = new HashSet<>();
        int deletedSatdNumber = 0;
        Set<String> deletedSatdCommits = new HashSet<>();
        int replacedSatdNumber =0;
        Set<String> replacedSatdCommits = new HashSet<>();

        for (String r : satdPerRelease.keySet()) {
            List<SATD> satdList = satdPerRelease.get(r);
            for (SATD satd : satdList) {
                switch (satd.type) {
                    case ADDED:
                        addedCountFileWriter.write(r + "," + satd.toString() + "\n");
                        addedSatdNumber++;
                        addedSatdCommits.add(satd.commitId);
                        break;
                    case DELETED:
                        deletedCountFileWriter.write(r + "," + satd.toString() + "\n");
                        deletedSatdNumber++;
                        deletedSatdCommits.add(satd.commitId);
                        break;
                    case REPLACE:
                        replacedCountFileWriter.write(r + "," + satd.toString() + "\n");
                        replacedSatdNumber++;
                        replacedSatdCommits.add(satd.commitId);
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        }
        //System.out.println("true回数" + added_number + "miner回数" + deleted_number);
        System.out.println("Added jp.naist.sdlab.miku.module.SATD number:" + addedSatdNumber);
        System.out.println("Deleted jp.naist.sdlab.miku.module.SATD number:" + deletedSatdNumber);
        System.out.println("Added jp.naist.sdlab.miku.module.SATD commits:" + addedSatdCommits.size());
        System.out.println("Deleted jp.naist.sdlab.miku.module.SATD commits:" + deletedSatdCommits.size());

    }
}
