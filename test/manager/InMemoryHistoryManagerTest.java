package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager manager = Managers.getDefault();

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("name1", "description1");
        final int taskId1 = manager.addNewTask(task);
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("name1", "description1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);
        manager.getHistoryManager().add(task);
        manager.getHistoryManager().add(epic);
        manager.getHistoryManager().add(subtask);
        assertNotNull(manager.getHistoryManager().getHistory());
        assertEquals(4, manager.getHistoryManager().getHistory().size());
    }

    @Test
    void shouldRemoveFirstTaskIfSizeHistoryOverMaxSize() {
        Task task1 = new Task("name1", "description1");
        final int taskId1 = manager.addNewTask(task1);
        Task task2 = new Task("name2", "description2");
        final int taskId2 = manager.addNewTask(task2);
        manager.getHistoryManager().add(task2);
        for (int i = 0; i < 10; i++) {
            manager.getHistoryManager().add(task1);
        }
        assertEquals(manager.getHistoryManager().getHistory().getFirst(), task1);
    }

    @Test
    void shouldSavePreviousVersionTaskInHistory() {
        Task task1 = new Task("name1", "description1");
        final int taskId1 = manager.addNewTask(task1);
        manager.getHistoryManager().add(task1);
        Task task1Update = new Task("name1", Status.valueOf("DONE"), "description1", taskId1);
        manager.updateTask(task1Update);
        assertEquals(manager.getHistoryManager().getHistory().getFirst().getStatus(), Status.NEW);
    }
}