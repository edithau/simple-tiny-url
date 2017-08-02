//package acme.store;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//import static org.junit.Assert.*;
//
//
//public class DBStoreTest {
//    @Mock ResultSet resultSet;
//    @Mock PreparedStatement pStmt;
//    @Mock Connection connection;
//    @InjectMocks private DBStore instance;
//
//    @Before
//    public void setup() throws Exception {
//        instance = DBStore.getInstance();
//        MockitoAnnotations.initMocks(this);
//
////        connection = Mockito.mock(Connection.class);
////        Mockito.when(connection.createStatement()).thenReturn(pStmt);
//    }
//
//    @Test
//    public void testGet() throws Exception {
////        resultSet = Mockito.mock(ResultSet.class);
//        Mockito.when(resultSet.next()).thenReturn(true);
//        Mockito.when(resultSet.getString("longURL")).thenReturn("www.google.com");
//
////        pStmt = Mockito.mock(PreparedStatement.class);
////        Mockito.when(pStmt.executeQuery()).thenReturn(resultSet);
//
//        String longURLFromDB = instance.get("123456");
//        assertEquals("www.google.com", longURLFromDB);
//
//    }
//
//}