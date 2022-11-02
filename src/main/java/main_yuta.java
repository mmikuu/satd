import com.sun.xml.internal.bind.v2.TODO;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import satd_detector.core.utils.SATDDetector;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main_yuta {
    public static int true_number = 0;
    public static int false_number = 0;
    public static List<String> true_message = new ArrayList<>();

    public static List<String> all_true = new ArrayList<>();

    public static List<String> false_message = new ArrayList<>();
    public static void main(String[] args) throws IOException, InterruptedException {
        Path directory = Paths.get("C:\\Users\\wm124\\summer_inter\\satd1\\aspectj.eclipse.jdt.core");
        GitService gitService = new GitServiceImpl();


        //TODO jgit
        try{
            Repository repository = gitService.openRepository("aspectj.eclipse.jdt.core");
            Git git = new Git(repository);
            Iterable<RevCommit> log = git.log().call();

            for(RevCommit commit:log){
                LocalDateTime now = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
                String date = String.valueOf(now);
                if(date.contains("2016")){
                    System.out.println("時刻"+now);
                    runCommand(directory, "git", "diff", "--no-ext-diff","--unified=0","--no-prefix","-a", "-w", commit.getName()+"^.."+commit.getName());


                }

                try{
                    File file = new File("C:\\Users\\wm124\\summer_inter\\satd1\\ture_message.txt");
                    FileWriter filewriter = new FileWriter(file);
                    for ( int i = 0; i<true_message.size();  i++){
                        filewriter.write(true_message.get(i)+"\n");
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }




        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
    public static void show(Process process) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            //System.out.println(output);
            System.out.println("開始位置"+output.indexOf("- "));
        } else {
            //abnormal...
        }

    }
    public static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
        Objects.requireNonNull(directory, "directory");
        if (!Files.exists(directory)) {
            throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
        }


        ProcessBuilder pb = new ProcessBuilder()
                .command(command)
                .directory(directory.toFile());
        Process p = pb.start();
        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
        outputGobbler.start();
        errorGobbler.start();
        int exit = p.waitFor();
        errorGobbler.join();
        outputGobbler.join();
        if (exit != 0) {
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }
    }
    private static class StreamGobbler extends Thread {


        private final InputStream is;
        private final String type;


        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            StringBuilder output = new StringBuilder();
            StringBuilder miner= new StringBuilder();
            SATDDetector detector1 = new SATDDetector();


            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
                String line;
                while ((line = br.readLine()) != null) {
                        String regex = "\\s*\\+\\s*.*\\/\\/(.*)";  //  "\\s*-\\s*.*\\/\\/(.*)"
                        Pattern p = Pattern.compile(regex);
                        System.out.println(line);
                        Matcher m = p.matcher(line);
                        if(m.find()) {
                            String check = m.group(1);
                            boolean result = detector1.isSATD(check);
                            if (result) {
                                true_number += 1;
                                true_message.add(String.valueOf(true_number)+":"+m.group(1));
                            } else {
                                false_number += 1;
                                false_message.add(m.group(1));
                            }
                            System.out.println("結果" + result+"回数"+true_number+" "+true_message);
                            sleep(0);

                        }
                }
                System.out.println("true回数"+true_number+"false回数"+false_number);
                System.out.println("true_message"+true_message);


            } catch (IOException | InterruptedException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
