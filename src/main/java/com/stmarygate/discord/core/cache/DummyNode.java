package com.stmarygate.discord.core.cache;

/**
 * A dummy node implementation.
 *
 * @param <T> The type of the elements in the list.
 */
public class DummyNode<T> implements LinkedListNode<T> {
  private final DoublyLinkedList<T> list;

  /**
   * Creates a new DummyNode instance.
   *
   * @param list The list the node is in.
   */
  public DummyNode(DoublyLinkedList<T> list) {
    this.list = list;
  }

  /**
   * Check if the node has an element.
   *
   * @return Whether the node has an element.
   */
  @Override
  public boolean hasElement() {
    return false;
  }

  /**
   * Check if the node is empty.
   *
   * @return Whether the node is empty.
   */
  @Override
  public boolean isEmpty() {
    return true;
  }

  /**
   * Get the element of the node.
   *
   * @return The element of the node.
   * @throws NullPointerException If the node has no element.
   */
  @Override
  public T getElement() throws NullPointerException {
    throw new NullPointerException();
  }

  /** Detach the node from the list. */
  @Override
  public void detach() {
    // Unused method
  }

  /**
   * Get the list the node is in.
   *
   * @return The list the node is in.
   */
  @Override
  public DoublyLinkedList<T> getListReference() {
    return list;
  }

  /**
   * Get the previous node.
   *
   * @return The previous node.
   */
  @Override
  public LinkedListNode<T> getPrev() {
    return this;
  }

  /**
   * Set the previous node.
   *
   * @param next The previous node.
   */
  @Override
  public void setPrev(LinkedListNode<T> next) {}

  /**
   * Get the next node.
   *
   * @return The next node.
   */
  @Override
  public LinkedListNode<T> getNext() {
    return this;
  }

  /**
   * Set the next node.
   *
   * @param prev The next node.
   */
  @Override
  public void setNext(LinkedListNode<T> prev) {}

  /**
   * Search for a value in the list.
   *
   * @param value The value to search for.
   * @return The node containing the value.
   */
  @Override
  public LinkedListNode<T> search(T value) {
    return this;
  }
}
