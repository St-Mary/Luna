package me.aikoo.StMary.core.cache;

import java.util.Optional;

public interface ICache<K, V> {
    boolean put(K key, V value);

    Optional<V> get(K key);

    void delete(K key);

    int size();

    boolean isEmpty();

    void clear();
}
