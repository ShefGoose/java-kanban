package utility;

import entity.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReformCSV {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static String toString(Task task) {
        String[] taskCSVFormat;
        if (task.getDuration() == null) {
            taskCSVFormat = new String[]{String.valueOf(task.getId()),
                    String.valueOf(task.getTaskType()),
                    task.getName(),
                    String.valueOf(task.getStatus()),
                    task.getDescription(),
                    null,
                    null,
                    getEpicId(task),
            };
        } else {
            taskCSVFormat = new String[]{String.valueOf(task.getId()),
                    String.valueOf(task.getTaskType()),
                    task.getName(),
                    String.valueOf(task.getStatus()),
                    task.getDescription(),
                    String.valueOf(task.getDuration().toMinutes()),
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
            Task task;
            if (taskFromString[5] == null) {
                task = new Task(taskFromString[2], taskFromString[4],
                        Integer.parseInt(taskFromString[0]), null,
                        null);
            } else {
                task = new Task(taskFromString[2], taskFromString[4],
                        Integer.parseInt(taskFromString[0]), Duration.ofMinutes(Long.parseLong(taskFromString[5])),
                        LocalDateTime.parse(taskFromString[6], DATE_TIME_FORMATTER));
            }
            task.setStatus(Status.valueOf(taskFromString[3]));
            return task;
        } else if (taskFromString[1].equals(String.valueOf(TaskType.EPIC))) {
            Epic epic;
            if (taskFromString[5] == null) {
                epic = new Epic(taskFromString[2], taskFromString[4],
                        Integer.parseInt(taskFromString[0]), null,
                        null);
            } else {
                epic = new Epic(taskFromString[2], taskFromString[4],
                        Integer.parseInt(taskFromString[0]), Duration.ofMinutes(Long.parseLong(taskFromString[5])),
                        LocalDateTime.parse(taskFromString[6], DATE_TIME_FORMATTER));
            }
            epic.setStatus(Status.valueOf(taskFromString[3]));
            return epic;
        } else {
            Subtask subtask;
            if (taskFromString[5] == null) {
                subtask = new Subtask(taskFromString[2], taskFromString[4],
                        Integer.parseInt(taskFromString[0]), null,
                        null, Integer.parseInt(taskFromString[7]));
            } else {
                subtask = new Subtask(taskFromString[2], taskFromString[4],
                        Integer.parseInt(taskFromString[0]), Duration.ofMinutes(Long.parseLong(taskFromString[5])),
                        LocalDateTime.parse(taskFromString[6], DATE_TIME_FORMATTER),
                        Integer.parseInt(taskFromString[7]));
            }
            subtask.setStatus(Status.valueOf(taskFromString[3]));
            return subtask;
        }
    }
}
