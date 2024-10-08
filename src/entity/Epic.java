package entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private LocalDateTime endTime;
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, int id, Duration duration, LocalDateTime startTime) {
        super(name, description, id, duration, startTime);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean isEpic() {
        return true;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "TaskType={'" + getTaskType() + '\'' +
                ",name='" + name + '\'' +
                ",subtaskIds='" + subtaskIds + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
