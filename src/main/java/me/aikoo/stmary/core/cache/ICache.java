package me.aikoo.stmary.core.cache;

import java.util.Optional;

/**
 * The ICache interface represents a cache.
 * @param <K> The key type.
 * @param <V> The value type.
 */
public interface ICache<K, V> {
    /**
     * Put a new item into the cache.
     * @param key The key of the item.
     * @param value The value of the item.
     * @return Whether the item was successfully put into the cache.
     */
    boolean put(K key, V value);

    /**
     * Get an item from the cache.
     * @param key The key of the item.
     * @return The item.
     */
    Optional<V> get(K key);

    /**
     * Delete an item from the cache.
     * @param key The key of the item.
     */
    void delete(K key);

    /**
     * Get the size of the cache.
     * @return The size of the cache.
     */
    int size();

    /**
     * Check if the cache is empty.
     * @return true if the cache is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Clear the cache.
     */
    void clear();
}
