package acme.store;

import java.sql.SQLException;

public class StoreException extends Exception {
    public StoreException(String e) {
        super(e);
    }

    public StoreException(String s, SQLException e) {
        super(s, e);
    }

    public StoreException(SQLException e) {
        super(e);
    }
}
