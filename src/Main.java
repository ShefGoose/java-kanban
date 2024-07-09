
import manager.TaskManager;
import entity.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Добавление новых задач

        Task task1 = new Task("name1", "description1");
        final int task1Id = manager.addNewTask(task1);

        Task task2 = new Task("name2", "description2");
        final int task2Id = manager.addNewTask(task2);

        Epic epic1 = new Epic("epicName1", "descriptionEpic1");
        final int epic1Id = manager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("sub1", "descrnSub1", epic1Id);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1);


        Epic epic2 = new Epic("epicName2", "descriptionEpic2");
        final int epic2Id = manager.addNewEpic(epic2);


        Subtask subtask2 = new Subtask("sub2", "descrnSub2", epic2Id);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2);
        Subtask subtask2_2 = new Subtask("sub2_2", "descrnSub2_2",
                epic2Id);
        final Integer subtaskId2_2 = manager.addNewSubtask(subtask2_2);
        // Печать списков задач
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getEpicSubtasks(epic2));
        System.out.println("-".repeat(20));

        //Изменение статусов задач
        Task task1Update = new Task("OtherName1", Status.valueOf("DONE"), "description1", task1Id);
        manager.updateTask(task1Update);
        Subtask subtask1Update = new Subtask("sub1", Status.valueOf("IN_PROGRESS"),
                "OtherDescrnSub1", subtaskId1, epic1Id);
        manager.updateSubtask(subtask1Update);
        Subtask subtask2_2Update = new Subtask("sub2_2", Status.valueOf("DONE"), "descrnSub2_2",
                subtaskId2_2, epic2Id);
        manager.updateSubtask(subtask2_2Update);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println("-".repeat(20));

        //Удаление задач
        manager.deleteTask(task2Id);
        manager.deleteEpic(epic1Id);
        manager.deleteSubtask(subtaskId2);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println("-".repeat(20));
        manager.deleteSubtasks();
        System.out.println(manager.getEpics());
    }
}
