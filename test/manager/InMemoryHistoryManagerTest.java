package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static HistoryManager historyManager;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("nameTask", Status.valueOf("NEW"), "descriptionTask", 1,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 9, 23, 14, 0));
        epic = new Epic("nameEpic", Status.valueOf("NEW"), "descriptionEpic", 2, null,
                null);
        subtask = new Subtask("nameSubtask", Status.valueOf("NEW"), "descriptionSubtask",
                3, Duration.ofMinutes(20), LocalDateTime.of(2024, 9, 23, 15, 0),
                epic.getId());
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertNotNull(historyManager.getHistory());
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskIfItIsAlreadyInTheList() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(task);
        assertEquals(List.of(epic, subtask, task), historyManager.getHistory());
    }

    @Test
    void shouldRemoveTaskFromBeginningHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(task.getId());
        assertEquals(List.of(epic, subtask), historyManager.getHistory());
    }

    @Test
    void shouldRemoveTaskFromMiddleHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(epic.getId());
        assertEquals(List.of(task, subtask), historyManager.getHistory());
    }

    @Test
    void shouldRemoveTaskFromEndHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(subtask.getId());
        assertEquals(List.of(task, epic), historyManager.getHistory());
    }

    @Test
    void shouldRemoveSingleTaskFromHistory() {
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void removeFromEmptyHistory() {
        historyManager.remove(task.getId());
        historyManager.remove(epic.getId());
        historyManager.remove(subtask.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldSavePreviousVersionTaskInHistory() {
        TaskManager manager = Managers.getDefault();
        Task OtherTask = new Task("OtherName", "OtherDescription", Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 14, 0));
        final int taskId = manager.addNewTask(task);
        historyManager.add(OtherTask);
        Task taskUpdate = new Task("UpdateName", Status.valueOf("DONE"), "UpdateDescription",
                taskId, Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 14, 0));
        manager.updateTask(taskUpdate);
        assertEquals(historyManager.getHistory().getFirst().getStatus(), Status.NEW);
    }
}