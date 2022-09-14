package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.Month.FEBRUARY;

public class InMemoryTaskManager implements TaskManager {
    private static final LocalDateTime EARLIEST_TASK_START_TIME =
            LocalDateTime.of(3000, FEBRUARY, 2, 22, 22);
    private static final LocalDateTime LATEST_TASK_END_TIME =
            LocalDateTime.of(1500, FEBRUARY, 2, 22, 22);

    protected int numberOfTasks;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;
    protected HistoryManager historyManager;
    private TreeSet<Task> prioritizedTasks;


    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        numberOfTasks = 0;
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public void addToPriorityList(Task task) {
        prioritizedTasks.add(task);
    }

    @Override
    public List<Task> getAllTasks() {
        ArrayList<Task> onlyTasks = new ArrayList<>();
        for(Task task : tasks.values()) {
            onlyTasks.add(task);
        }
        return onlyTasks;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        ArrayList<Subtask> onlySubtasks = new ArrayList<>();
        for(Subtask subtask : subtasks.values()) {
            onlySubtasks.add(subtask);
        }
        return onlySubtasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        ArrayList<Epic> onlyEpics = new ArrayList<>();
        for(Epic epic : epics.values()) {
            onlyEpics.add(epic);
        }
        return onlyEpics;
    }

    @Override
    public void deleteAll() {
        deleteAllTasks();
        deleteAllSubtasks();
        deleteAllEpics();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteAllTasks() {
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        prioritizedTasks.removeAll(epics.values());
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public Task findTaskByID(int ID) {
        historyManager.add(tasks.get(ID));
        return this.tasks.get(ID);
    }

    @Override
    public Subtask findSubtaskByID(int ID) {
        historyManager.add(subtasks.get(ID));
        return this.subtasks.get(ID);
    }

    @Override
    public Epic findEpicByID(int ID) {
        historyManager.add(epics.get(ID));
        return this.epics.get(ID);
    }

    @Override
    public Task findEveryTaskByID(int ID) {
        Task task = null;
        if (this.tasks.get(ID) != null) {
            historyManager.add(this.tasks.get(ID));
            task = this.tasks.get(ID);
        } else if (this.subtasks.get(ID) != null) {
            historyManager.add(this.subtasks.get(ID));
            task = this.subtasks.get(ID);
        } else if (this.epics.get(ID) != null) {
            historyManager.add(this.epics.get(ID));
            task = this.epics.get(ID);
        } else {
            throw new NullPointerException();
        }
        return task;
    }

    @Override
    public Integer addNewTask(Task task){
        if (doesTaskOverlap(task)) {
            System.out.println("Невозможно добавить задачу, так как выбранный диапазон уже занят");
            return 0;
        } else {
            numberOfTasks++;
            task.setId(numberOfTasks);
            tasks.put(numberOfTasks, task);
            prioritizedTasks.add(task);
            return task.getId();
        }
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (doesTaskOverlap(subtask)) {
            System.out.println("Невозможно добавить задачу, так как выбранный диапазон уже занят");
            return 0;
        } else {
            subtask.setId(numberOfTasks);
            subtasks.put(numberOfTasks, subtask);
            epics.get(subtask.getNumberOfEpicTask()).addSubtask(subtask);
            numberOfTasks++;

            // При необходимсоти изменяем статус самого эпика и длительность со временем окончания и начала
            updateEpicStatus(epics.get(subtask.getNumberOfEpicTask()));
            updateEpicDurationAndEndTime(epics.get(subtask.getNumberOfEpicTask()));

            prioritizedTasks.add(subtask);

            return subtask.getId();
        }
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        if (doesTaskOverlap(epic)) {
            System.out.println("Невозможно добавить задачу, так как выбранный диапазон уже занят");
            return 0;
        } else {
            epic.setId(numberOfTasks);
            epics.put(epic.getId(), epic);
            numberOfTasks++;
            prioritizedTasks.add(epic);
            return epic.getId();
        }
    }

    @Override
    public Integer updateTask(Task task, Integer ID) {
        task.setId(ID);
        prioritizedTasks.remove(tasks.get(ID));
        prioritizedTasks.add(task);
        tasks.put(ID, task);
        return task.getId();
    }

    @Override
    public Integer updateSubtask(Subtask subtask, Integer ID) {
        subtask.setId(ID);
        prioritizedTasks.remove(subtasks.get(ID));
        prioritizedTasks.add(subtask);
        subtasks.put(ID, subtask);
        return subtask.getId();
    }

    @Override
    public Integer updateEpic(Epic epic, Integer ID) {
        epic.setId(ID);
        prioritizedTasks.remove(epics.get(ID));
        prioritizedTasks.add(epic);
        epics.put(ID, epic);
        return epic.getId();
    }


    @Override
    public void deleteTaskByNum(Integer ID) {
        try {
            if (historyManager.getHistory().contains(tasks.get(ID))) {
                historyManager.remove(ID);
            }
            prioritizedTasks.remove(tasks.remove(ID));
        } catch (NullPointerException ignored){
            System.out.println("Задача для удаления не была найдена");
        }
    }

    @Override
    public void deleteSubtaskByNum(Integer ID) {
        try {
            if (historyManager.getHistory().contains(subtasks.get(ID))) {
                historyManager.remove(ID);
            }
            epics.get(subtasks.get(ID).getNumberOfEpicTask()).deleteSubtask(ID);
            prioritizedTasks.remove(subtasks.remove(ID));
        } catch (NullPointerException ignored){
            System.out.println("Подзадача для удаления не была найдена");
        }
    }

    @Override
    public void deleteEpicByNum(Integer ID) {
        try {
            for (Subtask subtask : getSubtasksFromEpic(ID)) {
                deleteSubtaskByNum(subtask.getId());
            }
            if (historyManager.getHistory().contains(epics.get(ID))) {
                historyManager.remove(ID);
            }
            prioritizedTasks.remove(epics.remove(ID));
        } catch (NullPointerException ignored){
            System.out.println("Эпик для удаления не был найден");
        }
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(Integer numberOfEpic) {
        Epic epic = epics.get(numberOfEpic);
        ArrayList<Subtask> subtasks = new ArrayList<>();

        for (Subtask subtask : epic.getsubtasks().values()) {
            subtasks.add(subtask);
        }
        return subtasks;
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    public String getNumbsFromHistory() {
        return historyManager.toString();
    }

    // Проводится проверка на необходимость изменения статуса эпика с возможностью изменения
    public void updateEpicStatus(Epic epic) {
        Status status = Status.IN_PROGRESS;

        if (epic.isAllSubtasksDone()) {
            status = Status.DONE;
        } else if (epic.getsubtasks().isEmpty() || epic.isAllSubtasksNew()) {
            status = Status.NEW;
        }
        Epic newEpic = new Epic(epic.getTaskName(), epic.getDescription(), status, epic.getDuration(), epic.getStartTime());
        newEpic.setId(epic.getId());
        for (Subtask subtask : epic.getsubtasks().values()) {
            newEpic.addSubtask(subtask);
        }
        prioritizedTasks.remove(epic);
        prioritizedTasks.add(newEpic);
        epics.put(epic.getId(), newEpic);
    }

    public void updateEpicDurationAndEndTime(Epic epic) {
        LocalDateTime mostEarlyTaskStart = EARLIEST_TASK_START_TIME;
        LocalDateTime mostLateTaskEnd = LATEST_TASK_END_TIME;
        Duration newDuration = Duration.ZERO;
        Epic newEpic = new Epic(epic.getTaskName(), epic.getDescription(), epic.getProgressStatus(), epic.getDuration(),
                epic.getStartTime());
        newEpic.setId(epic.getId());
        for (Subtask subtask : epic.getsubtasks().values()) {
            if (subtask.getStartTime().isBefore(mostEarlyTaskStart)) {
                mostEarlyTaskStart = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(mostLateTaskEnd)) {
                mostLateTaskEnd = subtask.getEndTime();
            }
            newEpic.addSubtask(subtask);
            newDuration = newDuration.plus(subtask.getDuration());
        }
        newEpic.setDuration(newDuration);
        newEpic.setStartTime(mostEarlyTaskStart);
        newEpic.setEndTime(mostLateTaskEnd);
        prioritizedTasks.remove(epic);
        prioritizedTasks.add(newEpic);
        epics.put(epic.getId(), newEpic);
    }

    private boolean doesTaskOverlap(Task task) {
        boolean doesOverlap = false;
        for (Task existedTask : prioritizedTasks) {
            if (existedTask.getStartTime().isBefore(task.getStartTime())
                    && existedTask.getEndTime().isAfter(task.getStartTime())
                    || existedTask.getStartTime().isBefore(task.getEndTime())
                    && existedTask.getEndTime().isAfter(task.getEndTime())
                    || existedTask.getStartTime().equals(task.getStartTime())) {
                doesOverlap = true;
                return doesOverlap;
            }
        }
        return doesOverlap;
    }
}
