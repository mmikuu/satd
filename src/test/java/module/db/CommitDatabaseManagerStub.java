package module.db;

import jp.naist.sdlab.miku.module.SATD;
import jp.naist.sdlab.miku.module.db.CommitDatabaseManager;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;

import java.sql.SQLException;

public class CommitDatabaseManagerStub extends CommitDatabaseManager {
    public static final String db = "test_db";

    public CommitDatabaseManagerStub() throws SQLException {
        super(db);
    }
    // TODO 　ここでcreateTableを呼び出す？どうやってcreateTableする？
    public void createTable() throws SQLException {
        super.creatTable(true);
        super.creatTable(false);
    }

    public void dataUpdate(SATD satd, Boolean isParent){
        super.dataUpdate(satd,isParent);
    }
}
