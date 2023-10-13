package stub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarityStub {
    class Similarity{
        Double birdSimilarity;
        Double levenSimilarity;
        Double levenDistance;
    }
    Map<String, Similarity> similarityMap;
    public SimilarityStub(String project){
        similarityMap = new HashMap<>();
        File file = new File(project);
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                String[] content = (line.split(","));
                Similarity simi = new Similarity();
                simi.birdSimilarity = Double.valueOf(content[2]);
                simi.levenSimilarity = Double.valueOf(content[3]);
                simi.levenDistance = Double.valueOf(content[4]);
                similarityMap.put(content[0]+content[1],simi);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double getBirdSimilarity(String pContent, String cContent){
        return similarityMap.get(pContent+cContent).birdSimilarity;
    }
    public double getLevenSimilarity(String pContent, String cContent){
        return similarityMap.get(pContent+cContent).levenSimilarity;
    }

    public double getLevenDistance(String pContent, String cContent){
        return similarityMap.get(pContent+cContent).levenDistance;
    }
}
