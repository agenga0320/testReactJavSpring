package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Record;

public interface MessageRepository extends JpaRepository<Record, Long> {
    // ここに必要なクエリメソッドを定義できます
    List<Record> findByStartDate(String date);
    
}
