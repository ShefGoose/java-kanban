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

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer = new HttpTaskServer(Managers.getDefault());
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
