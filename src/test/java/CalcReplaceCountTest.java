import jdk.jfr.internal.tool.Main;
import jp.naist.sdlab.miku.main.MainCountSATD;
import jp.naist.sdlab.miku.module.ReplaceCounter;
import jp.naist.sdlab.miku.module.commit.Replace;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;
import module.db.SATDDatabaseManagerStub;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class CalcReplaceCountTest {
    public SATDDatabaseManagerStub satdDBManager;

    public MainCountSATD mainCountSATD;
    public CalcReplaceCountTest() throws SQLException {
        satdDBManager = new SATDDatabaseManagerStub();
        mainCountSATD = new MainCountSATD();

    }

    @Test
    public void testReplaceSATD() throws SQLException {
        ResultSet rsH = satdDBManager.getHashDate();//similarityのデータを取得
        while(rsH.next()) {
            int id = rsH.getInt("id");
            boolean isReplace = mainCountSATD.checkReplace(rsH);//similarityをもとにreplaceか判定
            satdDBManager.addDate(id, isReplace);
        }

        Replace replace = satdDBManager.countAddSATD(true);//DBをもとに各TYPE(Add,Delete,Replace)をReplace Partごとに取得
        mainCountSATD.countSATD(replace,true);//取得したReplace partごとに分けた各TYPEを集計

        replace = satdDBManager.countAddSATD(false);//DBをもとに各TYPE(Add,Delete,Replace)をReplace Partごとに取得
        mainCountSATD.countSATD(replace,false);//取得したReplace partごとに分けた各TYPEを集計


        Map<String, String> countAdd = mainCountSATD.getCountAddDelete("Add");//Acountを取得
        Map<String, String> countDelete = mainCountSATD.getCountAddDelete("Del");//Dcountを取得

        mainCountSATD.countPrint(countAdd,countDelete);
    }

}
