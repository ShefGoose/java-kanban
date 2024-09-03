package entity;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, Status status, String description, int id) {
        super(name, status, description, id);
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public boolean isEpic() {
        return true;
    }

    @Override
    public String toString() {
        return "TaskType='" + taskType + '\'' +
                ",name='" + name + '\'' +
                ",subtaskIds='" + subtaskIds + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }
}
