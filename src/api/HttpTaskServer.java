package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.HTTPTaskManager;
import managers.Managers;
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
    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_DELETE = "DELETE";
    private static final String CONTEXT = "/tasks";
    private static final String TASK_PATH = "/task";
    private static final String SUBTASK_PATH = "/subtask";
    private static final String EPIC_PATH = "/epic";
    private static final String ANYTASK_PATH = "/anytask";
    private static final String ID_PATH = "/\\?id=";
    private static final int PORT = 8080;
    private static Gson gson;
    private static HttpServer server;
    private static HTTPTaskManager manager;
    private static String fullPath;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(HTTPTaskManager manager) throws IOException {
        this.manager = manager;

        gson = new Gson();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext(CONTEXT, new TaskHandler());
        server.start();
    }

    public void stopServer() {
        server.stop(0);
    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            fullPath = "";

            String method = httpExchange.getRequestMethod();
            fullPath = httpExchange.getRequestURI().toString();

            switch(method) {
                case HTTP_POST:
                    postRequests(httpExchange);
                    break;
                case HTTP_GET:
                    getRequests(httpExchange);
                    break;
                case HTTP_DELETE:
                    deleteRequests(httpExchange);
                    break;
                default:
                    System.out.println("Некорректный метод!");
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.close();
                    break;
            }
        }
    }

    private static void postRequests(HttpExchange httpExchange) throws IOException {
        if (fullPath.endsWith(TASK_PATH)) {
            postTask(httpExchange);
        } if (fullPath.endsWith(SUBTASK_PATH)) {
            postSubtask(httpExchange);
        } if (fullPath.endsWith(EPIC_PATH)) {
            postEpic(httpExchange);
        } else {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getRequests(HttpExchange httpExchange) throws IOException {
        if (fullPath.endsWith(TASK_PATH)) {
            getAllTasks(httpExchange);
        } else if (fullPath.endsWith(SUBTASK_PATH)) {
            getAllSubtasks(httpExchange);
        } else if (fullPath.endsWith(EPIC_PATH)) {
            getAllEpics(httpExchange);
        } else if (fullPath.endsWith(CONTEXT + "/")) {
            getAllSortedTasks(httpExchange);
        } else if (fullPath.endsWith("/history")) {
            getHistory(httpExchange);
        } else if (Pattern.matches("^" + CONTEXT + TASK_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + TASK_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            getTaskByID(httpExchange, id);
        } else if (Pattern.matches("^" + CONTEXT + SUBTASK_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + SUBTASK_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            getSubtaskByID(httpExchange, id);
        } else if (Pattern.matches("^" + CONTEXT + EPIC_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + EPIC_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            getEpicByID(httpExchange, id);
        } else if (Pattern.matches("^" + CONTEXT + ANYTASK_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + ANYTASK_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            getAnyTaskByID(httpExchange, id);
        } else if (Pattern.matches("^" + CONTEXT + SUBTASK_PATH + EPIC_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + SUBTASK_PATH + EPIC_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            getSubtasksFromEpic(httpExchange, id);
        } else {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteRequests(HttpExchange httpExchange) throws IOException {
        if (fullPath.endsWith(TASK_PATH + "/")) {
            deleteAllTasks(httpExchange);
        } else if (fullPath.endsWith(SUBTASK_PATH + "/")) {
            deleteAllSubtasks(httpExchange);
        } else if (fullPath.endsWith(EPIC_PATH + "/")) {
            deleteAllEpics(httpExchange);
        } else if (fullPath.endsWith(ANYTASK_PATH + "/")) {
            deleteAllTasksEpicsAndSubtasks(httpExchange);
        } else if (Pattern.matches("^" + CONTEXT + TASK_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + TASK_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            deleteTaskByID(httpExchange, id);
        } else if (Pattern.matches("^" + CONTEXT + SUBTASK_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + SUBTASK_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            deleteSubtaskByID(httpExchange, id);
        } else if (Pattern.matches("^" + CONTEXT + EPIC_PATH + ID_PATH + "\\d+$", fullPath)) {
            String idString = fullPath.replaceFirst(CONTEXT + EPIC_PATH + ID_PATH, "");
            int id = parsePathID(idString, httpExchange);
            deleteEpicByID(httpExchange, id);
        } else {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getTaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            Task task = manager.findTaskByID(id);
            final String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getSubtaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            Task task = manager.findSubtaskByID(id);
            final String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getEpicByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            Task task = manager.findEpicByID(id);
            final String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getAnyTaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            Task task = manager.findEveryTaskByID(id);
            final String response = gson.toJson(task);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void getSubtasksFromEpic(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            List<Subtask> listSubtasks = manager.getSubtasksFromEpic(id);
            final String response = gson.toJson(listSubtasks);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteTaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            manager.deleteTaskByNum(id);
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteSubtaskByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            manager.deleteSubtaskByNum(id);
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static void deleteEpicByID(HttpExchange httpExchange, Integer id) throws IOException {
        try {
            manager.deleteEpicByNum(id);
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
        } catch (IOException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
        }
    }

    private static int parsePathID(String idString, HttpExchange httpExchange) throws IOException {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            System.out.println("Возникла ошибка c id задачи. Пожалуйста, проверьте адрес запроса");
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
            return -1;
        }
    }
}