package managers;

import java.io.File;

public abstract class Managers {

    public static TaskManager getDefault(String path) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("output.csv"));
        return manager.loadFromFile(new File(path));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
