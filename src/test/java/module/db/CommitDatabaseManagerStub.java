package module.db;

import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;

import java.sql.SQLException;

public class CommitDatabaseManagerStub extends SATDDatabaseManager {
    public static final String db = "test_db";

    public CommitDatabaseManagerStub() throws SQLException {
        super(this.db);
    }
}
