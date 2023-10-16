import jp.naist.sdlab.miku.module.ResultSummarizer;
import module.db.SATDDatabaseManagerStub;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

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

        int actualAdd = Integer.parseInt(summarizer.countAdd.get("test"));
        Assert.assertEquals(18, actualAdd);
        int actualDelete = Integer.parseInt(summarizer.countDelete.get("test"));
        Assert.assertEquals(9, actualDelete);

    }

}
