package me.aikoo.StMary.core.cache;

public class DummyNode<T> implements LinkedListNode<T> {
    private final DoublyLinkedList<T> list;

    public DummyNode(DoublyLinkedList<T> list) {
        this.list = list;
    }

    @Override
    public boolean hasElement() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public T getElement() throws NullPointerException {
        throw new NullPointerException();
    }

    @Override
    public void detach() {
    }

    @Override
    public DoublyLinkedList<T> getListReference() {
        return list;
    }

    @Override
    public LinkedListNode<T> setPrev(LinkedListNode<T> next) {
        return next;
    }

    @Override
    public LinkedListNode<T> setNext(LinkedListNode<T> prev) {
        return prev;
    }

    @Override
    public LinkedListNode<T> getPrev() {
        return this;
    }

    @Override
    public LinkedListNode<T> getNext() {
        return this;
    }

    @Override
    public LinkedListNode<T> search(T value) {
        return this;
    }
}
