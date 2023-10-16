import jp.naist.sdlab.miku.module.ResultSummarizer;
import module.db.SATDDatabaseManagerStub;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Map;

public class ResultSummarizerTest {
    public static SATDDatabaseManagerStub satdDBManager;

    @BeforeClass
    public static void setUp() throws SQLException {
        satdDBManager = new SATDDatabaseManagerStub();

    }


    @Test
    public void testReplaceSATD() throws SQLException {
        ResultSummarizer summarizer = new ResultSummarizer(satdDBManager);
        summarizer.run();

        testFistCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testSecondCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testThirdCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testForthCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testFiveCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testSixCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testSevenCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testEightCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testNineCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testTenCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testElevenCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testTwelveCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testThirteenCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testFourteenCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testFifteenCommitCountSATD(summarizer.countAdd,summarizer.countDelete);
        testSixteenCommitCountSATD(summarizer.countAdd,summarizer.countDelete);

    }

    public void testFistCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "bd2a0d30047aff33b8ce9533c5bc6c57f1d340dd";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }

    public void testSecondCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "f4366773967d694d739b502fedd2055b17981947";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,2, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }

    public void testThirdCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "68e5ea621ac9b428e0e5c2ef2c579eee1cce957a";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualDelete);
        }
    }

    public void testForthCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "37aba6c5b6b147ed43ad6b311c66792eac923567";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,2, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }

    public void testFiveCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "3b5f69c6678c03b39826dfac22740c2073c57595";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualDelete);
        }
    }

    public void testSixCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "7ded5e96a42f003e59db4fb5113cb0e6d41d1c8d";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }

    public void testSevenCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "e1816ae9ba9b8cfcb7e4104031660db8da807a51";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }
    public void testEightCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "7ded5e96a42f003e59db4fb5113cb0e6d41d1c8d";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,3, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }
    public void testNineCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "66b0a65fe927500a408254df35c7a0249f437a63";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }
    public void testTenCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "5faf778d36cab73413c9e43ee842ee8a14870cda";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,3, actualDelete);
        }
    }

    public void testElevenCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "5fb1611f41cd9b992a053cfebc8e8951a6a9c59a";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,3, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }
    public void testTwelveCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "8f38d962900877658f1a847b6767f682442eb000";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }
    public void testThirteenCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "0435a3375a599d45428bbc6444fe0843ba111f2b";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualDelete);
        }
    }
    public void testFourteenCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "fb6961b05f2cfe7393b13b4d880139b9f81cbedd";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,0, actualDelete);
        }
    }

    public void testFifteenCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "2c652847fde25285c63f267735c9f2ebcdd093c3";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualDelete);
        }
    }
    public void testSixteenCommitCountSATD(Map<String, String> countAdd, Map<String, String> countDelete) throws SQLException {

        String testCommitId = "f0ecbe90b60d0b6712001b28ef08dac681002887";

        if(countAdd.get("test_"+testCommitId) != null){
            int actualAdd = Integer.parseInt(countAdd.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualAdd);
        }
        if(countDelete.get("test_"+testCommitId) != null){
            int actualDelete = Integer.parseInt(countDelete.get("test_"+testCommitId));
            Assert.assertEquals(testCommitId,1, actualDelete);
        }
    }

}
