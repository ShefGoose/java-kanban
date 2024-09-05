package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static FileBackedTaskManager fileBackedManager;
    private static File dir;


    @BeforeEach
    void BeforeEach() throws IOException {
        dir = File.createTempFile("dataTest", ".txt");
        fileBackedManager = new FileBackedTaskManager(dir);
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task("nameTask", "descriptionTask");
        int taskId = fileBackedManager.addNewTask(task);
        Epic epic = new Epic("nameEpic", "descriptionEpic");
        int epicId = fileBackedManager.addNewEpic(epic);
        Subtask subtask = new Subtask("nameSubtask", "descriptionSubtask", epicId);
        int subtaskId = fileBackedManager.addNewSubtask(subtask);
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(dir);
        assertEquals(fileBackedManager.getTasks(), loadManager.getTasks());
        assertEquals(fileBackedManager.getEpics(), loadManager.getEpics());
        assertEquals(fileBackedManager.getSubtasks(), loadManager.getSubtasks());
    }

    @Test
    public void shouldLoadEmptyFile() {
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(dir);
        assertTrue(loadManager.getTasks().isEmpty());
        assertTrue(loadManager.getEpics().isEmpty());
        assertTrue(loadManager.getSubtasks().isEmpty());
    }
}
