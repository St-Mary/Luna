package me.aikoo.StMary.core.cache;

public interface LinkedListNode<V> {
    boolean hasElement();

    boolean isEmpty();

    V getElement() throws NullPointerException;

    void detach();

    DoublyLinkedList<V> getListReference();

    LinkedListNode<V> getPrev();

    LinkedListNode<V> setPrev(LinkedListNode<V> prev);

    LinkedListNode<V> getNext();

    LinkedListNode<V> setNext(LinkedListNode<V> next);

    LinkedListNode<V> search(V value);
}
