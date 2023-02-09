package jp.naist.sdlab.miku.main;

import jp.naist.sdlab.miku.module.CommandExecutor;
import jp.naist.sdlab.miku.module.SATD;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//日付import

//グラフ作成ライブラリ
//matplotっでしようかな？？
public class main_yuta {

    static String url = "https://github.com/eclipse-jdt/eclipse.jdt.core";
    public static int AddedSatd = 0;
    public static int DeletedSatd = 0;
    public static List<Integer> ReleaseAddedSatdes = new ArrayList(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0));
    public static List<Integer> ReleaseDeletedSatdes = new ArrayList(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0));
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static List<String> releaseDates = Arrays.asList("2016-06-22", "2017-06-28", "2018-06-27", "2018-09-19", "2018-12-19", "2019-03-20", "2019-06-19", "2019-09-19", "2019-12-18", "2020-03-18", "2020-06-17", "2020-09-16", "2020-12-16", "2021-03-17", "2021-06-16");//, "2020-06-16", "2020-06-16", "2021-09-15", "2021-12-08", "2022-03-16"

    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        String[] tmp = url.split("/");
        String project = tmp[tmp.length-1];
        String cloneDir = "repos/"+project;
        Map<String, List<SATD>> satdPerRelease = new LinkedHashMap<>();

        Repository repository = gitService.cloneIfNotExists(cloneDir, url);
        Git git = new Git(repository);
        Iterable<RevCommit> log = git.log().call();



        for (RevCommit commit : log) {
//            LocalDateTime commitDate = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
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
                    satdPerRelease.put(releaseDates.get(i), allSATDs);

                }else {
                    continue;
                }


            }
        }
        writeResult(satdPerRelease);
        printCounts(satdPerRelease);



    }

    private static void printCounts(Map<String, List<SATD>> satdPerRelease) throws IOException {

        FileWriter releaseAddedFileWriter = new FileWriter("releaseAddedSatd.csv");
        FileWriter releaseDeletedFileWriter = new FileWriter("releaseDeletedSatd.csv");
        Map<String, Integer> satdAddedRelease = new LinkedHashMap<>();
        Map<String, Integer> satdDeletedRelease = new LinkedHashMap<>();
        for(String dates: releaseDates){
            if(dates.equals("2021-06-16")){
                break;
            }
            List<SATD> satdList = satdPerRelease.get(dates);
            for( SATD totalsatd: satdList){
                switch(totalsatd.type){
                    case ADDED:
                        Integer AddedCount = satdAddedRelease.getOrDefault(dates,0);
                        AddedCount +=1;
                        satdAddedRelease.put(dates,AddedCount);
                    case DELETED:
                        Integer DeletedCount = satdDeletedRelease.getOrDefault(dates,0);
                        DeletedCount +=1;
                        satdDeletedRelease.put(dates,DeletedCount);
                }
            }
            System.out.println(dates+" : "+satdAddedRelease.get(dates));
            releaseAddedFileWriter.write(satdAddedRelease.get(dates)+"\n");
            System.out.println(dates+" : "+satdDeletedRelease.get(dates));
            releaseDeletedFileWriter.write(satdDeletedRelease.get(dates).toString()+"\n");
        }
    }


    private static void writeResult(Map<String, List<SATD>> satdPerRelease) throws IOException {
        FileWriter addedCountFileWriter = new FileWriter("added_satd.csv");
        FileWriter deletedCountFileWriter = new FileWriter("deleted_satd.csv");
        int addedSatdNumber = 0;
        Set<String> addedSatdCommits = new HashSet<>();
        int deletedSatdNumber = 0;
        Set<String> deletedSatdCommits = new HashSet<>();

        for (String r: satdPerRelease.keySet()){
            List<SATD> satdList = satdPerRelease.get(r);
            for (SATD satd: satdList){
                switch (satd.type){
                    case ADDED:
                        addedCountFileWriter.write(r+","+satd.toString() + "\n");
                        addedSatdNumber ++;
                        addedSatdCommits.add(satd.commitId);
                        break;
                    case DELETED:
                        deletedCountFileWriter.write(r+","+satd.toString() + "\n");
                        deletedSatdNumber ++;
                        deletedSatdCommits.add(satd.commitId);
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


