package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task findTaskByID(int ID) {
        Task task = super.findTaskByID(ID);
        save();
        return task;
    }

    @Override
    public Subtask findSubtaskByID(int ID) {
        Subtask subtask = super.findSubtaskByID(ID);
        save();
        return subtask;
    }

    @Override
    public Epic findEpicByID(int ID) {
        Epic epic = super.findEpicByID(ID);
        save();
        return epic;
    }

    @Override
    public Task findEveryTaskByID(int ID) {
        Task task = super.findEveryTaskByID(ID);
        save();
        return task;
    }

    public Integer addOldTask(Task task) {
        tasks.put(task.getId(), task);
        if (task.getId() > numberOfTasks) {
            numberOfTasks = task.getId() + 1;
        }
        this.addToPriorityList(task);
        return task.getId();
    }

    public Integer addOldSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getId() > numberOfTasks) {
            numberOfTasks = subtask.getId() + 1;
        }
        epics.get(subtask.getNumberOfEpicTask()).addSubtask(subtask);
        updateEpicStatus(epics.get(subtask.getNumberOfEpicTask()));
        updateEpicDurationAndEndTime(epics.get(subtask.getNumberOfEpicTask()));
        this.addToPriorityList(subtask);
        return subtask.getId();
    }

    public Integer addOldEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        if (epic.getId() > numberOfTasks) {
            numberOfTasks = epic.getId() + 1;
        }
        this.addToPriorityList(epic);
        return epic.getId();
    }

    @Override
    public void deleteTaskByNum(Integer ID) {
        super.deleteTaskByNum(ID);
        save();
    }

    @Override
    public void deleteSubtaskByNum(Integer ID) {
        super.deleteSubtaskByNum(ID);
        save();
    }

    @Override
    public void deleteEpicByNum(Integer ID) {
        super.deleteEpicByNum(ID);
        save();
    }

    protected void save() {
        try (FileWriter fileWriter = new FileWriter(file.toString());) {
            fileWriter.write("id,type,name,status,description,duration,startTime,epic\n");
            for (Task item : getAllTasks()) {
                fileWriter.write(item.toString() + '\n');
            }
            for (Epic item : getAllEpics()) {
                fileWriter.write(item.toString() + '\n');
            }
            for (Subtask item : getAllSubtasks()) {
                fileWriter.write(item.toString() + '\n');
            }
            fileWriter.write("\n");
            fileWriter.write(getNumbsFromHistory());
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла: " + e.getMessage());
        }
    }

    public FileBackedTasksManager loadFromFile(File file) {
        try {
            FileBackedTasksManager newManager = new FileBackedTasksManager(new File("src\\resources\\output.csv"));
            String recoveryFile = Files.readString(Path.of(file.toString()));
            String[] split = recoveryFile.split("\n");
            for (int i = 1; i < split.length - 2; i++) {
                String line = split[i];
                String[] splitLine = line.split(",");
                Status status = Status.valueOf(splitLine[3]);
                Types type = Types.valueOf(splitLine[1]);

                switch (type) {
                    case TASK:
                        Task task = new Task(splitLine[2], splitLine[4].replaceAll("(\\r|\\n)", ""),
                                status, Duration.of(Long.parseLong(splitLine[5]), ChronoUnit.MINUTES),
                                LocalDateTime.parse(splitLine[6].replaceAll("(\\r|\\n)", "")));
                        task.setId(Integer.parseInt(splitLine[0]));
                        newManager.addOldTask(task);
                        break;
                    case EPIC:
                        Epic epic = new Epic(splitLine[2], splitLine[4].replaceAll("(\\r|\\n)", ""),
                                status, Duration.of(Long.parseLong(splitLine[5]), ChronoUnit.MINUTES),
                                LocalDateTime.parse(splitLine[6].replaceAll("(\\r|\\n)", "")));
                        epic.setId(Integer.parseInt(splitLine[0]));
                        newManager.addOldEpic(epic);
                        break;
                    default:
                        Subtask subtask = new Subtask(splitLine[2], splitLine[4], status,
                                Duration.of(Long.parseLong(splitLine[5]), ChronoUnit.MINUTES),
                                LocalDateTime.parse(splitLine[6].replaceAll("(\\r|\\n)", "")),
                                Integer.parseInt(splitLine[7].replaceAll("(\\r|\\n)", "")));
                        subtask.setId(Integer.parseInt(splitLine[0]));
                        newManager.addOldSubtask(subtask);
                }
            }
            String line = split[split.length - 1];
            if (getHistoryFromString(line) != null) {
                List<Integer> historyList = getHistoryFromString(line);
                for (Integer item : historyList) {
                    newManager.findEveryTaskByID(item);
                }
            }
            return newManager;
        } catch (IOException e){
            throw new ManagerSaveException("Произошла ошибка во время сохранения файла: " + e.getMessage());
        }
    }

    private static List<Integer> getHistoryFromString(String value) {
        List<Integer> newList = new ArrayList<>();
        if (value != "" && value != "\n" && !value.isEmpty()) {
            String[] split = value.split(",");
            if (split.length > 0) {
                for (String item : split) {
                    try {
                        newList.add(Integer.parseInt(item.replaceAll("(\\r|\\n)", "")));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return newList;
    }
}