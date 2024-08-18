package manager;

import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Node(Task task, Node next, Node prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }

    Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(task, null, oldTail);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    private Node getNode(int id) {
        return nodeMap.get(id);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node element = head;
        while (element != null) {
            tasks.add(element.getTask());
            element = element.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            nodeMap.remove(node.getTask().getId());
            Node prev = node.getPrev();
            Node next = node.getNext();

            if (head == node) {
                head = node.getNext();
            }
            if (tail == node) {
                tail = node.getPrev();
            }
            if (prev != null) {
                prev.setNext(next);
            }
            if (next != null) {
                next.setPrev(prev);
            }
        }
    }
}
