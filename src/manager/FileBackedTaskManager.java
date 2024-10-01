package manager;

import entity.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import exception.ManagerSaveException;
import utility.ReformCSV;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void addTask(Task task) {
        if (task.getTaskType().equals(TaskType.EPIC)) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
            subtasks.put(task.getId(), (Subtask) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager returnManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int newGenerateId = 0;
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                Task task = ReformCSV.fromString(line);
                returnManager.addTask(task);
                if (returnManager.getPrioritizedTasks().stream()
                        .noneMatch(priorTask -> returnManager.checkCrossTime(priorTask, task))) {
                    returnManager.prioritizedTasks.add(task);
                }
                if (task.getId() > newGenerateId) {
                    newGenerateId = task.getId();
                    returnManager.generatorId = task.getId();
                }
            }
            if (!returnManager.getSubtasks().isEmpty()) {
                for (Subtask subtask : returnManager.getSubtasks()) {
                    Epic epic = returnManager.epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.getSubtaskIds().add(subtask.getId());
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла!", e);
        }
        return returnManager;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
            for (Task task : getTasks()) {
                writer.write(ReformCSV.toString(task) + "\n");
            }
            for (Task task : getEpics()) {
                writer.write(ReformCSV.toString(task) + "\n");
            }
            for (Task task : getSubtasks()) {
                writer.write(ReformCSV.toString(task) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла!", e);
        }
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}
