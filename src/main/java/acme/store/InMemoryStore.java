package acme.store;

import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryStore<K, V> extends LinkedHashMap<K, V> {
    private int cache_capacity;
    private static final Logger log = Logger.getLogger(InMemoryStore.class);

    public InMemoryStore(int capacity){
        super(16, 0.75f, true);
        this.cache_capacity = capacity + 1;

    }


    //TODO: not sure why super.put(key, value) returns null.
    @Override
    public V put(K key, V value) {
        log.debug("before super key=" + key + " value=" + value);
        V shouldNotBeNull = super.put(key, value);
        log.debug("before super key=" + key + " value=" + shouldNotBeNull);
        log.debug("Contents of LinkedHashMap : " + this);

        return super.get(key);
    }


    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > cache_capacity;
    }
}