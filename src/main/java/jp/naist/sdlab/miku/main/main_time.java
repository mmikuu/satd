package jp.naist.sdlab.miku.main;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jfree.ui.action.ActionRadioButton;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



public class main_time {

    public static List<String> file_name = new ArrayList();
    public static List<String> miner_message = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Path directory = Paths.get("C:\\Users\\wm124\\summer_inter\\satd1\\eclipse.jdt.core");//aspectj.eclipse.jdt.core
        GitService gitService = new GitServiceImpl();
        boolean reverse = false;
        try {
            FileInputStream fis_name = new FileInputStream("file_name.txt");
            FileInputStream fis_miner = new FileInputStream("miner_message.txt");
            BufferedReader bf_name = new BufferedReader(new InputStreamReader(fis_name, "UTF-8"));
            BufferedReader bf_miner = new BufferedReader(new InputStreamReader(fis_miner, "UTF-8"));
            String content;
            while ((content = bf_name.readLine()) != null) {
                file_name.add(content);
                System.out.println(content);
            }
            bf_name.close();
            while ((content = bf_miner.readLine()) != null) {
                miner_message.add(content);
                System.out.println(content);
            }
            bf_miner.close();


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            Repository repository = gitService.openRepository("eclipse.jdt.core");
            Git git = new Git(repository);
            ObjectId startCommit = repository.resolve(file_name.get(0));//ここを開始拠点とする
            BlameCommand blamer = git.blame();
            if (!reverse) {//普通のBlame
                blamer.setStartCommit(startCommit);
            } else {//リバースの場合
                blamer.reverse(startCommit, repository.resolve("HEAD"));
            }
            blamer.setFilePath(file_name.get(0));
            BlameResult result = blamer.call();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}