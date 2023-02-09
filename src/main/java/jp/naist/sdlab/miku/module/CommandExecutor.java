package jp.naist.sdlab.miku.module;

import satd_detector.core.utils.SATDDetector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {
    public static int AddedCount = 0;
    public static int DeletedCount = 0;
    public static List<SATD> runCommand(String commitId, Path directory, String... command) throws IOException, InterruptedException {
        Objects.requireNonNull(directory, "directory");
        if (!Files.exists(directory)) {
            throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
        }

        List<SATD> results = new ArrayList<SATD>();
        ProcessBuilder pb = new ProcessBuilder()
                .command(command)
                .directory(directory.toFile());
        Process p = pb.start();
        StreamGobbler outputGobbler = new StreamGobbler(commitId, p.getInputStream(), results);
        outputGobbler.start();

        int exit = p.waitFor();
        outputGobbler.join();
        if (exit != 0) {
            System.err.println("!!!!!!!!!!ERROR!!!!!!!!!!");//TODO: handle this
//            throw new AssertionError(String.format("runCommand returned %d", exit));
        }
        p.destroy();

        return results;
    }
    private static class StreamGobbler extends Thread {
        private final InputStream is;
        public List<SATD> satdList;
        String commitId;
        SATDDetector detector;
        private StreamGobbler(String commitId, InputStream is, List<SATD> satdList) {
            this.is = is;
            this.satdList = satdList;
            this.commitId = commitId;
            this.detector = new SATDDetector();

        }

        @Override
        public void run() {
            List<String> addedSatdRegrex = new ArrayList<>();
            List<String> deletedSatdRegrex = new ArrayList<>();
            List<Pattern> addedRegrexesPatterns = new ArrayList<>();
            List<Pattern> deletedRegrexesPatterns = new ArrayList<>();

            addedSatdRegrex.add("\\s*\\+\\s*.*\\/\\/(.+)");
            addedSatdRegrex.add("\\s*\\+\\s*.*\\/\\*(.+)");
            addedSatdRegrex.add("\\s*\\+\\s*.*\\*(.+)");
            addedSatdRegrex.add("\\s*\\+\\s*(.+)\\*\\/");

            deletedSatdRegrex.add("\\s*-\\s*.*\\/\\/(.+)");
            deletedSatdRegrex.add("\\s*-\\s*.*\\/\\*(.+)");
            deletedSatdRegrex.add("\\s*-\\s*.*\\*(.+)");
            deletedSatdRegrex.add("\\s*-\\s*(.+)\\*\\/");

            String fileRegrex = "^diff\\s-{2}git\\s(.{1,})";
            String lineNoRegrex = "@@\\s-(\\d+).*\\s\\+(\\d+).*\\s@@";

            Pattern fileNamePattern = Pattern.compile(fileRegrex);
            for (int i = 0; i<addedSatdRegrex.size(); i++) {
                addedRegrexesPatterns.add(Pattern.compile(addedSatdRegrex.get(i)));
                deletedRegrexesPatterns.add(Pattern.compile(deletedSatdRegrex.get(i)));
            }
            Pattern lineNoPattern = Pattern.compile(lineNoRegrex);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                String aFile = null;
                String bFile = null;
                Integer aFileLineNo = null;
                Integer bFileLineNo = null;
                while ((line = br.readLine()) != null) {
                    //Get file names
                    Matcher fileNameMatcher = fileNamePattern.matcher(line);
                    if(fileNameMatcher.find()){
                        String[] tmp = fileNameMatcher.group(1).split(" ");
                        aFile = tmp[0];
                        bFile = tmp[1];
                        aFileLineNo = null;
                        bFileLineNo = null;
                        continue;
                    }
                    //Get the start line number in diff files
                    Matcher lineNoMatcher = lineNoPattern.matcher(line);
                    if(lineNoMatcher.find()){
                        aFileLineNo = Integer.parseInt(lineNoMatcher.group(1));
                        bFileLineNo = Integer.parseInt(lineNoMatcher.group(2));
                        continue;
                    }
                    //Skip irrelevant line (---, +++)
                    if (aFileLineNo == null) continue;
                    //Count line number
                    SATD satd = null;
                    if (line.startsWith("-")){//When only deleted
                        aFileLineNo ++;
                        //Detect deleted jp.naist.sdlab.miku.module.SATD
                        for(Pattern pattern :deletedRegrexesPatterns) {
                            Matcher deletedMatcher = pattern.matcher(line);
                            satd = detect(deletedMatcher, SATD.Type.DELETED, aFile, aFileLineNo);
                            if(satd !=null){
                                DeletedCount += 1;
                                break;
                            }
                        }
                    } else if (line.startsWith("+")){//When only added
                        bFileLineNo ++;
                        //Detect added jp.naist.sdlab.miku.module.SATD
                        for(Pattern pattern :addedRegrexesPatterns) {
                            Matcher addedMatcher = pattern.matcher(line);
                            satd = detect(addedMatcher, SATD.Type.ADDED, aFile, aFileLineNo);
                            if(satd !=null){
                                AddedCount +=1;
                                break;
                            }
                        }
                    }else {//When no changes
                        aFileLineNo ++;
                        bFileLineNo ++;
                        continue;
                    }
                    if(satd!=null) satdList.add(satd);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private SATD detect(Matcher matcher, SATD.Type type, String fileName, int lineNo) {
            if (matcher.find()) {
                String check = matcher.group(1);
                boolean result = this.detector.isSATD(check);
                if (result) {
                    SATD satd = new SATD();
                    satd.commitId = this.commitId;
                    satd.fileName = fileName;
                    satd.line = lineNo;
                    satd.content = check;
                    satd.type = type;
                    return satd;
                }
            }
            return null;
        }

    }
}
