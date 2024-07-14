package manager;

import entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager = Managers.getDefault();

    @Test
    void addNewTask() {
        Task task = new Task("name1", "description1");
        final int taskId = manager.addNewTask(task);
        final Task taskUpdate = manager.getTask(taskId);

        assertNotNull(taskUpdate, "Задача не найдена.");
        assertEquals(task, taskUpdate, "Задачи не совпадают.");

        final ArrayList<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        final Epic epicUpdate = manager.getEpic(epicId);

        assertNotNull(epicUpdate, "Задача не найдена.");
        assertEquals(epic, epicUpdate, "Задачи не совпадают.");

        final ArrayList<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("name1", "description1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);
        final Subtask subtaskUpdate = manager.getSubtask(subtaskId);

        assertNotNull(subtaskUpdate, "Задача не найдена.");
        assertEquals(subtask, subtaskUpdate, "Задачи не совпадают.");

        final ArrayList<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task("name1", "description1");
        final int taskId = manager.addNewTask(task);
        manager.deleteTask(taskId);
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void shouldDeleteEpicByIdAndHisSubtasks() {
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("name1", "description1", epicId);
        final int subtaskId1 = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("name1", "description1", epicId);
        final int subtaskId2 = manager.addNewSubtask(subtask2);
        manager.deleteEpic(epicId);
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getEpicSubtasks(epic).isEmpty());
    }

    @Test
    void shouldDeleteSubtaskAndSubtaskIdFromEpic() {
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("name1", "description1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);
        manager.deleteSubtask(subtaskId);
        assertTrue(manager.getSubtasks().isEmpty());
        assertTrue(manager.getEpic(epicId).getSubtaskIds().isEmpty());

    }

    @Test
    void shouldDeleteAllTasks() {
        Task task = new Task("name1", "description1");
        final int taskId = manager.addNewTask(task);
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("name1", "description1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();
        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteSubtasksIdFromEpicIfDeleteSubtasks() {
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("name1", "description1", epicId);
        final int subtaskId1 = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("name1", "description1", epicId);
        final int subtaskId2 = manager.addNewSubtask(subtask2);
        manager.deleteSubtasks();
        assertTrue(manager.getEpic(epicId).getSubtaskIds().isEmpty());
    }

    @Test
    void shouldUpdateTaskStatusToInProgress() {
        Task task = new Task("name1", "description1");
        final int taskId = manager.addNewTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTask(taskId).getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("name1", "description1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtask(subtaskId).getStatus(), "Статусы не совпадают.");
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldUpdateEpicStatusToInProgressIfSubtaskStatusInProgress() {
        Epic epic = new Epic("name1", "description1");
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("name1", "description1", epicId);
        final int subtaskId1 = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("name1", "description1", epicId);
        final int subtaskId2 = manager.addNewSubtask(subtask2);
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, manager.getSubtask(subtaskId2).getStatus(), "Статусы не совпадают.");
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus(), "Статусы не совпадают.");
    }

}