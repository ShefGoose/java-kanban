package utility;

import entity.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReformCSV {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    private static final int secondsInMinute = 60;

    public static String toString(Task task) {
        String[] taskCSVFormat;
        if (task.getDuration() == null && task.getStartTime() == null) {
             taskCSVFormat = new String[]{String.valueOf(task.getId()),
                     String.valueOf(task.getTaskType()),
                     task.getName(),
                     String.valueOf(task.getStatus()),
                     task.getDescription(),
                     null,
                     null,
                     getEpicId(task),
             };
        } else if (task.getDuration() == null) {
             taskCSVFormat = new String[]{String.valueOf(task.getId()),
                     String.valueOf(task.getTaskType()),
                     task.getName(),
                     String.valueOf(task.getStatus()),
                     task.getDescription(),
                     null,
                     task.getStartTime().format(DATE_TIME_FORMATTER),
                     getEpicId(task),
             };
        } else if (task.getStartTime() == null) {
            taskCSVFormat = new String[]{String.valueOf(task.getId()),
                    String.valueOf(task.getTaskType()),
                    task.getName(),
                    String.valueOf(task.getStatus()),
                    task.getDescription(),
                    String.valueOf(task.getDuration().getSeconds() / secondsInMinute),
                    null,
                    getEpicId(task),
            };
        } else {
             taskCSVFormat = new String[]{String.valueOf(task.getId()),
                     String.valueOf(task.getTaskType()),
                     task.getName(),
                     String.valueOf(task.getStatus()),
                     task.getDescription(),
                     String.valueOf(task.getDuration().getSeconds() / secondsInMinute),
                     task.getStartTime().format(DATE_TIME_FORMATTER),
                     getEpicId(task),
             };
        }
        return String.join(",", taskCSVFormat);
    }

    private static String getEpicId(Task task) {
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            return String.valueOf(((Subtask) task).getEpicId());
        }
        return " ";
    }

    public static Task fromString(String value) {
        String[] taskFromString = value.split(",");
        if (taskFromString[1].equals(String.valueOf(TaskType.TASK))) {
            return new Task(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]), Duration.ofMinutes(Long.parseLong(taskFromString[5])),
                    LocalDateTime.parse(taskFromString[6], DATE_TIME_FORMATTER));
        } else if (taskFromString[1].equals(String.valueOf(TaskType.EPIC))) {
            return new Epic(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]), Duration.ofMinutes(Long.parseLong(taskFromString[5])),
                    LocalDateTime.parse(taskFromString[6], DATE_TIME_FORMATTER));
        } else {
            return new Subtask(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]), Duration.ofMinutes(Long.parseLong(taskFromString[5])),
                    LocalDateTime.parse(taskFromString[6], DATE_TIME_FORMATTER), Integer.parseInt(taskFromString[5]));
        }
    }
}
