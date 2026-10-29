package net.zhangrui.monomod.lib.Queue;

public class QueueNode<T> {

    public T val;
    public QueueNode<T> prev = null;
    public QueueNode<T> next = null;

    public QueueNode(T val) {
        this.val = val;
    }

}
