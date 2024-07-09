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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }


    @Override
    public String toString() {
        return "objects.Subtask{" +
                "epicId=" + epicId +
                "} \n " + super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
