package jp.naist.sdlab.miku.module.commit;

import org.eclipse.jgit.revwalk.RevCommit;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
