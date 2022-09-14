package API;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

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
            String fullpath = "";
            String StringPostId = "";
            int postId = 0;

            String method = httpExchange.getRequestMethod();
            fullpath = httpExchange.getRequestURI().toString();

            switch(method) {
                case "POST":
                    if (fullpath.endsWith("/task")) {
                        try {
                            InputStream inputStream = httpExchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Task task = gson.fromJson(body, Task.class);
                            if (task.getId() != null) {
                                manager.updateTask(task, task.getId());
                            } else {
                                manager.addNewTask(task);
                            }
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                    }
                    break;
                case "GET":
                    if (fullpath.endsWith("/task")) {
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
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (fullpath.endsWith("/subtask")) {
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
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (fullpath.endsWith("/epic")) {
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
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (fullpath.endsWith("/tasks/")) {
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
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (fullpath.endsWith("/history")) {
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
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/task/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                Task task = manager.findTaskByID(id);
                                final String response = gson.toJson(task);
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }

                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/subtask/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                Task task = manager.findSubtaskByID(id);
                                final String response = gson.toJson(task);
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }

                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/epic/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                Task task = manager.findEpicByID(id);
                                final String response = gson.toJson(task);
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }

                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/anytask/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/anytask/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                Task task = manager.findEveryTaskByID(id);
                                final String response = gson.toJson(task);
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }

                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/subtask/epic/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/subtask/epic/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                List<Subtask> listSubtasks = manager.getSubtasksFromEpic(id);
                                final String response = gson.toJson(listSubtasks);
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }

                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                    }
                    break;
                case "DELETE":
                    System.out.println();
                    if (fullpath.endsWith("/task/")) {
                        try {
                            manager.deleteAllTasks();
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (fullpath.endsWith("/subtask/")) {
                        try {
                            manager.deleteAllSubtasks();
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (fullpath.endsWith("/epic/")) {
                        try {
                            manager.deleteAllSubtasks();
                            manager.deleteAllEpics();
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (fullpath.endsWith("/alltasks/")) {
                        try {
                            manager.deleteAll();
                            httpExchange.sendResponseHeaders(200, 0);
                            httpExchange.close();
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/task/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                manager.deleteTaskByNum(id);
                                httpExchange.sendResponseHeaders(200, 0);
                                httpExchange.close();
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/subtask/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                manager.deleteSubtaskByNum(id);
                                httpExchange.sendResponseHeaders(200, 0);
                                httpExchange.close();
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", fullpath)) {
                        try {
                            String idString = fullpath.replaceFirst("/tasks/epic/\\?id=", "");
                            int id = parsePathID(idString);
                            if (id != -1) {
                                manager.deleteEpicByNum(id);
                                httpExchange.sendResponseHeaders(200, 0);
                                httpExchange.close();
                            } else {
                                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                                httpExchange.sendResponseHeaders(404, 0);
                                httpExchange.close();
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                    "Проверьте, пожалуйста, адрес и повторите попытку.");
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                        }
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                    }
                    break;
                default:
                    System.out.println("Некорректный метод!");
                    break;
            }
        }
    }


    private static int parsePathID(String idString) {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
