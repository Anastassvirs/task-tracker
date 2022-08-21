package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    TreeSet<Task> getPrioritizedTasks();

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

     Task findTaskByID(int ID);

     Subtask findSubtaskByID(int ID);

     Epic findEpicByID(int ID);

     Task findEveryTaskByID(int ID);

     Integer addNewTask(Task task);

     Integer addNewSubtask(Subtask subtask);

     Integer addNewEpic(Epic epic);

     Integer updateTask(Task task, Integer ID);

     Integer updateSubtask(Subtask subtask, Integer ID);

     Integer updateEpic(Epic epic, Integer ID);

     void deleteTaskByNum(Integer ID);

     void deleteSubtaskByNum(Integer ID);

     void deleteEpicByNum(Integer ID);

     ArrayList<Subtask> getSubtasksFromEpic(Integer numberOfEpic);

     List<Task> history();
}
