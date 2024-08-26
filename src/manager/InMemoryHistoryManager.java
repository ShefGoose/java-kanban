package manager;

import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Node(Task task, Node next, Node prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(nodeMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task, null, tail);

        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node element = head;
        while (element != null) {
            tasks.add(element.task);
            element = element.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            nodeMap.remove(node.task.getId());
            Node prev = node.prev;
            Node next = node.next;

            if (head == node) {
                head = node.next;
            }
            if (tail == node) {
                tail = node.prev;
            }
            if (prev != null) {
                prev.next = next;
            }
            if (next != null) {
                next.prev = prev;
            }
        }
    }
}
