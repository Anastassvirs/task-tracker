package managers;

import tasks.Task;

public class ListNode {
    private Task item;
    private ListNode next;
    private ListNode prev;

    ListNode(ListNode prev, Task element, ListNode next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }

    public Task getItem() {
        return item;
    }

    public void setItem(Task item) {
        this.item = item;
    }

    public ListNode getNext() {
        return next;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }

    public ListNode getPrev() {
        return prev;
    }

    public void setPrev(ListNode prev) {
        this.prev = prev;
    }
}
