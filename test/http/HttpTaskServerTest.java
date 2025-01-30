package http;

import com.google.gson.Gson;
import entity.Epic;
import entity.Subtask;
import entity.Task;
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
    HttpTaskServer taskServer = new HttpTaskServer();

    public HttpTaskServerTest() throws IOException {
    }

    TaskManager manager = taskServer.getTaskManager();
    Gson gson = taskServer.getGson();

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();git
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
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 1);
        String subtaskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask 1", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        int idTask = manager.getTasks().getFirst().getId();
        String verifyTask = "[" + gson.toJson(manager.getTask(idTask)) + "]";
        String xui = gson.toJson(manager.getTask(idTask));
        assertEquals(verifyTask, response.body());
        url = URI.create("http://localhost:8080/tasks/" + idTask);
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        int idEpic = manager.getEpics().getFirst().getId();
        String verifyEpic = "[" + gson.toJson(manager.getEpic(idEpic)) + "]";
        assertEquals(verifyEpic, response.body());
        url = URI.create("http://localhost:8080/epics/" + idEpic);
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 1);
        String subtaskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        int idSubtask = manager.getSubtasks().getFirst().getId();
        String verifySubtask = "[" + gson.toJson(manager.getSubtask(idSubtask)) + "]";
        assertEquals(verifySubtask, response.body());
        url = URI.create("http://localhost:8080/subtasks/" + idSubtask);
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicsSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 1);
        String subtaskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        int idEpic = manager.getEpics().getFirst().getId();
        int idSubtask = manager.getSubtasks().getFirst().getId();
        String verifySubtask = "[" + gson.toJson(manager.getSubtask(idSubtask)) + "]";
        url = URI.create("http://localhost:8080/epics/" + idEpic + "/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(verifySubtask, response.body());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 15, 0));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 2);
        String subtaskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        int idTask = manager.getTasks().getFirst().getId();
        int idEpic = manager.getEpics().getFirst().getId();
        int idSubtask = manager.getSubtasks().getFirst().getId();
        url = URI.create("http://localhost:8080/tasks/" + idTask);
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        url = URI.create("http://localhost:8080/epics/" + idEpic);
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        url = URI.create("http://localhost:8080/subtasks/" + idSubtask);
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskVerify = new Task("Task 1", "Testing task 1", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2025, 1, 27, 15, 0));
        Epic epicVerify = new Epic("Epic 1", "Testing epic 1", 2, null, null);
        Subtask subtaskVerify = new Subtask("Subtask 1", "Testing subtask 1", 3,
                null, null, 2);
        ArrayList<Integer> epicSubtasksIds = new ArrayList<>();
        epicSubtasksIds.add(3);
        epicVerify.setSubtaskIds(epicSubtasksIds);
        ArrayList<Task> verifyHistoryList = new ArrayList<>();
        verifyHistoryList.add(taskVerify);
        verifyHistoryList.add(epicVerify);
        verifyHistoryList.add(subtaskVerify);
        String verifyListJson = gson.toJson(verifyHistoryList);
        assertEquals(verifyListJson, response.body());
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 15, 0));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                Duration.ofMinutes(20),
                LocalDateTime.of(2025, 1, 27, 16, 0), 2);
        String subtaskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Set<Task> prioritizedVerifyTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        Task taskVerify = new Task("Task 1", "Testing task 1", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2025, 1, 27, 15, 0));
        Subtask subtaskVerify = new Subtask("Subtask 1", "Testing subtask 1", 3,
                Duration.ofMinutes(20),
                LocalDateTime.of(2025, 1, 27, 16, 0), 2);
        prioritizedVerifyTasks.add(taskVerify);
        prioritizedVerifyTasks.add(subtaskVerify);

        String verifyPrioritizedListJson = gson.toJson(prioritizedVerifyTasks);
        assertEquals(verifyPrioritizedListJson, response.body());
    }

    @Test
    public void testHasCrossingTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 15, 0));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task 2", "Testing task 2", Duration.ofMinutes(5),
                LocalDateTime.of(2025, 1, 27, 14, 57));
        String task2Json = gson.toJson(task2);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }
}
