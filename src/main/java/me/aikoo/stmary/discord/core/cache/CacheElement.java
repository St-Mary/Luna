package me.aikoo.stmary.discord.core.cache;

/**
 * The CacheElement class is a simple implementation of a cache element.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class CacheElement<K, V> {
  private K key;
  private V value;

  /**
   * Creates a new CacheElement instance with the specified key and value.
   *
   * @param key The key of the element.
   * @param value The value of the element.
   */
  public CacheElement(K key, V value) {
    this.value = value;
    this.key = key;
  }

  /**
   * Get the key of the element.
   *
   * @return The key of the element.
   */
  public K getKey() {
    return key;
  }

  /**
   * Set the key of the element.
   *
   * @param key The key of the element.
   */
  public void setKey(K key) {
    this.key = key;
  }

  /**
   * Get the value of the element.
   *
   * @return The value of the element.
   */
  public V getValue() {
    return value;
  }

  /**
   * Set the value of the element.
   *
   * @param value The value of the element.
   */
  public void setValue(V value) {
    this.value = value;
  }
}
