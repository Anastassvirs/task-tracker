package tests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager manager;
    private Integer subtask;
    Epic epic;
    Integer epicTaskNum;

    @BeforeEach
     void createSubtasks() {
        manager = Managers.getDefault("src\\resources\\input.csv");
        epic = new Epic("ep2", "cool epic doesn't need subtasks", Status.NEW, Duration.ZERO,
                LocalDateTime.of(2022, 1, 1, 0, 0));
        epicTaskNum = manager.addNewEpic(epic);
    }

    @Test
    void emptyEpic() {
        assertNotNull(manager.getAllEpics());
        assertEquals(manager.findEpicByID(epicTaskNum), epic, "Эпик добавлен некорректно");
    }

    @Test
    void newEpicWithTwoNewSubtasks() {
        Subtask subtaskSub = new Subtask("sub1", "little subbie", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 1, 1, 0, 0), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        subtaskSub = new Subtask("sub2", "subsub", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 1, 1, 0, 0), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        assertNotNull(manager.getAllEpics());
        assertEquals(manager.findEpicByID(epicTaskNum).getProgressStatus(), Status.NEW, "Статус эпика не NEW," +
                " хотя все подзадачи имеют статус NEW");
    }

    @Test
    void doneEpicWithTwoDoneSubtasks() {
        Subtask subtaskSub = new Subtask("sub1", "little subbie", Status.DONE,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2021, 1, 1, 0, 0), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        subtaskSub = new Subtask("sub2", "subsub", Status.DONE,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 1, 1, 0, 0), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        assertNotNull(manager.getAllEpics());
        assertEquals(Status.DONE, manager.findEpicByID(epicTaskNum).getProgressStatus(), "Статус эпика не DONE," +
                " хотя все подзадачи имеют статус DONE");
    }

    @Test
    void inProgressEpicWithDoneAndNewSubtasks() {
        Subtask subtaskSub = new Subtask("sub1", "little subbie", Status.DONE,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 2, 1, 0, 0), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        subtaskSub = new Subtask("sub2", "subsub", Status.NEW,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 2, 1, 0, 21), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        assertNotNull(manager.getAllEpics());
        assertEquals(Status.IN_PROGRESS, manager.findEpicByID(epicTaskNum).getProgressStatus(), "Статус эпика не IN_PROGRESS," +
                " хотя подзадачи имеют статусы DONE и NEW");
    }

    @Test
    void inProgressEpicWithTwoInProgressSubtasks() {
        Subtask subtaskSub = new Subtask("sub1", "little subbie", Status.IN_PROGRESS,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 3, 1, 0, 21), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        subtaskSub = new Subtask("sub2", "subsub", Status.IN_PROGRESS,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 3, 1, 0, 0), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        assertNotNull(manager.getAllEpics());
        assertEquals(manager.findEpicByID(epicTaskNum).getProgressStatus(), Status.IN_PROGRESS, "Статус эпика не IN_PROGRESS," +
                " хотя все подзадачи имеют статус IN_PROGRESS");
    }

    @Test
    void epicWithSubtasksDurationAndStartEndTimeTest() {
        Subtask subtaskSub = new Subtask("sub1", "little subbie", Status.DONE,
                Duration.of((long) 20, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 5, 1, 0, 0), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        subtaskSub = new Subtask("sub2", "subsub", Status.NEW,
                Duration.of((long) 30, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, 6, 1, 18, 36), epicTaskNum);
        subtask = manager.addNewSubtask(subtaskSub);
        assertNotNull(manager.getAllEpics());
        assertEquals( Duration.of((long) 50, ChronoUnit.MINUTES), manager.findEpicByID(epicTaskNum).getDuration(),
                "Продолжительность эпика расчитана неправильно");
        assertEquals(LocalDateTime.of(2022, 5, 1, 0, 0)
                , manager.findEpicByID(epicTaskNum).getStartTime(),
                "Время начала эпика записано неправильно");
        assertEquals(LocalDateTime.of(2022, 6, 1, 19, 06)
                , manager.findEpicByID(epicTaskNum).getEndTime(),
                "Время окончания эпика расчитано неправильно");
    }
}