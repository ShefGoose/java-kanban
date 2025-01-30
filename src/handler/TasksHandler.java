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

public class TasksHandler extends BaseHttpHandler implements HttpHandler {


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
                            send200(exchange, gson.toJson(allTasks));
                        } else {
                            send404(exchange, "Список задач пуст.");
                            break;
                        }
                    }

                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getTask(id) != null) {
                            send200(exchange, gson.toJson(taskManager.getTask(id)));
                        } else {
                            send404(exchange, "Задача с id:" + id + " не найдена.");
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
                            send400(exchange, "Имя и описание задачи не могут быть пустыми.");
                            return;
                        }
                        if (!bodyTask.contains("status")) {
                            task.setStatus(Status.valueOf("NEW"));

                        }
                        if (taskManager.getTaskList().containsKey(task.getId())) {
                            taskManager.updateTask(task);
                            send201(exchange, "Задача с id: " + task.getId() + " обновлена.");
                        } else {
                            taskManager.addNewTask(task);
                            send201(exchange, "Задача добавлена в менеджер.");
                            break;
                        }
                    } catch (ManagerValidateException e) {
                        send406(exchange, "Задача пересекается с уже существующей.");
                        break;
                    } catch (JsonSyntaxException ex) {
                        send400(exchange, "Неккоректный JSON.");
                        break;
                    }
                }
                break;
                case "DELETE": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getTask(id) != null) {
                            taskManager.deleteTask(id);
                            send200(exchange, "Задача с id: " + id + " удалена.");
                        } else {
                            send400(exchange, "Задача не найдена для удаления.");
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
