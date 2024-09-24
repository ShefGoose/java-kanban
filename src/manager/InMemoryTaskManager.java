package manager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import entity.*;
import exception.ManagerValidateException;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected int generatorId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        try {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, task))) {
                throw new ManagerValidateException("Задача " + task.getName() + " пересекается с уже существующей");
            }
        } catch (ManagerValidateException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        try {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, epic))) {
                throw new ManagerValidateException("Задача " + epic.getName() + " пересекается с уже существующей");
            }
        } catch (ManagerValidateException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        prioritizedTasks.add(epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика нет");
            return -1;
        }
        try {
            if (getPrioritizedTasks().stream().anyMatch(priorTask -> checkCrossTime(priorTask, subtask))) {
                throw new ManagerValidateException("Задача " + subtask.getName() + " пересекается с уже существующей");
            }
        } catch (ManagerValidateException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        final int id = ++generatorId;
        subtask.setId(id);
        epic.getSubtaskIds().add(subtask.getId());
        subtasks.put(id, subtask);
        updateEpicStatus(epic);
        updateEpicDuration(epic);
        updateEpicStartTime(epic);
        updateEpicEndTime(epic);
        prioritizedTasks.add(subtask);
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
        prioritizedTasks.remove(epics.get(id));
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
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        Integer subIdInteger = subtask.getId();
        epic.getSubtaskIds().remove(subIdInteger);
        updateEpicStatus(epic);
        updateEpicDuration(epic);
        updateEpicStartTime(epic);
        updateEpicEndTime(epic);
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
            prioritizedTasks.remove(epic);
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
            updateEpicDuration(epic);
            updateEpicStartTime(epic);
            updateEpicEndTime(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет в списке, обновление невозможно");
            return;
        }
        prioritizedTasks.removeIf(taskPriority -> taskPriority.getId() == task.getId());
        prioritizedTasks.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет в списке, обновление невозможно");
            return;
        }
        updateEpicStatus(epic);
        updateEpicDuration(epic);
        updateEpicStartTime(epic);
        updateEpicEndTime(epic);
        epics.put(epic.getId(), epic);
        prioritizedTasks.removeIf(taskPriority -> taskPriority.getId() == epic.getId());
        prioritizedTasks.add(epic);
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
        updateEpicDuration(epic);
        updateEpicStartTime(epic);
        updateEpicEndTime(epic);
        prioritizedTasks.removeIf(taskPriority -> taskPriority.getId() == subtask.getId());
        prioritizedTasks.add(subtask);
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

    private void updateEpicDuration(Epic epic) {
        Duration newEpicDuration = Duration.ofMinutes(0);
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setDuration(null);
            return;
        }
        for (Integer subtaskId : epic.getSubtaskIds()) {
            newEpicDuration = newEpicDuration.plus(subtasks.get(subtaskId).getDuration());
        }
        epic.setDuration(newEpicDuration);
    }

    private void updateEpicStartTime(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStartTime(null);
            return;
        }
        epic.setStartTime(subtasks.get((epic.getSubtaskIds().getFirst())).getStartTime());
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (subtasks.get(subtaskId).getStartTime() != null
                    && subtasks.get(subtaskId).getStartTime().isBefore(epic.getStartTime())) {
                epic.setStartTime(subtasks.get(subtaskId).getStartTime());
            }
        }
    }

    private void updateEpicEndTime(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setEndTime(null);
            return;
        }
        epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (subtasks.get(subtaskId).getStartTime() != null
                    && subtasks.get(subtaskId).getStartTime().isAfter(epic.getEndTime())) {
                epic.setEndTime(subtasks.get(subtaskId).getStartTime().plus(subtasks.get(subtaskId).getDuration()));
            }
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

    private boolean checkCrossTime(Task task1, Task task2) {
        if (task2.getStartTime() == null || task1.getStartTime() == null
                || task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return !(task1.getStartTime().isAfter(task2.getEndTime()) || task1.getEndTime().isBefore(task2.getStartTime()));
    }

}

