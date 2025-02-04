package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {


    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                if (Pattern.matches("^/history$", path)) {
                    sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
                } else {
                    sendText(exchange, "Неизвестный путь.", 400);
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}