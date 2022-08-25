package tasks;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks;

    public Epic(String taskName, String description, Status progressStatus, Duration duration, LocalDateTime startTime) {
        super(taskName, description, progressStatus, duration, startTime);
        this.subtasks = new HashMap<>();
    }

    public boolean isAllSubtasksDone() {
        boolean isDone = true;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getProgressStatus() != Status.DONE) {
                isDone = false;
                break;
            }
        }
        return isDone;
    }

    public boolean isAllSubtasksNew() {
        boolean isNew = true;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getProgressStatus() != Status.NEW) {
                isNew = false;
                break;
            }
        }
        return isNew;
    }

    public HashMap<Integer, Subtask> getsubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
    }

    public void setSubtask(Integer ID, Subtask subtask) {
        subtasks.put(ID, subtask);
    }

    @Override
    public String toString() {
        return id +
                "," + Types.EPIC +
                "," + taskName +
                "," + progressStatus +
                "," + description +
                "," + duration +
                "," + startTime
                ;
    }
}