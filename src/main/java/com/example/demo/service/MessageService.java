package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Record;
import com.example.demo.model.Message;
import com.example.demo.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    public List<Message> sortMessagesByTime(List<Message> messages) {
        messages.sort((m1, m2) -> {
            int startComparison = m1.getStartTime().compareTo(m2.getStartTime());
            if (startComparison != 0) {
                return startComparison;
            }
            return m1.getEndTime().compareTo(m2.getEndTime());
        });
        return messages;
    }

    public boolean isTimeConflict(String date, String startTime, String endTime) {
        List<Message> messages = getMessageByDate(date);
        for (Message message : messages) {
            if ((startTime.compareTo(message.getEndTime()) <= 0 && startTime.compareTo(message.getStartTime()) >= 0) ||
                (endTime.compareTo(message.getEndTime()) <= 0 && endTime.compareTo(message.getStartTime()) >= 0)) {
                return false; // 時間が重複している
            }
            if (startTime.compareTo(message.getStartTime()) < 0 && endTime.compareTo(message.getEndTime()) > 0) {
                return false; // 新しい時間が既存のメッセージの時間を完全に覆っている
            }
        }
        return true;
    }

    public boolean isTimeConflictOnUpdate(String date, String startTime, String endTime, String oldStartTime, String oldEndTime) {
        List<Message> messages = getMessageByDate(date);
        for (Message message : messages) {
            if (oldStartTime.equals(message.getStartTime()) && oldEndTime.equals(message.getEndTime())) {
                continue; // 更新前の時間と同じ場合はスキップ
            }
            if ((startTime.compareTo(message.getEndTime()) <= 0 && startTime.compareTo(message.getStartTime()) >= 0) ||
                (endTime.compareTo(message.getEndTime()) <= 0 && endTime.compareTo(message.getStartTime()) >= 0)) {
                return false; // 時間が重複している
            }
            if (startTime.compareTo(message.getStartTime()) < 0 && endTime.compareTo(message.getEndTime()) > 0) {
                return false; // 新しい時間が既存のメッセージの時間を完全に覆っている
            }
        }
        return true;
    }

    @Transactional
    public List<Message> getMessageByDate(String date) {
        List<Record> records = messageRepository.findByStartDate(date);
        List<Message> messageList = new ArrayList<>();

        for (Record record : records) {
                Message message = new Message();
                message.setDate(record.getStartDate());
                message.setStartTime(record.getStartTime());
                message.setEndTime(record.getEndTime());
                message.setTitle(record.getTitle());
                message.setCategory(record.getCategory());

                messageList.add(message);
        }
        messageList = sortMessagesByTime(messageList);
        return messageList;
    }

    @Transactional
    public void saveMessage(String startDate, String startTime, String endTime, String title, String category) {
        Record record = new Record();
        record.setStartDate(startDate);
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setTitle(title);
        record.setCategory(category);
        messageRepository.save(record);
    }

    @Transactional
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    @Transactional
    public long getMessageIdbyDateAndStartTime(String startDate, String startTime) {
        List<Record> records = messageRepository.findByStartDate(startDate);
        for (Record record : records) {
            if (record.getStartTime().equals(startTime)) {
                return record.getId();
            }
        }
        return -1; // 見つからなかった場合
    }

    @Transactional
    public void updateMessage(Long id, String startDate, String startTime, String endTime, String title, String category) {
        Record record = new Record();
        record.setId(id);
        record.setStartDate(startDate);
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setTitle(title);
        record.setCategory(category);
        messageRepository.save(record);
    }

}
