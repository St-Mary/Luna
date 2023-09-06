package me.aikoo.stmary.core.cache;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A doubly linked list implementation.
 * @param <T> The type of the elements in the list.
 */
public class DoublyLinkedList<T> {

    private final DummyNode<T> dummyNode;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private LinkedListNode<T> head;
    private LinkedListNode<T> tail;
    private AtomicInteger size;

    /**
     * Creates a new DoublyLinkedList instance.
     */
    public DoublyLinkedList() {
        this.dummyNode = new DummyNode<T>(this);
        clear();
    }

    /**
     * Clears the list.
     */
    public void clear() {
        this.lock.writeLock().lock();
        try {
            head = dummyNode;
            tail = dummyNode;
            size = new AtomicInteger(0);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Get the size of the list.
     * @return The size of the list.
     */
    public int size() {
        this.lock.readLock().lock();
        try {
            return size.get();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Check if the list is empty.
     * @return Whether the list is empty.
     */
    public boolean isEmpty() {
        this.lock.readLock().lock();
        try {
            return head.isEmpty();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Check if the list contains the specified value.
     * @param value The value to check for.
     * @return Whether the list contains the specified value.
     */
    public boolean contains(T value) {
        this.lock.readLock().lock();
        try {
            return search(value).hasElement();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Search for the specified value in the list.
     * @param value The value to search for.
     * @return The node containing the specified value, or an empty node if not found.
     */
    public LinkedListNode<T> search(T value) {
        this.lock.readLock().lock();
        try {
            return head.search(value);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Add a value to the list.
     * @param value The value to add.
     * @return The node containing the value.
     */
    public LinkedListNode<T> add(T value) {
        this.lock.writeLock().lock();
        try {
            head = new Node<T>(value, head, this);
            if (tail.isEmpty()) {
                tail = head;
            }
            size.incrementAndGet();
            return head;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Add a collection of values to the list.
     * @param values The values to add.
     * @return Whether all the values were successfully added.
     */
    public boolean addAll(Collection<T> values) {
        this.lock.writeLock().lock();
        try {
            for (T value : values) {
                if (add(value).isEmpty()) {
                    return false;
                }
            }
            return true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Remove a value from the list.
     *
     * @param value The value to remove.
     */
    public void remove(T value) {
        this.lock.writeLock().lock();
        try {
            LinkedListNode<T> linkedListNode = head.search(value);
            if (!linkedListNode.isEmpty()) {
                if (linkedListNode == tail) {
                    tail = tail.getPrev();
                }
                if (linkedListNode == head) {
                    head = head.getNext();
                }
                linkedListNode.detach();
                size.decrementAndGet();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Remove the head of the list.
     * @return The node that was removed.
     */
    public LinkedListNode<T> removeTail() {
        this.lock.writeLock().lock();
        try {
            LinkedListNode<T> oldTail = tail;
            if (oldTail == head) {
                tail = head = dummyNode;
            } else {
                tail = tail.getPrev();
                oldTail.detach();
            }
            if (!oldTail.isEmpty()) {
                size.decrementAndGet();
            }
            return oldTail;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Move a node to the front of the list.
     * @param node The node to move.
     * @return The node that was moved.
     */
    public LinkedListNode<T> moveToFront(LinkedListNode<T> node) {
        return node.isEmpty() ? dummyNode : updateAndMoveToFront(node, node.getElement());
    }

    /**
     * Update a node and move it to the front of the list.
     * @param node The node to update.
     * @param newValue The new value of the node.
     * @return The node that was updated.
     */
    public LinkedListNode<T> updateAndMoveToFront(LinkedListNode<T> node, T newValue) {
        this.lock.writeLock().lock();
        try {
            if (node.isEmpty() || (this != (node.getListReference()))) {
                return dummyNode;
            }
            detach(node);
            add(newValue);
            return head;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Detach a node from the list.
     * @param node The node to detach.
     */
    private void detach(LinkedListNode<T> node) {
        if (node != tail) {
            node.detach();
            if (node == head) {
                head = head.getNext();
            }
            size.decrementAndGet();
        } else {
            removeTail();
        }
    }
}
