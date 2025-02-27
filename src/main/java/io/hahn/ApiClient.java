package io.hahn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hahn.comment.dto.Comment;
import io.hahn.comment.dto.CommentPage;
import io.hahn.account.dto.Account;
import io.hahn.account.dto.Login;
import io.hahn.ticket.dto.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON

    private String rememberMeToken; // Store the authentication token

    public Account getAccount() {
        return account;
    }

    private Account account;

    public String getRememberMeToken() {
        return rememberMeToken;
    }

    public ApiClient() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Account login(String username, String password) throws Exception {
        Login loginRequest = new Login().username(username).password(password);
        String json = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login")) // Replace with your URL
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            this.rememberMeToken = response.headers()
                    .allValues("set-cookie")
                    .stream()
                    .filter(cookie -> cookie.contains("remember-me"))
                    .findFirst()
                    .orElseThrow();
            System.out.println("Login successful");
            account = objectMapper.readValue(response.body(), Account.class);
            return account;
        } else {
            throw new Exception("Login failed: " + response.statusCode());
        }
    }

    public TicketPage getTickets(String status, Long id) throws Exception {
        List<String> queryString = new ArrayList<>();
        queryString.add("sort=id,desc");
        if (status != null && !status.isEmpty()) {
            queryString.add("status=" + status);
        }
        if (id != null) {
            queryString.add("id=" + id);
        }

        var uri = "http://localhost:8080/tickets";
        uri += "?" + String.join("&", queryString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri)) // Replace with your URL
                .header("Content-Type", "application/json")
                .header("Cookie", rememberMeToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), TicketPage.class);
        } else {
            throw new Exception("Error getting tickets: " + response.statusCode());
        }
    }

    public Ticket createTicket(TicketCreate dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets")) // Replace with your URL
                .header("Content-Type", "application/json")
                .header("Cookie", rememberMeToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), Ticket.class);
        } else {
            throw new Exception("Ticket creation failed: " + response.statusCode());
        }
    }

    public Ticket getTicketById(Long ticketId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/" + ticketId)) // Replace with your URL
                .header("Content-Type", "application/json")
                .header("Cookie", rememberMeToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Ticket.class);
        } else {
            throw new Exception("Error getting tickets: " + response.statusCode());
        }
    }

    public Ticket updateTicketStatus(Long id, String newStatus) throws Exception {
        String json = objectMapper.writeValueAsString(new UpdateTicketStatusRequest().status(Status.fromValue(newStatus)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/" + id)) // Replace with your URL
                .header("Content-Type", "application/json")
                .header("Cookie", rememberMeToken)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Ticket creation failed: " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), Ticket.class);
    }

    public CommentPage getCommentsByTicketId(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/" + id + "/comments?sort=id,desc")) // Replace with your URL
                .header("Content-Type", "application/json")
                .header("Cookie", rememberMeToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), CommentPage.class);
        } else {
            throw new Exception("Error getting tickets: " + response.statusCode());
        }
    }

    public List<Comment> getCommentHistory(Long ticketId, Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/" + ticketId + "/comments/" + id + "/history")) // Replace with your URL
                .header("Content-Type", "application/json")
                .header("Cookie", rememberMeToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Error getting tickets: " + response.statusCode());
        }
        return Arrays.asList(objectMapper.readValue(response.body(), Comment[].class));
    }

    public Comment publishComment(Long ticketId, Comment comment) throws Exception {
        String json = objectMapper.writeValueAsString(comment);

        String method = "POST";
        String url = "http://localhost:8080/tickets/" + ticketId + "/comments";
        if(comment.getId() != null) {
            url += "/" + comment.getId();
            method = "PATCH";
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // Replace with your URL
                .header("Content-Type", "application/json")
                .header("Cookie", rememberMeToken)
                .method(method, HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (200 <= response.statusCode() && response.statusCode() < 400) {
            return objectMapper.readValue(response.body(), Comment.class);
        }
        throw new Exception("Ticket creation failed: " + response.statusCode());
    }
}