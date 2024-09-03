package manager;

import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager manager;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;
    private static int taskId;
    private static int epicId;
    private static int subtaskId;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        task = new Task("nameTask", "descriptionTask");
        taskId = manager.addNewTask(task);
        epic = new Epic("nameEpic", "descriptionEpic");
        epicId = manager.addNewEpic(epic);
        subtask = new Subtask("nameSubtask", "descriptionSubtask", epicId);
        subtaskId = manager.addNewSubtask(subtask);

    }

    @Test
    public void shouldAddNewTaskAndGetTasks() {
        assertNotNull(task, "Задача не найдена.");
        final ArrayList<Task> tasks = manager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldAddNewEpicAndGetEpics() {
        assertNotNull(epic, "Задача не найдена.");
        final ArrayList<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldAddNewSubtaskAndGetSubtasks() {
        assertNotNull(subtask, "Задача не найдена.");
        final ArrayList<Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldGetTask() {
        assertEquals(task, manager.getTask(taskId), "Задачи не совпадают.");
    }

    @Test
    void shouldGetEpic() {
        assertEquals(epic, manager.getEpic(epicId), "Задачи не совпадают.");
    }

    @Test
    void shouldGetSubtask() {
        assertEquals(subtask, manager.getSubtask(subtaskId), "Задачи не совпадают.");
    }

    @Test
    void mustEqualTasksWithSameId() {
        final Task taskUpdate = new Task("OtherName", Status.DONE, "OtherDescription", task.getId());
        final Epic epicUpdate = new Epic("OtherName", Status.NEW, "OtherDescription", epic.getId());
        final Subtask subtaskUpdate = new Subtask("OtherName", Status.NEW, "OtherDescription", subtask.getId(), epic.getId());
        assertEquals(task, taskUpdate, "Задачи не совпадают");
        assertEquals(epic, epicUpdate, "Задачи не совпадают");
        assertEquals(subtask, subtaskUpdate, "Задачи не совпадают");

    }

    @Test
    void shouldDeleteTaskById() {
        manager.deleteTask(taskId);
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void shouldDeleteEpicByIdAndHisSubtasks() {
        manager.deleteEpic(epicId);
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getEpicSubtasks(epic).isEmpty());
    }

    @Test
    void shouldDeleteSubtaskAndSubtaskIdFromEpic() {
        manager.deleteSubtask(subtaskId);
        assertTrue(manager.getSubtasks().isEmpty());
        assertTrue(manager.getEpic(epicId).getSubtaskIds().isEmpty());

    }

    @Test
    void shouldRemoveSubtaskInHistoryIfDeleteHisEpic() {
        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);
        manager.deleteEpic(epicId);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldDeleteAllTasks() {
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();
        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteSubtasksIdFromEpicIfDeleteSubtasks() {
        manager.deleteSubtasks();
        assertTrue(manager.getEpic(epicId).getSubtaskIds().isEmpty());
    }

    @Test
    void shouldUpdateTaskStatusToInProgress() {
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTask(taskId).getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldUpdateSubtaskStatusToInProgressAndUpdateHisEpicStatusToInProgress() {
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtask(subtaskId).getStatus(), "Статусы не совпадают.");
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldGetHistory() {
        manager.getTask(taskId);
        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);
        assertEquals(3, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task));
        assertTrue(manager.getHistory().contains(epic));
        assertTrue(manager.getHistory().contains(subtask));
    }
}