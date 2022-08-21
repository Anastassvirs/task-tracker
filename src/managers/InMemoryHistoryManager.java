package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private ListNode head;
    private ListNode tail;
    private int size;
    private HashMap<Integer, ListNode> numbersOfTasks;

    public InMemoryHistoryManager() {
        numbersOfTasks = new HashMap<>();
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public void add(Task task) {
        if (size >= 10) {
            remove(head.getItem().getId());
        }
        if (numbersOfTasks.containsKey(task.getId())) {
            remove(task.getId());
        }
        numbersOfTasks.put(task.getId(), linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(numbersOfTasks.get(id));
        numbersOfTasks.remove(id);
    }

    private ListNode linkLast(Task task) {
        final ListNode l = tail;
        final ListNode newNode = new ListNode(l, task, null);
        tail = newNode;
        if (l == null)
            head = newNode;
        else
            l.setNext(newNode);
        size++;
        return newNode;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> historyGet = new ArrayList<>();
        if (head != null) {
            historyGet.add(head.getItem());
            ListNode next = head.getNext();
            ListNode thisOne;
            while (next != null) {
                thisOne = next;
                next = thisOne.getNext();
                historyGet.add(thisOne.getItem());
            }
        }
        return historyGet;
    }

    private void removeNode(ListNode node) {
        final ListNode next = node.getNext();
        final ListNode prev = node.getPrev();

        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }

        node.setItem(null);
        size--;
    }

    @Override
    public String toString() {
        String numbers = "";
        for (Integer numb: numbersOfTasks.keySet()) {
            numbers += numb + ",";
        }
        if (numbers.length() >= 1) {
            return numbers.substring(0, numbers.length() - 1);
        } else {
            return "";
        }
    }
}
