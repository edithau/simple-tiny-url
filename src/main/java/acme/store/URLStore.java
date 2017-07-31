package acme.store;


public class URLStore {
    private InMemoryStore<String, String> inMemStore;


    public URLStore(int storeCapacity) {
        inMemStore = new InMemoryStore<String, String>(storeCapacity);
    }

    public String get(String key) {
        return inMemStore.get(key);
    }

    public String put(String key, String value) {
        return inMemStore.put(key, value);
    }

}
