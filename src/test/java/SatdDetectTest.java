import jp.naist.sdlab.miku.module.DiffSATDDetector;
import jp.naist.sdlab.miku.module.SATD;
import org.junit.Assert;
import org.junit.Test;

import static util.SatdUtil.getExecutor;

public class SatdDetectTest {
    @Test
    public void testDetectFirstSATD() throws Exception {
        //First
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="bd2a0d30047aff33b8ce9533c5bc6c57f1d340dd";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(1, executor.resultsChild.size());
        Assert.assertEquals(0, executor.resultsParent.size());

        //1つ目
        SATD resultSATD1 = executor.resultsChild.get(0);
        Assert.assertEquals("no match","//TODO I just need to rewrite the existing SATD DB, so I don't have to create a new DB?", resultSATD1.content);
        Assert.assertTrue(resultSATD1.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD1.type);

    }

    @Test
    public void testDetectSecondCommitSATD() throws Exception{
        //Second
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="f4366773967d694d739b502fedd2055b17981947";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(0, executor.resultsParent.size());
        Assert.assertEquals(2, executor.resultsChild.size());

        //1つ目
        SATD resultSATD1 = executor.resultsChild.get(0);
        Assert.assertEquals("no match","//* TODO", resultSATD1.content);
        Assert.assertTrue(resultSATD1.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD1.type);

        //2つ目
        SATD resultSATD2 = executor.resultsChild.get(1);
        Assert.assertEquals("no match","* bug 1", resultSATD2.content);
        Assert.assertTrue(resultSATD2.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD2.type);

    }

    @Test
    public void testDetectThirdCommitSATD() throws Exception {
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="68e5ea621ac9b428e0e5c2ef2c579eee1cce957a";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(1, executor.resultsParent.size());
        Assert.assertEquals(0, executor.resultsChild.size());

        //1つ目
        SATD resultSATD1 = executor.resultsParent.get(0);
        Assert.assertEquals("no match","//init database //TODO I just need to rewrite the existing SATD DB, so I don't have to create a new DB?", resultSATD1.content);
        Assert.assertTrue(resultSATD1.isSATD());
        Assert.assertEquals(SATD.Type.DELETED, resultSATD1.type);

    }
    @Test
    public void testDetectForthCommitSATD() throws Exception {
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="37aba6c5b6b147ed43ad6b311c66792eac923567";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(1, executor.resultsParent.size());
        Assert.assertEquals(3, executor.resultsChild.size());

        //1つ目
        SATD resultSATD1 = executor.resultsParent.get(0);
        Assert.assertEquals("no match","* bug 1", resultSATD1.content);
        Assert.assertTrue(resultSATD1.isSATD());
        Assert.assertEquals(SATD.Type.DELETED, resultSATD1.type);

        //2つ目
        SATD resultSATD2 = executor.resultsChild.get(0);
        Assert.assertEquals("no match","//TODO new function add", resultSATD2.content);
        Assert.assertTrue(resultSATD2.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD2.type);

        //3つ目
        SATD resultSATD3 = executor.resultsChild.get(1);
        Assert.assertEquals("no match","* bug 10", resultSATD3.content);
        Assert.assertTrue(resultSATD3.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD3.type);

        //3つ目
        SATD resultSATD4 = executor.resultsChild.get(2);
        Assert.assertEquals("no match","//TODO change name", resultSATD4.content);
        Assert.assertTrue(resultSATD4.isSATD());
        Assert.assertEquals(SATD.Type.ADDED, resultSATD4.type);

    }
    @Test
    public void testDetectFiveCommitSATD() throws Exception {
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="3b5f69c6678c03b39826dfac22740c2073c57595";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(1, executor.resultsParent.size());
        Assert.assertEquals(1, executor.resultsChild.size());

        //1つ目
        SATD resultSATD1 = executor.resultsParent.get(0);
        Assert.assertEquals("no match","//TODO change name",resultSATD1.content);
        Assert.assertEquals(SATD.Type.DELETED, resultSATD1.type);

        //2つ目
        SATD resultSATD2 = executor.resultsChild.get(0);
        Assert.assertEquals("no match","* //TODO change name",resultSATD2.content);
        Assert.assertEquals(SATD.Type.ADDED, resultSATD2.type);

    }

    @Test
    public void testDetectSixCommitSATD() throws Exception {
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="7ded5e96a42f003e59db4fb5113cb0e6d41d1c8d";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(1, executor.resultsParent.size());
        Assert.assertEquals(2, executor.resultsChild.size());

        //1つ目
        SATD resultSATD1 = executor.resultsParent.get(0);
        Assert.assertEquals("no match","//TODO new function add",resultSATD1.content);
        Assert.assertEquals(SATD.Type.DELETED, resultSATD1.type);

        //2つ目
        SATD resultSATD2 = executor.resultsChild.get(0);
        Assert.assertEquals("no match","//TODO new function added",resultSATD2.content);
        Assert.assertEquals(SATD.Type.ADDED, resultSATD2.type);

        //3つ目
        SATD resultSATD3 = executor.resultsChild.get(0);
        Assert.assertEquals("no match","//TODO new function added",resultSATD3.content);
        Assert.assertEquals(SATD.Type.ADDED, resultSATD3.type);

    }

    @Test
    public void testDetectEightCommitSATD() throws Exception {
        String testUrl = "https://github.com/mmikuu/CalcTestSatd";
        String testCommitId ="e1816ae9ba9b8cfcb7e4104031660db8da807a51";

        DiffSATDDetector executor = getExecutor(testUrl, testCommitId);

        //全体
        Assert.assertEquals(0, executor.resultsParent.size());
        Assert.assertEquals(1, executor.resultsChild.size());

        //1つ目
        SATD resultSATD1 = executor.resultsChild.get(0);
        Assert.assertEquals("no match","//Fixme //TODO",resultSATD1.content);
        Assert.assertEquals(SATD.Type.ADDED, resultSATD1.type);


    }
}
