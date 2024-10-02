package entity;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int id, Duration duration,
                   LocalDateTime startTime, int epicId) {
        super(name, description, id, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "TaskType={'" + getTaskType() + '\'' +
                ",name='" + name + '\'' +
                ",epicId='" + epicId + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
