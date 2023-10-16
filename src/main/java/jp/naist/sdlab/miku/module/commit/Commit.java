package jp.naist.sdlab.miku.module.commit;

import org.eclipse.jgit.revwalk.RevCommit;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static jp.naist.sdlab.miku.main.MainCalculatedTime.formatter;

/**
 * This class is used to store/get data from Database (hibernate)
 */
public class Commit implements Serializable {
//    @Transient
    /**
     * Project name
     */
    public String project;
    /**
     * Commit sha
     */
    public String commitId;
    /**
     * Name of committer
     */
    public String committerName;
    /**
     * E-mail address of committer
     */
    public String committerEmail;
    /**
     * Date of commit
     */
    public LocalDateTime commitDate;

    /**
     * changed files in this commit
     */
    public List<ChangedFile> changedFileList;

    /**
     * parents commits.
     * When the size of list is more than 1, this is a merge commit.
     * When the size of list is 0, this is the first commit in this repository.
     */
    public List<String> parentCommitIds;

    /**
     * child commits.
     */
    public List<String> childCommitIds;

    /**
     * Bug reports fixed by this commit.
     * Note that this bug reports is roughly detected by this tools using simple regrex.
     * This study does not use this information.
     */
    public Set<String> fixedReports;
    /**
     * Commit message
     */
    public String commitComment;
    /**
     * Total added lines in this commit.
     */
    public Integer addedLines;
    /**
     * Total deleted lines in this commit.
     */
    public Integer deletedLines;
    /**
     * Total number of changed files in this commit.
     */
    public Integer changedFiles;
    /**
     * Boolean if this commit is a merge commit.
     */
    public boolean isMergeCommit;

    public static List<String> releaseDates = Arrays.asList("2016-06-22", "2017-06-28", "2018-06-27", "2018-09-19", "2018-12-19", "2019-03-20", "2019-06-19", "2019-09-19", "2019-12-18", "2020-03-18", "2020-06-16");//, "2020-06-16", "2020-06-16", "2021-09-15", "2021-12-08", "2022-03-16"
    public static List<String> release = Arrays.asList("TR1","TR2","RR1","RR2","RR3","RR4","RR5","RR6","RR7","RR8");

    public Commit(){
    }

    /**
     * This constructor copy the contents of RevCommit by JGit to this class to store the data.
     * @param revCommit
     */
    public Commit(String project, RevCommit revCommit){
        this.project = project;
        changedFileList = new ArrayList<>();
        parentCommitIds = new ArrayList<>();
        childCommitIds = new ArrayList<>();
        this.commitId = revCommit.name();
        this.commitDate = LocalDateTime.ofInstant(revCommit.getAuthorIdent().getWhen().toInstant(),
                ZoneId.systemDefault());
        this.commitComment = revCommit.getShortMessage();
        this.committerName = revCommit.getCommitterIdent().getName();
        this.committerEmail = revCommit.getCommitterIdent().getEmailAddress();
        for (RevCommit parent : revCommit.getParents()) {
            this.parentCommitIds.add(parent.getId().name());
        }
//        this.fixedReports = MyBugReportUtil.searchIssue(this.commitComment);
        this.isMergeCommit = isMergeCommit();
    }

    public boolean isMergeCommit(){
        return this.parentCommitIds.size() > 1;
    }

    /**
     * Calculate the number of total lines in this commit
     */
    public void setLines() {
        addedLines = 0;
        deletedLines = 0;
        changedFiles = changedFileList.size();
        for(ChangedFile cf: changedFileList){
            addedLines += cf.addedLines;
            deletedLines += cf.deletedLines;
        }
    }
    public String getRelease(){
        for(int i =0; i<releaseDates.size(); i++){
            if( releaseDates.get(i) == releaseDates.get(10)){
                break;
            }
            LocalDateTime releaseStartDate = LocalDate.parse(releaseDates.get(i), formatter).atStartOfDay();
            LocalDateTime releaseEndDate = LocalDate.parse(releaseDates.get(i + 1), formatter).atStartOfDay();
            if (this.commitDate.isBefore(releaseStartDate)) {
                break;
            } else if (this.commitDate.isBefore(releaseEndDate)) {
                return release.get(i);
            }
        }
        return null;
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Commit){
            Commit c = (Commit) o;
            return this.commitId.equals(c.commitId)&&this.project.equals(c.project);
        }
        return false;
    }
    @Override
    public int hashCode(){
        return Objects.hash(project, commitId);
    }
}
