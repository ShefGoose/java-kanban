import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, Status status, String description, int id) {
        super(name, status, description, id);
    }

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    @Override
    public boolean isEpic() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                "} \n " + super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}
