package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Message;
import com.example.demo.service.JobRunner;
import com.example.demo.service.MessageService;


@RestController
@CrossOrigin(origins = "http://localhost:3000") // React 側からのアクセスを許可
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JobRunner jobRunner;

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

        long id = messageService.getMessageIdbyDateAndStartTime(startDate, startTime);
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

        long id = messageService.getMessageIdbyDateAndStartTime(oldStartDate, oldStartTime);
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

    @PostMapping("/api/upload-csv")
    public ResponseEntity<Map<String, String>> uploadCsv(@RequestParam("file") MultipartFile file) {
        final String UPLOAD_DIR = "C:/Users/S0000061/Documents/demo/uploaded_file/";
        Map<String, String> response = new HashMap<>();
        if (file == null || file.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No messages to upload.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = UPLOAD_DIR + formattedDateTime + "_" + file.getOriginalFilename();
            File uploadFile = new File(filename);
            file.transferTo(uploadFile);

            try {
                jobRunner.runUploadCsvJob(filename); // ジョブを実行してCSVファイルを処理
            } catch (Exception e) {
                response.put("status", "error");
                response.put("message", "ジョブの実行中にエラーが発生しました。");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
            }

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "ファイルの保存に失敗しました。");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }

        response.put("status", "success");
        response.put("message", "Messages uploaded successfully.");
        return ResponseEntity.ok(response);
    }
}

