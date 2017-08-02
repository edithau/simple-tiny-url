package acme.store;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBStore {
    private static final String table = "urlmap";
    private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + table + " (" + //
            "shortURL VARCHAR(22) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," + //
            "longURL VARCHAR(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," + //
            "timestamp INT(11) unsigned NOT NULL," + //
            "PRIMARY KEY (shortURL)" + //
            ") ENGINE=InnoDB;";
    private DataSource dataSource = null;
    private static final Logger log = Logger.getLogger(DBStore.class);
    private Properties config = null;

    public DBStore() throws IOException {
        try {
            init();
        } catch (IOException e) {
            throw new IOException("Cannot initiate a DBStore instance.  ", e);
        }
    }

    public String get(String key) throws IOException {
        Connection conn = null;
        PreparedStatement pStmtGet = null;
        ResultSet rSet = null;
        try {
            conn = dataSource.getConnection();
            pStmtGet = conn.prepareStatement("SELECT longURL FROM " + table + " WHERE shortURL = ?");
            pStmtGet.setString(1, key);
            rSet = pStmtGet.executeQuery();
            return rSet.getString("longURL");
        } catch (SQLException e) {
            throw new IOException("Cannot connect or query the database store", e);
        } finally {
            closeSilent(rSet, pStmtGet, conn);
        }
    }


    public String put(String key, String value) throws IOException {
        Connection conn = null;
        PreparedStatement pStmtPut = null;
        try {
            conn = dataSource.getConnection();
            pStmtPut = conn.prepareStatement("INSERT INTO " + table
                    + " (shortURL, longURL, timestamp) VALUES(?, ?, ?)");
            pStmtPut.setString(1, key);
            pStmtPut.setString(2, value);
            pStmtPut.setInt(3, (int) (System.currentTimeMillis() / 1000));
            try {
                pStmtPut.executeUpdate();
            } catch (SQLException e) {
                // already inserted?
                return get(key);
            }
            return value;
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            closeSilent(null, pStmtPut, conn);
        }
    }

    private void closeSilent(ResultSet rSet, PreparedStatement pStmt, Connection conn) {
        try {
            if (rSet != null)
                rSet.close();
            if (pStmt != null)
                pStmt.close();
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            log.warn("DBStore cannot close closeable. ", e);
        }
    }

    private void init() throws IOException {
        config = DataSourceFactory.getProperties();
        try {
            dataSource = DataSourceFactory.createDataSource(config);
        } catch (Exception e) {
            DataSourceFactory.closeDataSource(dataSource);
            throw new IOException(e);
        }
        Connection conn = null;
        Statement stmt;
        PreparedStatement pStmtCreate = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + table + "'");
            if (!rs.next()) {
                // table does not exist so let's create one
                pStmtCreate = conn.prepareStatement(TABLE_CREATE);
                pStmtCreate.executeUpdate();
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            closeSilent(null, pStmtCreate, conn);
        }
    }


}
