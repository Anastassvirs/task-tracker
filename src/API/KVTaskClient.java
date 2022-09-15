package API;

import exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String APIToken;

    public KVTaskClient(int port) {
        url = "http://localhost:" + port + "/";
        APIToken = register(url);
    }

    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка в запросе на сохранение");
            }
            return response.body().toString();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка в запросе на сохранение");
        }
    }

    public void put(String key, String value) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + APIToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка в запросе на сохранение");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка в запросе на сохранение");
        }
    }

    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + APIToken))
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка в запросе на сохранение");
            }
            return response.body().toString();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка в запросе на сохранение");
        }
    }
}
