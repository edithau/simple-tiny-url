package acme.store;

import javax.sql.DataSource;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory;


public class DataSourceFactory {
    private static final Logger log = Logger.getLogger(DataSourceFactory.class);

    public static DataSource createDataSource(final Properties prop) throws Exception {
        return BasicDataSourceFactory.createDataSource(prop);
    }

    /**
     * <a href="https://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html#How_to_use">tomcat dbcp</a>
     *
     * TODO: move properties to a config file
     */
    public static Properties getProperties() {
        final Properties prop = new Properties();
        prop.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        prop.setProperty("url", "jdbc:mysql://localhost:3306/my_database");
        prop.setProperty("username", "my_user");
        prop.setProperty("password", "secret");

        prop.setProperty("maxActive", "6");
        prop.setProperty("maxIdle", "6");
        prop.setProperty("maxWait", "6");
        prop.setProperty("initialSize", "1");
        prop.setProperty("poolPreparedStatements", "true");
        prop.setProperty("maxOpenPreparedStatements", "10");
        prop.setProperty("testOnBorrow", "true");
        prop.setProperty("validationQuery", "SELECT 1 FROM DUAL");
        prop.setProperty("removeAbandoned", "true");
        prop.setProperty("removeAbandonedTimeout", "300");
        prop.setProperty("logAbandoned", "true");
        prop.setProperty("closeMethod", "close");
        return prop;
    }

    public static void closeDataSource(DataSource ds) {
        if (ds != null) {
            try {
                ((BasicDataSource) ds).close();
            } catch (Exception e) {
                log.error("Unable to close data source (" + ds + "): " + e.toString(), e);
            }
        }

    }

}
