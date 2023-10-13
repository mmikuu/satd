package module;

import jp.naist.sdlab.miku.module.SATDDatabaseManager;

import java.sql.SQLException;

public class SATDDatabaseManagerStub extends SATDDatabaseManager {
    public static final String db = "test_db";

    public SATDDatabaseManagerStub() throws SQLException {
        super();
    }
}
