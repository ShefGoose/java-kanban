package manager;

import java.util.*;

import entity.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
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

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Такого эпика нет");
            return;
        }
        for (Integer subtaskId : epic.getSubtaskIds()) {
            deleteSubtask(subtaskId);
            historyManager.remove(subtaskId);
        }
        historyManager.remove(id);
    }

    @Override
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
        historyManager.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        ArrayList<Epic> allEpics = getEpics();
        for (Epic epic : allEpics) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }

    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет в списке, обновление невозможно");
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет в списке, обновление невозможно");
            return;
        }
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
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

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        if (!epics.containsValue(epic)) {
            return new ArrayList<>();
        }
        ArrayList<Subtask> epicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            epicsSubtasks.add(subtasks.get(subtaskId));
        }
        return epicsSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
