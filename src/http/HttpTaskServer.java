package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import handler.*;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;
    private TaskManager taskManager;
    private TasksHandler tasksHandler;
    private EpicsHandler epicsHandler;
    private SubtasksHandler subtasksHandler;
    private HistoryHandler historyHandler;

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Gson getGson() {
        return gson;
    }

    private PrioritizedHandler prioritizedHandler;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        tasksHandler = new TasksHandler(taskManager, gson);
        epicsHandler = new EpicsHandler(taskManager, gson);
        subtasksHandler = new SubtasksHandler(taskManager, gson);
        historyHandler = new HistoryHandler(taskManager, gson);
        prioritizedHandler = new PrioritizedHandler(taskManager, gson);

        server.createContext("/tasks", tasksHandler);
        server.createContext("/epics", epicsHandler);
        server.createContext("/subtasks", subtasksHandler);
        server.createContext("/history", historyHandler);
        server.createContext("/prioritized", prioritizedHandler);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer = new HttpTaskServer();
        httpServer.start();
        httpServer.stop();
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен на " + PORT + " порту.");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер на порту " + PORT + " остановлен.");
    }
}
