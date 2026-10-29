package net.zhangrui.monomod.lib.Queue;

public class Test {
    public class Queue<T> {

        QueueNode<T> head = null;
        QueueNode<T> tail = null;

        public boolean isEmpty() {
            return head == null && tail == null;
        }

        public QueueNode<T> peek() {
            return head;
        }

        public void add(QueueNode<T> node) {

            if (isEmpty()) {
                head = node;
                tail = node;
                return;
            }



            tail.next = node;
            node.prev = tail;
            node.next = null;
            tail = node;

        }

        public QueueNode<T> remove() {
            if (isEmpty()) {
                return null;
            }

            QueueNode<T> previousHead = head;
            QueueNode<T> next = head.next;

            head.prev = null;
            head.next = null;
            head = next;

            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }

            return previousHead;
        }

        public void remove(QueueNode<T> node) {

            if (isEmpty()) {
                return;

            }

            QueueNode<T> next = node.next;
            QueueNode<T> prev = node.prev;

            if (head == tail && head == node) {
                head = tail = null;
                return;
            }


            if (next != null) {
                next.prev = prev;
            } else {
                // we were the tail
                tail = prev;
                tail.next = null;
            }

            if (prev != null) {
                prev.next = next;
            } else {
                // we were the head
                head = next;
                head.prev = null;
            }

        }

    }
}
