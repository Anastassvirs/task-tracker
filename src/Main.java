import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws Exception {
        TaskManager manager = Managers.getDefault("input.csv");
        System.out.println('\n' + "Список обычных задач: " + manager.getAllTasks());
        System.out.println("Список эпиков: " + manager.getAllEpics());
        System.out.println("Список подзадач: " + manager.getAllSubtasks());
        Task newTask = new Task("task1", "strong and serious", Status.NEW, (long) 20,
                LocalDateTime.of(2022, 1, 1, 0, 0));
        Integer taskNum1 = manager.addNewTask(newTask);
        newTask = new Task("taskie daski", "funny and nice", Status.NEW, (long) 20,
                LocalDateTime.of(2022, 2, 1, 0, 0));
        Integer taskNum2 = manager.addNewTask(newTask);
        // Три подзадачи + эпик
        Epic newEpic = new Epic("ep1", "usual epic you know", Status.NEW, (long) 20,
                LocalDateTime.of(2022, 3, 1, 0, 0));
        Integer epicTaskNum1 = manager.addNewEpic(newEpic);
        Subtask newSubtask = new Subtask("sub1", "little subbie", Status.NEW, (long) 20,
                LocalDateTime.of(2022, 4, 1, 0, 0), epicTaskNum1);
        Integer subtask1 = manager.addNewSubtask(newSubtask);
        newSubtask = new Subtask("sub2", "subsub", Status.NEW, (long) 20,
                LocalDateTime.of(2022, 5, 1, 0, 0), epicTaskNum1);
        Integer subtask2 = manager.addNewSubtask(newSubtask);
        newSubtask = new Subtask("sub3", "subsubsub", Status.NEW, (long) 20,
                LocalDateTime.of(2022, 6, 1, 0, 0), epicTaskNum1);
        Integer subtask3 = manager.addNewSubtask(newSubtask);
        // Пустой эпик
        newEpic = new Epic("ep2", "cool epic doesn't need subtasks", Status.NEW, (long) 20,
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

        manager = Managers.getDefault("output.csv");
        System.out.println('\n' + "Список обычных задач: " + manager.getAllTasks());
        System.out.println("Список эпиков: " + manager.getAllEpics());
        System.out.println("Список подзадач: " + manager.getAllSubtasks());
    }
}
