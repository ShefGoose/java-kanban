package http;

import com.google.gson.Gson;
import entity.Epic;
import entity.Subtask;
import entity.Task;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    TaskManager manager = Managers.getDefault();
    Gson gson = Managers.getGson();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 1", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 1);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask 1", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Testing task 2", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 15, 0));
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String taskListJson = gson.toJson(manager.getTasks());
        assertEquals(taskListJson, response.body());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        int idTask = manager.getTasks().getFirst().getId();
        URI url = URI.create("http://localhost:8080/tasks/" + idTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String verifyTask = gson.toJson(manager.getTask(idTask));
        assertEquals(verifyTask, response.body());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1");
        Epic epic2 = new Epic("Epic 2", "Testing epic 2");
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String epicListJson = gson.toJson(manager.getEpics());
        assertEquals(epicListJson, response.body());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1");
        manager.addNewEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        int idEpic = manager.getEpics().getFirst().getId();
        URI url = URI.create("http://localhost:8080/epics/" + idEpic);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String verifyEpic = gson.toJson(manager.getEpic(idEpic));
        assertEquals(verifyEpic, response.body());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", 1);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String subtaskListJson = gson.toJson(manager.getSubtasks());
        assertEquals(subtaskListJson, response.body());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1");
        manager.addNewEpic(epic1);
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 1);
        manager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        int idSubtask = manager.getSubtasks().getFirst().getId();
        URI url = URI.create("http://localhost:8080/subtasks/" + idSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String verifySubtask = gson.toJson(manager.getSubtask(idSubtask));
        assertEquals(verifySubtask, response.body());
    }

    @Test
    public void testGetEpicsSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1");
        manager.addNewEpic(epic1);
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 1);
        manager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        int idEpic = manager.getEpics().getFirst().getId();
        URI url = URI.create("http://localhost:8080/epics/" + idEpic + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        ArrayList<Subtask> verifySubtaskList = new ArrayList<>();
        verifySubtaskList.add(subtask);
        String verifySubtask = gson.toJson(verifySubtaskList);
        assertEquals(verifySubtask, response.body());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task1);
        Epic epic1 = new Epic("Epic 1", "Testing epic 1");
        manager.addNewEpic(epic1);
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 2);
        manager.addNewSubtask(subtask);
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String verifyHistoryList = gson.toJson(manager.getHistory());
        assertEquals(verifyHistoryList, response.body());
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 15, 0));
        manager.addNewTask(task);
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1",
                Duration.ofMinutes(20),
                LocalDateTime.of(2025, 1, 27, 16, 0), 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2",
                Duration.ofMinutes(15),
                LocalDateTime.of(2025, 1, 27, 18, 0), 2);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String verifyPrioritizedListJson = gson.toJson(manager.getPrioritizedTasks());
        assertEquals(verifyPrioritizedListJson, response.body());
    }

    @Test
    public void testHasCrossingTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 15, 0));
        manager.addNewTask(task1);

        Task task2 = new Task("Task 2", "Testing task 2", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 14, 57));
        String task2Json = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }
}
