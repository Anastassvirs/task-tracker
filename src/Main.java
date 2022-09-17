import api.HttpTaskServer;
import api.KVServer;
import managers.HTTPTaskManager;
import managers.Managers;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        KVServer server = new KVServer();
        server.start();
        HTTPTaskManager manager = Managers.getDefault();
        System.out.println('\n' + "Список обычных задач: " + manager.getAllTasks());
        System.out.println("Список эпиков: " + manager.getAllEpics());
        System.out.println("Список подзадач: " + manager.getAllSubtasks());
        Task newTask = new Task("task1", "strong and serious", Status.NEW, Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 1, 1, 0, 0));
        Integer taskNum1 = manager.addNewTask(newTask);
        newTask = new Task("taskie daski", "funny and nice", Status.NEW, Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 2, 1, 0, 0));
        Integer taskNum2 = manager.addNewTask(newTask);
        // Три подзадачи + эпик
        Epic newEpic = new Epic("ep1", "usual epic you know", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 3, 1, 0, 0));
        Integer epicTaskNum1 = manager.addNewEpic(newEpic);
        Subtask newSubtask = new Subtask("sub1", "little subbie", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 4, 1, 0, 0), epicTaskNum1);
        Integer subtask1 = manager.addNewSubtask(newSubtask);
        newSubtask = new Subtask("sub2", "subsub", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 5, 1, 0, 0), epicTaskNum1);
        Integer subtask2 = manager.addNewSubtask(newSubtask);
        newSubtask = new Subtask("sub3", "subsubsub", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 6, 1, 0, 0), epicTaskNum1);
        Integer subtask3 = manager.addNewSubtask(newSubtask);
        // Пустой эпик
        newEpic = new Epic("ep2", "cool epic doesn't need subtasks",
                Status.NEW, Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 7, 1, 0, 0));
        Integer epicTaskNum2 = manager.addNewEpic(newEpic);

        System.out.println('\n' + "Список обычных задач: " + manager.getAllTasks());
        System.out.println("Список эпиков: " + manager.getAllEpics());
        System.out.println("Список подзадач: " + manager.getAllSubtasks());

        Task task = manager.findTaskByID(taskNum1);
        task = manager.findTaskByID(taskNum1);
        task = manager.findTaskByID(taskNum2);
        task = manager.findSubtaskByID(subtask1);
        task = manager.findTaskByID(taskNum2);
        task = manager.findEpicByID(epicTaskNum1);
        task = manager.findSubtaskByID(subtask3);
        task = manager.findSubtaskByID(subtask2);
        System.out.println(manager.history());

        manager = Managers.getDefaultWithLoad();
        System.out.println('\n' + "Список обычных задач: " + manager.getAllTasks());
        System.out.println("Список эпиков: " + manager.getAllEpics());
        System.out.println("Список подзадач: " + manager.getAllSubtasks());

        server.stop();

        HttpTaskServer taskserver = new HttpTaskServer();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response);
        taskserver.stopServer();
    }
}
