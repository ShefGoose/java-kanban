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

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {


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
                            send200(exchange, gson.toJson(allTasks));
                        } else {
                            send404(exchange, "Список задач пуст.");
                            break;
                        }
                    }

                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getSubtask(id) != null) {
                            send200(exchange, gson.toJson(taskManager.getSubtask(id)));
                        } else {
                            send404(exchange, "Задача с id:" + id + " не найдена.");
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
                            send400(exchange, "Имя и описание задачи не могут быть пустыми.");
                            return;
                        }
                        if (!bodyTask.contains("status")) {
                            subtask.setStatus(Status.valueOf("NEW"));

                        }
                        if (taskManager.getSubtaskList().containsKey(subtask.getId())) {
                            taskManager.updateSubtask(subtask);
                            send201(exchange, "Задача с id: " + subtask.getId() + " обновлена.");
                        } else {
                            taskManager.addNewSubtask(subtask);
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
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getSubtask(id) != null) {
                            taskManager.deleteSubtask(id);
                            send200(exchange, "Задача с id: " + id + " удалена.");
                        } else {
                            send400(exchange, "задача не найдена для удаления.");
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
