import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.*;
import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import satd_detector.core.train.Train;
import satd_detector.core.utils.SATDDetector;


public class main {
    static List<String> commitDate = new ArrayList<>();
    static List<String> commit_include = new ArrayList<>();



    public static void main(String[] args) {
        GitService gitService = new GitServiceImpl();

        ProcessBuilder processBuilder = new ProcessBuilder();

        try {
            Repository repository = gitService.openRepository("aspectj.eclipse.jdt.core");
            Git git = new Git(repository);
            Iterable<RevCommit> log = git.log().call();
            processBuilder.command("CMD","/C","cd ..");
            Process process = processBuilder.start();
            process.waitFor();
            processBuilder.command("CMD","/C","cd aspectj.eclipse.jdt.core");
            Process process2 = processBuilder.start();
            process2.waitFor();
            processBuilder.command("CMD","/C","pwd");
            Process process3 = processBuilder.start();
            process3.waitFor();


            for (RevCommit commit:log) {
                String front = null;
                System.out.println("commitID: " + commit.getName());
                //front = commit.getName();
                //processBuilder.command("git checkout" + commit.getName());
                processBuilder.command("CMD","/C","git diff " + commit.getName());

                try {

                    Process process4 = processBuilder.start();

                    StringBuilder output = new StringBuilder();
                    System.out.println("2");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process4.getInputStream()));
                    String line;
                    System.out.println("3");

                    while ((line = reader.readLine()) != null) {
                        
                        processBuilder.command("CMD","/C","q");
                        Process process5 = processBuilder.start();

                        System.out.println("1");
                        output.append(line + "\n");
                        System.out.println("1");

                    }

                    int exitVal = process.waitFor();
                    if (exitVal == 0) {
                        System.out.println("Success!");
                        System.out.println(output);
                        System.exit(0);
                    } else {
                        //abnormal...
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (NoHeadException ex) {
            throw new RuntimeException(ex);
        } catch (GitAPIException ex) {
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }



    }

    public static void day() throws Exception {
        GitService gitService = new GitServiceImpl();
        Repository repository = gitService.openRepository("aspectj.eclipse.jdt.core");

        Git git = new Git(repository);
        Iterable<RevCommit> log = git.log().call();

        commitDate = commitData(log,repository);
    }

    public static List<DiffEntry> diff(RevCommit parent,RevCommit commit ) throws Exception {
        GitService gitService = new GitServiceImpl();
        Repository repository = gitService.openRepository("aspectj.eclipse.jdt.core");
        DiffFormatter df = new DiffFormatter(System.out);
        df.setRepository(repository);
        RevWalk walk = new RevWalk(repository);
        ObjectId newTree = commit.getTree();
        ObjectId oldTree = parent.getTree();
        TreeWalk tw = new TreeWalk(repository);
        tw.setRecursive(true);
        tw.addTree(oldTree);
        tw.addTree(newTree);
        List<DiffEntry> diffs = DiffEntry.scan(tw);

        return diffs;
    }



    private static  List<String> commitData(Iterable<RevCommit> log,Repository repository) throws Exception {
        List<String> date = new ArrayList<>();
        String diff;
        for (RevCommit commit :log) {

            // Prepare the pieces
            //final Instant commitInstant = Instant.ofEpochSecond(commit.getCommitTime());
            //final ZoneId zoneId = commit.getAuthorIdent().getTimeZone().toZoneId();
            //final ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, zoneId);
            //final String gitDateTimeFormatString = "EEE MMM dd HH:mm:ss yyyy Z";
            //final String formattedDate = authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString));

            LocalDateTime now = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
            //date.add(String.valueOf(now));
            RevCommit child = commit;
            RevCommit parent = child.getParent(0);
            FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);
            //try (DiffFormatter formatter = new DiffFormatter(stdout)) {
            //    formatter.setRepository(repository);
            //    formatter.setContext(0);
            //    formatter.format(parent, child);

            //}
            diff = stdout.toString();
            //System.out.println("a:"+diff);
            String[] result = diff.split("\\n");

            for (int i = 0; i<result.length; i++){
                if(result[i].startsWith(" -")){
                    date.add(result[i]);
                }

            }

            //System.out.println(diff);
            //System.out.println(String.valueOf(now));
            date.add(String.valueOf(now));
            //System.out.println(date);


            //System.out.println(diff.get(0)); List<DiffEntry> diff = diff(commit.getParent(0),commit);

        }

        return date;
    }


}
