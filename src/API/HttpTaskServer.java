package API;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static Gson gson;
    private static HttpServer server;
    private static TaskManager manager;
    private static String fullpath;

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
            fullpath = "";
            String StringPostId = "";
            int postId = 0;

            String method = httpExchange.getRequestMethod();
            fullpath = httpExchange.getRequestURI().toString();

            switch(method) {
                case "POST":
                    if (fullpath.endsWith("/task")) {
                        postTask(httpExchange);
                    } if (fullpath.endsWith("/subtask")) {
                        postSubtask(httpExchange);
                    } if (fullpath.endsWith("/epic")) {
                        postEpic(httpExchange);
                    } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            httpExchange.close();
                    }
                        break;
                case "GET":
                    if (fullpath.endsWith("/task")) {
                        getAllTasks(httpExchange);
                    } else if (fullpath.endsWith("/subtask")) {
                        getAllSubtasks(httpExchange);
                    } else if (fullpath.endsWith("/epic")) {
                        getAllEpics(httpExchange);
                    } else if (fullpath.endsWith("/tasks/")) {
                        getAllSortedTasks(httpExchange);
                    } else if (fullpath.endsWith("/history")) {
                        getHistory(httpExchange);
                    } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/task/\\?id=", "");
                        int id = parsePathID(idString);
                        getTaskByID(httpExchange, id);
                    } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/subtask/\\?id=", "");
                        int id = parsePathID(idString);
                        getSubtaskByID(httpExchange, id);
                    } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/epic/\\?id=", "");
                        int id = parsePathID(idString);
                        getEpicByID(httpExchange, id);
                    } else if (Pattern.matches("^/tasks/anytask/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/anytask/\\?id=", "");
                        int id = parsePathID(idString);
                        getAnyTaskByID(httpExchange, id);
                    } else if (Pattern.matches("^/tasks/subtask/epic/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/subtask/epic/\\?id=", "");
                        int id = parsePathID(idString);
                        getSubtasksFromEpic(httpExchange, id);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                    }
                    break;
                case "DELETE":
                    System.out.println();
                    if (fullpath.endsWith("/task/")) {
                        deleteAllTasks(httpExchange);
                    } else if (fullpath.endsWith("/subtask/")) {
                        deleteAllSubtasks(httpExchange);
                    } else if (fullpath.endsWith("/epic/")) {
                        deleteAllEpics(httpExchange);
                    } else if (fullpath.endsWith("/alltasks/")) {
                        deleteAllTasksEpicsAndSubtasks(httpExchange);
                    } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/task/\\?id=", "");
                        int id = parsePathID(idString);
                        deleteTaskByID(httpExchange, id);
                    } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/subtask/\\?id=", "");
                        int id = parsePathID(idString);
                        deleteSubtaskByID(httpExchange, id);
                    } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", fullpath)) {
                        String idString = fullpath.replaceFirst("/tasks/epic/\\?id=", "");
                        int id = parsePathID(idString);
                        deleteEpicByID(httpExchange, id);
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

    private static void postTask(HttpExchange httpExchange) throws IOException {
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
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void postSubtask(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() != null) {
                manager.updateTask(subtask, subtask.getId());
            } else {
                manager.addNewTask(subtask);
            }
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (NullPointerException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void postEpic(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getId() != null) {
                manager.updateTask(epic, epic.getId());
            } else {
                manager.addNewTask(epic);
            }
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (NullPointerException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getAllTasks(HttpExchange httpExchange) throws IOException {
        try {
            final List<Task> tasks = manager.getAllTasks();
            final String response = gson.toJson(tasks);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getAllSubtasks(HttpExchange httpExchange) throws IOException {
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
    }

    private static void getAllEpics(HttpExchange httpExchange) throws IOException {
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
    }

    private static void getAllSortedTasks(HttpExchange httpExchange) throws IOException {
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
    }

    private static void getHistory(HttpExchange httpExchange) throws IOException {
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
    }

    private static void getTaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
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
    }

    private static void getSubtaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
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
    }

    private static void getEpicByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
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
    }

    private static void getAnyTaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
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
    }

    private static void getSubtasksFromEpic(HttpExchange httpExchange, Integer id) throws IOException {
        try {
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
    }

    private static void deleteAllTasks(HttpExchange httpExchange) throws IOException {
        try {
            manager.deleteAllTasks();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteAllSubtasks(HttpExchange httpExchange) throws IOException {
        try {
            manager.deleteAllSubtasks();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteAllEpics(HttpExchange httpExchange) throws IOException {
        try {
            manager.deleteAllSubtasks();
            manager.deleteAllEpics();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteAllTasksEpicsAndSubtasks(HttpExchange httpExchange) throws IOException {
        try {
            manager.deleteAll();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteTaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            if (id != -1) {
                manager.deleteTaskByNum(id);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            } else {
                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                httpExchange.sendResponseHeaders(404, 0);
                httpExchange.close();
            }
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteSubtaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            if (id != -1) {
                manager.deleteSubtaskByNum(id);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            } else {
                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                httpExchange.sendResponseHeaders(404, 0);
                httpExchange.close();
            }
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteEpicByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            if (id != -1) {
                manager.deleteEpicByNum(id);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            } else {
                System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
                httpExchange.sendResponseHeaders(404, 0);
                httpExchange.close();
            }
        } catch (NullPointerException | IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
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
