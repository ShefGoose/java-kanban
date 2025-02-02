package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Status;
import entity.Task;
import exception.ManagerValidateException;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler {


    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks$", path)) {
                        List<Task> allTasks = taskManager.getTasks();
                        if (!allTasks.isEmpty()) {
                            sendText(exchange, gson.toJson(allTasks), 200);
                        } else {
                            sendText(exchange, "Список задач пуст.", 404);
                            break;
                        }
                    }

                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getTaskIdsList().contains(id)) {
                            sendText(exchange, gson.toJson(taskManager.getTask(id)), 200);
                        } else {
                            sendText(exchange, "Задача с id: " + id + " не найдена.", 404);
                            break;
                        }
                    }
                }
                break;

                case "POST": {
                    Task task;
                    String bodyTask = readText(exchange);
                    try {
                        task = gson.fromJson(bodyTask, Task.class);
                        if (task.getName().isEmpty() || task.getDescription().isEmpty()) {
                            sendText(exchange, "Имя и описание задачи не могут быть пустыми.", 400);
                            return;
                        }
                        if (!bodyTask.contains("status")) {
                            task.setStatus(Status.valueOf("NEW"));

                        }
                        if (taskManager.getTaskIdsList().contains(task.getId())) {
                            taskManager.updateTask(task);
                            sendText(exchange, "Задача с id: " + task.getId() + " обновлена.", 201);
                        } else {
                            taskManager.addNewTask(task);
                            sendText(exchange, "Задача добавлена в менеджер.", 201);
                            break;
                        }
                    } catch (ManagerValidateException e) {
                        sendText(exchange, "Задача пересекается с уже существующей.", 406);
                        break;
                    } catch (JsonSyntaxException ex) {
                        sendText(exchange, "Неккоректный JSON.", 400);
                        break;
                    }
                }
                break;
                case "DELETE": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getTaskIdsList().contains(id)) {
                            taskManager.deleteTask(id);
                            sendText(exchange, "Задача с id: " + id + " удалена.", 200);
                        } else {
                            sendText(exchange, "Задача не найдена для удаления.", 400);
                            break;
                        }
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    exchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
