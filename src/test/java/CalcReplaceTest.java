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
    @Test
    public void testReplacePatternB() throws Exception {
        //TODO: 1コミット分データを入れる



        //TODO: PYTHONをJavaから動かす（テストテーブルで動くようにする）
        //TODO: 集計
        //TODO: 答え合わせ（Assert）
    }

}
