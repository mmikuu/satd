package jp.naist.sdlab.miku.module.commit;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.refactoringminer.util.GitServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitServiceImpl2 extends GitServiceImpl {

    /**
     * create a Commit from RevCommit of JGit
     * @param repository
     * @param currentCommit
     * @return
     */
    public Commit getCommit(String project, Repository repository, RevCommit currentCommit) {
        if (currentCommit.getParentCount() <= 0) {
            return getCommit(project, repository, currentCommit, null);
        }
        RevCommit parent = currentCommit.getParent(0);
        return getCommit(project, repository, currentCommit, parent);
    }

    /**
     * create a Commit from RevCommit of JGit
     * @param repository
     * @param currentCommit
     * @param parent
     * @return
     */
    private Commit getCommit(String project, Repository repository, RevCommit currentCommit, RevCommit parent){
        ObjectId newTree = currentCommit.getTree();
        ObjectId oldTree;

        if (parent==null){
            oldTree = null;
        }else{
            oldTree = parent.getTree();
        }
        TreeWalk tw = new TreeWalk(repository);
        tw.setRecursive(true);
        try {
            if(oldTree==null){
                tw.addTree(new EmptyTreeIterator());
            }else{
                tw.addTree(oldTree);
            }
            tw.addTree(newTree);
            List<DiffEntry> diffs = DiffEntry.scan(tw);
            Commit commit = new Commit(project, currentCommit);
            commit.changedFileList = this.getChangedFiles(repository, diffs);
            commit.setLines();
            return commit;
        } catch (Exception e) {
            System.out.println(e);
            throw new AssertionError();
        }
    }


    /**
     * get changed files in the commit
     * @param repository
     * @param diffs
     * @return
     * @throws IOException
     */
    private List<ChangedFile> getChangedFiles(Repository repository, List<DiffEntry> diffs) throws IOException {
        List<ChangedFile> files = new ArrayList<>();
        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        diffFormatter.setRepository(repository);
        diffFormatter.setContext(0);
        for (DiffEntry entry : diffs) {
            FileHeader header = diffFormatter.toFileHeader(entry);
//            System.out.println("   :"+header.getHunks().get(0).toEditList().get(0));
            files.add(this.getChangedFile(header));
        }
        diffFormatter.close();
        return files;
    }

    /**
     * get changed files in the commit
     * @param header
     * @return
     */
    private ChangedFile getChangedFile(FileHeader header) {
        ChangedFile cf =new ChangedFile(header);
        List<? extends HunkHeader> hunks = header.getHunks();
        for (HunkHeader hunkHeader : hunks) {
            EditList var16 = hunkHeader.toEditList();
            cf.chunks = this.getChunks(var16);
        }
        cf.setLines();
        return cf;
    }

    /**
     * Create Chunk from EditList of JGit
     * @param editList
     * @return
     */
    private List<Chunk> getChunks(EditList editList) {
        List<Chunk> chunks = new ArrayList<>();
        for(Edit edit: editList) {
            chunks.add(new Chunk(edit));
        }
        return chunks;
    }

    /**
     * Extract RevCommits and create a list of Commits
     * @param repo
     * @return
     * @throws Exception
     */
    public List<Commit> getAllCommits(String project, Repository repo, String branch) throws Exception {
        List<Commit> commits = new ArrayList<>();
        RevWalk walk = this.createAllRevsWalk(repo, branch);
        walk.markStart(walk.parseCommit(repo.resolve("HEAD")));
        for (RevCommit r: walk){
            if(r.getParents().length>0){
                Commit c = getCommit(project, repo, r, r.getParent(0));
                commits.add(c);
            }else{
                Commit c = new Commit(project, r);
                commits.add(c);
            }

        }
        return commits;
    }

    /**
     * get rev walk. If branch is specified, only its revWalk will be returned.
     * @param repository
     * @param branch
     * @return
     * @throws Exception
     */
    public RevWalk createAllRevsWalk(Repository repository, String branch) throws Exception {
        List<ObjectId> currentRemoteRefs = new ArrayList<ObjectId>();
        for (Ref ref : repository.getRefDatabase().getRefs()) {
            String refName = ref.getName();
            if (refName.startsWith("refs/remotes/origin/")) {
                if (branch == null || refName.endsWith("/" + branch)) {
                    currentRemoteRefs.add(ref.getObjectId());
                }
            }
        }

        RevWalk walk = new RevWalk(repository);
        for (ObjectId newRef : currentRemoteRefs) {
            walk.markStart(walk.parseCommit(newRef));
        }
        return walk;
    }
}
