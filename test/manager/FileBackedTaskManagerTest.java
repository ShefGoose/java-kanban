package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static File dir;
    private static File otherDir;

    @BeforeEach
    void BeforeEach() throws IOException {
        dir = File.createTempFile("dataTest", ".txt");
        otherDir = File.createTempFile("emptyFile", ".txt");
        manager = new FileBackedTaskManager(dir);
        task = new Task("nameTask", "descriptionTask", Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 12,0));
        taskId = manager.addNewTask(task);
        epic = new Epic("nameEpic", "descriptionEpic");
        epicId = manager.addNewEpic(epic);
        subtask = new Subtask("nameSubtask", "descriptionSubtask",Duration.ofMinutes(20),
                LocalDateTime.of(2024, 9, 23, 13,0), epicId);
        subtaskId = manager.addNewSubtask(subtask);
    }

    @Test
    void shouldSaveAndLoadTasks() {
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(dir);
        assertEquals(manager.getTasks(), loadManager.getTasks());
        assertEquals(manager.getEpics(), loadManager.getEpics());
        assertEquals(manager.getSubtasks(), loadManager.getSubtasks());
    }

    @Test
    void shouldLoadEmptyFile() {
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(otherDir);
        assertTrue(loadManager.getTasks().isEmpty());
        assertTrue(loadManager.getEpics().isEmpty());
        assertTrue(loadManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldLoadPrioritizedTaskList() {
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(dir);
        assertEquals(List.of(task, subtask), loadManager.getPrioritizedTasks());
    }
}
