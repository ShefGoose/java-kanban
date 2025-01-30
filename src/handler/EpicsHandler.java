package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Epic;
import entity.Status;
import entity.Subtask;
import exception.ManagerValidateException;
import manager.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

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
                            send200(exchange, gson.toJson(allEpics));
                        } else {
                            send404(exchange, "Список задач пуст.");
                            break;
                        }
                    }

                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getEpic(id) != null) {
                            send200(exchange, gson.toJson(taskManager.getEpic(id)));
                        } else {
                            send404(exchange, "Задача с id:" + id + " не найдена.");
                            break;
                        }
                    }

                    if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/epics/", "")
                                .replaceAll("/subtasks", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getEpic(id) != null) {
                            ArrayList<Subtask> listSubtasks = taskManager.getEpicSubtasks(taskManager.getEpic(id));
                            if (listSubtasks.isEmpty()) {
                                send404(exchange, "Подзадач нет.");
                            } else {
                                send200(exchange, gson.toJson(listSubtasks));
                            }
                        } else {
                            send404(exchange, "Задача с id:" + id + " не найдена.");
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
                            send400(exchange, "Имя и описание задачи не могут быть пустыми.");
                            return;
                        }
                        if (epic.getSubtaskIds() == null) {
                            epic.setSubtaskIds(new ArrayList<>());
                        }
                        if (!bodyEpic.contains("status")) {
                            epic.setStatus(Status.valueOf("NEW"));

                        }
                        if (taskManager.getEpicList().containsKey(epic.getId())) {
                            taskManager.updateEpic(epic);
                            send201(exchange, "Задача с id: " + epic.getId() + " обновлена.");
                        } else {
                            taskManager.addNewEpic(epic);
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
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathId);
                        if (taskManager.getEpic(id) != null) {
                            taskManager.deleteEpic(id);
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
