//package acme.store;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//
//public class URLStoreTest {
//    String key, value;
//    URLStore store = new URLStore();
//
//    @Before
//    public void setup() {
//        key = "123456";
//        value = "www.google.com";
//
//    }
//
//
//    @Test
//    public void testGetWithKeyCached() throws Exception {
//        DBStore dbStore = mock(DBStore.class);
//        when(dbStore.get(key)).thenReturn("blah");
//
//        store.put(key, value);
//        String actualValue = store.get(key);
//        assertEquals(value, actualValue);
//        verify(dbStore, never()).get(key);
//    }
//
////    @Test
////    public void testGetWithoutKeyCached() {
////
////    }
//
//}