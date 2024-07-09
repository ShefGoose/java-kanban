package manager;

import java.util.*;
import entity.*;

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
        epic.getSubtaskIds().add(subtask.getId());
        subtasks.put(id, subtask);
        updateEpicStatus(epic);
        return id;
    }

    public Epic getEpic(Integer id) {
        return epics.get(id);
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Такого эпика нет");
            return;
        }
        for (Integer subtaskId : epic.getSubtaskIds()) {
            deleteSubtask(subtaskId);
        }
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("Такой подзадачи нет");
            return;
        }
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        Integer subIdInteger = subtask.getId();
        epic.getSubtaskIds().remove(subIdInteger);
        updateEpicStatus(epic);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        ArrayList<Epic> allEpics = getEpics();
        for (Epic epic : allEpics) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }

    }

    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет в списке, обновление невозможно");
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет в списке, обновление невозможно");
            return;
        }
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Такой подзадачи нет в списке, обновление невозможно");
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpic(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    private void updateEpicStatus(Epic epic) {
        HashSet<Status> allSubStatus = new HashSet<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (subtasks.get(subtaskId) == null) {
                return;
            }
            allSubStatus.add(subtasks.get(subtaskId).getStatus());
        }

        if ((allSubStatus.size() == 1) && allSubStatus.contains(Status.valueOf("NEW"))
                || (allSubStatus.isEmpty())) {
            epic.setStatus(Status.valueOf("NEW"));
        } else if ((allSubStatus.size() == 1) && allSubStatus.contains(Status.valueOf("DONE"))) {
            epic.setStatus(Status.valueOf("DONE"));
        } else {
            epic.setStatus(Status.valueOf("IN_PROGRESS"));
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> epicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (epic.getId() == subtasks.get(subtaskId).getEpicId()) {
                epicsSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicsSubtasks;
    }
}
