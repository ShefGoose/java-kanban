package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {


    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                if (Pattern.matches("^/prioritized$", path)) {
                    if (!taskManager.getPrioritizedTasks().isEmpty()) {
                        send200(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
                    } else {
                        send404(exchange, "Список приоритетных задач пуст.");
                    }
                } else {
                    send400(exchange, "Неизвестный путь.");
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
