package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer numberOfEpicTask;

    public Subtask(String taskName, String description, Status progressStatus, Duration duration, LocalDateTime startTime, Integer numberOfEpicTask) {
        super(taskName, description, progressStatus, duration, startTime);
        this.numberOfEpicTask = numberOfEpicTask;
        this.taskType = Types.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(numberOfEpicTask, subtask.numberOfEpicTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numberOfEpicTask);
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
