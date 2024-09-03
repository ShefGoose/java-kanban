package manager;

import entity.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import exception.ManagerSaveException;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected String toString(Task task) {
        String[] taskCSVFormat = {String.valueOf(task.getId()),
                String.valueOf(task.getTaskType()),
                task.getName(),
                String.valueOf(task.getStatus()),
                task.getDescription(),
                getEpicId(task)
        };
        return String.join(",", taskCSVFormat);
    }

    protected String getEpicId(Task task) {
        if (task instanceof Subtask) {
            return String.valueOf(((Subtask) task).getEpicId());
        }
        return " ";
    }

    protected static Task fromString(String value) {
        String[] taskFromString = value.split(",");
        if (taskFromString[1].equals("TASK")) {
            return new Task(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]));
        } else if (taskFromString[1].equals("EPIC")) {
            return new Epic(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]));
        } else {
            return new Subtask(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]), Integer.parseInt(taskFromString[5]));
        }
    }

    private void addTask(Task task) {
        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            subtasks.put(task.getId(), (Subtask) task);
        } else if (task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Это не задача!");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager returnManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int newGenerateId = 0;
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                Task task = fromString(line);
                returnManager.addTask(task);
                if (task.getId() > newGenerateId) {
                    newGenerateId = task.getId();
                    returnManager.generatorId = task.getId();
                }
            }
            if (!returnManager.getSubtasks().isEmpty()) {
                for (Subtask subtask : returnManager.getSubtasks()) {
                    Epic epic = returnManager.epics.get(subtask.getEpicId());
                    epic.getSubtaskIds().add(subtask.getId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла!", e);
        }
        return returnManager;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Task task : getEpics()) {
                writer.write(toString(task) + "\n");
            }
            for (Task task : getSubtasks()) {
                writer.write(toString(task) + "\n");
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
