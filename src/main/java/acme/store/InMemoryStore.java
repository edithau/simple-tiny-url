package acme.store;

import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

class InMemoryStore<K, V> extends LinkedHashMap<K, V> {
    private int cache_capacity;
    private static final Logger log = Logger.getLogger(InMemoryStore.class);

    InMemoryStore(int capacity){
        super(16, 0.75f, true);
        this.cache_capacity = capacity + 1;

    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > cache_capacity;
    }
}