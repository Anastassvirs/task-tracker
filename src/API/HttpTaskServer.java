package API;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.TreeSet;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static Gson gson;
    private static HttpServer server;
    private static TaskManager manager;

    public static void main(String[] args) throws IOException {
        HttpTaskServer taskserver = new HttpTaskServer();
        server.start();
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault("src\\resources\\input.csv"));
    }
    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;

        gson = new Gson();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String path = "";
            String StringPostId = "";
            int postId = 0;

            String method = httpExchange.getRequestMethod();

            switch(method) {
                case "POST":
                    break;
                case "GET":
                    path = httpExchange.getRequestURI().getPath();

                    if (path.endsWith("/task")) {
                        try {
                            final List<Task> tasks = manager.getAllTasks();
                            final String response = gson.toJson(tasks);
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                        }
                    } else if (path.endsWith("/subtask")) {
                        try {
                            final List<Subtask> subtasks = manager.getAllSubtasks();
                            final String response = gson.toJson(subtasks);
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                        }
                    } else if (path.endsWith("/epic")) {
                        try {
                            final List<Epic> epics = manager.getAllEpics();
                            final String response = gson.toJson(epics);
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                        }
                    } else if (path.endsWith("/tasks/")) {
                        try {
                            final TreeSet<Task> alltasks = manager.getPrioritizedTasks();
                            System.out.println(alltasks);
                            final String response = gson.toJson(alltasks);
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                        }
                    } else if (path.endsWith("/history")) {
                        try {
                            final List<Task> historyTasks = manager.history();
                            final String response = gson.toJson(historyTasks);
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                        }
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                    }
                    break;
                case "DELETE":
                    break;
                default:
                    System.out.println("Некорректный метод!");
                    break;
            }
        }
    }

}
