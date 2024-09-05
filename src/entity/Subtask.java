package entity;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, Status status, String description, int id, int epicId) {
        super(name, status, description, id);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
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
        return "TaskType='" + getTaskType() + '\'' +
                ",name='" + name + '\'' +
                ",epicId='" + epicId + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }
}
