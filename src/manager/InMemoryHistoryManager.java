package manager;

import entity.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();
    private final static int MAX_SIZE_HISTORY = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() == MAX_SIZE_HISTORY) {
            history.removeFirst();
        }
        history.add(task);

    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

}
