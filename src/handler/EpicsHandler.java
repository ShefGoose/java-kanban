package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import entity.Epic;
import entity.Status;
import entity.Subtask;
import exception.ManagerValidateException;
import manager.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/epics$", path)) {
                        List<Epic> allEpics = taskManager.getEpics();
                        if (!allEpics.isEmpty()) {
                            sendText(exchange, gson.toJson(allEpics), 200);
                        } else {
                            sendText(exchange, "Список задач пуст.", 404);
                            break;
                        }
                    }

                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathId);
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic), 200);
                        } else {
                            sendText(exchange, "Задача с id: " + id + " не найдена.", 404);
                            break;
                        }
                    }

                    if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/epics/", "")
                                .replaceAll("/subtasks", "");
                        int id = parsePathId(pathId);
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            ArrayList<Subtask> listSubtasks = taskManager
                                    .getEpicSubtasks(epic);
                            if (listSubtasks.isEmpty()) {
                                sendText(exchange, "Подзадач нет.", 404);
                            } else {
                                sendText(exchange, gson.toJson(listSubtasks), 200);
                            }
                        } else {
                            sendText(exchange, "Задача с id: " + id + " не найдена.", 404);
                            break;
                        }
                    }
                }
                break;

                case "POST": {
                    Epic epic;
                    String bodyEpic = readText(exchange);
                    try {
                        epic = gson.fromJson(bodyEpic, Epic.class);
                        if (epic.getName().isEmpty() || epic.getDescription().isEmpty()) {
                            sendText(exchange, "Имя и описание задачи не могут быть пустыми.", 400);
                            return;
                        }
                        if (epic.getSubtaskIds() == null) {
                            epic.setSubtaskIds(new ArrayList<>());
                        }
                        if (!bodyEpic.contains("status")) {
                            epic.setStatus(Status.valueOf("NEW"));

                        }
                        if (epic.getId() != 0) {
                            taskManager.updateEpic(epic);
                            sendText(exchange, "Задача с id: " + epic.getId() + " обновлена.", 201);
                        } else {
                            taskManager.addNewEpic(epic);
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
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathId);
                        taskManager.deleteEpic(id);
                        sendText(exchange, "Задача с id: " + id + " удалена.", 200);
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
