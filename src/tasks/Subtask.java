package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer numberOfEpicTask;

    public Subtask(String taskName, String description, Status progressStatus, Long duration, LocalDateTime startTime, Integer numberOfEpicTask) {
        super(taskName, description, progressStatus, duration, startTime);
        this.numberOfEpicTask = numberOfEpicTask;
    }

    public Integer getNumberOfEpicTask() {
        return numberOfEpicTask;
    }

    @Override
    public String toString() {
        return id +
                "," + Types.SUBTASK +
                "," + taskName +
                "," + progressStatus +
                "," + description +
                "," + duration +
                "," + startTime +
                "," + numberOfEpicTask
                ;
    }
}
