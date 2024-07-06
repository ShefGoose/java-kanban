import java.util.Objects;

public class Task {
    private String name;
    private Status status;
    private String description;
    private int id;

    public Task(String name, Status status, String description, int id) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.id = id;
    }

    public Task(String name, Status status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEpic() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(status, task.status) && Objects.equals(description, task.description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, status, description, id);
    }
}
