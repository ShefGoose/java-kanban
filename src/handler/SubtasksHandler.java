package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Status;
import entity.Subtask;
import exception.ManagerValidateException;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler {


    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/subtasks$", path)) {
                        List<Subtask> allTasks = taskManager.getSubtasks();
                        if (!allTasks.isEmpty()) {
                            sendText(exchange, gson.toJson(allTasks), 200);
                        } else {
                            sendText(exchange, "Список задач пуст.", 404);
                            break;
                        }
                    }

                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getSubtaskIdsList().contains(id)) {
                            sendText(exchange, gson.toJson(taskManager.getSubtask(id)), 200);
                        } else {
                            sendText(exchange, "Задача с id: " + id + " не найдена.", 404);
                            break;
                        }
                    }
                }
                break;

                case "POST": {
                    Subtask subtask;
                    String bodyTask = readText(exchange);
                    try {
                        subtask = gson.fromJson(bodyTask, Subtask.class);
                        if (subtask.getName().isEmpty() || subtask.getDescription().isEmpty()) {
                            sendText(exchange, "Имя и описание задачи не могут быть пустыми.", 400);
                            return;
                        }
                        if (!bodyTask.contains("status")) {
                            subtask.setStatus(Status.valueOf("NEW"));

                        }
                        if (taskManager.getSubtaskIdsList().contains(subtask.getId())) {
                            taskManager.updateSubtask(subtask);
                            sendText(exchange, "Задача с id: " + subtask.getId() + " обновлена.", 201);
                        } else {
                            taskManager.addNewSubtask(subtask);
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
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getSubtaskIdsList().contains(id)) {
                            taskManager.deleteSubtask(id);
                            sendText(exchange, "Задача с id: " + id + " удалена.", 200);
                        } else {
                            sendText(exchange, "задача не найдена для удаления.", 400);
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
