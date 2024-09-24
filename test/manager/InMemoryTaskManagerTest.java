package manager;

import entity.*;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        task = new Task("nameTask", "descriptionTask", Duration.ofMinutes(10),
                LocalDateTime.of(2024, 9, 23, 12,0));
        taskId = manager.addNewTask(task);
        epic = new Epic("nameEpic", "descriptionEpic");
        epicId = manager.addNewEpic(epic);
        subtask = new Subtask("nameSubtask", "descriptionSubtask",Duration.ofMinutes(20),
                LocalDateTime.of(2024, 9, 23, 13,0), epicId);
        subtaskId = manager.addNewSubtask(subtask);
    }
}