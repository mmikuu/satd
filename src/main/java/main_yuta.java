import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import satd_detector.core.utils.SATDDetector;
import weka.core.pmml.jaxbbindings.False;
import weka.core.pmml.jaxbbindings.True;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//日付import

//グラフ作成ライブラリ
//matplotっでしようかな？？
public class main_yuta {
    public static int added_number = 0;
    public static int deleted_number = 0;

    public static String file_check = null;
    public static List<String> true_message = new ArrayList<>();
    public static List<String> miner_message = new ArrayList<>();

    public static List<String> file_name = new ArrayList<>();

    public static List<String> coment = new ArrayList<>();

    public static boolean miner_check = false;


    public static void main(String[] args) throws IOException, InterruptedException {
        Path directory = Paths.get("C:\\Users\\wm124\\summer_inter\\satd1\\eclipse.jdt.core");//aspectj.eclipse.jdt.core
        GitService gitService = new GitServiceImpl();
        File file_add = new File("C:\\Users\\wm124\\summer_inter\\satd1\\ture_message.txt");
        File file_miner = new File("C:\\Users\\wm124\\summer_inter\\satd1\\miner_message.txt");
        File file_path = new File("C:\\Users\\wm124\\summer_inter\\satd1\\file_name.txt");
        File file_commit = new File("C:\\Users\\wm124\\summer_inter\\satd1\\commitID.txt");
        FileWriter filewriter_add = new FileWriter(file_add);
        FileWriter filewriter_miner = new FileWriter(file_miner);
        FileWriter filewriter_name = new FileWriter(file_path);
        FileWriter filewriter_commit = new FileWriter(file_commit);

        int addedSATDnumber[] = new int[15];
        int deletdSATDnumber[] = new int[15];
        int commitNumber[] = new int[15];

        //TODO jgitdd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> start = Arrays.asList("2016-06-22", "2017-06-28", "2018-06-27", "2018-09-19", "2018-12-19", "2019-03-20", "2019-06-19", "2019-09-19", "2019-12-18", "2020-03-18", "2020-06-17", "2020-09-16", "2020-12-16", "2021-03-17", "2021-06-16");//, "2020-06-16", "2020-06-16", "2021-09-15", "2021-12-08", "2022-03-16"
//        List<String> end = Arrays.asList("2017-06-27", "2018-06-27", "2018-09-19", "2018-12-19", "2019-03-20", "2019-06-19", "2019-09-19", "2019-12-18", "2020-03-18", "2020-06-17", "2020-09-16", "2020-12-16", "2020-03-17", "2020-06-16");//, "2020-06-16", "2021-09-15", "2021-12-08", "2022-03-16", "2022-06-16"



            try {
                Repository repository = gitService.openRepository("eclipse.jdt.core");
                Git git = new Git(repository);

                Iterable<RevCommit> log = git.log().call();

                for (RevCommit commit : log) {
                    LocalDateTime commitDate = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
                    commitDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    for (int i = 0; i < start.size()-1; i++) {
                        int number = 0;
                        LocalDate date_start = LocalDate.parse(start.get(i), formatter);
                        LocalDate date_end = LocalDate.parse(start.get(i+1), formatter);

                        LocalDateTime releaseDate_start = date_start.atStartOfDay();
                        LocalDateTime releaseDate_end = date_end.atStartOfDay();
                        //System.out.println(commit);
                        if (commitDate.isBefore(releaseDate_start) ) {
                            break;
                        }else if (commitDate.isBefore(releaseDate_end)){
                            added_number = 0;
                            deleted_number = 0;
                            runCommand(directory, "git", "diff", "--no-ext-diff", "--unified=0", "--no-prefix", "-a", "-w", commit.getName() + "^.." + commit.getName());
                            addedSATDnumber[i]+=added_number;
                            deletdSATDnumber[i]+=deleted_number;
                            commitNumber[i]++;
                            if(miner_check == true) {//commitid
                                filewriter_commit.write(commit.getName() + "\n");
                            }
                            miner_check = false;
                        }//&& releaseDate_end.isAfter(commitDate)) || releaseDate_start.isEqual(commitDate) || releaseDate_end.isEqual(commitDate
                            number++;
                            //commitDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                            //System.out.println("時刻" + commitDate);
                    }

                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (String s : true_message) {
                filewriter_add.write(s + "\n");
            }
            for (String s : miner_message) {
                filewriter_miner.write(s + "\n");
            }
            for (String s:file_name){
                filewriter_name.write(s +"\n");
            }

            //Tnumber[i] = true_number;
            //Mnumber[i] = miner_number;
            //Gitnumber[i]= number;
            //System.out.println(number);

        //System.out.println("true回数" + added_number + "miner回数" + deleted_number);
        System.out.println("Tnumber"+ Arrays.toString(addedSATDnumber));
        System.out.println("Mnumber"+ Arrays.toString(deletdSATDnumber));
        System.out.println("Number" + Arrays.toString(commitNumber));
    }
    //グフラ作成

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
            StringBuilder miner = new StringBuilder();
            SATDDetector detector1 = new SATDDetector();


            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
                String line;
                while ((line = br.readLine()) != null) {
                    String regex_add = "\\s*\\+\\s*.*\\/\\/(.+)";
                    String regex_miner = "\\s*-\\s*.*\\/\\/(.+)";
                    String file_match = "^diff\\s-{2}git\\s(.{1,})";
                    boolean add = false;
                    boolean mina = false;

                    //
                    Pattern p_add = Pattern.compile(regex_add);
                    Pattern p_miner = Pattern.compile(regex_miner);
                    Pattern p_file = Pattern.compile(file_match);

                    //System.out.println(line);

                    Matcher m_add = p_add.matcher(line);
                    Matcher m_miner = p_miner.matcher(line);
                    Matcher m_file = p_file.matcher(line);
                    //System.out.println(line);

                    if(m_file.find()){
                        file_check = m_file.group(1);
                        //System.out.println(file_check);
                    }

                    if (m_add.find()) {
                        String check = m_add.group(1);
                        boolean result = detector1.isSATD(check);

                        if (result) {
                            added_number += 1;
                            true_message.add(String.valueOf(added_number) + ":" + m_add.group(0));
                            add = true;
                        }

                    }

                    if (m_miner.find()) {
                        String check = m_miner.group(1);

                        boolean result = detector1.isSATD(check);
                        if (result) {
                            miner_check = true;
                            deleted_number += 1;
                            miner_message.add(String.valueOf(deleted_number) + ":" + m_miner.group(0));
                            mina = true;
                        }

                    }

                    if(mina == true ){
                        file_name.add(file_check);
                        //System.out.println("2"+file_name);
                    }





                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}


