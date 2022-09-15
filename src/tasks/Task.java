package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task>{
    protected String taskName;
    protected String description;
    protected Integer id;
    protected Status progressStatus; // Статус задачи (Новая / В процессе / Выполнена)
    protected Duration duration; // Продолжительность задачи, оценка того, сколько времени она займёт в минутах
    protected LocalDateTime startTime; // Дата, когда предполагается приступить к выполнению задачи
    protected LocalDateTime endTime; // Время завершения задачи, которое рассчитывается исходя из startTime и duration
    protected Types taskType;

    public Task(String taskName, String description, Status progressStatus, Duration duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.progressStatus = progressStatus;
        this.duration = duration;
        this.startTime = startTime;
        this.taskType = Types.TASK;
        calculateEndTime();
    }

    public Types getTaskType() {
        return taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getProgressStatus() {
        return progressStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setProgressStatus(Status progressStatus) {
        this.progressStatus = progressStatus;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    private void calculateEndTime() {
        endTime = startTime.plus(duration);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskName, task.taskName)
                && Objects.equals(description, task.description)
                && Objects.equals(id, task.id)
                && progressStatus == task.progressStatus
                && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, id, progressStatus, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return id +
                "," + Types.TASK +
                "," + taskName +
                "," + progressStatus +
                "," + description +
                "," + duration +
                "," + startTime
                ;
    }

    @Override
    public int compareTo(Task o) {
        if (startTime.isAfter(o.startTime)) {
            return 1;
        } else {
            return -1;
        }
    }
}
