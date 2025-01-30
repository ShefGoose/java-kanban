import com.google.gson.Gson;
import entity.Epic;
import entity.Subtask;
import entity.Task;
import handler.BaseHttpHandler;
import manager.Managers;
import manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
       TaskManager man = Managers.getDefault();
        Task task1 = new Task( "name", "d", Duration.ofMinutes(10), LocalDateTime.of(2025, 1, 27, 10,0));
        int task1Id = man.addNewTask(task1);
        Gson gson = Managers.getGson();
//        String taska = gson.toJson(task1);
//        System.out.println(taska);
//        Task fromaTask = gson.fromJson(taska, Task.class);
//        System.out.println(fromaTask);
//        String statusTaska = new String("{\"name\":\"NAME\",\"description\":\"d\",\"id\":1,\"duration\":null,\"startTime\":null}");
//        Task stat = gson.fromJson(statusTaska, Task.class);
//        System.out.println(stat);
        Epic epic1 = new Epic("name", "d");
       int epic1Id = man.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("EE", "DDS", Duration.ofMinutes(20), LocalDateTime.of(2025,1,27,12,0), 2);
        int subId1 = man.addNewSubtask(subtask1);
        System.out.println(man.getPrioritizedTasks());


    }
}
