package managers;

import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTasksManager{
    private final Gson gson;
    private final KVTaskClient client;

    public HTTPTaskManager(int port) {
        this(port, false);
    }

    public HTTPTaskManager(int port, boolean load) {
        super(null);
        gson = new Gson();
        client = new KVTaskClient(port);
        if (load) {
            load();
        }
    }

    protected void load() {
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"),
                new TypeToken<ArrayList<Task>>() {}.getType());
        addTasks(tasks);

        ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"),
                new TypeToken<ArrayList<Subtask>>() {}.getType());
        addTasks(subtasks);

        ArrayList<Epic> epics = gson.fromJson(client.load("epics"),
                new TypeToken<ArrayList<Epic>>() {}.getType());
        addTasks(epics);

        List<Integer> history = gson.fromJson(client.load("history"),
                new TypeToken<ArrayList<Integer>>() {}.getType());

        for (Integer taskId: history) {
            historyManager.add(findEveryTaskByID(taskId));
        }
    }

    protected void addTasks(List<? extends Task> tasks) {
        for (Task task: tasks) {
            final int id = task.getId();
            if (id > numberOfTasks) {
                numberOfTasks = id;
            }
            if (task.getTaskType() == Types.TASK) {
                this.tasks.put(id, task);
                prioritizedTasks.add(task);
            } else if (task.getTaskType() == Types.SUBTASK) {
                this.subtasks.put(id, (Subtask) task);
                prioritizedTasks.add(task);
            } else if (task.getTaskType() == Types.EPIC) {
                this.epics.put(id, (Epic) task);
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);

        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks", jsonSubtasks);

        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics", jsonEpics);

        String jsonHistory =
                gson.toJson(historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }
}
