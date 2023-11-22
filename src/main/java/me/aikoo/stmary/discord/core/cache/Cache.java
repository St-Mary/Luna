package me.aikoo.stmary.discord.core.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The Cache class is a simple implementation of a cache. This cache is made by Baeldung.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class Cache<K, V> implements ICache<K, V> {
  private final int size;
  private final Map<K, LinkedListNode<CacheElement<K, V>>> linkedListNodeMap;
  private final DoublyLinkedList<CacheElement<K, V>> doublyLinkedList;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  /**
   * Creates a new Cache instance with the specified size.
   *
   * @param size The size of the cache.
   */
  public Cache(int size) {
    this.size = size;
    this.linkedListNodeMap = new ConcurrentHashMap<>(size);
    this.doublyLinkedList = new DoublyLinkedList<>();
  }

  /**
   * Put a new item into the cache.
   *
   * @param key The key of the item.
   * @param value The value of the item.
   * @return Whether the item was successfully put into the cache.
   */
  @Override
  public boolean put(K key, V value) {
    this.lock.writeLock().lock();
    try {
      CacheElement<K, V> item = new CacheElement<K, V>(key, value);
      LinkedListNode<CacheElement<K, V>> newNode;
      if (this.linkedListNodeMap.containsKey(key)) {
        LinkedListNode<CacheElement<K, V>> node = this.linkedListNodeMap.get(key);
        newNode = doublyLinkedList.updateAndMoveToFront(node, item);
      } else {
        if (this.size() >= this.size) {
          this.evictElement();
        }
        newNode = this.doublyLinkedList.add(item);
      }
      if (newNode.isEmpty()) {
        return false;
      }
      this.linkedListNodeMap.put(key, newNode);
      return true;
    } finally {
      this.lock.writeLock().unlock();
    }
  }

  /**
   * Get an item from the cache.
   *
   * @param key The key of the item.
   * @return The value of the item.
   */
  @Override
  public Optional<V> get(K key) {
    this.lock.readLock().lock();
    try {
      LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);
      if (linkedListNode != null && !linkedListNode.isEmpty()) {
        linkedListNodeMap.put(key, this.doublyLinkedList.moveToFront(linkedListNode));
        return Optional.of(linkedListNode.getElement().getValue());
      }
      return Optional.empty();
    } finally {
      this.lock.readLock().unlock();
    }
  }

  /**
   * Delete an item from the cache.
   *
   * @param key The key of the item.
   */
  @Override
  public void delete(K key) {
    this.lock.writeLock().lock();
    try {
      LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);
      if (linkedListNode != null && !linkedListNode.isEmpty()) {
        this.doublyLinkedList.remove(linkedListNode.getElement());
        this.linkedListNodeMap.remove(key);
      }
    } finally {
      this.lock.writeLock().unlock();
    }
  }

  /**
   * Get the size of the cache.
   *
   * @return The size of the cache.
   */
  @Override
  public int size() {
    this.lock.readLock().lock();
    try {
      return doublyLinkedList.size();
    } finally {
      this.lock.readLock().unlock();
    }
  }

  /**
   * Check if the cache is empty.
   *
   * @return true if the cache is empty, false otherwise.
   */
  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  /** Clear the cache. This method is not thread-safe. */
  @Override
  public void clear() {
    this.lock.writeLock().lock();
    try {
      linkedListNodeMap.clear();
      doublyLinkedList.clear();
    } finally {
      this.lock.writeLock().unlock();
    }
  }

  /** Evict an element from the cache. This method is not thread-safe. */
  private void evictElement() {
    this.lock.writeLock().lock();
    try {
      LinkedListNode<CacheElement<K, V>> linkedListNode = doublyLinkedList.removeTail();
      if (linkedListNode.isEmpty()) {
        return;
      }
      linkedListNodeMap.remove(linkedListNode.getElement().getKey());
    } finally {
      this.lock.writeLock().unlock();
    }
  }
}
