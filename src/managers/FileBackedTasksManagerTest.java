package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {

    FileBackedTasksManager taskManager;

    @BeforeEach
    void makeManager() {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("output.csv"));
        this.taskManager = manager.loadFromFile(new File("forTests.csv"));
    }

    @Test
    void noHistoryManager() {
        FileBackedTasksManager taskManagerWithNoHistory = new FileBackedTasksManager(new File("output.csv"));
        taskManagerWithNoHistory = taskManagerWithNoHistory.loadFromFile(new File("forTestsWithEmptyHistory.csv"));

        final List<Task> taskss = taskManagerWithNoHistory.history();

        assertNotNull(taskss, "Задачи из истории не возвращаются.");
        assertEquals(0, taskss.size(), "Неверное количество задач в истории.");
    }

    @Test
    void noTasksManager() {
        FileBackedTasksManager taskManagerWithNoHistory = new FileBackedTasksManager(new File("output.csv"));
        taskManagerWithNoHistory = taskManagerWithNoHistory.loadFromFile(new File("forTestsWithNoTasks.csv"));

        final List<Task> history = taskManagerWithNoHistory.history();
        final List<Task> taskss = taskManagerWithNoHistory.history();
        final List<Task> subtaskss = taskManagerWithNoHistory.history();
        final List<Task> epicss = taskManagerWithNoHistory.history();

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
    void isEveryTasksAdded() {
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

        assertNotNull(taskManager.findEpicByID(taskManager.findSubtaskByID(4).getNumberOfEpicTask()).getsubtasks(),
                "Подзадачи не добавляются в эпик.");
        assertEquals(
                taskManager.findEpicByID(taskManager.findSubtaskByID(4).getNumberOfEpicTask()).getsubtasks().get(4),
                taskManager.findSubtaskByID(4),
                "В соответствующий эпик добавлена не та подзадача"
        );

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпик не возвращается.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void addOldTask() {
        assertNotNull(taskManager.findTaskByID(1), "Задача не найдена.");
        assertEquals(
                taskManager.findTaskByID(1).getTaskName(),
                "task for tests",
                "Задачи не совпадают."
        );
    }

    @Test
    void addOldSubtask() {
        assertNotNull(taskManager.findSubtaskByID(4), "Задача не найдена.");
        assertEquals(
                taskManager.findSubtaskByID(4).getTaskName(),
                "subtask for tests",
                "Задачи не совпадают."
        );
    }

    @Test
    void addOldEpic() {
        assertNotNull(taskManager.findEpicByID(2), "Задача не найдена.");
        assertEquals(
                taskManager.findEpicByID(2).getTaskName(),
                "epic for tests",
                "Задачи не совпадают."
        );
    }

    @Test
    void addOldEpicWithoutSubtasks() {
        assertNotNull(taskManager.findEpicByID(3), "Задача не найдена.");
        assertEquals(
                taskManager.findEpicByID(3).getTaskName(),
                "empty epic for tests",
                "Эпик добавлен некорректно."
        );

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.get(1).getsubtasks().size(), "Неверное количество подзадач.");
    }

    @Test
    void save() throws IOException {
        taskManager.deleteAllEpics();
        String recoveryFile = Files.readString(Path.of(new File("output.csv").toString()));
        assertEquals(recoveryFile, "id,type,name,status,description,duration,startTime,epic\n" +
                "1,TASK,task for tests,NEW,description,20,2022-01-01T10:15:30\n" +
                "\n" +
                "1,3", "Задачи сохраняются неправильно");
    }

    @Test
    void isDurationAndStartTimeAndEndTimeOK() {
        assertNotNull(taskManager.findEpicByID(2), "Задача не найдена.");
        assertEquals(
                23,
                taskManager.findEpicByID(2).getDuration(),
                "Длительность восстановлена из памяти неверно"
        );
        assertEquals(LocalDateTime.of(
                2022, 1, 2, 11, 15, 30),
                taskManager.findEpicByID(2).getStartTime(),
                "Время начала эпика записано неправильно");
        assertEquals(LocalDateTime.of(2022, 1, 2, 11, 38,30),
                taskManager.findEpicByID(2).getEndTime(),
                "Время окончания эпика расчитано неправильно");
    }
}