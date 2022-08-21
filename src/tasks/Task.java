package tasks;

import java.time.LocalDateTime;

// Класс, описывающий все задачи
public class Task implements Comparable<Task>{
    protected String taskName; // Имя задачи
    protected String description; // Описание задачи
    protected Integer id; // Уникальный номер задачи
    protected Status progressStatus; // Статус задачи (Новая / В процессе / Выполнена)
    protected Long duration; // Продолжительность задачи, оценка того, сколько времени она займёт в минутах
    protected LocalDateTime startTime; // Дата, когда предполагается приступить к выполнению задачи
    protected LocalDateTime endTime; // Время завершения задачи, которое рассчитывается исходя из startTime и duration

    public Task(String taskName, String description, Status progressStatus, Long duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.progressStatus = progressStatus;
        this.duration = duration;
        this.startTime = startTime;
        calculateEndTime();
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    private void calculateEndTime() {
        endTime = startTime.plusMinutes(duration);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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
