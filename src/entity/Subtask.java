package entity;

import java.util.Objects;

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
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                "} \n " + super.toString();
    }
}
