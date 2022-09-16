## Это репозиторий проекта "Трекер задач"  
#### Код создан Свирской Анастасией в рамках обучающей программы от Яндекс Практикум.

Наше приложение работает с тремя видами задач:
1. Классическая задача.
2. Большая задача(Эпик). 
3. Подзадачи(входят в эпик).

Приложение написано на *Java*. Программа запускается из файла Main:
```java
public class Main {
    public static void main(String[] args) {
       //код программы
    }
}
```
И включает в себя 13 классов. Из них:
* Класс для демонстрации работы программы
  * Main.java
* 3 класса задач:
    * tasks.Task.java
    * tasks.Subtask.java
    * tasks.Epic.java
* класс-Node
  * managers.ListNode.java
* 4 менеджера:
  * managers.InMemoryTaskManager.java
  * managers.InMemoryHistoryManager.java
  * managers.FileBackedTasksManager.java
  * managers.HTTPTaskManager .java
* Класс для вызова менеджеров:
  * managers.Managers.java
* 2 класса-сервера
  * api.HttpTaskServer.java
  * api.KVServer.java
* класс-клиент
  * api.KVTaskClient.java
* 5 тест-классов
    * tests.EpicTest.java
    * tests.FileBackedTasksManagerTest.java
    * tests.HistoryManagerTest.java
    * tests.HTTPTaskManagerTest.java
    * tests.TaskManagerTest.java

А так же:
* 2 enum:
  * Status.java
  * Types.java
* 2 интерфейса:
  * managers.HistoryManager
  * managers.TaskManager
  
Программа представляет собой бэкенд трекера задач. Управление происходит через класс Managers 
Программа реализует управление задачами и просмотр истории вызовов задач. Кроме того, сохраняет данные в файл или на сервер при 
завершении и восстанавливает данные из файла (сервера по желанию) при запуске.

------
По всем интересующим вопросам обращаться на почту: foreducationYa@ya.ru