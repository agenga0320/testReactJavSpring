package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Message;
import com.example.demo.service.MessageService;


@RestController
@CrossOrigin(origins = "http://localhost:3000") // React 側からのアクセスを許可
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/api/get-message")
    public List<Message> getMessage(@RequestParam String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return messageService.getMessageByDate(date);
    }

    @PostMapping("/api/add-message")
    public ResponseEntity<Map<String, String>> receiveMessage(@RequestBody Map<String, String> payload) {
        Map<String, String> response = new HashMap<>();
        String startTime = payload.get("startTime");
        if (startTime == null || startTime.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Start time is required.");
            return ResponseEntity.badRequest().body(response);
        }

        String endTime = payload.get("endTime");
        if (endTime == null || endTime.isEmpty()) {
            response.put("status", "error");
            response.put("message", "End time is required.");
            return ResponseEntity.badRequest().body(response);
        }

        String startDate = payload.get("startDate");
        if (startDate == null || startDate.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Start date is required.");
            return ResponseEntity.badRequest().body(response);
        }

        String title = payload.get("title");
        if (title == null || title.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Title is required.");
            return ResponseEntity.badRequest().body(response);
        }

        String category = payload.get("category");
        if (category == null || category.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Category is required.");
            return ResponseEntity.badRequest().body(response);
        }

        if (!messageService.isTimeConflict(payload.get("startDate"), payload.get("startTime"), payload.get("endTime"))) {
            response.put("status", "error");
            response.put("message", "Time conflict detected.");
            return ResponseEntity.badRequest().body(response);
        }

        messageService.saveMessage(startDate, startTime, endTime, title, category);
        
        response.put("status", "success");
        response.put("message", "Message received successfully.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/delete-message")
    public ResponseEntity<Map<String, String>> deleteMessage(@RequestBody Map<String, String> payload) {
        Map<String, String> response = new HashMap<>();
        String startDate = payload.get("startDate");
        String startTime = payload.get("startTime");
        String endTime = payload.get("endTime");
        String title = payload.get("title");
        String category = payload.get("category");

        if (startDate == null || startDate.isEmpty() || startTime == null || startTime.isEmpty() ||
            endTime == null || endTime.isEmpty() || title == null || title.isEmpty() || category == null || category.isEmpty()) {
            response.put("status", "error");
            response.put("message", "All fields are required.");
            return ResponseEntity.badRequest().body(response);
        }

        long id = messageService.getMessageId(startDate, startTime, endTime, title, category);
        if (id == -1) {
            response.put("status", "error");
            response.put("message", "Message not found.");
            return ResponseEntity.badRequest().body(response);
        }
        messageService.deleteMessage(id);

        response.put("status", "success");
        response.put("message", "Message deleted successfully.");
        return ResponseEntity.ok(response);
    }   

    @PostMapping("/api/update-message")
    public ResponseEntity<Map<String, String>> updateMessage(@RequestBody Map<String, String> payload) {
        Map<String, String> response = new HashMap<>();
        String startDate = payload.get("startDate");
        String startTime = payload.get("startTime");
        String endTime = payload.get("endTime");
        String title = payload.get("title");
        String category = payload.get("category");
        String oldStartDate = payload.get("oldStartDate");
        String oldStartTime = payload.get("oldStartTime");
        String oldEndTime = payload.get("oldEndTime");
        String oldTitle = payload.get("oldTitle");
        String oldCategory = payload.get("oldCategory");

        if (startDate == null || startDate.isEmpty() || startTime == null || startTime.isEmpty() ||
            endTime == null || endTime.isEmpty() || title == null || title.isEmpty() || category == null || category.isEmpty() ||
            oldStartDate == null || oldStartDate.isEmpty() || oldStartTime == null || oldStartTime.isEmpty() ||
            oldEndTime == null || oldEndTime.isEmpty() || oldTitle == null || oldTitle.isEmpty() || oldCategory == null || oldCategory.isEmpty()) {
            response.put("status", "error");
            response.put("message", "All fields are required.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!startDate.equals(oldStartDate) && !messageService.isTimeConflict(startDate, startTime, endTime)) {
            response.put("status", "error");
            response.put("message", "Time conflict detected.");
            return ResponseEntity.badRequest().body(response);
        }
        if (startDate.equals(oldStartDate) && !messageService.isTimeConflictOnUpdate(startDate, startTime, endTime, oldStartTime, oldEndTime)) {
            response.put("status", "error");
            response.put("message", "Time conflict detected.");
            return ResponseEntity.badRequest().body(response);
        }

        long id = messageService.getMessageId(oldStartDate, oldStartTime, oldEndTime, oldTitle, oldCategory);
        if (id == -1) {
            response.put("status", "error");
            response.put("message", "Message not found.");
            return ResponseEntity.badRequest().body(response);
        }

        messageService.updateMessage(id, startDate, startTime, endTime, title, category);

        response.put("status", "success");
        response.put("message", "Message updated successfully.");
        return ResponseEntity.ok(response);
    }

}

