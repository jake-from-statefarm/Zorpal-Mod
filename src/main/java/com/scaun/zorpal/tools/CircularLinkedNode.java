package com.scaun.zorpal.tools;

public class CircularLinkedNode<T> {

    private T data;
    private CircularLinkedNode<T> next;

    public CircularLinkedNode(T data) {
        this.data = data;
        this.next = this;
    }

    public T getData() {
        return data;
    }

    public void setData(T newData) {
        data = newData;
    }

    public CircularLinkedNode<T> getNext() {
        return next;
    }

    public void setNext (T newNext) {
        CircularLinkedNode<T> tmp = next;
        next = new CircularLinkedNode<T>(newNext);
        next.setNextSingle(tmp);
    }

    public void setNext(CircularLinkedNode<T> newNext) {
        CircularLinkedNode<T> tmp = next;
        next = newNext;
        next.setNextSingle(tmp);
    }

    public void setNextSingle(CircularLinkedNode<T> newNext) {
        next = newNext;
    }

    @Override
    public String toString() {
        return "CircularLinkedNode{" + data + "}";
    }
}
