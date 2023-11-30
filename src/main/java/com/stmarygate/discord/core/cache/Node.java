package com.stmarygate.discord.core.cache;

/**
 * A node implementation.
 *
 * @param <T> The type of the elements in the list.
 */
public class Node<T> implements LinkedListNode<T> {
  private final T value;
  private final DoublyLinkedList<T> list;
  private LinkedListNode next;
  private LinkedListNode prev;

  /**
   * Creates a new Node instance.
   *
   * @param value The value of the node.
   * @param next The next node.
   * @param list The list the node is in.
   */
  public Node(T value, LinkedListNode<T> next, DoublyLinkedList<T> list) {
    this.value = value;
    this.next = next;
    this.setPrev(next.getPrev());
    this.prev.setNext(this);
    this.next.setPrev(this);
    this.list = list;
  }

  /**
   * Check if the node has an element.
   *
   * @return Whether the node has an element.
   */
  @Override
  public boolean hasElement() {
    return true;
  }

  /**
   * Check if the node is empty.
   *
   * @return Whether the node is empty.
   */
  @Override
  public boolean isEmpty() {
    return false;
  }

  /**
   * Get the element of the node.
   *
   * @return The element of the node.
   */
  public T getElement() {
    return value;
  }

  /** Detach the node from the list. */
  public void detach() {
    this.prev.setNext(this.getNext());
    this.next.setPrev(this.getPrev());
  }

  /**
   * Get the list the node is in.
   *
   * @return The list the node is in.
   */
  @Override
  public DoublyLinkedList<T> getListReference() {
    return this.list;
  }

  /**
   * Get the previous node.
   *
   * @return The previous node.
   */
  @Override
  public LinkedListNode<T> getPrev() {
    return this.prev;
  }

  /**
   * Set the previous node.
   *
   * @param prev The previous node.
   */
  @Override
  public void setPrev(LinkedListNode<T> prev) {
    this.prev = prev;
  }

  /**
   * Get the next node.
   *
   * @return The next node.
   */
  @Override
  public LinkedListNode<T> getNext() {
    return this.next;
  }

  /**
   * Set the next node.
   *
   * @param next The next node.
   */
  @Override
  public void setNext(LinkedListNode<T> next) {
    this.next = next;
  }

  /**
   * Search for a value in the list.
   *
   * @param value The value to search for.
   * @return The node containing the value.
   */
  @Override
  public LinkedListNode<T> search(T value) {
    return this.getElement() == value ? this : this.getNext().search(value);
  }
}
