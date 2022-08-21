package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void makeManager() {
        this.taskManager = new InMemoryTaskManager();
    }

    @Test
    void getAllTasks() {
        Task task1 = new Task("Test getAllTasks", "Test getAllTasks description", Status.NEW,
                 (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewTask(task1);
        Task task2 = new Task("Test getAllTasks2", "Test getAllTasks description2", Status.NEW,
                 (long) 20, LocalDateTime.of(2022, 1, 2, 0, 0));
        taskManager.addNewTask(task2);
        final Task[] tasks = new Task[] {task1, task2};

        final List<Task> tasksInManagerList = taskManager.getAllTasks();
        final Task[] tasksInManager = new Task[] {tasksInManagerList.get(0), tasksInManagerList.get(1)};

        assertNotNull(tasksInManagerList, "Задачи не возвращаются.");
        assertEquals(2, tasksInManagerList.size(), "Неверное количество задач.");

        assertArrayEquals(tasksInManager, tasks, "Массивы задач не совпадают");
    }

    @Test
    void getAllSubtasks() {
        Epic epic1 = new Epic("Test getAllEpics", "Test getAllEpics description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Test getAllSubtasks", "Test getAllSubtasks description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 2, 0, 0), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test getAllSubtasks2", "Test getAllSubtasks description2",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 3, 0, 0), epic1.getId());
        taskManager.addNewSubtask(subtask2);
        final Subtask[] subtasks = new Subtask[] {subtask1, subtask2};

        final List<Subtask> subtasksInManagerList = taskManager.getAllSubtasks();
        final Subtask[] subtasksInManager = new Subtask[] {subtasksInManagerList.get(0), subtasksInManagerList.get(1)};

        assertNotNull(subtasksInManagerList, "Подзадачи не возвращаются.");
        assertEquals(2, subtasksInManagerList.size(), "Неверное количество подзадач.");

        assertArrayEquals(subtasksInManager, subtasks, "Массивы подзадач не совпадают");
    }

    @Test
    void getAllEpics() {
        Epic epic1 = new Epic("Test getAllEpics", "Test getAllEpics description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Test getAllEpics2", "Test getAllEpics description2", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 2, 0, 0));
        taskManager.addNewEpic(epic2);
        final Epic[] epics = new Epic[] {epic1, epic2};

        final List<Epic> epicsInManagerList = taskManager.getAllEpics();
        final Epic[] epicsInManager = new Epic[] {epicsInManagerList.get(0), epicsInManagerList.get(1)};

        assertNotNull(epicsInManagerList, "Эпики не возвращаются.");
        assertEquals(2, epicsInManagerList.size(), "Неверное количество эпиков.");

        assertArrayEquals(epicsInManager, epics, "Массивы эпиков не совпадают");
    }

    @Test
    void deleteAllTasks() {
        Task task1 = new Task("Test deleteAllTasks", "Test deleteAllTasks description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewTask(task1);
        taskManager.deleteAllTasks();

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Список задач не создан, а не пуст.");
        assertEquals(0, tasks.size(), "Ненулевое количество задач.");
    }

    @Test
    void deleteAllSubtasks() {
        Epic epic1 = new Epic("Test deleteAllSubtasks", "Test deleteAllSubtasks description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Test deleteAllSubtasks", "Test deleteAllSubtasks description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.deleteAllSubtasks();

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Список подзадач не создан, а не пуст.");
        assertEquals(0, subtasks.size(), "Ненулевое количество подадач.");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Test deleteAllSubtasks", "Test deleteAllSubtasks description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Test deleteAllSubtasks", "Test deleteAllSubtasks description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.deleteAllEpics();

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(subtasks, "Список подзадач не создан, а не пуст.");
        assertNotNull(epics, "Список эпиков не создан, а не пуст.");
        assertEquals(0, subtasks.size(), "Ненулевое количество подадач.");
        assertEquals(0, epics.size(), "Ненулевое количество эпиков.");
    }

    @Test
    void findTaskByID() {
        Task task1 = new Task("Test findTaskByID", "Test findTaskByID description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        int taskId = taskManager.addNewTask(task1);
        Task task2 = new Task("Test findTaskByID2", "Test findTaskByID description2", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 3, 1, 0, 0));
        taskManager.addNewTask(task2);

        final Task task = taskManager.findTaskByID(taskId);

        assertNotNull(task, "Задача не найдена.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void findSubtaskByID() {
        Epic epic = new Epic("Test findSubtaskByID", "Test findSubtaskByID description for Epic",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test findSubtaskByID", "Test findSubtaskByID description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 2, 1, 0, 0), epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        Subtask subtask2 = new Subtask("Test findSubtaskByID2", "Test findSubtaskByID description2",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 3, 1, 0, 0), epic.getId());
        taskManager.addNewSubtask(subtask2);

        final Subtask findedSubtask = taskManager.findSubtaskByID(subtaskId);

        assertNotNull(findedSubtask, "Подзадача не найдена.");
        assertNotNull(taskManager.getAllEpics().get(findedSubtask.getNumberOfEpicTask()), "Подзадача не найдена.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(findedSubtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void findEpicByID() {
        Epic epic = new Epic("Test findEpicByID", "Test findEpicByID description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 4, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Epic epic2 = new Epic("Test findEpicByID2", "Test findEpicByID description2", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 5, 1, 0, 0));
        taskManager.addNewEpic(epic2);

        final Epic findedEpic = taskManager.findEpicByID(epicId);

        assertNotNull(findedEpic, "Эпик не найден.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void findEveryTaskByID() {
        Epic epic = new Epic("Test findEveryTaskByID", "Test findEveryTaskByID description for Epic",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 6, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Epic epic2 = new Epic("Test findEveryTaskByID2", "Test findEveryTaskByID description2", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 7, 1, 0, 0));
        taskManager.addNewEpic(epic2);
        Subtask subtask = new Subtask("Test findEveryTaskByID", "Test findEveryTaskByID description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 8, 1, 0, 0), epicId);
        taskManager.addNewSubtask(subtask);
        Subtask subtask2 = new Subtask("Test findEveryTaskByID2", "Test findEveryTaskByID description2",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 9, 1, 0, 0), epic.getId());
        taskManager.addNewSubtask(subtask2);
        Task task1 = new Task("Test findEveryTaskByID", "Test findEveryTaskByID description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int taskId = taskManager.addNewTask(task1);
        Task task2 = new Task("Test findEveryTaskByID2", "Test findEveryTaskByID description2", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 2, 1, 0, 0));
        taskManager.addNewTask(task2);

        final Task findedSubtask = taskManager.findEveryTaskByID(taskId);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(findedSubtask, "Задача не найдена.");
        assertEquals(findedSubtask, tasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.findTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewSubtask", "Test addNewSubtask description for Epic",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 2, 1, 0, 0), epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final Subtask savedSubtask = taskManager.findSubtaskByID(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW,
                 (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);

        final Epic savedEpic = taskManager.findEpicByID(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int taskId = taskManager.addNewTask(task);

        LocalDateTime newTime = LocalDateTime.of(2022, 3, 1, 0, 0);
        Task newTask = new Task("Test addNewTask", "Test addNewTask description", Status.NEW,
                (long) 20, newTime);

        taskManager.updateTask(newTask, taskId);

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, tasks.get(0), "Задачи не обновляются.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Test addNewSubtask", "Test addNewSubtask description for Epic",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 2, 1, 0, 0), epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        LocalDateTime newTime = LocalDateTime.of(2022, 3, 1, 0, 0);
        Subtask newSubtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                Status.NEW, (long) 20, newTime, epicId);

        taskManager.updateSubtask(newSubtask, subtaskId);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(newSubtask, subtasks.get(0), "Подзадачи не обновляются.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test addNewSubtask", "Test addNewSubtask description for Epic",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);

        LocalDateTime newTime = LocalDateTime.of(2022, 3, 1, 0, 0);
        Epic newEpic = new Epic("Test addNewSubtask", "Test addNewSubtask description for Epic",
                Status.NEW, (long) 20, newTime);

        taskManager.updateEpic(newEpic, epicId);

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(newEpic, epics.get(0), "Эпики не обновляются.");
    }

    @Test
    void deleteTaskByNum() {
        Task task1 = new Task("Test deleteTaskByNum", "Test deleteTaskByNum description", Status.NEW,
                 (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        int taskId = taskManager.addNewTask(task1);
        Task task2 = new Task("Test deleteTaskByNum2", "Test deleteTaskByNum description2",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewTask(task2);

        final Task savedTask = taskManager.findTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");

        taskManager.deleteTaskByNum(taskId);

        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.findTaskByID(taskId));

        assertNull(ex.getMessage());
    }

    @Test
    void deleteSubtaskByNum() {
        Epic epic = new Epic("Test deleteSubtaskByNum", "Tst deleteSubtaskByNum description for epic",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Test deleteSubtaskByNum",
                "Test deleteSubtaskByNum description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 2, 1, 0, 0), epicId);
        int subtaskId = taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test deleteSubtaskByNum",
                "Test deleteSubtaskByNum description2", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 2, 3, 0, 0), epicId);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteSubtaskByNum(subtaskId);

        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.findSubtaskByID(subtaskId));

        assertNull(ex.getMessage());
    }

    @Test
    void deleteEpicByNum() {
        Epic epic = new Epic("Test deleteEpicByNum", "Test deleteEpicByNum description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Epic epic2 = new Epic("Test deleteEpicByNum2", "Test deleteEpicByNum description2",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewEpic(epic2);

        taskManager.deleteEpicByNum(epicId);

        NullPointerException ex = assertThrows(NullPointerException.class, () -> taskManager.findEpicByID(epicId));

        assertNull(ex.getMessage());
    }

    @Test
    void getSubtasksFromEpic() {
        Epic epic1 = new Epic("Test getAllEpics", "Test getAllEpics description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Test getAllSubtasks", "Test getAllSubtasks description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 2, 0, 0), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test getAllSubtasks2", "Test getAllSubtasks description2",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 4, 0, 0), epic1.getId());
        taskManager.addNewSubtask(subtask2);
        final Subtask[] subtasks = new Subtask[] {subtask1, subtask2};

        final List<Subtask> subtasksInEpicList = taskManager.getSubtasksFromEpic(epic1.getId());
        final Subtask[] subtasksInEpic = new Subtask[] {subtasksInEpicList.get(0), subtasksInEpicList.get(1)};

        assertNotNull(subtasksInEpicList, "Подзадачи не возвращаются.");
        assertEquals(2, subtasksInEpicList.size(), "Неверное количество подзадач.");

        assertArrayEquals(subtasksInEpic, subtasks, "Массивы подзадач не совпадают");
    }

    @Test
    void getNoSubtasksFromEpic() {
        Epic epic1 = new Epic("Test getAllEpics", "Test getAllEpics description",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        taskManager.addNewEpic(epic1);

        final List<Subtask> subtasksInEpicList = taskManager.getSubtasksFromEpic(epic1.getId());

        assertNotNull(subtasksInEpicList, "Null вместо пустого массива подзадач");
        assertEquals(0, subtasksInEpicList.size(), "Ненулевое количество подзадач.");
    }

    @Test
    void history() {
        TaskManager taskManager1 = Managers.getDefault("input.csv");
        Task task1 = new Task("Test history", "Test history description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 2, 1, 4, 50));
        int taskId = taskManager1.addNewTask(task1);
        taskManager1.findTaskByID(taskId);
        taskManager1.deleteEpicByNum(6);
        taskManager1.deleteTaskByNum(1);
        List<Task> historyTasksList = taskManager1.history();
        Task[] historyTasks = new Task[] {task1};
        Task[] newHistoryTasks = new Task[1];
        for (int i = 0; i < historyTasksList.size(); i++) {
            newHistoryTasks[i] = historyTasksList.get(i);
        }

        assertNotNull(taskManager1.history(), "Задачи не возвращаются.");
        assertEquals(1, taskManager1.history().size(), "Неверное количество подзадач.");
        assertArrayEquals(newHistoryTasks, historyTasks, "Задачи не совпадают");
    }

    @Test
    void taskDurationStartTimeAndEndTime() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW,
                (long) 20, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.findTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(20, tasks.get(taskId).getDuration(), "Время исполнения установлено неправильно");
        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0),
                tasks.get(taskId).getStartTime(), "Время начала задачи установлено неправильно");
        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 20),
                tasks.get(taskId).getEndTime(), "Время окончания задачи расчитано неправильно");
    }

    @Test
    void subtaskDurationStartTimeAndEndTime() {
        Epic epic = new Epic("Test addNewSubtask", "Test addNewSubtask description for Epic",
                Status.NEW, (long) 30, LocalDateTime.of(2022, 1, 1, 0, 0));
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                Status.NEW, (long) 40, LocalDateTime.of(2022, 1, 2, 0, 0), epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(40, subtasks.get(0).getDuration(),
                "Время исполнения установлено неправильно");
        assertEquals(LocalDateTime.of(2022, 1, 2, 0, 0),
                taskManager.findSubtaskByID(subtaskId).getStartTime(), "Время начала задачи установлено неправильно");
        assertEquals(LocalDateTime.of(2022, 1, 2, 0, 40),
                taskManager.findSubtaskByID(subtaskId).getEndTime(), "Время окончания задачи расчитано неправильно");
    }

    @Test
    void getPrioritizedTasksTest() {
        Task task1 = new Task("Test getPrioritizedTasksTest",
                "Test getPrioritizedTasksTest description", Status.NEW,
                (long) 20, LocalDateTime.of(2020, 6, 1, 0, 0));
        taskManager.addNewTask(task1);
        Task task2 = new Task("Test getPrioritizedTasksTest",
                "Test getPrioritizedTasksTest description2",
                Status.NEW, (long) 20, LocalDateTime.of(2023, 1, 1, 0, 0));
        taskManager.addNewTask(task2);
        Task task3 = new Task("Test getPrioritizedTasksTest",
                "Test getPrioritizedTasksTest description", Status.NEW,
                (long) 20, LocalDateTime.of(2020, 5, 13, 0, 0));
        taskManager.addNewTask(task3);
        Task task4 = new Task("Test getPrioritizedTasksTest",
                "Test getPrioritizedTasksTest description2",
                Status.NEW, (long) 20, LocalDateTime.of(2020, 5, 11, 6, 3));
        taskManager.addNewTask(task4);
        Task task5 = new Task("Test getPrioritizedTasksTest",
                "Test getPrioritizedTasksTest description2",
                Status.NEW, (long) 20, LocalDateTime.of(2020, 5, 11, 0, 0));
        taskManager.addNewTask(task5);
        System.out.println(taskManager.getPrioritizedTasks());
        assertNotNull(taskManager.getPrioritizedTasks(), "Подзадачи на возвращаются.");
    }

    @Test
    void validate() {
        Task task1 = new Task("Test validate",
                "Test validate description", Status.NEW,
                (long) 20, LocalDateTime.of(2020, 1, 1, 0, 0));
        taskManager.addNewTask(task1);
        Task task2 = new Task("Test validate",
                "Test validate description 2",
                Status.NEW, (long) 20, LocalDateTime.of(2020, 1, 1, 0, 0));
        taskManager.addNewTask(task2);
        Task task3 = new Task("Test validate",
                "Test validate description 3", Status.NEW,
                (long) 20, LocalDateTime.of(2020, 1, 1, 0, 19));
        taskManager.addNewTask(task3);

        Epic epic1 = new Epic("Test validate", "Test validate description 4",
                Status.NEW, (long) 20, LocalDateTime.of(2021, 1, 1, 0, 0));
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Test validate", "Test validate description 5",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 2, 0, 0),
                epic1.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test validate", "Test validate description 6",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 2, 0, 20),
                epic1.getId());
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Test validate", "Test validate description 7",
                Status.NEW, (long) 20, LocalDateTime.of(2022, 1, 2, 0, 15),
                epic1.getId());
        taskManager.addNewSubtask(subtask3);

        Task task4 = new Task("Test validate",
                "Test validate description 8", Status.NEW,
                (long) 20, LocalDateTime.of(2021, 1, 1, 0, 1));
        taskManager.addNewTask(task4);

        List<Task> tasks = taskManager.getAllTasks();
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(),
                "Задачи сохраняются, хотя накладываются по времени друг на друга");
        assertEquals(2, subtasks.size(),
                "Ошибка валидации");
    }
}