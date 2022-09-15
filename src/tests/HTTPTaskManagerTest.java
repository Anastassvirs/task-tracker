package tests;

import API.KVServer;
import managers.HTTPTaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest {
    static HTTPTaskManager manager;
    KVServer kvServer;

    @BeforeEach
    void makeManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault();
    }

    @AfterEach
    void closeServer() {
        kvServer.stop();
    }

    @Test
    void noHistoryManager() {
        manager = Managers.getDefault();

        final List<Task> taskss = manager.history();

        assertNotNull(taskss, "Задачи из истории не возвращаются.");
        assertEquals(0, taskss.size(), "Неверное количество задач в истории.");
    }

    @Test
    void noTasksManager() {
        final List<Task> history = manager.history();
        final List<Task> taskss = manager.getAllTasks();
        final List<Subtask> subtaskss = manager.getAllSubtasks();
        final List<Epic> epicss = manager.getAllEpics();

        assertNotNull(history, "Задачи из истории не возвращаются.");
        assertEquals(0, history.size(), "Неверное количество задач в истории.");
        assertNotNull(taskss, "Задачи не возвращаются.");
        assertEquals(0, taskss.size(), "Неверное количество задач.");
        assertNotNull(subtaskss, "Подзадачи не возвращаются.");
        assertEquals(0, subtaskss.size(), "Неверное количество подзадач.");
        assertNotNull(epicss, "Эпики не возвращаются.");
        assertEquals(0, epicss.size(), "Неверное количество эпиков.");
    }

    @Test
    void isEveryTaskSaved() {
        List<Task> tasks = new ArrayList<>();

        Task newTask = new Task("task1", "strong and serious", Status.NEW, Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 1, 1, 0, 0));
        Integer taskNum1 = manager.addNewTask(newTask);
        tasks.add(newTask);
        newTask = new Task("taskie daski", "funny and nice", Status.NEW, Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 2, 1, 0, 0));
        Integer taskNum2 = manager.addNewTask(newTask);
        tasks.add(newTask);

        Task task = manager.findTaskByID(taskNum1);
        task = manager.findTaskByID(taskNum2);

        manager = Managers.getDefaultWithLoad();
        final List<Task> newtasks = manager.getAllTasks();

        assertNotNull(newtasks, "Задачи из истории не возвращаются.");
        assertEquals(2, newtasks.size(), "Неверное количество задач в истории.");
        assertEquals(tasks, newtasks, "Задачи сохранены неправильно");
    }

    @Test
    void isEverySubtaskSaved() {
        List<Task> subtasks = new ArrayList<>();
        Epic newEpic = new Epic("ep1", "usual epic you know", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 3, 1, 0, 0));
        Integer epicTaskNum1 = manager.addNewEpic(newEpic);
        Subtask newSubtask = new Subtask("sub1", "little subbie", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 4, 1, 0, 0), epicTaskNum1);
        Integer subtask1 = manager.addNewSubtask(newSubtask);
        subtasks.add(newSubtask);
        newSubtask = new Subtask("sub2", "subsub", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 5, 1, 0, 0), epicTaskNum1);
        Integer subtask2 = manager.addNewSubtask(newSubtask);
        subtasks.add(newSubtask);
        newSubtask = new Subtask("sub3", "subsubsub", Status.IN_PROGRESS,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 6, 1, 0, 0), epicTaskNum1);
        Integer subtask3 = manager.addNewSubtask(newSubtask);
        subtasks.add(newSubtask);

        Task task = manager.findSubtaskByID(subtask1);
        task = manager.findSubtaskByID(subtask2);
        task = manager.findSubtaskByID(subtask3);

        manager = Managers.getDefaultWithLoad();

        final List<Subtask> newsubtasks = manager.getAllSubtasks();

        assertNotNull(newsubtasks, "Подзадачи из истории не возвращаются.");
        assertEquals(3, newsubtasks.size(), "Неверное количество подзадач в истории.");
        assertEquals(subtasks, newsubtasks, "Подадачи сохранены неправильно");
    }

    @Test
    void isEveryEpicSaved() {
        List<Epic> epics = new ArrayList<>();

        Epic newEpic = new Epic("ep1", "usual epic you know", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 3, 1, 0, 0));
        Integer epicTaskNum1 = manager.addNewEpic(newEpic);
        epics.add(newEpic);
        newEpic = new Epic("ep2", "soooo unusual epic", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 5, 1, 0, 0));
        Integer epicTaskNum2 = manager.addNewEpic(newEpic);
        epics.add(newEpic);

        Task task = manager.findEpicByID(epicTaskNum1);
        task = manager.findEpicByID(epicTaskNum2);

        manager = Managers.getDefaultWithLoad();
        final List<Epic> newepics = manager.getAllEpics();

        assertNotNull(newepics, "Задачи из истории не возвращаются.");
        assertEquals(2, newepics.size(), "Неверное количество задач в истории.");
        assertEquals(epics, newepics, "Задачи сохранены неправильно");

    }

    @Test
    void isDurationAndStartTimeAndEndTimeOK() {
        List<Task> tasks = new ArrayList<>();

        Task newTask = new Task("task1", "strong and serious", Status.NEW, Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 1, 1, 0, 0));
        Integer taskNum1 = manager.addNewTask(newTask);
        tasks.add(newTask);

        Task task = manager.findTaskByID(taskNum1);

        manager = Managers.getDefaultWithLoad();

        assertNotNull(manager.findTaskByID(taskNum1), "Задача не найдена.");
        assertEquals(
                Duration.of((long) 20, ChronoUnit.MINUTES),
                manager.findTaskByID(taskNum1).getDuration(),
                "Длительность восстановлена из памяти неверно"
        );
        assertEquals(LocalDateTime.of(
                        2022, 1, 1, 0, 0),
                manager.findTaskByID(taskNum1).getStartTime(),
                "Время начала эпика записано неправильно");
        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 20,0),
                manager.findTaskByID(taskNum1).getEndTime(),
                "Время окончания эпика расчитано неправильно");
    }
}