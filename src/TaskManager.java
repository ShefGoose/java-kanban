import java.util.*;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;

    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public Integer addNewSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика нет");
            return -1;
        }
        final int id = ++generatorId;
        subtask.setId(id);
        epic.subtaskIds.add(subtask.getId());
        subtasks.put(id, subtask);
        updateEpicStatus(epic);
        return id;
    }

    public Epic getEpic(Integer id) {
        Epic epic = null;
        if (epics.containsKey(id)) {
            epic = epics.get(id);
        } else {
            System.out.println("Такого эпика нет");
        }
        return epic;
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    void deleteTask(int id) {
        tasks.remove(id);
    }

    void deleteEpic(int id) {
        Epic epic = getEpic(id);
        for (int i = 0; i < epic.subtaskIds.size(); i++) {
            int removeSubId = epic.subtaskIds.get(i);
            deleteSubtask(removeSubId);
        }
        epics.remove(id);
    }

    void deleteSubtask(int id) {
        Subtask subtask = getSubtask(id);
        Epic epic = getEpic(subtask.getEpicId());
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    void deleteTasks() {
        tasks.clear();
    }

    void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    void deleteSubtasks() {
        subtasks.clear();
    }

    void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    void updateEpic(Epic epic) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                epic.subtaskIds.add(subtask.getId());
            }
        }
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpic(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    void updateEpicStatus(Epic epic) {
        HashSet<Status> allSubStatus = new HashSet<>();
        for (Subtask subtask : subtasks.values()) {
            if (epic.getId() == subtask.getEpicId()) allSubStatus.add(subtask.getStatus());
        }

        if ((allSubStatus.size() == 1) && allSubStatus.contains(Status.valueOf("NEW")) || (allSubStatus.isEmpty())) {
            epic.setStatus(Status.valueOf("NEW"));
        } else if ((allSubStatus.size() == 1) && allSubStatus.contains(Status.valueOf("DONE"))) {
            epic.setStatus(Status.valueOf("DONE"));
        } else {
            epic.setStatus(Status.valueOf("IN_PROGRESS"));
        }
    }

    ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    ArrayList<Subtask> getSubtasks() {
        if (subtasks.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(subtasks.values());
    }

    ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> epicsSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (epic.getId() == subtask.getEpicId()) {
                epicsSubtasks.add(subtask);
            }
        }
        return epicsSubtasks;
    }
}
