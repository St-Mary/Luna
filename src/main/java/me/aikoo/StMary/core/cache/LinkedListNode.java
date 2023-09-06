package me.aikoo.StMary.core.cache;

/**
 * The LinkedListNode interface represents a node in a linked list.
 * @param <V> The type of the elements in the list.
 */
public interface LinkedListNode<V> {
    /**
     * Check if the node has an element.
     * @return Whether the node has an element.
     */
    boolean hasElement();

    /**
     * Check if the node is empty.
     * @return Whether the node is empty.
     */
    boolean isEmpty();

    /**
     * Get the element of the node.
     * @return The element of the node.
     * @throws NullPointerException If the node has no element.
     */
    V getElement() throws NullPointerException;

    /**
     * Detach the node from the list.
     */
    void detach();

    /**
     * Get the list the node is in.
     * @return The list the node is in.
     */
    DoublyLinkedList<V> getListReference();

    /**
     * Get the previous node.
     * @return The previous node.
     */
    LinkedListNode<V> getPrev();

    /**
     * Set the previous node.
     *
     * @param prev The previous node.
     */
    void setPrev(LinkedListNode<V> prev);

    /**
     * Set the next node.
     * @return The next node.
     */
    LinkedListNode<V> getNext();

    /**
     * Set the next node.
     *
     * @param next The next node.
     */
    void setNext(LinkedListNode<V> next);

    /**
     * Search for a value in the list.
     * @param value The value to search for.
     * @return The node containing the value, or null if not found.
     */
    LinkedListNode<V> search(V value);
}
