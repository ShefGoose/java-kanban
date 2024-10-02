package manager;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import entity.*;
import exception.ManagerValidateException;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int generatorId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, task))) {
                throw new ManagerValidateException("Задача " + task.getName() + " пересекается с уже существующей");
            } else {
                prioritizedTasks.add(task);
            }
        }
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        if (epic.getStartTime() != null) {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, epic))) {
                throw new ManagerValidateException("Задача " + epic.getName() + " пересекается с уже существующей");
            }
        }
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика нет");
            return -1;
        }
        final int id = ++generatorId;
        subtask.setId(id);
        epic.getSubtaskIds().add(subtask.getId());
        subtasks.put(id, subtask);
        if (subtask.getStartTime() != null) {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, subtask))) {
                throw new ManagerValidateException("Задача " + subtask.getName() + " пересекается с уже существующей");
            } else {
                prioritizedTasks.add(subtask);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
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
        prioritizedTasks.remove(tasks.get(id));
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
            prioritizedTasks.remove(subtasks.get(subtaskId));
            deleteSubtask(subtaskId);
            historyManager.remove(subtaskId);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        prioritizedTasks.remove(subtasks.get(id));
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("Такой подзадачи нет");
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        Integer subIdInteger = subtask.getId();
        epic.getSubtaskIds().remove(subIdInteger);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        historyManager.remove(id);
    }

    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        ArrayList<Epic> allEpics = getEpics();
        for (Epic epic : allEpics) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет в списке, обновление невозможно");
            return;
        }
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, task))) {
                throw new ManagerValidateException("Задача " + task.getName() + " пересекается с уже существующей");
            } else {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет в списке, обновление невозможно");
            return;
        }
        updateEpicStatus(epic);
        updateEpicTime(epic);
        epics.put(epic.getId(), epic);
        if (epic.getStartTime() != null) {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, epic))) {
                throw new ManagerValidateException("Задача " + epic.getName() + " пересекается с уже существующей");
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Такой подзадачи нет в списке, обновление невозможно");
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, subtask))) {
                throw new ManagerValidateException("Задача " + subtask.getName() + " пересекается с уже существующей");
            } else {
                prioritizedTasks.add(subtask);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
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

    private void updateEpicTime(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        Duration newEpicDuration = Duration.ofMinutes(0);
        List<Subtask> subtasksStartTimeNotNull = getSubtasks().stream()
                .filter(subtask -> subtask.getStartTime() != null).toList();
        List<Subtask> subtasksEndTimeNotNull = getSubtasks().stream()
                .filter(subtask -> subtask.getEndTime() != null).toList();

        if (subtasksStartTimeNotNull.isEmpty()) {
            return;
        }

        epic.setStartTime(subtasksStartTimeNotNull.getFirst().getStartTime());
        epic.setEndTime(subtasksEndTimeNotNull.getFirst().getEndTime());
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (subtasks.get(subtaskId).getDuration() != null) {
                newEpicDuration = newEpicDuration.plus(subtasks.get(subtaskId).getDuration());
            }
            if (subtasks.get(subtaskId).getStartTime() != null
                    && subtasks.get(subtaskId).getStartTime().isBefore(epic.getStartTime())) {
                epic.setStartTime(subtasks.get(subtaskId).getStartTime());
            }
            if (subtasks.get(subtaskId).getEndTime() != null
                    && subtasks.get(subtaskId).getEndTime().isAfter(epic.getEndTime())) {
                epic.setEndTime(subtasks.get(subtaskId).getEndTime());
            }
        }
        epic.setDuration(newEpicDuration);
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
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epic.getId())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected boolean checkCrossTime(Task task1, Task task2) {
        return !(task1.getStartTime().isAfter(task2.getEndTime()) || task1.getEndTime().isBefore(task2.getStartTime()));
    }
}

