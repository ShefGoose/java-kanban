package utility;

import entity.*;

public class ReformCSV {
    public static String toString(Task task) {
        String[] taskCSVFormat = {String.valueOf(task.getId()),
                String.valueOf(task.getTaskType()),
                task.getName(),
                String.valueOf(task.getStatus()),
                task.getDescription(),
                getEpicId(task)
        };
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
                    Integer.parseInt(taskFromString[0]));
        } else if (taskFromString[1].equals(String.valueOf(TaskType.EPIC))) {
            return new Epic(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]));
        } else {
            return new Subtask(taskFromString[2], Status.valueOf(taskFromString[3]), taskFromString[4],
                    Integer.parseInt(taskFromString[0]), Integer.parseInt(taskFromString[5]));
        }
    }
}
