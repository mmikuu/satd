package module.db;

import jp.naist.sdlab.miku.module.commit.Replace;
import jp.naist.sdlab.miku.module.db.SATDDatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SATDDatabaseManagerStub extends SATDDatabaseManager {
    public static final String db = "test_db";

    public SATDDatabaseManagerStub() throws SQLException {
        super(db);
    }

    public Replace countAddSATD(boolean isParent) throws SQLException {
        return super.countAddSatd(isParent);
    }
}
