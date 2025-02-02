package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    Epic getEpic(Integer id);

    Epic getEpicNotHistory(Integer id);

    Task getTask(Integer id);

    Subtask getSubtask(Integer id);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    Set<Integer> getTaskIdsList();

    Set<Integer> getEpicIdsList();

    Set<Integer> getSubtaskIdsList();

}
