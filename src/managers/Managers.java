package managers;

import java.io.File;

public abstract class Managers {

    public static TaskManager getDefault(String path) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("src\\resources\\output.csv"));
        return manager.loadFromFile(new File(path));
    }

    public static HTTPTaskManager getDefault() {
        HTTPTaskManager manager = new HTTPTaskManager(8078, false);
        return manager;
    }

    public static HTTPTaskManager getDefaultWithLoad() {
        HTTPTaskManager manager = new HTTPTaskManager(8078, true);
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
