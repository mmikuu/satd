import jp.naist.sdlab.miku.module.DiffSATDDetector;
import org.junit.Assert;
import org.junit.Test;
import stub.SimilarityStub;
import util.SatdUtil;

import static util.SatdUtil.getExecutor;


public class CalcReplaceTest {
    @Test
    public void testReplacePatternA() throws Exception {

        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="fb6961b05f2cfe7393b13b4d880139b9f81cbedd";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        SimilarityStub similarityStub = new SimilarityStub("/Users/watanabemiku/sdlab/satd-analytics/src/test/java/stub/similarity/CalcTestSatd.csv");
        double BirdSimResult = similarityStub.getBirdSimilarity(executor.resultsParent.get(0).content,
                executor.resultsChild.get(0).content);
        double LevenSimResult = similarityStub.getLevenSimilarity(executor.resultsParent.get(0).content,
                executor.resultsChild.get(0).content);
        double LevenDisResult = similarityStub.getLevenSimilarity(executor.resultsParent.get(0).content,
                executor.resultsChild.get(0).content);



    }
}
